package com.spshop.stylistpark.activity.collage;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.MyWebViewActivity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.APIResult;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.utils.UserTracker;
import com.spshop.stylistpark.widgets.EditTextBackEvent;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

public class GeneratorPhotoEditActivity extends BaseActivity {

	private static final String TAG = "GeneratorPhotoEditActivity";

	public static final String COLLAGE_URI = "collage uri";
	public static final String COLLAGE_LIST = "collage product list";

	public static final int SUMBIT_SUCCESS = 0;
	public static final int SUMBIT_FAIL = 1;
	public static final int SUMBIT_INVALIDKEY = 2;

	public static final int HEADING_LIMIT = 20;
	public static final int COMMENT_LIMIT = 60;

	private static final int animationDuration = 400;

	private View layoutHeaderNImage;
	ImageView iv;
	EditTextBackEvent etHeading, etComment;
	Button btnShareSave, btnCancel;
	View viewDim;
	RelativeLayout layout2EditText;
	RelativeLayout layoutPopup, layoutRoot;
	ListView lvComment;

	ObjectAnimator mover = null;
	Bitmap collageBitmap;
	File collageFile;
	String[] productIdList;
	private Uri collageUri;

	public String heading, comment, lookBookUrl;
	int translateY4EditText;
	int heightKBHide, heightKBShow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collage_info);
		
		setTitle(R.string.collage_info);

		Intent intent = getIntent();
		productIdList = intent.getStringArrayExtra(COLLAGE_LIST);
		collageUri = (Uri) intent.getParcelableExtra(COLLAGE_URI);
		if (collageUri != null) {
			collageFile = new File(collageUri.getPath());
		}else {
			showErrorDialog(R.string.dialog_error_msg);
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		collageBitmap = null;
		try {
			collageBitmap = BitmapFactory.decodeStream(new FileInputStream(collageFile), null, options);
		} catch (Exception e) {
			ExceptionUtil.handle(mContext, e);
		}
		layoutHeaderNImage = findViewById(R.id.layoutHeaderNImage);
		iv = (ImageView) findViewById(R.id.ivCollage);
		etHeading = (EditTextBackEvent) findViewById(R.id.etHeading);
		etComment = (EditTextBackEvent) findViewById(R.id.etComment);
		btnShareSave = (Button) findViewById(R.id.btnShareSave);
		viewDim = findViewById(R.id.viewDim);
		layoutPopup = (RelativeLayout) findViewById(R.id.layoutPopup);
		layoutRoot = (RelativeLayout) findViewById(R.id.root_layout);
		lvComment = (ListView) findViewById(R.id.lvComment);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		layout2EditText = (RelativeLayout) findViewById(R.id.layout2EditText);

		init();
	}

	private void init() {
		etComment.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		etOriginHeight = etComment.getMeasuredHeight();
		LogUtil.i(TAG, "init etHeight " + etHeight);
		layoutRoot.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (animLock)
							return;
						LogUtil.i(TAG, "onGlobalLayout root.getHeight() " + layoutRoot.getHeight());

						if (heightKBHide == 0 && layoutRoot.getHeight() != 0)
							heightKBHide = layoutRoot.getHeight();
						else if (heightKBHide != layoutRoot.getHeight())
							heightKBShow = layoutRoot.getHeight();
						LogUtil.i(TAG, "onGlobalLayout big small " + heightKBHide + " " + heightKBShow);

						if (layoutRoot.getHeight() == heightKBHide) {
							LogUtil.i(TAG, "onGlobalLayout" + "kb");
							btnShareSave.setVisibility(View.VISIBLE);
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									reverseEditText();
								}
							}, 200);
						} else if (layoutRoot.getHeight() == heightKBShow) {
							LogUtil.i(TAG, "onGlobalLayout" + "kb hide");
							btnShareSave.setVisibility(View.INVISIBLE);
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									moveEditText();
								}
							}, 200);
						}
					}
				});

		if (collageBitmap != null) {
			iv.setImageBitmap(collageBitmap);
		}
		iv.post(new Runnable() {

			@Override
			public void run() {
				int h = iv.getHeight();
				int tm = ((RelativeLayout.LayoutParams) iv.getLayoutParams()).topMargin;
				translateY4EditText = h + tm;
				LogUtil.i(TAG, "run layoutHeaderNImage.getHeight(): " + layoutHeaderNImage.getHeight());
				translateY4EditText = layoutHeaderNImage.getHeight();
			}
		});

		viewDim.setVisibility(View.GONE);
		viewDim.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hidePopup();
				return true;
			}
		});

		layoutPopup.setVisibility(View.GONE);

		String[] shareComment = getResources().getStringArray(R.array.collage_array_share_comment);
		List<String> shareCommentList = Arrays.asList(shareComment);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list_collage_info, shareCommentList);
		lvComment.setAdapter(adapter);
		lvComment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String comment = (String) parent.getItemAtPosition(position);
				int length = etComment.getText().length();
				length += comment.length();
				if (length > COMMENT_LIMIT) {
					showErrorDialog(R.string.collage_msg_over_word_max);
				} else {
					etComment.append(comment);
				}
				hidePopup();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hidePopup();
			}
		});
	}

	ObjectAnimator mover4Info;
	ValueAnimator etHeightChanger;
	boolean animated = false;
	boolean animLock = false; // useless now
	int etOriginHeight, etHeight, etHeightAnimed;

	public void moveEditText() {
		LogUtil.i(TAG, "moveEditText animated " + animated);
		if (animated)
			return;
		animated = true;

		if (etHeightAnimed == 0) {
			etHeight = etComment.getHeight();
			int etTop = etComment.getTop() - translateY4EditText;
			etHeightAnimed = heightKBShow - etTop;
		}
		mover4Info = ObjectAnimator.ofFloat(layout2EditText, "translationY", 0f, -translateY4EditText);
		mover4Info.setDuration(animationDuration / 2);
		mover4Info.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				if (!animated) {
					runOnUiThread(new Runnable() {
						public void run() {
							layoutHeaderNImage.setVisibility(View.VISIBLE);
						}
					});
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				LogUtil.i(TAG, "onAnimationEnd animated" + animated);
				if (animated) {
					runOnUiThread(new Runnable() {
						public void run() {
							layoutHeaderNImage.setVisibility(View.GONE);
							layout2EditText.setTranslationY(0);
							heightenEditText();
						}
					});
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		mover4Info.start();
	}

	public void reverseEditText() {
		LogUtil.i(TAG, "reverseEditText");

		if (etHeightChanger != null && animated) {
			animated = false;
			etHeightChanger = ValueAnimator.ofInt(etHeightAnimed, etOriginHeight);
			etHeightChanger.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					int val = (Integer) valueAnimator.getAnimatedValue();
					etComment.setHeight(val);
				}

			});
			etHeightChanger.setDuration(animationDuration / 2);
			etHeightChanger.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mover4Info.reverse();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			etHeightChanger.start();
		}
	}

	public void heightenEditText() {
		LogUtil.i(TAG, "heightenEditText");
		etHeightChanger = ValueAnimator.ofInt(etHeight, etHeightAnimed);
		etHeightChanger.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int val = (Integer) valueAnimator.getAnimatedValue();
				etComment.setHeight(val);
			}

		});
		etHeightChanger.setDuration(animationDuration / 2);
		etHeightChanger.start();
	}

	public void clickAddComment(View v) {
		UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_SAMPLE_TEXT, null);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);

		// dim
		viewDim.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_popup_dismiss);
		viewDim.startAnimation(anim);

		int listViewH = CommonTools.getTotalHeightofListView(lvComment);
		int cancelTopMargin = ((RelativeLayout.LayoutParams) btnCancel.getLayoutParams()).topMargin;
		btnCancel.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int popupH = listViewH + btnCancel.getMeasuredHeight() + layoutPopup.getPaddingBottom() + cancelTopMargin;
		layoutPopup.getLayoutParams().height = popupH;
		layoutPopup.invalidate();
		layoutPopup.setVisibility(View.VISIBLE);
		layoutPopup.hasFocus();
		mover = ObjectAnimator.ofFloat(layoutPopup, "translationY", popupH, 0f);
		mover.setDuration(animationDuration);
		mover.start();
	}

	public void hidePopup() {
		viewDim.setVisibility(View.GONE);
		layoutPopup.setVisibility(View.GONE);
		layoutPopup.clearAnimation();
	}

	public void clickShareSave(View v) {
		LogUtil.i(TAG, "clickShareSave");
		UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_LOOKBOOK_SAVE, null);
		heading = etHeading.getText().toString();
		comment = etComment.getText().toString();
		if (TextUtils.isEmpty(heading) || TextUtils.isEmpty(comment)) {
			showErrorDialog(R.string.collage_msg_fill_title_comment);
			return;
		}
		if (collageFile != null) {
			startAnimation();
			String imgFileName = collageFile.getName();
			String html = getHtml(imgFileName, 450, "");
			String mobileHtml = getHtml(imgFileName, 225, "mobile");
			SubmitThread mSubmitThread = new SubmitThread(mContext, mHandler, "collage", 
					heading, comment, collageFile.getAbsolutePath(), productIdList, html, mobileHtml);
			mSubmitThread.start();
		}else {
			showErrorDialog(R.string.dialog_error_msg);
		}
	}

	@Override
	public void onBackPressed() {
		if (viewDim.getVisibility() == View.VISIBLE) {
			hidePopup();
		} else {
			super.onBackPressed();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SUMBIT_SUCCESS) {
				UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_LOOKBOOK_SHARE, null);
				APIResult result = (APIResult) msg.obj;
				String url = result.getReturnStr("url");
				String lookBookId = url.substring(url.indexOf("lookbook_id=") + 12, url.indexOf("&", url.indexOf("lookbook_id=")));
				url = AppConfig.URL_COMMON_FRANCHISEE_URL + "?act=lookbook_detail&lookbook_id="
						+ lookBookId + "&user_id=" + UserManager.getInstance().getUserId();
				
				Intent intent = new Intent();
				intent.setClass(GeneratorPhotoEditActivity.this, MyWebViewActivity.class); //Xu
				intent.putExtra("title", "搭配测试");
				intent.putExtra("lodUrl", url);
				intent.putExtra("vdoUrl", "");
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
			} else if (msg.what == SUMBIT_FAIL) {
				stopAnimation();
				showErrorDialog(R.string.loading_submit_fail);
			} else if (msg.what == SUMBIT_INVALIDKEY) {
				stopAnimation();
				//showInvaildKeyDialog(); //Xu
			}
		}
	};

	public static class SubmitThread extends Thread {
		String lookBookType, title, description, filePath, html, mobileHtml;
		String[] productIdList;
		private Context mCtx;
		private Handler mTHander;

		public SubmitThread(Context mContext, Handler mHandler,
				String lookBookType, String title, String description,
				String filePath, String[] productIdList, String html,
				String mobileHtml) {
			this.lookBookType = lookBookType;
			this.title = title;
			this.description = description;
			this.filePath = filePath;
			this.productIdList = productIdList;
			this.html = html;
			this.mobileHtml = mobileHtml;
			this.mCtx = mContext;
			this.mTHander = mHandler;
		}

		public void run() {
			Message msg = new Message();
			try {
				UserManager um = UserManager.getInstance();
				APIResult result = ServiceContext.getServiceContext().submitLookBook(mCtx, um.getUserId(), "", 
						lookBookType, title, description, filePath, productIdList, html, mobileHtml);
				if (result.isInvalidKey()) {
					AppApplication.AppLogout(false); //登录失效
					
					msg.what = SUMBIT_INVALIDKEY;
					msg.obj = result.getErrorMsg();
				} else if (result.isSuccess()) {
					msg.what = SUMBIT_SUCCESS;
					msg.obj = result;
				} else {
					LogUtil.i(TAG, "run" + result.getErrorMsg());
					msg.what = SUMBIT_FAIL;
				}
			} catch (Exception e) {
				ExceptionUtil.handle(mCtx, e);
			}
			if (!interrupted()) {
				mTHander.sendMessage(msg);
			}
		}
	}

	private String getHtml(String imgFileName, int side, String idTail) {
		String htmlDoc = "<!DOCTYPE html><html>";
		htmlDoc += "<body>";
		htmlDoc += "<div style=\"background-size: cover; background-image: url('"
				+ imgFileName
				+ "'); width:"
				+ side
				+ "px; height:"
				+ side
				+ "px;\">";
		htmlDoc += "</div>";
		htmlDoc += "</body>";
		htmlDoc += "</html>";
		return htmlDoc;
	}

}
