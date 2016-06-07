package com.spshop.stylistpark.activity.collage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.collage.BaseFragment.LoadingListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.RequestBlockingListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.ShowErrDialogListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.SoftKeyBoardListener;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductClickListener;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.DisplayMode;
import com.spshop.stylistpark.adapter.DecorationAdapter.OnDecorationItemClickListener;
import com.spshop.stylistpark.entity.Decoration;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.utils.LogUtil;

public class MultiAngleProductListActivity extends BaseActivity implements SoftKeyBoardListener {
	
	private static final String TAG = "MultiAngleProductListActivity";
	
	public static final String INTENT_SELECTED_PRODUCT = "intent_selected_product";
	public static final String INTENT_SELECTED_PRODUCT_TYPE_ID = "intent_selected_product_type_id";
	public static final int ACTIVITY_RESULT_CODE_WAIT_4_PRODUCT_PHOTO = 2002; 
	
	ProductDisplayFragment productDisplayFragment;
	ViewGroup productList_container;
	boolean isInit = true;
	Product selectedProduct;
	String productTypeID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiangle_product_list);
        
        setTitle(R.string.collage_select_product);
        
        productList_container = (FrameLayout) findViewById(R.id.productList_container);
        productDisplayFragment = new ProductDisplayFragment();
        productDisplayFragment.setEnableUnClickMenuItem(false);
        productDisplayFragment.setNoCosmetic();
        productDisplayFragment.setIncludeDecoration(false);
        
		productDisplayFragment.setOnProductClickListener(new OnProductClickListener() {

             @Override
             public void onProductClick(Product product, DisplayMode mode, String productCats) {
                 LogUtil.i(TAG, "Product Clicked -" + product.getName() + " Mode=" + mode.name() + " ProductCats=" + productCats);
                 selectedProduct=product;
                 productTypeID=productCats;
                 Intent data = new Intent(MultiAngleProductListActivity.this, MultiAngleProductPhotoActivity.class);
                 data.putExtra(INTENT_SELECTED_PRODUCT, product);
                 startActivityForResult(data,ACTIVITY_RESULT_CODE_WAIT_4_PRODUCT_PHOTO);
             }

         });

		productDisplayFragment.setOnDecorationItemClickListener(new OnDecorationItemClickListener() {
		
		     @Override
		     public void onDecorationItemClick(Decoration decoration) {
		    	 
		     }
		});
		 
		productDisplayFragment.setShowErrDialogListener(new ShowErrDialogListener(){
		
				@Override
				public void showErrDialog(String msg) {
					showErrorDialog(msg); 
				}
			});
		 
		productDisplayFragment.setLoadingListener(new LoadingListener(){
		
				@Override
				public void onShowLoading() {
					LogUtil.i(TAG,"event-onShowLoading()");
					  startAnimation();
				}
		
				@Override
				public void onHideLoading() {
					  LogUtil.i(TAG,"event-onHideLoading()");
					  stopAnimation();
				}
			});
		 
		productDisplayFragment.setRequestBlockingListener(new RequestBlockingListener(){
		
				@Override
				public void onRequestBlock() {
					 LogUtil.i(TAG,"event- onRequestBlock()");
//					 setCanClickIntecptLayout(false); //Xu
//					 showInterceotLayout(true); //Xu
					
				}
		
				@Override
				public void onReleaseBlock() {
					LogUtil.i(TAG,"event-onReleaseBlock()");
//					setCanClickIntecptLayout(true); //Xu
//					showInterceotLayout(false); //Xu
					
				}
			});
		 
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.productList_container, productDisplayFragment,"ProducDisplayFragment");
		ft.commit();
		fm.executePendingTransactions();
	}
	
	@Override
	protected void onPostResume(){
		super.onPostResume();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		LogUtil.i(TAG, "Lifecycle- onResume");
		if(isInit){
			isInit = false;
			productDisplayFragment.showProductList(AppConfig.PRODUCT_TYPE_NEW);
		}
		//productDisplayFragment.openMenu(AppConfig.PRODUCT_TYPE_UPPER_CLOTHES);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    if( requestCode == ACTIVITY_RESULT_CODE_WAIT_4_PRODUCT_PHOTO ) {
			LogUtil.i(TAG, "Result from MultiAngleProductPhotoActivity");
			if (resultCode == Activity.RESULT_OK) {
				// String url=data.getStringExtra(MultiAngleProductPhotoActivity.INTENT_SELECTED_PHOTO_URL);
				data.putExtra(INTENT_SELECTED_PRODUCT, selectedProduct);
				data.putExtra(INTENT_SELECTED_PRODUCT_TYPE_ID, productTypeID);
				setResult(RESULT_OK, data);
				finish();
			}
	    }
	}

	@Override
	public void finish(){
		super.finish();
		overridePendingTransition(R.anim.no_anim_up_down, R.anim.slide_down_out);
	}

	@Override
	public void onShowSoftKeyBoard() {
		productDisplayFragment.onKeyBoardShow(true);
	}

	@Override
	public void onHideSoftKeyBoard() {
		productDisplayFragment.onKeyBoardShow(false);
	}

}
