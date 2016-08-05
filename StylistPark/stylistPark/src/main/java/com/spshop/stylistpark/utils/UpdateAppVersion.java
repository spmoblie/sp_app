package com.spshop.stylistpark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.dialog.AppVersionDialog;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.dialog.LoadDialog;
import com.spshop.stylistpark.entity.UpdateVersionEntity;
import com.spshop.stylistpark.service.ServiceContext;

public class UpdateAppVersion {

	private static UpdateAppVersion instance;
	private SharedPreferences shared;
	private Context mContext;
	private DialogManager dm;
	private String curVersionName;
	private int curVersionCode;
	private boolean isNewVersion = false;
	private boolean isHomeIndex = false;

	public static UpdateAppVersion getInstance(Context context, boolean isHomeIndex) {
		if (instance == null) {
			instance = new UpdateAppVersion(context, isHomeIndex);
		}
		return instance;
	}
	
	private UpdateAppVersion(Context context, boolean isHomeIndex) {
		mContext = context;
		dm = DialogManager.getInstance(mContext);
		this.isHomeIndex = isHomeIndex;
		shared = AppApplication.getSharedPreferences();
		startCheckAppVersion();
	}

	public boolean isNewVersion() {
		return isNewVersion;
	}

	public void clearInstance() {
		instance = null;
		if (dm != null) {
			dm.clearInstance();
		}
	}

	private void startCheckAppVersion() {
		getAppVersionInfo();
		// 检测网络状态
		if (NetworkUtil.networkStateTips()) {
			if (!isHomeIndex) { //非首页
				LoadDialog.show(mContext);
			}
			new HttpTask().execute(); //异步检查版本信息
		} else {
			CommonTools.showToast(mContext.getString(R.string.network_fault), 1000);
			clearInstance();
		}
	}

	/**
	 * 获取App当前版本信息
	 */
	private void getAppVersionInfo() {
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			curVersionName = pinfo.versionName;
			curVersionCode = pinfo.versionCode;
			AppApplication.version_name = curVersionName;
		} catch (NameNotFoundException e) {
			ExceptionUtil.handle(e);
			clearInstance();
		}
	}

	/**
	 * 检测版本的异步任务
	 */
	class HttpTask extends AsyncTask<String, Void, Object> {

		protected UpdateVersionEntity doInBackground(String... url) {
			UpdateVersionEntity versionEn = null;
			try {
				versionEn = ServiceContext.getServiceContext().checkVersionUpdate(0, curVersionName);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				clearInstance();
			}
			return versionEn;
		}

		protected void onPostExecute(Object result) {
			if (result != null) {
				AppVersionDialog appDialog = new AppVersionDialog(mContext, dm);
				if (!isHomeIndex) {
					LoadDialog.hidden();
				}
				UpdateVersionEntity entity = (UpdateVersionEntity) result;
				if (entity != null && entity.getErrCode() == 0) {
					String version = entity.getVersion();
					String description = entity.getDescription();
					String address = entity.getUrl();
					boolean force = entity.isForce();
					boolean lessThanMin = false;
					if (version.contains(".")) { //检查是否需要更新
						lessThanMin = compareVersion(version);
					} else {
						lessThanMin = compareVersionCode(version);
					}
					if (force) { //是否需要强制更新
						appDialog.forceUpdateVersion(address, description);
						isNewVersion = true;
					} else if (!isNewVersion && lessThanMin) { //检测到新版本
						long newTime = System.currentTimeMillis();
						long oldTime = shared.getLong(AppConfig.KEY_UPDATE_VERSION_LAST_TIME, 0);
						if (newTime - oldTime > 86400000) { //设置首页检测版本的频率为一天
							appDialog.foundNewVersion(address, description);
							shared.edit().putLong(AppConfig.KEY_UPDATE_VERSION_LAST_TIME, newTime).apply();
							isNewVersion = true;
						} else {
							if (!isHomeIndex) {
								appDialog.foundNewVersion(address, description);
								isNewVersion = true;
							}
						}
					} else if (!isNewVersion) {
						if (!isHomeIndex) {
							appDialog.showStatus(mContext.getString(R.string.dialog_version_new)); //提示已是最新版本
						}
					}
				} else {
					appDialog.showStatus(mContext.getString(R.string.toast_server_busy));
				}
			} else {
			}
			clearInstance();
		}
	}

	/**
	 * 比较纯数字版本号判定是否需要更新
	 */
	private boolean compareVersionCode(String minVersion) {
		if (curVersionCode == 0) {
			getAppVersionInfo();
		}
		int newVersion = StringUtil.getInteger(minVersion);
		if (curVersionCode < newVersion) {
			return true;
		}
		return false;
	}

	/**
	 * 比较常规版本号判定是否需要更新
	 */
	private boolean compareVersion(String minVersion) {
		boolean ok = false;
		if (StringUtil.isNull(minVersion)){
			return ok;
		}
		if (StringUtil.isNull(curVersionName)) {
			getAppVersionInfo();
		}
		String[] minValues = minVersion.split("\\.");
		int minlength = minValues.length;
		String[] curValues = curVersionName.split("\\.");
		int curLength = curValues.length;
		if (minlength > 1 && curLength > 1) {
			int minFirst = Integer.parseInt(minValues[0]);
			int curFirst = Integer.parseInt(curValues[0]);
			if (curFirst < minFirst) {
				ok = true; //版本号第一位数小于时更新
			} else if (curFirst == minFirst) {
				int minSecond = Integer.parseInt(minValues[1]);
				int curSecond = Integer.parseInt(curValues[1]);
				if (curSecond < minSecond) {
					ok = true; //版本号第二位数小于时更新
				} else if (curSecond == minSecond) {
					int minThree = 0;
					int curThree = 0;
					if (curLength > 2) {
						curThree = Integer.parseInt(curValues[2]);
					}
					if (minlength > 2) {
						minThree = Integer.parseInt(minValues[2]);
					}
					if (curThree < minThree) {
						ok = true; //版本号第三位数小于时更新
					} 
				}
			}
		}
		return ok;
	}

}
