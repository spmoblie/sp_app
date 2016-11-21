package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.ArrayList;

import static com.spshop.stylistpark.activity.BaseActivity.DIALOG_CONFIRM_CLICK;


@SuppressLint("HandlerLeak")
public class ScreenVideoActivity extends Activity {

	private static final String TAG = "VideoActivity";
	
	private VideoView videoView;
	private LinearLayout loading_main;
	private RelativeLayout rl_close;
	private int old_duration;
	private int mSeekPosition;
	private int urlPosition = 0;
	private ArrayList<String> urlLists = new ArrayList<String>();
	private Runnable runnable;
	private Handler handler;
	private DialogManager dm;
	private PowerManager powerManager = null;
	private PowerManager.WakeLock wakeLock = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		dm = DialogManager.getInstance(this);
		powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
		wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

		findViewById();
		initView();
	}

	private void findViewById() {
		videoView = (VideoView) findViewById(R.id.popup_videoView);
		loading_main = (LinearLayout) findViewById(R.id.uvv_loading_ll_main);
		rl_close = (RelativeLayout) findViewById(R.id.popup_rl_close);
	}

	private void initView() {
		stopAnimation();

		//urlLists.add("S5C9F6000");
		//urlLists.add("S5C9A4957");
		//urlLists.add("S5C1C5170");

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
			showMyErrorDialog(getString(R.string.network_no_wifi));
			return;
		}
		initPalyView();
	}

	private void initPalyView() {
		startAnimation();
		MediaController  mc = new MediaController(this);
		mc.show(10000);
		videoView.setMediaController(mc);
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
				startPlay();
			}
		});
		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// 播放出错
				stopAnimation();
				//showMyErrorDialog(getString(R.string.dialog_error_video_url));
				startPlay();
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
		startPlay();
	}

	private void startPlay() {
		if (urlLists.size() == 0) {
			showMyErrorDialog(getString(R.string.dialog_error_video_url));
			return;
		}
		if (urlPosition < 0 || urlPosition >= urlLists.size()) {
			urlPosition = 0;
		}
		String videoUrl = urlLists.get(urlPosition);
		urlPosition++;
		if (!StringUtil.isNull(videoUrl)) {
			videoView.setVideoURI(Uri.parse("http://spshop.com/video/" + videoUrl + ".mp4"));
			videoView.start();
		}else {
			startPlay();
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
		dm.showOneBtnDialog(content, AppApplication.screenWidth * 2/3, true, false, mHandler, null);
	}

	private void startAnimation() {
		loading_main.setVisibility(View.VISIBLE);
	}

	private void stopAnimation() {
		loading_main.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		if (videoView != null && !videoView.isPlaying() && mSeekPosition > 0) {
			videoView.seekTo(mSeekPosition);
			videoView.start();
		}
		wakeLock.acquire();
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);

		if (videoView != null && videoView.isPlaying()) {
			mSeekPosition = videoView.getCurrentPosition();
			videoView.pause();
		}
		wakeLock.release();
		if (dm != null) {
			dm.clearInstance();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i(TAG, "onDestroy");
		if (videoView != null) {
			videoView.stopPlayback();
		}
		super.onDestroy();
	}

}
