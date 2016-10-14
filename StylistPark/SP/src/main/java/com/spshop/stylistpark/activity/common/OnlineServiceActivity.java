package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.ObservableWebView;
import com.spshop.stylistpark.widgets.WebViewLoadingBar;


/**
 * "在线客服"Activity
 */
public class OnlineServiceActivity extends BaseActivity {

	private static final String TAG = "OnlineServiceActivity";

	private Context mContext;
	// WebView组件
	private ObservableWebView webview;
	private WebViewLoadingBar webViewLoadingBar;
	private String titleStr, lodUrl;
	private boolean isSynCookies = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isInitShare = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_service);
		
		AppManager.getInstance().addActivity(this);//添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");

		mContext = this;
		Bundle bundle = getIntent().getExtras();
		titleStr = bundle.getString("title");
		lodUrl = bundle.getString("lodUrl");
		isSynCookies = bundle.getBoolean("isSynCookies", true);

		findViewById();
		initView();
	}
	
	private void findViewById() {
		webview = (ObservableWebView) findViewById(R.id.online_service_webview);
		webViewLoadingBar = (WebViewLoadingBar)this.findViewById(R.id.online_service_loading_bar);
	}

	private void initView() {
		setTitle(titleStr);
		initWebview();
	}

	@SuppressWarnings("static-access")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initWebview() {
		if (webview != null){
			//WebView属性设置
			WebSettings webSettings = webview.getSettings();
			webSettings.setDefaultTextEncodingName("UTF-8");
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
			webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
			//webSettings.setBlockNetworkImage(true); //是否显示网络图像
			webSettings.setSupportZoom(true); //设置是否支持变焦
			//webSettings.setDefaultFontSize(12); //设置默认的字体大小
			//webSettings.setFixedFontFamily(""); //设置固定使用的字体
			webSettings.setAllowFileAccess(true); //是否允许访问文件
			//webSettings.setDatabaseEnabled(true); //是否允许使用数据库api
			webSettings.setDomStorageEnabled(true); //是否允许使用Dom缓存
			webSettings.setAppCacheEnabled(true); //有选择的缓存web浏览器中的东西
			//webSettings.setAppCachePath(""); //设置缓存路径
			//webSettings.setSavePassword(true); //是否允许保存密码
			webSettings.setUseWideViewPort(true);  //设置webview推荐使用的窗口
			webSettings.setLoadWithOverviewMode(true);  //设置webview加载的页面的模式

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

			//加载Url
			if (!StringUtil.isNull(lodUrl)) {
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

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

}
