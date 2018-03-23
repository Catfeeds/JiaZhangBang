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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
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
	private Intent intent;
	private int selected , phase , unit;
	private int intervalValue , frequencyValue;
	private int leng;
	private String [] phraseContent , voiceContent;
	private MyListenWriteMainAdapter myListenWriteMainAdapter;
	private ArrayList < NewWords > newWordsList = new ArrayList < NewWords >();
	private ArrayList < NewWords > newWordsListStar = new ArrayList < NewWords >();
	private ArrayList < String > playList = new ArrayList < String >();
	private NewWords newWords , newWordsStar;

	private GridView gridView;
	private TextView textView_pause , textView_stop;

	private int WAIT_TIME = 5;
	private Timer timer = new Timer();

	private int currentIndex = 0;
	private MediaPlayer mp;

	// 定义当前播放器的状态
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;

	private int play_currentState = IDLE; // 当前播放器的状态

	// private ExecutorService es = Executors.newSingleThreadExecutor();

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_main);

		intent = getIntent();
		selected = intent.getIntExtra("selected" ,1);
		phase = intent.getIntExtra("phase" ,1);
		unit = intent.getIntExtra("units" ,1);

		// 两个词语间隔秒数
		intervalValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteInterval" ,1);
		// 每个词语阅读次数
		frequencyValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteFrequency" ,1);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听写 " + selected + "年级上学期第" + unit + "单元";
		if(2 == phase)
			content = "听写 " + selected + "年级下学期第" + unit + "单元";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		timer.schedule(task ,0 ,1000);
		initNewWordsData();
	}

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
					if(WAIT_TIME > 2)
						Toast.makeText(getApplicationContext() ,"倒计时 " + (WAIT_TIME - 2) ,1).show();
					WAIT_TIME -- ;
					if(WAIT_TIME <= 0)
					{
						play();
						startTime = System.currentTimeMillis();
						timer.cancel();
						task.cancel();
					}
				}
			});
		}
	};

	private void initNewWordsData()
	{
		// Log.d("log*************2" ,"classes : " + selected + "\nunits : " +
		// units + "\nuri : " + Util.SERVERADDRESS_listenWriteMain + "&classes="
		// + selected + "&units=" + units);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			final TreeMap < String , String > map = Util.getMap(getApplicationContext());
			map.put("course" ,Util.ChineseCourse);
			map.put("grade" ,selected + "");
			map.put("phase" ,phase + "");
			map.put("unit" ,unit + "");
			System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{
				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					if(Integer.valueOf(arg0) > 0)
						initPlayData();
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					// Log.d("log*************1" ,"classes : " + selected +
					// "\nunits : " + units + "\nuri : " +
					// Util.SERVERADDRESS_listenWriteMain + "&classes=" +
					// selected + "&units=" + units);
					String response = arg0.body().string().trim();
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("attr");
					JSONObject phlistJsonObject = null;
					leng = jsonArray.length();
					phraseContent = new String [leng];
					voiceContent = new String [leng];
					// System.out.println("*************************************************start...");
					for(int i = 0 ; i < leng ; i ++ )
					{
						// System.out.println("\n*************************************************start00..."
						// + i);
						phlistJsonObject = new JSONObject(jsonArray.getString(i));
						// System.out.println("\n*************************************************start01..."
						// + i);
						phraseContent[i] = phlistJsonObject.getString("phrase");
						// System.out.println("\n*************************************************start02..."
						// + i);
						voiceContent[i] = phlistJsonObject.getString("voice");
						// System.out.println("\n" + phraseContent[i] + "\t" +
						// voiceContent[i]);
					}
					// contents = jsonObject.getString("contents");

					// Log.d("log*************3" ,"leng : " + leng +
					// "\ncontents : " + contents);
					// Toast.makeText(getApplicationContext() ,"leng : " + leng
					// + "\ncontents : " + contents ,Toast.LENGTH_SHORT).show();

					return leng + "";
					// return null;
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

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				Toast.makeText(getApplicationContext() ,(newWordsList.get(position).getId() + 1) + "\n" + newWordsList.get(position).getName() ,Toast.LENGTH_SHORT).show();
			}
		});

		textView_pause = (TextView) findViewById(R.id.listen_write_main_textView_pause);
		textView_stop = (TextView) findViewById(R.id.listen_write_main_textView_stop);
		textView_pause.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				play();
			}
		});

		textView_stop.setOnClickListener(new OnClickListener()
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
						if(mp != null || mp.isPlaying())
						{
							mp.stop();
							mp.release();
						}
						// Log.d("log*********" ,"3***" +
						// newWordsList.toString());
						myListenWriteMainAdapter = new MyListenWriteMainAdapter(getApplicationContext() , newWordsList);
						gridView.setAdapter(myListenWriteMainAdapter);
						myListenWriteMainAdapter.notifyDataSetChanged();
						textView_stop.setEnabled(false);
						textView_pause.setEnabled(false);
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

				// Toast.makeText(getApplicationContext() ,"结束。。。"
				// ,Toast.LENGTH_SHORT).show();
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
			case IDLE:
				// threadPlay.start();
				start();
				break;
			case PAUSE:
				mp.pause();
				// btnPlay.setImageResource(R.drawable.play_pause);
				textView_pause.setText("继续");
				play_currentState = START;
				break;
			case START:
				// Log.d("LOG" ,"start02" + currentIndex + 1 + ":" +
				// playList.size());
				mp.start();
				textView_pause.setText("暂停");
				// btnPlay.setImageResource(R.drawable.play_start);
				play_currentState = PAUSE;
				break;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp )
	{
		++ currentIndex;
		if(currentIndex >= playList.size())
		{
			// if(mp != null || mp.isPlaying())
			// {
			// mp.release();
			// mp.stop();
			// }
			Toast.makeText(getApplicationContext() ,"听写完毕" ,Toast.LENGTH_SHORT).show();
			myListenWriteMainAdapter = new MyListenWriteMainAdapter(getApplicationContext() , newWordsList);
			gridView.setAdapter(myListenWriteMainAdapter);
			myListenWriteMainAdapter.notifyDataSetChanged();
			textView_stop.setEnabled(false);
			textView_pause.setEnabled(false);
			endTime = System.currentTimeMillis();
			long time = endTime - startTime;
			int totalTime = (int) (time / 1000);
			int hours = totalTime / 3600;
			int minutes = totalTime % 3600 / 60;
			int seconds = totalTime % 3600 % 60;
			Toast.makeText(getApplicationContext() ,"总用时：" + hours + "小时" + minutes + "分" + seconds + "秒" ,Toast.LENGTH_SHORT).show();

		}
		else
			// if(threadPlay.isAlive() || threadPlay != null)
			// {
			// threadPlay.stop();
			// threadPlay.start();
			// }
			start();
	}

	@SuppressWarnings("unused")
	private Thread threadPlay = new Thread(new Runnable()
	{

		@Override
		public void run()
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
			// Log.d("LOG" ,"start03" + currentIndex + 1 + ":" +
			// playList.size());
			if(playList.size() > 0 && currentIndex < playList.size())
			{
				String SongPath = playList.get(currentIndex);
				SongPath = "http://106.14.208.25:8080/wgcwgc/001.wav";
				mp.reset();
				try
				{
					// Log.d("LOG" ,"start04" + currentIndex);
					mp.setDataSource(SongPath);
					// Log.d("LOG" ,"start05:" + SongPath);
					mp.prepare();
					mp.start();
					// Log.d("LOG" ,SongPath);
					// es.execute((Runnable) this);
					// btnPlay.setImageResource(R.drawable.play_start);
					play_currentState = PAUSE;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Log.d("LOG" ,"bug了");
				}
			}
			else
			{
				Toast.makeText(getApplicationContext() ,"播放完毕" ,Toast.LENGTH_SHORT).show();
			}
		}
	});

	// 开始播放
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
		// Log.d("LOG" ,"start03" + currentIndex + 1 + ":" + playList.size());
		if(playList.size() > 0 && currentIndex < playList.size())
		{
			String SongPath = playList.get(currentIndex);
			// SongPath = "http://106.14.208.25:8080/wgcwgc/001.wav";
			mp.reset();
			try
			{
				// Log.d("LOG" ,"start04" + currentIndex);
				mp.setDataSource(SongPath);
				// Log.d("LOG" ,"start05:" + SongPath);
				mp.prepare();
				mp.start();
				// Log.d("LOG" ,SongPath);
				// es.execute((Runnable) this);
				// btnPlay.setImageResource(R.drawable.play_start);
				play_currentState = PAUSE;
			}
			catch(Exception e)
			{
				// e.printStackTrace();
				Toast.makeText(getApplicationContext() ,"音频出错 已自动跳过" ,Toast.LENGTH_SHORT).show();
				currentIndex += frequencyValue;
				notifyDataAdapter();
				start();
				Log.d("LOG" ,"bug了");
			}
		}
		else
		{
			Toast.makeText(this ,"播放完毕" ,Toast.LENGTH_SHORT).show();
		}
	}

	private Handler handlerView = new Handler();
	private Handler handlerPlay = new Handler();

	@SuppressWarnings("unused")
	private Runnable runnablePlay = new Runnable()
	{
		@Override
		public void run()
		{
			if(playList.size() > 0 && currentIndex < playList.size())
			{
				String SongPath = playList.get(currentIndex);
				// Log.d("LOG" ,SongPath);
				// SongPath = "http://106.14.208.25:8080/wgcwgc/001.wav";
				mp.reset();
				try
				{
					mp.setDataSource(SongPath);
					mp.prepare();
					mp.start();
					play_currentState = PAUSE;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Log.d("LOG" ,"bug了");
				}
			}
			else
			{
				Toast.makeText(getApplicationContext() ,"播放完毕" ,Toast.LENGTH_SHORT).show();
			}

			handlerPlay.postDelayed(this ,intervalValue * 1000);
		}
	};

	public void notifyDataAdapter()
	{
		int currentIndexTrue = (currentIndex + 1) / frequencyValue + (0 == ((currentIndex + 1) % frequencyValue) ? -1 : 0);

		if(currentIndex < playList.size())
		{
			Log.d("currentIndexLog" ,"currentIndexTrue:" + currentIndexTrue + ":currentIndex:" + currentIndex);
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
				if(mp != null || mp.isPlaying())
					mp.stop();
				try
				{
					runnableView.wait();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				// Toast.makeText(getApplicationContext()
				// ,"onOptiosItemSelected..." ,Toast.LENGTH_SHORT).show();
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
			if(mp != null || mp.isPlaying())
				mp.stop();

			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			try
			{
				runnableView.wait();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			// Toast.makeText(getApplicationContext() ,"onKeyDown..."
			// ,Toast.LENGTH_SHORT).show();
			ListenWriteMain.this.finish();
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

}
