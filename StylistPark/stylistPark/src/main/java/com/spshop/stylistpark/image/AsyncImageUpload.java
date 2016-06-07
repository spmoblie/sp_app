package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.utils.APIResult;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.NetworkUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class AsyncImageUpload {

	private ArrayList<ImageLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private Context context;
	private static AsyncImageUpload instance;

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static AsyncImageUpload getInstance(final Context context, final AsyncImageUploadCallback callback) {
		if (instance == null) {
			synchronized (AsyncImageUpload.class) {
				if (instance == null) {
					instance = new AsyncImageUpload(context, callback);
				}
			}
		}
		return instance;
	}

	@SuppressLint("HandlerLeak")
	private AsyncImageUpload(final Context context, final AsyncImageUploadCallback callback) {
		this.context = context;
		this.isLoop = true;
		this.tasks = new ArrayList<ImageLoadTask>();

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				APIResult result = null;
				if (msg.obj != null) {
					result = (APIResult) msg.obj;
				}
				callback.uploadImageUrls(result);
				/*if (imageUrl.equals("error") || StringUtil.isNull(imageUrl)) {
					callback.uploadImageUrls(imageUrls);
					imageUrls.clear();
				} else {
					imageUrls.add(imageUrl);
					if (imageUrls.size() == pathCount) {
						callback.uploadImageUrls(imageUrls);
						imageUrls.clear();
					}
				}*/
			};
		};
		this.workThread = new Thread() {
			@Override
			public void run() {
				while (isLoop) {
					while (isLoop && !tasks.isEmpty()) {
						// 从任务队列获取任务
						ImageLoadTask task = tasks.remove(0);
						try {
							JSONObject jsonObj = NetworkUtil.getJSONFromURL(context, task.url, task.postData, task.path);
							APIResult result = new APIResult(context, jsonObj, "imgUrl", null);
							Message msg = new Message();
							msg.obj = result;
							handler.sendMessage(msg);

//							URL url = new URL(task.url);
//							HttpURLConnection con = (HttpURLConnection) url.openConnection();
//							con.setConnectTimeout(10000);
//							// 允许Input、Output，不使用Cache
//							con.setDoInput(true);
//							con.setDoOutput(true);
//							con.setUseCaches(false);
//							// 设置传送的method=POST
//							con.setRequestMethod("POST");
//							con.setRequestProperty("Connection", "Keep-Alive");
//							con.setRequestProperty("Charset", "UTF-8");
//							con.setRequestProperty("Cookie", FileManager.readFileSaveString(context, AppConfig.cookiesFileName, true));
//							// 设置DataOutputStream
//							DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//							LogUtil.i("path", task.path);
//							Bitmap bm = BitmapUtil.getBitmap(task.path);
//							bm = BitmapUtil.resizeImageByWidth(bm, 640);
//							byte[] bytes = BitmapUtil.bmpToByteArray(context, bm, true);
//							ds.write(bytes, 0, bytes.length);
//							ds.flush();
//							// 取得Response内容
//							InputStream is = con.getInputStream();
//							int ch;
//							StringBuffer jsonStr = new StringBuffer();
//							while ((ch = is.read()) != -1) {
//								jsonStr.append((char) ch);
//							}
//							ds.close();
//
//							String imageUrl = JsonParser.getUploadImageUrl(jsonStr.toString());
//
//							Message msg = new Message();
//							msg.obj = imageUrl;
//							handler.sendMessage(msg);

						} catch (Exception e) {
							exceptionHandle(e);
						}
					}
					if (!isLoop) {
						break;
					}
					synchronized (workThread) {
						try {
							workThread.wait();
						} catch (InterruptedException e) {
							exceptionHandle(e);
						}
					}
				}
			}
		};
		this.workThread.start();
	}

	public void quit() {
		isLoop = false;
		synchronized (workThread) {
			try {
				workThread.notify();
			} catch (Exception e) {
				exceptionHandle(e);
			}
		}
	}

	public void clearInstance(){
		instance = null;
	}

	/**
	 * 根据图片路径启动线程上传图片到服务器
	 *
	 * @param path 图片路径
	 */
	public void uploadImage(String url, Map<String, String> postData, String path) {
		ImageLoadTask task = new ImageLoadTask(url, postData, path);
		if (!tasks.equals(task)) {
			tasks.add(task);
			synchronized (workThread) {
				try {
					// 唤醒工作线程
					workThread.notify();
				} catch (Exception e) {
					exceptionHandle(e);
				}
			}
		}
	}

	private class ImageLoadTask {
		private String url;
		private Map<String, String> postData;
		private String path;

		public ImageLoadTask(String url, Map<String, String> postData, String path) {
			this.url = url;
			this.postData = postData;
			this.path = path;
		}

		@Override
		public boolean equals(Object o) {
			ImageLoadTask task = (ImageLoadTask) o;
			return path.equals(task.path);
		}
	}

	public String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}
	}

	private void exceptionHandle(Exception e) {
		ExceptionUtil.handle(context, e);
		Message msg = new Message();
		msg.obj = null;
		handler.sendMessage(msg);
	}

	public interface AsyncImageUploadCallback {
		void uploadImageUrls(APIResult result);
	}

}
