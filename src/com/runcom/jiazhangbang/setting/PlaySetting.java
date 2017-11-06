package com.runcom.jiazhangbang.setting;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.voice.Speech2Text;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.mainActivity.Update;
import com.umeng.analytics.MobclickAgent;

public class PlaySetting extends Activity
{
	private TextView setting_speech_recognition_textView ,
	        setting_clearCache_textView , setting_opinion_textView ,
	        setting_checkUpdate_textView , setting_aboutUs_textView;
	private ImageView setting_speech_recognition , setting_clearCache_detail ,
	        setting_opinion_detail , setting_checkUpdate_detail ,
	        setting_aboutUs_detail;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_setting_and_help);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle(" …Ë÷√ ");

		initView();
	}

	private void initView()
	{
		setting_speech_recognition_textView = (TextView) findViewById(R.id.setting_speech_recognition_textView);
		setting_speech_recognition_textView.setOnClickListener(listener);
		setting_speech_recognition = (ImageView) findViewById(R.id.setting_speech_recognition);
		setting_speech_recognition.setOnClickListener(listener);

		setting_clearCache_textView = (TextView) findViewById(R.id.setting_clearCache_textView);
		setting_clearCache_textView.setOnClickListener(listener);
		setting_clearCache_detail = (ImageView) findViewById(R.id.setting_clearCache_detail);
		setting_clearCache_detail.setOnClickListener(listener);

		setting_opinion_textView = (TextView) findViewById(R.id.setting_opinion_textView);
		setting_opinion_textView.setOnClickListener(listener);
		setting_opinion_detail = (ImageView) findViewById(R.id.setting_opinion_detail);
		setting_opinion_detail.setOnClickListener(listener);

		setting_checkUpdate_textView = (TextView) findViewById(R.id.setting_checkUpdate_textView);
		setting_checkUpdate_textView.setOnClickListener(listener);
		setting_checkUpdate_detail = (ImageView) findViewById(R.id.setting_checkUpdate_detail);
		setting_checkUpdate_detail.setOnClickListener(listener);

		setting_aboutUs_textView = (TextView) findViewById(R.id.setting_aboutUs_textView);
		setting_aboutUs_textView.setOnClickListener(listener);
		setting_aboutUs_detail = (ImageView) findViewById(R.id.setting_aboutUs_detail);
		setting_aboutUs_detail.setOnClickListener(listener);

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
				case R.id.setting_speech_recognition:
					new Speech2Text(PlaySetting.this).play();
					break;
				case R.id.setting_clearCache_detail:
					// Toast.makeText(getApplicationContext()
					// ,"clearCache_detail..." ,Toast.LENGTH_SHORT).show();
					Toast.makeText(getApplicationContext() ,"“—«Â≥˝57MBª∫¥Ê" ,Toast.LENGTH_SHORT).show();
					break;
				case R.id.setting_opinion_detail:
					Toast.makeText(getApplicationContext() ,"opinion_detail..." ,Toast.LENGTH_SHORT).show();
					break;
				case R.id.setting_checkUpdate_detail:
					Update.update(PlaySetting.this);
					break;
				case R.id.setting_aboutUs_detail:
					Toast.makeText(getApplicationContext() ,"aboutUs_detail..." ,Toast.LENGTH_SHORT).show();
					break;
			}
		}

	};

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
