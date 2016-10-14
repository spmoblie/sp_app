package com.spshop.stylistpark.activity.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.HomeFragmentActivity;
import com.spshop.stylistpark.activity.sort.SortActivity;
import com.spshop.stylistpark.adapter.AdapterCallback;
import com.spshop.stylistpark.adapter.CartProductListAdapter;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.entity.GoodsCartEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;
import com.spshop.stylistpark.widgets.ScrollListView;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.spshop.stylistpark.widgets.pullrefresh.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("UseSparseArrays")
public class ChildFragmentFour extends Fragment implements OnClickListener, OnDataListener {

	private static final String TAG = "ChildFragmentFour";

	private Context mContext;
	private View ptrsv_chlid;
	private ScrollView ptrsv_sv;
	private PullToRefreshScrollView ptrsv;
	private ScrollListView scroll_lv;
	private AdapterCallback apCallback;
	private CartProductListAdapter lv_Adapter;
	private LinearLayout ll_no_data, ll_billing, ll_select_all;
	private RelativeLayout rl_no_more, rl_loading, rl_load_fail;
	private ImageView iv_left, iv_billing_line;
	private TextView tv_title, tv_shopping, tv_load_again;
	private TextView tv_curr, tv_total, tv_buy_now;
	
	private String currStr, amountStr;
	private int updateNum = 1;
	private int mPosition = 0;
	private boolean isLogined;
	private boolean isChange = true;
	private boolean selectAll = false;
	private boolean pullUpdate = false;
	private ProductDetailEntity changeData;
	private List<ProductDetailEntity> lv_datas = new ArrayList<ProductDetailEntity>();
	private SparseArray<ProductDetailEntity> sa_cart = new SparseArray<ProductDetailEntity>();
	private DialogManager dm;
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();

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
		mContext = getActivity();
		currStr = LangCurrTools.getCurrencyValue();
		dm = DialogManager.getInstance(mContext);
		atm = AsyncTaskManager.getInstance(mContext);

		View view = null;
		try {
			view = inflater.inflate(R.layout.fragment_layout_four, null);
			findViewById(view);
			initView();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
		return view;
	}

	private void findViewById(View view) {
		iv_left = (ImageView) view.findViewById(R.id.top_commom_iv_left);
		tv_title = (TextView) view.findViewById(R.id.top_commom_tv_title);
		tv_curr = (TextView) view.findViewById(R.id.fragment_four_tv_curr);
		tv_total = (TextView) view.findViewById(R.id.fragment_four_tv_total);
		tv_buy_now = (TextView) view.findViewById(R.id.fragment_four_tv_buy_now);
		ptrsv = (PullToRefreshScrollView) view.findViewById(R.id.fragment_four_ptrsv);
		ll_billing = (LinearLayout) view.findViewById(R.id.fragment_four_ll_billing);
		iv_billing_line = (ImageView) view.findViewById(R.id.fragment_four_iv_billing_line);
		ll_select_all = (LinearLayout) view.findViewById(R.id.fragment_four_ll_select_all);
		rl_loading = (RelativeLayout) view.findViewById(R.id.loading_anim_large_ll_main);
		rl_load_fail = (RelativeLayout) view.findViewById(R.id.loading_fail_rl_main);
		tv_load_again = (TextView) view.findViewById(R.id.loading_fail_tv_update);
		
		ptrsv_chlid = LayoutInflater.from(mContext).inflate(R.layout.layout_ptrsv_cart, null);
		scroll_lv = (ScrollListView) ptrsv_chlid.findViewById(R.id.ptrsv_cart_scroll_lv);
		rl_no_more = (RelativeLayout) ptrsv_chlid.findViewById(R.id.ptrsv_cart_rl_no_more);
		ll_no_data = (LinearLayout) ptrsv_chlid.findViewById(R.id.ptrsv_cart_ll_no_data);
		tv_shopping = (TextView) ptrsv_chlid.findViewById(R.id.ptrsv_cart_iv_go_shopping);
	}

