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
import com.runcom.jiazhangbang.mainActivity.Update;
import com.runcom.jiazhangbang.notification.MyNotification;
import com.runcom.jiazhangbang.setting.Setting;
import com.runcom.jiazhangbang.setting.SettingChoose;
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
		Update.update(Chinese.this ,true);
	}

	/**
	 * 1听课文
	 * 
	 * @param v
	 */
	public void listenText(View v )
	{
		intent.putExtra("class" ,Util.ListenTextMain);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 2听写
	 * 
	 * @param v
	 */
	public void listenWrite(View v )
	{
		intent.putExtra("class" ,Util.ListenWriteTips);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 3背诵检查
	 * 
	 * @param v
	 */
	public void reciteText(View v )
	{
		intent.putExtra("class" ,Util.ReciteTextTextChoose);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 4朗读
	 * 
	 * @param v
	 */
	public void repeat(View v )
	{
		intent.putExtra("class" ,Util.Repeat);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 5查生词
	 * 
	 * @param v
	 */
	public void findNewWords(View v )
	{
		intent.putExtra("class" ,Util.FindNewWords);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 6玩游戏
	 * 
	 * @param v
	 */
	public void playGame(View v )
	{
		intent.putExtra("class" ,Util.playGame);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		// intent.setClass(getApplicationContext()
		// ,ListenWriteGameChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			startActivity(intent);
		}
	}

	/**
	 * 7录课文
	 * 
	 * @param v
	 */
	public void recordText(View v )
	{
		intent.putExtra("class" ,Util.RecordText);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
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

	// 两秒内按返回键两次退出程序
	private long exitTime = 0;

	// 重写按返回键
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis() - exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext() ,"再按一次退出程序" ,Toast.LENGTH_SHORT).show();
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
