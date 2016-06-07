package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class InterceptTouchListView extends ListView{
	
	String TAG="InterceptTouchListView";
	
	OnInterceptTouchListener onInterceptTouchListener;
	
	public interface OnInterceptTouchListener{
		
		public void onInterceptTouch(MotionEvent ev);
		
	}
	
	public InterceptTouchListView(final Context context)
    {
        super(context);
    }

    public InterceptTouchListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InterceptTouchListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    public void setOnInterceptTouchListener(OnInterceptTouchListener onInterceptTouchListener){
    	this.onInterceptTouchListener=onInterceptTouchListener;
    }
    
    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev){
    	Log.d(TAG, "InterceptTouchListView event="+ev.getAction());
    	if(onInterceptTouchListener!=null){
    		onInterceptTouchListener.onInterceptTouch(ev);
    	}
    	return super.onInterceptTouchEvent(ev);
    	
    }
}
