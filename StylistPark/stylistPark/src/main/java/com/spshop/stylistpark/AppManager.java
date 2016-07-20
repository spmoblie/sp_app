package com.spshop.stylistpark;

import android.app.Activity;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.spshop.stylistpark.utils.CleanDataManager;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.Stack;

/**
 * 负责记录Activity的启动情况并控制Activity的退出
 */
public class AppManager {
	
	private static Stack<Activity> mActivityStack;
	private static AppManager mAppManager;

	private AppManager() {
		
	}

	/**
	 * 单一实例
	 */
	public static AppManager getInstance() {
		if (mAppManager == null) {
			mAppManager = new AppManager();
		}
		return mAppManager;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}

	/**
	 * 获取栈顶Activity（堆栈中最后一个压入的）
	 */
	public Activity getTopActivity() {
		return mActivityStack.lastElement();
	}

	/**
	 * 结束栈顶Activity（堆栈中最后一个压入的）
	 */
	public void killTopActivity() {
		killActivity(mActivityStack.lastElement());
	}

	/**
	 * 结束指定的Activity
	 */
	public void killActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void killActivity(Class<?> cls) {
		for (Activity activity : mActivityStack) {
			if (activity.getClass().equals(cls)) {
				killActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void killAllActivity() {
		for (int i = 0, size = mActivityStack.size(); i < size; i++) {
			if (null != mActivityStack.get(i)) {
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			//清除临时缓存
			CleanDataManager.cleanAppTemporaryData(context);
			killAllActivity();
//			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}
	
	/**
	 * App注销登录
	 */
	public void AppLogout(final Context ctx) {
		UserManager.getInstance().clearUserLoginInfo(ctx);
		clearAllCookie(ctx);
	}
	
	/**
	 * 清除所有缓存Cookie
	 */
	public void clearAllCookie(Context ctx) {
		CookieSyncManager.createInstance(ctx);
		CookieSyncManager.getInstance().startSync(); 
        CookieManager.getInstance().removeAllCookie();
	}
}
