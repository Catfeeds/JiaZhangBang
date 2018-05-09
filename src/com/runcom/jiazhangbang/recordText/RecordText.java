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
	private String lrcStr = "[00:00.18]������������\n" + "[00:03.03]ÿ��ҹĻ����\n" + "[00:06.36]��������������\n" + "[00:08.96]���������Ǳ���˵Ƶĺ���\n" + "[00:12.56]�������\n" + "[00:14.80]�����ֻ��Ƹ���\n" + "[00:18.54]������Ϣ������\n" + "[00:20.70]�ƹ���˸\n" + "[00:22.80]�����Ӵ������\n" + "[00:25.77]�찲�ų�¥��̻Ի�\n" + "[00:30.05]��ʶ�Ŀ\n" + "[00:32.19]�㳡����\n" + "[00:35.28]�ʵƹ�����һ�����ߴ��������ΰ����\n" + "[00:41.59]����·��\n" + "[00:44.29]һ������������������ʺ�\n" + "[00:48.16]�ֵ���\n" + "[00:50.54]�����Ʋ�ƺ����Ȫ���񻨵�\n" + "[00:55.78]װ���������ı���\n" + "[00:58.50]��Ȼһ�µ�������������ҵ����\n" + "[01:04.39]�����ĳ���\n" + "[01:06.67]Ѥ����ʵĹ��\n" + "[01:09.18]���ʮɫ���޺��\n" + "[01:11.46]�ѷ����Ĵ��װ����˱Ȱ�������Ĳ�ҹ��\n" + "[01:17.20]���ϵĹʹ����������\n" + "[01:22.24]һ�����ƹ�������\n" + "[01:25.18]�����ĳ�ǽ�������Ľ�¥��ӳ�ں�����\n" + "[01:31.16]��������\n" + "[01:32.87]ʮ�ֶ���\n" + "[01:34.67]ҹ��ı���\n" + "[01:37.82]��ô����\n" + "[01:39.44]��ô�Ի�\n" + "[01:41.35]";
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
		String content = "����";
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("���ڻ�ȡ����......");
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

	// ��д�����ؼ��˳�����
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
