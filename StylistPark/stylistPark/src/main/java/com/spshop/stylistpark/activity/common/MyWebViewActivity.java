package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.activity.login.LoginActivity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.ObservableWebView;
import com.spshop.stylistpark.widgets.WebViewLoadingBar;
import com.spshop.stylistpark.widgets.video.UniversalMediaController;
import com.spshop.stylistpark.widgets.video.UniversalVideoView;
import com.tencent.stat.StatService;

import java.io.File;


/**
 * "Html页面展示"Activity
 */
public class MyWebViewActivity extends BaseActivity implements UniversalVideoView.VideoViewCallback, View.OnClickListener {

	private static final String TAG = "MyWebViewActivity";
	private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
	private static final int TYPE_LOAD_SUCCESS = 1001;
	private Context mContext;
	private ShareEntity shareEn;
	private AsyncImageLoader asyncImageLoader;
	// 评论组件
	private LinearLayout ll_comment_main;
	private EditText et_comment;
	private TextView tv_comment;
	private int postId;
	private String commentStr;
	// WebView组件
	private ObservableWebView webview;
	private WebViewLoadingBar webViewLoadingBar;
	private String titleStr, lodUrl;
	private int currY = 0;
	private int saveY = 0;
	private boolean isScroll = false;
	// Video组件
	private View fl_video_main;
	private UniversalVideoView uvv;
	private UniversalMediaController umc;
	private int mSeekPosition;
	private int cachedHeight;
	private String vdoUrl;
	private boolean isFullscreen;
	private boolean isPause = false;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TYPE_LOAD_SUCCESS:
					if (webview != null && isScroll && saveY > 0) {
						webview.scrollTo(0, saveY);
						saveY = 0;
						isScroll = false;
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isInitShare = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_webview);
		
		AppManager.getInstance().addActivity(this);//添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		mContext = this;
		postId = getIntent().getExtras().getInt("postId", 0);
		titleStr = getIntent().getExtras().getString("title");
		lodUrl = getIntent().getExtras().getString("lodUrl");
		vdoUrl = getIntent().getExtras().getString("vdoUrl");
		shareEn = (ShareEntity) getIntent().getExtras().getSerializable("shareEn");

