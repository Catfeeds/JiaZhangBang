package com.runcom.jiazhangbang.chinese;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.lrcView.Welcome;
import com.runcom.jiazhangbang.notification.MyNotification;
import com.runcom.jiazhangbang.setting.Setting;
import com.runcom.jiazhangbang.setting.SettingChoose;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.commonsdk.UMConfigure;

public class Chinese extends Activity
{
	private Intent intent = new Intent();
	private int grade;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chinese);
		SpeechUtility.createUtility(this ,SpeechConstant.APPID + "=590aeb53");
		UMConfigure.init(this ,"58a3f9d6b27b0a332e001956" ,"wgcwgc75" ,UMConfigure.DEVICE_TYPE_PHONE ,"cd79ec4eb5e09d69b30139b4f03a7cb0");
		MobclickAgent.onProfileSignIn("123456890");
		UMConfigure.setLogEnabled(true);
		MobclickAgent.setScenarioType(this ,EScenarioType.E_DUM_NORMAL);
		UMConfigure.setEncryptEnabled(true);
		// Update.update(Chinese.this ,true);

		// Window window = this.getWindow();
		// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//
		// ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
		// View statusBarView = new View(window.getContext());
		// int statusBarHeight = getStatusBarHeight(window.getContext());
		// FrameLayout.LayoutParams params = new
		// FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT ,
		// statusBarHeight);
		// params.gravity = Gravity.TOP;
		// statusBarView.setLayoutParams(params);
		// statusBarView.setBackgroundColor(R.color.white);
		// decorViewGroup.addView(statusBarView);
	}

	@SuppressWarnings("unused")
	private static int getStatusBarHeight(Context context )
	{
		int statusBarHeight = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height" ,"dimen" ,"android");
		if(resourceId > 0)
		{
			statusBarHeight = res.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
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
		intent.putExtra("class" ,Util.PlayGame);
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

	/**
	 * setting
	 */
	public void setting(View v )
	{
		Intent intent = new Intent();
		intent.setClass(getApplicationContext() ,Setting.class);
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	private void notification()
	{
		MyNotification.myNotification(getApplicationContext());
		intent.putExtra("selected" ,grade);
		intent.setClass(getApplicationContext() ,Welcome.class);
		startActivity(intent);
		// TODO myNotification
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.setting_menu ,menu);
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
