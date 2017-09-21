package com.runcom.jiazhangbang.listenText;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;

public class ListenTextTest extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle(" 听课文 ");
		
		
	}
	
	

	// @Override
	// public boolean onMenuOpened(int featureId , Menu menu )
	// {
	// if(featureId == Window.FEATURE_ACTION_BAR && menu != null)
	// {
	// if(menu.getClass().getSimpleName().equals("MenuBuilder"))
	// {
	// try
	// {
	// Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible"
	// ,Boolean.TYPE);
	// m.setAccessible(true);
	// m.invoke(menu ,true);
	// }
	// catch(Exception e)
	// {
	// Toast.makeText(this ,"overflow 展开显示item图标异常" ,Toast.LENGTH_LONG).show();
	// }
	// }
	// }
	//
	// return super.onMenuOpened(featureId ,menu);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.welcome ,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 重写按返回键
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
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
