package com.spshop.stylistpark.activity.collage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserTracker;

public class ChildFragmentThree extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentThree";
	public static ChildFragmentThree instance = null;

	private Context mContext;
	private OnTouchListener menuTouchListener;
	private int menuAnimationTime = 400;
	private int collageW;
	private float translateX;
	private boolean isRunningAnimation = false;

	private RelativeLayout rl_video, rl_collage_free, rl_collage_template, rl_multiangle;
	private RelativeLayout rl_collage_anim, rl_collage_back;
	private ImageView iv_video, iv_collage, iv_multiangle;
	private View include_video, include_collage, include_multiangle, v_back;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 与Activity不一样
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		LogUtil.i(TAG, "onCreate");
		instance = this;
		mContext = getActivity();

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_three, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		rl_video = (RelativeLayout) view.findViewById(R.id.fragment_three_rl_video);
		rl_collage_free = (RelativeLayout) view.findViewById(R.id.fragment_three_rl_collage_free);
		rl_collage_template = (RelativeLayout) view.findViewById(R.id.fragment_three_rl_collage_template);
		rl_multiangle = (RelativeLayout) view.findViewById(R.id.fragment_three_rl_multiangle);

		rl_collage_anim = (RelativeLayout) view.findViewById(R.id.fragment_three_rl_collage_anim);
		rl_collage_back = (RelativeLayout) view.findViewById(R.id.fragment_three_rl_collage_back);
		v_back = view.findViewById(R.id.fragment_three_view_back);
		iv_video = (ImageView) view.findViewById(R.id.fragment_three_iv_video);
		iv_collage = (ImageView) view.findViewById(R.id.fragment_three_iv_collage);
		iv_multiangle = (ImageView) view.findViewById(R.id.fragment_three_iv_multiangle);

		include_video = view.findViewById(R.id.fragment_three_include_video);
		TextView tvTitle = (TextView) include_video.findViewById(R.id.create_menu_tv_title);
		TextView tvDesc = (TextView) include_video.findViewById(R.id.create_menu_tv_desc);
		TextView tvMake = (TextView) include_video.findViewById(R.id.create_menu_tv_make);
		tvTitle.setText(R.string.collage_video_title);
		tvDesc.setText(R.string.collage_video_desc);
		tvMake.setText(R.string.collage_upload);

		include_collage = view.findViewById(R.id.fragment_three_include_collage);
		tvTitle = (TextView) include_collage.findViewById(R.id.create_menu_tv_title);
		tvDesc = (TextView) include_collage.findViewById(R.id.create_menu_tv_desc);
		tvMake = (TextView) include_collage.findViewById(R.id.create_menu_tv_make);
		tvTitle.setText(R.string.collage_collage_title);
		tvDesc.setText(R.string.collage_collage_desc);
		tvMake.setText(R.string.collage_create);
		
		include_multiangle = view.findViewById(R.id.fragment_three_include_multiangle);
		tvTitle = (TextView) include_multiangle.findViewById(R.id.create_menu_tv_title);
		tvDesc = (TextView) include_multiangle.findViewById(R.id.create_menu_tv_desc);
		tvMake = (TextView) include_multiangle.findViewById(R.id.create_menu_tv_make);
		tvTitle.setText(R.string.collage_multiangle_title);
		tvDesc.setText(R.string.collage_multiangle_desc);
		tvMake.setText(R.string.collage_create);
	}

	private void initView() {
		rl_video.setOnClickListener(this);
		rl_collage_free.setOnClickListener(this);
		rl_collage_template.setOnClickListener(this);
		rl_multiangle.setOnClickListener(this);
		v_back.setOnClickListener(this);
		rl_collage_anim.setOnTouchListener(getMenuTouchListener());
		rl_collage_back.setOnTouchListener(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_three_rl_video:
			startActivity(new Intent(getActivity(), GeneratorPhotoActivity.class));
			break;
		case R.id.fragment_three_rl_collage_free:
			UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_FREE_COLLAGE_GENERATOR, null);
			startActivity(new Intent(mContext, GeneratorFreeStyleActivity.class));
			break;
		case R.id.fragment_three_rl_collage_template:
			UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_SELECT_TEMPLATE, null);
			startActivity(new Intent(mContext, GeneratorTemplateChooseActivity.class));
			break;
		case R.id.fragment_three_view_back:
			clickCollageBack();
			break;
		case R.id.fragment_three_rl_multiangle:
			startActivity(new Intent(mContext, MultiAngleMainActivity.class));
			break;
		}
	}

	public void clickCollage(View v) {
		int backViewWidth = rl_collage_back.getWidth();
		final int moveToX = (int) translateX + backViewWidth;
		LogUtil.i(TAG, "moveToX = " + moveToX);
		doMenuAnimation(rl_collage_anim, 0, moveToX, null, include_collage, rl_collage_back, menuAnimationTime);
	}

	public void clickCollageBack() {
		int backViewWidth = rl_collage_back.getWidth();
		final int moveFromX = (int) translateX + backViewWidth;
		LogUtil.i(TAG, "moveFromX = " + moveFromX);
		doMenuAnimation(rl_collage_anim, moveFromX, 0, getMenuTouchListener(), rl_collage_back, include_collage, menuAnimationTime);
		rl_collage_anim.setOnTouchListener(getMenuTouchListener());
	}

	public void doMenuAnimation(final View viewMove, final int viewMoveFromX,
			final int viewMoveToX, final OnTouchListener mOnTouchListener,
			final View viewFadeOut, final View viewFadeIn, int duration) {

		if (isRunningAnimation) {
			LogUtil.i(TAG, "Animation is running.");
			return;
		}
		isRunningAnimation = true;
		
		if (viewMove != null) {
			TranslateAnimation move = new TranslateAnimation(
					Animation.ABSOLUTE, viewMoveFromX, Animation.ABSOLUTE,
					viewMoveToX, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
			move.setDuration(duration);
			move.setFillEnabled(true);
			move.setFillAfter(true);
			move.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					// Tools.enableViews(viewMove, true);
					viewMove.setOnTouchListener(mOnTouchListener);
					isRunningAnimation = false;
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					
				}

				@Override
				public void onAnimationStart(Animation arg0) {
					
				}

			});
			viewMove.startAnimation(move);
		}

		if (viewFadeOut != null) {
			AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
			fadeOut.setDuration(duration);
			fadeOut.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					viewFadeOut.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					
				}

				@Override
				public void onAnimationStart(Animation arg0) {

				}

			});
			viewFadeOut.startAnimation(fadeOut);
		}

		if (viewFadeIn != null) {
			AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
			fadeIn.setDuration(duration);
			fadeIn.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					
				}

				@Override
				public void onAnimationStart(Animation arg0) {
					viewFadeIn.setVisibility(View.VISIBLE);
				}

			});
			viewFadeIn.startAnimation(fadeIn);
		}

	}

	public OnTouchListener getMenuTouchListener() {
		if (menuTouchListener != null) {
			return menuTouchListener;
		}

		menuTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					LogUtil.i(TAG, "menu onTouch() X=" + event.getX());
					clickCollage(v);
					v.playSoundEffect(SoundEffectConstants.CLICK);
				}
				return true;
			}
		};

		return menuTouchListener;
	}

	@Override
	public void onStart() {
		super.onStart();
		// 异步调整组件的宽度
		iv_video.post(new Runnable() {

			@Override
			public void run() {
				int h = iv_video.getHeight();

				BitmapDrawable bd = (BitmapDrawable) getResources()
						.getDrawable(R.drawable.generator_menu_image_video);
				double imageHeight = bd.getBitmap().getHeight();
				double imageWidth = bd.getBitmap().getWidth();

				double ratio = imageWidth / imageHeight;
				int newW = (int) (ratio * h);
				iv_video.getLayoutParams().width = newW;
				iv_collage.getLayoutParams().width = newW;
				iv_multiangle.getLayoutParams().width = newW;
			}
		});
		// 获取屏幕的宽度
		collageW = CommonTools.getScreeanSize(getActivity()).x;
		translateX = (float) -collageW;
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(getActivity(), TAG);
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		stopAnimation();
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		stopAnimation();
		CommonTools.showToast(String.valueOf(result), 1000);
	}

	/**
	 * 显示缓冲动画
	 */
	@SuppressWarnings("unused")
	private void startAnimation() {
		
	}

	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView()
					.setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}
