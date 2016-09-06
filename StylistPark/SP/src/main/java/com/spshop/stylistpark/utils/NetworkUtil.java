package com.spshop.stylistpark.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;

import java.util.List;

public class NetworkUtil {

	/**
	 * 检查网络状态并弹出对话框提醒
	 * @param context
	 */
	public static void checkNetworkState(final Context context){
		try {
			ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo=manager.getActiveNetworkInfo();
			if(networkInfo == null){
				AlertDialog.Builder dialog = new Builder(context);
				dialog.setTitle(R.string.network);
				dialog.setMessage(R.string.network_closed);
				dialog.setPositiveButton(R.string.setting, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							//打开系统的网络设置界面
							Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
							context.startActivity(intent);
						} catch (Exception e) {
							ExceptionUtil.handle(e);
						}
					}
				});
				dialog.setNeutralButton(R.string.cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}
	
	/**
	 * 网络是否可用
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 网络连接提示
	 */
	public static boolean networkStateTips() {
		return isNetworkAvailable();
	}

	/**
	 * Gps是否打开
	 */
	public static boolean isGpsEnabled() {
		LocationManager locationManager = ((LocationManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled() {
		Context ctx = AppApplication.getInstance().getApplicationContext();
		ConnectivityManager mgrConn = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
	 */
	public static boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否3G网络
	 */
	public static boolean is3G() {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
}
