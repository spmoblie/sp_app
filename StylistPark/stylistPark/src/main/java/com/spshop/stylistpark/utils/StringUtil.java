package com.spshop.stylistpark.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isNull(String str) {
		return str == null || str.length() == 0 || "".equals(str) || "null".equals(str);
	}

	public static int getInteger(String str) {
		if (isNull(str) || !isNumeric(str)) {
			return 0;
		} else {
			return Integer.valueOf(str);
		}
	}

	public static double getDouble(String str) {
		if (isNull(str) || !isNumeric(str)) {
			return 0.00;
		} else {
			return Double.valueOf(str);
		}
	}

	public static long getLong(String str) {
		if (isNull(str) || !isNumeric(str)) {
			return 0;
		} else {
			return Long.valueOf(str);
		}
	}

	/**
	 * 判断手机格式是否正确
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	/**
	 * 判断email格式是否正确
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 判断是否全是数字
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNull(str) || !isNum.matches()) {
			return false;
		}
		return true;
	}

	public static  String getLogisticsCode(String name) {
		if (!StringUtil.isNull(name)) {
			if ("EMS".equals(name)) {
				return "ems";
			}
			else if ("中国邮政".equals(name)) {
				return "ems";
			}
			else if ("申通快递".equals(name)) {
				return "shentong";
			}
			else if ("圆通速递".equals(name)) {
				return "yuantong";
			}
			else if ("顺丰速运".equals(name)) {
				return "shunfeng";
			}
			else if ("天天快递".equals(name)) {
				return "tiantian";
			}
			else if ("韵达快递".equals(name)) {
				return "yunda";
			}
			else if ("中通速递".equals(name)) {
				return "zhongtong";
			}
			else if ("龙邦物流".equals(name)) {
				return "longbanwuliu";
			}
			else if ("宅急送".equals(name)) {
				return "zhaijisong";
			}
			else if ("全一快递".equals(name)) {
				return "quanyikuaidi";
			}
			else if ("汇通速递".equals(name)) {
				return "huitongkuaidi";
			}
			else if ("民航快递".equals(name)) {
				return "minghangkuaidi";
			}
			else if ("亚风速递".equals(name)) {
				return "yafengsudi";
			}
			else if ("快捷速递".equals(name)) {
				return "kuaijiesudi";
			}
			else if ("华宇物流".equals(name)) {
				return "tiandihuayu";
			}
			else if ("中铁快运".equals(name)) {
				return "zhongtiewuliu";
			}
			else if ("FedEx".equals(name)) {
				return "fedex";
			}
			else if ("UPS".equals(name)) {
				return "ups";
			}
			else if ("DHL".equals(name)) {
				return "dhl";
			}else {
				return "";
			}
		}
		return "";
	}
}
