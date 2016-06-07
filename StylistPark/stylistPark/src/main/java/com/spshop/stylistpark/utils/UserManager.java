package com.spshop.stylistpark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.home.ChildFragmentOne;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.activity.home.ProductListActivity;
import com.spshop.stylistpark.activity.profile.ChildFragmentFive;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.entity.WXEntity;
import com.spshop.stylistpark.share.weibo.AccessTokenKeeper;

public class UserManager {
	
	private static UserManager instance;
	private static SharedPreferences sp;
	
	private static String mUserId = null;
	private static String mLoginName = null;
	private static String mUserNickName = null;
	private static String mUserHeadImg = null;
	private static String mUserIntro = null;
	private static String mUserBirthday = null;
	private static String mUserEmail = null;
	private static String mUserPhone = null;
	private static String mUserRank = null;
	private static String mUserLevel = null;
	
	private static String wxAccessToken = null;
	private static String wxOpenid = null;
	private static String wxUnionid = null;
	private static String wxRefreshToken = null;
	
	private UserManager(){
		sp = AppApplication.getSharedPreferences();
	}
	
	public static UserManager getInstance(){
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}
	
	public String getUserId(){
		if(StringUtil.isNull(mUserId)){
			mUserId = sp.getString(AppConfig.KEY_USER_ID, null);
		}
		LogUtil.i("isLogined", "getUserId = " + mUserId);
		return mUserId;
	}
	
	private void saveUserId(String userId){
		sp.edit().putString(AppConfig.KEY_USER_ID, userId).commit();
		mUserId = userId;
		LogUtil.i("isLogined", "saveUserId = " + userId);
	}
	
	public String getLoginName(){
		if(StringUtil.isNull(mLoginName)){
			mLoginName = sp.getString(AppConfig.KEY_USER_LOGIN_NAME, "");
		}
		return mLoginName;
	}
	
	public void saveLoginName(String loginName){
		sp.edit().putString(AppConfig.KEY_USER_LOGIN_NAME, loginName).commit();
	    mLoginName = loginName;
	}
	
	public String getUserNickName(){
		if(StringUtil.isNull(mUserNickName)){
			mUserNickName = sp.getString(AppConfig.KEY_USER_NICK_NAME, "");
		}
		return mUserNickName;
	}
	
	public void saveUserNickName(String nickName){
		sp.edit().putString(AppConfig.KEY_USER_NICK_NAME, nickName).commit();
		mUserNickName = nickName;
	}
	
	public String getUserHeadImg(){
		if(StringUtil.isNull(mUserHeadImg)){
			mUserHeadImg = sp.getString(AppConfig.KEY_USER_HEAD_IMG_URL, null);
		}
		return mUserHeadImg;
	}
	
	public void saveUserHeadImg(String userHeadImg){
		sp.edit().putString(AppConfig.KEY_USER_HEAD_IMG_URL, userHeadImg).commit();
		mUserRank = userHeadImg;
	}
	
	public String getUserIntro(){
		if(StringUtil.isNull(mUserIntro)){
			mUserIntro = sp.getString(AppConfig.KEY_USER_INTRO, null);
		}
		return mUserIntro;
	}
	
	public void saveUserIntro(String userIntro){
		sp.edit().putString(AppConfig.KEY_USER_INTRO, userIntro).commit();
		mUserIntro = userIntro;
	}
	
	public int getUserSex(){
		return sp.getInt(AppConfig.KEY_USER_SEX, 0);
	}
	
	public void saveUserSex(int sexCode){
		sp.edit().putInt(AppConfig.KEY_USER_SEX, sexCode).commit();
	}
	
	public String getUserBirthday(){
		if(StringUtil.isNull(mUserBirthday)){
			mUserBirthday = sp.getString(AppConfig.KEY_USER_BIRTHDAY, null);
		}
		return mUserBirthday;
	}
	
