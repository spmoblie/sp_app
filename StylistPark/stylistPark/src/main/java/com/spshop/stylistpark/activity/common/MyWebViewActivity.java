package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.WebViewLoadingBar;
import com.tencent.stat.StatService;


/**
 * "Html页面展示"Activity
 */
public class MyWebViewActivity extends BaseActivity {

	private static final String TAG = "MyWebViewActivity";
	
	private WebView webview;
	private WebViewLoadingBar webViewLoadingBar;
	private String titleStr, loadingUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_webview);
		
		AppManager.getInstance().addActivity(this);//添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		titleStr = getIntent().getExtras().getString("title");
		loadingUrl = getIntent().getExtras().getString("url");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		webview = (WebView) findViewById(R.id.my_webview);
		webViewLoadingBar = (WebViewLoadingBar)this.findViewById(R.id.my_webview_loading_bar);
		
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		setTitle(titleStr); //设置标题
		
		if (webview != null){
			//WebView属性设置
			WebSettings webSettings= webview.getSettings();
			String user_agent = webSettings.getUserAgentString();
			webSettings.setUserAgentString(user_agent+"_SP"); //设置UserAgent
			webSettings.setUseWideViewPort(true);  //设置webview推荐使用的窗口
			webSettings.setLoadWithOverviewMode(true);  //设置webview加载的页面的模式
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			//webSettings.setBlockNetworkImage(true); //是否显示网络图像
			//webSettings.setBuiltInZoomControls(true); //设置是否支持缩放
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
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		if(webview != null)
		{
			webview.destroy();
		}
	}
	
}
