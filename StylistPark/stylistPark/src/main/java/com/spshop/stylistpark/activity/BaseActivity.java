package com.spshop.stylistpark.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.login.LoginActivity;
import com.spshop.stylistpark.dialog.LoadDialog;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 所有Activity的父类
 */
@SuppressLint("HandlerLeak")
public  class BaseActivity extends FragmentActivity implements OnDataListener {

	public static final String TAG = BaseActivity.class.getSimpleName();
	
	protected Context mContext;
	protected AsyncTaskManager atm;
	protected int width, height;
	protected SharedPreferences shared;
	protected Editor editor;
	protected AlertDialog myDialog;
	public IWXAPI api;
	
	private RelativeLayout rl_head;
	private ImageView iv_left;
	private TextView tv_title;
	private Button btn_right;
	private ViewFlipper mLayoutBase;
	private Animation inAnim, outAnim;
	
	protected boolean headStatus = false; //记录头部组件显示的状态
	protected int headAnimHeight;
	protected TranslateAnimation headGONE, headVISIBLE;
	protected ServiceContext sc = ServiceContext.getServiceContext();
	
	protected static final int STACK_MAX_SIZE = 5;
	protected static final int MAX_DECOR = 5; //搭配素材上限
	protected static final int MIN_PRODUCT = 1; //搭配货品下限
	protected static final int MAX_PRODUCT = 10; //搭配货品上限
	protected static final int GEN_OUTPUT_SIDE = 450;
	protected static final int GEN_OUTPUT_MOBILE_SIDE = 300;
	protected static final int DIALOG_CONFIRM_CLICK = 456; //全局对话框“确定”
	protected static final int DIALOG_CANCEL_CLICK = 887; //全局对话框“取消”
	
