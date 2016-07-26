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
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.tencent.stat.StatService;
import com.umeng.message.PushAgent;

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
		editor.putInt(AppConfig.KEY_HOME_CURRENT_INDEX, 0).commit(); //设置首页初始化默认页

		// 启动友盟推送服务
		PushAgent mPushAgent = PushAgent.getInstance(mContext);
		// 开启推送并设置注册的回调处理
		mPushAgent.enable();
		
		// androidManifest.xml指定本activity最先启动
		// 因此，MTA的初始化工作需要在本onCreate中进行
		// 在startStatService之前调用StatConfig配置类接口，使得MTA配置及时生效
		initMTAConfig(!AppConfig.IS_PUBLISH);
		String appkey = "Aqc1106650619";
		// 初始化并启动MTA
		// 第三方SDK必须按以下代码初始化MTA，其中appkey为规定的格式或MTA分配的代码。
		// 其它普通的app可自行选择是否调用
		try {
			// 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
			StatService.startStatService(this, appkey, com.tencent.stat.common.StatConstants.VERSION);
		} catch (Exception e) {
			// MTA初始化失败
			ExceptionUtil.handle(e);
		}

	}

	/**
	 * 根据不同的模式，建议设置的开关状态，可根据实际情况调整，仅供参考。
	 * 
	 * @param isDebugMode
	 *            根据调试或发布条件，配置对应的MTA配置
	 */
	private void initMTAConfig(boolean isDebugMode) {
		if (isDebugMode) { //调试时建议设置的开关状态
			// 查看MTA日志及上报数据内容
			StatConfig.setDebugEnable(true);
			// 禁用MTA对app未处理异常的捕获，方便开发者调试时，及时获知详细错误信息。
			// StatConfig.setAutoExceptionCaught(false);
			// StatConfig.setEnableSmartReporting(false);
			// Thread.setDefaultUncaughtExceptionHandler(new
			// UncaughtExceptionHandler() {
			//
			// @Override
			// public void uncaughtException(Thread thread, Throwable ex) {
			// logger.error("setDefaultUncaughtExceptionHandler");
			// }
			// });
			// 调试时，使用实时发送
			// StatConfig.setMTAPreferencesFileName("test");
			StatConfig.setStatSendStrategy(StatReportStrategy.INSTANT);
		} else { // 发布时，建议设置的开关状态，请确保以下开关是否设置合理
			// 禁止MTA打印日志
			StatConfig.setDebugEnable(false);
			// 根据情况，决定是否开启MTA对app未处理异常的捕获
			StatConfig.setAutoExceptionCaught(true);
			// 选择默认的上报策略
			StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
		}
	}

	private void goHomeActivity() {
		startActivity(new Intent(SplashActivity.this, HomeFragmentActivity.class));
		finish();
		// 设置Activity的切换效果
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		StatService.onResume(this);
		// 请求校验登录状态
		request(AppConfig.REQUEST_SV_GET_SESSIONS_CODE);
		// 延迟1秒跳转页面
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				AppApplication.statusHeight = DeviceUtil.getStatusBarHeight(SplashActivity.this);
				goHomeActivity();
			}
		}, 1000);
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
	public Object doInBackground(int requsetCode) throws Exception {
		switch (requsetCode) {
		case AppConfig.REQUEST_SV_GET_SESSIONS_CODE:
			return sc.checkLoginSession();
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
