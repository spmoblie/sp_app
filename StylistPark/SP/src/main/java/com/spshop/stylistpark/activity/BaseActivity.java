package com.spshop.stylistpark.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.spshop.stylistpark.activity.cart.CartActivity;
import com.spshop.stylistpark.activity.cart.PostOrderActivity;
import com.spshop.stylistpark.activity.common.OnlineServiceActivity;
import com.spshop.stylistpark.activity.common.ScreenImageActivity;
import com.spshop.stylistpark.activity.common.ScreenVideoActivity;
import com.spshop.stylistpark.activity.common.ShowListActivity;
import com.spshop.stylistpark.activity.common.ShowListHeadActivity;
import com.spshop.stylistpark.activity.home.ChildFragmentOne;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.activity.home.ProductListActivity;
import com.spshop.stylistpark.activity.login.LoginActivity;
import com.spshop.stylistpark.activity.mine.AccountBalanceActivity;
import com.spshop.stylistpark.activity.mine.ChildFragmentFive;
import com.spshop.stylistpark.activity.mine.MyAddressActivity;
import com.spshop.stylistpark.activity.mine.OrderListActivity;
import com.spshop.stylistpark.adapter.AddCartPopupListAdapter;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.dialog.LoadDialog;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.GoodsCartEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductAttrEntity;
import com.spshop.stylistpark.entity.ShareEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.share.ShareView;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.OptionsManager;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.ScrollViewListView;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.spshop.stylistpark.AppApplication.isStartLoop;
import static com.spshop.stylistpark.AppApplication.mScale;
import static com.spshop.stylistpark.AppApplication.screenWidth;

/**
 * 所有Activity的父类
 */
