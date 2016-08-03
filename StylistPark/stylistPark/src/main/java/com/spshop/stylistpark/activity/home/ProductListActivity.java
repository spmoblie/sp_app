package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.SelectListActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductList2ItemAdapter;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.ScrollViewListView;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * "商品列表"Activity
 */
@SuppressLint("UseSparseArrays")
public class ProductListActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "ProductListActivity";
	public static ProductListActivity instance = null;
	public boolean isUpdate = false;
	public static final int TYPE_1 = 1;  //默认
	public static final int TYPE_3 = 3;  //价格

	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int page_type_1 = 1;  //默认列表加载页
	private int page_type_3_ASC = 1;  //价格升序列表加载页
	private int page_type_3_DSC = 1;  //价格降序列表加载页
	private int topType = TYPE_1; //Top标记
	private int sortType = 0; //排序标记(0:默认排序/1:价格降序/2:价格升序)
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int countTotal = 0; //数集总数量
	private int total_1, total_3_ASC, total_3_DSC;
	private boolean isLoadOk = true; //加载数据控制符
	private boolean isFrist = true; //识别是否第一次打开页面
	private boolean flag_type_3 = true; //价格排序控制符(true:价格升序/false:价格降序)

	private int mFirstVisibleItem = 0;
	private int typeId = 0; //0:搜索页面  非0:商品列表
	private int loadNum = 0; //记录加载筛选列表数据的次数
	private int brandId = 0; //筛选的品牌Id
	private String brandName = ""; //筛选的品牌名称
	private String attrStr = ""; //筛选的其它类型Value字符串
	private String titleName = "";
	private String searchStr = "";
	private String wordsHistoryStr = "";

	private RelativeLayout rl_search_et, rl_search_txt, rl_search_line;
	private RelativeLayout rl_screen, rl_words_clear, rl_search_no_data;
	private LinearLayout ll_top, ll_bottom, ll_other;
	private LinearLayout ll_hot_words, ll_radio_group, ll_search_history, ll_words_history;
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private Button btn_screen, btn_words_clear;
	private EditText et_search;
	private ImageView iv_top_back, iv_search_clear, iv_to_top;
	private TextView tv_title, tv_words_title;
	private TextView tv_words_1, tv_words_2, tv_words_3, tv_words_4;
	private Drawable rank_up, rank_down, rank_normal;
	private ScrollView sv_words_history;
	private ScrollViewListView lv_words_history;
	private SelectListAdapter lv_words_adapter;
	private AdapterCallback apCallback;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private AdapterCallback lv_callback;
	private ProductList2ItemAdapter lv_two_adapter;
	
	private SelectListEntity screen_MainEn;
	private ProductListEntity product_MainEn;
	private List<SelectListEntity> lv_words = new ArrayList<SelectListEntity>();
	private List<ListShowTwoEntity> lv_show_two = new ArrayList<ListShowTwoEntity>();
	private List<ProductListEntity> lv_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_1 = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_3_DSC = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_all_3_ASC = new ArrayList<ProductListEntity>();
	private SparseBooleanArray sa_all_1 = new SparseBooleanArray();
	private SparseBooleanArray sa_all_3_asc = new SparseBooleanArray();
	private SparseBooleanArray sa_all_3_dsc = new SparseBooleanArray();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_list);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		typeId = getIntent().getIntExtra("typeId", 0);

		findViewById();
		initView();
	}
	
	private void findViewById() {
		btn_1 = (RadioButton) findViewById(R.id.topbar_radio_rb_1);
		btn_2 = (RadioButton) findViewById(R.id.topbar_radio_rb_2);
		btn_3 = (RadioButton) findViewById(R.id.topbar_radio_rb_3);
		btn_4 = (RadioButton) findViewById(R.id.topbar_radio_rb_4);
		btn_screen = (Button) findViewById(R.id.topbar_radio_btn_screen);
		btn_words_clear = (Button) findViewById(R.id.button_confirm_btn_one);
		refresh_lv = (PullToRefreshListView) findViewById(R.id.product_refresh_lv);
		sv_words_history = (ScrollView) findViewById(R.id.scroll_list_btn_sv);
		lv_words_history = (ScrollViewListView) findViewById(R.id.scroll_list_btn_lv);
		iv_top_back = (ImageView) findViewById(R.id.search_iv_back);
		iv_search_clear = (ImageView) findViewById(R.id.search_iv_clear);
		iv_to_top = (ImageView) findViewById(R.id.product_iv_to_top);
		tv_title = (TextView) findViewById(R.id.search_tv_title);
		tv_words_1 = (TextView) findViewById(R.id.product_tv_hot_words_1);
		tv_words_2 = (TextView) findViewById(R.id.product_tv_hot_words_2);
		tv_words_3 = (TextView) findViewById(R.id.product_tv_hot_words_3);
		tv_words_4 = (TextView) findViewById(R.id.product_tv_hot_words_4);
		tv_words_title = (TextView) findViewById(R.id.scroll_list_btn_tv_top_title);
		et_search = (EditText) findViewById(R.id.search_et_search);
		rl_screen = (RelativeLayout) findViewById(R.id.topbar_radio_rl_screen);
		rl_search_et = (RelativeLayout) findViewById(R.id.search_rl_search_et);
		rl_search_txt = (RelativeLayout) findViewById(R.id.search_rl_search_txt);
		rl_search_line = (RelativeLayout) findViewById(R.id.product_rl_search_line);
		rl_search_no_data = (RelativeLayout) findViewById(R.id.product_ll_search_no_data);
		rl_words_clear = (RelativeLayout) findViewById(R.id.scroll_list_btn_rl_clear);
		ll_top = (LinearLayout) findViewById(R.id.product_ll_anim_top);
		ll_bottom = (LinearLayout) findViewById(R.id.product_ll_anim_bottom);
		ll_other = (LinearLayout) findViewById(R.id.product_ll_anim_other);
		ll_hot_words = (LinearLayout) findViewById(R.id.product_ll_search_hot_words);
		ll_radio_group = (LinearLayout) findViewById(R.id.topbar_radio_ll_main);
		ll_search_history = (LinearLayout) findViewById(R.id.product_ll_search_history);
		ll_words_history = (LinearLayout) findViewById(R.id.scroll_list_btn_ll_main);
		
		rank_up = getResources().getDrawable(R.drawable.icon_rank_up);
		rank_down = getResources().getDrawable(R.drawable.icon_rank_down);
		rank_normal = getResources().getDrawable(R.drawable.icon_rank_normal);
		rank_up.setBounds(0, 0, rank_up.getMinimumWidth(), rank_up.getMinimumHeight());
		rank_down.setBounds(0, 0, rank_down.getMinimumWidth(), rank_down.getMinimumHeight());
		rank_normal.setBounds(0, 0, rank_normal.getMinimumWidth(), rank_normal.getMinimumHeight());
	}

	private void initView() {
		setHeadVisibility(View.GONE);
		iv_top_back.setOnClickListener(this);
		iv_search_clear.setOnClickListener(this);
		iv_to_top.setOnClickListener(this);
		rl_search_txt.setOnClickListener(this);
		tv_words_1.setOnClickListener(this);
		tv_words_2.setOnClickListener(this);
		tv_words_3.setOnClickListener(this);
		tv_words_4.setOnClickListener(this);
		
		if (typeId == 0) {
			ll_radio_group.setVisibility(View.GONE);
			rl_screen.setVisibility(View.GONE);
			ll_other.setVisibility(View.GONE);
			ll_search_history.setVisibility(View.VISIBLE);
			
			getWordsHistoryLists();
			initEditText();
			initWordsHistoryList();
		}else {
			rl_search_et.setVisibility(View.GONE);
			rl_search_txt.setVisibility(View.GONE);
			rl_search_line.setVisibility(View.GONE);
			tv_title.setVisibility(View.VISIBLE);
			ll_hot_words.setVisibility(View.GONE);
			ll_radio_group.setVisibility(View.VISIBLE);
			rl_screen.setVisibility(View.VISIBLE);
			ll_other.setVisibility(View.VISIBLE);
			ll_search_history.setVisibility(View.GONE);
			setDefaultRadioButton();
			getScreenListDatas();
		}
		
		initRaidoGroup();
		initListView();
		setAdapter();
	}

	/**
	 * 初始化搜索输入框
	 */
	private void initEditText() {
		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					requestSearchDatas();
					return true;
				}
				return false;
			}
			
		});
		
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				searchStr = s.toString();
				et_search.setSelection(et_search.length());
				if (StringUtil.isNull(searchStr)) {
					clearAllData();
		        	iv_search_clear.setVisibility(View.GONE);

		        	ll_hot_words.setVisibility(View.VISIBLE);
					ll_radio_group.setVisibility(View.GONE);
					ll_other.setVisibility(View.GONE);
					ll_search_history.setVisibility(View.VISIBLE);
					ll_words_history.setVisibility(View.VISIBLE);
					rl_search_no_data.setVisibility(View.GONE);
				}else {
					iv_search_clear.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	/**
	 * 初始化历史搜索词汇
	 */
	private void initWordsHistoryList() {
		btn_words_clear.setOnClickListener(this);
		btn_words_clear.setText(getString(R.string.product_search_history_clear));
		if (lv_words.size() > 0) {
			tv_words_title.setText(getString(R.string.product_search_history));
			rl_words_clear.setVisibility(View.VISIBLE);
		}else {
			tv_words_title.setText(getString(R.string.product_search_history_no));
			rl_words_clear.setVisibility(View.GONE);
		}
		
		apCallback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				et_search.setText(lv_words.get(position).getChildShowName());
				requestSearchDatas();
			}
		};
		lv_words_adapter = new SelectListAdapter(mContext, lv_words, apCallback, SelectListAdapter.DATA_TYPE_3);
		lv_words_history.setAdapter(lv_words_adapter);
		lv_words_history.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		
		sv_words_history.post(new Runnable() {

			@Override
			public void run() {
				sv_words_history.scrollTo(0, 0);
			}
		});
	}

	private void initRaidoGroup() {
		btn_1.setText(getString(R.string.product_top_tab_1));
		btn_1.setChecked(true);
		btn_1.setOnClickListener(this);
		btn_2.setVisibility(View.GONE);
		btn_3.setText(getString(R.string.product_top_tab_3));
		btn_3.setOnClickListener(this);
		btn_4.setVisibility(View.GONE);
		btn_screen.setOnClickListener(this);
		btn_screen.setText(getString(R.string.product_top_tab_4));
	}

	private void initListView() {
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnScrollListener(new OnMyScrollListener());
		refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				refreshSVDatas();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 加载更多
				int page_num = lv_show.size() / Page_Count;
				if (lv_show.size() % Page_Count > 0) {
					page_num++;
				}
				int page_total = countTotal / Page_Count;
				if (countTotal % Page_Count > 0) {
					page_total++;
				}
				CommonTools.showPageNum(page_num + "/" + page_total, 1000);

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
	 * 加载筛选列表数据
	 */
	private void getScreenListDatas() {
		request(AppConfig.REQUEST_SV_GET_SCREEN_LIST_CODE);
	}
	
	/**
	 * 加载搜索数据
	 */
	private void requestSearchDatas() {
		if (StringUtil.isNull(searchStr)) {
			CommonTools.showToast(getString(R.string.product_search_et_hint), 1000);
			return;
		}
		clearAllData();
		btn_1.setChecked(true);
		onClick(btn_1);
		addNewSearchWords();
		initWordsHistoryList();
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		loadType = 1;
		current_Page = 1;
		countTotal = 0;
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
		requestProductLists();
	}

	/**
	 * 加载下拉刷新数据
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
		if (current_Page == 1) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					request(AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE);
				}
			}, 1000);
		}else {
			request(AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE);
		}
	}
	
	/**
	 * 品牌筛选
	 */
	public void updateScreenParameter(SelectListEntity selectEn){
		int selectId = -1;
		if (selectEn != null) {
			selectId = selectEn.getChildId();
			brandName = selectEn.getChildShowName();
		}else {
			selectId = 0;
			brandName = "";
		}
		if (screen_MainEn != null && screen_MainEn.getMainLists() != null
				&& screen_MainEn.getMainLists().get(0) != null) {
			screen_MainEn.getMainLists().get(0).setSelectEn(selectEn);
		}
		if (brandId != selectId) {
			isUpdate = true;
		}
		brandId = selectId;
	}
	
	/**
	 * 更新筛选参数信息
	 */
	public void updateScreenParameter(SelectListEntity mainEn, int brand_id, String attr_name){
		screen_MainEn = mainEn;
		brandId = brand_id;
		attrStr = attr_name;
		isUpdate = true;
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
				addOldListDatas(lv_all_1, page_type_1, total_1);
			}else {
				page_type_1 = 1;
				total_1 = 0;
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
					addOldListDatas(lv_all_3_ASC, page_type_3_ASC, total_3_ASC);
				}else {
					page_type_3_ASC = 1;
					total_3_ASC = 0;
					getSVDatas();
				}
			} else //价格降序
			{
				flag_type_3 = true;
				btn_3.setCompoundDrawables(null, null, rank_down, null);
				sortType = 1;
				if (lv_all_3_DSC != null && lv_all_3_DSC.size() > 0) {
					addOldListDatas(lv_all_3_DSC, page_type_3_DSC, total_3_DSC);
				} else {
					page_type_3_DSC = 1;
					total_3_DSC = 0;
					getSVDatas();
				}
			}
			break;
		case R.id.topbar_radio_btn_screen: //筛选
			if (screen_MainEn != null && screen_MainEn.getMainLists() != null) {
				/*Intent intent = new Intent(mContext, ScreenListActivity.class);
				intent.putExtra("data", screen_MainEn);
				startActivity(intent);*/
				SelectListEntity data = screen_MainEn.getMainLists().get(0);
				if (data != null) {
					Intent intent = new Intent(mContext, SelectListActivity.class);
					intent.putExtra("data", data);
					intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_4);
					startActivity(intent);
				}else {
					CommonTools.showToast(getString(R.string.toast_error_data_null), 1000);
				}
			}else {
				CommonTools.showToast(getString(R.string.toast_error_data_null), 1000);
			}
			break;
		case R.id.search_iv_back: //返回
			finish();
			break;
		case R.id.search_iv_clear: //清除搜索
			et_search.setText("");
			clearAllData();
			break;
		case R.id.button_confirm_btn_one: //清除历史搜索记录
			lv_words.clear();
			wordsHistoryStr = "";
			editor.putString(AppConfig.KEY_SEARCH_WORDS_HISTORY, wordsHistoryStr).commit();
			initWordsHistoryList();
			break;
		case R.id.search_rl_search_txt: //搜索
			requestSearchDatas();
			break;
		case R.id.product_tv_hot_words_1: //热词1
			updateSearchWords(tv_words_1);
			break;
		case R.id.product_tv_hot_words_2: //热词2
			updateSearchWords(tv_words_2);
			break;
		case R.id.product_tv_hot_words_3: //热词3
			updateSearchWords(tv_words_3);
			break;
		case R.id.product_tv_hot_words_4: //热词4
			updateSearchWords(tv_words_4);
			break;
		case R.id.product_iv_to_top: //回顶
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
		countTotal = oldTotal;
		myUpdateAdapter();
		if (current_Page != 1) {
			toTop();
		}
		setLoadMoreDate();
	}
	
	private void updateSearchWords(TextView tv_words) {
		et_search.setText(tv_words.getText().toString());
		requestSearchDatas();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

        if (isUpdate) {
        	isUpdate = false;
        	clearAllData();
        	getSVDatas();
        	if (isScreenOR()) {
        		btn_screen.setText(brandName);
        		btn_screen.setTextColor(mContext.getResources().getColor(R.color.text_color_red_1));
			}else {
				btn_screen.setText(getString(R.string.product_top_tab_4));
				btn_screen.setTextColor(mContext.getResources().getColor(R.color.text_color_assist));
			}
		}
	}
	
	/**
	 * 判定是否有筛选项
	 */
	private boolean isScreenOR() {
		if (screen_MainEn != null && screen_MainEn.getMainLists() != null) {
			List<SelectListEntity> lists = screen_MainEn.getMainLists();
			SelectListEntity typeEn = null;
			for (int i = 0; i < lists.size(); i++) {
				typeEn = lists.get(i);
				if (typeEn != null && typeEn.getSelectEn() != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		instance = null;
	}

	class OnMyScrollListener implements AbsListView.OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem == 0 || mFirstVisibleItem > firstVisibleItem) { //上滑
				if (firstVisibleItem == 0 || mFirstVisibleItem > firstVisibleItem + 10) {
					mFirstVisibleItem = firstVisibleItem;
					upMove(firstVisibleItem);
				} else {
					mFirstVisibleItem++;
				}
			} else if (firstVisibleItem == totalItemCount - visibleItemCount || mFirstVisibleItem < firstVisibleItem) { //下滑
				if (firstVisibleItem == totalItemCount - visibleItemCount || mFirstVisibleItem < firstVisibleItem - 80) {
					mFirstVisibleItem = firstVisibleItem;
					downMove(firstVisibleItem);
				} else {
					mFirstVisibleItem--;
				}
			}
		}
	}

	/**
	 * 向上滑动效果
	 */
	private void upMove(int firstVisibleItem) {
		if (headStatus && firstVisibleItem < countTotal - Page_Count / 2) {
			createAnimation(ll_top, ll_bottom, ll_other);
			ll_top.clearAnimation();
			ll_top.startAnimation(headVISIBLE);
			ll_other.clearAnimation();
			ll_other.startAnimation(headVISIBLE);
			headStatus = false;
		}
		if (firstVisibleItem > 5) {
			iv_to_top.setVisibility(View.VISIBLE);
		} else {
			iv_to_top.setVisibility(View.GONE);
		}
	}

	/**
	 * 向下滑动效果
	 */
	private void downMove(int firstVisibleItem) {
		if (countTotal > Page_Count && !headStatus && firstVisibleItem > 3
				&& firstVisibleItem < countTotal - Page_Count / 2) {
			createAnimation(ll_top, ll_bottom, ll_other);
			ll_top.clearAnimation();
			ll_top.startAnimation(headGONE);
			ll_other.clearAnimation();
			ll_other.startAnimation(headGONE);
			headStatus = true;
		}
		if (firstVisibleItem > 5) {
			iv_to_top.setVisibility(View.VISIBLE);
		} else {
			iv_to_top.setVisibility(View.GONE);
		}
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_SCREEN_LIST_CODE:
			screen_MainEn = sc.getScreenlistDatas(typeId, mContext.getString(R.string.all));
			return screen_MainEn;
		case AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE:
			product_MainEn = null;
			product_MainEn = sc.getProductListDatas(typeId, sortType, brandId, Page_Count, current_Page, searchStr, attrStr, 0);
			return product_MainEn;
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_SCREEN_LIST_CODE:
			if (screen_MainEn == null && loadNum < 3) {
				loadNum++;
				getScreenListDatas();
			}
			break;
		case AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE:
			if (product_MainEn != null && product_MainEn.getMainLists() != null) {
				ll_other.setVisibility(View.VISIBLE);
				ll_search_history.setVisibility(View.GONE);

				if (typeId != 0 && StringUtil.isNull(titleName)) {
					titleName = product_MainEn.getCategoryName();
					tv_title.setText(titleName);
				}
				int total = product_MainEn.getTotal();
				List<ProductListEntity> lists = product_MainEn.getMainLists();
				if (lists.size() > 0) {
					switch (topType) {
					case TYPE_1: //默认
						if (loadType == 0) { //下拉
							updEntity(total, countTotal, lists, lv_all_1, sa_all_1);
						}else {
							addEntity(lv_all_1, lists, sa_all_1);
							page_type_1++;
						}
						total_1 = total;
						break;
					case TYPE_3: //价格
						switch (sortType) {
						case 1: //降序
							if (loadType == 0) { //下拉
								updEntity(total, countTotal, lists, lv_all_3_DSC, sa_all_3_dsc);
							}else {
								addEntity(lv_all_3_DSC, lists, sa_all_3_dsc);
								page_type_3_DSC++;
							}
							total_3_DSC = total;
							break;
						case 2: //升序
							if (loadType == 0) { //下拉
								updEntity(total, countTotal, lists, lv_all_3_ASC, sa_all_3_asc);
							}else {
								addEntity(lv_all_3_ASC, lists, sa_all_3_asc);
								page_type_3_ASC++;
							}
							total_3_ASC = total;
							break;
						}
						break;
					}
					if (typeId == 0) { //搜索状态
						ll_hot_words.setVisibility(View.GONE);
						ll_radio_group.setVisibility(View.VISIBLE);
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
		if (typeId == 0 && current_Page == 1) { //搜索状态
			clearAllData();
			ll_hot_words.setVisibility(View.VISIBLE);
			ll_radio_group.setVisibility(View.GONE);
		}
		myUpdateAdapter();
	}

	/**
	 * 刷新数集
	 */
	private void updEntity(int newTotal, int oldTotal, List<ProductListEntity> newDatas, 
			List<ProductListEntity> oldDatas, SparseBooleanArray sa_old) {
		if (oldTotal < newTotal) {
			List<ProductListEntity> datas = new ArrayList<ProductListEntity>();
			datas.addAll(oldDatas);
			oldDatas.clear();

			ProductListEntity newEn, oldEn;
			int dataId = 0;
			for (int i = 0; i < (newTotal - oldTotal); i++) {
				newEn = newDatas.get(i);
				if (newEn != null) {
					dataId = newEn.getId();
					if (dataId != 0 && sa_old.indexOfKey(dataId) < 0) {
						// 添加至顶层
						oldDatas.add(newEn);
						sa_old.put(dataId, true);
						// 移除最底层
						oldEn = datas.remove(datas.size()-1);
						if (oldEn != null) {
							sa_old.delete(oldEn.getId());
						}
					}
				}
			}
			oldDatas.addAll(datas);
			addAllShow(oldDatas);
			setLoadMoreDate();
		}
	}
	
	/**
	 * 数据去重函数
	 */
	private void addEntity(List<ProductListEntity> oldDatas, List<ProductListEntity> newDatas, SparseBooleanArray sa_old) {
		ProductListEntity entity = null;
		int dataId = 0;
		for (int i = 0; i < newDatas.size(); i++) {
			entity = newDatas.get(i);
			if (entity != null) {
				dataId = entity.getId();
				if (dataId != 0 && sa_old.indexOfKey(dataId) < 0) {
					oldDatas.add(entity);
					sa_old.put(dataId, true);
				}
			}
		}
		addAllShow(oldDatas);
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
			refresh_lv.setVisibility(View.GONE);
			ll_other.setVisibility(View.GONE);
			ll_search_history.setVisibility(View.VISIBLE);
			rl_search_no_data.setVisibility(View.VISIBLE);
			ll_words_history.setVisibility(View.GONE);
		}else {
			refresh_lv.setVisibility(View.VISIBLE);
			ll_other.setVisibility(View.VISIBLE);
			ll_search_history.setVisibility(View.GONE);
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
	
	/**
	 * 获取商品搜索的历史记录集合
	 */
	private void getWordsHistoryLists() {
		wordsHistoryStr = shared.getString(AppConfig.KEY_SEARCH_WORDS_HISTORY, "");
		String[] strs = wordsHistoryStr.split("_");
		lv_words.clear();
		SelectListEntity en = null;
		for (int i = 0; i < strs.length; i++) {
			if (!StringUtil.isNull(strs[i])) {
				en = new SelectListEntity();
				en.setChildShowName(strs[i]);
				lv_words.add(en);
			}
		}
	}
	
	/**
	 * 添加新的搜索词汇至缓存
	 */
	private void addNewSearchWords() {
		StringBuilder sb = new StringBuilder();
		sb.append(searchStr).append("_");
		
		List<SelectListEntity> lists = new ArrayList<SelectListEntity>();
		lists.addAll(lv_words);
		SelectListEntity wordsEn = null;
		for (int i = 0; i < lists.size(); i++) {
			wordsEn = lists.get(i);
			if (wordsEn == null) continue;
			if (wordsEn.getChildShowName().equals(searchStr)) {
				lv_words.remove(wordsEn);
			}else {
				sb.append(wordsEn.getChildShowName()).append("_");
			}
		}
		wordsHistoryStr = sb.toString();
		lists.clear();
		lists.addAll(lv_words);
		lv_words.clear();
		wordsEn = new SelectListEntity();
		wordsEn.setChildShowName(searchStr);
		lv_words.add(wordsEn);
		lv_words.addAll(lists);
		editor.putString(AppConfig.KEY_SEARCH_WORDS_HISTORY, wordsHistoryStr).apply();
	}

	/**
	 * 清除所有记录的搜索结果
	 */
	private void clearAllData() {
		lv_show.clear();
		lv_show_two.clear();
		lv_all_1.clear();
		lv_all_3_DSC.clear();
		lv_all_3_ASC.clear();
		sa_all_1.clear();
		sa_all_3_asc.clear();
		sa_all_3_dsc.clear();
	}
	
}
