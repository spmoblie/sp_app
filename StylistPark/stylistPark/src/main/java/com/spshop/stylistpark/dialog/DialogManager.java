package com.spshop.stylistpark.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;


public class DialogManager {
	
	private Context mContext;
	
	public DialogManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 弹出一个按钮的通用对话框
	 * 
	 * @param titleStr 标题
	 * @param contentStr 提示内容
	 * @param width 对话框宽度
	 * @param center 提示内容是否居中
	 */
	public void showOneBtnDialog(String titleStr, String contentStr, int width, boolean isCenter){
		// 创建对话框
		final Dialog customDialog =  new Dialog(mContext, R.style.MyDialog);
		customDialog.setCanceledOnTouchOutside(false); 
		customDialog.setContentView(R.layout.dialog_btn_one);
		// 设置对话框的坐标及宽高
        LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = width;
        customDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView title = (TextView)customDialog.findViewById(R.id.dialog_title);
		title.setText(titleStr);
		final TextView content = (TextView)customDialog.findViewById(R.id.dialog_content);
		content.setText(contentStr);
		if (!isCenter) {
			content.setGravity(0);
		}
		final Button ok = (Button)customDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});
		// 显示对话框
		customDialog.show();
	}
	
	/**
	 * 弹出一个按钮的可回调事件的通用对话框
	 * 
	 * @param dialogCallback 按钮事件回调接口(type = 1 : 默认为右边按钮事件)
	 * @param titleStr 标题
	 * @param contentStr 提示内容
	 * @param width 对话框宽度
	 * @param center 提示内容是否居中
	 */
	public void showOneBtnDialog(final DialogManagerCallback dialogCallback, 
			String titleStr, String contentStr, int width, boolean isCenter){
		// 创建对话框
		final Dialog customDialog =  new Dialog(mContext, R.style.MyDialog);
		customDialog.setCanceledOnTouchOutside(false); 
		customDialog.setContentView(R.layout.dialog_btn_one);
		// 设置对话框的坐标及宽高
        LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = width;
        customDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView title = (TextView)customDialog.findViewById(R.id.dialog_title);
		title.setText(titleStr);
		final TextView content = (TextView)customDialog.findViewById(R.id.dialog_content);
		content.setText(contentStr);
		if (!isCenter) { //不居中
			content.setGravity(0);
		}
		final Button ok = (Button)customDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogCallback != null) {
					dialogCallback.setOnClick(1);
				}
				customDialog.dismiss();
			}
		});
		// 显示对话框
		customDialog.show();
	}
	
	/**
	 * 弹出两个按钮的可回调参数的通用对话框
	 * 
	 * @param dialogCallback 按钮事件回调接口(type:0 左边按钮事件/1 右边按钮事件)
	 * @param titleStr 对话框标题
	 * @param contentStr 对话框内容
	 * @param leftStr 对话框左边按钮文本
	 * @param rightStr 对话框右边按钮文本
	 * @param width 对话框宽度
	 * @param center 提示内容是否居中
	 */
	public void showTwoBtnDialog(final DialogManagerCallback dialogCallback,
			String titleStr, String contentStr, String leftStr, String rightStr, int width, boolean isCenter){
		// 创建对话框
		final Dialog customDialog =  new Dialog(mContext, R.style.MyDialog);
		customDialog.setContentView(R.layout.dialog_btn_two);
		// 设置对话框的坐标及宽高
        LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = width;
        customDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView title = (TextView)customDialog.findViewById(R.id.dialog_title);
		title.setText(titleStr);
		final TextView content = (TextView)customDialog.findViewById(R.id.dialog_contents);
		content.setText(contentStr);
		if (!isCenter) { //不居中
			content.setGravity(0);
		}
		final Button left = (Button)customDialog.findViewById(R.id.dialog_button_ok);
		left.setText(leftStr);
		left.setTextColor(mContext.getResources().getColor(R.color.text_color_assist));
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogCallback != null) {
					dialogCallback.setOnClick(0);
				}
				customDialog.dismiss(); 
			}
		});
		final Button right = (Button)customDialog.findViewById(R.id.dialog_button_cancel);
		right.setText(rightStr);
		right.setTextColor(mContext.getResources().getColor(R.color.text_color_black));
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogCallback != null) {
					dialogCallback.setOnClick(1);
				}
				customDialog.dismiss();
			}
		});
		// 显示对话框
		customDialog.show();
	}
	
	/**
	 * 弹出列表形式的对话框
	 * 
	 * @param dialogCallback
	 * @param titleStr
	 * @param str1 列表Item1
	 * @param str2 列表Item2
	 */
	public void showListDialog(final DialogManagerCallback dialogCallback, String titleStr, String str1, String str2){
		// 创建对话框
		final Dialog customDialog =  new Dialog(mContext, R.style.MyDialog);
		customDialog.setContentView(R.layout.dialog_list_2);
		// 设置对话框的坐标及宽高
        LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = AppApplication.screenWidth * 2/3;
        customDialog.getWindow().setAttributes(lp);
        // 初始化对话框中的子控件
		final TextView tv_title = (TextView)customDialog.findViewById(R.id.dialog_list_title);
		tv_title.setText(titleStr);
		final TextView tv_item_1 = (TextView)customDialog.findViewById(R.id.dialog_list_item_1);
		tv_item_1.setText(str1);
		tv_item_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogCallback != null) {
					dialogCallback.setOnClick(1);
				}
				customDialog.dismiss(); 
			}
		});
		final TextView tv_item_2 = (TextView)customDialog.findViewById(R.id.dialog_list_item_2);
		tv_item_2.setText(str2);
		tv_item_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogCallback != null) {
					dialogCallback.setOnClick(2);
				}
				customDialog.dismiss();
			}
		});
		// 显示对话框
		customDialog.show();
	}
	
	/**
	 * 对话框事件回调接口
	 */
	public interface DialogManagerCallback{
		void setOnClick(int type); //type(0:左边按钮/1:右边按钮)
	}

}
