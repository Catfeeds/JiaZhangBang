/**
 * 
 */
package com.runcom.jiazhangbang.read;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.judge.Judge;
import com.runcom.jiazhangbang.listenText.GetLrcContents;
import com.runcom.jiazhangbang.listenText.MyAudio;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.PermissionUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-12
 * @time ����10:36:45
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.repeat
 * @file_name Repeat.java
 * @type_name Repeat
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class Read extends Activity
{
	private MediaRecorder mediaRecorder = null;// ¼����
	private Timer timer;
	private String fileAllNameAmr = null;
	private String recordPath = Util.RECORDPATH;
	private ArrayList < String > myRecordList = new ArrayList < String >();// ���ϳɵ�¼��Ƭ��
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private TextView time;// ��ʱ��ʾ

	private Spinner spinner;
	private ImageButton startRecord , stopRecord;
	private List < MyAudio > play_list = new ArrayList < MyAudio >();
	private List < String > play_list_copy = new ArrayList < String >();
	private List < String > play_list_id = new ArrayList < String >();
	private MyAudio myAudio;
	private int currIndex = 0;// ��ʾ��ǰ���ŵ���������

	// ���嵱ǰ��������״̬
	private static final int IDLE = 0;
	private static final int PAUSE = 1;
	private static final int START = 2;

	private int play_currentState = IDLE; // ��ǰ��������״̬
	private Intent intent;
	private String lyricsPath;
	private int course , grade , phase , unit;
	private ProgressDialog progressDialog;
	private TextView textView_lrcView;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repeat_main);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[Util.Repeat] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[Util.Repeat] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[Util.Repeat] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[Util.Repeat] ,unit);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "�ʶ�" + Util.grade[grade] + "��ѧ��" + Util.unit[unit];
		if(2 == phase)
			content = "�ʶ�" + Util.grade[grade] + "��ѧ��" + Util.unit[unit];
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("���ڻ�ȡ����......");
		progressDialog.show();

		initPlayView();
		new PermissionUtil(this , Manifest.permission.RECORD_AUDIO);
	}

	private void initPlayView()
	{
		spinner = (Spinner) findViewById(R.id.repeat_spinner);
		startRecord = (ImageButton) findViewById(R.id.media_start);
		stopRecord = (ImageButton) findViewById(R.id.media_stop);
		time = (TextView) findViewById(R.id.listen_write_textView_nameShow);
		textView_lrcView = (TextView) findViewById(R.id.listenText_lyricShow_textView);
		initTitle();
	}

	/**
	 * ��ʼ������
	 */
	private void initTitle()
	{
		final TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,course + "");
		map.put("grade" ,grade + "");
		map.put("phase" ,phase + "");
		map.put("unit" ,0 == unit ? -- unit + "" : unit + "");
		System.out.println(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{
			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
				finish();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
				{
					initData();
				}
				else
					if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsMissingResourceString ,Toast.LENGTH_LONG).show();
						finish();
					}
					else
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
						finish();
					}
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
				if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
				{
					return result;
				}
				// play_list_title.clear();
				play_list_id.clear();
				// System.out.println(jsonObject.toString());
				JSONArray jsonArray = jsonObject.getJSONArray("textlist");
				JSONObject textListJsonObject = null;
				int leng = jsonArray.length();
				if(leng <= 0)
				{
					return Util.okHttpUtilsResultExceptionStringValue;
				}
				for(int i = 0 ; i < leng ; i ++ )
				{
					textListJsonObject = new JSONObject(jsonArray.getString(i));
					String parts = textListJsonObject.getString("parts");
					int part = Integer.valueOf(parts);
					if(1 == part)
					{
						// play_list_title.add(textListJsonObject.getString("title"));
						play_list_id.add(textListJsonObject.getString("id"));
					}
					else
						if(1 < part)
						{
							JSONArray subjsonArray = textListJsonObject.getJSONArray("partlist");
							int length = subjsonArray.length();
							for(int k = 0 ; k < length ; k ++ )
							{
								JSONObject subjsonObject = new JSONObject(subjsonArray.getString(k));
								// play_list_title.add(subjsonObject.getString("title"));
								play_list_id.add(subjsonObject.getString("id"));
							}
						}
						else
						{
							Toast.makeText(getApplicationContext() ,"�������쳣" ,Toast.LENGTH_LONG).show();
							finish();
						}
				}

				return result;
			}

		});

	}

	private void initData()
	{

		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,Util.okHttpUtilsInternetConnectExceptionString ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			final String resourceServer = MySharedPreferences.getValue(getApplicationContext() ,Util.utilResUrlHeadSharedPreferencesKey ,Util.utilResUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER);
			TreeMap < String , String > map = null;
			play_list.clear();
			// play_list_copy.clear();
			final int leng = play_list_id.size();
			for(int i = 0 ; i < leng ; i ++ )
			{
				final int ii = i;
				map = Util.getMap(getApplicationContext());
				map.put("textid" ,play_list_id.get(i));
				System.out.println(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map));
				OkHttpUtils.get().url(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
				{

					@Override
					public void onError(Call arg0 , Exception arg1 , int arg2 )
					{
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
						finish();
					}

					@Override
					public void onResponse(String arg0 , int arg1 )
					{
						if(leng - 1 == ii)
						{
							initSpinner();
						}
						else
							if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
							{
								Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
								finish();
							}
					}

					@Override
					public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
					{
						String response = arg0.body().string().trim();
						JSONObject jsonObject = new JSONObject(response);
						String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
						if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
						{
							return result;
						}
						JSONObject jsonObject_attr = new JSONObject(jsonObject.getString("attr"));
						JSONObject jsonObject_partlist = new JSONObject(jsonObject_attr.getString("partlist"));

						myAudio = new MyAudio();
						String lyric_copy = resourceServer + jsonObject_partlist.getString("subtitle");
						String title = jsonObject_partlist.getString("title");
						// play_list_copy.add(title);
						myAudio.setName(title);
						// if( !new File(Util.LYRICSPATH + title +
						// ".lrc").exists())
						// new LrcFileDownloader(lyric_copy , title +
						// ".lrc").start();
						myAudio.setLyric(lyric_copy);
						String source_copy = resourceServer + jsonObject_partlist.getString("voice");
						myAudio.setSource(source_copy);
						play_list.add(myAudio);
						try
						{
							Thread.sleep(2 * 1000);
						}
						catch(InterruptedException e)
						{
							System.out.println(e);
						}
						return result;
					}

				});
			}
		}
	}

	private void initSpinner()
	{

		initLyric();
		play_list_copy.clear();
		for(int i = 0 ; i < play_list.size() ; i ++ )
		{
			play_list_copy.add(play_list.get(i).getName());
		}
		ArrayAdapter < String > adapter;
		adapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , play_list_copy);

		spinner.setAdapter(adapter);
		progressDialog.dismiss();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				currIndex = arg2;
				initLyric();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}
		});

		stopRecord.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				if(play_currentState != IDLE)
				{
					stopRecord();
				}
				else
				{
					Toast.makeText(getApplicationContext() ,"�뿪ʼ¼��" ,Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void initLyric()
	{
		lyricsPath = play_list.get(currIndex).getLyric();
		String content = "";
		// content = Util.getLrcContents(lyricsPath);
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
		String contents[] = content.split("\n");
		content = "";
		for(int i = 0 , leng = contents.length ; i < leng ; i ++ )
		{
			content += (contents[i].substring(contents[i].indexOf("]") + 1) + "\n");
		}
		// File mFile = new File(lyricsPath);
		// if( !mFile.exists())
		// {
		// content = "\n\n\n������Ļ";
		// }
		// else
		// {
		// FileInputStream mFileInputStream;
		// BufferedReader mBufferedReader = null;
		// String Lrc_data = "";
		// try
		// {
		// mFileInputStream = new FileInputStream(mFile);
		// InputStreamReader mInputStreamReader;
		// mInputStreamReader = new InputStreamReader(mFileInputStream ,
		// "utf-8");
		// mBufferedReader = new BufferedReader(mInputStreamReader);
		// while((Lrc_data = mBufferedReader.readLine()) != null)
		// {
		// content += (Lrc_data.substring(Lrc_data.indexOf("]") + 1) + "\n");
		// }
		//
		// }
		// catch(Exception e)
		// {
		// System.out.println(e);
		// }
		// finally
		// {
		// try
		// {
		// mBufferedReader.close();
		// }
		// catch(IOException e)
		// {
		// System.out.println(e);
		// }
		// }
		// }
		textView_lrcView.setText(content);

	}

	public void onDetailSetting(View v )
	{
		intent = new Intent();
		intent.putExtra("selected" ,grade);
		intent.setClass(Read.this ,ReadList.class);
		startActivity(intent);
	}

	public void repeatSwitching(View v )
	{
		switch(play_currentState)
		{
			case IDLE:
				play_currentState = PAUSE;
				startRecord.setImageResource(R.drawable.play);
				startRecord();
				recordTime();
				break;
			case PAUSE:
				play_currentState = START;
				startRecord.setImageResource(R.drawable.record_pause);
				mediaRecorder.stop();
				mediaRecorder.release();
				timer.cancel();
				myRecordList.add(fileAllNameAmr);

				break;
			case START:
				play_currentState = PAUSE;
				startRecord.setImageResource(R.drawable.play);
				startRecord();
				recordTime();
				break;
			default:
				break;
		}

	}

	// ���¼��
	private void stopRecord()
	{
		play_currentState = IDLE;
		mediaRecorder.release();
		mediaRecorder = null;
		myRecordList.add(fileAllNameAmr);
		startRecord.setImageResource(R.drawable.record_pause);
		timer.cancel();

		final EditText editText = new EditText(Read.this);
		final AlertDialog.Builder inputDialog = new AlertDialog.Builder(Read.this);
		inputDialog.setTitle("Ҫ����¼����").setView(editText);
		inputDialog.setPositiveButton("����" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
			}
		});

		inputDialog.setNegativeButton("����" ,new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				time.setText("");
				for(int i = 0 ; i < myRecordList.size() ; i ++ )
				{
					File file = new File(myRecordList.get(i));
					if(file.exists())
					{
						file.delete();
					}
				}
			}
		});

		minute = 0;
		hour = 0;
		second = 0;

		final AlertDialog dialog = inputDialog.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view )
			{
				String playName = editText.getText().toString().trim();
				if(playName.isEmpty() || Judge.isNotName(playName))
				{
					Toast.makeText(getApplicationContext() ,"�������ֲ�����Ҫ������������" ,Toast.LENGTH_SHORT).show();
				}
				else
					if(isExit(playName))
					{
						Toast.makeText(getApplicationContext() ,"�ļ����ظ�������������" ,Toast.LENGTH_SHORT).show();
					}
					else
					{
						fileAllNameAmr = recordPath + playName + ".amr";
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
								fileInputStream = new FileInputStream(file);
								byte [] mByte = new byte [fileInputStream.available()];
								int length = mByte.length;
								if(i == 0)
								{
									while(fileInputStream.read(mByte) != -1)
									{
										fileOutputStream.write(mByte ,0 ,length);
									}
								}
								else
								{
									while(fileInputStream.read(mByte) != -1)
									{
										fileOutputStream.write(mByte ,6 ,length - 6);
									}
								}
							}
							Toast.makeText(getApplicationContext() ,"¼�����" ,Toast.LENGTH_SHORT).show();
							time.setText("¼�����");
						}
						catch(Exception e)
						{
							Toast.makeText(getApplicationContext() ,"¼���ϳɳ��������ԣ�" ,Toast.LENGTH_LONG).show();
							time.setText("¼���ϳɳ��������ԣ�");
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
						dialog.dismiss();
					}
			}
		});

	}

	private Boolean isExit(String name )
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

		// �ж�SD���Ƿ����
		if( !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this ,"SD��״̬�쳣��" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			// ���ݺ�׺�������жϡ���ȡ�ļ����е���Ƶ�ļ�
			File file = new File(recordPath);
			File files[] = file.listFiles();
			String childFileName = null;
			for(File childFile : files)
			{
				childFileName = childFile.toString();
				if(childFileName.length() > 0 && (childFileName.endsWith(".amr")))
				{
					if((childFileName.substring(childFileName.lastIndexOf("/") + 1 ,childFileName.lastIndexOf("."))).equals(name))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	// ��ʼ¼��
	@SuppressWarnings("deprecation")
	private void startRecord()
	{
		myRecordList.clear();
		File file = new File(recordPath);
		if( !file.exists())
		{
			file.mkdirs();
		}
		fileAllNameAmr = recordPath + getTime() + ".amr";
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// ѡ��amr��ʽ
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mediaRecorder.setOutputFile(fileAllNameAmr);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try
		{
			mediaRecorder.prepare();
		}
		catch(Exception e)
		{
			// ��¼��������ʧ�ܾ���Ҫ����Ӧ�ã����ε���ť�ĵ���¼��� �������ָ����쳣��
			Toast.makeText(this ,"¼��������ʧ�ܣ��뷵�����ԣ�" ,Toast.LENGTH_LONG).show();
			mediaRecorder.release();
			mediaRecorder = null;
			this.finish();
		}
		if(mediaRecorder != null)
		{
			mediaRecorder.start();
		}

	}

	// ��ʱ���첽���½���
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg )
		{
			time.setText("�����ε�¼��ʱ��Ϊ��" + String.format("%1$02d:%2$02d:%3$02d" ,hour ,minute ,second));
			super.handleMessage(msg);
		}
	};

	// ¼����ʱ
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

	// ��õ�ǰʱ��
	@SuppressLint("SimpleDateFormat")
	private String getTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		String time = formatter.format(curDate);
		return time;
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
				if(mediaRecorder != null)
				{
					mediaRecorder.release();
					mediaRecorder = null;
				}
				onBackPressed();
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
			if(mediaRecorder != null)
			{
				mediaRecorder.release();
				mediaRecorder = null;
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
		super.onDestroy();
	}

}
