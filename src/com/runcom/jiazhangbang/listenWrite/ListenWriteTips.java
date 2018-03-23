package com.runcom.jiazhangbang.listenWrite;

import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.setting.ListenWriteTipsPlaySetting;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteTips extends Activity
{
	private Intent intent;
	private int selected , unit;
	private int phase = 1;// 1����ѧ��;2����ѧ��
	private int intervalValue , frequencyValue;
	private int counts;
	private TextView textView_information , textView_start , textView_reset;
	private final int WORDSTIME = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_tips);

		intent = getIntent();
		selected = intent.getIntExtra("selected" ,1);// �꼶
		phase = intent.getIntExtra("phase" ,1);// ���²�
		unit = intent.getIntExtra("id" ,1);// ��Ԫ
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "��д " + selected + "�꼶�ϲ��" + unit + "��Ԫ";
		if(2 == phase)
			content = "��д " + selected + "�꼶�²��" + unit + "��Ԫ";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		initData();
	}

	private void initData()
	{
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
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
					String [] config = arg0.split("#");
					// for(int j = 0 ; j < config.length ; j ++ )
					// {
					// System.out.println(config[j]);
					// }
					if( !config[0].equals("0"))
					{
						textView_start.setEnabled(false);
						textView_reset.setEnabled(false);
						// Toast.makeText(getApplicationContext() ,config[1]
						// ,Toast.LENGTH_LONG).show();
					}
					else
						initView();
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					// System.out.println("***********************");
					String response = arg0.body().string().trim();
					JSONObject jsonObject = new JSONObject(response);
					String result = jsonObject.getString("result");
					String mesg = jsonObject.getString("mesg");
					JSONArray jsonArray = jsonObject.getJSONArray("attr");
					counts = jsonArray.length();
					// JSONObject phraseJsonObject = new
					// JSONObject(jsonArray.getString(5));
					// String phrase = phraseJsonObject.getString("phrase");
					// System.out.println("counts: " + counts + "\njson: " +
					// jsonObject.toString() + "\nphrase: " + phrase);
					return result + "#" + mesg;
				}
			});
		}
	}

	private void initView()
	{
		// ��������������
		intervalValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteInterval" ,1);
		// ÿ�������Ķ�����
		frequencyValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteFrequency" ,1);

		textView_information = (TextView) findViewById(R.id.listen_write_tips_informations);
		textView_start = (TextView) findViewById(R.id.listen_write_tips_start);
		textView_start.setEnabled(true);
		textView_reset = (TextView) findViewById(R.id.listen_write_tips_reset);
		textView_reset.setEnabled(true);
		int totalTime = (WORDSTIME + intervalValue) * counts * frequencyValue;
		int hours = totalTime / 3600;
		int minutes = totalTime % 3600 / 60;
		int seconds = totalTime % 3600 % 60;
		String contents = "������д\n�ܹ�" + counts + "������\n�����ʶ����" + intervalValue + "��\nÿ�������ʶ�" + frequencyValue + "��\n��Լ��ʱ" + totalTime + "��\n��" + hours + "ʱ" + minutes + "��" + seconds + "��";

		textView_information.setText(contents);

		textView_start.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intent = new Intent();
				intent.putExtra("selected" ,selected);
				intent.putExtra("phase" ,phase);
				intent.putExtra("units" ,unit);
				intent.setClass(getApplicationContext() ,ListenWriteMain.class);
				if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
				{
					Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
					startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				}
				else
					startActivity(intent);
			}
		});

		textView_reset.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intent = new Intent();
				intent.putExtra("selected" ,selected);
				intent.putExtra("units" ,unit);
				intent.setClass(getApplicationContext() ,ListenWriteTipsPlaySetting.class);
				if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
				{
					Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
					startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				}
				else
					startActivity(intent);
			}
		});
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
				// Toast.makeText(getApplicationContext()
				// ,"onOptionsItemSelected..." ,Toast.LENGTH_SHORT).show();
				// System.out.println("onOptionsItemSelected...");
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// ��д�����ؼ��˳�����
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			// Toast.makeText(getApplicationContext() ,"onKeyDown..."
			// ,Toast.LENGTH_SHORT).show();
			// System.out.println("onKeyDown...");
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		// Toast.makeText(getApplicationContext() ,"onResume..."
		// ,Toast.LENGTH_SHORT).show();
		// System.out.println("onResume...");
		initView();
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
