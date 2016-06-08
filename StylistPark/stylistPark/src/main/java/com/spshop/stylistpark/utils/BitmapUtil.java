package com.spshop.stylistpark.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class BitmapUtil {
	
	/**
	 * 调用此方法设置图片缩放的比例
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int width, int height) {
		Bitmap bm = null;
		if (bitmap != null) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, output);
			byte[] data = output.toByteArray();
			// 创建图片加载选项设置对象
			Options opts = new Options();
			// 设置仅加载图片的边界信息
			opts.inJustDecodeBounds = true;
			// 设置图片大小可变
			opts.inMutable = true;
			// 设置重用Bitmap内存从而改进性能，避免重新分配内存
			opts.inBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			// 设置图片缩放比例
			int xScale = Math.round((float) opts.outWidth / (float) width);
			int yScale = Math.round((float) opts.outHeight / (float) height);
			opts.inSampleSize = xScale > yScale ? xScale : yScale;
			// 取消仅加载边界的设置
			opts.inJustDecodeBounds = false;
    		// 设置系统回收时释放内存
			opts.inPurgeable = true; 
    		// 与inPurgeable配合使用
			opts.inInputShareable = true;
			// 按选项设置加载图片
			bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		}
		return bm;
	}

	/**
	 * 调用此方法设置图片缩放的比例
	 */
	public static Bitmap getBitmap(byte[] data, int width, int height) {
		Bitmap bm = null;
		if (data != null) {
			// 创建图片加载选项设置对象
			Options opts = new Options();
			// 设置仅加载图片的边界信息
			opts.inJustDecodeBounds = true;
    		// 设置图片大小可变
			opts.inMutable = true;
    		// 设置重用Bitmap内存从而改进性能，避免重新分配内存
			opts.inBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			// 设置图片缩放比例
			int xScale = Math.round((float) opts.outWidth / (float) width);
			int yScale = Math.round((float) opts.outHeight / (float) height);
			opts.inSampleSize = xScale > yScale ? xScale : yScale;
			// 取消仅加载边界的设置
			opts.inJustDecodeBounds = false;
    		// 设置系统回收时释放内存
			opts.inPurgeable = true; 
    		// 与inPurgeable配合使用
			opts.inInputShareable = true;
			// 按选项设置加载图片
			bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		}
		return bm;
	}
	
	/**
	 * 根据宽度等比例缩放图片
	 */
	public static Bitmap resizeImageByWidth(Bitmap defaultBitmap, int targetWidth) {
		int rawWidth = defaultBitmap.getWidth();
		int rawHeight = defaultBitmap.getHeight();
		float targetHeight = targetWidth * (float) rawHeight / (float) rawWidth;
		float scaleWidth = targetWidth / (float) rawWidth;
		float scaleHeight = targetHeight / (float) rawHeight;
		Matrix localMatrix = new Matrix();
		localMatrix.postScale(scaleHeight, scaleWidth);
		return Bitmap.createBitmap(defaultBitmap, 0, 0, rawWidth, rawHeight,localMatrix, true);
	}
	
	/**
	 * 按照比例系数缩放图片
	 */
	public static Bitmap sacleDownBitmap(Bitmap scr, float scaleFactor){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleFactor, scaleFactor);
		Bitmap newBm = Bitmap.createBitmap((int) (scr.getWidth() * scaleFactor),
				(int) (scr.getHeight() * scaleFactor), Config.ARGB_8888);
		Canvas canvas = new Canvas(newBm);
		canvas.drawBitmap(scr, matrix, paint);
		return newBm;
	}
	
	/**
	 * 获取指定路径的图片对象
	 * 
	 * @param path 指定图片的绝对路径
	 */
	public static Bitmap getBitmap(String path) throws Exception {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 获取指定路径及宽高的图片对象
	 * 
	 * @param path 指定图片的绝对路径
	 * @param reqWidth 图片宽
	 * @param reqHeight 图片高
	 */
	public static Bitmap getBitmap(String path, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeFile(path, options);
	}
	
    /**
     * 从字节流中获取图片对象
     */
    @SuppressWarnings("deprecation")
	public static Bitmap getBitmapFromByte(Context context, byte[] bitmapBytes) {
    	if (bitmapBytes != null) {
    		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    		int reqWidth, reqHeight;
    		Point point = new Point();
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
    			display.getSize(point);
    			reqWidth = point.x;
    			reqHeight = point.y;
    		} else {
    			reqWidth = display.getWidth();
    			reqHeight = display.getHeight();
    		}
    		final BitmapFactory.Options options = new BitmapFactory.Options();
    		// 设置仅加载图片的边界信息
    		options.inJustDecodeBounds = true;
    		// 设置图片大小可变
    		options.inMutable = true;
    		// 设置重用Bitmap内存从而改进性能，避免重新分配内存
    		options.inBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
    		// 设置图片缩放比例
    		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    		// 取消仅加载边界的设置
    		options.inJustDecodeBounds = false; 
    		// 设置系统回收时释放内存
    		options.inPurgeable = true; 
    		// 与inPurgeable配合使用
    		options.inInputShareable = true; 
    		return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
		}else {
			return null;
		}
    }
    
    /**
     * 计算Bitmap缩放比例1
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int initialInSampleSize = computeInitialSampleSize(options, reqWidth, reqHeight);
        int roundedInSampleSize;
        if (initialInSampleSize <= 8) {
            roundedInSampleSize = 1;
            while (roundedInSampleSize < initialInSampleSize) {
                // Shift one bit to left
                roundedInSampleSize <<= 1;
            }
        } else {
            roundedInSampleSize = (initialInSampleSize + 7) / 8 * 8;
        }
        return roundedInSampleSize;
    }

    /**
     * 计算Bitmap缩放比例2
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final double height = options.outHeight;
        final double width = options.outWidth;
        final long maxNumOfPixels = reqWidth * reqHeight;
        final int minSideLength = Math.min(reqHeight, reqWidth);
        int lowerBound = (maxNumOfPixels < 0) ? 1 : (int) Math.ceil(Math.sqrt(width * height / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 : (int) Math.min(Math.floor(width / minSideLength), Math.floor(height / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
	
	/**
	 * 过滤Path中的特殊字符
	 */
	public static String filterPath(String oldPath){
		String newPath = "";
		if (oldPath.contains(":")) {
			newPath = oldPath.toString().replace(":", "");
		}else {
			newPath = oldPath;
		}
		return newPath;
	}
	
	/**
	 * 创建保存文件到SD卡的路径
	 */
	public static File createPath(String path, boolean saveOr) {
		String[] a = path.split("/");
		String fileName = a[a.length-1];
		File file = null;
		if (saveOr) {
			file = new File(AppConfig.SAVE_IMAGE_PATH_LONG, fileName);
		}else {
			file = new File(AppConfig.SAVE_IMAGE_PATH_TEMPORARY, fileName);
		}
		file = checkFile(file);
		return file;
	}
	
	/**
	 * 保存图片
	 */
	public static void save(Bitmap bm, File file, int compress) throws IOException{
		if(bm != null && file != null){
			file = checkFile(file);
			FileOutputStream stream = new FileOutputStream(file);
			if (stream != null) {
				bm.compress(CompressFormat.JPEG, compress, stream);
				stream.flush();
				stream.close();
			}
		}
	}

	/**
	 * 检测文件路径是否存在，不存在则创建
	 */
	private static File checkFile(File file) {
		try {
			// 判定目录是否存在
			File parentFile = file.getParentFile();
			if(parentFile != null && !parentFile.exists()){
				parentFile.mkdirs();
			}
			// 判定文件是否存在
			if(!file.exists()){
				file.createNewFile();
			}
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将图片转换为数组格式
	 */
	public static byte[] bmpToByteArray(Context context, final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 60, output);
		if (needRecycle) {
			bmp.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			ExceptionUtil.handle(context, e);
		}
		return result;
	}
	
	/**
	 * 将Bitmap转换成Base64格式的字符串
	 * 
	 * @param bm
	 * @return
	 */
	public static String bitmapToString(Bitmap bm) {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
	    byte[] b = baos.toByteArray();
	    return Base64.encodeToString(b, Base64.DEFAULT);
	}
	
    /**
     * 根据图片路径获取图片旋转的角度
     * 
     * @param filepath
     * @return
     */
    public static int getExifOrientation(Context context, String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
        	ExceptionUtil.handle(context, e);
        }
        if (exif != null) {
    	int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
	
	/**
	 * 将图片设置为圆角矩形
	 * 
	 * @param bitmap 原图片
	 * @param corner 圆角参数值 
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int corner) {
		// 绘制圆角矩形
		Bitmap roundBitmap = Bitmap.createBitmap(
				bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(roundBitmap);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		int color = 0xff424242;
		float roundPx = corner; // 设置圆角
		// 绘制
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return roundBitmap;
	}
	
	/**
	 * 将两张位图拼接成一张(纵向拼接)
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Bitmap add2Bitmap(Bitmap first, Bitmap second) {
        int width = Math.max(first.getWidth(), second.getWidth());
        int height = first.getHeight() + second.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(first, 0, 0, null);
		canvas.drawBitmap(second, 0, first.getHeight(), null);
		return result;
	}
	
	/**
	 * 将多张图片拼接成长图并返回长图在本地的绝对路径
	 * 
	 * @param imageUris
	 * @return
	 */
	public static String addLongBitmap(Context context, ArrayList<Uri> imageUris) {
		String longImgPath = "";
		int size = imageUris.size();
		if (size == 1) {
			longImgPath = FileManager.getRealFilePath(context, imageUris.get(0));
		}else if (size > 1) {
			Bitmap bm1 = BitmapFactory.decodeFile(FileManager.getRealFilePath(context, imageUris.get(0)));
			Bitmap bm2 = null;
			Bitmap longBm = null;
			for (int i = 1; i < imageUris.size(); i++) {
				bm2 = BitmapFactory.decodeFile(FileManager.getRealFilePath(context, imageUris.get(i)));
				if (longBm != null) {
					longBm = add2Bitmap(longBm, bm2);
				}else {
					longBm = add2Bitmap(bm1, bm2);
				}
			}
			if (longBm != null) {
				longImgPath = AppConfig.SAVE_IMAGE_PATH_TEMPORARY + "longImg.jpg";
				try {
					save(longBm, new File(longImgPath), 100);
				} catch (IOException e) {
					ExceptionUtil.handle(context, e);
					longImgPath = "";
				}
			}
		}
		return longImgPath;
	}
    
    /**
     * 从ImageView中获取图片对象并返回本地Uri
     */
    public static URI captureView(View view, String filename, int width, int height, int compress){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        if(bitmap != null){
        	int color = bitmap.getPixel(1, 1);
        	int r = Color.red(color);
        	int g = Color.green(color);
        	int b = Color.blue(color);
        	LogUtil.i("BitmapUtil", "captureView r, g, b: " + r +" "+ g +" "+ b);
        	
            File file = BitmapUtil.createPath(filename, true);
            if (file != null) {
            	bitmap = BitmapUtil.getBitmap(bitmap, width, height);
            	AppApplication.saveBitmapFile(bitmap, file, compress);
            	return file.toURI();
			}
        }
        return null;
    }

	public static void scaleDownImageFile(File file, int width, int height, CompressFormat format, int compress) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(new FileInputStream(file), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;
		float desiredWScale = (float) width / srcWidth;
		float desiredHScale = (float) height / srcHeight;

		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = 2;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap sampledSrcBitmap = null;
		try {
			sampledSrcBitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Matrix matrix = new Matrix();
		matrix.postScale(desiredWScale, desiredHScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		float ratioX = width / (float) sampledSrcBitmap.getWidth();
		float ratioY = height / (float) sampledSrcBitmap.getHeight();
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY);
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(sampledSrcBitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, false);
			scaledBitmap.compress(format, compress, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

