package com.spshop.stylistpark.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.VideoView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.dialog.DialogManager;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.service.ServiceContext;
import com.spshop.stylistpark.task.AsyncTaskManager;
import com.spshop.stylistpark.task.OnDataListener;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.NetworkUtil;
import com.spshop.stylistpark.utils.QRCodeUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

import static com.spshop.stylistpark.activity.BaseActivity.DIALOG_CONFIRM_CLICK;


@SuppressLint("HandlerLeak")
public class ScreenVideoActivity extends Activity implements OnDataListener {

	private static final String TAG = "VideoActivity";

	private VideoView videoView;
	private TextView tv_price, tv_qr_buy;
	private LinearLayout loading_main;
	private RelativeLayout rl_next_1, rl_next_2, rl_close;
	private Drawable qr_default, qr_buy;
	private int old_duration;
	private int mSeekPosition;
	private int urlPosition = 0;
	private boolean isFirst = true;
	private boolean isRight = true;
	private ArrayList<ProductDetailEntity> urlLists = new ArrayList<ProductDetailEntity>();
	private Runnable runnable;
	private Handler handler;
	private DialogManager dm;
	private PowerManager powerManager = null;
	private PowerManager.WakeLock wakeLock = null;
	private AsyncTaskManager atm;
	private ServiceContext sc = ServiceContext.getServiceContext();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_video);

		dm = DialogManager.getInstance(this);
		powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
		wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
		atm = AsyncTaskManager.getInstance(this);

		findViewById();
		initView();
	}

	private void findViewById() {
		videoView = (VideoView) findViewById(R.id.screen_video_video_view);
		tv_price = (TextView) findViewById(R.id.screen_video_tv_price);
		tv_qr_buy = (TextView) findViewById(R.id.screen_video_qr_buy);
		rl_close = (RelativeLayout) findViewById(R.id.screen_video_rl_close);
		rl_next_1 = (RelativeLayout) findViewById(R.id.screen_video_rl_next_1);
		rl_next_2 = (RelativeLayout) findViewById(R.id.screen_video_rl_next_2);
		loading_main = (LinearLayout) findViewById(R.id.uvv_loading_ll_main);

		qr_default = getResources().getDrawable(R.drawable.icon_qr_public);
		qr_default.setBounds(0, 0, qr_default.getMinimumWidth(), qr_default.getMinimumHeight());
	}

	private void initView() {
		stopAnimation();

		rl_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl_next_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playBefore();
			}
		});
		rl_next_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playNext();
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

		initPlayData();
	}

	private void initPlayData() {
		if (AppApplication.videoEn != null && AppApplication.videoEn.getPromotionLists() != null) {
			urlLists.addAll(AppApplication.videoEn.getPromotionLists());
			rl_next_1.setVisibility(View.VISIBLE);
			rl_next_2.setVisibility(View.VISIBLE);
		} else if (isFirst) {
			startAnimation();
			atm.request(AppConfig.REQUEST_SV_GET_SCREEN_VIDEO_CODE, this);
			isFirst = false;
			return;
		}
		initPlayView();
	}

	private void initPlayView() {
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
				playNext();
			}
		});
		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// 播放出错
				stopAnimation();
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

	/**
	 * 播放上一个
	 */
	protected void playBefore() {
		if (isRight) {
			urlPosition -= 2;
			isRight = false;
		}
		startPlay();
	}

	/**
	 * 播放下一个
	 */
	protected void playNext() {
		if (!isRight) {
            urlPosition += 2;
            isRight = true;
        }
		startPlay();
	}

	private void startPlay() {
		if (urlLists.size() == 0) {
			showMyErrorDialog(getString(R.string.dialog_error_video_url));
			return;
		}
		startAnimation();
		if (urlPosition >= urlLists.size()) {
			urlPosition = 0;
		} else if (urlPosition < 0) {
			urlPosition = urlLists.size() - 1;
		}
		String priceStr = "";
		String videoUrl = "";
		String videoImg = "";
		ProductDetailEntity playEn = urlLists.get(urlPosition);
		if (playEn != null) {
			priceStr = playEn.getSellPrice();
			videoUrl = playEn.getVideoUrl();
			videoImg = playEn.getPromotionName() + UserManager.getInstance().getUserId();
		}
		if (isRight) {
			urlPosition++;
		} else {
			urlPosition--;
		}
		tv_price.setVisibility(View.GONE);
		tv_qr_buy.setCompoundDrawables(null, qr_default, null, null);
		if (!StringUtil.isNull(videoUrl)) {
			videoView.setVideoURI(Uri.parse(videoUrl));
			videoView.start();
			if (!StringUtil.isNull(priceStr)) {
				tv_price.setText(priceStr);
				tv_price.setVisibility(View.VISIBLE);
			}
			if (!StringUtil.isNull(videoImg)) {
				Bitmap bm = QRCodeUtil.createQRImage(videoImg, 200, 200);
				if (bm != null) {
					qr_buy = new BitmapDrawable(bm);
					qr_buy.setBounds(0, 0, qr_buy.getMinimumWidth(), qr_buy.getMinimumHeight());
					tv_qr_buy.setCompoundDrawables(null, qr_buy, null, null);
				}
			}
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
		if (dm != null) {
			dm.showOneBtnDialog(content, AppApplication.screenWidth * 2/3, true, false, mHandler, null);
		}
	}

	private void startAnimation() {
		if (loading_main != null) {
			loading_main.setVisibility(View.VISIBLE);
		}
	}

	private void stopAnimation() {
		if (loading_main != null) {
			loading_main.setVisibility(View.GONE);
		}
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

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SCREEN_VIDEO_CODE:
				params.add(new MyNameValuePair("app", "video"));
				return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_SCREEN_VIDEO_CODE, uri, params, HttpUtil.METHOD_GET);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SCREEN_VIDEO_CODE:
				if (result != null) {
					AppApplication.videoEn = (ProductDetailEntity) result;
				}
				initPlayData();
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		stopAnimation();
		showMyErrorDialog(getString(R.string.network_fault));
	}
}
