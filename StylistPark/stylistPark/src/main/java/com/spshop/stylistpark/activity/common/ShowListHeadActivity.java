package com.spshop.stylistpark.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.spshop.stylistpark.adapter.ShowList2ItemAdapter;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.listener.OnLoadMoreListener;
import com.spshop.stylistpark.widgets.listener.OnMyScrollListener;
import com.spshop.stylistpark.widgets.stikkyheader.AnimatorBuilder;
import com.spshop.stylistpark.widgets.stikkyheader.HeaderStikkyAnimator;
import com.spshop.stylistpark.widgets.stikkyheader.StikkyHeader;
import com.spshop.stylistpark.widgets.stikkyheader.StikkyHeaderBuilder;
import com.tencent.stat.StatService;

/**
 * "有头部跟随滑动的商品展示列表"
 */
@SuppressLint("UseSparseArrays")
public class ShowListHeadActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "ShowListHeadActivity";
	public static ShowListHeadActivity instance = null;
	public static final int PAGE_ROOT_CODE_1 = 1001; //CategoryActivity 或 ProductDetailActivity
	
	private static final int Page_Count = 40;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int default_Page = 1;  //默认列表加载页
	private int price_ASC_Page = 1;  //价格升序列表加载页
	private int price_DESC_Page = 1;  //价格降序列表加载页
	private int sortType = 0; //数据排序标记(0:默认排序/1:价格降序/2:价格升序)
	private int loadType = -1; //(0:下拉刷新/1:翻页加载)
	private int topType = 1; //Top标记(1:默认/3:价格)
	private int isStock = 0; //有货标记(0:默认/1:有货)
	private int goodsTotal = 0; //商品总数量
	private boolean btn_3_flag = true; //价格排序控制符(true:价格升序/false:价格降序)
	private boolean isLoadOk = true;
	private boolean isFrist = true; //识别是否第一次打开页面
	
	private LinearLayout ll_stikky_main, ll_foot_main;
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private Button btn_screen;
	private ImageView iv_to_top, iv_brand_img;
	private TextView tv_brand_desc, tv_unfold, tv_radio_other, tv_footer, tv_page_num;
	private Drawable rank_up, rank_down, rank_normal, select_yes, select_no;
	private StikkyHeader lv_header;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ShowList2ItemAdapter lv_two_adapter;
	private DisplayImageOptions options;
	
	private int pageCode = PAGE_ROOT_CODE_1;
	private int brandId = 0;
	private int logo_height, desc_max_height, desc_min_height, desc_lines;
	private boolean isGone = true;
	private String brandName = "";
	private BrandEntity brandEn;
	private ProductListEntity product_MainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_lists_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_1 = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_3_DESC = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_3_ASC = new ArrayList<ProductListEntity>();
	private HashMap<Integer, Boolean> hm_all = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> hm_asc = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> hm_desc = new HashMap<Integer, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_list);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		pageCode = getIntent().getIntExtra("pageCode", PAGE_ROOT_CODE_1);
		brandId = getIntent().getIntExtra("brandId", 0);
		brandName = getIntent().getStringExtra("brandName");
		
		options = AppApplication.getImageOptions(0, 0);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		mListView = (ListView) findViewById(R.id.show_list_listView);
		ll_stikky_main = (LinearLayout) findViewById(R.id.show_list_ll_stikky_main);
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		btn_screen = (Button) findViewById(R.id.topbar_radio_btn_screen);
		iv_brand_img = (ImageView) findViewById(R.id.show_list_iv_brand_img);
		iv_to_top = (ImageView) findViewById(R.id.show_list_iv_to_top);
		tv_brand_desc = (TextView) findViewById(R.id.show_list_tv_brand_desc);
		tv_unfold = (TextView) findViewById(R.id.show_list_tv_unfold);
		tv_radio_other = (TextView) findViewById(R.id.topbar_radio_tv_other);
		tv_page_num = (TextView) findViewById(R.id.show_list_tv_page_num);
		tv_footer = (TextView) findViewById(R.id.loading_anim_samll_tv_show);
		ll_foot_main = (LinearLayout) findViewById(R.id.loading_anim_samll_ll_main);
		
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
		setTitle(brandName);
		iv_to_top.setOnClickListener(this);
		if (pageCode == PAGE_ROOT_CODE_1) {
			// 设置头部跟随ListView滑动
			lv_header = StikkyHeaderBuilder.stickTo(mListView)
					.setHeader(ll_stikky_main)
					.minHeightHeaderPixel(CommonTools.dip2px(mContext, 36))
					.animator(new ParallaxStikkyAnimator())
					.setOnLoadListener(new MyOnLoadListener())
					.setOnMyScrollListener(new MyScrollListener()).build();
		}
		// 初始化组件
		initRaidoGroup();
		setAdapter();
		setDefaultRadioButton();
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
		tv_radio_other.setText(R.string.product_stock);
		tv_radio_other.setCompoundDrawables(select_no, null, null, null);
		tv_radio_other.setOnClickListener(this);
	}

	private void setHeadView() {
		if (brandEn != null) {
			ImageLoader.getInstance().displayImage(AppConfig.ENVIRONMENT_PRESENT_IMG_APP + brandEn.getDefineUrl(), iv_brand_img, options);
			logo_height = iv_brand_img.getHeight();
			tv_brand_desc.setText(brandEn.getDesc());
			
			desc_lines = tv_brand_desc.getLineCount();
			if (desc_lines > 2) {
				tv_unfold.setVisibility(View.VISIBLE);
				desc_min_height = 3 * tv_brand_desc.getLineHeight() + CommonTools.dip2px(mContext, 25);
				desc_max_height = (desc_lines + 1) * tv_brand_desc.getLineHeight() + CommonTools.dip2px(mContext, 25);
				goneDescTo2Line();
			}else {
				tv_unfold.setVisibility(View.GONE);
				if (StringUtil.isNull(brandEn.getDesc())) {
					desc_max_height = 0;
				}else {
					desc_max_height = desc_lines * tv_brand_desc.getLineHeight() + CommonTools.dip2px(mContext, 15);
				}
				showDescAll();
			}
			/*tv_brand_desc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showOrGoneDesc();
				}
			});*/
			tv_unfold.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showOrGoneDesc();
				}
				
			});
		}
	}

	private void showOrGoneDesc() {
		if (desc_lines <= 2) return;
		if (isGone) {
			showDescAll();
		}else {
			goneDescTo2Line();
		}
	}

	private void goneDescTo2Line() {
		isGone = true;
		tv_unfold.setText(R.string.unfold);
		tv_brand_desc.setLines(2);
		lv_header.setHeightHeader(logo_height + desc_min_height + CommonTools.dip2px(mContext, 36));
	}

	private void showDescAll() {
		isGone = false;
		tv_unfold.setText(R.string.put_away);
		tv_brand_desc.setLines(desc_lines);
		lv_header.setHeightHeader(logo_height + desc_max_height + CommonTools.dip2px(mContext, 36));
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
	 * 设置默认项
	 */
	private void setDefaultRadioButton() {
		RadioButton defaultBtn = null;
		switch (topType) {
		case 1:
			defaultBtn = btn_1;
			break;
		case 3:
			defaultBtn = btn_3;
			break;
		default:
			defaultBtn = btn_1;
			break;
		}
		defaultBtn.setChecked(true);
		if (isFrist) {
			onClick(defaultBtn);
		}
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		loadType = -1;
		current_Page = 1;
		ll_foot_main.setVisibility(View.GONE);
		startAnimation();
		sendRequestCode();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
		ll_foot_main.setVisibility(View.VISIBLE);
		switch (topType) {
		case 1: //默认
			current_Page = default_Page;
			break;
		case 3: //价格
			switch (sortType) {
			case 1: //降序
				current_Page = price_DESC_Page;
				break;
			case 2: //升序
				current_Page = price_ASC_Page;
				break;
			}
			break;
		}
		sendRequestCode();
	}
	
	private void sendRequestCode() {
		switch (pageCode) {
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
		isFrist = false;
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

	@Override
	public void onClick(View v) {
		if (!isLoadOk) { //加载频率控制
			setDefaultRadioButton();
			return;
		}
		switch (v.getId()) {
		case R.id.topbar_radio_rb_1: //默认
			topType = 1;
			btn_3.setCompoundDrawables(null, null, rank_normal, null);
			btn_3_flag = true;
			sortType = 0;
			if (lv_lists_all_1 != null && lv_lists_all_1.size() > 0) {
				addOldListDatas(lv_lists_all_1, default_Page);
			}else {
				default_Page = 1;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_3: //价格
			topType = 3;
			if (btn_3_flag) //价格升序
			{
				btn_3_flag = false;
				btn_3.setCompoundDrawables(null, null, rank_up, null);
				sortType = 2;
				if (lv_lists_all_3_ASC != null && lv_lists_all_3_ASC.size() > 0) {
					addOldListDatas(lv_lists_all_3_ASC, price_ASC_Page);
				}else {
					price_ASC_Page = 1;
					getSVDatas();
				}
			} else //价格降序
			{
				btn_3_flag = true;
				btn_3.setCompoundDrawables(null, null, rank_down, null);
				sortType = 1;
				if (lv_lists_all_3_DESC != null && lv_lists_all_3_DESC.size() > 0) {
					addOldListDatas(lv_lists_all_3_DESC, price_DESC_Page);
				} else {
					price_DESC_Page = 1;
					getSVDatas();
				}
			}
			break;
		case R.id.topbar_radio_tv_other: //有货
			if (isStock == 1) {
				isStock = 0; //默认
				tv_radio_other.setTextColor(getResources().getColor(R.color.text_color_assist));
				tv_radio_other.setCompoundDrawables(select_no, null, null, null);
			}else {
				isStock = 1; //有货
				tv_radio_other.setTextColor(getResources().getColor(R.color.text_color_black));
				tv_radio_other.setCompoundDrawables(select_yes, null, null, null);
			}
			updateAllDatas();
			break;
		case R.id.show_list_iv_to_top: //回顶
			iv_to_top.setVisibility(View.GONE);
			toGridViewTop();
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
			toGridViewTop();
		}
	}
	
	/**
	 * 刷新所有已缓存的数据
	 */
	private void updateAllDatas() {
		lv_lists_all_1.clear();
		lv_lists_all_3_ASC.clear();
		lv_lists_all_3_DESC.clear();
		hm_all.clear();
		hm_asc.clear();
		hm_desc.clear();
		default_Page = 1;
		price_ASC_Page = 1;
		price_DESC_Page = 1;
		getSVDatas();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
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
	
	class MyOnLoadListener implements OnLoadMoreListener{

		@Override
		public void onLoadMore(View mFooterView_TV) {
			tv_page_num.setVisibility(View.VISIBLE);
        	int page_num = lv_lists_show.size()/Page_Count;
        	if (lv_lists_show.size()%Page_Count > 0) {
				page_num++;
			}
        	int page_total = goodsTotal/Page_Count;
        	if (goodsTotal%Page_Count > 0) {
        		page_total++;
			}
        	tv_page_num.setText(page_num + "/" + page_total);
        	new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					tv_page_num.setVisibility(View.GONE);
				}
			}, 2000);
        	
        	if (isStop()) {
        		tv_footer.setText(getString(R.string.loading_no_more));
			}else {
				tv_footer.setText(getString(R.string.loading_strive_loading));
				loadSVDatas();
			}
		}
	}
	
	class MyScrollListener implements OnMyScrollListener {

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
			brandEn = sc.getBrandProfile(brandId);
			return brandEn;
		case AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE:
			product_MainEn = null;
			product_MainEn = sc.getProductListDatas(0, sortType, brandId, Page_Count, current_Page, "", "", isStock);
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
					case 1: //默认
						if (loadType == 0) { //下拉
							if (goodsTotal < total) {
								List<ProductListEntity> datas = new ArrayList<ProductListEntity>();
								datas.addAll(lv_lists_all_1);
								lv_lists_all_1.clear();
								for (int i = 0; i < (total - goodsTotal); i++) {
									if (!hm_all.containsKey(lists.get(i).getId())) {
										lv_lists_all_1.add(lists.get(i));
										datas.remove(datas.size()-1);
									}
								}
								lv_lists_all_1.addAll(datas);
								addAllShow(lv_lists_all_1);
							}
						}else {
							addEntity(lv_lists_all_1, lists, hm_all);
							default_Page++;
							LogUtil.i(TAG, "default_Page = " + default_Page);
						}
						break;
					case 3: //价格
						switch (sortType) {
						case 1: //降序
							addEntity(lv_lists_all_3_DESC, lists, hm_desc);
							price_DESC_Page++;
							LogUtil.i(TAG, "price_DESC_Page = " + price_DESC_Page);
							break;
						case 2: //升序
							addEntity(lv_lists_all_3_ASC, lists, hm_asc);
							price_ASC_Page++;
							LogUtil.i(TAG, "price_ASC_Page = " + price_ASC_Page);
							break;
						}
						break;
					}
					goodsTotal = total;
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
		case 1: //默认
			if (loadType != 0) { //非下拉
				addAllShow(lv_lists_all_1);
			}
			break;
		case 3: //价格
			switch (sortType) {
			case 1: //降序
				addAllShow(lv_lists_all_3_DESC);
				break;
			case 2: //升序
				addAllShow(lv_lists_all_3_ASC);
				break;
			}
			break;
		}
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
			toGridViewTop();
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
	 * 滚动到GridView顶部
	 */
	private void toGridViewTop() {
		setAdapter();
	}
	
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		ll_foot_main.setVisibility(View.GONE);
	}
	
	/**
	 * 商品数小于一页时停止加载翻页数据
	 */
	private boolean isStop(){
		LogUtil.i(TAG, "current_Page = " + current_Page + " show size = " + lv_lists_show.size() + " goodsTotal = " + goodsTotal);
		return lv_lists_show.size() < Page_Count || lv_lists_show.size() == goodsTotal;
	}
	
}
