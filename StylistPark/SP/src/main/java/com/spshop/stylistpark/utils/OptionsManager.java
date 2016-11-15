package com.spshop.stylistpark.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.widgets.Displayer;

public class OptionsManager {

	private static OptionsManager instance;
	private DisplayImageOptions defaultOptions, avatarOptions, goodsOptions;


	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new OptionsManager();
		}
	}

	public static OptionsManager getInstance(){
		if (instance == null) {
			syncInit();
		}
		return instance;
	}


	/**
	 * 创建图片加载器对象
	 *
	 * @param circular 加载圆形效果的数值
	 * @param drawableId 默认图片Id
	 */
	public DisplayImageOptions getImageOptions(int circular, int drawableId, boolean isCache) {
		return new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(circular))
				.showImageForEmptyUri(drawableId)
				.showImageOnFail(drawableId)
				.cacheInMemory(isCache) // 内存缓存
				.cacheOnDisc(isCache) // sdcard缓存
				.resetViewBeforeLoading(true)//设置图片下载前复位
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 获取默认图片加载器对象
	 */
	public DisplayImageOptions getDefaultOptions() {
		if (defaultOptions == null) {
			defaultOptions = new DisplayImageOptions.Builder()
					//.displayer(new FadeInBitmapDisplayer(300)) // 图片加载好后渐入的动画时间
					.showImageForEmptyUri(R.drawable.bg_img_white)
					.showImageOnFail(R.drawable.bg_img_white)
					.cacheInMemory(true) // 内存缓存
					.cacheOnDisc(true) // sdcard缓存
					.resetViewBeforeLoading(true)//设置图片下载前复位
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return defaultOptions;
	}

	/**
	 * 获取头像图片加载器对象
	 */
	public DisplayImageOptions getAvatarOptions() {
		if (avatarOptions == null) {
			avatarOptions = new DisplayImageOptions.Builder()
					//.displayer(new RoundedBitmapDisplayer(360))
					.displayer(new Displayer(0)) //自定义圆形参数
					.showImageForEmptyUri(R.drawable.default_avatar)
					.showImageOnFail(R.drawable.default_avatar)
					.cacheInMemory(true) // 内存缓存
					.cacheOnDisc(true) // sdcard缓存
					.resetViewBeforeLoading(true)//设置图片下载前复位
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return avatarOptions;
	}

	/**
	 * 获取商品图片加载器对象
	 */
	public DisplayImageOptions getGoodsOptions() {
		if (goodsOptions == null) {
			goodsOptions = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.icon_goods_default)
					.showImageOnFail(R.drawable.icon_goods_default)
					.cacheInMemory(true) // 内存缓存
					.cacheOnDisc(true) // sdcard缓存
					.resetViewBeforeLoading(true)//设置图片下载前复位
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return goodsOptions;
	}

}
