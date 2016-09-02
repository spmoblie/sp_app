package com.spshop.stylistpark.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;

/**
 * 网络状态监听器
 */
public class NetStateChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
			if(activeNetwork == null){
				AppApplication.network_current_state = -1;
				LogUtil.i("NetSatateChangedReceiver", "用户关闭了网络");
			}else{
				AppApplication.network_current_state = 1;
				NetworkInfo wifiNetwork = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if(wifiNetwork != null){
					if(wifiNetwork.isConnected()){
						AppApplication.network_current_state = 2;
						LogUtil.i("NetSatateChangedReceiver", "用户打开了wifi网络");
					}
				}
				NetworkInfo mobileNetwork = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(mobileNetwork != null){
					if(mobileNetwork.isConnected()){
						AppApplication.network_current_state = 3;
						LogUtil.i("NetSatateChangedReceiver", "用户打开了移动网络");
					}
				}
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}

}
