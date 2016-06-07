package com.spshop.stylistpark.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;

import com.spshop.stylistpark.AppConfig;
import com.tencent.stat.StatService;

public class ExceptionUtil {
	
	public static void handle(Context ctx, Exception e)
	{
		if (AppConfig.IS_PUBLISH) {
			StatService.reportException(ctx, e); // App正式发布之后将异常详细信息上传至TA平台
		}
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		LogUtil.i("ExceptionUtil", stringWriter.toString());
	}

}
