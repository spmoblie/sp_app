package com.spshop.stylistpark.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.SelectListActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.widgets.ScrollViewListView;

import java.util.ArrayList;
import java.util.List;

/**
 * "商品筛选"Activity
 */
public class ScreenListActivity extends BaseActivity {
	
	private static final String TAG = "ScreenListActivity";
	public static ScreenListActivity instance = null;
	
	private Button btn_clear;
	private RelativeLayout rl_top_title;
	private ScrollView sv;
	private ScrollViewListView lv;
	private AdapterCallback apCallback;
	private SelectListAdapter lv_Adapter;
	
	private int mPosition = 0;
	private boolean isChange = false;
	private SelectListEntity mainEn;
	private List<SelectListEntity> lv_lists = new ArrayList<SelectListEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_scroll_list_btn);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		mainEn = (SelectListEntity) getIntent().getExtras().get("data");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		sv = (ScrollView) findViewById(R.id.scroll_list_btn_sv);
		lv = (ScrollViewListView) findViewById(R.id.scroll_list_btn_lv);
		btn_clear = (Button) findViewById(R.id.button_confirm_btn_one);
		rl_top_title = (RelativeLayout) findViewById(R.id.scroll_list_btn_rl_top_title);
	}

	private void initView() {
		setTitle(R.string.title_screen);
		setBtnRight(getString(R.string.confirm));
		
		if (mainEn != null && mainEn.getMainLists() != null) {
			lv_lists.addAll(mainEn.getMainLists());
			setAdapter();
			sv.post(new Runnable() {

				@Override
				public void run() {
					sv.scrollTo(0, 0);
				}
			});
		}else {
			CommonTools.showToast(getString(R.string.toast_error_data_null), 1000);
		}
		
		rl_top_title.setVisibility(View.GONE);
		btn_clear.setText(getString(R.string.product_clear_options));
		btn_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (int i = 0; i < lv_lists.size(); i++) {
					lv_lists.get(i).setSelectEn(null);
				}
				setAdapter();
				if (ProductListActivity.instance != null) {
					mainEn.setMainLists(lv_lists);
					ProductListActivity.instance.updateScreenParameter(mainEn, 0, "");
				}
			}
		});
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		apCallback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				mPosition = position;
				SelectListEntity data = lv_lists.get(position);
				if (data != null) {
					Intent intent = new Intent(mContext, SelectListActivity.class);
					intent.putExtra("data", data);
					intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_2);
					startActivity(intent);
				}else {
					CommonTools.showToast(getString(R.string.toast_error_data_null), 1000);
				}
			}
		};
		lv_Adapter = new SelectListAdapter(mContext, lv_lists, apCallback, SelectListAdapter.DATA_TYPE_1);
		lv.setAdapter(lv_Adapter);
		lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}
	
	/**
	 * 更新集合数据并刷新界面
	 */
	public void updataListDatas(SelectListEntity en){
		lv_lists.get(mPosition).setSelectEn(en);
		setAdapter();
		isChange = true;
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		
		String other_name = "";
		StringBuffer sb = new StringBuffer();
		int brand_id = 0;
		
		if (lv_lists.size() > 0) {
			SelectListEntity brandEn = lv_lists.get(0);
			if (brandEn != null && brandEn.getSelectEn() != null) {
				brand_id = brandEn.getSelectEn().getChildId();
			}
			
			SelectListEntity otherEn = null;
			for (int i = 1; i < lv_lists.size(); i++) {
				otherEn = lv_lists.get(i);
				if (otherEn != null) {
					if (otherEn.getSelectEn() != null) {
						sb.append(otherEn.getSelectEn().getChildParamName()).append(",");
					}
				}
			}
			if (sb.toString().contains(",")) {
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			other_name = sb.toString();
		}
		
		if (isChange && ProductListActivity.instance != null) {
			mainEn.setMainLists(lv_lists);
			ProductListActivity.instance.updateScreenParameter(mainEn, brand_id, other_name);
		}
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
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
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
	}
	
}
