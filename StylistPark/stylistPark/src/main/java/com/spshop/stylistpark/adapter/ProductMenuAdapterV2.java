package com.spshop.stylistpark.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.R.color;
import com.spshop.stylistpark.activity.collage.ProductMenuFragment.ProductMenu;

public class ProductMenuAdapterV2 extends RecyclerView.Adapter<ProductMenuAdapterV2.ViewHolder> {

	private static final String TAG = "ProductMenuAdapterV2";
	
	public interface OnProductMenuClickListener {
		public void onProductMenuClick(ProductMenu productMenu);
	}

	public interface OnProductMenuUnClickListener {
		public void onProductMenuUnClick(ProductMenu productMenu);
	}

	private List<ProductMenu> dataList;
	private List<ViewHolder> holderList;
	private Context context;

	private int itemWidth;
	private long mLastClickTime;
	boolean enableUnClickMenuItem = true;

	private OnProductMenuClickListener onProductMenuClick;
	private OnProductMenuUnClickListener onProductMenuUnClick;

	public void setOnProductMenuClickListener(OnProductMenuClickListener onProductMenuClick) {
		this.onProductMenuClick = onProductMenuClick;
	}

	public void setOnProductMenuUnClickListener(OnProductMenuUnClickListener onProductMenuUnClick) {
		this.onProductMenuUnClick = onProductMenuUnClick;
	}

	public ProductMenuAdapterV2(List<ProductMenu> dataList, Context context) {
		this.dataList = dataList;
		holderList = new ArrayList<ViewHolder>();
		itemWidth = 0;
		this.context = context;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public ProductMenuAdapterV2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.item_product_menu, parent, false);
		// set the view's size, margins, paddings and layout parameters
		ViewHolder vh = new ViewHolder(v);
		if (itemWidth != 0) {
			vh.changeItemSize(itemWidth);
		}
		holderList.add(vh);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		ProductMenu item = dataList.get(position);

		holder.obj = item;
		holder.pos = position;
		holder.cellProductMenu_TextView.setText(item.name);

		if (item.isClicked) {
			holder.cellProductMenu_RelativeLayout.setBackgroundColor(context.getResources().getColor(color.product_menu_bg_down_color));
			holder.cellProductMenu_ImageView.setImageResource(item.drawableIdDown);
			holder.cellProductMenu_TextView.setTextColor(context.getResources().getColor(color.ui_bg_color_white));
		} else {
			holder.cellProductMenu_RelativeLayout.setBackgroundColor(context.getResources().getColor(color.product_menu_bg_up_color));
			holder.cellProductMenu_ImageView.setImageResource(item.drawableIdNormal);
			holder.cellProductMenu_TextView.setTextColor(context.getResources().getColor(color.ui_bg_color_black));
		}

		holder.cellProductMenu_RelativeLayout.setTag(holder);
		holder.cellProductMenu_RelativeLayout
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						ViewHolder holder = (ViewHolder) view.getTag();
						Log.d(TAG, "Menu Item Clicked Pos=" + holder.pos);

