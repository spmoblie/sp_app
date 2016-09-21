package com.spshop.stylistpark.service;

import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.service.impl.MainServiceImpl;

import java.util.List;

public class ServiceContext {

	private static ServiceContext sc;
	private MainService ms;

	private ServiceContext() {
		this.ms = new MainServiceImpl();
	}

	public static ServiceContext getServiceContext() {
		if (sc == null) {
			sc = new ServiceContext();
		}
		return sc;
	}

	/**
	 * 加载服务器数据
	 */
	public BaseEntity loadServerDatas(String tag, int requestCode, String uri, List<MyNameValuePair> params, int method) throws Exception {
		return ms.loadServerDatas(tag, requestCode, uri, params, method);
	}

}
