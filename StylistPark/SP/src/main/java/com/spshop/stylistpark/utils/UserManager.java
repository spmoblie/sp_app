package com.spshop.stylistpark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.entity.WXEntity;
import com.spshop.stylistpark.share.weibo.AccessTokenKeeper;

public class UserManager {
	
	private static UserManager instance;
	private SharedPreferences sp;
	private Editor editor;
	
	private String mUserId = null;
	private String shareId = null;
	private String loginAccount = null;
	private String mUserName = null;
	private String mUserNameID = null;
	private String mUserNick = null;
	private String mUserHeadImg = null;
	private String mUserIntro = null;
	private String mUserBirthday = null;
	private String mUserEmail = null;
	private String mUserPhone = null;
	private String mUserMoney = null;
	private String mUserRankName = null;

	private String wxAccessToken = null;
	private String wxOpenid = null;
	private String wxUnionid = null;
	private String wxRefreshToken = null;

	public static UserManager getInstance(){
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}

	private UserManager(){
		sp = AppApplication.getSharedPreferences();
		editor = sp.edit();
		editor.apply();
	}
	
	public String getUserId(){
		if(StringUtil.isNull(mUserId)){
			mUserId = sp.getString(AppConfig.KEY_USER_ID, null);
		}
		LogUtil.i("isLogined", "getUserId = " + mUserId);
		return mUserId;
	}
	
	private void saveUserId(String userId){
		editor.putString(AppConfig.KEY_USER_ID, userId).apply();
		mUserId = userId;
		LogUtil.i("isLogined", "saveUserId = " + userId);
	}

	public String getShareId(){
		if(StringUtil.isNull(shareId)){
			shareId = sp.getString(AppConfig.KEY_SHARE_ID, null);
		}
		LogUtil.i("isLogined", "getShareId = " + shareId);
		return shareId;
	}

	private void saveShareId(String id){
		editor.putString(AppConfig.KEY_SHARE_ID, id).apply();
		shareId = id;
		LogUtil.i("isLogined", "saveShareId = " + shareId);
	}

	public String getLoginAccount(){
		if(StringUtil.isNull(loginAccount)){
			loginAccount = sp.getString(AppConfig.KEY_LOGIN_ACCOUNT, "");
		}
		return loginAccount;
	}
	
	public void saveLoginAccount(String account){
		editor.putString(AppConfig.KEY_LOGIN_ACCOUNT, account).apply();
		loginAccount = account;
	}

	public String getUserName(){
		if(StringUtil.isNull(mUserName)){
			mUserName = sp.getString(AppConfig.KEY_USER_NAME, "");
		}
		return mUserName;
	}

	public void saveUserName(String userName){
		editor.putString(AppConfig.KEY_USER_NAME, userName).apply();
		mUserName = userName;
	}

	public String getUserNameID(){
		if(StringUtil.isNull(mUserNameID)){
			mUserNameID = sp.getString(AppConfig.KEY_USER_NAME_ID, "");
		}
		return mUserNameID;
	}

	public void saveUserNameID(String userNameID){
		editor.putString(AppConfig.KEY_USER_NAME_ID, userNameID).apply();
		mUserNameID = userNameID;
	}

	public String getUserNick(){
		if(StringUtil.isNull(mUserNick)){
			mUserNick = sp.getString(AppConfig.KEY_USER_NICK, "");
		}
		return mUserNick;
	}
	
	public void saveUserNick(String userNick){
		editor.putString(AppConfig.KEY_USER_NICK, userNick).apply();
		mUserNick = userNick;
	}
	
	public String getUserHeadImg(){
		if(StringUtil.isNull(mUserHeadImg)){
			mUserHeadImg = sp.getString(AppConfig.KEY_USER_HEAD_IMG_URL, null);
		}
		return mUserHeadImg;
	}
	
	public void saveUserHeadImg(String userHeadImg){
		editor.putString(AppConfig.KEY_USER_HEAD_IMG_URL, userHeadImg).apply();
		mUserHeadImg = userHeadImg;
	}

	public String getUserIntro(){
		if(StringUtil.isNull(mUserIntro)){
			mUserIntro = sp.getString(AppConfig.KEY_USER_INTRO, null);
		}
		return mUserIntro;
	}
	
