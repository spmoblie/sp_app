package com.spshop.stylistpark.activity.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.adapter.ClipPhotoGridAdapter;
import com.spshop.stylistpark.entity.ClipPhotoEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.LogUtil;
import com.tencent.stat.StatService;

/**
 * "选择相片"Activity
 */
public class ClipPhotoGridActivity extends BaseActivity {

	private static final String TAG = "ClipPhotoGridActivity";
	public static ClipPhotoGridActivity instance;
	private GridView gv_aibum;
	private List<ClipPhotoEntity> aibumList = new ArrayList<ClipPhotoEntity>();

	// 设置获取图片的字段信
	private static final String[] STORE_IMAGES = { 
		MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
		MediaStore.Images.Media.LATITUDE, // 维度
		MediaStore.Images.Media.LONGITUDE, // 经度
		MediaStore.Images.Media._ID, // id
		MediaStore.Images.Media.BUCKET_ID, // dir id 目录
		MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // dir name 目录名字
		MediaStore.Images.Media.DATA // 图片路径
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_photo_list);
		
		LogUtil.i(TAG, "onCreate");
		instance =this;
		AppManager.getInstance().addActivity(this);//添加Activity到堆栈
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		gv_aibum = (GridView) findViewById(R.id.clip_photo_list_gridview);
	}

	private void initView() {
		setTitle(R.string.photo_select_title);
		setBtnRight(getString(R.string.next));
		
		aibumList = getPhotoAlbum();
		if (aibumList.size() < 1) {
			CommonTools.showToast(this, getString(R.string.photo_select_no_data), 1000);
		}
		gv_aibum.setAdapter(new ClipPhotoGridAdapter(aibumList, this));
		gv_aibum.setOnItemClickListener(aibumClickListener);
		gv_aibum.setSelector(R.color.ui_bg_color_app);
	}
	
	@Override
	public void OnListenerRight() {
		if (aibumList.size() > 0) {
			startPhotoOneActivity(0);
		}
		super.OnListenerRight();
	}

	/**
	 * 跳转至指定相册
	 */
	private void startPhotoOneActivity(int position) {
		Intent intent = new Intent(this, ClipPhotoOneActivity.class);
		intent.putExtra("aibum", aibumList.get(position));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 页面开始
        StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
        StatService.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}
	
	/**
	 * 相册点击事件
	 */
	OnItemClickListener aibumClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startPhotoOneActivity(position);
		}
	};
	
	/**
	 * 方法描述：按相册获取图片信息
	 */
	private List<ClipPhotoEntity> getPhotoAlbum() {
		List<ClipPhotoEntity> aibumList = new ArrayList<ClipPhotoEntity>();
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		Map<String, ClipPhotoEntity> countMap = new HashMap<String, ClipPhotoEntity>();
		ClipPhotoEntity pa = null;
		while (cursor.moveToNext()) {
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			String url = cursor.getString(6);
			if (!countMap.containsKey(dir_id)) {
				pa = new ClipPhotoEntity();
				pa.setName(dir);
				pa.setBitmap(Integer.parseInt(id));
				pa.setCount("1");
				pa.getBitList().add(new ClipPhotoEntity(Integer.valueOf(id), url));
				countMap.put(dir_id, pa);
			} else {
				pa = countMap.get(dir_id);
				pa.setBitmap(Integer.parseInt(id));
				pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
				pa.getBitList().add(new ClipPhotoEntity(Integer.valueOf(id), url));
			}
		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it) {
			aibumList.add(countMap.get(key));
		}
		return aibumList;
	}
	
}
