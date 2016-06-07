package com.spshop.stylistpark.activity.collage;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.adapter.AppBaseAdapter.OnShowingLastItem;
import com.spshop.stylistpark.adapter.CollageProductListAdapter;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.DisplayMode;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.OnProductItemClickListener;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.entity.RowObject;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.InterceptTouchListView;
import com.spshop.stylistpark.widgets.InterceptTouchListView.OnInterceptTouchListener;

public class ProductListFragment extends BaseFragment {

	private static final String TAG = "ProductListFragment";

	private InterceptTouchListView clothes_ListView;
	private CollageProductListAdapter clothesAdapter;
	int pos = 0;
	int offset = 0;
	public Context mContext;

	private static final int GET_PRODUCT_LIST_SUCCESS = 5601;
	private static final int GET_PRODUCT_LIST_FAIL = 5602;
	private static final int GET_PRODUCT_LIST_FAIL_NO_CONNECTION = 5603;
	private ProductListThread mThreadProductList = null;

	List<Product> productList;
	String endKey;

	// for saved state
	List<RowObject> preRowProductList;
	List<Product> preRawProductList;

	String productType;
	boolean isLoading;
	OnProductItemClickListener onProductItemClickListener;

	// filter
	String color;
	String brand;
	String price;
	String keyword;
	DisplayMode displayMode = DisplayMode.Product;

	// err
	TextView clothes_errNoResultTextView;
	ViewGroup clothes_errNoNetworkLayout;
	Button errNoNetwork_Layout;

	// loading
	ProgressBar clothes_progressBar;

	ViewGroup clothes_dummyLayout;
	ViewGroup clothes_root;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_PRODUCT_LIST_SUCCESS:
				List<Product> preProductList = clothesAdapter.getRawProductList();
				if (preProductList == null) {
					preProductList = new ArrayList<Product>();
				}
				// productList will never be null because API return new List if GET_PRODUCT_LIST_SUCCESS.
				preProductList.addAll(productList);
				clothesAdapter.clearDataList();
				clothesAdapter.addDataList(CommonTools.convertToRowObject(preProductList, 2));
				clothesAdapter.setRawProductList(preProductList);
				
