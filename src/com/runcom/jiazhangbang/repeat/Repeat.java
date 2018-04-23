/**
 * 
 */
package com.runcom.jiazhangbang.repeat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.judge.Judge;
import com.runcom.jiazhangbang.listenText.LrcRead;
import com.runcom.jiazhangbang.listenText.LyricContent;
import com.runcom.jiazhangbang.listenText.LyricView;
import com.runcom.jiazhangbang.listenText.MyAudio;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.LrcFileDownloader;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
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

public class Repeat extends Activity
{
	private MediaRecorder mediaRecorder = null;// 录音器
	private Timer timer;
	private String fileAllNameAmr = null;
	private String fileAllNameMp3 = null;
	private String recordPath = Util.RECORDPATH;
	private ArrayList < String > myRecordList = new ArrayList < String >();// 待合成的录音片段
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private TextView time;// 计时显示

	private Spinner spinner;
	private ImageButton startRecord , stopRecord;
	private TextView tv_lrc;
	private List < MyAudio > play_list = new ArrayList < MyAudio >();
	private List < String > play_list_copy = new ArrayList < String >();
	private List < String > play_list_id = new ArrayList < String >();
	private MyAudio myAudio;
	private int currIndex = 0;// 表示当前播放的音乐索引

	// 定义当前播放器的状态
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;

	private int play_currentState = IDLE; // 当前播放器的状态
	private Intent intent;
	private String lyricsPath;
	private int course , grade , phase , unit;

