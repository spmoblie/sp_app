package com.spshop.stylistpark.activity.sort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.common.IndexDisplayFragment;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.activity.home.ProductListActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.BrandIndexDisplayAdapter;
import com.spshop.stylistpark.adapter.SortGridAdapter;
import com.spshop.stylistpark.adapter.SortLeftListAdapter;
import com.spshop.stylistpark.adapter.IndexDisplayAdapter;
import com.spshop.stylistpark.db.SortDBService;
import com.spshop.stylistpark.dialog.LoadDialog;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.SortListEntity;
import com.spshop.stylistpark.entity.IndexDisplay;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.IndexDisplayTool;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ChildFragmentTwo extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentTwo";

	private Context mContext;
	private AsyncTaskManager atm;
	private SharedPreferences shared;
	private ServiceContext sc = ServiceContext.getServiceContext();

	private ViewPager mViewPager;
	private RelativeLayout rl_top_left;
	private ImageView iv_lines_3;
	private TextView tv_title_1, tv_title_2, tv_title_3;
	private ListView lv_left;
	private GridView gv_right;
	private SortLeftListAdapter lv_left_Adapter;
	private SortGridAdapter gv_Adapter;
	private SortListEntity brandsEn;
	private SortDBService dbs;

	private int index = 0;
	private int dataType = 0; //区分父级、子级分类的标记(0表示父级/1表示子级)
	private int mCurrentItem;
	private boolean isLeftShow = true;
	private ArrayList<View> viewLists = new ArrayList<View>();
	private List<SortListEntity> lv_lists = new ArrayList<SortListEntity>();
	private List<SortListEntity> gv_lists = new ArrayList<SortListEntity>();
	private List<BrandEntity> brandList = new ArrayList<BrandEntity>();
	private ArrayMap<String, Integer> am_index = new ArrayMap<String, Integer>();

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
		mContext = getActivity();
		shared = AppApplication.getSharedPreferences();
		atm = AsyncTaskManager.getInstance(mContext);
		dbs = SortDBService.getInstance(mContext);

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_two, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		mViewPager = (ViewPager) view.findViewById(R.id.fragment_two_viewpager);
		rl_top_left = (RelativeLayout) view.findViewById(R.id.top_three_title_rl_left);
		tv_title_1 = (TextView) view.findViewById(R.id.top_three_title_tv_title_1);
		tv_title_2 = (TextView) view.findViewById(R.id.top_three_title_tv_title_2);
		tv_title_3 = (TextView) view.findViewById(R.id.top_three_title_tv_title_3);
		iv_lines_3 = (ImageView) view.findViewById(R.id.top_three_title_iv_lines_3);
	}

	private void initView() {
		rl_top_left.setVisibility(View.GONE);
		tv_title_1.setText(R.string.title_sort);
		tv_title_1.setOnClickListener(this);
		tv_title_2.setText(R.string.product_top_tab_4);
		tv_title_2.setOnClickListener(this);
		tv_title_3.setVisibility(View.GONE);
		iv_lines_3.setVisibility(View.GONE);

		initViewPager();
		setAdapter();
		getSVDatas();
	}

	private void initViewPager() {
		// 添加布局1
		FrameLayout frameLayout1 = new FrameLayout(mContext);
		View view1 = FrameLayout.inflate(mContext, R.layout.layout_sort_viewpager_1, null);
		lv_left = (ListView) view1.findViewById(R.id.sort_left_lv);
		gv_right = (GridView) view1.findViewById(R.id.sort_right_gv);
		frameLayout1.addView(view1);
		viewLists.add(frameLayout1);
		// 添加布局2
		FrameLayout frameLayout2 = new FrameLayout(mContext);
		View view2 = FrameLayout.inflate(mContext, R.layout.layout_sort_viewpager_2, null);
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
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
		FragmentManager fm = getActivity().getSupportFragmentManager();
		IndexDisplayFragment idf = (IndexDisplayFragment) fm.findFragmentById(R.id.sort_brand_fl);
		if (idf == null || idf.isRemoving()) {
			BrandIndexDisplayAdapter adapter = new BrandIndexDisplayAdapter(getActivity());
			adapter.setOnIndexDisplayItemClick(new IndexDisplayAdapter.OnIndexDisplayItemClick() {

				@Override
				public void onIndexDisplayItemClick(IndexDisplay indexDisplay) {
					if (indexDisplay != null) {
						BrandEntity brand = (BrandEntity) indexDisplay;
						startShowListHeadActivity(StringUtil.getInteger(brand.getBrandId()));
					}
				}

			});
			idf = IndexDisplayFragment.newInstance();
			idf.setDataList(IndexDisplayTool.buildIndexListChineseAndEng(mContext, brandList, am_index));
			idf.setAdapter(adapter);
			idf.setIndexHashMap(am_index);

			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.sort_brand_fl, idf).commitAllowingStateLoss();
		}
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		if (shared.getBoolean(AppConfig.KEY_LOAD_SORT_DATA, true)) {
			getSVSortDatas();
			getSVBrandDatas();
		}else {
			startAnimation();
			getDBDatas();
		}
	}

	/**
	 * 加载所有分类数据
	 */
	private void getSVSortDatas() {
		startAnimation();
		atm.request(AppConfig.REQUEST_SV_GET_SORT_LIST_CODE, this);
	}

	/**
	 * 加载所有品牌列表数据
	 */
	private void getSVBrandDatas() {
		atm.request(AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE, this);
	}

	/**
	 * 从本地数据库加载数据
	 */
	private void getDBDatas() {
		AppApplication.loadDBData = true;
		atm.request(AppConfig.REQUEST_DB_GET_SORT_LIST_CODE, this);
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_left_Adapter = new SortLeftListAdapter(mContext, lv_lists, new AdapterCallback() {

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

		gv_Adapter = new SortGridAdapter(mContext, gv_lists, new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				SortListEntity data = (SortListEntity) entity;
				if (data != null) {
					Intent intent = new Intent(mContext, ProductListActivity.class);
					intent.putExtra("typeId", data.getTypeId());
					startActivity(intent);
				}
			}
		}, gv_right);
		gv_right.setAdapter(gv_Adapter);
	}

	private void startShowListHeadActivity(int brandId) {
		Intent intent = new Intent(mContext, ShowListHeadActivity.class);
		intent.putExtra("pageCode", ShowListHeadActivity.PAGE_ROOT_CODE_1);
		intent.putExtra("brandId", brandId);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.top_three_title_tv_title_1:
				changeTitleStatus(0);
				mViewPager.setCurrentItem(0);
				break;
			case R.id.top_three_title_tv_title_2:
				changeTitleStatus(1);
				mViewPager.setCurrentItem(1);
				break;
		}
	}

	private void changeTitleStatus(int index){
		switch (index) {
			case 0:
				tv_title_1.setTextColor(getResources().getColor(R.color.tv_color_status));
				tv_title_2.setTextColor(getResources().getColor(R.color.tv_color_change));
				break;
			case 1:
				tv_title_1.setTextColor(getResources().getColor(R.color.tv_color_change));
				tv_title_2.setTextColor(getResources().getColor(R.color.tv_color_status));
				break;
		}
	}

	@Override
	public void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
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
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception{
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		BaseEntity baseEn;
		switch (requestCode) {
			case AppConfig.REQUEST_DB_GET_SORT_LIST_CODE:
				List<SortListEntity> lvs = dbs.getListData(dataType);
				if (lvs != null) {
					if (dataType == 0) { //父级
						lv_lists.clear();
						lv_lists.addAll(lvs);
					}else { //子级
						gv_lists.clear();
						gv_lists.addAll(lvs);
					}
				}
				Object obj = FileManager.readFileSaveObject(AppConfig.brandsFileName, true);
				if (obj != null) {
					brandsEn = (SortListEntity) obj;
				}
				return obj;
			case AppConfig.REQUEST_SV_GET_SORT_LIST_CODE:
				params.add(new MyNameValuePair("app", "menu"));
				baseEn = sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_SORT_LIST_CODE, uri, params, HttpUtil.METHOD_GET);
				if (baseEn != null) {
					SortListEntity mainEn = (SortListEntity) baseEn;
					if (mainEn.getMainLists() != null && mainEn.getMainLists().size() > 0) {
						lv_lists.addAll(mainEn.getMainLists());
						gv_lists.addAll(lv_lists.get(0).getChildLists());
						SortListEntity fEn;
						SortListEntity cEn;
						List<SortListEntity> lists;
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
				}
				return baseEn;
			case AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE:
				params.add(new MyNameValuePair("app", "key"));
				params.add(new MyNameValuePair("cat_id", "0"));
				baseEn = sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE, uri, params, HttpUtil.METHOD_GET);
				if (baseEn != null) {
					brandsEn = (SortListEntity) baseEn;
					FileManager.writeFileSaveObject(AppConfig.brandsFileName, brandsEn, true);
				}
				return brandsEn;
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
			case AppConfig.REQUEST_DB_GET_SORT_LIST_CODE:
				AppApplication.loadDBData = false;
				if (dataType == 0) { //父级
					if (lv_lists.size() > 0) {
						if (index >= 0 && index < lv_lists.size()) {
							dataType = lv_lists.get(index).getTypeId();
							getDBDatas();
						}
					} else {
						getSVSortDatas();
					}
					if (brandsEn != null && brandsEn.getBrandLists() != null) {
						setBrandDatas();
					} else {
						getSVBrandDatas();
					}
				}else { //子级
					if (isLeftShow) {
						lv_left_Adapter.updateAdapter(lv_lists, index);
						isLeftShow = false;
						stopAnimation();
					}
					gv_Adapter.updateAdapter(gv_lists);
				}
				break;
			case AppConfig.REQUEST_SV_GET_SORT_LIST_CODE:
				if (lv_lists != null && lv_lists.size() > 0) {
					lv_left_Adapter.updateAdapter(lv_lists, 0);
					gv_Adapter.updateAdapter(gv_lists);
					dataType = lv_lists.get(index).getTypeId();
					shared.edit().putBoolean(AppConfig.KEY_LOAD_SORT_DATA, false).apply();
				}else {
					CommonTools.showToast(getString(R.string.toast_server_busy), 1000);
				}
				stopAnimation();
				break;
			case AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE:
				if (brandsEn != null && brandsEn.getBrandLists() != null) {
					setBrandDatas();
				}
				break;
		}
	}

	private void setBrandDatas() {
		brandList.addAll(brandsEn.getBrandLists());
		initIndexDisplayFragment();
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		stopAnimation();
		if (getActivity() == null) return;
		CommonTools.showToast(String.valueOf(result), 1000);
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
		LoadDialog.hidden();
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

