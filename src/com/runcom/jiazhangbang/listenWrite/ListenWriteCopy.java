package com.runcom.jiazhangbang.listenWrite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.iflytek.voice.Text2Speech;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.MyAudio;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.LrcFileDownloader;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteCopy extends Activity
{
	private Spinner spinner;
	private SeekBar seekBar;
	private ImageButton btnShowText , btnPlay;
	private TextView tv_currTime , tv_totalTime;
	List < MyAudio > play_list = new ArrayList < MyAudio >();
	List < String > play_list_copy = new ArrayList < String >();
	MyAudio myAudio;
	public MediaPlayer mp;
	int currIndex = 0;// 表示当前播放的音乐索引

	// 定义当前播放器的状态
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;
	// private static final int CURR_TIME_VALUE = 1;

	private int play_currentState = IDLE; // 当前播放器的状态

	private Intent intent;
	int selected;
	Thread playThread = null;

	// List < String > data = new ArrayList < String >();
	private String [] data = null;
	private ListView listView;
	private int index;
	private int intervalValue , frequencyValue;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_copy);

		intent = getIntent();

		selected = intent.getIntExtra("selected" ,0);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = " 听写 " + selected + "年级";
//		new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);
		// 两个词语间隔秒数
		intervalValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteInterval" ,1);
		// 每个词语阅读次数
		frequencyValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteFrequency" ,1);
		// System.out.println("intervalValue: " + intervalValue +
		// "\nfrequencyValue: " + frequencyValue);
		initPlayView();
		initData();
	}

	private void initPlayView()
	{
		spinner = (Spinner) findViewById(R.id.listenText_spinner);
		btnPlay = (ImageButton) findViewById(R.id.media_play);
		seekBar = (SeekBar) findViewById(R.id.listenText_seekBar);
		tv_currTime = (TextView) findViewById(R.id.listenText_textView_curr_time);
		tv_totalTime = (TextView) findViewById(R.id.listenText_textView_total_time);
		listView = (ListView) findViewById(R.id.listenText_listView);
		btnShowText = (ImageButton) findViewById(R.id.listenText_showText);
	}

	private void initData()
	{
		OkHttpUtils.get().url(Util.SERVERADDRESS_listenWriteCopy).build().execute(new Callback < String >()
		{
			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				initSpinner();
				Log.d("执行LOG" ,"我执行了" + arg0);
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);

				String words = jsonObject.getString("words");
				data = words.split(",|，");

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
				return "initData";
			}
		});
	}

	public String getName(String url )
	{
		return url.contains("/") ? url.substring(url.lastIndexOf("/") + 1 ,url.lastIndexOf(".")) : url.substring(0 ,url.lastIndexOf("."));
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
				initListView();
				initSeekBar();
				btnShowText.setEnabled(false);
				btnPlay.setImageResource(R.drawable.play_pause);
				play_currentState = IDLE;
				if(playThread != null)
				{
					suspended = true;
					flag = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}
		});
	}

	String [] hideData = null;

	public void initListView()
	{
		int leng = data.length;
		hideData = new String [leng];
		if(data != null)
			for(int i = 0 ; i < leng ; i ++ )
			{
				hideData[i] = "";
				for(int j = 0 ; j < data[i].length() ; j ++ )
					hideData[i] += "*";
			}
		listView.setAdapter(new ArrayAdapter < String >(getApplicationContext() , R.layout.listen_write_copy_listview_item , hideData));
		// start();
	}

	public void detailSetting(View v )
	{
		Toast.makeText(this ,"detailSetting..." ,Toast.LENGTH_SHORT).show();
	}

	public void showText(View v )
	{
		onCompletion();
	}

	// 播放按钮
	public void playText(View v )
	{
		play();
		// start();
	}

	public synchronized void play()
	{
		switch(play_currentState)
		{
			case IDLE:
				start();
				play_currentState = PAUSE;
				Toast.makeText(getApplicationContext() ,"IDLE" ,Toast.LENGTH_SHORT).show();
				break;
			case PAUSE:
				suspended = true;
				// playThread.suspend();
				btnPlay.setImageResource(R.drawable.play_pause);
				play_currentState = START;
				Toast.makeText(getApplicationContext() ,"PAUSE" ,Toast.LENGTH_SHORT).show();
				break;
			case START:
				resume();
				// suspended = false;
				// notify();
				Toast.makeText(getApplicationContext() ,"START" ,Toast.LENGTH_SHORT).show();
				btnPlay.setImageResource(R.drawable.play_start);
				play_currentState = PAUSE;
		}

	}

	/**
	 * 继续
	 */
	synchronized void resume()
	{
		suspended = false;
		notify();
	}

	/**
	 * 播放
	 */
	Boolean suspended = false;
	Boolean flag = false;
	public Runnable playRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			flag = true;
			while(flag)
			{
				if(index == data.length || flag == false)
					break;
				for(int i = 1 ; i <= data.length ; i ++ )
				{
					index = i;
					ListenWriteCopy.this.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							tv_currTime.setText("" + index);
							seekBar.setProgress(index);
						}
					});

					synchronized(this)
					{
						while(suspended)
						{
							try
							{
								wait();
							}
							catch(InterruptedException e)
							{
								e.printStackTrace();
								flag = false;
							}
						}
					}

					for(int j = 1 ; j <= frequencyValue ; j ++ )
					{
						new Text2Speech(getApplicationContext() , data[i - 1]).play();
						try
						{
							Thread.sleep(intervalValue * 1000);
						}
						catch(InterruptedException e)
						{
							flag = false;
							e.printStackTrace();
						}

						if(i == data.length && frequencyValue == j)
						{
							ListenWriteCopy.this.runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									flag = false;
									tv_currTime.setText("0");
									seekBar.setProgress(0);
									btnShowText.setEnabled(true);
									btnPlay.setImageResource(R.drawable.play_pause);
									// btnPlay.setEnabled(true);
									Toast.makeText(ListenWriteCopy.this ,"播放完毕" ,Toast.LENGTH_SHORT).show();
								}
							});
						}
					}

					// synchronized(MainThread.this.subLock) //用subLock锁锁住
					// 让线程wait subLock为静态的Object对象
					// {
					// try
					// {
					// MainThread.subLock.wait();
					// }
					// catch(Exception e)
					// {
					// e.printStackTrace();
					// this.flag = true;
					// }
					// }
 
				}
			}
		}
	};

	// 开始播放
	public void start()
	{
		if(playThread == null)
		{
			initSeekBar();
			btnShowText.setEnabled(false);
			btnPlay.setImageResource(R.drawable.play_start);
			// btnPlay.setEnabled(false);

			playThread = new Thread(playRunnable);
			playThread.start();
		}
	}

	public void onCompletion()
	{
		ArrayAdapter < String > adapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.listen_write_copy_listview_item , data);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		tv_currTime.setText("0");
		seekBar.setProgress(0);
	}

	// 初始化SeekBar
	private void initSeekBar()
	{
		seekBar.setMax(data.length);
		seekBar.setProgress(0);
		tv_currTime.setText("0");
		tv_totalTime.setText(data.length + "");
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
				suspended = true;
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
			suspended = true;
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
