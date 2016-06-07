package com.spshop.stylistpark.activity.collage;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AppBaseAdapter.OnItemCellClickListener;
import com.spshop.stylistpark.adapter.ProductPhotoAdapter;
import com.spshop.stylistpark.entity.Product;

public class MultiAngleProductPhotoActivity extends BaseActivity {
	
	public static final String INTENT_SELECTED_PHOTO_URL = "intent_selected_photo_url";
	public static final String INTENT_FINISH_ANIM = "intent_finish_anim";
	
	ProductPhotoAdapter productPhotoAdapter;
	Product selectedProduct;
	List<List<String>> mImgUrlList;
	GridView photo_GridView;
	
	FinishAnim finishAnim=FinishAnim.SlideRight;
	public enum FinishAnim{
		SlideDown,
		SlideRight
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiangle_product_photo);
		
		setTitle(R.string.collage_select_photo);
		
		Intent data = getIntent();
		if (data != null) {
			selectedProduct = (Product) data.getParcelableExtra(MultiAngleProductListActivity.INTENT_SELECTED_PRODUCT);
			FinishAnim tmpFinishAnim = (FinishAnim) data.getSerializableExtra(INTENT_FINISH_ANIM);
			if (tmpFinishAnim != null) {
				finishAnim = tmpFinishAnim;
			}
			if (finishAnim == FinishAnim.SlideDown) {
				setBtnRight(getString(R.string.cancel));
			}
		}
		if (data != null) {
			if (finishAnim == FinishAnim.SlideDown) {
				setBtnRightGone(View.GONE);
			} else {
				setBtnRightGone(View.VISIBLE);
			}
		}

		if (selectedProduct != null) {
			mImgUrlList = new ArrayList<List<String>>();
			List<List<String>> imgList = selectedProduct.getImgList();
			if (imgList != null) {
				for (int i = 0; i < imgList.size(); i++) {
					mImgUrlList.add(imgList.get(i));
				}
			}
			while (mImgUrlList.size() % 3 != 0) {
				mImgUrlList.add(null);
			}
		}
        
		photo_GridView = (GridView) findViewById(R.id.photo_GridView);
		productPhotoAdapter = new ProductPhotoAdapter(this);
		productPhotoAdapter.setDataList(mImgUrlList);
		productPhotoAdapter.setOnItemCellClickListener(
				new OnItemCellClickListener() {

					@Override
					public void onItemCellClickListener(Object data) {

						@SuppressWarnings("unchecked")
						String url = ((List<String>) data).get(0);
						Intent intent = new Intent();
						intent.putExtra(INTENT_SELECTED_PHOTO_URL, url);
						setResult(RESULT_OK, intent);
						finish();
						overridePendingTransition(R.anim.no_anim_up_down, R.anim.slide_down_out);
					}
				});
        photo_GridView.setAdapter(productPhotoAdapter);
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		onBackPressed();
	}
	
	@Override
	public void finish (){
		super.finish();
		if(finishAnim==FinishAnim.SlideRight)
			overridePendingTransition(R.anim.no_anim_right_left, R.anim.slide_right_out);
		else
			overridePendingTransition(R.anim.no_anim_right_left, R.anim.slide_out_down);
	}

}
