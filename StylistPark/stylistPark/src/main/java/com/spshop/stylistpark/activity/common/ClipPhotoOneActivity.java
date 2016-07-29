package com.spshop.stylistpark.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.ClipPhotoOneAdapter;
import com.spshop.stylistpark.entity.ClipPhotoEntity;
import com.spshop.stylistpark.utils.LogUtil;

/**
 * "选择一张相片"Activity
 */
public class ClipPhotoOneActivity extends BaseActivity{

	private static final String TAG = "ClipPhotoOneActivity";
	public static ClipPhotoOneActivity instance;
	
	private ClipPhotoEntity aibum, photoitem;
	private GridView gv;
	private ClipPhotoOneAdapter adapter;
	private int sizeNub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_photo_one);
		
		LogUtil.i(TAG, "onCreate");
		instance = this;
		AppManager.getInstance().addActivity(this);// 添加Activity到堆栈
		
		aibum = (ClipPhotoEntity) getIntent().getExtras().get("aibum");
		
		findViewById();
		initView();
	}

	private void findViewById() {
		gv = (GridView) findViewById(R.id.clip_photo_one_gridview);
	}

	private void initView() {
		setTitle(R.string.photo_select_one_title);
		
		sizeNub = aibum.getBitList().size();
		adapter = new ClipPhotoOneAdapter(this, aibum);
		gv.setAdapter(adapter);
		gv.setSelector(R.color.ui_bg_color_app);
		gv.setOnItemClickListener(gvItemClickListener);
	}

	@Override
	public void OnListenerRight() {
		photoitem = aibum.getBitList().get(sizeNub - 1);
		startClipImageActivity();
		super.OnListenerRight();
	}

	/**
	 * 跳转至相片编辑器
	 */
	private void startClipImageActivity() {
		Intent intent;
		switch (AppApplication.clip_photo_type) {
		case 1: //圆形
			intent = new Intent(this, ClipImageCircularActivity.class);
			break;
		case 2: //方形
			intent = new Intent(this, ClipImageSquareActivity.class);
			break;
		default: //圆形
			intent = new Intent(this, ClipImageCircularActivity.class);
			break;
		}
		intent.putExtra(AppConfig.ACTIVITY_CLIP_PHOTO_PATH, photoitem.getPhotoUrl());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		instance = null;
	}

	private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (sizeNub - 1 - position >= 0) {
				photoitem = aibum.getBitList().get(sizeNub - 1 - position);
				startClipImageActivity();
			}
		}
	};

}
