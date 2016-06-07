package com.spshop.stylistpark.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.entity.RowObject;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;

/**
 * “搭配货品”商品列表适配器
 */
public class CollageProductListAdapter extends AppBaseAdapter<RowObject> {

	private static final String TAG = "CollageProductListAdapter";

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

	private DisplayImageOptions options;

	private long mLastClickTime;
	String dollorSign="";

	private List<Product> rawProductList;

	public enum DisplayMode {
		Product, Model
	}

	DisplayMode displayMode;

	public interface OnProductItemClickListener {
		public void onProductClick(Product product, DisplayMode mode);
	}

	private OnProductItemClickListener onProductClick;

	public void setOnProductClickListener(OnProductItemClickListener onProductClick) {
		this.onProductClick = onProductClick;
	}

	public void setRawProductList(List<Product> rawProductList) {
		this.rawProductList = rawProductList;
	}

	public List<Product> getRawProductList() {

		return rawProductList;
	}

	public CollageProductListAdapter(Context mContext) {
		super(mContext);
		displayMode = DisplayMode.Product;
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
		dollorSign = LangCurrTools.getCurrencyValue(mContext) + " ";
	}

	public void setDisplayMode(DisplayMode mode) {
		displayMode = mode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		RowObject row = (RowObject) getItem(position);
		Product colOneProduct = (Product) row.objectArr[0];
		Product colTwoProduct = (Product) row.objectArr[1];
		
		ChildHolder holder = null;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_list_commodity_three, null);
			holder.cellClothesIcon1_Layout = (ViewGroup) convertView.findViewById(R.id.cellClothesIcon1_Layout);
			holder.cellClothesIcon1_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesIcon1_ImageView);
			holder.cellClothesName1_TextView = (TextView) convertView.findViewById(R.id.cellClothesName1_TextView);
			holder.cellClothesDesc1_TextView = (TextView) convertView.findViewById(R.id.cellClothesDesc1_TextView);
			holder.cellClothesPrice1_TextView = (TextView) convertView.findViewById(R.id.cellClothesPrice1_TextView);
			holder.cellClothesRightLine1_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesRightLine1_ImageView);
			holder.cellClothesBottomLine1_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesBottomLine1_ImageView);
			holder.cellClothesIcon2_Layout = (ViewGroup) convertView.findViewById(R.id.cellClothesIcon2_Layout);
			holder.cellClothesIcon2_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesIcon2_ImageView);
			holder.cellClothesName2_TextView = (TextView) convertView.findViewById(R.id.cellClothesName2_TextView);
			holder.cellClothesDesc2_TextView = (TextView) convertView.findViewById(R.id.cellClothesDesc2_TextView);
			holder.cellClothesPrice2_TextView = (TextView) convertView.findViewById(R.id.cellClothesPrice2_TextView);
			holder.cellClothesRightLine2_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesRightLine2_ImageView);
			holder.cellClothesBottomLine2_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesBottomLine2_ImageView);
			holder.cellClothesIcon3_Layout = (ViewGroup) convertView.findViewById(R.id.cellClothesIcon3_Layout);
			holder.cellClothesIcon3_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesIcon3_ImageView);
			holder.cellClothesName3_TextView = (TextView) convertView.findViewById(R.id.cellClothesName3_TextView);
			holder.cellClothesDesc3_TextView = (TextView) convertView.findViewById(R.id.cellClothesDesc3_TextView);
			holder.cellClothesPrice3_TextView = (TextView) convertView.findViewById(R.id.cellClothesPrice3_TextView);
			holder.cellClothesBottomLine3_ImageView = (ImageView) convertView.findViewById(R.id.cellClothesBottomLine3_ImageView);

			holder.cellClothesIcon1_Layout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							LogUtil.i(TAG, "OnClick()-cellClothesIcon1_Layout" + SystemClock.elapsedRealtime());
							// Preventing multiple clicks, using threshold of 1 second
							if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
								LogUtil.i(TAG, "OnClick()-cellClothesIcon1_Layout- reject");
								return;
							}
							mLastClickTime = SystemClock.elapsedRealtime();
							if (onProductClick != null) {
								Product product = null;
								if (view.getTag() != null) {
									product = (Product) view.getTag();
								}
								onProductClick.onProductClick(product, displayMode);
							}
						}
					});
			holder.cellClothesIcon2_Layout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							LogUtil.i(TAG, "OnClick()-cellClothesIcon2_Layout" + SystemClock.elapsedRealtime());
							// Preventing multiple clicks, using threshold of 1 second
							if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
								LogUtil.i(TAG, "OnClick()-cellClothesIcon2_Layout- reject");
								return;
							}
							mLastClickTime = SystemClock.elapsedRealtime();
							if (onProductClick != null) {
								Product product = null;
								if (view.getTag() != null) {
									product = (Product) view.getTag();
								}
								onProductClick.onProductClick(product, displayMode);
							}
						}
					});

			holder.cellClothesIcon3_Layout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							LogUtil.i(TAG, "OnClick()-cellClothesIcon3_Layout" + SystemClock.elapsedRealtime());
							// Preventing multiple clicks, using threshold of 1 second
							if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
								LogUtil.i(TAG, "OnClick()-cellClothesIcon3_Layout- reject");
								return;
							}
							mLastClickTime = SystemClock.elapsedRealtime();
							if (onProductClick != null) {
								Product product = null;
								if (view.getTag() != null) {
									product = (Product) view.getTag();
								}
								onProductClick.onProductClick(product, displayMode);
							}
						}
					});
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		if (row != null) {
			if (colOneProduct != null) {
				holder.cellClothesIcon1_Layout.setTag(colOneProduct);
				ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + (displayMode == DisplayMode.Product ? 
							colOneProduct.getThumbUrl() : colOneProduct.getModelThumbUrl()), holder.cellClothesIcon1_ImageView, options);
				holder.cellClothesName1_TextView.setText(colOneProduct.getBrand());
				holder.cellClothesDesc1_TextView.setText(colOneProduct.getName());
				holder.cellClothesPrice1_TextView.setText(dollorSign + colOneProduct.getPrice());
				holder.cellClothesIcon1_Layout.setVisibility(View.VISIBLE);
			} else {
				holder.cellClothesIcon1_Layout.setVisibility(View.INVISIBLE);
			}
			if (colTwoProduct != null) {
				holder.cellClothesIcon2_Layout.setTag(colTwoProduct);
				ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + (displayMode == DisplayMode.Product ? 
						colTwoProduct.getThumbUrl() : colTwoProduct.getModelThumbUrl()), holder.cellClothesIcon2_ImageView, options);
				holder.cellClothesName2_TextView.setText(colTwoProduct.getBrand());
				holder.cellClothesDesc2_TextView.setText(colTwoProduct.getName());
				holder.cellClothesPrice2_TextView.setText(dollorSign + colTwoProduct.getPrice());
				holder.cellClothesIcon2_Layout.setVisibility(View.VISIBLE);
				holder.cellClothesRightLine2_ImageView.setVisibility(View.VISIBLE);
			} else {
				holder.cellClothesIcon2_Layout.setVisibility(View.INVISIBLE);
				holder.cellClothesRightLine2_ImageView.setVisibility(View.INVISIBLE);
			}
		}
		if (position == getCount() - 1) {
			onShowingLastItem.onShowingLastItem();
		}
		return convertView;
	}

	public static class ChildHolder extends SuperHolder {
		ViewGroup cellClothesIcon1_Layout;
		ImageView cellClothesIcon1_ImageView;
		TextView cellClothesName1_TextView;
		TextView cellClothesDesc1_TextView;
		TextView cellClothesPrice1_TextView;
		ImageView cellClothesBottomLine1_ImageView;
		ImageView cellClothesRightLine1_ImageView;

		ViewGroup cellClothesIcon2_Layout;
		ImageView cellClothesIcon2_ImageView;
		TextView cellClothesName2_TextView;
		TextView cellClothesDesc2_TextView;
		TextView cellClothesPrice2_TextView;
		ImageView cellClothesBottomLine2_ImageView;
		ImageView cellClothesRightLine2_ImageView;

		ViewGroup cellClothesIcon3_Layout;
		ImageView cellClothesIcon3_ImageView;
		TextView cellClothesName3_TextView;
		TextView cellClothesDesc3_TextView;
		TextView cellClothesPrice3_TextView;
		ImageView cellClothesBottomLine3_ImageView;
	}

}
