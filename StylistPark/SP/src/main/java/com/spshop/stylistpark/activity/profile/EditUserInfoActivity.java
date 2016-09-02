package com.spshop.stylistpark.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;

public class EditUserInfoActivity extends BaseActivity {
	
	private static final String TAG = "EditUserInfoActivity";
	
	private EditText et_content;
	private ImageView iv_clear;
	private TextView tv_reminder;
	
	private boolean isChange = false;
	private boolean isPost = true;
	private String titleStr, showStr, hintStr, reminderStr, changeTypeKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		Intent intent = getIntent();
		titleStr = intent.getExtras().getString("titleStr");
		showStr = intent.getExtras().getString("showStr");
		hintStr = intent.getExtras().getString("hintStr");
		reminderStr = intent.getExtras().getString("reminderStr");
		changeTypeKey = intent.getExtras().getString("changeTypeKey");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_content = (EditText) findViewById(R.id.edit_info_et_content);
		iv_clear = (ImageView) findViewById(R.id.edit_info_iv_clear);
		tv_reminder = (TextView) findViewById(R.id.edit_info_tv_reminder);
	}

	private void initView() {
		setTitle(titleStr);
		setBtnRight(getString(R.string.confirm));
		
		et_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					iv_clear.setVisibility(View.GONE);
				}else {
					iv_clear.setVisibility(View.VISIBLE);
				}
			}
		});
		et_content.setHint(hintStr);
		et_content.setText(showStr);
		if (!StringUtil.isNull(showStr)) {
			et_content.setSelection(showStr.length());
		}
		if (!StringUtil.isNull(reminderStr)) {
			tv_reminder.setText(reminderStr);
		}
		
		iv_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showStr = "";
				et_content.setText(showStr);
			}
		});
	}
	
	@Override
	public void OnListenerLeft() {
		super.OnListenerLeft();
		finish();
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		showStr = et_content.getText().toString();
		if (showStr.isEmpty()) {
			CommonTools.showToast(hintStr, 1000);
			return;
		}
		if ("email".equals(changeTypeKey) && !StringUtil.isEmail(showStr)) {
			CommonTools.showToast(getString(R.string.login_email_format_error), 1000);
			return;
		}
		postChangeCotent();
	}

	private void postChangeCotent() {
		if (isPost) {
			request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
			isPost = false;
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
	public void finish() {
		if (isChange && !StringUtil.isNull(showStr)) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.ACTIVITY_CHANGE_USER_CONTENT, showStr);
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		return sc.postChangeUserInfo(showStr, changeTypeKey);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		isPost = true;
		if (result != null) {
			BaseEntity baseEn = (BaseEntity) result;
			if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
				isChange = true;
				finish();
			}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
				// 登入超时，交BaseActivity处理
			}else {
				if (StringUtil.isNull(baseEn.getErrInfo())) {
					showServerBusy();
				} else {
					showErrorDialog(baseEn.getErrInfo());
				}
			}
		}else {
			showServerBusy();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
		isPost = true;
	}
	
}
