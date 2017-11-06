package com.runcom.jiazhangbang.listenText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.LrcFileDownloader;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class CopyOfListenText extends Activity implements Runnable , OnCompletionListener , OnErrorListener , OnSeekBarChangeListener , OnBufferingUpdateListener
{
	// spinner
	private Spinner spinner;

	// seekbar
	private SeekBar seekBar;
	private ImageButton btnPlay;
	private TextView tv_currTime , tv_totalTime , textView;
	// private List < String > play_list = new ArrayList < String >();
	private List < String > play_list_title = new ArrayList < String >();
	private List < String > play_list_id = new ArrayList < String >();
	private List < MyAudio > play_list = new ArrayList < MyAudio >();
	private MyAudio myAudio;
	private MediaPlayer mp;
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

	private String lyricsPath;
	private int selected , phase , unit;
	private Intent intent;
	// 歌词处理
	private LrcRead mLrcRead;
	private LyricView mLyricView;
	private int index = 0;
	private float progress = 0.000f;
	private int CurrentTime = 0;
	private int CountTime = 0;
	private List < LyricContent > LyricList = new ArrayList < LyricContent >();

	private int newIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_text);

		intent = getIntent();
		selected = intent.getIntExtra("selected" ,1);
		phase = intent.getIntExtra("phase" ,1);
		unit = (int) intent.getLongExtra("id" ,1);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听课文" + selected + "年级上册第" + unit + "单元";
		if(2 == phase)
			content = "听课文" + selected + "年级下册第" + unit + "单元";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);

		initPlayView();
	}

	private void initPlayView()
	{
		spinner = (Spinner) findViewById(R.id.listenText_spinner);
		btnPlay = (ImageButton) findViewById(R.id.media_play);
		seekBar = (SeekBar) findViewById(R.id.listenText_seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		tv_currTime = (TextView) findViewById(R.id.listenText_textView_curr_time);
		tv_totalTime = (TextView) findViewById(R.id.listenText_textView_total_time);
		textView = (TextView) findViewById(R.id.listenText_lyricView_textView);
		mp.setOnBufferingUpdateListener(this);

		initTitle();
	}

	/**
	 * 初始化数据
	 */
	private void initTitle()
	{
		final TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,Util.ChineseCourse);
		map.put("grade" ,selected + "");
		map.put("phase" ,phase + "");
		map.put("unit" ,unit + "");
		// System.out.println(Util.REALSERVER + "gettextlist.php?" +
		// URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{
			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				initLrcMp3();
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);

				play_list_title.clear();
				play_list_id.clear();
				// System.out.println(jsonObject.toString());
				JSONArray jsonArray = jsonObject.getJSONArray("textlist");
				JSONObject textListJsonObject = null;
				for(int i = 0 , leng = jsonArray.length() ; i < leng ; i ++ )
				{
					textListJsonObject = new JSONObject(jsonArray.getString(i));
					String parts = textListJsonObject.getString("parts");
					int part = Integer.valueOf(parts);
					if(1 == part)
					{
						play_list_title.add(textListJsonObject.getString("title"));
						play_list_id.add(textListJsonObject.getString("id"));
					}
					else
						if(1 < part)
						{
							JSONArray subjsonArray = new JSONArray(textListJsonObject.getJSONArray("partlist"));
							for(int k = 0 , length = subjsonArray.length() ; k < length ; k ++ )
							{
								JSONObject subjsonObject = new JSONObject(subjsonArray.getString(k));
								play_list_title.add(subjsonObject.getString("title"));
								play_list_id.add(subjsonObject.getString("id"));
							}
						}
						else
						{
							Toast.makeText(getApplicationContext() ,"服务器异常" ,Toast.LENGTH_LONG).show();
							System.exit(0);
						}
				}

				return null;
			}

		});

	}

	private void initLrcMp3()
	{
		TreeMap < String , String > map = null;
		play_list.clear();
		final int leng = play_list_id.size();
		for(int i = 0 ; i < leng ; i ++ )
		{
			final int ii = i;
			map = Util.getMap(getApplicationContext());
			map.put("textid" ,play_list_id.get(i));
			// System.out.println(Util.REALSERVER + "getfulltext.php?" +
			// URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{

				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					if(leng - 1 == ii)
						initSpinner();
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					JSONObject jsonObject = new JSONObject(arg0.body().string().trim());
					JSONObject jsonObject_attr = new JSONObject(jsonObject.getString("attr"));
					JSONObject jsonObject_partlist = new JSONObject(jsonObject_attr.getString("partlist"));

					myAudio = new MyAudio();
					String lyric_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("subtitle");
					// System.out.println(lyric_copy);
					if( !new File(Util.LYRICSPATH + lyric_copy.substring(lyric_copy.lastIndexOf("/") + 1)).exists())
						new LrcFileDownloader(lyric_copy).start();
					myAudio.setLyric(lyric_copy);
					String source_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("voice");
					// System.out.println(source_copy);
					myAudio.setSource(source_copy);
					play_list.add(myAudio);
					return null;
				}

			});

		}

	}

	private void initSpinner()
	{
		ArrayAdapter < String > adapter;
		adapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , play_list_title);

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

	@SuppressWarnings("unused")
	private String getName(String url )
	{
		return url.contains("/") ? url.substring(url.lastIndexOf("/") + 1 ,url.lastIndexOf(".")) : url.substring(0 ,url.lastIndexOf("."));
	}

	@SuppressLint("HandlerLeak")
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

	private void initLyric()
	{
		lyricsPath = play_list.get(currIndex).getLyric();
		lyricsPath = Util.LYRICSPATH + lyricsPath.substring(lyricsPath.lastIndexOf("/") + 1);
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
		mHandler.post(mRunnable);
		myHandler.post(myRunnable);
	}

	Handler mHandler = new Handler();
	Handler myHandler = new Handler();

	Runnable mRunnable = new Runnable()
	{
		public void run()
		{
			mLyricView.SetIndex(Index());
			mLyricView.SetProgress(Progress());
			mLyricView.invalidate();
			mHandler.postDelayed(mRunnable ,1);
		}
	};

	Runnable myRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			int indexTemp = Index();

			Log.d("LOG" ,"Index(): " + indexTemp + " newIndex: " + newIndex);
			if(indexTemp == newIndex)
			{
				mLyricView.setScrolled(false);
			}
			else
				if(indexTemp > newIndex)
				{
					newIndex = indexTemp;
					mLyricView.setScrolled(true);
				}
			String tempString = "";
			// Log.d("LOG" ,"size(): " + LyricList.size() + " Index(): " +
			// Index());
			for(int i = 0 ; i < (LyricList.size() - Index()) * 1.1 ; i ++ )
				tempString += " \n";
			textView.setText(tempString);
			myHandler.postDelayed(this ,1700);
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

	int selectedId = R.id.action_oo;

	public void detailSetting(View v )
	{
		final PopupMenu popup = new PopupMenu(this , v);
		getMenuInflater().inflate(R.menu.time_setting ,popup.getMenu());
		popup.show();
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item )
			{
				item.setCheckable(true);
				switch(item.getItemId())
				{
					case R.id.action_5:
						settingFinishTime(5 * 60);
						selectedId = R.id.action_5;
						break;

					case R.id.action_15:
						settingFinishTime(15 * 60);
						selectedId = R.id.action_15;
						break;

					case R.id.action_30:
						settingFinishTime(30 * 60);
						selectedId = R.id.action_30;
						break;

					case R.id.action_60:
						settingFinishTime(60 * 60);
						selectedId = R.id.action_60;
						break;

					default:
						selectedId = R.id.action_oo;
						break;

				}
				Toast.makeText(getApplicationContext() ,"【" + item.getTitle() + "】" ,Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		popup.getMenu().findItem(selectedId).setChecked(true);
	}

	// 播放按钮
	public void playText(View v )
	{
		play();
	}

	public void play()
	{
		switch(currState)
		{
			case IDLE:
				start();
				break;
			case PAUSE:
				mp.pause();
				btnPlay.setImageResource(R.drawable.play_pause);
				currState = START;
				break;
			case START:
				mp.start();
				btnPlay.setImageResource(R.drawable.play_start);
				currState = PAUSE;
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
		// Log.d("LOG" ,currIndex + "");
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
		Log.d("LOG" ,"start()" + currIndex + ":" + play_list.size() + play_list.get(currIndex).getSource());
		if(play_list.size() > 0 && currIndex < play_list.size())
		{
			String SongPath = play_list.get(currIndex).getSource();
			Log.d("LOG" ,SongPath);
			mp.reset();
			try
			{
				mp.setDataSource(SongPath);
				mp.prepare();
				mp.start();
				initSeekBar();
				es.execute(this);
				btnPlay.setImageResource(R.drawable.play_start);
				currState = PAUSE;
				initLyric();
			}
			catch(Exception e)
			{
				Log.d("LOG" ,"bugle");
				e.printStackTrace();
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
		// Log.d("LOG" , "percent: " + percent);
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
