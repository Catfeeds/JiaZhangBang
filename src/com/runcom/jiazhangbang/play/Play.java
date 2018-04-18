package com.runcom.jiazhangbang.play;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.LrcRead;
import com.runcom.jiazhangbang.listenText.LyricContent;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("HandlerLeak")
public class Play extends Activity implements Runnable , OnCompletionListener , OnErrorListener , OnSeekBarChangeListener , OnBufferingUpdateListener
{
	private SeekBar seekBar;
	private ImageButton btnPlay;
	private TextView tv_currTime , tv_totalTime , tv_showName;
	private List < String > play_list = new ArrayList < String >();
	public MediaPlayer mp;
	private int currIndex = 0;// 表示当前播放的音乐索引
	private boolean seekBarFlag = true;// 控制进度条线程标记

	// 定义当前播放器的状态
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;
	private static final int CURR_TIME_VALUE = 1;

	private int currState = IDLE; // 当前播放器的状态
	// 定义线程池（同时只能有一个线程运行）
	private ExecutorService es = Executors.newSingleThreadExecutor();

	private String source , lyricsPath , filePath , resultBuffer;

	// 歌词处理
	private LrcRead mLrcRead;
	private LyricView mLyricView;
	private int index = 0;
	private int CurrentTime = 0;
	private int CountTime = 0;
	private List < LyricContent > LyricList = new ArrayList < LyricContent >();

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("智能识文 ");
		// System.out.println("play 执行了");
		initPlayView();
	}

	private void initPlayView()
	{
		btnPlay = (ImageButton) findViewById(R.id.media_start);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(this);
		tv_currTime = (TextView) findViewById(R.id.textView1_curr_time);
		tv_totalTime = (TextView) findViewById(R.id.textView1_total_time);
		tv_showName = (TextView) findViewById(R.id.tv_showName);
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		mp.setOnBufferingUpdateListener(this);
		mp.setLooping(true);

		Intent intent = getIntent();
		filePath = intent.getStringExtra("filePath");
		resultBuffer = intent.getStringExtra("resultBuffer");
		// TODO
		source = "http://res.nutnet.cn:8800/cn/4-2/mp3/001-1.mp3";
		lyricsPath = Util.LYRICSPATH + filePath.substring(filePath.lastIndexOf("/"));
		play_list.clear();
		play_list.add(source);
		// Log.d("LOG" ,"source: " + source + "\nname: " + name +
		// "\nlyricPath: " + lyricsPath + "\nfilePath: " + filePath);
		initLyric();
		start();
	}

	private void initLyric()
	{
		int flag = Util.lyricChinese;
		mLrcRead = new LrcRead();
		mLyricView = (LyricView) findViewById(R.id.LyricShow);
		try
		{
			if(new File(lyricsPath).exists())
			{
				mLrcRead.Read(lyricsPath ,flag);
			}
			else
			{
				String defaultLyricPath = Util.lyricsPath + "defaultLyric.lrc";
				File defaultLyricPathFile = new File(defaultLyricPath);
				if( !defaultLyricPathFile.exists() || !defaultLyricPathFile.getParentFile().exists())
				{
					defaultLyricPathFile.getParentFile().mkdirs();

					defaultLyricPathFile.createNewFile();
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(defaultLyricPathFile , false));
					bufferedWriter.write("[00:00.00] NO LYRICS\r\n");
					bufferedWriter.flush();
					bufferedWriter.close();
				}
				mLrcRead.Read(defaultLyricPath ,flag);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		LyricList = mLrcRead.GetLyricContent();
		mLyricView.setSentenceEntities(LyricList);
		mHandler.post(mRunnable);
	}

	Handler mHandler = new Handler();

	Runnable mRunnable = new Runnable()
	{
		public void run()
		{
			mLyricView.SetIndex(Index());
			mLyricView.invalidate();
			mHandler.postDelayed(mRunnable ,1000);
		}
	};

	private int Index()
	{
		if(mp.isPlaying())
		{
			CurrentTime = mp.getCurrentPosition();
			CountTime = mp.getDuration();
		}
		if(CurrentTime < CountTime)
		{
			for(int i = 0 ; i < LyricList.size() ; i ++ )
			{
				if(i < LyricList.size() - 1)
				{
					if(CurrentTime < LyricList.get(i).getLyricTime() && i == 0)
					{
						index = i;
					}
					if(CurrentTime > LyricList.get(i).getLyricTime() && CurrentTime < LyricList.get(i + 1).getLyricTime())
					{
						index = i;
					}
				}

				if(i == LyricList.size() - 1 && CurrentTime > LyricList.get(i).getLyricTime())
				{
					index = i;
				}
			}
		}

		return index;
	}

	public Handler hander = new Handler()
	{
		public void handleMessage(Message msg )
		{
			switch(msg.what)
			{
				case CURR_TIME_VALUE:
					tv_currTime.setText(msg.obj.toString());
					break;
				default:
					break;
			}
		};
	};

	// 开始播放
	private void start()
	{
		if(play_list.size() > 0 && currIndex < play_list.size())
		{
			String SongPath = play_list.get(currIndex);
			mp.reset();
			try
			{
				mp.setDataSource(SongPath);
				mp.prepare();
				mp.start();
				initSeekBar();
				es.execute(this);
				tv_showName.setText(resultBuffer);
				btnPlay.setImageResource(R.drawable.play);
				currState = PAUSE;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(this ,"播放完毕" ,Toast.LENGTH_SHORT).show();
		}
	}

	// 播放按钮
	public void play(View v )
	{
		switch(currState)
		{
			case IDLE:
				start();
				break;
			case PAUSE:
				mp.pause();
				btnPlay.setImageResource(R.drawable.pause);
				currState = START;
				break;
			case START:
				mp.start();
				btnPlay.setImageResource(R.drawable.play);
				currState = PAUSE;
		}
	}

	// 快退按钮
	public void previous(View v )
	{
		mp.seekTo(mp.getCurrentPosition() - 7000);
	}

	// 快进按钮
	public void next(View v )
	{
		mp.seekTo(mp.getCurrentPosition() + 7000);
	}

	// 监听器，当当前歌曲播放完时触发，播放下一首
	public void onCompletion(MediaPlayer mp )
	{
		// mp.stop();
		// onBackPressed();
		// this.finish();

		if(play_list.size() > 0)
		{
			currIndex = 0;
			start();
		}
		else
		{
			initSeekBar();
			Toast.makeText(this ,"播放完毕" ,Toast.LENGTH_SHORT).show();
		}
	}

	// 当播放异常时触发
	public boolean onError(MediaPlayer mp , int what , int extra )
	{
		mp.reset();
		return false;
	}

	// 初始化SeekBar
	private void initSeekBar()
	{
		seekBar.setMax(mp.getDuration());
		seekBar.setProgress(0);
		tv_totalTime.setText(toTime(mp.getDuration()));
	}

	private String toTime(int time )
	{
		int minute = time / 1000 / 60;
		int s = time / 1000 % 60;
		String mm = null;
		String ss = null;
		if(minute < 10)
			mm = "0" + minute;
		else
			mm = minute + "";

		if(s < 10)
			ss = "0" + s;
		else
			ss = "" + s;

		return mm + ":" + ss;
	}

	public void run()
	{
		seekBarFlag = true;
		while(seekBarFlag)
		{
			if(mp.getCurrentPosition() < seekBar.getMax())
			{
				seekBar.setProgress(mp.getCurrentPosition());
				Message msg = hander.obtainMessage(CURR_TIME_VALUE ,toTime(mp.getCurrentPosition()));
				hander.sendMessage(msg);
			}
			else
			{
				seekBarFlag = false;
			}
		}
	}

	// SeekBar监听器
	public void onProgressChanged(SeekBar seekBar , int progress , boolean fromUser )
	{
		// 是否由用户改变
		if(fromUser)
		{
			mp.seekTo(progress);
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp , int percent )
	{
		seekBar.setSecondaryProgress(percent * mp.getDuration() / 100);
	}

	public void onStartTrackingTouch(SeekBar seekBar )
	{
	}

	public void onStopTrackingTouch(SeekBar seekBar )
	{
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.time_setting ,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				mp.stop();
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 重写按返回键退出播放
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			mp.stop();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
