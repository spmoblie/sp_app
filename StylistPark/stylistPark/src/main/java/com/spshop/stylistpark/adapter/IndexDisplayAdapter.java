package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.spshop.stylistpark.entity.IndexDisplay;

import java.util.List;

public abstract class IndexDisplayAdapter extends AppBaseAdapter<Pair<String, List<? extends IndexDisplay>>> {

	final String TAG="IndexDisplayAdapter";

	public interface OnIndexDisplayItemClick {
		
		public void onIndexDisplayItemClick(IndexDisplay indexDisplay);
		
	}
	
	public IndexDisplayAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public IndexDisplay getItem(int position) {
	
		int cursor=0;
		
		if(mDataList!=null && mDataList.size()>0){
			
			for (int i = 0; i < mDataList.size(); i++) {
				if (position >= cursor
						&& position < cursor + mDataList.get(i).second.size()) {
					Log.d(TAG,"getItem(): Position="+position+" Item Position="+(position - cursor)+" cursor="+cursor);
					return mDataList.get(i).second.get(position - cursor);
				}
				cursor += mDataList .get(i).second.size();
			}
		}
		
		return null;
	
	}
	
	@Override
	public int getCount() {
		int count = 0;
		if(mDataList!=null){
			for (int i = 0; i < mDataList.size(); i++) {
				count += mDataList.get(i).second.size();
			}
		}
		Log.d(TAG, "item count="+count);
		return count;
	}
	
	public boolean isFirst(int position){
		boolean result=false;
		int cursor=0;
		if(mDataList!=null && mDataList.size()>0){
			
			for (int i = 0; i < mDataList.size(); i++) {
				if(position==cursor){
					result= true;
					break;
				}else if(position<cursor){
					result= false;
					break;
				}else{
					cursor+=mDataList.get(i).second.size();
				}
			}
		}
		return result;
		
	}
	
	public boolean isLast(int position){
		boolean result=false;
		int cursor=0;
		if(mDataList!=null && mDataList.size()>0){
			
			for (int i = 0; i < mDataList.size(); i++) {
				
				cursor+=mDataList.get(i).second.size();
				
				if(position==cursor-1){
					result=true;
					break;
				}else if(position<cursor-1){
					result=false;
					break;
				}
			}
		}
		return result;
	}
	
	public String getIndexChar(int position){
		int cursor=0;
		if(mDataList!=null && mDataList.size()>0){
			
			for (int i = 0; i < mDataList.size(); i++) {
				if (position >= cursor
						&& position < cursor + mDataList.get(i).second.size()) {
					Log.d(TAG,"getIndexChar(): Position="+position+" Item Position="+(position - cursor)+" cursor="+cursor);
					return mDataList.get(i).first;
				}
				cursor += mDataList .get(i).second.size();
			}
		}
		return "";
	}
	
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
