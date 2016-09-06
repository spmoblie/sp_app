package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.cart.CartActivity;
import com.spshop.stylistpark.activity.common.ShowListActivity;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.activity.common.VideoActivity;
import com.spshop.stylistpark.activity.common.ViewPagerActivity;
import com.spshop.stylistpark.adapter.AddCartPopupListAdapter;
import com.spshop.stylistpark.adapter.AddCartPopupListAdapter.AddCartCallback;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.GoodsCartEntity;
import com.spshop.stylistpark.entity.ProductAttrEntity;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.image.AsyncImageLoader.AsyncImageLoaderCallback;
import com.spshop.stylistpark.image.AsyncImageLoader.ImageLoadTask;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.MyCountDownTimer;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.ObservableScrollView;
import com.spshop.stylistpark.widgets.ObservableScrollView.ScrollViewListener;
import com.spshop.stylistpark.widgets.ScrollViewListView;

import java.util.ArrayList;
import java.util.List;

/**
 * "商品详情"Activity
 */
@SuppressLint({ "UseSparseArrays", "NewApi" })
public class ProductDetailActivity extends BaseActivity implements OnDataListener, OnClickListener, ScrollViewListener {

	private static final String TAG = "ProductDetailActivity";
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static ProductDetailActivity instance = null;
	public boolean isUpdate = false;
	@SuppressWarnings("unused")
	private LinearLayout ll_other, ll_bottom, ll_head, ll_promotion, ll_show, ll_bottom_bar, ll_radio_main;
	private RelativeLayout rl_screen, rl_num_minus, rl_num_add;
	private FrameLayout fl_main;
	private ImageView iv_left, iv_goods_img, iv_video, iv_brang_logo, iv_num_minus, iv_num_add, iv_to_top;
	private TextView tv_title, tv_timer, tv_page, tv_name, tv_curr, tv_price_sell, tv_price_full, tv_discount;
	private TextView tv_property_1, tv_property_2, tv_property_3, tv_brand_name, tv_brand_country, tv_brand_go;
	private TextView tv_collection, tv_cart, tv_cart_total, tv_add_cart, tv_call, tv_home;
	private TextView tv_popup_name, tv_popup_curr, tv_popup_price;
	private TextView tv_popup_prompt, tv_popup_select, tv_popup_number, tv_popup_confirm;
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private Button btn_share;
	private PopupWindow popupWindow;
	private View popupView;
	private Animation popupAnimShow, popupAnimGone, numberAddAnim;
	private ObservableScrollView mScrollView;
	private ScrollViewListView svlv;
	private AddCartCallback apCallback;
	private AddCartPopupListAdapter svlvAdapter;
	private ViewPager viewPager;
	private Runnable mPagerAction;
	private WebView webview;
	private ProgressBar progressBar;
	private AsyncImageLoader asyncImageLoader;

