package com.spshop.stylistpark.service;

import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;

import java.util.List;

public interface MainService {

	String getServerJSONString(String uri) throws Exception;

	BaseEntity loadServerDatas(String tag, int requestCode, String uri, List<MyNameValuePair> params, int method) throws Exception;

}

