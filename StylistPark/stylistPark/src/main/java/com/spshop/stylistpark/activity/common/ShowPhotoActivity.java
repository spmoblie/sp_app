package com.spshop.stylistpark.activity.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.tencent.stat.StatService;

import java.util.ArrayList;

/**
 * 相片查看器
 */
public class ShowPhotoActivity extends FragmentActivity {

	private ArrayList<String> pathLists;
	private int position;
	private ImageView iv_img, iv_left;
	private TextView tv_title;
	private Button btn_right;
	private GestureDetector detector;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);

		pathLists = (ArrayList<String>) getIntent().getExtras().getStringArrayList(AppConfig.ACTIVITY_SHOW_PHOTO_LIST);
		position = getIntent().getExtras().getInt("position", 0);
		options = AppApplication.getDefaultImageOptions();

		setupView();

		detector = new GestureDetector(this, new MyGestureListener());
	}

	private void setupView() {
		iv_img = (ImageView) findViewById(R.id.show_photo_iv_img);
		iv_left = (ImageView) findViewById(R.id.top_bar_left);
		tv_title = (TextView) findViewById(R.id.top_bar_title);
		btn_right = (Button) findViewById(R.id.top_bar_right);
		btn_right.setText(R.string.delete);
		
		String path = pathLists.get(position);
		showImage(path);

		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowPhotoActivity.this.finish();
			}
		});
		btn_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ShowPhotoActivity.this);
				builder.setTitle("提示")
			           .setMessage("确定要删除吗？")
				       .setCancelable(false)
				       .setPositiveButton("取消", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.cancel();
				           }
				       })
				       .setNegativeButton("确定", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               pathLists.remove(position);
							   if (pathLists.size() > 0) {
								   position = 0;
								   String path = pathLists.get(position);
								   showImage(path);
							   }else{
								   ShowPhotoActivity.this.finish();
							   }
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	private void showImage(String path) {
		tv_title.setText(getString(R.string.viewpager_indicator, position+1, pathLists.size()));
		ImageLoader.getInstance().displayImage("file://" + path, iv_img, options);
	}

	public class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 25) {// 下一张
				if (++position == pathLists.size()) {
					position = 0;
				}
			} else if (e2.getX() - e1.getX() > 25) {// 上一张
				if (--position < 0) {
					position = pathLists.size() - 1;
				}
			}
			String path = pathLists.get(position);
			showImage(path);
			return false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "上一张");
		menu.add(1, 2, 2, "下一张");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			if (--position < 0) {
				position = pathLists.size() - 1;
			}
			break;
		case 2:
			if (++position == pathLists.size()) {
				position = 0;
			}
			break;
		}
		String path = pathLists.get(position);
		showImage(path);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(AppConfig.ACTIVITY_SHOW_PHOTO_LIST, (ArrayList<String>) pathLists);
		setResult(RESULT_OK, returnIntent);
		super.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 页面开始
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 页面结束
		StatService.onPause(this);
	}

}
