package com.spshop.stylistpark.activity.collage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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
import com.spshop.stylistpark.collageviews.CollageView;
import com.spshop.stylistpark.collageviews.CollageViewWithBorder;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@SuppressLint("HandlerLeak")
public class GeneratorFreeStyleActivity extends BaseActivity implements
		OnProductClickListener, OnDecorationItemClickListener, SoftKeyBoardListener, 
		Response.ErrorListener, UndoRedoListener, OnProductListSlideListener {
	
    public static final String TAG = "GeneratorFreeStyleActivity";
    
    private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
    
    private enum NextStep{KeyEffectAndAddView, KeyEffectAndKeyEffect};
    
    private DownloadDbThread dlDBThread;
    
    KeyEffectThread keyEffectThread;
    List<KeyEffectThread> KeyEffectThreadArray = new ArrayList<KeyEffectThread>();
    
    RelativeLayout layoutCollageArea, layoutSpace;
    View viewBorder, dummyView;
    ProductMainFragment productMain_Fragment;
    
    int cvInitMinSide;
    int cvInitMaxSide;
    private static final float cvInitMaxFactor = 0.5f;
    int cvCornerSide;
    
    CollageViewWithBorder selectedCollageView;
    LimitedHashMap<String, Bitmap> bitmapMap = new LimitedHashMap<String, Bitmap>(MAX_PRODUCT + STACK_MAX_SIZE);
    HashMap<String, Product> productMap = new HashMap<String, Product>();
    LinkedHashMap<String, DraftData> draftMap = new LinkedHashMap<String, DraftData>();
    EditList editList;
    boolean draftLoaded = false;
    boolean isLoadDraft = false;
     
    Handler downloadHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            NextStep nextStep = NextStep.values()[msg.what];
            Pair<String, Bitmap> result = null;
            if(msg.obj instanceof Pair) {
                result = (Pair<String, Bitmap>) msg.obj;
            }
            switch (nextStep) {
            case KeyEffectAndAddView:
                if (keyEffectThread != null && keyEffectThread.isAlive()) {
                    keyEffectThread.interrupt();
                }
                keyEffectThread = new KeyEffectThread(result, nextStep.ordinal(), mContext, keyEffectHandler);
                keyEffectThread.start();
                break;
            case KeyEffectAndKeyEffect:
                KeyEffectThread tmpThread = new KeyEffectThread(result, nextStep.ordinal(), mContext, keyEffectHandler);
                KeyEffectThreadArray.add(tmpThread);
                tmpThread.start();
                break;
            }
        }
    };
    Handler keyEffectHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            NextStep nextStep = NextStep.values()[msg.what];
            Pair<String, Bitmap> result = null;
            if(msg.obj instanceof Pair) {
                result = (Pair<String, Bitmap>) msg.obj;
            }
            switch (nextStep) {
            case KeyEffectAndAddView:
                bitmapMap.put(result.first, result.second);
                addCollageView(result);
                stopAnimation();
                break;
            case KeyEffectAndKeyEffect:
                bitmapMap.put(result.first, result.second);
                // if all finish, start add view
                if(productMap.size() == bitmapMap.size()) {
                    Set<String> set = draftMap.keySet();
                    LogUtil.i(TAG, "handleMessage set.size() " + set.size());
                    for (int i = 0; i < set.size(); i++) {
                        DraftData data = draftMap.get((String)set.toArray()[i]);
                        String url = data.url;
                        CollageViewWithBorder cv;
                        Bitmap bitmap = bitmapMap.get(url);
                        if(null == bitmap) {
                            cv = addCollageView(new Pair<String, Bitmap>(url, null));
                        } else {
                            cv = addCollageView(new Pair<String, Bitmap>(url, bitmap));
                        }
                        cv.setTranslationX((float) data.tx);
                        cv.setTranslationY((float) data.ty);
                        cv.setScale((float) data.s);
                        cv.setRotation((float) data.r);
                    }
                    stopAnimation();
                }
                break;
            }
        }
    };
    
    private Handler downloadDBHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DownloadDbThread.SUCCESS:
                LogUtil.i(TAG, "handleMessage: " + isLoadDraft);
                if(isLoadDraft) {
                    loadDraft();
                } else {
                	stopAnimation();
                }
                break;
            case DownloadDbThread.FAIL:
