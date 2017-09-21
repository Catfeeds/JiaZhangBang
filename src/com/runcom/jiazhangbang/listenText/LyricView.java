package com.runcom.jiazhangbang.listenText;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.runcom.jiazhangbang.util.Util;

public class LyricView extends ScrollView
{
	private float high;
	private float width;
	private Paint CurrentPaint;
	private Paint NotCurrentPaint;
	private Paint ThirdCurrentPaint;
	public float TextHigh = Util.sp2px(getContext() ,57);
	// public float TextHigh = Util.sp2px(getContext() ,37);
	float TextSize = Util.sp2px(getContext() ,15);
	private int Index = 0 , newIndex = 0;
	float progress = 0;

	private List < LyricContent > mySentenceEntities = new ArrayList < LyricContent >();

	// 滚动
	final Handler handler = new Handler();
	long duration = 57;
	boolean isScrolled = true;
	int currentIndex = 0;
	long period = 1;
	int currentY = -1;
	double x;
	double y;
	int type = -1;

	public void setSentenceEntities(List < LyricContent > mySentenceEntities )
	{
		this.mySentenceEntities = mySentenceEntities;
	}

	public LyricView(Context context)
	{
		super(context);
		init();

	}

	public LyricView(Context context , AttributeSet attrs , int defStyle)
	{
		super(context , attrs , defStyle);
		init();
	}

	public LyricView(Context context , AttributeSet attrs)
	{
		super(context , attrs);
		init();
	}

	private void init()
	{
		setFocusable(true);

		// 高亮部分
		CurrentPaint = new Paint();
		CurrentPaint.setAntiAlias(true);
		// CurrentPaint.setTextAlign(Paint.Align.CENTER);
		CurrentPaint.setColor(Color.parseColor("#39DF7C"));
		CurrentPaint.setTextSize(TextSize);
		CurrentPaint.setTypeface(Typeface.SERIF);

		// 滚动部分
		ThirdCurrentPaint = new Paint();
		ThirdCurrentPaint.setAntiAlias(true);
		ThirdCurrentPaint.setColor(Color.parseColor("#726463"));
		ThirdCurrentPaint.setTextSize(TextSize);
		ThirdCurrentPaint.setTypeface(Typeface.SERIF);

		// 非高亮部分
		NotCurrentPaint = new Paint();
		NotCurrentPaint.setAntiAlias(true);
		NotCurrentPaint.setTextAlign(Paint.Align.CENTER);
		NotCurrentPaint.setTextSize(TextSize);
		NotCurrentPaint.setTypeface(Typeface.SERIF);
		NotCurrentPaint.setColor(Color.parseColor("#726463"));

	}

	@Override
	protected void onDraw(Canvas canvas )
	{
		super.onDraw(canvas);

		if(canvas == null)
		{
			return;
		}

		try
		{
			int leng = mySentenceEntities.get(Index).getLyric().length();
			String content = mySentenceEntities.get(Index).getLyric().substring(0 ,(int) ((progress) * leng));
			if(Index == newIndex)
			{

			}
			else
				if(Index > newIndex)
				{
					newIndex = Index;
					high += (TextHigh + TextSize / 2);
				}
			int distance[] =
			{ 0, 220, 200, 170, 160, 150, 150, 150, 130, 140, 140, 130, 130, 120, 110, 100 };
			int baseX = 0;
			baseX = Util.sp2px(getContext() ,leng <= 15 ? distance[leng] : leng >= 50 ? 0 : 2300 / leng);

			canvas.drawText(mySentenceEntities.get(Index).getLyric() ,baseX ,high / 2 ,ThirdCurrentPaint);
			canvas.drawText(content ,baseX ,high / 2 ,CurrentPaint);

//			Log.d("LOG" ,"index: " + Index + " progress:" + progress + " leng: " + leng + " width: " + width + " baseX:" + baseX);

			float tempY = high / 2;
			// 画出本句之前的句子
			for(int i = Index - 1 ; i >= 0 ; i -- )
			{
				tempY = tempY - TextHigh;

				canvas.drawText(mySentenceEntities.get(i).getLyric() ,width / 2 ,tempY ,NotCurrentPaint);

			}

			tempY = high / 2;
			// 画出本句之后的句子
			for(int i = Index + 1 ; i < mySentenceEntities.size() ; i ++ )
			{
				tempY = tempY + TextHigh;

				canvas.drawText(mySentenceEntities.get(i).getLyric() ,width / 2 ,tempY ,NotCurrentPaint);

			}
		}
		catch(Exception e)
		{
		}

	}

