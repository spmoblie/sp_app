package com.spshop.stylistpark.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;


public class MyAddressActivity extends BaseActivity {

	private static final String TAG = "MyAddressActivity";
	public static MyAddressActivity instance = null;

	private RelativeLayout rl_store_pickup;
	private TextView tv_no_data;
	private ListView mListView;
	private AdapterCallback ap_callback;
	private AddressListAdapter lv_adapter;

	private boolean showTop;
	private String phoneStr;
	private AddressEntity data;
	private List<AddressEntity> lv_show = new ArrayList<AddressEntity>();
	private boolean isLogined, isUpdate, isSuccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_address);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			showTop = bundle.getBoolean("showTop", false);
		}
		instance = this;

		findViewById();
		initView();
	}

	private void findViewById() {
		rl_store_pickup = (RelativeLayout) findViewById(R.id.mey_address_rl_store_pickup);
		mListView = (ListView) findViewById(R.id.my_address_listView);
		tv_no_data = (TextView) findViewById(R.id.my_address_tv_no_data);
	}

	private void initView() {
		setTitle(R.string.mine_my_address);
		setBtnRight(getString(R.string.my_new));

		rl_store_pickup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtil.isNull(phoneStr)) {
					showEditDialog();
				} else {
					startStorePickupActivity();
				}
			}
		});
		if (showTop) {
			rl_store_pickup.setVisibility(View.VISIBLE);
		}
		setAdapter();
	}

	private void showEditDialog() {
		showEditDialog(getString(R.string.address_store_pickup_phone),
				InputType.TYPE_CLASS_PHONE, true, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case BaseActivity.DIALOG_CANCEL_CLICK:
						break;
					default:
						phoneStr = (String) msg.obj;
						if (!StringUtil.isNull(phoneStr)) {
							postUserPhone();
						}
						break;
				}
			}
		});
	}

	private void startStorePickupActivity() {
		startActivity(new Intent(mContext, StorePickupActivity.class));
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
					case AddressListAdapter.TYPE_EDIT:
						Intent intent = new Intent(mContext, AddressEditActivity.class);
						intent.putExtra("data", data);
						startActivity(intent);
						break;
					case AddressListAdapter.TYPE_DELETE:
						showConfirmDialog(getString(R.string.delete_confirm), getString(R.string.cancel),
								getString(R.string.confirm), true, true, new Handler() {
									@Override
									public void handleMessage(Message msg) {
										switch (msg.what) {
											case BaseActivity.DIALOG_CANCEL_CLICK:
												break;
											case BaseActivity.DIALOG_CONFIRM_CLICK:
												requestDeleteAddress();
												break;
										}
									}
								});
						break;
					}
				}
			}
		};
		lv_adapter = new AddressListAdapter(mContext, AddressListAdapter.TYPE_DATA_1, lv_show, ap_callback);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void getSVDatas() {
		isSuccess = false;
		startAnimation();
		request(AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE);
	}

	private void requestSelectAddress() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE);
	}

	private void requestDeleteAddress() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE);
	}

	private void postUserPhone() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
	}

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		startActivity(new Intent(mContext, AddressEditActivity.class));
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
		case AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE:
			params.add(new MyNameValuePair("act", "address_list"));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=is_address";
			params.add(new MyNameValuePair("id", String.valueOf(data.getAddressId())));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE, uri, params, HttpUtil.METHOD_POST);

		case AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=drop_address";
			params.add(new MyNameValuePair("id", String.valueOf(data.getAddressId())));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE, uri, params, HttpUtil.METHOD_POST);

		case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_profile";
			params.add(new MyNameValuePair("mobile", phoneStr));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE:
				stopAnimation();
				if (result != null) {
					AddressEntity mainEn = (AddressEntity) result;
					if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
						phoneStr = mainEn.getPhone();
						if (mainEn.getMainLists() != null) {
							isSuccess = true;
							lv_show.clear();
							lv_show.addAll(mainEn.getMainLists());
						}
						updateListView();
					} else if (mainEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
						showTimeOutDialog(TAG);
					} else {
						showServerBusy();
					}
				} else {
					showServerBusy();
				}
				break;
			default:
				if (result != null) {
					BaseEntity baseEn = (BaseEntity) result;
					if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
						switch (requestCode) {
							case AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE:
								stopAnimation();
								updateListView();
								updateActivityData(9);
								finish();
								break;
							case AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE:
								getSVDatas();
								updateActivityData(9);
								break;
							case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
								CommonTools.showToast(getString(R.string.submit_success), 1000);
								phoneStr = "";
								getSVDatas();
								break;
						}
					} else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
						stopAnimation();
						// 登入超时，交BaseActivity处理
					} else {
						stopAnimation();
						if (StringUtil.isNull(baseEn.getErrInfo())) {
							showServerBusy();
						} else {
							CommonTools.showToast(baseEn.getErrInfo(), 2000);
						}
					}
				} else {
					stopAnimation();
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
			lv_adapter.updateAdapter(lv_show, AddressListAdapter.TYPE_DATA_1);
		}else {
			mListView.setVisibility(View.GONE);
			tv_no_data.setVisibility(View.VISIBLE);
		}
	}

}
