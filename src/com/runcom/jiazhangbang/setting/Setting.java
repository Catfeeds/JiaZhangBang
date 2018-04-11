package com.runcom.jiazhangbang.setting;

import java.io.File;
import java.text.DecimalFormat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.voice.Speech2Text;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.mainActivity.Update;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class Setting extends Activity
{
	private TextView setting_modify_textView ,
	        setting_speech_recognition_textView , setting_clearCache_textView ,
	        setting_opinion_textView , setting_checkUpdate_textView ,
	        setting_aboutUs_textView , setting_version_textView ,
	        setting_clearCache_detail;
	private ImageView setting_modify_detail ,
	        setting_speech_recognition_detail , setting_opinion_detail ,
	        setting_checkUpdate_detail , setting_aboutUs_detail;
	private TableRow setting_modify_tableRow ,
	        setting_speech_recognition_tableRow , setting_clearCache_tableRow ,
	        setting_opinion_tableRow , setting_checkUpdate_tableRow ,
	        setting_aboutUs_tableRow;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("设置 ");

		initView();
	}

	private void initView()
	{
		setting_modify_textView = (TextView) findViewById(R.id.setting_modify_textView);
		setting_modify_textView.setOnClickListener(listener);
		setting_modify_detail = (ImageView) findViewById(R.id.setting_modify_detail);
		setting_modify_detail.setOnClickListener(listener);
		setting_modify_tableRow = (TableRow) findViewById(R.id.setting_modify_tableRow);
		setting_modify_tableRow.setOnClickListener(listener);

		setting_speech_recognition_textView = (TextView) findViewById(R.id.setting_speech_recognition_textView);
		setting_speech_recognition_textView.setOnClickListener(listener);
		setting_speech_recognition_detail = (ImageView) findViewById(R.id.setting_speech_recognition_detail);
		setting_speech_recognition_detail.setOnClickListener(listener);
		setting_speech_recognition_tableRow = (TableRow) findViewById(R.id.setting_speech_recognition_tableRow);
		setting_speech_recognition_tableRow.setOnClickListener(listener);

		setting_clearCache_textView = (TextView) findViewById(R.id.setting_clearCache_textView);
		setting_clearCache_textView.setOnClickListener(listener);
		setting_clearCache_detail = (TextView) findViewById(R.id.setting_clearCache_detail);

		double size = getFileCache(Util.APPPATH ,0) * 1.0 / 1024;
		if(size <= 0)
		{
			setting_clearCache_detail.setText("0KB");
		}
		else
			if(size < 1024)
			{
				setting_clearCache_detail.setText(new DecimalFormat("#.00").format(size) + "KB");
			}
			else
			{
				setting_clearCache_detail.setText(new DecimalFormat("#.00").format(size * 1.0 / 1024) + "MB");
			}
		setting_clearCache_detail.setOnClickListener(listener);
		setting_clearCache_tableRow = (TableRow) findViewById(R.id.setting_clearCache_tableRow);
		setting_clearCache_tableRow.setOnClickListener(listener);

		setting_opinion_textView = (TextView) findViewById(R.id.setting_opinion_textView);
		setting_opinion_textView.setOnClickListener(listener);
		setting_opinion_detail = (ImageView) findViewById(R.id.setting_opinion_detail);
		setting_opinion_detail.setOnClickListener(listener);
		setting_opinion_tableRow = (TableRow) findViewById(R.id.setting_opinion_tableRow);
		setting_opinion_tableRow.setOnClickListener(listener);

		setting_checkUpdate_textView = (TextView) findViewById(R.id.setting_checkUpdate_textView);
		setting_checkUpdate_textView.setOnClickListener(listener);
		setting_checkUpdate_detail = (ImageView) findViewById(R.id.setting_checkUpdate_detail);
		setting_checkUpdate_detail.setOnClickListener(listener);
		setting_checkUpdate_tableRow = (TableRow) findViewById(R.id.setting_checkUpdate_tableRow);
		setting_checkUpdate_tableRow.setOnClickListener(listener);

		setting_aboutUs_textView = (TextView) findViewById(R.id.setting_aboutUs_textView);
		setting_aboutUs_textView.setOnClickListener(listener);
		setting_aboutUs_detail = (ImageView) findViewById(R.id.setting_aboutUs_detail);
		setting_aboutUs_detail.setOnClickListener(listener);
		setting_aboutUs_tableRow = (TableRow) findViewById(R.id.setting_aboutUs_tableRow);
		setting_aboutUs_tableRow.setOnClickListener(listener);

		setting_version_textView = (TextView) findViewById(R.id.setting_version_textView);
		setting_version_textView.setOnClickListener(listener);
		try
		{
			String ver = this.getPackageManager().getPackageInfo(this.getPackageName() ,0).versionName;
			setting_version_textView.setText("版本号：" + ver + "\n版权所有：浙江兰创通信有限公司");
		}
		catch(NameNotFoundException e)
		{
			setting_version_textView.setText("版权所有：浙江兰创通信有限公司");
		}
	}

	/**
	 * 
	 */
	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v )
		{
			switch(v.getId())
			{
				case R.id.setting_modify_textView:
				case R.id.setting_modify_tableRow:
				case R.id.setting_modify_detail:
					// startActivity(new
					// Intent().setClass(getApplicationContext()
					// ,SettingChose.class));
					Toast.makeText(getApplicationContext() ,"modify..." ,Toast.LENGTH_LONG).show();
					break;

				case R.id.setting_speech_recognition_detail:
				case R.id.setting_speech_recognition_textView:
				case R.id.setting_speech_recognition_tableRow:
					new Speech2Text(Setting.this).play();
					break;
				case R.id.setting_clearCache_detail:
				case R.id.setting_clearCache_textView:
				case R.id.setting_clearCache_tableRow:
					deleteCache();
					break;
				case R.id.setting_opinion_detail:
				case R.id.setting_opinion_textView:
				case R.id.setting_opinion_tableRow:
					Toast.makeText(getApplicationContext() ,"opinion_detail..." ,Toast.LENGTH_SHORT).show();
					break;
				case R.id.setting_checkUpdate_detail:
				case R.id.setting_checkUpdate_textView:
				case R.id.setting_checkUpdate_tableRow:
					Update.update(Setting.this);
					break;
				case R.id.setting_aboutUs_detail:
				case R.id.setting_aboutUs_textView:
				case R.id.setting_aboutUs_tableRow:
					startActivity(new Intent().setClass(getApplicationContext() ,AboutUs.class));
					break;
				case R.id.setting_version_textView:
					break;
			}
		}

	};

	private void deleteCache()
	{
		long fileSizeCount = 0;
		fileSizeCount = deleteFile(Util.APPPATH ,fileSizeCount);
		Toast.makeText(getApplicationContext() ,"缓存已清完" ,Toast.LENGTH_SHORT).show();
		setting_clearCache_detail.setText("0KB");
	}

	private long deleteFile(String filePath , long fileSizeCount )
	{
		File dir = new File(filePath);
		File [] files = dir.listFiles();
		for(File file : files)
		{
			if(file.isDirectory())
			{
				deleteFile(file.toString() ,fileSizeCount);
				file.delete();
			}
			else
				if(file.toString().endsWith(".wav") || file.toString().endsWith(".amr") || file.toString().endsWith(".apk"))
				{
					fileSizeCount += file.length();
					file.delete();
					// System.out.println("删除:" + file);
				}
		}
		return fileSizeCount;

	}

	private long getFileCache(String filePath , long fileSizeCount )
	{
		File dir = new File(filePath);
		File [] files = dir.listFiles();
		for(File file : files)
		{
			if(file.isDirectory())
			{
				fileSizeCount += getFileCache(file.toString() ,fileSizeCount);
			}
			else
				if(file.toString().endsWith(".wav") || file.toString().endsWith(".amr") || file.toString().endsWith(".apk"))
				{
					fileSizeCount += file.length();
					// System.out.println(file.toString() + fileSizeCount);
				}
		}
		// System.out.println(fileSizeCount);
		return fileSizeCount;
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
				onBackPressed();
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart("PlaySettingScreen");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd("PlaySettingScreen");
		MobclickAgent.onPause(this);
	}
}
