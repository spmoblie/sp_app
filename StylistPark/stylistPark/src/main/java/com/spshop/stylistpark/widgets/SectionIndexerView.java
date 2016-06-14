package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.SectionIndexer;

public class SectionIndexerView extends View implements OnTouchListener {

    /**     */
    private SectionIndexer mSectionIndex;

    /**
     * 背景画笔
     */
    private Paint mBackgroundPaint;

    /**
     * 字母画笔
     */
    private Paint mAlphaPaint;

    /**
     * section
     */
    private Object[] mSections;

    /**
     * 字母上下填充
     */
    private int mAlphaPadding;

    /**
     * 当前选择的
     */
    private int mCurrentSection;

    /**
     * 单个字母显示高度, 包含mAlphaadding
     */
    private int mHeight;

    /**
     * 单个字母显示宽度, 包含mAlphaadding
     */
    private int mWidth;

    /**
     * 字母显示间隔
     */
    private int mAlphaInterval;


    private boolean mBackground = true;

    private SectionIndexerListener mListener;


    public SectionIndexerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SectionIndexerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SectionIndexerView(Context context) {
        super(context);
        init();
    }


    public void setSectionIndexer(SectionIndexer indexer) {
        this.mSectionIndex = indexer;
        mSections = indexer.getSections();
    }

    public void setSectionListener(SectionIndexerListener list) {
        mListener = list;
    }


    private void init() {

        /**
         * 初始化背景画笔.
         */
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.BLACK);
        mBackgroundPaint.setAlpha(50);

        /**
         *
         */
        mAlphaPaint = new Paint();
        mAlphaPaint.setAntiAlias(true);
        mAlphaPaint.setColor(Color.WHITE);
        mAlphaPaint.setTextSize(25);

        /**
         * 计算section的高度.
         */

        setOnTouchListener(this);
    }

    /**
     * 计算显示宽度
     *
     * @return
     */
    private int sectionWidth() {
        mWidth = (int) (mAlphaPaint.measureText("A"));
        return mWidth;
    }


    private int sectionHeight() {
        mHeight = (int) ((mAlphaPaint.descent() - mAlphaPaint.ascent()) + (mAlphaPadding * 2));
        return mHeight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (mSectionIndex == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (mSections == null)
            mSections = mSectionIndex.getSections();

        int measureHeight;
        int measureWidth;
        int height = (int) (sectionHeight() * mSections.length) + (getPaddingTop() + getPaddingBottom());
        int width = sectionWidth() + getPaddingLeft() + getPaddingRight();

        /**
         * 根据布局参数来设置View的宽高.
         * 如果布局参数的高或宽为LayoutParams.WRAP_CONTENT
         * 则View的宽高分别为 width , height
         * 否则直接根据布局参数的数值来设置
         */
        LayoutParams lp = getLayoutParams();
        if (lp.height != LayoutParams.WRAP_CONTENT)
            height = lp.height;

        if (lp.width != LayoutParams.WRAP_CONTENT)
            width = lp.width;

        /**     */
        measureHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, height);
        measureWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, width);

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mSectionIndex == null)
            return;

        int height = getHeight();
        int widht = getWidth();
        //画背景
        if (mBackground) {
            RectF round = new RectF(0, 0, widht, height);
            canvas.drawRect(round, mBackgroundPaint);
        }

        //画字母
        float textheight = mAlphaPaint.descent() - mAlphaPaint.ascent();
        float y = textheight / 1.5f + getPaddingTop();            //第一个字母偏移 .
        float x = getPaddingLeft();
        for (int i = 0; i < mSections.length; i++) {

            if (mCurrentSection == i)
                mAlphaPaint.setColor(Color.BLACK);
            else
                mAlphaPaint.setColor(Color.WHITE);

            y += mAlphaPadding + mAlphaInterval;
            canvas.drawText(mSections[i].toString(), x, y, mAlphaPaint);
            y += mAlphaPadding + textheight;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        int index = (int) event.getY() / (mHeight + mAlphaInterval);
        int status = 0;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                status = SectionIndexerListener.STATE_DONW;

                break;
            case MotionEvent.ACTION_MOVE:
                status = SectionIndexerListener.STATE_MOVE;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:

                status = SectionIndexerListener.STATE_UP;
                if (mListener != null)
                    mListener.onSectionChange(status, mSectionIndex.getPositionForSection(mCurrentSection), mSections[mCurrentSection]);

                return false;
        }

        if (index < mSections.length && index != mCurrentSection) {
            mCurrentSection = index;

            if (mListener != null) {
                mListener.onSectionChange(status, mSectionIndex.getPositionForSection(mCurrentSection), mSections[mCurrentSection]);
            }
            invalidate();
        }

        return true;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        /**
         * 	view 的高度大于列表显示的高度, 在每一个字母之间加入一些间隔,
         * 		使每一个字母对齐,并填满整个view.
         */
        int viewHeight = getHeight() - (getPaddingTop() + getPaddingBottom());
        int originalHeight = mHeight * mSections.length;

        int overHeight = viewHeight - originalHeight;

        if (overHeight <= 0) return;

        mAlphaInterval = overHeight / (mSections.length);
    }

    /**
     * 设置section字体大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        mAlphaPaint.setTextSize(size);
    }

    public void setSectionPadding(int padd) {
        mAlphaPadding = padd;
    }

    /**
     * 这个接口提供一些回调方法, 当选择变更时通知更改.
     *
     * @author juice
     */
    public interface SectionIndexerListener {

        int STATE_DONW = 0;


        int STATE_MOVE = 1;


        int STATE_UP = 2;

        /**
         * 当选择改变时,回调此方法.
         * 你可以在这个方法回调时,更新显示section.
         *
         * @param position   显示position
         * @param newSection 新的section
         */
        public void onSectionChange(int status, int position, Object newSection);

    }
}
