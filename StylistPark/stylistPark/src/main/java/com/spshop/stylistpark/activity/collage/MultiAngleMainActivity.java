package com.spshop.stylistpark.activity.collage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.collage.MultiAngleProductPhotoActivity.FinishAnim;
import com.spshop.stylistpark.collageviews.CollageView;
import com.spshop.stylistpark.collageviews.MultiTouchListener;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserTracker;
import com.spshop.stylistpark.widgets.InterceptRelativeLayout;
import com.spshop.stylistpark.widgets.InterceptRelativeLayout.OnDispatchTouchEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("HandlerLeak")
public class MultiAngleMainActivity extends BaseActivity implements OnClickListener {

	public static String TAG = "MultiAngleMainActivity";

	public static final int ACTIVITY_RESULT_CODE_WAIT_4_PRODUCT_AND_PHOTO = 2000;
	public static final int ACTIVITY_RESULT_CODE_WAIT_4_PHOTO = 2001;

	private static final int IMAGE_LOADING_TIMEOUT = 60000;
	private static final int DOWNLOAD_RETRY_TIME = 0;
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	private DisplayImageOptions options;

	ViewGroup root_layout;
	ViewGroup multiAngle_canvasLayout;
	ViewGroup multiAngle_gridLayout;

	InterceptRelativeLayout multiAngle_cell1Layout;
	InterceptRelativeLayout multiAngle_cell2Layout;
	InterceptRelativeLayout multiAngle_cell3Layout;
	InterceptRelativeLayout multiAngle_cell4Layout;

	InterceptRelativeLayout clickedViewGroup;
	Matrix oMatrix;

	ViewGroup multiangle_InfoLayout;
	ViewGroup multiangle_selectedInfoLayout;
	ImageView multiangle_photoImageView;
	TextView multiangle_brandTextView;
	TextView multiangle_nameTextView;
	TextView multiangle_priceTextView;

	Button multiAngle_changeProductBtn;

