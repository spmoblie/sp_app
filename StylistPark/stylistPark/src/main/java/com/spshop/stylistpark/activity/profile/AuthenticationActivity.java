package com.spshop.stylistpark.activity.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.ClipImageSquareActivity;
import com.spshop.stylistpark.activity.common.ClipPhotoGridActivity;
import com.spshop.stylistpark.activity.common.ShowPhotoActivity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.image.AsyncImageUpload;
import com.spshop.stylistpark.image.AsyncImageUpload.AsyncImageUploadCallback;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AuthenticationActivity";
	private static final String LOCAL_TEMP_IMG_DIR = "StylistPark/MicroMsg/Camera/SP";

	private String localTempImgFileName = "";
	private String clipPhotoPath, nameStr;
	private boolean isUpload = false;
	private boolean uploadOk = false;
	
	private ImageView iv_preview, iv_camera, iv_select;
	private EditText et_auth_name;
	private Button btn_submit;
	private DisplayImageOptions options;
	private AsyncImageUpload asyncImageUpload;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		options = AppApplication.getImageOptions(0, R.drawable.icon_authentication, true);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		iv_preview = (ImageView) findViewById(R.id.authentication_iv_preview);
		iv_camera = (ImageView) findViewById(R.id.authentication_iv_camera);
		iv_select = (ImageView) findViewById(R.id.authentication_iv_select);
		et_auth_name = (EditText) findViewById(R.id.authentication_et_name);
		btn_submit = (Button) findViewById(R.id.authentication_btn_submit);
	}

	private void initView() {
		setTitle(R.string.money_auth);
		iv_preview.setOnClickListener(this);
		iv_camera.setOnClickListener(this);
		iv_select.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		AppApplication.clip_photo_type = 2; //设定裁剪相片的类型为方形
		switch (v.getId()) {
		case R.id.authentication_iv_preview: //查看相片
			if (!StringUtil.isNull(clipPhotoPath)) {
				ArrayList<String> pathLists = new ArrayList<String>();
				pathLists.add(clipPhotoPath);
				Intent intent = new Intent(mContext, ShowPhotoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList(AppConfig.ACTIVITY_SHOW_PHOTO_LIST, pathLists);
				bundle.putInt("position", 0);
				intent.putExtras(bundle);
				startActivityForResult(intent, AppConfig.ACTIVITY_SHOW_PHOTO_PICKER);
			}
			break;
		case R.id.authentication_iv_camera: //拍照
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) { //先验证手机是否有sdcard
				try {
					File dir = new File(Environment.getExternalStorageDirectory(), LOCAL_TEMP_IMG_DIR);
					if (!dir.exists()) dir.mkdirs();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					localTempImgFileName = "microMsg." + System.currentTimeMillis() + ".jpg";
					File f = new File(dir, localTempImgFileName);
					Uri u = Uri.fromFile(f);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					startActivityForResult(intent,AppConfig.ACTIVITY_GET_IMAGE_VIA_CAMERA);
				} catch (ActivityNotFoundException e) {
					ExceptionUtil.handle(e);
					CommonTools.showToast(getString(R.string.photo_save_directory_error), 1000);
				}
			}else{ 
				CommonTools.showToast(getString(R.string.photo_save_sd_error), 1000);
			} 
			break;
		case R.id.authentication_iv_select: //选择相片
			startActivity(new Intent(this, ClipPhotoGridActivity.class));
			break;
		case R.id.authentication_btn_submit:
			postAuditData();
			break;
		}
	}

	private void postAuditData() {
		nameStr = et_auth_name.getText().toString();
		if (nameStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.money_auth_input_name_hint), 1000);
			return;
		}
		if (!isUpload) {
			CommonTools.showToast(getString(R.string.photo_ID_photo_null), 1000);
			return;
		}
		if (!uploadOk) { //身份证照片未上传
			uploadImage();
		}else {
			startAnimation();
			postAuthName();
		}
	}

	private void uploadImage() {
		if (!NetworkUtil.networkStateTips()) { //检测网络状态
			CommonTools.showToast(getString((R.string.network_fault)), 1000);
			return;
		}
		if (!StringUtil.isNull(clipPhotoPath)) {
			startAnimation();
			CommonTools.showToast(getString(R.string.photo_upload_img, getString(R.string.photo_ID_photo)), 1000);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					asyncImageUpload = AsyncImageUpload.getInstance(new AsyncImageUploadCallback() {
						
						@Override
						public void uploadImageUrls(BaseEntity baseEn) {
							if (baseEn != null) {
								uploadOk = baseEn.getErrCode() == 1 ? true : false;
								if (uploadOk) {
									asyncImageUpload.quit();
									postAuthName();
									//CommonTools.showToast(getString(R.string.photo_upload_img_ok, getString(R.string.photo_ID_photo)), 1000);
								} else {
									CommonTools.showToast(getString(R.string.photo_upload_ID_fail), 2000);
									stopAnimation();
								}
							}else {
								CommonTools.showToast(getString(R.string.photo_upload_ID_fail), 2000);
								stopAnimation();
							}
						}
						
					});
					Map<String, String> postData = new HashMap<String, String>();
					postData.put("userid", UserManager.getInstance().getUserId());
					asyncImageUpload.uploadImage("", postData, clipPhotoPath);
				}
			}, 2000);
		} else {
			isUpload = false;
			CommonTools.showToast(getString(R.string.photo_img_url_error, getString(R.string.photo_ID_photo)), 1000);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConfig.ACTIVITY_SHOW_PHOTO_PICKER) { //查看相片
			if (resultCode == RESULT_OK) {
				ArrayList<String> paths = data.getExtras().getStringArrayList(AppConfig.ACTIVITY_SHOW_PHOTO_LIST);
				if (paths == null || paths.size() == 0) {
					showClipPhoto("");
				}
			}
		} else if (requestCode == AppConfig.ACTIVITY_GET_IMAGE_VIA_CAMERA) { //拍照
			if(resultCode == RESULT_OK ) { 
				String path = Environment.getExternalStorageDirectory()+"/"+LOCAL_TEMP_IMG_DIR+"/"+localTempImgFileName;
				AppApplication.updatePhoto(new File(path));
				startClipImageActivity(path);
			} 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 跳转至相片编辑器
	 */
	private void startClipImageActivity(String path) {
		Intent intent = new Intent(this, ClipImageSquareActivity.class);
		intent.putExtra(AppConfig.ACTIVITY_CLIP_PHOTO_PATH, path);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void showClipPhoto(String path) {
		clipPhotoPath = path;
		ImageLoader.getInstance().displayImage("file://" + clipPhotoPath, iv_preview, options);
	}
	
	private void postAuthName() {
		request(AppConfig.REQUEST_SV_POST_AUTH_NAME);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

        if (!StringUtil.isNull(AppApplication.clip_photo_path)) {
        	isUpload = true;
        	showClipPhoto(AppApplication.clip_photo_path);
		}
        AppApplication.clip_photo_path = "";
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
        // 销毁对象
        if (asyncImageUpload != null) {
			asyncImageUpload.clearInstance();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		//return sc.postAuthName(nameStr);
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			UserInfoEntity userEn = (UserInfoEntity) result;
			if (userEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS){ //提交成功
				CommonTools.showToast(getString(R.string.submit_success), 2000);
			}else if (userEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
				// 登入超时，交BaseActivity处理
			}else {
				if (StringUtil.isNull(userEn.getErrInfo())) {
					showServerBusy();
				}else {
					CommonTools.showToast(userEn.getErrInfo(), 2000);
				}
			}
		}else {
			showServerBusy();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
	
}
