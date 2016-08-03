package com.spshop.stylistpark.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 全局配置文件(偏好设置)
 */
public class SharedConfig {
	
	private SharedPreferences shared;
	
	public SharedConfig(Context context){
		shared = context.getSharedPreferences("spConfig", Context.MODE_PRIVATE);
	}

	public SharedPreferences GetConfig(){
		return shared;
	}
	
	public void ClearConfig(){
		shared.edit().clear().apply();
	}
}
