package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class InterceptRelativeLayout extends RelativeLayout{
String TAG=InterceptRelativeLayout.class.getSimpleName();
	
	OnDispatchTouchEventListener onDispatchTouchEventListener;
	
	public interface OnDispatchTouchEventListener{
		
		public void onDispatchTouchEvent(InterceptRelativeLayout view, MotionEvent ev);
		
	}
	
	public InterceptRelativeLayout(final Context context)
    {
        super(context);
    }

    public InterceptRelativeLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InterceptRelativeLayout(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    public void setOnDispatchTouchEventListener(OnDispatchTouchEventListener onDispatchTouchEventListener){
    	this.onDispatchTouchEventListener=onDispatchTouchEventListener;
    }
    
    @Override
    public boolean dispatchTouchEvent (MotionEvent ev){
    	
    	if(onDispatchTouchEventListener!=null){
    		onDispatchTouchEventListener.onDispatchTouchEvent(this,ev);
    		return true;
    	}
    	
    	return super.dispatchTouchEvent(ev);
    }
    
    public void callSuperDispatchTouchEvent(MotionEvent ev){
    	 super.dispatchTouchEvent(ev);
    }

}
