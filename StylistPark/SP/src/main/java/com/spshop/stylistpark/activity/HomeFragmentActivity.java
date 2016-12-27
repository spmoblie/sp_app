package com.spshop.stylistpark.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.cart.ChildFragmentFour;
import com.spshop.stylistpark.activity.common.ScreenImageActivity;
import com.spshop.stylistpark.activity.common.ScreenVideoActivity;
import com.spshop.stylistpark.activity.find.ChildFragmentThree;
import com.spshop.stylistpark.activity.home.ChildFragmentOne;
import com.spshop.stylistpark.activity.home.ProductDetailActivity;
import com.spshop.stylistpark.activity.login.LoginActivity;
import com.spshop.stylistpark.activity.mine.ChildFragmentFive;
import com.spshop.stylistpark.activity.mine.MemberListActivity;
import com.spshop.stylistpark.activity.sort.ChildFragmentTwo;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UpdateAppVersion;
import com.spshop.stylistpark.utils.UserManager;

public class HomeFragmentActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = "HomeFragmentActivity";
	public static HomeFragmentActivity instance = null;

	private SharedPreferences shared;
	private FragmentManager manager;
	private FragmentPagerAdapter mFragmentPagerAdapter;
	private static Fragment fragment = null;

	private FrameLayout fl_container, fl_one, fl_two, fl_three, fl_four, fl_five;
	private TextView text_one, text_two, text_three, text_four, tv_four_warn, text_five;
	private ImageView img_two_warn, img_three_warn, img_five_warn;

	private static final String[] FRAGMENT_CONTAINER = { "one", "two", "three", "four", "five" };
	private static String current_fragment; //当前要显示的Fragment
	private int current_index = -1; //当前要显示的Fragment下标索引
	private boolean isNewFour = true; //是否刷新Four子页面
	private boolean exit = false;
	private boolean warn_two = false;
	private boolean warn_three = false;
	private boolean warn_five = false;
	private ACountDownTimer acdt;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		try {
			setContentView(R.layout.activity_home_fragment);
			LogUtil.i(TAG, "onCreate");

			AppManager.getInstance().addActivity(this);// 添加Activity到堆栈
			instance = this;
			shared = AppApplication.getSharedPreferences();
			manager = getSupportFragmentManager();
			mFragmentPagerAdapter = new MyFragmentPagerAdapter(manager);
			// 动态注册广播
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(AppConfig.RECEIVER_ACTION_HOME_DATA);
			registerReceiver(myBroadcastReceiver, mFilter);
			// 检测App版本信息
			UpdateAppVersion.getInstance(instance, true);
			// 设置App字体不随系统字体变化
			AppApplication.initDisplayMetrics();

			findViewById();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}

	private void findViewById() {
		fl_container = (FrameLayout) findViewById(R.id.home_fragment_fl_container);
		fl_one = (FrameLayout) findViewById(R.id.home_fragment_fl_one);
		fl_two = (FrameLayout) findViewById(R.id.home_fragment_fl_two);
		fl_three = (FrameLayout) findViewById(R.id.home_fragment_fl_three);
		fl_four = (FrameLayout) findViewById(R.id.home_fragment_fl_four);
		fl_five = (FrameLayout) findViewById(R.id.home_fragment_fl_five);
		text_one = (TextView) findViewById(R.id.home_fragment_tv_one);
		text_two = (TextView) findViewById(R.id.home_fragment_tv_two);
		text_three = (TextView) findViewById(R.id.home_fragment_tv_three);
		text_four = (TextView) findViewById(R.id.home_fragment_tv_four);
		text_five = (TextView) findViewById(R.id.home_fragment_tv_five);
		tv_four_warn = (TextView) findViewById(R.id.home_fragment_tv_four_warn);
		img_two_warn = (ImageView) findViewById(R.id.home_fragment_iv_two_warn);
		img_three_warn = (ImageView) findViewById(R.id.home_fragment_iv_three_warn);
		img_five_warn = (ImageView) findViewById(R.id.home_fragment_iv_five_warn);
	}

	private void initView() {
		fl_one.setOnClickListener(this);
		fl_two.setOnClickListener(this);
		fl_three.setOnClickListener(this);
		fl_four.setOnClickListener(this);
		fl_five.setOnClickListener(this);

		int shared_index = shared.getInt(AppConfig.KEY_HOME_CURRENT_INDEX, 0);
		LogUtil.i(TAG, "current_index = " + current_index + " shared_index = " + shared_index);
		// 程序出现错误时重启Home
		if (AppApplication.isStartHome && current_index != shared_index) {
			startFragmen();
			return;
		}else {
			AppApplication.isStartHome = true;
		}
		// 设置默认初始化的界面
		switch (shared_index) {
		case 0:
			fl_one.performClick();
			break;
		case 1:
			fl_two.performClick();
			break;
		case 2:
			fl_three.performClick();
			break;
		case 3:
			fl_four.performClick();
			break;
		case 4:
			fl_five.performClick();
			break;
		default:
			fl_one.performClick();
			break;
		}
		if (warn_two) {
			img_two_warn.setVisibility(View.VISIBLE);
		}
		if (warn_three) {
			img_three_warn.setVisibility(View.VISIBLE);
		}
		if (warn_five) {
			img_five_warn.setVisibility(View.VISIBLE);
		}
		changeCartTotal(-1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (exit) {
				AppManager.getInstance().AppExit(getApplicationContext());
			} else {
				exit = Boolean.TRUE;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						exit = Boolean.FALSE;
					}
				}, 2000);
				CommonTools.showToast(getString(R.string.toast_exit_prompt), 1000);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		// 设置App字体不随系统字体变化
		AppApplication.initDisplayMetrics();

		if (!UserManager.getInstance().checkIsLogined()) {
			openLoginActivity(TAG); //强制登入
		}

		exit = Boolean.FALSE;
		initView();

		// 开启倒计时
		startCountdown();
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		// 清除倒计时
		clearCountdown();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i(TAG, "onDestroy");
		if (myBroadcastReceiver != null) {
			unregisterReceiver(myBroadcastReceiver);
		}
		super.onDestroy();
	}

	/**
	 * 重新启动HomeFragmentActivity
	 */
	public void startFragmen() {
		AppApplication.isStartHome = false;
		finish();
		startActivity(new Intent(this, HomeFragmentActivity.class));
	}
	
	/**
	 * 修改购物车中商品数量
	 */
	public void changeCartTotal(int cartTotal){
		if (cartTotal < 0) {
			cartTotal = UserManager.getInstance().getCartTotal();
		}
		if (cartTotal > 0) {
			tv_four_warn.setVisibility(View.VISIBLE);
		}else {
			tv_four_warn.setVisibility(View.GONE);
		}
		tv_four_warn.setText(String.valueOf(cartTotal));
	}

	/**
	 * 打开推送通知跳转至MemberListActivity
	 */
	public void pushGoToMemberListActivity() {
		if (ChildFragmentFive.instance != null) {
			Intent intent = new Intent(this, MemberListActivity.class);
			intent.putExtra("topType", MemberListActivity.TYPE_1);
			startActivity(intent);
		}
		changeFragmen(4);
	}

	/**
	 * 跳转到Fragmen子界面
	 * @param index
	 */
	public void changeFragmen(int index) {
		switch (index) {
		case 0:
			fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_container, R.id.home_fragment_fl_one);
			break;
		case 1:
			fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_container, R.id.home_fragment_fl_two);
			img_two_warn.setVisibility(View.GONE);
			break;
		case 2:
			fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_container, R.id.home_fragment_fl_three);
			img_three_warn.setVisibility(View.GONE);
			break;
		case 3:
			if (!UserManager.getInstance().checkIsLogined()) {
				openLoginActivity(TAG);
				return;
			}
			fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_container, R.id.home_fragment_fl_four);
			break;
		case 4:
			fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_container, R.id.home_fragment_fl_five);
			img_five_warn.setVisibility(View.GONE);
			break;
		}
		current_index = index;
		current_fragment = FRAGMENT_CONTAINER[current_index];
		mFragmentPagerAdapter.setPrimaryItem(fl_container, 0, fragment);
		mFragmentPagerAdapter.finishUpdate(fl_container);
		updateImageViewStatus();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_fragment_fl_one:
			current_index = 0;
			break;
		case R.id.home_fragment_fl_two:
			if (current_index == 1) return;
			current_index = 1;
			img_two_warn.setVisibility(View.GONE);
			break;
		case R.id.home_fragment_fl_three:
			if (current_index == 2) return;
			current_index = 2;
			img_three_warn.setVisibility(View.GONE);
			break;
		case R.id.home_fragment_fl_four:
			if (current_index == 3) return;
			if (!UserManager.getInstance().checkIsLogined()) {
				openLoginActivity(TAG);
				return;
			}
			current_index = 3;
			break;
		case R.id.home_fragment_fl_five:
			current_index = 4;
			img_five_warn.setVisibility(View.GONE);
			break;
		}
		fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fl_container, v.getId());
		mFragmentPagerAdapter.setPrimaryItem(fl_container, 0, fragment);
		mFragmentPagerAdapter.finishUpdate(fl_container);
		current_fragment = FRAGMENT_CONTAINER[current_index];
		shared.edit().putInt(AppConfig.KEY_HOME_CURRENT_INDEX, current_index).apply();
		updateImageViewStatus();
		exit = Boolean.FALSE;
	}
	
	public void openLoginActivity(String rootStr){
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("rootPage", rootStr);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void openProductDetailActivity(int id){
		if (id == AppConfig.SP_JION_PROGRAM_ID) return; //虚拟商品
		Intent intent = new Intent(this, ProductDetailActivity.class);
		intent.putExtra("goodsId", id);
		startActivity(intent);
	}

	/**
	 * 自定义切换底栏的ImgaeView状态
	 */
	private void updateImageViewStatus() {
		text_one.setSelected(false);
		text_two.setSelected(false);
		text_three.setSelected(false);
		text_four.setSelected(false);
		text_five.setSelected(false);
		switch (current_index) {
		case 0:
			text_one.setSelected(true);
			break;
		case 1:
			text_two.setSelected(true);
			break;
		case 2:
			text_three.setSelected(true);
			break;
		case 3:
			text_four.setSelected(true);
			break;
		case 4:
			text_five.setSelected(true);
			break;
		}
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		
		FragmentManager fm;

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case R.id.home_fragment_fl_one:
				fragment = (Fragment) manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentOne();
				}
				return fragment;
			case R.id.home_fragment_fl_two:
				fragment = (Fragment) manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentTwo();
				}
				return fragment;
			case R.id.home_fragment_fl_three:
				fragment = (Fragment) manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentThree();
				}
				return fragment;
			case R.id.home_fragment_fl_four:
				fragment = (Fragment) manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentFour();
					isNewFour = false;
				}
				return fragment;
			case R.id.home_fragment_fl_five:
				fragment = (Fragment) manager.findFragmentByTag(current_fragment);
				if (fragment == null) {
					fragment = new ChildFragmentFive();
				}
				return fragment;
			default:
				fragment = (Fragment) manager.findFragmentByTag(FRAGMENT_CONTAINER[0]);
				if (fragment == null) {
					fragment = new ChildFragmentOne();
				}
				return fragment;
			}
		}

		@Override
		public int getCount() {
			return FRAGMENT_CONTAINER.length;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 得到缓存的fragment
			Fragment fragment = (Fragment) super.instantiateItem(container, position);
			// 得到tag，这点很重要
			String fragmentTag = fragment.getTag();
			switch (position) {
				case R.id.home_fragment_fl_four:
					if (isNewFour) {
						FragmentTransaction ft = fm.beginTransaction();
						// 移除旧的fragment
						ft.remove(fragment);
						// 换成新的fragment
						fragment = new ChildFragmentFour();
						// 添加新fragment时必须用前面获得的tag，这点很重要
						ft.add(container.getId(), fragment, fragmentTag);
						ft.attach(fragment);
						ft.commit();
					} else {
						isNewFour = true;
					}
				break;
			}
			return fragment;
		}
	}

	// 广播接收器
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AppConfig.RECEIVER_ACTION_HOME_DATA)) {
				int status = intent.getExtras().getInt(AppConfig.RECEIVER_ACTION_MAIN_MSG_KEY, 0);
				switch (status) {
				case 1:
					img_two_warn.setVisibility(View.VISIBLE);
					break;
				case 2:
					img_three_warn.setVisibility(View.VISIBLE);
					break;
				case 4:
					img_five_warn.setVisibility(View.VISIBLE);
					break;
				}
			}
		}
	};

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

	private class ACountDownTimer extends CountDownTimer {
		public ACountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			if (UserManager.getInstance().isPlayVideo()) {
				startActivity(new Intent(HomeFragmentActivity.this, ScreenVideoActivity.class));
			} else if (UserManager.getInstance().isPlayImage()){
				startActivity(new Intent(HomeFragmentActivity.this, ScreenImageActivity.class));
			}
		}
	}

}
