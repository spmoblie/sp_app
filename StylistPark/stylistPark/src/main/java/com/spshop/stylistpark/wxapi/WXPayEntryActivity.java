package com.spshop.stylistpark.wxapi;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.cart.PostOrderActivity;
import com.spshop.stylistpark.activity.category.CategoryActivity;
import com.spshop.stylistpark.activity.profile.ChildFragmentFive;
import com.spshop.stylistpark.activity.profile.OrderDetailActivity;
import com.spshop.stylistpark.activity.profile.OrderListActivity;
import com.spshop.stylistpark.entity.PayResult;
import com.spshop.stylistpark.entity.PaymentEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.stat.StatService;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, OnClickListener{
	
	private static final String TAG = "WXPayEntryActivity";
	public static final int PAY_ZFB = 1;
	public static final int PAY_WEIXI = 2;
	public static final int PAY_UNION = 3;
	public static final int PAY_PAL = 4;

	// 银联  mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
	private final String mMode = AppConfig.IS_PUBLISH ? "00":"01";
	// 支付宝
	private static final int SDK_ZFB_PAY_FLAG = 101;
	// PayPal
	private List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();
	private String PAYPAL_CURRENCY = "USD";
	private static final int REQUEST_CODE_PAYMENT = 1;
	private static final String PAYPAL_CLIENT_ID = AppConfig.PAYPAL_CLIENT_ID;
	private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
			.environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(PAYPAL_CLIENT_ID)
			.merchantName("Example Merchant")
			.merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
			.merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

	private TextView tv_pay_result, tv_pay_amount;
	private ImageView iv_select_zfb, iv_select_weixi, iv_select_union;
	private RelativeLayout rl_select_zfb, rl_select_weixi, rl_select_union;
	private Button btn_confirm, btn_done_left, btn_done_right;
	private LinearLayout ll_pay_type_1, ll_pay_type_2, ll_pay_confirm, ll_pay_done;
	
	private boolean isPayOk = false;
	private int payType = PAY_ZFB; //支付类型
	private String rootPage, orderSn, orderTotal, payResultStr;
	private PaymentEntity payEntity, payResultEntity;
	private ServiceContext sc = ServiceContext.getServiceContext();
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_ZFB_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				//String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					payResultStr = getString(R.string.pay_success);
					checkPayResult();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						payResultStr = getString(R.string.pay_wait_notice);
						checkPayResult();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						tv_pay_result.setText(getString(R.string.pay_fail));
					}
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
		
		api = WXAPIFactory.createWXAPI(this, AppConfig.WX_APP_ID);
		orderSn = getIntent().getExtras().getString("orderSn");
		orderTotal = getIntent().getExtras().getString("orderTotal");
		rootPage = getIntent().getExtras().getString("root");

		// 启动paypal的服务
		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
		startService(intent);
		
		findViewById();
		initView();
    }
    
	private void findViewById() {
		tv_pay_result = (TextView) findViewById(R.id.payment_tv_pay_result);
		tv_pay_amount = (TextView) findViewById(R.id.payment_tv_pay_amount);
		iv_select_zfb = (ImageView) findViewById(R.id.payment_iv_select_zfb);
		iv_select_weixi = (ImageView) findViewById(R.id.payment_iv_select_weixi);
		iv_select_union = (ImageView) findViewById(R.id.payment_iv_select_union);
		rl_select_zfb = (RelativeLayout) findViewById(R.id.payment_rl_select_zfb);
		rl_select_weixi = (RelativeLayout) findViewById(R.id.payment_rl_select_weixi);
		rl_select_union = (RelativeLayout) findViewById(R.id.payment_rl_select_union);
		ll_pay_type_1 = (LinearLayout) findViewById(R.id.payment_ll_pay_type_1);
		ll_pay_type_2 = (LinearLayout) findViewById(R.id.payment_ll_pay_type_2);
		ll_pay_confirm = (LinearLayout) findViewById(R.id.payment_ll_pay_confirm);
		ll_pay_done = (LinearLayout) findViewById(R.id.payment_ll_pay_done);
		btn_confirm = (Button) findViewById(R.id.button_confirm_btn_one);
		btn_done_left = (Button) findViewById(R.id.button_cancel_btn_left);
		btn_done_right = (Button) findViewById(R.id.button_confirm_btn_right);
	}

	private void initView() {
		setTitle(R.string.pay_title);  //设置标题
		tv_pay_amount.setText(orderTotal); //支付金额
		rl_select_zfb.setOnClickListener(this);
		rl_select_weixi.setOnClickListener(this);
		rl_select_union.setOnClickListener(this);
		btn_confirm.setText(getString(R.string.pay_confirm));
		btn_confirm.setOnClickListener(this);
		btn_done_left.setText(getString(R.string.order_list));
		btn_done_left.setOnClickListener(this);
		btn_done_right.setText(getString(R.string.cart_go_shopping));
		btn_done_right.setOnClickListener(this);

		PAYPAL_CURRENCY = LangCurrTools.getCurrencyHttpUrlValueStr();
		if (LangCurrTools.getCurrency() == LangCurrTools.Currency.HKD
				|| LangCurrTools.getCurrency() == LangCurrTools.Currency.USD) {
			payType = PAY_PAL;
			ll_pay_type_1.setVisibility(View.GONE);
			ll_pay_type_2.setVisibility(View.VISIBLE);
		} else {
			payType = PAY_ZFB;
			ll_pay_type_1.setVisibility(View.VISIBLE);
			ll_pay_type_2.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 提交支付请求
	 */
	private void postPayment() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_PAY_INFO_CODE);
	}
	
	/**
	 * 从服务器查询支付结果
	 */
	private void checkPayResult(){
		request(AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.payment_rl_select_zfb:
			if (payType != PAY_ZFB) {
				payType = PAY_ZFB;
				iv_select_zfb.setImageResource(R.drawable.btn_select_single_focused);
				iv_select_weixi.setImageResource(R.drawable.btn_select_single_normal);
				iv_select_union.setImageResource(R.drawable.btn_select_single_normal);
			}
			break;
		case R.id.payment_rl_select_weixi:
			if (payType != PAY_WEIXI) {
				payType = PAY_WEIXI;
				iv_select_zfb.setImageResource(R.drawable.btn_select_single_normal);
				iv_select_weixi.setImageResource(R.drawable.btn_select_single_focused);
				iv_select_union.setImageResource(R.drawable.btn_select_single_normal);
			}
			break;
		case R.id.payment_rl_select_union:
			if (payType != PAY_UNION) {
				payType = PAY_UNION;
				iv_select_zfb.setImageResource(R.drawable.btn_select_single_normal);
				iv_select_weixi.setImageResource(R.drawable.btn_select_single_normal);
				iv_select_union.setImageResource(R.drawable.btn_select_single_focused);
			}
			break;
		case R.id.button_confirm_btn_one:
			postPayment();
			break;
		case R.id.button_cancel_btn_left: //订单列表
			startOrderListActivity(0, OrderListActivity.TYPE_3);
			break;
		case R.id.button_confirm_btn_right: //继续购物
			startActivity(new Intent(this, CategoryActivity.class));
			finish();
			break;
		}
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
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public void onBackPressed() {
		if (!isPayOk) {
			ask4Leave();
		}else {
			finish();
		}
	}

	@Override
	public void OnListenerLeft() {
		if (!isPayOk) {
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
			if (ChildFragmentFive.instance != null) {
				ChildFragmentFive.instance.isUpdate = true; //创建新订单刷新个人页订单数据
			}
		}
		if (isPayOk) {
			if (OrderDetailActivity.instance != null) {
				OrderDetailActivity.instance.isUpdate = true;
			}
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
			if (resp.errCode == 0) {
				checkPayResult();
			}
		}else {
			tv_pay_result.setText(getString(R.string.pay_result_abnormal));
		}
	}
	
	@Override
	public Object doInBackground(int REQUESTCode) throws Exception {
        switch (REQUESTCode) {
        case AppConfig.REQUEST_SV_POST_PAY_INFO_CODE:
			payEntity = sc.postPayment(payType, orderSn);
			return payEntity;
        case AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE:
			payResultEntity = sc.checkPaymentResult(payType, orderSn);
			return payResultEntity;
        }
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_PAY_INFO_CODE:
			if (payEntity != null && payEntity.getErrCode() == 15) {
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
				case PAY_PAL: //PayPal支付
					sendPALPayReq(payEntity);
					break;
				}
			}else {
				getPayDataFail();
			}
			break;
		case AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE:
			isPayOk = true;
			if (payResultEntity != null && payResultEntity.getErrCode() == 15) {
				switch (payType) {
				case PAY_ZFB: //支付宝支付
					tv_pay_result.setText(payResultStr);
					updateViewStatus();
					break;
				case PAY_WEIXI: //微信支付
					if (payResultEntity.getTrade_state().equals("SUCCESS")) {
						tv_pay_result.setText(R.string.pay_success);
						updateViewStatus();
					}else {
						tv_pay_result.setText(payResultEntity.getTrade_state_desc());
					}
					break;
				}
			}else {
				tv_pay_result.setText(R.string.pay_result_abnormal);
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	/**
	 * 支付完成后更新界面显示状态
	 */
	private void updateViewStatus() {
		ll_pay_confirm.setVisibility(View.GONE);
		ll_pay_done.setVisibility(View.VISIBLE);
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
		
		api.registerApp(AppConfig.WX_API_KEY);
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
				String result = alipay.pay(payInfo);

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
		String payInfo = "201604051210026523228";
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
	private void sendPALPayReq(PaymentEntity payEntity) {
		// 获取支付订单号
		String payInfo = "201604051210026523228";
		if (!StringUtil.isNumeric(payInfo)) {
			getPayDataFail();
			return;
		}
		// 构建支付信息
		addToCart(payEntity);
		PayPalPayment thingsToBuy = prepareFinalCart();
		// 调用支付SDK
		Intent intent = new Intent(WXPayEntryActivity.this, PaymentActivity.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
		// 结束加载动画
		getPayDataSuccess();
	}

	public void addToCart(PaymentEntity payEn) {
		productsInCart.clear();
		PayPalItem item = new PayPalItem("sample item #1", 2, new BigDecimal("0.01"), PAYPAL_CURRENCY, "sku-12345678");
		productsInCart.add(item);
	}

	private PayPalPayment prepareFinalCart() {
		PayPalItem[] items = new PayPalItem[productsInCart.size()];
		items = productsInCart.toArray(items);

		// Total amount
		BigDecimal subtotal = PayPalItem.getItemTotal(items);
		// If you have shipping cost, add it here
		BigDecimal shipping = new BigDecimal("0.0");
		// If you have tax, add it here
		BigDecimal tax = new BigDecimal("0.0");
		PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
		BigDecimal amount = subtotal.add(shipping).add(tax);

		PayPalPayment payment = new PayPalPayment(
				amount, PAYPAL_CURRENCY,
				"Description about transaction. This will be displayed to the user.",
				PayPalPayment.PAYMENT_INTENT_SALE);
		payment.items(items).paymentDetails(paymentDetails);
		payment.custom("This is text that will be associated with the payment that the app can use.");

		return payment;
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
		}, 1000);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String msg = "";
		if (requestCode == REQUEST_CODE_PAYMENT) {
			// PayPal
			if (resultCode == Activity.RESULT_OK) {
				msg = getString(R.string.pay_success);
				PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						String paymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
						String payment_client = confirm.getPayment().toJSONObject().toString();
						LogUtil.i(TAG, "paymentId: " + paymentId + ", payment_json: " + payment_client);
						// 发送支付ID到你的服务器进行验证
						updateViewStatus();
					} catch (JSONException e) {
						ExceptionUtil.handle(e);
					}
				}
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				msg = getString(R.string.pay_cancel);
			}
			else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				msg = getString(R.string.pay_fail);
			}
		} else {
			// 银联手机支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
			String str = data.getExtras().getString("pay_result");
			if (str.equalsIgnoreCase("success")) {
				msg = getString(R.string.pay_success);
				updateViewStatus();
			} else if (str.equalsIgnoreCase("fail")) {
				msg = getString(R.string.pay_fail);
			} else if (str.equalsIgnoreCase("cancel")) {
				msg = getString(R.string.pay_cancel);
			}
		}
		tv_pay_result.setText(msg);
    }
	
}