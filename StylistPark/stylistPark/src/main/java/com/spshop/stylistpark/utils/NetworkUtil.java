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
import android.util.Log;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NetworkUtil {

	private final static String TAG = "NetworkUtil";
	public final static int SUCCESS = 999999;
	public final static int FAIL = -999999;

	public static JSONObject getJSONFromURL(Context ctx, String path)throws Exception{
		return getJSONFromURL(ctx, path, null, null);
	}

	public static JSONObject getJSONFromURL(Context ctx, String path, Map<String, String> postData) throws Exception{
		return getJSONFromURL(ctx, path, postData, null);
	}

	public static JSONObject getJSONFromURL(Context ctx, String path, Map<String, String> postData, String fileToUpload)
			throws Exception{
		try {
			String jsonText =  getJSONStringFromURL(ctx, path, (postData != null && !postData.isEmpty()), postData, fileToUpload);
			return new JSONObject(jsonText);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static JSONArray getJSONArrayFromURL(Context ctx, String path, Map<String, String> postData)
			throws Exception{
		try {
			String jsonText =  getJSONStringFromURL(ctx, path, (postData != null), postData, null);
			return new JSONArray(jsonText);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String getJSONStringFromURL(Context ctx, String path, boolean usePost, Map<String, String> postData, String fileToUpload)
			throws Exception{
		try{
			InputStream jsonInStream;

			jsonInStream = getInputStream(ctx, path, usePost, postData, fileToUpload);

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

	private static InputStream getInputStream(Context ctx, String path, boolean usePost, Map<String, String> postData, String fileToUpload )
			throws Exception{
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection)url.openConnection();

			if(usePost){
				if(fileToUpload == null || fileToUpload.isEmpty()){
					conn.setReadTimeout(12000 /* milliseconds */);
					conn.setConnectTimeout(20000 /* milliseconds */);
					conn.setUseCaches(false);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					conn.setRequestProperty("Cookie", FileManager.readFileSaveString(ctx, AppConfig.cookiesFileName, true));
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
					/*String boundary = "*****";
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
					conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary+"; charset=utf-8");
					conn.setRequestProperty("Cookie", FileManager.readFileSaveString(ctx, AppConfig.cookiesFileName, true));
					// not work
//		            conn.setRequestProperty("Accept-Charset", "utf-8");
//		            conn.setRequestProperty("contentType", "utf-8");

//		            File file = new File(fileToUpload);
//		            FileInputStream fin = null;
//		            byte[] fileContent;
//		            String fileContent2Str = null;
//		            try {
//		                // create FileInputStream object
//		                fin = new FileInputStream(file);
//
//		                fileContent = new byte[(int)file.length()];
//
//		                // Reads up to certain bytes of data from this input stream into an array of bytes.
//		                fin.read(fileContent);
//		                //create string from byte array
//		                fileContent2Str = new String(fileContent);
//		                Log.d("raydebug", "File content: " + fileContent2Str);
//		            }
//		            catch (FileNotFoundException e) {
//		                Log.d("raydebug", "File not found" + e);
//		            }
//		            catch (IOException ioe) {
//		                Log.d("raydebug", "Exception while reading file " + ioe);
//		            }
					conn.setRequestProperty("file", fileToUpload); // byte[]
//		            if(fileToUpload.endsWith("png")){
//		                conn.setRequestProperty("file", fileContent2Str);
//		            }else{
//		                conn.setRequestProperty("file", fileToUpload);
//		            }
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes("--"+boundary+"\r\n");
					String fileName = fileToUpload;
					if(postData != null) {
						fileName = postData.get("avatar");
						StringBuffer res = new StringBuffer("");
						Iterator<Map.Entry<String, String>> iterator = postData.entrySet().iterator();

						while (iterator.hasNext()) {
							Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
							res.append("Content-Disposition: form-data; name=\"").append(mapEntry.getKey()).append("\"\r\n")
									.append("\r\n").append(URLEncoder.encode(mapEntry.getValue(), "utf-8")).append("\r\n")
									.append("--").append(boundary).append("\r\n");
						}
						out.writeBytes(res.toString());
					}

					out.writeBytes("Content-Disposition: form-data; type=\"file\";filename=\"" + fileName + "\"\r\n");
					out.writeBytes("\r\n");

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
					out.writeBytes("\r\n");
					out.writeBytes("--"+boundary+"--\r\n");

					// close the streams //
					fileInputStream.close();
					out.flush();
					out.close();*/

					String end = "\r\n";
					String twoHyphens = "--";
					String boundary = "******";
					try
					{
						File sourceFile = new File(fileToUpload);
						if (!sourceFile.isFile()) {
							Log.e("getInputStream", "Source File Does not exist");
							return null;
						}
						// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
						// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
						conn.setChunkedStreamingMode(128 * 1024);// 128K
						// 允许输入输出流
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setUseCaches(false);
						// 使用POST方法
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Connection", "Keep-Alive");
						conn.setRequestProperty("Charset", "UTF-8");
						conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
						conn.setRequestProperty("Cookie", FileManager.readFileSaveString(ctx, AppConfig.cookiesFileName, true));

						DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
						dos.writeBytes(twoHyphens + boundary + end);
						dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
								+ postData.get("avatar") + "\"" + end);
						dos.writeBytes(end);

						FileInputStream fis = new FileInputStream(sourceFile);
						byte[] buffer = new byte[8192]; // 8k
						int count = 0;
						// 读取文件
						while ((count = fis.read(buffer)) != -1)
						{
							dos.write(buffer, 0, count);
						}
						fis.close();

						dos.writeBytes(end);
						dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
						dos.flush();
						dos.close();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}

			}else{
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(3000);
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
	}

	/**
	 * 检查网络状态并弹出对话框提醒
	 * @param context
	 */
	public static void checkNetworkState(final Context context){
		try {
			ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo=manager.getActiveNetworkInfo();
			if(networkInfo == null){
				AlertDialog.Builder dialog=new Builder(context);
				dialog.setTitle(R.string.network);
				dialog.setMessage(R.string.network_closed);
				dialog.setPositiveButton(R.string.setting, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							//打开系统的网络设置界面
							Intent intent=new Intent(android.provider.Settings.ACTION_SETTINGS);
							context.startActivity(intent);
						} catch (Exception e) {
							ExceptionUtil.handle(context, e);
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
			ExceptionUtil.handle(context, e);
		}
	}
	
	/**
	 * 网络是否可用
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	 * 
	 * @param context
	 * @return
	 */
	public static boolean networkStateTips(Context context) {
		return isNetworkAvailable(context);
	}

	/**
	 * Gps是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否3G网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
}
