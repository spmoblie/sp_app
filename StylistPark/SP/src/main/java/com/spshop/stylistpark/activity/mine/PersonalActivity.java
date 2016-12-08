package com.spshop.stylistpark.activity.mine;

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
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.image.AsyncImageUpload;
import com.spshop.stylistpark.image.AsyncImageUpload.AsyncImageUploadCallback;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.OptionsManager;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spshop.stylistpark.AppApplication.screenWidth;

public class PersonalActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "PersonalActivity";

	private RelativeLayout rl_avatar, rl_nick, rl_gender, rl_birthday, rl_intro, rl_email, rl_identity;
	private ImageView iv_avatar;
	private TextView tv_nick, tv_gender, tv_birthday, tv_rank, tv_intro, tv_email, tv_auth_go, tv_auth_ok;
	private String avatarStr, nickStr, genderStr, birthdayStr, rankStr, introStr, emailStr;
	private String changeStr, changeTypeKey;
	private File saveFile;
	private int genderCode = 0;
	private boolean isAuth = false;
	private boolean isUpload = false;

	private UserInfoEntity infoEn;
	private UserManager userManager;
	private AsyncImageUpload asyncImageUpload;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		infoEn = (UserInfoEntity) getIntent().getExtras().get("data");
		userManager = UserManager.getInstance();

		findViewById();
		initView();
	}
	
	private void findViewById() {
		rl_avatar = (RelativeLayout) findViewById(R.id.personal_rl_avatar);
		rl_nick = (RelativeLayout) findViewById(R.id.personal_rl_nick);
		rl_gender = (RelativeLayout) findViewById(R.id.personal_rl_gender);
		rl_birthday = (RelativeLayout) findViewById(R.id.personal_rl_birthday);
		rl_intro = (RelativeLayout) findViewById(R.id.personal_rl_intro);
		rl_email = (RelativeLayout) findViewById(R.id.personal_rl_email);
		rl_identity = (RelativeLayout) findViewById(R.id.personal_rl_auth);
		iv_avatar = (ImageView) findViewById(R.id.personal_iv_avatar);
		tv_nick = (TextView) findViewById(R.id.personal_tv_nick_content);
		tv_gender = (TextView) findViewById(R.id.personal_tv_gender_content);
		tv_birthday = (TextView) findViewById(R.id.personal_tv_birthday_content);
		tv_rank = (TextView) findViewById(R.id.personal_tv_rank_content);
		tv_intro = (TextView) findViewById(R.id.personal_tv_intro_content);
		tv_email = (TextView) findViewById(R.id.personal_tv_email_content);
		tv_auth_go = (TextView) findViewById(R.id.personal_tv_auth_go);
		tv_auth_ok = (TextView) findViewById(R.id.personal_tv_auth_ok);
	}

	private void initView() {
		setTitle(R.string.mine_page);
		rl_avatar.setOnClickListener(this);
		rl_nick.setOnClickListener(this);
		rl_gender.setOnClickListener(this);
		rl_birthday.setOnClickListener(this);
		rl_intro.setOnClickListener(this);
		rl_email.setOnClickListener(this);
		rl_identity.setOnClickListener(this);
		setView();
	}
	
	private void setView() {
		if (infoEn != null) {
			avatarStr = infoEn.getUserAvatar();
			nickStr = infoEn.getUserNick();
			birthdayStr = infoEn.getBirthday();
			rankStr = infoEn.getUserRankName();
			introStr = infoEn.getUserIntro();
			emailStr = infoEn.getUserEmail();
			genderCode = infoEn.getGenderCode();
		}else {
			avatarStr = userManager.getUserAvatar();
			nickStr = userManager.getUserNick();
			genderCode = userManager.getUserGender();
			birthdayStr = userManager.getUserBirthday();
			rankStr = userManager.getUserRankName();
			introStr = userManager.getUserIntro();
			emailStr = userManager.getUserEmail();
		}
		switch (genderCode) { //定义性别
		case 1:
			genderStr = getString(R.string.mine_gender_male);
			break;
		case 2:
			genderStr = getString(R.string.mine_gender_female);
			break;
		default :
			genderStr = getString(R.string.mine_gender_confidential);
			break;
		}
		tv_gender.setText(genderStr);
		tv_nick.setText(nickStr);
		tv_birthday.setText(birthdayStr);
		tv_rank.setText(rankStr);
		tv_intro.setText(introStr);
		tv_email.setText(emailStr);
		if (isAuth) {
			tv_auth_go.setVisibility(View.GONE);
			tv_auth_ok.setVisibility(View.VISIBLE);
		}else {
			tv_auth_go.setVisibility(View.VISIBLE);
			tv_auth_ok.setVisibility(View.GONE);
		}
		ImageLoader.getInstance().displayImage(avatarStr, iv_avatar, OptionsManager.getInstance().getAvatarOptions());
	}

	private void uploadImage() {
		if (!isUpload) return; //防止重复上传
		if (!NetworkUtil.networkStateTips()) { //检测网络状态
			CommonTools.showToast(getString((R.string.network_fault)), 1000);
			return;
		}
		if (!StringUtil.isNull(avatarStr)) {
			startAnimation();
			CommonTools.showToast(getString(R.string.photo_upload_img, getString(R.string.mine_avatar)), 1000);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					asyncImageUpload = AsyncImageUpload.getInstance(new AsyncImageUploadCallback() {
						
						@Override
						public void uploadImageUrls(BaseEntity baseEn) {
							if (baseEn != null) {
								isUpload = baseEn.getErrCode() == 1 ? false : true;
								if (!isUpload) {
									// 刷新头像
									updateActivityData(5);
									// 清除图片缓存
									AppApplication.clearImageLoaderCache();
									CommonTools.showToast(getString(R.string.photo_upload_img_ok, getString(R.string.mine_avatar)), 1000);
								} else {
									if (!StringUtil.isNull(baseEn.getErrInfo())) {
										CommonTools.showToast(baseEn.getErrInfo(), 2000);
									} else {
										CommonTools.showToast(getString(R.string.photo_upload_avatar_fail), 2000);
									}
								}
							}else {
								CommonTools.showToast(getString(R.string.photo_upload_avatar_fail), 2000);
							}
							stopAnimation();
						}
						
					});
					Map<String, String> postData = new HashMap<String, String>();
					postData.put("fileName", avatarStr);
					asyncImageUpload.uploadImage(AppConfig.API_UPDATE_PROFILE, postData, avatarStr);
				}
			}, 2000);
		} else {
			isUpload = false;
			CommonTools.showToast(getString(R.string.photo_img_url_error, getString(R.string.mine_avatar)), 1000);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.personal_rl_avatar:
			if (isUpload) {
				uploadImage();
			}else {
				changeAvatar();
			}
			break;
		case R.id.personal_rl_nick:
			intent = new Intent(mContext, EditUserInfoActivity.class);
			intent.putExtra("titleStr", getString(R.string.mine_change_nick));
			intent.putExtra("showStr", nickStr);
			intent.putExtra("hintStr", getString(R.string.mine_input_nick));
			intent.putExtra("reminderStr", "");
			intent.putExtra("changeTypeKey", "nickname");
			startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_NICK);
			return;
		case R.id.personal_rl_gender:
			SelectListEntity selectEn = getGenderListEntity();
			intent = new Intent(mContext, SelectListActivity.class);
			intent.putExtra("data", selectEn);
			intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_5);
			startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_GENDER);
			return;
		case R.id.personal_rl_birthday:
			showDateDialog();
			break;
		case R.id.personal_rl_intro:
			intent = new Intent(mContext, EditUserInfoActivity.class);
			intent.putExtra("titleStr", getString(R.string.mine_change_intro));
			intent.putExtra("showStr", introStr);
			intent.putExtra("hintStr", getString(R.string.mine_input_intro));
			intent.putExtra("reminderStr", "");
			intent.putExtra("changeTypeKey", "intro");
			startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_INTRO);
			return;
		case R.id.personal_rl_email:
			checkEmailStatus();
			return;
		case R.id.personal_rl_auth:
			if (!isAuth) {
				//intent = new Intent(mContext, AuthenticationActivity.class);
			}
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@SuppressWarnings("ResourceType")
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
	
	private void changeAvatar() {
		AppApplication.clip_photo_type = 1; //设定裁剪相片的类型为圆形
		showListDialog(R.string.photo_change_avatar, getResources().getStringArray(R.array.array_photo_choose),
				screenWidth * 1/2, false, new Handler(){

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						switch (msg.what) {
							case 0: //拍照
								String status = Environment.getExternalStorageState();
								if (status.equals(Environment.MEDIA_MOUNTED)) { //先验证手机是否有sdcard
									try {
										saveFile = BitmapUtil.createPath("IMG_" + System.currentTimeMillis() + ".jpg", true);
										Uri uri = Uri.fromFile(saveFile);
										Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
										intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
		intent.putExtra("titleStr", getString(R.string.mine_change_email));
		intent.putExtra("showStr", emailStr);
		intent.putExtra("hintStr", getString(R.string.login_input_email));
		intent.putExtra("reminderStr", getString(R.string.mine_change_email_notice));
		intent.putExtra("changeTypeKey", "email");
		startActivityForResult(intent,AppConfig.ACTIVITY_CHANGE_USER_EMAIL);
	}

	/**
	 * 弹出对话框发送邮件确认修改
	 */
	private void showSendEmailDialog() {
		showConfirmDialog(null, getString(R.string.mine_send_email_hint, emailStr),
				getString(R.string.cancel), getString(R.string.send_confirm),
				screenWidth * 5/6, false, false, new Handler() {
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
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConfig.ACTIVITY_GET_IMAGE_VIA_CAMERA) //拍照
			{
				AppApplication.updatePhoto(saveFile);
				startClipImageActivity(saveFile.getAbsolutePath());
			}
			else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_NICK) //修改昵称
			{
				nickStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
				if (infoEn != null) {
					infoEn.setUserNick(nickStr);
				}
				userManager.saveUserNick(nickStr);
				setView();
			}
			else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_GENDER) //修改性别
			{
				genderCode = data.getExtras().getInt(AppConfig.ACTIVITY_SELECT_LIST_POSITION, 1);
				if (infoEn != null) {
					infoEn.setGenderCode(genderCode);
				}
				userManager.saveUserGender(genderCode);
				setView();
			}
			else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_INTRO) //修改简介
			{
				introStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
				if (infoEn != null) {
					infoEn.setUserIntro(introStr);
				}
				userManager.saveUserIntro(introStr);
				setView();
			}
			else if (requestCode == AppConfig.ACTIVITY_CHANGE_USER_EMAIL) //修改邮箱
			{
				emailStr = data.getExtras().getString(AppConfig.ACTIVITY_CHANGE_USER_CONTENT);
				if (infoEn != null) {
					infoEn.setUserEmail(emailStr);
				}
				userManager.saveUserEmail(emailStr);
				setView();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

        if (!StringUtil.isNull(AppApplication.clip_photo_path)) { //修改头像
        	isUpload = true;
			avatarStr = AppApplication.clip_photo_path;
        	ImageLoader.getInstance().displayImage("file://" + avatarStr, iv_avatar, OptionsManager.getInstance().getAvatarOptions());
        	uploadImage();
		}
        AppApplication.clip_photo_path = "";
		super.onResume();
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
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS:
			params.add(new MyNameValuePair("app", "is_validated"));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=send_hash_mail";
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER, uri, params, HttpUtil.METHOD_POST);

		case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_profile";
			params.add(new MyNameValuePair(changeTypeKey, changeStr));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE, uri, params, HttpUtil.METHOD_POST);
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
					if (StringUtil.isNull(emailStr)) {
						showServerBusy();
					} else {
						showSendEmailDialog(); //需邮件确认
					}
				}
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					CommonTools.showToast(getString(R.string.mine_send_email_ok), 1000);
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(baseEn.getErrInfo(), 2000);
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
	private SelectListEntity getGenderListEntity() {
		SelectListEntity selectEn = new SelectListEntity();
		selectEn.setTypeName(getString(R.string.mine_change_gender));
		ArrayList<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
		SelectListEntity childEn1 = new SelectListEntity();
		childEn1.setChildId(1);
		childEn1.setChildShowName(getString(R.string.mine_gender_male));
		childLists.add(childEn1);
		SelectListEntity childEn2 = new SelectListEntity();
		childEn2.setChildId(2);
		childEn2.setChildShowName(getString(R.string.mine_gender_female));
		childLists.add(childEn2);
		/*SelectListEntity childEn3 = new SelectListEntity();
		childEn3.setChildId(3);
		childEn3.setChildShowName(getString(R.string.mine_gender_confidential));
		childLists.add(childEn3);*/
		switch (genderCode) {
		case 1:
			selectEn.setSelectEn(childEn1);
			break;
		case 2:
			selectEn.setSelectEn(childEn2);
			break;
		/*case 3:
			selectEn.setSelectEn(childEn3);
			break;*/
		}
		selectEn.setChildLists(childLists);
		return selectEn;
	}
	
}
