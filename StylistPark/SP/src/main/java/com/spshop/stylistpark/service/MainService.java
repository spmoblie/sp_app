package com.spshop.stylistpark.service;

import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;

import java.util.List;

public interface MainService {

	BaseEntity loadServerDatas(String tag, int requestCode, String uri, List<MyNameValuePair> params, int method) throws Exception;

}