	public void saveUserBirthday(String userBirthday){
		sp.edit().putString(AppConfig.KEY_USER_BIRTHDAY, userBirthday).commit();
		mUserBirthday = userBirthday;
	}
	
	public String getUserEmail(){
		if(StringUtil.isNull(mUserEmail)){
			mUserEmail = sp.getString(AppConfig.KEY_USER_EMAIL, null);
		}
		return mUserEmail;
	}
	
	public void saveUserEmail(String userEmail){
		sp.edit().putString(AppConfig.KEY_USER_EMAIL, userEmail).commit();
		mUserEmail = userEmail;
	}
	
	public String getUserPhone(){
		if(StringUtil.isNull(mUserPhone)){
			mUserPhone = sp.getString(AppConfig.KEY_USER_PHONE, null);
		}
		return mUserPhone;
	}
	
	public void saveUserPhone(String userPhone){
		sp.edit().putString(AppConfig.KEY_USER_PHONE, userPhone).commit();
		mUserPhone = userPhone;
	}
	
	public String getUserRank(){
		if(StringUtil.isNull(mUserRank)){
			mUserRank = sp.getString(AppConfig.KEY_USER_RANK, null);
		}
		return mUserRank;
	}
	
	public void saveUserRank(String userRank){
		sp.edit().putString(AppConfig.KEY_USER_RANK, userRank).commit();
		mUserRank = userRank;
	}
	
	public String getUserLevel(){
		if(StringUtil.isNull(mUserLevel)){
			mUserLevel = sp.getString(AppConfig.KEY_USER_LEVEL, null);
		}
		return mUserLevel;
	}
	
	public void saveUserLevel(String userLevel){
		sp.edit().putString(AppConfig.KEY_USER_LEVEL, userLevel).commit();
		mUserLevel = userLevel;
	}
	
	public String getWXAccessToken(){
		if(StringUtil.isNull(wxAccessToken)){
			wxAccessToken = sp.getString(AppConfig.KEY_WX_ACCESS_TOKEN, null);
		}
		return wxAccessToken;
	}
	
	public void saveWXAccessToken(String access_token){
		sp.edit().putString(AppConfig.KEY_WX_ACCESS_TOKEN, access_token).commit();
		wxAccessToken = access_token;
	}
	
	public String getWXOpenid(){
		if(StringUtil.isNull(wxOpenid)){
			wxOpenid = sp.getString(AppConfig.KEY_WX_OPEN_ID, null);
		}
		return wxOpenid;
	}
	
	public void saveWXOpenid(String openid){
		sp.edit().putString(AppConfig.KEY_WX_OPEN_ID, openid).commit();
		wxOpenid = openid;
	}
	
	public String getWXUnionid(){
		if(StringUtil.isNull(wxUnionid)){
			wxUnionid = sp.getString(AppConfig.KEY_WX_UNION_ID, null);
		}
		return wxUnionid;
	}
	
	public void saveWXUnionid(String unionid){
		sp.edit().putString(AppConfig.KEY_WX_UNION_ID, unionid).commit();
		wxUnionid = unionid;
	}
	
	public String getWXRefreshToken(){
		if(StringUtil.isNull(wxRefreshToken)){
			wxRefreshToken = sp.getString(AppConfig.KEY_WX_REFRESH_TOKEN, null);
		}
		return wxRefreshToken;
	}
	
	public void saveWXRefreshToken(String refreshToken){
		sp.edit().putString(AppConfig.KEY_WX_REFRESH_TOKEN, refreshToken).commit();
		wxRefreshToken = refreshToken;
	}
	
	public boolean getUserAuth(){
		return sp.getBoolean(AppConfig.KEY_USER_AUTH, false);
	}
	
	public void saveUserAuth(boolean isAuth){
		sp.edit().putBoolean(AppConfig.KEY_USER_AUTH, isAuth).commit();
	}
	
	public int getUserAddressId(){
		return sp.getInt(AppConfig.KEY_USER_ADDRESS, 0);
	}
	
