package com.runcom.jiazhangbang.listenWrite;

import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.setting.ListenWriteTipsSetting;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteTips extends Activity
{
	private Intent intent;
	private int course , grade , phase , unit;
	private int intervalValue , frequencyValue;
	private int counts;
	private TextView textView_time_information , textView_words_count;
	private TextView textView_interval_reduce , textView_interval_plus ,
	        textView_interval_count , textView_frequency_reduce ,
	        textView_frequency_plus , textView_frequency_count;
	private Button textView_start , textView_reset;
	private final int WORDSTIME = 2;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_tips);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.courseSharedPreferencesKeyString[Util.ListenWriteTips] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.gradeSharedPreferencesKeyString[Util.ListenWriteTips] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.phaseSharedPreferencesKeyString[Util.ListenWriteTips] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.unitSharedPreferencesKeyString[0] ,0);
		if(unit > 0)
		{
			unit -- ;
		}
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.sharedPreferencesKeySettingChoose ,Util.unitSharedPreferencesKeyString[Util.ListenWriteTips] ,unit);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听写 " + Util.grade[grade] + "上学期" + Util.unit[unit];
		if(2 == phase)
			content = "听写 " + Util.grade[grade] + "下学期" + Util.unit[unit];
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		initData();
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
			final TreeMap < String , String > map = Util.getMap(getApplicationContext());
			map.put("course" ,course + "");
			map.put("grade" ,grade + "");
			map.put("phase" ,phase + "");
			map.put("unit" ,++ unit + "");
			System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
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
					if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
					{
						textView_start.setEnabled(false);
						textView_reset.setEnabled(false);
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
						finish();
					}
					else
						if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
						{
							Toast.makeText(getApplicationContext() ,Util.okHttpUtilsMissingResourceString ,Toast.LENGTH_LONG).show();
							finish();
						}
						else
						{
							initView();
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
					JSONArray jsonArray = jsonObject.getJSONArray("phlist");
					counts = jsonArray.length();
					if(counts <= 0)
					{
						return Util.okHttpUtilsResultExceptionStringValue;
					}
					return result;
				}
			});
		}
	}

	private void setContents()
	{
		int totalTime = (WORDSTIME + intervalValue) * counts * frequencyValue;
		int hours = totalTime / 3600;
		int minutes = totalTime % 3600 / 60;
		int seconds = totalTime % 3600 % 60;
		String contents = hours + "时" + minutes + "分" + seconds + "秒";
		textView_time_information.setText(contents);
	}

	private void initView()
	{
		textView_time_information = (TextView) findViewById(R.id.listen_write_tips_informations_counts);
		textView_start = (Button) findViewById(R.id.listen_write_tips_start);
		textView_start.setEnabled(true);
		textView_reset = (Button) findViewById(R.id.listen_write_tips_reset);
		textView_reset.setEnabled(true);
		textView_words_count = (TextView) findViewById(R.id.listen_write_tips_words_count_textView);
		textView_words_count.setText(counts + "");
		// 两个词语间隔秒数
		intervalValue = MySharedPreferences.getValue(getApplicationContext() ,"ListenWriteSetting" ,"ListenWriteInterval" ,1);
		// 每个词语阅读次数
		frequencyValue = MySharedPreferences.getValue(getApplicationContext() ,"ListenWriteSetting" ,"ListenWriteFrequency" ,1);

		textView_interval_count = (TextView) findViewById(R.id.listen_write_tips_interval_count_textView);
		textView_interval_count.setText(intervalValue + "");

		textView_interval_reduce = (TextView) findViewById(R.id.listen_write_tips_interval_reduce_textView);
		textView_interval_reduce.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intervalValue -- ;
				if(intervalValue <= 1)
				{
					intervalValue = 1;
				}
				textView_interval_count.setText(intervalValue + "");
				MySharedPreferences.putValue(getApplicationContext() ,"ListenWriteSetting" ,"ListenWriteInterval" ,intervalValue);
				setContents();
			}
		});
		textView_interval_plus = (TextView) findViewById(R.id.listen_write_tips_interval_plus_textView);
		textView_interval_plus.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intervalValue ++ ;
				if(intervalValue >= 9)
				{
					intervalValue = 9;
				}
				textView_interval_count.setText(intervalValue + "");
				MySharedPreferences.putValue(getApplicationContext() ,"ListenWriteSetting" ,"ListenWriteInterval" ,intervalValue);
				setContents();
			}
		});
		textView_frequency_count = (TextView) findViewById(R.id.listen_write_tips_frequency_count_textView);
		textView_frequency_count.setText(frequencyValue + "");

		textView_frequency_reduce = (TextView) findViewById(R.id.listen_write_tips_frequency_reduce_textView);
		textView_frequency_reduce.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				frequencyValue -- ;
				if(frequencyValue <= 1)
				{
					frequencyValue = 1;
				}
				textView_frequency_count.setText(frequencyValue + "");
				MySharedPreferences.putValue(getApplicationContext() ,"ListenWriteSetting" ,"ListenWriteFrequency" ,frequencyValue);
				setContents();
			}
		});
		textView_frequency_plus = (TextView) findViewById(R.id.listen_write_tips_frequency_plus_textView);
		textView_frequency_plus.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				frequencyValue ++ ;
				if(frequencyValue >= 9)
				{
					frequencyValue = 9;
				}
				textView_frequency_count.setText(frequencyValue + "");
				MySharedPreferences.putValue(getApplicationContext() ,"ListenWriteSetting" ,"ListenWriteFrequency" ,frequencyValue);
				setContents();
			}
		});

		setContents();

		progressDialog.dismiss();
		textView_start.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intent = new Intent();
				intent.putExtra("selected" ,grade);
				intent.putExtra("phase" ,phase);
				intent.putExtra("units" ,unit);
				intent.setClass(getApplicationContext() ,ListenWriteMain.class);
				if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
				{
					Toast.makeText(getApplicationContext() ,Util.okHttpUtilsInternetConnectExceptionString ,Toast.LENGTH_SHORT).show();
					startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				}
				else
					startActivity(intent);
			}
		});

		textView_reset.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intent = new Intent();
				intent.putExtra("selected" ,grade);
				intent.putExtra("units" ,unit);
				intent.setClass(getApplicationContext() ,ListenWriteTipsSetting.class);
				if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
				{
					Toast.makeText(getApplicationContext() ,Util.okHttpUtilsInternetConnectExceptionString ,Toast.LENGTH_SHORT).show();
					startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				}
				else
					startActivity(intent);
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
				// Toast.makeText(getApplicationContext()
				// ,"onOptionsItemSelected..." ,Toast.LENGTH_SHORT).show();
				// System.out.println("onOptionsItemSelected...");
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
			// Toast.makeText(getApplicationContext() ,"onKeyDown..."
			// ,Toast.LENGTH_SHORT).show();
			// System.out.println("onKeyDown...");
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		// Toast.makeText(getApplicationContext() ,"onResume..."
		// ,Toast.LENGTH_SHORT).show();
		// System.out.println("onResume...");
		initView();
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
