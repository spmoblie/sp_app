package com.spshop.stylistpark.activity.collage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.collage.BaseFragment.LoadingListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.RequestBlockingListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.ShowErrDialogListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.SoftKeyBoardListener;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductClickListener;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductListSlideListener;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.DisplayMode;
import com.spshop.stylistpark.adapter.DecorationAdapter.OnDecorationItemClickListener;
import com.spshop.stylistpark.collageviews.CollageViewTemplate;
import com.spshop.stylistpark.collageviews.Edit;
import com.spshop.stylistpark.collageviews.Edit.Type;
import com.spshop.stylistpark.collageviews.EditList;
import com.spshop.stylistpark.collageviews.EditList.UndoRedoListener;
import com.spshop.stylistpark.entity.Decoration;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.task.DownloadDbThread;
import com.spshop.stylistpark.task.KeyEffectThread;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LimitedHashMap;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserTracker;
import com.spshop.stylistpark.widgets.listener.VolleyImageResponseListener;

@SuppressLint("NewApi")
public class GeneratorTemplateActivity extends BaseActivity implements UndoRedoListener, ErrorListener, 
    OnProductClickListener, OnDecorationItemClickListener, SoftKeyBoardListener, OnProductListSlideListener {
	
    private static final String TAG = "GeneratorTemplateActivityNew";
    
    private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
    private static final int JSON_SIDE = 225;
    
    private enum NextStep{KeyEffectAndSetImage, KeyEffectAndKeyEffect};
    
    private DownloadDbThread dlDBThread;
    
    KeyEffectThread keyEffectThread;
    List<KeyEffectThread> KeyEffectThreadArray = new ArrayList<KeyEffectThread>();
    List<DraftData> draftData;
    
    RelativeLayout layoutCollageArea, layoutSpace;
    ProductMainFragment productMain_Fragment;
    View dummyView;
    
    CollageViewTemplate selectedCollageView;
    LimitedHashMap<String, Bitmap> bitmapMap = new LimitedHashMap<String, Bitmap>(MAX_PRODUCT+STACK_MAX_SIZE);
    HashMap<String, Product> productMap = new HashMap<String, Product>();
    EditList editList;
    
    String imgUrl, templateNumber;
    List<TemplateItem> itemList;
    
    private String templateJsonStr;
    
    private boolean draftLoaded = false;
    
    private Handler downloadDBHandler = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case DownloadDbThread.SUCCESS:
                if(draftLoaded) {
                    stopAnimation();
                }
                break;
            case DownloadDbThread.FAIL:
                showErrorDialog("Decoration data download fail. English only now.");
                if(draftLoaded) {
                	stopAnimation();
                }
                break;
            default:
                break;
            }
        }
        
    };
    
    Handler downloadHandler = new Handler()
    {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg)
        {
            NextStep nextStep = NextStep.values()[msg.what];
            Pair<String, Bitmap> result = null;
            if(msg.obj instanceof Pair)
            {
                result = (Pair<String, Bitmap>) msg.obj;
            }
            switch (nextStep)
            {
            case KeyEffectAndSetImage:
                if (keyEffectThread != null && keyEffectThread.isAlive())
                {
                    keyEffectThread.interrupt();
                }
                keyEffectThread = new KeyEffectThread(
                        result, nextStep.ordinal(), mContext, keyEffectHandler);
                keyEffectThread.start();
                break;
            case KeyEffectAndKeyEffect:
                KeyEffectThread tmpThread = new KeyEffectThread(
                        result, nextStep.ordinal(), mContext, keyEffectHandler);
                KeyEffectThreadArray.add(tmpThread);
                tmpThread.start();
                break;
            default:
                break;
            }
        }
    };
    Handler keyEffectHandler = new Handler()
    {
        @SuppressWarnings({ "unchecked", "static-access" })
        @Override
        public void handleMessage(Message msg)
        {
            NextStep nextStep = NextStep.values()[msg.what];
            Pair<String, Bitmap> result = null;
            if(msg.obj instanceof Pair)
            {
                result = (Pair<String, Bitmap>) msg.obj;
            }
            switch (nextStep)
            {
            case KeyEffectAndSetImage:
                bitmapMap.put(result.first, result.second);
                Drawable tmp = selectedCollageView.getDrawable();
                TemplateItem item = (TemplateItem) selectedCollageView.getTag();
                String tmpStr = item.getUrl();
                int tmpType = item.getType();
                item.setType(TemplateItem.PRODUCT);
                item.setUrl(result.first);
                selectedCollageView.setImageBitmap(bitmapMap.get(result.first));
                selectedCollageView.setShowBackground(false);
                editList.addRecord(new Edit(selectedCollageView, tmp,
                        selectedCollageView.getDrawable(), tmpStr, item.getUrl(), tmpType, item.getType()));
                stopAnimation();
                break;
            case KeyEffectAndKeyEffect:
                bitmapMap.put(result.first, result.second);
                // if all finish, start set image
                if(productMap.size() == bitmapMap.size())
                {
                    for (int i = 0; i < draftData.size(); i++)
                    {
                        DraftData data = draftData.get(i);
                        for (int j = 0; j < layoutCollageArea.getChildCount(); j++)
                        {
                            TemplateItem templateItem = (TemplateItem) layoutCollageArea.getChildAt(j).getTag();
                            if(templateItem.z == data.originZ)
                            {
                                final CollageViewTemplate cv = (CollageViewTemplate) layoutCollageArea.getChildAt(j);
                                if(data.type == TemplateItem.PRODUCT)
                                {
                                    templateItem.setType(templateItem.PRODUCT);
                                    templateItem.setUrl(data.url);
                                    cv.setImageBitmap(bitmapMap.get(data.url));
                                    cv.setShowBackground(false);
                                } else if(data.type == TemplateItem.DECOR)
                                {
                                    templateItem.setType(templateItem.DECOR);
                                    templateItem.setUrl(data.url);
                                    ImageRequest ir = new ImageRequest(data.url, new Listener<Bitmap>()
                                            {
                                                @Override
                                                public void onResponse(Bitmap bitmap)
                                                {
                                                    cv.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                                                }
                                            }, 0, 0, null, new ErrorListener(){
                                                @Override
                                                public void onErrorResponse(VolleyError arg0)
                                                {
                                                    
                                                }
                                            });
                                    ir.setRetryPolicy(retryPolicy60s);
                                    AppApplication.getInstance().getRequestQueue().add(ir);
                                    cv.setShowBackground(false);
                                }
                                
                            }
                        }
                        
                    }
                    draftLoaded = true;
                    stopAnimation();
                }
                break;
            default:
                break;
            }
        }
    };
    
    private void addItemView(TemplateItem item)
    {
        CollageViewTemplate cv = new CollageViewTemplate(this);
        item.setType(TemplateItem.NOTSET);
        cv.setImageDrawable(null);
        cv.setShowBackground(true);
        cv.setTag(item);
        cv.setWord(item.groupName);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                (int) item.dw, (int) item.dh);
        lp.topMargin = (int) item.dy;
        lp.leftMargin = (int) item.dx;
        cv.setRotation(item.r);
        cv.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
               CollageViewTemplate cv = (CollageViewTemplate) v;
               LogUtil.i(TAG, "cv onTouch "+(cv.getDrawable() == null));
               selectCollageView(cv);
               if (cv.isShowBackground())
               {
                   // select and select product
                   TemplateItem item = (TemplateItem) cv.getTag();
                   productMain_Fragment.openMenu(AppConfig.PRODUCT_MENU_ORDER_ARR[item.groupId]);
               }
                
            }
        });
        layoutCollageArea.addView(cv, lp);
        
        int childCount = layoutCollageArea.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            if(cv == layoutCollageArea.getChildAt(i))
            {
                LogUtil.i(TAG, "addItemView z,i "+item.z+", " + i);
            }
        }
    }
    
    private void ask4Leave(final boolean isBack2Menu)
    {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what) {
                case 0: // save
                    UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_TEMPLATE_SAVE_DRAFT, null);
                    saveDraft(false);
                    if(isBack2Menu)
                    {
                        Intent i = getIntent();
                        setResult(RESULT_OK, i);
                    }
                    finish();
                    break;
                case 1: // discard
                    if(isBack2Menu)
                    {
                        Intent i = getIntent();
                        setResult(RESULT_OK, i);
                    }
                    finish();
                    break;
                case 2: // cancel
                    break;
                default:
                    break;
                }
            }
        };
        showConfirmDialog(R.string.collage_msg_leave_create, getResources().getStringArray(R.array.array_save_draft), handler);
    }
    
    public void clickBackward(View v)
    {
        if(selectedCollageView == null) return;
        editList.addRecord(new Edit(Type.moveDown, selectedCollageView));
        moveDown(selectedCollageView);
    }

    public void clickDelete(View v)
    {
        if (selectedCollageView == null)
            return;
        TemplateItem item = (TemplateItem) selectedCollageView.getTag();
        editList.addRecord(new Edit(selectedCollageView, selectedCollageView
                .getDrawable(), null, item.getUrl(), null, item.getType(), TemplateItem.NOTSET));
        delete(selectedCollageView);
    }

    public void clickForward(View v)
    {
        if(selectedCollageView == null) return;
        editList.addRecord(new Edit(Type.moveUp, selectedCollageView));
        moveUp(selectedCollageView);
    }

    public void clickRedo(View v)
    {
        editList.redo();
    }

    public void clickUndo(View v)
    {
        editList.undo();
    }
    
    public void convertJson(String jsonStr)
    {
        LogUtil.i(TAG, "convertJson");
        try
        {
            JSONObject json = new JSONObject(jsonStr);
            LogUtil.i(TAG, "convertJson success");
            
            templateNumber = json.getString("templateNo");
            imgUrl = json.getString("imgUrl");
            
            JSONArray jsonArr = json.getJSONArray("item");
            itemList = new ArrayList<TemplateItem>();
            for (int i = 0; i < jsonArr.length(); i++)
            {
                JSONObject itemJson = jsonArr.getJSONObject(i);
                int groupId = itemJson.getInt("groupId");
                float w = (float) itemJson.getDouble("w");
                float h = (float) itemJson.getDouble("h");
                float x = (float) itemJson.getDouble("x");
                float y = (float) itemJson.getDouble("y");
                float z = (float) itemJson.getDouble("z");
                float r = (float) itemJson.getDouble("deg");
                itemList.add(new TemplateItem(groupId, w, h, x, y, z, r));
            }
        } catch (JSONException e)
        {
            ExceptionUtil.handle(mContext, e);
            showErrorDialog(R.string.dialog_error_msg);
        }

    }
    
    private void delete(CollageViewTemplate cv)
    {
        TemplateItem item = (TemplateItem) cv.getTag();
        item.setUrl(null);
        item.setType(TemplateItem.NOTSET);
        cv.setShowBackground(true);
        cv.setImageDrawable(null);
    }
    
    private void downloadImage(String url, NextStep nextStep)
    {
        startAnimation();
        ImageRequest ir = new ImageRequest(url, new VolleyImageResponseListener(
        		url, nextStep.ordinal(), downloadHandler), 0, 0, null, this);
        ir.setRetryPolicy(retryPolicy60s);
        AppApplication.getInstance().getRequestQueue().add(ir);
    }
    
    private String getDraftTemplateJsonStr()
    {
        File file = new File(getExternalCacheDir(), AppConfig.TEMPLATE_DRAFT_NAME);
        JSONObject templateJson = null;
        try
        {
            String templateJsonStr = FileManager.getStringFromFile(file);
            templateJson = new JSONObject(templateJsonStr);
            templateNumber = templateJson.getString("templateNumber");
            LogUtil.i(TAG, "getDraftTemplateJsonStr templateNumber: " + templateNumber);
        } catch (Exception e)
        {
            ExceptionUtil.handle(mContext, e);
            CommonTools.deleteFileInCache(this, AppConfig.TEMPLATE_DRAFT_NAME);
            showErrorDialog(R.string.collage_error_load_draft);
            finish();
        }
        
        file = new File(getExternalCacheDir(), AppConfig.TEMPLATE_DRAFT_ORIGIN_NAME);
        
        try
        {
            return FileManager.getStringFromFile(file);
        } catch (Exception e)
        {
            ExceptionUtil.handle(mContext, e);
            showErrorDialog(R.string.dialog_error_msg);
            finish();
        }
        LogUtil.i(TAG, "getDraftTemplateJsonStr templateNumber: " + templateNumber);
        LogUtil.i(TAG, "getDraftTemplateJsonStr cannot find template by template number");
        showErrorDialog(R.string.dialog_error_msg);
        finish();
        return null;
    }
    
    public String getHtml(String imgFileName, int side, String idTail)
    {

        float scale = (float) side/JSON_SIDE;

        String css = "<style>";
        
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                TemplateItem item = (TemplateItem) layoutCollageArea.getChildAt(i).getTag();
                css += "#a" + i + "-img"+idTail+"{";
                css += "display: inline-block;";
                css += "position: absolute;";
                css += "width: " + (int) (item.w * scale) + "px;";
                css += "height: " + (int) (item.h * scale) + "px;";
                css += "-webkit-transform: "
                        + "translate(" + item.x * scale + "px, " + item.y * scale + "px) "
                                + "rotate(" + item.r + "deg);";
                css += "-ms-transform: "
                        + "translate(" + item.x * scale + "px, " + item.y * scale + "px) "
                            + "rotate(" + item.r + "deg);";
                css += "transform: "
                        + "translate(" + item.x * scale + "px, " + item.y * scale + "px) "
                            + "rotate(" + item.r + "deg);";
                css += "background-size: cover;}";
            }
        }
        
        css += "</style>";

        String htmlDoc = "<!DOCTYPE html><html>";
        htmlDoc += css;
        htmlDoc += "<body>";
        htmlDoc += "<div style=\"background-size: cover; background-image: url('"
                + imgFileName
                + "'); width:"
                + side
                + "px; height:"
                + side
                + "px;\">";
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            TemplateItem item = (TemplateItem) layoutCollageArea.getChildAt(i).getTag();
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                String url = String.format(AppConfig.PRODUCT_DETAIL_PAGE_PTAH_FORMAT, productMap.get(item.getUrl()).getItemId());
                htmlDoc += "<a href=\"" + url + "\"><div id=\"a" + i
                        + "-img"+idTail+"\" \"></div></a>";
            }
        }
        htmlDoc += "</div>";
        htmlDoc += "</body>";
        htmlDoc += "</html>";
        return htmlDoc;
    }

    
    private boolean isDecor(View view)
    {
        TemplateItem item = (TemplateItem) view.getTag();
        return item.getType() == TemplateItem.DECOR;
    }
    
    private boolean isProduct(View view)
    {
        TemplateItem item = (TemplateItem) view.getTag();
        return item.getType() == TemplateItem.PRODUCT;
    }
    
    private void loadDraft()
    {
        File file = new File(getExternalCacheDir(), AppConfig.TEMPLATE_DRAFT_NAME);
        JSONObject templateJson = null;
        try
        {
            String templateJsonStr = FileManager.getStringFromFile(file);
            templateJson = new JSONObject(templateJsonStr);
        } catch (Exception e)
        {
            ExceptionUtil.handle(mContext, e);
            CommonTools.deleteFileInCache(this, AppConfig.TEMPLATE_DRAFT_NAME);
            showErrorDialog(R.string.collage_error_load_draft);
            return;
        }
        
        draftData = new ArrayList<DraftData>();
        try
        {
            JSONArray jsonArr = templateJson.getJSONArray("items");
            for (int i = 0; i < jsonArr.length(); i++)
            {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                String url = null;
                JSONObject productJson = null;
                if(jsonObj.has("url"))
                {
                    url = jsonObj.getString("url");
                }
                if(jsonObj.has("productJson"))
                {
                    productJson = jsonObj.getJSONObject("productJson");
                }
                int type = jsonObj.getInt("type");
                float z = (float) jsonObj.getDouble("originZIndex");
                draftData.add(new DraftData(url, z, new Product(productJson), type));
            }
            
        } catch (JSONException e)
        {
            ExceptionUtil.handle(mContext, e);
        }
        
    }
    
    private void moveDown(View view)
    {
        if (view == null)
            return;
        // the lowest layer
        if (view == layoutCollageArea.getChildAt(0))
            return;
        for (int i = 1; i < layoutCollageArea.getChildCount(); i++)
        {
            if (layoutCollageArea.getChildAt(i) == view)
            {
                layoutCollageArea.removeViewAt(i);
                layoutCollageArea.addView(view, i - 1);
            }
        }
    }

    private void moveUp(View view)
    {
        if (view == null)
            return;
        // the highest layer
        if (view == layoutCollageArea.getChildAt(layoutCollageArea.getChildCount() - 1))
            return;
        for (int i = 0; i < layoutCollageArea.getChildCount() - 1; i++)
        {
            if (layoutCollageArea.getChildAt(i) == view)
            {
                layoutCollageArea.removeViewAt(i);
                layoutCollageArea.addView(view, i + 1);
            }
        }
    }
    
    @Override
    public void onBackPressed()
    {
        if (!productMain_Fragment.onBackPressed()){
        	ask4Leave(false);
        }else {
        	productMain_Fragment.onKeyBoardShow(false);
		}
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_template);
        
        setTitle(R.string.collage_create_template);
        setBtnRight(getString(R.string.save));
        
        findView();
        init();
    }
    
    private void findView()
    {
        layoutCollageArea = (RelativeLayout) findViewById(R.id.layoutCollageArea);
        layoutSpace = (RelativeLayout) findViewById(R.id.layoutSpace);
        
        productMain_Fragment = (ProductMainFragment) getSupportFragmentManager().findFragmentById(R.id.productMain_Fragment);
        dummyView = (View) findViewById(R.id.dummyView);
    }
    
    private void init()
    {
        startAnimation();
        dlDBThread = new DownloadDbThread(this, downloadDBHandler);
        dlDBThread.start();
        
        editList = new EditList(STACK_MAX_SIZE, this);
        
        templateJsonStr = getIntent().getStringExtra(GeneratorTemplateChooseActivity.TEMPLATE_JSON);
        if(templateJsonStr == null)
        {
            templateJsonStr = getDraftTemplateJsonStr();
            LogUtil.i(TAG, "init templateJsonStr: " + templateJsonStr);
            loadDraft();
            convertJson(templateJsonStr);
        } else 
        {
            convertJson(templateJsonStr);
        }
        
        LogUtil.i(TAG, "init imgUrl: " + imgUrl);
        ImageRequest ir = new ImageRequest(imgUrl, 
                new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap bitmap) {
                    	if (bitmap != null) {
                    		int color = bitmap.getPixel(1, 1);
                    		int r = Color.red(color);
                    		int g = Color.green(color);
                    		int b = Color.blue(color);
                    		LogUtil.i(TAG, "onResponse r, g, b: " + r+" "+g+" "+b);
                    		layoutCollageArea.setBackground(new BitmapDrawable(getResources(), bitmap));
						}
                    }
                }, 
                0, 0, null, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        LogUtil.i(TAG, "onErrorResponse start");
                    }
                });
        ir.setRetryPolicy(retryPolicy60s);
        AppApplication.getInstance().getRequestQueue().add(ir);
        layoutSpace.post(new Runnable() {

            @Override
            public void run() {
                int w = layoutSpace.getWidth();
                int h = layoutSpace.getHeight();

                int margin = getResources().getDimensionPixelSize(R.dimen.generator_template_collage_area_margin);
                int shorterSide = Math.min(w, h) - 2 * margin;
                float itemScale = (float) shorterSide / JSON_SIDE;
                LogUtil.i(TAG, "run itemScale " + itemScale);
                layoutCollageArea.getLayoutParams().width = shorterSide;
                layoutCollageArea.getLayoutParams().height = shorterSide;
                // by json, add item
                String[] product_groups = getResources().getStringArray(R.array.array_product_group);
                for (int i = itemList.size() - 1; i >= 0 ; i--)
                {
                    TemplateItem item = itemList.get(i);
                    item.setScaledSize(itemScale);
                    item.setGroupName(product_groups[item.groupId]);
                    addItemView(item);
                }
                
                if(draftData != null) {
                    LogUtil.i(TAG, "run" + " load draft data, size: "+draftData.size());
                    // set View z index
                    for(int i = 0;i< draftData.size();i++)
                    {
                        DraftData data = draftData.get(i);
                        for (int j = 0; j < layoutCollageArea.getChildCount(); j++)
                        {
                            TemplateItem item = (TemplateItem) layoutCollageArea.getChildAt(j).getTag();
                            if(item.z == data.originZ) {
                                View v = layoutCollageArea.getChildAt(j);
                                layoutCollageArea.removeView(v);
                                layoutCollageArea.addView(v);
                            }
                        }
                        
                    }
                    // download or add if no product
                    boolean noProduct = true;
                    for(int i=0;i<draftData.size();i++)
                    {
                        DraftData data = draftData.get(i);
                        LogUtil.i(TAG, "run " + data.originZ);
                        boolean a = (!TextUtils.isEmpty(data.url) && data.product != null);
                        LogUtil.i(TAG, "run boolean: " + a);
                        if(!TextUtils.isEmpty(data.url) && data.product != null)
                        {
                            noProduct = false;
                            productMap.put(data.url, data.product);
                            downloadImage(data.url, NextStep.KeyEffectAndKeyEffect);
                        }
                    }
                    if(noProduct)
                    {
                        for(int i=0;i<draftData.size();i++)
                        {
                            DraftData data = draftData.get(i);
                            if(data.type == TemplateItem.DECOR)
                            {
                                for (int j = 0; j < layoutCollageArea.getChildCount(); j++)
                                {
                                    TemplateItem item = (TemplateItem) layoutCollageArea.getChildAt(j).getTag();
                                    if(item.z == data.originZ)
                                    {
                                        item.setType(TemplateItem.DECOR);
                                        item.setUrl(data.url);
                                        final CollageViewTemplate cv = (CollageViewTemplate) layoutCollageArea.getChildAt(j);
//                                        cv.setImageUrl(data.url, AppApplication.getInstance().getImageLoader());
                                        ImageRequest ir = new ImageRequest(data.url, new Listener<Bitmap>()
                                                {
                                                    @Override
                                                    public void onResponse(Bitmap bitmap)
                                                    {
                                                        cv.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                                                    }
                                                }, 0, 0, null, new ErrorListener(){
                                                    @Override
                                                    public void onErrorResponse(VolleyError arg0)
                                                    {
                                                        
                                                    }
                                                });
                                        ir.setRetryPolicy(retryPolicy60s);
                                        AppApplication.getInstance().getRequestQueue().add(ir);
                                        cv.setShowBackground(false);
                                    }
                                }
                            }
                        }
                        draftLoaded = true;
                        stopAnimation();
                    }
                } else {
                    draftLoaded = true;
                }
            }
        });
        
        layoutSpace.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                unselect();
                return true;
            }
        });
        
        productMain_Fragment.setOnProductListSlideListener(this);
        productMain_Fragment.dimMenu(true);
        productMain_Fragment.setOnProductClickListener(this);
        productMain_Fragment.setOnDecorationItemClickListener(this);
        productMain_Fragment.setShowErrDialogListener(new ShowErrDialogListener()
        {
            @Override
            public void showErrDialog(String msg)
            {
                showErrorDialog(msg);
            }
        });
        
        productMain_Fragment.setLoadingListener(new LoadingListener()
        {
            @Override
            public void onShowLoading()
            {
                LogUtil.i(TAG,"event-onShowLoading()");
                startAnimation();
            }
        
            @Override
            public void onHideLoading()
            {
                LogUtil.i(TAG,"event-onHideLoading()");
                stopAnimation();
            }
        });
        
        productMain_Fragment.setRequestBlockingListener(new RequestBlockingListener()
        {
            @Override
            public void onRequestBlock()
            {
                LogUtil.i(TAG,"event-onRequestBlock()");
//                setCanClickIntecptLayout(false);
//                showInterceotLayout(true); //Xu
            }

            @Override
            public void onReleaseBlock()
            {
                LogUtil.i(TAG,"event-onReleaseBlock()");
//                setCanClickIntecptLayout(true);
//                showInterceotLayout(false); //Xu
            }
        });
        