	public void saveUserAddressId(int addressId){
		sp.edit().putInt(AppConfig.KEY_USER_ADDRESS, addressId).commit();
	}
	
	public int getCartTotal(){
		return sp.getInt(AppConfig.KEY_CART_NUM, 0);
	}
	
	public void saveCartTotal(int cartTotal){
		if (HomeFragmentActivity.instance != null) {
			HomeFragmentActivity.instance.changeCartTotal(cartTotal);
		}
		LogUtil.i("CartTotal", "saveCartTotal = " + cartTotal);
		sp.edit().putInt(AppConfig.KEY_CART_NUM, cartTotal).commit();
	}
	
	/**
	 * 判定是否登录
	 */
	public boolean checkIsLogined(){
		return !StringUtil.isNull(getUserId()) && !getUserId().equals("0");
	}

	/**
	 * 保存用户资料
	 */
	public void saveUserInfo(UserInfoEntity infoEn) {
		if (infoEn != null) {
			saveUserId(infoEn.getUserId());
			saveUserNickName(infoEn.getUserNick());
			saveUserHeadImg(infoEn.getHeadImg());
			saveUserIntro(infoEn.getUserIntro());
			saveUserSex(infoEn.getSexCode());
			saveUserBirthday(infoEn.getBirthday());
			saveUserEmail(infoEn.getUserEmail());
			saveUserPhone(infoEn.getUserPhone());
			saveUserAuth(infoEn.isAuth());
			saveUserRank(infoEn.getUserRankName());
		}
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
	 * 登入成功保存状态
	 */
	public void saveUserLoginSuccess(String userId){
		saveUserId(userId);
		changeAllDataStatus();
	}
	
	private void changeAllDataStatus() {
		if (ChildFragmentOne.instance != null) {
			ChildFragmentOne.instance.isUpdate = true;
		}
		if (ChildFragmentFive.instance != null) {
			ChildFragmentFive.instance.isUpdate = true;
		}
		if (ProductDetailActivity.instance != null) {
			ProductDetailActivity.instance.isUpdate = true;
		}
		if (ProductListActivity.instance != null) {
			ProductListActivity.instance.isUpdate = true;
		}
	}

	/**
	 * 用户登出清除状态
	 */
	public void clearUserLoginInfo(Context ctx){
		clearWechatUserInfo(); //清空微信授权信息
		AccessTokenKeeper.clear(ctx); //清空微博授权信息
		updateUserLoginInfo(null, null, null, null, 0, null, null, null, null, null, false, 0, 0);
		changeAllDataStatus();
	}

	/**
	 * 更新用户个人资料
	 */
	private void updateUserLoginInfo(String userId, String nickName, String userHeadImg, String userIntro,
			int sexCode, String userBirthday, String userEmail, String userPhone, String userRank, 
			String userLevel, boolean isAuth, int addressId, int cartTotal){
		saveUserId(userId);
		saveUserNickName(nickName);
		saveUserHeadImg(userHeadImg);
		saveUserIntro(userIntro);
		saveUserSex(sexCode);
		saveUserBirthday(userBirthday);
		saveUserEmail(userEmail);
		saveUserPhone(userPhone);
		saveUserRank(userRank);
		saveUserLevel(userLevel);
		saveUserAuth(isAuth);
		saveUserAddressId(addressId);
		saveCartTotal(cartTotal);
	}
	
	/**
	 * 清除微信授权信息
	 */
	private void clearWechatUserInfo() {
		Editor editor = sp.edit();
		editor.putString(AppConfig.KEY_WX_ACCESS_TOKEN, null);
		editor.putString(AppConfig.KEY_WX_OPEN_ID, null);
		editor.putString(AppConfig.KEY_WX_UNION_ID, null);
		editor.putString(AppConfig.KEY_WX_REFRESH_TOKEN, null);
		editor.commit();
	}

}
