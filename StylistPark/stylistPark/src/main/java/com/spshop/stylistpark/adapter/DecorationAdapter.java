package com.spshop.stylistpark.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.Decoration;
import com.spshop.stylistpark.entity.RowObject;

public class DecorationAdapter extends AppBaseAdapter<RowObject>{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	
	private DisplayImageOptions options;
	
	OnDecorationItemClickListener onDecorationItemClickListener;
	
	
	public interface OnDecorationItemClickListener{
		
		public void onDecorationItemClick(Decoration decoration);
		
	}
	

	public DecorationAdapter(Context mContext) {
		super(mContext);
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
	}
	

	public void setOnDecorationItemClickListener(OnDecorationItemClickListener onDecorationItemClickListener){
		this.onDecorationItemClickListener=onDecorationItemClickListener;
	}
	
//	public static List<ThreeColDecoration> convertToThreeColDecoration(List<Decoration> dataList){
//		List<ThreeColDecoration> result=new ArrayList<ThreeColDecoration>();
//		
//		for(int i=0; i<dataList.size();){
//			ThreeColDecoration tmp=null;
//			
//			if(i<dataList.size() && dataList.get(i)!=null){
//				
//				tmp =new ThreeColDecoration();
//				
//				tmp.col1=dataList.get(i);
//				i+=1;
//			}
//			
//			if(i<dataList.size() && dataList.get(i)!=null ){
//				
//				tmp.col2=dataList.get(i);
//				i+=1;
//			}
//			
//			if(i<dataList.size() && dataList.get(i)!=null){
//				
//				tmp.col3=dataList.get(i);
//				i+=1;
//			}
//			
//			if(tmp!=null){
//				result.add(tmp);
//			}
//		}
//		
//		return result;
//		
//	}
//	
	


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RowObject row=(RowObject)getItem(position);
		Decoration colOneDecoration=(Decoration)row.objectArr[0];
		Decoration colTwoDecoration=(Decoration)row.objectArr[1];
		//Decoration colThreeDecoration=row.col3;
		
		ChildHolder holder = null;

		if (convertView == null) {
			
			holder = new ChildHolder();
			
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_list_decoration_three, null);
			holder.cellDecorationIcon1_Layout=(ViewGroup) convertView.findViewById(R.id.cellDecorationIcon1_Layout);
			holder.cellDecorationIcon1_ImageView=(ImageView ) convertView.findViewById(R.id.cellDecorationIcon1_ImageView);
		
			holder.cellDecorationIcon2_Layout=(ViewGroup) convertView.findViewById(R.id.cellDecorationIcon2_Layout);
			holder.cellDecorationIcon2_ImageView=(ImageView ) convertView.findViewById(R.id.cellDecorationIcon2_ImageView);
		
			holder.cellDecorationIcon3_Layout=(ViewGroup) convertView.findViewById(R.id.cellDecorationIcon3_Layout);
			holder.cellDecorationIcon3_ImageView=(ImageView) convertView.findViewById(R.id.cellDecorationIcon3_ImageView);
			
			holder.cellDecorationIcon1_Layout.setOnClickListener(getDecorationOnClickListener());
			holder.cellDecorationIcon2_Layout.setOnClickListener(getDecorationOnClickListener());
			holder.cellDecorationIcon3_Layout.setOnClickListener(getDecorationOnClickListener());