//                showErrorDialog("Decoration data download fail. English only now.");
                LogUtil.i(TAG, "handleMessage: " + isLoadDraft);
                if(isLoadDraft) {
                    loadDraft();
                } else {
                	stopAnimation();
                }
                break;
            default:
                break;
            }
        }
        
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_free_style);
        
        setTitle(R.string.collage_create_free);
        setBtnRight(getString(R.string.confirm));
        
        findView();
        init();
    }
    
    @Override
    protected void onDestroy()
    {
        if (keyEffectThread != null && keyEffectThread.isAlive())
            keyEffectThread.interrupt();
        super.onDestroy();
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        onLoadDraft();
    }

    private void add(View view)
    {
        if (view == null)
            return;
        layoutCollageArea.addView(view);
    }
    
    private CollageViewWithBorder addCollageView(Pair<String, Bitmap> source)
    {
        final CollageViewWithBorder cv = new CollageViewWithBorder(this);
        cv.setTag(source.first);
        
        RelativeLayout.LayoutParams lp;
        if(null != source.second) {
            Rect rect = calculateScaledRect(source.second.getWidth(), source.second.getHeight(), cvInitMinSide, cvInitMaxSide);
            int w =rect.width() + cvCornerSide;
            int h = rect.height() + cvCornerSide;
            lp = new RelativeLayout.LayoutParams(w, h);
        } else {
            lp = new RelativeLayout.LayoutParams(CommonTools.dpToPx(mContext, 100), CommonTools.dpToPx(mContext, 100));
        }
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        
        // set Clip Children to false to prevent clip 4 corner circle
        cv.setClipChildren(false);
        cv.setProduct(null != source.second);
        if(null == source.second) {
            ImageRequest ir = new ImageRequest(source.first, new Listener<Bitmap>()
            {
                @Override
                public void onResponse(Bitmap bitmap)
                {
                    LogUtil.i(TAG, "no image onResponse: " + bitmap.getWidth());
                    LogUtil.i(TAG, "no image onResponse: " + bitmap.getHeight());
                    Rect rect = calculateScaledRect(bitmap.getWidth(), bitmap.getHeight(), cvInitMinSide, cvInitMaxSide);
                    int w =rect.width() + cvCornerSide;
                    int h = rect.height() + cvCornerSide;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cv.getLayoutParams();
                    lp.width = w;
                    lp.height = h;
                    cv.setLayoutParams(lp);
                    cv.setImageBitmap(bitmap);
                    stopAnimation();
                }
            }, 0, 0, null, this);
            ir.setRetryPolicy(retryPolicy60s);
            AppApplication.getInstance().getRequestQueue().add(ir);
        } else {
            cv.setImageBitmap(source.second);
        }
        selectCollageView(cv);
        cv.setOnTouchListener(new Click2SelectListener());
        
        editList.addRecord(new Edit(cv));
        layoutCollageArea.addView(cv, lp);
        return cv;
    }
    
    @Override
    public void onBackPressed() {
        if (!productMain_Fragment.onBackPressed()){
        	ask4Leave();
        }else {
        	productMain_Fragment.onKeyBoardShow(false);
		}
    }
    
    private void ask4Leave() {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 0: // save
                    UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_FREE_COLLAGE_SAVE_DRAFT, null);
                    saveDraft(false);
                    finish();
                    break;
                case 1: // discard 
                    finish();
                    break;
                case 2: // cancel
                    break;
                }
            }
        };
        showConfirmDialog(R.string.collage_msg_leave_create, getResources().getStringArray(R.array.array_save_draft), handler);
    }
    
    private Rect calculateScaledRect(int width, int height, int minSide, int maxSide)
    {
        float scale = 1;
        if (width < minSide || height < minSide)
        {
            if (width < height)
            {
                scale = (float) minSide / width;
            } else
            {
                scale = (float) minSide / height;
            }
        }
        if (width > maxSide || height > maxSide)
        {
            if (width > height)
            {
                scale = (float) maxSide / width;
            } else
            {
                scale = (float) maxSide / height;
            }
        }
        return new Rect(0, 0, (int) (width * scale), (int) (height * scale));
    }
    
    public void clickBackward(View v)
    {
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_LOW_LAYER, null);
        if(selectedCollageView == null) return;
        editList.addRecord(new Edit(Type.moveDown, selectedCollageView));
        moveDown(selectedCollageView);
    }

    public void clickDelete(View v)
    {
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_DELETE, null);
        if(selectedCollageView == null) return;
        editList.addRecord(new Edit(Type.delete, selectedCollageView, layoutCollageArea.indexOfChild(selectedCollageView)));
        delete(selectedCollageView);
    }

    public void clickForward(View v)
    {
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_UP_LAYER, null);
        if(selectedCollageView == null) return;
        editList.addRecord(new Edit(Type.moveUp, selectedCollageView));
        moveUp(selectedCollageView);
    }

    public void clickRedo(View v)
    {
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_REDO, null);
        editList.redo();
    }
    
    public void clickUndo(View v)
    {
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_UNDO, null);
        editList.undo();
    }
    
    private void delete(View view)
    {
        layoutCollageArea.removeView(view);
        view.setSelected(false);
        selectedCollageView = null;
    }
    
    private void downloadImage(String url, NextStep nextStep)
    {
        startAnimation();
        ImageRequest ir = new ImageRequest(url, new VolleyImageResponseListener(
        		url, nextStep.ordinal(), downloadHandler), 0, 0, null, this);
        ir.setRetryPolicy(retryPolicy60s);
        AppApplication.getInstance().getRequestQueue().add(ir);
    }
    
    private void findView()
    {
        layoutCollageArea = (RelativeLayout) findViewById(R.id.layoutCollageArea);
        layoutSpace = (RelativeLayout) findViewById(R.id.layoutSpace);
        viewBorder = findViewById(R.id.viewCollageBorder);
        dummyView = findViewById(R.id.dummyView);
        productMain_Fragment = (ProductMainFragment) getSupportFragmentManager().findFragmentById(R.id.productMain_Fragment);
    }
    
    public String getHtml(String imgFileName, int side, String idTail)
    {
        float bitmapScale = (float) side / layoutCollageArea.getWidth();

        String css = "<style>";
        css += ".left_part .mobile-html > div > a, .left_part .desktop-html > div > a{position: relative;overflow: hidden;}";
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                CollageViewWithBorder cv = (CollageViewWithBorder) layoutCollageArea.getChildAt(i);
                css += "#a" + i + "-img"+idTail+"{";
                css += "display: inline-block;";
                css += "position: absolute;";
                css += "width: " + (int) (cv.getWidth() * bitmapScale) + "px;";
                css += "height: " + (int) (cv.getHeight() * bitmapScale) + "px;";
                css += "-webkit-transform: "
                        + cv.getCSS(layoutCollageArea, bitmapScale) + ";";
                css += "-ms-transform: "
                        + cv.getCSS(layoutCollageArea, bitmapScale) + ";";
                css += "transform: " + cv.getCSS(layoutCollageArea, bitmapScale)
                        + ";";
                css += "background-size: cover; }";
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
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                String imgUrl = (String) layoutCollageArea.getChildAt(i).getTag();
                LogUtil.i(TAG, "getHtml imgUrl: " + imgUrl);
                String url = "/product/";
                Product pro = productMap.get(imgUrl);
                if (pro != null) {
                	url = url + pro.getItemId();
				}
                htmlDoc += "<a href=\"" + url + "\"><div id=\"a" + i + "-img"+idTail+"\" \"></div></a>";
            }
        }
        htmlDoc += "</div>";
        htmlDoc += "</body>";
        htmlDoc += "</html>";
        return htmlDoc;
    }

    private void init()
    {
        cvInitMinSide = getResources().getDimensionPixelSize(R.dimen.generator_collage_initial_min_side);
        cvCornerSide = getResources().getDimensionPixelSize(R.dimen.generator_collage_image_corner_side);
        editList = new EditList(STACK_MAX_SIZE, this);
        
        layoutCollageArea.post(new Runnable()
        {
            @Override
            public void run()
            {
                layoutCollageArea.getLayoutParams().width = layoutCollageArea.getWidth();
                layoutCollageArea.getLayoutParams().height = layoutCollageArea.getHeight();
                cvInitMaxSide = (int) (layoutCollageArea.getWidth() * cvInitMaxFactor);
            }
        });
        layoutSpace.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                unselectCollageView();
                return true;
            }
        });
        
        productMain_Fragment.setOnProductListSlideListener(this);
        productMain_Fragment.setOnProductClickListener(this);
        productMain_Fragment.setOnDecorationItemClickListener(this);
