package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.collage.IndexDisplayFragment;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.BrandIndexDisplayAdapter;
import com.spshop.stylistpark.adapter.CategoryGridAdapter;
import com.spshop.stylistpark.adapter.CategoryLeftListAdapter;
import com.spshop.stylistpark.adapter.CategoryRightListAdapter;
import com.spshop.stylistpark.adapter.IndexDisplayAdapter.OnIndexDisplayItemClick;
import com.spshop.stylistpark.db.CategoryDBService;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.CategoryListEntity;
import com.spshop.stylistpark.entity.IndexDisplay;
import com.spshop.stylistpark.utils.IndexDisplayTool;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * "商品分类"Activity
 */
@SuppressLint("NewApi")
public class CategoryActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "CategoryActivity";
	public static CategoryActivity instance = null;
	
	private ViewPager mViewPager;
	private ImageView iv_top_left;
	private TextView tv_title_1, tv_title_2;
	private ListView lv_left, lv_brand;
	private GridView gv_right;
	private CategoryLeftListAdapter lv_left_Adapter;
	private CategoryRightListAdapter lv_right_Adapter;
	private CategoryGridAdapter gv_Adapter;
	private CategoryListEntity mainEn, brandsEn;
	private CategoryDBService dbs;
	private IndexDisplayFragment indexDisplayFragment;
	
	private int index = 0;
	private int dataType = 0; //区分父级、子级分类的标记(0表示父级/1表示子级)
	private int mCurrentItem;
	private ArrayList<View> viewLists = new ArrayList<View>();
	private List<CategoryListEntity> lv_lists = new ArrayList<CategoryListEntity>();
	private List<CategoryListEntity> gv_lists = new ArrayList<CategoryListEntity>();
	private List<BrandEntity> brandList = new ArrayList<BrandEntity>();
	private HashMap<String, Integer> hm_index = new HashMap<String, Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		dbs = CategoryDBService.getInstance(this);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		mViewPager = (ViewPager) findViewById(R.id.category_viewpager);
		iv_top_left = (ImageView) findViewById(R.id.top_two_title_iv_left);
		tv_title_1 = (TextView) findViewById(R.id.top_two_title_tv_title_1);
		tv_title_2 = (TextView) findViewById(R.id.top_two_title_tv_title_2);
	}

	private void initView() {
		setHeadVisibility(View.GONE);
		tv_title_1.setText(R.string.title_category);
		tv_title_2.setText(R.string.product_top_tab_4);
		tv_title_1.setOnClickListener(this);
		tv_title_2.setOnClickListener(this);
		iv_top_left.setOnClickListener(this);
		
		initViewPager();
		setAdapter();
		getSVDatas();
	}
	
	private void initViewPager() {
		// 添加布局1
		FrameLayout frameLayout1 = new FrameLayout(this);
		View view1 = FrameLayout.inflate(mContext, R.layout.layout_category_viewpager_1, null);
		lv_left = (ListView) view1.findViewById(R.id.category_left_lv);
		gv_right = (GridView) view1.findViewById(R.id.category_right_gv);
		frameLayout1.addView(view1);
		viewLists.add(frameLayout1);
		// 添加布局2
		FrameLayout frameLayout2 = new FrameLayout(this);
		View view2 = FrameLayout.inflate(mContext, R.layout.layout_category_viewpager_2, null);
		lv_brand = (ListView) view2.findViewById(R.id.category_brand_lv);
		frameLayout2.addView(view2);
		viewLists.add(frameLayout2);
		
		mViewPager.setAdapter(new PagerAdapter() {
			// 创建
			@Override
			public Object instantiateItem(View container, int position) {
				View layout = viewLists.get(position % viewLists.size());
				mViewPager.addView(layout);
				return layout;
			}

			// 销毁
			@Override
			public void destroyItem(View container, int position, Object object) {
				View layout = viewLists.get(position % viewLists.size());
				mViewPager.removeView(layout);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return viewLists.size();
			}

		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(final int arg0) {
				mCurrentItem = arg0 % viewLists.size();
				changeTitleStatus(mCurrentItem);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mViewPager.setCurrentItem(mCurrentItem);
	}

	public void initIndexDisplayFragment() {
		BrandIndexDisplayAdapter adapter = new BrandIndexDisplayAdapter(mContext);
		adapter.setOnIndexDisplayItemClick(new OnIndexDisplayItemClick() {

			@Override
			public void onIndexDisplayItemClick(IndexDisplay indexDisplay) {
				if (indexDisplay != null) {
					BrandEntity brand = (BrandEntity) indexDisplay;
					startShowListHeadActivity(StringUtil.getInteger(brand.getBrandId()), brand.getName());
				}
			}

		});
		indexDisplayFragment = IndexDisplayFragment.newInstance();
		indexDisplayFragment.setDataList(IndexDisplayTool.buildIndexListChineseAndEng(this, brandList, hm_index));
		indexDisplayFragment.setAdapter(adapter);
		indexDisplayFragment.setIndexHashMap(hm_index);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.category_brand_rl, indexDisplayFragment);
		ft.commit();
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		if (AppApplication.loadSVData_category) {
			startAnimation();
			request(AppConfig.REQUEST_SV_GET_CATEGORY_LIST_CODE);
		}else {
			getDBDatas();
		}
		getSVBrandDatas();
	}
	
	/**
	 * 加载所有品牌列表数据
	 */
	private void getSVBrandDatas() {
		request(AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE);
	}

	/**
	 * 从本地数据库加载数据
	 */
	private void getDBDatas() {
		AppApplication.loadDBData = true;
		request(AppConfig.REQUEST_DB_GET_CATEGORY_LIST_CODE);
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_left_Adapter = new CategoryLeftListAdapter(mContext, lv_lists, new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				index = position;
				lv_left_Adapter.updateAdapter(lv_lists, index);
				dataType = lv_lists.get(index).getTypeId();
				getDBDatas();
			}
		});
		lv_left.setAdapter(lv_left_Adapter);
		lv_left.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		
		lv_right_Adapter = new CategoryRightListAdapter(mContext, gv_lists, new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				if (entity != null) {
					CategoryListEntity itemEn = (CategoryListEntity) entity;
					startShowListHeadActivity(itemEn.getTypeId(), itemEn.getName());
				}
			}
		});
		lv_brand.setAdapter(lv_right_Adapter);
		lv_brand.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		
		gv_Adapter = new CategoryGridAdapter(mContext, gv_lists, new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				CategoryListEntity data = (CategoryListEntity) entity;
				if (data != null) {
					Intent intent = new Intent(mContext, ProductListActivity.class);
					intent.putExtra("typeId", data.getTypeId());
					intent.putExtra("typeName", data.getName());
					startActivity(intent);
				}
			}
		}, gv_right);
		gv_right.setAdapter(gv_Adapter);
	}

	private void startShowListHeadActivity(int brandId, String brandName) {
		Intent intent = new Intent(mContext, ShowListHeadActivity.class);
		intent.putExtra("pageCode", ShowListHeadActivity.PAGE_ROOT_CODE_1);
		intent.putExtra("brandId", brandId);
		intent.putExtra("brandName", brandName);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_two_title_iv_left:
			finish();
			break;
		case R.id.top_two_title_tv_title_1:
			changeTitleStatus(0);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.top_two_title_tv_title_2:
			changeTitleStatus(1);
			mViewPager.setCurrentItem(1);
			break;
		}
	}
	
	private void changeTitleStatus(int index){
		switch (index) {
		case 0:
			tv_title_1.setTextColor(getResources().getColor(R.color.text_color_white));
			tv_title_1.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_black_4));
			tv_title_2.setTextColor(getResources().getColor(R.color.text_color_title));
			tv_title_2.setBackground(getResources().getDrawable(R.drawable.shape_frame_tv_white_black_4));
			break;
		case 1:
			tv_title_1.setTextColor(getResources().getColor(R.color.text_color_title));
			tv_title_1.setBackground(getResources().getDrawable(R.drawable.shape_frame_tv_white_black_4));
			tv_title_2.setTextColor(getResources().getColor(R.color.text_color_white));
			tv_title_2.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_black_4));
			break;
		}
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
		instance = null;
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_DB_GET_CATEGORY_LIST_CODE:
			List<CategoryListEntity> lvs = dbs.getListData(dataType); 
			if (lvs != null) {
				if (dataType == 0) { //父级
					lv_lists.clear();
					lv_lists.addAll(lvs);
				}else { //子级
					gv_lists.clear();
					gv_lists.addAll(lvs);
				}
			}
			return lvs;
		case AppConfig.REQUEST_SV_GET_CATEGORY_LIST_CODE:
			mainEn = null;
			mainEn = sc.getCategoryListDatas();
			if (mainEn != null && mainEn.getMainLists() != null) {
				lv_lists.addAll(mainEn.getMainLists());
				gv_lists.addAll(lv_lists.get(0).getChildLists());
				CategoryListEntity fEn = null;
				CategoryListEntity cEn = null;
				List<CategoryListEntity> lists = null;
				for (int i = 0; i < lv_lists.size(); i++) {
					fEn = lv_lists.get(i);
					dbs.update(fEn, 0); //更新父级分类
					
					lists = fEn.getChildLists();
					for (int j = 0; j < lists.size(); j++) {
						cEn = lists.get(j);
						dbs.update(cEn, fEn.getTypeId()); //更新子级分类
					}
				}
			}
			return mainEn;
		case AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE:
			brandsEn = null;
			brandsEn = sc.getCategoryBrandDatas();
			return brandsEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_DB_GET_CATEGORY_LIST_CODE:
			if (dataType == 0) { //父级
				lv_left_Adapter.updateAdapter(lv_lists, index);
				dataType = lv_lists.get(index).getTypeId();
				getDBDatas();
			}else { //子级
				gv_Adapter.updateAdapter(gv_lists);
			}
			break;
		case AppConfig.REQUEST_SV_GET_CATEGORY_LIST_CODE:
			if (lv_lists.size() > 0) {
				AppApplication.loadSVData_category = false;
				lv_left_Adapter.updateAdapter(lv_lists, 0);
				gv_Adapter.updateAdapter(gv_lists);
				dataType = lv_lists.get(index).getTypeId();
			}else {
				AppApplication.loadSVData_category = true;
				showServerBusy();
			}
			stopAnimation();
			break;
		case AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE:
			if (brandsEn != null && brandsEn.getBrandLists() != null) {
				brandList.addAll(brandsEn.getBrandLists());
				initIndexDisplayFragment();
				//lv_right_Adapter.updateAdapter(brandsEn.getChildLists());
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
		stopAnimation();
	}
	
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		AppApplication.loadDBData = false;
	}
    
}
