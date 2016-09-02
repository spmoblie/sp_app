package com.spshop.stylistpark.activity.profile;

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

public class AuthenticationActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AuthenticationActivity";

	private EditText et_auth_name, et_auth_number;
	private Button btn_submit;
	private String nameStr, numberStr, hintStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_auth_name = (EditText) findViewById(R.id.authentication_et_name);
		et_auth_number = (EditText) findViewById(R.id.authentication_et_number);
		btn_submit = (Button) findViewById(R.id.authentication_btn_submit);
	}

	private void initView() {
		setTitle(R.string.money_auth);
		hintStr = getString(R.string.not_empty);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.authentication_btn_submit:
			postAuditData();
			break;
		}
	}

	private void postAuditData() {
		nameStr = et_auth_name.getText().toString();
		numberStr = et_auth_number.getText().toString();
		if (nameStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.money_auth_input_name_hint) + hintStr, 1000);
			return;
		}
		if (numberStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.money_auth_input_number_hint) + hintStr, 1000);
			return;
		}
		startAnimation();
		postAuthName();
	}

	private void postAuthName() {
		request(AppConfig.REQUEST_SV_POST_AUTH_NAME);
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
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		return sc.postAuthData(nameStr, numberStr);
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
