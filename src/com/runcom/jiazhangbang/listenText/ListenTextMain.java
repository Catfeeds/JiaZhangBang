package com.runcom.jiazhangbang.listenText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.app.ProgressDialog;
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
import com.runcom.jiazhangbang.listenText.lrcView.LrcView;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.LrcFileDownloader;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenTextMain extends Activity implements Runnable , OnCompletionListener , OnErrorListener , OnSeekBarChangeListener , OnBufferingUpdateListener
{
	private Spinner spinner;
	private SeekBar seekBar;
	private ImageButton btnPlay;
	private TextView tv_currTime , tv_totalTime;
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

	String lyricsPath;
	private int course , grade , phase , unit;
	// 歌词处理
	private LrcView mLyricView;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_text_main);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.courseSharedPreferencesKeyString[Util.ListenTextMain] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.gradeSharedPreferencesKeyString[Util.ListenTextMain] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.phaseSharedPreferencesKeyString[Util.ListenTextMain] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.unitSharedPreferencesKeyString[Util.ListenTextMain] ,unit);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听课文" + Util.grade[grade] + "上学期" + Util.unit[unit];
		if(2 == phase)
			content = "听课文" + Util.grade[grade] + "下学期" + Util.unit[unit];
		actionbar.setTitle(content);

		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		initPlayView();
	}

	private void initPlayView()
	{
		mLyricView = (LrcView) findViewById(R.id.listenText_lyricShow);
		spinner = (Spinner) findViewById(R.id.repeat_spinner);
		btnPlay = (ImageButton) findViewById(R.id.media_start);
		seekBar = (SeekBar) findViewById(R.id.listenText_seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		tv_currTime = (TextView) findViewById(R.id.listenText_textView_curr_time);
		tv_totalTime = (TextView) findViewById(R.id.listenText_textView_total_time);
		mp.setOnBufferingUpdateListener(this);

		initTitle();
	}

	/**
	 * 初始化数据
	 */
	private void initTitle()
	{
		final TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,course + "");
		map.put("grade" ,grade + "");
		map.put("phase" ,phase + "");
		map.put("unit" ,0 == unit ? -- unit + "" : unit + "");
		System.out.println(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{
			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
				finish();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
				{
					initLrcMp3();
				}
				else
					if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsMissingResourceString ,Toast.LENGTH_LONG).show();
						finish();
					}
					else
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
						finish();
					}
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
				if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
				{
					return result;
				}
				// play_list_title.clear();
				play_list_id.clear();
				// System.out.println(jsonObject.toString());
				JSONArray jsonArray = jsonObject.getJSONArray("textlist");
				JSONObject textListJsonObject = null;
				int leng = jsonArray.length();
				if(leng <= 0)
				{
					return Util.okHttpUtilsResultExceptionStringValue;
				}
				for(int i = 0 ; i < leng ; i ++ )
				{
					textListJsonObject = new JSONObject(jsonArray.getString(i));
					String parts = textListJsonObject.getString("parts");
					int part = Integer.valueOf(parts);
					if(1 == part)
					{
						// play_list_title.add(textListJsonObject.getString("title"));
						play_list_id.add(textListJsonObject.getString("id"));
					}
					else
						if(1 < part)
						{
							JSONArray subjsonArray = textListJsonObject.getJSONArray("partlist");
							int length = subjsonArray.length();
							for(int k = 0 ; k < length ; k ++ )
							{
								JSONObject subjsonObject = new JSONObject(subjsonArray.getString(k));
								// play_list_title.add(subjsonObject.getString("title"));
								play_list_id.add(subjsonObject.getString("id"));
							}
						}
						else
						{
							Toast.makeText(getApplicationContext() ,"服务器异常" ,Toast.LENGTH_LONG).show();
							finish();
						}
				}

				return result;
			}

		});

	}

	private void initLrcMp3()
	{
		TreeMap < String , String > map = null;
		play_list.clear();
		// play_list_title.clear();
		final int leng = play_list_id.size();
		for(int i = 0 ; i < leng ; i ++ )
		{
			final int ii = i;
			map = Util.getMap(getApplicationContext());
			map.put("textid" ,play_list_id.get(i));
			System.out.println(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{

				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
					Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
					finish();
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					if(leng - 1 == ii)
					{
						initSpinner();
					}
					else
						if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
						{
							Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
							finish();
						}
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					JSONObject jsonObject = new JSONObject(arg0.body().string().trim());
					String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
					if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
					{
						return result;
					}
					JSONObject jsonObject_attr = new JSONObject(jsonObject.getString("attr"));
					JSONObject jsonObject_partlist = new JSONObject(jsonObject_attr.getString("partlist"));

					myAudio = new MyAudio();
					String lyric_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("subtitle");
					String title = jsonObject_partlist.getString("title");
					// play_list_title.add(title);
					myAudio.setName(title);
					// System.out.println(title);
					if( !new File(Util.LYRICSPATH + title + ".lrc").exists())
						new LrcFileDownloader(lyric_copy , title + ".lrc").start();
					myAudio.setLyric(Util.LYRICSPATH + title + ".lrc");
					// System.out.println(Util.LYRICSPATH + title + ".lrc");
					String source_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("voice");
					// System.out.println(source_copy);
					myAudio.setSource(source_copy);
					play_list.add(myAudio);
					try
					{
						Thread.sleep(2 * 1000);
					}
					catch(InterruptedException e)
					{
						System.out.println(e);
					}
					return result;
				}

			});

		}

	}

	private void initSpinner()
	{
		play_list_title.clear();
		for(int i = 0 ; i < play_list.size() ; i ++ )
		{
			play_list_title.add(play_list.get(i).getName());
			// System.out.println("play_list_lyric:" +
			// play_list.get(i).getLyric() + "\tplay_list_source:" +
			// play_list.get(i).getSource());
		}
		ArrayAdapter < String > adapter;
		adapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , play_list_title);
		spinner.setAdapter(adapter);
		progressDialog.dismiss();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				currIndex = arg2;
				// System.out.println("currIndex:" + currIndex);
				start();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}
		});
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
		File mFile = new File(lyricsPath);
		FileInputStream mFileInputStream;
		BufferedReader mBufferedReader = null;
		String Lrc_data = "";
		String content = "";
		try
		{
			mFileInputStream = new FileInputStream(mFile);
			InputStreamReader mInputStreamReader;
			mInputStreamReader = new InputStreamReader(mFileInputStream , "utf-8");
			mBufferedReader = new BufferedReader(mInputStreamReader);
			while((Lrc_data = mBufferedReader.readLine()) != null)
			{
				content += (Lrc_data + "\n");
			}

		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				mBufferedReader.close();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
		}
		// content = "[00:00.18]北京亮起来了\n" + "[00:03.03]每当夜幕降临\n" +
		// "[00:06.36]北京就亮起来了\n" + "[00:08.96]整个北京城变成了灯的海洋\n" +
		// "[00:12.56]光的世界\n" + "[00:14.80]长安街华灯高照\n" + "[00:18.54]川流不息的汽车\n" +
		// "[00:20.70]灯光闪烁\n" + "[00:22.80]像银河从天而降\n" + "[00:25.77]天安门城楼金碧辉煌\n"
		// + "[00:30.05]光彩夺目\n" + "[00:32.19]广场四周\n" +
		// "[00:35.28]彩灯勾画出一幢幢高大建筑物的雄伟轮廓\n" + "[00:41.59]环形路上\n" +
		// "[00:44.29]一座座立交桥犹如道道彩虹\n" + "[00:48.16]街道上\n" +
		// "[00:50.54]照明灯草坪灯喷泉灯礼花灯\n" + "[00:55.78]装点着美丽的北京\n" +
		// "[00:58.50]焕然一新的王府井西单商业街上\n" + "[01:04.39]明亮的橱窗\n" +
		// "[01:06.67]绚丽多彩的广告\n" + "[01:09.18]五光十色的霓虹灯\n" +
		// "[01:11.46]把繁华的大街装扮成了比白天更美的不夜城\n" + "[01:17.20]古老的故宫变得年轻了\n" +
		// "[01:22.24]一束束灯光照着她\n" + "[01:25.18]长长的城墙和美丽的角楼倒映在河面上\n" +
		// "[01:31.16]银光闪闪\n" + "[01:32.87]十分动人\n" + "[01:34.67]夜晚的北京\n" +
		// "[01:37.82]多么明亮\n" + "[01:39.44]多么辉煌\n" + "[01:41.35]";
		mLyricView.setLrc(content);
		mLyricView.setPlayer(mp);
		mLyricView.init();
	}

	public void settingFinishTime(long time )
	{
		final Timer timer = new Timer();
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				if(mp != null || mp.isPlaying())
				{
					mp.release();
					mp = null;
				}
				seekBarFlag = false;
				onBackPressed();
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
				// initLyric();
				break;
			case PAUSE:
				mp.pause();
				btnPlay.setImageResource(R.drawable.pause);
				currState = START;
				// initLyric();
				break;
			case START:
				mp.start();
				// initLyric();
				btnPlay.setImageResource(R.drawable.play);
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
			spinner.setSelection(currIndex ,true);
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
			spinner.setSelection(currIndex ,true);
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
					Toast.makeText(getApplicationContext() ,"当前已经是最后一首了" ,Toast.LENGTH_SHORT).show();
					currIndex = -1;
					next();
				}
	}

	// 开始播放
	public void start()
	{
		if(play_list.size() > 0 && currIndex < play_list.size())
		{
			// for(int i = 0 ; i < play_list.size() ; i ++ )
			// {
			// System.out.println(play_list.get(i).getLyric() + "play_list:" +
			// play_list.get(i).getSource());
			// }
			String SongPath = play_list.get(currIndex).getSource();
			// String string = "start()" + currIndex + ":" + play_list.size() +
			// play_list.get(currIndex).getSource() + ":" +
			// play_list.get(currIndex).getLyric();
			// System.out.println(string);
			mp.reset();
			try
			{
				mp.setDataSource(SongPath);
				initLyric();
				mp.prepare();
				mp.start();
				initSeekBar();
				es.execute(this);
				btnPlay.setImageResource(R.drawable.play);
				currState = PAUSE;
			}
			catch(Exception e)
			{
				System.out.println(e);
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
			// System.out.println("————————————————————next——————————————");
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
			if(mp != null)
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
						System.out.println(e);
					}
				}
				else
				{
					seekBarFlag = false;
				}
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				if(mp != null)
				{
					mp.pause();
					mp.release();
					mp = null;
				}
				mLyricView.setPlayer(mp);
				mLyricView.init();
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
			if(mp != null)
			{
				mp.pause();
				mp.release();
				mp = null;
			}
			mLyricView.setPlayer(mp);
			mLyricView.init();
			seekBarFlag = false;
			onBackPressed();
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

	@Override
	protected void onDestroy()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
		if(mp != null)
		{
			mp.pause();
			mp.release();
			mp = null;
		}
		mLyricView.setPlayer(mp);
		mLyricView.init();
		super.onDestroy();
	}
}