	protected RetryPolicy retryPolicy60s = new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_base);
		LogUtil.i(TAG, "onCreate()");

		mContext = this;
		shared = AppApplication.getSharedPreferences();
		editor = shared.edit();
		atm = AsyncTaskManager.getInstance(mContext);
		AppManager.getInstance().addActivity(this);
		api = WXAPIFactory.createWXAPI(this, AppConfig.WX_APP_ID);
		api.registerApp(AppConfig.WX_APP_ID);
		
		// 获取屏幕配置
		width = AppApplication.screenWidth;
		height = AppApplication.screenHeight;
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		rl_head = (RelativeLayout) findViewById(R.id.top_bar_head_rl);
		iv_left = (ImageView) findViewById(R.id.top_bar_left);
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
			if (rl_head.getVisibility() == View.GONE) {
				rl_head.clearAnimation();
				rl_head.startAnimation(inAnim);
			}
			break;
		case View.GONE:
			if (rl_head.getVisibility() == View.VISIBLE) {
				rl_head.clearAnimation();
				rl_head.startAnimation(outAnim);
			}
			break;
		}
		rl_head.setVisibility(visibility);
	}
	
	/**
	 * 设置头部View背景色
	 */
	public void setHeadBackground(int color){
		rl_head.setBackgroundColor(color);
	}
	
	/**
	 * 获取头部View的高度
	 */
	public int getHeadHeight(){
		return rl_head.getHeight();
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
		tv_title.setText(getString(titleId));
	}
	
	/**
	 * 标题右边添加图片资源（资源Id）
	 */
	public void setTitleDrawableRight(int drawableRight){
		Resources res = getResources();
		Drawable rightDrawable = res.getDrawable(drawableRight);
		rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
		tv_title.setCompoundDrawables(null, null, rightDrawable, null);
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
	
	/**
	 * 设置标题（文本对象）
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}
	
	/**
	 * 左键键监听执行方法，让子类重写该方法
	 */
	public void OnListenerLeft(){
			finish();
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
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume()");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause()");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy()");
		
		headGONE = null;
		headVISIBLE = null;
		dissmissMyDialog();
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
		LoadDialog.hidden(mContext);
	}
	
	/**
	 * 弹出登入超时对话框
	 */
	protected void showTimeOutDialog(final String rootPage) {
		AppApplication.AppLogout(true);
		showErrorDialog(getString(R.string.login_timeout), new Handler(){
			@Override
			public void handleMessage(Message msg) {
				openLoginActivity(rootPage);
			}
		});
	}
	
	/**
	 * 加载数据出错，提示服务器繁忙
	 */
	protected void showServerBusy() {
		showErrorDialog(R.string.toast_server_busy);
	}

	protected void showErrorDialog(int resId) {
		showErrorDialog(getString(resId));
	}

	protected void showErrorDialog(String content) {
		showErrorDialog(content, null);
	}
	
	protected void showErrorDialog(String content, final Handler handler) {
		try {
			dissmissMyDialog(); //销毁旧对话框
			content = (TextUtils.isEmpty(content)) ? getString(R.string.dialog_error_msg) : content;
			Builder dialog = new AlertDialog.Builder(this)
			.setMessage(content)
			.setCancelable(false)
			.setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					if (handler != null) {
						handler.sendEmptyMessage(DIALOG_CONFIRM_CLICK);
					}
				}
			});
			myDialog = dialog.show();
			TextView messageText = (TextView) myDialog.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		} catch (Exception e) {
			ExceptionUtil.handle(mContext, e);
		}
	}
	
    protected void showConfirmDialog(int contentResId, String positiveBtnStr,
			String negativeBtnStr, final Handler handler) {
		showConfirmDialog(getString(contentResId), positiveBtnStr, negativeBtnStr, handler);
	}

	protected void showConfirmDialog(String content, String positiveBtnStr,
			String negativeBtnStr, final Handler handler) {
		showConfirmDialog(null, content, positiveBtnStr, negativeBtnStr, handler);
	}

	protected void showConfirmDialog(String title, String content, String positiveBtnStr, String negativeBtnStr, final Handler handler) {
		try {
			dissmissMyDialog(); //销毁旧对话框
			positiveBtnStr = (positiveBtnStr == null) ? getString(R.string.confirm) : positiveBtnStr;
			negativeBtnStr = (negativeBtnStr == null) ? getString(R.string.cancel) : negativeBtnStr;
			Builder dialog = new AlertDialog.Builder(this)
			.setMessage(content)
			.setCancelable(false)
			.setPositiveButton(positiveBtnStr,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					if (handler != null) {
						handler.sendEmptyMessage(DIALOG_CONFIRM_CLICK);
					}
				}
			})
			.setNegativeButton(negativeBtnStr,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					if (handler != null) {
						handler.sendEmptyMessage(DIALOG_CANCEL_CLICK);
					}
				}
			});
			if (!TextUtils.isEmpty(title)) {
				dialog.setTitle(title);
			}
			myDialog = dialog.show();
			TextView messageText = (TextView) myDialog.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		} catch (Exception e) {
			ExceptionUtil.handle(mContext, e);
		}
	}

	protected void showConfirmDialog(int contentResId, CharSequence[] items, final Handler handler) {
		showConfirmDialog(getString(contentResId), items, handler);
	}

	protected void showConfirmDialog(String content, CharSequence[] items, final Handler handler) {
		try {
			dissmissMyDialog(); //销毁旧对话框
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.dialog_list_1, null, false);
			ListView lv = (ListView) v.findViewById(R.id.lv);
			List<CharSequence> itemList = Arrays.asList(items);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, itemList) {

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					TextView tv = (TextView) v.findViewById(android.R.id.text1);
					tv.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
					tv.setGravity(Gravity.CENTER);
					return v;
				}

			};
			lv.setAdapter(adapter);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this).setMessage(content).setCancelable(false).setView(v);

			myDialog = dialog.show();
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					LogUtil.i(TAG, "onItemClick rayrayray: " + position);
					handler.sendEmptyMessage(position);
					myDialog.dismiss();
				}
			});
			TextView messageText = (TextView) myDialog.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		} catch (Exception e) {
			ExceptionUtil.handle(mContext, e);
		}
	}
	
	private void dissmissMyDialog(){
		if (myDialog != null) {
			myDialog.dismiss();
			myDialog = null;
		}
	}
	
	/**
	 * 创建向上缩进、向下拉出的动画效果
	 * 
	 * @param topView 缩进、拉出的顶层View
	 * @param bottomView 缩进、拉出的底层View
	 * @param otherView 其它跟随动画的View
	 */
	protected void createAnimation(final View topView, final View bottomView, final View otherView) {
		headAnimHeight = topView.getHeight();
		if (headGONE == null) {
			headGONE = new TranslateAnimation(0, 0, 0, -headAnimHeight);
			headGONE.setDuration(500);
			headGONE.setFillAfter(true);
			headGONE.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					topView.setVisibility(View.GONE);
					int headLeft = topView.getLeft();
					int headTop = topView.getTop()-headAnimHeight;
					int headWidth = topView.getWidth();
					int headHeight = topView.getHeight();
					topView.clearAnimation();
					topView.layout(headLeft, headTop, headWidth+headLeft, headHeight+headTop);
					int listLeft = otherView.getLeft();
					int listTop = otherView.getTop()-headAnimHeight;
					int listWidth = otherView.getWidth();
					int listHeight = otherView.getHeight();
					otherView.clearAnimation();
					otherView.layout(listLeft, listTop, listWidth+listLeft, listHeight+listTop);
					headStatus = true;
					bottomView.setVisibility(View.GONE);
				}
			});
		}
		if (headVISIBLE == null) {
			headVISIBLE = new TranslateAnimation(0, 0, 0, headAnimHeight);
			headVISIBLE.setDuration(500);
			headVISIBLE.setFillAfter(true);
			headVISIBLE.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					topView.setVisibility(View.VISIBLE);
					bottomView.setVisibility(View.VISIBLE);
					int headLeft = topView.getLeft();
					int headTop = topView.getTop()+headAnimHeight;
					int headWidth = topView.getWidth();
					int headHeight = topView.getHeight();
					topView.clearAnimation();
					topView.layout(headLeft, headTop, headWidth+headLeft, headHeight+headTop);
					int listLeft = otherView.getLeft();
					int listTop = otherView.getTop()+headAnimHeight;
					int listWidth = otherView.getWidth();
					int listHeight = otherView.getHeight();
					otherView.clearAnimation();
					otherView.layout(listLeft, listTop, listWidth+listLeft, listHeight+listTop);
					headStatus = false;
				}
			});
		}
	}
	
}