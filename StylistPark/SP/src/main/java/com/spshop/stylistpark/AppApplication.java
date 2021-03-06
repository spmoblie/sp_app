package com.spshop.stylistpark;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.SplashActivity;
import com.spshop.stylistpark.config.SharedConfig;
import com.spshop.stylistpark.db.SortDBService;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CleanDataManager;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.PushManager;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint({ "NewApi", "UseSparseArrays" })
public class AppApplication extends Application implements OnDataListener{

	private static AppApplication spApp = null;

	public static String clip_photo_path; //裁剪后相片的路径
	public static String version_name = ""; //当前版本号
	public static int screenWidth; //手机屏幕的宽
	public static int screenHeight; //手机屏幕的高
	public static int statusHeight; //手机状态栏高
	public static int mScale = 1;
	public static int clip_photo_type = 1; //记录裁剪相片的类型(1:圆形/2:方形)
	public static int network_current_state = 0; //记录当前网络的状态

	public static boolean loadDBData = false; //是否从本地数据库加载数据
	public static boolean isWXShare = false; //记录是否微信分享
	public static boolean isStartLoop = true; //记录是否开启循播
	public static boolean isStartHome = true; //记录是否允许重新启动HomeFragmentActivity

	public static ThemeEntity themeEn;
	public static ProductDetailEntity videoEn, imageEn;

	private static SharedPreferences shared;
	private static AsyncTaskManager atm;
	private static PushManager pushManager;
	private ServiceContext sc = ServiceContext.getServiceContext();

	//必须注册，Android框架调用Application
	@Override
	public void onCreate() {
		super.onCreate();
		spApp = this;
		shared = getSharedPreferences();
		atm = AsyncTaskManager.getInstance(spApp);
		pushManager = PushManager.getInstance();
		// 初始化推送服务SDK
		pushManager.initPushService();
		// 初始化应用统计SDK
		MobclickAgent.setDebugMode(!AppConfig.IS_PUBLISH); //设置调试模式
		// 设置是否对日志信息进行加密, 默认false(不加密).
		MobclickAgent.enableEncrypt(true);
		// 禁止默认的页面统计方式，在onResume()和onPause()手动添加代码统计;
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.setScenarioType(spApp, MobclickAgent.EScenarioType.E_UM_NORMAL);

		// 获取手机型号及屏幕的宽高
		screenWidth = DeviceUtil.getDeviceWidth(spApp);
		screenHeight = DeviceUtil.getDeviceHeight(spApp);
		// 判定是否为Pad
		LogUtil.i("device", "手机型号："+ DeviceUtil.getModel() + " 宽："+screenWidth + " / 高："+screenHeight);

		// 设置每天第一次启动App时清除与日期关联的缓存标志
		long newDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		long oldDay = shared.getLong(AppConfig.KEY_LOAD_SV_DATA_DAY, 0);
		if ((newDay == 1 && oldDay != 1) || newDay - oldDay > 0) {
			clearSharedLoadSVData();
			shared.edit().putLong(AppConfig.KEY_LOAD_SV_DATA_DAY, newDay).commit();
		}

		// 程序崩溃时触发线程
		Thread.setDefaultUncaughtExceptionHandler(restartHandler);

		// 设置App字体不随系统字体变化
		initDisplayMetrics();

		// 初始化异步加载图片的jar配置
		initImageLoader(spApp);

		// Facebook SDK初始化
		FacebookSdk.sdkInitialize(getApplicationContext());
	}

	public static synchronized AppApplication getInstance() {
		return spApp;
	}

	public static SharedPreferences getSharedPreferences() {
		if (shared == null) {
			shared = new SharedConfig(spApp).GetConfig();
		}
		return shared;
	}

