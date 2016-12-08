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
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.find.CommentActivity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.image.AsyncImageLoader;
import com.spshop.stylistpark.image.AsyncImageLoader.ImageLoadTask;
import com.spshop.stylistpark.image.AsyncMediaLoader;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.ObservableWebView;
import com.spshop.stylistpark.widgets.WebViewLoadingBar;
import com.spshop.stylistpark.widgets.video.UniversalMediaController;
import com.spshop.stylistpark.widgets.video.UniversalVideoView;

import java.io.File;
import java.io.IOException;

import static com.spshop.stylistpark.AppApplication.screenWidth;


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
	private AsyncMediaLoader asyncMediaLoader;
	// 评论组件
	private LinearLayout ll_comment_main;
	private int postId, goodsId;
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
	private Bitmap vdoImg;
	private String vdoUrl;
	private int mSeekPosition;
	private int cachedHeight;
	private boolean isComment, isFullscreen;
	private boolean isStop = false;
	private boolean isSynCookies = true;

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
		Bundle bundle = getIntent().getExtras();
		postId = bundle.getInt("postId", 0);
		goodsId = bundle.getInt("goodsId", 0);
		titleStr = bundle.getString("title");
		lodUrl = bundle.getString("lodUrl");
		vdoUrl = bundle.getString("vdoUrl");
		isComment = bundle.getBoolean("isComment", false);
		isSynCookies = bundle.getBoolean("isSynCookies", true);
		shareEn = (ShareEntity) bundle.getSerializable("shareEn");

		findViewById();
		initView();
	}
	
	private void findViewById() {
		ll_comment_main = (LinearLayout) findViewById(R.id.my_webview_ll_comment_main);
		fl_video_main = findViewById(R.id.my_webview_fl_video_main);
		uvv = (UniversalVideoView) findViewById(R.id.my_webview_uvv);
		umc = (UniversalMediaController) findViewById(R.id.my_webview_umc);
		webview = (ObservableWebView) findViewById(R.id.my_webview);
		webViewLoadingBar = (WebViewLoadingBar)this.findViewById(R.id.my_webview_loading_bar);
	}

	private void initView() {
		setTitle(titleStr);

		if (!NetworkUtil.isNetworkAvailable()) {
			showMyErrorDialog(getString(R.string.network_fault));
			return;
		}
		initVideo();
		loadShareImg();
		initWebview();
		// 初始化评论组件
		if (isComment) {
			ll_comment_main.setVisibility(View.VISIBLE);
			ll_comment_main.setOnClickListener(this);
		}
		// 初始化底部购物栏并显示
		if (goodsId == 1) { //达人计划
			setBottomBarVisibility(View.VISIBLE);
			setCollectionVisibility(View.GONE);
			showCartTotal();
			setAddCartViewStatus(getString(R.string.product_join_now), true);
		}
	}

	private void showMyErrorDialog(String content) {
		Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case DIALOG_CONFIRM_CLICK:
						finish();
						break;
				}
			}
		};
		showErrorDialog(content, false, mHandler);
	}

	private void loadShareImg() {
		if (shareEn != null && !StringUtil.isNull(shareEn.getImageUrl())) {
			asyncImageLoader = AsyncImageLoader.getInstance(new AsyncImageLoader.AsyncImageLoaderCallback() {

				@Override
				public void imageLoaded(String path, String cachePath, Bitmap bm) {
					vdoImg = bm;
					setVideoViewBackground();
					initShareData(vdoImg);
				}
			});
			ImageLoadTask task = asyncImageLoader.loadImage(shareEn.getImageUrl(), 0);
			if (task != null && task.getBitmap() != null) {
				vdoImg = task.getBitmap();
				setVideoViewBackground();
				initShareData(vdoImg);
			}
		}
	}

	private void initShareData(Bitmap bm) {
		if (shareEn != null) {
			shareEn.setShareBm(bm);
			setBtnRight(R.drawable.topbar_icon_share);
		}
	}

	@SuppressWarnings("static-access")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initWebview() {
		if (webview != null){
			//WebView属性设置
			WebSettings webSettings = webview.getSettings();
			webSettings.setDefaultTextEncodingName("UTF-8");
			//String user_agent = webSettings.getUserAgentString();
			//webSettings.setUserAgentString(user_agent+"_SP"); //设置UserAgent
			webSettings.setUserAgentString(" "); //设置UserAgent
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
			webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
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
			webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局

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
				lodUrl = lodUrl + LangCurrTools.getHttpUrlLangCurValueStr();
				myLoadUrl(lodUrl);
			}
		}
	}

	private void myLoadUrl(String url) {
		if (isSynCookies) {
			HttpUtil.synCookies(url); //同步Cookies
		}
		webview.loadUrl(url);
	}

	private void initVideo() {
		if (!StringUtil.isNull(vdoUrl)) {
			fl_video_main.setVisibility(View.VISIBLE);
			umc.setMediaPlayer(uvv);
			umc.showComplete(); //显示居中播放按钮
			umc.setOnErrorViewClick(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					umc.hideError();
				}
			});
			uvv.setMediaController(umc);
			setVideoAreaSize();
			uvv.setVideoViewCallback(this);
			uvv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					setVideoViewBackground();
					startCountdown();
					isStop = true;
					mSeekPosition = uvv.getCurrentPosition();
					LogUtil.i(TAG, "onCompletion Position = " + mSeekPosition);
				}
			});
			uvv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					umc.setIsFrist(true);
					try { //播放出错清除所有缓存视频
						FileManager.deleteFolderFile(new File(AppConfig.SAVE_PATH_MEDIA_DICE));
					} catch (IOException e) {
						ExceptionUtil.handle(e);
					}
					LogUtil.i(TAG, "onError");
					return false;
				}
			});
			// 异步抓取视频缩略图
			//new MyVideoBitmapTask().execute(vdoUrl);
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
				int wdith = fl_video_main.getWidth();
				cachedHeight = (int) (wdith * 405f / 720f);
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
		LogUtil.i(TAG, "onSaveInstanceState Position = " + mSeekPosition);
		outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		super.onRestoreInstanceState(outState);
		mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
		LogUtil.i(TAG, "onRestoreInstanceState Position = " + mSeekPosition);
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
	public void onLoadVideo() {
		if (!NetworkUtil.isWifi()) {
			umc.setIsFrist(true);
			showConfirmDialog(getString(R.string.network_no_wifi), getString(R.string.cancel),
					getString(R.string.proceed), true, true, new Handler() {
						@Override
						public void handleMessage(Message msg) {
							switch (msg.what) {
								case DIALOG_CANCEL_CLICK:
									break;
								case DIALOG_CONFIRM_CLICK:
									loadVideo();
									break;
							}
						}
					});
			return;
		} else {
			loadVideo();
		}
	}

	private void loadVideo() {
		clearCountdown();
		asyncMediaLoader = AsyncMediaLoader.getInstance(new AsyncMediaLoader.AsyncMediaLoaderCallback() {

			@Override
			public void mediaLoaded(String path, String savePath) {
				if (uvv != null && !StringUtil.isNull(savePath)) {
					if (uvv.isPlaying()) {
						mSeekPosition = uvv.getCurrentPosition();
					}
					uvv.setVideoPath(savePath);
					uvv.seekTo(mSeekPosition);
					LogUtil.i(TAG, "change cache path Position = " + mSeekPosition);
				}
			}
		});
		String videoPath = vdoUrl;
		String cachePath = asyncMediaLoader.createCachePath(AsyncMediaLoader.TYPE_VIDEO, vdoUrl, false, false);
		boolean isExists = FileManager.checkFileExists(cachePath);
		if (isExists) {
			videoPath = cachePath;
		}
		umc.setIsFrist(false);
		uvv.setVideoPath(videoPath);
		uvv.requestFocus();
		uvv.start();
		if (!isExists) {
			asyncMediaLoader.loadMedia(false, vdoUrl, AsyncMediaLoader.TYPE_VIDEO);
		}
	}

	private void setVideoViewBackground() {
		if (vdoImg != null && uvv != null) {
			uvv.setBackground(new BitmapDrawable(vdoImg));
		}
	}

	@Override
	public void onPause(MediaPlayer mediaPlayer) {
		LogUtil.i(TAG, "onPause UniversalVideoView callback");
		if (uvv != null) {
			mSeekPosition = uvv.getCurrentPosition();
			LogUtil.i(TAG, "onPause mSeekPosition = " + mSeekPosition);
		}
	}

	@Override
	public void onStart(MediaPlayer mediaPlayer) {
		isStop = false;
		if (uvv != null) {
			uvv.setBackgroundResource(R.color.ui_bg_color_percent_10);
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
		showShareView(shareEn);
	}

	@Override
	protected void openLoginActivity() {
		openLoginActivity(TAG);
	}

	@Override
	protected void postCollectionProduct() {
		if (goodsId > 0) {
			postCollectionProduct(goodsId);
		}
	}

	@Override
	protected void requestProductAttrData() {
		if (goodsId > 0) {
			requestProductAttrData(goodsId);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.my_webview_ll_comment_main:
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra("postId", postId);
				intent.putExtra("title", titleStr);
				startActivity(intent);
				break;
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		if (uvv != null && umc != null) {
			if (mSeekPosition > 0 && !isStop) {
				uvv.seekTo(mSeekPosition);
				uvv.start();
				LogUtil.i(TAG, "onResume mSeekPosition = " + mSeekPosition);
			}
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		// 销毁对象
		if (asyncImageLoader != null) {
			asyncImageLoader.clearInstance();
		}
		// 销毁对象
		if (asyncMediaLoader != null) {
			asyncMediaLoader.clearInstance();
		}
		if (uvv != null && uvv.isPlaying()) {
			uvv.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");

		if (uvv != null) {
			uvv.closePlayer();
			uvv = null;
		}
	}

	class MyVideoBitmapTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			return BitmapUtil.createVideoThumbnail(params[0], screenWidth, cachedHeight);
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			vdoImg = bitmap;
			setVideoViewBackground();
		}
	}

	class JsToJava {
		@JavascriptInterface
		public void jsMethod(String goodsId) {
			if (!StringUtil.isNull(goodsId)) {
				openProductDetailActivity(StringUtil.getInteger(goodsId));
			} else {
				CommonTools.showToast("GoodsId is null", 1000);
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
		return super.doInBackground(requestCode);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
}
