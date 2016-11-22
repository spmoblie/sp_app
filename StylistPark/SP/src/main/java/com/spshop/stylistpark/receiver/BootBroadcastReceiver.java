package com.spshop.stylistpark.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/11/21 0021.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            //开机自启动应用
            //context.startActivity(context.getPackageManager().getLaunchIntentForPackage("com.spshop.stylistpark"));
        }
    }

}
