package com.spshop.stylistpark.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.spshop.stylistpark.AppConfig;

import java.io.File;

/** 
 * 清除应用缓存数据控制器 
 */  
@SuppressLint("SdCardPath")
public class CleanDataManager {  
	
    /** 
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) 
     *  
     * @param context 
     */  
    public static void cleanInternalCache(Context context) {  
        deleteFilesByDirectory(context.getCacheDir());  
    }  
  
    /** 
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) 
     *  
     * @param context 
     */  
    public static void cleanDatabases(Context context) {  
        deleteFilesByDirectory(new File("/data/data/"+ context.getPackageName() + "/databases"));  
    }  
  
    /** 
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) 
     *  
     * @param context 
     */  
    public static void cleanSharedPreference(Context context) {  
        deleteFilesByDirectory(new File("/data/data/"+ context.getPackageName() + "/shared_prefs"));  
    }  
  
    /** 
     * 按名字清除本应用数据库 
     *  
     * @param context 
     * @param dbName 
     */  
    public static void cleanDatabaseByName(Context context, String dbName) {  
        context.deleteDatabase(dbName);  
    }  
  
    /** 
     * 清除/data/data/com.xxx.xxx/files下的内容 
     *  
     * @param context 
     */  
    public static void cleanFiles(Context context) {  
        deleteFilesByDirectory(context.getFilesDir());  
    }  
  
    /** 
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) 
     *  
     * @param context 
     */  
    public static void cleanExternalCache(Context context) {  
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
            deleteFilesByDirectory(context.getExternalCacheDir());  
        }  
    } 

    /** 
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 
     *  
     * @param filePath 
     */  
    public static void cleanCustomCache(String filePath) {  
        deleteFilesByDirectory(new File(filePath));  
    } 

    /** 
     * 递归删除文件和文件夹
     */  
    private static void deleteFilesByDirectory(File file) {  
    	if (file == null) {
			return;
		}
    	if(file.isFile()){ //文件
            file.delete();
            return;
        }
        if(file.isDirectory()){ //文件夹
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
            	deleteFilesByDirectory(f); //递归
            }
            file.delete();
        }
    }  
    
    /**
     * 清除本应用临时缓存的数据
     * 
     * @param context
     */
    public static void cleanAppTemporaryData(Context context){
    	cleanCustomCache(AppConfig.SAVE_TXT_PATH_TEMPORARY); 
        cleanCustomCache(AppConfig.SAVE_IMAGE_PATH_TEMPORARY);
    }
  
    /** 
     * 清除本应用所有的数据 
     *  
     * @param context 
     * @param filepath 
     */  
    public static void cleanApplicationData(Context context, String... filepath) {  
        cleanInternalCache(context);  
        cleanExternalCache(context);  
        //cleanDatabases(context);  
        //cleanDatabaseByName(context, "stylistpark.db");
        cleanSharedPreference(context);  
        cleanFiles(context);  
        /*for (String filePath : filepath) {  
            cleanCustomCache(filePath);  
        } */ 
        cleanAppTemporaryData(context);
    }  
  
}  

