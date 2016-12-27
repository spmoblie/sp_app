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

public class ResetPasswordActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "ResetPasswordActivity";

	private EditText et_email, et_verify_code;
	private Button btn_send;
	private ImageView iv_verify_code;
	private String emailStr, verifyCodeStr, randomCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_email = (EditText) findViewById(R.id.reset_password_et_email);
		et_verify_code = (EditText) findViewById(R.id.reset_password_et_verify_code);
		iv_verify_code = (ImageView) findViewById(R.id.reset_password_iv_verify_code);
		btn_send = (Button) findViewById(R.id.reset_password_btn_send);
	}

	private void initView() {
		setTitle(R.string.login_reset_password);
		iv_verify_code.setOnClickListener(this);
		btn_send.setOnClickListener(this);
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
		postResetData();
	}
	
	private void postResetData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_RESET_PASSWORD_CODE);
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
		case R.id.reset_password_iv_verify_code:
			createVerifyCode();
			break;
		case R.id.reset_password_btn_send:
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
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=send_pwd_email";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("email", emailStr));
		return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_RESET_PASSWORD_CODE, uri, params, HttpUtil.METHOD_POST);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			createVerifyCode();
			CommonTools.showToast(((BaseEntity) result).getErrInfo(), 2000);
		}else {
			showServerBusy();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
	
}
