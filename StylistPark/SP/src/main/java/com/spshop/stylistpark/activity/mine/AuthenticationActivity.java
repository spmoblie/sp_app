package com.spshop.stylistpark.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AuthenticationActivity";

	public static final int MODE_WEIXI = 1;
	public static final int MODE_ZFB = 2;
	public static final int MODE_UNION = 3;

	private EditText et_auth_name, et_auth_name_id, et_auth_phone, et_auth_email, et_auth_account;
	private CheckBox cb_type_wx, cb_type_zfb, cb_type_union;
	private Button btn_submit;
	private String nameStr, nameIDStr, phoneStr, emailStr, accountStr, hintStr;
	private boolean isCheckEmail = true;
	private int modeType = MODE_UNION; //提现方式
	private UserManager userManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		userManager = UserManager.getInstance();
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_auth_name = (EditText) findViewById(R.id.authentication_et_name);
		et_auth_name_id = (EditText) findViewById(R.id.authentication_et_name_id);
		et_auth_phone = (EditText) findViewById(R.id.authentication_et_phone);
		et_auth_email = (EditText) findViewById(R.id.authentication_et_email);
		et_auth_account = (EditText) findViewById(R.id.authentication_et_account);
		cb_type_wx = (CheckBox) findViewById(R.id.authentication_cb_type_wx);
		cb_type_zfb = (CheckBox) findViewById(R.id.authentication_cb_type_zfb);
		cb_type_union = (CheckBox) findViewById(R.id.authentication_cb_type_union);
		btn_submit = (Button) findViewById(R.id.authentication_btn_submit);
	}

	private void initView() {
		setTitle(R.string.money_auth);
		hintStr = getString(R.string.not_empty);
		cb_type_wx.setOnClickListener(this);
		cb_type_zfb.setOnClickListener(this);
		cb_type_union.setSelected(true);
		cb_type_union.setOnClickListener(this);
		btn_submit.setOnClickListener(this);

		nameStr = UserManager.getInstance().getUserName();
		if (!StringUtil.isNull(nameStr)) {
			et_auth_name.setText(nameStr);
			et_auth_name.setTextColor(getResources().getColor(R.color.debar_text_color));
			et_auth_name.setBackground(getResources().getDrawable(R.drawable.shape_frame_white_dfdfdf_4));
			et_auth_name.setEnabled(false);
		}
		nameIDStr = UserManager.getInstance().getUserNameID();
		if (!StringUtil.isNull(nameIDStr)) {
			et_auth_name_id.setText(nameIDStr);
			et_auth_name_id.setTextColor(getResources().getColor(R.color.debar_text_color));
			et_auth_name_id.setBackground(getResources().getDrawable(R.drawable.shape_frame_white_dfdfdf_4));
			et_auth_name_id.setEnabled(false);
		}
		phoneStr = UserManager.getInstance().getUserPhone();
		if (!StringUtil.isNull(phoneStr)) {
			et_auth_phone.setText(phoneStr);
			et_auth_phone.setTextColor(getResources().getColor(R.color.debar_text_color));
			et_auth_phone.setBackground(getResources().getDrawable(R.drawable.shape_frame_white_dfdfdf_4));
			et_auth_phone.setEnabled(false);
		}
		emailStr = UserManager.getInstance().getUserEmail();
		if (!StringUtil.isNull(emailStr)) {
			et_auth_email.setText(emailStr);
			et_auth_email.setTextColor(getResources().getColor(R.color.debar_text_color));
			et_auth_email.setBackground(getResources().getDrawable(R.drawable.shape_frame_white_dfdfdf_4));
			et_auth_email.setEnabled(false);
			isCheckEmail = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.authentication_cb_type_wx:
				if (modeType != MODE_WEIXI) {
					changeSelected(MODE_WEIXI);
				}
				break;
			case R.id.authentication_cb_type_zfb:
				if (modeType != MODE_ZFB) {
					changeSelected(MODE_ZFB);
				}
				break;
			case R.id.authentication_cb_type_union:
				if (modeType != MODE_UNION) {
					changeSelected(MODE_UNION);
				}
				break;
			case R.id.authentication_btn_submit:
				postAuditData();
				break;
		}
	}

	private void changeSelected(int typeCode) {
		modeType = typeCode;
		cb_type_wx.setSelected(false);
		cb_type_zfb.setSelected(false);
		cb_type_union.setSelected(false);
		switch (typeCode) {
			case MODE_WEIXI:
				cb_type_wx.setSelected(true);
				break;
			case MODE_ZFB:
				cb_type_zfb.setSelected(true);
				break;
			case MODE_UNION:
				cb_type_union.setSelected(true);
				break;
		}
	}

	private void postAuditData() {
		nameStr = et_auth_name.getText().toString();
		nameIDStr = et_auth_name_id.getText().toString();
		phoneStr = et_auth_phone.getText().toString();
		emailStr = et_auth_email.getText().toString();
		accountStr = et_auth_account.getText().toString();
		if (nameStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.money_auth_input_name_hint) + hintStr, 1000);
			return;
		}
		if (nameIDStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.money_auth_input_name_id_hint) + hintStr, 1000);
			return;
		}
		if (phoneStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.address_phone_number) + hintStr, 1000);
			return;
		}
		if (isCheckEmail && !StringUtil.isEmail(emailStr)) {
			CommonTools.showToast(getString(R.string.money_auth_input_email_hint) + hintStr, 1000);
			return;
		}
		/*if (accountStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.money_auth_input_account_hint) + hintStr, 1000);
			return;
		}*/
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
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_name";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("name", nameStr));
		params.add(new MyNameValuePair("name_id", nameIDStr));
		params.add(new MyNameValuePair("mobile", phoneStr));
		params.add(new MyNameValuePair("email", emailStr));
		//params.add(new MyNameValuePair("name_pay", String.valueOf(modeType)));
		//params.add(new MyNameValuePair("name_payid", accountStr));
		return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_AUTH_NAME, uri, params, HttpUtil.METHOD_POST);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			BaseEntity userEn = (BaseEntity) result;
			if (userEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS){ //提交成功
				CommonTools.showToast(getString(R.string.submit_success), 2000);
				userManager.saveUserName(nameStr);
				userManager.saveUserNameID(nameIDStr);
				userManager.saveUserPhone(phoneStr);
				userManager.saveUserEmail(emailStr);
				updateActivityData(7);
				finish();
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