						// Preventing multiple clicks, using threshold of 1 second
						if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
							Log.d(TAG, "Menu Item Clicked Pos=" + holder.pos + " -rejected");
							return;
						}
						mLastClickTime = SystemClock.elapsedRealtime();
						Log.d(TAG, "Menu Item Vaild Clicked Pos=" + holder.pos);

						ProductMenu item = (ProductMenu) holder.obj;
						if (item.isClicked && enableUnClickMenuItem) {
							item.isClicked = false;
							holder.cellProductMenu_RelativeLayout.setBackgroundColor(
									context.getResources().getColor(color.product_menu_bg_up_color));
							holder.cellProductMenu_ImageView.setImageResource(item.drawableIdNormal);
							holder.cellProductMenu_TextView.setTextColor(
									context.getResources().getColor(color.ui_bg_color_black));
							if (onProductMenuUnClick != null) {
								onProductMenuUnClick.onProductMenuUnClick(item);
							}
						} else {
							resetMenu();
							item.isClicked = true;
							holder.cellProductMenu_RelativeLayout.setBackgroundColor(
									context.getResources().getColor(color.product_menu_bg_down_color));
							holder.cellProductMenu_ImageView.setImageResource(item.drawableIdDown);
							holder.cellProductMenu_TextView.setTextColor(
									context.getResources().getColor(color.ui_bg_color_white));
							if (onProductMenuClick != null) {
								onProductMenuClick.onProductMenuClick(item);
							}
						}
						view.setSoundEffectsEnabled(true);
					}
				});
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewGroup cellProductMenu_RelativeLayout;
		public ImageView cellProductMenu_ImageView;
		public TextView cellProductMenu_TextView;
		public Object obj;
		public int pos;

		public ViewHolder(View v) {
			super(v);
			cellProductMenu_RelativeLayout = (ViewGroup) v.findViewById(R.id.cellProductMenu_RelativeLayout);
			cellProductMenu_ImageView = (ImageView) v.findViewById(R.id.cellProductMenu_ImageView);
			cellProductMenu_TextView = (TextView) v.findViewById(R.id.cellProductMenu_TextView);
		}

		public void changeItemSize(int width) {
			if (width != 0) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) cellProductMenu_RelativeLayout.getLayoutParams();
				lp.width = width;
			}
		}

	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return dataList.size();
	}

	public void setItemWidth(int width) {
		this.itemWidth = width;
	}

	public void clear() {
		holderList.clear();
	}

	public void resetMenu() {
		for (int i = 0; i < holderList.size(); i++) {
			ViewHolder holder = holderList.get(i);
			ProductMenu item = (ProductMenu) holder.obj;
			holder.cellProductMenu_RelativeLayout.setBackgroundColor(
					context.getResources().getColor(color.product_menu_bg_up_color));
			holder.cellProductMenu_ImageView.setImageResource(item.drawableIdNormal);
			holder.cellProductMenu_TextView.setTextColor(
					context.getResources().getColor(color.ui_bg_color_black));
		}
		for (int i = 0; i < dataList.size(); i++) {
			dataList.get(i).isClicked = false;
		}
	}

	public void openMenu(String menuTypeID) {
		if (menuTypeID == null || menuTypeID.isEmpty())
			return;
		for (int i = 0; i < holderList.size(); i++) {
			ViewHolder holder = holderList.get(i);
			ProductMenu item = (ProductMenu) holder.obj;
			if (item.productTypeId.equals(menuTypeID)) {
				holder.cellProductMenu_RelativeLayout.setSoundEffectsEnabled(false);
				holder.cellProductMenu_RelativeLayout.performClick();
				break;
			}
		}
	}

	public int getMenuItemIndex(String menuTypeID) {
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).productTypeId.equals(menuTypeID)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isMenuTypeOnScreen(String menuTypeID) {
		boolean result = false;
		if (menuTypeID == null || menuTypeID.isEmpty())
			return result;
		for (int i = 0; i < holderList.size(); i++) {
			ViewHolder holder = holderList.get(i);
			ProductMenu item = (ProductMenu) holder.obj;
			if (item.productTypeId.equals(menuTypeID)) {
				ViewGroup holderView = ((ViewGroup) holder.cellProductMenu_RelativeLayout.getParent());
				ViewGroup containerView = ((ViewGroup) holderView.getParent());
				if (containerView == null) {
					for (int j = 0; i < holderList.size(); j++) {
						containerView = ((ViewGroup) holderList.get(j).cellProductMenu_RelativeLayout.getParent().getParent());
						if (containerView != null)
							break;
					}
				}
				int left = holderView.getLeft();
				int right = holderView.getRight();
				int containterViewWidth = containerView.getWidth();
				if (left >= 0 && right <= containterViewWidth) {
					result = true;
				}
				break;
			}
		}
		return result;
	}

	public void setEnableUnClickMenuItem(boolean enable) {
		enableUnClickMenuItem = enable;
	}

}
