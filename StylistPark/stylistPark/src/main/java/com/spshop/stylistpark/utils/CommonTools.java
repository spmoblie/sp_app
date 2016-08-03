package com.spshop.stylistpark.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.RowObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统通用工具类
 */
public class CommonTools {

	private static Toast toast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
        	toast.cancel();
        	toast = null; //toast隐藏后，将其置为null
        }
    };

    /**
     * 显示Toast消息
     *
     * @param message 消息文本
     * @param time 消息显示的时长
     */
    public static void showToast(String message, long time) {
		Context ctx = AppApplication.getInstance().getApplicationContext();
    	LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view = inflater.inflate(R.layout.layout_toast, null);
    	TextView text = (TextView) view.findViewById(R.id.toast_message);
    	text.setText(message);

    	mHandler.removeCallbacks(r);
    	if (toast == null){ //只有mToast==null时才重新创建，否则只需更改提示文字
    		toast = new Toast(ctx);
    		toast.setDuration(Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.BOTTOM, 0, AppApplication.screenHeight / 6);
    		toast.setView(view);
    	}
    	mHandler.postDelayed(r, time); //延迟隐藏toast
    	toast.show();
    }

    /**
     * 显示翻页数量
     *
     * @param message 页数
     * @param time 显示的时长
     */
    public static void showPageNum(String message, long time) {
		Context ctx = AppApplication.getInstance().getApplicationContext();
    	LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view = inflater.inflate(R.layout.layout_toast_page_num, null);
    	TextView text = (TextView) view.findViewById(R.id.toast_message);
    	text.setText(message);

    	mHandler.removeCallbacks(r);
    	if (toast == null){ //只有mToast==null时才重新创建，否则只需更改提示文字
    		toast = new Toast(ctx);
    		toast.setDuration(Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.BOTTOM, 0, AppApplication.screenHeight / 12);
    		toast.setView(view);
    	}
    	mHandler.postDelayed(r, time); //延迟隐藏toast
    	toast.show();
    }

	/**
	 * 根据手机分辨率从dp转成px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static float dpToPx(Context context, float dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    float px = dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);       
	    return px;
	}
	
	public static int dpToPx(Context context, int dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}

	public static float convertDpToPixel(Context context, float dp) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return px;
	}
	
	public static double getHypotenuseByPyth(PointF pt1, PointF pt2) {
        int w = (int) (Math.max(pt1.x, pt2.x) - Math.min(pt1.x, pt2.x));
        int h = (int) (Math.max(pt1.y, pt2.y) - Math.min(pt1.y, pt2.y));
        return Math.sqrt(w * w + h * h);
    }
	
	@SuppressWarnings("rawtypes")
	public static List<RowObject> convertToRowObject(List dataList, int numOfCol) {
		List<RowObject> result = new ArrayList<RowObject>();
		for (int i = 0; i < dataList.size();) {
			RowObject tmp = new RowObject(numOfCol);
			for (int j = 0; j < tmp.objectArr.length; j++) {
				if (i < dataList.size() && dataList.get(i) != null) {
					tmp.objectArr[j] = dataList.get(i);
					i += 1;
				}
			}
			result.add(tmp);
		}
		return result;
	}
	
	public static double getAngle(float x, float y, float x2, float y2) {
        float dx = x2 - x;
        float dy = y2 - y;
        double DRoation = Math.atan2(dy, dx);
        return DRoation / Math.PI * 180;
    }
	
	public static float getScaleFactor(double oldDist, double newDist) {
        return (float) (newDist / oldDist);
    }
	
	public static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }
        return degrees;
    }
	
	public static void deleteFileInCache(Context context, String fileName) {
	    File file = new File(context.getExternalCacheDir(), fileName);
	    file.delete();
	}
	
	public static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = { deltaX, deltaY };
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }
	
	public static  void enableViews(View view, boolean enabled) {
		
	    view.setEnabled(enabled);

	    if ( view instanceof ViewGroup ) {
	        ViewGroup group = (ViewGroup)view;

	        for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
	        	enableViews(group.getChildAt(idx), enabled);
	        }
	    }
	}

	/**
	 * 获取存储屏幕信息的Point
	 */
	public static Point getScreeanSize(Activity activity){
	    Display display = activity.getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    return size;
	}

	/**
	 * 正则匹配手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileOR(String mobiles){
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Matcher matcher = pattern.matcher(mobiles);  
		return matcher.matches();
	}
	
	/**
	 * 动态设置布局的宽高
	 */
	public static void setLayoutParams(View view, int width, int height){
		// 获取布局参数
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) view.getLayoutParams();  
		linearParams.width = width;   
		linearParams.height = height;
		// 应用布局参数
		view.setLayoutParams(linearParams);
	}

}
