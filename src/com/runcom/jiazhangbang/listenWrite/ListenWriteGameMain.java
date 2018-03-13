package com.runcom.jiazhangbang.listenWrite;

import java.util.ArrayList;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.iflytek.voice.Text2Speech;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteGameMain extends Activity
{
	private int degree , grade , phase , unit;
	private Intent intent = null;
	private String [] phraseContent , voiceContent , pinyinContent;
	private ArrayList < ListenWriteGameItemBean > gameItemBeanList ,
	        tempGameItemBeanList;
	private ListenWriteGameItemBean gameItemBean;
	private GridView gridView;
	private ListenWriteGameMainGridViewAdapter listenWriteGameMainGridViewAdapter;
	private MediaPlayer mediaPlayer;
	private int currentPosition = -1 , lastPosition = -1;
	private Boolean flag;
	private TextView textView_historyScore , textView_currentScore;
	private int clickCount = 0;
	private static final String sharedPreferencesKey = "ListenWriteGameMainMenu";
	private static final String sharedPreferencesHistoryScore = "ListenWriteGameMainMenuHistoryScore";

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

		intent = getIntent();
		degree = intent.getIntExtra("degree" ,1);
		grade = intent.getIntExtra("grade" ,1);
		phase = intent.getIntExtra("phase" ,1);
		unit = intent.getIntExtra("unit" ,1);
		String str = "degree:" + degree + "\tgrade:" + grade + "\tphase:" + phase + "\tunit:" + unit;
		System.out.println(str);

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
		textView_historyScore = (TextView) findViewById(R.id.listen_write_game_main_textView_historyScore);
		int historyScore = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScore ,99);
		textView_historyScore.setText("历史成绩：" + historyScore);
		textView_currentScore = (TextView) findViewById(R.id.listen_write_game_main_textView_currentScore);

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
		mediaPlayer = new MediaPlayer();
		gridView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				clickCount ++ ;
				String content = "phrase:" + tempGameItemBeanList.get(position).getPhrase() + "\nvoice:" + tempGameItemBeanList.get(position).getVoice() + "\npinyin:" + tempGameItemBeanList.get(position).getPinyin();
				System.out.println(content);
				// Toast.makeText(getApplicationContext() ,content
				// ,Toast.LENGTH_SHORT).show();
				mediaPlayer.reset();
				try
				{
					mediaPlayer.setDataSource(tempGameItemBeanList.get(position).getVoice());
					mediaPlayer.prepare();
					mediaPlayer.start();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
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
							// System.out.println("删除" + lastPosition + "和" +
							// currentPosition);
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
					// System.out.println("重复");
					clickCount -- ;
				}
				textView_currentScore.setText("当前成绩：" + clickCount);
				if(0 == tempGameItemBeanList.size())
				{
					new Text2Speech(getApplicationContext() , "好厉害哟").play();
					// Toast.makeText(getApplicationContext()
					// ,"恭喜恭喜   ！！！\r\n过关了" ,Toast.LENGTH_SHORT).show();
					int clickCountHistory = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScore ,999);
					// System.out.println(clickCountHistory + "\n" +
					// clickCount);
					if(clickCount < clickCountHistory)
					{
						textView_historyScore.setText("历史成绩：" + clickCount);
						MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScore ,clickCount);
						// Toast.makeText(getApplicationContext()
						// ,"恭喜恭喜  ！！！\r\n打破自己记录了" ,Toast.LENGTH_SHORT).show();
					}

					AlertDialog.Builder successDialog = new AlertDialog.Builder(ListenWriteGameMain.this);
					successDialog.setMessage("恭喜过关咯，再次挑战？");
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
					successDialog.show();
				}
			}
		});
	}

	private void initData()
	{
		TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,Util.ChineseCourse);
		map.put("grade" ,grade + "");
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
				int leng = Integer.valueOf(arg0);
				if(leng > 0)
				{
					// System.out.println("连接服务器成功");
					if(0 == degree)
					{
						initSimpleModelView();
					}
					else
						if(1 == degree)
						{
							initMediumModeView();
						}
						else
							if(2 == degree)
							{
								initHardModeView();
							}

				}
				else
				{
					// System.out.println("连接服务器失败");
					// Toast.makeText(getApplicationContext() ,"连接服务器失败"
					// ,Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				String result = jsonObject.getString("result");
				JSONArray jsonArray = jsonObject.getJSONArray("attr");
				JSONObject phlistJsonObject = null;
				int leng = jsonArray.length();
				phraseContent = new String [leng];
				voiceContent = new String [leng];
				pinyinContent = new String [leng];
				String content = "";
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
					content = i + "\t" + phraseContent[i] + "\t" + voiceContent[i] + "\t" + pinyinContent[i];
					System.out.println(content);
				}
				return result + leng + "";
			}
		});
	}

	private void initHardModeView()
	{
		// TODO Auto-generated method stub

	}

	private void initMediumModeView()
	{
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	private void initPinYin()
	{
		TreeMap < String , String > map = null;
		int length = phraseContent.length;
		pinyinContent = new String [length];
		for(int i = 0 ; i < length ; i ++ )
		{
			map = Util.getMap(getApplicationContext());
			map.put("course" ,Util.ChineseCourse);
			map.put("phrase" ,phraseContent[i]);
			map.put("flag" ,"1");
			map.put("blank" ,"1");
			System.out.println(Util.REALSERVER + "getpinyin.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getpinyin.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{

				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					if( !arg0.equals("0"))
					{
						Toast.makeText(getApplicationContext() ,"连接服务器失败" ,Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					String response = arg0.body().string().trim();
					JSONObject jsonObject = new JSONObject(response);
					String result = jsonObject.getString("result");
					String pinyin = jsonObject.getString("pinyin");
					System.out.println(":" + pinyin);
					return result;
				}
			});

			pinyinContent[i] = "";

			if(i == length - 1)
			{
				initLayout();
			}

		}
	}

	private void initLayout()
	{
		System.out.println("end");
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

}
