package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import com.spshop.stylistpark.utils.LogUtil;

public class BaseFragment extends Fragment {

	String TAG = BaseFragment.class.getSimpleName();
	ShowErrDialogListener showErrDialogListener;
	LoadingListener loadingListener;
	SoftKeyBoardListener softKeyBoardListener;
	RequestBlockingListener requestBlockingListener;

	public interface ShowErrDialogListener {
		public void showErrDialog(String msg);
	}

	public interface LoadingListener {
		public void onShowLoading();

		public void onHideLoading();
	}

	public interface SoftKeyBoardListener {
		public void onShowSoftKeyBoard();

		public void onHideSoftKeyBoard();
	}

	public interface RequestBlockingListener {
		public void onRequestBlock();

		public void onReleaseBlock();
	}

	//
	// public interface ShowErrDialogListener{
	// public void showErrDialog(String msg);
	// }

	public void setShowErrDialogListener(
			ShowErrDialogListener showErrDialogListener) {
		this.showErrDialogListener = showErrDialogListener;
	}

	public void setLoadingListener(LoadingListener loadingListener) {
		this.loadingListener = loadingListener;
	}

	public void setSoftKeyBoardListener(
			SoftKeyBoardListener softKeyBoardListener) {
		this.softKeyBoardListener = softKeyBoardListener;
	}

	public void setRequestBlockingListener(
			RequestBlockingListener requestBlockingListener) {
		this.requestBlockingListener = requestBlockingListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	public boolean onBackPressed() {
		return false;
	}

	public void onKeyBoardShow(boolean show) {

	}

	public void enableIntercept(boolean enable, View view) {
		if (enable) {
			if (view != null) {

				// view.setOnTouchListener(new OnTouchListener(){
				//
				// @Override
				// public boolean onTouch(View arg0, MotionEvent arg1) {
				// if(arg1.getAction()==MotionEvent.ACTION_DOWN){
				// LogUtil.i(TAG, "Touched Intercept view." );
				// }
				//
				// return true;
				// }
				// });
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						LogUtil.i(TAG, "Clicked Intercept view.");
					}

				});

			}
		} else {
			if (view != null) {

				// view.setOnTouchListener(null);
				view.setOnClickListener(null);

			}
		}
	}

	public void addFragment(BaseFragment fragment, String name, boolean needBack, int containerId) {
		fragment.setShowErrDialogListener(showErrDialogListener);
		fragment.setLoadingListener(loadingListener);
		fragment.setRequestBlockingListener(requestBlockingListener);

		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(containerId, fragment, name);
		if (needBack)
			ft.addToBackStack(name);
		//ft.commit();
		ft.commitAllowingStateLoss(); // study later
		fm.executePendingTransactions();
		LogUtil.i(TAG, "addFragment(): fragment added. " + name);
	}

	public void removeFragment(BaseFragment fragment) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(fragment);
		//ft.commit();
		ft.commitAllowingStateLoss();
		fm.executePendingTransactions();
		LogUtil.i(TAG, "removeFragment(): fragment removed. " + fragment.getTag());
	}

	public void putViewInCenterVertical(final View v) {
		final ViewGroup parent = ((ViewGroup) v.getParent());
		parent.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {

						if (Build.VERSION.SDK_INT < 16) {
							parent.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						} else {
							parent.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						}
						// if(noResultParentHeight==0){
						// noResultParentHeight=parent.getHeight();
						// }
						int parentViewHeight = parent.getHeight();
						int tarViewHeight = v.getHeight();
						int topMargin = (parentViewHeight - tarViewHeight) / 2;
						FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
								.getLayoutParams();
						lp.setMargins(0, topMargin, 0, 0);
						lp.gravity = Gravity.CENTER_HORIZONTAL;
						v.setLayoutParams(lp);

						new Handler().post(new Runnable() {
							@Override
							public void run() {
								v.setVisibility(View.VISIBLE);
								parent.setVisibility(View.VISIBLE);

							}
						});

					}

				});
	}

}
