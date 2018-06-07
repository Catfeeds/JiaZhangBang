package com.runcom.jiazhangbang.ui;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView
{
	public MarqueeTextView(Context context)
	{
		this(context , null);
	}

	public MarqueeTextView(Context context , AttributeSet attrs)
	{
		super(context , attrs);
		// ���õ���
		setSingleLine();
		// ����Ellipsize
		setEllipsize(TextUtils.TruncateAt.MARQUEE);
		// ��ȡ����
		setFocusable(true);
		// ����Ƶ��ظ�������-1���������ظ�
		setMarqueeRepeatLimit( -1);
		// ǿ�ƻ�ý���
		setFocusableInTouchMode(true);
	}

	/*
	 * ����������View�õ�����,��������������Ϊtrue,���View����Զ���н����
	 */
	@Override
	public boolean isFocused()
	{
		return true;
	}

	/*
	 * ����EditText��ע���������
	 */
	@Override
	protected void onFocusChanged(boolean focused , int direction , Rect previouslyFocusedRect )
	{
		if(focused)
		{
			super.onFocusChanged(focused ,direction ,previouslyFocusedRect);
		}
	}

	/*
	 * Window��Window�佹�㷢���ı�ʱ�Ļص�
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus )
	{
		if(hasWindowFocus)
			super.onWindowFocusChanged(hasWindowFocus);
	}
}
