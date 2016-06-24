package com.spshop.stylistpark.share;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.share.weibo.AccessTokenKeeper;
import com.spshop.stylistpark.share.weixi.WXShareUtil;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.DeviceUtil;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ShareView{

	private static final int animationDuration = 400;
	
	Context mContext;
	Activity mActivity;
	ShareEntity mShareEn;
	ObjectAnimator mover;
	ShareVewButtonListener listener;
	
	View rootView;
	View viewDim;
	View ll_mainLayout;
	View tv_Share_QQ;
	View tv_Share_Friends;
	View tv_Share_Wechat;
	View tv_Share_Weibo;
	View tv_Share_Facebook;
	View tv_Share_WhatsApp;
	View tv_Share_Line;
	View tv_Share_Copy;
	
	// QQ
	private Tencent mTencent;
	private static final String QQ_APP_ID = AppConfig.QQ_APP_ID;
	// WX
	private IWXAPI mWXApi;
	private static final String WX_APP_ID = AppConfig.WX_APP_ID;
	// WB
	private AuthInfo mAuthInfo;
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
	private Oauth2AccessToken mAccessToken;
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;
	private IWeiboShareAPI  mWeiboShareAPI;
	private static final String WB_APP_ID = AppConfig.WB_APP_ID;
	private static final String WB_REDIRECT_URL = AppConfig.WB_REDIRECT_URL;
	private static final String WB_SCOPE = AppConfig.WB_SCOPE;
	// FB
	private UiLifecycleHelper uiHelper;

	
	public ShareView(Bundle savedInstanceState, Context context, Activity activity,
			View rootView, ShareVewButtonListener listener){
		this.mContext = context;
		this.mActivity = activity;
		this.rootView = rootView;
		this.listener = listener;
		
		// QQ
		if (mTencent == null) {
            mTencent = Tencent.createInstance(QQ_APP_ID, context);
        }
		// WX
		mWXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID, false);
		mWXApi.registerApp(WX_APP_ID);
		// FB
		uiHelper = new UiLifecycleHelper(mActivity, new Session.StatusCallback() {
			
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if(session.isOpened()) {
					facebookShare();
				}
			}
			
		});
		uiHelper.onCreate(savedInstanceState);
		
		initView();
	}
	
	public void setRootView(View rootView){
		this.rootView = rootView;
		initView();
	}
	
	public void setShareEntity(ShareEntity entity){
		this.mShareEn = entity;
	}
	
	public void setListener(ShareVewButtonListener listener){
		this.listener = listener;
	}
	
	public void showShareLayer(Context ctx, boolean show){
		if(checkViewIsOk()){
			if(show){
				viewDim.setVisibility(View.VISIBLE);
				ll_mainLayout.setVisibility(View.VISIBLE);
				Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.anim_popup_dismiss);
				viewDim.startAnimation(anim);
				int height = ll_mainLayout.getHeight();
				mover = ObjectAnimator.ofFloat(ll_mainLayout, "translationY", height, 0f);
		        mover.setDuration(animationDuration);
		        mover.start();
			}else{
				viewDim.setVisibility(View.GONE);
				ll_mainLayout.setVisibility(View.GONE);
				ll_mainLayout.clearAnimation();
			}
		}
	}
	
	public boolean isShowing(){
		return viewDim.getVisibility() == View.VISIBLE;
	}
	
	private boolean checkViewIsOk(){
		if(rootView == null){
			LogUtil.i("ShareView", "Error! rootView is null");
			return false;
		}
		if(ll_mainLayout == null){
			LogUtil.i("ShareView", "Error! ll_mainLayout is null");
			return false;
		}
		if(tv_Share_Facebook == null){
			LogUtil.i("ShareView", "Error! tv_Share_Facebook is null");
			return false;
		}
		if(tv_Share_Friends == null){
			LogUtil.i("ShareView", "Error! tv_Share_Friends is null");
			return false;
		}
		if(tv_Share_Wechat == null){
			LogUtil.i("ShareView", "Error! tv_Share_Wechat is null");
			return false;
		}
		if(tv_Share_WhatsApp == null){
			LogUtil.i("ShareView", "Error! tv_Share_WhatsApp is null");
			return false;
		}
		if(tv_Share_Weibo == null){
			LogUtil.i("ShareView", "Error! tv_Share_Weibo is null");
			return false;
		}
		if(tv_Share_Line == null){
			LogUtil.i("ShareView", "Error! tv_Share_Line is null");
			return false;
		}
		return true;
	}
	
	private void initView(){
		viewDim = rootView.findViewById(R.id.share_view_dismiss);
		viewDim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showShareLayer(null, false);
				if(listener != null){
					listener.onClick_Dismiss();
				}
			}
		});
		ll_mainLayout = rootView.findViewById(R.id.share_view_ll_show_main);
		ll_mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
		tv_Share_QQ = rootView.findViewById(R.id.share_view_tv_share_QQ);
		tv_Share_QQ.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_QQ();
				}else {
					qqShare();
				}
			}
		});
		tv_Share_Friends = rootView.findViewById(R.id.share_view_tv_share_Friends);
		tv_Share_Friends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Friends();
				}else {
					wechatShare(true);
				}
			}
		});
		tv_Share_Wechat = rootView.findViewById(R.id.share_view_tv_share_Wechat);
		tv_Share_Wechat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Wechat();
				}else {
					wechatShare(false);
				}
			}
		});
		tv_Share_Weibo = rootView.findViewById(R.id.share_view_tv_share_Weibo);
		tv_Share_Weibo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Weibo();
				}else {
					weiboShare1();
				}
			}
		});
		tv_Share_Facebook = rootView.findViewById(R.id.share_view_tv_share_Facebook);
		tv_Share_Facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Facebook();
				}else {
					facebookShare();
				}
			}
		});
		tv_Share_WhatsApp = rootView.findViewById(R.id.share_view_tv_share_WhatsApp);
		tv_Share_WhatsApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_WhatsApp();
				}else {
					whatsAppShare();
				}
			}
		});
		tv_Share_Line = rootView.findViewById(R.id.share_view_tv_share_Line);
		tv_Share_Line.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Line();
				}else {
					lineShare();
				}
			}
		});
		tv_Share_Copy = rootView.findViewById(R.id.share_view_tv_share_Copy);
		tv_Share_Copy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick_Share_Copy();
				}else {
					urlCopy();
				}
			}
		});
	}

	private void qqShare() {
		if (mShareEn != null) {
			showShareLayer(mContext, false);
			Bundle params = new Bundle();
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
			params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareEn.getTitle());
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareEn.getText());
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareEn.getUrl());
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareEn.getImageUrl());
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getResources().getString(R.string.app_name));
			mTencent.shareToQQ(mActivity, params, qqShareListener);
		}else {
			showEntityError();
		}
	}
	
	/**
     * QQ登录、快速支付登录、应用分享、应用邀请等回调接口
     */
	IUiListener qqShareListener = new IUiListener() {
		
		@Override
		public void onComplete(Object response) {
			showShareSuccess();
		}

		@Override
		public void onError(UiError e) {
			showShareError();
		}
		
		@Override
		public void onCancel() {
			showShareCancel();
	    }
		
	};

	private void wechatShare(boolean isFriends){
		if(!DeviceUtil.checkAppInstalled(mContext, "com.tencent.mm")){ //检测是否安装微信APP
			CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_no_wechat), 1000);
			return;
		}
		if (mShareEn != null) {
			showShareLayer(mContext, false);
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = mShareEn.getUrl();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = mShareEn.getTitle();
			msg.description = mShareEn.getText();
			Bitmap bitmap = BitmapFactory.decodeFile(mShareEn.getImagePath());
			if (bitmap != null) {
				bitmap = BitmapUtil.getBitmap(bitmap, 80, 80);
			}
			msg.thumbData = WXShareUtil.bmpToByteArray(bitmap, false);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			if(isFriends){
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
			}else{
				req.scene = SendMessageToWX.Req.WXSceneSession;
			}
			mWXApi.sendReq(req);
			AppApplication.isWXShare = true; //标记为微信分享
		}else {
			showEntityError();
		}
	}
    
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	private void weiboShare1(){
		showShareLayer(mContext, false);
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		if (mAccessToken != null  && mAccessToken.isSessionValid()) {
			weiboShare2();
		}else {
			mAuthInfo = new AuthInfo(mContext, WB_APP_ID, WB_REDIRECT_URL, WB_SCOPE);
			mSsoHandler = new SsoHandler(mActivity, mAuthInfo);
			// SSO 授权, 仅客户端
			mSsoHandler.authorize(new WeiboAuthListener() {
				@Override
				public void onComplete(Bundle values) {
					// 从 Bundle 中解析 Token
					mAccessToken = Oauth2AccessToken.parseAccessToken(values);
					if (mAccessToken != null && mAccessToken.isSessionValid()) {
						// 保存 Token 到 SharedPreferences
						AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
						weiboShare2();
					} else {
						// 以下几种情况，您会收到 Code：
						// 1. 当您未在平台上注册的应用程序的包名与签名时；
						// 2. 当您注册的应用程序包名与签名不正确时；
						// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
						showAuthFail();
					}
				}

				@Override
				public void onCancel() {
					showShareCancel();
				}

				@Override
				public void onWeiboException(WeiboException e) {
					ExceptionUtil.handle(mContext, e);
					showAuthFail();
				}
			});
		}
	}
	
	private void weiboShare2(){
		if (mShareEn != null) {
			showShareLayer(mContext, false);
			// 创建微博分享接口实例
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, WB_APP_ID);
			if (mWeiboShareAPI == null) return;
			
			// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
			// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
			// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
			mWeiboShareAPI.registerApp();
			
			// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
			// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
			// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
			// 失败返回 false，不调用上述回调
			
			// 1. 初始化微博的分享消息
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			TextObject textObject = new TextObject();
			if(!TextUtils.isEmpty(mShareEn.getUrl())) {
				textObject.text = mShareEn.getTitle()+" "+mShareEn.getText()+" "+mShareEn.getUrl();
			}else{
				textObject.text = mShareEn.getText();
			}
			weiboMessage.textObject = textObject;
			ImageObject imageObject = new ImageObject();
			Bitmap bitmap = BitmapFactory.decodeFile(mShareEn.getImagePath());
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
			}
			imageObject.setImageObject(bitmap);
			weiboMessage.imageObject = imageObject;
			
			// 2. 初始化从第三方到微博的消息请求
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			// 用transaction唯一标识一个请求
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;
			
			// 3. 发送请求消息到微博，唤起微博分享界面
			Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mContext.getApplicationContext());
			String token = "";
			if (accessToken != null) {
				token = accessToken.getToken();
			}
			mWeiboShareAPI.sendRequest(mActivity, request, mAuthInfo, token, new WeiboAuthListener() {
				@Override
				public void onWeiboException( WeiboException arg0 ) {
					ExceptionUtil.handle(mContext, arg0);
				}
				
				@Override
				public void onComplete( Bundle bundle ) {
					Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
					AccessTokenKeeper.writeAccessToken(mContext, newToken);
				}
				
				@Override
				public void onCancel() {
					showShareCancel();
				}
			});
		}else {
			showEntityError();
		}
	}

	private void facebookShare() {
		// 先判断设备上是否已经安装了Facebook客户端，如果没有则提示用户安装客户端后才能进行分享。
		if (FacebookDialog.canPresentShareDialog(mContext, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			if (mShareEn != null) {
				showShareLayer(mContext, false);
				FacebookDialog.ShareDialogBuilder mBuilder = new FacebookDialog.ShareDialogBuilder(mActivity);
				mBuilder.setCaption(mContext.getString(R.string.app_name));
				mBuilder.setApplicationName(mContext.getString(R.string.app_name));
				if(!TextUtils.isEmpty(mShareEn.getTitle())){
					mBuilder.setName(mShareEn.getTitle());
				}
				if(!TextUtils.isEmpty(mShareEn.getText())){
					mBuilder.setDescription(mShareEn.getText());
				}
				if(!TextUtils.isEmpty(mShareEn.getUrl())){
					mBuilder.setLink(mShareEn.getUrl());
				}
				FacebookDialog shareDialog = mBuilder.build();
				uiHelper.trackPendingDialogCall(shareDialog.present());
			}else {
				showEntityError();
			}
		} else {
			CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_no_facebook), 1000);
		}
	}
	
	private void whatsAppShare() {
		if(!DeviceUtil.checkAppInstalled(mContext, "com.whatsapp")){ //检测是否安装WhatsApp
			CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_no_whatsapp), 1000);
			return;
		}
		if (mShareEn != null) {
			showShareLayer(mContext, false);
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.setPackage("com.whatsapp");
			sendIntent.putExtra(Intent.EXTRA_TEXT, mShareEn.getTitle() + "\n" + mShareEn.getText() + "\n" + mShareEn.getUrl());
			sendIntent.setType("text/plain");
			mContext.startActivity(sendIntent);
		}else {
			showEntityError();
		}
	}

	private void lineShare() {
		if(!DeviceUtil.checkAppInstalled(mContext, "jp.naver.line.android")){ //检测是否安装Line
			CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_no_line), 1000);
			return;
		}
		if (mShareEn != null) {
			showShareLayer(mContext, false);
			String share_Msg_For_Line = mShareEn.getTitle() + "\n" + mShareEn.getText() + "\n" + mShareEn.getUrl();
			try {
                share_Msg_For_Line = URLEncoder.encode(share_Msg_For_Line, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                ExceptionUtil.handle(mContext, e);
            }
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("line://msg/text/" + share_Msg_For_Line)));
		}else {
			showEntityError();
		}
	}

	@SuppressWarnings("deprecation")
	private void urlCopy() {
		if (mShareEn != null) {
			ClipboardManager clip = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(mShareEn.getUrl()); // Copy link
			CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_copy_link_ok), 2000);
		}else {
			showEntityError();
		}
	}
	
	/**
	 * 分享成功提示
	 */
	private void showShareSuccess() {
		CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_success), 1000);
	}

	/**
	 * 分享出错提示
	 */
	private void showShareError() {
		CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_error), 1000);
	}

	/**
	 * 用户取消了分享操作
	 */
	private void showShareCancel() {
		CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_cancel), 1000);
	}
	
	/**
	 * 分享参数出错提示
	 */
	private void showEntityError() {
		CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_entity_error), 1000);
	}

	/**
	 * 提示授权失败
	 */
	private void showAuthFail() {
		CommonTools.showToast(mContext, mContext.getString(R.string.share_msg_error_license), 1000);
	}
	
	/**
	 * 在Activity中的onActivityResult调用此方法
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// QQ
		if (requestCode == Constants.REQUEST_QQ_SHARE) {
			Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
		}
		// WB
		if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
		// FB
		if (uiHelper != null) {
			uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
				
				@Override
				public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
					LogUtil.i("ShareView", String.format("Error: %s", error.toString()));
				}
				
				@Override
				public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
					String result = data.getString("com.facebook.platform.extra.COMPLETION_GESTURE");//cancel取消 post成功
					if ("cancel".equals(result)) {
						showShareCancel();
					}else if ("post".equals(result)) {
						showShareSuccess();
					}
				}
				
			});
		}
	}
	
	/**
	 * 在Activity中的onNewIntent调用此方法
	 */
	public void onNewIntent(Intent intent, Response response, IWXAPIEventHandler handler){
		// WX
		if(mWXApi != null){
			mWXApi.handleIntent(intent, handler);
		}
		// WB
		if(mWeiboShareAPI != null){ 
			mWeiboShareAPI.handleWeiboResponse(intent, response);
		}
	}
	
	/**
	 * 在Activity中的onResume调用此方法
	 */
	public void onResume(){
		Session session = Session.getActiveSession();
		if(uiHelper != null && session != null && session.isOpened()){
			uiHelper.onResume();
		}
	}
	
	/**
	 * 在Activity中的onPause调用此方法
	 */
	public void onPause(){
		if (uiHelper != null) {
			uiHelper.onPause();
		}
	}
	
	/**
	 * 在Activity中的onDestroy调用此方法
	 */
	public void onDestroy(){
		if (uiHelper != null) {
			uiHelper.onDestroy();
		}
	}
	
	/**
	 * 在Activity中的onSaveInstanceState调用此方法
	 */
	public void onSaveInstanceState(Bundle outState){
		if (uiHelper != null) {
			uiHelper.onSaveInstanceState(outState);
		}
	}
	
	public static interface ShareVewButtonListener{
		public void onClick_Dismiss();
		public void onClick_Share_QQ();
		public void onClick_Share_Facebook();
		public void onClick_Share_Friends();
		public void onClick_Share_Wechat();
		public void onClick_Share_WhatsApp();
		public void onClick_Share_Weibo();
		public void onClick_Share_Line();
		public void onClick_Share_Copy();
	}
	
}