//        setOnInterceptListener(this);
//        setSoftKeyBoardListener(this); //Xu
        
        productMain_Fragment.setShowErrDialogListener(new ShowErrDialogListener(){

            @Override
            public void showErrDialog(String msg) {
                showErrorDialog(msg);
            }
        });
        
        productMain_Fragment.setLoadingListener(new LoadingListener(){

            @Override
            public void onHideLoading() {
                  LogUtil.i(TAG,"event-onHideLoading()");
                  stopAnimation();
            }

            @Override
            public void onShowLoading() {
                  LogUtil.i(TAG,"event-onShowLoading()");
                  startAnimation();
            }
        });
        
        productMain_Fragment.setRequestBlockingListener(new RequestBlockingListener(){

            @Override
            public void onReleaseBlock() {
                LogUtil.i(TAG,"event-onReleaseBlock()");
//                setCanClickIntecptLayout(true);
//                showInterceotLayout(false); //Xu
            }

            @Override
            public void onRequestBlock() {
                 LogUtil.i(TAG,"event- onRequestBlock()");
//                 setCanClickIntecptLayout(false);
//                 showInterceotLayout(true); //Xu
            }
        });
        
        dummyView.setOnTouchListener(new OnTouchListener(){

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(arg1.getAction()==MotionEvent.ACTION_DOWN){
                    LogUtil.i(TAG, "dummyView Touched");
                }
                return true;
            }
        });
    }
    
    private boolean isProduct(View view)
    {
        LogUtil.i(TAG, "isProduct: " + (view instanceof CollageViewWithBorder));
        if(view instanceof CollageViewWithBorder) {
            CollageViewWithBorder cv = (CollageViewWithBorder) view;
            return cv.isProduct();
        }
        return false;
    }
    
    private void loadDraft()
    {
        File file = new File(getExternalCacheDir(), AppConfig.FREE_STYLE_DRAFT_NAME);
        
        draftMap = null;
        try
        {
            String jsonStr = FileManager.getStringFromFile(file);
            JSONArray jsonArr = new JSONArray(jsonStr);
            draftMap = new LinkedHashMap<String, DraftData>();
            LogUtil.i(TAG, "loadDraft jsonArr: " + jsonArr.length());
            for (int i = 0; i < jsonArr.length(); i++)
            {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                DraftData data = new DraftData(jsonObj);
                if(TextUtils.isEmpty(data.url))
                {
                    draftMap.put(String.valueOf(data.resId), data);
                }else
                {
                    draftMap.put(data.url, data);
                }
            }
            LogUtil.i(TAG, "loadDraft draftMap.size() " + draftMap.size());
        } catch (Exception e)
        {
            LogUtil.i(TAG, "loadDraft" + " error");
            ExceptionUtil.handle(mContext, e);
            CommonTools.deleteFileInCache(this, AppConfig.FREE_STYLE_DRAFT_NAME);
            showErrorDialog(R.string.collage_error_load_draft);
        }
        if(draftMap == null || draftMap.isEmpty()) {
            stopAnimation();
            return;
        }
        boolean noProduct = true;
        Set<String> set = draftMap.keySet();
        for (int i = 0; i < set.size(); i++)
        {
            DraftData data = draftMap.get((String)set.toArray()[i]);
            LogUtil.i(TAG, "loadDraft data == null: " + (data == null));
            if(data.url != null && data.product != null)
            {
                noProduct = false;
                productMap.put(data.url, data.product);
                downloadImage(data.url, NextStep.KeyEffectAndKeyEffect);
            }
        }
        // 
        if(noProduct)
        {
            for (int i = 0; i < set.size(); i++)
            {
                DraftData data = draftMap.get((String)set.toArray()[i]);
                String url = data.url;
                CollageViewWithBorder cv = addCollageView(new Pair<String, Bitmap>(url, null));
                cv.setTranslationX((float) data.tx);
                cv.setTranslationY((float) data.ty);
                cv.setScale((float) data.s);
                cv.setRotation((float) data.r);
            }
            stopAnimation();
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
    
    private void onLoadDraft()
    {
        if(draftLoaded) return;
        draftLoaded = true;
        File file = new File(getExternalCacheDir(), AppConfig.FREE_STYLE_DRAFT_NAME);
        LogUtil.i(TAG, "loadDraft file exist: " + file.exists());
        if(!file.exists()) {
            startDownloadDecorDB();
            return;
        }
        
        int length = getResources().getStringArray(R.array.array_continue_last_create).length;
        LogUtil.i(TAG, "onLoadDraft length: " + length);
        
        showConfirmDialog(R.string.collage_continue_last_create, getResources().getStringArray(R.array.array_continue_last_create), 
                new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                case 0:
                    UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_FREE_COLLAGE_DRAFT, null);
                    isLoadDraft = true;
                    startDownloadDecorDB();
                    break;
                case 1:
                    UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_FREE_COLLAGE_NEW, null);
                    startDownloadDecorDB();
                    break;
                }
            }
            
        });
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
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                String imgUrl = (String) layoutCollageArea.getChildAt(i).getTag();
                Product pro = productMap.get(imgUrl);
                if (pro != null) {
                	productIdList[j] = pro.getItemId();
                	j++;
				}
            }
        }
        unselectCollageView();
        URI uri = BitmapUtil.captureView(layoutCollageArea, imgFileName, GEN_OUTPUT_SIDE, GEN_OUTPUT_SIDE, 70);
        
        UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_LOOKBOOK_DESCRIPTION_FORM, null);
        
        Intent i = new Intent(this, CollageInfoActivity.class);
        i.putExtra(CollageInfoActivity.CREATE_TYPE, TAG);
        i.putExtra(CollageInfoActivity.COLLAGE_URI, uri);
        i.putExtra(CollageInfoActivity.COLLAGE_HTML, htmlDoc);
        i.putExtra(CollageInfoActivity.COLLAGE_MOBILE_HTML, mobileHtmlDoc);
        i.putExtra(CollageInfoActivity.COLLAGE_LIST, productIdList);
        startActivity(i); 
    }
    
    private void saveDraft(boolean promptDialog)
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
        {
            CollageViewWithBorder cv = (CollageViewWithBorder) layoutCollageArea.getChildAt(i);
            String url = null;
            JSONObject productJson = null;
            if (isProduct(layoutCollageArea.getChildAt(i)))
            {
                url = (String) cv.getTag();
                Product pro = productMap.get(url);
                if (pro != null) {
                	productJson = pro.getJson();
				}
            }else
            {
                url = (String) cv.getTag();
            }
            JSONObject jsonObj = new JSONObject();
            try
            {
                jsonObj.put("url", url);
                jsonObj.put("productJson",  productJson);
                jsonObj.put("scale", cv.getScaleX());
                jsonObj.put("translateX", cv.getTranslationX());
                jsonObj.put("translateY", cv.getTranslationY());
                jsonObj.put("rotate", cv.getRotation());
            } catch (JSONException e)
            {
                ExceptionUtil.handle(mContext, e);
            }
            jsonArray.put(jsonObj);
        }
        
        
        File file = new File(getExternalCacheDir(), AppConfig.FREE_STYLE_DRAFT_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(jsonArray.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            ExceptionUtil.handle(mContext, e);
        } catch (IOException e) {
            ExceptionUtil.handle(mContext, e);
        }
    }
    
    private void selectCollageView(CollageViewWithBorder cv)
    {
        if (selectedCollageView != null)
        {
            selectedCollageView.setSelected(false);
        }
        cv.setSelected(true);
        cv.invalidate();
        selectedCollageView = cv;
    }
    
    private void unselectCollageView()
    {
        if (selectedCollageView != null)
        {
            selectedCollageView.setSelected(false);
            selectedCollageView.invalidate();
            selectedCollageView = null;
        }
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
                addCollageView( new Pair<String, Bitmap>( url, bitmapMap.get(url) ));
        } else
        {
            productMap.put(url, product);
            downloadImage(url, NextStep.KeyEffectAndAddView);
        }
    }
    
    @Override
    public void onDecorationItemClick(final Decoration decoration)
    {
        int decorNumber = 0;
        for (int i = 0; i < layoutCollageArea.getChildCount(); i++)
            if (!isProduct(layoutCollageArea.getChildAt(i)))
                decorNumber++;
        
        if (decorNumber >= MAX_DECOR)
        {
            showErrorDialog(R.string.collage_msg_decor_too_many);
            return;
        }
        
        startAnimation();
        addCollageView(new Pair<String, Bitmap>(decoration.getUrl2x(), null));
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        int resId = R.string.dialog_error_msg;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            resId = R.string.dialog_error_msg;
        } else if (error instanceof AuthFailureError) {
            resId = R.string.dialog_error_msg;
        } else if (error instanceof ServerError) {
            resId = R.string.dialog_error_msg;
        } else if (error instanceof NetworkError) {
            resId = R.string.dialog_error_msg;
        } else if (error instanceof ParseError) {
            resId = R.string.dialog_error_msg;
        }
        showErrorDialog(resId);
        stopAnimation();
    }

    @Override
    public void undo(Edit edit)
    {
        unselectCollageView();
        switch (edit.type)
        {
        case add:
            delete(edit.view);
            break;
        case delete:
            layoutCollageArea.addView(edit.view, edit.index);
            break;
        case move:
            edit.view.setTranslationX(edit.value1);
            edit.view.setTranslationY(edit.valueY1);
            break;
        case moveDown:
            moveUp(edit.view);
            break;
        case moveUp:
            moveDown(edit.view);
            break;
        case rotate:
            edit.view.setRotation(edit.value1);
            break;
        case scale:
            ((CollageViewWithBorder) edit.view).setScale(edit.value1);
            break;
        default:
            break;
        }
    }

    @Override
    public void redo(Edit edit)
    {
        unselectCollageView();
        switch (edit.type)
        {
        case add:
            add(edit.view);
            break;
        case delete:
            delete(edit.view);
            break;
        case move:
            edit.view.setTranslationX(edit.value2);
            edit.view.setTranslationY(edit.valueY2);
            break;
        case moveDown:
            moveDown(edit.view);
            break;
        case moveUp:
            moveUp(edit.view);
            break;
        case rotate:
            edit.view.setRotation(edit.value2);
            break;
        case scale:
            ((CollageViewWithBorder) edit.view).setScale(edit.value2);
            break;
        default:
            break;
        }
    }

    @Override
    public void onShowSoftKeyBoard()
    {
        productMain_Fragment.onKeyBoardShow(true);
    }

    @Override
    public void onHideSoftKeyBoard()
    {
        productMain_Fragment.onKeyBoardShow(false);
    }
    
    private void startDownloadDecorDB() {
        startAnimation();
        dlDBThread = new DownloadDbThread(this, downloadDBHandler);
        dlDBThread.start();
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
    	ask4Leave();
    }
    
    @Override
    public void OnListenerRight() {
    	super.OnListenerRight();
    	save();
    }
    
    
    private class Click2SelectListener implements OnTouchListener
    {
        CollageView.status status;
        float x, y;
        PointF center;
        double prevDist;
        float prevDeg;
        float savedValue, savedValue2;

        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            switch (event.getAction() & event.getActionMasked())
            {
            case MotionEvent.ACTION_DOWN:
            {
                if (view instanceof CollageViewWithBorder)
                {
                    CollageViewWithBorder cv = (CollageViewWithBorder) view;
                    selectCollageView(cv);
                }
                    x = event.getX();
                    y = event.getY();
                    status = selectedCollageView.checkCorner(x, y);
                    center = selectedCollageView.getCenter();
                    switch (status)
                    {
                    case TRANSLATE:
                        savedValue = selectedCollageView.getTranslationX();
                        savedValue2 = selectedCollageView.getTranslationY();
                        break;
                    case ROTATE:
                        savedValue = selectedCollageView.getRotation();
                        prevDeg = (float) CommonTools.getAngle(center.x, center.y, x, y);
                        break;
                    case SCALE:
                        savedValue = selectedCollageView.getScaleX();
                        prevDist = CommonTools.getHypotenuseByPyth(center, new PointF(x, y));
                        break;
                    default:
                        break;
                    }
                    break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                switch (status)
                {
                case SCALE:
                    float scale = CommonTools.getScaleFactor(prevDist,
                    		CommonTools.getHypotenuseByPyth(center, new PointF(event.getX(), event.getY())));
                    selectedCollageView.setScale(selectedCollageView.getScaleX() * scale);
                    break;
                case ROTATE:
                    float deltaAngle = (float) CommonTools.getAngle(center.x, center.y,
                            event.getX(), event.getY()) - prevDeg;
                    float rotation = CommonTools.adjustAngle(
                            selectedCollageView.getRotation() + deltaAngle);
                    selectedCollageView.setRotation(rotation);
                    break;
                case TRANSLATE:
                	CommonTools.adjustTranslation(view, event.getX() - x, event.getY() - y);
                    break;
                default:
                    break;
                }
            }
                break;
            case MotionEvent.ACTION_UP:
            {
                switch (status)
                {
                case ROTATE:
                {
                    if (savedValue == selectedCollageView.getRotation())
                        return true;
                    editList.addRecord(new Edit(Type.rotate,
                            selectedCollageView, savedValue,
                            selectedCollageView.getRotation()));
                }
                    break;
                case SCALE:
                {
                    if (savedValue == selectedCollageView.getScaleX())
                        return true;
                    editList.addRecord(new Edit(Type.scale,
                            selectedCollageView, savedValue,
                            selectedCollageView.getScaleX()));
                }
                    break;
                case TRANSLATE:
                {
                    if (savedValue == selectedCollageView.getTranslationX()
                            && savedValue2 == selectedCollageView
                                    .getTranslationY())
                        return true;
                    editList.addRecord(new Edit(Type.move, selectedCollageView,
                            savedValue, selectedCollageView.getTranslationX(),
                            savedValue2, selectedCollageView.getTranslationY()));
                }
                    break;
                default:
                    break;
                }
            }
                break;
            }
            return true;
        }
    }
    
    private class DraftData
    {
        String url;
        JSONObject productJson;
        Product product;
        int resId;
        double s, tx, ty, r;
        
        public DraftData(JSONObject jsonObj)
        {
            try
            {
                if(jsonObj.has("url") && !TextUtils.isEmpty(jsonObj.getString("url")))
                {
                    url = jsonObj.getString("url");
                    if(jsonObj.has("productJson")) {
                        productJson = jsonObj.getJSONObject("productJson");
                    }
                }
                s = jsonObj.getDouble("scale");
                tx = jsonObj.getDouble("translateX");
                ty = jsonObj.getDouble("translateY");
                r = jsonObj.getDouble("rotate");
            } catch (Exception e)
            {
                ExceptionUtil.handle(mContext, e);
                CommonTools.deleteFileInCache(GeneratorFreeStyleActivity.this, AppConfig.FREE_STYLE_DRAFT_NAME);
                showErrorDialog(R.string.collage_error_load_draft);
            }
            if(productJson != null && url != null)
            {
                product = new Product(productJson);
            }
        }
    }
    
}
