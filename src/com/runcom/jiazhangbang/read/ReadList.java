package com.runcom.jiazhangbang.read;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

public class ReadList extends Activity implements OnClickListener , OnItemClickListener
{
	// 语音文件
	private String fileAllNameAmr = null;
	// 音频文件保存的路�?
	private String recordPath = Util.RECORDPATH;
	// 界面控件z
	private Button startRecord;// �?始录�?
	private ImageButton startPlay;// �?始播�?
	private Button stopRecord;// 完成录音
	private Button stopPlay;// 停止播放
	private TextView time , textView_tips;// 计时显示
	private ListView mListView;// 音频文件列表
	private MAdapter mAdapter;
	private Button delete;// 删除按钮
	private Button share;// 分享按钮
	private Button pausePlay;// 暂停播放

	// 语音操作对象
	private MediaPlayer mPlayer = null;// 播放�?
	private MediaRecorder mRecorder = null;// 录音�?
	private boolean isPause = false;// 当前录音是否处于暂停状�??
	private boolean isPausePlay = false;// 当前播放器是否处于暂停状�?
	private ArrayList < String > mList = new ArrayList < String >();// 待合成的录音片段
	private ArrayList < String > list = new ArrayList < String >();// 已合成的录音片段
	private String deleteStr = null; // 列表中要删除的文件名
	private Timer timer;
	private String playFileName = null;// 选中的播放文�?
	// 相关变量
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private int currentPosition = 0;
	private long limitTime = 0;// 录音文件�?短时�?1�?

