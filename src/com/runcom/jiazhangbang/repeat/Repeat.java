/**
 * 
 */
package com.runcom.jiazhangbang.repeat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.LrcRead;
import com.runcom.jiazhangbang.listenText.LyricContent;
import com.runcom.jiazhangbang.listenText.LyricView;
import com.runcom.jiazhangbang.listenText.MyAudio;
import com.runcom.jiazhangbang.util.LrcFileDownloader;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-12
 * @time 上午10:36:45
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.repeat
 * @file_name Repeat.java
 * @type_name Repeat
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

@SuppressLint("HandlerLeak")
public class Repeat extends Activity implements Runnable , OnCompletionListener , OnErrorListener , OnSeekBarChangeListener , OnBufferingUpdateListener
{

	VideoView videoView;
	// spinner
	private Spinner spinner;

	// seekbar
	private SeekBar seekBar;
	private ImageButton btnPlay;
	private TextView tv_currTime , tv_totalTime , tv_lrc;
	List < MyAudio > play_list = new ArrayList < MyAudio >();
	List < String > play_list_copy = new ArrayList < String >();
	MyAudio myAudio;
	public MediaPlayer mp;
	int currIndex = 0;// 表示当前播放的音乐索引
	private boolean seekBarFlag = true;// 控制进度条线程标记

	// 定义当前播放器的状态
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;
	private static final int CURR_TIME_VALUE = 1;

	private int play_currentState = IDLE; // 当前播放器的状态
	// 定义线程池（同时只能有一个线程运行）
	private ExecutorService es = Executors.newSingleThreadExecutor();

	// initialization
	private Intent intent;
	private String lyricsPath;
	int selected;

	// 歌词处理
	private LrcRead mLrcRead;
	private LyricView mLyricView;
	private int index = 0;
	private float progress = 0.000f;
	private int CurrentTime = 0;
	private int CountTime = 0;
	private List < LyricContent > LyricList = new ArrayList < LyricContent >();

