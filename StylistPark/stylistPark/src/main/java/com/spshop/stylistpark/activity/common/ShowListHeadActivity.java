package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductList2ItemAdapter;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.utils.CommonTools;
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
import com.tencent.stat.StatService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * "有头部跟随滑动的商品展示列表"
 */
@SuppressLint("UseSparseArrays")
public class ShowListHeadActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ShowListHeadActivity";
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final int PAGE_ROOT_CODE_1 = 1001; //CategoryActivity 或 ProductDetailActivity 或 ChildFragmentOne
	public static ShowListHeadActivity instance = null;
	public boolean isUpdate = false;
	public static final int TYPE_1 = 1;  //默认
	public static final int TYPE_3 = 3;  //价格

	private static final int Page_Count = 40;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //默认列表加载页
	private int page_type_3_ASC = 1;  //价格升序列表加载页
	private int page_type_3_DSC = 1;  //价格降序列表加载页
	private int topType = TYPE_1; //Top标记
	private int sortType = 0; //排序标记(0:默认排序/1:价格降序/2:价格升序)
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int isStock = 0; //有货标记(0:默认/1:有货)
	private int countTotal = 0; //数集总数量
	private boolean isLoadOk = true; //加载数据控制符
	private boolean isFrist = true; //识别是否第一次打开页面
	private boolean flag_type_3 = true; //价格排序控制符(true:价格升序/false:价格降序)

	private int rootCode = PAGE_ROOT_CODE_1;
	private int brandId = 0;
	private int selectId = 0;
	private int logo_height, time_height, group_height, spaceHeight;
	private int other_height, desc_max_height, desc_min_height, desc_lines;
	private long endTime = 0;
	private boolean isGone = true;
	private String selectName = "";
	private String logoImgUrl, logoImgPath;

	private LinearLayout ll_stikky_main, ll_favourable_time;
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private Button btn_screen;
	private ImageView iv_to_top, iv_brand_img, iv_brand_logo;
	private TextView tv_brand_name, tv_brand_desc, tv_unfold, tv_radio_other, tv_no_data;
	private TextView tv_favourable_title, tv_time_day, tv_time_hour, tv_time_minute, tv_time_second;
	private Drawable rank_up, rank_down, rank_normal, select_yes, select_no;
	private MyCountDownTimer mcdt;
	private StikkyHeader lv_header;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ProductList2ItemAdapter lv_two_adapter;
	private DisplayImageOptions options;
	private AsyncImageLoader asyncImageLoader;

	private BrandEntity brandEn;
	private SelectListEntity selectEn;
	private ProductListEntity product_MainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_1 = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_3_DSC = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_3_ASC = new ArrayList<ProductListEntity>();
	private HashMap<Integer, Boolean> hm_all_1 = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> hm_all_3_asc = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> hm_all_3_dsc = new HashMap<Integer, Boolean>();
	
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
		options = AppApplication.getImageOptions(0, 0);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		refresh_lv = (PullToRefreshListView) findViewById(R.id.show_list_listView);
		tv_no_data = (TextView) findViewById(R.id.show_list_tv_no_data);
		ll_stikky_main = (LinearLayout) findViewById(R.id.show_list_ll_stikky_main);
		ll_favourable_time = (LinearLayout) findViewById(R.id.show_list_ll_favourable_time);
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		btn_screen = (Button) findViewById(R.id.topbar_radio_btn_screen);
		iv_brand_img = (ImageView) findViewById(R.id.show_list_iv_brand_img);
		iv_brand_logo = (ImageView) findViewById(R.id.show_list_iv_brand_img_logo);
		iv_to_top = (ImageView) findViewById(R.id.show_list_iv_to_top);
		tv_brand_name = (TextView) findViewById(R.id.show_list_tv_brand_name);
		tv_brand_desc = (TextView) findViewById(R.id.show_list_tv_brand_desc);
		tv_unfold = (TextView) findViewById(R.id.show_list_tv_unfold);
		tv_favourable_title = (TextView) findViewById(R.id.show_list_tv_favourable);
		tv_time_day = (TextView) findViewById(R.id.show_list_tv_time_day);
		tv_time_hour = (TextView) findViewById(R.id.show_list_tv_time_hour);
		tv_time_minute = (TextView) findViewById(R.id.show_list_tv_time_minute);
		tv_time_second = (TextView) findViewById(R.id.show_list_tv_time_second);
		tv_radio_other = (TextView) findViewById(R.id.topbar_radio_tv_other);

		rank_up = getResources().getDrawable(R.drawable.icon_rank_up);
		rank_up.setBounds(0, 0, rank_up.getMinimumWidth(), rank_up.getMinimumHeight());
		rank_down = getResources().getDrawable(R.drawable.icon_rank_down);
		rank_down.setBounds(0, 0, rank_down.getMinimumWidth(), rank_down.getMinimumHeight());
		rank_normal = getResources().getDrawable(R.drawable.icon_rank_normal);
		rank_normal.setBounds(0, 0, rank_normal.getMinimumWidth(), rank_normal.getMinimumHeight());
		select_yes = getResources().getDrawable(R.drawable.btn_select_hook_yes);
		select_yes.setBounds(0, 0, select_yes.getMinimumWidth(), select_yes.getMinimumHeight());
		select_no = getResources().getDrawable(R.drawable.btn_select_hook_no);
		select_no.setBounds(0, 0, select_no.getMinimumWidth(), select_no.getMinimumHeight());
	}

	private void initView() {
		iv_to_top.setOnClickListener(this);
		// 初始化组件
		initListView();
		initRaidoGroup();
		setAdapter();
		setDefaultRadioButton();
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
				int page_num = lv_show.size()/Page_Count;
				if (lv_show.size()%Page_Count > 0) {
					page_num++;
				}
				int page_total = countTotal/Page_Count;
				if (countTotal%Page_Count > 0) {
					page_total++;
				}
				CommonTools.showPageNum(mContext, page_num + "/" + page_total, 1000);

				if (!isStop()) {
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

	private void initRaidoGroup() {
		btn_1.setText(getString(R.string.product_top_tab_1));
		btn_1.setChecked(true);
		btn_1.setOnClickListener(this);
		btn_2.setVisibility(View.GONE);
		btn_3.setText(getString(R.string.product_top_tab_3));
		btn_3.setOnClickListener(this);
		btn_4.setVisibility(View.GONE);
		btn_screen.setVisibility(View.GONE);
		tv_radio_other.setVisibility(View.VISIBLE);
		tv_radio_other.setText(R.string.filter);
		//tv_radio_other.setCompoundDrawables(select_no, null, null, null);
		tv_radio_other.setOnClickListener(this);
	}

	private void setHeadView() {
		if (brandEn != null) {
			//setTitle(brandEn.getName());
			setTitleLogo(options, IMAGE_URL_HTTP + brandEn.getLogoUrl());
			setBtnRight(R.drawable.topbar_icon_share);
			selectEn = brandEn.getSelectEn();
			endTime = brandEn.getEndTime();
			if (endTime > 0) {
				tv_favourable_title.setText(brandEn.getFavourable());
				mcdt = new MyCountDownTimer(mContext, tv_time_day, tv_time_hour, tv_time_minute, tv_time_second,
						endTime * 1000, 1000, new MyCountDownTimer.MyTimerCallback() {
					@Override
					public void onFinish() {
						getSVDatas();
					}
				});
				mcdt.start(); //开始倒计时
			}
			logoImgUrl = IMAGE_URL_HTTP + brandEn.getDefineUrl();
			ImageLoader.getInstance().displayImage(logoImgUrl, iv_brand_img, options);
			loadShareImg();
			//ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + brandEn.getLogoUrl(), iv_brand_logo, options);
			//tv_brand_name.setText(brandEn.getName());
			tv_brand_desc.setText(brandEn.getDesc());
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				logo_height = iv_brand_img.getHeight();
				if (endTime > 0) {
					ll_favourable_time.setVisibility(View.VISIBLE); //height = 64dp
					other_height = time_height + group_height;
				}else {
					ll_favourable_time.setVisibility(View.GONE);
					other_height = group_height;
				}
				desc_lines = tv_brand_desc.getLineCount();
				desc_min_height = 0;
				if (brandEn == null || StringUtil.isNull(brandEn.getDesc())) {
					tv_unfold.setVisibility(View.GONE);
					desc_max_height = 0;
				}else {
					tv_unfold.setVisibility(View.VISIBLE);
					desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + spaceHeight;
				}
				goneDesc();
//				if (desc_lines > 2) {
//					tv_unfold.setVisibility(View.VISIBLE);
//					desc_min_height = 2 * tv_brand_desc.getLineHeight() + spaceHeight;
//					desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + spaceHeight;
//					goneDesc();
//				} else {
//					tv_unfold.setVisibility(View.GONE);
//					if (StringUtil.isNull(brandEn.getDesc())) {
//						desc_max_height = 0;
//					} else {
//						desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + spaceHeight;
//					}
//					showDesc();
//				}
			}
		}, 1000);
		tv_unfold.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showOrGoneDesc();
			}

		});
	}

	private void loadShareImg() {
		if (!StringUtil.isNull(logoImgUrl)) {
			asyncImageLoader = AsyncImageLoader.getInstance(mContext, new AsyncImageLoader.AsyncImageLoaderCallback() {

				@Override
				public void imageLoaded(String path, File saveFile, Bitmap bm) {
					if (saveFile != null) {
						logoImgPath = saveFile.getPath();
					}
				}
			});
			asyncImageLoader.loadImage(false, logoImgUrl, 0);
		}
	}

	private void showOrGoneDesc() {
//		if (desc_lines <= 2) return;
		if (isGone) {
			showDesc();
		}else {
			goneDesc();
		}
	}

	private void goneDesc() {
		isGone = true;
		tv_unfold.setText(R.string.profile_intro);
		tv_brand_desc.setVisibility(View.GONE);
		//tv_brand_desc.setLines(0);
		lv_header.setHeightHeader(logo_height + desc_min_height +  other_height);
	}

	private void showDesc() {
		isGone = false;
		tv_unfold.setText(R.string.put_away);
		tv_brand_desc.setVisibility(View.VISIBLE);
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
	 * 设置默认项
	 */
	private void setDefaultRadioButton() {
		RadioButton defaultBtn = null;
		switch (topType) {
		case TYPE_1:
			defaultBtn = btn_1;
			break;
		case TYPE_3:
			defaultBtn = btn_3;
			break;
		default:
			defaultBtn = btn_1;
			break;
		}
		defaultBtn.setChecked(true);
		if (isFrist) {
			onClick(defaultBtn);
			isFrist = false;
		}
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		loadType = -1;
		current_Page = 1;
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
		case TYPE_3: //价格
			switch (sortType) {
			case 1: //降序
				current_Page = page_type_3_DSC;
				break;
			case 2: //升序
				current_Page = page_type_3_ASC;
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
					}
					getBrandProductLists();
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
		request(AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE);
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
			isUpdate = true;
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
		showShareView();
	}

	private void showShareView() {
		if (mShareView != null && brandEn != null) {
			if (mShareView.isShowing()) {
				mShareView.showShareLayer(mContext, false);
				return;
			}
			int uid = StringUtil.getInteger(UserManager.getInstance().getUserId());
			ShareEntity shareEn = new ShareEntity();
			shareEn.setTitle(brandEn.getName());
			shareEn.setText(brandEn.getDesc());
			shareEn.setUrl(AppConfig.ENVIRONMENT_PRESENT_SHARE_URL + "brand.php?id=" + brandEn.getBrandId() + "&uid=" + uid);
			shareEn.setImageUrl(logoImgUrl);
			shareEn.setImagePath(logoImgPath);
			mShareView.setShareEntity(shareEn);
			mShareView.showShareLayer(mContext, true);
		}else {
			showShareError();
		}
	}

	@Override
	public void onClick(View v) {
		if (!isLoadOk) { //加载频率控制
			setDefaultRadioButton();
			return;
		}
		switch (v.getId()) {
		case R.id.topbar_radio_rb_1: //默认
			topType = TYPE_1;
			btn_3.setCompoundDrawables(null, null, rank_normal, null);
			flag_type_3 = true;
			sortType = 0;
			if (lv_all_1 != null && lv_all_1.size() > 0) {
				addOldListDatas(lv_all_1, page_type_1);
			}else {
				page_type_1 = 1;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_3: //价格
			topType = TYPE_3;
			if (flag_type_3) //价格升序
			{
				flag_type_3 = false;
				btn_3.setCompoundDrawables(null, null, rank_up, null);
				sortType = 2;
				if (lv_all_3_ASC != null && lv_all_3_ASC.size() > 0) {
					addOldListDatas(lv_all_3_ASC, page_type_3_ASC);
				}else {
					page_type_3_ASC = 1;
					getSVDatas();
				}
			} else //价格降序
			{
				flag_type_3 = true;
				btn_3.setCompoundDrawables(null, null, rank_down, null);
				sortType = 1;
				if (lv_all_3_DSC != null && lv_all_3_DSC.size() > 0) {
					addOldListDatas(lv_all_3_DSC, page_type_3_DSC);
				} else {
					page_type_3_DSC = 1;
					getSVDatas();
				}
			}
			break;
		case R.id.topbar_radio_tv_other: //有货或筛选
//			if (isStock == 1) {
//				isStock = 0; //默认
//				tv_radio_other.setTextColor(getResources().getColor(R.color.text_color_assist));
//				tv_radio_other.setCompoundDrawables(select_no, null, null, null);
//			}else {
//				isStock = 1; //有货
//				tv_radio_other.setTextColor(getResources().getColor(R.color.text_color_black));
//				tv_radio_other.setCompoundDrawables(select_yes, null, null, null);
//			}
//			updateAllDatas();
			if (selectEn != null) {
				selectEn.setTypeName(getString(R.string.filter));
				Intent intent = new Intent(mContext, SelectListActivity.class);
				intent.putExtra("data", selectEn);
				intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_7);
				startActivity(intent);
			}else {
				CommonTools.showToast(mContext, getString(R.string.toast_error_data_null), 1000);
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
	private void addOldListDatas(List<ProductListEntity> oldLists, int oldPage) {
		addAllShow(oldLists);
		current_Page = oldPage;
		myUpdateAdapter();
		if (current_Page != 1) {
			toTop();
		}
	}
	
	/**
	 * 刷新所有已缓存的数据
	 */
	private void updateAllDatas() {
		lv_all_1.clear();
		lv_all_3_DSC.clear();
		lv_all_3_ASC.clear();
		hm_all_1.clear();
		hm_all_3_asc.clear();
		hm_all_3_dsc.clear();
		current_Page = 1;
		page_type_1 = 1;
		page_type_3_ASC = 1;
		page_type_3_DSC = 1;
		getSVDatas();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);

		if (isUpdate) {
			isUpdate = false;
			updateAllDatas();
			if (isScreenOR()) {
				tv_radio_other.setText(selectName);
				tv_radio_other.setTextColor(mContext.getResources().getColor(R.color.text_color_red_1));
			}else {
				tv_radio_other.setText(getString(R.string.filter));
				tv_radio_other.setTextColor(mContext.getResources().getColor(R.color.text_color_assist));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
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
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BRAND_PROFILE_CODE:
			brandEn = sc.getBrandProfile(brandId, mContext.getString(R.string.all));
			return brandEn;
		case AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE:
			product_MainEn = null;
			product_MainEn = sc.getBrandProductLists(brandId, sortType, selectId, Page_Count, current_Page);
			return product_MainEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BRAND_PROFILE_CODE:
			setHeadView();
			break;
		case AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE:
			if (product_MainEn != null && product_MainEn.getMainLists() != null) {
				int total = product_MainEn.getTotal();
				List<ProductListEntity> lists = product_MainEn.getMainLists();
				if (lists.size() > 0) {
					switch (topType) {
					case TYPE_1: //默认
						addEntity(lv_all_1, lists, hm_all_1);
						page_type_1++;
						break;
					case TYPE_3: //价格
						switch (sortType) {
						case 1: //降序
							addEntity(lv_all_3_DSC, lists, hm_all_3_dsc);
							page_type_3_DSC++;
							break;
						case 2: //升序
							addEntity(lv_all_3_ASC, lists, hm_all_3_asc);
							page_type_3_ASC++;
							break;
						}
						break;
					}
					countTotal = total;
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
		case TYPE_3: //价格
			switch (sortType) {
			case 1: //降序
				addAllShow(lv_all_3_DSC);
				break;
			case 2: //升序
				addAllShow(lv_all_3_ASC);
				break;
			}
			break;
		}
		myUpdateAdapter();
	}

	/**
	 * 刷新数集
	 */
	private void updEntity(int newTotal, int oldTotal, List<ProductListEntity> newDatas,
						   List<ProductListEntity> oldDatas, HashMap<Integer, Boolean> oldMap) {
		if (oldTotal < newTotal) {
			List<ProductListEntity> datas = new ArrayList<ProductListEntity>();
			datas.addAll(oldDatas);
			oldDatas.clear();
			for (int i = 0; i < (newTotal - oldTotal); i++) {
				if (!oldMap.containsKey(newDatas.get(i).getId())) {
					oldDatas.add(newDatas.get(i));
					datas.remove(datas.size()-1);
				}
			}
			oldDatas.addAll(datas);
			addAllShow(oldDatas);
			refresh_lv.setHasMoreData(true); //设置允许加载更多
		}
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

	@Override
	protected void startAnimation() {
		super.startAnimation();
		tv_no_data.setVisibility(View.GONE);
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		switch (loadType) {
			case 0: //下拉刷新
				refresh_lv.onPullDownRefreshComplete();
				break;
			case 1: //加载更多
				refresh_lv.onPullUpRefreshComplete();
				break;
		}
		if (lv_show.size() == 0) {
			tv_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			tv_no_data.setVisibility(View.GONE);
			refresh_lv.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 判定是否停止加载翻页数据
	 */
	private boolean isStop(){
		return lv_show.size() > 0 && lv_show.size() == countTotal;
	}
	
}
