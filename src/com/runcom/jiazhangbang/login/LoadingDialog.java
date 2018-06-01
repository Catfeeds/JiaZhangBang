package com.runcom.jiazhangbang.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.runcom.jiazhangbang.R;

/**
 * ���ضԻ���
 */

public class LoadingDialog extends ProgressDialog
{

	private String mMessage;

	private TextView mTitleTv;

	public LoadingDialog(Context context , String message , boolean canceledOnTouchOutside)
	{
		super(context , R.style.Theme_Light_LoadingDialog);
		this.mMessage = message;
		// ���������Ļ��������,����ѡ�������progressDialog��ʧ�����ޱ仯
		setCanceledOnTouchOutside(canceledOnTouchOutside);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		mTitleTv = (TextView) findViewById(R.id.tv_loading_dialog);
		mTitleTv.setText(mMessage);
		setCancelable(false);// ����ȡ��
	}

	public void setTitle(String message )
	{
		this.mMessage = message;
		mTitleTv.setText(mMessage);
	}

	/**
	 * ��ʾ�ڵײ�
	 */
	public void showButtom()
	{
		// WindowManager windowManager = ((Activity)
		// mContext).getWindowManager();
		// Display display = windowManager.getDefaultDisplay();
		//
		// WindowManager.LayoutParams lp = getWindow().getAttributes();
		// lp.width = (int) (display.getWidth() * 0.8);
		// getWindow().setAttributes(lp);
		// super.show();
	}

}
