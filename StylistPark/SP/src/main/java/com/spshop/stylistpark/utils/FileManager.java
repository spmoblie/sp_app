package com.spshop.stylistpark.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;

import org.apache.http.HttpEntity;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {
	
	public final static int SUCCESS = 999999;
	public final static int FAIL = -999999;

	/**
	 * 写入数据（String）
	 * 
	 * @param fileName 文件名
	 * @param writeStr 写入文本对象
	 * @param isSave 是否保存
	 */
	public static void writeFileSaveString(String fileName, String writeStr, boolean isSave) {
		if (StringUtil.isNull(fileName) || StringUtil.isNull(writeStr)) return;
		FileOutputStream fout = null;
		try {
			String path = "";
			if (isSave) {
				path = AppConfig.SAVE_PATH_TXT_SAVE + fileName;
			}else {
				path = AppConfig.SAVE_PATH_TXT_DICE + fileName;
			}
			checkFilePath(path);
			fout = new FileOutputStream(path);
			byte[] bytes = writeStr.getBytes();
			fout.write(bytes);
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
        	try {
        		if (fout != null) {
        			fout.close();
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
        }
	}

	/**
	 * 读取数据（String）
	 * 
	 * @param fileName 文件名
	 * @param isSave 是否保存
	 */
	public static String readFileSaveString(String fileName, boolean isSave) {
		FileInputStream fin = null;
		String path = "";
		String resu = "";
		try {
			if (isSave) {
				path = AppConfig.SAVE_PATH_TXT_SAVE + fileName;
			}else {
				path = AppConfig.SAVE_PATH_TXT_DICE + fileName;
			}
			File file = new File(path);
			if (file.exists()) {
				fin = new FileInputStream(file);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				resu = EncodingUtils.getString(buffer, "UTF-8");
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
        	try {
        		if (fin != null) {
        			fin.close();
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
        }
		return resu;
	}
	
	/**
	 * 写入数据（Object）
	 * 
	 * @param fileName 文件名
	 * @param obj 写入对象
	 * @param isSave 是否保存
	 */
    public static void writeFileSaveObject(String fileName, Object obj, boolean isSave) {
		if (StringUtil.isNull(fileName) || obj == null) return;
		ObjectOutputStream objOut = null;
    	FileOutputStream fos = null;
        try {
        	String path = "";
			if (isSave) {
				path = AppConfig.SAVE_PATH_TXT_SAVE + fileName;
			}else {
				path = AppConfig.SAVE_PATH_TXT_DICE + fileName;
			}
			checkFilePath(path);
			fos = new FileOutputStream(new File(path));
			objOut = new ObjectOutputStream(fos);
            objOut.writeObject(obj);
            objOut.flush();
        } catch (IOException e) {
			ExceptionUtil.handle(e);
        }finally{
        	try {
        		if (fos != null) {
        			fos.close();
				}
        		if (objOut != null) {
        			objOut.close();
				}
			} catch (IOException e) {
				ExceptionUtil.handle(e);
			}
        }
    }
    
    /**
	 * 读取数据（Object）
	 * 
	 * @param fileName 文件名
	 * @param isSave 是否保存
	 */
    public static Object readFileSaveObject(String fileName, boolean isSave) {
        Object temp = null;
        FileInputStream in = null;
        ObjectInputStream objIn = null;
        try {
        	String path = "";
        	if (isSave) {
				path = AppConfig.SAVE_PATH_TXT_SAVE + fileName;
			}else {
				path = AppConfig.SAVE_PATH_TXT_DICE + fileName;
			}
        	File file = new  File(path);
			if (file.exists()) {
				in = new FileInputStream(file);
				objIn = new ObjectInputStream(in);
				temp = objIn.readObject();
			}
        } catch (Exception e) {
			ExceptionUtil.handle(e);
        }finally{
        	try {
        		if (in != null) {
        			in.close();
				}
        		if (objIn != null) {
        			objIn.close();
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
        }
        return temp;
    }
	
	/**
	 * 写入数据（网络流对象）
	 * 
	 * @param path 保存路径
	 * @param entity 网络流对象
	 */
    public static String writeFileSaveHttpEntity(String path, HttpEntity entity) {
		if (StringUtil.isNull(path) || entity == null) return "";
    	InputStream is = null;
		FileOutputStream fos = null;
		String resu = "ok";
        try {
			checkFilePath(path);
			is = entity.getContent();
			if (is != null) {
				File file = new File(path);
				fos = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					fos.write(buf, 0, ch);
				}
				fos.flush();
			}
        } catch (IOException e) {
			ExceptionUtil.handle(e);
            resu = null;
        } finally {
        	try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				ExceptionUtil.handle(e);
			}
        }
        return resu;
    }

	/**
	 * 校验文件路径，不存在则创建
	 */
	public static void checkFilePath(String path) throws IOException {
		File file = new File(path);
		//判定文件所在的目录是否存在，不存在则创建
		File parentFile = file.getParentFile();
		if(parentFile != null && !parentFile.exists()){
			parentFile.mkdirs();
		}
		//判断文件是否存在,不存在则创建
		if(!file.exists()){
			file.createNewFile();
		}
	}

	/**
	 * 校验文件是否存在
	 */
	public static boolean checkFileExists(String path) {
		if (StringUtil.isNull(path)) return false;
		File file = new File(path);
		//判定文件所在的目录是否存在
		File parentFile = file.getParentFile();
		if(parentFile == null || !parentFile.exists()){
			return false;
		}
		//判断文件是否存在
		if(!file.exists()){
			return false;
		}
		return true;
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file File实例
	 * @return long 单位为b
	 * @throws Exception
	 */
	public static long getFolderSize(java.io.File file) throws Exception {
		long size = 0;
		if (file == null) {
			return size;
		}
		java.io.File[] fileList = file.listFiles();
		if (fileList == null) {
			return size;
		}
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}
		return size;
	}

	/**
	 * 获取文件夹文件数目
	 */
	public static int getFolderNum(java.io.File file) throws Exception {
		if (file == null) {
			return 0;
		}
		java.io.File[] fileList = file.listFiles();
		if (fileList == null) {
			return 0;
		}
		return fileList.length;
	}

	/**
	 * 删除指定目录下文件及目录
	 */
	public static void deleteFolderFile(File file) throws IOException {
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
				deleteFolderFile(f); //递归
			}
			file.delete();
		}
	}
	
	/**
	 * 从给定的Uri返回文件的绝对路径
	 *
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath(final Uri uri ) {
	    if ( null == uri ) return null;
	    final String scheme = uri.getScheme();
	    String data = null;
	    if ( scheme == null )
	        data = uri.getPath();
	    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
	        data = uri.getPath();
	    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
	        Cursor cursor = AppApplication.getInstance().getApplicationContext()
					.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
	        if ( null != cursor ) {
	            if ( cursor.moveToFirst() ) {
	                int index = cursor.getColumnIndex( ImageColumns.DATA );
	                if ( index > -1 ) {
	                    data = cursor.getString( index );
	                }
	            }
	            cursor.close();
	        }
	    }
	    return data;
	}

	/**
	 * 使用当前时间生成文件名
	 * @param fileType 文件类型(.jpg / .txt / ...)
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFileName(String fileType){
	    SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String dateTime = s.format(new Date());
        String imgFileName = dateTime + fileType;
        return imgFileName;
	}

	/**
	 * 读取指定文件中的内容
	 */
	public static String getStringFromFile(File file) throws Exception {
	    FileInputStream fis = new FileInputStream(file);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    reader.close();
	    fis.close();        
	    return sb.toString();
	}

	/**
	 * 读取Asset中的文件内容
	 */
	public static String loadJSONFromAsset(String filename) {
		String json = null;
		try {
			InputStream is = AppApplication.getInstance()
					.getApplicationContext().getAssets().open(filename);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			return null;
		}
		return json;
	}
    
	/**
	 * 读取Asset中的图片
	 */
	public static Bitmap getBitmapFromAssets(String filename){
		try {
			InputStream is = AppApplication.getInstance()
					.getApplicationContext().getAssets().open(filename + ".png");
			Bitmap bitmap = BitmapFactory.decodeStream(is);   
			is.close();
			return bitmap;
		} catch (IOException e) {
			ExceptionUtil.handle(e);
			return null;
		}
	}
	
	/**
	 * 下载指定Url的文件保存至指定路径
	 */
	public static int downloadFile(String urlStr, String fileName) {
		Context ctx = AppApplication.getInstance().getApplicationContext();
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		int result = FAIL;
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return connection.getResponseCode();
			}
			input = connection.getInputStream();
			output = new FileOutputStream(new File(ctx.getFilesDir(), "/" + fileName));
			byte data[] = new byte[4096];
			int count;
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}
			result = SUCCESS;
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
			if (connection != null)
				connection.disconnect();
		}
		return result;
	}
	
}
