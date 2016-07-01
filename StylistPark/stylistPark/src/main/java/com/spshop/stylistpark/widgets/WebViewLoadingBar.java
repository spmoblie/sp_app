package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.utils.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;

public class WebViewLoadingBar extends ImageView{
	
	private Context context;
	private List<Integer> resIdList = new ArrayList<Integer>();
	private int scaleSize = 0;
	private Bitmap previousBitmap;
	private boolean exceptionOccurred = false;
	
	public WebViewLoadingBar(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public WebViewLoadingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public WebViewLoadingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	private void init() {
		setVisibility(View.GONE);
		setScaleType(ScaleType.FIT_XY);
		getImageResId();
	}
	
	private void getImageResId() {
		resIdList.add(R.drawable.icon_locading_horizontal_01);
		resIdList.add(R.drawable.icon_locading_horizontal_02);
		resIdList.add(R.drawable.icon_locading_horizontal_03);
		resIdList.add(R.drawable.icon_locading_horizontal_04);
		resIdList.add(R.drawable.icon_locading_horizontal_05);
		resIdList.add(R.drawable.icon_locading_horizontal_06);
		resIdList.add(R.drawable.icon_locading_horizontal_07);
		resIdList.add(R.drawable.icon_locading_horizontal_08);
		resIdList.add(R.drawable.icon_locading_horizontal_09);
		resIdList.add(R.drawable.icon_locading_horizontal_10);
		
		scaleSize = (int)(100/resIdList.size());
	}
	
	public void setProgress(int progress) {
		if(!exceptionOccurred) {
			if(progress >= 0 && progress != 100) {
				setVisibility(View.VISIBLE);
			} else if(progress == 100) {
				setVisibility(View.GONE);
			}
			
			int pendingIndex = 0;
			
			if(progress > 0) {
				pendingIndex = (int)(progress/scaleSize);
			}
			
			if(pendingIndex < resIdList.size()) {
				try {
					Bitmap loadingBarBitmap = BitmapFactory.decodeResource(getResources(), resIdList.get(pendingIndex));
					
					setImageBitmap(loadingBarBitmap);
					
					recyclePreviousBitmap();
					
					previousBitmap = loadingBarBitmap;
				} catch(Exception e) {
					ExceptionUtil.handle(context, e);
					
					exceptionOccurred = true;
					setVisibility(View.GONE);
					
					setImageBitmap(null);
					recyclePreviousBitmap();
				}
			}
		}
	}
	
	private void recyclePreviousBitmap() {
		if(previousBitmap != null && !previousBitmap.isRecycled()) {
			previousBitmap.recycle();
		}
	}
}
