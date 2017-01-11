package com.spshop.stylistpark.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

import static com.spshop.stylistpark.AppApplication.mScale;


public class DialogManager {

	private static DialogManager instance;
	private Context mContext;
	private Dialog mDialog;

	private DialogManager(Context context) {
		this.mContext = context;
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new DialogManager(context);
		}
	}

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static DialogManager getInstance(Context context) {
		if (instance == null) {
			syncInit(context);
		}
		return instance;
	}

	public void clearInstance(){
		instance = null;
	}

	public void dismiss(){
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	/**
	 * 弹出下载缓冲对话框
	 * 
	 * @param width 对话框宽度
	 * @param keylistener 物理键盘监听器
	 */
	public void showLoadDialog(int width, OnKeyListener keylistener){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog = new Dialog(mContext, R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(false);
		if (keylistener != null) {
			mDialog.setOnKeyListener(keylistener);
		}
		mDialog.setContentView(R.layout.dialog_download);
		// 设置对话框的坐标及宽高
        LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = width;
        mDialog.getWindow().setAttributes(lp);
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出一个按钮的通用对话框
	 *
	 * @param contentStr 提示内容
	 * @param width 对话框宽度
	 * @param isCenter 提示内容是否居中
	 * @param isVanish 点击框以外是否消失
	 * @param keylistener 物理键盘监听器
	 */
	public void showOneBtnDialog(String contentStr, int width, boolean isCenter,
				 boolean isVanish, final Handler handler, OnKeyListener keylistener){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(mContext, R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(isVanish);
		if (keylistener != null) {
			mDialog.setOnKeyListener(keylistener);
		}
		mDialog.setContentView(R.layout.dialog_btn_one);
		// 设置对话框的坐标及宽高
        LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = width;
        mDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView content = (TextView)mDialog.findViewById(R.id.dialog_content);
		content.setText(contentStr);
		if (!isCenter) { //不居中
			content.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		}
		final Button ok = (Button)mDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //确定
					handler.sendEmptyMessage(BaseActivity.DIALOG_CONFIRM_CLICK);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出两个按钮的通用对话框
	 * 
	 * @param titleStr 对话框标题
	 * @param contentStr 对话框内容
	 * @param leftStr 对话框左边按钮文本
	 * @param rightStr 对话框右边按钮文本
	 * @param width 对话框宽度
	 * @param isCenter 提示内容是否居中
	 * @param isVanish 点击框以外是否消失
	 */
	public void showTwoBtnDialog(String titleStr, String contentStr, String leftStr, String rightStr,
								 int width, boolean isCenter, boolean isVanish, final Handler handler){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(mContext, R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(isVanish);
		mDialog.setContentView(R.layout.dialog_btn_two);
		// 设置对话框的坐标及宽高
        LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = width;
        mDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView title = (TextView) mDialog.findViewById(R.id.dialog_title);
		if (!StringUtil.isNull(titleStr)) {
			title.setText(titleStr);
			title.setVisibility(View.VISIBLE);
		}
		final TextView content = (TextView)mDialog.findViewById(R.id.dialog_contents);
		content.setText(contentStr);
		if (!isCenter) { //不居中
			content.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		}
		final Button left = (Button)mDialog.findViewById(R.id.dialog_button_cancel);
		if (!StringUtil.isNull(leftStr)) {
			left.setText(leftStr);
		}
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //取消
					handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL_CLICK);
				}
				mDialog.dismiss(); 
			}
		});
		final Button right = (Button)mDialog.findViewById(R.id.dialog_button_confirm);
		if (!StringUtil.isNull(rightStr)) {
			right.setText(rightStr);
		}
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //确定
					handler.sendEmptyMessage(BaseActivity.DIALOG_CONFIRM_CLICK);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出带输入框的对话框
	 */
	public void showEditDialog(String titleStr, int width, int inputType, boolean isVanish, final Handler handler){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(mContext, R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(isVanish);
		mDialog.setContentView(R.layout.dialog_edit);
		// 设置对话框的坐标及宽高
		LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = width;
		mDialog.getWindow().setAttributes(lp);
		// 初始化对话框中的子控件
		final TextView title = (TextView) mDialog.findViewById(R.id.dialog_title);
		if (!StringUtil.isNull(titleStr)) {
			title.setText(titleStr);
			title.setVisibility(View.VISIBLE);
		}
		final EditText et_password = (EditText) mDialog.findViewById(R.id.dialog_et_password);
		et_password.setInputType(inputType);
		final Button left = (Button)mDialog.findViewById(R.id.dialog_button_cancel);
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //取消
					handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL_CLICK);
				}
				mDialog.dismiss();
			}
		});
		final Button right = (Button)mDialog.findViewById(R.id.dialog_button_confirm);
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String passStr = et_password.getText().toString();
				if (passStr.isEmpty()) return;
				if (handler != null) { //确定
					Message msg = Message.obtain();
					msg.obj = passStr;
					handler.sendMessage(msg);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}
	
	/**
	 * 弹出列表形式的对话框
	 */
	public void showListItemDialog(String title, CharSequence[] items, int width, final boolean isCenter, final Handler handler){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(mContext, R.style.MyDialog);
		mDialog.setContentView(R.layout.dialog_listview);
		// 设置对话框的坐标及宽高
        LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = width;
        mDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView tv_title = (TextView)mDialog.findViewById(R.id.dialog_list_title);
		tv_title.setText(title);
		if (!isCenter) { //不居中
			tv_title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		}
		ListView lv = (ListView) mDialog.findViewById(R.id.dialog_list_lv);
		lv.setSelector(R.color.ui_bg_color_white);
		List<CharSequence> itemList = Arrays.asList(items);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, itemList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView tv_item = (TextView) view.findViewById(android.R.id.text1);
				tv_item.setPadding(30, 0, 30, 0);
				tv_item.setTextSize(mScale * 18);
				tv_item.setTextColor(mContext.getResources().getColor(R.color.conte_text_color));
				if (!isCenter) { //不居中
					tv_item.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				} else {
					tv_item.setGravity(Gravity.CENTER);
				}
				return view;
			}

		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (handler != null) {
					handler.sendEmptyMessage(position);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

}
