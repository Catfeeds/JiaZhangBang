package com.runcom.jiazhangbang.setting;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteTipsSetting extends Activity
{

	private static final String sharedPreferencesKey = "ListenWriteSetting";
	private static final String sharedPreferencesInterval = "ListenWriteInterval";
	private static final String sharedPreferencesFrequency = "ListenWriteFrequency";
	private NumberPicker intervalNumberPicker , frequencyNumberPicker;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_tips_paly_setting);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("重置 ");

		initView();
		initNumberPicker();

	}

	private void initView()
	{
		intervalNumberPicker = (NumberPicker) findViewById(R.id.setting_intervalNumberPicker);
		frequencyNumberPicker = (NumberPicker) findViewById(R.id.setting_frequencyNumberPicker);
	}

	private void initNumberPicker()
	{
		// 两个词语间隔秒数
		intervalNumberPicker.setMaxValue(17);
		intervalNumberPicker.setMinValue(1);
		// intervalNumberPicker.set
		intervalNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);// 用户不可编辑输入
		intervalNumberPicker.setFormatter(new Formatter()
		{

			@Override
			public String format(int value )
			{
				if(value < 10)
				{
					return "0" + String.valueOf(value);
				}
				return String.valueOf(value);
			}
		});

		int intervalValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesInterval ,1);
		// Toast.makeText(getApplicationContext() , "" + intervalValue ,
		// Toast.LENGTH_SHORT).show();
		intervalNumberPicker.setValue(intervalValue);
		intervalNumberPicker.setOnValueChangedListener(new OnValueChangeListener()
		{
			@Override
			public void onValueChange(NumberPicker picker , int oldVal , int newVal )
			{
				intervalNumberPicker.setValue(newVal);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesInterval ,newVal);
				// Toast.makeText(getApplicationContext() , "" + newVal ,
				// Toast.LENGTH_SHORT).show();
			}
		});
		// 两个词语阅读次数
		frequencyNumberPicker.setMaxValue(17);
		frequencyNumberPicker.setMinValue(1);
		frequencyNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		frequencyNumberPicker.setFormatter(new Formatter()
		{
			@Override
			public String format(int value )
			{
				if(value < 10)
				{
					return "0" + String.valueOf(value);
				}
				return String.valueOf(value);
			}
		});
		int frequencyValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesFrequency ,1);
		frequencyNumberPicker.setValue(frequencyValue);
		frequencyNumberPicker.setOnValueChangedListener(new OnValueChangeListener()
		{
			@Override
			public void onValueChange(NumberPicker picker , int oldVal , int newVal )
			{
				frequencyNumberPicker.setValue(newVal);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,sharedPreferencesFrequency ,newVal);
			}
		});
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
			finish();
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