	// rocord
	private int record_currentVoice = 0;
	private MediaRecorder myAutoRecorder;
	// private AudioRecord audioRecord;
	private String outputFile = Util.RECORDPATH + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss" , Locale.CHINA).format(new Date()) + ".mp3";

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repeat_main);

		intent = getIntent();
		selected = intent.getIntExtra("selected" ,0);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = " 跟读 " + selected + "年级";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		initPlayView();
	}

	private void initPlayView()
	{
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		mp.setOnBufferingUpdateListener(this);
		// spinner
		spinner = (Spinner) findViewById(R.id.listenText_spinner);
		btnPlay = (ImageButton) findViewById(R.id.media_play);
		seekBar = (SeekBar) findViewById(R.id.listenText_seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		tv_currTime = (TextView) findViewById(R.id.listenText_textView_curr_time);
		tv_totalTime = (TextView) findViewById(R.id.listenText_textView_total_time);
		tv_lrc = (TextView) findViewById(R.id.listenText_lyricView_textView);

		initData();
		initRecord();

	}

	private void initData()
	{
		// source1 = "http://if.redvpn.cn:9900/cn/stream/14/14.m3u8";
		// source2 = "http://123.206.133.214:8080/wgcwgc/mp3/001.mp3";
		// source3 = "http://if.redvpn.cn:9900/cn/stream/14.mp3";
		// source4 = "http://123.206.133.214:8080/wgcwgc/mp3/001.mp3";

		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
			OkHttpUtils.get().url(Util.SERVERADDRESS_repeat).build().execute(new Callback < String >()
			{

				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					initSpinner();
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					String response = arg0.body().string().trim();
					JSONObject jsonObject = new JSONObject(response);
					String source = jsonObject.getString("source");
					String lyric = jsonObject.getString("lyric");
					String name = jsonObject.getString("name");
					play_list.clear();
					play_list_copy.clear();
					for(int i = 1 ; i <= 8 ; i ++ )
					{
						myAudio = new MyAudio();
						myAudio.setId(i);
						myAudio.setName(name + i);
						String lyric_copy = lyric.substring(0 ,lyric.lastIndexOf("/") + 1) + "00" + i + ".lrc";
						myAudio.setLyric(lyric_copy);
						if( !new File(Util.LYRICSPATH + lyric_copy.substring(lyric_copy.lastIndexOf("/") + 1)).exists())
							new LrcFileDownloader(lyric_copy).start();
						String source_copy = source.substring(0 ,source.lastIndexOf("/") + 1) + "00" + i + ".mp3";
						myAudio.setSource(source_copy);
						play_list.add(myAudio);
						play_list_copy.add(myAudio.getName());
					}

					Log.d("执行LOG" ,play_list.toString() + "\n" + play_list_copy.toString() + "\n");
					return "initData" + arg1;
				}

			});

	}

	private void initSpinner()
	{

		ArrayAdapter < String > adapter;
		adapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , play_list_copy);

		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				currIndex = arg2;
				start();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}
		});

	}

	public String getName(String url )
	{
		return url.contains("/") ? url.substring(url.lastIndexOf("/") + 1 ,url.lastIndexOf(".")) : url.substring(0 ,url.lastIndexOf("."));
	}

	private void initLyric()
	{
		lyricsPath = play_list.get(currIndex).getLyric();
		new LrcFileDownloader(lyricsPath);
		lyricsPath = Util.LYRICSPATH + lyricsPath.substring(lyricsPath.lastIndexOf("/"));
		int flag = Util.lyricChinese;

		mLrcRead = new LrcRead();
		mLyricView = (LyricView) findViewById(R.id.listenText_lyricShow);
		try
		{
			if(new File(lyricsPath).exists())
			{
				mLrcRead.Read(lyricsPath ,flag);
			}
			else
			{
				String defaultLyricPath = Util.LYRICSPATH + "defaultLyric.lrc";
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
		mLyricView.setBackgroundColor(Color.parseColor("#969696"));
		mHandler.post(mRunnable);
		myHandler.post(myRunnable);
		// MyHandler.post(MyRunnable);
	}

	Handler mHandler = new Handler();
	Handler myHandler = new Handler();
	Handler MyHandler = new Handler();

	Runnable mRunnable = new Runnable()
	{
		public void run()
		{
			mLyricView.SetIndex(Index());
			mLyricView.SetProgress(Progress());
			mLyricView.invalidate();
			mHandler.postDelayed(mRunnable ,10);
		}
	};

	Runnable myRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			String tempString = "";
			Log.d("LOG" ,"size(): " + LyricList.size() + " Index(): " + Index());
			for(int i = 0 ; i < (LyricList.size() - Index()) * 1.1 ; i ++ )
				tempString += " \n";
			tv_lrc.setText(tempString);
			myHandler.postDelayed(myRunnable ,1700);
		}
	};

	private int index_MyRunnable_before = 0 , index_MyRunnable_after = 0;
	Runnable MyRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			Log.d("LOGMyRunnable" ,"gege is running");

			index_MyRunnable_before = index_MyRunnable_after;
			index_MyRunnable_after = Index();
			if(index_MyRunnable_before < index_MyRunnable_after)
			{
				// play();
				mp.pause();
				btnPlay.setImageResource(R.drawable.play_pause);
				play_currentState = START;
				// if(play_currentState == START)
				{
					// 录音**********************************************************
					Log.d("LOGluyin" ,"gege is recording");
					// try
					// {
					// Thread.sleep(3 * 1000);
					// }
					// catch(InterruptedException e)
					// {
					// e.printStackTrace();
					// }
					// play_currentState = START;
					// play();
				}
			}
			else
			{
				mp.start();
				btnPlay.setImageResource(R.drawable.play_start);
				play_currentState = PAUSE;
			}
			MyHandler.postAtTime(MyRunnable ,1000);
		}

	};

	public float Progress()
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
						progress = 0;
					}
					if(CurrentTime > LyricList.get(i).getLyricTime() && CurrentTime < LyricList.get(i + 1).getLyricTime())
					{
						index = i;
						progress = (float) ((float) (CurrentTime - LyricList.get(i).getLyricTime()) / (float) (LyricList.get(i + 1).getLyricTime() - LyricList.get(i).getLyricTime()));
					}
				}

				if(i == LyricList.size() - 1 && CurrentTime > LyricList.get(i).getLyricTime())
				{
					index = i;
					progress = 1;
				}
			}
		}
		return progress;
	}

	public int Index()
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

	public void settingFinishTime(long time )
	{
		final Timer timer = new Timer();
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				mp.stop();
				finish();
			}
		};
		timer.schedule(task ,1000 * time);
	}

	public void onDetailSetting(View v )
	{
		intent = new Intent();
		intent.putExtra("selected" ,selected);
		intent.setClass(Repeat.this ,RepeatMainActivity.class);
		startActivity(intent);
	}

	// 播放按钮
	public void playText(View v )
	{
		play();
	}

	public void play()
	{
		switch(play_currentState)
		{
			case IDLE:
				start();
				break;
			case PAUSE:
				mp.pause();
				btnPlay.setImageResource(R.drawable.play_pause);
				play_currentState = START;
				break;
			case START:
				mp.start();
				btnPlay.setImageResource(R.drawable.play_start);
				play_currentState = PAUSE;
		}

	}

	// 快退按钮
	public void previousText(View v )
	{
		previous();
	}

	public void previous()
	{
		// Log.d("LOG" ,currIndex + "");
		if(currIndex >= 1 && play_list.size() > 0)
		{
			currIndex -- ;
			spinner.setSelection(currIndex);
			start();
		}
		else
			if(play_list.size() <= 0)
			{
				Toast.makeText(getApplicationContext() ,"播放列表为空" ,Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext() ,"当前已经是第一首了" ,Toast.LENGTH_SHORT).show();
			}
	}

	// 快进按钮
	public void nextText(View v )
	{
		next();
	}

	public void next()
	{
		if(currIndex < play_list.size() - 1)
		{
			++ currIndex;
			spinner.setSelection(currIndex);
			start();
		}
		else
			if(currIndex == play_list.size())
			{
				Toast.makeText(getApplicationContext() ,"播放列表为空" ,Toast.LENGTH_SHORT).show();
			}
			else
				if(currIndex == play_list.size() - 1)
				{
					Toast.makeText(getApplicationContext() ,"当前已经是最后一首了" ,Toast.LENGTH_SHORT).show();
					currIndex = -1;
					next();
				}
				else
				{
					Toast.makeText(getApplicationContext() ,"当前已经是最后一首了lelele" ,Toast.LENGTH_SHORT).show();
					currIndex = -1;
					next();
				}
	}

	// 开始播放
	public void start()
	{
		if(play_list.size() > 0 && currIndex < play_list.size())
		{
			String SongPath = play_list.get(currIndex).getSource();
			mp.reset();
			try
			{
				// AssetManager assetManager = getAssets();
				// AssetFileDescriptor afd = assetManager.openFd(SongPath);
				// mp.setDataSource(afd.getFileDescriptor());

				// videoView.setVideoURI(Uri.parse(SongPath));
				// videoView.requestFocus();
				// videoView.start();

				mp.setDataSource(SongPath);
				mp.prepare();
				mp.start();
				Log.d("LOG" ,SongPath);
				initSeekBar();
				es.execute(this);
				btnPlay.setImageResource(R.drawable.play_start);
				play_currentState = PAUSE;
				initLyric();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Log.d("LOG" ,"bugle");
			}
		}
		else
		{
			Toast.makeText(this ,"播放完毕" ,Toast.LENGTH_SHORT).show();
		}
	}

	// 监听器，当当前歌曲播放完时触发
	public void onCompletion(MediaPlayer mp )
	{
		if(currIndex < play_list.size() - 1 && currIndex >= 0)
		{
			next();
		}
		else
		{
			tv_currTime.setText("00:00");
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
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
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

	// 初始化录音配置
	private void initRecord()
	{
		myAutoRecorder = new MediaRecorder();
		// 从麦克风源进行录音
		myAutoRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		// 设置输出格式
		myAutoRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		// 设置编码格式
		myAutoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

		myAutoRecorder.setOutputFile(outputFile);
	}

	public void recordStart()
	{
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		record_currentVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,0 ,0);

		try
		{
			myAutoRecorder.prepare();
			myAutoRecorder.start();
		}
		catch(Exception e)
		{
			Log.d("LOG" ,e.toString());
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext() ,"Recording..." ,Toast.LENGTH_LONG).show();

	}

	public void recordPause()
	{
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		record_currentVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,0 ,0);

		try
		{
			myAutoRecorder.prepare();
			myAutoRecorder.start();
		}
		catch(Exception e)
		{
			Log.d("LOG" ,e.toString());
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext() ,"Recording..." ,Toast.LENGTH_LONG).show();

	}

	public void recordStop()
	{
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,record_currentVoice ,0);

		myAutoRecorder.stop();
		myAutoRecorder.release();
		myAutoRecorder = null;
		Toast.makeText(getApplicationContext() ,"Record successfully!!!\n文件保存在:" + outputFile ,Toast.LENGTH_LONG).show();

	}

	public void recordPlay()
	{
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,record_currentVoice ,0);
		mp.pause();
		btnPlay.setImageResource(R.drawable.play_pause);
		// play_currentState = START;

		MediaPlayer m = new MediaPlayer();
		try
		{
			m.setDataSource(outputFile);
			m.prepare();
			m.start();
			Toast.makeText(getApplicationContext() ,"Your record is playing." ,Toast.LENGTH_LONG).show();
		}
		catch(Exception e)
		{
			Log.d("LOG" ,e.toString());
			e.printStackTrace();
		}
	}

	public void recordShare()
	{
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,record_currentVoice ,0);

		Intent intent = new Intent(Intent.ACTION_SEND);

		intent.setType("audio/*");
		intent.putExtra(Intent.EXTRA_SUBJECT ,"Share");
		String url = outputFile.toString();
		Uri uri = Uri.parse(url);
		intent.putExtra(Intent.EXTRA_STREAM ,uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent ,"分享"));
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
				seekBarFlag = false;
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
			seekBarFlag = false;
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
