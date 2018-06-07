package com.runcom.jiazhangbang.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class PermissionUtil
{

	private String [] mPermissionList = new String []
	{
	// Manifest.permission.ACCESS_NETWORK_STATE, // Normal Permissions
	// Manifest.permission.ACCESS_WIFI_STATE, // Normal Permissions
	// Manifest.permission.CHANGE_NETWORK_STATE, // Normal Permissions
	// Manifest.permission.INTERNET, // Normal Permissions
	// Manifest.permission.READ_EXTERNAL_STORAGE, // Dangerous
	// Permissions
	// Manifest.permission.RECORD_AUDIO,// Dangerous Permissions �õ�ʱ��������
	// Manifest.permission.READ_PHONE_STATE, // Dangerous Permissions
	Manifest.permission.WRITE_EXTERNAL_STORAGE,// Dangerous Permissions
	// Manifest.permission.WRITE_SETTINGS
	};

	/**
	 * Ĭ��Ȩ�޼��
	 * 
	 * @param activity
	 */
	public PermissionUtil(Activity activity)
	{
		if(Build.VERSION.SDK_INT >= 23)
		{
			if(Util.debug)
			{
				Toast.makeText(activity ,"0" ,Toast.LENGTH_SHORT).show();
			}
			ActivityCompat.requestPermissions(activity ,mPermissionList ,0);
			checkPermissions(activity ,mPermissionList ,0);
		}
	}

	/**
	 * ��Ȩ�޼��
	 * 
	 * @param activity
	 * @param permission
	 */
	public PermissionUtil(Activity activity , String permission)
	{
		if(Build.VERSION.SDK_INT >= 23)
		{
			if(Util.debug)
			{
				Toast.makeText(activity ,"1" ,Toast.LENGTH_SHORT).show();
			}
			ActivityCompat.requestPermissions(activity ,new String []
			{ permission } ,1);
			checkPermission(activity ,permission ,1);
		}
	}

	/**
	 * ��Ȩ�޼��
	 * 
	 * @param activity
	 * @param permission
	 */
	public PermissionUtil(Activity activity , String [] permission)
	{
		if(Build.VERSION.SDK_INT >= 23)
		{
			if(Util.debug)
			{
				Toast.makeText(activity ,"2" ,Toast.LENGTH_SHORT).show();
			}
			ActivityCompat.requestPermissions(activity ,permission ,2);
			checkPermissions(activity ,permission ,2);
		}
	}

	/**
	 * ��ⵥ��Ȩ���Ƿ���Ȩ
	 * 
	 * @param activity
	 *            ������
	 * @param permission
	 *            ����Ȩ��
	 * @param resultCode
	 *            �����
	 */
	private void checkPermission(Activity activity , String permission , int resultCode )
	{
		if(ContextCompat.checkSelfPermission(activity ,permission) != PackageManager.PERMISSION_GRANTED)
		{
			if(ActivityCompat.shouldShowRequestPermissionRationale(activity ,permission))
			{
				showMissingPermissionDialog(activity ,permission);
			}
			else
			{
				ActivityCompat.requestPermissions(activity ,new String []
				{ permission } ,resultCode);
			}
		}
		else
		{
			if(Util.debug)
			{
				Toast.makeText(activity ,permission + "�ѱ���Ȩ" ,Toast.LENGTH_SHORT).show();
				System.out.println(permission + "�ѱ���Ȩ");
			}
		}
	}

	/**
	 * �����Ȩ���Ƿ񱻻�ȡ
	 * 
	 * @param permission
	 * @param resultCode
	 */
	private void checkPermissions(Activity activity , String [] permission , int resultCode )
	{
		for(int i = 0 , leng = permission.length ; i < leng ; i ++ )
		{
			checkPermission(activity ,permission[i] ,resultCode);
		}
	}

	/**
	 * ��ʾ�û�����Ȩ��
	 * 
	 * @param permission
	 */
	private void showMissingPermissionDialog(final Activity activity , String permission )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("��ʾ");
		builder.setMessage("��ǰӦ��ȱ��Ȩ��" + permission + "��\n\n����\"����\"-\"Ȩ��\"-������Ȩ�ޡ�");
		builder.setNegativeButton("�ر�" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Toast.makeText(activity ,"ȡ����Ȩ���ܵ��²��ֹ����޷�ʹ��" ,Toast.LENGTH_SHORT).show();
			}
		});
		builder.setPositiveButton("����" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + activity.getPackageName()));
				activity.startActivity(intent);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
}