	private void initView() {
		tv_title.setText(getString(R.string.title_fragment_four));
		iv_left.setVisibility(View.GONE);
		tv_buy_now.setOnClickListener(this);
		tv_shopping.setOnClickListener(this);
		tv_load_again.setOnClickListener(this);
		ll_select_all.setOnClickListener(this);
		
		initScrollView();
		setAdapter();
		startAnimation();
	}

	private void initScrollView() {
		ptrsv.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // 下拉刷新
            	pullUpdate = true;
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						requestProductLists();
					}
				}, 1000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// 加载更多(GridView无效)
            }
        });
		ptrsv_sv = ptrsv.getRefreshableView();
		ptrsv_sv.addView(ptrsv_chlid);
	}

	private void setView() {
		if (lv_datas.size() > 0) {
			showNoDataView(false);
			ll_billing.setVisibility(View.VISIBLE);
			iv_billing_line.setVisibility(View.VISIBLE);
			rl_no_more.setVisibility(View.VISIBLE);
		}else {
			showNoDataView(true);
			ll_billing.setVisibility(View.GONE);
			iv_billing_line.setVisibility(View.GONE);
			rl_no_more.setVisibility(View.GONE);
		}
		updateSelectAllView();
	}

	private void showNoDataView(boolean isShow) {
		if (isShow) {
			CommonTools.setLayoutParams(ll_no_data, AppApplication.screenWidth,
					AppApplication.screenHeight - AppApplication.statusHeight - 300);
			ll_no_data.setVisibility(View.VISIBLE);
			//ptrsv.setBackgroundColor(getResources().getColor(R.color.ui_bg_color_white));
		} else {
			ll_no_data.setVisibility(View.GONE);
			//ptrsv.setBackgroundColor(getResources().getColor(R.color.ui_bg_color_nut));
		}
	}

	private void setAdapter() {
		apCallback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object entity, int position, int type) {
				if (position < 0 || position >= lv_datas.size()) return;
				mPosition = position;
				changeData = (ProductDetailEntity) entity;
				if (changeData != null) {
					switch (type) {
					case CartProductListAdapter.TYPE_SCROLL: //滚动
						lv_Adapter.reset(mPosition);
						break;
					case CartProductListAdapter.TYPE_SELECT: //选择或取消
						if (sa_cart.indexOfKey(changeData.getRecId()) > 0) {
							sa_cart.remove(changeData.getRecId());
						}else {
							sa_cart.put(changeData.getRecId(), changeData);
						}
						updateSelectAllView();
						break;
					case CartProductListAdapter.TYPE_CHECK: //查看
						HomeFragmentActivity.instance.openProductDetailActivity(changeData.getId());
						break;
					case CartProductListAdapter.TYPE_MINUS: //数量减1
						if (!isChange) return;
						updateNum = lv_datas.get(position).getCartNum();
						if (updateNum > 1) {
							updateNum--;
							requestChangeCartNum();
						}
						break;
					case CartProductListAdapter.TYPE_ADD: //数量加1
						if (!isChange) return;
						updateNum = lv_datas.get(position).getCartNum();
						if (updateNum < changeData.getStockNum()) {
							updateNum++;
							requestChangeCartNum();
						}
						break;
					case CartProductListAdapter.TYPE_DELETE: //删除
						dm.showTwoBtnDialog(null, getString(R.string.delete_confirm),
								getString(R.string.delete_pain), getString(R.string.delete_think),
								AppApplication.screenWidth * 2/3, true, true, new Handler() {
									@Override
									public void handleMessage(Message msg) {
										switch (msg.what) {
											case BaseActivity.DIALOG_CANCEL_CLICK:
												requestDeleteGoods();
												break;
											case BaseActivity.DIALOG_CONFIRM_CLICK:
												break;
										}
									}
								});
						break;
					}
				}
			}
		};
		lv_Adapter = new CartProductListAdapter(mContext, lv_datas, sa_cart, apCallback);
		scroll_lv.setAdapter(lv_Adapter);
		scroll_lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	/**
	 * 更新购物车全选状态
	 */
	private void updateSelectAllView() {
//		if (lv_datas.size() != 0 && cartHashMap.size() == lv_datas.size()) {
//			selectAll = true;
//			iv_select_all.setImageResource(R.drawable.btn_select_hook_yes);
//		}else {
//			selectAll = false;
//			iv_select_all.setImageResource(R.drawable.btn_select_hook_no);
//		}
		updatePriceTotal();
	}

	/**
	 * 更新购物车商品总价格
	 */
	private void updatePriceTotal() {
		lv_Adapter.updateAdapter(lv_datas, sa_cart);
		tv_curr.setText(currStr);
		tv_total.setText(amountStr);
	}

	/**
	 * 从远程服务器加载数据
	 */
	private void getSVDatas() {
		startAnimation();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				requestProductLists();
			}
		}, 1000);
	}

	/**
	 * 发起加载数据的请求
	 */
	private void requestProductLists() {
		atm.request(AppConfig.REQUEST_SV_GET_CART_LIST_CODE, this);
	}
	
	/**
	 * 发起删除商品的请求
	 */
	private void requestDeleteGoods() {
		startAnimation();
		atm.request(AppConfig.REQUEST_SV_POST_DELETE_GOODS_CODE, this);
	}
	
	/**
	 * 发起修改商品数量的请求
	 */
	private void requestChangeCartNum() {
		isChange = false;
		startAnimation();
		atm.request(AppConfig.REQUEST_SV_POST_CHANGE_GOODS_CODE, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_four_tv_buy_now:
			startActivity(new Intent(mContext, PostOrderActivity.class));
			break;
		case R.id.ptrsv_cart_iv_go_shopping:
			startActivity(new Intent(mContext, SortActivity.class));
			break;
		case R.id.loading_fail_tv_update:
			getSVDatas();
			break;
		case R.id.fragment_four_ll_select_all:
			if (selectAll) {
				sa_cart.clear();
			}else {
				sa_cart.clear();
				for (int i = 0; i < lv_datas.size(); i++) {
					sa_cart.put(lv_datas.get(i).getRecId(), lv_datas.get(i));
				}
			}
			updateSelectAllView();
			lv_Adapter.updateAdapter(lv_datas, sa_cart);
			break;
		}
	}

	@Override
	public void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(TAG);
		checkLogin();
		super.onResume();
	}

	private void checkLogin() {
		isLogined = UserManager.getInstance().checkIsLogined();
		if (isLogined) {
			requestProductLists();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(getActivity(), TAG);

		if (dm != null) {
			dm.clearInstance();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_CART_LIST_CODE:
			params.add(new MyNameValuePair("app", "cart"));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_CART_LIST_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_POST_DELETE_GOODS_CODE:
			uri = AppConfig.URL_COMMON_INDEX_URL + "?app=delete_cart";
			params.add(new MyNameValuePair("id", String.valueOf(changeData.getRecId())));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_DELETE_GOODS_CODE, uri, params, HttpUtil.METHOD_POST);

		case AppConfig.REQUEST_SV_POST_CHANGE_GOODS_CODE:
			uri = AppConfig.URL_COMMON_INDEX_URL + "?app=update_cart";
			params.add(new MyNameValuePair("rec_id", String.valueOf(changeData.getRecId())));
			params.add(new MyNameValuePair("number", String.valueOf(updateNum)));
			params.add(new MyNameValuePair("goods_id", String.valueOf(changeData.getId())));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_CHANGE_GOODS_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (getActivity() == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_CART_LIST_CODE:
			lv_datas.clear();
			if (result != null) {
				GoodsCartEntity mainEn = (GoodsCartEntity) result;
				if (mainEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) //登录失效
				{
					loginTimeoutHandle();
				}
				else if (mainEn.getChildLists() != null) 
				{
					rl_load_fail.setVisibility(View.GONE);
					/*if (pullUpdate) {
						sa_cart.clear();
					}*/
					lv_datas.addAll(mainEn.getChildLists());
					loadSuccessHandle(mainEn);
				}
				else 
				{
					loadFailHandle();
				}
			}else {
				loadFailHandle();
			}
			break;
		case AppConfig.REQUEST_SV_POST_DELETE_GOODS_CODE:
			if (result != null) {
				GoodsCartEntity deleteEn = (GoodsCartEntity) result;
				if (deleteEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					if (sa_cart.indexOfKey(changeData.getRecId()) > 0) {
						sa_cart.remove(changeData.getRecId());
					}
					lv_datas.remove(changeData);
					loadSuccessHandle(deleteEn);
				}else if (deleteEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					loginTimeoutHandle();
				}else {
					if (StringUtil.isNull(deleteEn.getErrInfo())) {
						changeFailUpdateCartData(getString(R.string.toast_server_busy));
					}else {
						changeFailUpdateCartData(deleteEn.getErrInfo());
					}
				}
			}else {
				changeFailUpdateCartData(getString(R.string.toast_server_busy));
			}
			break;
		case AppConfig.REQUEST_SV_POST_CHANGE_GOODS_CODE:
			if (result != null) {
				GoodsCartEntity changeEn = (GoodsCartEntity) result;
				if (changeEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					updateCartNum(changeEn.getSkuNum());
					loadSuccessHandle(changeEn);
				}else if (changeEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					loginTimeoutHandle();
				}else {
					if (StringUtil.isNull(changeEn.getErrInfo())) {
						changeFailUpdateCartData(getString(R.string.toast_server_busy));
					}else {
						changeFailUpdateCartData(changeEn.getErrInfo());
					}
				}
			}else {
				changeFailUpdateCartData(getString(R.string.toast_server_busy));
			}
			break;
		}
		stopAnimation();
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (getActivity() == null) return;
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_CART_LIST_CODE:
			if (!pullUpdate && lv_datas.size() == 0) {
				loadFailHandle();
			}
			break;
		}
		stopAnimation();
		CommonTools.showToast(String.valueOf(result), 1000);
	}

	private void loadSuccessHandle(GoodsCartEntity resultEn) {
		amountStr = resultEn.getAmount();
		BaseActivity.updateCartTotal(resultEn.getGoodsTotal());
		setView();
	}

	private void loadFailHandle() {
		if (!pullUpdate) {
			showNoDataView(false);
			rl_load_fail.setVisibility(View.VISIBLE);
		}
		updateSelectAllView();
	}

	private void loginTimeoutHandle() {
		AppApplication.AppLogout(true);
		HomeFragmentActivity.instance.openLoginActivity(TAG);
	}

	/**
	 * 显示缓冲动画
	 */
	private void startAnimation() {
		rl_load_fail.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
	}

	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		rl_loading.setVisibility(View.GONE);
		if (pullUpdate) {
			ptrsv.onPullDownRefreshComplete();
			pullUpdate = false;
		}
		isChange = true;
	}

	/**
	 * 修改或删除购物车中商品失败时提示
	 */
	private void changeFailUpdateCartData(String msg) {
		checkLogin();
		CommonTools.showToast(msg, 3000);
	}

	/**
	 * 更新购物车中商品数量
	 */
	private void updateCartNum(int newSkuNum) {
		lv_datas.get(mPosition).setCartNum(updateNum);
		if (newSkuNum > 0) {
			lv_datas.get(mPosition).setStockNum(newSkuNum);
		}
		/*if (changeData != null && sa_cart.indexOfKey(changeData.getRecId()) > 0) {
			sa_cart.put(changeData.getRecId(), lv_datas.get(mPosition));
		}*/
		updatePriceTotal();
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

}
