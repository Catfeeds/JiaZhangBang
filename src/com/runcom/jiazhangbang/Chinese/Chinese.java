package com.runcom.jiazhangbang.chinese;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenWrite.ListenWriteGameChose;
import com.runcom.jiazhangbang.notification.MyNotification;
import com.runcom.jiazhangbang.setting.Setting;
import com.runcom.jiazhangbang.setting.SettingChose;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.Util;
import com.runcom.jiazhangbang.welcome.Welcome;
import com.umeng.analytics.MobclickAgent;

public class Chinese extends Activity
{
	private Intent intent = new Intent();
	private int grade;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chinese);
		SpeechUtility.createUtility(this ,SpeechConstant.APPID + "=590aeb53");
		firstStartAndSetChose();
	}

	private void firstStartAndSetChose()
	{
		Boolean first = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeyFirstStart ,Util.firstStartSharedPreferencesKeyString ,true);
		if(first)
		{
			intent.putExtra("class" ,Util.FirstStartAndSetChose);
			intent.setClass(getApplicationContext() ,SettingChose.class);
			MySharedPreferences.putValue(getApplicationContext() ,Util.sharedPreferencesKeyFirstStart ,Util.firstStartSharedPreferencesKeyString ,false);
			startActivity(intent);
		}
	}

	/**
	 * 1������
	 * 
	 * @param v
	 */
	public void listenText(View v )
	{
		intent.putExtra("class" ,Util.ListenTextMain);
		intent.setClass(getApplicationContext() ,SettingChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 2��д
	 * 
	 * @param v
	 */
	public void listenAndWrite(View v )
	{
		intent.putExtra("class" ,Util.ListenWriteTips);
		intent.setClass(getApplicationContext() ,SettingChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 3���м��
	 * 
	 * @param v
	 */
	public void reciteText(View v )
	{
		intent.putExtra("class" ,Util.ReciteTextTextChose);
		intent.setClass(getApplicationContext() ,SettingChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 4�ʶ�
	 * 
	 * @param v
	 */
	public void repeat(View v )
	{
		intent.putExtra("class" ,Util.Repeat);
		intent.setClass(getApplicationContext() ,SettingChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 5������
	 * 
	 * @param v
	 */
	public void findNewWords(View v )
	{
		intent.putExtra("class" ,Util.FindNewWords);
		intent.setClass(getApplicationContext() ,SettingChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * ����Ϸ
	 * 
	 * @param v
	 */
	public void playGame(View v )
	{
		intent.setClass(getApplicationContext() ,ListenWriteGameChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 6¼����
	 * 
	 * @param v
	 */
	public void recordText(View v )
	{
		intent.putExtra("class" ,Util.RecordText);
		intent.setClass(getApplicationContext() ,SettingChose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"������������" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
		// notification();
	}

	@SuppressWarnings("unused")
	private void notification()
	{
		MyNotification.myNotification(getApplicationContext());
		intent.putExtra("selected" ,grade);
		intent.setClass(getApplicationContext() ,Welcome.class);
		startActivity(intent);
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.welcome ,menu);//TODO
		getMenuInflater().inflate(R.menu.setting_menu ,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case R.id.main_menu_setting_menu:
				Intent intent = new Intent();
				intent.setClass(getApplicationContext() ,Setting.class);
				startActivity(intent);
				break;
			case android.R.id.home:
				onBackPressed();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// �����ڰ����ؼ������˳�����
	private long exitTime = 0;

	// ��д�����ؼ�
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis() - exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext() ,"�ٰ�һ���˳�����" ,Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				MobclickAgent.onKillProcess(this);
				finish();
				System.exit(0);
			}
			return true;
		}

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
