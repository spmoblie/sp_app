package com.spshop.stylistpark.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.ProductAttrEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * “加入购物车”弹层ListView适配器
 */
@SuppressLint({ "NewApi", "UseSparseArrays" })
public class AddCartPopupListAdapter extends BaseAdapter{
	
	private Context context;
	private List<ProductAttrEntity> datas;
	private HashMap<String, Integer> skuHashMap = new HashMap<String, Integer>();
	private HashMap<Integer, ProductAttrEntity> attrHashMap = new HashMap<Integer, ProductAttrEntity>();;
	private AddCartCallback callback;
	private int mgWidth, pdWidth, size;
	private int select_id_1, select_id_2;
	private String attr_name_1, attr_name_2, select_name_1, select_name_2;
	private View[] views_1, views_2;
	
	public AddCartPopupListAdapter(Context context, ProductAttrEntity attrEn, AddCartCallback callback) {
		this.context = context;
		this.callback = callback;
		
		pdWidth = CommonTools.dip2px(context, 15);
		mgWidth = CommonTools.dip2px(context, 15);
		
		getAttrDatas(attrEn);
		if (datas == null) {
			datas = new ArrayList<ProductAttrEntity>();
		}
	}

	/**获得总共有多少条数据*/
	@Override
	public int getCount() {
		size = datas.size();
		return size;
	}

	/**在ListView中显示的每个item内容*/
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	/**返回集合中个某个元素的位置*/
	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder{
		
		RelativeLayout rl_main;
		
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_cart_popup, null);
			
			holder = new ViewHolder();
			holder.rl_main = (RelativeLayout) convertView.findViewById(R.id.popup_add_cart_rl_attr_main);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		final ProductAttrEntity data = datas.get(position);
		
		if (data != null) {
			holder.rl_main.removeAllViews();
			addAttributeView(holder.rl_main, data, position);
		}
		
