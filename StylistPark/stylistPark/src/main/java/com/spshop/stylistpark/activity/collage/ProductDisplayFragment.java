package com.spshop.stylistpark.activity.collage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.collage.ColorActivity.ColorMode;
import com.spshop.stylistpark.activity.collage.ProductMenuFragment.ProductMenu;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.DisplayMode;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.OnProductItemClickListener;
import com.spshop.stylistpark.adapter.DecorationAdapter.OnDecorationItemClickListener;
import com.spshop.stylistpark.adapter.ProductMenuAdapterV2.OnProductMenuClickListener;
import com.spshop.stylistpark.adapter.ProductMenuAdapterV2.OnProductMenuUnClickListener;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.Decoration;
import com.spshop.stylistpark.entity.FilterColor;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.entity.RowObject;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.UserTracker;
import com.spshop.stylistpark.widgets.EditTextBackEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDisplayFragment extends BaseFragment {

	private static final String TAG = "ProductDisplayFragment";
	
	public static final int ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT = 1000;
	public static final int ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT_DEC = 1001;

	Context mContext;
	LinearLayout productDisplay_mainLinearLayout;
	FrameLayout productDisplay_listContainer;
	FrameLayout productDisplay_MenuContainer;
	ProductListFragment productListFragment;
	ProductMenuFragment productMenuFragment;
	DecorationListFragment decorationListFragment;
	ViewGroup productDisplay_searchBarLayout;
	ViewGroup productDisplay_productTopBarLayout;
	ViewGroup productDisplay_decorationTopBarLayout;
	ViewGroup productDisplay_topBarLayout;
	ViewGroup productDisplay_modeLayout;
	ViewGroup productDisplay_clothModeLayout;
	ViewGroup productDisplay_modelModeLayout;

	ImageView productDisplay_clothModeImageView;
	ImageView productDisplay_modelModeImageView;
	ImageView productDisplay_bottomLineImageView;
	ViewGroup productDisplay_filterLayout;
	ViewGroup productDisplay_mainLayout;
	View productDisplay_interceptLayout;
	private OnProductListSlideListener onProductListSlideListener;
	OnProductItemClickListener onProductItemClickListener;
	OnProductClickListener onProductClickListener;
	OnDecorationItemClickListener onDecorationItemClickListener;
	OnDecorationItemClickListener onDecorationItemClickListenerDisplay;
	// EditText productDisplay_searchEditText;
	EditTextBackEvent productDisplay_searchEditText;
	EditText productDisplay_dummyEditText;
	ImageView productDisplay_searchIconImageView;
	ImageView productDisplay_delIconImageView;

	String curProductTypeCats = null;

	// filter result
	ViewGroup productDisplay_filterResultLayout;
	TextView productDisplay_filterResultTextView;

	// product variables passed among activities
	BrandEntity selectedBrand = null;
	FilterColor selectedColor = null;
	int minPrice = 0;
	int maxPrice = FilterMainActivity.MAX_AMOUNT;
	String priceSubmit;
	float leftThumbX = -1f;
	float rightThumbX = -1f;
	String keyword = null;
	String tmpKeyword = null;
	DisplayMode displayMode = DisplayMode.Product;

	// decoration top bar
	EditTextBackEvent decoration_searchEditText;
	ImageView decoration_searchIconImageView;
	ImageView decoration_delIconImageView;

	// decoration variables passed among activities
	String decKeyword = null;
	String decTmpKeyword;
	FilterColor decSelectedColor = null;
	ViewGroup decoration_filterResultLayout;
	ViewGroup decoration_filterLayout;
	TextView decoration_filterResultTextView;

	HashMap<ProductMenu, ProductHistory> productHistoryMap = new HashMap<ProductMenu, ProductHistory>();
	DecorationHistory decorationHistory;

	ProductMenu curProductMenu;

	CustomGestureDetector editTextGestureDetector;
	GestureDetector editTextDetector;

	private long mLastClickTime;

	boolean searchResultWaitForKeyboardClose = false;
	boolean isAnimating = false;

	boolean enableUnClickMenuItem = true;
	boolean includeDecoration = true;
	private boolean noCosmetic = false;

	private String dollarSign;

	public interface OnProductClickListener {

		public void onProductClick(Product product, DisplayMode mode,
				String productCats);

	}

	public void setEnableUnClickMenuItem(boolean enable) {
		enableUnClickMenuItem = enable;
	}

	public void setIncludeDecoration(boolean enable) {
		includeDecoration = enable;
	}

	public void setOnProductClickListener(
			OnProductClickListener onProductClickListener) {
		this.onProductClickListener = onProductClickListener;
	}

	public void setOnDecorationItemClickListener(
			OnDecorationItemClickListener onDecorationItemClickListener) {
		this.onDecorationItemClickListener = onDecorationItemClickListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dollarSign = LangCurrTools.getCurrencyValue(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.i(TAG, "onCreateView()");
		mContext = getActivity();
		View view = null;
		view = inflater.inflate(R.layout.fragment_product_display, container, false);

		/****
		 * Decoration top bar
		 ****/
		productDisplay_decorationTopBarLayout = (ViewGroup) view.findViewById(R.id.productDisplay_decorationTopBarLayout);
		decoration_filterResultLayout = (ViewGroup) view.findViewById(R.id.decoration_filterResultLayout);
		decoration_filterResultTextView = (TextView) view.findViewById(R.id.decoration_filterResultTextView);
		decoration_searchEditText = (EditTextBackEvent) view.findViewById(R.id.decoration_searchEditText);
		decoration_searchEditText.setOnEditorActionListener(getSearchEditTextEditorActionListener());
		decoration_searchEditText.setOnTouchListener(getSearchEditTextOnTouchListener());
		decoration_searchEditText.addTextChangedListener(getSearchEditTextTextWatcher());
		decoration_searchIconImageView = (ImageView) view.findViewById(R.id.decoration_searchIconImageView);
		decoration_searchIconImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		decoration_delIconImageView = (ImageView) view.findViewById(R.id.decoration_delIconImageView);
		decoration_delIconImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				decoration_searchEditText.setText("");
				if (!decoration_searchEditText.hasFocus()) {
					decKeyword = null;
					decTmpKeyword = null;
					refreshDecorationFilterResult();
				}
			}
		});
		decoration_filterLayout = (ViewGroup) view.findViewById(R.id.decoration_filterLayout);
		decoration_filterLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_DECORATION_FILTER, null);
				LogUtil.i(TAG, "filter button clicked.");
				Intent intent = new Intent(getActivity(), ColorActivity.class);
				intent.putExtra(ColorActivity.INTENT_SELECTED_COLOR, decSelectedColor);
				intent.putExtra(ColorActivity.INTENT_COLOR_MODE, ColorMode.Decoration);
				startActivityForResult(intent, ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT_DEC);
			}
		});

		/****
		 * Product top bar
		 ****/
		productDisplay_productTopBarLayout = (ViewGroup) view.findViewById(R.id.productDisplay_productTopBarLayout);
		productDisplay_modeLayout = (ViewGroup) view.findViewById(R.id.productDisplay_modeLayout);
		productDisplay_topBarLayout = (ViewGroup) view.findViewById(R.id.productDisplay_topBarLayout);
		productDisplay_mainLayout = (ViewGroup) view.findViewById(R.id.productDisplay_mainLayout);
		productDisplay_searchBarLayout = (ViewGroup) view.findViewById(R.id.productDisplay_searchBarLayout);
		productDisplay_searchEditText = (EditTextBackEvent) view.findViewById(R.id.productDisplay_searchEditText);
		productDisplay_searchEditText.setOnEditorActionListener(getSearchEditTextEditorActionListener());
		productDisplay_searchEditText.setOnTouchListener(getSearchEditTextOnTouchListener());
		productDisplay_searchEditText.addTextChangedListener(getSearchEditTextTextWatcher());
		productDisplay_searchIconImageView = (ImageView) view.findViewById(R.id.productDisplay_searchIconImageView);
		productDisplay_searchIconImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		productDisplay_delIconImageView = (ImageView) view.findViewById(R.id.productDisplay_delIconImageView);
		productDisplay_delIconImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				productDisplay_searchEditText.setText("");
				if (!productDisplay_searchEditText.hasFocus()) {
					keyword = null;
					tmpKeyword = null;
					refreshFilterResult();
				}
			}
		});

		// GestureDetector
		editTextGestureDetector = new CustomGestureDetector();
		// Create a GestureDetector
		editTextDetector = new GestureDetector(getActivity(), editTextGestureDetector);
		// Attach listeners that'll be called for double-tap and related gestures
		editTextDetector.setOnDoubleTapListener(editTextGestureDetector);

		productDisplay_mainLinearLayout = (LinearLayout) view.findViewById(R.id.productDisplay_mainLinearLayout);
		productDisplay_mainLinearLayout.getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					boolean isFristTime = true;
					boolean isKeyBoardOpen = false;
					int initHeight;

					@Override
					public void onGlobalLayout() {
						LogUtil.i(TAG, "Key Board onGlobalLayout");
						if (isFristTime) {
							initHeight = productDisplay_mainLinearLayout.getHeight();
							if (initHeight != 0)
								isFristTime = false;
						}
						int currentHeight = productDisplay_mainLinearLayout.getHeight();
						LogUtil.i(TAG, "Key Board initHeight=" + initHeight + ", currentHeight=" + currentHeight);
						if (currentHeight != initHeight && !isKeyBoardOpen) {
							LogUtil.i(TAG, "Key Board Open.");
							isKeyBoardOpen = true;
							if (softKeyBoardListener != null) {
								softKeyBoardListener.onShowSoftKeyBoard();
							}
							onKeyBoardShow(true);
						} else if (currentHeight == initHeight && isKeyBoardOpen) {
							LogUtil.i(TAG, "Key Board Close.");
							isKeyBoardOpen = false;
							if (softKeyBoardListener != null) {
								softKeyBoardListener.onHideSoftKeyBoard();
							}
							onKeyBoardShow(false);
						}
						LogUtil.i(TAG, "Key Show "+productDisplay_mainLayout.getRootView().getHeight()+" "+productDisplay_mainLayout.getHeight());
					}
				});

		productDisplay_listContainer = (FrameLayout) view.findViewById(R.id.productDisplay_listContainer);
		productDisplay_MenuContainer = (FrameLayout) view.findViewById(R.id.productDisplay_MenuContainer);
		productDisplay_clothModeLayout = (ViewGroup) view.findViewById(R.id.productDisplay_clothModeLayout);
		productDisplay_clothModeLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_PRODUCT_VIEW, null);

						displayMode = DisplayMode.Product;
						productDisplay_clothModeLayout.setBackgroundResource(R.drawable.shape_generator_top_bt_left_dn_bg);
						productDisplay_modelModeLayout.setBackgroundResource(R.drawable.shape_generator_top_bt_right_up_bg);
						((ImageView) productDisplay_clothModeLayout.getChildAt(0)).setImageResource(R.drawable.generator_filter_wh_cloth);
						((ImageView) productDisplay_modelModeLayout.getChildAt(0)).setImageResource(R.drawable.generator_filter_girl);
						if (productListFragment != null) {
							productListFragment.switchDisplayMode(DisplayMode.Product);
						}
					}
				});
		productDisplay_modelModeLayout = (ViewGroup) view.findViewById(R.id.productDisplay_modelModeLayout);
		productDisplay_modelModeLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_MODEL_VIEW, null);

						displayMode = DisplayMode.Model;
						productDisplay_modelModeLayout.setBackgroundResource(R.drawable.shape_generator_top_bt_right_dn_bg);
						productDisplay_clothModeLayout.setBackgroundResource(R.drawable.shape_generator_top_bt_left_up_bg);
						((ImageView) productDisplay_modelModeLayout.getChildAt(0)).setImageResource(R.drawable.generator_filter_wh_girl);
						((ImageView) productDisplay_clothModeLayout.getChildAt(0)).setImageResource(R.drawable.generator_filter_cloth);
						if (productListFragment != null) {
							productListFragment.switchDisplayMode(DisplayMode.Model);
						}
					}
				});

		productDisplay_filterLayout = (ViewGroup) view.findViewById(R.id.productDisplay_filterLayout);
		productDisplay_filterLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_VIEW_FILTER, null);

				LogUtil.i(TAG, "filter button clicked.");
				Intent intent = new Intent(getActivity(), FilterMainActivity.class);
				intent.putExtra(BrandListActivity.INTENT_SELECTED_BRAND, selectedBrand);
				intent.putExtra(ColorActivity.INTENT_SELECTED_COLOR, selectedColor);
				intent.putExtra(FilterMainActivity.INTENT_SELECTED_MIN_PRICE, minPrice);
				intent.putExtra(FilterMainActivity.INTENT_SELECTED_MAX_PRICE, maxPrice);
				intent.putExtra(FilterMainActivity.INTENT_RANGE_BAR_LEFT_THUMB_X, leftThumbX);
				intent.putExtra(FilterMainActivity.INTENT_RANGE_BAR_RIGHT_THUMB_X, rightThumbX);
				startActivityForResult(intent, ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT);
				getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.no_anim_right_left);
			}
		});

		productDisplay_clothModeImageView = (ImageView) view.findViewById(R.id.productDisplay_clothModeImageView);
		productDisplay_modelModeImageView = (ImageView) view.findViewById(R.id.productDisplay_modelModeImageView);

		// Filter result
		productDisplay_filterResultLayout = (ViewGroup) view.findViewById(R.id.productDisplay_filterResultLayout);
		productDisplay_filterResultTextView = (TextView) view.findViewById(R.id.productDisplay_filterResultTextView);

		/*
		 * on product item click
		 */
		onProductItemClickListener = new OnProductItemClickListener() {

			@Override
			public void onProductClick(Product product, DisplayMode mode) {
				LogUtil.i(TAG, "event- product item click");
				if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
					LogUtil.i(TAG, "onProductClick()- reject");
					return;
				}
				mLastClickTime = SystemClock.elapsedRealtime();
				UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_PRODUCT_LIST_ITEM, null);

				if (!NetworkUtil.isNetworkAvailable(mContext)) {
					if (showErrDialogListener != null) {
						showErrDialogListener.showErrDialog(getActivity().getResources().getString(R.string.network_fault));
					}
					return;
				}
				if (enableUnClickMenuItem) {
					slideDownProductList(curProductMenu, product, mode);
					productMenuFragment.resetMenu();
				} else {
					if (onProductClickListener != null) {
						onProductClickListener.onProductClick(product, mode, curProductTypeCats);
					}
				}
				// hideKeyboard();

				// if(onProductClick!=null)
				// onProductClick.onProductClick(product,mode);

			}
		};

		/*
		 * on decoration item click
		 */
		onDecorationItemClickListenerDisplay = new OnDecorationItemClickListener() {

			@Override
			public void onDecorationItemClick(Decoration decoration) {
				UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_PRODUCT_LIST_DECORATION_ITEM, null);
				if (enableUnClickMenuItem) {
					slideDownProductList(curProductMenu, decoration);
					productMenuFragment.resetMenu();
				} else {
					if (onDecorationItemClickListener != null) {
						onDecorationItemClickListener.onDecorationItemClick(decoration);
					}
				}
			}
		};

		addMenuFragment();

		/*
		 * intercept layout
		 */
		productDisplay_interceptLayout = (View) view.findViewById(R.id.productDisplay_interceptLayout);
		productDisplay_interceptLayout.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {

						LogUtil.i(TAG, "Touch event. InterceptLayout event=" + arg1.getAction());

						if (productDisplay_searchBarLayout.dispatchTouchEvent(arg1)) { // search bar
							LogUtil.i(TAG, "Touch event. searchBarLayout event=" + arg1.getAction());
						} else {
							if (arg1.getAction() == MotionEvent.ACTION_UP) {
								if (curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
									if (viewContainPoint(decoration_delIconImageView, arg1)) {
										decoration_delIconImageView.performClick();
									} else {
										CommonTools.enableViews(getView(), false);
									}
								} else {
									if (viewContainPoint(productDisplay_delIconImageView, arg1)) {
										productDisplay_delIconImageView.performClick();
									} else {
										CommonTools.enableViews(getView(), false);
									}
								}
							}
							// onKeyBoardShow(false);
						}
						// onKeyBoardShow(false);
						return true;
					}
				});

		productDisplay_bottomLineImageView = (ImageView) view.findViewById(R.id.productDisplay_bottomLineImageView);

		enableIntercept(true, productDisplay_mainLinearLayout);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.i(TAG, "Lifecycle- onActivityCreated");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "Lifecycle- onResume");
	}

	private void hideKeyboard() { 
		// Check if no view has focus:
		productDisplay_mainLayout.requestFocus();
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		if (curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {// decoration menu item
			decoration_searchIconImageView.setVisibility(View.VISIBLE);
			if (decKeyword != null) {
				decTmpKeyword = decKeyword;
				decoration_searchEditText.setText(decKeyword);
			} else {
				decoration_searchEditText.setText("");
			}
			if (decoration_searchEditText.getText() != null && !decoration_searchEditText.getText().toString().isEmpty()) {
				decoration_delIconImageView.setVisibility(View.VISIBLE);
			}
		} else {// product menu item
			productDisplay_searchIconImageView.setVisibility(View.VISIBLE);
			if (keyword != null) {
				tmpKeyword = keyword;
				productDisplay_searchEditText.setText(keyword);
			} else {
				productDisplay_searchEditText.setText("");
			}
			if (productDisplay_searchEditText.getText() != null && !productDisplay_searchEditText.getText().toString().isEmpty()) {
				productDisplay_delIconImageView.setVisibility(View.VISIBLE);
			}
		}
	}

	public void addMenuFragment() {
		productMenuFragment = ProductMenuFragment.newInstance(enableUnClickMenuItem, includeDecoration, noCosmetic);
		productMenuFragment.setOnProductMenuClickListener(new OnProductMenuClickListener() {

					@Override
					public void onProductMenuClick(ProductMenu productMenu) {
						// [1,2,3]
						String ids = productMenu.productTypeId.substring(1, productMenu.productTypeId.length() - 1);
						Map<String, String> flurryParam = new HashMap<String, String>();
						flurryParam.put("CategoryIds", ids);
						UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_CLICK_PRODUCT_CAT_OPEN, flurryParam);

						// change layout based on clicked menu item
						if (productMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
							// productDisplay_topBarLayout.setVisibility(View.GONE);
							// productDisplay_modeLayout.setVisibility(View.GONE);
							// productDisplay_filterResultLayout.setVisibility(View.GONE);
							productDisplay_productTopBarLayout.setVisibility(View.GONE);
							productDisplay_decorationTopBarLayout.setVisibility(View.VISIBLE);
						} else {
							// if(needShowFilterBar()){
							// productDisplay_filterResultLayout.setVisibility(View.VISIBLE);
							// }
							// productDisplay_topBarLayout.setVisibility(View.VISIBLE);
							// productDisplay_modeLayout.setVisibility(View.VISIBLE);
							productDisplay_productTopBarLayout.setVisibility(View.VISIBLE);
							productDisplay_decorationTopBarLayout.setVisibility(View.GONE);
						}
						final ProductMenu fProductMenu = productMenu;
						if (productDisplay_mainLinearLayout.getVisibility() == View.GONE) {
							if (isAnimating) {
								LogUtil.i(TAG, "event- onProductMenuClick-rejected");
								return;
							} else {
								isAnimating = true;
							}
							// productMenuFragment.enableMenu(false);
							CommonTools.enableViews(getView(), false);
							if (requestBlockingListener != null) {
								requestBlockingListener.onRequestBlock();
							}
							Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_in);
							bottomUp.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationEnd(Animation arg0) {
									if (requestBlockingListener != null) {
										requestBlockingListener.onReleaseBlock();
									}
									processMenuOnClick(fProductMenu, true);
									// productMenuFragment.enableMenu(true);
									CommonTools.enableViews(getView(), true);
									isAnimating = false;
									if (null != onProductListSlideListener) {
										onProductListSlideListener.onSlideUpEnd();
									}
								}

								@Override
								public void onAnimationRepeat(Animation arg0) {
									
								}

								@Override
								public void onAnimationStart(Animation arg0) {
									
								}

							});
							productDisplay_mainLinearLayout.setVisibility(View.VISIBLE);
							productDisplay_mainLinearLayout.startAnimation(bottomUp);
						} else {
							processMenuOnClick(productMenu, true);
						}
					}
					
				});

		productMenuFragment.setOnProductMenuUnClickListener(
				new OnProductMenuUnClickListener() {
					@Override
					public void onProductMenuUnClick(ProductMenu productMenu) {
						LogUtil.i(TAG, "OnMenuItemUnClick()-" + productMenu.name);
						slideDownProductList(productMenu);
					}
				});

		// FragmentManager fm = getChildFragmentManager();
		// FragmentTransaction ft = fm.beginTransaction();
		//
		// ft.add(R.id.productDisplay_MenuContainer,
		// productMenuFragment,"ProductMenuFragment");
		//
		// ft.commit();
		// LogUtil.i(TAG,
		// "addMenuFragment(): fragment added. "+"ProductMenuFragment");
		addFragment(productMenuFragment, "ProductMenuFragment", false, R.id.productDisplay_MenuContainer);
	}

	public void saveProductListState(ProductMenu productMenu) {

		LogUtil.i(TAG, "Save productlist state ProductMenu=" + productMenu.name);
		ProductHistory productHist = productHistoryMap.get(productMenu);

		// if( productListFragment==null ||
		// productListFragment.getProductList()==null||
		// productListFragment.getProductList().size()==0)
		if (productListFragment == null
				|| productListFragment.getRowProductList() == null
				|| productListFragment.getRawProductList() == null)
			return;

		if (productHist == null) {

			ProductHistory newHistory = new ProductHistory();
			newHistory.endKey = productListFragment.getEndKey();
			if (productListFragment.getRowProductList().size() != 0) {
				newHistory.itemPos = productListFragment.getCurrentItemPos();
				newHistory.offset = productListFragment.getCurrentItemOffSet();
			}
			newHistory.rowProductList = productListFragment.getRowProductList();
			newHistory.rawProductList = productListFragment.getRawProductList();
			productHistoryMap.put(productMenu, newHistory);

		} else {
			productHist.endKey = productListFragment.getEndKey();
			if (productListFragment.getRowProductList().size() != 0) {
				productHist.itemPos = productListFragment.getCurrentItemPos();
				productHist.offset = productListFragment.getCurrentItemOffSet();
			}
			productHist.rowProductList = productListFragment.getRowProductList();
			productHist.rawProductList = productListFragment.getRawProductList();
		}
	}

	public void saveDecorationListState(ProductMenu productMenu) {

		LogUtil.i(TAG, "Save decoration list state ProductMenu=" + productMenu.name);

		if (decorationListFragment == null || decorationListFragment.getRowDecorationList() == null)
			return;

		if (decorationHistory == null) {

			decorationHistory = new DecorationHistory();
			if (decorationListFragment.getRowDecorationList().size() != 0) {
				decorationHistory.itemPos = decorationListFragment.getCurrentItemPos();
				decorationHistory.offset = decorationListFragment.getCurrentItemOffSet();
			}
			decorationHistory.rowDecorationList = decorationListFragment.getRowDecorationList();

		} else {
			if (decorationListFragment.getRowDecorationList().size() != 0) {
				decorationHistory.itemPos = decorationListFragment.getCurrentItemPos();
				decorationHistory.offset = decorationListFragment.getCurrentItemOffSet();
			}
			decorationHistory.rowDecorationList = decorationListFragment.getRowDecorationList();
		}
	}

	@Override
	public boolean onBackPressed() {

		if (productDisplay_mainLinearLayout.getVisibility() == View.GONE) {
			return false;
		} else {
			if (slideDownProductList(curProductMenu)) {
				productMenuFragment.resetMenu();
			}
			// curProductTypeCats=null;
			return true;
		}
	}

	public boolean slideDownProductList(ProductMenu productMenu) {
		return slideDownProductList(productMenu, null, null);
	}

	public boolean slideDownProductList(ProductMenu productMenu,
			Product product, DisplayMode mode) {
		return slideDownProductList(productMenu, product, mode, null);
	}

	public boolean slideDownProductList(ProductMenu productMenu,
			Decoration decoration) {
		return slideDownProductList(productMenu, null, null, decoration);
	}

	public boolean slideDownProductList(final ProductMenu productMenu,
			final Product product, final DisplayMode mode,
			final Decoration decoration) {

		if (isAnimating) {
			return false;
		} else {
			isAnimating = true;
		}

		// productMenuFragment.enableMenu(false);
		CommonTools.enableViews(getView(), false);
		if (requestBlockingListener != null) {
			requestBlockingListener.onRequestBlock();
		}

		Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_out);
		bottomUp.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				processMenuUnClick(productMenu);
				// productMenuFragment.enableMenu(true);
				CommonTools.enableViews(getView(), true);
				if (requestBlockingListener != null) {
					requestBlockingListener.onReleaseBlock();
				}
				if (product != null && mode != null) {
					if (onProductClickListener != null) {
						onProductClickListener.onProductClick(product, mode, curProductTypeCats);
					}
				}
				if (decoration != null) {
					if (onDecorationItemClickListener != null) {
						onDecorationItemClickListener.onDecorationItemClick(decoration);
					}
				}
				isAnimating = false;
				if (null != onProductListSlideListener) {
					onProductListSlideListener.onSlideDownEnd();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		productDisplay_mainLinearLayout.startAnimation(bottomUp);
		productDisplay_mainLinearLayout.setVisibility(View.GONE);
		return true;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT) {
			LogUtil.i(TAG, "Result from FilterActivity");
			if (resultCode == Activity.RESULT_OK) {
				selectedBrand = (BrandEntity) data.getParcelableExtra(BrandListActivity.INTENT_SELECTED_BRAND);
				selectedColor = (FilterColor) data.getParcelableExtra(ColorActivity.INTENT_SELECTED_COLOR);
				minPrice = data.getIntExtra(FilterMainActivity.INTENT_SELECTED_MIN_PRICE, 0);
				maxPrice = data.getIntExtra(FilterMainActivity.INTENT_SELECTED_MAX_PRICE, FilterMainActivity.MAX_AMOUNT);
				leftThumbX = data.getFloatExtra(FilterMainActivity.INTENT_RANGE_BAR_LEFT_THUMB_X, -1f);
				rightThumbX = data.getFloatExtra(FilterMainActivity.INTENT_RANGE_BAR_RIGHT_THUMB_X, -1f);
				priceSubmit = minPrice + ";" + maxPrice;
				refreshFilterResult();
			}
		}
		if (requestCode == ACTIVITY_RESULT_CODE_WAIT_4_FILTER_RESULT_DEC) {
			LogUtil.i(TAG, "Result from ColorActivity");
			if (resultCode == Activity.RESULT_OK) {
				decSelectedColor = (FilterColor) data.getParcelableExtra(ColorActivity.INTENT_SELECTED_COLOR);
				refreshDecorationFilterResult();
			}
		}
	}

	public void processMenuUnClick(ProductMenu productMenu) {

		if (productMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {

			if (decorationListFragment != null) {
				saveDecorationListState(productMenu);
				removeFragment(decorationListFragment);
				decorationListFragment = null;
			}

		} else {
			if (productListFragment != null) {
				saveProductListState(productMenu);
				removeFragment(productListFragment);
				productListFragment = null;
			}
		}

		curProductTypeCats = null;
		curProductMenu = null;

		// productDisplay_mainLinearLayout.setVisibility(View.GONE);
	}

	public void processMenuOnClick(ProductMenu productMenu, boolean saveHistory) {
		if (saveHistory) {
			// save state base on last menu item
			if (curProductMenu != null && curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
				saveDecorationListState(curProductMenu);
			} else if (curProductMenu != null) {
				saveProductListState(curProductMenu);
			}
		}
		// current menu item
		if (productMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
			// productDisplay_topBarLayout.setVisibility(View.GONE);
			if (decorationListFragment != null)
				removeFragment(decorationListFragment);
			decorationListFragment = DecorationListFragment.newInstance();
			// history
			if (decorationHistory != null) {
				decorationListFragment.setItemPos(decorationHistory.itemPos);
				decorationListFragment.setOffset(decorationHistory.offset);
				decorationListFragment.setRowDecorationList(decorationHistory.rowDecorationList);
			}
			if (onDecorationItemClickListenerDisplay != null) {
				decorationListFragment.setOnDecorationItemClickListener(onDecorationItemClickListenerDisplay);
			}
			// fliter
			if (decKeyword != null && !decKeyword.equals("")) {
				decorationListFragment.setKeyword(decKeyword);
			}
			if (decSelectedColor != null) {
				decorationListFragment.setColor(decSelectedColor.getName());
			}
			addFragment(decorationListFragment, "DecorationFragment", false, R.id.productDisplay_listContainer);

		} else {
			// productDisplay_topBarLayout.setVisibility(View.VISIBLE);
			if (!NetworkUtil.isNetworkAvailable(mContext)) {
				clearHistory();
			}
			if (productListFragment != null)
				removeFragment(productListFragment);
			productListFragment = ProductListFragment.newInstance(productMenu.productTypeId);
			if (onProductItemClickListener != null)
				productListFragment.setOnProductClickListener(onProductItemClickListener);
			// productListFragment.setLoadingListener(loadingListener);

			// history
			ProductHistory productHist = productHistoryMap.get(productMenu);
			if (productHist != null) {
				productListFragment.setItemPos(productHist.itemPos);
				productListFragment.setOffset(productHist.offset);
				productListFragment.setRowProductList(productHist.rowProductList);
				productListFragment.setRawProductList(productHist.rawProductList);
				productListFragment.setEndKey(productHist.endKey);
			}
			// filter
			if ((minPrice != 0 || maxPrice != FilterMainActivity.MAX_AMOUNT)) {
				productListFragment.setPrice(minPrice + ";" + maxPrice);
			}
			if (selectedColor != null) {
				productListFragment.setColor(selectedColor.getName());
			}
			if (selectedBrand != null) {
				productListFragment.setBrand(selectedBrand.getBrandId());
			}
			if (keyword != null && !keyword.equals("")) {
				productListFragment.setKeyword(keyword);
			}
			productListFragment.setDisplayMode(displayMode);
			addFragment(productListFragment, "ProductListFragment", false, R.id.productDisplay_listContainer);
		}
		curProductTypeCats = productMenu.getProductCatId();
		curProductMenu = productMenu;
	}

	public void refreshFilterResult() {
		clearHistory();
		if ((minPrice == 0 && maxPrice == FilterMainActivity.MAX_AMOUNT)
				&& selectedBrand == null && selectedColor == null) {
			productDisplay_filterResultLayout.setVisibility(View.GONE);
		} else {
			String filterResult = "";
			if ((minPrice != 0 || maxPrice != FilterMainActivity.MAX_AMOUNT)) {
				filterResult += dollarSign + minPrice + " - ";
				if (maxPrice >= FilterMainActivity.MAX_AMOUNT) {
					filterResult += maxPrice + getActivity().getResources().getString(R.string.filter_item_above);
				} else {
					filterResult += maxPrice;
				}
			}
			if (selectedColor != null) {
				if (!filterResult.equals("")) {
					filterResult += getActivity().getResources().getString(R.string.dot);
				}
				filterResult += selectedColor.getDisplayName();
			}
			if (selectedBrand != null) {
				if (!filterResult.equals("")) {
					filterResult += getActivity().getResources().getString(R.string.dot);
				}
				filterResult += selectedBrand.getName();
			}
			productDisplay_filterResultTextView.setText(filterResult);
			productDisplay_filterResultLayout.setVisibility(View.VISIBLE);

		}
		processMenuOnClick(curProductMenu, false);
	}

	public void refreshDecorationFilterResult() {
		decorationHistory = null;
		if (decSelectedColor == null) {
			decoration_filterResultLayout.setVisibility(View.GONE);
		} else {
			String filterResult = "";
			if (decSelectedColor != null) {
				if (!filterResult.equals("")) {
					filterResult += getActivity().getResources().getString(R.string.dot);
				}
				filterResult += decSelectedColor.getDisplayName();
			}
			decoration_filterResultTextView.setText(filterResult);
			decoration_filterResultLayout.setVisibility(View.VISIBLE);
		}
		processMenuOnClick(curProductMenu, false);
	}

	// private boolean needShowFilterBar(){
	//
	// boolean result=false;
	//
	// if((minPrice!=0 || maxPrice!=FilterMainActivity.MAX_AMOUNT)){
	// result=true;
	// }
	//
	// if(selectedColor!=null){
	// result=true;
	// }
	//
	// if(selectedBrand!=null){
	// result=true;
	// }
	//
	// return result;
	//
	// }

	public void clearHistory() {
		productHistoryMap.clear();
	}

	@Override
	public void onKeyBoardShow(boolean show) {
		productDisplay_interceptLayout.setVisibility(show ? View.VISIBLE : View.GONE);
		if (show) {

		} else {
			hideKeyboard();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					showMenu(true);
					if (productListFragment != null)
						productListFragment.showDummyFooter(true);

					if (searchResultWaitForKeyboardClose) {
						searchResultWaitForKeyboardClose = false;
						if (curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
							refreshDecorationFilterResult();
						} else {
							refreshFilterResult();
						}
					}
					CommonTools.enableViews(getView(), true);
				}
				
			}, 200);
		}
	}

	public void showMenu(boolean show) {
		productDisplay_MenuContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		productDisplay_bottomLineImageView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}

	public void refrashSearchBarControl() {
		if (curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {// decoration menu item
			if (decTmpKeyword == null || decTmpKeyword.isEmpty()) {
				decoration_searchIconImageView.setVisibility(View.VISIBLE);
				decoration_delIconImageView.setVisibility(View.GONE);
			} else {
				decoration_searchIconImageView.setVisibility(View.GONE);
				decoration_delIconImageView.setVisibility(View.VISIBLE);
			}
		} else {
			if (tmpKeyword == null || tmpKeyword.isEmpty()) {
				// tmpKeyword=null;
				productDisplay_searchIconImageView.setVisibility(View.VISIBLE);
				productDisplay_delIconImageView.setVisibility(View.GONE);
			} else {
				productDisplay_searchIconImageView.setVisibility(View.GONE);
				productDisplay_delIconImageView.setVisibility(View.VISIBLE);
			}
		}
	}

	// call this method only when all UI are rendered completely
	public void openMenu(String menuTypeID) {
		if (productMenuFragment != null) {
			productMenuFragment.openMenu(menuTypeID);
		}
	}

	public void dimMenu(boolean dim) {
		if (productMenuFragment != null) {
			productMenuFragment.dimMenu(dim);
		}
	}

	public void showProductList(String menuTypeID) {
		ProductMenu tarMenuItem = productMenuFragment.getMenuItem(menuTypeID);
		if (tarMenuItem != null) {
			productDisplay_mainLinearLayout.setVisibility(View.VISIBLE);
			tarMenuItem.isClicked = true;
			processMenuOnClick(tarMenuItem, false);
			if (productMenuFragment != null) {
				productMenuFragment.refreshMenu();
			}
			productMenuFragment.scrollMenuItemIfNotOnScreen(productMenuFragment.getMenuItemPos(menuTypeID));
		}
	}

	public OnTouchListener getSearchEditTextOnTouchListener() {
		return new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					editTextGestureDetector.setIsLongClick(false);
				}
				editTextDetector.onTouchEvent(event);
				if (event.getAction() == MotionEvent.ACTION_UP && !editTextGestureDetector.isLongClick()) {
					LogUtil.i(TAG, "event- searchEditText up");
					if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
						LogUtil.i(TAG, "onProductClick()- reject");
					} else {
						mLastClickTime = SystemClock.elapsedRealtime();
						showMenu(false);
						if (productListFragment != null)
							productListFragment.showDummyFooter(false);

						if (decorationListFragment != null)
							decorationListFragment.showDummyFooter(false);

						editTextGestureDetector.setIsLongClick(false);

						refrashSearchBarControl();
					}
				}
				return v.onTouchEvent(event);
			}
		};
	}

	public TextView.OnEditorActionListener getSearchEditTextEditorActionListener() {
		return new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
						decKeyword = decTmpKeyword;
						if (!TextUtils.isEmpty(decKeyword)) {
							HashMap<String, String> flurryParam = new HashMap<String, String>();
							flurryParam.put("Keyword", decKeyword);
							UserTracker.getInstance().trackUserAction(
									UserTracker.Action.EVENT_PRODUCT_LIST_DECORATION_SEARCH, flurryParam);
						}
						LogUtil.i(TAG, "dec-Keyboard search clicked.");
						LogUtil.i(TAG, "dec-keyword=" + decKeyword);
					} else {
						keyword = tmpKeyword;
						if (!TextUtils.isEmpty(keyword)) {
							HashMap<String, String> flurryParam = new HashMap<String, String>();
							flurryParam.put("Keyword", keyword);
							UserTracker.getInstance().trackUserAction(
									UserTracker.Action.EVENT_PRODUCT_LIST_SEARCH, flurryParam);
						}
						LogUtil.i(TAG, "Keyboard search clicked.");
						LogUtil.i(TAG, "keyword=" + keyword);
					}
					CommonTools.enableViews(getView(), false);
					hideKeyboard();
					searchResultWaitForKeyboardClose = true;
					return true;
				}
				return false;
			}
		};
	}

	public TextWatcher getSearchEditTextTextWatcher() {
		return new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (curProductMenu.productTypeId.equals(AppConfig.PRODUCT_TYPE_DECORATION)) {
					// if it is the same word, ignore
					if (s.toString().equals(decKeyword)) return;
					decTmpKeyword = s.toString();
					// keyword = s.toString();
					LogUtil.i(TAG, "decTmpKeyword=" + decTmpKeyword);
				} else {
					// if it is the same word, ignore
					if (s.toString().equals(keyword)) return;
					tmpKeyword = s.toString();
					// keyword = s.toString();
					LogUtil.i(TAG, "tmpKeyword=" + tmpKeyword);
				}
				refrashSearchBarControl();
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
		};
	}

	public boolean viewContainPoint(View v, MotionEvent event) {
		// delete icon in search bar layout
		int[] viewXY = new int[2];
		v.getLocationOnScreen(viewXY);
		int viewX = viewXY[0];
		int viewY = viewXY[1];
		Rect delIconRect = new Rect();
		v.getDrawingRect(delIconRect);
		delIconRect.offset(viewX, viewY);

		if (delIconRect.contains((int) event.getRawX(), (int) event.getRawY())) {
			return true;
		} else {
			return false;
		}
	}

	public void setOnProductListSlideListener(
			OnProductListSlideListener onProductListSlideListener) {
		this.onProductListSlideListener = onProductListSlideListener;
	}

	public static class ProductHistory {
		public int itemPos;
		public int offset;
		public List<RowObject> rowProductList;
		public List<Product> rawProductList;
		public String endKey;

		public ProductHistory() {
			clear();
		}

		public void clear() {
			itemPos = 0;
			offset = 0;
			if (rowProductList != null) {
				rowProductList.clear();
			}
			if (rawProductList != null) {
				rawProductList.clear();
			}
			rowProductList = null;
			rawProductList = null;
			endKey = null;
		}
	}

	public static class DecorationHistory {
		public int itemPos;
		public int offset;
		public List<RowObject> rowDecorationList;

		public DecorationHistory() {
			clear();
		}

		public void clear() {
			itemPos = 0;
			offset = 0;
			if (rowDecorationList != null) {
				rowDecorationList.clear();
			}
			rowDecorationList = null;
		}
	}

	public static class CustomGestureDetector implements
			GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

		String TAG = "CustomGestureDetector";

		private boolean isLongClick;
		private boolean isTap;

		public void setIsLongClick(boolean bool) {
			isLongClick = bool;
		}

		public boolean isLongClick() {
			return isLongClick;
		}

		public void setIsTap(boolean bool) {
			isTap = bool;
		}

		public boolean isTap() {
			return isTap;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			LogUtil.i(TAG, "onSingleTapConfirmed");
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			LogUtil.i(TAG, "onDoubleTap");
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			LogUtil.i(TAG, "onDoubleTapEvent");
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			LogUtil.i(TAG, "onDown");
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			LogUtil.i(TAG, "onShowPress");
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			LogUtil.i(TAG, "onSingleTapUp");
			isTap = true;
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			LogUtil.i(TAG, "onScroll");
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			LogUtil.i(TAG, "onLongPress");
			isLongClick = true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			LogUtil.i(TAG, "onFling");
			return true;
		}
	}

	public interface OnProductListSlideListener {
		public void onSlideUpEnd();

		public void onSlideDownEnd();
	}

	public void setNoCosmetic() {
		noCosmetic = true;
	}

}
