package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 下载并保存图片到本地
 */
public class LoaderAndSaveImage {
	
	private ArrayList<ImageLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private int pathCount;
	private ArrayList<File> imageUrls;
	private static LoaderAndSaveImage instance;
	
	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static LoaderAndSaveImage getInstance(final LoaderAndSaveImageCallback callback){
		if (instance == null) {
			synchronized (LoaderAndSaveImage.class) {
				if (instance == null) {
					instance = new LoaderAndSaveImage(AppApplication.getInstance().getApplicationContext(), callback);
				}
			}
		}
		return instance;
	}
	
	@SuppressLint("HandlerLeak")
	private LoaderAndSaveImage(final Context context, final LoaderAndSaveImageCallback callback){
		this.isLoop = true;
		this.tasks = new ArrayList<ImageLoadTask>();
		imageUrls = new ArrayList<File>();
		
		this.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				File file = (File) msg.obj;
				
				if (file == null) {
					callback.imageLoaded(imageUrls);
					imageUrls.clear();
				}else {
					imageUrls.add(file);
					if (imageUrls.size() == pathCount) {
						callback.imageLoaded(imageUrls);
						imageUrls.clear();
					}
				}
			};
		};
		this.workThread = new Thread(){
			@Override
			public void run() {
				while(isLoop){
					while(isLoop&&!tasks.isEmpty()){
						//从任务队列获取任务
						ImageLoadTask task = tasks.remove(0);
						try {
							HttpEntity entity = HttpUtil.getEntity(task.oldPath, null, HttpUtil.METHOD_GET);
						    byte[] data = EntityUtils.toByteArray(entity);
						    task.bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
						    //保存到本地
						    File file = BitmapUtil.createPath(task.newPath, task.saveOR);
						    AppApplication.saveBitmapFile(task.bitmap, file, 100);
						    Message msg = Message.obtain();
						    msg.obj = file;
						    handler.sendMessage(msg);
						}catch (Exception e) {
							exceptionHandle(e);
						}
					}
					if(!isLoop){
						break;
					}
					synchronized(workThread){
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
	
	public void quit(){
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
	 * 下载并保存图片
	 * 
	 * @param pathCount 图片张数
	 * @param oldPath 图片URL
	 * @param saveOR 是否保存到本地
	 */
	public void loadAndSaveImage(int pathCount, String oldPath, boolean saveOR){
		this.pathCount = pathCount;
		ImageLoadTask task = new ImageLoadTask(BitmapUtil.filterPath(oldPath), oldPath, saveOR);
		if(!tasks.equals(task)){
			tasks.add(task);
			synchronized (workThread) {
				try {
					//唤醒工作线程
					workThread.notify();
				} catch (Exception e) {
					exceptionHandle(e);
				}
			}
		}
	}

	private void exceptionHandle(Exception e) {
		Message msg = Message.obtain();
		msg.obj = null;
		handler.sendMessage(msg);
		ExceptionUtil.handle(e);
	}
	
	private class ImageLoadTask{
		private String newPath;
		private String oldPath;
		private Bitmap bitmap;
		private boolean saveOR;
		
		public ImageLoadTask(String newPath, String oldPath, boolean saveOR){
			this.newPath = newPath;
			this.oldPath = oldPath;
			this.saveOR = saveOR;
		}
		
		@Override
		public boolean equals(Object o) {
			ImageLoadTask task = (ImageLoadTask) o;
			return newPath.equals(task.newPath);
		}
	}
	
	public interface LoaderAndSaveImageCallback{
		void imageLoaded(ArrayList<File> imageUrls);
	}

}
