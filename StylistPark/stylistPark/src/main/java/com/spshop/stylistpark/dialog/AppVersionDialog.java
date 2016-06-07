package com.spshop.stylistpark.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.StringUtil;


public class AppVersionDialog {
	
	private static final String APK_PATH = AppConfig.SAVE_APK_PATH_LONG + "/stylistpark.apk"; 
	private static final int DIALOG_WIDTH = AppApplication.screenWidth * 2/3;
	private Context mContext;
	
	public AppVersionDialog(Context context) {
		this.mContext = context;
	}

	/**
	 * 提示已是最新版本
	 */
	public void isNewVersion(){
		final Dialog customDialog =  new Dialog(mContext, R.style.MyDialog);
		customDialog.setContentView(R.layout.dialog_btn_one);
		LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = DIALOG_WIDTH;
        customDialog.getWindow().setAttributes(lp);
		final TextView title = (TextView)customDialog.findViewById(R.id.dialog_title);
		title.setText(R.string.setting_version);
		final TextView content = (TextView)customDialog.findViewById(R.id.dialog_content);
		content.setText(R.string.dialog_version_new);
		final Button ok = (Button)customDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});
		customDialog.show();
	}
	
	/**
	 * 提示有新版本可以更新
	 * 
	 * @param address Apk下载地址
	 * @param isHomeIndex 是否首页
	 * @param description 更新描述
	 */
	public void foundNewVersion(final String address, final boolean isHomeIndex, String description){
		final Dialog customDialog =  new Dialog(mContext, R.style.MyDialog);
		customDialog.setContentView(R.layout.dialog_btn_two);
		LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = DIALOG_WIDTH;
        customDialog.getWindow().setAttributes(lp);
		final TextView title = (TextView)customDialog.findViewById(R.id.dialog_title);
		title.setText(R.string.setting_version);
		final TextView content = (TextView)customDialog.findViewById(R.id.dialog_contents);
		if (StringUtil.isNull(description)) {
			content.setText(R.string.dialog_version_update);
		}else{
			content.setText(Html.fromHtml(description));
			content.setGravity(0);
		}
		final Button ok = (Button)customDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 startLoadApk(address);
			}
		});
		final Button cancel = (Button)customDialog.findViewById(R.id.dialog_button_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});
		customDialog.show();
	}
	
	/**
	 * 提示有新版本需要强制更新
	 * 
	 * @param address Apk下载地址
	 * @param isHomeIndex 是否首页
	 * @param description 更新描述
	 */
	public void forceUpdateVersion(final String address, final boolean isHomeIndex, String description) {
		final Dialog customDialog = new Dialog(mContext, R.style.MyDialog);
		customDialog.setContentView(R.layout.dialog_btn_one);
		customDialog.setCanceledOnTouchOutside(false); //点区域外不销毁
		customDialog.setOnKeyListener(keylistener); //添加返回键监听器
		LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = DIALOG_WIDTH;
        customDialog.getWindow().setAttributes(lp);
		final TextView title = (TextView)customDialog.findViewById(R.id.dialog_title);
		title.setText(R.string.setting_version);
		final TextView content = (TextView) customDialog.findViewById(R.id.dialog_content);
		if (description.equals("")) {
			content.setText(R.string.dialog_version_update_force);
		} else {
			content.setText(Html.fromHtml(description));
			content.setGravity(0);
		}
		final Button ok = (Button) customDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startLoadApk(address);
			}
		});
		customDialog.show();
	}

	/**
	 * 开始下载apk程序
	 */
	private void startLoadApk(final String address) {
		new UpdateAppHttpTask().execute(address);
	}
	
	/**
	 * 开始安装apk程序
	 */
	public static void startInstallApk(Context context) {  
		 Intent intent = new Intent(Intent.ACTION_VIEW);  
		 intent.setDataAndType(Uri.fromFile(new File(APK_PATH)),"application/vnd.android.package-archive");  
		 context.startActivity(intent);
	} 

	/**
	 * 物理键盘监听器
	 */
	OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		
		private boolean exit = false;

		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
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
					CommonTools.showToast(mContext, mContext.getString(R.string.toast_exit_prompt), 1000);
				}
				return true;
			} else {
				return false;
			}
		}
	};
	
	/**
	 * 下载apk安装包的异步任务
	 */
	class UpdateAppHttpTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... url) {
			String result = "ok";
			List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
			try {
				HttpEntity entity = HttpUtil.getEntity(url[0], params, HttpUtil.METHOD_POST);
				result = FileManager.writeFileSaveHttpEntity(mContext, APK_PATH, entity);
			} catch (Exception e) {
				ExceptionUtil.handle(mContext, e);
				result = null;
			}
			return result;
		}

		protected void onPostExecute(String result) {
			if (result != null) {
				startInstallApk(mContext);
			} else {
				CommonTools.showToast(mContext, mContext.getString(R.string.toast_server_busy), 1000);
			}
		}

	}

}
