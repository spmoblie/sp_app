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

	public static final int TYPE_PAGE_0 = 0;
	public static final int TYPE_PAGE_1 = 1;
	public static final int TYPE_PAGE_2 = 2;

	private EditText et_coupon;
	private Button btn_confirm;
	private int pageType = TYPE_PAGE_0;
	private String couponNo, hintStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_coupon);

		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			pageType = bundle.getInt("pageType", TYPE_PAGE_0);
		}

		findViewById();
		initView();
	}

	private void findViewById() {
		et_coupon = (EditText) findViewById(R.id.add_coupon_et_no);
		btn_confirm = (Button) findViewById(R.id.add_coupon_btn_confirm);
	}

	private void initView() {
		switch (pageType) {
			case TYPE_PAGE_1: //充值
				setTitle(R.string.money_recharge);
				hintStr = getString(R.string.money_recharge_input_hint);
				et_coupon.setHint(hintStr);
				break;
			case TYPE_PAGE_2: //达人
				setTitle(R.string.money_auth_daren);
				hintStr = getString(R.string.money_auth_daren_input_hint);
				et_coupon.setHint(hintStr);
				break;
			default:
				setTitle(getString(R.string.add) + getString(R.string.coupon_coupon));
				hintStr = getString(R.string.coupon_input_error);
				break;
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
		switch (pageType) {
			case TYPE_PAGE_1: //充值
				uri = AppConfig.URL_COMMON_USER_URL + "?act=act_account";
				params.add(new MyNameValuePair("bank", couponNo));
				params.add(new MyNameValuePair("type", "0"));
				break;
			case TYPE_PAGE_2: //达人
				uri = AppConfig.URL_COMMON_USER_URL + "?act=add_chain";
				params.add(new MyNameValuePair("sn", couponNo));
				break;
			default:
				params.add(new MyNameValuePair("bonus_sn", couponNo));
				break;
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
				switch (pageType) {
					case TYPE_PAGE_1: //充值
						updateActivityData(7);
						CommonTools.showToast(getString(R.string.money_recharge_success), 2000);
						break;
					case TYPE_PAGE_2: //达人
						AppApplication.AppLogout(false); //升级后重新登入
						if (PersonalActivity.instance != null) {
							PersonalActivity.instance.finish();
						}
						CommonTools.showToast(getString(R.string.money_auth_daren_success), 3000);
						break;
					default:
						if (CouponListActivity.instance != null) {
							CouponListActivity.instance.addCouponOk();
						}
						CommonTools.showToast(getString(R.string.coupon_add_ok), 2000);
						break;
				}
				finish();
			}else if (baseEn.getErrCode() == 997) { //未实名认证
				if (pageType == TYPE_PAGE_2) {
					CommonTools.showToast(getString(R.string.money_auth_daren_hint), 2000);
				}
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
