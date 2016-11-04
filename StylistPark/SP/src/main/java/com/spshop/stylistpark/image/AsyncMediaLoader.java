package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.StringUtil;

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
			syncInit(callback);
		}
		return instance;
	}

	private static synchronized void syncInit(AsyncMediaLoaderCallback callback) {
		if (instance == null) {
			instance = new AsyncMediaLoader(callback);
		}
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
							File file = new File(AppConfig.SAVE_PATH_MEDIA_DICE);
							int fileNum = FileManager.getFolderNum(file);
							if (fileNum >= 10) { //最多缓存10个视频
								FileManager.deleteFolderFile(file);
							}
							task.savePath = createCachePath(task.type, task.path, task.isSave, true);
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
	 * @param isSave
	 *            是否保存
	 * @param path
	 *            多媒体路径
	 * @param type
	 *            多媒体类型（0:音乐/1:视频）
	 */
	public String loadMedia(boolean isSave, String path, int type) {
		// 新建任务加入任务队列
		MediaLoadTask task = new MediaLoadTask(path, type, isSave);
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

	public static String createCachePath(int type, String path, boolean isSave, boolean isCreate) {
		if (StringUtil.isNull(path)) return "";
		String savePath;
		String nameStr;
		if (type == TYPE_VIDEO) { //视频
			nameStr = path.substring(path.lastIndexOf("/"));
		} else { //音乐
			nameStr = "sp_play.mp3";
		}
		if (isSave) {
			savePath = AppConfig.SAVE_PATH_MEDIA_SAVE + nameStr;
		}else {
			savePath = AppConfig.SAVE_PATH_MEDIA_DICE + nameStr;
		}
		if (isCreate) {
			return BitmapUtil.checkFile(new File(savePath)).getAbsolutePath();
		} else {
			return savePath;
		}
	}

	private class MediaLoadTask {
		private String path, savePath;
		private int type;
		private boolean isSave;

		public MediaLoadTask(String path, int type, boolean isSave) {
			this.path = path;
			this.type = type;
			this.isSave = isSave;
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
