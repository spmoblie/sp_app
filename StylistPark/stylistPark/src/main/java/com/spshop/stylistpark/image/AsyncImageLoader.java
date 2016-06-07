package com.spshop.stylistpark.image;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;

/**
 * 执行图片下载任务并缓存到集合
 */
public class AsyncImageLoader {

	private ArrayList<ImageLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private BitmapCache caches;
	private Context context;
	private static AsyncImageLoader instance;

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static AsyncImageLoader getInstance(final Context context, final AsyncImageLoaderCallback callback) {
		if (instance == null) {
			synchronized (AsyncImageLoader.class) {
				if (instance == null) {
					instance = new AsyncImageLoader(context, callback);
				}
			}
		}
		return instance;
	}

	@SuppressLint("HandlerLeak")
	private AsyncImageLoader(final Context context, final AsyncImageLoaderCallback callback) {
		this.context = context;
		this.isLoop = true;
		this.caches = BitmapCache.getInstance();
		this.tasks = new ArrayList<ImageLoadTask>();

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ImageLoadTask task = (ImageLoadTask) msg.obj;
				callback.imageLoaded(task.oldPath, task.saveFile, task.bitmap);
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
							HttpEntity entity = HttpUtil.getEntity(task.oldPath, null, HttpUtil.METHOD_GET);
							byte[] data = EntityUtils.toByteArray(entity);
							if (task.type == 1) { // 下载头像
								task.bitmap = BitmapUtil.getBitmap(data, 80, 80);
							} else {
								task.bitmap = BitmapUtil.getBitmap(data, 640, 1080);
							}
							// 缓存到集合
							caches.addCacheBitmap(task.bitmap, task.newPath);
							// 缓存到内存
							task.saveFile = BitmapUtil.createPath(task.newPath, false);
							BitmapUtil.save(task.bitmap, task.saveFile, 100);
						} catch (Exception e) {
							ExceptionUtil.handle(context, e);
						}
						Message msg = Message.obtain();
						msg.obj = task;
						handler.sendMessage(msg);
					}
					if (!isLoop) {
						break;
					}
					synchronized (workThread) {
						try {
							workThread.wait();
						} catch (InterruptedException e) {
							ExceptionUtil.handle(context, e);
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
				ExceptionUtil.handle(context, e);
			}
		}
	}
	
	public void clearInstance(){
		instance = null;
	}

	/**
	 * 根据指定的图片路径获取图片对象
	 * 
	 * @param readCach
	 *            是否从缓存获取
	 * @param oldPath
	 *            图片路径
	 * @param type
	 *            图片类型（0:普通相片/1:头像）
	 */
	public Bitmap loadImage(boolean readCach, String oldPath, int type) {
		String newPath = "";
		if (oldPath.contains(":")) {
			newPath = oldPath.toString().replace(":", "");
		} else {
			newPath = oldPath;
		}
		Bitmap bm = null;
		if (readCach) {
			// 判定缓存集合中是否存在图片,如果存在则直接返回
			bm = caches.getBitmap(newPath);
			if (bm != null) {
				return bm;
			}
			// 判定SD卡中是否存在图片,如果存在则直接返回
			File file = BitmapUtil.createPath(newPath, false);
			if (file == null) {
    			return bm;
			}
			bm = BitmapUtil.getBitmap(file.getAbsolutePath());
			if (bm != null) {
				return bm;
			}
		}
		// 缓存及SD卡都不存在图片则新建任务加入任务队列
		ImageLoadTask task = new ImageLoadTask(newPath, oldPath, type);
		if (!tasks.equals(task)) {
			tasks.add(task);
			synchronized (workThread) {
				try {
					// 唤醒工作线程
					workThread.notify();
				} catch (Exception e) {
					ExceptionUtil.handle(context, e);
				}
			}
		}
		return bm;
	}

	private class ImageLoadTask {
		private String newPath;
		private String oldPath;
		private int type;
		private File saveFile;
		private Bitmap bitmap;

		public ImageLoadTask(String newPath, String oldPath, int type) {
			this.newPath = newPath;
			this.oldPath = oldPath;
			this.type = type;
		}

		@Override
		public boolean equals(Object o) {
			ImageLoadTask task = (ImageLoadTask) o;
			return newPath.equals(task.newPath);
		}
	}

	public interface AsyncImageLoaderCallback {
		void imageLoaded(String path, File saveFile, Bitmap bm);
	}

}
