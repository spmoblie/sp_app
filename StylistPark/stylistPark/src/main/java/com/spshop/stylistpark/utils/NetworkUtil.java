package com.spshop.stylistpark.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;

import java.util.List;

public class NetworkUtil {

/*	private final static String TAG = "NetworkUtil";
	public final static int SUCCESS = 999999;
	public final static int FAIL = -999999;

	public static JSONObject getJSONFromURL(String path)throws Exception{
		return getJSONFromURL(path, null, null);
	}

	public static JSONObject getJSONFromURL(String path, Map<String, String> postData) throws Exception{
		return getJSONFromURL(path, postData, null);
	}

	public static JSONObject getJSONFromURL(String path, Map<String, String> postData, String fileToUpload)
			throws Exception{
		try {
			String jsonText =  getJSONStringFromURL(path, (postData != null && !postData.isEmpty()), postData, fileToUpload);
			return new JSONObject(jsonText);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static JSONArray getJSONArrayFromURL(String path, Map<String, String> postData)
			throws Exception{
		try {
			String jsonText =  getJSONStringFromURL(path, (postData != null), postData, null);
			return new JSONArray(jsonText);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String getJSONStringFromURL(String path, boolean usePost, Map<String, String> postData, String fileToUpload)
			throws Exception{
		try{
			InputStream jsonInStream;

			jsonInStream = getInputStream(path, usePost, postData, fileToUpload);

			BufferedReader rd = new BufferedReader(new InputStreamReader(jsonInStream, Charset.forName("UTF-8")));
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			String returnStr = sb.toString();
			LogUtil.i("JsonParser", path + "\n" + returnStr);
			return  returnStr;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	private static InputStream getInputStream(String path, boolean usePost, Map<String, String> postData, String fileToUpload ) throws Exception{
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection)url.openConnection();
			String cookie = FileManager.readFileSaveString(AppConfig.cookiesFileName, true);
			LogUtil.i("JsonParser", "read cookie = " + cookie);
			if(usePost){
				if(fileToUpload == null || fileToUpload.isEmpty()){
					conn.setReadTimeout(12000 *//* milliseconds *//*);
					conn.setConnectTimeout(20000 *//* milliseconds *//*);
					conn.setUseCaches(false);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					conn.setRequestProperty("Cookie", cookie);
					conn.setRequestMethod("POST");
					// Starts the query
					conn.connect();
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					String content = "";
					if(postData != null) {
						Iterator<Map.Entry<String, String>> iterator = postData.entrySet().iterator();
						while (iterator.hasNext()) {
							Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
							if(mapEntry.getValue() == null || mapEntry.getKey() == null){
								continue;
							}
							if (content.length() > 0) {
								content += "&";
							}
							content += mapEntry.getKey() + "=" + URLEncoder.encode(mapEntry.getValue(), "utf-8");
						}
					}
					out.writeBytes(content);
					out.flush();
					out.close();
				}else{
					String twoHyphens = "--";
					String boundary = "*****";
					String end = "\r\n";
					byte[] buffer;
					int maxBufferSize = 1 * 1024 * 1024;
					int bytesRead, bytesAvailable, bufferSize;
					File sourceFile = new File(fileToUpload);
					if (!sourceFile.isFile()) {
						Log.e("getInputStream", "Source File Does not exist");
						return null;
					}
					FileInputStream fileInputStream = new FileInputStream(sourceFile);

					conn.setUseCaches(false);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("ENCTYPE", "multipart/form-data");
					conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary + "; charset=utf-8");
					conn.setRequestProperty("Cookie", cookie);
					conn.setRequestProperty("file", fileToUpload);

					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(twoHyphens + boundary + end);
					String fileName = fileToUpload;
					if(postData != null) {
						fileName = postData.get("fileName");
						StringBuffer res = new StringBuffer("");
						Iterator<Map.Entry<String, String>> iterator = postData.entrySet().iterator();
						while (iterator.hasNext()) {
							Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
							res.append("Content-Disposition: form-data; name=\"")
									.append(mapEntry.getKey()).append("\"")
									.append(end).append(end)
									.append(URLEncoder.encode(mapEntry.getValue(), "utf-8"))
									.append(end)
									.append(twoHyphens).append(boundary).append(end);
						}
						out.writeBytes(res.toString());
					}

					out.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + end);
					out.writeBytes(end);

					bytesAvailable = fileInputStream.available(); // create a buffer of maximum size
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];
					// read file and write it into form...
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					while (bytesRead > 0) {
						out.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					}
					// send multipart form data necesssary after file data...
					out.writeBytes(end);
					out.writeBytes(twoHyphens + boundary + twoHyphens + end);
					// close the streams //
					fileInputStream.close();
					out.flush();
					out.close();
				}
			}else{
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(3000);
				conn.setRequestProperty("Cookie", cookie);
			}

			int resCode = conn.getResponseCode();
			if( resCode == HttpURLConnection.HTTP_NOT_MODIFIED || resCode == HttpURLConnection.HTTP_OK ){
				return conn.getInputStream();
			}else{
				Log.e(TAG, "getInputStream - HTTP Response code:"+resCode);
			}
		}catch(SocketTimeoutException e){
			Log.e(TAG, "getInputStream - Socket Time out");
			throw e;
		}catch(ConnectException e){
			Log.e(TAG, "getInputStream - No connection");
			throw e;
		}catch(UnknownHostException e){
			Log.e(TAG, "getInputStream - Cannot connect to host");
			throw e;
		}catch(Exception e){
			Log.e(TAG, "getInputStream - ex: "+e.toString()+",msg: "+e.getMessage());
			throw e;
		}
		return null;
	}*/

	/**
	 * 检查网络状态并弹出对话框提醒
	 * @param context
	 */
	public static void checkNetworkState(final Context context){
		try {
			ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo=manager.getActiveNetworkInfo();
			if(networkInfo == null){
				AlertDialog.Builder dialog = new Builder(context);
				dialog.setTitle(R.string.network);
				dialog.setMessage(R.string.network_closed);
				dialog.setPositiveButton(R.string.setting, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							//打开系统的网络设置界面
							Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
							context.startActivity(intent);
						} catch (Exception e) {
							ExceptionUtil.handle(e);
						}
					}
				});
				dialog.setNeutralButton(R.string.cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}
	
	/**
	 * 网络是否可用
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 网络连接提示
	 */
	public static boolean networkStateTips() {
		return isNetworkAvailable();
	}

	/**
	 * Gps是否打开
	 */
	public static boolean isGpsEnabled() {
		LocationManager locationManager = ((LocationManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled() {
		Context ctx = AppApplication.getInstance().getApplicationContext();
		ConnectivityManager mgrConn = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
	 */
	public static boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否3G网络
	 */
	public static boolean is3G() {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getInstance()
				.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
}
