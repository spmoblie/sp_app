package com.spshop.stylistpark.activity.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.image.AsyncImageLoader.AsyncImageLoaderCallback;
import com.spshop.stylistpark.image.AsyncImageLoader.ImageLoadTask;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.QRCodeUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.IViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.spshop.stylistpark.AppApplication.screenWidth;

/**
 * 图片轮播页面
 */
public class ScreenImageActivity extends BaseActivity {

	private static final int QR_IMG_WIDTH = screenWidth * 1 / 10;

	public static final String HASHMAP_KEY_IMG = "img";
	public static final String HASHMAP_KEY_BAR = "bar";
	public static final String HASHMAP_KEY_BTM = "btm";
	
	private ArrayList<ProductDetailEntity> enLists = new ArrayList<ProductDetailEntity>();
	private ArrayList<View> viewLists = new ArrayList<View>();
	private ArrayList<ImageView> imagLists = new ArrayList<ImageView>();
	private ArrayMap<String, ImageView> am_img = new ArrayMap<String, ImageView>();
	private ArrayMap<String, ProgressBar> am_bar = new ArrayMap<String, ProgressBar>();
	private ArrayMap<String, Bitmap> am_btm = new ArrayMap<String, Bitmap>();
	private int idsSize, idsPosition, vprPosition;
	private boolean isFirst = true;
	private boolean vprStop = true;
	private FrameLayout frameLayout;
	private ProgressBar progress;
	private IViewPager viewPager;
	private ImageView iv_qr_buy;
	private TextView tv_name, tv_price_sell;
	private Runnable mPagerAction;
	private AsyncImageLoader asyncImageLoader;
	private PowerManager powerManager = null;
	private PowerManager.WakeLock wakeLock = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_image);

		powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
		wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

		long newDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		long oldDay = shared.getLong(AppConfig.KEY_UPDATE_LOOP_DATA_DAY, 0);
		if ((newDay == 1 && oldDay != 1) || newDay - oldDay > 0) {
			AppApplication.videoEn = null;
			AppApplication.imageEn = null;
			shared.edit().putLong(AppConfig.KEY_UPDATE_LOOP_DATA_DAY, newDay).apply();
		}
		idsPosition = shared.getInt(AppConfig.KEY_SCREEN_IMAGE_POSITION, 0);

		findViewById();
		initData();
	}


	private void findViewById() {
		viewPager = (IViewPager) findViewById(R.id.screen_image_viewpager);
		tv_name = (TextView) findViewById(R.id.screen_image_tv_product_name);
		tv_price_sell = (TextView) findViewById(R.id.screen_image_tv_product_price_sell);
		iv_qr_buy = (ImageView) findViewById(R.id.screen_image_qr_buy);

		LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp_1.width = QR_IMG_WIDTH;
		lp_1.height = QR_IMG_WIDTH;
		iv_qr_buy.setLayoutParams(lp_1);
		iv_qr_buy.setScaleType(ImageView.ScaleType.FIT_XY);
	}

	private void initData() {
		if (AppApplication.imageEn != null && AppApplication.imageEn.getPromotionLists() != null) {
			enLists.addAll(AppApplication.imageEn.getPromotionLists());
		} else if (isFirst) {
			startAnimation();
			request(AppConfig.REQUEST_SV_GET_SCREEN_IMAGE_CODE);
			isFirst = false;
			return;
		}
		initViewPager();
	}
	
	private void initViewPager() {
		setHeadVisibility(View.GONE);
		if (enLists == null || enLists.size() == 0) {
			showErrorDialog(null, false, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case DIALOG_CONFIRM_CLICK:
							finish();
							break;
					}
				}
			});
			return;
		}
		idsSize = enLists.size();
		if (idsSize == 2 || idsSize == 3) {
			enLists.addAll(enLists);
		}
		// 创建网络图片加载器
		asyncImageLoader = AsyncImageLoader.getInstance(new AsyncImageLoaderCallback() {

			@Override
			public void imageLoaded(String path, String cachePath, Bitmap bm) {
				ImageView imgView = am_img.get(HASHMAP_KEY_IMG + path);
				if (imgView != null && bm != null) {
					am_btm.put(HASHMAP_KEY_BTM + path, bm); //记录图片
					imgView.setImageBitmap(bm);
				}
				ProgressBar progress = am_bar.get(HASHMAP_KEY_BAR + path);
				if (progress != null) {
					progress.setVisibility(View.GONE);
				}
			}
		});
		// 设置布局参数
		FrameLayout.LayoutParams lp_w = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp_w.gravity = Gravity.CENTER;
		FrameLayout.LayoutParams lp_m = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		lp_m.gravity = Gravity.CENTER;
		// 循环添加View
		for (int i = 0; i < enLists.size(); i++) {
			String imgUrl = enLists.get(i).getImgMaxUrl();
			// 创建父布局
			frameLayout = new FrameLayout(getApplicationContext());
			frameLayout.setLayoutParams(lp_m);
			// 创建子布局-加载动画
			progress = new ProgressBar(getApplicationContext());
			progress.setVisibility(View.GONE);
			progress.setLayoutParams(lp_w);
			// 创建子布局-显示图片
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setLayoutParams(lp_m);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// 加载图片对象
			ImageLoadTask task = asyncImageLoader.loadImage(imgUrl, 0);
			if (task != null && task.getBitmap() != null) {
				imageView.setImageBitmap(task.getBitmap());
				am_btm.put(HASHMAP_KEY_BTM + imgUrl, task.getBitmap()); //记录图片
			}else {
				imageView.setImageResource(R.drawable.bg_img_white);
				progress.setVisibility(View.VISIBLE);
				am_bar.put(HASHMAP_KEY_BAR + imgUrl, progress); //记录加载动画
				am_img.put(HASHMAP_KEY_IMG + imgUrl, imageView); //记录View
			}
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			frameLayout.addView(imageView);
			frameLayout.addView(progress);
			viewLists.add(frameLayout);
			imagLists.add(imageView);
		}
		final boolean loop = viewLists.size() > 3 ? true:false;
		viewPager.setAdapter(new PagerAdapter()
        {
            // 创建
            @Override
            public Object instantiateItem(View container, int position)
            {
				View layout;
				if (loop) {
					layout = viewLists.get(position % viewLists.size());
				}else {
					layout = viewLists.get(position);
				}
				if (layout != null) {
					viewPager.addView(layout);
				}
				return layout;
            }
            
            // 销毁
            @Override
            public void destroyItem(View container, int position, Object object)
            {
				View layout = null;
				if (loop) {
					layout = viewLists.get(position % viewLists.size());
				}else {
					layout = viewLists.get(position);
				}
				if (layout != null) {
					viewPager.removeView(layout);
				}
            }
            
            @Override
            public boolean isViewFromObject(View arg0, Object arg1)
            {
                return arg0 == arg1;
                
            }
            
            @Override
            public int getCount()
            {
				if (loop) {
					return Integer.MAX_VALUE;
				}else {
					return viewLists.size();
				}
            }
            
        });
		viewPager.setOnPageChangeListener(new OnPageChangeListener(){
            
            @Override
            public void onPageSelected(final int arg0){
				if (loop) {
					vprPosition = arg0;
					idsPosition = arg0 % viewLists.size();
					if (idsPosition == viewLists.size()) {
						idsPosition = 0;
						viewPager.setCurrentItem(0);
					}
				}else {
					idsPosition = arg0;
				}
				// 变更指示器
				if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
					idsPosition = idsPosition - idsSize;
				}
				setPageNum();
            }
            
            @Override
            public void onPageScrolled(int arg0, float positionOffset, int positionOffsetPixels){
            	
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0){
				if (arg0 == 1) {
					vprStop = true;
				}
            }
        });
		if (loop) {
			viewPager.setCurrentItem(viewLists.size() * 10 + idsPosition);
			mPagerAction = new Runnable(){

				@Override
				public void run(){
					if (!vprStop) {
						vprPosition++;
						if (viewPager != null) {
							viewPager.setCurrentItem(vprPosition);
						}
					}
					vprStop = false;
					if (viewPager != null) {
						viewPager.postDelayed(mPagerAction, 5000);
					}
				}
			};
			if (viewPager != null) {
				viewPager.postDelayed(mPagerAction, 5000);
			}
		}
	}

	private void setPageNum() {
		//tv_page.setText(getString(R.string.viewpager_indicator, idsPosition + 1, idsSize));
		editor.putInt(AppConfig.KEY_SCREEN_IMAGE_POSITION, idsPosition).apply();
		if (idsPosition < 0 || idsPosition >= enLists.size()) return;
		ProductDetailEntity mainEn = enLists.get(idsPosition);

		if (mainEn != null && !StringUtil.isNull(mainEn.getPromotionName())) {
			Bitmap bm = QRCodeUtil.createQRImage(mainEn.getPromotionName(), QR_IMG_WIDTH * 2, QR_IMG_WIDTH * 2, 1);
			if (bm != null) {
				iv_qr_buy.setImageBitmap(bm);
			}
		}
		tv_name.setText(mainEn.getName());
		tv_price_sell.setText(mainEn.getSellPrice());
	}

	@Override
	protected void onResume() {
		super.onResume();
		stopLoop();
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		if (wakeLock != null) {
			wakeLock.acquire();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		// 销毁对象
        if (asyncImageLoader != null) {
        	asyncImageLoader.clearInstance();
		}
		if (wakeLock != null) {
			wakeLock.release();
		}
	}

	@Override
	protected void onDestroy() {
		am_img.clear();
		am_bar.clear();
		am_btm.clear();
		super.onDestroy();
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SCREEN_IMAGE_CODE:
				params.add(new MyNameValuePair("app", "best"));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_SCREEN_IMAGE_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SCREEN_IMAGE_CODE:
				if (result != null) {
					AppApplication.imageEn = (ProductDetailEntity) result;
				}
				initData();
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

}
