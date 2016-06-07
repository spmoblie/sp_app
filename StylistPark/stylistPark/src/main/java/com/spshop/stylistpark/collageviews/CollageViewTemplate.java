package com.spshop.stylistpark.collageviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.spshop.stylistpark.R;

public class CollageViewTemplate extends CollageView
{
    private static final String TAG = "CollageViewTemplate";
    
    Paint wordPaint;
    String word;
    boolean showBackground = true;
    
    public CollageViewTemplate(Context context)
    {
        super(context);
        init();
    }
    public CollageViewTemplate(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CollageViewTemplate(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw" + (getDrawable() == null) );
        if(showBackground)
        {
            setBackgroundResource(R.color.collage_template_bg);
        }else
        {
            setBackground(null);
        }
        super.onDraw(canvas);
        
        if(showBackground)
        {
            if (wordPaint == null)
            {
                wordPaint = new Paint();
                wordPaint.setColor(Color.BLACK);
                wordPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_16));
            }
            Rect bounds = new Rect();
            wordPaint.getTextBounds(word, 0, word.length(), bounds);
            int x = (canvas.getWidth() - bounds.width())/2;
            int y = (canvas.getHeight() + bounds.height())/2;
           
            canvas.drawText(word, x, y, wordPaint);
        }
    }

    public String getWord()
    {
        return word;
    }

    private void init()
    {
        setScaleType(ScaleType.FIT_CENTER);
    }

    public void setWord(String word)
    {
        this.word = word;
    }
    public boolean isShowBackground()
    {
        return showBackground;
    }
    public void setShowBackground(boolean showBackground)
    {
        this.showBackground = showBackground;
    }

}
