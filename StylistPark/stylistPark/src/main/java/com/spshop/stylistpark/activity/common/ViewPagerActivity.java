package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.image.AsyncImageLoader.AsyncImageLoaderCallback;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.widgets.DragImageView;
import com.spshop.stylistpark.widgets.DragImageView.ImgOnClickListener;
import com.spshop.stylistpark.widgets.DragImageView.ImgOnLongClickListener;
import com.tencent.stat.StatService;

import java.io.File;
import java.util.ArrayList;

/**
 * 相片查看器
 */
public class ViewPagerActivity extends BaseActivity {

	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String HASHMAP_KEY_IMG = "img";
	public static final String HASHMAP_KEY_BAR = "bar";
	public static final String HASHMAP_KEY_BTM = "btm";
	
	private ArrayList<String> urlLists;
	private ArrayList<View> viewLists = new ArrayList<View>();
	private ArrayList<DragImageView> imagLists = new ArrayList<DragImageView>();
	private int mCurrentItem;
	private TextView tv_save, tv_page;
	private Bitmap defaultImg, showBitmap;
	private FrameLayout frameLayout;
	private ProgressBar progress;
	private DragImageView imageView, showView;
	private ViewPager viewPager;
	private AsyncImageLoader asyncImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);

		mCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urlLists = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
		
		findViewById();
		initViewPager();
	}


	private void findViewById() {
		viewPager = (ViewPager) findViewById(R.id.my_viewpager);
		tv_save = (TextView) findViewById(R.id.my_image_save);
		tv_page = (TextView) findViewById(R.id.my_viewpager_tv_page);
	}
	
	@SuppressLint("HandlerLeak")
	private void initViewPager() {
		setHeadVisibility(View.GONE);
		if (urlLists == null) {
			Handler mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case DIALOG_CONFIRM_CLICK:
						finish();
						break;
					}
				}
			};
			showErrorDialog(null, false, mHandler);
			return;
		}
		setPageNum(urlLists.size());
		// 获取默认显示图片
		defaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.bg_img_white);
		defaultImg = BitmapUtil.resizeImageByWidth(defaultImg, width);
		// 创建网络图片加载器
		AsyncImageLoaderCallback callback = new AsyncImageLoaderCallback() {

			@Override
			public void imageLoaded(String path, File saveFile, Bitmap bm) {
				DragImageView imgView = AppApplication.imgHashMap.get(HASHMAP_KEY_IMG + path);
				if (imgView != null && bm != null) {
					showBitmap = bm;
					AppApplication.btmHashMap.put(HASHMAP_KEY_BTM + path, bm); //记录图片
					imgView.setImageBitmap(bm);
				}
				ProgressBar progress = AppApplication.barHashMap.get(HASHMAP_KEY_BAR + path);
				if (progress != null) {
					progress.setVisibility(View.GONE);
				}
			}
		};
		asyncImageLoader = AsyncImageLoader.getInstance(this, callback);
		// 设置布局参数
		FrameLayout.LayoutParams lp_w = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp_w.gravity = Gravity.CENTER;
		FrameLayout.LayoutParams lp_m = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		lp_m.gravity = Gravity.CENTER;
		// 循环添加View
		for (int i = 0; i < urlLists.size(); i++) {
			String imgUrl = urlLists.get(i);
			// 创建父布局
			frameLayout = new FrameLayout(this);
			frameLayout.setLayoutParams(lp_m);
			// 创建子布局-加载动画
			progress = new ProgressBar(this);
			progress.setVisibility(View.GONE);
			progress.setLayoutParams(lp_w);
			// 创建子布局-显示图片
			imageView = new DragImageView(this);
			imageView.setLayoutParams(lp_m);
			imageView.setmActivity(this);
			imageView.setScreen_H(height - statusHeight);
			imageView.setScreen_W(width);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// 加载图片对象
			Bitmap bm = asyncImageLoader.loadImage(true, imgUrl, 0);
			if (bm != null) {
				showBitmap = bm;
				AppApplication.btmHashMap.put(HASHMAP_KEY_BTM + imgUrl, bm); //记录图片
			}else {
				showBitmap = defaultImg;
				progress.setVisibility(View.VISIBLE);
				AppApplication.barHashMap.put(HASHMAP_KEY_BAR + imgUrl, progress); //记录加载动画
				AppApplication.imgHashMap.put(HASHMAP_KEY_IMG + imgUrl, imageView); //记录View
			}
			imageView.setImageBitmap(showBitmap);
			imageView.setImgOnLongClickListener(new ImgOnLongClickListener() {
				
				@Override
				public void onLongClick() {
					if (tv_save.getVisibility() == View.GONE) {
						tv_save.setVisibility(View.VISIBLE);
					}
				}
			});
			
			imageView.setImgOnClickListener(new ImgOnClickListener() {
				
				@Override
				public void onClick() {
					if (tv_save.getVisibility() == View.VISIBLE) {
						tv_save.setVisibility(View.GONE);
					}else {
						finish();
					}
				}
			});
			frameLayout.addView(imageView);
			frameLayout.addView(progress);
			viewLists.add(frameLayout);
			imagLists.add(imageView);
		}
		viewPager.setAdapter(new PagerAdapter()
        {
            // 创建
            @Override
            public Object instantiateItem(View container, int position)
            {
                View layout = viewLists.get(position % viewLists.size());
                viewPager.addView(layout);
				return layout;
            }
            
            // 销毁
            @Override
            public void destroyItem(View container, int position, Object object)
            {
                View layout = viewLists.get(position % viewLists.size());
                viewPager.removeView(layout);
            }
            
            @Override
            public boolean isViewFromObject(View arg0, Object arg1)
            {
                return arg0 == arg1;
                
            }
            
            @Override
            public int getCount()
            {
                return viewLists.size();
            }
            
        });
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){
            
            @Override
            public void onPageSelected(final int position){
            	tv_save.setVisibility(View.GONE);
            	mCurrentItem = position % viewLists.size();
				if (mCurrentItem >= 0 && mCurrentItem < imagLists.size()) {
					if (showView != null) {
						showView.setReset();
					}
					showView = imagLists.get(mCurrentItem);
				}
				setPageNum(viewLists.size());
            }
            
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
            	
            }
            
            @Override
            public void onPageScrollStateChanged(int position){
            	
            }
        });
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (showView != null && !showView.isChange()) {
					showView.onTouchEvent(event);
					return true; //不切换图片则拦截事件
				}
				return false;
			}
		});
		viewPager.setCurrentItem(mCurrentItem);
		
		tv_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bm = AppApplication.btmHashMap.get(HASHMAP_KEY_BTM + urlLists.get(mCurrentItem));
				File file = BitmapUtil.createPath(BitmapUtil.filterPath(urlLists.get(mCurrentItem)), true);
				if (file == null) {
	            	showErrorDialog(R.string.photo_show_save_fail);
	    			return;
				}
				AppApplication.saveBitmapFile(bm, file, 100);
				CommonTools.showToast(ViewPagerActivity.this, getString(R.string.photo_show_save_ok), 1000);
				tv_save.setVisibility(View.GONE);
			}
		});
	}


	private void setPageNum(int totalNum) {
		tv_page.setText(getString(R.string.viewpager_indicator, mCurrentItem + 1, totalNum));
	}
	
	@Override
	public void finish() {
		super.finish();
		AppApplication.imgHashMap.clear();
		AppApplication.barHashMap.clear();
		AppApplication.btmHashMap.clear();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 页面开始
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 页面结束
		StatService.onPause(this);
		// 销毁对象
        if (asyncImageLoader != null) {
        	asyncImageLoader.clearInstance();
		}
	}

}