	@Override
	protected void onSizeChanged(int w , int h , int oldw , int oldh )
	{
		super.onSizeChanged(w ,h ,oldw ,oldh);
		this.high = h;
		this.width = w;
	}

	public void SetIndex(int index )
	{
		this.Index = index;
		// System.out.println("x: " + getX() + " y: " + getY());
	}

	public void SetProgress(float progress )
	{
		this.progress = progress;
		// System.out.println(progress);
	}

	public void autoScroll()
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				boolean flag = isScrolled;
				if(flag)
				{
					// Log.d("LOG", "currentY = " + currentY +
					// "  getScrollY() = "+ getScrollY() );
					if(currentY == getScrollY())
					{
						try
						{
							Thread.sleep(period);
						}
						catch(InterruptedException e)
						{
							e.printStackTrace();
						}
						// currentIndex = 0;
//						scrollTo(0 ,currentIndex);
						handler.postDelayed(this ,period);
					}
					else
					{
						currentY = getScrollY();
						handler.postDelayed(this ,duration);
						currentIndex = (int) (width / 2 / 318);
						// scrollTo(0, (int) (width / 2));
						scrollBy(0 , 2);
						// offsetTopAndBottom(currentIndex);
					}
				}
				else
				{
					// currentIndex = 0;
					// scrollTo(0, 0);
				}
			}
		} ,duration);
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event )
	{
		int Action = event.getAction();
		switch(Action)
		{
			case MotionEvent.ACTION_DOWN:
				x = event.getX();
				y = event.getY();
				if(type == 0)
				{
					setScrolled(false);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				double moveY = event.getY() - y;
				double moveX = event.getX() - x;
				Log.d("LOG" ,"moveY = " + moveY + "  moveX = " + moveX);
				if((moveY > 20 || moveY < -20) && (moveX < 50 || moveX > -50) && getParent() != null)
				{
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				break;
			case MotionEvent.ACTION_UP:
				if(type == 0)
				{
					currentIndex = getScrollY();
					setScrolled(true);
				}
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent p_event )
	{
		Log.d("LOG" ,"onInterceptTouchEvent");
		return true;
	}

	/**
	 * 判断当前是否为滚动状态
	 * 
	 * @return the isScrolled
	 */
	public boolean isScrolled()
	{
		return isScrolled;
	}

	/**
	 * 开启或者关闭自动滚动功能
	 * 
	 * @param isScrolled
	 *            true为开启，false为关闭
	 */
	public void setScrolled(boolean isScrolled )
	{
		this.isScrolled = isScrolled;
		autoScroll();
	}

	/**
	 * 获取当前滚动到结尾时的停顿时间，单位：毫秒
	 * 
	 * @return the period
	 */
	public long getPeriod()
	{
		return period;
	}

	/**
	 * 设置当前滚动到结尾时的停顿时间，单位：毫秒
	 * 
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(long period )
	{
		this.period = period;
	}

	/**
	 * 获取当前的滚动速度，单位：毫秒，值越小，速度越快。
	 * 
	 * @return the speed
	 */
	public long getSpeed()
	{
		return duration;
	}

	/**
	 * 设置当前的滚动速度，单位：毫秒，值越小，速度越快。
	 * 
	 * @param speed
	 *            the duration to set
	 */
	public void setSpeed(long speed )
	{
		this.duration = speed;
	}

	public void setType(int type )
	{
		this.type = type;
	}

}
