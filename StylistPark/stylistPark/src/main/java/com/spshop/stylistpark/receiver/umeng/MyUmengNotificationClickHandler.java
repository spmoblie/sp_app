package com.spshop.stylistpark.receiver.umeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class MyUmengNotificationClickHandler extends UmengNotificationClickHandler {

    @Override
    public void autoUpdate(Context context, UMessage uMessage) {
        super.autoUpdate(context, uMessage);
    }

    @Override
    public void openUrl(Context context, UMessage uMessage) {
        super.openUrl(context, uMessage);
    }

    @Override
    public void openActivity(Context context, UMessage uMessage) {
        super.openActivity(context, uMessage);
    }

    @Override
    public void launchApp(Context context, UMessage uMessage) {
        //页面跳转路径参数设置
        SharedPreferences shared = AppApplication.getSharedPreferences();
        shared.edit().putInt(AppConfig.KEY_HOME_CURRENT_INDEX, 4).commit();
        shared.edit().putBoolean(AppConfig.KEY_PUSH_PAGE_MEMBER, true).commit();
        //判断app进程是否存活
        if(DeviceUtil.isAppAlive(context, "com.spshop.stylistpark")){
            LogUtil.i("UmengPush", "the app process is alive");
            //如果App存活的话，就直接启动Activity，但要考虑一种情况，就是app的进程虽然仍然在
            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收。
            if (HomeFragmentActivity.instance != null) {
                LogUtil.i("UmengPush", "HomeFragmentActivity != null");
                HomeFragmentActivity.instance.pushGoToMemberListActivity();
            } else {
                LogUtil.i("UmengPush", "HomeFragmentActivity is null");
                //该Handler是在BroadcastReceiver中被调用。
                //因此若需启动Activity，需为Intent添加Flag：Intent.FLAG_ACTIVITY_NEW_TASK，否则无法启动Activity。
                Intent mainIntent = new Intent(context, HomeFragmentActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
                //创建待启动的Activity队列
                //Intent detailIntent = new Intent(context, MemberListActivity.class);
                //detailIntent.putExtra("topType", MemberListActivity.TYPE_1);
                //Intent[] intents = {mainIntent, detailIntent};
                //context.startActivities(intents);
            }
        }else {
            LogUtil.i("UmengPush", "the app process is dead");
            //如果App进程已经被杀死，则重新启动App。
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.spshop.stylistpark");
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
        }
    }

    @Override
    public void dealWithCustomAction(Context context, UMessage uMessage) {
        super.dealWithCustomAction(context, uMessage);
    }
}
