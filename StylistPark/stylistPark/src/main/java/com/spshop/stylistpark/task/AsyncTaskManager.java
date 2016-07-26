package com.spshop.stylistpark.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.TimeUtil;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [A brief description]
 * 
 * @version 1.0
 * @date 2016-1-15
 * 
 **/
public class AsyncTaskManager {

	private final String TAG = AsyncTaskManager.class.getSimpleName();

	private Context mContext;
	public final int REQUEST_SUCCESS_CODE = 200;
	public final int REQUEST_ERROR_CODE = -200;
	public final int NETWORK_ERROR_CODE = -999;


	private static AsyncTaskManager instance;
	private static ExecutorService mExecutorService;
	private static Map<Integer, WeakReference<BaseAsyncTask>> requestMap;

	private AsyncTaskManager(Context context) {
		mContext = context;
		mExecutorService = Executors.newFixedThreadPool(10);
		requestMap = new WeakHashMap<Integer, WeakReference<BaseAsyncTask>>();
	}

	/**
	 * [AsyncTaskManager constructor]
	 * 
	 * @param context
	 * @return
	 */
	public static AsyncTaskManager getInstance(Context context) {
		if (instance == null) {
			synchronized (AsyncTaskManager.class) {
				if (instance == null) {
					instance = new AsyncTaskManager(context);
				}
			}
		}
		return instance;
	}

	/**
	 * send request method
	 * 
	 * @param requestCode
	 * @param listener
	 */
	public void request(int requestCode, OnDataListener listener) {
		DownLoad bean = new DownLoad(requestCode, listener);
		if (requestCode > 0) {
			BaseAsyncTask mAsynctask = new BaseAsyncTask(bean);
			// after version 2.3 added executeOnExecutor method.
			// before 2.3 only run five asyntask, more than five must wait
			if (Build.VERSION.SDK_INT >= 11) {
				mAsynctask.executeOnExecutor(mExecutorService);
			} else {
				mAsynctask.execute();
			}
			requestMap.put(requestCode, new WeakReference<BaseAsyncTask>(
					mAsynctask));
		} else {
			LogUtil.i(TAG, "the error is requestCode < 0");
		}

	}

	/**
	 * cancelRequest
	 * 
	 * @param requestCode
	 */
	public void cancelRequest(int requestCode) {
		WeakReference<BaseAsyncTask> requestTask = requestMap.get(requestCode);
		if (requestTask != null) {
			BaseAsyncTask request = requestTask.get();
			if (request != null) {
				request.cancel(true);
				request = null;
			}
		}
		requestMap.remove(requestCode);
	}

	/**
	 * cancel all Request
	 */
	public void cancelRequest() {
		if (requestMap != null) {
			Iterator<Entry<Integer, WeakReference<BaseAsyncTask>>> it = requestMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, WeakReference<BaseAsyncTask>> entry = (Entry<Integer, WeakReference<BaseAsyncTask>>) it.next();
				Integer requestCode = entry.getKey();
				cancelRequest(requestCode);
			}
			requestMap.clear();
		}
	}

	/**
	 * [the AsyncTask base class. it can handle all take many time task]
	 * 
	 * @version: 1.0
	 * @date: 2016-1-15
	 */
	class BaseAsyncTask extends AsyncTask<Object, Void, Object> {

		DownLoad bean = null;
		TimeUtil timeLog = null;

		public BaseAsyncTask(DownLoad bean) {
			this.bean = bean;
			this.timeLog = new TimeUtil();
		}

		@Override
		protected Object doInBackground(Object... params) {
			int num = 1;
			while (num < 4) {
				LogUtil.i(TAG, "loadNum = " + num);
				num++;
				try {
					boolean networkOK = NetworkUtil.networkStateTips();
					if (networkOK || AppApplication.loadDBData) {
						Object result = null;
						if (bean.getListener() != null) {
							result = bean.getListener().doInBackground(bean.getRequestCode());
							bean.setState(REQUEST_SUCCESS_CODE);
							bean.setResult(result);
							if (result != null) {
								break;
							}
						}else {
							bean.setState(REQUEST_ERROR_CODE);
							bean.setResult("listener is null");
							LogUtil.i(TAG, "listener is null");
							break;
						}
					} else {
						bean.setState(NETWORK_ERROR_CODE);
						bean.setResult(mContext.getString((R.string.network_fault)));
						break;
					}
				} catch (Exception e) {
					bean.setState(REQUEST_ERROR_CODE);
					bean.setResult(mContext.getString((R.string.toast_server_busy)));
					ExceptionUtil.handle(e);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					ExceptionUtil.handle(e1);
				}
			}
			return bean;
		}

		@Override
		protected void onPostExecute(Object result) {
			DownLoad bean = (DownLoad) result;
			if (bean == null || bean.getListener() == null) return;
			switch (bean.getState()) {
			case REQUEST_SUCCESS_CODE: // 加载成功
				bean.getListener().onSuccess(bean.getRequestCode(), bean.getResult());
				break;
			case REQUEST_ERROR_CODE: // 加载失败
				bean.getListener().onSuccess(bean.getRequestCode(), null);
				break;
			case NETWORK_ERROR_CODE: // 网络故障
				bean.getListener().onFailure(bean.getRequestCode(), bean.getState(), bean.getResult());
				break;
			}
			timeLog.log(TAG, bean.getListener() + "加载数据总共耗时:");
		}
	}
}
