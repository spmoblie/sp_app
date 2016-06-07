package com.spshop.stylistpark.utils;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

public class APIResult {

	private boolean mSuccess = false;
	private boolean mInvalidKey = false;
	private String mErrorMsg = null;
//	private String mReturnStr = null;
//	private String mReturnStr2 = null;
//	private String[] mReturnStrArr = null;
	private HashMap<String, String> mReuturnStrMap = null;
	private Object mObj = null;
	
	public APIResult() {
		// comment line for code analysis
	}

	public APIResult(Context context, JSONObject json, String... returnParamNames) {
		if(json == null){
			mErrorMsg = "Sever error";
			return;
		}
		String tmpStr = null;
		try{
			try{
				tmpStr = json.getString("result");
				if(!TextUtils.isEmpty(tmpStr)){
					if(tmpStr.equalsIgnoreCase("invalidKey")){
						mInvalidKey = true;
					}else if(!tmpStr.equalsIgnoreCase("fail")){
						mSuccess = true;
					}
				}
			}catch(JSONException e){
				ExceptionUtil.handle(context, e);
			}
			try{
				mErrorMsg = json.getString("errMsg");
			}catch(JSONException e){
				ExceptionUtil.handle(context, e);
				LogUtil.i("APIResult", "JSONException: no errMsg");
			}
			
			if(returnParamNames != null){
				mReuturnStrMap = new HashMap<String, String>();
//				mReturnStrArr = new String[returnParamNames.length];
				String tmpParamName;
				String tmpReturnStr;
				for (int i = 0; i < returnParamNames.length; i++){
					tmpReturnStr = null;
					tmpParamName = returnParamNames [i];
					if(tmpParamName != null){
						try{
							tmpReturnStr = json.getString(tmpParamName);
						}catch(JSONException e){
							ExceptionUtil.handle(context, e);
						}
						if(tmpReturnStr == null){
							try{
								tmpReturnStr = json.getInt(tmpParamName) + "";
							}catch(JSONException e){
								ExceptionUtil.handle(context, e);
							}
						}
						mReuturnStrMap.put(tmpParamName, tmpReturnStr);
					}
//					mReturnStrArr[i] = tmpReturnStr;
				}
			}
			display();
		}catch (Exception e) {
			ExceptionUtil.handle(context, e);
		}
		
	}
	
    public boolean isSuccess() {
		return mSuccess;
	}
    
    public void setSuccess(boolean isSuccess){
    	mSuccess = isSuccess;
    }

	public boolean isInvalidKey() {
		return mInvalidKey;
	}

	public String getErrorMsg() {
		return mErrorMsg;
	}

	public String getReturnStr(String paramName) {
		if(mReuturnStrMap != null && !TextUtils.isEmpty(paramName)){
			return mReuturnStrMap.get(paramName);
		}
		return null;
	}
	
	public void setReturnStr(String paramName,String value) {
		if(TextUtils.isEmpty(paramName)){
			return;
		}
		if(mReuturnStrMap == null){
			mReuturnStrMap = new HashMap<String, String>();
		}
		mReuturnStrMap.put(paramName, value);
	}
	
	public Object getObj() {
		return mObj;
	}

	public void setObj(Object obj) {
		mObj = obj;
	}

	public void display() {
		LogUtil.i("APIResult", " isSuccess = " + isSuccess() + " ErrorMsg = " + getErrorMsg()
				+ " Return = " + ((mReuturnStrMap == null)?"null":mReuturnStrMap.entrySet().toArray().toString())
				+ " Obj = " + ((mObj == null)?"null":mObj.toString()));
	}
}