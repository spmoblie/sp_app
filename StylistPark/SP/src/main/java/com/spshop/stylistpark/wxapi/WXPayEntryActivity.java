package com.spshop.stylistpark.wxapi;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.cart.PostOrderActivity;
import com.spshop.stylistpark.activity.mine.OrderDetailActivity;
import com.spshop.stylistpark.activity.mine.OrderListActivity;
import com.spshop.stylistpark.activity.sort.SortActivity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.PayResult;
import com.spshop.stylistpark.entity.PaymentEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, OnClickListener {
	
	private static final String TAG = "WXPayEntryActivity";
	public static final int PAY_ZFB = 12;
	public static final int PAY_WEIXI = 11;
	public static final int PAY_UNION = 13;
	public static final int PAY_PAYPAL = 14;

	public static final int PAY_SUCCESS = 1;
	public static final int PAY_CANCEL = 0;
	public static final int PAY_FAIL = -1;
	public static final int PAY_ERROR = -999;

	// 银联  mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
	private final String mMode = AppConfig.IS_PUBLISH ? "00":"01";
	// 支付宝
	private static final int SDK_ZFB_PAY_FLAG = 101;
	// PayPal
	private static final int REQUEST_CODE_PAYMENT = 1;
	private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
			.environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
			.clientId(AppConfig.PAYPAL_CLIENT_ID);

	private TextView tv_pay_amount, tv_pay_start;
	private ImageView iv_select_zfb, iv_select_weixi, iv_select_paypal, iv_select_union;
	private RelativeLayout rl_select_zfb, rl_select_weixi, rl_select_paypal, rl_select_union;
	private Button btn_confirm, btn_done_left, btn_done_right;
	private LinearLayout ll_pay_select, ll_pay_confirm, ll_pay_done;
	
	private int payStatus = PAY_CANCEL; //支付状态
	private int payType = PAY_ZFB; //支付类型
	private int checkCount = 0; //查询支付结果的次数
	private String rootPage, orderSn, orderTotal;
	private ServiceContext sc = ServiceContext.getServiceContext();
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_ZFB_PAY_FLAG: {
					PayResult payResult = new PayResult((Map<String, String>) msg.obj);
					// 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功
					if (TextUtils.equals(resultStatus, "9000")) {
						checkPayResult();
					} else if (TextUtils.equals(resultStatus, "6001")) {
						showPayResult(PAY_CANCEL);
					} else {
						showPayResult(PAY_FAIL);
					}
					break;
				}
			}
		};
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);
        
        AppManager.getInstance().addActivity(this);//添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		orderSn = getIntent().getExtras().getString("orderSn");
		orderTotal = getIntent().getExtras().getString("orderTotal");
		rootPage = getIntent().getExtras().getString("root");

		findViewById();
		initView();

		// 启动paypal的服务
		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
		startService(intent);
    }
    
	private void findViewById() {
		tv_pay_amount = (TextView) findViewById(R.id.payment_tv_pay_amount);
		tv_pay_start = (TextView) findViewById(R.id.payment_tv_pay_start);
		iv_select_zfb = (ImageView) findViewById(R.id.payment_iv_select_zfb);
		iv_select_weixi = (ImageView) findViewById(R.id.payment_iv_select_weixi);
		iv_select_paypal = (ImageView) findViewById(R.id.payment_iv_select_paypal);
		iv_select_union = (ImageView) findViewById(R.id.payment_iv_select_union);
		rl_select_zfb = (RelativeLayout) findViewById(R.id.payment_rl_select_zfb);
		rl_select_weixi = (RelativeLayout) findViewById(R.id.payment_rl_select_weixi);
		rl_select_paypal = (RelativeLayout) findViewById(R.id.payment_rl_select_paypal);
		rl_select_union = (RelativeLayout) findViewById(R.id.payment_rl_select_union);
		ll_pay_select = (LinearLayout) findViewById(R.id.payment_ll_select_pay_select);
		ll_pay_confirm = (LinearLayout) findViewById(R.id.payment_ll_pay_confirm);
		ll_pay_done = (LinearLayout) findViewById(R.id.payment_ll_pay_done);
		btn_confirm = (Button) findViewById(R.id.button_confirm_btn_one);
		btn_done_left = (Button) findViewById(R.id.button_cancel_btn_left);
		btn_done_right = (Button) findViewById(R.id.button_confirm_btn_right);
	}

	private void initView() {
		setTitle(R.string.pay_title);  //设置标题
		tv_pay_amount.setText(currStr + orderTotal); //支付金额
		rl_select_zfb.setOnClickListener(this);
		iv_select_zfb.setSelected(true);
		rl_select_weixi.setOnClickListener(this);
		rl_select_paypal.setOnClickListener(this);
		rl_select_union.setOnClickListener(this);
		btn_confirm.setText(getString(R.string.pay_confirm));
		btn_confirm.setOnClickListener(this);
		btn_done_left.setText(getString(R.string.order_list));
		btn_done_left.setOnClickListener(this);
		btn_done_right.setText(getString(R.string.cart_go_shopping));
		btn_done_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.payment_rl_select_zfb:
			if (payType != PAY_ZFB) {
				changeSelected(PAY_ZFB);
			}
			break;
		case R.id.payment_rl_select_weixi:
			if (payType != PAY_WEIXI) {
				changeSelected(PAY_WEIXI);
			}
			break;
		case R.id.payment_rl_select_paypal:
			if (payType != PAY_PAYPAL) {
				changeSelected(PAY_PAYPAL);
			}
			break;
		case R.id.payment_rl_select_union:
			if (payType != PAY_UNION) {
				changeSelected(PAY_UNION);
			}
			break;
		case R.id.button_confirm_btn_one:
			postPayment();
			break;
		case R.id.button_cancel_btn_left: //订单列表
			startOrderListActivity(0, OrderListActivity.TYPE_3);
			break;
		case R.id.button_confirm_btn_right: //继续购物
			startActivity(new Intent(this, SortActivity.class));
			finish();
			break;
		}
	}

	private void changeSelected(int typeCode) {
		if (payStatus == PAY_SUCCESS) {
			showPaySuccess();
			return;
		} else if (payStatus == PAY_ERROR) {
			checkCount = 0;
			checkPayResult();
			return;
		}
		payType = typeCode;
		iv_select_zfb.setSelected(false);
		iv_select_weixi.setSelected(false);
		iv_select_paypal.setSelected(false);
		iv_select_union.setSelected(false);
		switch (typeCode) {
			case PAY_ZFB:
				iv_select_zfb.setSelected(true);
				break;
			case PAY_WEIXI:
				iv_select_weixi.setSelected(true);
				break;
			case PAY_PAYPAL:
				iv_select_paypal.setSelected(true);
				break;
			case PAY_UNION:
				iv_select_union.setSelected(true);
				break;
		}
	}

	/**
	 * 提交支付请求
	 */
	private void postPayment() {
		if (payStatus == PAY_SUCCESS) {
			showPaySuccess();
			return;
		} else if (payStatus == PAY_ERROR) {
			checkCount = 0;
			checkPayResult();
			return;
		}
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_PAY_INFO_CODE);
	}

	private void showPaySuccess() {
		CommonTools.showToast(getString(R.string.pay_result_ok), 2000);
	}

	/**
	 * 从服务器查询支付结果
	 */
	private void checkPayResult(){
		startAnimation();
		checkCount++;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE);
			}
		}, AppConfig.LOADING_TIME);
	}

	/**
	 * 跳转到订单列表并销毁此页面
	 */
	private void startOrderListActivity(int rootType, int topType) {
//		if ("PostOrderActivity".equals(rootPage)) {
//		}
		Intent intent = new Intent(mContext, OrderListActivity.class);
		intent.putExtra("rootType", rootType);
		intent.putExtra("topType", topType);
		startActivity(intent);
		finish();
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
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public void onBackPressed() {
		if (payStatus == PAY_CANCEL || payStatus == PAY_FAIL) {
			ask4Leave();
		}else {
			finish();
		}
	}

	@Override
	public void OnListenerLeft() {
		if (payStatus == PAY_CANCEL || payStatus == PAY_FAIL) {
			ask4Leave();
		}else {
			finish();
		}
	}

	private void ask4Leave() {
		showConfirmDialog(R.string.abandon_confirm, getString(R.string.leave_confirm),
				getString(R.string.pay_continue), true, true, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case DIALOG_CANCEL_CLICK:
								if (!"OrderDetailActivity".equals(rootPage)) {
									startOrderListActivity(0, OrderListActivity.TYPE_2);
								}else {
									finish();
								}
								break;
						}
					}
				});
	}
	
	@Override
	public void finish() {
		if ("PostOrderActivity".equals(rootPage) && PostOrderActivity.instance != null) {
			PostOrderActivity.instance.finish(); //销毁确认订单页面
		}
		if (payStatus == PAY_SUCCESS || payStatus == PAY_ERROR) {
			if (OrderDetailActivity.instance != null) {
				OrderDetailActivity.instance.finish();
			}
			updateActivityData(10);
		}
		super.finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		
	}

	@Override
	public void onResp(BaseResp resp) {
		LogUtil.i(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX ) {
			if (resp.errCode == 0) { //(0:成功/-1:失败/-2:取消)
				checkPayResult();
			} else if (resp.errCode == -2) {
				showPayResult(PAY_CANCEL);
			} else {
				showPayResult(PAY_FAIL);
			}
		}
	}
	
	@Override
	public Object doInBackground(int REQUESTCode) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
        switch (REQUESTCode) {
        case AppConfig.REQUEST_SV_POST_PAY_INFO_CODE:
			params.add(new MyNameValuePair("app", "edit_payment"));
			params.add(new MyNameValuePair("pay_id", String.valueOf(payType)));
			params.add(new MyNameValuePair("order_id", orderSn));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_PAY_INFO_CODE, uri, params, HttpUtil.METHOD_GET);

        case AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE:
			params.add(new MyNameValuePair("app", "pay_log"));
			params.add(new MyNameValuePair("id", orderSn));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE, uri, params, HttpUtil.METHOD_GET);
        }
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_PAY_INFO_CODE:
			if (result != null) {
				PaymentEntity payEntity = (PaymentEntity) result;
				switch (payType) {
				case PAY_ZFB: //支付宝支付
					sendZFBPayReq(payEntity);
					break;
				case PAY_WEIXI: //微信支付
					sendWeiXiPayReq(payEntity);
					break;
				case PAY_UNION: //银联支付
					sendUnionPayReq(payEntity);
					break;
				case PAY_PAYPAL: //PayPal支付
					sendPayPalPayReq(payEntity);
					break;
				}
			}else {
				sendPayPalPayReq(null);
				//getPayDataFail();
			}
			break;
		case AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE:
			if (result != null && ((PaymentEntity)result).getErrCode() == 1) {
				showPayResult(PAY_SUCCESS);
			}else {
				if (checkCount < 3) {
					checkPayResult();
				} else {
					showPayResult(PAY_ERROR);
				}
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	/**
	 * 发送微信支付请求
	 */
	private void sendWeiXiPayReq(PaymentEntity payEntity) {
		if (payEntity == null || StringUtil.isNull(payEntity.getPrepayid())
				|| StringUtil.isNull(payEntity.getNoncestr())
				|| StringUtil.isNull(payEntity.getSign())
				|| StringUtil.isNull(payEntity.getSign())) {
			getPayDataFail();
			return;
		}
		PayReq req = new PayReq();
		req.appId = AppConfig.WX_APP_ID;
		req.partnerId = AppConfig.WX_MCH_ID;
		req.prepayId = payEntity.getPrepayid();
		req.packageValue = "Sign=WXPay";
		req.nonceStr = payEntity.getNoncestr();
		req.timeStamp = payEntity.getTimestamp();
		req.sign = payEntity.getSign();
		// 发起支付
		api.sendReq(req);
		// 结束加载动画
		getPayDataSuccess();
	}

	/**
	 * 发送支付宝支付请求
	 */
	private void sendZFBPayReq(PaymentEntity payEntity) {
		// 获取订单数据
		final String payInfo = payEntity.getAlipay();
		if (StringUtil.isNull(payInfo)) {
			getPayDataFail();
			return;
		}
		// 创建异步任务
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(WXPayEntryActivity.this);
				// 调用支付接口，获取支付结果
				Map<String, String> result = alipay.payV2(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_ZFB_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
		// 结束加载动画
		getPayDataSuccess();
	}

	/**
	 * 发送银联支付请求
	 */
	private void sendUnionPayReq(PaymentEntity payEntity) {
		// 获取银联支付订单号
		String payInfo = "201608011419051350568";
		if (!StringUtil.isNumeric(payInfo)) {
			getPayDataFail();
			return;
		}
		// 调用支付SDK
		UPPayAssistEx.startPay(this, null, null, payInfo, mMode);
		// 结束加载动画
		getPayDataSuccess();
	}

	/**
	 * 发送PayPal支付请求
	 */
	private void sendPayPalPayReq(PaymentEntity payEntity) {
		if (payEntity != null) {
			getPayDataFail();
			return;
		}
		// 构建支付信息
		PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal("99.99"),
				"USD", "Name", PayPalPayment.PAYMENT_INTENT_SALE);
		// 调用支付SDK
		Intent intent = new Intent(WXPayEntryActivity.this, PaymentActivity.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
		// 结束加载动画
		getPayDataSuccess();
	}

	private void getPayDataFail() {
		stopAnimation();
		showErrorDialog(R.string.pay_info_error);
	}

	private void getPayDataSuccess() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stopAnimation();
			}
		}, AppConfig.LOADING_TIME);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		int payCode = PAY_FAIL;
		if (requestCode == REQUEST_CODE_PAYMENT) { //PayPal支付
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						String paymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
						String payment_client = confirm.getPayment().toJSONObject().toString();
						LogUtil.i(TAG, "paymentId: " + paymentId + ", payment_json: " + payment_client);
						// 发送支付ID到你的服务器进行验证
						payCode = PAY_SUCCESS;
					} catch (JSONException e) {
						ExceptionUtil.handle(e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				payCode = PAY_CANCEL;
			}
		} else {
			// 银联手机支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
			String str = data.getExtras().getString("pay_result");
			if (str.equalsIgnoreCase("success")) {
				payCode = PAY_SUCCESS;
			} else if (str.equalsIgnoreCase("cancel")) {
				payCode = PAY_CANCEL;
			}
		}
		showPayResult(payCode);
    }

	private void showPayResult(int payCode) {
		stopAnimation();
		String startStr;
		payStatus = payCode;
		switch (payCode) {
			case PAY_SUCCESS:
				startStr = getString(R.string.pay_success);
				updateViewStatus(startStr);
				break;
			case PAY_CANCEL:
				CommonTools.showToast(getString(R.string.pay_cancel), 1000);
				break;
			case PAY_FAIL:
				CommonTools.showToast(getString(R.string.pay_fail), 3000);
				break;
			case PAY_ERROR:
				startStr = getString(R.string.pay_result_abnormal);
				showErrorDialog(startStr);
				updateViewStatus(startStr);
				break;
		}
	}

	/**
	 * 支付完成后更新界面显示状态
	 */
	private void updateViewStatus(String startStr) {
		ll_pay_confirm.setVisibility(View.GONE);
		ll_pay_done.setVisibility(View.VISIBLE);
		ll_pay_select.setVisibility(View.GONE);
		tv_pay_start.setText(startStr);
		tv_pay_start.setVisibility(View.VISIBLE);
	}

}