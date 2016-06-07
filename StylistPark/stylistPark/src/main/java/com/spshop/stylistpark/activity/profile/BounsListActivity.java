package com.spshop.stylistpark.activity.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.cart.PostOrderActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.BounsListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.BounsEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;
import com.tencent.stat.StatService;

/**
 * "红包列表"Activity
 */
public class BounsListActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "BounsListActivity";
	public static BounsListActivity instance = null;
	public boolean isUpdateAllData = false; 
	public static final int TYPE_1 = 0;  //全部
	public static final int TYPE_2 = 1;  //未使用
	public static final int TYPE_3 = 2;  //已过期
	public static final int TYPE_4 = 3;  //已使用
	
	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //全部加载页
	private int page_type_2 = 1;  //未使用加载页
	private int page_type_3 = 1;  //已过期加载页
	private int page_type_4 = 1;  //已使用加载页
	private int countTotal = 0; //数集总数量
	private int topType = TYPE_1; //Top标记
	private boolean isFrist = true; //识别是否第一次打开页面
	private boolean isLoadOk = true; //加载数据控制符
	private boolean isLogined, isSuccess;
	private String rootStr, bounsId, bounsStr, noDataShowStr;
	
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private RelativeLayout rl_top_screen, rl_loading;
	private LinearLayout ll_radio_main;
	private FrameLayout rl_no_data;
	private ImageView iv_to_top;
	private TextView tv_no_data;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private BounsListAdapter lv_adapter;
	
	private BounsEntity mainEn;
	private List<BounsEntity> lv_lists_show = new ArrayList<BounsEntity>();
	private List<BounsEntity> lv_lists_all_1 = new ArrayList<BounsEntity>();
	private List<BounsEntity> lv_lists_all_2 = new ArrayList<BounsEntity>();
	private List<BounsEntity> lv_lists_all_3 = new ArrayList<BounsEntity>();
	private List<BounsEntity> lv_lists_all_4 = new ArrayList<BounsEntity>();
	private HashMap<String, Boolean> hm_all_1 = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> hm_all_2 = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> hm_all_3 = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> hm_all_4 = new HashMap<String, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_radio_common);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		instance = this;
		topType = getIntent().getExtras().getInt("topType", TYPE_1);
		rootStr = getIntent().getExtras().getString("root");
		bounsId = getIntent().getExtras().getString("bounsId");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		ll_radio_main = (LinearLayout) findViewById(R.id.topbar_radio_ll_main);
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		rl_top_screen = (RelativeLayout) findViewById(R.id.topbar_radio_rl_screen);
		rl_loading = (RelativeLayout) findViewById(R.id.loading_anim_large_ll_main);
		rl_no_data = (FrameLayout) findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) findViewById(R.id.loading_no_data_tv_show);
		refresh_lv = (PullToRefreshListView) findViewById(R.id.list_radio_common_refresh_lv);
		iv_to_top = (ImageView) findViewById(R.id.list_radio_common_iv_to_top);
	}

	private void initView() {
		bounsStr = getString(R.string.bouns_bouns);
		setTitle(R.string.profile_bonus);
		setBtnRight(getString(R.string.add));
		rl_loading.setVisibility(View.GONE);
		iv_to_top.setOnClickListener(this);
		
		initRaidoGroup();
		initListView();
		setDefaultRadioButton();
	}

	private void initRaidoGroup() {
		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(40, 0, 40, 0);
		btn_1.setLayoutParams(params);
		btn_2.setLayoutParams(params);
		btn_3.setLayoutParams(params);
		btn_4.setLayoutParams(params);
		
		btn_1.setText(getString(R.string.bouns_top_tab_1));
		btn_1.setOnClickListener(this);
		btn_2.setText(getString(R.string.bouns_top_tab_2));
		btn_2.setOnClickListener(this);
		btn_3.setText(getString(R.string.bouns_top_tab_3));
		btn_3.setOnClickListener(this);
		btn_4.setText(getString(R.string.bouns_top_tab_4));
		btn_4.setOnClickListener(this);
		rl_top_screen.setVisibility(View.GONE);
		ll_radio_main.setVisibility(View.GONE); //隐藏红包分类
	}

	private void initListView() {
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
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
		mListView.setDivider(null);
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_callback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				if ("PostOrderActivity".equals(rootStr) && entity != null) {
					postChooseBouns((BounsEntity) entity);
				}
			}
			
		};
		lv_adapter = new BounsListAdapter(mContext, lv_lists_show, bounsId, lv_callback);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	private void postChooseBouns(BounsEntity bounsEn) {
		if (bounsEn != null) {
			if (!StringUtil.isNull(bounsId) && bounsId.equals(bounsEn.getBounsId())) {
				bounsId = "0";
			}else {
				bounsId = bounsEn.getBounsId();
			}
			startAnimation();
			request(AppConfig.REQUEST_SV_POST_CHOOSE_BOUNS_CODE);
		}
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
		case TYPE_2:
			defaultBtn = btn_2;
			break;
		case TYPE_3:
			defaultBtn = btn_3;
			break;
		case TYPE_4:
			defaultBtn = btn_4;
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
		current_Page = 1;
		startAnimation();
		requestProductLists();
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		switch (topType) {
		case TYPE_1:
			current_Page = page_type_1;
			break;
		case TYPE_2:
			current_Page = page_type_2;
			break;
		case TYPE_3:
			current_Page = page_type_3;
			break;
		case TYPE_4:
			current_Page = page_type_4;
			break;
		}
		requestProductLists();
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		isFrist = false;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_BOUNS_LIST_CODE);
			}
		}, 1000);
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		startActivity(new Intent(mContext, AddBounsActivity.class));
	}

	@Override
	public void onClick(View v) {
		if (!isLoadOk) { //加载频率控制
			setDefaultRadioButton();
			return;
		}
		switch (v.getId()) {
		case R.id.topbar_radio_rb_1:
			if (!isFrist && topType == TYPE_1) return;
			topType = TYPE_1;
			noDataShowStr = bounsStr;
			if (lv_lists_all_1 != null && lv_lists_all_1.size() > 0) {
				addOldListDatas(lv_lists_all_1, page_type_1);
			}else {
				page_type_1 = 1;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_2:
			if (!isFrist && topType == TYPE_2) return;
			topType = TYPE_2;
			noDataShowStr = getString(R.string.bouns_top_tab_2) + bounsStr;
			if (lv_lists_all_2 != null && lv_lists_all_2.size() > 0) {
				addOldListDatas(lv_lists_all_2, page_type_2);
			}else {
				page_type_2 = 1;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_3:
			if (!isFrist && topType == TYPE_3) return;
			topType = TYPE_3;
			noDataShowStr = getString(R.string.bouns_top_tab_3) + bounsStr;
			if (lv_lists_all_3 != null && lv_lists_all_3.size() > 0) {
				addOldListDatas(lv_lists_all_3, page_type_3);
			}else {
				page_type_3 = 1;
				getSVDatas();
			}
			break;
		case R.id.topbar_radio_rb_4:
			if (!isFrist && topType == TYPE_4) return;
			topType = TYPE_4;
			noDataShowStr = getString(R.string.bouns_top_tab_4) + bounsStr;
			if (lv_lists_all_4 != null && lv_lists_all_4.size() > 0) {
				addOldListDatas(lv_lists_all_4, page_type_4);
			}else {
				page_type_4 = 1;
				getSVDatas();
			}
			break;
		case R.id.show_list_iv_to_top: //回顶
			iv_to_top.setVisibility(View.GONE);
			toTop();
			break;
		}
	}

	/**
	 * 展示已缓存的数据并至顶
	 */
	private void addOldListDatas(List<BounsEntity> oldLists, int oldPage) {
		addAllShow(oldLists);
		current_Page = oldPage;
		myUpdateAdapter();
		if (current_Page != 1) {
			toTop();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        
        checkLogin();
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogined();
		if (isLogined) {
			if (!isSuccess) {
				isUpdateAllData = true;
			}
			updateAllData();
		}else {
			showTimeOutDialog(TAG);
		}
	}

	private void updateAllData() {
		if (isUpdateAllData) {
			isUpdateAllData = false;
			lv_lists_show.clear();
			lv_lists_all_1.clear();
			lv_lists_all_2.clear();
			lv_lists_all_3.clear();
			lv_lists_all_4.clear();
			hm_all_1.clear();
			hm_all_2.clear();
			hm_all_3.clear();
			hm_all_4.clear();
			getSVDatas();
		}
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
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BOUNS_LIST_CODE:
			mainEn = null;
			mainEn = sc.getBounsLists(topType, Page_Count, current_Page, rootStr);
			return mainEn;
		case AppConfig.REQUEST_SV_POST_CHOOSE_BOUNS_CODE:
			return sc.postChooseBouns(bounsId);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_BOUNS_LIST_CODE:
			if (mainEn != null) {
				if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					isSuccess = true;
					countTotal = mainEn.getCountTotal();
					List<BounsEntity> lists = mainEn.getMainLists();
					if (lists != null && lists.size() > 0) {
						switch (topType) {
						case TYPE_1: 
							addEntity(lv_lists_all_1, lists, hm_all_1);
							page_type_1++;
							break;
						case TYPE_2: 
							addEntity(lv_lists_all_2, lists, hm_all_2);
							page_type_2++;
							break;
						case TYPE_3: 
							addEntity(lv_lists_all_3, lists, hm_all_3);
							page_type_3++;
							break;
						case TYPE_4: 
							addEntity(lv_lists_all_4, lists, hm_all_4);
							page_type_4++;
							break;
						}
						myUpdateAdapter();
					}else {
						loadFailHandle();
					}
				}else {
					loadFailHandle();
				}
			}else {
				loadFailHandle();
				showServerBusy();
			}
			break;
		case AppConfig.REQUEST_SV_POST_CHOOSE_BOUNS_CODE:
			stopAnimation();
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					if (PostOrderActivity.instance != null) {
						PostOrderActivity.instance.isUpdate = true;
					}
					finish();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(mContext, baseEn.getErrInfo(), 2000);
					}
				}
			}else {
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
		case TYPE_1: 
			addAllShow(lv_lists_all_1);
			break;
		case TYPE_2: 
			addAllShow(lv_lists_all_2);
			break;
		case TYPE_3: 
			addAllShow(lv_lists_all_3);
			break;
		case TYPE_4: 
			addAllShow(lv_lists_all_4);
			break;
		}
		myUpdateAdapter();
	}
	
	/**
	 * 数据去重函数
	 */
	private void addEntity(List<BounsEntity> oldDatas, List<BounsEntity> newDatas, HashMap<String, Boolean> hashMap) {
		BounsEntity entity = null;
		for (int i = 0; i < newDatas.size(); i++) {
			entity = newDatas.get(i);
			if (entity != null && !hashMap.containsKey(entity.getBounsId())) {
				oldDatas.add(entity);
			}
		}
		addAllShow(oldDatas);
		hashMap.clear();
		for (int i = 0; i < oldDatas.size(); i++) {
			hashMap.put(oldDatas.get(i).getBounsId(), true);
		}
	}

	private void addAllShow(List<BounsEntity> showLists) {
		lv_lists_show.clear();
		lv_lists_show.addAll(showLists);
	}
	
	private void myUpdateAdapter() {
		if (current_Page == 1) {
			toTop();
		}
		lv_adapter.updateAdapter(lv_lists_show, bounsId);
		stopAnimation();
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		setAdapter();
	}
	
	@Override
	protected void startAnimation() {
		rl_no_data.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void stopAnimation() {
		isLoadOk = true;
		refresh_lv.onPullUpRefreshComplete();
		rl_loading.setVisibility(View.GONE);
		if (lv_lists_show.size() == 0) {
			tv_no_data.setText(getString(R.string.loading_no_data, noDataShowStr));
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
		return lv_lists_show.size() < Page_Count || lv_lists_show.size() == countTotal;
	}
	
}
