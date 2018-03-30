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
		String content = "��Ϸ";
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
	 * ���ָ����Χ��N�����ظ�����
	 * 
	 * @param min
	 *            ָ����Χ��Сֵ
	 * @param max
	 *            ָ����Χ���ֵ
	 * @param n
	 *            ���������
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
	 *            ����
	 * @return �������
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
		// System.out.println("gegezhixingle**************************");
		clickCount = 0;
		textView_historyScore = (TextView) findViewById(R.id.listen_write_game_main_textView_historyScore);
		int historyScore = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScore ,99);
		textView_historyScore.setText("��ʷ�ɼ���" + historyScore);
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
		// for(int i = 0 ; i < count * 2 ; i ++ )
		// {
		// System.out.println(tempGameItemBeanList.get(i).getPhrase());
		// }
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
				// String content = "phrase:" +
				// tempGameItemBeanList.get(position).getPhrase() + "\nvoice:" +
				// tempGameItemBeanList.get(position).getVoice() + "\npinyin:" +
				// tempGameItemBeanList.get(position).getPinyin();
				// System.out.println(content);
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
							// System.out.println("ɾ��" + lastPosition + "��" +
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
					// System.out.println("�ظ�");
					clickCount -- ;
				}
				textView_currentScore.setText("��ǰ�ɼ���" + clickCount);
				if(0 == tempGameItemBeanList.size())
				{
					new Text2Speech(getApplicationContext() , "������Ӵ").play();
					// Toast.makeText(getApplicationContext()
					// ,"��ϲ��ϲ   ������\r\n������" ,Toast.LENGTH_SHORT).show();
					int clickCountHistory = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScore ,999);
					// System.out.println(clickCountHistory + "\n" +
					// clickCount);
					if(clickCount < clickCountHistory)
					{
						textView_historyScore.setText("��ʷ�ɼ���" + clickCount);
						MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesHistoryScore ,clickCount);
						// Toast.makeText(getApplicationContext()
						// ,"��ϲ��ϲ  ������\r\n�����Լ���¼��" ,Toast.LENGTH_SHORT).show();
					}

					AlertDialog.Builder successDialog = new AlertDialog.Builder(ListenWriteGameMain.this);
					successDialog.setMessage("��ϲ���ؿ����ٴ���ս��");
					successDialog.setPositiveButton("ȷ��" ,new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog , int which )
						{
							initData();
						}
					});
					successDialog.setNegativeButton("ȡ��" ,new DialogInterface.OnClickListener()
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
				Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
				finish();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
				{
					if(1 == degree)
					{
						initSimpleModelView();
					}
					else
						if(2 == degree)
						{
							initMediumModeView();
						}
						else
							if(3 == degree)
							{
								initHardModeView();
							}
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
				JSONArray jsonArray = jsonObject.getJSONArray("attr");
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

	private void initHardModeView()
	{
		// TODO Auto-generated method stub

	}

	private void initMediumModeView()
	{
		// TODO Auto-generated method stub

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

	// ��д�����ؼ��˳�����
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
