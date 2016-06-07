package com.spshop.stylistpark.activity.collage;

import java.util.ArrayList;
import java.util.List;

import org.lucasr.twowayview.widget.TwoWayView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.adapter.ProductMenuAdapter;
import com.spshop.stylistpark.adapter.ProductMenuAdapterV2;
import com.spshop.stylistpark.adapter.ProductMenuAdapterV2.OnProductMenuClickListener;
import com.spshop.stylistpark.adapter.ProductMenuAdapterV2.OnProductMenuUnClickListener;
import com.spshop.stylistpark.widgets.HorizontalListView;

public class ProductMenuFragment extends BaseFragment {

	private static final String TAG = "ProductMenuFragment";

	Context mContext;
	ViewGroup menuItem;
	HorizontalListView productCollectionMenu_ListView;
	ViewPager productCollection_menuViewPager;
	MenuPagerAdapter menuPagerAdapter;
	List<ProductMenu> menuItemList;
	ProductMenuAdapter productMenuAdapter;
	TwoWayView twoWayView;
	int NUM_OF_MENU_ITEM_SHOWING = 6;

	RecyclerView productCollectionMenu_View;
	ProductMenuAdapterV2 productMenuAdapterV2;
	LinearLayoutManager mLayoutManager;

	ImageView productCollectionMenu_backImageView;
	ImageView productCollectionMenu_nextImageView;

	OnProductMenuClickListener onProductMenuClickListener;
	OnProductMenuUnClickListener onProductMenuUnClickListener;

	boolean waitingForOpenMenu = false;
	String openMenuTypeId = null;

	ImageView productCollectionMenu_interceptImageView;

	boolean enableUnClickMenuItem = true;
	boolean includeDecoration = true;
	boolean noCosmetic = false;

	public static ProductMenuFragment newInstance(boolean enableUnClickMenuItem, 
			boolean includeDecoration, boolean noCosmetic) {
		ProductMenuFragment fragment = new ProductMenuFragment();
		fragment.setEnableUnClickMenuItem(enableUnClickMenuItem);
		fragment.setIncludeDecoration(includeDecoration);
		fragment.setNoCosmetic(noCosmetic);
		return fragment;
	}

	public void setOnProductMenuClickListener(OnProductMenuClickListener onProductMenuClickListener) {
		this.onProductMenuClickListener = onProductMenuClickListener;
	}

	public void setOnProductMenuUnClickListener(OnProductMenuUnClickListener onProductMenuUnClickListener) {
		this.onProductMenuUnClickListener = onProductMenuUnClickListener;
	}

	public void setEnableUnClickMenuItem(boolean enable) {
		enableUnClickMenuItem = enable;
	}

	public void setIncludeDecoration(boolean enable) {
		includeDecoration = enable;
	}

