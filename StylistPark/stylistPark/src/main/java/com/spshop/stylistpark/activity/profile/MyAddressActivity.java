package com.spshop.stylistpark.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.cart.PostOrderActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.AddressListAdapter;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.dialog.DialogManager.DialogManagerCallback;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.List;


public class MyAddressActivity extends BaseActivity {

	private static final String TAG = "MyAddressActivity";
	public static MyAddressActivity instance = null;
	public boolean isChange = false;
	
	private TextView tv_no_data;
	private ListView mListView;
	private AdapterCallback ap_callback;
	private AddressListAdapter lv_adapter;
	private boolean isLogined, isSuccess;
	private AddressEntity mainEn, data;
	private List<AddressEntity> addrLists = new ArrayList<AddressEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_address);
		
		instance = this;
		
		findViewById();
		initView();
	}

	private void findViewById() {
		mListView = (ListView) findViewById(R.id.my_address_listView);
		tv_no_data = (TextView) findViewById(R.id.my_address_tv_no_data);
	}

	private void initView() {
		setTitle(R.string.title_my_address);
		setBtnRight(getString(R.string.my_new));
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
					case AddressListAdapter.TYPE_EDIT:
						Intent intent = new Intent(mContext, AddressEditActivity.class);
						intent.putExtra("data", data);
						startActivity(intent);
						break;
					case AddressListAdapter.TYPE_DELETE:
						new DialogManager(mContext).showTwoBtnDialog(new DialogManagerCallback() {
							
							@Override
							public void setOnClick(int type) {
								if (type == 1) {
									requestDeleteAddress();
								}
							}
						}, getString(R.string.prompt), getString(R.string.delete_confirm),
						   getString(R.string.cancel), getString(R.string.confirm), 
						   AppApplication.screenWidth * 2/3, true);
						break;
					}
				}
			}
		};
		lv_adapter = new AddressListAdapter(mContext, addrLists, ap_callback);
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

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		startActivity(new Intent(mContext, AddressEditActivity.class));
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
				isChange = true;
			}
			updateAllData();
		}else {
			showTimeOutDialog(TAG);
		}
	}

	private void updateAllData() {
		if (isChange) {
			isChange = false;
			getSVDatas();
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
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE:
			mainEn = sc.getAddressLists();
			return mainEn;
		case AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE:
			return sc.postSelectAddress(String.valueOf(data.getAddressId()));
		case AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE:
			return sc.postDeleteAddress(String.valueOf(data.getAddressId()));
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE:
			stopAnimation();
			if (mainEn != null) {
				if (mainEn.getMainLists() != null) {
					isSuccess = true;
					addrLists.clear();
					addrLists.addAll(mainEn.getMainLists());
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
					if (PostOrderActivity.instance != null) {
						PostOrderActivity.instance.isUpdate = true;
					}
					finish();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(mContext, baseEn.getErrInfo(), 2000);
					}
				}
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE:
			if (result != null && ((BaseEntity) result).getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
				getSVDatas();
			}else {
				stopAnimation();
				showServerBusy();
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	private void updateListView() {
		if (addrLists.size() > 0) {
			mListView.setVisibility(View.VISIBLE);
			tv_no_data.setVisibility(View.GONE);
			lv_adapter.updateAdapter(addrLists);
		}else {
			mListView.setVisibility(View.GONE);
			tv_no_data.setVisibility(View.VISIBLE);
		}
	}

}
