package com.spshop.stylistpark.utils;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.tencent.stat.StatService;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
	
	public static void handle(Exception e)
	{
		if (AppConfig.IS_PUBLISH) {
			// App正式发布之后将异常详细信息上传至TA平台
			StatService.reportException(AppApplication.getInstance().getApplicationContext(), e);
		}
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		LogUtil.i("ExceptionUtil", stringWriter.toString());
	}

}
