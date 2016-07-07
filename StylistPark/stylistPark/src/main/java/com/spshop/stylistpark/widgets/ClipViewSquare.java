package com.spshop.stylistpark.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.activity.common.ClipImageSquareActivity;

/**
 * 裁剪方形边框
 */
public class ClipViewSquare extends View {

	/**
	 * 屏幕宽
	 */
	public static final int WIDTH = AppApplication.screenWidth;
	/**
	 * 屏幕高
	 */
	public static final int HEIGHT = AppApplication.screenHeight;
	/**
	 * 方形宽高
	 */
	public static final int WIDTH_HEIGHT = WIDTH * 2 / 3;

	private Paint mPaint;

	public ClipViewSquare(Context context) {
		this(context, null);
	}

	public ClipViewSquare(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipViewSquare(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = this.getHeight();
		// 获取屏幕状态栏和导航栏高度
		int otherTopHeight = 150;
		if (ClipImageSquareActivity.instance != null) {
			otherTopHeight = ClipImageSquareActivity.instance.getStatusBarHeight();
		}
		int left = (WIDTH - WIDTH_HEIGHT) / 2; //左
		int top = (HEIGHT - otherTopHeight - WIDTH_HEIGHT) / 2; //上
		int right = left + WIDTH_HEIGHT; //右
		int bottom = top + WIDTH_HEIGHT; //下
		
		// 绘制边框以外的半透明效果
		mPaint.setColor(0xaa000000);
		mPaint.setStrokeWidth(height);
		mPaint.setStyle(Paint.Style.FILL); //实心矩形
		canvas.drawRect(new RectF(0, 0, left, HEIGHT), mPaint); //绘制边框左方
		canvas.drawRect(new RectF(left, 0, right, top), mPaint); //绘制边框上方
		canvas.drawRect(new RectF(right, 0, WIDTH, HEIGHT), mPaint); //绘制边框右方
		canvas.drawRect(new RectF(left, bottom, right, HEIGHT), mPaint); //绘制边框下方
		
		// 绘制白色边框线
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(2);
		mPaint.setStyle(Paint.Style.STROKE); //空心矩形
		canvas.drawRect(new RectF(left, top, right, bottom), mPaint);
	}

}
