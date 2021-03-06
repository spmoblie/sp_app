package com.spshop.stylistpark.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.AddressListAdapter;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;


public class StorePickupActivity extends BaseActivity {

	private static final String TAG = "StorePickupActivity";
	public static StorePickupActivity instance = null;

	private TextView tv_no_data;
	private ListView mListView;
	private AdapterCallback ap_callback;
	private AddressListAdapter lv_adapter;

	private OrderEntity orderEn;
	private AddressEntity data;
	private List<AddressEntity> lv_show = new ArrayList<AddressEntity>();
	private boolean isLogined, isUpdate, isSuccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_address);

		instance = this;
		orderEn = (OrderEntity) getIntent().getExtras().get("data");
		if (orderEn != null && orderEn.getAddLists() != null) {
			lv_show.addAll(orderEn.getAddLists());
		}
		findViewById();
		initView();
	}

	private void findViewById() {
		mListView = (ListView) findViewById(R.id.my_address_listView);
		tv_no_data = (TextView) findViewById(R.id.my_address_tv_no_data);
	}

	private void initView() {
		setTitle(R.string.order_support_pay);
		setAdapter();
	}
	
	private void setAdapter() {
		ap_callback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				if (entity != null) {
					data = (AddressEntity) entity;
					switch (type) {
					case AddressListAdapter.TYPE_SELECT:
						if (data.getDefaultId() != data.getAddressId()) { //非默认
							requestSelectAddress();
						}
						break;
					}
				}
			}
		};
		lv_adapter = new AddressListAdapter(mContext, AddressListAdapter.TYPE_DATA_2, lv_show, ap_callback);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void getSVDatas() {
		isSuccess = false;
		startAnimation();
		request(AppConfig.REQUEST_SV_GET_PICKUP_LIST_CODE);
	}

	private void requestSelectAddress() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		//checkLogin();
		super.onResume();
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogin();
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
			getSVDatas();
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
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		instance = null;
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_PICKUP_LIST_CODE:
			params.add(new MyNameValuePair("act", "address_list"));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_PICKUP_LIST_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE:
			// 提交默认地址
			//uri = AppConfig.URL_COMMON_USER_URL + "?act=is_address";
			//params.add(new MyNameValuePair("id", String.valueOf(data.getAddressId())));
			 // 提交配送方式
			uri = AppConfig.URL_COMMON_FLOW_URL + "?step=select_shipping";
			params.add(new MyNameValuePair("shipping_id", String.valueOf(data.getAddressId())));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_PICKUP_LIST_CODE:
			stopAnimation();
			if (result != null) {
				AddressEntity mainEn = (AddressEntity) result;
				if (mainEn.getMainLists() != null) {
					isSuccess = true;
					lv_show.clear();
					lv_show.addAll(mainEn.getMainLists());
				}
				updateListView();
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE:
			stopAnimation();
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					updateListView();
					updateActivityData(9);
					if (MyAddressActivity.instance != null) {
						MyAddressActivity.instance.finish();
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
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
	}

	private void updateListView() {
		if (lv_show.size() > 0) {
			mListView.setVisibility(View.VISIBLE);
			tv_no_data.setVisibility(View.GONE);
			lv_adapter.updateAdapter(lv_show, AddressListAdapter.TYPE_DATA_2);
		}else {
			mListView.setVisibility(View.GONE);
			tv_no_data.setVisibility(View.VISIBLE);
		}
	}

}
