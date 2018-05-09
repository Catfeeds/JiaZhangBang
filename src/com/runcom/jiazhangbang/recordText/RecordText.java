package com.runcom.jiazhangbang.recordText;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.lrcView.LrcView;
import com.umeng.analytics.MobclickAgent;

public class RecordText extends Activity
{
	private LrcView lrcView;
	private String lrcStr = "[00:00.18]北京亮起来了\n" + "[00:03.03]每当夜幕降临\n" + "[00:06.36]北京就亮起来了\n" + "[00:08.96]整个北京城变成了灯的海洋\n" + "[00:12.56]光的世界\n" + "[00:14.80]长安街华灯高照\n" + "[00:18.54]川流不息的汽车\n" + "[00:20.70]灯光闪烁\n" + "[00:22.80]像银河从天而降\n" + "[00:25.77]天安门城楼金碧辉煌\n" + "[00:30.05]光彩夺目\n" + "[00:32.19]广场四周\n" + "[00:35.28]彩灯勾画出一幢幢高大建筑物的雄伟轮廓\n" + "[00:41.59]环形路上\n" + "[00:44.29]一座座立交桥犹如道道彩虹\n" + "[00:48.16]街道上\n" + "[00:50.54]照明灯草坪灯喷泉灯礼花灯\n" + "[00:55.78]装点着美丽的北京\n" + "[00:58.50]焕然一新的王府井西单商业街上\n" + "[01:04.39]明亮的橱窗\n" + "[01:06.67]绚丽多彩的广告\n" + "[01:09.18]五光十色的霓虹灯\n" + "[01:11.46]把繁华的大街装扮成了比白天更美的不夜城\n" + "[01:17.20]古老的故宫变得年轻了\n" + "[01:22.24]一束束灯光照着她\n" + "[01:25.18]长长的城墙和美丽的角楼倒映在河面上\n" + "[01:31.16]银光闪闪\n" + "[01:32.87]十分动人\n" + "[01:34.67]夜晚的北京\n" + "[01:37.82]多么明亮\n" + "[01:39.44]多么辉煌\n" + "[01:41.35]";
	private ProgressDialog progressDialog;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_text_main);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "返回";
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		initView();
	}

	private void initView()
	{
		lrcView = (LrcView) findViewById(R.id.listenText_lyricShow);
		lrcView.setLrc(lrcStr);
		mediaPlayer = new MediaPlayer();
		try
		{
			mediaPlayer.setDataSource("http://res.nutnet.cn:8800/cn/2-2/mp3/012.mp3");
			lrcView.setPlayer(mediaPlayer);
			lrcView.init();
			progressDialog.dismiss();
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				if(mediaPlayer != null)
				{
					mediaPlayer.release();
					mediaPlayer = null;
				}
				if(progressDialog != null)
				{
					progressDialog.dismiss();
				}
				finish();
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
			if(progressDialog != null)
			{
				progressDialog.dismiss();
			}
			if(mediaPlayer != null)
			{
				mediaPlayer.release();
				mediaPlayer = null;
			}
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

	@Override
	protected void onDestroy()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}

		if(mediaPlayer != null)
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}

}
