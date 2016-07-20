package com.spshop.stylistpark.activity.collage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener;
import com.edmodo.rangebar.RangeBar.OnThumbMoveListener;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.FilterColor;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.UserTracker;

public class FilterMainActivity extends BaseActivity {

	private static final String TAG = "FilterMainActivity";
	
	public static final String INTENT_SELECTED_MIN_PRICE = "intent_selected_min_price";
	public static final String INTENT_SELECTED_MAX_PRICE = "intent_selected_max_price";
	public static final String INTENT_RANGE_BAR_LEFT_THUMB_X = "intent_range_bar_left_thumb_x";
	public static final String INTENT_RANGE_BAR_RIGHT_THUMB_X = "intent_range_bar_right_thumb_x";
	public static final int ACTIVITY_RESULT_CODE_WAIT_4_BRAND = 1882;
	public static final int ACTIVITY_RESULT_CODE_WAIT_4_COLOR = 1883;
	public static final int MAX_AMOUNT = 15000;
	final int EACH_PERCENTAGE_AMOUNT = MAX_AMOUNT / 100;
	final int BUFFER_PERCENTAGE_IDX = 1;

	final int NUM_OF_PERCENTAGE_IDX = BUFFER_PERCENTAGE_IDX + 101 + BUFFER_PERCENTAGE_IDX;
	final int PERCENTAGE_START_VALUE = BUFFER_PERCENTAGE_IDX;
	final int PERCENTAGE_LAST_VALUE = NUM_OF_PERCENTAGE_IDX - BUFFER_PERCENTAGE_IDX - 1;

	ViewGroup filterMain_priceLayout;
	RangeBar filterMain_rangeBar;
	TextView filterMain_minPriceTextView;
	TextView filterMain_maxPriceTextView;
	TextView filterMain_brandNameTextView;
	TextView filterMain_colorNameTextView;

	ImageView filterMain_priceCrossImageView;
	ImageView filterMain_brandCrossImageView;
	ImageView filterMain_colorCrossImageView;
	ImageView filterMain_brandListImageView;
	ImageView filterMain_colorListImageView;
	ImageView filterMain_colorIconImageView;

	ViewGroup filterMain_brandLayout;
	ViewGroup filterMain_colorLayout;
	Button filterMain_resetLayout;
	Button filterMain_completeLayout;

