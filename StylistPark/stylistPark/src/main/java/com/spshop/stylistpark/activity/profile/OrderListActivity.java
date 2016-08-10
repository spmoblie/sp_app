package com.spshop.stylistpark.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.MemberOrderListAdapter;
import com.spshop.stylistpark.adapter.OrderListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;
import com.spshop.stylistpark.wxapi.WXPayEntryActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * "订单列表"Activity
 */
public class OrderListActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "OrderListActivity";
	public static OrderListActivity instance = null;
	public boolean isUpdate = false;
	public static final int TYPE_1 = 0;  //全部
	public static final int TYPE_2 = 1;  //待付款或已完成
	public static final int TYPE_3 = 2;  //待发货或待分成
	public static final int TYPE_4 = 3;  //待收货
	public static final int TYPE_5 = 4;  //退换货

	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //全部列表加载页
	private int page_type_2 = 1;  //待付款或已完成列表加载页
	private int page_type_3 = 1;  //待发货或待分成列表加载页
	private int page_type_4 = 1;  //待收货列表加载页
	private int page_type_5 = 1;  //退换货列表加载页
	private int topType = TYPE_1; //Top标记
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int countTotal = 0; //数集总数量
	private int total_1, total_2, total_3, total_4, total_5;
	private boolean isLoadOk = true; //加载数据控制符
	private boolean isLogined, isSuccess;

	private int rootCode = 0; //页面来源标记
	private String orderId, orderStr, noDataShowStr;
	
	private RadioButton btn_1, btn_2, btn_3, btn_4, btn_5;
	private RelativeLayout rl_top_screen, rl_loading;
	private FrameLayout rl_no_data;
	private ImageView iv_to_top;
	private TextView tv_no_data;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback, lv_callback_member;
	private OrderListAdapter lv_adapter;
	private MemberOrderListAdapter lv_adapter_member;
	
	private OrderEntity mainEn;
	private List<OrderEntity> lv_show = new ArrayList<OrderEntity>();
	private List<OrderEntity> lv_all_1 = new ArrayList<OrderEntity>();
	private List<OrderEntity> lv_all_2 = new ArrayList<OrderEntity>();
	private List<OrderEntity> lv_all_3 = new ArrayList<OrderEntity>();
	private List<OrderEntity> lv_all_4 = new ArrayList<OrderEntity>();
	private List<OrderEntity> lv_all_5 = new ArrayList<OrderEntity>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_2 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_3 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_4 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_5 = new ArrayMap<String, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_radio_common);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		instance = this;
		rootCode = getIntent().getExtras().getInt("rootType", 0);
		topType = getIntent().getExtras().getInt("topType", TYPE_1);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		btn_5 = (RadioButton) findViewById(R.id.topbar_radio_rb_5);
		rl_top_screen = (RelativeLayout) findViewById(R.id.topbar_radio_rl_screen);
		rl_loading = (RelativeLayout) findViewById(R.id.loading_anim_large_ll_main);
		rl_no_data = (FrameLayout) findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) findViewById(R.id.loading_no_data_tv_show);
		refresh_lv = (PullToRefreshListView) findViewById(R.id.list_radio_common_refresh_lv);
		iv_to_top = (ImageView) findViewById(R.id.list_radio_common_iv_to_top);
	}

	private void initView() {
		orderStr = getString(R.string.order_order);
		if (rootCode == 0) {
			setTitle(R.string.profile_my_order);
		}else { //会员订单
			setTitle(R.string.profile_member_order);
		}
		rl_loading.setVisibility(View.GONE);
		iv_to_top.setOnClickListener(this);
		
		initRaidoGroup();
		initListView();
		setDefaultRadioButton();
	}

	private void initRaidoGroup() {
		if (rootCode == 0) {
			btn_1.setText(getString(R.string.order_top_tab_1));
			btn_2.setText(getString(R.string.order_top_tab_2));
			btn_3.setText(getString(R.string.order_top_tab_3));
			btn_4.setText(getString(R.string.order_top_tab_4));
			
			btn_5.setVisibility(View.VISIBLE);
			btn_5.setText(getString(R.string.order_top_tab_5));
			btn_5.setOnClickListener(this);
		}else { //会员订单
			btn_1.setText(getString(R.string.order_top_tab_1));
			btn_2.setText(getString(R.string.profile_done));
			btn_3.setText(getString(R.string.profile_wait_commission));
			btn_4.setText(getString(R.string.order_top_tab_4));
			btn_4.setVisibility(View.GONE);
		}
		btn_1.setOnClickListener(this);
		btn_2.setOnClickListener(this);
		btn_3.setOnClickListener(this);
		btn_4.setOnClickListener(this);
		rl_top_screen.setVisibility(View.GONE);
	}

	private void initListView() {
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 下拉刷新
				refreshSVDatas();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 加载更多
            	if (!isStopLoadMore(lv_show.size(), countTotal)) {
            		loadSVDatas();
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
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		if (rootCode == 0) {
			lv_callback = new AdapterCallback() {
				
				@Override
				public void setOnClick(Object entity, int position, int type) {
					if (entity != null) {
						OrderEntity data = (OrderEntity) entity;
						switch (type) {
						case OrderListAdapter.TYPE_CHECK:
							Intent intent = new Intent(mContext, OrderDetailActivity.class);
							intent.putExtra("orderId", data.getOrderId());
							startActivity(intent);
							break;
						case OrderListAdapter.TYPE_PAY:
							startPayActivity(data);
							break;
						case OrderListAdapter.TYPE_CACEL:
							confirmCacelOrder(data);
							break;
						}
					}
				}

			};
			lv_adapter = new OrderListAdapter(mContext, lv_show, lv_callback);
			mListView.setAdapter(lv_adapter);
			mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		}else { //会员订单
			lv_callback_member = new AdapterCallback() {

				@Override
				public void setOnClick(Object entity, int position, int type) {
					
				}
			};
			lv_adapter_member = new MemberOrderListAdapter(mContext, lv_show, lv_callback_member);
			mListView.setAdapter(lv_adapter_member);
			mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		}
	}

	/**
	 * 跳转到支付页面
	 */
	private void startPayActivity(OrderEntity orderEn) {
		if (orderEn != null) {
			Intent intent =new Intent(mContext, WXPayEntryActivity.class);
			intent.putExtra("orderSn", orderEn.getOrderNo());
			intent.putExtra("orderTotal", LangCurrTools.getCurrencyValue() + orderEn.getPriceTotal());
			intent.putExtra("root", TAG);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else {
			CommonTools.showToast(getString(R.string.pay_order_error), 1000);
		}
	}

	/**
	 * 确认取消订单
	 */
	private void confirmCacelOrder(final OrderEntity data) {
		showConfirmDialog(R.string.order_cacel_confirm, getString(R.string.confirm),
				getString(R.string.being_not), true, true, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case DIALOG_CANCEL_CLICK:
								postCacelOrder(data);
								break;
						}
					}
				});
	}

	private void postCacelOrder(OrderEntity data) {
		if (data != null) {
			orderId = data.getOrderId();
			startAnimation();
			request(AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE);
		}
	}

	/**
	 * 设置默认项
	 */
	private void setDefaultRadioButton() {
		RadioButton defaultBtn = null;
		switch (topType) {
		case TYPE_1:
			defaultBtn = btn_1;
			noDataShowStr = orderStr;
			break;
		case TYPE_2:
			defaultBtn = btn_2;
			if (rootCode == 0) {
				noDataShowStr = getString(R.string.profile_wait_pay) + orderStr;
			}else { //会员订单
				noDataShowStr = getString(R.string.profile_done) + orderStr;
			}
			break;
		case TYPE_3:
			defaultBtn = btn_3;
			if (rootCode == 0) {
				noDataShowStr = getString(R.string.profile_wait_delivery) + orderStr;
			}else { //会员订单
				noDataShowStr = getString(R.string.profile_wait_commission) + orderStr;
			}
			break;
		case TYPE_4:
			defaultBtn = btn_4;
			noDataShowStr = getString(R.string.profile_wait_receive) + orderStr;
			break;
		case TYPE_5:
			defaultBtn = btn_5;
			noDataShowStr = getString(R.string.profile_repair_return) + orderStr;
			break;
		default:
			defaultBtn = btn_1;
			noDataShowStr = orderStr;
			break;
		}
		defaultBtn.setChecked(true);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		isSuccess = false;
		loadType = 1;
		current_Page = 1;
		countTotal = 0;
		startAnimation();
		setLoadMoreData();
		requestProductLists();
	}

	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
		switch (topType) {
		case TYPE_1: //全部
			current_Page = page_type_1;
			break;
		case TYPE_2: //待付款或已完成
			current_Page = page_type_2;
			break;
		case TYPE_3: //待发货或待分成
			current_Page = page_type_3;
			break;
		case TYPE_4: //待收货
			current_Page = page_type_4;
			break;
		case TYPE_5: //返修、退换
			current_Page = page_type_5;
			break;
		}
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
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		if (!isLoadOk) { //加载频率控制
			if (loadType == 0) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						refresh_lv.onPullDownRefreshComplete();
					}
				}, 1000);
			}
			return;
		}
		isLoadOk = false;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_ORDER_LIST_CODE);
			}
		}, 1000);
	}

	@Override
	public void onClick(View v) {
		if (!isLoadOk) { //加载频率控制
			setDefaultRadioButton();
			return;
		}
		switch (v.getId()) {
		case R.id.topbar_radio_rb_1: //全部
			if (topType == TYPE_1) return;
			topType = TYPE_1;
			noDataShowStr = orderStr;
			if (lv_all_1 != null && lv_all_1.size() > 0) {
				addOldListDatas(lv_all_1, page_type_1, total_1);
			}else {
				page_type_1 = 1;
				total_1 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_2: //待付款或已完成
			if (topType == TYPE_2) return;
			topType = TYPE_2;
			if (rootCode == 0) {
				noDataShowStr = getString(R.string.profile_wait_pay) + orderStr;
			}else { //会员订单
				noDataShowStr = getString(R.string.profile_done) + orderStr;
			}
			if (lv_all_2 != null && lv_all_2.size() > 0) {
				addOldListDatas(lv_all_2, page_type_2, total_2);
			}else {
				page_type_2 = 1;
				total_2 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_3: //待发货或待分成
			if (topType == TYPE_3) return;
			topType = TYPE_3;
			if (rootCode == 0) {
				noDataShowStr = getString(R.string.profile_wait_delivery) + orderStr;
			}else { //会员订单
				noDataShowStr = getString(R.string.profile_wait_commission) + orderStr;
			}
			if (lv_all_3 != null && lv_all_3.size() > 0) {
				addOldListDatas(lv_all_3, page_type_3, total_3);
			}else {
				page_type_3 = 1;
				total_3 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_4: //待收货
			if (topType == TYPE_4) return;
			topType = TYPE_4;
			noDataShowStr = getString(R.string.profile_wait_receive) + orderStr;
			if (lv_all_4 != null && lv_all_4.size() > 0) {
				addOldListDatas(lv_all_4, page_type_4, total_4);
			}else {
				page_type_4 = 1;
				total_4 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_5: //退换货
			if (topType == TYPE_5) return;
			topType = TYPE_5;
			noDataShowStr = getString(R.string.profile_repair_return) + orderStr;
			if (lv_all_5 != null && lv_all_5.size() > 0) {
				addOldListDatas(lv_all_5, page_type_5, total_5);
			}else {
				page_type_5 = 1;
				total_5 = 0;
				getSVDatas();
			}
			break;
		case R.id.show_list_iv_to_top: //回顶
			toTop();
			break;
		}
	}

	/**
	 * 展示已缓存的数据并至顶
	 */
	private void addOldListDatas(List<OrderEntity> oldLists, int oldPage, int oldTotal) {
		addAllShow(oldLists);
		current_Page = oldPage;
		countTotal = oldTotal;
		myUpdateAdapter();
		if (current_Page != 1) {
			toTop();
		}
		setLoadMoreData();
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
			if (rootCode == 0 && lv_adapter != null) {
	        	lv_adapter.updateAdapter(lv_show);
			}
		}else {
			showTimeOutDialog(TAG);
		}
	}

	private void updateAllData() {
		if (isUpdate) {
			isUpdate = false;
			lv_show.clear();
			lv_all_1.clear();
			lv_all_2.clear();
			lv_all_3.clear();
			lv_all_4.clear();
			lv_all_5.clear();
			am_all_1.clear();
			am_all_2.clear();
			am_all_3.clear();
			am_all_4.clear();
			am_all_5.clear();
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
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ORDER_LIST_CODE:
			mainEn = null;
			if (rootCode == 0) {
				mainEn = sc.getOrderLists(topType, Page_Count, current_Page);
			}else { //会员订单
				mainEn = sc.getMemberOrderLists(topType, Page_Count, current_Page);
			}
			return mainEn;
		case AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE:
			return sc.postCacelOrder(orderId);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_ORDER_LIST_CODE:
			if (mainEn != null) {
				if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					boolean isOk;
					int total = mainEn.getOrderTotal();
					List<OrderEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						List<BaseEntity> newLists = null;
						switch (topType) {
						case TYPE_1: 
							if (loadType == 0) { //下拉
								newLists = updNewEntity(total, total_1, lists, lv_all_1, am_all_1);
							}else {
								newLists = addNewEntity(lv_all_1, lists, am_all_1);
								if (newLists != null) {
									page_type_1++;
								}
							}
							total_1 = total;
							break;
						case TYPE_2: 
							if (loadType == 0) { //下拉
								newLists = updNewEntity(total, total_2, lists, lv_all_2, am_all_2);
							}else {
								newLists = addNewEntity(lv_all_2, lists, am_all_2);
								if (newLists != null) {
									page_type_2++;
								}
							}
							total_2 = total;
							break;
						case TYPE_3: 
							if (loadType == 0) { //下拉
								newLists = updNewEntity(total, total_3, lists, lv_all_3, am_all_3);
								page_type_3 = 2;
							}else {
								newLists = addNewEntity(lv_all_3, lists, am_all_3);
								if (newLists != null) {
									page_type_3++;
								}
							}
							total_3 = total;
							break;
						case TYPE_4: 
							if (loadType == 0) { //下拉
								newLists = updNewEntity(total, total_4, lists, lv_all_4, am_all_4);
								page_type_4 = 2;
							}else {
								newLists = addNewEntity(lv_all_4, lists, am_all_4);
								if (newLists != null) {
									page_type_4++;
								}
							}
							total_4 = total;
							break;
						case TYPE_5: 
							if (loadType == 0) { //下拉
								newLists = updNewEntity(total, total_5, lists, lv_all_5, am_all_5);
								page_type_5 = 2;
							}else {
								newLists = addNewEntity(lv_all_5, lists, am_all_5);
								if (newLists != null) {
									page_type_5++;
								}
							}
							total_5 = total;
							break;
						}
						if (newLists != null) {
							addNewShowLists(newLists);
						}
						countTotal = total;
						myUpdateAdapter();
					}else {
						loadFailHandle();
					}
				}else {
					loadFailHandle();
				}
			}else {
				loadFailHandle();
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE:
			stopAnimation();
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isUpdate = true;
					updateAllData();
					if (ChildFragmentFive.instance != null) {
						ChildFragmentFive.instance.isUpdate = true;
					}
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						showErrorDialog(baseEn.getErrInfo());
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
		loadFailHandle();
	}

	private void loadFailHandle() {
		switch (topType) {
		case TYPE_1: 
			addAllShow(lv_all_1);
			break;
		case TYPE_2: 
			addAllShow(lv_all_2);
			break;
		case TYPE_3: 
			addAllShow(lv_all_3);
			break;
		case TYPE_4: 
			addAllShow(lv_all_4);
			break;
		case TYPE_5: 
			addAllShow(lv_all_5);
			break;
		}
		myUpdateAdapter();
	}

	private void myUpdateAdapter() {
		if (current_Page == 1) {
			toTop();
		}
		if (rootCode == 0) {
			lv_adapter.updateAdapter(lv_show);
		}else { //会员订单
			lv_adapter_member.updateAdapter(lv_show);
		}
		stopAnimation();
	}

	private void addAllShow(List<OrderEntity> showLists) {
		lv_show.clear();
		lv_show.addAll(showLists);
	}

	private void addNewShowLists(List<BaseEntity> showLists) {
		lv_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			lv_show.add((OrderEntity) showLists.get(i));
		}
		switch (topType) {
			case TYPE_1:
				lv_all_1.clear();
				lv_all_1.addAll(lv_show);
				break;
			case TYPE_2:
				lv_all_2.clear();
				lv_all_2.addAll(lv_show);
				break;
			case TYPE_3:
				lv_all_3.clear();
				lv_all_3.addAll(lv_show);
				break;
			case TYPE_4:
				lv_all_4.clear();
				lv_all_4.addAll(lv_show);
				break;
			case TYPE_5:
				lv_all_5.clear();
				lv_all_5.addAll(lv_show);
				break;
		}
		if (loadType == 0) {
			setLoadMoreData();
		}
	}

	@Override
	protected void startAnimation() {
		rl_no_data.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void stopAnimation() {
		isLoadOk = true;
		rl_loading.setVisibility(View.GONE);
		switch (loadType) {
		case 0: //下拉刷新
			refresh_lv.onPullDownRefreshComplete();
			break;
		case 1: //加载更多
			refresh_lv.onPullUpRefreshComplete();
			break;
		}
		if (lv_show.size() == 0) {
			if (rootCode == 0) {
				tv_no_data.setText(getString(R.string.loading_no_data, noDataShowStr));
			}else { //会员订单
				tv_no_data.setText(getString(R.string.member_no_order, noDataShowStr));
			}
			rl_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			rl_no_data.setVisibility(View.GONE);
			refresh_lv.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		setAdapter();
		iv_to_top.setVisibility(View.GONE);
	}

	/**
	 * 设置允许加载更多
	 */
	private void setLoadMoreData() {
		refresh_lv.setHasMoreData(true);
	}

}
