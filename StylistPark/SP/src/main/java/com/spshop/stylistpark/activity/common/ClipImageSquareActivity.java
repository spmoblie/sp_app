package com.spshop.stylistpark.activity.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.widgets.ClipImageView;

import java.io.File;

public class ClipImageSquareActivity extends BaseActivity{
	
	public static ClipImageSquareActivity instance;
	
	private String photoPath;
	private ClipImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_image_square);
		
		instance = this;
		photoPath = getIntent().getExtras().getString(AppConfig.ACTIVITY_CLIP_PHOTO_PATH);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		imageView = (ClipImageView) findViewById(R.id.clip_image_square_src_pic);
	}
	
	private void initView() {
		setTitle(R.string.photo_clip_head_title);
		setBtnRight(getString(R.string.confirm));
		// 设置需要裁剪的图片
		Bitmap bm = BitmapFactory.decodeFile(photoPath);
		if (bm != null) {
			bm = BitmapUtil.resizeImageByWidth(bm, 640);
			imageView.setImageBitmap(bm);
		}else {
			CommonTools.showToast(getString(R.string.photo_select_no_data), 1000);
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		// 此处获取剪裁后的bitmap
		Bitmap bm = imageView.clip();
		if (bm != null) {
			File file = BitmapUtil.createPath("ID_" + System.currentTimeMillis() + ".jpg", true);
			if (file == null) {
            	showErrorDialog(R.string.photo_show_save_fail);
    			return;
			}
			AppApplication.clip_photo_path = file.getPath();
			AppApplication.saveBitmapFile(bm, file, 100);
		}else {
			CommonTools.showToast(getString(R.string.photo_clip_error), 1000);
		}
		if (ClipPhotoGridActivity.instance != null) {
			ClipPhotoGridActivity.instance.finish();
		}
		if (ClipPhotoOneActivity.instance != null) {
			ClipPhotoOneActivity.instance.finish();
		}
		finish();
	}
	
	/**
	 * 获取屏幕状态栏和导航栏高度
	 */
	public int getStatusBarHeight(){
		return DeviceUtil.getStatusBarHeight(this) + getHeadHeight();
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
}
