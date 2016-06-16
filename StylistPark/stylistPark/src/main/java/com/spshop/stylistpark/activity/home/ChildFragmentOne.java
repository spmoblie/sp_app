package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.common.MipcaActivityCapture;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ShowList2ItemAdapter;
import com.spshop.stylistpark.dialog.LoadDialog;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.MyCountDownTimer;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("UseSparseArrays")
public class ChildFragmentOne extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentOne";
	public static ChildFragmentOne instance = null;
	public boolean isUpdate = true;

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	private static final int Page_Count = 40;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int mCurrentItem, position;
	private boolean loadMore = false;
	private boolean rotation = true;
	private Context mContext;
	private NetworkInfo netInfo;
	private MyCountDownTimer mcdt;
	private ConnectivityManager cm;
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();
	
	private RelativeLayout rl_category, rl_search, rl_zxing;
	private LinearLayout ll_head_main, ll_indicator, ll_foot_main;
	private TextView tv_time_hour, tv_time_minute, tv_time_second;
	private ViewPager viewPager;
	private ImageView iv_to_top;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ShowList2ItemAdapter lv_two_adapter;
	private Runnable mPagerAction;
	private DisplayImageOptions options;
	
	private ProductListEntity mainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_lists_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_1 = new ArrayList<ProductListEntity>();
	private HashMap<Integer, Boolean> hm_all = new HashMap<Integer, Boolean>();
	private ImageView[] indicators = null;
	private ArrayList<View> viewLists = new ArrayList<View>();
	private ArrayList<String> urlLists = new ArrayList<String>();
	private ArrayList<ProductDetailEntity> imgEns = new ArrayList<ProductDetailEntity>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		LogUtil.i(TAG, "onCreate");
		instance = this;
		mContext = getActivity();
		atm = AsyncTaskManager.getInstance(mContext);
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
		
		// 动态注册广播
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(AppConfig.RECEIVER_ACTION_MAIN_DATA);
		mContext.registerReceiver(myBroadcastReceiver, mFilter);
		
		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_one, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(getActivity(), e);
		}
		return view;
	}

	private void findViewById(View view) {
		rl_category = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_category);
		rl_search = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_search);
		rl_zxing = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_zxing);
		mListView = (ListView) view.findViewById(R.id.fragment_one_listview);
		iv_to_top = (ImageView) view.findViewById(R.id.fragment_one_iv_to_top);
		ll_foot_main = (LinearLayout) view.findViewById(R.id.loading_anim_samll_ll_main);
		
		ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_home, null);
	}

	private void initView() {
		rl_category.setOnClickListener(this);
		rl_search.setOnClickListener(this);
		rl_zxing.setOnClickListener(this);
		iv_to_top.setOnClickListener(this);
		
		initListViewHead();
		setAdapter();
	}

	private void initListViewHead() {
		viewPager = (ViewPager) ll_head_main.findViewById(R.id.home_list_head_viewPager);
		ll_indicator = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_indicator);
		tv_time_hour = (TextView) ll_head_main.findViewById(R.id.home_list_head_tv_time_hour);
		tv_time_minute = (TextView) ll_head_main.findViewById(R.id.home_list_head_tv_time_minute);
		tv_time_second = (TextView) ll_head_main.findViewById(R.id.home_list_head_tv_time_second);
		mListView.addHeaderView(ll_head_main);
		initViewPager();
		mcdt = new MyCountDownTimer(mContext, null, tv_time_hour, tv_time_minute, tv_time_second,
				36000000, 1000, new MyCountDownTimer.MyTimerCallback() {
			@Override
			public void onFinish() {
				getSVDatas();
			}
		});
		mcdt.start(); //开始倒计时
	}

	private void initViewPager() {
		viewLists.clear();
		urlLists.clear();
		imgEns.clear();
		//imgEns.addAll(mainEn.getImgLists());
		ProductDetailEntity mainEn = null;
		mainEn = new ProductDetailEntity();
		mainEn.setImgMinUrl("http://i01.pictn.sogoucdn.com/b0526a093f6f0f98");
		imgEns.add(mainEn);
		mainEn = new ProductDetailEntity();
		mainEn.setImgMinUrl("http://i03.pictn.sogoucdn.com/01cd128aa8c2eacc");
		imgEns.add(mainEn);
		mainEn = new ProductDetailEntity();
		mainEn.setImgMinUrl("http://i03.pictn.sogoucdn.com/45c88689cd284ab6");
		imgEns.add(mainEn);
		mainEn = new ProductDetailEntity();
		mainEn.setImgMinUrl("http://i02.pictn.sogoucdn.com/8604487f2228134f");
		imgEns.add(mainEn);
		mainEn = new ProductDetailEntity();
		mainEn.setImgMinUrl("http://i02.pictn.sogoucdn.com/7580ff5e191c8086");
		imgEns.add(mainEn);
		mainEn = new ProductDetailEntity();
		mainEn.setImgMinUrl("http://i03.pictn.sogoucdn.com/43f80c7a025f4927");
		imgEns.add(mainEn);
		
		indicators = new ImageView[imgEns.size()]; // 定义指示器数组大小
		for (int i = 0; i < imgEns.size(); i++) {
			String imgMaxUrl = IMAGE_URL_HTTP + imgEns.get(i).getImgMaxUrl();
			urlLists.add(imgMaxUrl);
			
			//String imgMinUrl = IMAGE_URL_HTTP + imgEns.get(i).getImgMinUrl();
			String imgMinUrl = imgEns.get(i).getImgMinUrl();
			ImageView imageView = new ImageView(mContext);
			imageView.setScaleType(ScaleType.FIT_XY);
			ImageLoader.getInstance().displayImage(imgMinUrl, imageView, options);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
			viewLists.add(imageView);
			// 循环加入指示器
		    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
		    params.setMargins(10, 0, 10, 0);
			indicators[i] = new ImageView(mContext);
			indicators[i].setLayoutParams(params);
			indicators[i].setImageResource(R.drawable.indicators_default);
			if (i == 0) {
				indicators[i].setImageResource(R.drawable.indicators_now);
			}
			ll_indicator.addView(indicators[i]);
		}
		final boolean loop = viewLists.size() > 3 ? true:false;
		viewPager.setAdapter(new PagerAdapter()
		{
			// 创建
			@Override
			public Object instantiateItem(View container, int position)
			{
				View layout = null;
				if (loop) {
					layout = viewLists.get(position % viewLists.size());
				}else {
					layout = viewLists.get(position);
				}
				viewPager.addView(layout);
				return layout;
			}
			
			// 销毁
			@Override
			public void destroyItem(View container, int position, Object object)
			{
				View layout = null;
				if (loop) {
					layout = viewLists.get(position % viewLists.size());
				}else {
					layout = viewLists.get(position);
				}
				viewPager.removeView(layout);
			}
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1)
			{
				return arg0 == arg1;
				
			}
			
			@Override
			public int getCount()
			{
				if (loop) {
					return Integer.MAX_VALUE;
				}else {
					return viewLists.size();
				}
			}
			
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){
			
			@Override
			public void onPageSelected(final int arg0){
				if (loop) {
					position = arg0;
					mCurrentItem = arg0 % viewLists.size();
					if (mCurrentItem == viewLists.size()) {
						mCurrentItem = 0;
						viewPager.setCurrentItem(mCurrentItem);
					}
				}else {
					mCurrentItem = arg0;
				}
				// 更改指示器图片
				for (int i = 0; i < viewLists.size(); i++) {
					ImageView imageView = (ImageView) ll_indicator.getChildAt(i);
					if (i == mCurrentItem)
						imageView.setImageResource(R.drawable.indicators_now);
					else
						imageView.setImageResource(R.drawable.indicators_default);
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2){
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0){
				if (arg0 == 1) {
					rotation = false;
				}
			}
		});
		if (loop) {
			mPagerAction = new Runnable(){
				
				@Override
				public void run(){
					if (rotation) {
						position++;
						viewPager.setCurrentItem(position);
					}
					rotation = true;
					viewPager.postDelayed(mPagerAction, 3000);
				}
			};
			viewPager.postDelayed(mPagerAction, 3000);
		}
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
		mListView.setOnScrollListener(new MyScrollListener());
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		current_Page = 1;
		lv_lists_all_1.clear();
		hm_all.clear();
		ll_foot_main.setVisibility(View.GONE);
		startAnimation();
		requestListDatas();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		ll_foot_main.setVisibility(View.VISIBLE);
		requestListDatas();
	}

	private void requestListDatas() {
		atm.request(AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE, instance);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.fragment_one_topbar_rl_category:
			//intent = new Intent(getActivity(), CategoryActivity.class);
			intent = new Intent(mContext, ProductListActivity.class);
			intent.putExtra("typeId", "0");
			intent.putExtra("typeName", getString(R.string.product_search));
			break;
		case R.id.fragment_one_topbar_rl_search:
			intent = new Intent(mContext, ProductListActivity.class);
			intent.putExtra("typeId", "0");
			intent.putExtra("typeName", getString(R.string.product_search));
			break;
		case R.id.fragment_one_topbar_rl_zxing:
			intent = new Intent(mContext, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		case R.id.fragment_one_iv_to_top: //回顶
			iv_to_top.setVisibility(View.GONE);
			toListViewTop();
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		StatService.onResume(getActivity());
		if (isUpdate) {
			isUpdate = false;
			getSVDatas();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		// 取消倒计时
		if (mcdt != null) {
			mcdt.cancel();
		}
		if(myBroadcastReceiver != null){
			mContext.unregisterReceiver(myBroadcastReceiver); 
        }
	}

	class MyScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (loadMore) {
					loadSVDatas();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem > 5) {
				iv_to_top.setVisibility(View.VISIBLE);
			} else {
				iv_to_top.setVisibility(View.GONE);
			}
			if (firstVisibleItem + visibleItemCount == totalItemCount) {
				loadMore = true;
			}else {
				loadMore = false;
			}
		}
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception{
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
			mainEn = null;
			mainEn = sc.getProductListDatas(0, 0, 1, Page_Count, current_Page, "", "", 0);
			return mainEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
			if (mainEn != null) {
				List<ProductListEntity> lists = mainEn.getMainLists();
				if (lists != null && lists.size() > 0) {
					addEntity(lv_lists_all_1, lists, hm_all);
					current_Page++;
					myUpdateAdapter();
				}else {
					loadFailHandle();
				}
			}else {
				loadFailHandle();
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		CommonTools.showToast(mContext, String.valueOf(result), 1000);
		loadFailHandle();
	}

	private void loadFailHandle() {
		addAllShow(lv_lists_all_1);
		myUpdateAdapter();
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
		if (current_Page == 1) {
			toListViewTop();
		}
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

	/**
	 * 滚动到ListView顶部
	 */
	private void toListViewTop() {
		setAdapter();
	}
	
	/**
	 * 显示缓冲动画
	 */
	private void startAnimation() {
		LoadDialog.show(mContext);
	}
	
	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		LoadDialog.hidden(mContext);
		ll_foot_main.setVisibility(View.GONE);
	}

	// 广播接收器
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isAvailable()) {
					// 网络连接
					if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					//WiFi网络
					} else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
					//有线网络
					} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					//3g网络
					}
				} else {
					//网络断开
				}
			}else if (action.equals(AppConfig.RECEIVER_ACTION_MAIN_DATA)) {
				int status = intent.getExtras().getInt(AppConfig.RECEIVER_ACTION_MAIN_MSG_KEY, 1);
				switch (status) {
				case 1:
					
					break;

				default:
					break;
				}
			}

		}
	};

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

