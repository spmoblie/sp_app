package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.CategoryListEntity;
import com.spshop.stylistpark.image.AsyncImageLruCache;
import com.spshop.stylistpark.image.AsyncImageLruCache.AsyncImageLruCacheCallback;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LangCurrTools.Language;


/**
 * 商品分类GridView适配器 
 */
public class CategoryGridAdapter extends BaseAdapter{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	
	private Context context;
	private List<CategoryListEntity> datas;
	private AdapterCallback apCallback;
	private GridView mGridView;
	private Language lang;
	private AsyncImageLruCache ailc;
    //可见的第一张图片的下标
    private int mFirstVisibleItem;
    //可见的图片的数量
    private int mVisibleItemCount;
    //记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    
    //private DisplayImageOptions options;
	
	public CategoryGridAdapter(Context context, List<CategoryListEntity> datas,
			AdapterCallback callback, GridView gridView) {
		this.context = context;
		this.datas = datas;
		this.apCallback = callback;
		lang = LangCurrTools.getLanguage(context);
		
		//options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
		
		mGridView = gridView;
        mGridView.setOnScrollListener(new ScrollListenerImpl());
		
        ailc = new AsyncImageLruCache(context, new AsyncImageLruCacheCallback() {
			
			@Override
			public void imageLoaded(String imageUrl, Bitmap bitmap) {
				setImageBitmap(imageUrl, bitmap);
			}
		});
	}
	
	public void updateAdapter(List<CategoryListEntity> datas){
		if (datas != null) {
			this.datas = datas;
			isFirstEnterThisActivity = true;
			notifyDataSetChanged();
		}
	}

	/**获得总共有多少条数据*/
	@Override
	public int getCount() {
		return datas.size();
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
		RelativeLayout rl_item;
		ImageView iv_img;
		TextView tv_name;
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_grid_category, null);
			
			holder = new ViewHolder();
			holder.rl_item = (RelativeLayout) convertView.findViewById(R.id.grid_item_category_rl);
			holder.iv_img = (ImageView) convertView.findViewById(R.id.grid_item_category_iv);
			holder.tv_name = (TextView) convertView.findViewById(R.id.grid_item_category_tv);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		final CategoryListEntity data = datas.get(position);
		
		if (lang == Language.En) {
			holder.tv_name.setTextSize(12);
		}else {
			holder.tv_name.setTextSize(14);
		}
		holder.tv_name.setText(data.getName());
		
		String imageUrl = IMAGE_URL_HTTP + data.getImageUrl();
		holder.iv_img.setTag(imageUrl);
		//为该ImageView设置显示的图片
        setImageForImageView(imageUrl, holder.iv_img);
		//ImageLoader.getInstance().displayImage(imageUrl, holder.iv_img, options);
		
		holder.rl_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, 0);
			}
		});
		return convertView;
	}
    
    /**
     * 为ImageView设置图片(Image)
     * 1 从缓存中获取图片
     * 2 若图片不在缓存中则为其设置默认图片
     */
	private void setImageForImageView(String imageUrl, ImageView imageView) {
        Bitmap bitmap = ailc.getBitmapFromLruCache(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher);
        }
    }

	/**
	 * 依据Tag找到对应的ImageView显示图片
	 */
	private void setImageBitmap(String imageUrl, Bitmap bitmap) {
		 ImageView imageView = (ImageView) mGridView.findViewWithTag(imageUrl);
		 if (imageView != null && bitmap != null) {
		     imageView.setImageBitmap(bitmap);
		 }
	}
	
	private class ScrollListenerImpl implements OnScrollListener{
        /**
         * 
         * 我们的本意是通过onScrollStateChanged获知:每次GridView停止滑动时加载图片
         * 但是存在一个特殊情况:
         * 当第一次入应用的时候,此时并没有滑动屏幕的操作即不会调用onScrollStateChanged,但应该加载图片.
         * 所以在此处做一个特殊的处理.
         * 即代码:
         * if (isFirstEnterThisActivity && visibleItemCount > 0) {
         *      loadBitmaps(firstVisibleItem, visibleItemCount);
         *      isFirstEnterThisActivity = false;
         *    }
         *    
         * ------------------------------------------------------------
         * 
         * 其余的都是正常情况.
         * 所以我们需要不断保存:firstVisibleItem和visibleItemCount
         * 从而便于中在onScrollStateChanged()判断当停止滑动时加载图片
         * 
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            if (isFirstEnterThisActivity && visibleItemCount > 0) {
                loadBitmaps(firstVisibleItem, visibleItemCount);
                isFirstEnterThisActivity = false;
            }
        }
         
        /**
         *  GridView停止滑动时下载图片
         *  其余情况下取消所有正在下载或者等待下载的任务
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
            } else {
                ailc.cancelAllTasks();
            }
        }
         
    }
    
    /**
     * 为GridView的item加载图片
     * 
     * @param firstVisibleItem 
     * GridView中可见的第一张图片的下标
     * 
     * @param visibleItemCount 
     * GridView中可见的图片的数量
     * 
     */
     private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
         try {
             for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                 String imageUrl = IMAGE_URL_HTTP + datas.get(i).getImageUrl();
                 Bitmap bitmap = ailc.getBitmapFromLruCache(imageUrl);
                 if (bitmap == null) {
                     ailc.executeAsyncTaskLoad(imageUrl);
                 } else {
                     setImageBitmap(imageUrl, bitmap);
                 }
             }
         } catch (Exception e) {
             ExceptionUtil.handle(context, e);
         }
     }
    
}