	public void setNoCosmetic(boolean noCosmetic) {
		this.noCosmetic = noCosmetic;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.d(TAG, "onCreateView()");

		// AppController.getInstance().getRequestQueue().getCache().clear();
		// AppController.getInstance().clearMemoryCache();

		mContext = getActivity();
		setupMenuItem(includeDecoration);
		View view = null;

		view = inflater.inflate(R.layout.fragment_product_menu, container, false);

		productCollectionMenu_backImageView = (ImageView) view.findViewById(R.id.productCollectionMenu_backImageView);
		productCollectionMenu_nextImageView = (ImageView) view.findViewById(R.id.productCollectionMenu_nextImageView);

		// productCollection_menuViewPager = (ViewPager)view.findViewById(R.id.productCollection_menuViewPager);
		// menuPagerAdapter = new MenuPagerAdapter(this.getChildFragmentManager());
		// productCollection_menuViewPager.setAdapter(menuPagerAdapter);

		productCollectionMenu_ListView = (HorizontalListView) view.findViewById(R.id.productCollectionMenu_ListView);
		// twoWayView = (TwoWayView) view.findViewById(R.id.twoWayView);

		productCollectionMenu_View = (RecyclerView) view.findViewById(R.id.productCollectionMenu_View);

		productMenuAdapterV2 = new ProductMenuAdapterV2(menuItemList, getActivity());
		productMenuAdapterV2.setOnProductMenuClickListener(onProductMenuClickListener);
		productMenuAdapterV2.setOnProductMenuUnClickListener(onProductMenuUnClickListener);
		productMenuAdapterV2.setEnableUnClickMenuItem(enableUnClickMenuItem);

		mLayoutManager = new LinearLayoutManager(mContext);
		mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		productCollectionMenu_View.setLayoutManager(mLayoutManager);
		productCollectionMenu_View.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int firstVisiblePosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
				int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
				int totalItem = productMenuAdapterV2.getItemCount();
				if (firstVisiblePosition == 0) {
					productCollectionMenu_backImageView.setImageResource(R.drawable.generator_items_back_dim);
				} else {
					productCollectionMenu_backImageView.setImageResource(R.drawable.generator_items_back);
				}
				if (lastVisiblePosition == totalItem - 1) {
					productCollectionMenu_nextImageView.setImageResource(R.drawable.generator_items_next_dim);
				} else {
					productCollectionMenu_nextImageView.setImageResource(R.drawable.generator_items_next);
				}
				Log.d(TAG, "first=" + firstVisiblePosition + " ,last=" + lastVisiblePosition);
			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					Log.i("TAG", "scrolling stopped...");
					if (waitingForOpenMenu) {
						if (productMenuAdapterV2 != null)
							productMenuAdapterV2.openMenu(openMenuTypeId);
						waitingForOpenMenu = false;
					}
				}
			}

		});

		ViewTreeObserver listViewTreeObserver = productCollectionMenu_View.getViewTreeObserver();
		if (listViewTreeObserver.isAlive()) {
			listViewTreeObserver.addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@SuppressWarnings("deprecation")
						@SuppressLint("NewApi")
						@Override
						public void onGlobalLayout() {
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
								productCollectionMenu_View.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							} else {
								productCollectionMenu_View.getViewTreeObserver().removeGlobalOnLayoutListener(this);
							}
							int lisViewWidth = productCollectionMenu_View.getWidth();
							int listViewItemWidth = lisViewWidth / NUM_OF_MENU_ITEM_SHOWING;

							// myAdapter = new MyAdapter(menuItemList);
							// myAdapter.setItemWidth(listViewItemWidth);
							// twoWayView.setAdapter(myAdapter);
							// twoWayView.setOrientation(Orientation.HORIZONTAL);

							productMenuAdapterV2.setItemWidth(listViewItemWidth);
							productCollectionMenu_View.setAdapter(productMenuAdapterV2);
						}
					});
		}

		productCollectionMenu_interceptImageView = (ImageView) view.findViewById(R.id.productCollectionMenu_interceptImageView);
		productCollectionMenu_interceptImageView.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}
				});

		enableIntercept(true, view);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "Lifecycle- onActivityCreated");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Lifecycle- onResume");
	}

	public ProductMenu getMenuItem(String menuTypeId) {
		if (menuItemList != null) {
			for (int i = 0; i < menuItemList.size(); i++) {
				if (menuItemList.get(i).productTypeId.equals(menuTypeId)) {
					return menuItemList.get(i);
				}
			}
		}
		return null;
	}

	public int getMenuItemPos(String menuTypeId) {
		if (menuItemList != null) {
			for (int i = 0; i < menuItemList.size(); i++) {
				if (menuItemList.get(i).productTypeId.equals(menuTypeId)) {
					return i;
				}
			}
		}
		return 0;
	}

	/**
	 * 创建商品类型集合
	 */
	private void setupMenuItem(boolean includeDecoration) {
		menuItemList = new ArrayList<ProductMenu>();
		menuItemList.add(new ProductMenu(R.drawable.generator_items_heart, R.drawable.generator_items_wh_heart, 
				mContext.getResources().getString(R.string.txt_favourite), AppConfig.PRODUCT_TYPE_FAVOURITE));
		
		menuItemList.add(new ProductMenu(R.drawable.generator_items_new, R.drawable.generator_items_wh_new, 
				mContext.getResources().getString(R.string.txt_new), AppConfig.PRODUCT_TYPE_NEW));
		
		menuItemList.add(new ProductMenu(R.drawable.generator_items_shirt, R.drawable.generator_items_wh_shirt, 
				mContext.getResources().getString(R.string.txt_shirt), AppConfig.PRODUCT_TYPE_UPPER_CLOTHES));

		menuItemList.add(new ProductMenu(R.drawable.generator_items_coats, R.drawable.generator_items_wh_coats, 
				mContext.getResources().getString(R.string.txt_coat), AppConfig.PRODUCT_TYPE_JACKET));

		menuItemList.add(new ProductMenu(R.drawable.generator_items_skirts, R.drawable.generator_items_wh_skirts, 
				mContext.getResources().getString(R.string.txt_skirt), AppConfig.PRODUCT_TYPE_DRESS));

		menuItemList.add(new ProductMenu(R.drawable.generator_items_trousers, R.drawable.generator_items_wh_trousers, 
				mContext.getResources().getString(R.string.txt_trouser), AppConfig.PRODUCT_TYPE_PLANT));

		menuItemList.add(new ProductMenu(R.drawable.generator_items_dress, R.drawable.generator_items_wh_dress, 
				mContext.getResources().getString(R.string.txt_dress), AppConfig.PRODUCT_TYPE_ONE_PIECE));

		if (!noCosmetic) {
			menuItemList.add(new ProductMenu(R.drawable.generator_items_beauty, R.drawable.generator_items_wh_beauty, 
					mContext.getResources().getString(R.string.txt_perfume), AppConfig.PRODUCT_TYPE_PERFUME));

			menuItemList.add(new ProductMenu(R.drawable.icon_createcollage_skincare, R.drawable.icon_createcollage_skincare_white, 
					mContext.getResources().getString(R.string.txt_skincare), AppConfig.PRODUCT_TYPE_SKINCARE));

			menuItemList.add(new ProductMenu(R.drawable.icon_createcollage_hair, R.drawable.icon_createcollage_hair_white, 
					mContext.getResources().getString(R.string.txt_haircare), AppConfig.PRODUCT_TYPE_HAIR_CARE));

			menuItemList.add(new ProductMenu(R.drawable.icon_createcollage_nail, R.drawable.icon_createcollage_nail_white, 
					mContext.getResources().getString(R.string.txt_nail), AppConfig.PRODUCT_TYPE_NAILS));

			menuItemList.add(new ProductMenu(R.drawable.icon_createcollage_makeup, R.drawable.icon_createcollage_makeup_white, 
					mContext.getResources().getString(R.string.txt_makeup), AppConfig.PRODUCT_TYPE_MAKE_UP));

			menuItemList.add(new ProductMenu(R.drawable.icon_createcollage_aroma, R.drawable.icon_createcollage_aroma_white, 
					mContext.getResources().getString(R.string.txt_homefragrance), AppConfig.PRODUCT_TYPE_HOME_FRAGRANCE));
		}

		menuItemList.add(new ProductMenu(R.drawable.generator_items_accessories, R.drawable.generator_items_wh_accessories, 
				mContext.getResources().getString(R.string.txt_accessorie), AppConfig.PRODUCT_TYPE_ACCESSORIES));

		menuItemList.add(new ProductMenu(R.drawable.generator_items_bags, R.drawable.generator_items_wh_bags, 
				mContext.getResources().getString(R.string.txt_bag), AppConfig.PRODUCT_TYPE_BAG));

		menuItemList.add(new ProductMenu(R.drawable.generator_items_shoes, R.drawable.generator_items_wh_shoes, 
				mContext.getResources().getString(R.string.txt_shoes), AppConfig.PRODUCT_TYPE_SHOES));

		if (includeDecoration) {
			menuItemList.add(new ProductMenu(R.drawable.generator_items_decoration, R.drawable.generator_items_wh_decoration, 
					mContext.getResources().getString(R.string.txt_decoration), AppConfig.PRODUCT_TYPE_DECORATION));
		}
	}

	public void resetMenu() {
		productMenuAdapterV2.resetMenu();
	}

	public void openMenu(String menuTypeID) {

		// productCollectionMenu_View.scrollBy(80, 0); //OK
		// productCollectionMenu_View.scrollToPosition(3);//not work
		// productCollectionMenu_View.smoothScrollToPosition(3);//not work
		// productCollectionMenu_View.smoothScrollBy(200, 0); //OK

		// mLayoutManager.scrollToPositionWithOffset(3,0); //OK
		// mLayoutManager.scrollToPosition(3); // Not OK

		int targetItemPos = productMenuAdapterV2.getMenuItemIndex(menuTypeID);
		Log.d(TAG, "openMenu(): menuTypeID=" + menuTypeID);
		if (targetItemPos == -1) {
			return;
		}
		if (waitingForOpenMenu) {
			Log.d(TAG, "event- openMenu-rejected");
			return;
		}
		if (productMenuAdapterV2 != null && productMenuAdapterV2.isMenuTypeOnScreen(menuTypeID)) {
			productMenuAdapterV2.openMenu(menuTypeID);
		} else {
			waitingForOpenMenu = true;
			openMenuTypeId = menuTypeID;

			// first visible item
			int firstVisibleItemIdx = mLayoutManager.findFirstVisibleItemPosition();
			View firstItem = productCollectionMenu_View.getChildAt(0);
			int firstItemWidth = firstItem.getWidth();
			int firstItemLeft = firstItem.getLeft();

			int numOfItemB4 = targetItemPos - firstVisibleItemIdx;
			// int menuViewWidth=productCollectionMenu_View.getWidth();
			// int targetX=(menuViewWidth-firstItemWidth)/2;
			int targetX;
			if (numOfItemB4 <= 0) {
				targetX = 0;
			} else {
				targetX = firstItemWidth * (NUM_OF_MENU_ITEM_SHOWING - 1);
			}
			int scrollDistant = (numOfItemB4 * firstItemWidth) - targetX + firstItemLeft;
			productCollectionMenu_View.smoothScrollBy(scrollDistant, 0);
		}

	}

	public void scrollMenuItemIfNotOnScreen(int pos) {
		if (pos > NUM_OF_MENU_ITEM_SHOWING - 1)
			mLayoutManager.scrollToPositionWithOffset(pos, 0);
	}

	public void dimMenu(boolean dim) {
		productCollectionMenu_interceptImageView.setVisibility(dim ? View.VISIBLE : View.GONE);
	}

	private class MenuPagerAdapter extends FragmentStatePagerAdapter {
		public MenuPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new ProductMenuFragment();
		}

		@Override
		public int getCount() {
			return 4;
		}
	}

	public void refreshMenu() {
		if (productMenuAdapterV2 != null)
			productMenuAdapterV2.notifyDataSetChanged();
	}

	public static class ProductMenu {
		
		public int drawableIdNormal;
		public int drawableIdDown;
		public String name;
		public boolean isClicked;
		public String productTypeId;

		public ProductMenu() {
			
		}

		public ProductMenu(int drawableIdNormal, int drawableIdDown, String name, String productTypeId) {
			this.drawableIdNormal = drawableIdNormal;
			this.drawableIdDown = drawableIdDown;
			this.name = name;
			isClicked = false;
			this.productTypeId = productTypeId;
		}

		public String getProductCatId() {
			return productTypeId;
		}
	}

}