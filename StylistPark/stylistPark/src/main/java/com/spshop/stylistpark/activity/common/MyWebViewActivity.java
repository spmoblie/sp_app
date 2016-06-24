package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.WebViewLoadingBar;
import com.spshop.stylistpark.widgets.video.UniversalMediaController;
import com.spshop.stylistpark.widgets.video.UniversalVideoView;
import com.tencent.stat.StatService;


/**
 * "Html页面展示"Activity
 */
public class MyWebViewActivity extends BaseActivity implements UniversalVideoView.VideoViewCallback {

	private static final String TAG = "MyWebViewActivity";
	private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
	private static final String VIDEO_URL = "http://200003856.vod.myqcloud.com/200003856_dec90ec622e611e6bb8811599adb8d4a.f20.mp4";
	// WebView组件
	private WebView webview;
	private WebViewLoadingBar webViewLoadingBar;
	private String titleStr, videoUrl, loadingUrl;
	// Video组件
	private View fl_video_main;
	private UniversalVideoView uvv;
	private UniversalMediaController umc;
	private int mSeekPosition;
	private int cachedHeight;
	private boolean isFullscreen;
	private boolean isPause = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_webview);
		
		AppManager.getInstance().addActivity(this);//添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		titleStr = getIntent().getExtras().getString("title");
		//videoUrl = "S5C8F5323";
		loadingUrl = getIntent().getExtras().getString("url");

		findViewById();
		initView();
	}
	
	private void findViewById() {
		fl_video_main = findViewById(R.id.my_webview_fl_video_main);
		uvv = (UniversalVideoView) findViewById(R.id.my_webview_uvv);
		umc = (UniversalMediaController) findViewById(R.id.my_webview_umc);
		webview = (WebView) findViewById(R.id.my_webview);
		webViewLoadingBar = (WebViewLoadingBar)this.findViewById(R.id.my_webview_loading_bar);
	}

	private void initView() {
		setTitle(titleStr); //设置标题
		initWebview();
		initVideo();
	}

	private void initWebview() {
		if (webview != null){
			webview.loadDataWithBaseURL("", "", "text/html", "UTF-8", "");
			webview.addJavascriptInterface(new JsToJava(), "stub");
			//WebView属性设置
			WebSettings webSettings= webview.getSettings();
			String user_agent = webSettings.getUserAgentString();
			webSettings.setUserAgentString(user_agent+"_SP"); //设置UserAgent
			webSettings.setUseWideViewPort(true);  //设置webview推荐使用的窗口
			webSettings.setLoadWithOverviewMode(true);  //设置webview加载的页面的模式
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			//webSettings.setBlockNetworkImage(true); //是否显示网络图像
			webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
			//webSettings.setSupportZoom(true); //设置是否支持变焦
			//webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
			//webSettings.setDefaultFontSize(12); //设置默认的字体大小
			//webSettings.setFixedFontFamily(""); //设置固定使用的字体
			//webSettings.setAllowFileAccess(true); //是否允许访问文件
			//webSettings.setDatabaseEnabled(true); //是否允许使用数据库api
			//webSettings.setDomStorageEnabled(true); //是否允许使用Dom缓存
			//webSettings.setAppCacheEnabled(true); //有选择的缓存web浏览器中的东西
			//webSettings.setAppCachePath(""); //设置缓存路径
			//webSettings.setSavePassword(true); //是否允许保存密码
			//webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //设置布局方式

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
				}
			});

			//设置发送Cookies
			HttpUtil.synCookies(MyWebViewActivity.this, loadingUrl);

			//加载Url
			if (!StringUtil.isNull(loadingUrl)) {
				webview.loadUrl(loadingUrl);
			}
		}
	}

	private void initVideo() {
		if (!StringUtil.isNull(videoUrl)) {
			fl_video_main.setVisibility(View.VISIBLE);
			videoUrl = AppConfig.ENVIRONMENT_PRESENT_IMG_APP + "/video/" + videoUrl + ".mp4";
			umc.setVideoPath(uvv, VIDEO_URL);
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
			new MyVideoBitmapTask().execute(VIDEO_URL);
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
		super.onSaveInstanceState(outState);
		LogUtil.i(TAG, "onSaveInstanceState Position=" + uvv.getCurrentPosition());
		outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
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
		if(webview != null)
		{
			webview.destroy();
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
	}

}
