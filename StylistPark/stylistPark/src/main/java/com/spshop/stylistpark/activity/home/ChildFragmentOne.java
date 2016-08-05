package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseBooleanArray;
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
import com.spshop.stylistpark.activity.common.MyWebViewActivity;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductList2ItemAdapter;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.MyCountDownTimer;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("UseSparseArrays")
public class ChildFragmentOne extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentOne";
	public static ChildFragmentOne instance = null;
	public boolean isUpdate = true;

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	private static final int Page_Count = 40;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private String currStr;
	private Context mContext;
	private NetworkInfo netInfo;
	private MyCountDownTimer mcdt;
	private ConnectivityManager cm;
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();
	private RelativeLayout rl_category, rl_search, rl_zxing;
	private LinearLayout ll_head_main, ll_indicator, ll_goods_main, ll_peida_main, ll_sale_main;

	private View sv_goods_main, vw_goods_title, sv_peida_main, vw_peida_title, vw_sale_title;
	private TextView tv_goods_title, tv_peida_title, tv_sale_title, tv_load_again;
	private RelativeLayout rl_loading, rl_load_fail;
	private ViewPager viewPager;
	private ImageView iv_to_top;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ProductList2ItemAdapter lv_two_adapter;
	private Runnable mPagerAction;
	private LayoutInflater mInflater;
	private DisplayImageOptions options;

	private ThemeEntity themeEn;
	private ProductListEntity mainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all = new ArrayList<ProductListEntity>();
	private SparseBooleanArray sa_all = new SparseBooleanArray();
	private boolean vprStop = false;
	private int idsSize, idsPosition, vprPosition;
	private ImageView[] indicators = null;
	private ArrayList<ImageView> viewLists = new ArrayList<ImageView>();
	private ArrayList<ThemeEntity> imgEns = new ArrayList<ThemeEntity>();

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
		currStr = LangCurrTools.getCurrencyValue();
		atm = AsyncTaskManager.getInstance(mContext);
		mInflater = LayoutInflater.from(mContext);
		options = AppApplication.getDefaultImageOptions();

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
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		rl_category = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_category);
		rl_search = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_search);
		rl_zxing = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_zxing);
		refresh_lv = (PullToRefreshListView) view.findViewById(R.id.fragment_one_listview);
		rl_loading = (RelativeLayout) view.findViewById(R.id.loading_anim_large_ll_main);
		iv_to_top = (ImageView) view.findViewById(R.id.fragment_one_iv_to_top);
		rl_load_fail = (RelativeLayout) view.findViewById(R.id.loading_fail_rl_main);
		tv_load_again = (TextView) view.findViewById(R.id.loading_fail_tv_update);

		ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_home, null);
	}

	private void initView() {
		rl_loading.setVisibility(View.GONE);
		rl_category.setOnClickListener(this);
		rl_search.setOnClickListener(this);
		rl_zxing.setOnClickListener(this);
		iv_to_top.setOnClickListener(this);
		tv_load_again.setOnClickListener(this);

		initListView();
		initListViewHead();
		setAdapter();
	}

	private void initListView() {
		refresh_lv.setPullRefreshEnabled(false);
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnScrollListener(new OnMyScrollListener());
		refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						refresh_lv.onPullDownRefreshComplete();
					}
				}, 1000);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 加载更多
				if (lv_show.size() > 0) {
					loadSVDatas();
				} else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							if (NetworkUtil.isNetworkAvailable()) {
								requestListDatas();
							} else {
								refresh_lv.onPullUpRefreshComplete();
							}
						}
					}, 1000);
				}
			}
		});
		mListView = refresh_lv.getRefreshableView();
		mListView.setDivider(null);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void initListViewHead() {
		viewPager = (ViewPager) ll_head_main.findViewById(R.id.home_list_head_viewPager);
		ll_indicator = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_indicator);
		sv_goods_main = ll_head_main.findViewById(R.id.home_list_head_sv_goods_main);
		vw_goods_title = ll_head_main.findViewById(R.id.home_list_head_ll_goods_title);
		tv_goods_title = (TextView) vw_goods_title.findViewById(R.id.text_two_line_tv_title);
		ll_goods_main = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_ll_goods_main);
		sv_peida_main = ll_head_main.findViewById(R.id.home_list_head_sv_peida_main);
		vw_peida_title = ll_head_main.findViewById(R.id.home_list_head_ll_peida_title);
		tv_peida_title = (TextView) vw_peida_title.findViewById(R.id.text_two_line_tv_title);
		ll_peida_main = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_ll_peida_main);
		vw_sale_title = ll_head_main.findViewById(R.id.home_list_head_ll_sale_title);
		tv_sale_title = (TextView) vw_sale_title.findViewById(R.id.text_two_line_tv_title);
		ll_sale_main = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_ll_sale_main);
	}

	private void setHeadView() {
		if (themeEn != null) {
			mListView.addHeaderView(ll_head_main);
			if (viewLists.size() == 0) {
				initViewPager(themeEn.getAdEn());
			}
			initGoodsView(themeEn.getGoodsEn());
			initPeidaView(themeEn.getPeidaEn());
			initSaleView(themeEn.getSaleEn());
		}
	}

	private void initViewPager(ThemeEntity adEn) {
		if (viewPager == null) return;
		if (adEn != null && adEn.getMainLists() != null) {
			viewLists.clear();
			imgEns.clear();
			imgEns.addAll(adEn.getMainLists());
			idsSize = imgEns.size();
			indicators = new ImageView[idsSize]; // 定义指示器数组大小
			if (idsSize == 2 || idsSize == 3) {
				imgEns.addAll(adEn.getMainLists());
			}
			for (int i = 0; i < imgEns.size(); i++) {
				final ThemeEntity items = imgEns.get(i);
				String imgUrl = IMAGE_URL_HTTP + items.getImgUrl();
				ImageView imageView = new ImageView(mContext);
				imageView.setScaleType(ScaleType.FIT_XY);
				ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
						intent.putExtra("title", items.getTitle());
						intent.putExtra("lodUrl", AppConfig.URL_COMMON_TOPIC_URL + "?topic_id=" + items.getId());
						intent.putExtra("vdoUrl", "");
						startActivity(intent);
					}
				});
				viewLists.add(imageView);
				if (i < idsSize) {
					// 循环加入指示器
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					params.setMargins(8, 0, 8, 0);
					indicators[i] = new ImageView(mContext);
					indicators[i].setLayoutParams(params);
					indicators[i].setImageResource(R.drawable.indicators_default);
					if (i == 0) {
						indicators[i].setImageResource(R.drawable.indicators_now);
					}
					ll_indicator.addView(indicators[i]);
				}
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
						vprPosition = arg0;
						idsPosition = arg0 % viewLists.size();
						if (idsPosition == viewLists.size()) {
							idsPosition = 0;
							viewPager.setCurrentItem(0);
						}
					}else {
						idsPosition = arg0;
					}
					// 更改指示器图片
					if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
						idsPosition = idsPosition - idsSize;
					}
					for (int i = 0; i < idsSize; i++) {
						ImageView imageView = indicators[i];
						if (i == idsPosition)
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
						vprStop = true;
					}
				}
			});
			if (loop) {
				mPagerAction = new Runnable(){

					@Override
					public void run(){
						if (!vprStop) {
							vprPosition++;
							if (viewPager != null) {
								viewPager.setCurrentItem(vprPosition);
							}
						}
						vprStop = false;
						if (viewPager != null) {
							viewPager.postDelayed(mPagerAction, 3000);
						}
					}
				};
				if (viewPager != null) {
					viewPager.postDelayed(mPagerAction, 3000);
				}
			}
		}
	}

	private void initGoodsView(ProductListEntity goodsEn) {
		if (goodsEn != null && goodsEn.getMainLists() != null) {
			vw_goods_title.setVisibility(View.VISIBLE);
			tv_goods_title.setText(R.string.home_txt_hot_goods);
			sv_goods_main.setVisibility(View.VISIBLE);
			List<ProductListEntity> datas = goodsEn.getMainLists();
			ll_goods_main.removeAllViews();
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(10, 0, 0, 0);
			lp.width = (AppApplication.screenWidth - 40) / 3;
			for (int i = 0; i < datas.size(); i++) {
				final ProductListEntity items = datas.get(i);
				if (items != null) {
					View view = mInflater.inflate(R.layout.item_line_goods, ll_goods_main, false);
					ImageView imgView = (ImageView) view.findViewById(R.id.home_line_goods_item_iv_img);
					String imgUrl = IMAGE_URL_HTTP + items.getImageUrl();
					ImageLoader.getInstance().displayImage(imgUrl, imgView, options);
					TextView tv_name = (TextView) view.findViewById(R.id.home_line_goods_item_tv_name);
					tv_name.setText(items.getName());

					TextView item_curr = (TextView) view.findViewById(R.id.home_line_goods_item_tv_curr);
					TextView item_sell_price = (TextView) view.findViewById(R.id.home_line_goods_item_tv_sell_price);
					TextView item_full_price = (TextView) view.findViewById(R.id.home_line_goods_item_tv_full_price);
					TextView item_discount = (TextView) view.findViewById(R.id.home_line_goods_item_tv_discount);
					item_curr.setText(currStr);
					item_sell_price.setText(items.getSellPrice()); //商品卖价
					String full_price = items.getFullPrice(); //商品原价
					if (StringUtil.isNull(full_price) || full_price.equals("0") || full_price.equals("0.00")) {
						item_full_price.getPaint().setFlags(0);
						item_full_price.setVisibility(View.GONE);
						item_discount.setVisibility(View.GONE);
					} else {
						item_full_price.setText(full_price);
						item_full_price.setVisibility(View.VISIBLE);
						item_full_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
						if (!StringUtil.isNull(items.getDiscount())) {
							item_discount.setVisibility(View.VISIBLE);
							item_discount.setText(items.getDiscount());
						} else {
							item_discount.setVisibility(View.GONE);
						}
					}

					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, ProductDetailActivity.class);
							intent.putExtra("goodsId", items.getId());
							startActivity(intent);
						}
					});
					ll_goods_main.addView(view, lp);
				}
			}
		} else {
			vw_goods_title.setVisibility(View.GONE);
			sv_goods_main.setVisibility(View.GONE);
		}
	}

	private void initPeidaView(ThemeEntity peidaEn) {
		if (peidaEn != null && peidaEn.getMainLists() != null) {
			vw_peida_title.setVisibility(View.VISIBLE);
			tv_peida_title.setText(R.string.home_txt_today_topics);
			sv_peida_main.setVisibility(View.VISIBLE);
			List<ThemeEntity> datas = peidaEn.getMainLists();
			ll_peida_main.removeAllViews();
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(10, 0, 0, 0);
			lp.width = (AppApplication.screenWidth - 40) / 3;
			lp.height = (AppApplication.screenWidth - 40) / 3;
			for (int i = 0; i < datas.size(); i++) {
				final ThemeEntity items = datas.get(i);
				if (items != null) {
					ImageView imgView = new ImageView(mContext);
					String imgUrl = IMAGE_URL_HTTP + items.getImgUrl();
					ImageLoader.getInstance().displayImage(imgUrl, imgView, options);
					imgView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
							intent.putExtra("title", items.getTitle());
							intent.putExtra("lodUrl", AppConfig.URL_COMMON_ARTICLE_URL + "?id=" + items.getId());
							intent.putExtra("vdoUrl", "");
							startActivity(intent);
						}
					});
					ll_peida_main.addView(imgView, lp);
				}
			}
		} else {
			vw_peida_title.setVisibility(View.GONE);
			sv_peida_main.setVisibility(View.GONE);
		}
	}

	private void initSaleView(ThemeEntity saleEn) {
		if (saleEn != null && saleEn.getMainLists() != null) {
			vw_sale_title.setVisibility(View.VISIBLE);
			tv_sale_title.setText(R.string.home_txt_limit_events);
			List<ThemeEntity> datas = saleEn.getMainLists();
			ll_sale_main.removeAllViews();
			for (int i = 0; i < datas.size(); i++) {
				final ThemeEntity items = datas.get(i);
				if (items != null) {
					View view = mInflater.inflate(R.layout.item_line_sales, ll_sale_main, false);
					ImageView imgView = (ImageView) view.findViewById(R.id.home_line_sales_item_iv_logo);
					String imgUrl = IMAGE_URL_HTTP + items.getImgUrl();
					ImageLoader.getInstance().displayImage(imgUrl, imgView, options);
					TextView tv_name = (TextView) view.findViewById(R.id.home_line_sales_item_tv_name);
					tv_name.setText(items.getTitle());
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = null;
							switch (items.getType()) {
								case 1: //分类
									intent = new Intent(mContext, ProductListActivity.class);
									intent.putExtra("typeId", items.getId());
									break;
								case 2: //品牌
									intent = new Intent(mContext, ShowListHeadActivity.class);
									intent.putExtra("pageCode", ShowListHeadActivity.PAGE_ROOT_CODE_1);
									intent.putExtra("brandId", items.getId());
									break;
								default:
									break;
							}
							if (intent != null) {
								startActivity(intent);
							}
						}
					});
					ll_sale_main.addView(view);
				}
			}
		} else {
			vw_sale_title.setVisibility(View.GONE);
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
		lv_two_adapter = new ProductList2ItemAdapter(mContext, lv_show_two, lv_callback);
		mListView.setAdapter(lv_two_adapter);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		current_Page = 1;
		lv_all.clear();
		sa_all.clear();
		startAnimation();
		requestHeadDatas();
	}

	/**
	 * 从本地数据库加载数据
	 */
	private void getDBDatas() {
		AppApplication.loadDBData = true;
		atm.request(AppConfig.REQUEST_DB_GET_HOME_SHOW_HEAD_CODE, instance);
	}

	private void requestHeadDatas() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				atm.request(AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE, instance);
			}
		}, 1000);
	}

	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
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
			toTop();
			break;
		case R.id.loading_fail_tv_update: //重加载
			getSVDatas();
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
		AppApplication.onPageStart(getActivity(), TAG);
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
		AppApplication.onPageEnd(getActivity(), TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		// 取消倒计时
		if (mcdt != null) {
			mcdt.cancel();
		}
		// 遍历创建的View，销毁以释放内存
		for (int i = 0; i < viewLists.size(); i++) {
			ImageView iv = viewLists.get(i);
			if (iv != null) {
				iv.setImageBitmap(null);
				iv = null;
			}
		}
		if (viewPager != null) {
			viewPager.removeAllViews();
			viewPager = null;
		}
		if(myBroadcastReceiver != null){
			mContext.unregisterReceiver(myBroadcastReceiver); 
        }
		instance = null;
	}

	class OnMyScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem > 5) {
				iv_to_top.setVisibility(View.VISIBLE);
			} else {
				iv_to_top.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception{
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE:
			themeEn = null;
			themeEn = sc.getHomeHeadDatas();
			if (themeEn != null) {
				FileManager.writeFileSaveObject(AppConfig.homeAdsFileName, themeEn, true);
			}
			return themeEn;
		case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
			mainEn = null;
			mainEn = sc.getProductListDatas(0, 0, 1, Page_Count, current_Page, "", "", 0);
			return mainEn;
		case AppConfig.REQUEST_DB_GET_HOME_SHOW_HEAD_CODE:
			Object obj = FileManager.readFileSaveObject(AppConfig.homeAdsFileName, true);
			if (obj != null) {
				themeEn = (ThemeEntity) obj;
			}
			return themeEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) {
			stopAnimation();
			return;
		}
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE:
			setHeadView();
			if (themeEn != null) {
				requestListDatas();
			} else {
				getDBDatas(); //加载远程数据失败则获取本地数据
			}
			break;
		case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
			if (mainEn != null) {
				List<ProductListEntity> lists = mainEn.getMainLists();
				if (lists != null && lists.size() > 0) {
					rl_load_fail.setVisibility(View.GONE);
					lv_all.addAll(lists);
					addAllShow(lv_all);
					//addEntity(lv_all, lists, sa_all);
					current_Page++;
					myUpdateAdapter();
				}else {
					loadFailHandle();
				}
			}else {
				loadFailHandle();
			}
			break;
		case AppConfig.REQUEST_DB_GET_HOME_SHOW_HEAD_CODE:
			AppApplication.loadDBData = false;
			setHeadView();
			if (themeEn != null) {
				requestListDatas();
			} else {
				stopAnimation();
				rl_load_fail.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) {
			stopAnimation();
			return;
		}
		CommonTools.showToast(String.valueOf(result), 1000);
		if (themeEn == null) {
			getDBDatas(); //加载远程数据失败则获取本地数据
		} else {
			loadFailHandle();
		}
	}

	private void loadFailHandle() {
		addAllShow(lv_all);
		myUpdateAdapter();
		if (themeEn == null && lv_show.size() == 0) {
			rl_load_fail.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 数据去重函数
	 */
	private void addEntity(List<ProductListEntity> oldDatas, List<ProductListEntity> newDatas, SparseBooleanArray sa_old) {
		ProductListEntity entity = null;
		int dataId = 0;
		for (int i = 0; i < newDatas.size(); i++) {
			entity = newDatas.get(i);
			if (entity != null) {
				dataId = entity.getId();
				if (dataId != 0 && sa_old.indexOfKey(dataId) < 0) {
					oldDatas.add(entity);
					sa_old.put(dataId, true);
				}
			}
		}
		addAllShow(oldDatas);
	}

	private void addAllShow(List<ProductListEntity> showLists) {
		lv_show.clear();
		lv_show.addAll(showLists);
	}
	
	private void myUpdateAdapter() {
		if (current_Page == 1) {
			toTop();
		}
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

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		setAdapter();
		iv_to_top.setVisibility(View.GONE);
	}
	
	/**
	 * 显示缓冲动画
	 */
	private void startAnimation() {
		rl_load_fail.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		rl_loading.setVisibility(View.GONE);
		refresh_lv.onPullUpRefreshComplete();
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

