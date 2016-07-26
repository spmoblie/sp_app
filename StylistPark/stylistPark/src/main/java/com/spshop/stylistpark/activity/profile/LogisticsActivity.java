package com.spshop.stylistpark.activity.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.LogisticsListAdapter;
import com.spshop.stylistpark.entity.LogisticsEntity;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.List;


public class LogisticsActivity extends BaseActivity {

	private static final String TAG = "LogisticsActivity";
	public boolean isUpdate = false;
	
	private FrameLayout rl_no_data;
	private TextView tv_no_data;
	private ListView mListView;
	private LogisticsListAdapter lv_adapter;
	private boolean isLogined, isSuccess;
	private String typeStr, postId;
	private LogisticsEntity mainEn;
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
		request(AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE);
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
			mainEn = sc.getLogisticsDatas(typeStr, postId);
			return mainEn;
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
