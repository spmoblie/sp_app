package com.spshop.stylistpark.activity.mine;

import android.os.Bundle;
import android.os.Handler;
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
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AddCouponActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AddCouponActivity";
	
	private EditText et_coupon;
	private Button btn_confirm;
	private int pageType = 0;
	private String couponNo, hintStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_coupon);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			pageType = bundle.getInt("pageType", 0);
		}
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_coupon = (EditText) findViewById(R.id.add_coupon_et_no);
		btn_confirm = (Button) findViewById(R.id.add_coupon_btn_confirm);
	}

	private void initView() {
		if (pageType == 0) {
			setTitle(getString(R.string.add) + getString(R.string.coupon_coupon));
			hintStr = getString(R.string.coupon_input_error);
		} else { //充值
			setTitle(R.string.money_recharge);
			hintStr = getString(R.string.money_recharge_input_hint);
			et_coupon.setHint(hintStr);
		}
		btn_confirm.setOnClickListener(this);
	}

	private void addConfirm() {
		couponNo = et_coupon.getText().toString();
		// 输入非空
		if (couponNo.isEmpty()) {
			CommonTools.showToast(hintStr, 1000);
			return;
		}
		postResetData();
	}
	
	private void postResetData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_COUPON_NO_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_coupon_btn_confirm:
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
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=add_bonus";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		if (pageType == 0) {
			params.add(new MyNameValuePair("bonus_sn", couponNo));
		} else { //充值
			uri = AppConfig.URL_COMMON_USER_URL + "?act=act_account";
			params.add(new MyNameValuePair("bank", couponNo));
			params.add(new MyNameValuePair("id", "0"));
		}
		return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_COUPON_NO_CODE, uri, params, HttpUtil.METHOD_POST);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			BaseEntity baseEn = (BaseEntity) result;
			if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
				if (pageType == 0) {
					if (CouponListActivity.instance != null) {
						CouponListActivity.instance.addCouponOk();
					}
					CommonTools.showToast(getString(R.string.coupon_add_ok), 1000);
				} else {
					updateActivityData(7);
					CommonTools.showToast(getString(R.string.money_recharge_success), 1000);
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
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (et_coupon != null) {
						et_coupon.setText("");
					}
				}
			}, 1000);
		}else {
			showServerBusy();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
	
}
