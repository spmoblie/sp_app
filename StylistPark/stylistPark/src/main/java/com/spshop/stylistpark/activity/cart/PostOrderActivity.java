package com.spshop.stylistpark.activity.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.SelectListActivity;
import com.spshop.stylistpark.activity.profile.BounsListActivity;
import com.spshop.stylistpark.activity.profile.ChildFragmentFive;
import com.spshop.stylistpark.activity.profile.MyAddressActivity;
import com.spshop.stylistpark.activity.profile.OrderListActivity;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.wxapi.WXPayEntryActivity;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class PostOrderActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "PostOrderActivity";
	private static final int PAY_TYPE_1 = 2; //在线支付
	private static final int PAY_TYPE_2 = 1; //货到付款
	public static PostOrderActivity instance = null;
	public boolean isUpdate = false;
	
	private TextView tv_name, tv_phone, tv_address, tv_address_hint;
	private TextView tv_pay_type, tv_bouns_use, tv_goods_total, tv_pay_total;
	private TextView tv_total, tv_fee, tv_charges, tv_bonus, tv_discount, tv_pay, tv_pay_now;
	private ImageView iv_go_pay_select, iv_invoice_select;
	private EditText et_invoice, et_buyer;
	private LinearLayout ll_goods_lists;
	private RelativeLayout rl_address_main, rl_pay_type, rl_bouns_main, rl_charges_main;
	
	private boolean addressOk = false;
	private boolean isCashPay = false;
	private boolean isInvoice = false;
	private boolean isLogined, isSuccess;
	private int payTypeCode;
	private int payType = PAY_TYPE_1;
	private int selectPayType = PAY_TYPE_1;
	private String bounsId, invoiceStr, buyerStr, pricePay, orderAmount;
	private OrderEntity orderEn, payOrderEn;
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_order);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_icon_120);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		rl_address_main = (RelativeLayout) findViewById(R.id.post_order_rl_address_main);
		rl_pay_type = (RelativeLayout) findViewById(R.id.post_order_rl_pay_type);
		rl_bouns_main = (RelativeLayout) findViewById(R.id.post_order_rl_bouns_main);
		rl_charges_main = (RelativeLayout) findViewById(R.id.post_order_rl_price_charges_main);
		tv_name = (TextView) findViewById(R.id.post_order_tv_address_name);
		tv_phone = (TextView) findViewById(R.id.post_order_tv_address_phone);
		tv_address = (TextView) findViewById(R.id.post_order_tv_address_address);
		tv_address_hint = (TextView) findViewById(R.id.post_order_tv_address_hint);
		tv_pay_type = (TextView) findViewById(R.id.post_order_tv_pay_type);
		tv_bouns_use = (TextView) findViewById(R.id.post_order_tv_bouns_num);
		tv_goods_total = (TextView) findViewById(R.id.post_order_tv_order_goods_total);
		tv_total = (TextView) findViewById(R.id.post_order_tv_price_total);
		tv_fee = (TextView) findViewById(R.id.post_order_tv_price_fee);
		tv_charges = (TextView) findViewById(R.id.post_order_tv_price_charges);
		tv_bonus = (TextView) findViewById(R.id.post_order_tv_price_bonus);
		tv_discount = (TextView) findViewById(R.id.post_order_tv_price_discount);
		tv_pay = (TextView) findViewById(R.id.post_order_tv_price_pay);
		tv_pay_total = (TextView) findViewById(R.id.post_order_tv_pay_total);
		tv_pay_now = (TextView) findViewById(R.id.post_order_tv_pay_now);
		iv_go_pay_select = (ImageView) findViewById(R.id.post_order_iv_go_pay_select);
		iv_invoice_select = (ImageView) findViewById(R.id.post_order_iv_invoice_select);
		et_invoice = (EditText) findViewById(R.id.post_order_et_invoice);
		et_buyer = (EditText) findViewById(R.id.post_order_et_buyer);
		ll_goods_lists = (LinearLayout) findViewById(R.id.post_order_ll_goods_lists);
	}

	private void initView() {
		setTitle(R.string.title_order_confirm);
		rl_address_main.setOnClickListener(this);
		rl_pay_type.setOnClickListener(this);
		rl_bouns_main.setOnClickListener(this);
		tv_pay_now.setOnClickListener(this);
	}

	private void setView() {
		if (orderEn != null) {
			tv_goods_total.setText(getString(R.string.num_total, orderEn.getGoodsTotal()));
			tv_total.setText(orderEn.getPriceTotal());
			tv_fee.setText(orderEn.getPriceFee());
			if (!StringUtil.isNull(orderEn.getPriceBonus())) {
				tv_bonus.setText("-" + orderEn.getPriceBonus());
			}
			if (!StringUtil.isNull(orderEn.getPriceDiscount())) {
				tv_discount.setText("-" + orderEn.getPriceDiscount());
			}
			orderAmount = orderEn.getOrderAmount();
			pricePay = orderEn.getPricePay();
			tv_pay.setText(pricePay);
			tv_pay_total.setText(pricePay);
			// 商品列表
			addGoodsLists();
			// 物流信息
			AddressEntity addrEn = orderEn.getAddressEn();
			if (addrEn != null && !StringUtil.isNull(addrEn.getName())
					&& !StringUtil.isNull(addrEn.getPhone()) && !StringUtil.isNull(addrEn.getAddress())) {
				tv_name.setVisibility(View.VISIBLE);
				tv_phone.setVisibility(View.VISIBLE);
				tv_address.setVisibility(View.VISIBLE);
				tv_address_hint.setVisibility(View.GONE);
				
				tv_name.setText(addrEn.getName());
				tv_phone.setText(addrEn.getPhone());
				tv_address.setText(addrEn.getAddress());
				addressOk = true;
				tv_pay_now.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_app_buttom_4));
				tv_pay_now.setTextColor(getResources().getColor(R.color.text_color_white));
			}else {
				tv_name.setVisibility(View.GONE);
				tv_phone.setVisibility(View.GONE);
				tv_address.setVisibility(View.GONE);
				tv_address_hint.setVisibility(View.VISIBLE);
				addressOk = false;
				tv_pay_now.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_app_hollow_4));
				tv_pay_now.setTextColor(getResources().getColor(R.color.text_color_app_bar));
			}
			// 付款方式
			payType = orderEn.getPayId();
			if (payType != PAY_TYPE_2) {
				payType = PAY_TYPE_1;
			}
			switch (payType) {
			case PAY_TYPE_1:
				tv_pay_type.setText(R.string.pay_title);
				tv_pay_now.setText(R.string.order_pay_now);
				rl_charges_main.setVisibility(View.GONE);
				tv_charges.setText(R.string.number_0);
				break;
			case PAY_TYPE_2:
				tv_pay_type.setText(R.string.product_cash_delivery);
				tv_pay_now.setText(R.string.title_order_confirm);
				rl_charges_main.setVisibility(View.VISIBLE);
				tv_charges.setText(orderEn.getPriceCharges());
				break;
			}
			payTypeCode = orderEn.getPayTypeCode();
			isCashPay = payTypeCode == 1 ? true : false;
			if (isCashPay) {
				iv_go_pay_select.setVisibility(View.VISIBLE);
			}else {
				iv_go_pay_select.setVisibility(View.INVISIBLE);
			}
			// 红包使用情况
			bounsId = orderEn.getBounsId();
			if (!StringUtil.isNull(bounsId) && !"0".equals(bounsId)) {
				tv_bouns_use.setText(getString(R.string.bouns_used_sum, orderEn.getPriceBonus()));
			}else {
				tv_bouns_use.setText("");
			}
			// 发票信息
			final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			iv_invoice_select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isInvoice) {
						iv_invoice_select.setSelected(false);
						et_invoice.setText("");
						et_invoice.setFocusable(false);
						et_invoice.setFocusableInTouchMode(false);
						//隐藏软键盘
						imm.hideSoftInputFromWindow(et_invoice.getWindowToken(), 0);
					}else {
						iv_invoice_select.setSelected(true);
						et_invoice.setFocusable(true);
						et_invoice.setFocusableInTouchMode(true);
						et_invoice.requestFocus();
						et_invoice.findFocus();
						//弹出软键盘
						imm.showSoftInput(et_invoice, InputMethodManager.RESULT_SHOWN);
					}
					isInvoice = !isInvoice;
				}
			});
		}else {
			showErrorDialog();
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
				View view = LayoutInflater.from(this).inflate(R.layout.item_goods_img_vertical, ll_goods_lists, false);  
				ImageView img = (ImageView) view.findViewById(R.id.item_goods_vertical_iv_img);
				String imgUrl = AppConfig.ENVIRONMENT_PRESENT_IMG_APP + goodsLists.get(i).getImageUrl();
				ImageLoader.getInstance().displayImage(imgUrl, img, options);
				
				TextView tv_brand = (TextView) view.findViewById(R.id.item_goods_vertical_tv_brand);
				tv_brand.setText(goodsLists.get(i).getBrand());
				TextView tv_price = (TextView) view.findViewById(R.id.item_goods_vertical_tv_price);
				tv_price.setText(goodsLists.get(i).getSellPrice());
				TextView tv_name = (TextView) view.findViewById(R.id.item_goods_vertical_tv_name);
				tv_name.setText(goodsLists.get(i).getName());
				TextView tv_number = (TextView) view.findViewById(R.id.item_goods_vertical_tv_number);
				tv_number.setText("x"+goodsLists.get(i).getTotal());
				TextView tv_attr = (TextView) view.findViewById(R.id.item_goods_vertical_tv_attr);
				String attrStr = goodsLists.get(i).getAttr();
				attrStr = attrStr.replace("\n", " ");
				tv_attr.setText(attrStr);
				
				if (i == goodsLists.size()-1) {
					ImageView iv_line = (ImageView) view.findViewById(R.id.item_goods_vertical_iv_line);
					iv_line.setVisibility(View.GONE);
				}
				ll_goods_lists.addView(view);
			}
		}
	}

	private void getSVData() {
		isSuccess = false;
		startAnimation();
		request(AppConfig.REQUEST_SV_GET_ORDER_CONFIRM_CODE);
	}
	
	private void postSelectPayment() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_SELECT_PAYMENT_CODE);
	}

	private void postConfirmOrderData() {
		invoiceStr = et_invoice.getText().toString();
		buyerStr = et_buyer.getText().toString();
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_CONFIRM_ORDER_CODE);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.post_order_rl_address_main:
			startActivity(new Intent(mContext, MyAddressActivity.class));
			break;
		case R.id.post_order_rl_pay_type:
			if (!isCashPay) return;
			SelectListEntity selectEn = getPayTypeListEntity();
			intent = new Intent(mContext, SelectListActivity.class);
			intent.putExtra("data", selectEn);
			intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_6);
			startActivityForResult(intent,AppConfig.ACTIVITY_CHOOSE_PAY_TYPE);
			break;
		case R.id.post_order_rl_bouns_main:
			intent = new Intent(mContext, BounsListActivity.class);
			intent.putExtra("topType", BounsListActivity.TYPE_2);
			intent.putExtra("root", TAG);
			intent.putExtra("bounsId", bounsId);
			startActivity(intent);
			break;
		case R.id.post_order_tv_pay_now:
			if (!addressOk) return;
			postConfirmOrderData();
			break;
		}
	}

	/**
	 * 生成支付方式列表数据
	 */
	private SelectListEntity getPayTypeListEntity() {
		SelectListEntity selectEn = new SelectListEntity();
		selectEn.setTypeName(getString(R.string.pay_type));
		ArrayList<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
		SelectListEntity childEn1 = new SelectListEntity();
		childEn1.setChildId(PAY_TYPE_1);
		childEn1.setChildShowName(getString(R.string.pay_title));
		childLists.add(childEn1);
		SelectListEntity childEn2 = new SelectListEntity();
		childEn2.setChildId(PAY_TYPE_2);
		childEn2.setChildShowName(getString(R.string.product_cash_delivery));
		childLists.add(childEn2);
		switch (payType) {
		case PAY_TYPE_1:
			selectEn.setSelectEn(childEn1);
			break;
		case PAY_TYPE_2:
			selectEn.setSelectEn(childEn2);
			break;
		}
		selectEn.setChildLists(childLists);
		return selectEn;
	}
	
	/**
	 * 跳转到支付页面
	 */
	private void startPayActivity(OrderEntity payEn) {
		if (payEn != null) {
			Intent intent =new Intent(mContext, WXPayEntryActivity.class);
			intent.putExtra("orderSn", payEn.getOrderNo());
			intent.putExtra("orderTotal", payEn.getPricePay());
			intent.putExtra("root", TAG);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else {
			showServerBusy();
		}
	}

	/**
	 * 跳转到订单列表并销毁此页面
	 */
	private void startOrderListActivity(int rootType, int topType) {
		Intent intent = new Intent(mContext, OrderListActivity.class);
		intent.putExtra("rootType", rootType);
		intent.putExtra("topType", topType);
		startActivity(intent);
		if (ChildFragmentFive.instance != null) {
			ChildFragmentFive.instance.isUpdate = true; //创建新订单刷新个人页订单数据
		}
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConfig.ACTIVITY_CHOOSE_PAY_TYPE) { //选择支付方式
			if (resultCode == RESULT_OK) {
				selectPayType = data.getExtras().getInt(AppConfig.ACTIVITY_SELECT_PAY_TYPE, 1);
				if (selectPayType != payType) {
					postSelectPayment();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        
        checkLogin();
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogined();
		if (isLogined) {
			if (!isSuccess) {
				isUpdate = true;
			}
			updateAllData();
		}else {
			showTimeOutDialog(TAG);
		}
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
        StatService.onPause(this);
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
	}

	@Override
	public void onBackPressed() {
		if (isSuccess) {
			ask4Leave();
		}else {
			super.onBackPressed();
		}
	}

	@Override
	public void OnListenerLeft() {
		if (isSuccess) {
			ask4Leave();
		}else {
			super.OnListenerLeft();
		}
	}

	@SuppressLint("HandlerLeak")
	private void ask4Leave() {
		Handler handler = new Handler() {
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
		showConfirmDialog(R.string.abandon_confirm, getString(R.string.leave_confirm), getString(R.string.delete_think), handler);
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ORDER_CONFIRM_CODE:
			orderEn = sc.getConfirmOrderData();
			return orderEn;
			
		case AppConfig.REQUEST_SV_POST_SELECT_PAYMENT_CODE:
			return sc.postSelectPayment(selectPayType);
			
		case AppConfig.REQUEST_SV_POST_CONFIRM_ORDER_CODE:
			payOrderEn = sc.postConfirmOrderData(payTypeCode, payType, bounsId, invoiceStr, buyerStr, orderAmount);
			return payOrderEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ORDER_CONFIRM_CODE:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					setView();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					showErrorDialog();
				}
			}else {
				showErrorDialog();
			}
			break;
		case AppConfig.REQUEST_SV_POST_SELECT_PAYMENT_CODE:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity)result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					payType = selectPayType;
					isUpdate = true;
					updateAllData();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					showServerBusy();
				}
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_CONFIRM_ORDER_CODE:
			if (payOrderEn != null) {
				if (payOrderEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					UserManager.getInstance().saveCartTotal(0);
					switch (payType) {
					case PAY_TYPE_1:
						payOrderEn.setPricePay(pricePay);
						startPayActivity(payOrderEn);
						break;
					case PAY_TYPE_2:
						startOrderListActivity(0, OrderListActivity.TYPE_3);
						break;
					}
				}else if (payOrderEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(payOrderEn.getErrInfo())) {
						showServerBusy();
					}else {
						showErrorDialog(payOrderEn.getErrInfo());
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
		showErrorDialog(getString(R.string.toast_server_busy), mHandler);
	}
	
}
