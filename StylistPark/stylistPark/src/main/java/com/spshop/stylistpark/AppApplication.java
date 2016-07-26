package com.spshop.stylistpark;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.spshop.stylistpark.config.SharedConfig;
import com.spshop.stylistpark.receiver.umeng.MyUmengMessageHandler;
import com.spshop.stylistpark.receiver.umeng.MyUmengNotificationClickHandler;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.umeng.message.PushAgent;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

@SuppressLint({ "NewApi", "UseSparseArrays" })
public class AppApplication extends Application implements OnDataListener{
	
	private static AppApplication spApp = null;
	
	public static String model = ""; //手机型号
	public static String version_name = ""; //当前版本号
	public static String clip_photo_path; //裁剪后相片的路径
	public static int screenWidth; //手机屏幕的宽
	public static int screenHeight; //手机屏幕的高
	public static int statusHeight; //手机状态栏高
	public static int clip_photo_type = 1; //记录裁剪相片的类型(1:圆形/2:方形)
	public static int network_current_state = 0; //记录当前网络的状态
	
	public static boolean loadDBData = false; //是否从本地数据库加载数据
	public static boolean loadSVData_category = true; //是从服务器加载商品分类数据
	public static boolean isWXShare = false; //记录是否微信分享
	public static boolean isStartHome = true; //记录是否允许重新启动HomeFragmentActivity

	public static DisplayImageOptions defaultOptions, headOptions;
	private static SharedPreferences shared;
	private static AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();
	private RequestQueue mRequestQueue;
	private Calendar calendar = Calendar.getInstance();

	
	//必须注册，Android框架调用Application
	@Override
	public void onCreate() {
		super.onCreate();
		spApp = this;
		shared = getSharedPreferences();
		atm = AsyncTaskManager.getInstance(spApp);

	    // 获取手机型号及屏幕的宽高
		screenWidth = DeviceUtil.getDeviceWidth(spApp);
		screenHeight = DeviceUtil.getDeviceHeight(spApp);
		model = DeviceUtil.getModel();
		LogUtil.i("device", "手机型号："+ model + "宽："+screenWidth + " / 高："+screenHeight);

		// 设置每天第一次启动App时清除与日期关联的缓存标志
		long newDay = calendar.get(Calendar.DAY_OF_MONTH);
		long oldDay = shared.getLong(AppConfig.KEY_LOAD_SV_DATA_DAY, 00);
		if ((newDay == 1 && oldDay != 1) || newDay - oldDay > 0) {
			clearSharedLoadSVData();
			shared.edit().putLong(AppConfig.KEY_LOAD_SV_DATA_DAY, newDay).commit();
		}

		// 设置App字体不随系统字体变化
		initDisplayMetrics();
		
		// 初始化异步加载图片的第三jar配置
		initImageLoader(spApp);

		// 启动友盟推送服务
		PushAgent mPushAgent = PushAgent.getInstance(spApp);
		mPushAgent.setDebugMode(true);
		// 自定义消息处理
		mPushAgent.setMessageHandler(new MyUmengMessageHandler());
		// 自定义通知处理
		mPushAgent.setNotificationClickHandler(new MyUmengNotificationClickHandler());
	}
	
	/**
	 * 清除偏好设置保存加载远程服务器数据的记录
	 */
	private void clearSharedLoadSVData(){
		
	}
	
    public static synchronized AppApplication getInstance() {
        return spApp;
    }
	
	/**
	 * 异步加载图片的第三jar配置
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
	 * 设置App字体不随系统字体变化
	 */
	public static void initDisplayMetrics() {
		DisplayMetrics displayMetrics = spApp.getResources().getDisplayMetrics();
		displayMetrics.scaledDensity = displayMetrics.density;
	}
	
	/**
	 * 创建图片加载器对象
	 * 
	 * @param circular 加载圆形效果的数值
	 * @param drawableId 默认图片Id
	 */
	public static DisplayImageOptions getImageOptions(int circular, int drawableId, boolean isCache) {
		return new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(circular))
				.showImageForEmptyUri(drawableId)
				.showImageOnFail(drawableId)
				.cacheInMemory(isCache) // 内存缓存
				.cacheOnDisc(isCache) // sdcard缓存
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
	}

	/**
	 * 获取默认图片加载器对象
	 */
	public static DisplayImageOptions getDefaultImageOptions() {
		if (defaultOptions == null) {
			defaultOptions = new DisplayImageOptions.Builder()
					//.displayer(new FadeInBitmapDisplayer(300)) // 图片加载好后渐入的动画时间
					.showImageForEmptyUri(R.drawable.bg_img_default)
					.showImageOnFail(R.drawable.bg_img_default)
					.cacheInMemory(true) // 内存缓存
					.cacheOnDisc(true) // sdcard缓存
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		}
		return defaultOptions;
	}

	/**
	 * 获取头像图片加载器对象
	 */
	public static DisplayImageOptions getHeadImageOptions() {
		if (headOptions == null) {
			headOptions = new DisplayImageOptions.Builder()
					.displayer(new RoundedBitmapDisplayer(90))
					.showImageForEmptyUri(R.drawable.head_portrait)
					.showImageOnFail(R.drawable.head_portrait)
					.cacheInMemory(true) // 内存缓存
					.cacheOnDisc(true) // sdcard缓存
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		}
		return headOptions;
	}

	public static SharedPreferences getSharedPreferences() {
		if (shared == null) {
			shared = new SharedConfig(spApp).GetConfig();
		}
		return shared;
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
			if (file.getPath().contains(AppConfig.SAVE_IMAGE_PATH_LONG)) {
				updatePhoto(file); //需要保存的图片更新相册
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
	 * App注销登入统一入口
	 */
	public static void AppLogout(boolean sendOr){
		AppManager.getInstance().AppLogout(spApp);
		if (sendOr) {
			atm.request(AppConfig.REQUEST_SV_POST_LOGOUT_CODE, spApp); //通知服务器登出
		}
	}
	
	/**
	 * 获取HttpUrl语言、货币参数
	 */
	public static String getHttpUrlLangCurValueStr(){
		return "&lang=" + LangCurrTools.getLanguageHttpUrlValueStr()
			 + "&currency=" + LangCurrTools.getCurrencyHttpUrlValueStr();
	}
	
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        
        return mRequestQueue;
    }
    
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_LOGOUT_CODE:
			return sc.postLogoutRequest();
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		CommonTools.showToast(String.valueOf(result), 1000);
	}

}


