package com.runcom.jiazhangbang.listenText.lrcView;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

import com.runcom.jiazhangbang.R;

public class LrcView extends View
{

	private List < LrcBean > list;
	private Paint gPaint;
	private Paint hPaint;
	private int width = 0 , height = 0;
	private int currentPosition = 0;
	private MediaPlayer player;
	private int lastPosition = 0;
	private int highLineColor;
	private int lrcColor;
	// private int lineSpacingExtra = 80;
	// private final int TEXTSIZE = 36;
	private int lineSpacingExtra = 123;
	private final int TEXTSIZE = 57;
	private int mode = 1;
	public final static int KARAOKE = 1;

	public void setHighLineColor(int highLineColor )
	{
		this.highLineColor = highLineColor;
	}

	public void setLrcColor(int lrcColor )
	{
		this.lrcColor = lrcColor;
	}

	public void setMode(int mode )
	{
		this.mode = mode;
	}

	public void setPlayer(MediaPlayer player )
	{
		this.player = player;
	}

	/**
	 * ±ê×¼¸è´Ê×Ö·û´®
	 * 
	 * @param lrc
	 */
	public void setLrc(String lrc )
	{
		list = LrcUtil.parseStr2List(lrc);
	}

	public LrcView(Context context)
	{
		this(context , null);
	}

	public LrcView(Context context , AttributeSet attrs)
	{
		this(context , attrs , 0);
	}

	public LrcView(Context context , AttributeSet attrs , int defStyleAttr)
	{
		super(context , attrs , defStyleAttr);

		TypedArray ta = context.obtainStyledAttributes(attrs ,R.styleable.LrcView);
		highLineColor = ta.getColor(R.styleable.LrcView_hignLineColor ,getResources().getColor(R.color.white));
		// lrcColor = ta.getColor(R.styleable.LrcView_lrcColor
		// ,getResources().getColor(R.color.lyricText));
		lrcColor = ta.getColor(R.styleable.LrcView_lrcColor ,getResources().getColor(android.R.color.darker_gray));
		mode = ta.getInt(R.styleable.LrcView_lrcMode ,mode);
		ta.recycle();
		gPaint = new Paint();
		gPaint.setAntiAlias(true);
		gPaint.setColor(lrcColor);
		gPaint.setTextSize(TEXTSIZE);
		gPaint.setTextAlign(Paint.Align.CENTER);
		hPaint = new Paint();
		hPaint.setAntiAlias(true);
		hPaint.setColor(highLineColor);
		hPaint.setTextSize(TEXTSIZE);
		hPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas )
	{
		if(width == 0 || height == 0)
		{
			width = getMeasuredWidth();
			height = getMeasuredHeight();
		}
		if(list == null || list.size() == 0)
		{
			canvas.drawText("ÔÝÎÞ×ÖÄ»" ,width / 2 ,height / 2 ,gPaint);
			return;
		}

		getCurrentPosition();

		// drawLrc1(canvas);
		int currentMillis = 0;
		if(null == player)
		{
			return;
		}
		currentMillis = player.getCurrentPosition();
		drawLrc2(canvas ,currentMillis);
		long start = list.get(currentPosition).getStart();
		float v = (currentMillis - start) > 500 ? currentPosition * lineSpacingExtra : lastPosition * lineSpacingExtra + (currentPosition - lastPosition) * lineSpacingExtra * ((currentMillis - start) / 500f);
		setScrollY((int) v);
		if(getScrollY() == currentPosition * lineSpacingExtra)
		{
			lastPosition = currentPosition;
		}
		postInvalidateDelayed(100);
	}

	private void drawLrc2(Canvas canvas , int currentMillis )
	{
		if(mode == 0)
		{
			for(int i = 0 ; i < list.size() ; i ++ )
			{
				if(i == currentPosition)
				{
					canvas.drawText(list.get(i).getLrc() ,width / 2 ,height / 2 + lineSpacingExtra * i ,hPaint);
				}
				else
				{
					canvas.drawText(list.get(i).getLrc() ,width / 2 ,height / 2 + lineSpacingExtra * i ,gPaint);
				}
			}
		}
		else
		{
			for(int i = 0 ; i < list.size() ; i ++ )
			{
				canvas.drawText(list.get(i).getLrc() ,width / 2 ,height / 2 + lineSpacingExtra * i ,gPaint);
			}
			String highLineLrc = list.get(currentPosition).getLrc();
			int highLineWidth = (int) gPaint.measureText(highLineLrc);
			int leftOffset = (width - highLineWidth) / 2;
			LrcBean lrcBean = list.get(currentPosition);
			long start = lrcBean.getStart();
			long end = lrcBean.getEnd();
			int i = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
			if(i > 0)
			{
				Bitmap textBitmap = Bitmap.createBitmap(i ,lineSpacingExtra ,Bitmap.Config.ARGB_8888);
				Canvas textCanvas = new Canvas(textBitmap);
				textCanvas.drawText(highLineLrc ,highLineWidth / 2 ,lineSpacingExtra ,hPaint);
				canvas.drawBitmap(textBitmap ,leftOffset ,height / 2 + lineSpacingExtra * (currentPosition - 1) ,null);
			}
		}
	}

	public void init()
	{
		currentPosition = 0;
		lastPosition = 0;
		setScrollY(0);
		invalidate();
	}

	private void getCurrentPosition()
	{
		try
		{
			int currentMillis = 0;
			if(player != null)
			{
				currentMillis = player.getCurrentPosition();
			}
			else
			{
				return;
			}
			if(currentMillis < list.get(0).getStart())
			{
				currentPosition = 0;
				return;
			}
			if(currentMillis > list.get(list.size() - 1).getStart())
			{
				currentPosition = list.size() - 1;
				return;
			}
			for(int i = 0 ; i < list.size() ; i ++ )
			{
				if(currentMillis >= list.get(i).getStart() && currentMillis < list.get(i).getEnd())
				{
					currentPosition = i;
					return;
				}
			}
		}
		catch(Exception e)
		{
			// e.printStackTrace();
			System.out.println("com.runcom.jiazhangbang.listenText.lrcView.LrcView.getCurrentPosition():" + e);
			postInvalidateDelayed(100);
		}
	}
}
