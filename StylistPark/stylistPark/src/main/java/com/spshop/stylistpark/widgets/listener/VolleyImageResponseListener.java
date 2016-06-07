package com.spshop.stylistpark.widgets.listener;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.android.volley.Response.Listener;
import com.spshop.stylistpark.utils.BitmapUtil;

public class VolleyImageResponseListener implements Listener<Bitmap> {

    String url;
    int nextStep;
    Handler handler;

    public VolleyImageResponseListener(String url, int nextStep, Handler handler) {
        this.url = url;
        this.nextStep = nextStep;
        this.handler = handler;
    }

    @Override
    public void onResponse(Bitmap bitmap) {
		Bitmap newBm = BitmapUtil.sacleDownBitmap(bitmap,0.5f);
        Message msg = new Message();
        msg.what = nextStep;
        msg.obj = new Pair<String, Bitmap>(url, newBm);
        bitmap.recycle();
        handler.sendMessage(msg);
    }
    
}
