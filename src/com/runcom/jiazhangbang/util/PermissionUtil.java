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
	// Manifest.permission.RECORD_AUDIO,// Dangerous Permissions 用到时单独申请
	// Manifest.permission.READ_PHONE_STATE, // Dangerous Permissions
	Manifest.permission.WRITE_EXTERNAL_STORAGE,// Dangerous Permissions
	// Manifest.permission.WRITE_SETTINGS
	};

	/**
	 * 默认权限检测
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
	 * 单权限检测
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
	 * 多权限检测
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
	 * 检测单个权限是否被授权
	 * 
	 * @param activity
	 *            上下文
	 * @param permission
	 *            单个权限
	 * @param resultCode
	 *            结果码
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
				Toast.makeText(activity ,permission + "已被授权" ,Toast.LENGTH_SHORT).show();
				System.out.println(permission + "已被授权");
			}
		}
	}

	/**
	 * 检测多个权限是否被获取
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
	 * 提示用户设置权限
	 * 
	 * @param permission
	 */
	private void showMissingPermissionDialog(final Activity activity , String permission )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("提示");
		builder.setMessage("当前应用缺少权限" + permission + "。\n\n请点击\"设置\"-\"权限\"-打开所需权限。");
		builder.setNegativeButton("关闭" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Toast.makeText(activity ,"取消授权可能导致部分功能无法使用" ,Toast.LENGTH_SHORT).show();
			}
		});
		builder.setPositiveButton("设置" ,new DialogInterface.OnClickListener()
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
