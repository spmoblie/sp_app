package com.spshop.stylistpark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.receiver.umeng.MyUmengMessageHandler;
import com.spshop.stylistpark.receiver.umeng.MyUmengNotificationClickHandler;
import com.umeng.message.PushAgent;

public class PushManager {

	private static final String ALIAS_TYPE = "SPSHOP";
	private static PushManager instance = null;
	private SharedPreferences shared;
	private Context mContext;
	private PushAgent pa;
	private UserManager um;

	
	public static PushManager getInstance(){
		if (instance == null) {
			instance = new PushManager();
		}
		return instance;
	}

	private PushManager(){
		mContext = AppApplication.getInstance().getApplicationContext();
		shared = AppApplication.getSharedPreferences();
		um = UserManager.getInstance();
		pa = PushAgent.getInstance(mContext);
	}

	/**
	 * 初始化推送服务
	 */
	public void initPushService() {
		// 设置使用调试模式
		pa.setDebugMode(!AppConfig.IS_PUBLISH);
		// 自定义消息的处理
		pa.setMessageHandler(new MyUmengMessageHandler());
		// 自定义通知的处理
		pa.setNotificationClickHandler(new MyUmengNotificationClickHandler());
	}

	/**
	 * 初始化推送服务状态(开启或关闭)
	 */
	public void onPushDefaultStatus() {
		if (getPushStatus()) {
			startPushService();
		} else {
			closePushService();
		}
	}

	/**
	 * 统计应用启动数据
	 * 如果不调用此方法，不仅会导致按照"几天不活跃"条件来推送失效，
	 * 还将导致广播发送不成功以及设备描述红色等问题发生。
	 */
	public void onPushAppStartData() {
		pa.onAppStart();
	}

	/**
	 * 设置推送服务的权限
	 */
	public void setPushStatus(boolean status) {
		if (status) {
			shared.edit().putBoolean(AppConfig.KEY_PUSH_STATUS, true).apply();
			startPushService(); //先修改状态标记再开启、注册
		} else {
			closePushService(); //先注销、关闭再修改状态标记
			shared.edit().putBoolean(AppConfig.KEY_PUSH_STATUS, false).apply();
		}
	}

	/**
	 * 获取推送服务权限
	 */
	public Boolean getPushStatus() {
		return shared.getBoolean(AppConfig.KEY_PUSH_STATUS, true);
	}

	/**
	 * 开启推送服务
	 */
	private void startPushService() {
		pa.enable(); //开启
		LogUtil.i("PushManager", "Push Service 启动");
		// 推送服务异步开启，所以需要延时注册
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				registerPush(); //注册
			}
		}, 5000);
	}

	/**
	 * 关闭推送服务
	 */
	private void closePushService() {
		unregisterPush(); //注销
		pa.disable(); //关闭
		LogUtil.i("PushManager", "Push Service 关闭");
	}

	/**
	 * 注册账户信息
	 */
	public void registerPush(){
		if (getPushStatus() && um.checkIsLogined()) {
			try {
				// 设置用户别名
				pa.setExclusiveAlias(um.getUserId(), ALIAS_TYPE);
				LogUtil.i("PushManager", "设置别名：alias = " + um.getUserId() + " type = " + ALIAS_TYPE);
				// 异步设置用户标签
				final String tagStr = um.getUserRankName();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							pa.getTagManager().add(tagStr);
							LogUtil.i("PushManager", "设置标签：tag = " + tagStr);
						} catch (Exception e) {
							ExceptionUtil.handle(e);
						}
					}
				}).start();
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}
	
	/**
	 * 注销账户信息
	 */
	public void unregisterPush(){
		if (getPushStatus() && um.checkIsLogined()) {
			try {
				// 移除用户别名
				pa.deleteAlias(um.getUserId(), ALIAS_TYPE);
				LogUtil.i("PushManager", "移除别名：alias = " + um.getUserId() + " type = " + ALIAS_TYPE);
				// 异步删除用户标签
				/*final String tagStr = um.getUserRankName();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							pa.getTagManager().delete(tagStr);
							LogUtil.i("PushManager", "移除标签：tag = " + tagStr);
						} catch (Exception e) {
							ExceptionUtil.handle(e);
						}
					}
				}).start();*/
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}
	
}