		return convertView;
	}
	
	/**
	 * 动态添加View
	 */
	private void addAttributeView(RelativeLayout rl_main, final ProductAttrEntity data, final int position) {
		// 添加属性类别名称
		int attrId = data.getAttrId();
		TextView tv_name = new TextView(context);
		tv_name.setText(data.getAttrName() + ":");
		tv_name.setTextColor(context.getResources().getColor(R.color.text_color_assist));
		tv_name.setTextSize(12);
		tv_name.setId(attrId);
		rl_main.addView(tv_name);
		
//		// 添加选择的属性名称
//		int showId = attrId + 10000;
//		final TextView tv_name_show = new TextView(context);
//		tv_name_show.setText("");
//		tv_name_show.setTextColor(context.getResources().getColor(R.color.text_color_lialic));
//		tv_name_show.setTextSize(12);
//		tv_name_show.setId(showId);
//		LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params1.addRule(RelativeLayout.RIGHT_OF, attrId);
//		params1.setMargins(5, 0, 0, 0);
//		rl_main.addView(tv_name_show, params1);
		
		ArrayList<ProductAttrEntity> nameLists = data.getAttrLists();
		if (nameLists == null || nameLists.size() == 0) {
			return;
		}
		// 循环添加属性View
		Paint pFont = new Paint();
		Rect rect = new Rect();
		String str = "";
		int viewId = 0;
		int strWidth = 0;
		int widthTotal = mgWidth;
		int fristId = 0;
		int beforeId = 0;
		
		switch (position) {
		case 0:
			attr_name_1 = data.getAttrName();
			views_1 = new View[nameLists.size()]; 
			break;
		case 1:
			attr_name_2 = data.getAttrName();
			views_2 = new View[nameLists.size()];
			break;
		}
		
		for (int i = 0; i < nameLists.size(); i++) {
			str = nameLists.get(i).getAttrName();
			// 计算str的宽度
			pFont.getTextBounds(str, 0, str.length(), rect);
			strWidth = CommonTools.dip2px(context, rect.width());
			widthTotal += (strWidth*1.5 + pdWidth);
			
			viewId = nameLists.get(i).getAttrId();
			if (i > 0) {
				beforeId = nameLists.get(i-1).getAttrId();
			}
			
			TextView tv = new TextView(context);
			// 判定库存数
			int skuNum = nameLists.get(i).getSkuNum();
			if (skuNum > 0) {
				tv.setTextColor(context.getResources().getColor(R.color.text_color_black));
				tv.setBackground(context.getResources().getDrawable(R.drawable.selector_btn_small));
			}else {
				tv.setTextColor(context.getResources().getColor(R.color.text_color_thin));
				tv.setBackground(context.getResources().getDrawable(R.drawable.shape_frame_tv_white_gray_4));
			}
			// 记录库存数
			skuHashMap.put(String.valueOf(viewId), skuNum);
			attrHashMap.put(viewId, nameLists.get(i));
			tv.setPadding(15, 8, 15, 8);
			tv.setGravity(Gravity.CENTER);
			tv.setText(str);
			tv.setTextSize(14);
			tv.setId(viewId);
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView tv = (TextView) v;
					switch (size) {
					case 1: //一种属性
						if (getSkuNum(String.valueOf(v.getId())) <= 0) { //不可选
							return;
						}
						defaultViewStatus(views_1);
						updateSelectStatus(v, tv, position, select_id_1);
						if (select_id_1 > 0) {
							callback.setOnClick(data, position, getSkuNum(String.valueOf(select_id_1)), getAttrPrice(select_id_1), 
									select_id_1, 0, getSelectShowStr(select_name_1, ""), getAttrImage(select_id_1));
						}else {
							callback.setOnClick(data, position, -1, 0, select_id_1, 0, attr_name_1, "");
						}
						break;
					case 2: //两种属性
						switch (position) {
						case 0: //第一种
							if (getSkuNum(String.valueOf(v.getId())) <= 0) { //不可选
								return;
							}
							// 首先更新第二种属性状态
							if (select_id_1 == v.getId()) { //取消
								select_id_2 = 0;
								select_name_2 = "";
								defaultViewStatus(views_2);
							}else {
								if (views_1.length > 1) { //两选项以上
									select_id_2 = 0;
									select_name_2 = "";
									updateViewStatus(v.getId());
								}
							}
							// 其次更新第一种属性状态
							defaultViewStatus(views_1);
							updateSelectStatus(v, tv, position, select_id_1);
							break;
						case 1: //第二种
							if (select_id_1 <= 0) { //第一未选
								if (getSkuNum(String.valueOf(v.getId())) <= 0) { //不可选
									return;
								}
								defaultViewStatus(views_2);
							}else { //第一已选
								if (getSkuNum(select_id_1 + "|" + String.valueOf(v.getId())) <= 0) { //不可选
									return;
								}
								updateViewStatus(select_id_1);
							}
							updateSelectStatus(v, tv, position, select_id_2);
							break;
						}
						int attrPrice = 0;
						if (select_id_1 > 0) {
							attrPrice += getAttrPrice(select_id_1);
						}
						if (select_id_2 > 0) {
							attrPrice += getAttrPrice(select_id_2);
						}
						if (select_id_1 > 0 && select_id_2 > 0) {
							callback.setOnClick(data, position, getSkuNum(select_id_1 + "|" + select_id_2), attrPrice, 
									select_id_1, select_id_2, getSelectShowStr(select_name_1, select_name_2), getAttrImage(select_id_1));
						}else {
							String select_name = "";
							if (select_id_1 <= 0) {
								select_name = attr_name_1;
							}
							if (select_id_2 <= 0) {
								if (StringUtil.isNull(select_name)) {
									select_name = attr_name_2;
								}else {
									select_name = select_name + "、" + attr_name_2;
								}
							}
							callback.setOnClick(data, position, -1, attrPrice, select_id_1, select_id_2, select_name, getAttrImage(select_id_1));
						}
						break;
					}
				}

			});
			switch (position) {
			case 0:
				views_1[i] = tv; 
				break;
			case 1:
				views_2[i] = tv;
				break;
			}
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (i == 0) {
				params.addRule(RelativeLayout.BELOW, data.getAttrId()); //在此id控件的下边
				fristId = viewId;
			}else {
				if (widthTotal < AppApplication.screenWidth) {
					params.addRule(RelativeLayout.RIGHT_OF, beforeId); //在控件的右边
					params.addRule(RelativeLayout.ALIGN_BOTTOM, beforeId); //与控件底部对齐
				}else {
					params.addRule(RelativeLayout.BELOW, fristId); //在控件的下边
					fristId = viewId;
					widthTotal = strWidth + mgWidth;
				}
			}
			params.setMargins(0, 10, 10, 0);
			rl_main.addView(tv,params);
		}
	}

	private void defaultViewStatus(View[] views) {
		int num = 0;
		for (int i = 0; i < views.length; i++) {
			TextView tv_item = (TextView)views[i];
			num = getSkuNum(String.valueOf(views[i].getId()));
			if (num == 0) {
				tv_item.setTextColor(context.getResources().getColor(R.color.text_color_thin));
				views[i].setBackground(context.getResources().getDrawable(R.drawable.shape_frame_tv_white_gray_4));
			}else {
				tv_item.setTextColor(context.getResources().getColor(R.color.text_color_black));
				views[i].setBackground(context.getResources().getDrawable(R.drawable.selector_btn_small));
				views[i].setSelected(false);
			}
			views[i].setPadding(15, 8, 15, 8);
		}
	}

	private void updateSelectStatus(View v, TextView tv, int position, int selectId) {
		TextView tv_item = (TextView)v;
		switch (position) {
		case 0:
			if (selectId != v.getId()) {
				tv_item.setTextColor(context.getResources().getColor(R.color.text_color_white));
				v.setSelected(true);
				selectId = v.getId();
				select_name_1 = tv.getText().toString();
			}else {
				selectId = 0;
				select_name_1 = "";
			}
			select_id_1 = selectId;
			//tv_show.setText(select_name_1);
			break;
		case 1:
			if (selectId != v.getId()) {
				tv_item.setTextColor(context.getResources().getColor(R.color.text_color_white));
				v.setSelected(true);
				selectId = v.getId();
				select_name_2 = tv.getText().toString();
			}else {
				selectId = 0;
				select_name_2 = "";
			}
			select_id_2 = selectId;
			//tv_show.setText(select_name_2);
			break;
		}
	}
	
	private void updateViewStatus(int selectId) {
		int num = 0;
		for (int i = 0; i < views_2.length; i++) {
			TextView tv_item = (TextView)views_2[i];
			num = getSkuNum(String.valueOf(selectId) + "|" + String.valueOf(views_2[i].getId()));
			if (num == 0) {
				tv_item.setTextColor(context.getResources().getColor(R.color.text_color_thin));
				views_2[i].setBackground(context.getResources().getDrawable(R.drawable.shape_frame_tv_white_gray_4));
			}else {
				tv_item.setTextColor(context.getResources().getColor(R.color.text_color_black));
				views_2[i].setBackground(context.getResources().getDrawable(R.drawable.selector_btn_small));
				views_2[i].setSelected(false);
			}
			views_2[i].setPadding(15, 8, 15, 8);
		}
	}
	
	private int getSkuNum(String keyStr) {
		if (skuHashMap.containsKey(keyStr)) {
			return skuHashMap.get(keyStr);
		}
		return -1;
	}
	
	private int getAttrPrice(int key) {
		if (attrHashMap.containsKey(key)) {
			return attrHashMap.get(key).getAttrPrice();
		}
		return 0;
	}
	
	private String getAttrImage(int key) {
		if (attrHashMap.containsKey(key)) {
			return attrHashMap.get(key).getAttrImg();
		}
		return "";
	}
	
	private String getSelectShowStr(String show1, String show2){
		StringBuilder sb = new StringBuilder();
		if (!StringUtil.isNull(show1)) {
			sb.append("“");
			sb.append(show1);
			sb.append("”");
		}
		if (!StringUtil.isNull(show2)) {
			if (!StringUtil.isNull(show1)) {
				sb.append(" ");
			}
			sb.append("“");
			sb.append(show2);
			sb.append("”");
		}
		return sb.toString();
	}

	private void getAttrDatas(ProductAttrEntity attrEn) {
		if (attrEn != null) {
			datas = attrEn.getAttrLists();
			if (attrEn.getSkuLists() != null) {
				skuHashMap.clear();
				ProductAttrEntity sku = null;
				for (int i = 0; i < attrEn.getSkuLists().size(); i++) {
					sku = attrEn.getSkuLists().get(i);
					if (sku != null) {
						skuHashMap.put(sku.getSku_key(), sku.getSku_value());
					}
				}
			}
		}
	}
	
	public interface AddCartCallback {
		
		void setOnClick(Object entity, int position, int num, int attrPrice, 
				int id1, int id2, String selectName, String selectImg);

	}
	
}
