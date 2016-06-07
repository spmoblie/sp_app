package com.spshop.stylistpark.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.tencent.stat.StatService;

public class ChildFragmentTwo extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentTwo";
	public static ChildFragmentTwo instance = null;
	
	private Context mContext;
	private SharedPreferences shared;

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
		mContext = getActivity().getApplicationContext();
		// 初始化偏好设置
		shared = AppApplication.getSharedPreferences();
		
		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_two, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(getActivity(), e);
		}
		return view;
	}

	private void findViewById(View view) {
		
	}

	private void initView() {
		
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		StatService.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception{
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		stopAnimation();
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		stopAnimation();
		CommonTools.showToast(mContext, String.valueOf(result), 1000);
	}
	
	/**
	 * 显示缓冲动画
	 */
	private void startAnimation() {
		
	}
	
	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}

