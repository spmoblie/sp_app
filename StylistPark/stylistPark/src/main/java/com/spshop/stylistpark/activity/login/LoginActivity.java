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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
import com.tencent.stat.StatService;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;

import java.util.Arrays;

public class LoginActivity extends BaseActivity implements OnClickListener{
	
	public static final String TAG = "LoginActivity";
	public static LoginActivity instance = null;
	
	public static final String LOGIN_TYPE_WX = "wechatapp";
	public static final String LOGIN_TYPE_QQ = "qq";
	public static final String LOGIN_TYPE_WB = "weibo";
	public static final String LOGIN_TYPE_ZF = "alipay";
	public static final String LOGIN_TYPE_FB = "fb";
	
	private EditText et_user, et_password;
	private Button btn_login;
	private ImageView iv_head, iv_clear_user, iv_check_password;
	private TextView tv_register, tv_forger;
	private TextView tv_wechat, tv_qq, tv_weibo, tv_alipay, tv_facebook;
	
	private HttpUtil http;
	private UserManager um;
	private DisplayImageOptions options;
	private UserInfoEntity infoEn, fbOauthEn;
	private boolean isStop = false;
	private String rootPage, loginType, postUid, userStr, passWordStr;
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
	// FB
	private UiLifecycleHelper uiHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		instance = this;
		rootPage = getIntent().getExtras().getString("rootPage");
		http = new HttpUtil();
		um = UserManager.getInstance();
		userStr = um.getLoginName();
		options = AppApplication.getImageOptions(90, R.drawable.ic_launcher);
		// QQ
		mTencent = Tencent.createInstance(QQ_APP_ID, mContext);
		// FB
		uiHelper = new UiLifecycleHelper(this, facebookCallback);
		uiHelper.onCreate(savedInstanceState);
		
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
		
		ImageLoader.getInstance().displayImage("", iv_head, options);
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
		if (!StringUtil.isNull(userStr)) { //显示偏好设置
			et_user.setText(userStr);
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
		userStr = et_user.getText().toString();
		if (userStr.isEmpty()) {
			CommonTools.showToast(mContext, getString(R.string.login_input_user_name), 1000);
			return;
		}
		passWordStr = et_password.getText().toString();
		if (passWordStr.isEmpty()) {
			CommonTools.showToast(mContext, getString(R.string.login_input_password), 1000);
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
			CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_no_wechat), 1000);
			return;
		}
