package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.activity.common.VideoActivity;
import com.spshop.stylistpark.activity.common.ViewPagerActivity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.image.AsyncImageLoader.AsyncImageLoaderCallback;
import com.spshop.stylistpark.image.AsyncImageLoader.ImageLoadTask;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.MyCountDownTimer;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.ObservableScrollView;
import com.spshop.stylistpark.widgets.ObservableScrollView.ScrollViewListener;

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

	@SuppressWarnings("unused")
	private LinearLayout ll_other, ll_bottom, ll_head, ll_promotion, ll_show, ll_radio_main;
	private FrameLayout fl_main;
	private RelativeLayout rl_screen;
	private ImageView iv_left, iv_video, iv_brang_logo, iv_to_top;
	private TextView tv_title, tv_timer, tv_page, tv_name, tv_curr, tv_price_sell, tv_price_full, tv_discount;
	private TextView tv_property_1, tv_property_2, tv_property_3, tv_brand_name, tv_brand_country, tv_brand_go;
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private Button btn_share;
	private ObservableScrollView mScrollView;
	private ViewPager viewPager;
	private Runnable mPagerAction;
	private WebView webview;
	private ProgressBar progressBar;
	private AsyncImageLoader asyncImageLoader;

	private ShareEntity shareEn;
	private ProductDetailEntity mainEn;
	private MyCountDownTimer mcdt;
	private boolean isShow = false;
	private boolean vprStop = true;
	private boolean isUpdate = false;
	private int idsSize, idsPosition, vprPosition;
	private int goodsId = 0;
	private int propertyNum = 3;
	private String fristGoodsImgUrl, shareImgUrl, shareImgPath, fristPromotionName;
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

		findViewById();
		initView();
	}

	private void findViewById() {
		fl_main = (FrameLayout) findViewById(R.id.product_detail_fl_main);
		ll_other = (LinearLayout) findViewById(R.id.product_detail_ll_anim_other);
		ll_bottom = (LinearLayout) findViewById(R.id.product_detail_ll_anim_bottom);
		ll_head = (LinearLayout) findViewById(R.id.top_search_ll_main);
		ll_promotion = (LinearLayout) findViewById(R.id.product_detail_ll_promotion);
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
	}

	private void initView() {
		setHeadVisibility(View.GONE);
		setBottomBarVisibility(View.VISIBLE);
		//tv_title.setText(getString(R.string.title_product_detail));
		btn_share.setBackground(getResources().getDrawable(R.drawable.topbar_icon_share));
		mScrollView.setScrollViewListener(this);
		btn_share.setOnClickListener(this);
		btn_share.setVisibility(View.GONE);
		iv_left.setOnClickListener(this);
		iv_video.setOnClickListener(this);
		iv_to_top.setOnClickListener(this);
		tv_brand_go.setOnClickListener(this);

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
			tv_name.setText(mainEn.getBrandName() + mainEn.getName());
			tv_curr.setText(currStr);

			String sell_price = mainEn.getSellPrice(); //商品卖价
			String full_price = mainEn.getFullPrice(); //商品原价
			if (StringUtil.priceIsNull(full_price) || StringUtil.priceIsNull(sell_price)) {
				if (!StringUtil.priceIsNull(sell_price)) {
					tv_price_sell.setText(sell_price);
				} else {
					tv_price_sell.setText(full_price);
				}
				tv_price_full.getPaint().setFlags(0);
				tv_price_full.setVisibility(View.GONE);
				tv_discount.setVisibility(View.GONE);
			} else {
				tv_price_sell.setText(sell_price);
				tv_price_full.setText(currStr + full_price);
				tv_price_full.setVisibility(View.VISIBLE);
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
			// 显示购物车商品数
			showCartTotal();
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
				if (UserManager.getInstance().isTalent()) { //达人
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
					initShareData(cachePath);
				}
			});
			ImageLoadTask task = asyncImageLoader.loadImage(shareImgUrl, 0);
			if (task != null && task.getBitmap() != null) {
				initShareData(task.getNewPath());
			}
		}
	}

	private void initShareData(String cachePath) {
		shareImgPath = cachePath;
		btn_share.setVisibility(View.VISIBLE);
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

	@Override
	protected void openLoginActivity() {
		openLoginActivity(TAG);
	}

	@Override
	protected void postCollectionProduct() {
		postCollectionProduct(goodsId);
	}

	@Override
	protected void requestProductAttrData() {
		requestProductAttrData(goodsId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_commom_btn_right_one:
			if (shareEn == null && mainEn != null) {
				String genuine = getString(R.string.product_genuine_safeguard);
				shareEn = new ShareEntity();
				shareEn.setTitle(mainEn.getName());
				shareEn.setText(mainEn.getBrandCountry() + " " + mainEn.getBrandName()
						+ genuine + " " + mainEn.getName() + " " + fristPromotionName);
				shareEn.setUrl(AppConfig.ENVIRONMENT_PRESENT_SHARE_URL + "goods.php?id=" + mainEn.getId());
				shareEn.setImageUrl(shareImgUrl);
				shareEn.setImagePath(shareImgPath);
			}
			showShareView(shareEn);
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
		case R.id.product_detail_iv_to_top:
			mScrollView.smoothScrollTo(0, 0);
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
		if (isUpdate) {
			isUpdate = false;
			getSVDatas();
			onClick(btn_1);
		}
		showCartTotal();
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
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE:
			params.add(new MyNameValuePair("app", "goods"));
			params.add(new MyNameValuePair("id", String.valueOf(goodsId)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE, uri, params, HttpUtil.METHOD_GET);

		default:
			return super.doInBackground(requestCode);
		}
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE:
			if (result != null) {
				mainEn = (ProductDetailEntity) result;
				if (mainEn.getIsVideo() == 1 && !StringUtil.isNull(mainEn.getVideoUrl())) {
					iv_video.setVisibility(View.VISIBLE);
				}else {
					iv_video.setVisibility(View.GONE);
				}
				setView();
			}
			break;
		default:
			super.onSuccess(requestCode, result);
			break;
		}
		stopAnimation();
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	@Override
	protected void showTimeOutDialog() {
		showTimeOutDialog(TAG);
	}

}
