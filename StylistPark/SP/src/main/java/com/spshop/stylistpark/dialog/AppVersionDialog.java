package com.spshop.stylistpark.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.StringUtil;

import org.apache.http.HttpEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AppVersionDialog {
	
	private static final String APK_PATH = AppConfig.SAVE_PATH_APK_DICE + "/stylistpark.apk";
	private static final int DIALOG_WIDTH = AppApplication.screenWidth * 2/3;
	private Context mContext;
	private DialogManager dm;
	private String apkLoadAddress;
	private boolean isForce = false;

	public AppVersionDialog(Context context, DialogManager dialogManager) {
		this.mContext = context;
		this.dm = dialogManager;
	}

	/**
	 * 更新状态提示
	 */
	public void showStatus(String msgStr){
		dm.showOneBtnDialog(msgStr, DIALOG_WIDTH, true, true, null, null);
	}
	
	/**
	 * 提示有新版本可以更新
	 * 
	 * @param address Apk下载地址
	 * @param description 更新描述
	 */
	public void foundNewVersion(String address, String description){
		isForce = false;
		apkLoadAddress = address;
		if (StringUtil.isNull(description)) {
			description = mContext.getString(R.string.dialog_version_update);
		} else {
			description = Html.fromHtml(description).toString();
		}
		dm.showTwoBtnDialog(null, description, mContext.getString(R.string.ignore),
				mContext.getString(R.string.confirm), DIALOG_WIDTH, true, true, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case BaseActivity.DIALOG_CONFIRM_CLICK:
								startLoadApk(apkLoadAddress);
								break;
						}
					}
				});
	}
	
	/**
	 * 提示有新版本需要强制更新
	 * 
	 * @param address Apk下载地址
	 * @param description 更新描述
	 */
	public void forceUpdateVersion(String address, String description) {
		isForce = true;
		apkLoadAddress = address;
		if (StringUtil.isNull(description)) {
			description = mContext.getString(R.string.dialog_version_update_force);
		} else {
			description = Html.fromHtml(description).toString();
		}
		dm.showOneBtnDialog(description,
				DIALOG_WIDTH, true, false, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case BaseActivity.DIALOG_CONFIRM_CLICK:
								startLoadApk(apkLoadAddress);
								break;
						}
					}
				}, keylistener);
	}

	/**
	 * 开始下载apk程序
	 */
	private void startLoadApk(final String address) {
		loadApkDialog();
		new UpdateAppHttpTask().execute(address);
	}

	/**
	 * 弹出下载缓冲对话框
	 */
	public void loadApkDialog() {
		dm.showLoadDialog(DIALOG_WIDTH, keylistener);
	}
	
	/**
	 * 开始安装apk程序
	 */
	private void startInstallApk() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(APK_PATH)), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
		dm.dismiss();
	} 

	/**
	 * 物理键盘监听器
	 */
	OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		
		private boolean exit = false;

		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
				if (exit) {
					AppManager.getInstance().AppExit(mContext);
				} else {
					exit = Boolean.TRUE;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							exit = Boolean.FALSE;
						}
					}, 2000);
					CommonTools.showToast(mContext.getString(R.string.toast_exit_prompt), 1000);
				}
			}
			return true;
		}
	};
	
	/**
	 * 下载apk安装包的异步任务
	 */
	class UpdateAppHttpTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... url) {
			List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
			try {
				HttpEntity entity = HttpUtil.getEntity(url[0], params, HttpUtil.METHOD_POST);
				return FileManager.writeFileSaveHttpEntity(APK_PATH, entity);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
			return null;
		}

		protected void onPostExecute(String result) {
			if (result != null) {
				startInstallApk();
			} else {
				if (isForce) {
					dm.showOneBtnDialog(mContext.getString(R.string.toast_server_busy),
							DIALOG_WIDTH, true, false, new Handler() {
								@Override
								public void handleMessage(Message msg) {
									switch (msg.what) {
										case BaseActivity.DIALOG_CONFIRM_CLICK:
											startLoadApk(apkLoadAddress);
											break;
									}
								}
							}, keylistener);
				} else {
					showStatus(mContext.getString(R.string.toast_server_busy));
				}
			}
		}

	}

}