		findViewById();
		initView();
	}
	
	private void findViewById() {
		ll_comment_main = (LinearLayout) findViewById(R.id.my_webview_ll_comment_main);
		et_comment = (EditText) findViewById(R.id.my_webview_et_comment);
		tv_comment = (TextView) findViewById(R.id.my_webview_tv_comment);
		fl_video_main = findViewById(R.id.my_webview_fl_video_main);
		uvv = (UniversalVideoView) findViewById(R.id.my_webview_uvv);
		umc = (UniversalMediaController) findViewById(R.id.my_webview_umc);
		webview = (ObservableWebView) findViewById(R.id.my_webview);
		webViewLoadingBar = (WebViewLoadingBar)this.findViewById(R.id.my_webview_loading_bar);
	}

	private void initView() {
		setTitle(titleStr);
		initWebview();
		initVideo();
		if (shareEn != null) {
			if (StringUtil.isNull(shareEn.getImagePath())) {
				loadShareImg();
			}
			setBtnRight(R.drawable.topbar_icon_share);
			// 初始化评论组件
			ll_comment_main.setVisibility(View.VISIBLE);
			tv_comment.setOnClickListener(this);
			et_comment.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					String comStr = s.toString();
					if (StringUtil.isNull(comStr)) {
						tv_comment.setTextColor(getResources().getColor(R.color.text_color_assist));
					} else {
						tv_comment.setTextColor(getResources().getColor(R.color.text_color_app_bar));
					}
				}
			});
		} else {
			ll_comment_main.setVisibility(View.GONE);
		}
	}

	private void loadShareImg() {
		if (!StringUtil.isNull(shareEn.getImageUrl())) {
			asyncImageLoader = AsyncImageLoader.getInstance(mContext, new AsyncImageLoader.AsyncImageLoaderCallback() {

				@Override
				public void imageLoaded(String path, File saveFile, Bitmap bm) {
					if (saveFile != null) {
						shareEn.setImagePath(saveFile.getPath());
					}
				}
			});
			asyncImageLoader.loadImage(false, shareEn.getImageUrl(), 0);
		}
	}

	@SuppressWarnings("static-access")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initWebview() {
		if (webview != null){
			//WebView属性设置
			WebSettings webSettings = webview.getSettings();
			String user_agent = webSettings.getUserAgentString();
			webSettings.setDefaultTextEncodingName("UTF-8");
			webSettings.setUserAgentString(user_agent+"_SP"); //设置UserAgent
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
			//webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
			//webSettings.setBlockNetworkImage(true); //是否显示网络图像
			//webSettings.setSupportZoom(true); //设置是否支持变焦
			//webSettings.setDefaultFontSize(12); //设置默认的字体大小
			//webSettings.setFixedFontFamily(""); //设置固定使用的字体
			//webSettings.setAllowFileAccess(true); //是否允许访问文件
			//webSettings.setDatabaseEnabled(true); //是否允许使用数据库api
			//webSettings.setDomStorageEnabled(true); //是否允许使用Dom缓存
			//webSettings.setAppCacheEnabled(true); //有选择的缓存web浏览器中的东西
			//webSettings.setAppCachePath(""); //设置缓存路径
			//webSettings.setSavePassword(true); //是否允许保存密码
			webSettings.setUseWideViewPort(true);  //设置webview推荐使用的窗口
			webSettings.setLoadWithOverviewMode(true);  //设置webview加载的页面的模式
			webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局

			//设置不允许外部浏览器打开
			webview.setWebViewClient(new WebViewClient(){

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});

			//设置加载动画
			webview.setWebChromeClient(new WebChromeClient(){
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);
					if(webViewLoadingBar != null) {
						webViewLoadingBar.setProgress(newProgress);
					}
					if (newProgress == 100) {
						mHandler.sendEmptyMessage(TYPE_LOAD_SUCCESS);
					}
				}
			});

			//设置滚动监听
			webview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
				@Override
				public void onScroll(int x, int y, int oldx, int oldy) {
					currY = y;
				}
			});

			//加载Url
			if (!StringUtil.isNull(lodUrl)) {
				webview.addJavascriptInterface(new JsToJava(), "stub");
				lodUrl = lodUrl + AppApplication.getHttpUrlLangCurValueStr();
				myLoadUrl(lodUrl);
			}
		}
	}

	private void myLoadUrl(String url) {
		HttpUtil.synCookies(MyWebViewActivity.this, url); //同步Cookies
		webview.loadUrl(url);
	}

	private void initVideo() {
		if (!StringUtil.isNull(vdoUrl)) {
			fl_video_main.setVisibility(View.VISIBLE);
			umc.setVideoPath(uvv, vdoUrl);
			umc.showComplete(); //显示居中播放按钮
			uvv.setMediaController(umc);
			setVideoAreaSize();
			uvv.setVideoViewCallback(this);
			uvv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					LogUtil.i(TAG, "onCompletion ");
				}
			});
			new MyVideoBitmapTask().execute(vdoUrl);
		}else {
			fl_video_main.setVisibility(View.GONE);
		}
	}

	/**
	 * 置视频区域大小
	 */
	private void setVideoAreaSize() {
		fl_video_main.post(new Runnable() {
			@Override
			public void run() {
				int width = fl_video_main.getWidth();
				cachedHeight = (int) (width * 405f / 720f);
                //cachedHeight = (int) (width * 3f / 4f);
                //cachedHeight = (int) (width * 9f / 16f);
				ViewGroup.LayoutParams videoLayoutParams = fl_video_main.getLayoutParams();
				videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
				videoLayoutParams.height = cachedHeight;
				fl_video_main.setLayoutParams(videoLayoutParams);
				//uvv.setVideoPath(VIDEO_URL);
				//uvv.requestFocus();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LogUtil.i(TAG, "onSaveInstanceState Position=" + uvv.getCurrentPosition());
		outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		super.onRestoreInstanceState(outState);
		mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
		LogUtil.i(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
	}


	@Override
	public void onScaleChange(boolean isFullscreen) {
		this.isFullscreen = isFullscreen;
		if (isFullscreen) {
			setHeadVisibility(View.GONE);
			ViewGroup.LayoutParams layoutParams = fl_video_main.getLayoutParams();
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
			fl_video_main.setLayoutParams(layoutParams);
		} else {
			setHeadVisibility(View.VISIBLE);
			ViewGroup.LayoutParams layoutParams = fl_video_main.getLayoutParams();
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = this.cachedHeight;
			fl_video_main.setLayoutParams(layoutParams);
		}
	}

	@Override
	public void onPause(MediaPlayer mediaPlayer) {
		LogUtil.i(TAG, "onPause UniversalVideoView callback");
		// 销毁对象
		if (asyncImageLoader != null) {
			asyncImageLoader.clearInstance();
		}
	}

	@Override
	public void onStart(MediaPlayer mediaPlayer) {
		if (uvv != null) {
			uvv.setBackgroundResource(R.color.ui_bg_color_percent_100);
		}
		LogUtil.i(TAG, "onStart UniversalVideoView callback");
	}

	@Override
	public void onBufferingStart(MediaPlayer mediaPlayer) {
		LogUtil.i(TAG, "onBufferingStart UniversalVideoView callback");
	}

	@Override
	public void onBufferingEnd(MediaPlayer mediaPlayer) {
		LogUtil.i(TAG, "onBufferingEnd UniversalVideoView callback");
	}

	@Override
	public void onBackPressed() {
		if (this.isFullscreen) {
			uvv.setFullscreen(false);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		showShareView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.my_webview_tv_comment:
				sendCommentTxt();
				break;
		}
	}

	private void showShareView(){
		if (mShareView != null && shareEn != null) {
			if (mShareView.isShowing()) {
				mShareView.showShareLayer(mContext, false);
				return;
			}
			mShareView.setShareEntity(shareEn);
			mShareView.showShareLayer(mContext, true);
		}else {
			showShareError();
		}
	}

	private void sendCommentTxt(){
		if (!UserManager.getInstance().checkIsLogined()) {
			openLoginActivity();
			return;
		}
		commentStr = et_comment.getText().toString();
		if (StringUtil.isNull(commentStr)) {
			CommonTools.showToast(mContext, getString(R.string.events_comment_input), 1000);
			return;
		}
		//new JsToJava().sendComment(commentStr);
		saveY = currY;
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_COMMENT_CODE);
	}

	private void openLoginActivity(){
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("rootPage", TAG);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);

		if (isPause && uvv != null && !uvv.isPlaying()) {
			LogUtil.i(TAG, "onResume mSeekPosition=" + mSeekPosition);
			uvv.seekTo(mSeekPosition);
			uvv.start();
			isPause = false;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);

		if (uvv != null && uvv.isPlaying()) {
			mSeekPosition = uvv.getCurrentPosition();
			LogUtil.i(TAG, "onPause mSeekPosition=" + mSeekPosition);
			uvv.pause();
			isPause = true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");

		if (uvv != null) {
			uvv.closePlayer();
		}
	}

	class MyVideoBitmapTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			return BitmapUtil.createVideoThumbnail(params[0], 640, 200);
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null && uvv != null) {
				uvv.setBackground(new BitmapDrawable(bitmap));
			}
		}
	}

	class JsToJava {
		@JavascriptInterface
		public void jsMethod(String goodsId) {
			if (!StringUtil.isNull(goodsId)) {
				Intent intent = new Intent(mContext, ProductDetailActivity.class);
				intent.putExtra("goodsId", StringUtil.getInteger(goodsId));
				startActivity(intent);
			} else {
				CommonTools.showToast(mContext, "GoodsId is null", 1000);
			}
		}

		@JavascriptInterface
		public void sendComment(final String commentStr) {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					webview.loadUrl("javascript: submitComment('" + commentStr + "')");
				}
			});
		}
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		switch (requestCode) {
			case AppConfig.REQUEST_SV_POST_COMMENT_CODE:
				return sc.postComment(postId, commentStr);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		stopAnimation();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_POST_COMMENT_CODE:
				if (result != null) {
					BaseEntity baseEn = (BaseEntity) result;
					if (baseEn.getErrCode() == 0) {
						et_comment.setText("");
						isScroll = true;
						myLoadUrl(lodUrl);
						if (StringUtil.isNull(baseEn.getErrInfo())) {
							CommonTools.showToast(mContext, getString(R.string.events_comment_ok), 2000);
						}else {
							CommonTools.showToast(mContext, baseEn.getErrInfo(), 2000);
						}
					}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
						// 登入超时，交BaseActivity处理
					}else {
						if (StringUtil.isNull(baseEn.getErrInfo())) {
							showServerBusy();
						}else {
							CommonTools.showToast(mContext, baseEn.getErrInfo(), 3000);
						}
					}
				}else {
					showServerBusy();
				}
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
}
