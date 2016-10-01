package com.spshop.stylistpark.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.login.LoginActivity;
import com.spshop.stylistpark.activity.profile.ChildFragmentFive;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.dialog.LoadDialog;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.share.ShareView;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有Activity的父类
 */
@SuppressLint("HandlerLeak")
public  class BaseActivity extends FragmentActivity implements OnDataListener,
		IWeiboHandler.Response, IWXAPIEventHandler {

	public static final String TAG = BaseActivity.class.getSimpleName();
	
	protected Context mContext;
	protected AsyncTaskManager atm;
	protected SharedPreferences shared;
	protected Editor editor;
	protected DialogManager dm;
	protected IWXAPI api;
	protected Boolean isInitShare = false;
	protected ShareView mShareView;
	protected DecimalFormat decimalFormat;
	private LinearLayout ll_head;

	private ImageView iv_left, iv_title_logo;
	private TextView tv_title;
	private Button btn_right;
	private ViewFlipper mLayoutBase;
	private Animation inAnim, outAnim;
	protected String currStr;

	protected boolean headStatus = false;
	protected int width, height, statusHeight;
	protected Animation headGONE, headVISIBLE;
	protected ServiceContext sc = ServiceContext.getServiceContext();

	public static final int DIALOG_CONFIRM_CLICK = 456; //全局对话框“确定”
	public static final int DIALOG_CANCEL_CLICK = 887; //全局对话框“取消”

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_base);

		LogUtil.i(TAG, "onCreate()");
		AppManager.getInstance().addActivity(this);

		mContext = this;
		shared = AppApplication.getSharedPreferences();
		editor = shared.edit();
		editor.apply();
		currStr = LangCurrTools.getCurrencyValue();
		decimalFormat = new DecimalFormat("0.00");
		dm = DialogManager.getInstance(mContext);
		atm = AsyncTaskManager.getInstance(mContext);
		api = WXAPIFactory.createWXAPI(mContext, AppConfig.WX_APP_ID);
		api.registerApp(AppConfig.WX_APP_ID);
		
		// 获取屏幕配置
		width = AppApplication.screenWidth;
		height = AppApplication.screenHeight;
		statusHeight = AppApplication.statusHeight;

		// 设置App字体不随系统字体变化
		AppApplication.initDisplayMetrics();

		// 推送服务统计应用启动数据
		AppApplication.onPushAppStartData();
		
		findViewById();
		initView();

		if (isInitShare) {
			try { //初始化ShareView
				View view = getLayoutInflater().inflate(R.layout.popup_share_view, (ViewGroup) findViewById(R.id.base_fl_main));
				mShareView = new ShareView(savedInstanceState, mContext, this, view, null);
				mShareView.showShareLayer(false);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}
	
	private void findViewById() {
		ll_head = (LinearLayout) findViewById(R.id.top_bar_head_ll_main);
		iv_left = (ImageView) findViewById(R.id.top_bar_left);
		iv_title_logo = (ImageView) findViewById(R.id.top_bar_title_logo);
		tv_title = (TextView) findViewById(R.id.top_bar_title);
		btn_right = (Button) findViewById(R.id.top_bar_right);
		mLayoutBase = (ViewFlipper) super.findViewById(R.id.base_ll_container);
	}

	@SuppressWarnings("static-access")
	private void initView() {
		iv_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OnListenerLeft();
			}
		});
		btn_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OnListenerRight();
			}
		});
		
		inAnim = new AnimationUtils().loadAnimation(mContext, R.anim.in_from_right);
		outAnim = new AnimationUtils().loadAnimation(mContext, R.anim.out_to_left);
	}
	
	/**
	 * 设置头部是否可见
	 */
	public void setHeadVisibility(int visibility) {
		switch (visibility) {
		case View.VISIBLE:
			if (ll_head.getVisibility() == View.GONE) {
				ll_head.clearAnimation();
				ll_head.startAnimation(inAnim);
			}
			break;
		case View.GONE:
			if (ll_head.getVisibility() == View.VISIBLE) {
				ll_head.clearAnimation();
				ll_head.startAnimation(outAnim);
			}
			break;
		}
		ll_head.setVisibility(visibility);
	}
	
	/**
	 * 设置头部View背景色
	 */
	public void setHeadBackground(int color){
		ll_head.setBackgroundColor(color);
	}
	
	/**
	 * 获取头部View的高度
	 */
	public int getHeadHeight(){
		return ll_head.getHeight();
	}

	/**
	 * 设置左边按钮是否可见
	 */
	public void setBtnLeftGone(int visibility){
		iv_left.setVisibility(visibility);
	}
	
	/**
	 * 设置右边按钮是否可见
	 */
	public void setBtnRightGone(int visibility){
		btn_right.setVisibility(visibility);
	}

	/**
	 * 设置标题（文本资源Id）
	 */
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	/**
	 * 设置标题（文本对象）
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	/**
	 * 设置标题Logo(资源Id)
	 */
	public void setTitleLogo(int drawableId){
		Drawable drawable = getResources().getDrawable(drawableId);
		/*rightDrawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		tv_title.setCompoundDrawables(null, null, drawable, null);*/
		tv_title.setVisibility(View.GONE);
		iv_title_logo.setVisibility(View.VISIBLE);
		iv_title_logo.setImageDrawable(drawable);
	}

	/**
	 * 设置标题Logo(资源路径)
	 */
	public void setTitleLogo(DisplayImageOptions options, String path){
		if (options != null && !StringUtil.isNull(path)) {
			tv_title.setVisibility(View.GONE);
			iv_title_logo.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(path, iv_title_logo, options);
		}
	}

	/**
	 * 设置右边按钮背景图片资源对象
	 */
	@SuppressLint("NewApi")
	public void setBtnRight(Drawable btnRight) {
		setBtnRightGone(View.VISIBLE);
		btn_right.setBackground(btnRight);
	}
	
	/**
	 * 设置右边按钮背景图片资源Id
	 */
	public void setBtnRight(int drawableId){
		setBtnRightGone(View.VISIBLE);
		btn_right.setBackgroundResource(drawableId);
	}
	
	/**
	 * 设置右边按钮显示文本
	 */
	public void setBtnRight(String text){
		setBtnRightGone(View.VISIBLE);
		btn_right.setText(text);
	}

	@Override
	public void onBackPressed() {
		if (mShareView != null && mShareView.isShowing()) {
			mShareView.showShareLayer(false);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 左键键监听执行方法，让子类重写该方法
	 */
	public void OnListenerLeft(){
		if (mShareView != null && mShareView.isShowing()) {
			mShareView.showShareLayer(false);
		} else {
			finish();
		}
	}

	/**
	 * 右键监听执行方法，让子类重写该方法
	 */
	public void OnListenerRight(){
			
	}
	
	public void request(int requsetCode) {
		atm.request(requsetCode, this);
	}

	public void cancelRequest(int requsetCode) {
		atm.cancelRequest(requsetCode);
	}

	public void cancelRequest() {
		atm.cancelRequest();
	}
	
	@Override
	public void setContentView(View view) {
		if (mLayoutBase.getChildCount() > 1) {
			mLayoutBase.removeViewAt(1);
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
		mLayoutBase.addView(view, lp);
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = LayoutInflater.from(this).inflate(layoutResID, null);
		setContentView(view);
	}
	
	protected void openLoginActivity(String rootPage){
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra("rootPage", rootPage);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity
	 * 
	 * @param pAction
	 */
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	/**
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mShareView != null) {
			mShareView.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (mShareView != null) {
			mShareView.onNewIntent(intent, this, this);
		}
		super.onNewIntent(intent);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp arg0) {

	}

	@Override
	public void onResponse(BaseResponse arg0) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume()");
		// 设置App字体不随系统字体变化
		AppApplication.initDisplayMetrics();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause()");
		if (dm != null) {
			dm.clearInstance();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy()");
		headGONE = null;
		headVISIBLE = null;
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		return null;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null && ((BaseEntity) result).getErrCode() == AppConfig.ERROR_CODE_LOGOUT) { //登录失效
			showTimeOutDialog(TAG);
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		stopAnimation();
		if (result == null) {
			showErrorDialog(R.string.toast_server_busy);
			return;
		}
		showErrorDialog(String.valueOf(result));
	}

	/**
	 * 显示缓冲动画
	 */
	protected void startAnimation() {
		LoadDialog.show(mContext);
	}

	/**
	 * 停止缓冲动画
	 */
	protected void stopAnimation() {
		LoadDialog.hidden();
	}
	
	/**
	 * 弹出登入超时对话框
	 */
	protected void showTimeOutDialog(final String rootPage) {
		AppApplication.AppLogout(true);
		showErrorDialog(getString(R.string.login_timeout), true, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				openLoginActivity(rootPage);
			}
		});
	}

	/**
	 * 分享参数出错提示
	 */
	protected void showShareError() {
		CommonTools.showToast(getString(R.string.share_msg_entity_error), 1000);
	}
	
	/**
	 * 加载数据出错提示
	 */
	protected void showServerBusy() {
		showErrorDialog(R.string.toast_server_busy);
	}

	protected void showErrorDialog(int resId) {
		showErrorDialog(getString(resId));
	}

	protected void showErrorDialog(String content) {
		showErrorDialog(content, true, null);
	}
	
	protected void showErrorDialog(String content, boolean isVanish, final Handler handler) {
		content = (TextUtils.isEmpty(content)) ? getString(R.string.dialog_error_msg) : content;
		dm.showOneBtnDialog(content, width * 2/3, true, isVanish, handler, null);
	}
	
    protected void showConfirmDialog(int contentResId, String positiveBtnStr, String negativeBtnStr,
					boolean isCenter, boolean isVanish, final Handler handler) {
		showConfirmDialog(getString(contentResId), positiveBtnStr, negativeBtnStr, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String content, String positiveBtnStr, String negativeBtnStr,
					boolean isCenter, boolean isVanish, final Handler handler) {
		showConfirmDialog(null, content, positiveBtnStr, negativeBtnStr, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String title, String content, String positiveBtnStr, String negativeBtnStr,
					boolean isCenter, boolean isVanish, final Handler handler) {
		positiveBtnStr = (positiveBtnStr == null) ? getString(R.string.confirm) : positiveBtnStr;
		negativeBtnStr = (negativeBtnStr == null) ? getString(R.string.cancel) : negativeBtnStr;
		showConfirmDialog(null, content, positiveBtnStr, negativeBtnStr, width * 2/3, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String title, String content, String positiveBtnStr, String negativeBtnStr,
					int width, boolean isCenter, boolean isVanish, final Handler handler) {
		positiveBtnStr = (positiveBtnStr == null) ? getString(R.string.confirm) : positiveBtnStr;
		negativeBtnStr = (negativeBtnStr == null) ? getString(R.string.cancel) : negativeBtnStr;
		dm.showTwoBtnDialog(null, content, positiveBtnStr, negativeBtnStr, width, isCenter, isVanish, handler);
	}

	protected void showListDialog(int contentResId, CharSequence[] items, boolean isCenter, final Handler handler) {
		showListDialog(contentResId, items, width * 2/3, isCenter, handler);
	}

	protected void showListDialog(int contentResId, CharSequence[] items, int width, boolean isCenter, final Handler handler) {
		showListDialog(getString(contentResId), items, width, isCenter, handler);
	}

	protected void showListDialog(String content, CharSequence[] items, boolean isCenter, final Handler handler) {
		showListDialog(content, items, width * 2/3, isCenter, handler);
	}

	protected void showListDialog(String content, CharSequence[] items, int width, boolean isCenter, final Handler handler) {
		dm.showListItemDialog(content, items, width, isCenter, handler);
	}

	/**
	 * 数据刷新函数
	 */
	public static List<BaseEntity> updNewEntity(int newTotal, int oldTotal, List<? extends BaseEntity> newDatas,
							  List<? extends BaseEntity> oldDatas, ArrayMap<String, Boolean> oldMap) {
		if (oldDatas == null || newDatas == null || oldMap == null) return null;
		if (oldTotal < newTotal) {
			List<BaseEntity> newLists = new ArrayList<BaseEntity>();
			BaseEntity newEn, oldEn;
			String dataId;
			int newCount = newTotal - oldTotal;
			if (newCount > newDatas.size()) {
				newCount = newDatas.size();
			}
			for (int i = 0; i < newCount; i++) {
				newEn = newDatas.get(i);
				if (newEn != null) {
					dataId = newEn.getEntityId();
					if (!StringUtil.isNull(dataId) && !oldMap.containsKey(dataId)) {
						// 添加至顶层
						newLists.add(newEn);
						oldMap.put(dataId, true);
						// 移除最底层
						if (oldDatas.size() >= 1) {
							oldEn = oldDatas.remove(oldDatas.size()-1);
							if (oldEn != null && oldMap.containsKey(oldEn.getEntityId())) {
								oldMap.remove(oldEn.getEntityId());
							}
						}
					}
				}
			}
			newLists.addAll(oldDatas);
			return newLists;
		}
		return null;
	}

	/**
	 * 数据去重函数
	 */
	public static List<BaseEntity> addNewEntity(List<? extends BaseEntity> oldDatas,
						List<? extends BaseEntity> newDatas, ArrayMap<String, Boolean> oldMap) {
		if (oldDatas == null || newDatas == null || oldMap == null) return null;
		List<BaseEntity> newLists = new ArrayList<BaseEntity>();
		newLists.addAll(oldDatas);
		BaseEntity newEn;
		String dataId;
		for (int i = 0; i < newDatas.size(); i++) {
			newEn = newDatas.get(i);
			if (newEn != null) {
				dataId = newEn.getEntityId();
				if (!StringUtil.isNull(dataId) && !oldMap.containsKey(dataId)) {
					newLists.add(newEn);
					oldMap.put(dataId, true);
				}
			}
		}
		return newLists;
	}

	/**
	 * 判定是否停止加载更多
	 */
	public static boolean isStopLoadMore(int showCount, int countTotal, int pageSize) {
		showPageNum(showCount, countTotal, pageSize);
		return showCount > 0 && showCount == countTotal;
	}

	/**
	 * 提示当前页数
	 */
	public static void showPageNum(int showCount, int countTotal, int pageSize) {
		if (pageSize <= 0) return;
		int page_num = showCount / pageSize;
		if (showCount % pageSize > 0) {
			page_num++;
		}
		int page_total = countTotal / pageSize;
		if (countTotal % pageSize > 0) {
			page_total++;
		}
		CommonTools.showPageNum(page_num + "/" + page_total, 1000);
	}

	/**
	 * 创建向上缩进、向下拉出的动画效果
	 */
	protected void createAnimation(final View animView) {
		if (headGONE == null) {
			headGONE = AnimationUtils.loadAnimation(mContext, R.anim.out_to_top);
			headGONE.setDuration(1000);
			headGONE.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					animView.setVisibility(View.GONE);
					headStatus = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {

				}
			});
		}
		if (headVISIBLE == null) {
			headVISIBLE = AnimationUtils.loadAnimation(mContext, R.anim.in_from_top);
			headVISIBLE.setDuration(0);
			headVISIBLE.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					animView.setVisibility(View.VISIBLE);
					headStatus = false;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {

				}
			});
		}
	}

	public static void updateActivityData(int type) {
		switch (type) {
			case 5: //刷新个人页数据
				if (ChildFragmentFive.instance != null) {
					ChildFragmentFive.instance.isUpdate = true;
				}
				break;
		}
	}

}