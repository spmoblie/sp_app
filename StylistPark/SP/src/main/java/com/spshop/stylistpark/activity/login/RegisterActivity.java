package com.spshop.stylistpark.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.VerifyCode;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "RegisterActivity";

	private EditText et_email, et_verify_code, et_password, et_password_again, et_invitation_code;
	private Button btn_register;
	private ImageView iv_verify_code;
	private String emailStr, verifyCodeStr, randomCode, passwordStr, passwordAgain, invitationCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_email = (EditText) findViewById(R.id.register_et_email);
		et_verify_code = (EditText) findViewById(R.id.register_et_verify_code);
		et_password = (EditText) findViewById(R.id.register_et_passwords);
		et_password_again = (EditText) findViewById(R.id.register_et_passwords_again);
		et_invitation_code = (EditText) findViewById(R.id.register_et_invitation_code);
		iv_verify_code = (ImageView) findViewById(R.id.register_iv_verify_code);
		btn_register = (Button) findViewById(R.id.register_btn_register);
	}

	private void initView() {
		setTitle(R.string.title_register);
		btn_register.setOnClickListener(this);
		iv_verify_code.setOnClickListener(this);
		createVerifyCode();
	}

	private void regitser() {
		emailStr = et_email.getText().toString();
		// 邮箱非空
		if (emailStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_email), 1000);
			return;
		}
		// 校验邮箱格式
		if (!StringUtil.isEmail(emailStr)) {
			CommonTools.showToast(getString(R.string.login_email_format_error), 1000);
			return;
		}
		// 密码非空
		passwordStr = et_password.getText().toString();
		if (passwordStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_password), 1000);
			return;
		}
		// 校验密码
		passwordAgain = et_password_again.getText().toString();
		if (!passwordStr.equals(passwordAgain)) {
			CommonTools.showToast(getString(R.string.login_password_error), 1000);
			return;
		}
		// 校验邀请码
		invitationCode = et_invitation_code.getText().toString();
		if (invitationCode.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_invitation_code), 1000);
			return;
		}
		// 验证码非空
		verifyCodeStr = et_verify_code.getText().toString();
		if (verifyCodeStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_verify_code), 1000);
			return;
		}
		// 校验验证码
		if (!verifyCodeStr.equalsIgnoreCase(randomCode)) {
			CommonTools.showToast(getString(R.string.login_error_verify_code), 1000);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					et_verify_code.setText("");
				}
			}, 2000);
			return;
		}
		postRegisterData();
	}
	
	private void postRegisterData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_REGISTER_CODE);
	}

	/**
	 * 生成随机验证码
	 */
	private void createVerifyCode() {
		iv_verify_code.setImageBitmap(VerifyCode.getInstance().createBitmap());
		et_verify_code.setText("");
		randomCode = VerifyCode.getInstance().getCode();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_iv_verify_code:
			createVerifyCode();
			break;
		case R.id.register_btn_register:
			regitser();
			break;
		}
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
		String uri = AppConfig.URL_COMMON_INDEX_URL + "?app=register";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("email", emailStr));
		params.add(new MyNameValuePair("password", passwordStr));
		params.add(new MyNameValuePair("captchas", invitationCode));
		return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_REGISTER_CODE, uri, params, HttpUtil.METHOD_POST);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		createVerifyCode();
		if (result != null) {
			BaseEntity baseEn = (BaseEntity) result;
			if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS){ //注册成功
				CommonTools.showToast(baseEn.getErrInfo(), 2000);
				finish();
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
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
	
}
