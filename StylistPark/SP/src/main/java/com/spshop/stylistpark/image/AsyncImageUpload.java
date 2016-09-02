package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.service.JsonParser;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LogUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class AsyncImageUpload {

	private ArrayList<ImageLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private static AsyncImageUpload instance;

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static AsyncImageUpload getInstance(final AsyncImageUploadCallback callback) {
		if (instance == null) {
			synchronized (AsyncImageUpload.class) {
				if (instance == null) {
					instance = new AsyncImageUpload(callback);
				}
			}
		}
		return instance;
	}

	@SuppressLint("HandlerLeak")
	private AsyncImageUpload(final AsyncImageUploadCallback callback) {
		this.isLoop = true;
		this.tasks = new ArrayList<ImageLoadTask>();

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				BaseEntity baseEn = null;
				if (msg.obj != null) {
					baseEn = (BaseEntity) msg.obj;
				}
				callback.uploadImageUrls(baseEn);
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
							URL url = new URL(task.url);
							HttpURLConnection conn = (HttpURLConnection)url.openConnection();
							String cookie = FileManager.readFileSaveString(AppConfig.cookiesFileName, true);
							LogUtil.i("JsonParser", "读取 Cookie = " + cookie);

							String end = "\r\n";
							String twoHyphens = "--";
							String boundary = "******";
							File sourceFile = new File(task.path);
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
							conn.setRequestProperty("Cookie", cookie);

							String userId = "";
							String fileName = "";
							if (task.postData != null) {
								userId = task.postData.get("userId");
								fileName = task.postData.get("fileName") + ".png";
							}
							DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
							// 发送参数
							dos.writeBytes(twoHyphens + boundary + end);
							dos.writeBytes("Content-Disposition: form-data; name=\"userid\"" + end);
							dos.writeBytes(end);
							dos.writeBytes(userId);
							dos.writeBytes(end);
							// 发送图片
							dos.writeBytes(twoHyphens + boundary + end);
							dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + fileName + "\"" + end);
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

							// 取得Response内容
							InputStream is = conn.getInputStream();
							int ch;
							StringBuffer jsonStr = new StringBuffer();
							while ((ch = is.read()) != -1) {
								jsonStr.append((char) ch);
							}
							LogUtil.i("JsonParser", task.url + "\n" + jsonStr.toString());
							BaseEntity baseEn = JsonParser.getCommonResult(jsonStr.toString());
							Message msg = new Message();
							msg.obj = baseEn;
							handler.sendMessage(msg);
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
		quit();
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
		Message msg = new Message();
		msg.obj = null;
		handler.sendMessage(msg);
		ExceptionUtil.handle(e);
	}

	public interface AsyncImageUploadCallback {
		void uploadImageUrls(BaseEntity baseEn);
	}

}
