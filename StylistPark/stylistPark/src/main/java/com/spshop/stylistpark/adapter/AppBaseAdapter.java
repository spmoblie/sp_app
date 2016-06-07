package com.spshop.stylistpark.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AppBaseAdapter<T> extends BaseAdapter {

	protected List<T> mDataList;
	protected WeakReference<Context> weakContext;
	protected Object tag;
	protected OnItemCellClickListener onItemCellClickListener;
	protected OnShowingLastItem onShowingLastItem;

	public interface OnItemCellClickListener {

		public void onItemCellClickListener(Object data);

	}

	public interface OnShowingLastItem {

		public void onShowingLastItem();

	}

	public AppBaseAdapter(Context mContext) {
		weakContext = new WeakReference<Context>(mContext);
	}

	public void setOnItemCellClickListener(OnItemCellClickListener onItemCellClickListener) {
		this.onItemCellClickListener = onItemCellClickListener;
	}

	public void setOnShowingLastItem(OnShowingLastItem onShowingLastItem) {
		this.onShowingLastItem = onShowingLastItem;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	@Override
	public int getCount() {
		if (mDataList != null)
			return mDataList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mDataList != null && mDataList.size() > 0)
			return mDataList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public void setDataList(List<T> mDataList) {
		this.mDataList = mDataList;
		this.notifyDataSetChanged();
	}

	public void addDataList(List<T> mDataList) {
		if (this.mDataList == null) {
			setDataList(mDataList);
		} else {
			this.mDataList.addAll(mDataList);
			this.notifyDataSetChanged();
		}
	}

	public List<T> getDataList() {
		return mDataList;
	}

	public void clearDataList() {
		if (mDataList != null)
			mDataList.clear();
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	public static class SuperHolder {
		public Object obj;
	}

}
