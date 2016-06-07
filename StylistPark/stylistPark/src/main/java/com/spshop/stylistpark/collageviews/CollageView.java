package com.spshop.stylistpark.collageviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.utils.CommonTools;

public class CollageView extends ImageView {
	
	public static enum status{SCALE, ROTATE, TRANSLATE};

    private static final int PADDING = 1;
    private static final float STROKE_WIDTH = 2f;
    // dp to px
    private int padding;
    private float strokeWidth;

    private Paint mBorderPaint;
    float scale = 1f;
    public Point coordinateTL, coordinateTR, coordinateBL, coordinateBR;
    boolean enableBorder=true;
    
    public CollageView(Context context) {
        this(context, null);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        padding = CommonTools.dip2px(getContext(), PADDING);
        strokeWidth = CommonTools.dip2px(getContext(), STROKE_WIDTH);
        if(enableBorder){
        	setPadding(padding, padding, padding, padding);
        	initBorderPaint();
        }
    }

    private void initBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(getResources().getColor(R.color.ui_bg_color_black));
        mBorderPaint.setStrokeWidth(strokeWidth);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isSelected())
        {
        	if(enableBorder){
	            float strokeWidth4restore = strokeWidth / scale;
	            mBorderPaint.setStrokeWidth(strokeWidth4restore);
	            canvas.drawRect(padding, padding, getWidth() - padding, getHeight() - padding, mBorderPaint);
        	}
        }
    }
    
    public boolean contains(PointF pt){
    	Matrix matrix = new Matrix();
    	matrix.setTranslate(getTranslationX(), getTranslationY());
    	matrix.setScale(getScaleX(), getScaleY());
    	matrix.setRotate(getRotation());
    	
    	int[] l = new int[2];
    	getLocationOnScreen(l);
    	
    	RectF rectF = new RectF(l[0], l[1], l[0] + getWidth(), l[1] + getHeight());


    	matrix.mapRect(rectF);

		return rectF.contains(pt.x, pt.y);
    }
    
    /**
     * get Point in screen
     * */
    
    public void getRawPoint(float x, float y, PointF point){
        final int location[] = { 0, 0 };
        getLocationOnScreen(location);

        double angle=Math.toDegrees(Math.atan2(y, x));
        angle+=getRotation();

        final float length=PointF.length(x,y);

        x=(float)(length*Math.cos(Math.toRadians(angle)))+location[0];
        y=(float)(length*Math.sin(Math.toRadians(angle)))+location[1];

        point.set(x,y);
    }
    
    String url;
    
//    public void setImageURL(String url) {
//
//        if(!TextUtils.isEmpty(this.url) && this.url.equals(url)) return;
//        this.url = url;
//        new LoadImage().execute(url);
//    }
    
    Bitmap bitmap;
    
//    private class LoadImage extends AsyncTask<String, String, Bitmap> {
//        @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//        	}
//           protected Bitmap doInBackground(String... args) {
//
//             try {
//                   bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
//
//            } catch (Exception e) {
//                  e.printStackTrace();
//            }
//
//          return bitmap;
//           }
//           protected void onPostExecute(Bitmap image) {
//             if(image != null){
//
////               setImageBitmap(Tools.getRoundedBitmap(bitmap));
////               setImageBitmap(Tools.getRoundedBitmap(bitmap, Tools.dpToPx(getContext(), 4), Color.WHITE));
//        	   setImageBitmap(bitmap);
//             }else if(listener != null){
//                 listener.onLoadImageError();
//             }
//           }
//       }
    
    /**
     * return tranform: translate, scale, rotate;
     * */
    
    public String getCSS(View v){
    	float tx = (v.getWidth()/2 - getWidth()/2) +getTranslationX();
    	float ty = (v.getHeight()/2 - getHeight()/2) +getTranslationY();
    	return "translate("+tx+"px, "+ty+"px) scale("+getScaleX()+", "+getScaleY()+") rotate("+getRotation()+"deg)";
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }
    
    public void setEnableBorder(boolean enable){
    	enableBorder=enable;
    }
    
}