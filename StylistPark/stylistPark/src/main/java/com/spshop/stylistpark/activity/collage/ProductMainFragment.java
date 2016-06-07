package com.spshop.stylistpark.activity.collage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductClickListener;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductListSlideListener;
import com.spshop.stylistpark.adapter.DecorationAdapter.OnDecorationItemClickListener;

public class ProductMainFragment extends BaseFragment {
	
	private ProductDisplayFragment mProductDisplayFragment;
	private OnProductClickListener mOnProductClickListener;
	private OnDecorationItemClickListener mOnDecorationItemClickListener;
	private OnProductListSlideListener mOnProductListSlideListener;

	private boolean includeDecoration = true;

	public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
		this.mOnProductClickListener = onProductClickListener;
	}

	public void setOnDecorationItemClickListener(OnDecorationItemClickListener onDecorationItemClickListener) {
		this.mOnDecorationItemClickListener = onDecorationItemClickListener;
	}

	public void setOnProductListSlideListener(OnProductListSlideListener onProductListSlideListener) {
		this.mOnProductListSlideListener = onProductListSlideListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_product_main, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mProductDisplayFragment = new ProductDisplayFragment();
		mProductDisplayFragment.setIncludeDecoration(includeDecoration);
		if (mOnProductClickListener != null) {
			mProductDisplayFragment.setOnProductClickListener(mOnProductClickListener);
		}
		if (mOnDecorationItemClickListener != null) {
			mProductDisplayFragment.setOnDecorationItemClickListener(mOnDecorationItemClickListener);
		}
		if (softKeyBoardListener != null) {
			mProductDisplayFragment.setSoftKeyBoardListener(softKeyBoardListener);
		}
		if (mOnProductListSlideListener != null) {
			mProductDisplayFragment.setOnProductListSlideListener(mOnProductListSlideListener);
		}
		addFragment(mProductDisplayFragment, "ProductDiaplay", false, R.id.fragment_product_rl_main);
	}

	@Override
	public boolean onBackPressed() {
		return mProductDisplayFragment.onBackPressed();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ProductDisplayFragment.ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT
				|| requestCode == ProductDisplayFragment.ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT_DEC) {
			mProductDisplayFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onKeyBoardShow(boolean show) {
		if (mProductDisplayFragment != null)
			mProductDisplayFragment.onKeyBoardShow(show);
	}

	public void openMenu(String menuTypeID) {
		if (mProductDisplayFragment != null) {
			mProductDisplayFragment.openMenu(menuTypeID);
		}
	}

	public void dimMenu(boolean dim) {
		if (mProductDisplayFragment != null) {
			mProductDisplayFragment.dimMenu(dim);
		}
	}

	public void setIncludeDecoration(boolean includeDecoration) {
		this.includeDecoration = includeDecoration;
		if (null != mProductDisplayFragment) {
			mProductDisplayFragment.setIncludeDecoration(includeDecoration);
		}
	}

}
