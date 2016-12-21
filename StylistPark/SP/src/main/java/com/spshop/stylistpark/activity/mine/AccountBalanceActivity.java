package com.spshop.stylistpark.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.MyWebViewActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.BalanceListAdapter;
import com.spshop.stylistpark.entity.BalanceDetailEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class AccountBalanceActivity extends BaseActivity {
	
	private static final String TAG = "AccountBalanceActivity";
	public static AccountBalanceActivity instance = null;
	private int dataTotal = 0; //数据总量

	private int current_Page = 1;  //当前列表加载页
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int overStatus = 0; //余额状态
	private String overHintStr; //余额状态描述
	private double amountTotal = 0; //账号余额
	private boolean isLogined, isUpdate, isSuccess;

	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private BalanceListAdapter lv_adapter;
	private TextView tv_curr, tv_amount, tv_withdrawals, tv_hint, tv_no_data;

	private List<BalanceDetailEntity> lv_show = new ArrayList<BalanceDetailEntity>();
	private List<BalanceDetailEntity> lv_all_1 = new ArrayList<BalanceDetailEntity>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<String, Boolean>();
	
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
		tv_curr = (TextView) findViewById(R.id.balance_list_head_tv_curr);
		tv_amount = (TextView) findViewById(R.id.balance_list_head_tv_amount);
		tv_withdrawals = (TextView) findViewById(R.id.balance_list_head_tv_withdrawals);
		tv_hint = (TextView) findViewById(R.id.balance_list_head_tv_withdrawals_hint);
		tv_no_data = (TextView) findViewById(R.id.account_balance_tv_no_data);
	}

	private void initView() {
		setTitle(R.string.mine_account_money);
		setBtnRight(getString(R.string.money_recharge));

		tv_withdrawals.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (overStatus) {
					case 1: //未实名
						startActivity(new Intent(mContext, AuthenticationActivity.class));
						break;
					case 2: //可提现
						if (isSuccess) {
							Intent intent = new Intent(mContext, WithdrawalsActivity.class);
							intent.putExtra("amountTotal", amountTotal);
							startActivity(intent);
						}
						break;
					case 3: //提现中
						CommonTools.showToast(overHintStr, 2000);
						break;
				}
			}
		});

		initListView();
		setAdapter();
		setHeadView(null);
	}

	private void setHeadView(BalanceDetailEntity mainEn) {
		String currStr = LangCurrTools.getCurrencyValue();
		tv_curr.setText(currStr);
		if (mainEn != null) {
			amountTotal = mainEn.getAmount();
			UserManager.getInstance().saveUserMoney(currStr + amountTotal);
			tv_amount.setText(decimalFormat.format(amountTotal));
			overHintStr = mainEn.getStatusHint();
			overStatus = mainEn.getStatus();
			if (overStatus == 3) { //提现中
				tv_hint.setText(overHintStr);
				tv_hint.setVisibility(View.VISIBLE);
				tv_withdrawals.setVisibility(View.GONE);
			} else {
				tv_hint.setVisibility(View.GONE);
				tv_withdrawals.setVisibility(View.VISIBLE);
			}
		}
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
				} else {
					refreshSVDatas();
				}
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 加载更多
            	if (!isStopLoadMore(lv_show.size(), dataTotal, 0)) {
            		loadMoreDatas();
				}else {
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							refresh_lv.onPullUpRefreshComplete();
							refresh_lv.setHasMoreData(false);
						}
					}, AppConfig.LOADING_TIME);
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
		loadType = 1;
		current_Page = 1;
		requestProductLists();
	}

	/**
	 * 加载下拉刷新数据
	 */
	private void refreshSVDatas() {
		loadType = 0;
		current_Page = 1;
		requestProductLists();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadMoreDatas() {
		loadType = 1;
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
		}, AppConfig.LOADING_TIME);
	}

	private void toUpgradeDaren() {
		Intent intent = new Intent(mContext, MyWebViewActivity.class);
		intent.putExtra("goodsId", AppConfig.SP_JION_PROGRAM_ID);
		intent.putExtra("title", getString(R.string.money_jion_program));
		intent.putExtra("lodUrl", AppConfig.URL_COMMON_TOPIC_URL + "?topic_id=" + AppConfig.SP_JION_PROGRAM_ID);
		startActivity(intent);
	}

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		Intent intent = new Intent(mContext, AddCouponActivity.class);
		intent.putExtra("pageType", 1);
		startActivity(intent);
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
			lv_show.clear();
			lv_all_1.clear();
			am_all_1.clear();
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
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE:
			params.add(new MyNameValuePair("app", "account"));
			params.add(new MyNameValuePair("page", String.valueOf(current_Page)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE, uri, params, HttpUtil.METHOD_GET);
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
			if (result != null) {
				BalanceDetailEntity mainEn = (BalanceDetailEntity) result;
				if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					if (current_Page == 1) {
						setHeadView(mainEn);
					}
					int newTotal = mainEn.getDataTotal();
					List<BalanceDetailEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						List<BaseEntity> newLists;
						if (loadType == 0) { //下拉
							newLists = updNewEntity(newTotal, dataTotal, lists, lv_all_1, am_all_1);
						}else {
							newLists = addNewEntity(lv_all_1, lists, am_all_1);
							if (newLists != null) {
								current_Page++;
							}
						}
						dataTotal = newTotal;
						if (newLists != null) {
							addNewShowLists(newLists);
						}
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

	private void addNewShowLists(List<BaseEntity> showLists) {
		lv_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			lv_show.add((BalanceDetailEntity) showLists.get(i));
		}
		lv_all_1.clear();
		lv_all_1.addAll(lv_show);
	}
	
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
	}

}
