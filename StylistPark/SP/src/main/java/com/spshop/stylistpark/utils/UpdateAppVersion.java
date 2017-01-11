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
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.UpdateVersionEntity;
import com.spshop.stylistpark.service.ServiceContext;

import java.util.ArrayList;
import java.util.List;

public class UpdateAppVersion {

	private static UpdateAppVersion instance;
	private SharedPreferences shared;
	private Context mContext;
	private DialogManager dm;
	private String curVersionName;
	private int curVersionCode;
	private boolean isHomeIndex = false;

	public static UpdateAppVersion getInstance(Context context, boolean isHomeIndex) {
		if (instance == null) {
			syncInit(context, isHomeIndex);
		}
		return instance;
	}

	private static synchronized void syncInit(Context context, boolean isHomeIndex) {
		if (instance == null) {
			instance = new UpdateAppVersion(context, isHomeIndex);
		}
	}
	
	private UpdateAppVersion(Context context, boolean isHomeIndex) {
		mContext = context;
		dm = DialogManager.getInstance(mContext);
		this.isHomeIndex = isHomeIndex;
		shared = AppApplication.getSharedPreferences();
		startCheckAppVersion();
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
				String uri = AppConfig.URL_COMMON_INDEX_URL + "?app=app";
				List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
				params.add(new MyNameValuePair("id", "1"));
				params.add(new MyNameValuePair("version", curVersionName));
				BaseEntity baseEn = ServiceContext.getServiceContext().loadServerDatas(
						"UpdateAppVersion", AppConfig.REQUEST_SV_POST_VERSION_CODE, uri, params, HttpUtil.METHOD_POST);
				if (baseEn != null) {
					versionEn = (UpdateVersionEntity) baseEn;
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				clearInstance();
			}
			return versionEn;
		}

		protected void onPostExecute(Object result) {
			AppVersionDialog appDialog = new AppVersionDialog(mContext, dm);
			if (!isHomeIndex) {
				LoadDialog.hidden();
			}
			if (result != null) {
				UpdateVersionEntity entity = (UpdateVersionEntity) result;
				String version = entity.getVersion();
				String description = entity.getDescription();
				String address = entity.getUrl();
				boolean isForce = entity.isForce();
				boolean isUpdate;
				if (version.contains(".")) { //检查是否需要更新
					isUpdate = compareVersion(version);
				} else {
					isUpdate = compareVersionCode(version);
				}
				if (isUpdate) { //检测到新版本
					if (isForce) { //是否强制更新
						appDialog.forceUpdateVersion(address, description);
					} else {
						long newTime = System.currentTimeMillis();
						long oldTime = shared.getLong(AppConfig.KEY_UPDATE_VERSION_LAST_TIME, 0);
						if (newTime - oldTime > 86400000) { //设置首页检测版本的频率为一天
							appDialog.foundNewVersion(address, description);
							shared.edit().putLong(AppConfig.KEY_UPDATE_VERSION_LAST_TIME, newTime).apply();
						} else {
							if (!isHomeIndex) {
								appDialog.foundNewVersion(address, description);
							}
						}
					}
				} else {
					if (!isHomeIndex) {
						appDialog.showStatus(mContext.getString(R.string.dialog_version_new)); //提示已是最新版本
					}
				}
			} else {
				if (!isHomeIndex) {
					appDialog.showStatus(mContext.getString(R.string.toast_server_busy));
				}
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
		boolean isUpdate = false;
		if (StringUtil.isNull(minVersion)){
			return isUpdate;
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
				isUpdate = true; //版本号第一位数小于时更新
			} else if (curFirst == minFirst) {
				int minSecond = Integer.parseInt(minValues[1]);
				int curSecond = Integer.parseInt(curValues[1]);
				if (curSecond < minSecond) {
					isUpdate = true; //版本号第二位数小于时更新
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
						isUpdate = true; //版本号第三位数小于时更新
					} 
				}
			}
		}
		return isUpdate;
	}

}
