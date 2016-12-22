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
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.common.MipcaActivityCapture;
import com.spshop.stylistpark.activity.common.MyWebViewActivity;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductList2ItemAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.MyCountDownTimer;
import com.spshop.stylistpark.utils.OptionsManager;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import static com.spshop.stylistpark.AppApplication.screenWidth;
import static com.spshop.stylistpark.R.id.home_list_head_iv_window_1;
import static com.spshop.stylistpark.R.id.home_list_head_iv_window_2;
import static com.spshop.stylistpark.R.id.home_list_head_iv_window_3;

@SuppressLint("UseSparseArrays")
public class ChildFragmentOne extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentOne";
	public static ChildFragmentOne instance = null;
	private static final int GOODS_WIDTH = (screenWidth - 80) / 4;

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	private int dataTotal = 0; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private boolean isUpdate = false;
	private String currStr;
	private Context mContext;
	private NetworkInfo netInfo;
	private MyCountDownTimer mcdt;
	private ConnectivityManager cm;
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();
	private RelativeLayout rl_zxing, rl_search, rl_right;

	private LinearLayout ll_head_main, ll_indicator, ll_window_main, ll_goods_main, ll_peida_main, ll_sale_main;
	private View sv_goods_main, vw_goods_title, sv_peida_main, vw_peida_title, vw_sale_title;
	private TextView tv_goods_title, tv_peida_title, tv_sale_title, tv_load_again;
	private RelativeLayout rl_loading, rl_load_fail;
	private ViewPager viewPager;
	private ImageView iv_window_1, iv_window_2, iv_window_3, iv_to_top;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ProductList2ItemAdapter lv_two_adapter;
	private Runnable mPagerAction;
	private LayoutInflater mInflater;
	private FrameLayout.LayoutParams brandLP;
	private LinearLayout.LayoutParams indicatorsLP, windowLP_1, windowLP_2, goodsItemLP;
	private RelativeLayout.LayoutParams goodsImgLP;

	private DisplayImageOptions defaultOptions;
	private ThemeEntity themeEn, windowEn_1, windowEn_2, windowEn_3;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all = new ArrayList<ProductListEntity>();
	private SparseBooleanArray sa_all = new SparseBooleanArray();
	private boolean vprStop = false;
	private boolean addHead = false;
	private int newWindowWD;
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
		themeEn = AppApplication.themeEn;
		defaultOptions = OptionsManager.getInstance().getDefaultOptions();

		// 动态调整宽高
		indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		indicatorsLP.setMargins(8, 0, 8, 0);

		brandLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		brandLP.width = screenWidth;
		brandLP.height = screenWidth / 2;

		int windowMg = getResources().getDimensionPixelSize(R.dimen.home_window_margin) * 2;
		newWindowWD = screenWidth / 2 - windowMg;
		windowLP_1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		windowLP_1.width = newWindowWD;
		windowLP_1.height = newWindowWD * (346 + windowMg) / 320;
		windowLP_2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		windowLP_2.width = newWindowWD;
		windowLP_2.height = newWindowWD * 173 / 320;

		goodsItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		goodsItemLP.setMargins(10, 0, 10, 0);
		goodsItemLP.width = GOODS_WIDTH;

		goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		goodsImgLP.width = GOODS_WIDTH;
		goodsImgLP.height = GOODS_WIDTH * 37 / 29;

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
		rl_right = (RelativeLayout) view.findViewById(R.id.fragment_one_topbar_rl_right);
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
		rl_right.setOnClickListener(this);
		rl_search.setOnClickListener(this);
		rl_zxing.setOnClickListener(this);
		iv_to_top.setOnClickListener(this);
		tv_load_again.setOnClickListener(this);

		initListView();
		initListViewHead();
		setAdapter();
		if (themeEn != null) {
			setHeadView();
			loadSVDatas();
		} else {
			getSVDatas();
		}
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
				}, AppConfig.LOADING_TIME);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 加载更多
				if (!BaseActivity.isStopLoadMore(lv_show.size(), dataTotal, 0)) {
					loadSVDatas();
				} else {
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
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void initListViewHead() {
		viewPager = (ViewPager) ll_head_main.findViewById(R.id.home_list_head_viewPager);
		ll_indicator = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_indicator);
		ll_window_main = (LinearLayout) ll_head_main.findViewById(R.id.home_list_head_ll_window_main);
		iv_window_1 = (ImageView) ll_head_main.findViewById(home_list_head_iv_window_1);
		iv_window_2 = (ImageView) ll_head_main.findViewById(home_list_head_iv_window_2);
		iv_window_3 = (ImageView) ll_head_main.findViewById(home_list_head_iv_window_3);
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
			if (!addHead) {
				mListView.addHeaderView(ll_head_main);
				addHead = true;
			}
			if (viewLists.size() == 0) {
				initViewPager(themeEn.getAdEn());
			}
			initWindows(themeEn.getWindowEn());
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
				ImageLoader.getInstance().displayImage(imgUrl, imageView, defaultOptions);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 创建分享数据
						ShareEntity shareEn = new ShareEntity();
						shareEn.setTitle(items.getTitle());
						shareEn.setText(items.getTitle());
						shareEn.setUrl(AppConfig.URL_COMMON_TOPIC_URL + "?topic_id=" + items.getId());
						shareEn.setImageUrl(items.getImgUrl());
						// 跳转至WebView
						Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
						intent.putExtra("shareEn", shareEn);
						intent.putExtra("goodsId", items.getId());
						intent.putExtra("title", items.getTitle());
						intent.putExtra("lodUrl", AppConfig.URL_COMMON_TOPIC_URL + "?topic_id=" + items.getId());
						startActivity(intent);
					}
				});
				viewLists.add(imageView);
				if (i < idsSize) {
					// 循环加入指示器
					indicators[i] = new ImageView(mContext);
					indicators[i].setLayoutParams(indicatorsLP);
					indicators[i].setImageResource(R.drawable.indicators_default);
					if (i == 0) {
						indicators[i].setImageResource(R.drawable.indicators_now);
					}
					ll_indicator.addView(indicators[i]);
				}
			}
			final boolean loop = viewLists.size() > 3 ? true:false;
			viewPager.setLayoutParams(brandLP);
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
				viewPager.setCurrentItem(viewLists.size() * 10);
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

	private void initWindows(ThemeEntity windowEn) {
		if (windowEn != null && windowEn.getMainLists() != null) {
			List<ThemeEntity> datas = windowEn.getMainLists();
			ll_window_main.setVisibility(View.VISIBLE);
			iv_window_1.setLayoutParams(windowLP_1);
			iv_window_2.setLayoutParams(windowLP_2);
			iv_window_3.setLayoutParams(windowLP_2);
			iv_window_1.setOnClickListener(this);
			iv_window_2.setOnClickListener(this);
			iv_window_3.setOnClickListener(this);

			for (int i = 0; i < datas.size(); i++) {
				ThemeEntity items = datas.get(i);
				if (i == 0) {
					windowEn_1 = items;
					ImageLoader.getInstance().displayImage(windowEn_1.getImgUrl(), iv_window_1, defaultOptions);
				} else if (i == 1) {
					windowEn_2 = items;
					ImageLoader.getInstance().displayImage(windowEn_2.getImgUrl(), iv_window_2, defaultOptions);
				} else if (i == 2) {
					windowEn_3 = items;
					ImageLoader.getInstance().displayImage(windowEn_3.getImgUrl(), iv_window_3, defaultOptions);
				}
			}
		} else {
			ll_window_main.setVisibility(View.GONE);
		}
	}

	private void initGoodsView(ProductListEntity goodsEn) {
		if (goodsEn != null && goodsEn.getMainLists() != null) {
			vw_goods_title.setVisibility(View.VISIBLE);
			tv_goods_title.setText(R.string.home_txt_hot_goods);
			sv_goods_main.setVisibility(View.VISIBLE);
			List<ProductListEntity> datas = goodsEn.getMainLists();
			ll_goods_main.removeAllViews();

			for (int i = 0; i < datas.size(); i++) {
				final ProductListEntity items = datas.get(i);
				if (items != null) {
					View view = mInflater.inflate(R.layout.item_line_goods, ll_goods_main, false);
					ImageView imgView = (ImageView) view.findViewById(R.id.home_line_goods_item_iv_img);
					imgView.setLayoutParams(goodsImgLP);
					String imgUrl = IMAGE_URL_HTTP + items.getImageUrl();
					ImageLoader.getInstance().displayImage(imgUrl, imgView, defaultOptions);
					TextView tv_name = (TextView) view.findViewById(R.id.home_line_goods_item_tv_name);
					tv_name.setText(items.getName());

					TextView item_curr = (TextView) view.findViewById(R.id.home_line_goods_item_tv_curr);
					TextView item_sell_price = (TextView) view.findViewById(R.id.home_line_goods_item_tv_sell_price);
					TextView item_full_price = (TextView) view.findViewById(R.id.home_line_goods_item_tv_full_price);
					TextView item_discount = (TextView) view.findViewById(R.id.home_line_goods_item_tv_discount);
					item_curr.setText(currStr);

					String sell_price = items.getSellPrice(); //商品卖价
					String full_price = items.getFullPrice(); //商品原价
					if (StringUtil.priceIsNull(full_price) || StringUtil.priceIsNull(sell_price)) {
						if (!StringUtil.priceIsNull(sell_price)) {
							item_sell_price.setText(sell_price);
						} else {
							item_sell_price.setText(full_price);
						}
						item_full_price.getPaint().setFlags(0);
						item_full_price.setVisibility(View.GONE);
						item_discount.setVisibility(View.GONE);
					} else {
						item_sell_price.setText(sell_price);
						item_full_price.setText(currStr + full_price);
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
							HomeFragmentActivity.instance.openProductDetailActivity(items.getId());
						}
					});
					ll_goods_main.addView(view, goodsItemLP);
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
			for (int i = 0; i < datas.size(); i++) {
				final ThemeEntity items = datas.get(i);
				if (items != null) {
					ImageView imgView = new ImageView(mContext);
					String imgUrl = IMAGE_URL_HTTP + items.getImgUrl();
					ImageLoader.getInstance().displayImage(imgUrl, imgView, defaultOptions);
					imgView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
							intent.putExtra("title", items.getTitle());
							intent.putExtra("lodUrl", AppConfig.URL_COMMON_ARTICLE_URL + "?id=" + items.getId());
							startActivity(intent);
						}
					});
					ll_peida_main.addView(imgView, goodsItemLP);
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
					imgView.setLayoutParams(brandLP);
					String imgUrl = IMAGE_URL_HTTP + items.getImgUrl();
					ImageLoader.getInstance().displayImage(imgUrl, imgView, defaultOptions);
					FrameLayout fl_intro = (FrameLayout) view.findViewById(R.id.home_line_sales_item_fl_main);
					fl_intro.setLayoutParams(brandLP);
					TextView tv_name = (TextView) view.findViewById(R.id.home_line_sales_item_tv_name);
					tv_name.setText(items.getTitle());
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startWindowActivity(items);
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
				if (entity == null) return;
				HomeFragmentActivity.instance.openProductDetailActivity(((ProductListEntity) entity).getId());
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

	private void requestHeadDatas() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				atm.request(AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE, instance);
			}
		}, AppConfig.LOADING_TIME);
	}

	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		atm.request(AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE, instance);
	}

	/**
	 * 从本地数据库加载数据
	 */
	private void getDBDatas() {
		AppApplication.loadDBData = true;
		atm.request(AppConfig.REQUEST_DB_GET_HOME_SHOW_HEAD_CODE, instance);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.fragment_one_topbar_rl_right:
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
		case R.id.home_list_head_iv_window_1:
			startWindowActivity(windowEn_1);
			break;
		case R.id.home_list_head_iv_window_2:
			startWindowActivity(windowEn_2);
			break;
		case R.id.home_list_head_iv_window_3:
			startWindowActivity(windowEn_3);
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

	private void startWindowActivity(ThemeEntity windowEn) {
		if (windowEn != null) {
			Intent intent = null;
			switch (windowEn.getType()) {
				case 1: //分类
					intent = new Intent(mContext, ProductListActivity.class);
					intent.putExtra("typeId", windowEn.getId());
					break;
				case 2: //品牌
					intent = new Intent(mContext, ShowListHeadActivity.class);
					intent.putExtra("pageCode", ShowListHeadActivity.PAGE_ROOT_CODE_1);
					intent.putExtra("brandId", windowEn.getId());
					break;
			}
			if (intent != null) {
				startActivity(intent);
			}
		}
	}

	public void updateData() {
		isUpdate = true;
	}

	@Override
	public void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		if (isUpdate) {
			isUpdate = false;
			getSVDatas();
		}
		super.onResume();
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
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE:
				params.add(new MyNameValuePair("app", "home"));
				BaseEntity baseEn = sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE, uri, params, HttpUtil.METHOD_GET);
				if (baseEn != null) {
					themeEn = (ThemeEntity) baseEn;
					FileManager.writeFileSaveObject(AppConfig.homeAdsFileName, themeEn, true);
				}
				return baseEn;

			case AppConfig.REQUEST_DB_GET_HOME_SHOW_HEAD_CODE:
				Object obj = FileManager.readFileSaveObject(AppConfig.homeAdsFileName, true);
				if (obj != null) {
					themeEn = (ThemeEntity) obj;
				}
				return obj;

			case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
				uri = AppConfig.URL_COMMON_PRODUCT_URL;
				params.add(new MyNameValuePair("app", "category"));
				params.add(new MyNameValuePair("cat_id", "0"));
				params.add(new MyNameValuePair("brand", "0"));
				params.add(new MyNameValuePair("order", "0"));
				params.add(new MyNameValuePair("page", String.valueOf(current_Page)));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE:
				setHeadView();
				if (themeEn == null) {
					getDBDatas();
				} else {
					stopAnimation();
					loadSVDatas();
				}
				break;
			case AppConfig.REQUEST_DB_GET_HOME_SHOW_HEAD_CODE:
				AppApplication.loadDBData = false;
				setHeadView();
				stopAnimation();
				if (themeEn == null) {
					rl_load_fail.setVisibility(View.VISIBLE);
				} else {
					loadSVDatas();
				}
				break;
			case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
				if (result != null) {
					ProductListEntity mainEn = (ProductListEntity) result;
					dataTotal = mainEn.getDataTotal();
					List<ProductListEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						rl_load_fail.setVisibility(View.GONE);
						lv_all.addAll(lists);
						addAllShow(lv_all);
						current_Page++;
						myUpdateAdapter();
					} else {
						loadFailHandle();
					}
				} else {
					loadFailHandle();
				}
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		if (themeEn == null) {
			getDBDatas();
		} else {
			loadFailHandle();
		}
		CommonTools.showToast(String.valueOf(result), 1000);
	}

	private void loadFailHandle() {
		addAllShow(lv_all);
		myUpdateAdapter();
		if (themeEn == null && lv_show.size() == 0) {
			rl_load_fail.setVisibility(View.VISIBLE);
		}
	}

	private void myUpdateAdapter() {
		lv_show_two.clear();
		ListShowTwoEntity lstEn;
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

	private void addAllShow(List<ProductListEntity> showLists) {
		lv_show.clear();
		lv_show.addAll(showLists);
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

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		setAdapter();
		iv_to_top.setVisibility(View.GONE);
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