@SuppressLint("HandlerLeak")
public  class BaseActivity extends FragmentActivity implements OnDataListener,
		IWeiboHandler.Response, IWXAPIEventHandler {

	public static final String TAG = BaseActivity.class.getSimpleName();
	public static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

	protected Context mContext;
	protected AsyncTaskManager atm;
	protected SharedPreferences shared;
	protected Editor editor;
	protected DialogManager dm;
	protected IWXAPI api;
	protected Boolean isInitShare = false;
	protected DecimalFormat decimalFormat;
	protected DisplayImageOptions goodsOptions, defaultOptions;

	private ShareView mShareView;
	private FrameLayout fl_main;
	private LinearLayout ll_head;
	private ImageView iv_left, iv_title_logo;
	private TextView tv_title;
	private Button btn_right;
	private ViewFlipper mLayoutBase;
	private Animation inAnim, outAnim;
	private ACountDownTimer acdt;

	// 底部购物栏
	private LinearLayout ll_bottom_main;
	private ImageView iv_line_2;
	private TextView tv_call, tv_collection, tv_cart, tv_cart_total, tv_add_cart;
	private boolean isAdd = true;
	protected boolean isColl = false;

	// 购物浮层组件
	private int dialogWidth;
	private int goodsId = 0;
	private int skuNum = 1;
	private int buyNumber = 1;
	private int selectId_1, selectId_2, attrNum;
	private double mathPrice;
	private boolean isNext = false;
	private ProductAttrEntity attrEn;
	private LinearLayout ll_cart_show, ll_state_show;
	private ImageView iv_goods_img, iv_num_minus, iv_num_add;
	private View cartPopupView, statePopupView;
	private PopupWindow cartPopupWindow, statePopupWindow;
	private Animation popupAnimShow, popupAnimGone;

	protected Animation headGONE, headVISIBLE;
	protected boolean headStatus = false;
	protected String currStr;
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
		dialogWidth = screenWidth * 2/3;
		decimalFormat = new DecimalFormat("0.00");
		dm = DialogManager.getInstance(mContext);
		atm = AsyncTaskManager.getInstance(mContext);
		goodsOptions = OptionsManager.getInstance().getGoodsOptions();
		defaultOptions = OptionsManager.getInstance().getDefaultOptions();
		api = WXAPIFactory.createWXAPI(mContext, AppConfig.WX_APP_ID);
		api.registerApp(AppConfig.WX_APP_ID);

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
		fl_main = (FrameLayout) findViewById(R.id.base_fl_main);
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
	 * 设置底部购物栏是否可见
	 */
	protected void setBottomBarVisibility(int visibility) {
		ll_bottom_main = (LinearLayout) findViewById(R.id.bottom_bar_base_ll_main);
		switch (visibility) {
			case View.VISIBLE:
				tv_call = (TextView) findViewById(R.id.bottom_bar_base_tv_call);
				tv_call.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, OnlineServiceActivity.class);
						intent.putExtra("title", getString(R.string.mine_call));
						intent.putExtra("lodUrl", AppConfig.API_CUSTOMER_SERVICE);
						startActivity(intent);
					}
				});
				tv_collection = (TextView) findViewById(R.id.bottom_bar_base_tv_collection);
				tv_collection.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!UserManager.getInstance().checkIsLogined()) {
							openLoginActivity();
							return;
						}
						postCollectionProduct();
					}
				});
				tv_cart = (TextView) findViewById(R.id.bottom_bar_base_tv_cart);
				tv_cart.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!UserManager.getInstance().checkIsLogined()) {
							openLoginActivity();
							return;
						}
						startActivity(new Intent(mContext, CartActivity.class));
					}
				});
				iv_line_2 = (ImageView) findViewById(R.id.bottom_bar_base_iv_line_2);
				tv_cart_total = (TextView) findViewById(R.id.bottom_bar_base_tv_cart_total);
				tv_add_cart = (TextView) findViewById(R.id.bottom_bar_base_tv_add_cart);
				tv_add_cart.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!UserManager.getInstance().checkIsLogined()) {
							openLoginActivity();
							return;
						}
						if (!isAdd) return;
						requestProductAttrData();
					}
				});
				break;
		}
		ll_bottom_main.setVisibility(visibility);
	}

	/**
	 * 设置关注按钮是否可见
	 */
	protected void setCollectionVisibility(int visibility) {
		if (tv_collection == null || iv_line_2 == null) return;
		iv_line_2.setVisibility(visibility);
		tv_collection.setVisibility(visibility);
	}

	/**
	 * 设置商品不可加入购物车
	 */
	protected void setAddCartViewStatus(String showStr, boolean isAdd) {
		if (tv_add_cart == null) return;
		tv_add_cart.setText(showStr);
		this.isAdd = isAdd; //库存为“null”或“0”不可加入购物车
		if (!isAdd) {
			tv_add_cart.setBackgroundColor(getResources().getColor(R.color.debar_text_color));
		}
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
		LogUtil.i(TAG, "onResume()");
		// 设置App字体不随系统字体变化
		AppApplication.initDisplayMetrics();
		// 开启倒计时
		startLoop();
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause()");
		if (dm != null) {
			dm.clearInstance();
		}
		// 清除倒计时
		clearCountdown();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i(TAG, "onDestroy()");
		headGONE = null;
		headVISIBLE = null;
		super.onDestroy();
	}

	protected void openLoginActivity(){
		openLoginActivity(TAG);
	}

	protected void openLoginActivity(String rootPage){
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra("rootPage", rootPage);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	protected void openProductDetailActivity(int id){
		if (id == AppConfig.SP_JION_PROGRAM_ID) return; //虚拟商品
		Intent intent = new Intent(mContext, ProductDetailActivity.class);
		intent.putExtra("goodsId", id);
		startActivity(intent);
	}

	/**
	 * 提交关注商品请求
	 */
	protected void postCollectionProduct() {

	}

	/**
	 * 提交关注商品请求
	 */
	protected void postCollectionProduct(int id) {
		goodsId = id;
		request(AppConfig.REQUEST_SV_POST_COLLECITON_CODE);
	}

	/**
	 * 加载商品属性数据
	 */
	protected void requestProductAttrData() {

	}

	/**
	 * 加载商品属性数据
	 */
	protected void requestProductAttrData(int id) {
		if (goodsId == id && attrEn != null) {
			initCartPopup();
		} else {
			attrEn = null;
			goodsId = id;
			skuNum = 1;
			buyNumber = 1;
			selectId_1 = 0;
			selectId_2 = 0;
			isNext = false;
			cartPopupWindow = null;
			startAnimation();
			request(AppConfig.REQUEST_SV_GET_PRODUCT_ATTR_CODE);
		}
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=add_to_cart";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		String jsonStrValue;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_PRODUCT_ATTR_CODE:
				jsonObject.put("quick", "0");
				jsonObject.put("spec", jsonArray);
				jsonObject.put("goods_id", goodsId);
				jsonObject.put("number", "1");
				jsonObject.put("parent", "0");
				jsonStrValue = jsonObject.toString();

				params.add(new MyNameValuePair("goods", jsonStrValue));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_PRODUCT_ATTR_CODE, uri, params, HttpUtil.METHOD_POST);

			case AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE:
				if (selectId_1 > 0) {
					jsonArray.put(String.valueOf(selectId_1));
					if (selectId_2 > 0) {
						jsonArray.put(String.valueOf(selectId_2));
					}
				}
				jsonObject.put("quick", "1");
				jsonObject.put("spec", jsonArray);
				jsonObject.put("goods_id", goodsId);
				jsonObject.put("number", buyNumber);
				jsonObject.put("parent", "0");
				jsonStrValue = jsonObject.toString();

				params.add(new MyNameValuePair("goods", jsonStrValue));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE, uri, params, HttpUtil.METHOD_POST);

			case AppConfig.REQUEST_SV_POST_COLLECITON_CODE:
				uri = AppConfig.URL_COMMON_USER_URL + "?act=collect";
				params.add(new MyNameValuePair("id", String.valueOf(goodsId)));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_COLLECITON_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			if (((BaseEntity) result).getErrCode() == AppConfig.ERROR_CODE_LOGOUT) { //登录失效
				showTimeOutDialog();
			} else {
				switch (requestCode) {
					case AppConfig.REQUEST_SV_GET_PRODUCT_ATTR_CODE:
						stopAnimation();
						attrEn = (ProductAttrEntity) result;
						initCartPopup();
						/*ProductAttrEntity mainEn = (ProductAttrEntity) result;
						if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) { //直接购买
							int cartNumTotal = UserManager.getInstance().getCartTotal() + 1;
							updateCartTotal(cartNumTotal);
							startNumberAddAnim(1);
						} else if (mainEn.getErrCode() == 6) { //需要选择商品属性
							attrEn = mainEn;
							initCartPopup();
						}else {
							if (StringUtil.isNull(mainEn.getErrInfo())) {
								showServerBusy();
							}else {
								CommonTools.showToast(mainEn.getErrInfo(), 2000);
							}
						}*/
						break;
					case AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE:
						stopAnimation();
						catrPopupDismiss(); //关闭弹层
						GoodsCartEntity cartEn = (GoodsCartEntity) result;
						if (cartEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
							int cartNumTotal = cartEn.getGoodsTotal();
							updateCartTotal(cartNumTotal);
							startNumberAddAnim(buyNumber);
						}else {
							if (StringUtil.isNull(cartEn.getErrInfo())) {
								showServerBusy();
							}else {
								CommonTools.showToast(cartEn.getErrInfo(), 2000);
							}
						}
						break;
					case AppConfig.REQUEST_SV_POST_COLLECITON_CODE:
						BaseEntity baseEn = (BaseEntity) result;
						if (baseEn.getErrCode() == 0 || baseEn.getErrCode() == 1) {
							isColl = !isColl;
							changeCollectionStatus();
							updateActivityData(6);
							CommonTools.showToast(baseEn.getErrInfo(), 1000);
						}else {
							if (StringUtil.isNull(baseEn.getErrInfo())) {
								showServerBusy();
							}else {
								CommonTools.showToast(baseEn.getErrInfo(), 2000);
							}
						}
						break;
				}
			}
		}else {
			switch (requestCode) {
				case AppConfig.REQUEST_SV_GET_PRODUCT_ATTR_CODE:
				case AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE:
				case AppConfig.REQUEST_SV_POST_COLLECITON_CODE:
					showServerBusy();
					break;
			}
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
	 * 切换收藏此商品的状态
	 */
	protected void changeCollectionStatus() {
		if (tv_collection == null) return;
		if (isColl) {
			tv_collection.setSelected(true);
			tv_collection.setTextColor(getResources().getColor(R.color.tv_color_status));
		}else {
			tv_collection.setSelected(false);
			tv_collection.setTextColor(getResources().getColor(R.color.label_text_color));
		}
	}

	/**
	 * 弹出声明浮层
	 */
	protected void initStatePopup(String contentStr) {
		if (statePopupWindow == null) {
			statePopupView = LayoutInflater.from(mContext).inflate(R.layout.popup_state_show, null);
			RelativeLayout rl_finish = (RelativeLayout) statePopupView.findViewById(R.id.state_show_rl_finish);
			rl_finish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					statePopupDismiss();
				}
			});
			popupAnimShow = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
			popupAnimGone = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
			ll_state_show = (LinearLayout) statePopupView.findViewById(R.id.state_show_ll_show);
			ll_state_show.startAnimation(popupAnimShow);

			final TextView tv_popup_content = (TextView) statePopupView.findViewById(R.id.state_show_tv_show_content);
			tv_popup_content.setText(contentStr);

			statePopupWindow = new PopupWindow(statePopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			statePopupWindow.setFocusable(true);
			statePopupWindow.update();
			statePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			statePopupWindow.setOutsideTouchable(true);
			statePopupWindow.showAtLocation(statePopupView, Gravity.BOTTOM, 0, 0);
		}else {
			ll_state_show.startAnimation(popupAnimShow);
			statePopupWindow.showAtLocation(statePopupView, Gravity.BOTTOM, 0, 0);
		}
	}

	/**
	 * 关闭声明浮层
	 */
	private void statePopupDismiss() {
		ll_state_show.startAnimation(popupAnimGone);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				statePopupWindow.dismiss();
			}
		}, 500);
	}

	@SuppressWarnings("deprecation")
	private void initCartPopup() {
		if (attrEn == null) return;
		goodsId = attrEn.getGoodsId();
		mathPrice = attrEn.getComputePrice();
		String attrNameStr = getSelectShowStr(attrEn);
		if (cartPopupWindow == null) {
			cartPopupView = LayoutInflater.from(mContext).inflate(R.layout.popup_add_cart_select, null);
			RelativeLayout rl_finish = (RelativeLayout) cartPopupView.findViewById(R.id.popup_add_cart_rl_finish);
			rl_finish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					catrPopupDismiss();
				}
			});
			popupAnimShow = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
			popupAnimGone = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
			ll_cart_show = (LinearLayout) cartPopupView.findViewById(R.id.popup_add_cart_ll_show);
			ll_cart_show.startAnimation(popupAnimShow);

			final TextView tv_popup_number = (TextView) cartPopupView.findViewById(R.id.popup_add_cart_tv_number);
			tv_popup_number.setText(String.valueOf(buyNumber));
			final TextView tv_popup_curr = (TextView) cartPopupView.findViewById(R.id.popup_add_cart_tv_curr);
			tv_popup_curr.setText(currStr);
			final TextView tv_popup_price = (TextView) cartPopupView.findViewById(R.id.popup_add_cart_tv_price);
			tv_popup_price.setText(decimalFormat.format(mathPrice));
			final TextView tv_popup_prompt = (TextView) cartPopupView.findViewById(R.id.popup_add_cart_tv_prompt);
			final TextView tv_popup_select = (TextView) cartPopupView.findViewById(R.id.popup_add_cart_tv_select);
			final TextView tv_popup_confirm = (TextView) cartPopupView.findViewById(R.id.popup_add_cart_tv_confirm);
			tv_popup_confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isNext) {
						startAnimation();
						request(AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE);
					} else {
						if (skuNum == 0) { //提示缺货
							CommonTools.showToast(getString(R.string.product_sku_null), 1000);
						}
					}
				}
			});

			RelativeLayout rl_num_minus = (RelativeLayout) cartPopupView.findViewById(R.id.popup_add_cart_rl_num_minus);
			rl_num_minus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (buyNumber > 1) {
						buyNumber--;
						if (buyNumber == 1) {
							iv_num_minus.setSelected(false); //不可-
						}
						if (buyNumber < skuNum) {
							iv_num_add.setSelected(true); //可+
						}
					}
					tv_popup_number.setText(String.valueOf(buyNumber));
				}
			});
			RelativeLayout rl_num_add = (RelativeLayout) cartPopupView.findViewById(R.id.popup_add_cart_rl_num_add);
			rl_num_add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (skuNum > 1) {
						if (buyNumber < skuNum) {
							buyNumber++;
							iv_num_minus.setSelected(true); //可-
							iv_num_add.setSelected(true); //可+
							tv_popup_number.setText(String.valueOf(buyNumber));
						} else {
							iv_num_add.setSelected(false); //不可+
						}
					} else {
						if (skuNum == 0) { //提示缺货
							CommonTools.showToast(getString(R.string.product_sku_null), 1000);
						}
					}
				}
			});

			iv_num_add = (ImageView) cartPopupView.findViewById(R.id.popup_add_cart_iv_num_add);
			iv_num_minus = (ImageView) cartPopupView.findViewById(R.id.popup_add_cart_iv_num_minus);
			iv_goods_img = (ImageView) cartPopupView.findViewById(R.id.popup_add_cart_iv_img);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + attrEn.getFristImgUrl(), iv_goods_img, goodsOptions);

			if (attrNum > 0) {
				ScrollViewListView svlv = (ScrollViewListView) cartPopupView.findViewById(R.id.popup_add_cart_svlv);
				AddCartPopupListAdapter.AddCartCallback apCallback = new AddCartPopupListAdapter.AddCartCallback() {

					@Override
					public void setOnClick(Object entity, int position, int num, double attrPrice,
										   int id1, int id2, String selectName, String selectImg) {
						// 图片替换
						if (!StringUtil.isNull(selectImg)) {
							ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + selectImg, iv_goods_img, goodsOptions);
						}else {
							ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + attrEn.getFristImgUrl(), iv_goods_img, goodsOptions);
						}
						// 刷新选择的属性名称
						tv_popup_select.setText(selectName);
						if (num == -1) {
							tv_popup_prompt.setText(getString(R.string.item_select_no));
							tv_popup_select.setTextColor(getResources().getColor(R.color.label_text_color));
						}else {
							tv_popup_prompt.setText(getString(R.string.item_select_ok));
							tv_popup_select.setTextColor(getResources().getColor(R.color.tv_color_status));
						}
						// 刷新商品价格及数量
						selectId_1 = id1;
						selectId_2 = id2;
						mathPrice = attrEn.getComputePrice() + attrPrice;
						tv_popup_curr.setText(currStr);
						tv_popup_price.setText(decimalFormat.format(mathPrice));
						skuNum = 1; //默认库存数量
						buyNumber = 1; //默认购买数量
						iv_num_add.setSelected(false); //不可+
						iv_num_minus.setSelected(false); //不可-
						if (num >= 0) {
							isNext = true;
							skuNum = num;
						}else {
							isNext = false;
						}
						if (skuNum > 1) {
							iv_num_add.setSelected(true); //可+
						}else if (skuNum == 0) {
							buyNumber = 0;
							isNext = false;
						}
						tv_popup_number.setText(String.valueOf(buyNumber));
						if (isNext) {
							tv_popup_confirm.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_app_buttom_0));
						}else {
							tv_popup_confirm.setBackgroundColor(getResources().getColor(R.color.input_text_color));
						}
					}

				};
				AddCartPopupListAdapter svlvAdapter = new AddCartPopupListAdapter(mContext, attrEn, apCallback);
				svlv.setAdapter(svlvAdapter);
				svlv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
			}else {
				attrNameStr = getString(R.string.product_buy_number);
				isNext = true;
				skuNum = attrEn.getSkuNum();
				if (skuNum > 1) {
					iv_num_add.setSelected(true); //可+
				}else if (skuNum == 0) {
					buyNumber = 0;
					isNext = false;
				}
				tv_popup_number.setText(String.valueOf(buyNumber));
				if (isNext) {
					tv_popup_confirm.setBackground(getResources().getDrawable(R.drawable.shape_frame_bg_app_buttom_0));
				}else {
					tv_popup_confirm.setBackgroundColor(getResources().getColor(R.color.input_text_color));
				}
			}
			tv_popup_select.setText(attrNameStr);

			cartPopupWindow = new PopupWindow(cartPopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			cartPopupWindow.setFocusable(true);
			cartPopupWindow.update();
			cartPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			cartPopupWindow.setOutsideTouchable(true);
			cartPopupWindow.showAtLocation(cartPopupView, Gravity.BOTTOM, 0, 0);
		}else {
			ll_cart_show.startAnimation(popupAnimShow);
			cartPopupWindow.showAtLocation(cartPopupView, Gravity.BOTTOM, 0, 0);
		}
	}

	private String getSelectShowStr(ProductAttrEntity en){
		if (en != null && en.getAttrLists() != null) {
			StringBuilder sb = new StringBuilder();
			attrNum = en.getAttrLists().size();
			ProductAttrEntity item;
			for (int i = 0; i < attrNum; i++) {
				item = en.getAttrLists().get(i);
				sb.append(item.getAttrName());
				sb.append("、");
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();
		}
		return "";
	}

	/**
	 * 关闭购物浮层
	 */
	private void catrPopupDismiss() {
		ll_cart_show.startAnimation(popupAnimGone);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				cartPopupWindow.dismiss();
			}
		}, 500);
	}

	/**
	 * 动态生成一个View实现数量增加的效果
	 */
	private void startNumberAddAnim(int addNum) {
		if (ll_bottom_main == null || tv_cart_total == null || tv_cart == null) return;
		tv_cart_total.setVisibility(View.VISIBLE);

		final TextView tv_name = new TextView(mContext);
		tv_name.setGravity(Gravity.CENTER);
		tv_name.setText("+" + addNum);
		tv_name.setTextColor(mContext.getResources().getColor(R.color.tv_color_status));
		tv_name.setTextSize(mScale * 14);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.BOTTOM;
		lp.setMargins(ll_bottom_main.getWidth() / 2 - tv_cart.getRight() / 2 + 6,
				0, 0, (ll_bottom_main.getBottom() - ll_bottom_main.getTop()) - 10);
		fl_main.addView(tv_name, lp);

		tv_name.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_number_add));
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				fl_main.removeView(tv_name);
				showCartTotal();
			}
		}, 1000);
	};

	/**
	 * 显示购物车商品数
	 */
	protected void showCartTotal() {
		if (tv_cart_total == null) return;
		int cartNumTotal = UserManager.getInstance().getCartTotal();
		if (cartNumTotal > 0) {
			tv_cart_total.setVisibility(View.VISIBLE);
			tv_cart_total.setText(String.valueOf(cartNumTotal));
		}else {
			tv_cart_total.setVisibility(View.GONE);
			tv_cart_total.setText(getString(R.string.number_0));
		}
	}

	/**
	 * 更新购物车缓存
	 */
	public static void updateCartTotal(int cartNumTotal) {
		UserManager.getInstance().saveCartTotal(cartNumTotal);
	}

	/**
	 * 显示分享View
	 */
	protected void showShareView(ShareEntity shareEn){
		if (mShareView != null && shareEn != null) {
			if (mShareView.getShareEntity() == null) {
				mShareView.setShareEntity(shareEn);
			}
			if (mShareView.isShowing()) {
				mShareView.showShareLayer(false);
			} else {
				if (!UserManager.getInstance().checkIsLogined()) {
					openLoginActivity();
					return;
				}
				mShareView.showShareLayer(true);
			}
		}else {
			showShareError();
		}
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
	protected void showTimeOutDialog() {
		openLoginActivity(TAG);
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
		dm.showOneBtnDialog(content, dialogWidth, true, isVanish, handler, null);
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
		showConfirmDialog(null, content, positiveBtnStr, negativeBtnStr, dialogWidth, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String title, String content, String positiveBtnStr, String negativeBtnStr,
									 int width, boolean isCenter, boolean isVanish, final Handler handler) {
		positiveBtnStr = (positiveBtnStr == null) ? getString(R.string.confirm) : positiveBtnStr;
		negativeBtnStr = (negativeBtnStr == null) ? getString(R.string.cancel) : negativeBtnStr;
		dm.showTwoBtnDialog(null, content, positiveBtnStr, negativeBtnStr, width, isCenter, isVanish, handler);
	}

	protected void showListDialog(int contentResId, CharSequence[] items, boolean isCenter, final Handler handler) {
		showListDialog(contentResId, items, dialogWidth, isCenter, handler);
	}

	protected void showListDialog(int contentResId, CharSequence[] items, int width, boolean isCenter, final Handler handler) {
		showListDialog(getString(contentResId), items, width, isCenter, handler);
	}

	protected void showListDialog(String content, CharSequence[] items, boolean isCenter, final Handler handler) {
		showListDialog(content, items, dialogWidth, isCenter, handler);
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
			case 1: //刷新"首页"页面数据
				if (ChildFragmentOne.instance != null) {
					ChildFragmentOne.instance.updateData();
				}
				break;
			case 5: //刷新头像
				if (ChildFragmentFive.instance != null) {
					ChildFragmentFive.instance.updateAvatar();
				}
				break;
			case 6: //刷新"我的关注"页面数据
				if (ShowListActivity.instance != null) {
					ShowListActivity.instance.updateData();
				}
				break;
			case 7: //刷新"我的钱包"页面数据
				if (AccountBalanceActivity.instance != null) {
					AccountBalanceActivity.instance.updateData();
				}
				break;
			case 8: //刷新"我的地址"页面数据
				if (MyAddressActivity.instance != null) {
					MyAddressActivity.instance.updateData();
				}
				break;
			case 9: //刷新"确认订单"页面数据
				if (PostOrderActivity.instance != null) {
					PostOrderActivity.instance.updateData();
				}
				break;
			case 10: //刷新"订单列表"页面数据
				if (OrderListActivity.instance != null) {
					OrderListActivity.instance.updateData();
				}
				break;
			case 20: //刷新"商品详情"页面数据
				if (ProductDetailActivity.instance != null) {
					ProductDetailActivity.instance.updateData();
				}
				break;
			case 21: //刷新"商品列表"页面数据
				if (ProductListActivity.instance != null) {
					ProductListActivity.instance.updateData();
				}
				break;
			case 22: //刷新"品牌列表"页面数据
				if (ShowListHeadActivity.instance != null) {
					ShowListHeadActivity.instance.updateData();
				}
				break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				clearCountdown();
				break;
			case MotionEvent.ACTION_UP:
				startCountdown();
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 开启倒计时
	 */
	private void startCountdown() {
		if (UserManager.getInstance().isPlayVideo() || UserManager.getInstance().isPlayImage()) {
			clearCountdown();
			acdt = new ACountDownTimer(AppConfig.TO_SCREEN_VIDEO_TIME, 1000);
			acdt.start();
		}
	}

	/**
	 * 清除倒计时
	 */
	private void clearCountdown() {
		if (acdt != null) {
			acdt.cancel();
			acdt = null;
		}
	}

	protected void stopLoop() {
		isStartLoop = false;
		clearCountdown();
	}

	protected void startLoop() {
		isStartLoop = true;
		startCountdown();
	}

	private class ACountDownTimer extends CountDownTimer {
		public ACountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			if (!isStartLoop) return;
			if (UserManager.getInstance().isPlayVideo()) {
				startActivity(new Intent(mContext, ScreenVideoActivity.class));
			} else if (UserManager.getInstance().isPlayImage()){
				startActivity(new Intent(mContext, ScreenImageActivity.class));
			}
		}
	}

}