			convertView.setTag(holder);
			
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		
		if(row!=null){
			
//			holder.cellDecorationIcon1_ImageView.setImageBitmap(null);
//			holder.cellDecorationIcon2_ImageView.setImageBitmap(null);
//			holder.cellDecorationIcon3_ImageView.setImageBitmap(null);
			if(colOneDecoration!=null){
				holder.cellDecorationIcon1_Layout.setTag(colOneDecoration);
				ImageLoader.getInstance().displayImage(
						IMAGE_URL_HTTP + colOneDecoration.getUrl2x(), holder.cellDecorationIcon1_ImageView, options);
//				holder.cellDecorationIcon1_ImageView.setImageResource(colOneDecoration.getThumbId());
//				holder.cellDecorationIcon1_ImageView.setImageBitmap(BitmapFactory.decodeResource(weakContext.get().getResources(),colOneDecoration.getThumbId()));
				//holder.cellDecorationIcon1_ImageView.setImageDrawable(super.weakContext.get().getResources().getDrawable(colOneProduct.getThumbId()));
//				LoadBitmapTask lastTask=(LoadBitmapTask)holder.cellDecorationIcon1_ImageView.getTag();
//				if(lastTask!=null){
//					lastTask.cancel(true);
//				}
//				LoadBitmapTask curTask=new LoadBitmapTask(super.weakContext.get(),holder.cellDecorationIcon1_ImageView);
//				holder.cellDecorationIcon1_ImageView.setTag(curTask);
//				curTask.execute(colOneDecoration.getThumbId());
			
			}else{
				holder.cellDecorationIcon1_Layout.setOnClickListener(null);
			
			}
			
			if(colTwoDecoration!=null){
				holder.cellDecorationIcon2_Layout.setTag(colTwoDecoration);
				ImageLoader.getInstance().displayImage(
						IMAGE_URL_HTTP + colTwoDecoration.getUrl2x(), holder.cellDecorationIcon2_ImageView, options);
//				holder.cellDecorationIcon2_ImageView.setImageResource(colTwoDecoration.getThumbId());
//				holder.cellDecorationIcon2_ImageView.setImageBitmap(BitmapFactory.decodeResource(weakContext.get().getResources(),colTwoDecoration.getThumbId()));
//				LoadBitmapTask lastTask=(LoadBitmapTask)holder.cellDecorationIcon2_ImageView.getTag();
//				if(lastTask!=null){
//					lastTask.cancel(true);
//				}
//				LoadBitmapTask curTask=new LoadBitmapTask(super.weakContext.get(),holder.cellDecorationIcon2_ImageView);
//				holder.cellDecorationIcon2_ImageView.setTag(curTask);
//				curTask.execute(colTwoDecoration.getThumbId());
				
				//holder.cellDecorationIcon2_ImageView.setImageDrawable(super.weakContext.get().getResources().getDrawable(colTwoProduct.getThumbId()));
				
			}else{
				holder.cellDecorationIcon2_Layout.setOnClickListener(null);
			
			}
		
			
//			if(colThreeDecoration!=null){
//				holder.cellDecorationIcon3_Layout.setTag(colThreeDecoration);
//				holder.cellDecorationIcon3_ImageView.setImageResource(colThreeDecoration.getThumbId());
//				//holder.cellDecorationIcon3_ImageView.setImageDrawable(super.weakContext.get().getResources().getDrawable(colThreeProduct.getThumbId()));
//				
//			}else{
//				holder.cellDecorationIcon3_Layout.setOnClickListener(null);
//			
//			}
		}
		
		return convertView;
	}
	
	public OnClickListener getDecorationOnClickListener(){
		
		return new OnClickListener(){

			@Override
			public void onClick(View v) {
				Decoration decoration=(Decoration)v.getTag();
				if(onDecorationItemClickListener!=null)
					onDecorationItemClickListener.onDecorationItemClick(decoration);
				
			}
		};
		
	}
	
	
	public static class ChildHolder extends SuperHolder{
		ViewGroup cellDecorationIcon1_Layout;
		ImageView  cellDecorationIcon1_ImageView;
	
		
		ViewGroup cellDecorationIcon2_Layout;
		ImageView  cellDecorationIcon2_ImageView;
	
		ViewGroup cellDecorationIcon3_Layout;
		ImageView  cellDecorationIcon3_ImageView;
		
		
	}
	
//	public static class ThreeColDecoration  {
//		Decoration col1;
//		Decoration col2;
//		Decoration col3;
//		
//	
//	}
	
//	private static class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
//		
//		WeakReference<Context> weakContext;
//		WeakReference<ImageView> weakImageView;
//	
//		
//		public LoadBitmapTask(Context context, ImageView imageView){
//			weakContext= new WeakReference<Context>(context);
//			weakImageView = new WeakReference<ImageView>(imageView);
//		}
//		
//	    @Override
//	    protected Bitmap doInBackground(Integer... resId) {
//	    	Bitmap result = null;
//	    	try{
//	    		final BitmapFactory.Options options = new BitmapFactory.Options();
//	    		options.inSampleSize =2;
//	    		result=BitmapFactory.decodeResource(weakContext.get().getResources(),resId[0],options);
//	
//	    	}catch(Exception e){
//	    		e.printStackTrace();
//	    		result=null;
//	    	}
//	    	return result;
//	    }
//
//	    @Override
//	    protected void onPostExecute(Bitmap bm) {
//	     
//	    	if(!isCancelled() && weakImageView.get()!=null){
//	    		weakImageView.get().setImageBitmap(bm);
//	    	}else{
//	    		if(bm!=null){
//	    			bm.recycle();
//	    		}
//	    	}
//	    }
//	  }

}