	@Override
	public void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repeat_record_main);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("录音");
		initList();
		initView();
	}

	// 初始化界�?
	private void initView()
	{
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		delete.setEnabled(false);
		share = (Button) findViewById(R.id.share);
		share.setOnClickListener(this);
		share.setEnabled(false);
		pausePlay = (Button) findViewById(R.id.pausePlay);
		pausePlay.setOnClickListener(this);
		pausePlay.setEnabled(false);
		startRecord = (Button) findViewById(R.id.startRecord);
		startRecord.setOnClickListener(this);
		stopRecord = (Button) findViewById(R.id.stopRecord);
		stopRecord.setOnClickListener(this);
		stopRecord.setEnabled(false);
		startPlay = (ImageButton) findViewById(R.id.startPlay);
		startPlay.setOnClickListener(this);
		startPlay.setEnabled(false);
		stopPlay = (Button) findViewById(R.id.stopPlay);
		stopPlay.setOnClickListener(this);
		stopPlay.setEnabled(false);
		time = (TextView) findViewById(R.id.time);
		mListView = (ListView) findViewById(R.id.repeat_record_main_listview);
		if(0 == list.size())
		{
			mListView.setVisibility(View.GONE);
			textView_tips = (TextView) findViewById(R.id.repeat_record_main_textView);
			textView_tips.setVisibility(View.VISIBLE);
		}
		mAdapter = new MAdapter(this , list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	// 初始化录音列�?
	@SuppressLint("DefaultLocale")
	private void initList()
	{
		File recordePathFile = new File(recordPath);
		if( !recordePathFile.exists())
		{
			try
			{
				recordePathFile.getParentFile().mkdirs();
				recordePathFile.mkdir();
			}
			catch(Exception e)
			{
			}
		}

		// 判断SD卡是否存�?
		if( !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this ,"SD卡状态异常，" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			// 根据后缀名进行判断�?�获取文件夹中的音频文件
			File file = new File(recordPath);
			File files[] = file.listFiles();
			int length = files.length;
			String childFileName = null;
			ArrayList < String > listTemp = new ArrayList < String >();

			for(File childFile : files)
			{
				childFileName = childFile.toString();
				if(childFileName.length() > 0 && (childFileName.endsWith(".amr")))
					listTemp.add(childFileName.substring(childFileName.lastIndexOf("/") + 1 ,childFileName.lastIndexOf(".")));
			}

			for(int i = length - 1 ; i >= 0 ; i -- )
			{
				list.add(listTemp.get(i).toString());
			}

		}
	}

	// 设置点击事件
	@Override
	public void onClick(View v )
	{
		switch(v.getId())
		{
			case R.id.startRecord:// �?始录�?
				// 判断SD卡是否存�?
				if( !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					Toast.makeText(this ,"SD卡状态异常，请检查后重试�?" ,Toast.LENGTH_LONG).show();
					break;
				}
				// �?始录�?
				startRecord();
				// 录音计时
				recordTime();
				break;
			case R.id.stopRecord:// 完成录音
				if(isPause)
				{
					// 完成录音
					stopRecord();
				}
				else
				{
					// 暂停录音
					try
					{
						pauseRecord();
					}
					catch(InterruptedException e)
					{
						// 当一个线程处于等待，睡眠，或者占用，也就是说阻塞状�?�，而这时线程被中断就会抛出这类错误
						// 上百次测试还未发现这个异常，但是�?要捕�?
						e.printStackTrace();
					}
				}
				break;
			case R.id.startPlay:
				// 播放录音
				playRecord();
				break;
			case R.id.stopPlay:
				// 停止播放
				startPlay.setBackgroundResource(R.drawable.pause);
				startPlay.setEnabled(true);
				stopPlay.setEnabled(false);
				startRecord.setEnabled(true);
				pausePlay.setEnabled(false);
				if(mPlayer != null)
				{
					// 释放资源
					// 对MediaPlayer多次使用而不释放资源就会出现MediaPlayer create faild 的异�?
					mPlayer.release();
					mPlayer = null;
				}
				delete.setEnabled(true);
				share.setEnabled(true);
				break;
			case R.id.delete:
				// 删除录音文件
				if(playFileName != null)
					deleteRecord();
				break;
			case R.id.share:
				// 分享录音文件
				if(playFileName != null)
					shareRecord();
				break;
			case R.id.pausePlay:
				// 暂停播放
				if(isPausePlay)
				{
					// pausePlay.setText("暂停");
					pausePlay.setEnabled(true);
					isPausePlay = false;
					mPlayer.start();
				}
				else
				{
					if(mPlayer != null)
					{
						mPlayer.pause();
					}
					// pausePlay.setText("继续");
					pausePlay.setEnabled(true);
					isPausePlay = true;
				}
				break;
			default:
				break;
		}
	}

	// 判断点击事件的时间间�?
	// 点击速度过快，比如在同一秒中点击三次，只会产生一个录音文件，因为命名�?样�??
	@SuppressWarnings("unused")
	private boolean limitTime()
	{
		limitTime = System.currentTimeMillis() - limitTime;
		if(limitTime >= 1100)
		{
			limitTime = System.currentTimeMillis();
			return true;
		}
		else
		{
			return false;
		}
	}

	// 分享录音文件
	private void shareRecord()
	{

		// TODO 分享�?选中的录音文�?
		File file = new File(playFileName);
		if(file.exists())
		{
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra("subject" ,file.getName());
			intent.putExtra("body" ,"android123 - email sender"); // 正文
			intent.putExtra(Intent.EXTRA_STREAM ,Uri.fromFile(file)); // 添加附件，附件为file对象
			if(file.getName().endsWith(".gz"))
			{
				intent.setType("application/x-gzip"); // 如果是gz使用gzip的mime
			}
			else
				if(file.getName().endsWith(".txt"))
				{
					intent.setType("text/plain"); // 纯文本则用text/plain的mime
				}
				else
				{
					intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发�?
				}
			startActivity(intent);
			time.setText("");
		}
		else
		{
			Toast.makeText(getApplicationContext() ,"录音文件不存在，请重�?" ,Toast.LENGTH_SHORT).show();
		}
		startPlay.setEnabled(false);
		playFileName = null;
		delete.setEnabled(false);
		share.setEnabled(false);
		startRecord.setEnabled(true);
		time.setText("您本次的录音时长为： 00:00:00");
		if(currentPosition >= 0 && currentPosition < list.size())
		{
			mAdapter.setItemisSelectedMap(currentPosition ,false);
			mAdapter.notifyDataSetChanged();
		}
	}

	// 删除录音文件
	private void deleteRecord()
	{
		// 删除�?选中的录音文�?
		final File file = new File(playFileName);
		if(file.exists())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ReadList.this);
			builder.setTitle("确定要删除吗�?");
			builder.setNegativeButton("取消" ,new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog , int which )
				{

				}
			});
			builder.setPositiveButton("确定" ,new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog , int which )
				{
					file.delete();
					list.remove(deleteStr);
					mAdapter.notifyDataSetChanged();
					time.setText("");
				}
			});
			builder.show();
		}
		else
		{
			list.remove(deleteStr);
			mAdapter.notifyDataSetChanged();
		}
		startPlay.setEnabled(false);
		playFileName = null;
		delete.setEnabled(false);
		startRecord.setEnabled(true);
		time.setText("您本次的录音时长为： 00:00:00");
		if(currentPosition >= 0 && currentPosition < list.size())
		{
			mAdapter.setItemisSelectedMap(currentPosition ,false);
			mAdapter.notifyDataSetChanged();
		}
	}

	// 播放录音
	private void playRecord()
	{
		startPlay.setBackgroundResource(R.drawable.play);
		// 对按钮的可点击事件的控制是保证不出现空指针的重点！！
		startRecord.setEnabled(false);
		delete.setEnabled(false);
		share.setEnabled(false);
		stopPlay.setEnabled(true);
		startPlay.setEnabled(false);
		pausePlay.setEnabled(true);
		if(mPlayer != null)
		{
			mPlayer.release();
			mPlayer = null;
		}
		mPlayer = new MediaPlayer();
		// 播放完毕的监�?
		mPlayer.setOnCompletionListener(new OnCompletionListener()
		{

			@Override
			public void onCompletion(MediaPlayer mp )
			{
				// 播放完毕改变状�?�，释放资源
				mPlayer.release();
				mPlayer = null;
				startRecord.setEnabled(true);
				startPlay.setEnabled(true);
				startPlay.setBackgroundResource(R.drawable.pause);
				stopPlay.setEnabled(false);
				delete.setEnabled(true);
				share.setEnabled(true);
				pausePlay.setEnabled(false);
			}
		});
		try
		{
			// 播放�?选中的录�?
			mPlayer.setDataSource(playFileName);
			mPlayer.prepare();
			mPlayer.start();
		}
		catch(Exception e)
		{
			// 删除�?选中的录音文�?
			File file = new File(playFileName);
			if(file.exists())
			{
				file.delete();
				list.remove(deleteStr);
				mAdapter.notifyDataSetChanged();
			}
			else
			{
				list.remove(deleteStr);
				mAdapter.notifyDataSetChanged();
			}
			// 若出现异常被捕获后，同样要释放掉资源
			// 否则程序会不稳定，不适合正式项目上使�?
			if(mPlayer != null)
			{
				mPlayer.release();
				mPlayer = null;
			}
			if(currentPosition >= 0 && currentPosition < list.size())
			{
				mAdapter.setItemisSelectedMap(currentPosition ,false);
				mAdapter.notifyDataSetChanged();
			}
			startPlay.setBackgroundResource(R.drawable.pause);
			Toast.makeText(this ,"文件失效" ,Toast.LENGTH_LONG).show();
			stopPlay.setEnabled(false);
			delete.setEnabled(true);
			share.setEnabled(true);
			pausePlay.setEnabled(false);
		}
	}

	// 完成录音
	private void stopRecord()
	{
		mRecorder.release();
		mRecorder = null;
		isPause = false;
		startRecord.setEnabled(true);
		startRecord.setText("�?始录�?");
		stopRecord.setEnabled(false);
		timer.cancel();
		// �?后合成的音频文件
		fileAllNameAmr = recordPath + getTime() + ".amr";
		String fileNameAmr = getTime();
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(fileAllNameAmr);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		FileInputStream fileInputStream = null;
		try
		{
			for(int i = 0 ; i < mList.size() ; i ++ )
			{
				File file = new File(mList.get(i));
				// 把因为暂停所录出的多段录音进行读�?
				fileInputStream = new FileInputStream(file);
				byte [] mByte = new byte [fileInputStream.available()];
				int length = mByte.length;
				// 第一个录音文件的前六位是不需要删除的
				if(i == 0)
				{
					while(fileInputStream.read(mByte) != -1)
					{
						fileOutputStream.write(mByte ,0 ,length);
					}
				}
				// 之后的文件，去掉前六�?
				else
				{
					while(fileInputStream.read(mByte) != -1)
					{
						fileOutputStream.write(mByte ,6 ,length - 6);
					}
				}
			}

			list.add(fileNameAmr);
			mAdapter.notifyDataSetChanged();
		}
		catch(Exception e)
		{
			// 这里捕获流的IO异常，万�?系统错误�?要提示用�?
			e.printStackTrace();
			Toast.makeText(this ,"录音合成出错，请重试�?" ,Toast.LENGTH_LONG).show();
		}
		finally
		{
			try
			{
				fileOutputStream.flush();
				fileInputStream.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			// 录音结束 、时间归�?
			minute = 0;
			hour = 0;
			second = 0;
		}
		// 不管合成是否成功、删除录音片�?
		for(int i = 0 ; i < mList.size() ; i ++ )
		{
			File file = new File(mList.get(i));
			if(file.exists())
			{
				file.delete();
			}
		}

	}

	// 暂停录音
	private void pauseRecord() throws InterruptedException
	{
		if(System.currentTimeMillis() - limitTime < 1100)
		{
			// 录音文件不得低于�?秒钟
			Toast.makeText(this ,"录音时间长度不得低于1秒钟�?" ,Toast.LENGTH_SHORT).show();
			return;
		}
		stopRecord.setEnabled(true);
		mRecorder.stop();
		mRecorder.release();
		timer.cancel();
		isPause = true;
		// 将录音片段加入列�?
		mList.add(fileAllNameAmr);
		startRecord.setEnabled(true);
		startRecord.setText("继续录音");
		stopRecord.setText("完成录音");
	}

	// �?始录�?
	@SuppressWarnings("deprecation")
	private void startRecord()
	{
		stopRecord.setText("暂停录音");
		startRecord.setText("录音�?...");
		startRecord.setEnabled(false);
		startPlay.setEnabled(false);
		stopRecord.setEnabled(true);
		delete.setEnabled(false);
		share.setEnabled(false);
		if( !isPause)
		{
			// 新录音清空列�?
			mList.clear();
		}
		File file = new File(recordPath);
		if( !file.exists())
		{
			file.mkdirs();
		}
		fileAllNameAmr = recordPath + getTime() + ".amr";
		isPause = false;
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 选择amr格式
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mRecorder.setOutputFile(fileAllNameAmr);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try
		{
			mRecorder.prepare();
		}
		catch(Exception e)
		{
			// 若录音器启动失败就需要重启应用，屏蔽掉按钮的点击事件�? 否则会出现各种异常�??
			Toast.makeText(this ,"录音器启动失败，请返回重试！" ,Toast.LENGTH_LONG).show();
			startPlay.setEnabled(false);
			stopPlay.setEnabled(false);
			delete.setEnabled(false);
			share.setEnabled(false);
			startRecord.setEnabled(false);
			stopRecord.setEnabled(false);
			mRecorder.release();
			mRecorder = null;
			this.finish();
		}
		if(mRecorder != null)
		{
			mRecorder.start();
			limitTime = System.currentTimeMillis();
		}

	}

	// 计时器异步更新界�?
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg )
		{
			time.setText("您本次的录音时长为：" + String.format("%1$02d:%2$02d:%3$02d" ,hour ,minute ,second));
			super.handleMessage(msg);
		}
	};

	// 录音计时
	private void recordTime()
	{
		TimerTask timerTask = new TimerTask()
		{

			@Override
			public void run()
			{
				second ++ ;
				if(second >= 60)
				{
					second = 0;
					minute ++ ;
					if(minute >= 60)
					{
						minute = 0;
						hour ++ ;
					}
				}
				handler.sendEmptyMessage(1);
			}

		};
		timer = new Timer();
		timer.schedule(timerTask ,1000 ,1000);
	}

	// 获得当前时间
	@SuppressLint("SimpleDateFormat")
	private String getTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(curDate);
		return time;
	}

	// 录音列表被点击事�?
	@Override
	public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
	{
		// 屏蔽点击事件的一种方�?
		if(mRecorder == null)
		{
			startPlay.setEnabled(true);
			if(mPlayer == null || !mPlayer.isPlaying())
			{
				delete.setEnabled(true);
				share.setEnabled(true);
			}
			else
			{
				delete.setEnabled(false);
				share.setEnabled(false);
			}
		}
		// startPlay.setText("播放");
		// 列表文件的�?�中效果
		view.setBackgroundColor(getResources().getColor(R.color.yes));
		if(currentPosition >= 0 && mAdapter.getisSelectedAt(currentPosition) && currentPosition < list.size())
		{
			mAdapter.setItemisSelectedMap(currentPosition ,false);
			mAdapter.notifyDataSetChanged();
		}
		if( !mAdapter.getisSelectedAt(position))
		{
			mAdapter.setItemisSelectedMap(position ,true);
			mAdapter.notifyDataSetChanged();
			currentPosition = position;
		}
		// 要播放文件的路径
		playFileName = recordPath + list.get(position) + ".amr";
		// 要删除文件的名称
		deleteStr = list.get(position);
		time.setText(list.get(position) + ".amr");
	}

	// Activity被销毁的时�?? 释放资源
	@Override
	protected void onDestroy()
	{
		// 删除片段
		if(mList != null && mList.size() > 0)
		{
			for(int i = 0 ; i < mList.size() ; i ++ )
			{
				File file = new File(mList.get(i));
				if(file.exists())
				{
					file.delete();
				}
			}
		}
		if(null != mRecorder)
		{
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
		if(null != mPlayer)
		{
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		if(timer != null)
		{
			timer.cancel();
		}
		super.onDestroy();
		UMShareAPI.get(this).release();
	}

	// 来电暂停
	@Override
	protected void onPause()
	{
		if(mRecorder != null)
		{
			// 暂停录音
			try
			{
				pauseRecord();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		if(mPlayer != null)
		{
			// 暂停播放
			mPlayer.pause();
			isPausePlay = true;
			// pausePlay.setText("继续");
			pausePlay.setEnabled(true);
		}

		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.time_setting ,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				if(mPlayer != null)
				{
					mPlayer.release();
					mPlayer = null;
				}
				if(mRecorder != null)
				{
					mRecorder.release();
					mRecorder = null;
				}
				finish();
				// onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 重写按返回键�?出播�?
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(mPlayer != null)
			{
				mPlayer.release();
				mPlayer = null;
			}
			if(mRecorder != null)
			{
				mRecorder.release();
				mRecorder = null;
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

}
