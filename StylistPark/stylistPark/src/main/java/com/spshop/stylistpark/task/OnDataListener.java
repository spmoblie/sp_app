package com.spshop.stylistpark.task;

/**
 * [A brief description]
 *	
 * @version 1.0
 * @date 2016-1-15
 *
 **/
public interface OnDataListener {

	public Object doInBackground(int requestCode) throws Exception;
	
	public void onSuccess(int requestCode, Object result);
	
	public void onFailure(int requestCode, int state, Object result);
}
