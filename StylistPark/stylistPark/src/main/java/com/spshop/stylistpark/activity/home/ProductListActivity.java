package com.spshop.stylistpark.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.SelectListActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.ProductGridAdapter;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.ScrollViewListView;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshGridView;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * "商品列表"Activity
 */
@SuppressLint("UseSparseArrays")
public class ProductListActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "ProductListActivity";
	public static ProductListActivity instance = null;
	public boolean isUpdate = false;
	
	private static final int Page_Count = 20;  //每页加载条数
	private int current_Page = 1;  //当前列表加载页
	private int default_Page = 1;  //默认列表加载页
	private int price_ASC_Page = 1;  //价格升序列表加载页
	private int price_DESC_Page = 1;  //价格降序列表加载页
	private int loadType = 1; //(0:下拉刷新/1:翻页加载)
	private int sortType = 0; //数据排序标记(0:默认排序/1:价格降序/2:价格升序)
	private int topType = 1; //Top标记(1:默认/3:价格/4:筛选)
	private int loadNum = 0; //记录加载筛选列表数据的次数
	private int brandId = 0; //筛选的品牌Id
	private int goodsTotal = 0; //商品总数量
	private String brandName = ""; //筛选的品牌名称
	private String attrStr = ""; //筛选的其它类型Value字符串
	private boolean btn_3_flag = true; //价格排序控制符(true:价格升序/false:价格降序)
	private boolean isLoadOk = true;
	private boolean isFooter = false;
	private boolean isFrist = true; //识别是否第一次打开页面
	
	private RelativeLayout rl_search_et, rl_search_txt, rl_search_line;
	private RelativeLayout rl_screen, rl_words_clear, rl_search_no_data;
	private LinearLayout ll_top, ll_bottom, ll_other;
	private LinearLayout ll_hot_words, ll_radio_group, ll_search_history, ll_words_history;
	private RadioButton btn_1, btn_2, btn_3, btn_4;
	private Button btn_screen, btn_words_clear;
	private EditText et_search;
	private ImageView iv_top_back, iv_search_clear, iv_to_top;
	private TextView tv_footer, tv_page_num, tv_title, tv_words_title;
	private TextView tv_words_1, tv_words_2, tv_words_3, tv_words_4;
	private Drawable rank_up, rank_down, rank_normal;
	private ScrollView sv_words_history;
	private ScrollViewListView lv_words_history;
	private SelectListAdapter lv_words_adapter;
	private AdapterCallback apCallback;
	private PullToRefreshGridView refresh_gv;
	private GridView mGridView;
	private AdapterCallback gv_callback;
	private ProductGridAdapter gv_adapter;
	
	private int mFirstVisibleItem = 0;
	private int typeId = 0; //0:商品列表  非0:搜索页面
	private String typeName = "";
	private String searchStr = "";
	private String wordsHistoryStr = "";
	private SelectListEntity screen_MainEn;
	private ProductListEntity product_MainEn;
	private List<ProductListEntity> lv_lists_show = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_1 = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_3_DESC = new ArrayList<ProductListEntity>();
	private List<ProductListEntity> lv_lists_all_3_ASC = new ArrayList<ProductListEntity>();
	private List<SelectListEntity> lv_words_lists = new ArrayList<SelectListEntity>();
	private HashMap<Integer, Boolean> hm_all = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> hm_asc = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> hm_desc = new HashMap<Integer, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_list);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		typeId = getIntent().getIntExtra("typeId", 0);
		typeName = getIntent().getStringExtra("typeName");
		
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
		refresh_gv = (PullToRefreshGridView) findViewById(R.id.product_refresh_gv);
		tv_footer = (TextView) findViewById(R.id.product_tv_footer_loading);
		sv_words_history = (ScrollView) findViewById(R.id.scroll_list_btn_sv);
		lv_words_history = (ScrollViewListView) findViewById(R.id.scroll_list_btn_lv);
		iv_top_back = (ImageView) findViewById(R.id.search_iv_back);
		iv_search_clear = (ImageView) findViewById(R.id.search_iv_clear);
		iv_to_top = (ImageView) findViewById(R.id.product_iv_to_top);
		tv_page_num = (TextView) findViewById(R.id.product_tv_page_num);
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
			tv_title.setText(typeName);
			ll_hot_words.setVisibility(View.GONE);
			ll_radio_group.setVisibility(View.VISIBLE);
			rl_screen.setVisibility(View.VISIBLE);
			ll_other.setVisibility(View.VISIBLE);
			ll_search_history.setVisibility(View.GONE);
			setDefaultRadioButton();
			getScreenListDatas();
		}
		
		initRaidoGroup();
		initGridView();
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
					lv_lists_all_1.clear();
		        	lv_lists_all_3_ASC.clear();
		        	lv_lists_all_3_DESC.clear();
		        	hm_all.clear();
		        	default_Page = 1;
		        	price_ASC_Page = 1;
		        	price_DESC_Page = 1;
		        	current_Page = 1;
		        	
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
		if (lv_words_lists.size() > 0) {
			tv_words_title.setText(getString(R.string.product_search_history));
			rl_words_clear.setVisibility(View.VISIBLE);
		}else {
			tv_words_title.setText(getString(R.string.product_search_history_no));
			rl_words_clear.setVisibility(View.GONE);
		}
		
		apCallback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				et_search.setText(lv_words_lists.get(position).getChildShowName());
				requestSearchDatas();
			}
		};
		lv_words_adapter = new SelectListAdapter(mContext, lv_words_lists, apCallback, SelectListAdapter.DATA_TYPE_3);
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

	private void initGridView() {
		refresh_gv.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 下拉刷新
				refreshSVDatas();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 加载更多(GridView无效)
			}
		});
		mGridView = refresh_gv.getRefreshableView();
		mGridView.setNumColumns(2);
		mGridView.setHorizontalSpacing(10);
		mGridView.setVerticalSpacing(10);
		mGridView.setGravity(Gravity.CENTER_HORIZONTAL);
		mGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (isFooter && scrollState == SCROLL_STATE_IDLE) {
					// 加载更多
					tv_page_num.setVisibility(View.VISIBLE);
					int page_num = lv_lists_show.size() / Page_Count;
					if (lv_lists_show.size() % Page_Count > 0) {
						page_num++;
					}
					int page_total = goodsTotal / Page_Count;
					if (goodsTotal % Page_Count > 0) {
						page_total++;
					}
					tv_page_num.setText(page_num + "/" + page_total);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							tv_page_num.setVisibility(View.GONE);
						}
					}, 2000);

					tv_footer.setVisibility(View.VISIBLE);
					if (isStop()) {
						tv_footer.setText(getString(R.string.loading_no_more));
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								tv_footer.setVisibility(View.GONE);
							}
						}, 1000);
					} else {
						tv_footer.setText(getString(R.string.loading_strive_loading));
						loadSVDatas();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					isFooter = true;
				} else {
					isFooter = false;
					if (mFirstVisibleItem > firstVisibleItem) { //上滑
						if (mFirstVisibleItem > firstVisibleItem + 10) {
							mFirstVisibleItem = firstVisibleItem;
							upMove(firstVisibleItem);
						} else {
							mFirstVisibleItem++;
						}
					} else if (mFirstVisibleItem < firstVisibleItem) { //下滑
						if (mFirstVisibleItem < firstVisibleItem - 10) {
							mFirstVisibleItem = firstVisibleItem;
							downMove(firstVisibleItem);
						} else {
							mFirstVisibleItem--;
						}
					}
				}
			}
			
		});
	}
	
	/**
	 * 向上滑动效果
	 */
	private void upMove(int firstVisibleItem) {
		if (headStatus && firstVisibleItem < goodsTotal - Page_Count / 2) {
			createAnimation(ll_top, ll_bottom, ll_other);
			ll_top.clearAnimation();
			ll_top.startAnimation(headVISIBLE);
			ll_other.clearAnimation();
			ll_other.startAnimation(headVISIBLE);
			headStatus = false;
		}
		if (firstVisibleItem > 10) {
			iv_to_top.setVisibility(View.VISIBLE);
		} else {
			iv_to_top.setVisibility(View.GONE);
		}
	}

	/**
	 * 向下滑动效果
	 */
	private void downMove(int firstVisibleItem) {
		if (goodsTotal > Page_Count && !headStatus && firstVisibleItem > 6 
				&& firstVisibleItem < goodsTotal - Page_Count / 2) {
			createAnimation(ll_top, ll_bottom, ll_other);
			ll_top.clearAnimation();
			ll_top.startAnimation(headGONE);
			ll_other.clearAnimation();
			ll_other.startAnimation(headGONE);
			headStatus = true;
		}
		if (firstVisibleItem > 10) {
			iv_to_top.setVisibility(View.VISIBLE);
		} else {
			iv_to_top.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		gv_callback = new AdapterCallback() {

			@Override
			public void setOnClick(Object entity, int position, int type) {
				ProductListEntity data = (ProductListEntity) entity;
				if (data != null) {
					Intent intent = new Intent(mContext, ProductDetailActivity.class);
					intent.putExtra("goodsId", data.getId());
					startActivity(intent);
				}
			}
		};
		gv_adapter = new ProductGridAdapter(mContext, lv_lists_show, gv_callback);
		mGridView.setAdapter(gv_adapter);
		mGridView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
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
			CommonTools.showToast(mContext, getString(R.string.product_search_et_hint), 1000);
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
		startAnimation();
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
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		loadType = 1;
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
		requestProductLists();
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
					CommonTools.showToast(mContext, getString(R.string.toast_error_data_null), 1000);
				}
			}else {
				CommonTools.showToast(mContext, getString(R.string.toast_error_data_null), 1000);
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
			lv_words_lists.clear();
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
			iv_to_top.setVisibility(View.GONE);
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
	
	private void updateSearchWords(TextView tv_words) {
		et_search.setText(tv_words.getText().toString());
		requestSearchDatas();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        
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
				
				int total = product_MainEn.getTotal();
				List<ProductListEntity> lists = product_MainEn.getMainLists();
				if (lists.size() > 0) {
					switch (topType) {
					case 1: //默认
						if (loadType == 0) { //下拉
							updEntity(total, goodsTotal, lists, lv_lists_all_1, hm_all);
						}else {
							addEntity(lv_lists_all_1, lists, hm_all);
							default_Page++;
						}
						break;
					case 3: //价格
						switch (sortType) {
						case 1: //降序
							if (loadType == 0) { //下拉
								updEntity(total, goodsTotal, lists, lv_lists_all_3_DESC, hm_desc);
							}else {
								addEntity(lv_lists_all_3_DESC, lists, hm_desc);
								price_DESC_Page++;
							}
							break;
						case 2: //升序
							if (loadType == 0) { //下拉
								updEntity(total, goodsTotal, lists, lv_lists_all_3_ASC, hm_asc);
							}else {
								addEntity(lv_lists_all_3_ASC, lists, hm_asc);
								price_ASC_Page++;
							}
							break;
						}
						break;
					}
					if (typeId == 0) { //搜索状态
						ll_hot_words.setVisibility(View.GONE);
						ll_radio_group.setVisibility(View.VISIBLE);
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
		if (typeId == 0 && current_Page == 1) { //搜索状态
			clearAllData();
			ll_hot_words.setVisibility(View.VISIBLE);
			ll_radio_group.setVisibility(View.GONE);
		}
		if (lv_lists_show.size() == 0) {
			ll_other.setVisibility(View.GONE);
			ll_search_history.setVisibility(View.VISIBLE);
			ll_words_history.setVisibility(View.GONE);
			rl_search_no_data.setVisibility(View.VISIBLE);
		}else {
			ll_other.setVisibility(View.VISIBLE);
			ll_search_history.setVisibility(View.GONE);
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
		lv_lists_show.clear();
		lv_lists_show.addAll(showLists);
	}
	
	private void myUpdateAdapter() {
		if (current_Page == 1) {
			toTop();
		}
		gv_adapter.updateAdapter(lv_lists_show);
		stopAnimation();
	}

	/**
	 * 滚动到顶部
	 */
	private void toTop() {
		setAdapter();
	}
	
	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		switch (loadType) {
		case 0: //下拉刷新
			refresh_gv.onPullDownRefreshComplete();
			break;
		case 1: //加载更多
			tv_footer.setVisibility(View.GONE);
			break;
		}
		if (lv_lists_show.size() > 0) {
			refresh_gv.setVisibility(View.VISIBLE);
		}else {
			refresh_gv.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 商品数小于一页时停止加载翻页数据
	 */
	private boolean isStop(){
		return lv_lists_show.size() < Page_Count || lv_lists_show.size() == goodsTotal;
	}
	
	/**
	 * 获取商品搜索的历史记录集合
	 */
	private void getWordsHistoryLists() {
		wordsHistoryStr = shared.getString(AppConfig.KEY_SEARCH_WORDS_HISTORY, "");
		String[] strs = wordsHistoryStr.split("_");
		lv_words_lists.clear();
		SelectListEntity en = null;
		for (int i = 0; i < strs.length; i++) {
			if (!StringUtil.isNull(strs[i])) {
				en = new SelectListEntity();
				en.setChildShowName(strs[i]);
				lv_words_lists.add(en);
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
		lists.addAll(lv_words_lists);
		SelectListEntity wordsEn = null;
		for (int i = 0; i < lists.size(); i++) {
			wordsEn = lists.get(i);
			if (wordsEn == null) continue;
			if (wordsEn.getChildShowName().equals(searchStr)) {
				lv_words_lists.remove(wordsEn);
			}else {
				sb.append(wordsEn.getChildShowName()).append("_");
			}
		}
		wordsHistoryStr = sb.toString();
		lists.clear();
		lists.addAll(lv_words_lists);
		lv_words_lists.clear();
		wordsEn = new SelectListEntity();
		wordsEn.setChildShowName(searchStr);
		lv_words_lists.add(wordsEn);
		lv_words_lists.addAll(lists);
		editor.putString(AppConfig.KEY_SEARCH_WORDS_HISTORY, wordsHistoryStr).commit();
	}

	/**
	 * 清除所有记录的搜索结果
	 */
	private void clearAllData() {
		lv_lists_show.clear();
		lv_lists_all_1.clear();
		lv_lists_all_3_DESC.clear();
		lv_lists_all_3_ASC.clear();
		hm_all.clear();
		hm_asc.clear();
		hm_desc.clear();
		default_Page = 1;
    	price_ASC_Page = 1;
    	price_DESC_Page = 1;
    	current_Page = 1;
	}
	
}
