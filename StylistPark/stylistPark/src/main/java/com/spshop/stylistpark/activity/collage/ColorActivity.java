package com.spshop.stylistpark.activity.collage;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.AppBaseAdapter.OnItemCellClickListener;
import com.spshop.stylistpark.adapter.ColorAdapter;
import com.spshop.stylistpark.entity.FilterColor;
import com.spshop.stylistpark.utils.UserTracker;

public class ColorActivity extends BaseActivity {
	
	public static final String INTENT_SELECTED_COLOR = "intent_selected_color";
	public static final String INTENT_COLOR_MODE = "intent_color_mode";
	GridView color_GridView;
	ColorAdapter colorAdapter;
	List<FilterColor> colorList;
	FilterColor selectedColor;

	ColorMode colorMode = ColorMode.Product;

	public enum ColorMode {
		Product, Decoration;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent data = getIntent();
		// read color mode
		if (data != null) {
			ColorMode tmpColorMode = (ColorMode) data.getSerializableExtra(INTENT_COLOR_MODE);
			if (tmpColorMode != null)
				colorMode = tmpColorMode;
		}
		// set color to be displayed and layout
		if (colorMode == ColorMode.Decoration) {
			setUpColorForDecoration();
		} else if (colorMode == ColorMode.Product) {
			setUpColorForProduct();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_list);
		
		setTitle(R.string.filter_select_color); //设置标题

		color_GridView = (GridView) findViewById(R.id.color_GridView);
		colorAdapter = new ColorAdapter(this);
		colorAdapter.setDataList(colorList);
		colorAdapter.setOnItemCellClickListener(new OnItemCellClickListener() {

			@Override
			public void onItemCellClickListener(Object data) {
				if (colorMode == ColorMode.Decoration) {
					UserTracker.getInstance().trackUserAction(UserTracker.Action.EVENT_DECORATION_FILTER, null);
				}
				FilterColor color = (FilterColor) data;
				Intent intent = new Intent();
				intent.putExtra(INTENT_SELECTED_COLOR, color);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		color_GridView.setAdapter(colorAdapter);

		if (data != null) {
			selectedColor = (FilterColor) data.getParcelableExtra(ColorActivity.INTENT_SELECTED_COLOR);
			colorAdapter.setSelectedColor(selectedColor);
		}
		if (selectedColor != null) {
			setBtnRight(getString(R.string.clean)); //设置右上角按钮
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}

	public void setUpColorForProduct() {
		colorList = new ArrayList<FilterColor>();
		FilterColor tmp;

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_print));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_print_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_printing, R.drawable.generator_filter_color_printing);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_dot));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_dot_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_dot, R.drawable.generator_filter_color_dot);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_grid));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_grid_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_plaid, R.drawable.generator_filter_color_plaid);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_line));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_line_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_stripe, R.drawable.generator_filter_color_stripe);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_red));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_red_zh));
		tmp.setColorId(R.color.color_red_229_0_28);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_pink));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_pink_zh));
		tmp.setColorId(R.color.color_pink_252_129_193);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_orange));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_orange_zh));
		tmp.setColorId(R.color.color_orange_253_134_9);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_yellow));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_yellow_zh));
		tmp.setColorId(R.color.color_yellow_252_224_16);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_green));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_green_zh));
		tmp.setColorId(R.color.color_green_62_224_56);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_deep_blue));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_deep_blue_zh));
		tmp.setColorId(R.color.color_deep_blue_11_34_103);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_blue));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_blue_zh));
		tmp.setColorId(R.color.color_blue_15_95_214);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_purple));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_purple_zh));
		tmp.setColorId(R.color.color_purple_97_13_203);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_brown));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_brown_zh));
		tmp.setColorId(R.color.color_brown_114_54_9);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_light_brown));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_light_brown_zh));
		tmp.setColorId(R.color.color_light_brown_213_161_57);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_light_yellow));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_light_yellow_zh));
		tmp.setColorId(R.color.color_light_yellow_253_241_162);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_white));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_white_zh));
		tmp.setDrawableId(R.drawable.shape_frame_iv_white_gray_2, R.drawable.shape_frame_iv_white_gray_2);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_black));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_black_zh));
		tmp.setColorId(R.color.text_color_black);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_grey));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_grey_zh));
		tmp.setColorId(R.color.color_grey_120_120_120);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_gold));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_gold_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_gold, R.drawable.generator_filter_list_color_gold);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_silver));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_silver_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_silver, R.drawable.generator_filter_color_silver);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName(getResources().getString(R.string.txt_color_muti_color));
		tmp.setDisplayName(getResources().getString(R.string.txt_color_muti_color_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_colorful, R.drawable.generator_filter_color_colorful);
		colorList.add(tmp);

	}

	public void setUpColorForDecoration() {
		colorList = new ArrayList<FilterColor>();
		FilterColor tmp;

		tmp = new FilterColor();
		tmp.setName("red");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_red_zh));
		tmp.setColorId(R.color.color_red_229_0_28);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("pink");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_pink_zh));
		tmp.setColorId(R.color.color_pink_252_129_193);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("orange");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_orange_zh));
		tmp.setColorId(R.color.color_orange_253_134_9);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("yellow");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_yellow_zh));
		tmp.setColorId(R.color.color_yellow_252_224_16);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("green");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_green_zh));
		tmp.setColorId(R.color.color_green_62_224_56);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("darkBlue");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_deep_blue_zh));
		tmp.setColorId(R.color.color_deep_blue_11_34_103);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("blue");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_blue_zh));
		tmp.setColorId(R.color.color_blue_15_95_214);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("purple");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_purple_zh));
		tmp.setColorId(R.color.color_purple_97_13_203);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("brown");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_brown_zh));
		tmp.setColorId(R.color.color_brown_114_54_9);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("apricot");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_light_brown_zh));
		tmp.setColorId(R.color.color_light_brown_213_161_57);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("rice");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_light_yellow_zh));
		tmp.setColorId(R.color.color_light_yellow_253_241_162);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("white");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_white_zh));
		tmp.setDrawableId(R.drawable.shape_frame_iv_white_gray_2, R.drawable.shape_frame_iv_white_gray_2);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("black");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_black_zh));
		tmp.setColorId(R.color.text_color_black);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("grey");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_grey_zh));
		tmp.setColorId(R.color.color_grey_120_120_120);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("gold");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_gold_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_gold, R.drawable.generator_filter_list_color_gold);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("silver");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_silver_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_silver, R.drawable.generator_filter_color_silver);
		colorList.add(tmp);

		tmp = new FilterColor();
		tmp.setName("colorful");
		tmp.setDisplayName(getResources().getString(R.string.txt_color_muti_color_zh));
		tmp.setDrawableId(R.drawable.generator_filter_list_color_colorful, R.drawable.generator_filter_color_colorful);
		colorList.add(tmp);
	}

}
