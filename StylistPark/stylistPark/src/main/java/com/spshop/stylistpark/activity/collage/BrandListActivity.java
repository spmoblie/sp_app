package com.spshop.stylistpark.activity.collage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.BrandIndexDisplayAdapter;
import com.spshop.stylistpark.adapter.IndexDisplayAdapter.OnIndexDisplayItemClick;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.IndexDisplay;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.IndexDisplayTool;
import com.spshop.stylistpark.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BrandListActivity extends BaseActivity {

	public static final String INTENT_SELECTED_BRAND = "intent_selected_brand";

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_BRAND_LIST_SUCCESS:
				init();
				isLoading = false;
				break;
			case GET_BRAND_LIST_FAIL:
				isLoading = false;
				showErrorDialog(null);
				break;
			case GET_PRODUCT_LIST_FAIL_NO_CONNECTION:
				showErrNoNetworkLayout();
				isLoading = false;
				break;
			default:
				break;
			}
			stopAnimation();
		}
	};

	String TAG = "BrandListActivity";
	List<BrandEntity> brandList;

	private static final int GET_BRAND_LIST_SUCCESS = 0503;
	private static final int GET_BRAND_LIST_FAIL = 0504;
	private static final int GET_PRODUCT_LIST_FAIL_NO_CONNECTION = 0505;
	private BrandListThread mThreadBrandList = null;

	boolean isLoading;

	IndexDisplayFragment indexDisplayFragment;
	BrandIndexDisplayAdapter indexDisplayAdapter;

	EditText brandList_searchEditText;
	ImageView brandList_delIconImageView;

	// err
	ViewGroup brandList_errNoNetworkLayout;
	Button errNoNetwork_Layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brand_list);
		
		setTitle(R.string.filter_select_brand);

		brandList_errNoNetworkLayout = (ViewGroup) findViewById(R.id.brandList_errNoNetworkLayout);
		brandList_errNoNetworkLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		errNoNetwork_Layout = (Button) findViewById(R.id.errNoNetwork_Layout);
		errNoNetwork_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				brandList_errNoNetworkLayout.setVisibility(View.GONE);
				startAnimation();
				mThreadBrandList = new BrandListThread();
				mThreadBrandList.start();
			}
		});

		brandList_searchEditText = (EditText) findViewById(R.id.brandList_searchEditText);
		brandList_searchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				List<BrandEntity> searchBrandList = searchBrand(s.toString());
				indexDisplayFragment.updateDataList(
						IndexDisplayTool.buildIndexListChineseAndEng(BrandListActivity.this, searchBrandList));
				if (s.toString().isEmpty()) {
					brandList_delIconImageView.setVisibility(View.GONE);
				} else {
					brandList_delIconImageView.setVisibility(View.VISIBLE);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
		});
		brandList_delIconImageView = (ImageView) findViewById(R.id.brandList_delIconImageView);
		brandList_delIconImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				brandList_searchEditText.setText("");
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (brandList == null || brandList.size() == 0) {
			startAnimation();
			mThreadBrandList = new BrandListThread();
			mThreadBrandList.start();
		}
	}

	public void init() {
		BrandIndexDisplayAdapter adapter = new BrandIndexDisplayAdapter(mContext);
		adapter.setOnIndexDisplayItemClick(new OnIndexDisplayItemClick() {

			@Override
			public void onIndexDisplayItemClick(IndexDisplay indexDisplay) {
				BrandEntity brand = (BrandEntity) indexDisplay;
				Intent data = new Intent();
				data.putExtra(INTENT_SELECTED_BRAND, brand);
				setResult(RESULT_OK, data);
				finish();
			}
		});
		indexDisplayFragment = IndexDisplayFragment.newInstance();
		indexDisplayFragment.setDataList(IndexDisplayTool.buildIndexListChineseAndEng(this, brandList));
		indexDisplayFragment.setAdapter(adapter);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.brandList_container, indexDisplayFragment);
		ft.commit();
	}

	public List<BrandEntity> searchBrand(String s) {
		List<BrandEntity> result = null;
		for (int i = 0; i < brandList.size(); i++) {
			BrandEntity brand = brandList.get(i);
			String brandName = brand.getName();
			if (brandName.toLowerCase(Locale.US).contains(s.toLowerCase(Locale.US))) {
				if (result == null) {
					result = new ArrayList<BrandEntity>();
				}
				result.add(brand);
			}
		}
		return result;
	}

	public void addFragment(IndexDisplayFragment fragment, String name) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.brandList_container, fragment, name);
		ft.commit();
		LogUtil.i(TAG, "addFragment(): fragment added. " + name);
	}

	public void showErrNoNetworkLayout() {
		brandList_errNoNetworkLayout.setVisibility(View.VISIBLE);
	}

	private class BrandListThread extends Thread {

		public void run() {
			Message msg = new Message();
			try {
				List<BrandEntity> mbrandList = ServiceContext.getServiceContext().getCollageBrandList(); 

				if (mbrandList != null && mbrandList.size() >= 1) {
					brandList = new ArrayList<BrandEntity>();
					for (int i = 0; i < mbrandList.size(); i++) {
						BrandEntity brand = new BrandEntity();
						brand.setName(mbrandList.get(i).getName());
						brandList.add(brand);
					}
					brandList = mbrandList;
					msg.what = GET_BRAND_LIST_SUCCESS;
				} else {
					msg.what = GET_BRAND_LIST_FAIL;
				}
			} catch (Exception e) {
				if (brandList != null)
					brandList.clear();
				brandList = null;
				msg.what = GET_BRAND_LIST_FAIL;
				ExceptionUtil.handle(e);
			}
			if (!interrupted()) {
				mHandler.sendMessage(msg);
			}
		}
	}

}
