package com.spshop.stylistpark.collageviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.collageviews.CollageView.status;

public class CollageViewWithBorder extends RelativeLayout {

    public CollageView cv;
    View scaleLT, scaleRB, rotate;
    View scaleLB;
    private boolean isProduct;
    
    float mScale = 1;
    int originWidth, originHeight, mWidth, mHeight;
    public float minimumScale = 0.4f;
    public float maximumScale = 10.0f;
    int originMargin;

    public CollageViewWithBorder(Context context) {
        super(context);
        init();
    }

    public CollageViewWithBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollageViewWithBorder(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        originMargin = getResources().getDimensionPixelSize(
                R.dimen.generator_collage_image_margin);
        inflate(getContext(), R.layout.layout_collage_free_style, this);
        cv = (CollageView) findViewById(R.id.collageView);
        scaleLT = findViewById(R.id.viewScaleLT);
        scaleLB = findViewById(R.id.viewScaleLB);
        scaleRB = findViewById(R.id.viewScaleRB);
        rotate = findViewById(R.id.viewRotate);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        cv.setSelected(selected);
        if (selected) {
            scaleLT.setVisibility(View.VISIBLE);
            scaleLB.setVisibility(View.VISIBLE);
            scaleRB.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
        } else {
            // android 4.1.2, offset and scale if set gone
            scaleLT.setVisibility(View.INVISIBLE);
            scaleLB.setVisibility(View.INVISIBLE);
            scaleRB.setVisibility(View.INVISIBLE);
            rotate.setVisibility(View.INVISIBLE);
        }
    }

    public void setScale(float scale) {
        if (scale > maximumScale)
            scale = maximumScale;
        if (scale < minimumScale)
            scale = minimumScale;
        float scale4restore = 1 / scale;
        setScale(scaleLT, scale4restore);
        setScale(scaleLB, scale4restore);
        setScale(scaleRB, scale4restore);
        setScale(rotate, scale4restore);
        setScaleX(scale);
        setScaleY(scale);
        cv.setScale(scale);
        cv.invalidate();
    }

    public void setScale(View v, float scale) {
        v.setScaleX(scale);
        v.setScaleY(scale);
    }

//    public void setImageURL(String url) {
//        cv.setImageURL(url);
//    }
    
    public void setImageBitmap(Bitmap bm) {
        cv.setImageBitmap(bm);
    }

    /**
     * return tranform: translate, scale, rotate;
     * */

    public String getCSS(View v) {
        //
        float tx = (v.getWidth() / 2 - getWidth() / 2) + getTranslationX();
        float ty = (v.getHeight() / 2 - getHeight() / 2) + getTranslationY();
        return "translate(" + tx + "px, " + ty + "px) scale(" + getScaleX()
                + ", " + getScaleY() + ") rotate(" + getRotation() + "deg)";
    }
    
    public String getCSS(View v, float scale) {
        float tx = (v.getWidth() / 2 - getWidth() / 2) + getTranslationX();
        float ty = (v.getHeight() / 2 - getHeight() / 2) + getTranslationY();
        tx = tx*scale;
        ty = ty*scale;
        return "translate(" + tx + "px, " + ty + "px) scale(" + getScaleX()
                + ", " + getScaleY() + ") rotate(" + getRotation() + "deg)";
    }
    
    public float getTranslationX(View v, float scale){
        float tx = (v.getWidth() / 2 - getWidth() / 2) + getTranslationX();
        tx = tx*scale;
        return tx;
    }
    
    public float getTranslationY(View v, float scale){
        float ty = (v.getHeight() / 2 - getHeight() / 2) + getTranslationY();
        ty = ty*scale;
        return ty;
    }

    /**
     * This method will save some value before transform, Not only check if
     * point at corner rectangle
     * 
     * @param x
     * @param y
     * @return status( scale, rotate, none )
     */
    public status checkCorner(float x, float y) {
        RectF cornerLT, cornerRT, cornerRB, cornerLB;
        cornerLT = getRectF(scaleLT);
        cornerLB = getRectF(scaleLB);
        cornerRT = getRectF(rotate);
        cornerRB = getRectF(scaleRB);
        if (cornerRT.contains(x, y))
            return status.ROTATE;
        if ( cornerLT.contains(x, y) || cornerRB.contains(x, y) || 
                cornerLB.contains(x, y) ) {
            return status.SCALE;
        }
        return status.TRANSLATE;
    }

    public RectF getRectF(View v) {
        return new RectF(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
    }

    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    public boolean isProduct()
    {
        return isProduct;
    }

    public void setProduct(boolean isProduct)
    {
        this.isProduct = isProduct;
    }

}