	/**
	 * 清除联网加载数据控制符的缓存
	 */
	public void clearSharedLoadSVData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				SortDBService.getInstance(spApp).deleteAll(); //清空数据库
				clearImageLoaderCache(); //清除图片缓存
				CleanDataManager.cleanAppTemporaryData(spApp); //清除临时缓存
				CleanDataManager.cleanCustomCache(AppConfig.SAVE_PATH_MEDIA_DICE); //清除视频缓存
			}
		}).start();
		shared.edit().putBoolean(AppConfig.KEY_LOAD_SORT_DATA, true).apply();
	}

	/**
	 * 设置App字体不随系统字体变化
	 */
	public static void initDisplayMetrics() {
		DisplayMetrics displayMetrics = spApp.getResources().getDisplayMetrics();
		displayMetrics.scaledDensity = displayMetrics.density;
	}

	/**
	 * 异步加载图片的jar配置
	 */
	public static void initImageLoader(Context mContext){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 清除图片缓存
	 */
	public static void clearImageLoaderCache() {
		ImageLoader.getInstance().clearDiscCache();
		ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * 保存图片对象到指定文件并通知相册更新相片
	 */
	public static void saveBitmapFile(Bitmap bm, File file, int compress) {
		if (bm == null || file == null) {
			CommonTools.showToast(spApp.getResources().getString(R.string.photo_show_save_fail), 2000);
			return;
		}
		try {
			BitmapUtil.save(bm, file, compress);
			if (file.getAbsolutePath().contains(AppConfig.SAVE_PATH_IMAGE_SAVE)) {
				updatePhoto(file); //需要保存的图片通知更新相册
			}
		} catch (IOException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 通知相册更新相片
	 */
	public static void updatePhoto(File file) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		spApp.sendBroadcast(intent);
	}

	/**
	 * 应用数据统计之页面启动
	 */
	public static void onPageStart(String pageName) {
		MobclickAgent.onPageStart(pageName);
	}

	/**
	 * 应用数据统计之页面启动
	 */
	public static void onPageStart(Activity activity, String pageName) {
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(activity);
	}

	/**
	 * 应用数据统计之页面关闭
	 */
	public static void onPageEnd(Context ctx, String pageName) {
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(ctx);
	}

	/**
	 * 推送服务统计应用启动数据
	 */
	public static void onPushAppStartData() {
		pushManager.onPushAppStartData();
	}

	/**
	 * 初始化推送服务状态
	 */
	public static void onPushDefaultStatus() {
		pushManager.onPushDefaultStatus();
	}

	/**
	 * 设置推送服务的权限
	 */
	public static void setPushStatus(boolean isStatus) {
		pushManager.setPushStatus(isStatus);
	}

	/**
	 * 获取推送服务的权限
	 */
	public static boolean getPushStatus() {
		return pushManager.getPushStatus();
	}

	/**
	 * 注册或注销用户信息至推送服务
	 */
	public static void onPushRegister(boolean isRegister) {
		if (isRegister) {
			pushManager.registerPush();
		} else {
			pushManager.unregisterPush();
		}
	}

	/**
	 * App注销登出统一入口
	 */
	public static void AppLogout(boolean isSend) {
		AppManager.getInstance().AppLogout(spApp);
		if (isSend) {
			atm.request(AppConfig.REQUEST_SV_POST_LOGOUT_CODE, spApp); //通知服务器登出
		}
		// 刷新头像
		BaseActivity.updateActivityData(5);
	}

	// 创建服务用于捕获崩溃异常
	private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			restartApp(); //发生崩溃异常时,重启应用
		}
	};

	// 重启应用
	public void restartApp() {
		Intent intent = new Intent(spApp, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		spApp.startActivity(intent);
		AppManager.getInstance().AppExit(spApp);
		// 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
			case AppConfig.REQUEST_SV_POST_LOGOUT_CODE:
				String uri = AppConfig.URL_COMMON_INDEX_URL;
				List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
				params.add(new MyNameValuePair("app", "logout"));
				return sc.loadServerDatas("AppApplication", AppConfig.REQUEST_SV_POST_LOGOUT_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (spApp == null) return;
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (spApp == null) return;
		CommonTools.showToast(String.valueOf(result), 1000);
	}

}