	private LrcRead mLrcRead;
	private LyricView mLyricView;
	private List < LyricContent > LyricList = new ArrayList < LyricContent >();

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repeat_main);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.courseSharedPreferencesKeyString[Util.Repeat] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.gradeSharedPreferencesKeyString[Util.Repeat] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.phaseSharedPreferencesKeyString[Util.Repeat] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.unitSharedPreferencesKeyString[Util.Repeat] ,unit);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "朗读" + Util.grade[grade] + "上学期" + Util.unit[unit];
		if(2 == phase)
			content = "朗读" + Util.grade[grade] + "下学期" + Util.unit[unit];
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		initPlayView();
	}

	private void initPlayView()
	{
		spinner = (Spinner) findViewById(R.id.repeat_spinner);
		startRecord = (ImageButton) findViewById(R.id.media_start);
		stopRecord = (ImageButton) findViewById(R.id.media_stop);
		tv_lrc = (TextView) findViewById(R.id.listenText_lyricView_textView);
		time = (TextView) findViewById(R.id.listen_write_textView_nameShow);
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
					initData();
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
							System.exit(0);
						}
				}

				return result;
			}

		});

	}

	private void initData()
	{

		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,Util.okHttpUtilsInternetConnectExceptionString ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			TreeMap < String , String > map = null;
			play_list.clear();
			play_list_copy.clear();
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
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
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
						String response = arg0.body().string().trim();
						JSONObject jsonObject = new JSONObject(response);
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
						play_list_copy.add(title);
						myAudio.setName(title);
						if( !new File(Util.LYRICSPATH + title + ".lrc").exists())
							new LrcFileDownloader(lyric_copy , title + ".lrc").start();
						myAudio.setLyric(Util.LYRICSPATH + title + ".lrc");
						String source_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("voice");
						myAudio.setSource(source_copy);
						play_list.add(myAudio);
						return result;
					}

				});
			}
		}
	}

	private void initSpinner()
	{

		initLyric();
		ArrayAdapter < String > adapter;
		adapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , play_list_copy);

		spinner.setAdapter(adapter);
		progressDialog.dismiss();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				currIndex = arg2;
				initLyric();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}
		});

		stopRecord.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				if(play_currentState != IDLE)
				{
					stopRecord();
				}
				else
				{
					Toast.makeText(getApplicationContext() ,"请开始录音" ,Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void initLyric()
	{
		lyricsPath = play_list.get(currIndex).getLyric();
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
		myHandler.post(myRunnable);
	}

	Handler myHandler = new Handler();

	Runnable myRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			String tempString = "";
			for(int i = 0 ; i < LyricList.size() * 1.5 ; i ++ )
				tempString += " \n";
			tv_lrc.setText(tempString);
			myHandler.postDelayed(myRunnable ,1700);
		}
	};

	public void onDetailSetting(View v )
	{
		intent = new Intent();
		intent.putExtra("selected" ,grade);
		intent.setClass(Repeat.this ,RepeatMainActivity.class);
		startActivity(intent);
	}

	public void repeatSwitching(View v )
	{
		switch(play_currentState)
		{
			case IDLE:
				play_currentState = PAUSE;
				startRecord.setImageResource(R.drawable.play);
				startRecord();
				recordTime();
				break;
			case PAUSE:
				play_currentState = START;
				startRecord.setImageResource(R.drawable.record_pause);
				mediaRecorder.stop();
				mediaRecorder.release();
				timer.cancel();
				myRecordList.add(fileAllNameAmr);

				break;
			case START:
				play_currentState = PAUSE;
				startRecord.setImageResource(R.drawable.play);
				startRecord();
				recordTime();
				break;
			default:
				break;
		}

	}

	// 完成录音
	private void stopRecord()
	{
		play_currentState = IDLE;
		mediaRecorder.release();
		mediaRecorder = null;
		myRecordList.add(fileAllNameAmr);
		startRecord.setImageResource(R.drawable.record_pause);
		timer.cancel();

		final EditText editText = new EditText(Repeat.this);
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(Repeat.this);
		inputDialog.setTitle("要保存录音吗？").setView(editText);
		inputDialog.setPositiveButton("保存" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				// 最后合成的音频文件
				String playName = editText.getText().toString().trim();
				if(playName.isEmpty() || Judge.isNotName(playName))
				{
					playName = getTime();
					if(playName.isEmpty())
					{
						Toast.makeText(getApplicationContext() ,"已自动命名为：" + playName ,Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext() ,"输入名字不符合要求\n已自动命名为：" + playName ,Toast.LENGTH_SHORT).show();
					}
				}

				if(isExit(playName))
				{
					playName += "_" + getTime();
					Toast.makeText(getApplicationContext() ,"文件已存在\n已自动命名为：" + playName ,Toast.LENGTH_SHORT).show();
				}

				fileAllNameAmr = recordPath + playName + ".amr";
				fileAllNameMp3 = recordPath + playName + ".mp3";
				FileOutputStream fileOutputStream = null;
				try
				{
					fileOutputStream = new FileOutputStream(fileAllNameAmr);
				}
				catch(FileNotFoundException e)
				{
				}
				FileInputStream fileInputStream = null;
				try
				{
					for(int i = 0 ; i < myRecordList.size() ; i ++ )
					{
						File file = new File(myRecordList.get(i));
						// 把因为暂停所录出的多段录音进行读取
						fileInputStream = new FileInputStream(file);
						byte [] mByte = new byte [fileInputStream.available()];
						int length = mByte.length;
						// 第一个录音文件的前六位是不需要删除的
						if(i == 0)
						{
							while(fileInputStream.read(mByte) != -1)
							{
								fileOutputStream.write(mByte ,0 ,length);
							}
						}
						// 之后的文件，去掉前六位
						else
						{
							while(fileInputStream.read(mByte) != -1)
							{
								fileOutputStream.write(mByte ,6 ,length - 6);
							}
						}
					}

					Amr2Mp3.transformation(fileAllNameAmr ,fileAllNameMp3);
					time.setText("录音完成");
				}
				catch(Exception e)
				{
					Toast.makeText(getApplicationContext() ,"录音合成出错，请重试！" ,Toast.LENGTH_LONG).show();
					time.setText("录音合成出错，请重试！");
					System.out.println(e);
				}
				finally
				{
					try
					{
						fileOutputStream.flush();
						fileInputStream.close();
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}

				for(int i = 0 ; i < myRecordList.size() ; i ++ )
				{
					File file = new File(myRecordList.get(i));
					if(file.exists())
					{
						file.delete();
					}
				}
			}
		});
		inputDialog.setNegativeButton("放弃" ,new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				time.setText("");
				for(int i = 0 ; i < myRecordList.size() ; i ++ )
				{
					File file = new File(myRecordList.get(i));
					if(file.exists())
					{
						file.delete();
					}
				}
			}
		});
		inputDialog.show();

		minute = 0;
		hour = 0;
		second = 0;
	}

	private Boolean isExit(String name )
	{
		File recordePathFile = new File(recordPath);
		if( !recordePathFile.exists())
		{
			try
			{
				recordePathFile.getParentFile().mkdirs();
				recordePathFile.mkdir();
			}
			catch(Exception e)
			{
			}
		}

		// 判断SD卡是否存在
		if( !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this ,"SD卡状态异常，" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			// 根据后缀名进行判断、获取文件夹中的音频文件
			File file = new File(recordPath);
			File files[] = file.listFiles();
			String childFileName = null;
			for(File childFile : files)
			{
				childFileName = childFile.toString();
				if(childFileName.length() > 0 && (childFileName.endsWith(".amr")))
				{
					if((childFileName.substring(childFileName.lastIndexOf("/") + 1 ,childFileName.lastIndexOf("."))).equals(name))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	// 开始录音
	@SuppressWarnings("deprecation")
	private void startRecord()
	{
		myRecordList.clear();
		File file = new File(recordPath);
		if( !file.exists())
		{
			file.mkdirs();
		}
		fileAllNameAmr = recordPath + getTime() + ".amr";
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 选择amr格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mediaRecorder.setOutputFile(fileAllNameAmr);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try
		{
			mediaRecorder.prepare();
		}
		catch(Exception e)
		{
			// 若录音器启动失败就需要重启应用，屏蔽掉按钮的点击事件。 否则会出现各种异常。
			Toast.makeText(this ,"录音器启动失败，请返回重试！" ,Toast.LENGTH_LONG).show();
			mediaRecorder.release();
			mediaRecorder = null;
			this.finish();
		}
		if(mediaRecorder != null)
		{
			mediaRecorder.start();
		}

	}

	// 计时器异步更新界面
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg )
		{
			time.setText("您本次的录音时长为：" + String.format("%1$02d:%2$02d:%3$02d" ,hour ,minute ,second));
			super.handleMessage(msg);
		}
	};

	// 录音计时
	private void recordTime()
	{
		TimerTask timerTask = new TimerTask()
		{

			@Override
			public void run()
			{
				second ++ ;
				if(second >= 60)
				{
					second = 0;
					minute ++ ;
					if(minute >= 60)
					{
						minute = 0;
						hour ++ ;
					}
				}
				handler.sendEmptyMessage(1);
			}

		};
		timer = new Timer();
		timer.schedule(timerTask ,1000 ,1000);
	}

	// 获得当前时间
	@SuppressLint("SimpleDateFormat")
	private String getTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(curDate);
		return time;
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

	@Override
	protected void onDestroy()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

}
