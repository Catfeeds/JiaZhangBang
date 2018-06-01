package com.runcom.jiazhangbang.listenWrite;

import java.util.ArrayList;
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
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ShowToast")
public class ListenWriteMain extends Activity implements OnCompletionListener , OnErrorListener
{
	private long startTime , endTime;
	private int course , grade , phase , unit;
	private int intervalValue , frequencyValue;
	private int leng;
	private String [] phraseContent , voiceContent;
	private MyListenWriteMainAdapter myListenWriteMainAdapter;
	private ArrayList < NewWords > newWordsList = new ArrayList < NewWords >();
	private ArrayList < NewWords > newWordsListStar = new ArrayList < NewWords >();
	private ArrayList < String > playList = new ArrayList < String >();
	private NewWords newWords , newWordsStar;

	private GridView gridView;
	private ImageButton imageButton_pause , imageButton_stop;

	private int WAIT_TIME = 5;
	private Timer timer = new Timer();

	private int currentIndex = 0;
	private MediaPlayer mp;

	// 定义当前播放器的状态
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;

	private int play_currentState = IDLE; // 当前播放器的状态

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_main);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[Util.ListenWriteTips] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[Util.ListenWriteTips] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[Util.ListenWriteTips] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);
		if(unit > 0)
		{
			unit -- ;
		}
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[Util.ListenWriteTips] ,unit);

		// 两个词语间隔秒数
		intervalValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteInterval" ,1);
		// 每个词语阅读次数
		frequencyValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteFrequency" ,1) + 1;

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听写 " + Util.grade[grade] + "上学期" + Util.unit[unit];
		if(2 == phase)
			content = "听写 " + Util.grade[grade] + "下学期" + Util.unit[unit];
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		timer.schedule(task ,0 ,1000);
		initNewWordsData();
	}

	Toast toast = null;

	TimerTask task = new TimerTask()
	{

		@Override
		public void run()
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if(toast != null)
					{
						toast.cancel();
					}
					toast = Toast.makeText(getApplicationContext() ,"倒计时 " + WAIT_TIME ,Toast.LENGTH_SHORT);
					if(WAIT_TIME > 0)
					{
						toast.show();
					}
					if(WAIT_TIME <= 0)
					{
						toast.cancel();
						start();
						startTime = System.currentTimeMillis();
						timer.cancel();
						task.cancel();
					}
					WAIT_TIME -- ;
				}
			});

		}
	};

	private void initNewWordsData()
	{
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,Util.okHttpUtilsInternetConnectExceptionString ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			final TreeMap < String , String > map = Util.getMap(getApplicationContext());
			map.put("course" ,course + "");
			map.put("grade" ,grade + "");
			map.put("phase" ,phase + "");
			map.put("unit" ,++ unit + "");
			System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
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
						initPlayData();
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
					JSONArray jsonArray = jsonObject.getJSONArray("phlist");
					JSONObject phlistJsonObject = null;
					leng = jsonArray.length();
					if(leng <= 0)
					{
						return Util.okHttpUtilsResultExceptionStringValue;
					}
					phraseContent = new String [leng];
					voiceContent = new String [leng];
					for(int i = 0 ; i < leng ; i ++ )
					{
						phlistJsonObject = new JSONObject(jsonArray.getString(i));
						phraseContent[i] = phlistJsonObject.getString("phrase");
						voiceContent[i] = phlistJsonObject.getString("voice");
					}

					return result;
				}

			});
		}
	}

	private void initPlayData()
	{
		newWordsList.clear();
		newWordsListStar.clear();
		playList.clear();
		for(int i = 0 ; i < phraseContent.length ; i ++ )
		{
			for(int k = 0 ; k < frequencyValue ; k ++ )
			{
				playList.add(Util.RESOURCESERVER + voiceContent[i]);
				// System.out.println("*******************************" +
				// Util.RESOURCESERVER + voiceContent[i]);
			}
			// Log.d("log*********" ,"0***" + playList.get(i).toString());

			newWords = new NewWords();
			newWords.setId(i);
			newWords.setName(phraseContent[i]);
			newWordsList.add(newWords);
			// Log.d("log*********" ,"1***" + newWords.getName());
			// Toast.makeText(getApplicationContext() , newWords.getName()
			// ,Toast.LENGTH_SHORT).show();
			// String stars = "";
			// for(int j = 0 ; j < phraseContent[i].length() ; j ++ )
			// {
			// stars += "*";
			// }
			newWordsStar = new NewWords();
			newWordsStar.setId(i);
			// newWordsStar.setName(stars);
			newWordsStar.setName((i + 1) + "");
			newWordsListStar.add(newWordsStar);
			// Log.d("log*********" ,"2***" + newWordsStar.getName());
			// Toast.makeText(getApplicationContext() , newWords.getName()
			// ,Toast.LENGTH_SHORT).show();
		}

		initView();

	}

	private void initView()
	{
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		gridView = (GridView) findViewById(R.id.listen_write_main_gridView);
		myListenWriteMainAdapter = new MyListenWriteMainAdapter(getApplicationContext() , newWordsListStar);
		gridView.setAdapter(myListenWriteMainAdapter);
		gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		myListenWriteMainAdapter.notifyDataSetInvalidated();
		progressDialog.dismiss();
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				if(Util.debug)
				{
					Toast.makeText(getApplicationContext() ,(newWordsList.get(position).getId() + 1) + "\n" + newWordsList.get(position).getName() ,Toast.LENGTH_SHORT).show();
				}
			}
		});

		imageButton_pause = (ImageButton) findViewById(R.id.listen_write_main_textView_pause);
		imageButton_stop = (ImageButton) findViewById(R.id.listen_write_main_textView_stop);
		imageButton_pause.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				play();
			}
		});

		imageButton_stop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				final AlertDialog.Builder dialog = new AlertDialog.Builder(ListenWriteMain.this);
				dialog.setCancelable(false);
				dialog.setMessage("确定要结束吗 ？");
				dialog.setPositiveButton("确定" ,new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog , int which )
					{
						if(mp != null)
						{
							mp.stop();
							mp.release();
							mp = null;
						}
						myListenWriteMainAdapter = new MyListenWriteMainAdapter(getApplicationContext() , newWordsList);
						gridView.setAdapter(myListenWriteMainAdapter);
						myListenWriteMainAdapter.notifyDataSetChanged();
						imageButton_stop.setEnabled(false);
						imageButton_pause.setEnabled(false);
					}
				});

				dialog.setNegativeButton("取消" ,new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog , int which )
					{
					}
				});
				dialog.show();

			}
		});

		// play();
		handlerView.post(runnableView);
		// handlerPlay.post(runnablePlay);
	}

	private void play()
	{

		switch(play_currentState)
		{
		// case IDLE:
		// start();
		// break;
			case IDLE:
				if(mp != null)
				{
					mp.pause();
				}
				else
				{
					mp = null;
				}
				imageButton_pause.setBackgroundResource(R.drawable.pause);
				play_currentState = START;
				break;
			case PAUSE:
				if(mp != null)
				{
					mp.pause();
				}
				else
				{
					mp = null;
				}
				imageButton_pause.setBackgroundResource(R.drawable.pause);
				play_currentState = START;
				break;
			case START:
				if(mp != null)
				{
					mp.start();
				}
				else
				{
					start();
				}
				imageButton_pause.setBackgroundResource(R.drawable.play);
				play_currentState = PAUSE;
				break;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp )
	{
		if(play_currentState == START)
		{
			if(mp != null)
			{
				mp.pause();
			}
			else
			{
				mp = null;
			}
		}
		else
		{
			++ currentIndex;
			if(currentIndex >= playList.size())
			{
				if(mp != null)
				{
					mp.release();
					mp = null;
				}
				Toast.makeText(getApplicationContext() ,"听写完毕" ,Toast.LENGTH_SHORT).show();
				myListenWriteMainAdapter = new MyListenWriteMainAdapter(getApplicationContext() , newWordsList);
				gridView.setAdapter(myListenWriteMainAdapter);
				myListenWriteMainAdapter.notifyDataSetChanged();
				imageButton_stop.setEnabled(false);
				imageButton_pause.setEnabled(false);
				endTime = System.currentTimeMillis();
				long time = endTime - startTime;
				int totalTime = (int) (time / 1000);
				int hours = totalTime / 3600;
				int minutes = totalTime % 3600 / 60;
				int seconds = totalTime % 3600 % 60;
				Toast.makeText(getApplicationContext() ,"总用时：" + hours + "小时" + minutes + "分" + seconds + "秒" ,Toast.LENGTH_LONG).show();

			}
			else
				start();
		}
	}

	// 开始播放 TODO
	private void start()
	{
		if(currentIndex != 0 && currentIndex != playList.size())
		{
			try
			{
				Thread.sleep(intervalValue * 1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		if(playList.size() > 0 && currentIndex < playList.size())
		{
			String SongPath = playList.get(currentIndex);
			if(mp != null)
			{
				mp.reset();
			}
			try
			{
				play_currentState = PAUSE;
				mp.setDataSource(SongPath);
				mp.prepareAsync();
				mp.setOnPreparedListener(new OnPreparedListener()
				{

					@Override
					public void onPrepared(MediaPlayer mp )
					{
						mp.start();
					}
				});
			}
			catch(Exception e)
			{
				Toast.makeText(this ,"音频出错 已自动跳过" ,Toast.LENGTH_SHORT).show();
				currentIndex += frequencyValue;
				notifyDataAdapter();
				start();
			}
		}
		else
		{
		}
	}

	private Handler handlerView = new Handler();

	@SuppressWarnings("unused")
	private Runnable runnablePlay = new Runnable()
	{
		@Override
		public void run()
		{
			if(playList.size() > 0 && currentIndex < playList.size())
			{
				String SongPath = playList.get(currentIndex);
				mp.reset();
				try
				{
					play_currentState = PAUSE;
					mp.setDataSource(SongPath);
					mp.prepare();
					mp.start();
				}
				catch(Exception e)
				{
					System.out.println(e);
					// Toast.makeText(this ,"音频出错 已自动跳过"
					// ,Toast.LENGTH_SHORT).show();
					System.out.println("音频出错 已自动跳过");
					currentIndex += frequencyValue;
					notifyDataAdapter();
					start();
				}
			}
			else
			{
			}
		}
	};

	public void notifyDataAdapter()
	{
		int currentIndexTrue = (currentIndex + 1) / frequencyValue + (0 == ((currentIndex + 1) % frequencyValue) ? -1 : 0);

		if(currentIndex < playList.size())
		{
			// Log.d("currentIndexLog" ,"currentIndexTrue:" + currentIndexTrue +
			// ":currentIndex:" + currentIndex);
			if(0 == currentIndex)
				myListenWriteMainAdapter.setItemisSelectedMap(0 ,true);
			else
			{
				if(currentIndexTrue > 0 && myListenWriteMainAdapter.getisSelectedAt(currentIndexTrue - 1))
					myListenWriteMainAdapter.setItemisSelectedMap(currentIndexTrue - 1 ,false);

				boolean isSelect = myListenWriteMainAdapter.getisSelectedAt(currentIndexTrue);
				if( !isSelect)
					myListenWriteMainAdapter.setItemisSelectedMap(currentIndexTrue ,true);
			}
			myListenWriteMainAdapter.notifyDataSetChanged();
		}
		else
		{
			myListenWriteMainAdapter.setItemisSelectedMap(currentIndexTrue ,false);
			myListenWriteMainAdapter.notifyDataSetChanged();
		}

	}

	private Runnable runnableView = new Runnable()
	{
		@Override
		public void run()
		{
			notifyDataAdapter();
			handlerView.postDelayed(this ,100);
		}
	};

	@Override
	public boolean onError(MediaPlayer mp , int what , int extra )
	{
		mp.reset();
		return false;
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
				if(progressDialog != null)
				{
					progressDialog.dismiss();
				}

//				try
//				{
//					runnableView.wait();
//				}
//				catch(InterruptedException e)
//				{
//					System.out.println(e);
//				}

				if(mp != null)
				{
					mp.release();
					mp = null;
				}
				// onBackPressed();
				finish();
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
			try
			{
				runnableView.wait();
			}
			catch(InterruptedException e)
			{
				System.out.println(e);
			}

			if(mp != null)
			{
				mp.release();
				mp = null;
			}
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
	public void onConfigurationChanged(Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
		try
		{
			runnableView.wait();
		}
		catch(InterruptedException e)
		{
			System.out.println(e);
		}

		if(mp != null)
		{
			mp.release();
			mp = null;
		}
		super.onDestroy();
	}

}
