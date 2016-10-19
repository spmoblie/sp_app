package com.spshop.stylistpark.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
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

public class AccountBalanceActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "AccountBalanceActivity";
	public static AccountBalanceActivity instance = null;
	private int dataTotal = 0; //数据总量

	private int current_Page = 1;  //当前列表加载页
	private int overStatus = 0; //余额状态
	private String overHintStr; //余额状态描述
	private double amountTotal = 0; //账号余额
	private boolean isLogined, isUpdate, isSuccess;

	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private BalanceListAdapter lv_adapter;
	private LinearLayout ll_auth_main;
	private TextView tv_amount_title, tv_amount, tv_hint, tv_auth, tv_no_data;

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
		tv_amount_title = (TextView) findViewById(R.id.balance_list_head_tv_amount_title);
		tv_amount = (TextView) findViewById(R.id.balance_list_head_tv_amount);
		ll_auth_main = (LinearLayout) findViewById(R.id.balance_list_head_ll_auth);
		tv_hint = (TextView) findViewById(R.id.balance_list_head_tv_withdrawals_hint);
		tv_auth = (TextView) findViewById(R.id.balance_list_head_tv_auth);
		tv_no_data = (TextView) findViewById(R.id.account_balance_tv_no_data);
	}

	private void initView() {
		setTitle(R.string.mine_account_money);
		setBtnRight(getString(R.string.money_withdrawals));
		tv_auth.setOnClickListener(this);

		initListView();
		setAdapter();
		setHeadView(null);
	}

	private void setHeadView(BalanceDetailEntity mainEn) {
		ll_auth_main.setVisibility(View.GONE);
		String currStr = LangCurrTools.getCurrencyValue();
		if (mainEn != null) {
			amountTotal = mainEn.getAmount();
			UserManager.getInstance().saveUserMoney(currStr + amountTotal);
			tv_amount.setText(decimalFormat.format(amountTotal));

			ll_auth_main.setVisibility(View.VISIBLE);
			tv_auth.setVisibility(View.GONE);
			tv_hint.setVisibility(View.VISIBLE);
			overStatus = mainEn.getStatus();
			switch (overStatus) {
				case 1: //提现中
					overHintStr = mainEn.getStatusHint();
					tv_hint.setText(overHintStr);
					break;
				case 2: //未认证
					overHintStr = getString(R.string.money_auth_explain);
					tv_hint.setText(overHintStr);
					//tv_hint.setText(overHintStr + "，");
					//tv_auth.setVisibility(View.VISIBLE);
					break;
				case 3: //非达人
					tv_hint.setText(R.string.money_over_hint);
					break;
				default: //可提现
					ll_auth_main.setVisibility(View.GONE);
					break;
			}
		}
		currStr = currStr.replace(" ", "");
		tv_amount_title.setText(getString(R.string.money_balance_burrency, currStr));
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
            	if (!isStopLoadMore(lv_show.size(), dataTotal, 0)) {
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
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	public void OnListenerRight() {
		switch (overStatus) {
			case 1: //提现中
			case 2: //未认证
				CommonTools.showToast(overHintStr, 2000);
				break;
			case 3: //非达人
				showConfirmDialog(R.string.money_over_hint, getString(R.string.cancel),
						getString(R.string.money_upgrade), true, true, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								switch (msg.what) {
									case DIALOG_CONFIRM_CLICK:
										toUpgradeDaren();
										break;
								}
							}
						});
				break;
			default: //可提现
				if (isSuccess) {
					Intent intent = new Intent(mContext, WithdrawalsActivity.class);
					intent.putExtra("amountTotal", amountTotal);
					startActivity(intent);
				}
				break;
		}
	}

	private void toUpgradeDaren() {
		Intent intent = new Intent(mContext, MyWebViewActivity.class);
		intent.putExtra("goodsId", AppConfig.SP_JION_PROGRAM_ID);
		intent.putExtra("title", getString(R.string.money_jion_program));
		intent.putExtra("lodUrl", AppConfig.URL_COMMON_TOPIC_URL + "?topic_id=" + AppConfig.SP_JION_PROGRAM_ID);
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
					dataTotal = mainEn.getDataTotal();
					List<BalanceDetailEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						List<BaseEntity> newLists = addNewEntity(lv_all_1, lists, am_all_1);
						if (newLists != null) {
							current_Page++;
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
