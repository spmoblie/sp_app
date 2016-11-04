package com.spshop.stylistpark.activity.mine;

import android.os.Bundle;
import android.os.Handler;
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
import com.spshop.stylistpark.adapter.MemberListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MemberEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * "会员列表"Activity
 */
public class MemberListActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "MemberListActivity";

	public static final int TYPE_1 = 0;  //全部
	public static final int TYPE_2 = 1;  //收入榜
	public static final int TYPE_3 = 2;  //订单榜
	public static final int TYPE_4 = 3;

	private int dataTotal = 0; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //最新客户加载页
	private int page_type_2 = 1;  //活跃排名加载页
	private int page_type_3 = 1;  //订单排名加载页
	private int page_type_4 = 1;  //收入排名加载页
	private int topType = TYPE_1; //Top标记
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int total_1, total_2, total_3, total_4;
	private boolean isLoadOk = true; //加载数据控制符
	private boolean isLogined, isUpdate, isSuccess;
	private String titleStr, noDataShowStr;

	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private RelativeLayout rl_top_screen, rl_loading;
	private FrameLayout rl_no_data;
	private ImageView iv_to_top;
	private TextView tv_no_data;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private MemberListAdapter lv_adapter;
	
	private List<MemberEntity> lv_show = new ArrayList<MemberEntity>();
	private List<MemberEntity> lv_all_1 = new ArrayList<MemberEntity>();
	private List<MemberEntity> lv_all_2 = new ArrayList<MemberEntity>();
	private List<MemberEntity> lv_all_3 = new ArrayList<MemberEntity>();
	private List<MemberEntity> lv_all_4 = new ArrayList<MemberEntity>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_2 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_3 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_4 = new ArrayMap<String, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_radio_common);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		shared.edit().putBoolean(AppConfig.KEY_PUSH_PAGE_MEMBER, false).apply();
		topType = getIntent().getExtras().getInt("topType", TYPE_1);
		titleStr = getIntent().getExtras().getString("showTitle", "");

		findViewById();
		initView();
	}
	
	private void findViewById() {
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		rl_top_screen = (RelativeLayout) findViewById(R.id.topbar_radio_rl_screen);
		rl_loading = (RelativeLayout) findViewById(R.id.loading_anim_large_ll_main);
		rl_no_data = (FrameLayout) findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) findViewById(R.id.loading_no_data_tv_show);
		refresh_lv = (PullToRefreshListView) findViewById(R.id.list_radio_common_refresh_lv);
		iv_to_top = (ImageView) findViewById(R.id.list_radio_common_iv_to_top);
	}

	private void initView() {
		setTitle(titleStr);
		noDataShowStr = getString(R.string.loading_no_data, titleStr);
		iv_to_top.setOnClickListener(this);
		
		initRaidoGroup();
		initListView();
		setDefaultRadioButton();
	}

	private void initRaidoGroup() {
		btn_1.setText(getString(R.string.member_top_tab_1));
		btn_1.setOnClickListener(this);
		btn_2.setText(getString(R.string.member_top_tab_2));
		btn_2.setOnClickListener(this);
		btn_3.setText(getString(R.string.member_top_tab_3));
		btn_3.setOnClickListener(this);
		btn_4.setText(getString(R.string.member_top_tab_4));
		btn_4.setOnClickListener(this);
		btn_4.setVisibility(View.GONE);
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
            	if (!isStopLoadMore(lv_show.size(), dataTotal, 0)) {
            		loadSVDatas();
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
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_callback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				
			}
		};
		lv_adapter = new MemberListAdapter(mContext, lv_show, lv_callback);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	/**
	 * 设置默认项
	 */
	private void setDefaultRadioButton() {
		RadioButton defaultBtn = null;
		switch (topType) {
		case TYPE_1:
			defaultBtn = btn_1;
			break;
		case TYPE_2:
			defaultBtn = btn_2;
			break;
		case TYPE_3:
			defaultBtn = btn_3;
			break;
		case TYPE_4:
			defaultBtn = btn_4;
			break;
		default:
			defaultBtn = btn_1;
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
		dataTotal = 0;
		setLoadMoreData();
		startAnimation();
		requestProductLists();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
		switch (topType) {
		case TYPE_1: //最新客户
			current_Page = page_type_1;
			break;
		case TYPE_2: //活跃排名
			current_Page = page_type_2;
			break;
		case TYPE_3: //订单排名
			current_Page = page_type_3;
			break;
		case TYPE_4: //收入排名
			current_Page = page_type_4;
			break;
		}
		requestProductLists();
	}

	/**
	 * 下拉刷新数据
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
				}, AppConfig.LOADING_TIME);
			}
			return;
		}
		isLoadOk = false;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_MEMBER_LIST_CODE);
			}
		}, AppConfig.LOADING_TIME);
	}

	@Override
	public void onClick(View v) {
		if (!isLoadOk) { //加载频率控制
			setDefaultRadioButton();
			return;
		}
		switch (v.getId()) {
		case R.id.topbar_radio_rb_1:
			if (topType == TYPE_1) return;
			topType = TYPE_1;
			noDataShowStr = getString(R.string.loading_no_data, titleStr);
			if (lv_all_1 != null && lv_all_1.size() > 0) {
				addOldListDatas(lv_all_1, page_type_1, total_1);
			}else {
				page_type_1 = 1;
				total_1 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_2:
			if (topType == TYPE_2) return;
			topType = TYPE_2;
			noDataShowStr = getString(R.string.loading_no_data, getString(R.string.member_data_null_2));
			if (lv_all_2 != null && lv_all_2.size() > 0) {
				addOldListDatas(lv_all_2, page_type_2, total_2);
			}else {
				page_type_2 = 1;
				total_2 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_3:
			if (topType == TYPE_3) return;
			topType = TYPE_3;
			noDataShowStr = getString(R.string.loading_no_data, getString(R.string.member_data_null_3));
			if (lv_all_3 != null && lv_all_3.size() > 0) {
				addOldListDatas(lv_all_3, page_type_3, total_3);
			}else {
				page_type_3 = 1;
				total_3 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_4:
			if (topType == TYPE_4) return;
			topType = TYPE_4;
			//noDataShowStr = getString(R.string.loading_no_data, getString(R.string.member_data_null_3));
			if (lv_all_4 != null && lv_all_4.size() > 0) {
				addOldListDatas(lv_all_4, page_type_4, total_4);
			}else {
				page_type_4 = 1;
				total_4 = 0;
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
	private void addOldListDatas(List<MemberEntity> oldLists, int oldPage, int oldTotal) {
		addAllShow(oldLists);
		current_Page = oldPage;
		dataTotal = oldTotal;
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
			lv_all_2.clear();
			lv_all_3.clear();
			lv_all_4.clear();
			am_all_1.clear();
			am_all_2.clear();
			am_all_3.clear();
			am_all_4.clear();
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
		String uri = AppConfig.URL_COMMON_MY_URL;;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_MEMBER_LIST_CODE:
			params.add(new MyNameValuePair("app", "member"));
			params.add(new MyNameValuePair("status", String.valueOf(topType)));
			params.add(new MyNameValuePair("page", String.valueOf(current_Page)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_MEMBER_LIST_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_MEMBER_LIST_CODE:
			if (result != null) {
				MemberEntity mainEn = (MemberEntity) result;
				if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					int newTotal = mainEn.getDataTotal();
					List<MemberEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						List<BaseEntity> newLists = null;
						switch (topType) {
						case TYPE_1:
							if (loadType == 0) { //下拉
								newLists = updNewEntity(newTotal, total_1, lists, lv_all_1, am_all_1);
							}else {
								newLists = addNewEntity(lv_all_1, lists, am_all_1);
								if (newLists != null) {
									page_type_1++;
								}
							}
							total_1 = newTotal;
							break;
						case TYPE_2:
							if (loadType == 0) { //下拉
								newLists = updNewEntity(newTotal, total_2, lists, lv_all_2, am_all_2);
							}else {
								newLists = addNewEntity(lv_all_2, lists, am_all_2);
								if (newLists != null) {
									page_type_2++;
								}
							}
							total_2 = newTotal;
							break;
						case TYPE_3:
							if (loadType == 0) { //下拉
								newLists = updNewEntity(newTotal, total_3, lists, lv_all_3, am_all_3);
							}else {
								newLists = addNewEntity(lv_all_3, lists, am_all_3);
								if (newLists != null) {
									page_type_3++;
								}
							}
							total_3 = newTotal;
							break;
						case TYPE_4:
							if (loadType == 0) { //下拉
								newLists = updNewEntity(newTotal, total_4, lists, lv_all_4, am_all_4);
							}else {
								newLists = addNewEntity(lv_all_4, lists, am_all_4);
								if (newLists != null) {
									page_type_4++;
								}
							}
							total_4 = newTotal;
							break;
						}
						if (newLists != null) {
							addNewShowLists(newLists);
						}
						dataTotal = newTotal;
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
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
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
		}
		myUpdateAdapter();
	}

	private void myUpdateAdapter() {
		if (current_Page == 1) {
			toTop();
		}
		lv_adapter.updateAdapter(lv_show);
		stopAnimation();
	}

	private void addAllShow(List<MemberEntity> showLists) {
		lv_show.clear();
		lv_show.addAll(showLists);
	}

	private void addNewShowLists(List<BaseEntity> showLists) {
		lv_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			lv_show.add((MemberEntity) showLists.get(i));
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
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
		if (lv_show.size() == 0) {
			tv_no_data.setText(noDataShowStr);
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
