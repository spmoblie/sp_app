package com.spshop.stylistpark.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.BalanceListAdapter;
import com.spshop.stylistpark.entity.BalanceDetailEntity;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class AccountBalanceActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AccountBalanceActivity";
	public static AccountBalanceActivity instance = null;
	public boolean isUpdate = false;
	
	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int countTotal = 0; //数集总数量
	private int amountTotal = 0; //账号余额
	private boolean isLogined, isSuccess;

	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private BalanceListAdapter lv_adapter;
	private Button btn_withdrawals;
	private LinearLayout ll_auth;
	private TextView tv_amount_title, tv_amount, tv_hint, tv_auth, tv_no_data;

	private BalanceDetailEntity mainEn;
	private List<BalanceDetailEntity> lv_show = new ArrayList<BalanceDetailEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_balance);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		refresh_lv = (PullToRefreshListView) findViewById(R.id.account_balance_refresh_lv);
		tv_amount_title = (TextView) findViewById(R.id.balance_list_head_tv_amount_title);
		tv_amount = (TextView) findViewById(R.id.balance_list_head_tv_amount);
		tv_hint = (TextView) findViewById(R.id.balance_list_head_tv_withdrawals_hint);
		tv_auth = (TextView) findViewById(R.id.balance_list_head_tv_auth);
		tv_no_data = (TextView) findViewById(R.id.account_balance_tv_no_data);
		ll_auth = (LinearLayout) findViewById(R.id.balance_list_head_ll_auth);
		btn_withdrawals = (Button) findViewById(R.id.balance_list_head_btn_withdrawals);
	}

	private void initView() {
		setTitle(R.string.profile_account_money);
		tv_auth.setOnClickListener(this);
		btn_withdrawals.setOnClickListener(this);
		
		initListView();
		setAdapter();
		setHeadView();
	}

	private void setHeadView() {
		if (mainEn != null) {
			amountTotal = mainEn.getAmount();
			String amountStr = String.valueOf(amountTotal);
			if (StringUtil.isNull(amountStr)) {
				amountStr = "0";
			}
			if (amountStr.length() <= 1) {
				tv_amount.setTextSize(24);
			}else if (amountStr.length() == 2) {
				tv_amount.setTextSize(23);
			}else if (amountStr.length() == 3) {
				tv_amount.setTextSize(22);
			}else if (amountStr.length() == 4) {
				tv_amount.setTextSize(21);
			}else if (amountStr.length() == 5) {
				tv_amount.setTextSize(20);
			}else {
				tv_amount.setTextSize(18);
			}
			tv_amount.setText(amountStr);
			switch (mainEn.getStatus()) {
			case 0: //未认证
				ll_auth.setVisibility(View.VISIBLE);
				btn_withdrawals.setVisibility(View.GONE);
				break;
			case 1: //可提现
				ll_auth.setVisibility(View.GONE);
				btn_withdrawals.setVisibility(View.VISIBLE);
				break;
			case 2: //提现中
				btn_withdrawals.setVisibility(View.GONE);
				ll_auth.setVisibility(View.VISIBLE);
				tv_auth.setVisibility(View.GONE);
				tv_hint.setText(mainEn.getStatusHint());
				break;
			}
		}
		tv_amount_title.setText(getString(R.string.money_balance_burrency, LangCurrTools.getCurrencyValue()));
	}

	private void initListView() {
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 下拉刷新
            	if (lv_show.size() == 0) {
            		getSVDatas();
				}else {
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							refresh_lv.onPullDownRefreshComplete();
						}
					}, 1000);
				}
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 加载更多
            	if (!isStopLoadMore(lv_show.size(), countTotal)) {
            		loadMoreDatas();
				}else {
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							refresh_lv.onPullUpRefreshComplete();
							refresh_lv.setHasMoreData(false);
						}
					}, 1000);
				}
            }
        });
		mListView = refresh_lv.getRefreshableView();
		mListView.setDivider(null);
		mListView.setSelector(R.color.ui_bg_color_app);
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_callback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				if (entity != null) {
					
				}
			}
		};
		lv_adapter = new BalanceListAdapter(mContext, lv_show, lv_callback);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		current_Page = 1;
		lv_show.clear();
		requestProductLists();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadMoreDatas() {
		requestProductLists();
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		tv_no_data.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE);
			}
		}, 1000);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.balance_list_head_tv_auth:
			intent = new Intent(mContext, AuthenticationActivity.class);
			break;
		case R.id.balance_list_head_btn_withdrawals:
			intent = new Intent(mContext, WithdrawalsActivity.class);
			intent.putExtra("amountTotal", amountTotal);
			break;
		}
		if (intent != null) {
			startActivity(intent);
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
        	refresh_lv.doPullRefreshing(true, 500);
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
		instance = null;
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE:
			mainEn = null;
			mainEn = sc.getBalanceDetailList(current_Page, Page_Count);
			return mainEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE:
			if (mainEn != null) {
				if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					if (current_Page == 1) {
						setHeadView();
					}
					countTotal = mainEn.getCountTotal();
					List<BalanceDetailEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						lv_show.addAll(lists);
						current_Page++;
					}
				}else if (mainEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}
				myUpdateAdapter();
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
		stopAnimation();
	}

	private void myUpdateAdapter() {
		if (lv_show.size() == 0) {
			tv_no_data.setVisibility(View.VISIBLE);
			tv_no_data.setText(R.string.money_no_detail);
		}
		lv_adapter.updateAdapter(lv_show);
	}
	
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
	}

}
