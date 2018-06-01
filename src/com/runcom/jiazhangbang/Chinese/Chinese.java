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
	// Toast.makeText(this ,"δ��ö�ȡ����Ȩ��" ,Toast.LENGTH_SHORT).show();
	// }
	// }

	protected void checkPermission(String permission , int resultCode )
	{
		if(ContextCompat.checkSelfPermission(this ,permission) != PackageManager.PERMISSION_GRANTED)
		{
			// û��Ȩ��
			Log.i("info" ,"1,��Ҫ����Ȩ�ޡ�");
			if(ActivityCompat.shouldShowRequestPermissionRationale(this ,permission))
			{
				// TODO �û�δ�ܾ��� ��Ȩ�� shouldShowRequestPermissionRationale����false
				// �û��ܾ���һ����һֱ����true
				// ע��С���ֻ� ��һֱ����ʱ false
				Log.i("info" ,"3,�û��Ѿ��ܾ���һ�θ�Ȩ�ޣ���Ҫ��ʾ�û�Ϊʲô��Ҫ��Ȩ�ޡ�\n" + "��ʱshouldShowRequestPermissionRationale���أ�" + ActivityCompat.shouldShowRequestPermissionRationale(this ,permission));
				// ����Ϊʲô ��Ҫ��Ȩ�޵� �Ի���
				showMissingPermissionDialog();
			}
			else
			{
				// ������Ȩ��
				ActivityCompat.requestPermissions(this ,new String []
				{ permission } ,resultCode);
				Log.i("info" ,"2,�û��ܾ�����Ȩ�ޣ������û���δ��������Ȩ�ޣ���ʼ����Ȩ�ޡ�-\n" + "��ʱshouldShowRequestPermissionRationale���أ�" + ActivityCompat.shouldShowRequestPermissionRationale(this ,permission));
			}
		}
		else
		{
			// Ȩ�� �Ѿ���׼�� you can do something
			permissionHasGranted();
			Log.i("info" ,"7,�Ѿ����û���Ȩ����=������������������==����ϵ�˽���");
		}
	}

	protected void permissionHasGranted()
	{
		Toast.makeText(getApplicationContext() ,"Ȩ���Ѿ���׼����,�������������������" ,Toast.LENGTH_SHORT).show();
	}

	int clicki = 0;

	/**
	 * ��ʾ�û��� dialog
	 */
	protected void showMissingPermissionDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ʾ");
		builder.setMessage("��ǰӦ��ȱ����ϵ��Ȩ�ޡ�\n\n����\"����\"-\"Ȩ��\"-������Ȩ�ޡ�");
		// �ܾ�, �˳�Ӧ��
		builder.setNegativeButton("�ر�" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Log.i("info" ,"8--Ȩ�ޱ��ܾ�,��ʱ�����ٻص�onRequestPermissionsResult����");
			}
		});
		builder.setPositiveButton("����" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Log.i("info" ,"4,��Ҫ�û��ֶ����ã�������ǰapp���ý���");
				startAppSettings();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	/**
	 * �� App���ý���
	 */
	private void startAppSettings()
	{
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

	/**
	 * ֱ�� ���� Ȩ��
	 * 
	 * @param permission
	 *            Ȩ��
	 * @param resultCode
	 *            �����
	 */
	protected void directRequestPermisssion(String permission , int resultCode )
	{
		ActivityCompat.requestPermissions(this ,new String []
		{ permission } ,resultCode);
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
				// new ShareUtils(this).shareMultipleLink(Util.update
				// ,getResources().getString(R.string.app_name) ," " ,null
				// ,R.drawable.ic_launcher);
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
