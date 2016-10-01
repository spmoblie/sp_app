package com.spshop.stylistpark.service;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.entity.AuthResult;
import com.spshop.stylistpark.entity.QQEntity;
import com.spshop.stylistpark.entity.QQUserInfoEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.entity.WXEntity;
import com.spshop.stylistpark.entity.WXUserInfoEntity;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginJsonParser {


	/**
	 * 获取用户登录校验结果
	 */
	public static UserInfoEntity postAccountLoginData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = Integer.valueOf(jsonObject.getString("error"));
		String errInfo = jsonObject.getString("message");
		UserInfoEntity infoEn = new UserInfoEntity(errCode, errInfo);
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) { //校验通过
			infoEn.setUserId(jsonObject.getString("user_id"));
		}
		return infoEn;
	}

	/**
	 * 获取微信AccessToken
	 */
	public static WXEntity getWexiAccessToken(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(jsonObject.getString("access_token"), 
				jsonObject.getString("expires_in"), jsonObject.getString("refresh_token"), 
				jsonObject.getString("openid"), jsonObject.getString("scope"), jsonObject.getString("unionid"));
	}
	
	/**
	 * 校验微信AccessToken
	 */
	public static WXEntity authWexiAccessToken(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(Integer.parseInt(jsonObject.getString("errcode")), jsonObject.getString("errmsg"));
	}

	/**
	 * 微信刷新AccessToken结果
	 */
	public static WXEntity getWexiAccessAuth(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(jsonObject.getString("access_token"), 
				jsonObject.getString("expires_in"), jsonObject.getString("refresh_token"),
				jsonObject.getString("openid"), jsonObject.getString("scope"));
	}

	/**
	 * 获取微信用户信息
	 */
	public static WXUserInfoEntity getWexiUserInfo(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXUserInfoEntity(
				jsonObject.getString("openid"), jsonObject.getString("nickname"), 
				jsonObject.getString("sex"), jsonObject.getString("province"), 
				jsonObject.getString("city"), jsonObject.getString("country"),
				jsonObject.getString("headimgurl"), jsonObject.getString("privilege"),
				jsonObject.getString("unionid"), jsonObject.getString("language"));
	}

	/**
	 * 微信校验AccessToken有效性
	 */
	public static WXEntity getWexiAccessTokenAuto(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(Integer.parseInt(jsonObject.getString("errcode")), jsonObject.getString("errmsg"));
	}

	/**
	 * 获取QQ登录结果
	 */
	public static QQEntity getQQLoginResult(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		QQEntity qqEn;
		int ret = jsonObj.getInt("ret");
		String msg = jsonObj.getString("msg");
		if (ret == 0) {
			qqEn = new QQEntity(ret, msg, jsonObj.getString("access_token"), jsonObj.getString("expires_in"), 
					jsonObj.getString("openid"), jsonObj.getString("pay_token"), 
					jsonObj.getString("pf"), jsonObj.getString("pfkey"));
		}else {
			qqEn = new QQEntity(ret, msg);
		}
		return qqEn;
	}

	/**
	 * 获取QQ用户资料
	 */
	public static QQUserInfoEntity getQQUserInfo(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		QQUserInfoEntity userInfo;
		int ret = jsonObj.getInt("ret");
		String msg = jsonObj.getString("msg");
		if (ret == 0) {
			userInfo = new QQUserInfoEntity(ret, msg, jsonObj.getString("nickname"), 
					jsonObj.getString("gender"), jsonObj.getString("figureurl_qq_2"));
		}else {
			userInfo = new QQUserInfoEntity(ret, msg);
		}
		return userInfo;
	}

	/**
	 * 获取支付宝授权信息
	 */
	public static AuthResult getAlipayAuthInfo(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		AuthResult authResult = null;
		return authResult;
	}

	/**
	 * 获取支付宝用户信息
	 */
	public static UserInfoEntity getAlipayUserInfo(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		UserInfoEntity userInfo = null;
		return userInfo;
	}

}
