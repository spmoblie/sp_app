package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ShowList2ItemAdapter;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * "通用商品展示列表"
 */
@SuppressLint("UseSparseArrays")
public class ShowListActivity extends BaseActivity {
	
	private static final String TAG = "ShowListActivity";
	public static ShowListActivity instance = null;
	public static final int PAGE_ROOT_CODE_1 = 1001; //ChildFragmentFive：收藏商品
	public static final int PAGE_ROOT_CODE_2 = 1002; //ChildFragmentFive：浏览记录
	public boolean isUpdate = false;
	
	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int countTotal = 0; //商品总数量
	private boolean isLoadOk = true;
	
	private FrameLayout rl_no_data;
	private TextView tv_no_data;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ShowList2ItemAdapter lv_two_adapter;
	
	private int pageCode = PAGE_ROOT_CODE_1;
	private String pageName = "";
	private ProductListEntity product_MainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_lists_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all = new ArrayList<ProductListEntity>();
	private HashMap<Integer, Boolean> hm_all = new HashMap<Integer, Boolean>();
	
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
	}

	private void initView() {
		setTitle(pageName);
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
            	if (lv_lists_show.size() == 0) {
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
            	if (!isStop()) {
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
				ProductListEntity data = (ProductListEntity) entity;
				Intent intent = new Intent(mContext, ProductDetailActivity.class);
				intent.putExtra("goodsId", data.getId());
				startActivity(intent);
			}
		};
		lv_two_adapter = new ShowList2ItemAdapter(mContext, lv_show_two, lv_callback);
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
		}, 1000);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        
        if (pageCode == PAGE_ROOT_CODE_1 && isUpdate) {
        	isUpdate = false;
        	lv_show_two.clear();
        	lv_lists_show.clear();
        	lv_lists_all.clear();
        	hm_all.clear();
        	refresh_lv.doPullRefreshing(true, 500);
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
		product_MainEn = null;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_USER_PRODUCT_LIST:
			switch (pageCode) {
			case PAGE_ROOT_CODE_1: //收藏商品
				product_MainEn = sc.getCollectionOrHistoryList(Page_Count, current_Page, "collection");
				break;
			case PAGE_ROOT_CODE_2: //浏览记录
				product_MainEn = sc.getCollectionOrHistoryList(Page_Count, current_Page, "history");
				break;
			}
			break;
		}
		return product_MainEn;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		if (product_MainEn != null && product_MainEn.getMainLists() != null) {
			countTotal = product_MainEn.getTotal();
			List<ProductListEntity> lists = product_MainEn.getMainLists();
			if (lists.size() > 0) {
				addEntity(lv_lists_all, lists, hm_all);
				current_Page++;
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
		super.onFailure(requestCode, state, result);
		loadFailHandle();
	}

	private void loadFailHandle() {
		stopAnimation();
	}
	
	/**
	 * 数据去重函数
	 */
	private void addEntity(List<ProductListEntity> oldDatas, List<ProductListEntity> newDatas, HashMap<Integer, Boolean> hashMap) {
		ProductListEntity entity = null;
		for (int i = 0; i < newDatas.size(); i++) {
			entity = newDatas.get(i);
			if (entity != null && !hashMap.containsKey(entity.getId())) {
				oldDatas.add(entity);
			}
		}
		addAllShow(oldDatas);
		hashMap.clear();
		for (int i = 0; i < oldDatas.size(); i++) {
			hashMap.put(oldDatas.get(i).getId(), true);
		}
	}

	private void addAllShow(List<ProductListEntity> showLists) {
		lv_lists_show.clear();
		lv_lists_show.addAll(showLists);
	}
	
	private void myUpdateAdapter() {
		lv_show_two.clear();
		ListShowTwoEntity lstEn = null;
		for (int i = 0; i < lv_lists_show.size(); i++) {
			ProductListEntity en = lv_lists_show.get(i);
			if (i%2 == 0) {
				lstEn = new ListShowTwoEntity();
				lstEn.setLeftEn(en);
				if (i+1 < lv_lists_show.size()) {
					lstEn.setRightEn(lv_lists_show.get(i+1));
				}
				lv_show_two.add(lstEn);
			}
		}
		lv_two_adapter.updateAdapter(lv_show_two);
		stopAnimation();
	}
	
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
		if (lv_lists_show.size() == 0) {
			tv_no_data.setText(getString(R.string.loading_no_data, pageName));
			rl_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			rl_no_data.setVisibility(View.GONE);
			refresh_lv.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 商品数小于一页时停止加载翻页数据
	 */
	private boolean isStop(){
		return lv_lists_show.size() < Page_Count || lv_lists_show.size() == countTotal;
	}
	
}
