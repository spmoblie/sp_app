package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;

import org.apache.http.HttpEntity;

import java.io.File;
import java.util.ArrayList;

/**
 * 执行多媒体下载任务并缓存到本地
 */
public class AsyncMediaLoader {

	public static final int TYPE_MUSIC = 0;
	public static final int TYPE_VIDEO = 1;

	private ArrayList<MediaLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private static AsyncMediaLoader instance;

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static AsyncMediaLoader getInstance(final AsyncMediaLoaderCallback callback) {
		if (instance == null) {
			synchronized (AsyncMediaLoader.class) {
				if (instance == null) {
					instance = new AsyncMediaLoader(callback);
				}
			}
		}
		return instance;
	}

	@SuppressLint("HandlerLeak")
	private AsyncMediaLoader(final AsyncMediaLoaderCallback callback) {
		this.isLoop = true;
		this.tasks = new ArrayList<MediaLoadTask>();

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				MediaLoadTask task = (MediaLoadTask) msg.obj;
				callback.mediaLoaded(task.path, task.savePath);
			};
		};
		this.workThread = new Thread() {
			@Override
			public void run() {
				while (isLoop) {
					while (isLoop && !tasks.isEmpty()) {
						// 从任务队列获取任务
						MediaLoadTask task = tasks.remove(0);
						try {
							HttpEntity entity = HttpUtil.getEntity(task.path, null, HttpUtil.METHOD_GET);
							// 缓存到内存
							task.savePath = createCachePath(task.type, task.saveOr);
							FileManager.writeFileSaveHttpEntity(task.savePath, entity);
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
	 * 根据指定的路径下载多媒体对象
	 * 
	 * @param saveOr
	 *            是否长久保存
	 * @param path
	 *            多媒体路径
	 * @param type
	 *            多媒体类型（0:音乐/1:视频）
	 */
	public String loadMedia(boolean saveOr, String path, int type) {
		// 新建任务加入任务队列
		MediaLoadTask task = new MediaLoadTask(path, type, saveOr);
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
		return "";
	}

	public static String createCachePath(int type, boolean saveOr) {
		String savePath = "";
		String nameStr = "";
		if (type == TYPE_VIDEO) { //视频
			nameStr = "sp_play.mp4";
		} else { //音乐
			nameStr = "sp_play.mp3";
		}
		if (saveOr) {
			savePath = AppConfig.SAVE_MEDIA_PATH_LONG + nameStr;
		}else {
			savePath = AppConfig.SAVE_MEDIA_PATH_TEMPORARY + nameStr;
		}
		return BitmapUtil.checkFile(new File(savePath)).getPath();
	}

	private class MediaLoadTask {
		private String path, savePath;
		private int type;
		private boolean saveOr;

		public MediaLoadTask(String path, int type, boolean saveOr) {
			this.path = path;
			this.type = type;
			this.saveOr = saveOr;
		}

		@Override
		public boolean equals(Object o) {
			MediaLoadTask task = (MediaLoadTask) o;
			return path.equals(task.path);
		}
	}

	public interface AsyncMediaLoaderCallback {
		void mediaLoaded(String path, String saveFile);
	}

}
