package com.runcom.jiazhangbang.listenWrite.listenWriteGame;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteGameMain extends Activity
{
	private int course , grade , phase , unit;
	private String [] phraseContent , voiceContent , pinyinContent;
	private ArrayList < ListenWriteGameItemBean > gameItemBeanList ,
	        tempGameItemBeanList;
	private ListenWriteGameItemBean gameItemBean;
	private GridView gridView;
	private ListenWriteGameMainGridViewAdapter listenWriteGameMainGridViewAdapter;
	// private MediaPlayer mediaPlayer;
	private int currentPosition = -1 , lastPosition = -1;
	private Boolean flag;
	private TextView textView_historyScore , textView_currentScore;
	int clickCount = 0;
	private static final String sharedPreferencesKey = "ListenWriteGameMainMenu";
	private static final String sharedPreferencesHistoryScoreHour = "ListenWriteGameMainMenuHistoryScoreHours";
	private static final String sharedPreferencesHistoryScoreMinute = "ListenWriteGameMainMenuHistoryScoreHours";
	private static final String sharedPreferencesHistoryScoreSecond = "ListenWriteGameMainMenuHistoryScoreSeconds";

	private ProgressDialog progressDialog;
	private Timer timer;
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private final int MAX = 0x3f3f3f3f;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_game_main);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "游戏";
		actionbar.setTitle(content);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[Util.PlayGame] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[Util.PlayGame] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[Util.PlayGame] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[Util.PlayGame] ,unit);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		initData();
	}

	/**
	 * 随机指定范围内N个不重复的数
	 * 
	 * @param min
	 *            指定范围最小值
	 * @param max
	 *            指定范围最大值
	 * @param n
	 *            随机数个数
	 */
	private static int [] getRandomNumber(int min , int max , int n )
	{
		if(n > (max - min + 1) || max < min)
		{
			return null;
		}
		int [] result = new int [n];
		int count = 0;
		while(count < n)
		{
			int num = (int) (Math.random() * (max - min)) + min;
			boolean flag = true;
			for(int j = 0 ; j < count ; j ++ )
			{
				if(num == result[j])
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				result[count] = num;
				count ++ ;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param random
	 *            数组
	 * @return 随机数组
	 */
	private static int [] getRandomRandomNumber(int [] random )
	{
		int n = random.length;
		int [] result = new int [n];
		int count = 0;
		while(count < n)
		{
			int num = (int) (Math.random() * n);
			boolean flag = true;
			for(int j = 0 ; j < count ; j ++ )
			{
				if(random[num] == result[j])
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				result[count] = random[num];
				count ++ ;
			}
		}
		return result;
	}

	private void initSimpleModelView()
	{
		clickCount = 0;
		textView_currentScore = (TextView) findViewById(R.id.listen_write_game_main_textView_currentScore);
		textView_historyScore = (TextView) findViewById(R.id.listen_write_game_main_textView_historyScore);
		int historyScoreHour = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreHour ,MAX);
		int historyScoreMinute = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreMinute ,MAX);
		int historyScoreSecond = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreSecond ,MAX);
		// System.out.println(historyScoreHour + "\nhistoryScoreMinute:" +
		// historyScoreMinute + "\nhistoryScoreSecond:" + historyScoreSecond);
		if(MAX == historyScoreHour && MAX == historyScoreMinute && MAX == historyScoreSecond)
		{
			textView_historyScore.setText("00:00:00");
		}
		else
		{
			textView_historyScore.setText(String.format("%1$02d:%2$02d:%3$02d" ,historyScoreHour ,historyScoreMinute ,historyScoreSecond));
		}

		int count = 6;
		tempGameItemBeanList = new ArrayList < ListenWriteGameItemBean >();
		int randomArray[] = getRandomNumber(0 ,gameItemBeanList.size() ,count);
		for(int i = 0 ; i < count ; i ++ )
		{
			tempGameItemBeanList.add(gameItemBeanList.get(randomArray[i]));
		}
		randomArray = getRandomRandomNumber(randomArray);
		for(int i = 0 ; i < count ; i ++ )
		{
			tempGameItemBeanList.add(gameItemBeanList.get(randomArray[i]));
		}
		gridView = (GridView) findViewById(R.id.listen_write_game_main_gridView);
		listenWriteGameMainGridViewAdapter = new ListenWriteGameMainGridViewAdapter(getApplicationContext() , tempGameItemBeanList);
		gridView.setAdapter(listenWriteGameMainGridViewAdapter);
		gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		listenWriteGameMainGridViewAdapter.notifyDataSetChanged();

		progressDialog.dismiss();
		hour = 0;
		minute = 0;
		second = 0;
		recordTime();
		// mediaPlayer = new MediaPlayer();
		gridView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				clickCount ++ ;
				// mediaPlayer.reset();
				// try
				// {
				// mediaPlayer.setDataSource(tempGameItemBeanList.get(position).getVoice());
				// mediaPlayer.prepare();
				// mediaPlayer.start();
				// }
				// catch(Exception e)
				// {
				// System.out.println(e);
				// }
				flag = listenWriteGameMainGridViewAdapter.getisSelectedAt(position);
				if( !flag)
				{
					listenWriteGameMainGridViewAdapter.setItemisSelectedMap(position ,true);
					listenWriteGameMainGridViewAdapter.notifyDataSetChanged();
					if( -1 == lastPosition)
					{
						lastPosition = position;
					}
					else
					{
						currentPosition = position;
						if(tempGameItemBeanList.get(currentPosition).getPhrase() == tempGameItemBeanList.get(lastPosition).getPhrase() || tempGameItemBeanList.get(currentPosition).getPhrase() == tempGameItemBeanList.get(lastPosition).getPinyin())
						{
							tempGameItemBeanList.remove(lastPosition);
							if(lastPosition < currentPosition)
							{
								currentPosition -- ;
							}
							tempGameItemBeanList.remove(currentPosition);
						}
						listenWriteGameMainGridViewAdapter.setItemisSelectedMap(position ,false);
						listenWriteGameMainGridViewAdapter.setItemisSelectedMap(lastPosition ,false);
						listenWriteGameMainGridViewAdapter.notifyDataSetChanged();
						lastPosition = -1;
						currentPosition = -1;
					}
				}
				else
				{
					clickCount -- ;
				}
				// textView_currentScore.setText("当前成绩：" + clickCount);
				if(0 == tempGameItemBeanList.size())
				{
					timer.cancel();
					int clickCountHistoryHour = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreHour ,MAX);
					int clickCountHistoryMinute = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreMinute ,MAX);
					int clickCountHistorySecond = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreSecond ,MAX);
					// System.out.println(clickCountHistoryHour +
					// "\nclickCountHistoryMinute:" + clickCountHistoryMinute +
					// "\nclickCountHistorySecond:" + clickCountHistorySecond);
					AlertDialog.Builder successDialog = new AlertDialog.Builder(ListenWriteGameMain.this);
					successDialog.setMessage("恭喜过关，再次挑战？");
					if(clickCountHistoryHour * 3600 + clickCountHistoryMinute * 60 + clickCountHistorySecond > hour * 3600 + minute * 60 + second)
					{
						textView_historyScore.setText(String.format("%1$02d:%2$02d:%3$02d" ,hour ,minute ,second));
						MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreHour ,hour);
						MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreMinute ,minute);
						MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScoreSecond ,second);
						successDialog.setMessage("打破自己记录了呢，再次挑战？");
					}
					successDialog.setPositiveButton("确定" ,new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog , int which )
						{
							initData();
						}
					});
					successDialog.setNegativeButton("取消" ,new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog , int which )
						{
							onBackPressed();
						}
					});
					successDialog.setCancelable(false);
					successDialog.show();
				}
			}
		});
	}

	private void initData()
	{
		TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,course + "");
		map.put("grade" ,grade + "");
		map.put("phase" ,phase + "");
		map.put("unit" ,0 == unit ? -- unit + "" : unit + "");
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
					initSimpleModelView();
				}
				else
					if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsMissingResourceString ,Toast.LENGTH_SHORT).show();
						finish();
					}
					else
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_SHORT).show();
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
				int leng = jsonArray.length();
				if(leng <= 0)
				{
					return Util.okHttpUtilsResultExceptionStringValue;
				}
				phraseContent = new String [leng];
				voiceContent = new String [leng];
				pinyinContent = new String [leng];
				gameItemBeanList = new ArrayList < ListenWriteGameItemBean >();
				for(int i = 0 ; i < leng ; i ++ )
				{
					gameItemBean = new ListenWriteGameItemBean();
					phlistJsonObject = new JSONObject(jsonArray.getString(i));
					phraseContent[i] = phlistJsonObject.getString("phrase");
					voiceContent[i] = Util.RESOURCESERVER + phlistJsonObject.getString("voice");
					pinyinContent[i] = phlistJsonObject.getString("pinyin");
					gameItemBean.setPhrase(phraseContent[i]);
					gameItemBean.setVoice(voiceContent[i]);
					gameItemBean.setPinyin(pinyinContent[i]);
					gameItemBeanList.add(gameItemBean);
				}
				return result;
			}
		});
	}

	// 计时器异步更新界面
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg )
		{
			textView_currentScore.setText(String.format("%1$02d:%2$02d:%3$02d" ,hour ,minute ,second));
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.listen_write_game_main_textView_currentScore:
				if(Util.debug)
				{
					hour = 0;
					minute = 0;
					second = 0;
				}
				// case R.id.listen_write_game_main_textView_historyScore:
				// if(Util.debug)
				// {
				// hour = 11;
				// minute = 11;
				// second = 11;
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,sharedPreferencesHistoryScoreHour
				// ,hour);
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,sharedPreferencesHistoryScoreMinute
				// ,minute);
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,sharedPreferencesHistoryScoreSecond
				// ,second);
				// textView_historyScore.setText(String.format("%1$02d:%2$02d:%3$02d"
				// ,hour ,minute ,second));
				// }
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		getMenuInflater().inflate(R.menu.listen_write_game_main_menu ,menu);
		return super.onCreateOptionsMenu(menu);
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
