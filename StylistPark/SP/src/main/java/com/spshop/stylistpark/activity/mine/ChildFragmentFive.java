package com.spshop.stylistpark.activity.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.common.OnlineServiceActivity;
import com.spshop.stylistpark.activity.common.ShowListActivity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class ChildFragmentFive extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentFive";
	public static ChildFragmentFive instance = null;

	private Context mContext;
	private SharedPreferences shared;
	private UserInfoEntity infoEn;
	private boolean isLogined, isUpdate, isSuccess;

	private RelativeLayout rl_my_member, rl_member_order, rl_order_all, rl_my_address;
	private RelativeLayout rl_my_wallet, rl_my_coupon, rl_collection, rl_history, rl_call;
	private FrameLayout fl_order_pay, fl_order_delivery, fl_order_receive, fl_order_return;
	private ImageView iv_setting, iv_avatar;
	private TextView tv_pay_num, tv_delivery_num, tv_receive_num, tv_return_num;
	private TextView tv_my_member, tv_member_order, tv_money, tv_coupon, tv_collection, tv_history;
	
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LogUtil.i(TAG, "onCreate");
		instance = this;
		mContext = getActivity();
		atm = AsyncTaskManager.getInstance(mContext);
		shared = AppApplication.getSharedPreferences();
		// 自动跳转至MemberListActivity
		if (shared.getBoolean(AppConfig.KEY_PUSH_PAGE_MEMBER, false)) {
			startMemberListActivity(MemberListActivity.TYPE_1);
		}

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_five, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		iv_setting = (ImageView) view.findViewById(R.id.fragment_five_iv_top_left);
		iv_avatar = (ImageView) view.findViewById(R.id.fragment_five_iv_avatar);
		rl_my_member = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_my_member);
		rl_member_order = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_member_order);
		rl_order_all = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_order);
		fl_order_pay = (FrameLayout) view.findViewById(R.id.fragment_five_fl_wait_pay);
		fl_order_delivery = (FrameLayout) view.findViewById(R.id.fragment_five_fl_wait_delivery);
		fl_order_receive = (FrameLayout) view.findViewById(R.id.fragment_five_fl_wait_receive);
		fl_order_return = (FrameLayout) view.findViewById(R.id.fragment_five_fl_wait_return);
		tv_pay_num = (TextView) view.findViewById(R.id.fragment_five_tv_wait_pay_num);
		tv_receive_num = (TextView) view.findViewById(R.id.fragment_five_tv_wait_receive_num);
		tv_delivery_num = (TextView) view.findViewById(R.id.fragment_five_tv_wait_delivery_num);
		tv_return_num = (TextView) view.findViewById(R.id.fragment_five_tv_wait_return_num);
		tv_my_member = (TextView) view.findViewById(R.id.fragment_five_tv_my_member);
		tv_member_order = (TextView) view.findViewById(R.id.fragment_five_tv_member_order);
		tv_money = (TextView) view.findViewById(R.id.fragment_five_tv_money);
		tv_coupon = (TextView) view.findViewById(R.id.fragment_five_tv_coupon);
		tv_collection = (TextView) view.findViewById(R.id.fragment_five_tv_collection);
		tv_history = (TextView) view.findViewById(R.id.fragment_five_tv_history);
		rl_my_address = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_address);
		rl_my_wallet = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_wallet);
		rl_my_coupon = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_coupon);
		rl_collection = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_collection);
		rl_history = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_history);
		rl_call = (RelativeLayout) view.findViewById(R.id.fragment_five_rl_call);
	}

	private void initView() {
		iv_setting.setOnClickListener(this);
		iv_avatar.setOnClickListener(this);
		rl_my_member.setOnClickListener(this);
		rl_member_order.setOnClickListener(this);
		rl_order_all.setOnClickListener(this);
		fl_order_pay.setOnClickListener(this);
		fl_order_delivery.setOnClickListener(this);
		fl_order_receive.setOnClickListener(this);
		fl_order_return.setOnClickListener(this);
		rl_my_address.setOnClickListener(this);
		rl_my_wallet.setOnClickListener(this);
		rl_my_coupon.setOnClickListener(this);
		rl_collection.setOnClickListener(this);
		rl_history.setOnClickListener(this);
		rl_call.setOnClickListener(this);
	}

	private void setView() {
		String memberType = getString(R.string.mine_member);
		if (UserManager.getInstance().isTalent()) { //达人
			memberType = getString(R.string.mine_customer);
		}
		tv_my_member.setText(getString(R.string.mine_my_member, memberType));
		tv_member_order.setText(getString(R.string.mine_member_order, memberType));

		updateUserMoney();
		String userAvatar = "";
		if (infoEn != null) {
			userAvatar = infoEn.getUserAvatar();
			int order_1 = infoEn.getOrder_1();
			if (order_1 > 0) { //待付款
				if (order_1 > 99) {
					order_1 = 99;
				}
				tv_pay_num.setVisibility(View.VISIBLE);
				tv_pay_num.setText(String.valueOf(order_1));
			}else {
				tv_pay_num.setVisibility(View.GONE);
			}
			int order_2 = infoEn.getOrder_2();
			if (order_2 > 0) { //待发货
				if (order_2 > 99) {
					order_2 = 99;
				}
				tv_delivery_num.setVisibility(View.VISIBLE);
				tv_delivery_num.setText(String.valueOf(order_2));
			}else {
				tv_delivery_num.setVisibility(View.GONE);
			}
			int order_3 = infoEn.getOrder_3();
			if (order_3 > 0) { //待收货
				if (order_3 > 99) {
					order_3 = 99;
				}
				tv_receive_num.setVisibility(View.VISIBLE);
				tv_receive_num.setText(String.valueOf(order_3));
			}else {
				tv_receive_num.setVisibility(View.GONE);
			}
			int order_4 = infoEn.getOrder_4();
			if (order_4 > 0) { //返修、退换
				if (order_4 > 99) {
					order_4 = 99;
				}
				tv_return_num.setVisibility(View.VISIBLE);
				tv_return_num.setText(String.valueOf(order_4));
			}else {
				tv_return_num.setVisibility(View.GONE);
			}
			BaseActivity.updateCartTotal(infoEn.getCartTotal());
		}else {
			String num = getString(R.string.number_0);
			tv_pay_num.setText(num);
			tv_pay_num.setVisibility(View.GONE);
			tv_delivery_num.setText(num);
			tv_delivery_num.setVisibility(View.GONE);
			tv_receive_num.setText(num);
			tv_receive_num.setVisibility(View.GONE);
			tv_return_num.setText(num);
			tv_return_num.setVisibility(View.GONE);
			tv_money.setText(LangCurrTools.getCurrencyValue() + num);
			tv_coupon.setText("");
		}
		ImageLoader.getInstance().displayImage(userAvatar, iv_avatar, AppApplication.getAvatarOptions());
	}

	private void updateUserMoney() {
		if (tv_money != null) {
			tv_money.setText(UserManager.getInstance().getUserMoney());
		}
	}

	public void updateData() {
		isUpdate = true;
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogined();
		LogUtil.i("isLogined", isLogined);
		if (isLogined) {
			if (isUpdate || !isSuccess) {
				requestGetUserInfo();
				isUpdate = false;
			}
			updateUserMoney();
		}else {
			infoEn = null;
			setView();
		}
	}

	private void requestGetUserInfo() {
		atm.request(AppConfig.REQUEST_SV_GET_USERINFO_SUMMARY_CODE, instance);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fragment_five_iv_top_left) {
			startActivity(new Intent(mContext, SettingActivity.class));
			return;
		} else if (v.getId() == R.id.fragment_five_rl_call) {
			Intent intent = new Intent(mContext, OnlineServiceActivity.class);
			intent.putExtra("title", getString(R.string.mine_call));
			intent.putExtra("lodUrl", AppConfig.API_CUSTOMER_SERVICE);
			startActivity(intent);
			return;
		}
		if (!isLogined) { //未登入
			HomeFragmentActivity.instance.openLoginActivity(TAG);
			return;
		}
		switch (v.getId()) {
			case R.id.fragment_five_iv_avatar:
				startPersonalActivity();
				break;
			case R.id.fragment_five_rl_my_member:
				startMemberListActivity(MemberListActivity.TYPE_1);
				break;
			case R.id.fragment_five_rl_member_order:
				startOrderListActivity(1, OrderListActivity.TYPE_1);
				break;
			case R.id.fragment_five_rl_order:
				startOrderListActivity(0, OrderListActivity.TYPE_1);
				break;
			case R.id.fragment_five_fl_wait_pay:
				startOrderListActivity(0, OrderListActivity.TYPE_2);
				break;
			case R.id.fragment_five_fl_wait_delivery:
				startOrderListActivity(0, OrderListActivity.TYPE_3);
				break;
			case R.id.fragment_five_fl_wait_receive:
				startOrderListActivity(0, OrderListActivity.TYPE_4);
				break;
			case R.id.fragment_five_fl_wait_return:
				startOrderListActivity(0, OrderListActivity.TYPE_5);
				break;
			case R.id.fragment_five_rl_address:
				startActivity(new Intent(mContext, MyAddressActivity.class));
				break;
			case R.id.fragment_five_rl_wallet:
				startActivity(new Intent(mContext, AccountBalanceActivity.class));
				break;
			case R.id.fragment_five_rl_coupon:
				startCouponListActivity(CouponListActivity.TYPE_1);
				break;
			case R.id.fragment_five_rl_collection:
				startShowListActivity(ShowListActivity.PAGE_ROOT_CODE_1, getString(R.string.mine_collection));
				break;
			case R.id.fragment_five_rl_history:
				startShowListActivity(ShowListActivity.PAGE_ROOT_CODE_2, getString(R.string.mine_history));
				break;
		}
	}

	/**
	 * 跳转到“收藏商品”或“浏览记录”页面
	 */
	private void startShowListActivity(int pageCode, String pageName) {
		Intent intent = new Intent(mContext, ShowListActivity.class);
		intent.putExtra("pageCode", pageCode);
		intent.putExtra("pageName", pageName);
		startActivity(intent);
	}
	
	/**
	 * 跳转个人专页
	 */
	private void startPersonalActivity() {
		Intent intent = new Intent(mContext, PersonalActivity.class);
		intent.putExtra("data", infoEn);
		startActivity(intent);
	}

	/**
	 * 跳转到会员列表
	 */
	private void startMemberListActivity(int topType) {
		Intent intent = new Intent(mContext, MemberListActivity.class);
		intent.putExtra("topType", topType);
		startActivity(intent);
	}

	/**
	 * 跳转到订单列表
	 */
	private void startOrderListActivity(int rootType, int topType) {
		Intent intent = new Intent(mContext, OrderListActivity.class);
		intent.putExtra("rootType", rootType);
		intent.putExtra("topType", topType);
		startActivity(intent);
	}
	
	/**
	 * 跳转到优惠券列表
	 */
	private void startCouponListActivity(int topType) {
		Intent intent = new Intent(mContext, CouponListActivity.class);
		intent.putExtra("topType", topType);
		intent.putExtra("root", TAG);
		intent.putExtra("couponId", "");
		startActivity(intent);
	}

	@Override
	public void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		checkLogin();
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		instance = null;
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception{
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_USERINFO_SUMMARY_CODE:
			params.add(new MyNameValuePair("app", "my"));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_USERINFO_SUMMARY_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_USERINFO_SUMMARY_CODE:
			if (result != null) {
				infoEn = (UserInfoEntity) result;
				if (infoEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					UserManager.getInstance().saveUserInfo(infoEn);
					setView();
				}else if (infoEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) { //登录失效
					AppApplication.AppLogout(false);
					checkLogin();
				}else {
					loadFailHandle(getString(R.string.toast_server_busy));
				}
			}else {
				infoEn = null;
				loadFailHandle(getString(R.string.toast_server_busy));
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		loadFailHandle(String.valueOf(result));
	}

	private void loadFailHandle(String msg) {
		isSuccess = false;
		CommonTools.showToast(msg, 3000);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

