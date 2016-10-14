package com.spshop.stylistpark.activity.mine;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * 提现页面
 */
public class WithdrawalsActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "WithdrawalsActivity";

	private EditText et_amount;
	private Button btn_confirm;
	private double amountTotal, inputAmount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdrawals);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		amountTotal = getIntent().getExtras().getDouble("amountTotal", 0);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_amount = (EditText) findViewById(R.id.withdrawals_et_amount);
		btn_confirm = (Button) findViewById(R.id.withdrawals_btn_confirm);
	}

	private void initView() {
		setTitle(R.string.money_withdrawals_confirm);
		btn_confirm.setOnClickListener(this);

		et_amount.setHint(getString(R.string.money_max_amount_hint, currStr + decimalFormat.format(amountTotal)));
		et_amount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String input = s.toString();
				// 以“.”开头
				if (input.startsWith(".")) {
					input = "0" + input;
					et_amount.setText(input);
					et_amount.setSelection(input.length());
					return;
				}
				if (input.contains(".")) {
					// 存在两个以上“.”
					String beStr = input.substring(0, input.lastIndexOf("."));
					if (beStr.contains(".")) {
						input = beStr;
						et_amount.setText(input);
						et_amount.setSelection(input.length());
						return;
					}
					// 取“.”后两位数
					if (input.length() - 1 - input.indexOf(".") > 2) {
						input = input.substring(0, input.indexOf(".") + 3);
						et_amount.setText(input);
						et_amount.setSelection(input.length());
						return;
					}
				}
				// 以“0”开头
				if (input.startsWith("0") && input.trim().length() > 1) {
					if (!input.substring(1, 2).equals(".")) {
						input = input.substring(1, input.length());
						et_amount.setText(input);
						et_amount.setSelection(input.length());
						return;
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				inputAmount = StringUtil.getDouble(et_amount.getText().toString());
				if (inputAmount > amountTotal) {
					inputAmount = amountTotal;
					et_amount.setText(decimalFormat.format(inputAmount));
					et_amount.setSelection(et_amount.length());
				}
			}
		});
	}

	private void confirmWithdrawals() {
		// 金额校验
		if (inputAmount <= 0) {
			CommonTools.showToast(getString(R.string.money_input_amount_hint), 1000);
			return;
		}
		postWithdrawalsData();
	}
	
	private void postWithdrawalsData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_WITHDRAWALS_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.withdrawals_btn_confirm:
			confirmWithdrawals();
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
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=act_account";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("amount", String.valueOf(inputAmount)));
		return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_WITHDRAWALS_CODE, uri, params, HttpUtil.METHOD_POST);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		if (result != null) {
			BaseEntity baseEn = (BaseEntity) result;
			if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS){ //确认提现OK
				CommonTools.showToast(getString(R.string.money_withdrawals_success), 2000);
				updateActivityData(7);
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
