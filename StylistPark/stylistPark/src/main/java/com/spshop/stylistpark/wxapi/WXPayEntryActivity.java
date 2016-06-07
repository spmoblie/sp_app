package com.spshop.stylistpark.wxapi;


import android.annotation.SuppressLint;
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
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.cart.PostOrderActivity;
import com.spshop.stylistpark.activity.home.CategoryActivity;
import com.spshop.stylistpark.activity.profile.ChildFragmentFive;
import com.spshop.stylistpark.activity.profile.OrderDetailActivity;
import com.spshop.stylistpark.activity.profile.OrderListActivity;
import com.spshop.stylistpark.entity.PayResult;
import com.spshop.stylistpark.entity.PaymentEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.CommonTools;
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

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, OnClickListener{
	
	private static final String TAG = "WXPayEntryActivity";
	private static final int SDK_ZFB_PAY_FLAG = 1;
	public static final int PAY_ZFB = 2;
	public static final int PAY_WEIXI = 3;
	public static final int PAY_UNION = 4;
	// mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
	//private final String mMode = AppConfig.IS_PUBLISH ? "00":"01";
	private final String mMode = "00";

	private TextView tv_pay_result, tv_pay_amount;
	private ImageView iv_select_zfb, iv_select_weixi, iv_select_union;
	private RelativeLayout rl_select_zfb, rl_select_weixi, rl_select_union;
	private Button btn_confirm, btn_done_left, btn_done_right;
	private LinearLayout ll_pay_confirm, ll_pay_done;
	
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

	@SuppressLint("HandlerLeak")
	private void ask4Leave() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DIALOG_CONFIRM_CLICK:
					if (!"OrderDetailActivity".equals(rootPage)) {
						startOrderListActivity(0, OrderListActivity.TYPE_2);
					}else {
						finish();
					}
					break;
				default:
					break;
				}
			}
		};
		showConfirmDialog(R.string.abandon_confirm, getString(R.string.leave_confirm), getString(R.string.pay_continue), handler);
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
				}
			}else {
				stopAnimation();
				CommonTools.showToast(mContext, getString(R.string.pay_info_error), 1000);
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
						tv_pay_result.setText(getString(R.string.pay_success));
						updateViewStatus();
					}else {
						tv_pay_result.setText(payResultEntity.getTrade_state_desc());
					}
					break;
				}
			}else {
				tv_pay_result.setText(getString(R.string.pay_result_abnormal));
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
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stopAnimation();
			}
		}, 1000);
	}

	/**
	 * 发送支付宝支付请求
	 */
	private void sendZFBPayReq(PaymentEntity payEntity) {
		// 获取订单数据
		final String payInfo = payEntity.getAlipay();
		if (StringUtil.isNull(payInfo)) {
			CommonTools.showToast(mContext, getString(R.string.pay_info_error), 1000);
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
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stopAnimation();
			}
		}, 1000);
	}
	
	/**
	 * 发送银联支付请求
	 */
	private void sendUnionPayReq(PaymentEntity payEntity) {
		stopAnimation();
		// 获取银联支付订单号
		//String payInfo = "201604051523222282578";
		String payInfo = "201604051210026523228";
		if (!StringUtil.isNumeric(payInfo)) {
			CommonTools.showToast(mContext, getString(R.string.pay_info_error), 1000);
			return;
		}
		// 调用支付SDK
		UPPayAssistEx.startPay(this, null, null, payInfo, mMode);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 处理银联手机支付控件返回的支付结果
        if (data == null) {
            return;
        }
        // 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
        String msg = "";
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = getString(R.string.pay_success);
        } else if (str.equalsIgnoreCase("fail")) {
            msg = getString(R.string.pay_fail);
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = getString(R.string.pay_cancel);
        }
        tv_pay_result.setText(msg);
    }
	
}