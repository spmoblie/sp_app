package com.spshop.stylistpark.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.home.ProductListActivity;
import com.spshop.stylistpark.activity.home.ScreenListActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择列表Activity
 */
public class SelectListActivity extends BaseActivity {
	
	private static final String TAG = "SelectListActivity";
	private int dataType = SelectListAdapter.DATA_TYPE_2;
	private boolean isChange = false;
	
	private ListView lv;
	private AdapterCallback lv_Callback;
	private SelectListAdapter lv_Adapter;
	
	private SelectListEntity data, selectEn;
	private List<SelectListEntity> lv_lists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_list);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		data = (SelectListEntity) getIntent().getExtras().get("data");
		dataType = getIntent().getExtras().getInt("dataType", SelectListAdapter.DATA_TYPE_2);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		lv = (ListView) findViewById(R.id.select_list_lv);
	}

	private void initView() {
		if (data != null) {
			setTitle(data.getTypeName()); //标题
			selectEn = data.getSelectEn();
			if (dataType == SelectListAdapter.DATA_TYPE_4
					| dataType == SelectListAdapter.DATA_TYPE_7) {
				setBtnRight(getString(R.string.clean)); //右边按钮
				if (selectEn == null) {
					setBtnRightGone(View.GONE);
				}
			}
			lv_lists = data.getChildLists();
			if (lv_lists != null) {
				setAdapter();
			}
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		if (dataType == SelectListAdapter.DATA_TYPE_4 && ProductListActivity.instance != null) {
			ProductListActivity.instance.updateScreenParameter(null);
			finish();
		}
		if (dataType == SelectListAdapter.DATA_TYPE_7 && ShowListHeadActivity.instance != null) {
			ShowListHeadActivity.instance.updateScreenParameter(null);
			finish();
		}
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_Callback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				selectEn = (SelectListEntity) entity;
				switch (dataType) {
				case SelectListAdapter.DATA_TYPE_2: //ScreenListActivity --> SelectListActivity
					if (ScreenListActivity.instance != null) {
						if (selectEn != null && selectEn.getChildId() != 0) {
							ScreenListActivity.instance.updataListDatas(selectEn);
						}else {
							ScreenListActivity.instance.updataListDatas(null);
						}
					}
					finish();
					break;
				case SelectListAdapter.DATA_TYPE_4: //ProductListActivity --> SelectListActivity
					if (ProductListActivity.instance != null) {
						if (selectEn != null && selectEn.getChildId() != 0) {
							ProductListActivity.instance.updateScreenParameter(selectEn);
						}else {
							ProductListActivity.instance.updateScreenParameter(null);
						}
					}
					finish();
					break;
				case SelectListAdapter.DATA_TYPE_5: //PersonalActivity --> SelectListActivity
					if (selectEn != null) {
						postChangeCotent();
					}else {
						finish();
					}
					break;
				case SelectListAdapter.DATA_TYPE_6: //PostOrderActivity --> SelectListActivity
					isChange = true;
					finish();
					break;
				case SelectListAdapter.DATA_TYPE_7: //ShowListHeadActivity --> SelectListActivity
					if (ShowListHeadActivity.instance != null) {
						if (selectEn != null && selectEn.getChildId() != 0) {
							ShowListHeadActivity.instance.updateScreenParameter(selectEn);
						}else {
							ShowListHeadActivity.instance.updateScreenParameter(null);
						}
					}
					finish();
					break;
				}
			}
		};
		lv_Adapter = new SelectListAdapter(mContext, selectEn, lv_lists, lv_Callback, dataType);
		lv.setAdapter(lv_Adapter);
		lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void postChangeCotent() {
		request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
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
	public void finish() {
		if (isChange && selectEn != null) { 
			Intent returnIntent = new Intent();
			if (dataType == SelectListAdapter.DATA_TYPE_5) //PersonalActivity --> SelectListActivity
			{ 
				returnIntent.putExtra(AppConfig.ACTIVITY_CHANGE_USER_CONTENT, selectEn.getChildId());
			}
			else if (dataType == SelectListAdapter.DATA_TYPE_6) //PostOrderActivity --> SelectListActivity
			{
				returnIntent.putExtra(AppConfig.ACTIVITY_SELECT_PAY_TYPE, selectEn.getChildId());
			}
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_profile";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			params.add(new MyNameValuePair("sex", String.valueOf(selectEn.getChildId())));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isChange = true;
					finish();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					showServerBusy();
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
	
}
