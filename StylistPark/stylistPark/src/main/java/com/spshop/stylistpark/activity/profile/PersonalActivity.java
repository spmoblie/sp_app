package com.spshop.stylistpark.activity.profile;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.ClipImageCircularActivity;
import com.spshop.stylistpark.activity.common.ClipPhotoGridActivity;
import com.spshop.stylistpark.activity.common.SelectListActivity;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.image.AsyncImageUpload;
import com.spshop.stylistpark.image.AsyncImageUpload.AsyncImageUploadCallback;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.tencent.stat.StatService;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonalActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "PersonalActivity";
	private static final String LOCAL_TEMP_IMG_DIR = "StylistPark/MicroMsg/Camera/SP";
	
	public static PersonalActivity instance = null;
	
	private RelativeLayout rl_head, rl_nick, rl_sex, rl_birthday, rl_email, rl_phone, rl_identity;
	private ImageView iv_head;
	private TextView tv_nick, tv_sex, tv_birthday, tv_email, tv_phone, tv_auth_go, tv_auth_ok;
	private String headUrl, nickStr, sexStr, birthdayStr, emailStr, phoneStr;
	private String changeStr, changeTypeKey;
	private String localTempImgFileName = "";
	private int sexCode = 0;
	private boolean isAuth = false;
	private boolean isUpload = false;
	private boolean update_fragment = false;
	
	private UserInfoEntity infoEn;
	private UserManager userManager;
	private DisplayImageOptions options;
	private AsyncImageUpload asyncImageUpload;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		infoEn = (UserInfoEntity) getIntent().getExtras().get("data");
		userManager = UserManager.getInstance();
		options = AppApplication.getNotCacheImageOptions(90, R.drawable.head_portrait);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		rl_head = (RelativeLayout) findViewById(R.id.personal_rl_head);
		rl_nick = (RelativeLayout) findViewById(R.id.personal_rl_nick);
		rl_sex = (RelativeLayout) findViewById(R.id.personal_rl_sex);
		rl_birthday = (RelativeLayout) findViewById(R.id.personal_rl_birthday);
		rl_email = (RelativeLayout) findViewById(R.id.personal_rl_email);
		rl_phone = (RelativeLayout) findViewById(R.id.personal_rl_phone);
		rl_identity = (RelativeLayout) findViewById(R.id.personal_rl_auth);
		iv_head = (ImageView) findViewById(R.id.personal_iv_head_img);
		tv_nick = (TextView) findViewById(R.id.personal_tv_nick_content);
		tv_sex = (TextView) findViewById(R.id.personal_tv_sex_content);
		tv_birthday = (TextView) findViewById(R.id.personal_tv_birthday_content);
		tv_email = (TextView) findViewById(R.id.personal_tv_email_content);
		tv_phone = (TextView) findViewById(R.id.personal_tv_phone_content);
		tv_auth_go = (TextView) findViewById(R.id.personal_tv_auth_go);
		tv_auth_ok = (TextView) findViewById(R.id.personal_tv_auth_ok);
	}

	private void initView() {
		setTitle(R.string.profile_page);
		rl_head.setOnClickListener(this);
		rl_nick.setOnClickListener(this);
		rl_sex.setOnClickListener(this);
		rl_birthday.setOnClickListener(this);
		rl_email.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		rl_identity.setOnClickListener(this);
		setView();
	}
	
	private void setView() {
		if (infoEn != null) {
			headUrl = infoEn.getHeadImg();
			nickStr = infoEn.getUserNick();
			birthdayStr = infoEn.getBirthday();
			emailStr = infoEn.getUserEmail();
			phoneStr = infoEn.getUserPhone();
			isAuth = infoEn.isAuth();
			sexCode = infoEn.getSexCode();
		}else {
			headUrl = userManager.getUserHeadImg();
			nickStr = userManager.getUserNickName();
			sexCode = userManager.getUserSex();
			birthdayStr = userManager.getUserBirthday();
			emailStr = userManager.getUserEmail();
			phoneStr = userManager.getUserPhone();
			isAuth = userManager.getUserAuth();
		}
		switch (sexCode) { //定义性别
		case 1:
			sexStr = getString(R.string.profile_gender_male);
			break;
		case 2:
			sexStr = getString(R.string.profile_gender_female);
			break;
		default :
			sexStr = getString(R.string.profile_gender_confidential);
			break;
		}
		tv_sex.setText(sexStr);
		tv_nick.setText(nickStr);
		tv_birthday.setText(birthdayStr);
		tv_email.setText(emailStr);
		tv_phone.setText(phoneStr);
		if (isAuth) {
			tv_auth_go.setVisibility(View.GONE);
			tv_auth_ok.setVisibility(View.VISIBLE);
		}else {
			tv_auth_go.setVisibility(View.VISIBLE);
			tv_auth_ok.setVisibility(View.GONE);
		}
		ImageLoader.getInstance().displayImage(AppConfig.ENVIRONMENT_PRESENT_IMG_APP + headUrl, iv_head, options);
	}

	private void uploadImage() {
		if (!isUpload) return; //防止重复上传
		if (!NetworkUtil.networkStateTips(mContext)) { //检测网络状态
			CommonTools.showToast(mContext, getString((R.string.network_fault)), 1000);
			return;
		}
		if (!StringUtil.isNull(headUrl)) {
			startAnimation();
			CommonTools.showToast(mContext, getString(R.string.photo_upload_img, getString(R.string.profile_head)), 1000);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					asyncImageUpload = AsyncImageUpload.getInstance(mContext, new AsyncImageUploadCallback() {
						
						@Override
						public void uploadImageUrls(BaseEntity baseEn) {
							if (baseEn != null) {
								isUpload = baseEn.getErrCode() == 1 ? false : true;
								if (!isUpload) {
									update_fragment = true;
									asyncImageUpload.quit();
									CommonTools.showToast(mContext, getString(R.string.photo_upload_img_ok, getString(R.string.profile_head)), 1000);
								} else {
									CommonTools.showToast(mContext, getString(R.string.photo_upload_head_fail), 2000);
								}
							}else {
								CommonTools.showToast(mContext, getString(R.string.photo_upload_head_fail), 2000);
							}
							stopAnimation();
						}
						
					});
					Map<String, String> postData = new HashMap<String, String>();
					postData.put("fileName", UserManager.getInstance().getUserId());
					asyncImageUpload.uploadImage(AppConfig.API_UPDATE_PROFILE, postData, headUrl);
				}
			}, 2000);
		} else {
			isUpload = false;
			CommonTools.showToast(mContext, getString(R.string.photo_img_url_error, getString(R.string.profile_head)), 1000);
		}
	}
	
	@Override
	public void OnListenerLeft() {
		if (update_fragment && ChildFragmentFive.instance != null) {
			ChildFragmentFive.instance.isUpdate = true; //刷新个人页数据
		}
		super.OnListenerLeft();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.personal_rl_head:
			if (isUpload) {
				uploadImage();
			}else {
				changeHeadImg();
			}
			break;
		case R.id.personal_rl_nick:
			intent = new Intent(mContext, EditUserInfoActivity.class);
			intent.putExtra("titleStr", getString(R.string.profile_change_nick));
			intent.putExtra("showStr", nickStr);
			intent.putExtra("hintStr", getString(R.string.profile_input_nick));
			intent.putExtra("reminderStr", "");
			intent.putExtra("changeTypeKey", "nickname");
			startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_NICK);
			return;
		case R.id.personal_rl_sex:
			SelectListEntity selectEn = getSexListEntity();
			intent = new Intent(mContext, SelectListActivity.class);
			intent.putExtra("data", selectEn);
			intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_5);
			startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_SEX);
			return;
		case R.id.personal_rl_birthday:
			showDateDialog();
			break;
		case R.id.personal_rl_email:
			checkEmailStatus();
			return;
		case R.id.personal_rl_phone:
			break;
		case R.id.personal_rl_auth:
			if (!isAuth) {
				intent = new Intent(mContext, AuthenticationActivity.class);
			}
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	private void showDateDialog() {
		String[] dates = null;
		if (!StringUtil.isNull(birthdayStr) && birthdayStr.contains("-")) { //解析当前生日
			dates = birthdayStr.split("-");
		}
		int year = 0;
		int month = 0;
		int day = 0;
		if (dates != null && dates.length  > 2) { //判定当前生日有效性1
			year = StringUtil.getInteger(dates[0]);
			month = StringUtil.getInteger(dates[1]) - 1;
			day = StringUtil.getInteger(dates[2]);
		}
		if (year == 0 || month == 0 || day == 0) { //判定当前生日有效性2
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		}
		new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// 修改本地生日
				String yearStr = year + "-";
				String monthStr = "";
				if (monthOfYear < 9) {
					monthStr = "0" + (monthOfYear + 1) + "-";
				}else {
					monthStr = (monthOfYear + 1) + "-";
				}
				String dayStr = "";
				if (dayOfMonth < 10) {
					dayStr = "0" + dayOfMonth;
				}else {
					dayStr = "" + dayOfMonth;
				}
				birthdayStr = yearStr + monthStr + dayStr;
				if (infoEn != null) {
					infoEn.setBirthday(birthdayStr);
				}
				userManager.saveUserBirthday(birthdayStr);
				setView();
				// 修改服务器生日
				changeStr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
				changeTypeKey = "birthday";
				postChangeUserCotent();
			}
		}, year, month, day).show();
	}
	
	private void changeHeadImg() {
		AppApplication.clip_photo_type = 1; //设定裁剪相片的类型为圆形
		showListDialog(R.string.photo_change_head, getResources().getStringArray(R.array.array_photo_choose),
				width * 1/2, false, new Handler(){

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						switch (msg.what) {
							case 0: //拍照
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
										ExceptionUtil.handle(mContext, e);
										CommonTools.showToast(mContext, getString(R.string.photo_save_directory_error), 1000);
									}
								}else{
									CommonTools.showToast(mContext, getString(R.string.photo_save_sd_error), 1000);
								}
								break;
							case 1: //本地
								startActivity(new Intent(mContext, ClipPhotoGridActivity.class));
								break;
						}
					}

				});
	}

	/**
	 * 提交修改用户资料
	 */
	private void postChangeUserCotent() {
		if (!StringUtil.isNull(changeStr) && !StringUtil.isNull(changeTypeKey)) {
			request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
		}
	}

	/**
	 * 检测邮箱可修改状态
	 */
	private void checkEmailStatus() {
		startAnimation();
		request(AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS);
	}
	
	/**
	 * 发送邮件给用户
	 */
	private void sendEmailToUser() {
		request(AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER);
	}

	/**
	 * 跳转至相片编辑器
	 */
	private void startClipImageActivity(String path) {
		Intent intent = new Intent(this, ClipImageCircularActivity.class);
		intent.putExtra(AppConfig.ACTIVITY_CLIP_PHOTO_PATH, path);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * 跳转至修改邮箱页面
	 */
	private void startChangeEmail() {
		Intent intent = new Intent(mContext, EditUserInfoActivity.class);
		intent.putExtra("titleStr", getString(R.string.profile_change_email));
		intent.putExtra("showStr", emailStr);
		intent.putExtra("hintStr", getString(R.string.login_input_email));
		intent.putExtra("reminderStr", getString(R.string.profile_change_email_notice));
		intent.putExtra("changeTypeKey", "email");
		startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_EMAIL);
	}

	/**
	 * 弹出对话框发送邮件确认修改
	 */
	private void showSendEmailDialog() {
		showConfirmDialog(null, getString(R.string.profile_send_email_hint, emailStr),
				getString(R.string.cancel), getString(R.string.send_confirm),
				width * 5/6, false, false, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case DIALOG_CANCEL_CLICK:
								break;
							case DIALOG_CONFIRM_CLICK:
								sendEmailToUser();
								break;
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConfig.ACTIVITY_GET_IMAGE_VIA_CAMERA) { //拍照
			if(resultCode == RESULT_OK ) { 
				String path = Environment.getExternalStorageDirectory()+"/"+LOCAL_TEMP_IMG_DIR+"/"+localTempImgFileName;
				AppApplication.updatePhoto(new File(path));
				startClipImageActivity(path);
			} 
		}else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_NICK) { //修改昵称
			if (resultCode == RESULT_OK) {
				nickStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
				if (infoEn != null) {
					infoEn.setUserNick(nickStr);
				}
				userManager.saveUserNickName(nickStr);
				setView();
				update_fragment = true;
			}
		}else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_SEX) { //修改性别
			if (resultCode == RESULT_OK) {
				sexCode = data.getExtras().getInt(AppConfig.ACTIVITY_CHANGE_USER_CONTENT, 1);
				if (infoEn != null) {
					infoEn.setSexCode(sexCode);
				}
				userManager.saveUserSex(sexCode);
				setView();
				update_fragment = true;
			}
		}else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_EMAIL) { //修改邮箱
			if (resultCode == RESULT_OK) {
				emailStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
				if (infoEn != null) {
					infoEn.setUserEmail(emailStr);
				}
				userManager.saveUserEmail(emailStr);
				setView();
				update_fragment = true;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        
        if (!StringUtil.isNull(AppApplication.clip_photo_path)) { //修改头像
        	isUpload = true;
        	headUrl = AppApplication.clip_photo_path;
        	ImageLoader.getInstance().displayImage("file://" + headUrl, iv_head, options);
        	uploadImage();
		}
        AppApplication.clip_photo_path = "";
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
        // 销毁对象
        if (asyncImageUpload != null) {
			asyncImageUpload.clearInstance();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.i(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS:
			return sc.checkUserEmailStatus();
		case AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER:
			return sc.sendEmailToUser();
		case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			return sc.postChangeUserInfo(changeStr, changeTypeKey);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					startChangeEmail(); //可修改
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					showSendEmailDialog(); //需邮件确认
				}
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					CommonTools.showToast(mContext, getString(R.string.profile_send_email_ok), 1000);
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(mContext, baseEn.getErrInfo(), 2000);
					}
				}
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					changeStr = "";
					changeTypeKey = "";
					update_fragment = true;
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					showServerBusy();
				}
			}else {
				showServerBusy();
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	/**
	 * 生成性别列表数据
	 */
	private SelectListEntity getSexListEntity() {
		SelectListEntity selectEn = new SelectListEntity();
		selectEn.setTypeName(getString(R.string.profile_change_sex));
		ArrayList<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
		SelectListEntity childEn1 = new SelectListEntity();
		childEn1.setChildId(1);
		childEn1.setChildShowName(getString(R.string.profile_gender_male));
		childLists.add(childEn1);
		SelectListEntity childEn2 = new SelectListEntity();
		childEn2.setChildId(2);
		childEn2.setChildShowName(getString(R.string.profile_gender_female));
		childLists.add(childEn2);
		switch (sexCode) {
		case 1:
			selectEn.setSelectEn(childEn1);
			break;
		case 2:
			selectEn.setSelectEn(childEn2);
			break;
		}
		selectEn.setChildLists(childLists);
		return selectEn;
	}
	
}