	long mLastClickTime;
	String productTypeID = null;
	Product selectedProduct = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiangle_main);

		setTitle(R.string.collage_create_multiangle);
		setBtnRight(getString(R.string.save));

		options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);

		multiangle_InfoLayout = (ViewGroup) findViewById(R.id.multiangle_InfoLayout);
		multiangle_selectedInfoLayout = (ViewGroup) findViewById(R.id.multiangle_selectedInfoLayout);
		multiangle_photoImageView = (ImageView) findViewById(R.id.multiangle_photoImageView);
		multiangle_brandTextView = (TextView) findViewById(R.id.multiangle_brandTextView);
		multiangle_nameTextView = (TextView) findViewById(R.id.multiangle_nameTextView);
		multiangle_priceTextView = (TextView) findViewById(R.id.multiangle_priceTextView);

		multiAngle_gridLayout = (ViewGroup) findViewById(R.id.multiAngle_gridLayout);
		multiAngle_canvasLayout = (ViewGroup) findViewById(R.id.multiAngle_canvasLayout);
		multiAngle_canvasLayout.getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
							multiAngle_canvasLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						else
							multiAngle_canvasLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) multiAngle_canvasLayout.getLayoutParams();
						lp.width = multiAngle_canvasLayout.getWidth();
						lp.height = multiAngle_canvasLayout.getWidth();
						multiAngle_canvasLayout.setLayoutParams(lp);
					}
				});
		multiAngle_cell1Layout = (InterceptRelativeLayout) findViewById(R.id.multiAngle_cell1Layout);
		multiAngle_cell1Layout.setOnDispatchTouchEventListener(getOnDispatchTouchEventListener());
		multiAngle_cell1Layout.setOnClickListener(this);
		multiAngle_cell1Layout.setOnTouchListener(getCheckClickOnTouchListener());
		multiAngle_cell2Layout = (InterceptRelativeLayout) findViewById(R.id.multiAngle_cell2Layout);
		multiAngle_cell2Layout.setOnDispatchTouchEventListener(getOnDispatchTouchEventListener());
		multiAngle_cell2Layout.setOnClickListener(this);
		multiAngle_cell2Layout.setOnTouchListener(getCheckClickOnTouchListener());
		multiAngle_cell3Layout = (InterceptRelativeLayout) findViewById(R.id.multiAngle_cell3Layout);
		multiAngle_cell3Layout.setOnDispatchTouchEventListener(getOnDispatchTouchEventListener());
		multiAngle_cell3Layout.setOnClickListener(this);
		multiAngle_cell3Layout.setOnTouchListener(getCheckClickOnTouchListener());
		multiAngle_cell4Layout = (InterceptRelativeLayout) findViewById(R.id.multiAngle_cell4Layout);
		multiAngle_cell4Layout.setOnDispatchTouchEventListener(getOnDispatchTouchEventListener());
		multiAngle_cell4Layout.setOnClickListener(this);
		multiAngle_cell4Layout.setOnTouchListener(getCheckClickOnTouchListener());

		multiAngle_changeProductBtn = (Button) findViewById(R.id.multiAngle_changeProductBtn);
		multiAngle_changeProductBtn.setOnClickListener(this);

		root_layout = (ViewGroup) findViewById(R.id.root_layout);
		root_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LogUtil.i(TAG, "root touched");
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// CollageView view =(CollageView)multiAngle_cell1Layout.getChildAt(0);
					break;
				}
				return true;
			}
		});

		clearSelectedProduct();

		/**
		 * For test only
		 */
		findViewById(R.id.multiAngle_testBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						CollageView view = (CollageView) multiAngle_cell1Layout.getChildAt(0);
						view.setPivotX(200.5f);
						view.setPivotY(254.5f);
						view.setScaleX(0.52238464f);
						view.setScaleY(0.52238464f);
						view.setRotation(42.1831f);
						view.setTranslationX(-135.31552f);
						view.setTranslationY(-120.195175f);
					}
				});
	}

	public OnDispatchTouchEventListener getOnDispatchTouchEventListener() {
		return new OnDispatchTouchEventListener() {

			float preX, preY;
			boolean preformClick = false;
			boolean passTouch = false;
			MotionEvent touchEvent;

			@Override
			public void onDispatchTouchEvent(InterceptRelativeLayout view, MotionEvent event) {
				if (event.getPointerCount() > 1) {
					view.callSuperDispatchTouchEvent(touchEvent); // pre down
					touchEvent.recycle();
					touchEvent = MotionEvent.obtain(event);
					preformClick = false;
					passTouch = true;
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touchEvent = MotionEvent.obtain(event);
					preX = event.getX();
					preY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					if (!passTouch) {
						float curX = event.getX();
						float curY = event.getY();
						float px = CommonTools.convertDpToPixel(MultiAngleMainActivity.this, 2.0f);
						double distance = CommonTools.getHypotenuseByPyth(new PointF(preX, preY), new PointF(curX, curY));
						LogUtil.i(TAG, "distance=" + distance + " px=" + px);
						if (distance <= px) {
							preformClick = true;
						} else {
							view.callSuperDispatchTouchEvent(touchEvent);
							touchEvent.recycle();
							touchEvent = MotionEvent.obtain(event);
							preformClick = false;
							passTouch = true;
						}
					} else {
						touchEvent.recycle();
						touchEvent = MotionEvent.obtain(event);
						view.callSuperDispatchTouchEvent(touchEvent);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (preformClick) {
						touchEvent.recycle();
						LogUtil.i(TAG, "Perfrom Click.");
						view.performClick();
					} else {
						touchEvent.recycle();
						touchEvent = MotionEvent.obtain(event);
						view.callSuperDispatchTouchEvent(touchEvent);
					}
					preformClick = false;
					passTouch = false;
					break;
				default:
					if (passTouch) {
						if (touchEvent != null) {
							touchEvent.recycle();
						}
						touchEvent = MotionEvent.obtain(event);
						view.callSuperDispatchTouchEvent(touchEvent);
					}
					break;
				}
			}

		};
	}

	public OnTouchListener getCheckClickOnTouchListener() {
		return new OnTouchListener() {

			float preX;
			float preY;
			boolean canPreformClick = true;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					preX = event.getX();
					preY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					float curX = event.getX();
					float curY = event.getY();
					float px = CommonTools.convertDpToPixel(MultiAngleMainActivity.this, 3.0f);
					double distance = CommonTools.getHypotenuseByPyth(new PointF(preX, preY), new PointF(curX, curY));
					LogUtil.i(TAG, "distance=" + distance + " px=" + px);
					if (distance >= px) {
						canPreformClick = false;
					}
				case MotionEvent.ACTION_UP:
					if (canPreformClick)
						v.performClick();
					break;
				}
				return true;
			}

		};
	}

	@Override
	public void onClick(View v) {
		// Preventing multiple clicks, using threshold of 1 second
		if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
			LogUtil.i(TAG, "onClick() - rejected");
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();

		switch (v.getId()) {
		case R.id.multiAngle_cell1Layout:
		case R.id.multiAngle_cell2Layout:
		case R.id.multiAngle_cell3Layout:
		case R.id.multiAngle_cell4Layout:
			clickedViewGroup = (InterceptRelativeLayout) v;
			if (selectedProduct == null) {
				selectProduct();
			} else {
				selectPhoto();
			}
			break;
		case R.id.multiAngle_changeProductBtn:
			UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_MULTIANGLE_CHANGE_PRODUCT, null);
			selectProduct();
			break;
		}
	}

	public void selectProduct() {
		Intent intent = new Intent(this, MultiAngleProductListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, ACTIVITY_RESULT_CODE_WAIT_4_PRODUCT_AND_PHOTO);
		overridePendingTransition(R.anim.slide_up_in, R.anim.no_anim_up_down);
	}

	public void selectPhoto() {
		Intent intent = new Intent(this, MultiAngleProductPhotoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(MultiAngleProductListActivity.INTENT_SELECTED_PRODUCT, selectedProduct);
		intent.putExtra(MultiAngleProductPhotoActivity.INTENT_FINISH_ANIM, FinishAnim.SlideDown);
		startActivityForResult(intent, ACTIVITY_RESULT_CODE_WAIT_4_PHOTO);
		overridePendingTransition(R.anim.slide_up_in, R.anim.no_anim_up_down);
	}

	public void clearSelectedProduct() {
		selectedProduct = null;
		productTypeID = null;
		multiangle_selectedInfoLayout.setVisibility(View.INVISIBLE);
		multiAngle_changeProductBtn.setVisibility(View.GONE);
		multiangle_InfoLayout.setVisibility(View.VISIBLE);
		clearCollage();
	}

	public void clearCollage() {
		multiAngle_cell1Layout.removeAllViews();
		multiAngle_cell2Layout.removeAllViews();
		multiAngle_cell3Layout.removeAllViews();
		multiAngle_cell4Layout.removeAllViews();
	}

	public boolean isAllGridHaveProduct() {
		boolean result = true;
		if (multiAngle_cell1Layout.getChildCount() == 0)
			result = false;
		if (multiAngle_cell2Layout.getChildCount() == 0)
			result = false;
		if (multiAngle_cell3Layout.getChildCount() == 0)
			result = false;
		if (multiAngle_cell4Layout.getChildCount() == 0)
			result = false;
		return result;

	}

	public boolean needDownloadProductThumb() {
		boolean result = false;
		Drawable drawable = multiangle_photoImageView.getDrawable();
		if (drawable == null) {
			result = true;
		} else {
			Bitmap b = ((BitmapDrawable) drawable).getBitmap();
			if (b == null || b.getWidth() == 0 || b.getHeight() == 0) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (needDownloadProductThumb()) {
			if (selectedProduct != null) {
				LogUtil.i(TAG, "Downlaod product thumb again.");
				ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + selectedProduct.getThumbUrl(), multiangle_photoImageView, options);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTIVITY_RESULT_CODE_WAIT_4_PRODUCT_AND_PHOTO) {
			LogUtil.i(TAG, "Result from MultiAngleProductListActivity");
			if (resultCode == Activity.RESULT_OK) {
				clearSelectedProduct();

				selectedProduct = (Product) data.getParcelableExtra(MultiAngleProductListActivity.INTENT_SELECTED_PRODUCT);
				productTypeID = data.getStringExtra(MultiAngleProductListActivity.INTENT_SELECTED_PRODUCT_TYPE_ID);

				String url = IMAGE_URL_HTTP + data.getStringExtra(MultiAngleProductPhotoActivity.INTENT_SELECTED_PHOTO_URL);
				LogUtil.i(TAG, "Img url=" + url + " productTypeID=" + productTypeID);

				if (selectedProduct != null) {
					ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + selectedProduct.getThumbUrl(), multiangle_photoImageView, options);
					multiangle_brandTextView.setText(selectedProduct.getBrand());
					multiangle_nameTextView.setText(selectedProduct.getName());
					multiangle_priceTextView.setText(LangCurrTools.getCurrencyValue(mContext) + selectedProduct.getPrice());
				}
				multiangle_selectedInfoLayout.setVisibility(View.VISIBLE);
				multiAngle_changeProductBtn.setVisibility(View.VISIBLE);
				multiangle_InfoLayout.setVisibility(View.INVISIBLE);
				downloadPhoto(clickedViewGroup, url);
			}
		}
		if (requestCode == ACTIVITY_RESULT_CODE_WAIT_4_PHOTO) {
			LogUtil.i(TAG, "Result from MultiAngleProductPhotoActivity");
			if (resultCode == Activity.RESULT_OK) {
				String url = IMAGE_URL_HTTP + data.getStringExtra(MultiAngleProductPhotoActivity.INTENT_SELECTED_PHOTO_URL);
				LogUtil.i(TAG, "Img url=" + url + " productTypeID=" + productTypeID);
				downloadPhoto(clickedViewGroup, url);
			}
		}

	}

	public void downloadPhoto(final RelativeLayout viewGroup, String url) {
		viewGroup.removeAllViews();
		startAnimation();
		// download, then add view
		ImageRequest ir = new ImageRequest(url,
				new Response.Listener<Bitmap>() {

					@Override
					public void onResponse(Bitmap bitmap) {
						LogUtil.i(TAG, "onResponse");
						if (bitmap == null) {
							LogUtil.i(TAG, "onResponse bitmap is null");
							showErrorDialog(R.string.dialog_error_msg);
							return;
						} else {
							LogUtil.i(TAG, "onResponse bitmap is not null");
						}
						final CollageView collageView = createCollageView();
						Bitmap newBm = BitmapUtil.sacleDownBitmap(bitmap, 0.5f);
						new KeyEffectTask(MultiAngleMainActivity.this, viewGroup, collageView).execute(newBm);
						//collageView.setImageBitmap(bitmap);
						//collageView.setTag(bitmap);
						//viewGroup.addView(collageView);
						//stopAnimation();
					}

				}, 0, 0, null, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError error) {
						ExceptionUtil.handle(mContext, error);
						showErrorDialog(R.string.dialog_error_msg);
						stopAnimation();
					}

				});
		ir.setRetryPolicy(new DefaultRetryPolicy(IMAGE_LOADING_TIMEOUT, DOWNLOAD_RETRY_TIME, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppApplication.getInstance().getRequestQueue().add(ir);
	}

	public void save() {
		if (selectedProduct != null && isAllGridHaveProduct()) {
			String filename = FileManager.getFileName();
			URI uri = CommonTools.captureView(this, multiAngle_canvasLayout, filename, GEN_OUTPUT_SIDE);
			// URI uri =drawResult();
			Intent i = new Intent(this, CollageInfoActivity.class);
			i.putExtra(CollageInfoActivity.CREATE_TYPE, TAG);
			i.putExtra(CollageInfoActivity.COLLAGE_URI, uri);
			i.putExtra(CollageInfoActivity.COLLAGE_HTML, getHtml(filename, GEN_OUTPUT_SIDE));
			i.putExtra(CollageInfoActivity.COLLAGE_MOBILE_HTML, getHtml(filename, GEN_OUTPUT_MOBILE_SIDE));
			i.putExtra(CollageInfoActivity.COLLAGE_LIST, new String[] { selectedProduct.getItemId() });
			i.putExtra(CollageInfoActivity.COLLAGE_NAME, selectedProduct.getName());
			startActivity(i);
		} else {
			showErrorDialog(R.string.collage_select_4_image);
		}
	}

	/**
	 * not in use
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public URI drawResult() {
		Bitmap result = null;
		File file = null;
		try {
			// float centerX;
			// float centerY;

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			// paint.setFilterBitmap(true);
			paint.setDither(true);

			result = Bitmap.createBitmap(multiAngle_canvasLayout.getWidth(), multiAngle_canvasLayout.getHeight(), Config.ARGB_8888);

			// centerX=multiAngle_canvasLayout.getWidth()/2;
			// centerY=multiAngle_canvasLayout.getHeight()/2;

			multiAngle_cell1Layout.getChildAt(0);
			CollageView view = (CollageView) multiAngle_cell1Layout.getChildAt(0);

			LogUtil.i(TAG, "ScaleX=" + view.getScaleX() + " ScaleY=" + view.getScaleY() + " Scale=" + view.getScale()
							+ " Rotate=" + view.getRotation() + " TranX=" + view.getTranslationX() 
							+ " TranY=" + view.getTranslationY() + " PivotX=" + view.getPivotX() + " PivotY=" + view.getPivotY());

			Bitmap bm = (Bitmap) view.getTag();

			int collageWidth = view.getWidth();
			int collageHeight = view.getHeight();
			int bmWidth = bm.getWidth();
			int bmHeight = bm.getHeight();

			Matrix matrix = new Matrix();
			matrix.setTranslate(view.getLeft(), view.getTop());
			matrix.postScale((float) collageWidth / bmWidth, (float) collageHeight / bmHeight);
			matrix.postScale(view.getScaleX(), view.getScaleX(), view.getPivotX(), view.getPivotY());
			matrix.postTranslate(view.getTranslationX(), view.getTranslationY());
			matrix.postRotate(view.getRotation(), view.getPivotX(), view.getPivotY());

			Canvas canvas = new Canvas(result);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bm, matrix, paint);
			multiAngle_gridLayout.setDrawingCacheEnabled(true);
			canvas.drawBitmap(multiAngle_gridLayout.getDrawingCache(), 0, 0, paint);
			multiAngle_gridLayout.destroyDrawingCache();
			multiAngle_gridLayout.setDrawingCacheEnabled(false);

			SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
			String dateTime = s.format(new Date());
			String filename = dateTime + ".png";

			File sdCard = getExternalCacheDir();
			file = new File(sdCard, filename);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				result.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				ExceptionUtil.handle(mContext, e);
			} catch (IOException e) {
				ExceptionUtil.handle(mContext, e);
			}
			BitmapUtil.scaleDownImageFile(file, 450, 450, CompressFormat.PNG, 100);

		} catch (Exception e) {
			ExceptionUtil.handle(mContext, e);
			if (result != null) {
				result.recycle();
				result = null;
			}
		}
		return file.toURI();
	}

	public CollageView createCollageView() {
		CollageView result;

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		result = new CollageView(MultiAngleMainActivity.this);
		result.setAdjustViewBounds(true);
		result.setLayoutParams(layoutParams);
		result.setOnTouchListener(new MultiTouchListener());
		result.setEnableBorder(false);
		result.setBackgroundColor(this.getResources().getColor(R.color.text_color_white));

		return result;

	}

	public String getHtml(String imgFileName, int side) {
		String url = String.format(AppConfig.PRODUCT_DETAIL_PAGE_PTAH_FORMAT, selectedProduct.getItemId());
		String htmlDoc = "<!DOCTYPE html><html>";
		htmlDoc += "<body>";
		htmlDoc += "<a href=\"" + url + "\">";
		htmlDoc += "<div style=\"background-size: cover; background-image: url('" + imgFileName + "'); " + 
		           "width:" + side + "px; height:" + side + "px;\">";
		htmlDoc += "</div>";
		htmlDoc += "</a>";
		htmlDoc += "</body>";
		htmlDoc += "</html>";
		return htmlDoc;
	}

	@Override
	public void onBackPressed() {
		ask4Leave();
	}

	@Override
	public void OnListenerLeft() {
		ask4Leave();
	}

	private void ask4Leave() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DIALOG_CONFIRM_CLICK:
					finish();
					break;
				default:
					break;
				}
			}
		};
		showConfirmDialog(R.string.collage_msg_leave_confirm, getString(R.string.confirm), getString(R.string.cancel), handler);
	}

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		save();
	}

	private static class KeyEffectTask extends AsyncTask<Bitmap, Void, Bitmap> {

		WeakReference<MultiAngleMainActivity> weakActivity;
		WeakReference<ViewGroup> weakViewGroup;
		CollageView weakCollageView;

		public KeyEffectTask(MultiAngleMainActivity multiAngleMainActivity, ViewGroup viewGroup, CollageView collageView) {
			weakActivity = new WeakReference<MultiAngleMainActivity>(multiAngleMainActivity);
			weakViewGroup = new WeakReference<ViewGroup>(viewGroup);
			weakCollageView = collageView;
		}

		@Override
		protected Bitmap doInBackground(Bitmap... arg) {
			Bitmap result = arg[0];
			try {
				result = CommonTools.keyEffects(weakActivity.get().getApplicationContext(), arg[0]);
			} catch (Exception e) {
				ExceptionUtil.handle(weakActivity.get(), e);
				result = null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Bitmap bm) {
			if (weakActivity.get() != null) {
				weakActivity.get().stopAnimation();
			}
			if (!isCancelled() && weakViewGroup.get() != null && weakCollageView != null) {
				weakCollageView.setImageBitmap(bm);
				weakViewGroup.get().addView(weakCollageView);
			} else {
				if (bm != null) {
					bm.recycle();
				}
			}
			weakCollageView = null;
		}
	}

}
