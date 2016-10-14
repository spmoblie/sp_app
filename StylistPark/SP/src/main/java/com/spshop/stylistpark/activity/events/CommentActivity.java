package com.spshop.stylistpark.activity.events;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.CommentListAdapter;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.CommentEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "CommentActivity";
	public static CommentActivity instance = null;

	private int dataTotal = 0; //数据总量
	private int current_Page = 1;  //当前列表加载页
	private boolean isLoadOk = true;
	private boolean isUpdate = false;
	private int postId;
	private String commentStr;

	private EditText et_input;
	private TextView tv_post;
	private FrameLayout rl_no_data;
	private TextView tv_no_data;
	private ImageView iv_to_top;
	private PullToRefreshListView refresh_lv;
	private ListView mListView;
	private CommentListAdapter lv_adapter;
	
	private List<CommentEntity> lv_show = new ArrayList<CommentEntity>();
	private List<CommentEntity> lv_all_1 = new ArrayList<CommentEntity>();
	private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<String, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		Bundle bundle = getIntent().getExtras();
		postId = bundle.getInt("postId", 0);

		findViewById();
		initView();
	}
	
	private void findViewById() {
		refresh_lv = (PullToRefreshListView) findViewById(R.id.comment_refresh_lv);
		et_input = (EditText) findViewById(R.id.comment_et_input);
		tv_post = (TextView) findViewById(R.id.comment_tv_post);
		rl_no_data = (FrameLayout) findViewById(R.id.loading_no_data_fl_main);
		tv_no_data = (TextView) findViewById(R.id.loading_no_data_tv_show);
		iv_to_top = (ImageView) findViewById(R.id.comment_iv_to_top);
	}

	private void initView() {
		setCommentTitle(0);
		iv_to_top.setOnClickListener(this);
		initListView();
		initEditText();
		setAdapter();
	}

	private void initListView() {
		refresh_lv.setPullLoadEnabled(false);
		refresh_lv.setScrollLoadEnabled(true);
		refresh_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 下拉刷新
            	if (lv_show.size() == 0) {
            		getSVDatas(1000);
				}else {
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							refresh_lv.onPullDownRefreshComplete();
						}
					}, 1000);
				}
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	// 加载更多
            	if (!isStopLoadMore(lv_show.size(), dataTotal, 0)) {
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
		refresh_lv.doPullRefreshing(true, 500);
		mListView = refresh_lv.getRefreshableView();
		mListView.setSelector(R.color.ui_bg_color_app);
	}

	private void initEditText() {
		tv_post.setOnClickListener(this);
		et_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String comStr = s.toString();
				if (StringUtil.isNull(comStr)) {
					tv_post.setTextColor(getResources().getColor(R.color.label_text_color));
				} else {
					tv_post.setTextColor(getResources().getColor(R.color.tv_color_status));
				}
			}
		});
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_adapter = new CommentListAdapter(mContext, lv_show);
		mListView.setAdapter(lv_adapter);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas(int time) {
		current_Page = 1;
		requestProductLists(time);
	}
	
	/**
	 * 加载翻页数据
	 */
	private void loadSVDatas() {
		requestProductLists(1000);
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists(int time) {
		if (!isLoadOk) return; //加载频率控制
		isLoadOk = false;
		rl_no_data.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_GET_COMMENT_LIST_CODE);
			}
		}, time);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.comment_tv_post: //评论
				sendCommentTxt();
				break;
			case R.id.show_list_iv_to_top: //回顶
				toTop();
				break;
		}
	}

	private void sendCommentTxt(){
		if (!UserManager.getInstance().checkIsLogined()) {
			openLoginActivity(TAG);
			return;
		}
		commentStr = et_input.getText().toString();
		if (StringUtil.isNull(commentStr)) {
			CommonTools.showToast(getString(R.string.events_comment_input), 1000);
			return;
		}
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_COMMENT_CODE);
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
			lv_all_1.clear();
			am_all_1.clear();
			getSVDatas(0);
		}
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
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_COMMENT_URL;;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_COMMENT_LIST_CODE:
				params.add(new MyNameValuePair("act", "gotopage"));
				params.add(new MyNameValuePair("id", String.valueOf(postId)));
				params.add(new MyNameValuePair("type", "1"));
				params.add(new MyNameValuePair("page", String.valueOf(current_Page)));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_COMMENT_LIST_CODE, uri, params, HttpUtil.METHOD_GET);
			case AppConfig.REQUEST_SV_POST_COMMENT_CODE:
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", postId);
				jsonObject.put("type", 1);
				jsonObject.put("content", commentStr);
				String jsonStrValue = jsonObject.toString();

				//uri = AppConfig.URL_COMMON_COMMENT_URL + "?cmt=" + jsonStrValue;
				params.add(new MyNameValuePair("cmt", jsonStrValue));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_COMMENT_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_COMMENT_LIST_CODE:
				if (result != null) {
					CommentEntity mainEn = (CommentEntity) result;
					dataTotal = mainEn.getDataTotal();
					setCommentTitle(dataTotal);
					List<CommentEntity> lists = mainEn.getMainLists();
					if (lists!= null && lists.size() > 0) {
						List<BaseEntity> newLists = addNewEntity(lv_all_1, lists, am_all_1);
						if (newLists != null) {
							addNewShowLists(newLists);
							current_Page++;
						}
						myUpdateAdapter();
					}else {
						loadFailHandle();
					}
				}else {
					loadFailHandle();
					showServerBusy();
				}
				break;
			case AppConfig.REQUEST_SV_POST_COMMENT_CODE:
				stopAnimation();
				if (result != null) {
					BaseEntity baseEn = (BaseEntity) result;
					if (baseEn.getErrCode() == 0) {
						updateData();
						updateAllData();
						et_input.setText("");
						if (StringUtil.isNull(baseEn.getErrInfo())) {
							CommonTools.showToast(getString(R.string.events_comment_ok), 2000);
						}else {
							CommonTools.showToast(baseEn.getErrInfo(), 2000);
						}
					}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
						// 登入超时，交BaseActivity处理
					}else {
						if (StringUtil.isNull(baseEn.getErrInfo())) {
							showServerBusy();
						}else {
							CommonTools.showToast(baseEn.getErrInfo(), 3000);
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
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
		loadFailHandle();
	}

	private void loadFailHandle() {
		stopAnimation();
	}

	private void myUpdateAdapter() {
		lv_adapter.updateAdapter(lv_show);
		stopAnimation();
	}

	private void addNewShowLists(List<BaseEntity> showLists) {
		lv_show.clear();
		for (int i = 0; i < showLists.size(); i++) {
			lv_show.add((CommentEntity) showLists.get(i));
		}
		lv_all_1.clear();
		lv_all_1.addAll(lv_show);
	}

	@Override
	protected void stopAnimation() {
		super.stopAnimation();
		isLoadOk = true;
		refresh_lv.onPullDownRefreshComplete();
		refresh_lv.onPullUpRefreshComplete();
		if (lv_show.size() == 0) {
			tv_no_data.setText(getString(R.string.loading_no_data, getString(R.string.events_comment)));
			rl_no_data.setVisibility(View.VISIBLE);
			refresh_lv.setVisibility(View.GONE);
		}else {
			rl_no_data.setVisibility(View.GONE);
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

	private void setCommentTitle(int total) {
		setTitle(getString(R.string.events_comment_total, total));
	}

}
