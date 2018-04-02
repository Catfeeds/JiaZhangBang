package com.runcom.jiazhangbang.listenWrite;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWritePhaseChose extends Activity
{
	private Intent intent;
	private int selected;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_phase_chose);
		intent = getIntent();

		selected = intent.getIntExtra("selected" ,1);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听写 " + Util.grade[selected];
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

	}

	public void listenWritePhaseChoseFirstPhase(View v )
	{
		intent = new Intent();
		intent.putExtra("selected" ,selected);
		intent.putExtra("phase" ,1);
		intent.setClass(getApplicationContext() ,ListenWriteBackups.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
			startActivity(intent);
	}

	public void listenWritePhaseChoseSecondPhase(View v )
	{

		intent = new Intent();
		intent.putExtra("selected" ,selected);
		intent.putExtra("phase" ,2);
		intent.setClass(getApplicationContext() ,ListenWriteBackups.class);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
			startActivity(intent);

	}

	public void gameTest()
	{
		// Toast.makeText(getApplicationContext() ,"听写游戏测试"
		// ,Toast.LENGTH_SHORT).show();
		// TextView textView = (TextView)
		// findViewById(R.id.listen_write_phase_chose_test_item);
		// textView.setText("asdf");
		intent = new Intent();
		intent.putExtra("selected" ,selected);
		intent.setClass(getApplicationContext() ,ListenWriteGameTest.class);

		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
			startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		getMenuInflater().inflate(R.menu.listen_write_phasechose_menu ,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{

		switch(item.getItemId())
		{
			case R.id.listen_write_phase_chose_test_item:
				gameTest();
				break;
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 重写按返回键退出播放
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
