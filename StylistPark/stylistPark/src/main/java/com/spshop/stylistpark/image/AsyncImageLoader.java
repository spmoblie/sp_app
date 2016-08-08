package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

/**
 * 执行图片下载任务并缓存到集合
 */
public class AsyncImageLoader {

	private ArrayList<ImageLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private BitmapCache caches;
	private static AsyncImageLoader instance;

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static AsyncImageLoader getInstance(final AsyncImageLoaderCallback callback) {
		if (instance == null) {
			synchronized (AsyncImageLoader.class) {
				if (instance == null) {
					instance = new AsyncImageLoader(callback);
				}
			}
		}
		return instance;
	}

	@SuppressLint("HandlerLeak")
	private AsyncImageLoader(final AsyncImageLoaderCallback callback) {
		this.isLoop = true;
		this.caches = BitmapCache.getInstance();
		this.tasks = new ArrayList<ImageLoadTask>();

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ImageLoadTask task = (ImageLoadTask) msg.obj;
				callback.imageLoaded(task.oldPath, task.newPath, task.bitmap);
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
							if (task.type == 1) { //下载头像
								task.bitmap = BitmapUtil.getBitmap(data, 80, 80);
							} else {
								task.bitmap = BitmapUtil.getBitmap(data, 640, 1280);
							}
							// 缓存到集合
							caches.addCacheBitmap(task.bitmap, task.newPath);
							// 缓存到内存
							/*task.cachePath = BitmapUtil.createPath(task.newPath, false).getAbsolutePath();
							BitmapUtil.save(task.bitmap, new File(task.cachePath), 100);*/
						} catch (Exception e) {
							ExceptionUtil.handle(e);
						} finally {
							Message msg = Message.obtain();
							msg.obj = task;
							handler.sendMessage(msg);
						}
					}
					if (!isLoop) {
						break;
					}
					synchronized (workThread) {
						try {
							workThread.wait();
						} catch (InterruptedException e) {
							ExceptionUtil.handle(e);
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
				ExceptionUtil.handle(e);
			}
		}
	}
	
	public void clearInstance(){
		quit();
		instance = null;
	}

	/**
	 * 根据指定的图片路径获取图片对象
	 * 
	 * @param oldPath
	 *            图片路径
	 * @param type
	 *            图片类型（0:普通相片/1:头像）
	 */
	public ImageLoadTask loadImage(String oldPath, int type) {
		ImageLoadTask task = null;
		String newPath = "";
		if (oldPath.contains(":")) {
			newPath = oldPath.toString().replace(":", "");
		} else {
			newPath = oldPath;
		}
		try {
			// 判定缓存集合中是否存在图片,如果存在则直接返回
			Bitmap bm = caches.getBitmap(newPath);
			if (bm != null) {
				task = new ImageLoadTask(oldPath, newPath, bm);
				return task;
			}
			// 判定SD卡中是否存在图片,如果存在则直接返回
			/*File file = BitmapUtil.createPath(newPath, false);
			if (file != null) {
				bm = BitmapUtil.getBitmap(file.getAbsolutePath());
				if (bm != null) {
					task = new ImageLoadTask(oldPath, file.getAbsolutePath(), bm);
					return task;
				}
			}*/
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			return  null;
		}
		// 缓存及SD卡都不存在图片则新建任务加入任务队列
		task = new ImageLoadTask(newPath, oldPath, type);
		if (!tasks.equals(task)) {
			tasks.add(task);
			synchronized (workThread) {
				try {
					// 唤醒工作线程
					workThread.notify();
				} catch (Exception e) {
					ExceptionUtil.handle(e);
				}
			}
		}
		return task;
	}

	public class ImageLoadTask {
		private String newPath;
		private String oldPath;
		private String cachePath;
		private int type;
		private Bitmap bitmap;

		public ImageLoadTask(String oldPath, String newPath, Bitmap bitmap) {
			this.oldPath = oldPath;
			this.newPath = newPath;
			this.bitmap = bitmap;
		}

		public ImageLoadTask(String newPath, String oldPath, int type) {
			this.newPath = newPath;
			this.oldPath = oldPath;
			this.type = type;
		}

		public String getNewPath() {
			return newPath;
		}

		public String getOldPath() {
			return oldPath;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		@Override
		public boolean equals(Object o) {
			ImageLoadTask task = (ImageLoadTask) o;
			return newPath.equals(task.newPath);
		}
	}

	public interface AsyncImageLoaderCallback {
		void imageLoaded(String path, String cachePath, Bitmap bm);
	}

}
