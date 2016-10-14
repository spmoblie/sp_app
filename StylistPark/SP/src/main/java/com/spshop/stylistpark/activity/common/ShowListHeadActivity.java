package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductList2ItemAdapter;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.image.AsyncImageLoader.ImageLoadTask;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.MyCountDownTimer;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;
import com.spshop.stylistpark.widgets.stikkyheader.AnimatorBuilder;
import com.spshop.stylistpark.widgets.stikkyheader.HeaderStikkyAnimator;
import com.spshop.stylistpark.widgets.stikkyheader.StikkyHeader;
import com.spshop.stylistpark.widgets.stikkyheader.StikkyHeaderBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * "有头部跟随滑动的商品展示列表"
 */
@SuppressLint("UseSparseArrays")
public class ShowListHeadActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ShowListHeadActivity";
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static ShowListHeadActivity instance = null;

	public static final int PAGE_ROOT_CODE_1 = 1001; //CategoryActivity 或 ProductDetailActivity 或 ChildFragmentOne
	public static final int TYPE_1 = 1;  //默认
	public static final int TYPE_2 = 2;  //价格

	private int pageCount = 0; //每页数量
	private int dataTotal = 0; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //默认列表加载页
	private int page_type_2_ASC = 1;  //价格升序列表加载页
	private int page_type_2_DSC = 1;  //价格降序列表加载页
	private int topType = TYPE_1; //Top标记
	private int sortType = 0; //排序标记(0:默认排序/1:价格升序/2:价格降序)
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int total_1, total_2_ASC, total_2_DSC;
	private boolean isLoadOk = true; //加载数据控制符
	private boolean isUpdate = true;
	private boolean flag_type_2 = true; //价格排序控制符(true:价格升序/false:价格降序)

	private int rootCode = PAGE_ROOT_CODE_1;
	private int brandId = 0;
	private int selectId = 0;
	private int logo_height, time_height, group_height, spaceHeight;
	private int other_height, desc_max_height, desc_min_height, desc_lines;
	private long endTime = 0;
	private boolean isHead = true;
	private boolean isGone = true;
	private String selectName = "";
	private String logoImgUrl, shareImgUrl, shareImgPath;

	private LinearLayout ll_stikky_main, ll_favourable_time, ll_group_main;
	private RelativeLayout rl_top_1, rl_top_2, rl_top_3;
	private TextView tv_top_1, tv_top_2, tv_top_3;
	private ImageView iv_topbar_line, iv_to_top, iv_brand_img, iv_brand_logo;
	private TextView tv_brand_name, tv_brand_desc, tv_unfold, tv_no_data;
	private TextView tv_favourable_title, tv_time_day, tv_time_hour, tv_time_minute, tv_time_second;
	private Drawable rank_up, rank_down, rank_normal;
	private MyCountDownTimer mcdt;
	private StikkyHeader lv_header;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ProductList2ItemAdapter lv_two_adapter;
	private DisplayImageOptions options;
	private AsyncImageLoader asyncImageLoader;

	private BrandEntity brandEn;
	private ShareEntity shareEn;
	private SelectListEntity selectEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_1 = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_2_DSC = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_2_ASC = new ArrayList<ProductListEntity>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_2_asc = new ArrayMap<String, Boolean>();
	private ArrayMap<String, Boolean> am_all_2_dsc = new ArrayMap<String, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isInitShare = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_list);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		rootCode = getIntent().getIntExtra("pageCode", PAGE_ROOT_CODE_1);
		brandId = getIntent().getIntExtra("brandId", 0);

		time_height = getResources().getDimensionPixelSize(R.dimen.favourable_time_height);
		group_height = getResources().getDimensionPixelSize(R.dimen.topbar_group_height);
		spaceHeight = CommonTools.dip2px(mContext, 15);
		options = AppApplication.getImageOptions(0, 0, true);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		refresh_lv = (PullToRefreshListView) findViewById(R.id.show_list_listView);
		tv_no_data = (TextView) findViewById(R.id.show_list_tv_no_data);
		ll_stikky_main = (LinearLayout) findViewById(R.id.show_list_ll_stikky_main);
		ll_favourable_time = (LinearLayout) findViewById(R.id.show_list_ll_favourable_time);
		ll_group_main = (LinearLayout) findViewById(R.id.topbar_group_ll_main);
		rl_top_1 = (RelativeLayout) findViewById(R.id.topbar_group_rl_1);
		rl_top_2 = (RelativeLayout) findViewById(R.id.topbar_group_rl_2);
		rl_top_3 = (RelativeLayout) findViewById(R.id.topbar_group_rl_3);
		tv_top_1 = (TextView) findViewById(R.id.topbar_group_tv_1);
		tv_top_2 = (TextView) findViewById(R.id.topbar_group_tv_2);
		tv_top_3 = (TextView) findViewById(R.id.topbar_group_tv_3);
		iv_brand_img = (ImageView) findViewById(R.id.show_list_iv_brand_img);
		iv_brand_logo = (ImageView) findViewById(R.id.show_list_iv_brand_img_logo);
		iv_topbar_line = (ImageView) findViewById(R.id.show_list_iv_topbar_line);
		iv_to_top = (ImageView) findViewById(R.id.show_list_iv_to_top);
		tv_brand_name = (TextView) findViewById(R.id.show_list_tv_brand_name);
		tv_brand_desc = (TextView) findViewById(R.id.show_list_tv_brand_desc);
		tv_unfold = (TextView) findViewById(R.id.show_list_tv_unfold);
		tv_favourable_title = (TextView) findViewById(R.id.show_list_tv_favourable);
		tv_time_day = (TextView) findViewById(R.id.show_list_tv_time_day);
		tv_time_hour = (TextView) findViewById(R.id.show_list_tv_time_hour);
		tv_time_minute = (TextView) findViewById(R.id.show_list_tv_time_minute);
		tv_time_second = (TextView) findViewById(R.id.show_list_tv_time_second);

		rank_up = getResources().getDrawable(R.drawable.icon_rank_up);
		rank_up.setBounds(0, 0, rank_up.getMinimumWidth(), rank_up.getMinimumHeight());
		rank_down = getResources().getDrawable(R.drawable.icon_rank_down);
		rank_down.setBounds(0, 0, rank_down.getMinimumWidth(), rank_down.getMinimumHeight());
		rank_normal = getResources().getDrawable(R.drawable.icon_rank_normal);
		rank_normal.setBounds(0, 0, rank_normal.getMinimumWidth(), rank_normal.getMinimumHeight());
	}

	private void initView() {
		iv_to_top.setOnClickListener(this);
		// 初始化组件
		initListView();
		initViewGroup();
		setAdapter();
	}

	private void initListView() {
		refresh_lv.setPullRefreshEnabled(false);
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
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
				if (!isStopLoadMore(lv_show.size(), dataTotal, pageCount)) {
					loadSVDatas();
				}else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							refresh_lv.onPullUpRefreshComplete();
							refresh_lv.setHasMoreData(false); //设置不允许加载更多
						}
					}, 1000);
				}
			}
		});
		mListView = refresh_lv.getRefreshableView();
		mListView.setDivider(null);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

		if (rootCode == PAGE_ROOT_CODE_1) {
			// 设置头部跟随ListView滑动
			lv_header = StikkyHeaderBuilder.stickTo(refresh_lv, mListView)
					.setHeader(ll_stikky_main)
					.minHeightHeaderPixel(group_height)
					.animator(new ParallaxStikkyAnimator())
					//.setOnLoadListener(new MyOnLoadListener())
					.setOnMyScrollListener(new OnMyScrollListener()).build();
		} else {
			refresh_lv.setOnScrollListener(new OnMyScrollListener());
		}
	}

	private void initViewGroup() {
		tv_top_1.setText(getString(R.string.product_top_tab_1));
		rl_top_1.setOnClickListener(this);
		tv_top_2.setText(getString(R.string.product_top_tab_3));
		rl_top_2.setOnClickListener(this);
		tv_top_3.setText(R.string.filter);
		rl_top_3.setOnClickListener(this);
		updateViewGroupStatus();
	}

	/**
	 * 自定义ViewGroup状态切换
	 */
	private void updateViewGroupStatus() {
		tv_top_1.setSelected(false);
		tv_top_2.setSelected(false);
		switch (topType) {
			case TYPE_1:
				tv_top_1.setSelected(true);
				tv_top_2.setCompoundDrawables(null, null, rank_normal, null);
				break;
			case TYPE_2:
				tv_top_2.setSelected(true);
				if (flag_type_2) {
					tv_top_2.setCompoundDrawables(null, null, rank_up, null);
				} else {
					tv_top_2.setCompoundDrawables(null, null, rank_down, null);
				}
				break;
		}
	}

	/**
	 * 自定义筛选状态切换
	 */
	private void updateScreenStatus() {
		if (isScreenOR()) {
			tv_top_3.setSelected(true);
			tv_top_3.setText(selectName);
		}else {
			tv_top_3.setSelected(false);
			tv_top_3.setText(getString(R.string.filter));
		}
	}

	private void setHeadView() {
		if (brandEn != null) {
			setTitleLogo(options, IMAGE_URL_HTTP + brandEn.getLogoUrl());
			selectEn = brandEn.getSelectEn();
			endTime = brandEn.getEndTime();
			if (endTime > 0) {
				tv_favourable_title.setText(brandEn.getFavourable());
				mcdt = new MyCountDownTimer(tv_time_day, tv_time_hour, tv_time_minute, tv_time_second,
						endTime * 1000, 1000, new MyCountDownTimer.MyTimerCallback() {
					@Override
					public void onFinish() {
						getSVDatas();
					}
				});
				mcdt.start(); //开始倒计时
			}
			loadShareImg();
			//tv_brand_name.setText(brandEn.getName());
			//tv_brand_desc.setText(brandEn.getDesc());
			tv_unfold.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showOrGoneDesc();
				}

			});
		}
		ll_group_main.setVisibility(View.VISIBLE);
	}

	private void loadShareImg() {
		if (!StringUtil.isNull(brandEn.getDefineUrl())) {
			logoImgUrl = IMAGE_URL_HTTP + brandEn.getDefineUrl();
			ImageLoader.getInstance().displayImage(logoImgUrl, iv_brand_img, options);
			if (UserManager.getInstance().isTalent()) { //达人
				shareImgUrl = IMAGE_URL_HTTP + brandEn.getDefineUrl();
			} else {
				shareImgUrl = logoImgUrl;
			}
			asyncImageLoader = AsyncImageLoader.getInstance(new AsyncImageLoader.AsyncImageLoaderCallback() {

				@Override
				public void imageLoaded(String path, String cachePath, Bitmap bm) {
					initShareData(cachePath);
					showHeadViews(bm);
				}
			});
			ImageLoadTask task = asyncImageLoader.loadImage(shareImgUrl, 0);
			if (task != null && task.getBitmap() != null) {
				initShareData(task.getNewPath());
				showHeadViews(task.getBitmap());
			}
		}
	}

	private void initShareData(String cachePath) {
		shareImgPath = cachePath;
		setBtnRight(R.drawable.topbar_icon_share);
	}

	private void showHeadViews(Bitmap bm) {
		if (bm != null) {
			int w = bm.getWidth();
			int h = bm.getHeight();
            logo_height = h * AppApplication.screenWidth / w;
			iv_topbar_line.setVisibility(View.VISIBLE);
        } else {
            logo_height = 0;
        }
		if (endTime > 0) {
            ll_favourable_time.setVisibility(View.VISIBLE); //height = 64dp
            other_height = time_height + group_height;
			iv_topbar_line.setVisibility(View.VISIBLE);
		}else {
			ll_favourable_time.setVisibility(View.GONE);
			other_height = group_height;
        }
		desc_lines = tv_brand_desc.getLineCount();
		desc_min_height = 0;
		/*if (brandEn == null || StringUtil.isNull(brandEn.getDesc())) {
			tv_unfold.setVisibility(View.GONE);
			desc_max_height = 0;
		} else {
			tv_unfold.setVisibility(View.VISIBLE);
			desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + spaceHeight;
		}*/
		goneDesc();
		/*if (desc_lines > 2) {
			tv_unfold.setVisibility(View.VISIBLE);
			desc_min_height = 2 * tv_brand_desc.getLineHeight() + spaceHeight;
			desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + spaceHeight;
			goneDesc();
		} else {
			tv_unfold.setVisibility(View.GONE);
			if (StringUtil.isNull(brandEn.getDesc())) {
				desc_max_height = 0;
			} else {
				desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + spaceHeight;
			}
			showDesc();
		}*/
	}

	private void showOrGoneDesc() {
		if (desc_lines <= 2) return;
		if (isGone) {
			showDesc();
		}else {
			goneDesc();
		}
	}

	private void goneDesc() {
		isGone = true;
		//tv_unfold.setText(R.string.profile_intro);
		//tv_brand_desc.setVisibility(View.GONE);
		//tv_brand_desc.setLines(2);
		lv_header.setHeightHeader(logo_height + desc_min_height +  other_height);
	}

	private void showDesc() {
		isGone = false;
		//tv_unfold.setText(R.string.put_away);
		//tv_brand_desc.setVisibility(View.VISIBLE);
		//tv_brand_desc.setLines(desc_lines);
		lv_header.setHeightHeader(logo_height + desc_max_height + other_height);
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
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		loadType = -1;
		current_Page = 1;
		dataTotal = 0;
		setLoadMoreData();
		startAnimation();
		sendRequestCode();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
		switch (topType) {
		case TYPE_1: //默认
			current_Page = page_type_1;
			break;
		case TYPE_2: //价格
			switch (sortType) {
			case 1: //升序
				current_Page = page_type_2_ASC;
				break;
			case 2: //降序
				current_Page = page_type_2_DSC;
				break;
			}
			break;
		}
		sendRequestCode();
	}
	
	private void sendRequestCode() {
		switch (rootCode) {
		case PAGE_ROOT_CODE_1:
			requestProductLists();
			break;
		default:
			requestProductLists();
			break;
		}
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		if (current_Page == 1) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (brandEn == null) {
						getBrandProfile();
					} else {
						getBrandProductLists();
					}
				}

			}, 1000);
		}else {
			getBrandProductLists();
		}
	}
	
	private void getBrandProfile(){
		request(AppConfig.REQUEST_SV_GET_BRAND_PROFILE_CODE);
	}
	
	private void getBrandProductLists(){
		request(AppConfig.REQUEST_SV_GET_BRAND_PRODUCT_CODE);
	}

	/**
	 * 筛选
	 */
	public void updateScreenParameter(SelectListEntity newEn){
		int newId = -1;
		if (newEn != null) {
			newId = newEn.getChildId();
			selectName = newEn.getChildShowName();
		}else {
			newId = 0;
			selectName = "";
		}
		if (selectEn != null) {
			selectEn.setSelectEn(newEn);
		}
		if (selectId != newId) {
			updateData();
		}
		selectId = newId;
	}

	/**
	 * 判定是否有筛选项
	 */
	private boolean isScreenOR() {
		if (selectEn != null && selectEn.getSelectEn() != null) {
			return true;
		}
		return false;
	}

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		if (shareEn == null && brandEn != null) {
			shareEn = new ShareEntity();
			shareEn.setTitle(brandEn.getName());
			shareEn.setText(brandEn.getDesc());
			shareEn.setUrl(AppConfig.ENVIRONMENT_PRESENT_SHARE_URL + "brand.php?id=" + brandEn.getBrandId());
			shareEn.setImageUrl(shareImgUrl);
			shareEn.setImagePath(shareImgPath);
		}
		showShareView(shareEn);
	}

	@Override
	protected void openLoginActivity() {
		openLoginActivity(TAG);
	}

	@Override
	public void onClick(View v) {
		if (!isLoadOk) return; //加载频率控制
		switch (v.getId()) {
		case R.id.topbar_group_rl_1: //默认
			if (topType == TYPE_1) return;
			topType = TYPE_1;
			updateViewGroupStatus();
			flag_type_2 = true;
			sortType = 0;
			if (lv_all_1 != null && lv_all_1.size() > 0) {
				addOldListDatas(lv_all_1, page_type_1, total_1);
			}else {
				page_type_1 = 1;
				total_1 = 0;
				getSVDatas();
			}
			break;
		case R.id.topbar_group_rl_2: //价格
			topType = TYPE_2;
			if (flag_type_2) //价格升序
			{
				updateViewGroupStatus();
				flag_type_2 = false;
				sortType = 1;
				if (lv_all_2_ASC != null && lv_all_2_ASC.size() > 0) {
					addOldListDatas(lv_all_2_ASC, page_type_2_ASC, total_2_ASC);
				}else {
					page_type_2_ASC = 1;
					total_2_ASC = 0;
					getSVDatas();
				}
			} else //价格降序
			{
				updateViewGroupStatus();
				flag_type_2 = true;
				sortType = 2;
				if (lv_all_2_DSC != null && lv_all_2_DSC.size() > 0) {
					addOldListDatas(lv_all_2_DSC, page_type_2_DSC, total_2_DSC);
				} else {
					page_type_2_DSC = 1;
					total_2_DSC = 0;
					getSVDatas();
				}
			}
			break;
		case R.id.topbar_group_rl_3: //筛选
			if (selectEn != null) {
				selectEn.setTypeName(getString(R.string.filter));
				Intent intent = new Intent(mContext, SelectListActivity.class);
				intent.putExtra("data", selectEn);
				intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_7);
				startActivity(intent);
			}else {
				CommonTools.showToast(getString(R.string.toast_error_data_null), 1000);
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
	private void addOldListDatas(List<ProductListEntity> oldLists, int oldPage, int oldTotal) {
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

		updateAllData();
		super.onResume();
	}

	public void updateData() {
		isUpdate = true;
	}

	private void updateAllData() {
		if (isUpdate) {
			isUpdate = false;
			lv_show.clear();
			lv_show_two.clear();
			lv_all_1.clear();
			lv_all_2_DSC.clear();
			lv_all_2_ASC.clear();
			am_all_1.clear();
			am_all_2_asc.clear();
			am_all_2_dsc.clear();
			getSVDatas();
			updateScreenStatus();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		// 销毁对象
		if (asyncImageLoader != null) {
			asyncImageLoader.clearInstance();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		// 取消倒计时
		if (mcdt != null) {
			mcdt.cancel();
		}
		instance = null;
	}

	class OnMyScrollListener implements AbsListView.OnScrollListener {

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

    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator {

        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View mHeader_image = getHeader().findViewById(R.id.show_list_iv_brand_img);

            return AnimatorBuilder.create().applyVerticalParallax(mHeader_image);
        }
    }

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BRAND_PROFILE_CODE:
			params.add(new MyNameValuePair("app", "brand"));
			params.add(new MyNameValuePair("id", String.valueOf(brandId)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_BRAND_PROFILE_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_GET_BRAND_PRODUCT_CODE:
			params.add(new MyNameValuePair("app", "brand_goods"));
			params.add(new MyNameValuePair("id", String.valueOf(brandId)));
			params.add(new MyNameValuePair("order", String.valueOf(sortType)));
			params.add(new MyNameValuePair("cat_id", String.valueOf(selectId)));
			params.add(new MyNameValuePair("page", String.valueOf(current_Page)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_BRAND_PRODUCT_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BRAND_PROFILE_CODE:
			if (result != null) {
				brandEn = (BrandEntity) result;
			}
			getBrandProductLists();
			break;
		case AppConfig.REQUEST_SV_GET_BRAND_PRODUCT_CODE:
			if (result != null) {
				ProductListEntity mainEn = (ProductListEntity) result;
				pageCount = mainEn.getPageSize();
				int newTotal = mainEn.getDataTotal();
				List<ProductListEntity> lists = mainEn.getMainLists();
				if (lists != null && lists.size() > 0) {
					List<BaseEntity> newLists = null;
					switch (topType) {
					case TYPE_1: //默认
						newLists = addNewEntity(lv_all_1, lists, am_all_1);
						if (newLists != null) {
							page_type_1++;
						}
						total_1 = newTotal;
						break;
					case TYPE_2: //价格
						switch (sortType) {
						case 1: //升序
							newLists = addNewEntity(lv_all_2_ASC, lists, am_all_2_asc);
							if (newLists != null) {
								page_type_2_ASC++;
							}
							total_2_ASC = newTotal;
							break;
						case 2: //降序
							newLists = addNewEntity(lv_all_2_DSC, lists, am_all_2_dsc);
							if (newLists != null) {
								page_type_2_DSC++;
							}
							total_2_DSC = newTotal;
							break;
						}
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
		case TYPE_1: //默认
			if (loadType != 0) { //非下拉
				addAllShow(lv_all_1);
			}
			break;
		case TYPE_2: //价格
			switch (sortType) {
			case 1: //升序
				addAllShow(lv_all_2_ASC);
				break;
			case 2: //降序
				addAllShow(lv_all_2_DSC);
				break;
			}
			break;
		}
		myUpdateAdapter();
	}

	private void myUpdateAdapter() {
		if (current_Page == 1) {
			toTop();
		}
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
		if (isHead) {
			setHeadView();
			isHead = false;
		}
		lv_two_adapter.updateAdapter(lv_show_two);
		stopAnimation();
	}

	private void addAllShow(List<ProductListEntity> showLists) {
		lv_show.clear();
		lv_show.addAll(showLists);
	}

	private void addNewShowLists(List<BaseEntity> showLists) {
		lv_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			lv_show.add((ProductListEntity) showLists.get(i));
		}
		switch (topType) {
			case TYPE_1:
				lv_all_1.clear();
				lv_all_1.addAll(lv_show);
				break;
			case TYPE_2: //价格
				switch (sortType) {
					case 1: //升序
						lv_all_2_ASC.clear();
						lv_all_2_ASC.addAll(lv_show);
						break;
					case 2: //降序
						lv_all_2_DSC.clear();
						lv_all_2_DSC.addAll(lv_show);
						break;
				}
				break;
		}
		if (loadType == 0) {
			setLoadMoreData();
		}
	}

	@Override
	protected void startAnimation() {
		super.startAnimation();
		tv_no_data.setVisibility(View.GONE);
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
		if (lv_show.size() == 0) {
			tv_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			tv_no_data.setVisibility(View.GONE);
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
