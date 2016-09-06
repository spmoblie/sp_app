package com.spshop.stylistpark.utils;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.entity.MyNameValuePair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpUtil {

	/* 请求方式 */
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;

	/**
	 * httpGet请求，返回一个Json字符串
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public String httpGet(String url, ArrayList<NameValuePair> params) {

		return null;
	}

	/**
	 * httpPost请求，返回一个Json字符串
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public String httpPost(String url, ArrayList<NameValuePair> params) {

		return null;
	}

	/**
	 * 返回响应实体
	 * 
	 * @param uri
	 * @param params
	 * @param method
	 */
	public static HttpEntity getEntity(String uri, List<MyNameValuePair> params, int method) throws Exception {
		HttpEntity entity = null;
		/** 设置连接、读取数据超时时间 */
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient client = new DefaultHttpClient(httpParams);

		String langStr = LangCurrTools.getLanguageHttpUrlValueStr();
		String curStr = LangCurrTools.getCurrencyHttpUrlValueStr();
		String cookie = FileManager.readFileSaveString(AppConfig.cookiesFileName, true);
		LogUtil.i("JsonParser", "读取 Cookie = " + cookie);
		
		HttpUriRequest request = null;
		switch (method) {
		case METHOD_GET: // get请求
			StringBuilder sb = new StringBuilder(uri);
			if (params != null && !params.isEmpty()) {
				params.add(new MyNameValuePair("lang", langStr));
				params.add(new MyNameValuePair("currency", curStr));
				
				sb.append('?');
				for (MyNameValuePair pair : params) {
					sb.append(pair.getName()).append('=')
					.append(pair.getValue()).append('&');
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			LogUtil.i("JsonParser", sb.toString());
			request = new HttpGet(sb.toString());
			request.setHeader("Cookie", cookie);
			break;
			
		case METHOD_POST: // post请求
			LogUtil.i("JsonParser", uri);
			request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setHeader("Cookie", cookie);

			if (params != null && !params.isEmpty()) {
				params.add(new MyNameValuePair("lang", langStr));
				params.add(new MyNameValuePair("currency", curStr));

				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, "UTF-8");
				((HttpPost) request).setEntity(reqEntity);
			}
			break;
		}
		
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				List<Cookie> cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
				if (cookies.isEmpty()) {
					LogUtil.i("JsonParser", "cookie is empty");
				} else {
					String cookieValue = "";
					for (int i = 0; i < cookies.size(); i++) {
						cookieValue += cookies.get(i).getName() + "=" + cookies.get(i).getValue() + "; ";
					}
					if (!StringUtil.isNull(cookieValue)) {
						LogUtil.i("JsonParser", "缓存 Cookie = " + cookieValue);
						FileManager.writeFileSaveString(AppConfig.cookiesFileName, cookieValue, true);
					}
				}
				entity = response.getEntity();
			}
		} catch (ConnectTimeoutException e) { // 超时时报此异常
			ExceptionUtil.handle(e);
		}

		return entity;
	}

	/**
	 * 返回实体内容字符串
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	public static String getString(HttpEntity entity) throws Exception {
		if (entity != null) {
			return EntityUtils.toString(entity, "UTF-8");
		}
		return "";
	}
	
	/**
	 * 通过响应实体对象获取网络输入流对象
	 * @param entity  响应实体对象
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static InputStream getStream(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = null;
		if (entity != null) {
			in = entity.getContent();
		}
		return in;
	}

	public String HttpGet(String httpUrl) {
		// HttpGet连接对象
		HttpGet httpRequest = new HttpGet(httpUrl);
		// 取得HttpClient对象
		HttpClient httpclient = new DefaultHttpClient();
		// 请求超时
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 1500);
		// 读取超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				1500);
		HttpResponse httpResponse;
		try {
			// 请求HttpClient，取得HttpResponse
			httpResponse = httpclient.execute(httpRequest);
			// 请求成功
			int returnCode = httpResponse.getStatusLine().getStatusCode();
			String strResult = "";
			if (returnCode == HttpStatus.SC_OK) {
				// 取得返回的字符串
				strResult = getString(httpResponse.getEntity());
				return strResult;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	/**
	 * 同步一下cookie
	 */
	public static void synCookies(String url) {
		CookieSyncManager.createInstance(AppApplication.getInstance().getApplicationContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie(); //移除所有Cookie
		String cookies = FileManager.readFileSaveString(AppConfig.cookiesFileName, true);
		if (!StringUtil.isNull(cookies)) { // cookies是在HttpClient中获得的cookie
        	String[] cks = cookies.split(";");
        	for (int i = 0; i < cks.length; i++) {
        		String ck = cks[i];
        		ck = ck.replace(" ", "");
        		if (!StringUtil.isNull(ck)) {
					cookieManager.setCookie(url, ck);
					LogUtil.i("JsonParser", "同步 setCookie = " + ck);
				}
			}
		}
		String newCookie = cookieManager.getCookie(url);
		LogUtil.i("JsonParser", "同步 getCookie = " + newCookie);
        CookieSyncManager.getInstance().sync();
	}

}

