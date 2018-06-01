package com.runcom.jiazhangbang.chinese;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.setting.Setting;
import com.runcom.jiazhangbang.setting.SettingChoose;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

@SuppressLint("Override")
public class Chinese extends Activity
{
	private Intent intent = new Intent();

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chinese);

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
				new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						if(Build.VERSION.SDK_INT >= 23)
						{
							checkPermission();
						}
						else
						{
							intentSetting();
						}
						// directRequestPermisssion(Manifest.permission.WRITE_EXTERNAL_STORAGE
						// ,0);
						// checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE
						// ,0);
					}
				}).start();
				break;
			case android.R.id.home:
				Toast.makeText(getApplicationContext() ,"home" ,Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void intentSetting()
	{
		Intent intent = new Intent();
		intent.setClass(getApplicationContext() ,Setting.class);
		startActivity(intent);
	}

	private void checkPermission()
	{
		// TODO Auto-generated method stub
		if(ContextCompat.checkSelfPermission(this ,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this ,new String []
			{ Manifest.permission.WRITE_EXTERNAL_STORAGE } ,007);
		}
		else
		{
			intentSetting();
		}
	}

	// @Override
	// public void onRequestPermissionsResult(int requestCode , String []
	// permissions , int [] grantResults )
	// {
	// if(ContextCompat.checkSelfPermission(this
	// ,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
	// PackageManager.PERMISSION_GRANTED)
	// {
	// intentSetting();
	// }
	// else
	// {
	// Toast.makeText(this ,"未获得读取本地权限" ,Toast.LENGTH_SHORT).show();
	// }
	// }

	protected void checkPermission(String permission , int resultCode )
	{
		if(ContextCompat.checkSelfPermission(this ,permission) != PackageManager.PERMISSION_GRANTED)
		{
			// 没有权限
			Log.i("info" ,"1,需要申请权限。");
			if(ActivityCompat.shouldShowRequestPermissionRationale(this ,permission))
			{
				// TODO 用户未拒绝过 该权限 shouldShowRequestPermissionRationale返回false
				// 用户拒绝过一次则一直返回true
				// 注意小米手机 则一直返回时 false
				Log.i("info" ,"3,用户已经拒绝过一次该权限，需要提示用户为什么需要该权限。\n" + "此时shouldShowRequestPermissionRationale返回：" + ActivityCompat.shouldShowRequestPermissionRationale(this ,permission));
				// 解释为什么 需要该权限的 对话框
				showMissingPermissionDialog();
			}
			else
			{
				// 申请授权。
				ActivityCompat.requestPermissions(this ,new String []
				{ permission } ,resultCode);
				Log.i("info" ,"2,用户拒绝过该权限，或者用户从未操作过该权限，开始申请权限。-\n" + "此时shouldShowRequestPermissionRationale返回：" + ActivityCompat.shouldShowRequestPermissionRationale(this ,permission));
			}
		}
		else
		{
			// 权限 已经被准许 you can do something
			permissionHasGranted();
			Log.i("info" ,"7,已经被用户授权过了=可以做想做的事情了==打开联系人界面");
		}
	}

	protected void permissionHasGranted()
	{
		Toast.makeText(getApplicationContext() ,"权限已经被准许了,你可以做你想做的事情" ,Toast.LENGTH_SHORT).show();
	}

	int clicki = 0;

	/**
	 * 提示用户的 dialog
	 */
	protected void showMissingPermissionDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("当前应用缺少联系人权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。");
		// 拒绝, 退出应用
		builder.setNegativeButton("关闭" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Log.i("info" ,"8--权限被拒绝,此时不会再回调onRequestPermissionsResult方法");
			}
		});
		builder.setPositiveButton("设置" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Log.i("info" ,"4,需要用户手动设置，开启当前app设置界面");
				startAppSettings();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	/**
	 * 打开 App设置界面
	 */
	private void startAppSettings()
	{
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

	/**
	 * 直接 请求 权限
	 * 
	 * @param permission
	 *            权限
	 * @param resultCode
	 *            结果码
	 */
	protected void directRequestPermisssion(String permission , int resultCode )
	{
		ActivityCompat.requestPermissions(this ,new String []
		{ permission } ,resultCode);
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
				// new ShareUtils(this).shareMultipleLink(Util.update
				// ,getResources().getString(R.string.app_name) ," " ,null
				// ,R.drawable.ic_launcher);
				Toast.makeText(getApplicationContext() ,"再按一次退出程序" ,Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				MobclickAgent.onKillProcess(this);
				finish();
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
	protected void onActivityResult(int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult(requestCode ,resultCode ,data);
		UMShareAPI.get(this).onActivityResult(requestCode ,resultCode ,data);
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
