package com.spshop.stylistpark.activity.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.common.MyWebViewActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.SpecialAdapter;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.List;

public class ChildFragmentThree2 extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentThree2";
	public static final int TYPE_1 = 3;  //全部
	public static final int TYPE_2 = 5;  //视频
	public static final int TYPE_3 = 7;  //专题

	private Context mContext;
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();
	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //默认列表加载页
	private int page_type_2 = 1;  //视频列表加载页
	private int page_type_3 = 1;  //专题列表加载页
	private int topType = TYPE_1; //Top标记
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int countTotal = 0; //数集总数量
	private int total_1, total_2, total_3;
	private boolean isLoadOk = true; //加载数据控制符

	private RelativeLayout rl_loading;
	private FrameLayout rl_no_data;
	private ImageView iv_top_left, iv_to_top;
	private TextView tv_title_1, tv_title_2, tv_title_3;
	private TextView tv_title, tv_no_data;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private SpecialAdapter lv_adapter;

	private ThemeEntity mainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ThemeEntity> lv_show = new ArrayList<ThemeEntity>();
	private List<ThemeEntity> lv_all_1 = new ArrayList<ThemeEntity>();
	private List<ThemeEntity> lv_all_2 = new ArrayList<ThemeEntity>();
	private List<ThemeEntity> lv_all_3 = new ArrayList<ThemeEntity>();
	private SparseBooleanArray sa_all_1 = new SparseBooleanArray();
	private SparseBooleanArray sa_all_2 = new SparseBooleanArray();
	private SparseBooleanArray sa_all_3 = new SparseBooleanArray();

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
		atm = AsyncTaskManager.getInstance(mContext);

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_three_2, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		iv_top_left = (ImageView) view.findViewById(R.id.top_three_title_iv_left);
		tv_title_1 = (TextView) view.findViewById(R.id.top_three_title_tv_title_1);
		tv_title_2 = (TextView) view.findViewById(R.id.top_three_title_tv_title_2);
		tv_title_3 = (TextView) view.findViewById(R.id.top_three_title_tv_title_3);
		refresh_lv = (PullToRefreshListView) view.findViewById(R.id.fragment_three_2_refresh_lv);
		rl_loading = (RelativeLayout) view.findViewById(R.id.loading_anim_large_ll_main);
		rl_no_data = (FrameLayout) view.findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) view.findViewById(R.id.loading_no_data_tv_show);
		iv_to_top = (ImageView) view.findViewById(R.id.fragment_three_2_iv_to_top);
	}

	private void initView() {
		iv_top_left.setVisibility(View.GONE);
		iv_to_top.setOnClickListener(this);
		tv_title_1.setText(R.string.events_top_tab_1);
		tv_title_2.setText(R.string.events_top_tab_2);
		tv_title_3.setText(R.string.events_top_tab_3);
		tv_title_1.setOnClickListener(this);
		tv_title_2.setOnClickListener(this);
		tv_title_3.setOnClickListener(this);
		initListView();
		setAdapter();
		getSVDatas();
	}

	private void initListView() {
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnScrollListener(new MyRefreshScrollListener());
		refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				refreshSVDatas();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 加载更多
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
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_callback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				if (entity == null) return;
				ThemeEntity selectEn = (ThemeEntity) entity;
				// 创建分享数据
				int uid = StringUtil.getInteger(UserManager.getInstance().getUserId());
				ShareEntity shareEn = new ShareEntity();
				shareEn.setTitle(selectEn.getTitle());
				shareEn.setText(selectEn.getTitle());
				shareEn.setUrl(AppConfig.URL_COMMON_ARTICLE_SHARE_URL + "?id=" + selectEn.getId() + "&uid=" + uid);
				shareEn.setImageUrl(AppConfig.ENVIRONMENT_PRESENT_IMG_APP + selectEn.getImgUrl());
				// 跳转至WebView
				Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
				intent.putExtra("shareEn", shareEn);
				intent.putExtra("postId", selectEn.getId());
				intent.putExtra("title", selectEn.getTitle());
				intent.putExtra("lodUrl", AppConfig.URL_COMMON_ARTICLE_URL + "?id=" + selectEn.getId());
				intent.putExtra("vdoUrl", selectEn.getVdoUrl());
				startActivity(intent);
				// 刷新阅读数
				int newNum = selectEn.getClickNum() + 1;
				switch (type) {
					case SpecialAdapter.TYPE_SELECT_LEFT:
						((ThemeEntity) lv_show_two.get(position).getLeftEn()).setClickNum(newNum);
						break;
					case SpecialAdapter.TYPE_SELECT_RIGHT:
						((ThemeEntity) lv_show_two.get(position).getRightEn()).setClickNum(newNum);
						break;
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						lv_adapter.updateAdapter(lv_show_two);
					}
				}, 1000);
			}
		};
		lv_adapter = new SpecialAdapter(mContext, lv_show_two, lv_callback);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		loadType = 1;
		current_Page = 1;
		startAnimation();
		setLoadMoreDate();
		requestProductLists();
	}

	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
		switch (topType) {
			case TYPE_1: //全部
				current_Page = page_type_1;
				break;
			case TYPE_2: //视频
				current_Page = page_type_2;
				break;
			case TYPE_3: //专题
				current_Page = page_type_3;
				break;
		}
		requestProductLists();
	}

	/**
	 * 下拉刷新数据
	 */
	private void refreshSVDatas() {
		loadType = 0;
		current_Page = 1;
		requestProductLists();
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		if (!isLoadOk) { //加载频率控制
			if (loadType == 0) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						refresh_lv.onPullDownRefreshComplete();
					}
				}, 1000);
			}
			return;
		}
		isLoadOk = false;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				atm.request(AppConfig.REQUEST_SV_GET_SPECIAL_LIST_CODE, ChildFragmentThree2.this);
			}
		}, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.top_three_title_tv_title_1: //全部
				if (topType == TYPE_1) return;
				changeTitleStatus(0);
				topType = TYPE_1;
				if (lv_all_1 != null && lv_all_1.size() > 0) {
					addOldListDatas(lv_all_1, page_type_1, total_1);
				}else {
					page_type_1 = 1;
					total_1 = 0;
					getSVDatas();
				}
				break;
			case R.id.top_three_title_tv_title_2: //视频
				if (topType == TYPE_2) return;
				changeTitleStatus(1);
				topType = TYPE_2;
				if (lv_all_2 != null && lv_all_2.size() > 0) {
					addOldListDatas(lv_all_2, page_type_2, total_2);
				}else {
					page_type_2 = 1;
					total_2 = 0;
					getSVDatas();
				}
				break;
			case R.id.top_three_title_tv_title_3: //专题
				if (topType == TYPE_3) return;
				changeTitleStatus(2);
				topType = TYPE_3;
				if (lv_all_3 != null && lv_all_3.size() > 0) {
					addOldListDatas(lv_all_3, page_type_3, total_3);
				}else {
					page_type_3 = 1;
					total_3 = 0;
					getSVDatas();
				}
				break;
			case R.id.fragment_three_2_iv_to_top: //回顶
				toTop();
				break;
		}
	}

	private void changeTitleStatus(int index){
		switch (index) {
			case 0:
				tv_title_1.setTextColor(getResources().getColor(R.color.text_color_app_bar));
				tv_title_2.setTextColor(getResources().getColor(R.color.text_color_assist));
				tv_title_3.setTextColor(getResources().getColor(R.color.text_color_assist));
				break;
			case 1:
				tv_title_1.setTextColor(getResources().getColor(R.color.text_color_assist));
				tv_title_2.setTextColor(getResources().getColor(R.color.text_color_app_bar));
				tv_title_3.setTextColor(getResources().getColor(R.color.text_color_assist));
				break;
			case 2:
				tv_title_1.setTextColor(getResources().getColor(R.color.text_color_assist));
				tv_title_2.setTextColor(getResources().getColor(R.color.text_color_assist));
				tv_title_3.setTextColor(getResources().getColor(R.color.text_color_app_bar));
				break;
		}
	}

	/**
	 * 展示已缓存的数据并至顶
	 */
	private void addOldListDatas(List<ThemeEntity> oldLists, int oldPage, int oldTotal) {
		addAllShow(oldLists);
		current_Page = oldPage;
		countTotal = oldTotal;
		myUpdateAdapter();
		if (current_Page != 1) {
			toTop();
		}
		setLoadMoreDate();
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		StatService.onResume(mContext);
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(mContext);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	class MyRefreshScrollListener implements AbsListView.OnScrollListener {

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
			case AppConfig.REQUEST_SV_GET_SPECIAL_LIST_CODE:
				mainEn = null;
				mainEn = sc.getSpecialListDatas(topType, current_Page, Page_Count);
				return mainEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SPECIAL_LIST_CODE:
				if (mainEn != null) {
					if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
						int total = mainEn.getCountTotal();
						List<ThemeEntity> lists = mainEn.getMainLists();
						if (lists != null && lists.size() > 0) {
							switch (topType) {
								case TYPE_1:
									if (loadType == 0) { //下拉
										updEntity(total, total_1, lists, lv_all_1, sa_all_1);
										//page_type_1 = 2;
									}else {
										addEntity(lv_all_1, lists, sa_all_1);
										page_type_1++;
									}
									total_1 = total;
									break;
								case TYPE_2:
									if (loadType == 0) { //下拉
										updEntity(total, total_2, lists, lv_all_2, sa_all_2);
										//page_type_2 = 2;
									}else {
										addEntity(lv_all_2, lists, sa_all_2);
										page_type_2++;
									}
									total_2 = total;
									break;
								case TYPE_3:
									if (loadType == 0) { //下拉
										updEntity(total, total_3, lists, lv_all_3, sa_all_3);
										//page_type_3 = 2;
									}else {
										addEntity(lv_all_3, lists, sa_all_3);
										page_type_3++;
									}
									total_3 = total;
									break;
							}
							countTotal = total;
							myUpdateAdapter();
						}else {
							loadFailHandle();
						}
					}else {
						loadFailHandle();
					}
				}else {
					loadFailHandle();
					CommonTools.showToast(getString(R.string.toast_server_busy), 1000);
				}
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		loadFailHandle();
		CommonTools.showToast(String.valueOf(result), 1000);
	}

	private void loadFailHandle() {
		switch (topType) {
			case TYPE_1:
				addAllShow(lv_all_1);
				break;
			case TYPE_2:
				addAllShow(lv_all_2);
				break;
			case TYPE_3:
				addAllShow(lv_all_3);
				break;
		}
		myUpdateAdapter();
	}

	/**
	 * 刷新数集
	 */
	private void updEntity(int newTotal, int oldTotal, List<ThemeEntity> newDatas,
						   List<ThemeEntity> oldDatas, SparseBooleanArray oldMap) {
		if (oldTotal < newTotal) {
			List<ThemeEntity> datas = new ArrayList<ThemeEntity>();
			datas.addAll(oldDatas);
			oldDatas.clear();

			ThemeEntity newEn, oldEn;
			int dataId = 0;
			for (int i = 0; i < (newTotal - oldTotal); i++) {
				newEn = newDatas.get(i);
				if (newEn != null) {
					dataId = newEn.getId();
					if (dataId != 0 && oldMap.indexOfKey(dataId) < 0) {
						// 添加至顶层
						oldDatas.add(newEn);
						oldMap.put(dataId, true);
						// 移除最底层
						oldEn = datas.remove(datas.size()-1);
						if (oldEn != null) {
							oldMap.delete(oldEn.getId());
						}
					}
				}
			}
			oldDatas.addAll(datas);
			addAllShow(oldDatas);
			setLoadMoreDate();
		}
		/*oldDatas.clear();
		oldMap.clear();
		addEntity(oldDatas, newDatas, oldMap);
		setLoadMoreDate();*/
	}

	/**
	 * 数据去重函数
	 */
	private void addEntity(List<ThemeEntity> oldDatas, List<ThemeEntity> newDatas, SparseBooleanArray oldMap) {
		ThemeEntity entity = null;
		int dataId = 0;
		for (int i = 0; i < newDatas.size(); i++) {
			entity = newDatas.get(i);
			if (entity != null) {
				dataId = entity.getId();
				if (dataId != 0 && oldMap.indexOfKey(dataId) < 0) {
					oldDatas.add(entity);
					oldMap.put(dataId, true);
				}
			}
		}
		addAllShow(oldDatas);
	}

	private void addAllShow(List<ThemeEntity> showLists) {
		lv_show.clear();
		lv_show.addAll(showLists);
	}

	private void myUpdateAdapter() {
		lv_show_two.clear();
		ListShowTwoEntity lstEn = null;
		for (int i = 0; i < lv_show.size(); i++) {
			ThemeEntity en = lv_show.get(i);
			if (i%2 == 0) {
				lstEn = new ListShowTwoEntity();
				lstEn.setLeftEn(en);
				if (i+1 < lv_show.size()) {
					lstEn.setRightEn(lv_show.get(i+1));
				}
				lv_show_two.add(lstEn);
			}
		}
		lv_adapter.updateAdapter(lv_show_two);
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
		rl_no_data.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		isLoadOk = true;
		rl_loading.setVisibility(View.GONE);
		switch (loadType) {
			case 0: //下拉刷新
				refresh_lv.onPullDownRefreshComplete();
				break;
			case 1: //加载更多
				refresh_lv.onPullUpRefreshComplete();
				break;
		}
		if (lv_show.size() == 0) {
			tv_no_data.setText(getString(R.string.events_no_data));
			rl_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			rl_no_data.setVisibility(View.GONE);
			refresh_lv.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 判定是否停止加载翻页数据
	 */
	private boolean isStop(){
		return lv_show.size() > 0 && lv_show.size() == countTotal;
	}

	/**
	 * 设置允许加载更多
	 */
	private void setLoadMoreDate() {
		refresh_lv.setHasMoreData(true);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