	BrandEntity selectedBrand;
	FilterColor selectedColor;
	int minPrice;
	int maxPrice;
	float leftThumbX;
	float rightThumbX;
	private String dollarSign;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_main);

		setTitle(R.string.filter);
		dollarSign = LangCurrTools.getCurrencyValue();

		// create RangeSeekBar as Integer range between 20 and 75
		// RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(0, 100,
		// this);
		// seekBar.setOnRangeSeekBarChangeListener(new
		// OnRangeSeekBarChangeListener<Integer>() {
		// @Override
		// public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer
		// minValue, Integer maxValue) {
		// // handle changed range values
		// Log.i(TAG, "User selected new range values: MIN=" + minValue +
		// ", MAX=" + maxValue);
		// filterMain_minPriceTextView.setText(""+(minValue*EACH_PERCENTAGE_AMOUNT));
		// filterMain_maxPriceTextView.setText(""+(maxValue*EACH_PERCENTAGE_AMOUNT));
		// }
		// });

		// // add RangeSeekBar to pre-defined layout
		// filterMain_priceLayout = (ViewGroup)
		// findViewById(R.id.filterMain_priceLayout);
		// filterMain_priceLayout.addView(seekBar);

		Intent data = getIntent();
		if (data != null) {
			selectedBrand = (BrandEntity) data.getParcelableExtra(BrandListActivity.INTENT_SELECTED_BRAND);
			selectedColor = (FilterColor) data.getParcelableExtra(ColorActivity.INTENT_SELECTED_COLOR);
			minPrice = data.getIntExtra(FilterMainActivity.INTENT_SELECTED_MIN_PRICE, 0);
			maxPrice = data.getIntExtra(FilterMainActivity.INTENT_SELECTED_MAX_PRICE, FilterMainActivity.MAX_AMOUNT);
			leftThumbX = data.getFloatExtra(INTENT_RANGE_BAR_LEFT_THUMB_X, -1f);
			rightThumbX = data.getFloatExtra(INTENT_RANGE_BAR_RIGHT_THUMB_X, -1f);
		}

		// Price
		filterMain_minPriceTextView = (TextView) findViewById(R.id.filterMain_minPriceTextView);
		filterMain_maxPriceTextView = (TextView) findViewById(R.id.filterMain_maxPriceTextView);
		filterMain_rangeBar = (RangeBar) findViewById(R.id.filterMain_rangeBar);
		filterMain_rangeBar.setTickCount(NUM_OF_PERCENTAGE_IDX);
		filterMain_rangeBar.setTickHeight(0);
		filterMain_rangeBar.setOnRangeBarChangeListener(new OnRangeBarChangeListener() {

					@Override
					public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
						Log.d(TAG, "User selected new range values: MIN=" + leftThumbIndex + ", MAX=" + rightThumbIndex);

						// if(leftThumbIndex<0){
						// filterMain_rangeBar.setThumbIndices(0,rightThumbIndex);
						// return;
						// }
						// if(rightThumbIndex<0){
						// filterMain_rangeBar.setThumbIndices(leftThumbIndex,0);
						// return;
						// }
						// if(leftThumbIndex>NUM_OF_PERCENTAGE_IDX-1){
						// filterMain_rangeBar.setThumbIndices(NUM_OF_PERCENTAGE_IDX-1,rightThumbIndex);
						// return;
						// }
						// if(rightThumbIndex>NUM_OF_PERCENTAGE_IDX-1){
						// filterMain_rangeBar.setThumbIndices(leftThumbIndex,NUM_OF_PERCENTAGE_IDX-1);
						// return;
						// }
						//
						// int minPrice=calPrice(leftThumbIndex);
						// int maxPrice=calPrice(rightThumbIndex);
						//
						// if(minPrice>=MAX_AMOUNT){
						// filterMain_minPriceTextView.setText("$"+minPrice+getResources().getString(R.string.txt_above));
						// }else{
						// filterMain_minPriceTextView.setText("$"+minPrice);
						// }
						//
						// if(maxPrice>=MAX_AMOUNT){
						// filterMain_maxPriceTextView.setText("$"+maxPrice+getResources().getString(R.string.txt_above));
						// }else{
						// filterMain_maxPriceTextView.setText("$"+maxPrice);
						// }

					}
				});

		filterMain_rangeBar.setOnThumbMoveListener(new OnThumbMoveListener() {

			@Override
			public void onThumbMove(float barWidth, float leftCurX, float rightCurX) {
				Log.d(TAG, "RangeBar: barWidth=" + barWidth + ", leftCurX=" + leftCurX + ", rightCurX=" + rightCurX);

				float mLeftX = leftCurX;
				float mRightX = rightCurX;
				minPrice = (int) ((MAX_AMOUNT / barWidth) * mLeftX);
				maxPrice = (int) ((MAX_AMOUNT / barWidth) * mRightX);

				if (mLeftX >= barWidth) {
					filterMain_minPriceTextView.setText(dollarSign + minPrice + getResources().getString(R.string.filter_item_above));
				} else {
					filterMain_minPriceTextView.setText(dollarSign + minPrice);
				}
				if (mRightX >= barWidth) {
					filterMain_maxPriceTextView.setText(dollarSign + maxPrice + getResources().getString(R.string.filter_item_above));
				} else {
					filterMain_maxPriceTextView.setText(dollarSign + maxPrice);
				}
				if (leftCurX == 0 && rightCurX == barWidth) {
					filterMain_priceCrossImageView.setVisibility(View.INVISIBLE);
				} else {
					filterMain_priceCrossImageView.setVisibility(View.VISIBLE);
				}
			}
		});
		filterMain_priceCrossImageView = (ImageView) findViewById(R.id.filterMain_priceCrossImageView);
		filterMain_priceCrossImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						resetPrice();
					}
					
				});

		// Brand
		filterMain_brandLayout = (ViewGroup) findViewById(R.id.filterMain_brandLayout);
		filterMain_brandLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(FilterMainActivity.this, BrandListActivity.class);
				startActivityForResult(intent, ACTIVITY_RESULT_CODE_WAIT_4_BRAND);
			}

		});

		filterMain_brandListImageView = (ImageView) findViewById(R.id.filterMain_brandListImageView);
		filterMain_brandNameTextView = (TextView) findViewById(R.id.filterMain_brandNameTextView);
		filterMain_brandCrossImageView = (ImageView) findViewById(R.id.filterMain_brandCrossImageView);
		filterMain_brandCrossImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				resetBrand();
			}

		});

		// Color
		filterMain_colorLayout = (ViewGroup) findViewById(R.id.filterMain_colorLayout);
		filterMain_colorLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(FilterMainActivity.this, ColorActivity.class);
				intent.putExtra(ColorActivity.INTENT_SELECTED_COLOR, selectedColor);
				startActivityForResult(intent, ACTIVITY_RESULT_CODE_WAIT_4_COLOR);
			}

		});

		filterMain_colorListImageView = (ImageView) findViewById(R.id.filterMain_colorListImageView);
		filterMain_colorIconImageView = (ImageView) findViewById(R.id.filterMain_colorIconImageView);
		filterMain_colorNameTextView = (TextView) findViewById(R.id.filterMain_colorNameTextView);
		filterMain_colorCrossImageView = (ImageView) findViewById(R.id.filterMain_colorCrossImageView);
		filterMain_colorCrossImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				resetColor();
			}
		});

		filterMain_resetLayout = (Button) findViewById(R.id.filterMain_resetLayout);
		filterMain_resetLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				resetPrice();
				resetBrand();
				resetColor();
				putIntentData();
			}
		});

		filterMain_completeLayout = (Button) findViewById(R.id.filterMain_completeLayout);
		filterMain_completeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_PRODUCT_LIST_FILTER, null);
				putIntentData();
				finish();
			}
		});

		refreshSelectedPrice();
		refreshSelectedBrand();
		refreshSelectedColor();
	}

	public int calPrice(int percentageIdx) {
		int result = 0;
		int actualIdx = percentageIdx - BUFFER_PERCENTAGE_IDX;
		if (actualIdx < 0) {
			actualIdx = 0;
		}
		if (actualIdx > 100) {
			actualIdx = 100;
		}
		result = actualIdx * EACH_PERCENTAGE_AMOUNT;
		return result;
	}

	public void resetPrice() {
		// filterMain_rangeBar.setThumbIndices(0, NUM_OF_PERCENTAGE_IDX-1);
		filterMain_rangeBar.reset();
		filterMain_priceCrossImageView.setVisibility(View.INVISIBLE);
	}

	public void resetBrand() {
		filterMain_brandNameTextView.setText("");
		selectedBrand = null;
		filterMain_brandCrossImageView.setVisibility(View.INVISIBLE);
		filterMain_brandListImageView.setVisibility(View.VISIBLE);
	}

	public void resetColor() {
		filterMain_colorIconImageView.setVisibility(View.GONE);
		filterMain_colorNameTextView.setText("");
		selectedColor = null;
		filterMain_colorCrossImageView.setVisibility(View.INVISIBLE);
		filterMain_colorListImageView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_RESULT_CODE_WAIT_4_BRAND) {
			Log.d(TAG, "Result from BrandListActivity");
			if (resultCode == RESULT_OK) {
				BrandEntity brand = (BrandEntity) data.getParcelableExtra(BrandListActivity.INTENT_SELECTED_BRAND);
				// filterMain_brandNameTextView.setText(brand.getName());
				selectedBrand = brand;
				// filterMain_brandCrossImageView.setVisibility(View.VISIBLE);
				// filterMain_brandListImageView.setVisibility(View.INVISIBLE);
				refreshSelectedBrand();
			}
		}
		if (requestCode == ACTIVITY_RESULT_CODE_WAIT_4_COLOR) {
			Log.d(TAG, "Result from ColorActivity");
			if (resultCode == RESULT_OK) {
				FilterColor color = (FilterColor) data.getParcelableExtra(ColorActivity.INTENT_SELECTED_COLOR);
				// filterMain_colorNameTextView.setText(color.getName());
				// filterMain_colorIconImageView.setVisibility(View.VISIBLE);
				// if(color.getDrawableId()!=-1){
				//
				// filterMain_colorIconImageView.setBackgroundResource(color.getDrawableIdIconId());
				// }else if(color.getColorId()!=-1){
				//
				// filterMain_colorIconImageView.setBackgroundColor(getResources().getColor(color.getColorId()));
				// }
				selectedColor = color;
				//
				// filterMain_colorCrossImageView.setVisibility(View.VISIBLE);
				// filterMain_colorListImageView.setVisibility(View.INVISIBLE);
				refreshSelectedColor();
			}
		}
	}

	public void putIntentData() {
		Intent data = new Intent();
		data.putExtra(BrandListActivity.INTENT_SELECTED_BRAND, selectedBrand);
		data.putExtra(ColorActivity.INTENT_SELECTED_COLOR, selectedColor);
		data.putExtra(INTENT_SELECTED_MIN_PRICE, minPrice);
		data.putExtra(INTENT_SELECTED_MAX_PRICE, maxPrice);
		data.putExtra(INTENT_RANGE_BAR_LEFT_THUMB_X, filterMain_rangeBar.getLeftThumbsX());
		data.putExtra(INTENT_RANGE_BAR_RIGHT_THUMB_X, filterMain_rangeBar.getRightThumbsX());
		setResult(RESULT_OK, data);
	}

	public void refreshSelectedPrice() {
		// filterMain_rangeBar.setThumbsPosition(MAX_AMOUNT,minPrice,maxPrice);
		if (leftThumbX != -1 && rightThumbX != -1) {
			Log.d(TAG, "leftThumbX=" + leftThumbX + ", rightThumbX=" + rightThumbX);
			filterMain_rangeBar.setThumbsPosition(leftThumbX, rightThumbX);
		}
	}

	public void refreshSelectedBrand() {
		if (selectedBrand != null) {
			filterMain_brandNameTextView.setText(selectedBrand.getName());
			filterMain_brandCrossImageView.setVisibility(View.VISIBLE);
			filterMain_brandListImageView.setVisibility(View.INVISIBLE);
		}
	}

	public void refreshSelectedColor() {
		if (selectedColor != null) {
			filterMain_colorNameTextView.setText(selectedColor.getDisplayName());
			filterMain_colorIconImageView.setVisibility(View.VISIBLE);
			if (selectedColor.getDrawableId() != -1) {
				filterMain_colorIconImageView.setBackgroundResource(selectedColor.getDrawableIdIconId());
			} else if (selectedColor.getColorId() != -1) {
				filterMain_colorIconImageView.setBackgroundColor(getResources().getColor(selectedColor.getColorId()));
			}
			filterMain_colorCrossImageView.setVisibility(View.VISIBLE);
			filterMain_colorListImageView.setVisibility(View.INVISIBLE);
		}else {
			resetColor();
		}
	}

}
