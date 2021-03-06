package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.StringUtil;


@SuppressLint("HandlerLeak")
public class VideoActivity extends BaseActivity {

	private static final String TAG = "VideoActivity";
	
	private VideoView videoView;
	private LinearLayout loading_main;
	private RelativeLayout rl_close;
	private String videoUrl;
	private int old_duration;
	private int mSeekPosition;
	private Runnable runnable;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		
		videoUrl = getIntent().getStringExtra("videoUrl");
		
		findViewById();
		initView();
	}

	private void findViewById() {
		videoView = (VideoView) findViewById(R.id.video_video_view);
		loading_main = (LinearLayout) findViewById(R.id.uvv_loading_ll_main);
		rl_close = (RelativeLayout) findViewById(R.id.video_rl_close);
	}

	private void initView() {
		setHeadVisibility(View.GONE);
		stopAnimation();
		
		rl_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (!NetworkUtil.isNetworkAvailable()) {
			showMyErrorDialog(getString(R.string.network_fault));
			return;
		}
		if (!NetworkUtil.isWifi()) {
			showConfirmDialog(getString(R.string.network_no_wifi), getString(R.string.cancel),
					getString(R.string.proceed), true, true, new Handler(){
						@Override
						public void handleMessage(Message msg) {
							switch (msg.what) {
								case DIALOG_CANCEL_CLICK:
									finish();
									break;
								case DIALOG_CONFIRM_CLICK:
									startPaly();
									break;
							}
						}
					});
			return;
		}
		startPaly();
	}

	private void startPaly() {
		if (!StringUtil.isNull(videoUrl)) {
			startAnimation();
			Uri uri = Uri.parse("http://spshop.com/video/" + videoUrl + ".mp4");
			MediaController  mc = new MediaController(mContext);
			mc.show(10000);
			videoView.setMediaController(mc);
			videoView.setVideoURI(uri);
			videoView.start();
			videoView.requestFocus();
			videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// 播放开始
					stopAnimation();
				}
			});
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// 播放结束
					videoView.start();
				}
			});
			videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// 播放出错
					stopAnimation();
					showMyErrorDialog(getString(R.string.dialog_error_video_url));
					return true;
				}

			});
			// 播放视频卡顿处理
			handler = new Handler();
			runnable = new Runnable() {   
			    public void run() {  
			        int duration = videoView.getCurrentPosition();  
			        if (videoView.isPlaying()) {
			        	if (old_duration == duration) {
			        		startAnimation();
			        	} else {  
			        		stopAnimation();
			        	}  
			        	old_duration = duration;
					}
			        handler.postDelayed(runnable, 500);
			    }  
			};  
			handler.postDelayed(runnable, 0); 
		}else {
			showMyErrorDialog(getString(R.string.dialog_error_video_url));
		}
	}

	private void showMyErrorDialog(String content) {
		Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DIALOG_CONFIRM_CLICK:
					finish();
					break;
				default:
					break;
				}
			}
		};
		showErrorDialog(content, false, mHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		stopLoop();
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		if (videoView != null && !videoView.isPlaying() && mSeekPosition > 0) {
			videoView.seekTo(mSeekPosition);
			videoView.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);

		if (videoView != null && videoView.isPlaying()) {
			mSeekPosition = videoView.getCurrentPosition();
			videoView.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
		if (videoView != null) {
			videoView.stopPlayback();
		}
	}

	@Override
	public Object doInBackground(int requsetCode) throws Exception {
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {

	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}

	@Override
	protected void startAnimation() {
		loading_main.setVisibility(View.VISIBLE);
	}

	@Override
	protected void stopAnimation() {
		loading_main.setVisibility(View.GONE);
	}
}
