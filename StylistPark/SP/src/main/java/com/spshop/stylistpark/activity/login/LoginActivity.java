package com.spshop.stylistpark.activity.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.cart.CartActivity;
import com.spshop.stylistpark.entity.AuthResult;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.QQEntity;
import com.spshop.stylistpark.entity.QQUserInfoEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.entity.WXEntity;
import com.spshop.stylistpark.entity.WXUserInfoEntity;
import com.spshop.stylistpark.service.LoginJsonParser;
import com.spshop.stylistpark.share.weibo.AccessTokenKeeper;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity implements OnClickListener{
	
	public static final String TAG = "LoginActivity";
	public static LoginActivity instance = null;
	
	public static final String LOGIN_TYPE_WX = "wechat";
	public static final String LOGIN_TYPE_QQ = "qq";
	public static final String LOGIN_TYPE_WB = "weibo";
	public static final String LOGIN_TYPE_ZF = "alipay";
	public static final String LOGIN_TYPE_FB = "fb";
	
	private EditText et_user, et_password;
	private Button btn_login;
	private ImageView iv_head, iv_clear_user, iv_check_password;
	private TextView tv_register, tv_forger;
	private TextView tv_wechat, tv_qq, tv_weibo, tv_alipay, tv_facebook;
	
	private UserManager um;
	private UserInfoEntity fbOauthEn;
	private boolean isStop = false;
	private String rootPage, loginType, postUid, loginAccount, passWordStr;
	// WX
	private static final String WX_APP_ID = AppConfig.WX_APP_ID;
	private String access_token, openid, unionid, refresh_token;
	// QQ
	private Tencent mTencent;
	private boolean isQQLogin = true;
	private static final String QQ_APP_ID = AppConfig.QQ_APP_ID;
	private static final String QQ_SCOPE = AppConfig.QQ_SCOPE;
	// WB
	private AuthInfo mAuthInfo;
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
	private Oauth2AccessToken mAccessToken;
	private UsersAPI mUsersAPI;
	private static final String WB_APP_ID = AppConfig.WB_APP_ID;
	private static final String WB_REDIRECT_URL = AppConfig.WB_REDIRECT_URL;
	private static final String WB_SCOPE = AppConfig.WB_SCOPE;
	// ZF
	private String alipayOpenId;
	private String alipayAuthCode;
	// FB
	private CallbackManager callbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		rootPage = getIntent().getExtras().getString("rootPage");
		um = UserManager.getInstance();
		loginAccount = um.getLoginAccount();

		// QQ
		mTencent = Tencent.createInstance(QQ_APP_ID, mContext);
		// FB
		callbackManager = CallbackManager.Factory.create();

		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_user = (EditText) findViewById(R.id.login_et_users);
		et_password = (EditText) findViewById(R.id.login_et_passwords);
		btn_login = (Button) findViewById(R.id.login_btn_login);
		iv_head = (ImageView) findViewById(R.id.login_iv_head);
		iv_clear_user = (ImageView) findViewById(R.id.login_iv_clear_user);
		iv_check_password = (ImageView) findViewById(R.id.login_iv_password_check);
		tv_register = (TextView) findViewById(R.id.login_tv_register);
		tv_forger = (TextView) findViewById(R.id.login_tv_forger_password);
		tv_wechat = (TextView) findViewById(R.id.login_tv_wechat);
		tv_qq = (TextView) findViewById(R.id.login_tv_qq);
		tv_weibo = (TextView) findViewById(R.id.login_tv_weibo);
		tv_alipay = (TextView) findViewById(R.id.login_tv_alipay);
		tv_facebook = (TextView) findViewById(R.id.login_tv_facebook);
	}

	private void initView() {
		setTitle(R.string.title_login);
		btn_login.setOnClickListener(this);
		iv_clear_user.setOnClickListener(this);
		iv_check_password.setOnClickListener(this);
		tv_register.setOnClickListener(this);
		tv_forger.setOnClickListener(this);
		tv_wechat.setOnClickListener(this);
		tv_qq.setOnClickListener(this);
		tv_weibo.setOnClickListener(this);
		tv_alipay.setOnClickListener(this);
		tv_facebook.setOnClickListener(this);

		ImageLoader.getInstance().displayImage("", iv_head, AppApplication.getHeadImageOptions());
		initEditText();
	}
	
	private void initEditText() {
		et_user.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					iv_clear_user.setVisibility(View.GONE);
				}else {
					iv_clear_user.setVisibility(View.VISIBLE);
				}
			}
		});
		if (!StringUtil.isNull(loginAccount)) { //显示偏好设置
			et_user.setText(loginAccount);
			et_user.setSelection(et_user.length());
		}
		iv_check_password.setSelected(false);//设置默认隐藏密码
	}

	/**
	 * 更新密码输入框密码显示的状态
	 */
	private void updatePasswordEditText(boolean isShow) {
		if (isShow) { //显示密码
			et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); 
		}else { //隐藏密码
			et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		et_password.setSelection(et_password.length()); //调整光标至最后
	}

	private void login() {
		loginAccount = et_user.getText().toString();
		if (loginAccount.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_user_name), 1000);
			return;
		}
		passWordStr = et_password.getText().toString();
		if (passWordStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_password), 1000);
			return;
		}
		requestAccountLogin();
	}

	/**
	 * 请求账号密码登录
	 */
	private void requestAccountLogin() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_ACCOUNT_LOGIN_CODE);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.login_btn_login:
			login();
			break;
		case R.id.login_iv_clear_user:
			et_user.setText("");
			break;
		case R.id.login_iv_password_check:
			if (!iv_check_password.isSelected()) {
				iv_check_password.setSelected(true); 
				updatePasswordEditText(true);
			}else {
				iv_check_password.setSelected(false); 
				updatePasswordEditText(false);
			}
			break;
		case R.id.login_tv_register:
			intent = new Intent(mContext, RegisterActivity.class);
			break;
		case R.id.login_tv_forger_password:
			intent = new Intent(mContext, ResetPasswordActivity.class);
			break;
		case R.id.login_tv_wechat:
			loginWechat();
			break;
		case R.id.login_tv_qq:
			loginQQ();
			break;
		case R.id.login_tv_weibo:
			loginWeibo();
			break;
		case R.id.login_tv_alipay:
			loginAlipay();
			break;
		case R.id.login_tv_facebook:
			loginFacebook();
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	/**
	 * 微信登录
	 */
	private void loginWechat() {
		if(!api.isWXAppInstalled()){ //检测是否安装微信客户端
			CommonTools.showToast(mContext.getString(R.string.share_msg_no_wechat), 1000);
			return;
		}
		startAnimation();
//		access_token = um.getWXAccessToken();
//		openid = um.getWXOpenid();
//		unionid = um.getWXUnionid();
//		refresh_token = um.getWXRefreshToken();
//		if (access_token != null && openid != null && unionid != null && refresh_token != null) {
//			// 校验access_token有效性
//			new HttpWechatAuthTask().execute("https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openid);
//		} else {
			// 用户授权获取access_token
			AppApplication.isWXShare = false;
			api.registerApp(AppConfig.WX_APP_ID);
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "stylistpark";
			api.sendReq(req);
			api.isWXAppSupportAPI();
//		}
	}

	/**
	 * 异步校验微信access_token
	 */
	class HttpWechatAuthTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				return sc.getServerJSONString(params[0]);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				return "";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {
				WXEntity wxEn = LoginJsonParser.authWexiAccessToken(result);
				if (wxEn != null && wxEn.getErrcode() == 0) { //校验有效直接登入
					postWechatLoginRequest();
				} else { //需要刷新或续期access_token
					new HttpWechatRefreshTask().execute("https://api.weixin.qq.com/sns/oauth2/refresh_token?"
							+ "appid=" + WX_APP_ID + "&grant_type=refresh_token&refresh_token=" + refresh_token);
				}
			} catch (JSONException e) {
				ExceptionUtil.handle(e);
				showLoginError();
			}
		}
	}
	
	/**
	 * 异步刷新或续期微信access_token
	 */
	class HttpWechatRefreshTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				return sc.getServerJSONString(params[0]);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				return "";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {
				WXEntity wxEn = LoginJsonParser.getWexiAccessAuth(result);
				if (wxEn != null) { //刷新或续期access_token成功
					wxEn.setUnionid(unionid);
					UserManager.getInstance().saveWechatUserInfo(wxEn);
					postWechatLoginRequest();
				} else {
					showLoginError();
				}
			} catch (JSONException e) {
				ExceptionUtil.handle(e);
				showLoginError();
			}
		}
	}
	
	/**
	 * 提交微信授权登录请求
	 */
	public void postWechatLoginRequest() {
		loginType = LOGIN_TYPE_WX;
		access_token = um.getWXAccessToken();
		openid = um.getWXOpenid();
		unionid = um.getWXUnionid();
		refresh_token = um.getWXRefreshToken();
		postUid = unionid;
		requestThirdPartiesLogin();
	}

	/**
	 * 获取微信用户信息
	 */
	private void getWechatUserInfo() {
		new HttpWechatUserTask().execute("https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid);
	}

	/**
	 * 异步获取微信用户信息
	 */
	class HttpWechatUserTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				return sc.getServerJSONString(params[0]);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				WXUserInfoEntity userInfo = LoginJsonParser.getWexiUserInfo(result);
				if (userInfo != null) {
					UserInfoEntity oauthEn = new UserInfoEntity();
					oauthEn.setUserRankName(LOGIN_TYPE_WX);
					oauthEn.setUserId(userInfo.getUnionid());
					oauthEn.setUserNick(userInfo.getNickname());
					oauthEn.setUserIntro(userInfo.getSex());
					oauthEn.setHeadImg(userInfo.getHeadUrl());
					// 注册微信用户信息
					startRegisterOauthActivity(oauthEn);
				} else {
					showLoginError();
				}
			} catch (JSONException e) {
				ExceptionUtil.handle(e);
				showLoginError();
			}
		}
	}

	/**
	 * QQ登录
	 */
	private void loginQQ() {
		if (mTencent != null) {
			isQQLogin = true;
			mTencent.login(LoginActivity.this, QQ_SCOPE, qqLoginListener);
		}else {
			showLoginError();
		}
	}

	/**
	 * 提交QQ授权登录请求
	 */
	private void postQQLoginRequest() {
		if (mTencent != null) {
			loginType = LOGIN_TYPE_QQ;
			postUid = mTencent.getOpenId();
			requestThirdPartiesLogin();
		}else {
			showLoginError();
		}
	}

	/**
	 * 获取QQ用户信息
	 */
	private void getQQUserInfo() {
		if (mTencent != null) {
			isQQLogin = false;
			UserInfo info = new UserInfo(LoginActivity.this, mTencent.getQQToken());
			info.getUserInfo(qqLoginListener);
		}else {
			showLoginError();
		}
	}
	
	/**
	 * QQ登录、快速支付登录、应用分享、应用邀请等回调接口
	 */
	IUiListener qqLoginListener = new IUiListener(){

		@Override
		public void onCancel() {
			showLoginCancel();
		}

		@Override
		public void onComplete(Object jsonObject) {
			try {
				if (isQQLogin) {
					QQEntity qqEn = LoginJsonParser.getQQLoginResult(jsonObject);
					if (mTencent != null && qqEn != null && qqEn.getErrcode() == 0) { //用户授权成功
						mTencent.setOpenId(qqEn.getOpenid());
						mTencent.setAccessToken(qqEn.getAccess_token(), qqEn.getExpires_in());
						postQQLoginRequest();
					} else {
						showLoginError();
					}
				}else {
					QQUserInfoEntity userInfo = LoginJsonParser.getQQUserInfo(jsonObject);
					if (userInfo != null && userInfo.getErrcode() == 0) { //获取用户信息成功
						UserInfoEntity oauthEn = new UserInfoEntity();
						oauthEn.setUserRankName(LOGIN_TYPE_QQ);
						oauthEn.setUserId(mTencent.getOpenId());
						oauthEn.setUserNick(userInfo.getNickname());
						oauthEn.setUserIntro(userInfo.getGender());
						oauthEn.setHeadImg(userInfo.getHeadUrl());
						// 注册QQ用户信息
						startRegisterOauthActivity(oauthEn);
					} else {
						showLoginError();
					}
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				showLoginError();
			}
		}

		@Override
		public void onError(UiError jsonObject) {
			showLoginError();
		}
		
	};

	/**
	 * 微博登录
	 */
	private void loginWeibo() {
//		mAccessToken = AccessTokenKeeper.readAccessToken(this);
//		if (mAccessToken != null  && mAccessToken.isSessionValid()) { //检测登录有效性
//			String date = TimeUtil.dateToStrLong(new java.util.Date(mAccessToken.getExpiresTime()));  
//            LogUtil.i(TAG, "weibo access_token 仍在有效期内,无需再次登录！有效期：" + date);
//            postWeiboLoginRequest();
//		}else {
			mAuthInfo = new AuthInfo(mContext, WB_APP_ID, WB_REDIRECT_URL, WB_SCOPE);
			mSsoHandler = new SsoHandler(this, mAuthInfo);
			// SSO 授权, 仅客户端
			mSsoHandler.authorize(new WeiboAuthListener() {
				@Override
				public void onComplete(Bundle values) {
					mAccessToken = Oauth2AccessToken.parseAccessToken(values);
					if (mAccessToken != null && mAccessToken.isSessionValid()) {
						AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
						postWeiboLoginRequest();
					}else {
						showLoginError();
					}
				}
				
				@Override
				public void onCancel() {
					
				}
				
				@Override
				public void onWeiboException(WeiboException e) {
					ExceptionUtil.handle(e);
					showLoginError();
				}
			});
//		}
	}

	/**
	 * 提交微博授权登录请求
	 */
	private void postWeiboLoginRequest() {
		if (mAccessToken != null) {
			loginType = LOGIN_TYPE_WB;
			postUid = mAccessToken.getUid();
			requestThirdPartiesLogin();
		}else {
			showLoginError();
		}
	}

	/**
	 * 获取微博用户信息
	 */
	private void getWeiboUserInfo() {
		if (mAccessToken != null) {
			mUsersAPI = new UsersAPI(getApplicationContext(), WB_APP_ID, mAccessToken);
			mUsersAPI.show(Long.parseLong(mAccessToken.getUid()), weiboListener);
		}else {
			showLoginError();
		}
	}
	
	/**
	 * 微博异步请求回调接口
	 */
	private RequestListener weiboListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                if (user != null) {
                    Oauth2AccessToken token =  AccessTokenKeeper.readAccessToken(getApplicationContext());
                    UserInfoEntity oauthEn = new UserInfoEntity();
                    oauthEn.setUserRankName(LOGIN_TYPE_WB);
                    oauthEn.setUserId(token.getUid());
                    oauthEn.setUserNick(user.name);
                    oauthEn.setUserIntro(user.gender);
                    oauthEn.setHeadImg(user.profile_image_url);
					// 注册微博用户信息
                    startRegisterOauthActivity(oauthEn);
                } else {
                	showLoginError();
                }
            }
        }

		@Override
        public void onWeiboException(WeiboException e) {
			ExceptionUtil.handle(e);
        	showLoginError();
        }
    };

	/**
	 * 支付宝登录
	 */
	private void loginAlipay() {
		// 从服务器获取授权参数
		startAnimation();
		request(AppConfig.REQUEST_SV_GET_ALIPAY_AUTHINFO_CODE);
	}

	/**
	 * 使用授权参数换取支付宝授权Code和用户ID
	 */
	private void getAlipayOpenId(final String authInfo) {
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(LoginActivity.this);
				// 调用授权接口，获取授权结果
				Map<String, String> result = authTask.authV2(authInfo, true);
				// 处理授权结果
				AuthResult alipayAuthResult = new AuthResult(result, true);
				alipayOpenId = alipayAuthResult.getAlipayOpenId();
				alipayAuthCode = alipayAuthResult.getAuthCode();
				String resultStatus = alipayAuthResult.getResultStatus();
				// 判断resultStatus 为“9000”且result_code为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(alipayAuthResult.getResultCode(), "200")) {
					postAlipayLoginRequest();
				} else {
					showLoginError();
				}
			}
		};
		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}

	/**
	 * 提交支付宝授权登录请求
	 */
	private void postAlipayLoginRequest() {
		if (!StringUtil.isNull(alipayOpenId)) {
			loginType = LOGIN_TYPE_ZF;
			postUid = alipayOpenId;
			requestThirdPartiesLogin();
		} else {
			showLoginError();
		}
	}

	/**
	 * 获取支付宝用户信息
	 */
	private void getAlipayUserInfo() {
		if (!StringUtil.isNull(alipayAuthCode)) {
			// 使用auth_code从服务器获取用户信息
			request(AppConfig.REQUEST_SV_GET_ALIPAY_USERINFO_CODE);
		} else {
			showLoginError();
		}
	}

	/**
	 * 注册支付宝用户信息
	 */
	private void registAlipayUserInfo(UserInfoEntity oauthEn) {
		if (oauthEn != null && !StringUtil.isNull(alipayOpenId)) {
			oauthEn.setUserRankName(LOGIN_TYPE_ZF);
			oauthEn.setUserId(alipayOpenId);
			startRegisterOauthActivity(oauthEn);
		} else {
			showLoginError();
		}
	}

	/**
	 * Facebook登录
	 */
	private void loginFacebook() {
		LoginManager loginManager = LoginManager.getInstance();
		loginManager.registerCallback(callbackManager, new FaceBookCallBackListener());
		loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email","user_likes", "user_status"));
	}

	private class FaceBookCallBackListener implements FacebookCallback<LoginResult>{
		@Override
		public void onSuccess(LoginResult loginResult) {
			LogUtil.i(TAG, "fb login success");
			//获取登录信息
			getFacebookUserInfo(loginResult.getAccessToken());
		}

		@Override
		public void onCancel() {
			LogUtil.i(TAG, "fb login cancel");
		}

		@Override
		public void onError(FacebookException e) {
			LogUtil.i(TAG, "fb login error");
			ExceptionUtil.handle(e);
			showLoginError();
		}
	}

	/**
	 * 获取Facebook登录信息
	 */
	public void getFacebookUserInfo(AccessToken accessToken){
		GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
			 @Override
			public void onCompleted(JSONObject object, GraphResponse response) {
				 if (object != null) {
					 //获取用户头像
					 JSONObject object_pic = object.optJSONObject("picture");
					 JSONObject object_data = object_pic.optJSONObject("data");
					 String photo = object_data.optString("url");
					 //获取地域信息
					 //String locale = object.optString("locale"); //zh_CN 代表中文简体
					 //获取用户ID
					 String fbId = object.optString("id");
					 // 记录用户信息
					 fbOauthEn = new UserInfoEntity();
					 fbOauthEn.setUserRankName(LOGIN_TYPE_FB);
					 fbOauthEn.setUserId(fbId);
					 fbOauthEn.setUserNick(object.optString("name"));
					 fbOauthEn.setUserIntro(object.optString("gender"));
					 fbOauthEn.setHeadImg(photo);
					 // 校验用户ID
					 postFacebookLoginRequest(fbId);
				 } else {
					 showLoginError();
				 }
			}
		}) ;
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
		request.setParameters(parameters);
		request.executeAsync() ;
	}

	/**
	 * 提交Facebook授权登录请求
	 */
	private void postFacebookLoginRequest(String fbId) {
		if (mAccessToken != null) {
			loginType = LOGIN_TYPE_FB;
			postUid = fbId;
			requestThirdPartiesLogin();
		}
	}

	/**
	 * 获取并注册Facebook用户信息
	 */
	private void getFacebookUserInfo() {
		if (fbOauthEn != null) {
			startRegisterOauthActivity(fbOauthEn);
		}else {
			showLoginError();
		}
	}

	/**
	 * 用户取消授权登录
	 */
	private void showLoginCancel() {
		stopAnimation();
		CommonTools.showToast(getString(R.string.login_oauth_cancel), 1000);
	}
	
	/**
	 * 提示授权登录失败
	 */
	private void showLoginError() {
		stopAnimation();
		CommonTools.showToast(getString(R.string.login_error_oauth), 1000);
	}

	/**
	 * 发起第三方授权登录请求
	 */
	private void requestThirdPartiesLogin() {
		if (StringUtil.isNull(loginType) || StringUtil.isNull(postUid)) {
			showLoginError();
			return;
		}
		startAnimation();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				request(AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN);
			}
		}, 1000);
	}

    /**
     * 跳转到绑定账号页面
     */
    private void startRegisterOauthActivity(UserInfoEntity oauthEn) {
    	if (isStop) return;
		Intent intent = new Intent(mContext, RegisterOauthActivity.class);
		intent.putExtra("oauthEn", oauthEn);
		startActivity(intent);
		stopAnimation();
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// QQ
		if (requestCode == Constants.REQUEST_LOGIN) {
			Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener);
		}
		// WB
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		// FB
		if (callbackManager != null) {
			callbackManager.onActivityResult(requestCode, resultCode, data);
		}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void OnListenerLeft() {
    	LogUtil.i(TAG, "rootPage = " + rootPage);
    	if (!rootPage.equals("ProductDetailActivity") 
    	 && !rootPage.equals("ShowListHeadActivity")
    	 && !rootPage.equals("HomeFragmentActivity")
    	 && !rootPage.equals("MyWebViewActivity")
    	 && !rootPage.equals("CartActivity")
    	 && !rootPage.equals("SettingActivity"))
    	{
    		LogUtil.i(TAG, "start ChildFragmentFive");
    		editor.putInt(AppConfig.KEY_HOME_CURRENT_INDEX, 4).commit();
    		startActivity(new Intent(this, HomeFragmentActivity.class));
		}
    	else {
			if (CartActivity.instance != null) {
				CartActivity.instance.finish();
			}
		}
    	super.OnListenerLeft();
    }

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		isStop = true;
		instance = null;
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=oath_api";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_ACCOUNT_LOGIN_CODE: //账号密码登入
			uri = AppConfig.URL_COMMON_USER_URL + "?act=signin";
			params.add(new MyNameValuePair("username", loginAccount));
			params.add(new MyNameValuePair("password", passWordStr));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_ACCOUNT_LOGIN_CODE, uri, params, HttpUtil.METHOD_POST);

		case AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN: //第三方授权登入
			params.add(new MyNameValuePair("type", loginType));
			params.add(new MyNameValuePair("userid", postUid));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN, uri, params, HttpUtil.METHOD_POST);

		case AppConfig.REQUEST_SV_GET_ALIPAY_AUTHINFO_CODE: //获取支付宝授权信息
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_ALIPAY_AUTHINFO_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_GET_ALIPAY_USERINFO_CODE: //获取支付宝用户信息
			params.add(new MyNameValuePair("authCode", alipayAuthCode));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_ALIPAY_USERINFO_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (instance == null) return;
		super.onSuccess(requestCode, result);
		switch (requestCode) {
			case AppConfig.REQUEST_SV_POST_ACCOUNT_LOGIN_CODE: //账号密码登入
			case AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN: //第三方授权登入
				if (result != null) {
					UserInfoEntity infoEn = (UserInfoEntity) result;
					if (infoEn.getErrCode() == 1) //校验通过
					{
						um.saveUserLoginSuccess(infoEn.getUserId());
						um.saveLoginAccount(loginAccount);
						stopAnimation();
						finish();
					}
					else if (infoEn.getErrCode() == 2) //校验不通过
					{
						if (requestCode == AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN) { //第三方授权登入
							if (loginType == LOGIN_TYPE_WX) {
								getWechatUserInfo();
							}else if (loginType == LOGIN_TYPE_QQ) {
								getQQUserInfo();
							}else if (loginType == LOGIN_TYPE_WB) {
								getWeiboUserInfo();
							}else if (loginType == LOGIN_TYPE_ZF) {
								getAlipayUserInfo();
							}else if (loginType == LOGIN_TYPE_FB) {
								getFacebookUserInfo();
							}
						}else {
							stopAnimation();
							if (StringUtil.isNull(infoEn.getErrInfo())) {
								showServerBusy();
							}else {
								CommonTools.showToast(infoEn.getErrInfo(), 2000);
							}
						}
					}
					else
					{
						stopAnimation();
						if (StringUtil.isNull(infoEn.getErrInfo())) {
							showServerBusy();
						}else {
							CommonTools.showToast(infoEn.getErrInfo(), 2000);
						}
					}
				}else {
					stopAnimation();
					showServerBusy();
				}
				break;
			case AppConfig.REQUEST_SV_GET_ALIPAY_AUTHINFO_CODE: //获取支付宝授权信息
				if (result != null) {
					AuthResult authResult = (AuthResult) result;
					getAlipayOpenId(authResult.getAuthCode());
				} else {
					showLoginError();
				}
				stopAnimation();
				break;
			case AppConfig.REQUEST_SV_GET_ALIPAY_USERINFO_CODE: //获取支付宝用户信息
				if (result != null) {
					registAlipayUserInfo((UserInfoEntity) result);
				} else {
					showLoginError();
				}
				stopAnimation();
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (instance == null) return;
		super.onFailure(requestCode, state, result);
	}
	
}
