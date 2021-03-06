package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductList2ItemAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * "通用商品展示列表"
 */
@SuppressLint("UseSparseArrays")
public class ShowListActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ShowListActivity";
	public static final int PAGE_ROOT_CODE_1 = 1001; //ChildFragmentFive：收藏商品
	public static final int PAGE_ROOT_CODE_2 = 1002; //ChildFragmentFive：浏览记录
	public static ShowListActivity instance = null;

	private int dataTotal = 0; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private boolean isLoadOk = true;
	private boolean isUpdate = false;

	private int pageCode = PAGE_ROOT_CODE_1;
	private String pageName = "";
	
	private FrameLayout rl_no_data;
	private TextView tv_no_data;
	private ImageView iv_to_top;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ProductList2ItemAdapter lv_two_adapter;
	
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_1 = new ArrayList<ProductListEntity>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<String, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_head_common);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		pageCode = getIntent().getIntExtra("pageCode", PAGE_ROOT_CODE_1);
		pageName = getIntent().getStringExtra("pageName");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		refresh_lv = (PullToRefreshListView) findViewById(R.id.list_head_common_refresh_lv);
		rl_no_data = (FrameLayout) findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) findViewById(R.id.loading_no_data_tv_show);
		iv_to_top = (ImageView) findViewById(R.id.list_head_common_iv_to_top);
	}

	private void initView() {
		setTitle(pageName);
		iv_to_top.setOnClickListener(this);
		initListView();
		setAdapter();
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
					}, AppConfig.LOADING_TIME);
				}
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
		refresh_lv.doPullRefreshing(true, 500);
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
				if (entity == null) return;
				openProductDetailActivity(((ProductListEntity) entity).getId());
			}
		};
		lv_two_adapter = new ProductList2ItemAdapter(mContext, lv_show_two, lv_callback);
		mListView.setAdapter(lv_two_adapter);
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
	private void loadSVDatas() {
		requestProductLists();
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		rl_no_data.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				switch (pageCode) {
				case PAGE_ROOT_CODE_1:
					request(AppConfig.REQUEST_SV_GET_USER_PRODUCT_LIST);
					break;
				case PAGE_ROOT_CODE_2:
					request(AppConfig.REQUEST_SV_GET_USER_PRODUCT_LIST);
					break;
				}
			}
		}, AppConfig.LOADING_TIME);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.show_list_iv_to_top: //回顶
				toTop();
				break;
		}
	}

	public void updateData() {
		isUpdate = true;
	}
	
	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

        if (pageCode == PAGE_ROOT_CODE_1 && isUpdate) {
        	isUpdate = false;
        	lv_show_two.clear();
			lv_show.clear();
			lv_all_1.clear();
			am_all_1.clear();
        	refresh_lv.doPullRefreshing(true, 500);
		}
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
		instance = null;
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_USER_PRODUCT_LIST:
			switch (pageCode) {
			case PAGE_ROOT_CODE_1: //收藏商品
				params.add(new MyNameValuePair("app", "collection"));
				break;
			case PAGE_ROOT_CODE_2: //浏览记录
				params.add(new MyNameValuePair("app", "history"));
				break;
			}
			params.add(new MyNameValuePair("page", String.valueOf(current_Page)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_USER_PRODUCT_LIST, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		if (result != null) {
			ProductListEntity mainEn = (ProductListEntity) result;
			dataTotal = mainEn.getDataTotal();
			List<ProductListEntity> lists = mainEn.getMainLists();
			if (lists!= null && lists.size() > 0) {
				List<BaseEntity> newLists = addNewEntity(lv_all_1, lists, am_all_1);
				if (newLists != null) {
					addNewShowLists(newLists);
					current_Page++;
				}
				myUpdateAdapter();
			}else {
				loadFailHandle();
			}
		}else {
			loadFailHandle();
			showServerBusy();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
		loadFailHandle();
	}

	private void loadFailHandle() {
		stopAnimation();
	}

	private void myUpdateAdapter() {
		lv_show_two.clear();
		ListShowTwoEntity lstEn = null;
		for (int i = 0; i < lv_show.size(); i++) {
			ProductListEntity en = lv_show.get(i);
			if (i%2 == 0) {
				lstEn = new ListShowTwoEntity();
				lstEn.setLeftEn(en);
				if (i+1 < lv_show.size()) {
					lstEn.setRightEn(lv_show.get(i+1));
				}
				lv_show_two.add(lstEn);
			}
		}
		lv_two_adapter.updateAdapter(lv_show_two);
		stopAnimation();
	}

	private void addNewShowLists(List<BaseEntity> showLists) {
		lv_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			lv_show.add((ProductListEntity) showLists.get(i));
		}
		lv_all_1.clear();
		lv_all_1.addAll(lv_show);
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
		if (lv_show.size() == 0) {
			switch (pageCode) {
				case PAGE_ROOT_CODE_1: //收藏商品
					tv_no_data.setText(getString(R.string.loading_no_data, getString(R.string.mine_concerns_goods)));
					break;
				case PAGE_ROOT_CODE_2: //浏览记录
					tv_no_data.setText(getString(R.string.loading_no_data, getString(R.string.mine_history_no_data)));
					break;
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

}
