package com.spshop.stylistpark.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.LogisticsListAdapter;
import com.spshop.stylistpark.entity.LogisticsEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;


public class LogisticsActivity extends BaseActivity {

	private static final String TAG = "LogisticsActivity";

	private FrameLayout rl_no_data;
	private TextView tv_no_data;
	private ListView mListView;
	private LogisticsListAdapter lv_adapter;
	private String typeStr, postId;
	private boolean isLogined, isUpdate, isSuccess;
	private List<LogisticsEntity> logLists = new ArrayList<LogisticsEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logistics);
		
		typeStr = getIntent().getExtras().getString("typeStr");
		postId = getIntent().getExtras().getString("postId");
		
		findViewById();
		initView();
	}

	private void findViewById() {
		mListView = (ListView) findViewById(R.id.logistics_listView);
		rl_no_data = (FrameLayout) findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) findViewById(R.id.loading_no_data_tv_show);
	}

	private void initView() {
		setTitle(R.string.order_logistic_title);
		setAdapter();
	}
	
	private void setAdapter() {
		lv_adapter = new LogisticsListAdapter(mContext, logLists);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void getSVDatas() {
		isSuccess = false;
		startAnimation();
		rl_no_data.setVisibility(View.GONE);
		request(AppConfig.REQUEST_SV_GET_LOGISTICS_DATA_CODE);
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
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = "http://www.kuaidi100.com/query";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_LOGISTICS_DATA_CODE:
			params.add(new MyNameValuePair("type", typeStr));
			params.add(new MyNameValuePair("postid", postId));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_LOGISTICS_DATA_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_LOGISTICS_DATA_CODE:
			stopAnimation();
			if (result != null) {
				LogisticsEntity mainEn = (LogisticsEntity) result;
				if (mainEn.getErrCode() == 200) {
					if (mainEn.getMainLists() != null) {
						isSuccess = true;
						logLists.clear();
						logLists.addAll(mainEn.getMainLists());
					}
					updateListView(200);
				} else if (mainEn.getErrCode() == 201) {
					updateListView(201);
				} else {
					updateListView(0);
				}
			} else {
				updateListView(0);
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	private void updateListView(int code) {
		if (logLists.size() == 0) {
			if (code == 201) {
				tv_no_data.setText(R.string.order_logistic_expired);
			} else {
				tv_no_data.setText(R.string.order_logistic_no_data);
			}
			rl_no_data.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}else {
			rl_no_data.setVisibility(View.GONE);
			lv_adapter.updateAdapter(logLists);
			mListView.setVisibility(View.VISIBLE);
		}
	}

}