//        setOnInterceptListener(this);
//        setSoftKeyBoardListener(this); //Xu
        
        dummyView.setOnTouchListener(new OnTouchListener()
        {
        
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1)
            {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN)
                {
                    LogUtil.i(TAG, "dummyView Touched");
                }
                return true;
            }
        });
    }

    @Override
    public void onDecorationItemClick(Decoration decoration)
    {
        int decorNumber = 0;
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            if (isDecor(layoutCollageArea.getChildAt(i)))
                decorNumber++;
        }
        if (decorNumber >= MAX_DECOR)
        {
            showErrorDialog(R.string.collage_msg_decor_too_many);
            return;
        }
        
        Drawable tmp = selectedCollageView.getDrawable();
        TemplateItem item = (TemplateItem) selectedCollageView.getTag();
        String tmpStr = item.getUrl();
        int tmpType = item.getType();
        item.setType(TemplateItem.DECOR);
        item.setUrl(decoration.getUrl2x());
//        selectedCollageView.setImageUrl(item.getUrl(), AppApplication.getInstance().getImageLoader());
        ImageRequest ir = new ImageRequest(item.getUrl(), new Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap bitmap)
                    {
                        selectedCollageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                    }
                }, 0, 0, null, new ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        
                    }
                });
        ir.setRetryPolicy(retryPolicy60s);
        AppApplication.getInstance().getRequestQueue().add(ir);
        selectedCollageView.setShowBackground(false);
        editList.addRecord(new Edit(selectedCollageView, tmp,
                selectedCollageView.getDrawable(), tmpStr, item.getUrl(), tmpType, item.getType()));
    }

    @Override
    public void onErrorResponse(VolleyError arg0)
    {
        showErrorDialog(R.string.network_fault);
        stopAnimation();
    }

    @Override
    public void onShowSoftKeyBoard()
    {
        //showInterceotLayout(true); //Xu
        productMain_Fragment.onKeyBoardShow(true);
    }

    @Override
    public void onHideSoftKeyBoard()
    {
        //showInterceotLayout(false); //Xu
        productMain_Fragment.onKeyBoardShow(false);
    }
    
    @Override
    public void onProductClick(Product product, DisplayMode mode, String productCats)
    {
        int productNumber = 0;
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
            if (isProduct(layoutCollageArea.getChildAt(i)))
                productNumber++;
        
        if (productNumber >= MAX_PRODUCT)
        {
            showErrorDialog(R.string.collage_msg_product_too_many);
            return;
        }
        // after check product number, add view or download image
        String url = IMAGE_URL_HTTP + ((mode == DisplayMode.Product)? product.getImgUrl():product.getModelImgUrl());
        if ( bitmapMap.containsKey(url) )
        {
            if ( bitmapMap.get(url) != null)
            {
                TemplateItem item = (TemplateItem) selectedCollageView.getTag();
                item.setType(TemplateItem.PRODUCT);
                item.setUrl(url);
                selectedCollageView.setImageBitmap(bitmapMap.get(url));
                selectedCollageView.setShowBackground(false);
            }
        } else
        {
            productMap.put(url, product);
            downloadImage(url, NextStep.KeyEffectAndSetImage);
        }
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        if(selectedCollageView == null)
            productMain_Fragment.dimMenu(true);
    }
    
    @Override
    public void redo(Edit edit)
    {
        unselect();
        switch (edit.type)
        {
        case changeImage:
        {
            TemplateItem item = (TemplateItem) edit.view.getTag();
            item.setType(edit.type2);
            item.setUrl(edit.url2);
            ((CollageViewTemplate) edit.view).setImageDrawable(edit.drawable2);
            if(edit.drawable2 == null) {
                ((CollageViewTemplate) edit.view).setShowBackground(true);
            } else {
                ((CollageViewTemplate) edit.view).setShowBackground(false);
            }
        }
            break;
        case moveDown:
        {
            moveDown(edit.view);
        }
            break;
        case moveUp:
        {
            moveUp(edit.view);
        }
            break;
        default:
            break;
        }
    }
    
    private void save()
    {
        int productNumber = 0;
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                productNumber++;
            }
        }
        if (productNumber < MIN_PRODUCT)
        {
            showErrorDialog(R.string.collage_no_item);
            return;
        }
        // after check, generate things
        String imgFileName = FileManager.getFileName();

        String htmlDoc = getHtml(imgFileName, GEN_OUTPUT_SIDE, "");
        String mobileHtmlDoc = getHtml(imgFileName, GEN_OUTPUT_MOBILE_SIDE, "mobile");

        String[] productIdList = new String[productNumber];
        int j = 0;
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            TemplateItem item = (TemplateItem) layoutCollageArea.getChildAt(i).getTag();
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                productIdList[j] = productMap.get(item.getUrl()).getItemId();
                j++;
            }
        }
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            CollageViewTemplate cv = (CollageViewTemplate) layoutCollageArea.getChildAt(i);
            if(cv.getDrawable() == null)
            {
                cv.setVisibility(View.GONE);
            }
        }
        unselect();
        URI uri = BitmapUtil.captureView(layoutCollageArea, imgFileName, GEN_OUTPUT_SIDE, GEN_OUTPUT_SIDE, 70);
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            CollageViewTemplate cv = (CollageViewTemplate) layoutCollageArea.getChildAt(i);
            if(cv.getDrawable() == null)
            {
                cv.setVisibility(View.VISIBLE);
            }
        }
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_LOOKBOOK_DESCRIPTION_FORM, null);
        
        Intent i = new Intent(this, CollageInfoActivity.class);
        i.putExtra(CollageInfoActivity.CREATE_TYPE, GeneratorTemplateChooseActivity.TAG);
        i.putExtra(CollageInfoActivity.COLLAGE_URI, uri);
        i.putExtra(CollageInfoActivity.COLLAGE_HTML, htmlDoc);
        i.putExtra(CollageInfoActivity.COLLAGE_MOBILE_HTML, mobileHtmlDoc);
        i.putExtra(CollageInfoActivity.COLLAGE_LIST, productIdList);
        startActivity(i);
    }
    
    private void saveDraft(boolean promptDialog)
    {
        JSONObject wholeJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            CollageViewTemplate cv = (CollageViewTemplate) layoutCollageArea.getChildAt(i);
            String url = null;
            JSONObject productJson = null;
            TemplateItem item = (TemplateItem) cv.getTag();
            if(isDecor(cv))
            {
                url = item.getUrl();
            } else if(isProduct(cv))
            {
                url = item.getUrl();
                productJson = productMap.get(url).getJson();
            }
            JSONObject jsonObj = new JSONObject();
            try
            {
                jsonObj.put("url", url);
                jsonObj.put("productJson",  productJson);
                jsonObj.put("originZIndex", item.z);
                jsonObj.put("type", item.getType());
            } catch (JSONException e)
            {
                ExceptionUtil.handle(mContext, e);
            }
            jsonArray.put(jsonObj);
        }
        try
        {
            wholeJson.put("templateNumber", templateNumber);
            wholeJson.put("items", jsonArray);
        } catch (JSONException e1)
        {
        	ExceptionUtil.handle(mContext, e1);
        }
        
        File file = new File(getExternalCacheDir(), AppConfig.TEMPLATE_DRAFT_ORIGIN_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(templateJsonStr.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            ExceptionUtil.handle(mContext, e);
        } catch (IOException e) {
            ExceptionUtil.handle(mContext, e);
        }
        
        file = new File(getExternalCacheDir(), AppConfig.TEMPLATE_DRAFT_NAME);
        fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(wholeJson.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            ExceptionUtil.handle(mContext, e);
        } catch (IOException e) {
            ExceptionUtil.handle(mContext, e);
        }
    }
    
    private void selectCollageView(CollageViewTemplate cv)
    {
        if (selectedCollageView != null)
        {
            selectedCollageView.setSelected(false);
        }
        selectedCollageView = cv;
        cv.setSelected(true);
        cv.invalidate();
        productMain_Fragment.dimMenu(false);
    }
    
    @Override
    public void undo(Edit edit)
    {
        unselect();
        switch (edit.type)
        {
        case changeImage:
        {
            TemplateItem item = (TemplateItem) edit.view.getTag();
            item.setType(edit.type1);
            item.setUrl(edit.url1);
            ((CollageViewTemplate) edit.view).setImageDrawable(edit.drawable1);
            if(edit.drawable1 == null) {
                ((CollageViewTemplate) edit.view).setShowBackground(true);
            } else {
                ((CollageViewTemplate) edit.view).setShowBackground(false);
            }
        }
            break;
        case moveDown:
        {
            moveUp(edit.view);
        }
            break;
        case moveUp:
        {
            moveDown(edit.view);
        }
            break;
        default:
            break;
        }
    }
    
    private void unselect()
    {
        if (selectedCollageView != null)
        {
            productMain_Fragment.dimMenu(true);
            selectedCollageView.setSelected(false);
            selectedCollageView.invalidate();
            selectedCollageView = null;
        }
    }
    
    private class DraftData
    {
        String url;
        float originZ;
        Product product;
        int type;
        public DraftData(String url, float originZ,
                Product product, int type)
        {
            this.url = url;
            this.originZ = originZ;
            this.product = product;
            this.type = type;
        }
    }

    public class TemplateItem
    {
        int groupId;
        String groupName;
        float w, h, x, y, z, r;
        float dw, dh, dx, dy;
        String url;
        int type;
        private static final int NOTSET = 0;
        private static final int PRODUCT = 1;
        private static final int DECOR = 2;
        
        public TemplateItem(int groupId, float w, float h, float x, float y, float z, float r)
        {
            this.groupId = groupId;
            this.w = w;
            this.h = h;
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
        }

        public void setScaledSize(float scale)
        {
            dw = w * scale;
            dh = h * scale;
            dx = x * scale;
            dy = y * scale;
        }

        public String getGroupName()
        {
            return groupName;
        }

        public void setGroupName(String groupName)
        {
            this.groupName = groupName;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }

        public int getType()
        {
            return type;
        }

        public void setType(int type)
        {
            this.type = type;
        }

    }

    @Override
    public void onSlideUpEnd()
    {
    	setBtnLeftGone(View.GONE);
    	setBtnRightGone(View.GONE);
    }

    @Override
    public void onSlideDownEnd()
    {
    	setBtnLeftGone(View.VISIBLE);
    	setBtnRightGone(View.VISIBLE);
    }
    
    @Override
    public void OnListenerLeft() {
    	ask4Leave(false);
    }
    
    @Override
    public void OnListenerRight() {
    	super.OnListenerRight();
    	save();
    }
    
}