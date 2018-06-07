package com.runcom.jiazhangbang.chinese;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.setting.Setting;
import com.runcom.jiazhangbang.setting.SettingChoose;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.PermissionUtil;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

public class Chinese extends Activity
{
	private Intent intent = new Intent();

	// private String [] mPermissionList = new String []
	// { Manifest.permission.ACCESS_NETWORK_STATE,
	// Manifest.permission.ACCESS_WIFI_STATE,
	// Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.INTERNET,
	// Manifest.permission.MODIFY_AUDIO_SETTINGS,
	// Manifest.permission.MOUNT_FORMAT_FILESYSTEMS,
	// Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
	// Manifest.permission.READ_EXTERNAL_STORAGE,
	// Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO,
	// Manifest.permission.VIBRATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
	// Manifest.permission.WRITE_SETTINGS };

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chinese);
		new PermissionUtil(this , Manifest.permission.WRITE_EXTERNAL_STORAGE);
		// if(Build.VERSION.SDK_INT >= 23)
		// {
		// Toast.makeText(getApplicationContext() ,"0"
		// ,Toast.LENGTH_SHORT).show();
		//
		// ActivityCompat.requestPermissions(this ,mPermissionList ,0);
		// checkPermission(mPermissionList ,0);
		// }
		Util.SetResUrlHead(getApplicationContext());
	}

	/**
	 * 1������
	 * 
	 * @param v
	 */
	public void listenText(View v )
	{
		intent.putExtra("class" ,Util.ListenTextMain);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
	public void listenWrite(View v )
	{
		intent.putExtra("class" ,Util.ListenWriteTips);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
		intent.putExtra("class" ,Util.ReciteTextTextChoose);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
	 * 6����Ϸ
	 * 
	 * @param v
	 */
	public void playGame(View v )
	{
		intent.putExtra("class" ,Util.PlayGame);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
	 * 7¼����
	 * 
	 * @param v
	 */
	public void recordText(View v )
	{
		intent.putExtra("class" ,Util.RecordText);
		intent.setClass(getApplicationContext() ,SettingChoose.class);
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
				Intent intent = new Intent();
				intent.setClass(getApplicationContext() ,Setting.class);
				startActivity(intent);
				break;
			case android.R.id.home:
				Toast.makeText(getApplicationContext() ,"home" ,Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public void onRequestPermissionsResult(int requestCode , String []
	// permissions , int [] grantResults )
	// {
	// if(ContextCompat.checkSelfPermission(this
	// ,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
	// PackageManager.PERMISSION_GRANTED)
	// {
	// }
	// else
	// {
	// Toast.makeText(this ,"δ��ö�ȡ����Ȩ��" ,Toast.LENGTH_SHORT).show();
	// }
	// }

	/**
	 * ���Ȩ���Ƿ񱻻�ȡ
	 * 
	 * @param permission
	 * @param resultCode
	 */
	@SuppressWarnings("unused")
	private void checkPermission(String [] permission , int resultCode )
	{
		for(int i = 0 , leng = permission.length ; i < leng ; i ++ )
		{
			if(ContextCompat.checkSelfPermission(this ,permission[i]) != PackageManager.PERMISSION_GRANTED)
			{
				if(ActivityCompat.shouldShowRequestPermissionRationale(this ,permission[i]))
				{
					// TODO �û��ܾ�����Ȩ��
					showMissingPermissionDialog(permission[i]);
				}
				else
				{
					ActivityCompat.requestPermissions(this ,new String []
					{ permission[i] } ,resultCode);
				}
			}
			else
			{
				Toast.makeText(getApplicationContext() ,permission[i] + "�ѱ���Ȩ" ,Toast.LENGTH_SHORT).show();
				System.out.println(permission[i] + "�ѱ���Ȩ");
			}
		}
	}

	/**
	 * ��ʾ�û�����Ȩ��
	 * 
	 * @param permission
	 */
	private void showMissingPermissionDialog(String permission )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ʾ");
		builder.setMessage("��ǰӦ��ȱ��Ȩ��" + permission + "��\n\n����\"����\"-\"Ȩ��\"-������Ȩ�ޡ�");
		builder.setNegativeButton("�ر�" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Toast.makeText(getApplicationContext() ,"ȡ����Ȩ���ܵ��²��ֹ����޷�ʹ��" ,Toast.LENGTH_SHORT).show();
			}
		});
		builder.setPositiveButton("����" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + getPackageName()));
				startActivity(intent);
			}
		});
		builder.setCancelable(false);
		builder.show();
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
