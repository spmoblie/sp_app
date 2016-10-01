package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.CommentEntity;

import java.util.List;

/**
 * 评论列表适配器
 */
public class CommentListAdapter extends BaseAdapter {

	private Context context;
	private List<CommentEntity> datas;
	private DisplayImageOptions headOptions;

	public CommentListAdapter(Context context, List<CommentEntity> datas) {
		this.context = context;
		this.datas = datas;
		this.headOptions = AppApplication.getHeadImageOptions();
	}

	public void updateAdapter(List<CommentEntity> datas) {
		if (datas != null) {
			this.datas = datas;
			notifyDataSetChanged();
		}
	}

	/** 获得总共有多少条数据 */
	@Override
	public int getCount() {
		return datas.size();
	}

	/** 在ListView中显示的每个item内容 */
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	/** 返回集合中个某个元素的位置 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {

		ImageView iv_head;
		TextView tv_nick, tv_content, tv_time;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_comment, null);
			holder = new ViewHolder();
			holder.iv_head = (ImageView) convertView.findViewById(R.id.item_list_comment_iv_head);
			holder.tv_nick = (TextView) convertView.findViewById(R.id.item_list_comment_tv_nick);
			holder.tv_content = (TextView) convertView.findViewById(R.id.item_list_comment_tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.item_list_comment_tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CommentEntity data = datas.get(position);

		ImageLoader.getInstance().displayImage(data.getHeadImg(), holder.iv_head, headOptions);
		holder.tv_nick.setText(data.getUserNick());
		holder.tv_content.setText(data.getContent());
		holder.tv_time.setText(data.getAddTime());

		return convertView;
	}

}
