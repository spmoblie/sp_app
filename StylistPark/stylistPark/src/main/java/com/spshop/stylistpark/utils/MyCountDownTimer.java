package com.spshop.stylistpark.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class MyCountDownTimer  extends CountDownTimer {

    private Context mContext;
    private MyTimerCallback mCallback;
    private int strId;
    private TextView tv_time_one, tv_time_day, tv_time_hour, tv_time_minute, tv_time_second;

    public MyCountDownTimer(Context ctx, TextView timeView, int stringId,
                            long millisInFuture, long countDownInterval, MyTimerCallback callback){
        super(millisInFuture, countDownInterval);
        this.mContext = ctx;
        this.mCallback = callback;
        this.strId = stringId;
        this.tv_time_one = timeView;
    }

    public MyCountDownTimer(Context ctx, TextView dayView, TextView hourView, TextView minuteView, TextView secondView,
                            long millisInFuture, long countDownInterval, MyTimerCallback callback){
        super(millisInFuture, countDownInterval);
        this.mContext = ctx;
        this.mCallback = callback;
        this.tv_time_day = dayView;
        this.tv_time_hour = hourView;
        this.tv_time_minute = minuteView;
        this.tv_time_second = secondView;
    }

    @Override
    public void onFinish() {
        if (mCallback != null) {
            mCallback.onFinish();
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long time = millisUntilFinished/1000;
        updateTime(time);
        if (time == 1) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    updateTime(0);
                }
            }, 1000);
        }
    }

    private void updateTime(long time) {
        if (tv_time_one != null) {
            String timeStr = TimeUtil.getTextTime(mContext, time);
            tv_time_one.setText(timeStr);
        }else {
            Integer[] times = TimeUtil.getArrayIntegerTime(mContext, time);
            String day = "00";
            String hour = "00";
            String minute = "00";
            String second = "00";
            if (times != null && times.length > 3) {
                day = changeTime(times[0]);
                hour = changeTime(times[1]);
                minute = changeTime(times[2]);
                second = changeTime(times[3]);
            }
            if (tv_time_day != null) {
                tv_time_day.setText(day);
            }
            if (tv_time_hour != null) {
                tv_time_hour.setText(hour);
            }
            if (tv_time_minute != null) {
                tv_time_minute.setText(minute);
            }
            if (tv_time_second != null) {
                tv_time_second.setText(second);
            }
        }
    }

    private String changeTime(int time) {
        if (time < 10) {
            return "0" + time;
        }else {
            return String.valueOf(time);
        }
    }

    public interface MyTimerCallback {

        public void onFinish();

    }

}
