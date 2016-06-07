package com.spshop.stylistpark.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.spshop.stylistpark.utils.CommonTools;

public class KeyEffectThread extends Thread
{
    private Pair<String, Bitmap> source;
    private int nextStep;
    private Context ctx;
    private Handler mHandler;
    
    public KeyEffectThread(Pair<String, Bitmap> source, int nextStep, Context ctx, Handler mHandler)
    {
        this.source = source;
        this.nextStep = nextStep;
        this.ctx = ctx;
        this.mHandler = mHandler;
    }

    public void run()
    {
        Message msg = new Message();
        Bitmap changedBitmap = CommonTools.keyEffects(ctx, source.second);
        source.second.recycle();
        msg.what = nextStep;
        msg.obj = new Pair<String, Bitmap>(source.first, changedBitmap);
        if (!interrupted())
        {
            mHandler.sendMessage(msg);
        }
    }
}
