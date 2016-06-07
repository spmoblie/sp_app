package com.spshop.stylistpark.utils;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码图片生成工具
 */
public class QRCodeUtil{
	
	private static int QR_WIDTH = 400, QR_HEIGHT = 400;
	
	/**
	 * @param url 要转换的地址或字符串,可以是中文
	 * @return
	 */
	public static Bitmap createQRImage(String url)
	{
		Bitmap bitmap = null;
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return bitmap;
			}
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 2); 
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			
			return bitmap;
		}
		catch (WriterException e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * 生成二维码Bitmap
	 *
	 * @param content   内容
	 * @param widthPix  图片宽度
	 * @param heightPix 图片高度
	 * @param logoBm    二维码中心的Logo图标（可以为null）
	 * @param filePath  用于存储二维码图片的文件路径
	 * @return 生成二维码及保存文件是否成功
	 */ 
	public static boolean createQRImage(Context context, String content, 
			int widthPix, int heightPix, Bitmap logoBm, String filePath) { 
		try { 
			if (content == null || "".equals(content)) { 
				return false; 
			} 
			
			//配置参数 
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>(); 
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
			//容错级别 
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); 
			//设置空白边距的宽度 
	        hints.put(EncodeHintType.MARGIN, 2); //default is 4 
			// 图像数据转换，使用了矩阵转换 
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints); 
			int[] pixels = new int[widthPix * heightPix]; 
			// 下面这里按照二维码的算法，逐个生成二维码的图片， 
			// 两个for循环是图片横列扫描的结果 
			for (int y = 0; y < heightPix; y++) { 
				for (int x = 0; x < widthPix; x++) { 
					if (bitMatrix.get(x, y)) { 
						pixels[y * widthPix + x] = 0xff000000; 
					} else { 
						pixels[y * widthPix + x] = 0xffffffff; 
					} 
				} 
			} 
			
			// 生成二维码图片的格式，使用ARGB_8888 
			Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888); 
			bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix); 
			// 添加Logo边框
			Bitmap logoFrame = BitmapFactory.decodeResource(
					context.getResources(), com.spshop.stylistpark.R.drawable.bg_img_white);
			if (logoFrame != null) {
				bitmap = addLogoFrame(context, bitmap, logoFrame); 
			}
			// 添加Logo
			if (logoBm != null) { 
				bitmap = addLogo(context, bitmap, logoBm); 
			} 
			FileManager.checkFilePath(context, filePath);
			//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！ 
			return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath)); 
		} catch (Exception e) { 
			ExceptionUtil.handle(context, e);
		} 
		
		return false; 
	} 
	
	/**
	 * 在二维码中间添加Logo图案
	 */ 
	private static Bitmap addLogo(Context context, Bitmap src, Bitmap logo) { 
		if (src == null) { 
			return null; 
		} 
		
		if (logo == null) { 
			return src; 
		} 
		//获取图片的宽高 
		int srcWidth = src.getWidth(); 
		int srcHeight = src.getHeight(); 
		int logoWidth = logo.getWidth(); 
		int logoHeight = logo.getHeight(); 
		
		if (srcWidth == 0 || srcHeight == 0) { 
			return null; 
		} 
		
		if (logoWidth == 0 || logoHeight == 0) { 
			return src; 
		} 
		
		logo = BitmapUtil.getRoundedCornerBitmap(logo, 10);
		
		//logo大小为二维码整体大小的1/5 
		float scaleFactor = srcWidth * 1.0f / 4 / logoWidth; 
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888); 
		try { 
			Canvas canvas1 = new Canvas(bitmap); 
			canvas1.drawBitmap(src, 0, 0, null); 
		    canvas1.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2); 
		    canvas1.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null); 
			
		    canvas1.save(Canvas.ALL_SAVE_FLAG); 
		    canvas1.restore(); 
		} catch (Exception e) { 
			bitmap = null; 
			ExceptionUtil.handle(context, e);
		} 
		
		return bitmap; 
	} 
	
	/**
	 * 为二维码的Logo添加边框
	 */ 
	private static Bitmap addLogoFrame(Context context, Bitmap src, Bitmap frame) { 
		if (src == null) { 
			return null; 
		} 
		
		if (frame == null) { 
			return src; 
		} 
		//获取图片的宽高 
		int srcWidth = src.getWidth(); 
		int srcHeight = src.getHeight(); 
		int frameWidth = frame.getWidth(); 
		int frameHeight = frame.getHeight(); 
		
		if (srcWidth == 0 || srcHeight == 0) { 
			return null; 
		} 
		
		if (frameWidth == 0 || frameHeight == 0) { 
			return src; 
		} 
		
		frame = BitmapUtil.getRoundedCornerBitmap(frame, 10);
		
		float scaleFactor = srcWidth * 1.1f / 4 / frameWidth; 
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888); 
		try { 
			Canvas canvas1 = new Canvas(bitmap); 
			canvas1.drawBitmap(src, 0, 0, null); 
			canvas1.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2); 
			canvas1.drawBitmap(frame, (srcWidth - frameWidth) / 2, (srcHeight - frameHeight) / 2, null); 
			
			canvas1.save(Canvas.ALL_SAVE_FLAG); 
			canvas1.restore(); 
		} catch (Exception e) { 
			bitmap = null; 
			ExceptionUtil.handle(context, e);
		} 
		
		return bitmap; 
	} 
	
}





  
