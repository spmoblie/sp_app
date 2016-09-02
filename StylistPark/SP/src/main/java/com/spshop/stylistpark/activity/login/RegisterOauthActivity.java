package com.spshop.stylistpark.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;

/**
 * 第三方账号绑定页面
 */
public class RegisterOauthActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "RegisterOauthActivity";

	private EditText et_account, et_password;
	private Button btn_oauth, btn_register;
	private UserInfoEntity infoEn;
	private String accountStr, passwordStr, loginType, uidStr, nickname, sex, headUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_oauth);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		infoEn = (UserInfoEntity) getIntent().getExtras().get("oauthEn");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_account = (EditText) findViewById(R.id.register_oauth_et_account);
		et_password = (EditText) findViewById(R.id.register_oauth_et_passwords);
		btn_oauth = (Button) findViewById(R.id.register_oauth_btn_oath);
		btn_register = (Button) findViewById(R.id.register_oauth_btn_register);
	}

	private void initView() {
		setTitle(R.string.login_bound);
		setBtnRight(getString(R.string.skip));
		btn_oauth.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		
		if (infoEn != null) {
			loginType = infoEn.getUserName();
			uidStr = infoEn.getUserId();
			nickname = infoEn.getUserNick();
			sex = infoEn.getUserIntro();
			headUrl = infoEn.getHeadImg();
		}
	}

	private void accountOauth() {
		accountStr = et_account.getText().toString();
		// 账号非空
		if (accountStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_user_name), 1000);
			return;
		}
		// 密码非空
		passwordStr = et_password.getText().toString();
		if (passwordStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_password), 1000);
			return;
		}
		postAccountOauthData();
	}
	
	private void postAccountOauthData() {
		if (!StringUtil.isNull(loginType) && !StringUtil.isNull(uidStr)) {
			startAnimation();
			request(AppConfig.REQUEST_SV_POST_REGISTER_OAUTH_CODE);
		}else {
			CommonTools.showToast(getString(R.string.share_msg_error_oauth), 1000);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_oauth_btn_oath:
			accountOauth();
			break;
		case R.id.register_oauth_btn_register:
			startActivity(new Intent(mContext, RegisterActivity.class));
			break;
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		accountStr = "";
		passwordStr = "";
		postAccountOauthData();
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
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
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		return sc.postRegisterOauthData(accountStr, passwordStr, loginType, uidStr, nickname, sex, headUrl);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			UserInfoEntity userEn = (UserInfoEntity) result;
			if (userEn.getErrCode() == 1){ //绑定成功
				UserManager.getInstance().saveUserLoginSuccess(userEn.getUserId());
				if (LoginActivity.instance != null) {
					LoginActivity.instance.finish();
				}
				finish();
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
