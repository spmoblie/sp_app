package com.spshop.stylistpark.image;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.spshop.stylistpark.utils.ExceptionUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

/**
 * LruCache图片缓存工具类
 */
@SuppressLint("NewApi")
public class AsyncImageLruCache {

	private AsyncImageLruCacheCallback mCallback;
	// 图片缓存类
	private LruCache<String, Bitmap> mLruCache;
	// 记录所有正在下载或等待下载的任务
	private HashSet<DownloadBitmapAsyncTask> dbatHashSet;

	public AsyncImageLruCache(AsyncImageLruCacheCallback callback) {
		this.mCallback = callback;
		dbatHashSet = new HashSet<DownloadBitmapAsyncTask>();

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		// 设置图片缓存大小为maxMemory的1/6
		int cacheSize = maxMemory / 6;

		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}

	/**
	 * 启动新的下载任务
	 */
	public void executeAsyncTaskLoad(String imageUrl) {
		DownloadBitmapAsyncTask dbat = new DownloadBitmapAsyncTask();
		dbatHashSet.add(dbat);
		dbat.execute(imageUrl);
	}

	/**
	 * 下载图片的异步任务
	 */
	public class DownloadBitmapAsyncTask extends
			AsyncTask<String, Void, Bitmap> {

		private String imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			Bitmap bitmap = downloadBitmap(params[0]);
			if (bitmap != null) {
				// 下载完后,将其缓存到LrcCache
				addBitmapToLruCache(params[0], bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			mCallback.imageLoaded(imageUrl, bitmap);
			dbatHashSet.remove(this);
		}
	}

	/**
	 * 获取Bitmap
	 */
	private Bitmap downloadBitmap(String imageUrl) {
		Bitmap bitmap = null;
		HttpURLConnection httpURLConnection = null;
		try {
			URL url = new URL(imageUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5 * 1000);
			httpURLConnection.setReadTimeout(10 * 1000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return bitmap;
	}

	/**
	 * 将图片存储到LruCache
	 */
	public void addBitmapToLruCache(String key, Bitmap bitmap) {
		if (getBitmapFromLruCache(key) == null) {
			mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache缓存获取图片
	 */
	public Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 * 取消所有正在下载或等待下载的任务
	 */
	public void cancelAllTasks() {
		if (dbatHashSet != null) {
			for (DownloadBitmapAsyncTask task : dbatHashSet) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 回调接口
	 */
	public interface AsyncImageLruCacheCallback {
		void imageLoaded(String imageUrl, Bitmap bm);
	}

}
