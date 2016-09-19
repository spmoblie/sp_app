package com.spshop.stylistpark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * App首页欢迎界面
 */
public class SplashActivity extends BaseActivity {

	private static final String TAG = "SplashActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		LangCurrTools.setLanguage(this, LangCurrTools.getLanguage()); //更新设置的系统语言
		setHeadVisibility(View.GONE); //隐藏父类组件
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈

		// 非推送通知打开首页
		boolean isPushOpen = shared.getBoolean(AppConfig.KEY_PUSH_PAGE_MEMBER, false);
		if (!isPushOpen) {
			editor.putInt(AppConfig.KEY_HOME_CURRENT_INDEX, -1).commit(); //设置首页初始化默认页
		}

		// 初始化推送服务状态(开启或关闭)
		AppApplication.onPushDefaultStatus();
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		// 请求校验登录状态
		request(AppConfig.REQUEST_SV_GET_SESSIONS_CODE);
		// 延迟1秒跳转页面
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				goHomeActivity();
			}
		}, 1000);
		super.onResume();
	}

	private void goHomeActivity() {
		AppApplication.statusHeight = DeviceUtil.getStatusBarHeight(SplashActivity.this);
		startActivity(new Intent(SplashActivity.this, HomeFragmentActivity.class));
		finish();
		// 设置Activity的切换效果
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
	}

	@Override
	public Object doInBackground(int requsetCode) throws Exception {
		switch (requsetCode) {
		case AppConfig.REQUEST_SV_GET_SESSIONS_CODE:
			String uri = AppConfig.URL_COMMON_MY_URL;
			List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
			params.add(new MyNameValuePair("app", "sessions"));
			return sc.loadServerDatas(AppConfig.REQUEST_SV_GET_SESSIONS_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_SESSIONS_CODE:
			if (result != null && ((BaseEntity) result).getErrCode() == AppConfig.ERROR_CODE_LOGOUT) { //登录失效
				AppApplication.AppLogout(false);
			}else {
				if (!UserManager.getInstance().checkIsLogined()) {
					AppApplication.AppLogout(true);
				}
			}
			break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}

}
