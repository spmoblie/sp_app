package com.spshop.stylistpark.utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.RowObject;

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
     * @param context 上下文对象
     * @param message 消息文本
     * @param time 消息显示的时长
     */
    public static void showToast(Context context, String message, long time) {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view = inflater.inflate(R.layout.layout_toast, null);
    	TextView text = (TextView) view.findViewById(R.id.toast_message);
    	text.setText(message);
    	
    	mHandler.removeCallbacks(r);
    	if (toast == null){ //只有mToast==null时才重新创建，否则只需更改提示文字
    		toast = new Toast(context);
    		toast.setDuration(Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.BOTTOM, 0, AppApplication.screenHeight / 6);
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
	
	public static int getTotalHeightofListView(ListView listView) {

	    ListAdapter mAdapter = listView.getAdapter();

	    int totalHeight = 0;

	    for (int i = 0; i < mAdapter.getCount(); i++) {
	        View mView = mAdapter.getView(i, null, listView);

	        mView.measure(
	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

	        totalHeight += mView.getMeasuredHeight();
	        Log.w("HEIGHT" + i, String.valueOf(totalHeight));

	    }
	    return totalHeight;
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
	
	public static URI captureView(Context context, View view, String filename,int scaledWidth){
        view.setDrawingCacheEnabled(true);
        Bitmap b = view.getDrawingCache();
        File file = new File(context.getExternalCacheDir(), filename);
        b = BitmapUtil.getBitmap(b, scaledWidth, scaledWidth);
        AppApplication.saveBitmapFile(b, file, 70);
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return file.toURI();
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
	
	public static Bitmap keyEffects(Context ctx, Bitmap bitmap){
	    try{
        	Bitmap bm = Bitmap.createBitmap(bitmap);
        	GPUImage gpuImage = new GPUImage(ctx);
        	gpuImage.setImage(bm);
        	GPUImageGrayscaleFilter grayF = new GPUImageGrayscaleFilter();
        	gpuImage.setFilter(grayF);
        	bm = gpuImage.getBitmapWithFilterApplied();
        	fixGPUImage(bm);
        	gpuImage.setImage(bm);
        	GPUImageSobelEdgeDetection edgeF = new GPUImageSobelEdgeDetection();
        	edgeF.setLineSize(0.5f);
        	gpuImage.setFilter(edgeF);
        	bm = gpuImage.getBitmapWithFilterApplied();
        	fixGPUImage(bm);
        	
        	FloodFill ff = new FloodFill();
        	ff.mBitmap = bm;
        	ff.floodFillScanlineStack(0, 0, Color.MAGENTA, Color.BLACK);
        	int w = bm.getWidth();
        	int h = bm.getHeight();
        	for (int i = 0; i < h; i++) {  
                for (int j = 0; j < w; j++) {  
                    int color = bm.getPixel(j, i);
                    if(color == Color.MAGENTA){
                    	bm.setPixel(j, i, Color.TRANSPARENT);  
                    }else{
                    	bm.setPixel(j, i, Color.WHITE);  
                    }
                }  
            }
        	gpuImage.setImage(bm);
        	
        	GPUImageGaussianBlurFilter gBlurF = new GPUImageGaussianBlurFilter();
        	gpuImage.setFilter(gBlurF);
        	bm = gpuImage.getBitmapWithFilterApplied();
        	fixGPUImage(bm);
        	
        	Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        	Canvas mCanvas = new Canvas(result);
        	
        	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        	mCanvas.drawBitmap(bitmap, 0, 0, null);
        	mCanvas.drawBitmap(bm, 0, 0, paint);
        	paint.setXfermode(null);
        	return result;
	    }catch (OutOfMemoryError error) {
	        int newW = (int) (bitmap.getWidth() * 0.75);
	        int newH = (int) (bitmap.getHeight() * 0.75);
	        Bitmap bm = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
	        return keyEffects(ctx, bm);
	    }
	}
	
	private static void fixGPUImage(Bitmap bitmap) {
	    int wrongLineCount = getWrongLineCount(bitmap);
        if(wrongLineCount != 0) {
            int pixel = bitmap.getPixel((bitmap.getWidth() - 1) - wrongLineCount, 0);
            for (int i = bitmap.getWidth() - 1; i > (bitmap.getWidth() - 1) - wrongLineCount; i--) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    bitmap.setPixel(i, j, pixel);
                }
            }
        }
	}
	
	private static int getWrongLineCount(Bitmap bitmap) {
        int count = 0;
        int pixel = bitmap.getPixel(bitmap.getWidth() - 1, 0);
        for(int i = bitmap.getWidth() - 1;i >= 0;i--) {
            boolean isWrongLine = true;
            for(int j = 0;j < bitmap.getHeight();j++) {
                if(pixel != bitmap.getPixel(i, j)) {
                    isWrongLine = false;
                    return count;
                }
            }
            if(isWrongLine) {
                count++;
            }
        }
        return 0;
    }
	
}