	public void saveUserIntro(String userIntro){
		editor.putString(AppConfig.KEY_USER_INTRO, userIntro).apply();
		mUserIntro = userIntro;
	}
	
	public int getUserSex(){
		return sp.getInt(AppConfig.KEY_USER_SEX, 0);
	}
	
	public void saveUserSex(int sexCode){
		editor.putInt(AppConfig.KEY_USER_SEX, sexCode).apply();
	}
	
	public String getUserBirthday(){
		if(StringUtil.isNull(mUserBirthday)){
			mUserBirthday = sp.getString(AppConfig.KEY_USER_BIRTHDAY, null);
		}
		return mUserBirthday;
	}
	
	public void saveUserBirthday(String userBirthday){
		editor.putString(AppConfig.KEY_USER_BIRTHDAY, userBirthday).apply();
		mUserBirthday = userBirthday;
	}
	
	public String getUserEmail(){
		if(StringUtil.isNull(mUserEmail)){
			mUserEmail = sp.getString(AppConfig.KEY_USER_EMAIL, null);
		}
		return mUserEmail;
	}
	
	public void saveUserEmail(String userEmail){
		editor.putString(AppConfig.KEY_USER_EMAIL, userEmail).apply();
		mUserEmail = userEmail;
	}
	
	public String getUserPhone(){
		if(StringUtil.isNull(mUserPhone)){
			mUserPhone = sp.getString(AppConfig.KEY_USER_PHONE, null);
		}
		return mUserPhone;
	}
	
	public void saveUserPhone(String userPhone){
		editor.putString(AppConfig.KEY_USER_PHONE, userPhone).apply();
		mUserPhone = userPhone;
	}

	public String getUserMoney(){
		if(StringUtil.isNull(mUserMoney)){
			mUserMoney = sp.getString(AppConfig.KEY_USER_MONEY, null);
		}
		return mUserMoney;
	}

	public void saveUserMoney(String userMoney){
		editor.putString(AppConfig.KEY_USER_MONEY, userMoney).apply();
		mUserMoney = userMoney;
	}

	public int getUserRankCode(){
		return sp.getInt(AppConfig.KEY_USER_RANK_CODE, 0);
	}

	public boolean isTalent() { //判定是否达人
		return getUserRankCode() == 4;
	}
	
	public void saveUserRankCode(int userRankCode){
		editor.putInt(AppConfig.KEY_USER_RANK_CODE, userRankCode).apply();
	}

	public String getUserRankName(){
		if(StringUtil.isNull(mUserRankName)){
			mUserRankName = sp.getString(AppConfig.KEY_USER_RANK_NAME, null);
		}
		return mUserRankName;
	}

	public void saveUserRankName(String userRankName){
		editor.putString(AppConfig.KEY_USER_RANK_NAME, userRankName).apply();
		mUserRankName = userRankName;
	}

	public String getWXAccessToken(){
		if(StringUtil.isNull(wxAccessToken)){
			wxAccessToken = sp.getString(AppConfig.KEY_WX_ACCESS_TOKEN, null);
		}
		return wxAccessToken;
	}
	
	public void saveWXAccessToken(String access_token){
		editor.putString(AppConfig.KEY_WX_ACCESS_TOKEN, access_token).apply();
		wxAccessToken = access_token;
	}
	
	public String getWXOpenid(){
		if(StringUtil.isNull(wxOpenid)){
			wxOpenid = sp.getString(AppConfig.KEY_WX_OPEN_ID, null);
		}
		return wxOpenid;
	}
	
	public void saveWXOpenid(String openid){
		editor.putString(AppConfig.KEY_WX_OPEN_ID, openid).apply();
		wxOpenid = openid;
	}
	
	public String getWXUnionid(){
		if(StringUtil.isNull(wxUnionid)){
			wxUnionid = sp.getString(AppConfig.KEY_WX_UNION_ID, null);
		}
		return wxUnionid;
	}
	
	public void saveWXUnionid(String unionid){
		editor.putString(AppConfig.KEY_WX_UNION_ID, unionid).apply();
		wxUnionid = unionid;
	}
	
