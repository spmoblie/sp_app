package com.spshop.stylistpark.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.common.MyWebViewActivity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LangCurrTools.Currency;
import com.spshop.stylistpark.utils.LangCurrTools.Language;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UpdateAppVersion;
import com.spshop.stylistpark.utils.UserManager;
import com.tencent.stat.StatService;

public class SettingActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "SettingActivity";
	
	public static SettingActivity instance = null;
	public boolean change_language = false;
	public boolean change_currency = false;
	
	private RelativeLayout rl_language, rl_currency, rl_feedback;
	private RelativeLayout rl_version, rl_about_us, rl_logout;
	private TextView tv_lang_title, tv_lang_content, tv_cur_title, tv_cur_content;
	private TextView tv_feedback, tv_version_title, tv_version_no, tv_about_us, tv_logout;
	
	private boolean isLogined = false;
	private boolean update_fragment = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;

		findViewById();
		initView();
	}
	
	private void findViewById() {
		rl_language = (RelativeLayout) findViewById(R.id.setting_rl_language);
		rl_currency = (RelativeLayout) findViewById(R.id.setting_rl_currency);
		rl_feedback = (RelativeLayout) findViewById(R.id.setting_rl_feedback);
		rl_version = (RelativeLayout) findViewById(R.id.setting_rl_version);
		rl_about_us = (RelativeLayout) findViewById(R.id.setting_rl_about_us);
		rl_logout = (RelativeLayout) findViewById(R.id.setting_rl_logout);
		tv_lang_title = (TextView) findViewById(R.id.setting_tv_language_title);
		tv_lang_content = (TextView) findViewById(R.id.setting_tv_language_content);
		tv_cur_title = (TextView) findViewById(R.id.setting_tv_currency_title);
		tv_cur_content = (TextView) findViewById(R.id.setting_tv_currency_content);
		tv_feedback = (TextView) findViewById(R.id.setting_tv_feedback_title);
		tv_version_title = (TextView) findViewById(R.id.setting_tv_version_title);
		tv_version_no = (TextView) findViewById(R.id.setting_tv_version_content);
		tv_about_us = (TextView) findViewById(R.id.setting_tv_about_us_title);
		tv_logout = (TextView) findViewById(R.id.setting_tv_logout);
	}

	private void initView() {
		setTitle(R.string.setting);
		tv_lang_title.setText(getString(R.string.setting_language));
		tv_cur_title.setText(getString(R.string.setting_currency));
		tv_feedback.setText(getString(R.string.setting_feedback));
		tv_version_title.setText(getString(R.string.setting_version));
		tv_about_us.setText(getString(R.string.setting_about_us));
		rl_language.setOnClickListener(this);
		rl_currency.setOnClickListener(this);
		rl_feedback.setOnClickListener(this);
		rl_version.setOnClickListener(this);
		rl_about_us.setOnClickListener(this);
		rl_logout.setOnClickListener(this);
		// 当前语言
		Language lang = LangCurrTools.getLanguage(mContext);
		switch (lang) {
		case En:
			tv_lang_content.setText(getString(R.string.language_en));
			break;
		case Zh:
			tv_lang_content.setText(getString(R.string.language_zh));
			break;
		case Cn:
			tv_lang_content.setText(getString(R.string.language_cn));
			break;
		default:
			tv_lang_content.setText(getString(R.string.language_cn));
			break;
		}
		// 当前货币
		Currency cur = LangCurrTools.getCurrency(mContext);
		switch (cur) {
		case HKD:
			tv_cur_content.setText(getString(R.string.currency_hkd));
			break;
		case RMB:
			tv_cur_content.setText(getString(R.string.currency_rmb));
			break;
		case USD:
			tv_cur_content.setText(getString(R.string.currency_usd));
			break;
		default:
			tv_cur_content.setText(getString(R.string.currency_rmb));
			break;
		}
		tv_version_no.setText("V" + AppApplication.version_name);
	}
	
	private void postLogouRequest(){
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_LOGOUT_CODE);
	}
	
	@Override
	public void OnListenerLeft() {
		if (update_fragment && HomeFragmentActivity.instance != null) {
			update_fragment = false;
			HomeFragmentActivity.instance.startFragmen();
		}
		super.OnListenerLeft();
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.setting_rl_language:
			intent = new Intent(mContext, LanguageCurrencyActivity.class);
			intent.putExtra("dataType", 1);
			startActivity(intent);
			break;
		case R.id.setting_rl_currency:
			intent = new Intent(mContext, LanguageCurrencyActivity.class);
			intent.putExtra("dataType", 2);
			break;
		case R.id.setting_rl_feedback:
			intent = new Intent(mContext, FeedBackActivity.class);
			break;
		case R.id.setting_rl_version:
			UpdateAppVersion.getInstance(this, false);
			break;
		case R.id.setting_rl_about_us:
			intent = new Intent(mContext, MyWebViewActivity.class);
			intent.putExtra("title", getString(R.string.setting_about_us));
			intent.putExtra("lodUrl", "http://sports.qq.com/nba/");
			intent.putExtra("vdoUrl", "");
			break;
		case R.id.setting_rl_logout:
			if (isLogined) {
				showConfirmDialog(getString(R.string.setting_logout_confirm), getString(R.string.cancel),
						getString(R.string.confirm), true, true, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								switch (msg.what) {
									case BaseActivity.DIALOG_CANCEL_CLICK:
										break;
									case BaseActivity.DIALOG_CONFIRM_CLICK:
										postLogouRequest();
										break;
								}
							}
						});
			}else {
				openLoginActivity(TAG);
			}
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        
        if (change_language) {
        	initView();
        	change_language = false;
        	update_fragment = true;
		}
        if (change_currency) {
        	initView();
        	change_currency = false;
        	update_fragment = true;
		}
        checkLogin();
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogined();
		if (isLogined) {
			tv_logout.setText(getString(R.string.setting_logout));
		}else {
			tv_logout.setText(getString(R.string.login_login));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
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
		case AppConfig.REQUEST_SV_POST_LOGOUT_CODE:
			return sc.postLogoutRequest();
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_LOGOUT_CODE:
			if (result != null && ((BaseEntity)result).getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
				AppApplication.AppLogout(false);
				finish();
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
	
}