	private ProductDetailEntity mainEn;
	private ProductAttrEntity attrEn;
	private GoodsCartEntity cartEn;
	private DisplayImageOptions options;
	private MyCountDownTimer mcdt;
	private boolean isShow = false;
	private boolean isNext = false;
	private boolean isColl = false;
	private boolean vprStop = true;
	private int idsSize, idsPosition, vprPosition;
	private int goodsId = 0;
	private int cartNumTotal = 0;
	private int buyNumber = 1;
	private int skuNum = 1;
	private int propertyNum = 3;
	private int selectId_1, selectId_2, attrNum;
	private double price, mathPrice;
	private String attrNameStr, fristGoodsImgUrl, shareImgUrl, shareImgPath, fristPromotionName;
	private ArrayList<ImageView> viewLists = new ArrayList<ImageView>();
	private ArrayList<String> urlLists = new ArrayList<String>();
	private ArrayList<ProductDetailEntity> imgEns = new ArrayList<ProductDetailEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isInitShare = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);

		AppManager.getInstance().addActivity(this); // 添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		instance = this;
		goodsId = getIntent().getIntExtra("goodsId", 0);
		options = AppApplication.getDefaultImageOptions();

		findViewById();
		initView();
	}

	private void findViewById() {
		fl_main = (FrameLayout) findViewById(R.id.product_detail_fl_main);
		ll_other = (LinearLayout) findViewById(R.id.product_detail_ll_anim_other);
		ll_bottom = (LinearLayout) findViewById(R.id.product_detail_ll_anim_bottom);
		ll_head = (LinearLayout) findViewById(R.id.top_search_ll_main);
		ll_promotion = (LinearLayout) findViewById(R.id.product_detail_ll_promotion);
		ll_bottom_bar = (LinearLayout) findViewById(R.id.product_detail_ll_bottom_bar);
		iv_left = (ImageView) findViewById(R.id.top_commom_iv_left);
		tv_title = (TextView) findViewById(R.id.top_commom_tv_title);
		btn_share = (Button) findViewById(R.id.top_commom_btn_right_one);
		mScrollView = (ObservableScrollView) findViewById(R.id.product_detail_scrollView);
		viewPager = (ViewPager) findViewById(R.id.product_detail_viewPager);
		iv_video = (ImageView) findViewById(R.id.product_detail_iv_video);
		tv_timer = (TextView) findViewById(R.id.product_detail_tv_timer);
		tv_page = (TextView) findViewById(R.id.product_detail_tv_page);
		tv_name = (TextView) findViewById(R.id.product_detail_tv_product_name);
		tv_curr = (TextView) findViewById(R.id.product_detail_tv_curr);
		tv_price_sell = (TextView) findViewById(R.id.product_detail_tv_product_price_sell);
		tv_price_full = (TextView) findViewById(R.id.product_detail_tv_product_price_full);
		tv_discount = (TextView) findViewById(R.id.product_detail_tv_product_discount);
		tv_property_1 = (TextView) findViewById(R.id.product_detail_tv_property_1);
		tv_property_2 = (TextView) findViewById(R.id.product_detail_tv_property_2);
		tv_property_3 = (TextView) findViewById(R.id.product_detail_tv_property_3);
		iv_brang_logo = (ImageView) findViewById(R.id.product_detail_iv_brand_logo);
		tv_brand_name = (TextView) findViewById(R.id.product_detail_tv_brand_name);
		tv_brand_country = (TextView) findViewById(R.id.product_detail_tv_brand_country);
		tv_brand_go = (TextView) findViewById(R.id.product_detail_tv_brand_go);
		iv_to_top = (ImageView) findViewById(R.id.product_detail_iv_to_top);
		ll_radio_main = (LinearLayout) findViewById(R.id.topbar_radio_ll_main);
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		rl_screen = (RelativeLayout) findViewById(R.id.topbar_radio_rl_screen);
		webview = (WebView) findViewById(R.id.product_detail_webView);
		progressBar = (ProgressBar) findViewById(R.id.product_detail_wv_progress);
		tv_collection = (TextView) findViewById(R.id.product_detail_tv_collection);
		tv_cart = (TextView) findViewById(R.id.product_detail_tv_cart);
		tv_cart_total = (TextView) findViewById(R.id.product_detail_tv_cart_total);
		tv_add_cart = (TextView) findViewById(R.id.product_detail_tv_add_cart);
		tv_call = (TextView) findViewById(R.id.product_detail_tv_call);
		tv_home = (TextView) findViewById(R.id.product_detail_tv_home);
	}

	private void initView() {
		setHeadVisibility(View.GONE);
		//tv_title.setText(getString(R.string.title_product_detail));
		btn_share.setBackground(getResources().getDrawable(R.drawable.topbar_icon_share));
		mScrollView.setScrollViewListener(this);
		btn_share.setOnClickListener(this);
		iv_left.setOnClickListener(this);
		iv_video.setOnClickListener(this);
		iv_to_top.setOnClickListener(this);
		tv_brand_go.setOnClickListener(this);
		tv_collection.setOnClickListener(this);
		tv_cart.setOnClickListener(this);
		tv_add_cart.setOnClickListener(this);
		tv_call.setOnClickListener(this);
		tv_home.setOnClickListener(this);

		initRaidoGroup();
		initWebView();
		getSVDatas();
		onClick(btn_1);
	}

	private void setView() {
		if (mainEn != null) {
			ll_other.setVisibility(View.VISIBLE);
			tv_title.setText(mainEn.getName());
			if (mainEn.getImgLists() != null && viewLists.size() == 0) {
				initViewPager();
			}
			attrEn = mainEn.getAttrEn();
			attrNameStr = getSelectShowStr(attrEn);
			price = mainEn.getComputePrice();
			mathPrice = mainEn.getComputePrice();
			tv_name.setText(mainEn.getBrandName() + mainEn.getName());
			tv_curr.setText(currStr);
			tv_price_sell.setText(mainEn.getSellPrice()); //商品卖价

			String full_price = mainEn.getFullPrice(); //商品原价
			if (StringUtil.isNull(full_price) || full_price.equals("0") || full_price.equals("0.00")) {
				tv_price_full.setVisibility(View.GONE);
				tv_discount.setVisibility(View.GONE);
			} else {
				tv_price_full.setVisibility(View.VISIBLE);
				tv_price_full.setText(full_price);
				tv_price_full.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				if (!StringUtil.isNull(mainEn.getDiscount())) {
					tv_discount.setVisibility(View.VISIBLE);
					tv_discount.setText(mainEn.getDiscount());
				}else {
					tv_discount.setVisibility(View.GONE);
				}
			}
			
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + mainEn.getBrandLogo(), iv_brang_logo, options);
			tv_brand_name.setText(mainEn.getBrandName());
			tv_brand_country.setText(mainEn.getBrandCountry());
			
			// 判定商品折价倒计时
			if (mainEn.getPromoteTime() > 0) {
				tv_timer.setVisibility(View.VISIBLE);
				mcdt = new MyCountDownTimer(tv_timer, 0, mainEn.getPromoteTime(), 1000,
						new MyCountDownTimer.MyTimerCallback() {
					@Override
					public void onFinish() {
						getSVDatas();
					}
				});
				mcdt.start(); //开始倒计时
				propertyNum = 2;
			}else {
				propertyNum = 3;
			}
			// 判定商品特性(折价商品恕不退换/非大陆货源不能货到付款)
			switch (propertyNum) {
			case 2:
				tv_property_1.setVisibility(View.VISIBLE); //正品保证
				tv_property_2.setVisibility(View.GONE);
				if (StringUtil.isNull(mainEn.getMailCountry()) || mainEn.getMailCountry().equals("0")) {
					tv_property_2.setText(getString(R.string.product_cash_delivery)); //货到付款
				}else {
					tv_property_2.setText(mainEn.getMailCountry() + getString(R.string.product_mail)); //香港直邮
				}
				tv_property_3.setVisibility(View.GONE);
				break;
			case 3:
				tv_property_1.setVisibility(View.VISIBLE); //正品保证
				tv_property_2.setVisibility(View.VISIBLE);
				tv_property_2.setText(getString(R.string.product_carefree_return)); //无忧退换
				tv_property_3.setVisibility(View.GONE);
				if (StringUtil.isNull(mainEn.getMailCountry()) || mainEn.getMailCountry().equals("0")) {
					tv_property_3.setText(getString(R.string.product_cash_delivery)); //货到付款
				}else {
					tv_property_3.setText(mainEn.getMailCountry() + getString(R.string.product_mail)); //香港直邮
				}
				break;
			}
			// 判定商品活动
			if (mainEn.getPromotionLists() != null && mainEn.getPromotionLists().size() > 0) {
				ll_promotion.setVisibility(View.VISIBLE);
				addPromotionView(ll_promotion, mainEn.getPromotionLists());
			}else {
				ll_promotion.setVisibility(View.GONE);
			}
			// 判定是否收藏此商品
			isColl = !StringUtil.isNull(mainEn.getIsCollection());
			changeCollectionStatus();
			// 判定购物车商品数
			updateCartTotalNum();
		}
	}

	/**
	 * 切换收藏此商品的状态
	 */
	private void changeCollectionStatus() {
		if (isColl) {
			tv_collection.setSelected(true);
			tv_collection.setTextColor(getResources().getColor(R.color.tv_color_status));
		}else {
			tv_collection.setSelected(false);
			tv_collection.setTextColor(getResources().getColor(R.color.label_text_color));
		}
	}

	/**
	 * 动态添加商品活动View
	 */
	private void addPromotionView(LinearLayout ll_main, List<ProductDetailEntity> proLists) {
		ll_main.removeAllViews();
		for (int i = 0; i < proLists.size(); i++) {
			View proView = LayoutInflater.from(mContext).inflate(R.layout.item_linear_promotion, null);
			TextView tv_type = (TextView) proView.findViewById(R.id.item_tv_promotion_type);
			tv_type.setText(proLists.get(i).getPromotionType());
			TextView tv_name = (TextView) proView.findViewById(R.id.item_tv_promotion_name);
			tv_name.setText(proLists.get(i).getPromotionName());
			ll_main.addView(proView);
			if (i == 0) {
				fristPromotionName = proLists.get(i).getPromotionName();
			}
		}
	}

	private void initViewPager() {
		if (viewPager == null) return;
		viewPager.removeAllViews();
		viewLists.clear();
		urlLists.clear();
		imgEns.clear();
		imgEns.addAll(mainEn.getImgLists());
		idsSize = imgEns.size();
		if (idsSize == 2 || idsSize == 3) {
			imgEns.addAll(mainEn.getImgLists());
		}
		tv_page.setVisibility(View.VISIBLE);
		tv_page.setText(getString(R.string.viewpager_indicator, 1, idsSize));
		for (int i = 0; i < imgEns.size(); i++) {
			if (i == 0) {
				fristGoodsImgUrl = IMAGE_URL_HTTP + imgEns.get(i).getImgMinUrl();
				if (UserManager.getInstance().getUserRankCode() == 4) { //达人
					shareImgUrl = IMAGE_URL_HTTP + imgEns.get(i).getImgMaxUrl();
				} else {
					shareImgUrl = fristGoodsImgUrl;
				}
			}
			if (i < idsSize) {
				String imgMaxUrl = IMAGE_URL_HTTP + imgEns.get(i).getImgMaxUrl();
				urlLists.add(imgMaxUrl);
			}
			String imgMinUrl = IMAGE_URL_HTTP + imgEns.get(i).getImgMinUrl();
			ImageView imageView = new ImageView(mContext);
			ImageLoader.getInstance().displayImage(imgMinUrl, imageView, options);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ViewPagerActivity.class);
				    intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
				    intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, idsPosition);
				    startActivity(intent);
				}
			});
			viewLists.add(imageView);
		}
		loadShareImg();
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
				if (layout != null) {
					viewPager.addView(layout);
				}
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
				if (layout != null) {
					viewPager.removeView(layout);
				}
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
				// 变更指示器
				if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
					idsPosition = idsPosition - idsSize;
				}
				tv_page.setText(getString(R.string.viewpager_indicator, idsPosition + 1, idsSize));
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

	private void loadShareImg() {
		if (!StringUtil.isNull(shareImgUrl)) {
			asyncImageLoader = AsyncImageLoader.getInstance(new AsyncImageLoaderCallback() {
				
				@Override
				public void imageLoaded(String path, String cachePath, Bitmap bm) {
					shareImgPath = cachePath;
				}
			});
			ImageLoadTask task = asyncImageLoader.loadImage(shareImgUrl, 0);
			if (task != null && task.getBitmap() != null) {
				shareImgPath = task.getNewPath();
			}
		}
	}

	private void initRaidoGroup() {
		ll_radio_main.setVisibility(View.GONE);
		btn_1.setText(getString(R.string.product_detail_tab_1));
		btn_1.setChecked(true);
		btn_1.setOnClickListener(this);
		//btn_2.setText(getString(R.string.product_detail_tab_2));
		//btn_2.setOnClickListener(this);
		btn_2.setVisibility(View.GONE);
		btn_3.setText(getString(R.string.product_detail_tab_3));
		btn_3.setOnClickListener(this);
		btn_3.setVisibility(View.GONE);
		btn_4.setVisibility(View.GONE);
		rl_screen.setVisibility(View.GONE);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		if (webview != null){
			//WebView属性设置
			WebSettings webSettings= webview.getSettings();
			//String user_agent = webSettings.getUserAgentString();
			//webSettings.setUserAgentString(user_agent+"_SP");
			webSettings.setUserAgentString(" "); //设置UserAgent
			webSettings.setUseWideViewPort(true); 
			webSettings.setLoadWithOverviewMode(true); 
			webSettings.setJavaScriptEnabled(true);
			webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			webSettings.setAllowFileAccess(true);
			webSettings.setAppCacheEnabled(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setDatabaseEnabled(true);
			
			//设置不允许外部浏览器打开
			webview.setWebViewClient(new WebViewClient(){
				
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
			
			//设置加载动画
			webview.setWebChromeClient(new WebChromeClient(){
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);
					progressBar.setVisibility(View.VISIBLE);
					if (newProgress == 100) {
						progressBar.setVisibility(View.GONE);
					}
				}
			});
		}
	}

	private void initVideoPopup() {
		Intent intent = new Intent(mContext, VideoActivity.class);
		intent.putExtra("videoUrl", mainEn.getVideoUrl());
		startActivity(intent);
	}
	
	@SuppressWarnings("deprecation")
	private void initCartPopup() {
		if (popupWindow == null) {
			popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_add_cart_select, null);
			RelativeLayout rl_finish = (RelativeLayout) popupView.findViewById(R.id.popup_add_cart_rl_finish);
			rl_finish.setOnClickListener(this);
			popupAnimShow = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
			popupAnimGone = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
			numberAddAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_number_add);
			ll_show = (LinearLayout) popupView.findViewById(R.id.popup_add_cart_ll_show);
			ll_show.startAnimation(popupAnimShow);
			
			iv_goods_img = (ImageView) popupView.findViewById(R.id.popup_add_cart_iv_img);
			iv_num_minus = (ImageView) popupView.findViewById(R.id.popup_add_cart_iv_num_minus);
			iv_num_add = (ImageView) popupView.findViewById(R.id.popup_add_cart_iv_num_add);
			rl_num_minus = (RelativeLayout) popupView.findViewById(R.id.popup_add_cart_rl_num_minus);
			rl_num_minus.setOnClickListener(this);
			rl_num_add = (RelativeLayout) popupView.findViewById(R.id.popup_add_cart_rl_num_add);
			rl_num_add.setOnClickListener(this);
			
			tv_popup_number = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_number);
			tv_popup_number.setText(String.valueOf(buyNumber));
			tv_popup_name = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_name);
			tv_popup_curr = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_curr);
			tv_popup_price = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_price);
			tv_popup_prompt = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_prompt);
			tv_popup_select = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_select);
			tv_popup_confirm = (TextView) popupView.findViewById(R.id.popup_add_cart_tv_confirm);
			tv_popup_confirm.setOnClickListener(this);
			
			if (mainEn != null) {
				ImageLoader.getInstance().displayImage(fristGoodsImgUrl, iv_goods_img, options);
				tv_popup_name.setText(mainEn.getName());
				tv_popup_curr.setText(currStr);
				tv_popup_price.setText(decimalFormat.format(mathPrice));
			}
			
			if (attrNum > 0) {
				svlv = (ScrollViewListView) popupView.findViewById(R.id.popup_add_cart_svlv);
				apCallback = new AddCartCallback() {
					
					@Override
					public void setOnClick(Object entity, int position, int num, double attrPrice,
							int id1, int id2, String selectName, String selectImg) {
						// 图片替换
						if (!StringUtil.isNull(selectImg)) {
							ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + selectImg, iv_goods_img, options);
						}else {
							ImageLoader.getInstance().displayImage(fristGoodsImgUrl, iv_goods_img, options);
						}
						// 刷新选择的属性名称
						tv_popup_select.setText(selectName);
						if (num == -1) {
							tv_popup_prompt.setText(getString(R.string.item_select_no));
							tv_popup_select.setTextColor(getResources().getColor(R.color.label_text_color));
						}else {
							tv_popup_prompt.setText(getString(R.string.item_select_ok));
							tv_popup_select.setTextColor(getResources().getColor(R.color.tv_color_status));
						}
						// 刷新商品价格及数量
						selectId_1 = id1;
						selectId_2 = id2;
						mathPrice = price + attrPrice;
						tv_popup_curr.setText(currStr);
						tv_popup_price.setText(decimalFormat.format(mathPrice));
						skuNum = 1; //默认库存数量
						buyNumber = 1; //默认购买数量
						iv_num_add.setSelected(false); //不可+
						iv_num_minus.setSelected(false); //不可-
						if (num >= 0) {
							isNext = true;
							skuNum = num;
						}else {
							isNext = false;
						}
						if (skuNum > 1) {
							iv_num_add.setSelected(true); //可+
						}else if (skuNum == 0) {
							buyNumber = 0;
							isNext = false;
						}
						updateBuyNumber(buyNumber);
						if (isNext) {
							tv_popup_confirm.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_app_buttom_0));
						}else {
							tv_popup_confirm.setBackgroundColor(getResources().getColor(R.color.input_text_color));
						}
					}
					
				};
				svlvAdapter = new AddCartPopupListAdapter(mContext, attrEn, apCallback);
				svlv.setAdapter(svlvAdapter);
				svlv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
			}else {
				attrNameStr = getString(R.string.product_buy_number);
				isNext = true;
				skuNum = mainEn.getStockNum();
				if (skuNum > 1) {
					iv_num_add.setSelected(true); //可+
				}else if (skuNum == 0) {
					buyNumber = 0;
					isNext = false;
				}
				updateBuyNumber(buyNumber);
				if (isNext) {
					tv_popup_confirm.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_app_buttom_0));
				}else {
					tv_popup_confirm.setBackgroundColor(getResources().getColor(R.color.input_text_color));
				}
			}
			tv_popup_select.setText(attrNameStr);
			
			popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			popupWindow.setFocusable(true);
			popupWindow.update();
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);
		}else {
			ll_show.startAnimation(popupAnimShow);
			popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);
		}
	}

	private void catrPopupDismiss() {
		ll_show.startAnimation(popupAnimGone);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				popupWindow.dismiss();
			}
		}, 500);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		startAnimation();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE);
			}
		}, 1000);
	}

	/**
	 * 提交加入购物车商品数据
	 */
	private void postCartProductData() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE);
	}
	
	/**
	 * 提交加入购物车商品数据
	 */
	private void postCollectionProduct() {
		request(AppConfig.REQUEST_SV_POST_COLLECITON_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_commom_btn_right_one:
			showShareView();
			break;
		case R.id.top_commom_iv_left:
			finish();
			break;
		case R.id.product_detail_iv_video:
			initVideoPopup();
			break;
		case R.id.topbar_radio_rb_1:
			String loadingUrl = AppConfig.URL_COMMON_GOODS_DETAIL_URL + "?id=" + goodsId + AppApplication.getHttpUrlLangCurValueStr();
			// 同步Cookies
			HttpUtil.synCookies(loadingUrl);
			webview.loadUrl(loadingUrl);
			break;
		case R.id.topbar_radio_rb_2:

			break;
		case R.id.topbar_radio_rb_3:

			break;
		case R.id.product_detail_tv_brand_go:
			if (mainEn != null) {
				Intent intent = new Intent(mContext, ShowListHeadActivity.class);
				intent.putExtra("pageCode", ShowListHeadActivity.PAGE_ROOT_CODE_1);
				intent.putExtra("brandId", StringUtil.getInteger(mainEn.getBrandId()));
				startActivity(intent);
			}
			break;
		case R.id.product_detail_tv_collection:
			if (!UserManager.getInstance().checkIsLogined()) {
				openLoginActivity(TAG);
				return;
			}
			postCollectionProduct();
			break;
		case R.id.product_detail_tv_cart:
			if (!UserManager.getInstance().checkIsLogined()) {
				openLoginActivity(TAG);
				return;
			}
			startActivity(new Intent(mContext, CartActivity.class));
			break;
		case R.id.product_detail_tv_add_cart:
			initCartPopup();
			break;
		case R.id.product_detail_tv_call:
			
			break;
		case R.id.product_detail_tv_home:
			startActivity(new Intent(mContext, HomeFragmentActivity.class));
			break;
		case R.id.popup_add_cart_rl_finish:
			catrPopupDismiss();
			break;
		case R.id.popup_add_cart_rl_num_minus: //-
			if (buyNumber > 1) {
				buyNumber--;
				if (buyNumber == 1) {
					iv_num_minus.setSelected(false); //不可-
				}
				if (buyNumber < skuNum) {
					iv_num_add.setSelected(true); //可+
				}
			}
			updateBuyNumber(buyNumber);
			break;
		case R.id.popup_add_cart_rl_num_add: //+
			if (skuNum > 1) {
				if (buyNumber < skuNum) {
					buyNumber++;
					iv_num_minus.setSelected(true); //可-
					iv_num_add.setSelected(true); //可+
					updateBuyNumber(buyNumber);
				}else {
					iv_num_add.setSelected(false); //不可+
				}
			}
			break;
		case R.id.popup_add_cart_tv_confirm:
			if (!UserManager.getInstance().checkIsLogined()) {
				openLoginActivity(TAG);
				return;
			}
			if (isNext) {
				postCartProductData();
			}
			break;
		case R.id.product_detail_iv_to_top:
			mScrollView.scrollTo(0, 0);
			break;
		}
	}

	private void showShareView() {
		if (mShareView != null && mainEn != null) {
			if (mShareView.getShareEntity() == null) {
				String genuine = getString(R.string.product_genuine_safeguard);
				ShareEntity shareEn = new ShareEntity();
				shareEn.setTitle(mainEn.getName());
				shareEn.setText(mainEn.getBrandCountry() + " " + mainEn.getBrandName()
						+ genuine + " " + mainEn.getName() + " " + fristPromotionName);
				shareEn.setUrl(AppConfig.ENVIRONMENT_PRESENT_SHARE_URL + "goods.php?id=" + mainEn.getId());
				shareEn.setImageUrl(shareImgUrl);
				shareEn.setImagePath(shareImgPath);
				mShareView.setShareEntity(shareEn);
			}
			if (mShareView.isShowing()) {
				mShareView.showShareLayer(false);
			} else {
				mShareView.showShareLayer(true);
			}
		}else {
			showShareError();
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		if (isUpdate) {
			isUpdate = false;
			getSVDatas();
			onClick(btn_1);
		}
		updateCartTotalNum();
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		// 销毁对象
        if (asyncImageLoader != null) {
        	asyncImageLoader.clearInstance();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
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
		instance = null;
		super.onDestroy();
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		if (y > height * 2) {
			if (!isShow) {
				isShow = true;
				iv_to_top.setVisibility(View.VISIBLE);
			}
		}else {
			if (isShow) {
				isShow = false;
				iv_to_top.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE:
			mainEn = null;
			mainEn = sc.getProductDetailDatas(goodsId);
			return mainEn;
		case AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE:
			cartEn = null;
			cartEn = sc.postCartProductData(1, goodsId, selectId_1, selectId_2, buyNumber, 0);
			return cartEn;
		case AppConfig.REQUEST_SV_POST_COLLECITON_CODE:
			return sc.postCollectionProduct(goodsId);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE:
			if (mainEn != null) {
				if (mainEn.getIsVideo() == 1 && !StringUtil.isNull(mainEn.getVideoUrl())) {
					iv_video.setVisibility(View.VISIBLE);
				}else {
					iv_video.setVisibility(View.GONE);
				}
				setView();
			}
			break;
		case AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE:
			if (cartEn != null) {
				catrPopupDismiss(); //关闭弹层
				if (cartEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					cartNumTotal = cartEn.getGoodsTotal();
					UserManager.getInstance().saveCartTotal(cartNumTotal);
					startNumberAddAnim(buyNumber);
				}else if (cartEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					loginTimeoutHandle();
				}else {
					if (StringUtil.isNull(cartEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(cartEn.getErrInfo(), 2000);
					}
				}
			}else {
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_COLLECITON_CODE:
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == 0 || baseEn.getErrCode() == 1) {
					isColl = !isColl;
					changeCollectionStatus();
					if (ShowListActivity.instance != null) { //收藏打开时刷新数据
						ShowListActivity.instance.isUpdate = true;
					}
					CommonTools.showToast(baseEn.getErrInfo(), 1000);
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					loginTimeoutHandle();
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(baseEn.getErrInfo(), 2000);
					}
				}
			}else {
				showServerBusy();
			}
			break;
		}
		stopAnimation();
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
	}

	private void loginTimeoutHandle() {
		showTimeOutDialog(TAG);
	}

	private String getSelectShowStr(ProductAttrEntity en){
		if (en != null && en.getAttrLists() != null) {
			StringBuilder sb = new StringBuilder();
			attrNum = en.getAttrLists().size();
			for (int i = 0; i < attrNum; i++) {
				sb.append(en.getAttrLists().get(i).getAttrName());
				sb.append("、");
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();
		}
		return "";
	}

	/**
	 * 更新购买数量
	 */
	private void updateBuyNumber(int number) {
		tv_popup_number.setText(String.valueOf(number));
	}

	/**
	 * 动态生成一个View实现数量增加的效果
	 */
	private void startNumberAddAnim(int addNum) {
		tv_cart_total.setVisibility(View.VISIBLE);
		
		final TextView tv_name = new TextView(mContext);
		tv_name.setGravity(Gravity.CENTER);
		tv_name.setText("+" + addNum);
		tv_name.setTextColor(mContext.getResources().getColor(R.color.tv_color_status));
		tv_name.setTextSize(14);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.BOTTOM;
		lp.setMargins(tv_collection.getRight()+tv_cart.getRight()/2+8, 
				0, 0, (ll_bottom_bar.getBottom()-ll_bottom_bar.getTop())-10);
		fl_main.addView(tv_name, lp);
		
		tv_name.startAnimation(numberAddAnim);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				fl_main.removeView(tv_name);
				updateCartTotalNum();
			}
		}, 1000);
	}

	/**
	 * 刷新购物车商品总数量
	 */
	private void updateCartTotalNum() {
		cartNumTotal = UserManager.getInstance().getCartTotal();
		if (cartNumTotal > 0) {
			tv_cart_total.setVisibility(View.VISIBLE);
			tv_cart_total.setText(String.valueOf(cartNumTotal));
		}else {
			tv_cart_total.setVisibility(View.GONE);
			tv_cart_total.setText(getString(R.string.number_0));
		}
	}

}