	public String getWXRefreshToken(){
		if(StringUtil.isNull(wxRefreshToken)){
			wxRefreshToken = sp.getString(AppConfig.KEY_WX_REFRESH_TOKEN, null);
		}
		return wxRefreshToken;
	}
	
	public void saveWXRefreshToken(String refreshToken){
		editor.putString(AppConfig.KEY_WX_REFRESH_TOKEN, refreshToken).apply();
		wxRefreshToken = refreshToken;
	}

	public int getCartTotal(){
		return sp.getInt(AppConfig.KEY_CART_NUM, 0);
	}
	
	public void saveCartTotal(int cartTotal){
		if (HomeFragmentActivity.instance != null) {
			HomeFragmentActivity.instance.changeCartTotal(cartTotal);
		}
		LogUtil.i("CartTotal", "saveCartTotal = " + cartTotal);
		editor.putInt(AppConfig.KEY_CART_NUM, cartTotal).apply();
	}
	
	/**
	 * 判定是否登录
	 */
	public boolean checkIsLogined(){
		return !StringUtil.isNull(getUserId()) && !getUserId().equals("0");
	}

	/**
	 * 登入成功保存状态
	 */
	public void saveUserLoginSuccess(String userId){
		saveUserId(userId);
		changeAllDataStatus();
	}

	/**
	 * 刷新所有登录状态下的数据
	 */
	private void changeAllDataStatus() {
		BaseActivity.updateActivityData(1);
		BaseActivity.updateActivityData(5);
		BaseActivity.updateActivityData(20);
		BaseActivity.updateActivityData(21);
		BaseActivity.updateActivityData(22);
	}

	/**
	 * 用户登出清除状态
	 */
	public void clearUserLoginInfo(Context ctx){
		// 解绑推送服务的用户信息
		AppApplication.onPushRegister(false);
		// 清空微信授权信息
		clearWechatUserInfo();
		// 清空微博授权信息
		AccessTokenKeeper.clear(ctx);
		// 清空缓存的用户信息
		clearUserLoginInfo();
		// 刷新所有登录状态下的数据
		changeAllDataStatus();
	}

	/**
	 * 保存用户信息
	 */
	public void saveUserInfo(UserInfoEntity infoEn) {
		if (infoEn != null) {
			saveUserId(infoEn.getUserId());
			saveShareId(infoEn.getShareId());
			saveUserName(infoEn.getUserName());
			saveUserNameID(infoEn.getUserNameID());
			saveUserNick(infoEn.getUserNick());
			saveUserHeadImg(infoEn.getHeadImg());
			saveUserIntro(infoEn.getUserIntro());
			saveUserSex(infoEn.getSexCode());
			saveUserBirthday(infoEn.getBirthday());
			saveUserEmail(infoEn.getUserEmail());
			saveUserPhone(infoEn.getUserPhone());
			saveUserMoney(infoEn.getMoney());
			saveUserRankCode(infoEn.getUserRankCode());
			saveUserRankName(infoEn.getUserRankName());
			// 绑定用户信息至推送服务
			AppApplication.onPushRegister(true);
		}
	}

	/**
	 * 清空缓存的用户信息
	 */
	private void clearUserLoginInfo(){
		saveUserId(null);
		saveShareId(null);
		saveUserName(null);
		saveUserNameID(null);
		saveUserNick(null);
		saveUserHeadImg(null);
		saveUserIntro(null);
		saveUserSex(0);
		saveUserBirthday(null);
		saveUserEmail(null);
		saveUserPhone(null);
		saveUserMoney(null);
		saveUserRankCode(0);
		saveUserRankName(null);
		saveCartTotal(0); //购物车商品数
	}

	/**
	 * 保存微信授权信息
	 */
	public void saveWechatUserInfo(WXEntity wxEn) {
		if (wxEn != null) {
			saveWXAccessToken(wxEn.getAccess_token());
			saveWXOpenid(wxEn.getOpenid());
			saveWXUnionid(wxEn.getUnionid());
			saveWXRefreshToken(wxEn.getRefresh_token());
		}
	}

	/**
	 * 清除微信授权信息
	 */
	private void clearWechatUserInfo() {
		saveWXAccessToken(null);
		saveWXOpenid(null);
		saveWXUnionid(null);
		saveWXRefreshToken(null);
	}

}
