package com.spshop.stylistpark.task;

/**
 * [A brief description]
 * 
 * @version 1.0
 * @date 2016-1-15
 * 
 **/
public class DownLoad {

	private int requestCode;
	private OnDataListener listener;
	private int state;
	private Object result;

	public DownLoad() {
		super();
	}

	public DownLoad(int requestCode, OnDataListener listener) {
		this.requestCode = requestCode;
		this.listener = listener;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public OnDataListener getListener() {
		return listener;
	}

	public void setListener(OnDataListener listener) {
		this.listener = listener;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
