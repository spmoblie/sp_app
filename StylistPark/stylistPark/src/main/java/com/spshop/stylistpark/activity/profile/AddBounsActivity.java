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
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;

public class AddBounsActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AddBounsActivity";
	
	private EditText et_bouns;
	private Button btn_confirm;
	private String bounsNo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bouns);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_bouns = (EditText) findViewById(R.id.add_bouns_et_no);
		btn_confirm = (Button) findViewById(R.id.add_bouns_btn_confirm);
	}

	private void initView() {
		setTitle(getString(R.string.add) + getString(R.string.bouns_bouns));
		btn_confirm.setOnClickListener(this);
	}

	private void addConfirm() {
		bounsNo = et_bouns.getText().toString();
		// 输入非空
		if (bounsNo.isEmpty()) {
			CommonTools.showToast(getString(R.string.bouns_input_error), 1000);
			return;
		}
		postResetData();
	}
	
	private void postResetData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_BOUNS_NO_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_bouns_btn_confirm:
			addConfirm();
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
		return sc.postBounsNoData(bounsNo);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			BaseEntity baseEn = (BaseEntity) result;
			if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
				CommonTools.showToast(getString(R.string.bouns_add_ok), 1000);
				if (BounsListActivity.instance != null) {
					BounsListActivity.instance.addBounsOk();
				}
				if (ChildFragmentFive.instance != null) { //刷新个人页数据
					ChildFragmentFive.instance.isUpdate = true;
				}
				finish();
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
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
	
}
