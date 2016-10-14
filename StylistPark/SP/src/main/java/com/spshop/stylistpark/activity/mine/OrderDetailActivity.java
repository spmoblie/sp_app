package com.spshop.stylistpark.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.TimeUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.wxapi.WXPayEntryActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "OrderDetailActivity";
	public static OrderDetailActivity instance = null;

	private TextView tv_name, tv_phone, tv_address, tv_order_no, tv_order_date, tv_order_status;
	private TextView tv_logistics_name, tv_logistics_no;
	private TextView tv_goods_total, tv_buyer, tv_invoice, tv_pay_type, tv_valid_time;
	private TextView tv_total_name, tv_total, tv_fee_name, tv_fee, tv_coupon_name, tv_coupon;
	private TextView tv_discount_name, tv_discount, tv_pay_name, tv_pay, tv_pay_now, tv_order_cacel;
	private RelativeLayout rl_pay_type;
	private LinearLayout ll_logistics, ll_goods_lists, ll_order_edit;
	
	private OrderEntity orderEn;
	private MyTimer mTimer;
	private String logisticsCode, orderId;
	private boolean isLogined, isUpdate, isSuccess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		orderId = getIntent().getExtras().getString("orderId");

		findViewById();
		initView();
	}
	
	private void findViewById() {
		tv_name = (TextView) findViewById(R.id.order_detail_tv_address_name);
		tv_phone = (TextView) findViewById(R.id.order_detail_tv_address_phone);
		tv_address = (TextView) findViewById(R.id.order_detail_tv_address_address);
		tv_order_no = (TextView) findViewById(R.id.order_detail_tv_order_no);
		tv_order_date = (TextView) findViewById(R.id.order_detail_tv_order_date);
		tv_order_status = (TextView) findViewById(R.id.order_detail_tv_order_status);
		tv_logistics_name = (TextView) findViewById(R.id.order_detail_tv_logistics_name);
		tv_logistics_no = (TextView) findViewById(R.id.order_detail_tv_logistics_no);
		tv_goods_total = (TextView) findViewById(R.id.order_detail_tv_order_goods_total);
		tv_buyer = (TextView) findViewById(R.id.order_detail_tv_buyer);
		tv_invoice = (TextView) findViewById(R.id.order_detail_tv_invoice);
		tv_pay_type = (TextView) findViewById(R.id.order_detail_tv_pay_type);
		tv_valid_time = (TextView) findViewById(R.id.order_detail_tv_valid_time);
		tv_total_name = (TextView) findViewById(R.id.order_detail_tv_price_total_name);
		tv_total = (TextView) findViewById(R.id.order_detail_tv_price_total);
		tv_fee_name = (TextView) findViewById(R.id.order_detail_tv_price_fee_name);
		tv_fee = (TextView) findViewById(R.id.order_detail_tv_price_fee);
		tv_coupon_name = (TextView) findViewById(R.id.order_detail_tv_price_coupon_name);
		tv_coupon = (TextView) findViewById(R.id.order_detail_tv_price_coupon);
		tv_discount_name = (TextView) findViewById(R.id.order_detail_tv_price_discount_name);
		tv_discount = (TextView) findViewById(R.id.order_detail_tv_price_discount);
		tv_pay_name = (TextView) findViewById(R.id.order_detail_tv_price_pay_name);
		tv_pay = (TextView) findViewById(R.id.order_detail_tv_price_pay);
		tv_pay_now = (TextView) findViewById(R.id.order_detail_tv_pay_now);
		tv_order_cacel = (TextView) findViewById(R.id.order_detail_tv_cacel_order);
		rl_pay_type = (RelativeLayout) findViewById(R.id.order_detail_rl_pay_type);
		ll_logistics = (LinearLayout) findViewById(R.id.order_detail_ll_logistics);
		ll_goods_lists = (LinearLayout) findViewById(R.id.order_detail_ll_goods_lists);
		ll_order_edit = (LinearLayout) findViewById(R.id.order_detail_ll_edit);
	}

	private void initView() {
		setTitle(R.string.title_order_detail);
		tv_pay_now.setOnClickListener(this);
		tv_order_cacel.setOnClickListener(this);
	}

	private void setView() {
		if (orderEn != null) {
			String signStr = getString(R.string.sign_semicolon);
			tv_order_no.setText(getString(R.string.order_order_no, orderEn.getOrderNo()));
			tv_order_date.setText(getString(R.string.order_order_date, 
					TimeUtil.getFormatedDateTime("yyyy-MM-dd HH:mm:ss", orderEn.getCreateTime())));
			tv_order_status.setText(orderEn.getStatusName());
			tv_logistics_name.setText(getString(R.string.order_logistics_name, orderEn.getLogisticsName()));
			tv_logistics_no.setText(getString(R.string.order_logistics_no, orderEn.getLogisticsNo()));
			//tv_goods_total.setText(getString(R.string.num_total, orderEn.getGoodsTotal()));
			tv_goods_total.setText(orderEn.getGoodsTotalStr());
			tv_buyer.setText(orderEn.getBuyerName() + signStr + orderEn.getBuyer());
			tv_invoice.setText(orderEn.getInvoiceName() + signStr + orderEn.getInvoiceType());
			tv_pay_type.setText(getString(R.string.order_pay_type, orderEn.getPayType()));
			tv_total_name.setText(orderEn.getPriceTotalName() + signStr);
			tv_total.setText(currStr + orderEn.getPriceTotal());
			tv_fee_name.setText(orderEn.getPriceFeeName() + signStr);
			tv_fee.setText(currStr + orderEn.getPriceFee());
			tv_coupon_name.setText(orderEn.getPriceCouponName() + signStr);
			tv_coupon.setText("-" + currStr + orderEn.getPriceCoupon());
			tv_discount_name.setText(orderEn.getPriceDiscountName() + signStr);
			tv_discount.setText("-" + currStr + orderEn.getPriceDiscount());
			tv_pay_name.setText(orderEn.getPricePaidName() + signStr);
			tv_pay.setText(currStr + orderEn.getPricePaid());
			// 商品列表
			addGoodsLists();
			// 物流信息
			AddressEntity addrEn = orderEn.getAddressEn();
			if (addrEn != null) {
				tv_name.setText(addrEn.getName());
				tv_phone.setText(addrEn.getPhone());
				tv_address.setText(addrEn.getAddress());
			}
			// 订单状态
			ll_logistics.setVisibility(View.GONE);
			rl_pay_type.setVisibility(View.VISIBLE);
			if (orderEn.getStatus() == 1) { //待支付
				tv_pay_name.setText(orderEn.getPricePayName() + signStr);
				tv_pay.setText(currStr + orderEn.getPricePay());

				ll_order_edit.setVisibility(View.VISIBLE);
				rl_pay_type.setVisibility(View.GONE);
				tv_pay_now.setVisibility(View.VISIBLE);
				tv_pay_now.setText(getString(R.string.order_pay_now));
				tv_order_cacel.setText(getString(R.string.order_cacel));
			}else if (orderEn.getStatus() == 3) { //待收货
				logisticsCode = StringUtil.getLogisticsCode(orderEn.getLogisticsName());
				ll_logistics.setVisibility(View.VISIBLE);
				ll_order_edit.setVisibility(View.VISIBLE);
				tv_pay_now.setVisibility(View.GONE);
				//tv_pay_now.setText(getString(R.string.order_confirm_receive));
				tv_order_cacel.setText(getString(R.string.order_check_logistic));
			}else if (orderEn.getStatus() == 5) { //已完成
				logisticsCode = StringUtil.getLogisticsCode(orderEn.getLogisticsName());
				ll_logistics.setVisibility(View.VISIBLE);
				ll_order_edit.setVisibility(View.VISIBLE);
				tv_pay_now.setVisibility(View.GONE);
				tv_order_cacel.setText(getString(R.string.order_check_logistic));
			}else {
				ll_order_edit.setVisibility(View.GONE);
			}
			// 设置付款倒计时
//			tv_valid_time.setVisibility(View.GONE);
//			if (orderEn.getStatus() == 1) { //待付款
//				String timeStr = TimeUtil.getTextTimeMinuteSecond((orderEn.getValidTime()-System.currentTimeMillis())/1000);
//				if (!StringUtil.isNull(timeStr)) {
//					tv_valid_time.setVisibility(View.VISIBLE);
//					tv_valid_time.setText(getString(R.string.order_valid_time, timeStr));
//					if (mTimer != null) {
//						mTimer.cancel();
//					}
//					mTimer = new MyTimer(orderEn.getValidTime()-System.currentTimeMillis(), 1000);
//					mTimer.start(); //启动倒计时
//				}
//			}
		}
	}

	/**
	 * 动态添加商品列表
	 */
	private void addGoodsLists() {
		List<ProductListEntity> goodsLists = orderEn.getGoodsLists();
		if (goodsLists != null) {
			ll_goods_lists.removeAllViews(); //移除之前添加的所有View
			for (int i = 0; i < goodsLists.size(); i++) {
				final ProductListEntity itemEn = goodsLists.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.item_goods_img_vertical, ll_goods_lists, false);  
				ImageView img = (ImageView) view.findViewById(R.id.item_goods_vertical_iv_img);
				String imgUrl = AppConfig.ENVIRONMENT_PRESENT_IMG_APP + itemEn.getImageUrl();
				ImageLoader.getInstance().displayImage(imgUrl, img, options);
				
				TextView tv_brand = (TextView) view.findViewById(R.id.item_goods_vertical_tv_brand);
				tv_brand.setText(itemEn.getBrand());
				TextView tv_curr = (TextView) view.findViewById(R.id.item_goods_vertical_tv_curr);
				tv_curr.setText(currStr);
				TextView tv_price = (TextView) view.findViewById(R.id.item_goods_vertical_tv_price);
				tv_price.setText(itemEn.getSellPrice());
				TextView tv_name = (TextView) view.findViewById(R.id.item_goods_vertical_tv_name);
				tv_name.setText(itemEn.getName());
				TextView tv_number = (TextView) view.findViewById(R.id.item_goods_vertical_tv_number);
				tv_number.setText("x"+itemEn.getTotal());
				TextView tv_attr = (TextView) view.findViewById(R.id.item_goods_vertical_tv_attr);
				String attrStr = itemEn.getAttr();
				attrStr = attrStr.replace("\n", " ");
				tv_attr.setText(attrStr);
				
				if (i == goodsLists.size()-1) {
					ImageView iv_line = (ImageView) view.findViewById(R.id.item_goods_vertical_iv_line);
					iv_line.setVisibility(View.GONE);
				}
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						openProductDetailActivity(itemEn.getId());
					}
				});
				ll_goods_lists.addView(view);
			}
		}
	}

	/**
	 * 刷新订单数据状态
	 */
	public void updateOrderStatus() {
		updateData();
		updateActivityData(5);
		updateActivityData(10);
	}

	private void getSVData() {
		isSuccess = false;
		startAnimation();
		request(AppConfig.REQUEST_SV_GET_ORDER_DETAIL_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_detail_tv_pay_now:
			if (orderEn != null) {
				switch (orderEn.getStatus()) {
				case 1: //待支付
					startPayActivity(orderEn);
					break;
				}
			}
			break;
		case R.id.order_detail_tv_cacel_order:
			if (orderEn != null) {
				switch (orderEn.getStatus()) {
				case 1: //待支付
					confirmCacelOrder();
					break;
				case 3: //待收货
					startLogisticsActivity();
					break;
				case 5: //已完成
					startLogisticsActivity();
					break;
				}
			}
			break;
		}
	}

	private void startLogisticsActivity() {
		if (orderEn != null) {
			Intent intent = new Intent(mContext, LogisticsActivity.class);
			intent.putExtra("typeStr", logisticsCode);
			intent.putExtra("postId", orderEn.getLogisticsNo());
			startActivity(intent);
		}
	}

	/**
	 * 确认取消订单
	 */
	private void confirmCacelOrder() {
		showConfirmDialog(R.string.order_cacel_confirm, getString(R.string.confirm),
				getString(R.string.being_not), true, true, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case DIALOG_CANCEL_CLICK:
								postCacelOrder();
								break;
						}
					}

				});
	}

	private void postCacelOrder() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE);
	}

	/**
	 * 跳转到支付页面
	 */
	private void startPayActivity(OrderEntity orderEn) {
		if (orderEn != null) {
			Intent intent =new Intent(mContext, WXPayEntryActivity.class);
			intent.putExtra("orderSn", orderEn.getOrderId());
			intent.putExtra("orderTotal", orderEn.getPricePay());
			intent.putExtra("root", TAG);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else {
			CommonTools.showToast(getString(R.string.pay_order_error), 1000);
		}
	}
	
	// 自定义一个倒计时
	class MyTimer extends CountDownTimer{

		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			String timeStr = TimeUtil.getTextTimeMinuteSecond(millisUntilFinished/1000);
			tv_valid_time.setText(getString(R.string.order_valid_time, timeStr));
		}

		@Override
		public void onFinish() {
			updateOrderStatus();
			updateAllData();
		}
		
	}
	
	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
        checkLogin();
		super.onResume();
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogined();
		if (isLogined) {
			if (!isSuccess) {
				updateData();
			}
			updateAllData();
		}else {
			showTimeOutDialog(TAG);
		}
	}

	public void updateData() {
		isUpdate = true;
	}

	private void updateAllData() {
		if (isUpdate) {
			isUpdate = false;
			getSVData();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.i(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		if (mTimer != null) {
			mTimer.cancel();
		}
		instance = null;
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ORDER_DETAIL_CODE:
			params.add(new MyNameValuePair("app", "order_detail"));
			params.add(new MyNameValuePair("order_id", orderId));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_ORDER_DETAIL_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=cancel_order";
			params.add(new MyNameValuePair("order_id", orderId));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ORDER_DETAIL_CODE:
			if (result != null) {
				orderEn = (OrderEntity) result;
				if (orderEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					setView();
				}else if (orderEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					showErrorDialog();
				}
			}else {
				orderEn = null;
				showErrorDialog();
			}
			break;
		case AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					updateOrderStatus();
					updateAllData();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						showErrorDialog(baseEn.getErrInfo());
					}
				}
			}else {
				showServerBusy();
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
	}

	@SuppressLint("HandlerLeak")
	private void showErrorDialog() {
		Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DIALOG_CONFIRM_CLICK:
					finish();
					break;
				default:
					break;
				}
			}
		};
		showErrorDialog(getString(R.string.toast_server_busy), false, mHandler);
	}
	
}
