package com.spshop.stylistpark.activity.collage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserTracker;
import com.spshop.stylistpark.widgets.SquareImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneratorTemplateChooseActivity extends BaseActivity {
    public static final String TAG = "GeneratorTemplateChooseActivity";
    public static final String TEMPLATE_JSON = "template json";
    public static final String TEMPLATE_JSON_ARRAY = "template json array";
    public static final int BACK_MENU_REQUEST = 1234;
    
    GridView gv;
    ImageAdapter adapter;
    JSONArray jsonArr;
    List<Drawable> drawableList = new ArrayList<Drawable>();
    boolean draftLoaded = false;
    private GetTemplateJsonThread mGetJsonThread = null;
    private DisplayImageOptions options;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_choose);
        
        setTitle(R.string.collage_select_template);
        options = AppApplication.getDefaultImageOptions();
        
        jsonArr = new JSONArray();
        convertJson();
        for (int i = 0; i < jsonArr.length(); i++)
        {
            try
            {
                JSONObject json = (JSONObject) jsonArr.getJSONObject(i);
                Bitmap bitmap = FileManager.getBitmapFromAssets(json.getString("localThumbName"));
                drawableList.add(new BitmapDrawable(getResources(), bitmap));
            } catch (Exception e)
            {
                ExceptionUtil.handle(e);
            }
        }
        if(jsonArr.length() != drawableList.size())
        {
            showErrorDialog("OMG! data, resource not match");
        }
        gv = (GridView) findViewById(R.id.gv);
        adapter = new ImageAdapter(this, jsonArr);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_SELECT_TEMPLATE_SELECT, null);
                UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_TEMPLATE_COLLAGE_GENERATOR, null);
                Intent i = new Intent(mContext, GeneratorTemplateActivity.class);
                JSONObject json = null;
                try {
                    json = (JSONObject) jsonArr.getJSONObject(position);
                } catch (JSONException e) {
                    ExceptionUtil.handle(e);
                }
                i.putExtra(TEMPLATE_JSON, json.toString());
                startActivityForResult(i, BACK_MENU_REQUEST);
            }
        });
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        onLoadDraft();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        getTemplateJson();
    }

    private void getTemplateJson()
    {
        startAnimation();
        mGetJsonThread = new GetTemplateJsonThread(this, mHandler);
        mGetJsonThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == BACK_MENU_REQUEST) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }
    
    @SuppressLint("HandlerLeak")
	private void onLoadDraft()
    {
        if(draftLoaded) return;
        draftLoaded = true;
        File file = new File(getExternalCacheDir(), AppConfig.TEMPLATE_DRAFT_NAME);
        LogUtil.i(TAG, "loadDraft file exist: " + file.exists());
        if(!file.exists()) return;
        
        int length = getResources().getStringArray(R.array.array_continue_last_create).length;
        LogUtil.i(TAG, "onLoadDraft length: " + length);

        showListDialog(R.string.collage_continue_last_create,
                getResources().getStringArray(R.array.array_continue_last_create), true, new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case 0:
                                UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_TEMPLATE_DRAFT, null);
                                UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_TEMPLATE_COLLAGE_GENERATOR, null);
                                Intent i = new Intent(mContext, GeneratorTemplateActivity.class);
                                startActivityForResult(i, BACK_MENU_REQUEST);
                                break;
                            case 1:
                                UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_TEMPLATE_NEW, null);
                                break;
                        }
                    }

                });
    }

    public void convertJson()
    {
        LogUtil.i(TAG, "convertJson");
        String str = FileManager.loadJSONFromAsset("template_sample.json");
        try
        {
        	if (!StringUtil.isNull(str)) {
        		jsonArr = new JSONArray(str);
			}
        } catch (Exception e)
        {
            ExceptionUtil.handle(e);
        }

    }
    
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        JSONArray jsonArr;

        public ImageAdapter(Context mContext, JSONArray jsonArr) {
            this.mContext = mContext;
            this.jsonArr = jsonArr;
        }

        public int getCount() {
           return jsonArr.length();
        }

        public Object getItem(int position) {
			try {
				return jsonArr.getJSONObject(position);
			} catch (JSONException e) {
                ExceptionUtil.handle(e);
				return null;
			}
        }

        public long getItemId(int position) {
           return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
           final SquareImageView imageView;
           if (convertView == null) {
               imageView = new SquareImageView(mContext);
               imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
               imageView.setScaleType(ImageView.ScaleType.FIT_XY);
           } else {
               imageView = (SquareImageView) convertView;
           }
           JSONObject jsonObj = (JSONObject) getItem(position);
           String imgUrl = null;
           try {
                imgUrl = jsonObj.getString("thumbUrl");
           } catch (JSONException e) {
               ExceptionUtil.handle(e);
           }
           LogUtil.i(TAG, "getView imgUrl: " + position +", " + imgUrl);
           // it was SquareNetworkImageView
           if(!TextUtils.isEmpty(imgUrl)) {
        	   ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
           }
           return imageView;
        }
        
        public void setJsonArray(JSONArray jsonArr) {
            this.jsonArr = jsonArr;
        }

    }
    
    public static class GetTemplateJsonThread extends Thread {

        private Context mCtx;
        private Handler mTHander;
        public static final int SUCCESS = 0;
        public static final int FAIL = 1;
        
        public GetTemplateJsonThread(Context context, Handler handler) {
            this.mCtx = context;
            this.mTHander = handler;
        }
        
        public void run() {
            Message msg = new Message();
            msg.what = FAIL;
            try {
                JSONArray jsonArray = ServiceContext.getServiceContext().getCollageTemplateList();
                if (jsonArray != null) {
                	msg.obj = jsonArray;
                	msg.what = SUCCESS;
				}else {
                	msg.what = FAIL;
				}
            } catch (Exception e) {
                ExceptionUtil.handle(e);
            }
            if (!interrupted()) {
                mTHander.sendMessage(msg);
            }
        }
    }
    
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GetTemplateJsonThread.SUCCESS:
                stopAnimation();
                LogUtil.i(TAG, "GetTemplateJsonThread handleMessage SUCCESS msg.obj == null: "+(msg.obj == null));
                jsonArr = (JSONArray) msg.obj;
                adapter.setJsonArray(jsonArr);
                adapter.notifyDataSetChanged();
                break;
            case GetTemplateJsonThread.FAIL:
                LogUtil.i(TAG, "GetTemplateJsonThread handleMessage FAIL");
                stopAnimation();
                showErrorDialog("");
                break;
            }
        }
    };

}
