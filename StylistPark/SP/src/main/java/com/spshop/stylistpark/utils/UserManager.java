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
	private String mUserAvatar = null;
	private String mUserIntro = null;
	private String mUserBirthday = null;
	private String mUserEmail = null;
	private String mUserPhone = null;
	private String mUserMoney = null;
	private String mUserRankName = null;
	private String mUserRankTime = null;

	private String wxAccessToken = null;
	private String wxOpenid = null;
	private String wxUnionid = null;
	private String wxRefreshToken = null;

	public static UserManager getInstance(){
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new UserManager();
		}
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

	public String getUserAvatar(){
		if(StringUtil.isNull(mUserAvatar)){
			mUserAvatar = sp.getString(AppConfig.KEY_USER_AVATAR_URL, null);
		}
		return mUserAvatar;
	}

	public void saveUserAvatar(String userAvatar){
		editor.putString(AppConfig.KEY_USER_AVATAR_URL, userAvatar).apply();
		mUserAvatar = userAvatar;
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

	public int getUserGender(){
		return sp.getInt(AppConfig.KEY_USER_GENDER, 0);
	}

	public void saveUserGender(int genderCode){
		editor.putInt(AppConfig.KEY_USER_GENDER, genderCode).apply();
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

	public String getUserMoney(){
		if(StringUtil.isNull(mUserMoney)){
			mUserMoney = sp.getString(AppConfig.KEY_USER_MONEY, "0.00");
		}
		return mUserMoney;
	}

	public void saveUserMoney(String userMoney){
		editor.putString(AppConfig.KEY_USER_MONEY, userMoney).commit();
		mUserMoney = userMoney;
	}

	public int getUserRankCode(){
		return sp.getInt(AppConfig.KEY_USER_RANK_CODE, 0);
	}

	public boolean isTalent() { //判定是否达人
		//return getUserRankCode() == 4;
		return false;
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

	public String getUserRankTime(){
		if(StringUtil.isNull(mUserRankTime)){
			mUserRankTime = sp.getString(AppConfig.KEY_USER_RANK_TIME, null);
		}
		return mUserRankTime;
	}

	public void saveUserRankTime(String userRankTime){
		editor.putString(AppConfig.KEY_USER_RANK_TIME, userRankTime).apply();
		mUserRankTime = userRankTime;
	}

	public boolean isPlayVideo(){
		return sp.getBoolean(AppConfig.KEY_IS_SCREEN_PLAY_VIDEO, false);
	}

	public void setPlayVideo(boolean isPlay){
		editor.putBoolean(AppConfig.KEY_IS_SCREEN_PLAY_VIDEO, isPlay).apply();
	}

	public boolean isPlayImage(){
		return sp.getBoolean(AppConfig.KEY_IS_SCREEN_PLAY_IMAGE, false);
	}

	public void setPlayImage(boolean isPlay){
		editor.putBoolean(AppConfig.KEY_IS_SCREEN_PLAY_IMAGE, isPlay).apply();
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
	 * 判定是否实名认证
	 */
	public boolean checkIsAuth(){
		return StringUtil.isNull(getUserName()) || StringUtil.isNull(getUserNameID())
				|| StringUtil.isNull(getUserPhone()) || StringUtil.isNull(getUserEmail());
	}

	/**
	 * 判定是否登录
	 */
	public boolean checkIsLogin(){
		return true;
		//return !StringUtil.isNull(getUserId()) && !getUserId().equals("0");
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
		BaseActivity.updateActivityData(1); //首页
		BaseActivity.updateActivityData(5); //头像
		BaseActivity.updateActivityData(20); //商品详情
		BaseActivity.updateActivityData(21); //商品列表
		BaseActivity.updateActivityData(22); //品牌列表
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
			saveUserPhone(infoEn.getUserPhone());
			saveUserEmail(infoEn.getUserEmail());
			saveUserNick(infoEn.getUserNick());
			saveUserAvatar(infoEn.getUserAvatar());
			saveUserIntro(infoEn.getUserIntro());
			saveUserGender(infoEn.getGenderCode());
			saveUserBirthday(infoEn.getBirthday());
			if (StringUtil.isNull(infoEn.getMoney())) {
				saveUserMoney("0.00");
			} else {
				saveUserMoney(infoEn.getMoney());
			}
			saveUserRankCode(infoEn.getUserRankCode());
			saveUserRankName(infoEn.getUserRankName());
			saveUserRankTime(infoEn.getUserRankTime());
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
		saveUserPhone(null);
		saveUserEmail(null);
		saveUserNick(null);
		saveUserAvatar(null);
		saveUserIntro(null);
		saveUserGender(0);
		saveUserBirthday(null);
		saveUserMoney("0.00");
		saveUserRankCode(0);
		saveUserRankName(null);
		saveUserRankTime(null);
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
