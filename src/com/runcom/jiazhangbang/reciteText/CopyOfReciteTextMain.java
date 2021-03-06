/**
 * 
 */
package com.runcom.jiazhangbang.reciteText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.FutureTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.GetLrcContents;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.PermissionUtil;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-3-20
 * @time 下午5:02:50
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.reciteText
 * @file_name ReciteTextMain.java
 * @type_name ReciteTextMain
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class CopyOfReciteTextMain extends Activity implements Checkable
{
	private int flag = 0;
	private Intent intent;
	private ListView listView;
	private MyListViewMainAdapter myListViewMainAdapter;
	private MyTextContent myTextContent = new MyTextContent();
	private final ArrayList < MyTextContent > myTextContentArraylist = new ArrayList < MyTextContent >();
	private int dataMax;
	private int [] counts = null;
	private final String [] scores = new String [10];
	private int note = 0 , temp = 0;
	private float ans = 100;
	private String name , lyricsPath;
	// private List < LyricContent > LyricList = new ArrayList < LyricContent
	// >();
	private ImageButton imageButton_record_stop , startRecord ,
	        imageButton_play_record , imageButton_submit_score ,
	        imageButton_score_list;
	private TextView textView_record_pause , textView_play_record ,
	        textView_listView_tips , time , textView_submit , textView_grade;
	private ProgressDialog progressDialog;
	private MenuItem menuItem;
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	// 定义当前录音器状态
	private static final int IDLE_record = 0;
	private static final int PAUSE_record = 1;
	private static final int START_record = 2;
	private int record_currentState = IDLE_record;
	// 定义当前播放器状态
	private static final int IDLE_play = 0;
	private static final int PAUSE_play = 1;
	private static final int START_play = 2;
	private int play_currentState = IDLE_play;

	private MediaRecorder mediaRecorder = null;// 录音器
	private final ArrayList < String > myRecordList = new ArrayList < String >();// 待合成的录音片段
	private String fileAllNameAmr = null;
	private final String recordPath = Util.RECORDPATH_RECITE_TEXT;
	private Timer timer;
	private Boolean isRecord = true;
	String playName;
	private MediaPlayer mediaPlayer = null;// 播放器
	private final String finalPlayName = "wgc";

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recite_text_main);

		intent = getIntent();
		name = intent.getStringExtra("name");
		lyricsPath = intent.getStringExtra("lrc");

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle(name);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		Arrays.fill(scores ,"");
		initView();
		new PermissionUtil(this , Manifest.permission.RECORD_AUDIO);
	}

	public void reciteSwitching(View v )
	{
		// if(play_currentState != IDLE_play)
		{
			// if(Util.debug)
			// Toast.makeText(getApplicationContext() ,"播放未完成"
			// ,Toast.LENGTH_SHORT).show();
			// time.setText("播放未完成");
			// mediaPlayer = null;
		}
		// else
		{
			mediaPlayer = null;
			isRecord = true;
			textView_listView_tips.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			switch(record_currentState)
			{
				case IDLE_record:
					record_currentState = PAUSE_record;
					startRecord.setImageResource(R.drawable.play);
					textView_record_pause.setText("暂停录音");
					textView_listView_tips.setText("录制中...");
					startRecord();
					recordTime();
					break;
				case PAUSE_record:
					record_currentState = START_record;
					startRecord.setImageResource(R.drawable.record_pause);
					textView_record_pause.setText("开始录音");
					mediaRecorder.stop();
					textView_listView_tips.setText("暂停录制...");
					mediaRecorder.release();
					timer.cancel();
					myRecordList.add(fileAllNameAmr);
					break;
				case START_record:
					record_currentState = PAUSE_record;
					startRecord.setImageResource(R.drawable.play);
					textView_record_pause.setText("暂停录音");
					textView_listView_tips.setText("录制中...");
					startRecord();
					recordTime();
					break;
				default:
					break;
			}
		}

	}

	/**
	 * 
	 */
	private void initView()
	{
		startRecord = (ImageButton) findViewById(R.id.recite_text_main_start);
		textView_record_pause = (TextView) findViewById(R.id.recite_text_main_start_textView);
		textView_listView_tips = (TextView) findViewById(R.id.recite_text_main_listview_textView);
		imageButton_record_stop = (ImageButton) findViewById(R.id.recite_text_main_stop);
		imageButton_record_stop.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				if(record_currentState != IDLE_record && isRecord)
				{
					stopRecord();
				}
				else
				{
					if(Util.debug)
						Toast.makeText(getApplicationContext() ,"请开始录音" ,Toast.LENGTH_LONG).show();
					time.setText("请开始录音");
				}
			}
		});

		imageButton_play_record = (ImageButton) findViewById(R.id.recite_text_main_play);
		textView_play_record = (TextView) findViewById(R.id.recite_text_main_play_textView);
		imageButton_play_record.setVisibility(View.INVISIBLE);
		textView_play_record.setVisibility(View.INVISIBLE);// TODO
		imageButton_play_record.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				if( !isRecord)
				{
					switch(play_currentState)
					{
						case IDLE_play:
							play_currentState = PAUSE_play;
							imageButton_play_record.setImageResource(R.drawable.play);
							textView_play_record.setText("暂停播放");
							playRecord();
							break;
						case PAUSE_play:
							play_currentState = START_play;
							imageButton_play_record.setImageResource(R.drawable.pause);
							textView_play_record.setText("开始播放");
							if(mediaPlayer != null)
							{
								mediaPlayer.pause();
							}
							break;
						case START_play:
							play_currentState = PAUSE_play;
							imageButton_play_record.setImageResource(R.drawable.play);
							textView_play_record.setText("暂停播放");
							if(mediaPlayer != null)
							{
								mediaPlayer.start();
							}
							break;
						default:
							break;
					}
				}
				else
				{
					if(Util.debug)
						Toast.makeText(getApplicationContext() ,"请先完成录制" ,Toast.LENGTH_SHORT).show();
					time.setText("请先完成录制");
				}
			}
		});

		imageButton_submit_score = (ImageButton) findViewById(R.id.recite_text_main_submit);
		textView_submit = (TextView) findViewById(R.id.recite_text_main_submit_textView);
		textView_submit.setVisibility(View.INVISIBLE);
		imageButton_submit_score.setVisibility(View.INVISIBLE);// TODO
		imageButton_submit_score.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				if(isRecord)
				{
					if(Util.debug)
						Toast.makeText(getApplicationContext() ,"请先完成录制" ,Toast.LENGTH_SHORT).show();
					time.setText("请先完成录制");
				}
				else
				{
					int right = 0;
					for(int i = 0 ; i < dataMax ; i ++ )
						if(0 == counts[i])
							right ++ ;
					ans = (float) (right * 1.0 / dataMax) * 100;
					ans = Float.valueOf(new DecimalFormat("##0.0").format(ans));// 保留一位小数
					AlertDialog.Builder builder = new AlertDialog.Builder(CopyOfReciteTextMain.this);
					builder.setTitle("确定提交本次成绩：" + ans);
					builder.setNegativeButton("确定" ,new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog , int which )
						{
							Toast.makeText(getApplicationContext() ,"您提交了 " + ans + " 分" ,Toast.LENGTH_SHORT).show();
							if(temp > 9)
								temp = 0;
							scores[temp] = "\t\t\t\t\t\t\t第" + (note + 1) + "次：\t\t" + ans;
							++ temp;
							++ note;
						}
					});
					builder.setPositiveButton("放弃" ,new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog , int which )
						{
						}
					});
					builder.show();
				}
			}
		});

		imageButton_score_list = (ImageButton) findViewById(R.id.recite_text_main_detail);
		imageButton_score_list.setVisibility(View.INVISIBLE);// TODO
		textView_grade = (TextView) findViewById(R.id.recite_text_main_detail_textView);
		textView_grade.setVisibility(View.INVISIBLE);
		imageButton_score_list.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(CopyOfReciteTextMain.this , R.style.NoBackGroundDialog);
				builder.setIcon(R.drawable.ic_launcher);
				getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				builder.setTitle("历史成绩");
				builder.setNegativeButton("确定" ,new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog , int which )
					{
						dialog.dismiss();
					}
				});

				builder.setItems(scores ,new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog , int which )
					{
						Toast.makeText(getApplication() ,scores[which] ,Toast.LENGTH_SHORT).show();
					}
				});
				builder.show();
			}
		});
		time = (TextView) findViewById(R.id.recite_text_main_nameShow);
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			String content = "";
			// content = Util.getLrcContents(lrc);
			FutureTask < String > faeature = new FutureTask < String >(new GetLrcContents(lyricsPath));
			new Thread(faeature).start();
			try
			{
				content = faeature.get();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
			// System.out.println(content);
			String contents[] = content.split("\n");
			String Lrc_data = "";
			for(int i = 0 , leng = contents.length ; i < leng ; i ++ )
			{
				myTextContent = new MyTextContent();
				Lrc_data = contents[i];
				// System.out.println("Lrc_data:" + Lrc_data);
				if(Lrc_data.contains("\t"))
				{
					myTextContent.setName(++ flag + "\t\u3000" + Lrc_data.substring(Lrc_data.indexOf("]") + 1));
				}
				else
				{
					myTextContent.setName("\t\u3000\u3000" + Lrc_data.substring(Lrc_data.indexOf("]") + 1));
				}
				dataMax ++ ;
				myTextContentArraylist.add(myTextContent);
			}
			/*
			 * String lyricsPath = Util.LYRICSPATH + lrc; File mFile = new
			 * File(lyricsPath); FileInputStream mFileInputStream;
			 * BufferedReader mBufferedReader = null; try { mFileInputStream =
			 * new FileInputStream(mFile); InputStreamReader mInputStreamReader;
			 * mInputStreamReader = new InputStreamReader(mFileInputStream ,
			 * "utf-8"); mBufferedReader = new
			 * BufferedReader(mInputStreamReader); int flag = 0; dataMax = 0;
			 * while((Lrc_data = mBufferedReader.readLine()) != null) {
			 * myTextContent = new MyTextContent(); if(Lrc_data.contains("\t"))
			 * { myTextContent.setName(++ flag + "\t\u3000" +
			 * Lrc_data.substring(Lrc_data.indexOf("]") + 1)); } else {
			 * myTextContent.setName("\t\u3000\u3000" +
			 * Lrc_data.substring(Lrc_data.indexOf("]") + 1)); } dataMax ++ ;
			 * myTextContentArraylist.add(myTextContent); }
			 * 
			 * } catch(Exception e) { System.out.println(e); } finally { try {
			 * mBufferedReader.close(); } catch(IOException e) {
			 * System.out.println(e); } }
			 */

			counts = new int [dataMax];
			Arrays.fill(counts ,0);
			initListview();

		}

	}

	public void onCompletion(MediaPlayer mp )
	{
		play_currentState = IDLE_play;
	}

	// 播放录音
	private void playRecord()
	{
		// 对按钮的可点击事件的控制是保证不出现空指针的重点！！
		startRecord.setEnabled(false);
		if(mediaPlayer != null)
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mediaPlayer = new MediaPlayer();
		// 播放完毕的监听
		mediaPlayer.setOnCompletionListener(new OnCompletionListener()
		{

			@Override
			public void onCompletion(MediaPlayer mp )
			{
				// 播放完毕改变状态，释放资源
				mediaPlayer.release();
				mediaPlayer = null;
				startRecord.setEnabled(true);
				imageButton_play_record.setImageResource(R.drawable.pause);
			}
		});
		try
		{
			// 播放所选中的录音
			mediaPlayer.setDataSource(recordPath + finalPlayName + ".amr");
			System.out.println();
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
		catch(Exception e)
		{
			// 否则程序会不稳定，不适合正式项目上使用
			if(mediaPlayer != null)
			{
				mediaPlayer.release();
				mediaPlayer = null;
			}
			startRecord.setEnabled(true);
			imageButton_play_record.setImageResource(R.drawable.pause);
		}
	}

	// 开始录音
	@SuppressWarnings("deprecation")
	private void startRecord()
	{
		if(menuItem != null)
		{
			menuItem.setTitle("");
		}
		myRecordList.clear();
		File file = new File(recordPath);
		if( !file.exists())
		{
			file.mkdirs();
		}
		fileAllNameAmr = recordPath + getTime() + ".amr";
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 选择amr格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mediaRecorder.setOutputFile(fileAllNameAmr);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try
		{
			mediaRecorder.prepare();
		}
		catch(Exception e)
		{
			// 若录音器启动失败就需要重启应用，屏蔽掉按钮的点击事件。 否则会出现各种异常。
			if(Util.debug)
				Toast.makeText(this ,"录音器启动失败，请稍后重试！" ,Toast.LENGTH_LONG).show();
			time.setText("录音器启动失败，请稍后重试！");
			mediaRecorder.release();
			mediaRecorder = null;
			this.finish();
		}
		if(mediaRecorder != null)
		{
			mediaRecorder.start();
		}

	}

	// 计时器异步更新界面
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

	// 完成录音
	private void stopRecord()
	{
		if(menuItem != null)
		{
			menuItem.setTitle("正确行数：" + flag + "/" + flag);
		}
		imageButton_play_record.setVisibility(View.VISIBLE);
		textView_play_record.setVisibility(View.VISIBLE);// TODO
		imageButton_submit_score.setVisibility(View.VISIBLE);
		textView_submit.setVisibility(View.VISIBLE);
		imageButton_score_list.setVisibility(View.VISIBLE);
		textView_grade.setVisibility(View.VISIBLE);
		isRecord = false;
		play_currentState = IDLE_play;
		textView_listView_tips.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		record_currentState = IDLE_record;
		mediaRecorder.release();
		mediaRecorder = null;
		myRecordList.add(fileAllNameAmr);

		startRecord.setImageResource(R.drawable.record_pause);
		textView_record_pause.setText("开始录音");
		timer.cancel();

		// playName = getTime();
		// fileAllNameAmr = recordPath + playName + ".amr";
		fileAllNameAmr = recordPath + finalPlayName + ".amr";
		if(new File(fileAllNameAmr).exists())
		{
			new File(fileAllNameAmr).delete();
		}
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(fileAllNameAmr);
		}
		catch(FileNotFoundException e)
		{
		}
		FileInputStream fileInputStream = null;
		try
		{
			for(int i = 0 ; i < myRecordList.size() ; i ++ )
			{
				File file = new File(myRecordList.get(i));
				// 把因为暂停所录出的多段录音进行读取
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
				// 之后的文件，去掉前六位
				else
				{
					while(fileInputStream.read(mByte) != -1)
					{
						fileOutputStream.write(mByte ,6 ,length - 6);
					}
				}
			}
			time.setText("录音完成");
		}
		catch(Exception e)
		{
			if(Util.debug)
				Toast.makeText(getApplicationContext() ,"录音合成出错，请重试！" ,Toast.LENGTH_LONG).show();
			time.setText("录音合成出错，请重试！");
			System.out.println(e);
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
				System.out.println(e);
			}
		}
		for(int i = 0 ; i < myRecordList.size() ; i ++ )
		{
			File file = new File(myRecordList.get(i));
			if(file.exists())
			{
				file.delete();
			}
		}

		minute = 0;
		hour = 0;
		second = 0;

	}

	// 获得当前时间
	@SuppressLint("SimpleDateFormat")
	private String getTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(curDate);
		return time;
	}

	private void initListview()
	{
		listView = (ListView) findViewById(R.id.recite_text_main_listview);
		myListViewMainAdapter = new MyListViewMainAdapter(getApplicationContext() , myTextContentArraylist);
		listView.setAdapter(myListViewMainAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		flag = myTextContentArraylist.size();
		if(menuItem != null)
		{
			menuItem.setTitle("正确行数：" + flag + "/" + flag);
		}
		progressDialog.dismiss();
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				if(isRecord)
				{
					if(Util.debug)
						Toast.makeText(getApplicationContext() ,"请先完成录制" ,Toast.LENGTH_SHORT).show();
					time.setText("请先完成录制");
				}
				else
				{
					if(0 == counts[position])
					{
						counts[position] = 1;
						flag -- ;
						if(flag < 0)
						{
							flag = 0;
						}
						if(menuItem != null)
						{
							menuItem.setTitle("正确行数：" + flag + "/" + myTextContentArraylist.size());
						}
					}
					else
					{
						counts[position] = 0;
						flag ++ ;
						if(flag >= myTextContentArraylist.size())
						{
							flag = myTextContentArraylist.size();
						}
					}
					if(menuItem != null)
					{
						menuItem.setTitle("正确行数：" + flag + "/" + myTextContentArraylist.size());
					}
					boolean isSelect = myListViewMainAdapter.getisSelectedAt(position);
					myListViewMainAdapter.setItemisSelectedMap(position , !isSelect);
					myListViewMainAdapter.notifyDataSetChanged();
					if(Util.debug)
						Toast.makeText(getApplication() ,"position : " + position + "\nstate : " + counts[position] ,Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void setChecked(boolean checked )
	{
		// setBackgroundDrawable(checked ? new ColorDrawable(0xff0000a0) :
		// null);//当选中时呈现蓝色
	}

	@Override
	public boolean isChecked()
	{
		return false;
	}

	@Override
	public void toggle()
	{
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		getMenuInflater().inflate(R.menu.recite_text_main_menu ,menu);
		menuItem = menu.findItem(R.id.recite_text_main_menu_score);
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
		// MobclickAgent.onPageStart("ChineseScreen");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		// MobclickAgent.onPageEnd("ChineseScreen");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

}