//		access_token = um.getWXAccessToken();
//		openid = um.getWXOpenid();
//		unionid = um.getWXUnionid();
//		refresh_token = um.getWXRefreshToken();
//		if (access_token != null && openid != null && unionid != null && refresh_token != null) {
//			startAnimation();
//			// 校验access_token有效性
//			new HttpWechatAuthTask().execute("https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openid);
//		} else {
			// 用户授权获取access_token
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
			return http.HttpGet(instance, params[0]);
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
				ExceptionUtil.handle(instance, e);
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
			return http.HttpGet(instance, params[0]);
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
				ExceptionUtil.handle(instance, e);
				showLoginError();
			}
		}
	}
	
	/**
	 * 提交微信授权登录请求
	 */
	public void postWechatLoginRequest() {
		if (mTencent != null) {
			loginType = LOGIN_TYPE_WX;
			postUid = um.getWXUnionid();
			requestThirdPartiesLogin();
		}
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
			return http.HttpGet(instance, params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				WXUserInfoEntity userInfo = LoginJsonParser.getWexiUserInfo(result);
				if (userInfo != null) {
					UserInfoEntity oauthEn = new UserInfoEntity();
					oauthEn.setUserName(LOGIN_TYPE_WX);
					oauthEn.setUserId(userInfo.getUnionid());
					oauthEn.setUserNick(userInfo.getNickname());
					oauthEn.setUserIntro(userInfo.getSex());
					oauthEn.setHeadImg(userInfo.getHeadUrl());
					startRegisterOauthActivity(oauthEn);
				} else {
					showLoginError();
				}
			} catch (JSONException e) {
				ExceptionUtil.handle(instance, e);
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
						oauthEn.setUserName(LOGIN_TYPE_QQ);
						oauthEn.setUserId(mTencent.getOpenId());
						oauthEn.setUserNick(userInfo.getNickname());
						oauthEn.setUserIntro(userInfo.getGender());
						oauthEn.setHeadImg(userInfo.getHeadUrl());
						startRegisterOauthActivity(oauthEn);
					} else {
						showLoginError();
					}
				}
			} catch (Exception e) {
				ExceptionUtil.handle(mContext, e);
				showLoginError();
			}
		}

		@Override
		public void onError(UiError jsonObject) {
			showLoginError();
		}
		
	};
	
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
	 * 根据用户ID获取用户信息
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
					ExceptionUtil.handle(mContext, e);
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
	 * 根据Uid获取微博用户信息
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
                    oauthEn.setUserName(LOGIN_TYPE_WB);
                    oauthEn.setUserId(token.getUid());
                    oauthEn.setUserNick(user.name);
                    oauthEn.setUserIntro(user.gender);
                    oauthEn.setHeadImg(user.profile_image_url);
                    startRegisterOauthActivity(oauthEn);
                } else {
                	showLoginError();
                }
            }
        }

		@Override
        public void onWeiboException(WeiboException e) {
        	ExceptionUtil.handle(mContext, e);
        	showLoginError();
        }
    };

	/**
	 * Facebook登录
	 */
	private void loginFacebook() {
		Session.openActiveSession(this, true, Arrays.asList("email","user_likes", "user_status"), facebookCallback);
	}
	
	/**
	 * Facebook授权回调接口
	 */
	private Session.StatusCallback facebookCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			LogUtil.i(TAG, "Facebook session = " + session.isOpened() + " state = " + state.isOpened());
			if(session.isOpened()) {
				fbLogin(session, state, exception);
			}
		}
	};
	
	/**
	 * 登录成功后获取用户信息
	 */
	protected void fbLogin(final Session session, SessionState state, Exception exception){
	    Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				LogUtil.i(TAG, "FB onComplete response = " + response);
				if (user != null) {
					String fbId = user.getId();
					postFacebookLoginRequest(fbId);
					// 记录用户信息
					fbOauthEn = new UserInfoEntity();
					fbOauthEn.setUserName(LOGIN_TYPE_FB);
					fbOauthEn.setUserId(fbId);
					fbOauthEn.setUserNick(user.getName());
					fbOauthEn.setUserIntro(user.asMap().get("gender").toString());
					fbOauthEn.setHeadImg(user.getProperty("email").toString());
				} else {
					showLoginError();
				}
			}
		}).executeAsync();
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
	 * 获取Facebook用户信息
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
		CommonTools.showToast(mContext, getString(R.string.login_oauth_cancel), 1000);
	}
	
	/**
	 * 提示授权登录失败
	 */
	private void showLoginError() {
		stopAnimation();
		CommonTools.showToast(mContext, getString(R.string.login_error_oauth), 1000); 
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
		if (uiHelper != null) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void OnListenerLeft() {
    	LogUtil.i(TAG, "rootPage = " + rootPage);
    	if (!rootPage.equals("ProductDetailActivity") 
    	 && !rootPage.equals("HomeFragmentActivity")
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
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
        // FB
		if(uiHelper != null){
			uiHelper.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
        // FB
        if (uiHelper != null) {
			uiHelper.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		// FB
		if (uiHelper != null) {
			uiHelper.onDestroy();
		}
		isStop = true;
	}
	
	/**
	 * 在Activity中的onSaveInstanceState调用此方法
	 */
	public void onSaveInstanceState(Bundle outState){
		if (uiHelper != null) {
			uiHelper.onSaveInstanceState(outState);
		}
	}
	
	@Override
	public Object doInBackground(int requestCode) throws Exception {
		infoEn = null;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_POST_ACCOUNT_LOGIN_CODE: //账号密码登入
			infoEn = sc.postAccountLoginData(userStr, passWordStr);
			break;
		case AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN: //第三方授权登入
			infoEn = sc.postThirdPartiesLogin(loginType, postUid);
			break;
		}
		return infoEn;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		if (infoEn != null) {
			if (infoEn.getErrCode() == 1) //校验通过
			{ 
				um.saveUserLoginSuccess(infoEn.getUserId());
				um.saveLoginName(userStr);
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
						// 备用
					}else if (loginType == LOGIN_TYPE_FB) {
						getFacebookUserInfo();
					}
				}else {
					stopAnimation();
					if (StringUtil.isNull(infoEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(this, infoEn.getErrInfo(), 2000);
					}
				}
			}
			else 
			{
				stopAnimation();
				if (StringUtil.isNull(infoEn.getErrInfo())) {
					showServerBusy();
				}else {
					CommonTools.showToast(this, infoEn.getErrInfo(), 2000);
				}
			}
		}else {
			stopAnimation();
			showServerBusy();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}
	
}
