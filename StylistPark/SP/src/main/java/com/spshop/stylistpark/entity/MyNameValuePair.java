package com.spshop.stylistpark.entity;

import org.apache.http.NameValuePair;

/**
 * NameValuePair实现类
 */
public class MyNameValuePair implements NameValuePair {

	private String name;
	private String value;

	public MyNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

}
