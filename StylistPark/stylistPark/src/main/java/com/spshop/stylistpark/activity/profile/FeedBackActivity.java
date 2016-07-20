package com.spshop.stylistpark.activity.profile;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.tencent.stat.StatService;

public class FeedBackActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "FeedBackActivity";
	
	private EditText et_feed_cotent;
	private Button btn_submit;
	private String cotentStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_feed_cotent = (EditText) findViewById(R.id.feed_back_et_content);
		btn_submit = (Button) findViewById(R.id.feed_back_btn_submit);
	}

	private void initView() {
		setTitle(R.string.setting_feedback);
		btn_submit.setOnClickListener(this);
	}

	private void backSubmit() {
		cotentStr = et_feed_cotent.getText().toString();
		// 输入非空
		if (cotentStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.setting_input_error_feedback), 1000);
			return;
		}
		postResetData();
	}
	
	private void postResetData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_FEED_BACK_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_back_btn_submit:
			backSubmit();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		return sc.postFeedBackData(cotentStr);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			BaseEntity baseEn = (BaseEntity) result;
			if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
				
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
