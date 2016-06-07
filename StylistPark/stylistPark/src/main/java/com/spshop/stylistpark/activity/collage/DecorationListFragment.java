package com.spshop.stylistpark.activity.collage;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.adapter.DecorationAdapter;
import com.spshop.stylistpark.adapter.DecorationAdapter.OnDecorationItemClickListener;
import com.spshop.stylistpark.db.DecorationDBManager;
import com.spshop.stylistpark.entity.Decoration;
import com.spshop.stylistpark.entity.RowObject;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.widgets.InterceptTouchListView;

public class DecorationListFragment  extends BaseFragment{
	
	InterceptTouchListView decoration_ListView;
	List<Decoration> decorationList;
	DecorationAdapter decorationAdapter;
	OnDecorationItemClickListener onDecorationItemClickListener;

	int pos = 0;
	int offset = 0;
	List<RowObject> rowDecorstionList;
	Context context;

	// filter
	String keyword = null;
	String color = null;

	View decoration_dummyLayout;
	TextView decoration_errNoResultTextView;
	
	public static DecorationListFragment newInstance(){
		return new DecorationListFragment();
	}
	
	public void setOnDecorationItemClickListener(OnDecorationItemClickListener onDecorationItemClickListener){
		this.onDecorationItemClickListener = onDecorationItemClickListener;
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_product_decoration, container, false);
		decoration_ListView = (InterceptTouchListView) view.findViewById(R.id.decoration_ListView);
		decorationAdapter = new DecorationAdapter(getActivity());
		decoration_dummyLayout = view.findViewById(R.id.decoration_dummyLayout);
		decoration_errNoResultTextView = (TextView) view.findViewById(R.id.decoration_errNoResultTextView);
		return view;
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		if (onDecorationItemClickListener != null) {
			decorationAdapter.setOnDecorationItemClickListener(onDecorationItemClickListener);
		}
		if (rowDecorstionList == null) {
			setUpDecoration();
			if (decorationList != null && decorationList.size() != 0) {
				decorationAdapter.setDataList(CommonTools.convertToRowObject(decorationList, 2));
				decoration_ListView.setAdapter(decorationAdapter);
			} else {
				showNoResult();
			}
		} else {
			decorationAdapter.addDataList(rowDecorstionList);
			decoration_ListView.setAdapter(decorationAdapter);
			decoration_ListView.setSelectionFromTop(pos, offset);
		}
	}

	public void setItemPos(int pos) {
		this.pos = pos;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setRowDecorationList(List<RowObject> rowDecorstionList) {
		this.rowDecorstionList = rowDecorstionList;
	}

	public int getCurrentItemPos() {
		return decoration_ListView.getFirstVisiblePosition();
	}

	public int getCurrentItemOffSet() {
		return decoration_ListView.getChildAt(0).getTop();
	}

	public List<RowObject> getRowDecorationList() {
		return decorationAdapter.getDataList();
	}
	
	public void setUpDecoration(){
		Log.d(TAG, "setUpDecoration()");
		if(keyword != null || color != null){
			decorationList = DecorationDBManager.getInstance(context).searchDecoration(keyword, color);
		}else{
			decorationList = DecorationDBManager.getInstance(context).getAllDecoration();
		}
//		decorationList=new ArrayList<Decoration>();
//		
//		Decoration tmp;
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_001);
//		tmp.setImgThumbId(R.drawable.decoration_image_001_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_002);
//		tmp.setImgThumbId(R.drawable.decoration_image_002_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_003);
//		tmp.setImgThumbId(R.drawable.decoration_image_003_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_004);
//		tmp.setImgThumbId(R.drawable.decoration_image_004_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_005);
//		tmp.setImgThumbId(R.drawable.decoration_image_005_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_006);
//		tmp.setImgThumbId(R.drawable.decoration_image_006_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_007);
//		tmp.setImgThumbId(R.drawable.decoration_image_007_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_008);
//		tmp.setImgThumbId(R.drawable.decoration_image_008_300x300);
//		decorationList.add(tmp);
//		
//		tmp=new Decoration();
//		tmp.setImgId(R.drawable.decoration_image_009);
//		tmp.setImgThumbId(R.drawable.decoration_image_009_300x300);
//		decorationList.add(tmp);
	
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void showDummyFooter(boolean show) {
		decoration_dummyLayout.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showNoResult() {
		putViewInCenterVertical(decoration_errNoResultTextView);
	}

}