				if (productList.size() == 0) {
					showNoResult();
				}
				isLoading = false;
				break;
			case GET_PRODUCT_LIST_FAIL:
				isLoading = false;
				if (showErrDialogListener != null) {
					showErrDialogListener.showErrDialog(null);
				}
				break;
			case GET_PRODUCT_LIST_FAIL_NO_CONNECTION:
				showNoNetworkErrMsg();
				isLoading = false;
				break;
			default:
				isLoading = false;
				break;
			}
			if (loadingListener != null)
				loadingListener.onHideLoading();
			clothes_progressBar.setVisibility(View.GONE);
		}
	};

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void setOnProductClickListener(
			OnProductItemClickListener onProductItemClickListener) {
		this.onProductItemClickListener = onProductItemClickListener;
	}

	public static ProductListFragment newInstance(String productType) {
		ProductListFragment fragment = new ProductListFragment();
		fragment.setProductType(productType);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		View view = inflater.inflate(R.layout.fragment_product_list, container, false);
		clothes_root = (ViewGroup) view.findViewById(R.id.clothes_root);
		clothes_progressBar = (ProgressBar) view.findViewById(R.id.clothes_progressBar);
		clothes_progressBar.setVisibility(View.GONE);
		clothes_dummyLayout = (ViewGroup) view.findViewById(R.id.clothes_dummyLayout);
		clothes_errNoResultTextView = (TextView) view.findViewById(R.id.clothes_errNoResultTextView);
		clothes_errNoNetworkLayout = (ViewGroup) view.findViewById(R.id.clothes_errNoNetworkLayout);
		clothes_errNoNetworkLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		errNoNetwork_Layout = (Button) view.findViewById(R.id.errNoNetwork_Layout);
		errNoNetwork_Layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ProductDisplayFragment) getParentFragment()).clearHistory();
				((ViewGroup) clothes_errNoNetworkLayout.getParent()).setVisibility(View.INVISIBLE);
				if (loadingListener != null)
					loadingListener.onShowLoading();
				mThreadProductList = new ProductListThread(productType);
				mThreadProductList.start();
			}
		});
		clothes_ListView = (InterceptTouchListView) view.findViewById(R.id.clothes_ListView);
		// View footerView = inflater.inflate(R.layout.layout_list_footer_load, null, false);
		// clothes_ListView.addFooterView(footerView);

		clothesAdapter = new CollageProductListAdapter(getActivity());
		clothesAdapter.setDisplayMode(displayMode);
		clothesAdapter.setOnShowingLastItem(new OnShowingLastItem() {
			@Override
			public void onShowingLastItem() {
				loadMore();
			}
		});
		clothes_ListView.setAdapter(clothesAdapter);
		clothes_ListView.setOnInterceptTouchListener(
				new OnInterceptTouchListener() {
					int downY = 0;
					int movementInterval = 20;
					boolean needDetect;

					@Override
					public void onInterceptTouch(MotionEvent event) {
						LogUtil.i(TAG, "event.getAction()=" + event.getAction());
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							downY = (int) event.getY();
							needDetect = true;
							LogUtil.i(TAG, "onTouch()-Down y=" + downY);
							break;
						case MotionEvent.ACTION_MOVE:
							int curY = (int) event.getY();
							if (needDetect && downY - curY >= movementInterval) {
								needDetect = false;
								if (isShowingLastItem()) {
									loadMore();
								}
								LogUtil.i(TAG, "onTouch() reach movement interval");
							}
							LogUtil.i(TAG, "onTouch()-Move y=" + curY);
							break;
						}
					}
				});
		return view;
	}

	public boolean isShowingLastItem() {
		boolean result = false;
		if (clothes_ListView.getLastVisiblePosition() == clothesAdapter.getCount() - 1) {
			result = true;
		}
		return result;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.i(TAG, "Lifecycle- onActivityCreated");
		if (preRowProductList == null || preRawProductList == null) {
			if (loadingListener != null)
				loadingListener.onShowLoading();
			mThreadProductList = new ProductListThread(productType);
			mThreadProductList.start();
		} else {
			if (preRowProductList.size() == 0 || preRawProductList.size() == 0) {
				showNoResult();
			} else {
				clothesAdapter.addDataList(preRowProductList);
				clothesAdapter.setRawProductList(preRawProductList);
				// clothes_ListView.setAdapter(clothesAdapter);
				clothes_ListView.setSelectionFromTop(pos, offset);
			}
		}
		if (onProductItemClickListener != null) {
			clothesAdapter.setOnProductClickListener(onProductItemClickListener);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "Lifecycle- onResume");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void loadMore() {
		if (isLoading) {
			LogUtil.i(TAG, "loadMore() isLoading");
			return;
		} else {
			isLoading = true;
		}
		if (endKey != null && !endKey.equals("-1")) {
			clothes_progressBar.setVisibility(View.VISIBLE);
			mThreadProductList = new ProductListThread(productType);
			mThreadProductList.start();
			LogUtil.i(TAG, "Showing last item.");
		}
	}

	public void setItemPos(int pos) {
		this.pos = pos;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setRowProductList(List<RowObject> preRowProductList) {
		this.preRowProductList = preRowProductList;
	}

	public void setRawProductList(List<Product> preRawProductList) {
		this.preRawProductList = preRawProductList;
	}

	public void setEndKey(String endKey) {
		this.endKey = endKey;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public int getCurrentItemPos() {
		try {
			return clothes_ListView.getFirstVisiblePosition();
		} catch (Exception e) {
			return 0;
		}
	}

	public int getCurrentItemOffSet() {
		try {
			return clothes_ListView.getChildAt(0).getTop();
		} catch (Exception e) {
			return 0;
		}

	}

	public List<RowObject> getRowProductList() {
		if (clothesAdapter != null)
			return clothesAdapter.getDataList();
		else
			return null;
	}

	public List<Product> getRawProductList() {
		if (clothesAdapter != null)
			return clothesAdapter.getRawProductList();
		else
			return null;
	}

	public String getEndKey() {
		return endKey;
	}

	public void switchDisplayMode(DisplayMode mode) {
		if (clothesAdapter.getDataList() == null || clothesAdapter.getDataList().size() == 0)
			return;
		// int pos=getCurrentItemPos();
		// int offset=getCurrentItemOffSet();
		clothesAdapter.setDisplayMode(mode);
		// clothes_ListView.setAdapter(clothesAdapter);
		// clothes_ListView.setSelectionFromTop(pos, offset);
		clothesAdapter.notifyDataSetChanged();
	}

	public void showNoNetworkErrMsg() {
		if (clothesAdapter.getDataList() != null && clothesAdapter.getDataList().size() > 0) {
			if (showErrDialogListener != null) {
				showErrDialogListener.showErrDialog(getActivity().getResources().getString(R.string.network_fault));
			}
		} else {
			showErrNoNetworkLayout();
		}
	}

	public void showErrNoNetworkLayout() {
		ViewGroup parent = ((ViewGroup) clothes_errNoNetworkLayout.getParent());
		int parentViewHeight = parent.getHeight();
		int errLayoutHeight = clothes_errNoNetworkLayout.getHeight();
		int topMargin = (parentViewHeight - errLayoutHeight) / 2;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) clothes_errNoNetworkLayout.getLayoutParams();
		lp.setMargins(0, topMargin, 0, 0);
		clothes_errNoNetworkLayout.setLayoutParams(lp);
		parent.setVisibility(View.VISIBLE);
	}

	public void showNoResult() {
		putViewInCenterVertical(clothes_errNoResultTextView);
	}

	public void showDummyFooter(boolean show) {
		clothes_dummyLayout.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private class ProductListThread extends Thread {
		
		private String mProductTypeId;

		public ProductListThread(String typeId) {
			this.mProductTypeId = typeId;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			Message msg = new Message();
			try {
				productList = null;
				UserManager um = UserManager.getInstance();
				LogUtil.i(TAG, "ProductListThread endKey=" + endKey);
				LogUtil.i(TAG, "ProductListThread userId=" + um.getUserId());
				LogUtil.i(TAG, "ProductListThread mProductTypeId=" + mProductTypeId);
				LogUtil.i(TAG, "ProductListThread Filter color=" + color + " brand=" + brand + " price=" + price + " keyword=" + keyword);
				Object[] objArr;
				ServiceContext sc = ServiceContext.getServiceContext();
				if (mProductTypeId.equals(AppConfig.PRODUCT_TYPE_SUGGESTED)) 
				{
					objArr = sc.getCollageProductList(null, null, color, brand, price, keyword, true, endKey);
				} 
				else if (mProductTypeId.equals(AppConfig.PRODUCT_TYPE_FAVOURITE)) 
				{
					objArr = sc.getCollageProductList(um.getUserId(), mProductTypeId, color, brand, price, keyword, null, endKey);
				} 
				else {
					objArr = sc.getCollageProductList(null, mProductTypeId, color, brand, price, keyword, null, endKey);
				}
				if (objArr != null && objArr.length > 1) {
					endKey = (String) objArr[0];
					productList = (ArrayList<Product>) objArr[1];
					msg.what = GET_PRODUCT_LIST_SUCCESS;
					LogUtil.i(TAG, "return endKey=" + endKey);
				} else {
					msg.what = GET_PRODUCT_LIST_FAIL;
				}
			} catch (ConnectException e) {
				ExceptionUtil.handle(mContext, e);
				msg.what = GET_PRODUCT_LIST_FAIL_NO_CONNECTION;
				productList = null;
			} catch (Exception e) {
				ExceptionUtil.handle(mContext, e);
				msg.what = GET_PRODUCT_LIST_FAIL;
				productList = null;
			}
			if (!interrupted()) {
				mHandler.sendMessage(msg);
			}
		}
	}

}
