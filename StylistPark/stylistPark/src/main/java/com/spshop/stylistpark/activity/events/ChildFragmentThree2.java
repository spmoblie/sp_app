package com.spshop.stylistpark.activity.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.SpecialAdapter;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildFragmentThree2 extends Fragment implements OnDataListener {

	private static final String TAG = "ChildFragmentThree2";
	public static ChildFragmentThree2 instance = null;

	private Context mContext;
	private SharedPreferences shared;
	private AsyncTaskManager atm;
	protected ServiceContext sc = ServiceContext.getServiceContext();
	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //默认列表加载页
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private boolean isLoadOk = true; //加载数据控制符

	private RelativeLayout rl_top_screen, rl_loading;
	private FrameLayout rl_no_data;
	private ImageView iv_to_top;
	private TextView tv_no_data;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private SpecialAdapter lv_adapter;

	private int countTotal = 0; //数集总数量
	private ThemeEntity mainEn;
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ThemeEntity> lv_show = new ArrayList<ThemeEntity>();
	private List<ThemeEntity> lv_all = new ArrayList<ThemeEntity>();
	private HashMap<Integer, Boolean> hm_all = new HashMap<Integer, Boolean>();

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
		atm = AsyncTaskManager.getInstance(mContext);
		// 初始化偏好设置
		shared = AppApplication.getSharedPreferences();
		
		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_three_2, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(mContext, e);
		}
		return view;
	}

	private void findViewById(View view) {
		refresh_lv = (PullToRefreshListView) view.findViewById(R.id.fragment_three_2_refresh_lv);
		rl_loading = (RelativeLayout) view.findViewById(R.id.loading_anim_large_ll_main);
		rl_no_data = (FrameLayout) view.findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) view.findViewById(R.id.loading_no_data_tv_show);
		iv_to_top = (ImageView) view.findViewById(R.id.fragment_three_2_iv_to_top);
	}

	private void initView() {
		setAdapter();
		getSVDatas();
	}

	private void initListView() {
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
				if (!isStop()) {
					loadSVDatas();
				}else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							refresh_lv.onPullUpRefreshComplete();
							refresh_lv.setHasMoreData(false);
						}
					}, 1000);
				}
			}
		});
		mListView = refresh_lv.getRefreshableView();
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_callback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {

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
		requestProductLists();
	}

	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
		current_Page = page_type_1;
		requestProductLists();
	}

	/**
	 * 加载下拉刷新数据
	 */
	private void refreshSVDatas() {
		if (!isLoadOk) return; //加载频率控制
		loadType = 0;
		current_Page = 1;
		requestProductLists();
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				atm.request(AppConfig.REQUEST_SV_GET_SPECIAL_LIST_CODE, instance);
			}
		}, 1000);
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

	@Override
	public Object doInBackground(int requestCode) throws Exception{
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SPECIAL_LIST_CODE:
				mainEn = null;
				mainEn = sc.getSpecialListDatas(current_Page, Page_Count);
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
							addEntity(lv_all, lists, hm_all);
							current_Page++;
							myUpdateAdapter();
							if (loadType == 0) { //下拉
								updEntity(total, countTotal, lists, lv_all, hm_all);
								//page_type_1 = 1;
							}else {
								addEntity(lv_all, lists, hm_all);
								page_type_1++;
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
					//showServerBusy();
				}
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		loadFailHandle();
		CommonTools.showToast(mContext, String.valueOf(result), 1000);
	}

	private void loadFailHandle() {
		addAllShow(lv_all);
		myUpdateAdapter();
	}

	/**
	 * 刷新数集
	 */
	private void updEntity(int newTotal, int oldTotal, List<ThemeEntity> newDatas,
						   List<ThemeEntity> oldDatas, HashMap<Integer, Boolean> oldMap) {
		if (oldTotal < newTotal) {
			List<ThemeEntity> datas = new ArrayList<ThemeEntity>();
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
		}
		/*oldDatas.clear();
		oldMap.clear();
		addEntity(oldDatas, newDatas, oldMap);*/
	}

	/**
	 * 数据去重函数
	 */
	private void addEntity(List<ThemeEntity> oldDatas, List<ThemeEntity> newDatas, HashMap<Integer, Boolean> hashMap) {
		ThemeEntity entity = null;
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
		refresh_lv.onPullUpRefreshComplete();
		rl_loading.setVisibility(View.GONE);
		if (lv_show.size() == 0) {
			tv_no_data.setText(getString(R.string.member_no_member));
			rl_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			rl_no_data.setVisibility(View.GONE);
			refresh_lv.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 数量小于一页时停止加载翻页数据
	 */
	private boolean isStop(){
		return lv_show.size() < Page_Count || lv_show.size() == countTotal;
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

