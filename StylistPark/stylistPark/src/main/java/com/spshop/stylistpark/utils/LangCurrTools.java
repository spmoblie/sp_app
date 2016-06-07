package com.spshop.stylistpark.utils;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.spshop.stylistpark.R;


/**
 * 国际化工具类
 */
public class LangCurrTools {
	
	public static enum Language {
		En, Zh, Cn, Unknown
	}

	private static String langConfig = "Language";
	private static String langKey = "Lang";
	private static Language curLanguage = null;
	private static Configuration config = null;

	public static Language getLanguage(Context ctx) {
		if (curLanguage == null) {
			loadLanguage(ctx);
		}
		return curLanguage;
	}

	private static void loadLanguage(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(langConfig, 0);
		String res = sp.getString(langKey, Locale.getDefault().toString());
		if (res.equals("En") || res.contains(Locale.ENGLISH.toString())) {
			curLanguage = Language.En;
		} else if (res.equals("Cn") || res.contains(Locale.SIMPLIFIED_CHINESE.toString())) {
			curLanguage = Language.Cn;
		} else if (res.equals("zh_HK") || res.equals("Zh") || res.contains(Locale.TRADITIONAL_CHINESE.toString())) {
			curLanguage = Language.Zh;
		} else {
			curLanguage = Language.Cn;
		}
	}

	public static void setLanguage(Activity ctx, Language data) {
		config = ctx.getBaseContext().getResources().getConfiguration();
		saveLanguage(ctx.getApplicationContext(), data);
		if (data == Language.En) {
			Locale.setDefault(Locale.ENGLISH);
			config.locale = Locale.ENGLISH;
		} else if (data == Language.Zh) {
			Locale.setDefault(Locale.TRADITIONAL_CHINESE);
			config.locale = Locale.TRADITIONAL_CHINESE;
		} else if (data == Language.Cn) {
			Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
			config.locale = Locale.SIMPLIFIED_CHINESE;
		} else {
			Locale.setDefault(Locale.ENGLISH);
			config.locale = Locale.ENGLISH;
		}
		curLanguage = data;
		ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
		config = null;
	}

	private static void saveLanguage(Context ctx, Language data) {
		SharedPreferences sp = ctx.getSharedPreferences(langConfig, 0);
		SharedPreferences.Editor editor = sp.edit();
		if (data == Language.En) {
			editor.putString(langKey, "En");
		} else if (data == Language.Zh) {
			editor.putString(langKey, "Zh");
		} else if (data == Language.Cn) {
			editor.putString(langKey, "Cn");
		} else {
			editor.putString(langKey, "Cn");
		}
		editor.commit();
	}
	
	public static String getLanguageHttpUrlValueStr(Context ctx){
		String langStr = "";
		switch (getLanguage(ctx)) {
		case En:
			langStr = "en_us";
			break;
		case Zh:
			langStr = "zh_tw";
			break;
		case Cn:
			langStr = "zh_cn";
			break;
		default:
			langStr = "zh_cn";
			break;
		}
		return langStr;
	}
	
	public static enum Currency {
		HKD, RMB, USD
	}
	private static String currencyConfig = "Currency";
	private static String currencyKey = "curr";
	private static Currency curCurrency = null;
	
	public static Currency getCurrency(Context ctx) {
		if (curCurrency == null) {
			loadCurrency(ctx);
		}
		return curCurrency;
	}
	
	private static void loadCurrency(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(currencyConfig, 0);
		String res = sp.getString(currencyKey, "");
		
		if (res.equals("HKD")) {
			curCurrency = Currency.HKD;
		} else if (res.equals("RMB")) {
			curCurrency = Currency.RMB;
		} else if (res.equals("USD")) {
			curCurrency = Currency.USD;
		} else {
			curCurrency = Currency.RMB;
		}
		
		if(TextUtils.isEmpty(res)){
			saveCurrency(ctx, curCurrency);
		}
	}

	public static void setCurrency(Context ctx, Currency data) {
		saveCurrency(ctx.getApplicationContext(), data);
		curCurrency = data;
	}
	
	private static void saveCurrency(Context ctx, Currency data) {
		SharedPreferences sp = ctx.getSharedPreferences(currencyConfig, 0);
		SharedPreferences.Editor editor = sp.edit();
		if (data == Currency.HKD) {
			editor.putString(currencyKey, "HKD");
		} else if (data == Currency.RMB) {
			editor.putString(currencyKey, "RMB");
		} else if (data == Currency.USD) {
			editor.putString(currencyKey, "USD");
		} else {
			editor.putString(currencyKey, "RMB");
		}
		editor.commit();
	}
	
	public static String getCurrencyValue(Context ctx){
		String currencyValue = "";
		Currency currency = getCurrency(ctx);
		switch (currency) {
		case HKD:
			currencyValue = ctx.getString(R.string.currency_hkd_sign);
			break;
		case RMB:
			currencyValue = ctx.getString(R.string.currency_rmb_sign);
			break;
		case USD:
			currencyValue = ctx.getString(R.string.currency_usd_sign);
			break;
		default:
			currencyValue = ctx.getString(R.string.currency_rmb_sign);
			break;
		}
		return currencyValue;
	}
	
	public static String getCurrencyHttpUrlValueStr(Context ctx){
		String curStr = "";
		switch (getCurrency(ctx)) {
		case USD:
			curStr = "USD";
			break;
		case HKD:
			curStr = "HKD";
			break;
		case RMB:
			curStr = "RMB";
			break;
		default:
			curStr = "RMB";
			break;
		}
		return curStr;
	}
	
}
