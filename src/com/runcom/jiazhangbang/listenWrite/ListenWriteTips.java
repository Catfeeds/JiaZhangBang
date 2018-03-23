package com.runcom.jiazhangbang.listenWrite;

import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.setting.ListenWriteTipsPlaySetting;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteTips extends Activity
{
	private Intent intent;
	private int selected , unit;
	private int phase = 1;// 1：上学期;2：下学期
	private int intervalValue , frequencyValue;
	private int counts;
	private TextView textView_information , textView_start , textView_reset;
	private final int WORDSTIME = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_tips);

		intent = getIntent();
		selected = intent.getIntExtra("selected" ,1);// 年级
		phase = intent.getIntExtra("phase" ,1);// 上下册
		unit = intent.getIntExtra("id" ,1);// 单元
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听写 " + selected + "年级上册第" + unit + "单元";
		if(2 == phase)
			content = "听写 " + selected + "年级下册第" + unit + "单元";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		initData();
	}

	private void initData()
	{
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			final TreeMap < String , String > map = Util.getMap(getApplicationContext());
			map.put("course" ,Util.ChineseCourse);
			map.put("grade" ,selected + "");
			map.put("phase" ,phase + "");
			map.put("unit" ,unit + "");
			System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{
				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					String [] config = arg0.split("#");
					// for(int j = 0 ; j < config.length ; j ++ )
					// {
					// System.out.println(config[j]);
					// }
					if( !config[0].equals("0"))
					{
						textView_start.setEnabled(false);
						textView_reset.setEnabled(false);
						// Toast.makeText(getApplicationContext() ,config[1]
						// ,Toast.LENGTH_LONG).show();
					}
					else
						initView();
				}

				@Override
				public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
				{
					// System.out.println("***********************");
					String response = arg0.body().string().trim();
					JSONObject jsonObject = new JSONObject(response);
					String result = jsonObject.getString("result");
					String mesg = jsonObject.getString("mesg");
					JSONArray jsonArray = jsonObject.getJSONArray("attr");
					counts = jsonArray.length();
					// JSONObject phraseJsonObject = new
					// JSONObject(jsonArray.getString(5));
					// String phrase = phraseJsonObject.getString("phrase");
					// System.out.println("counts: " + counts + "\njson: " +
					// jsonObject.toString() + "\nphrase: " + phrase);
					return result + "#" + mesg;
				}
			});
		}
	}

	private void initView()
	{
		// 两个词语间隔秒数
		intervalValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteInterval" ,1);
		// 每个词语阅读次数
		frequencyValue = MySharedPreferences.getValue(this ,"ListenWriteSetting" ,"ListenWriteFrequency" ,1);

		textView_information = (TextView) findViewById(R.id.listen_write_tips_informations);
		textView_start = (TextView) findViewById(R.id.listen_write_tips_start);
		textView_start.setEnabled(true);
		textView_reset = (TextView) findViewById(R.id.listen_write_tips_reset);
		textView_reset.setEnabled(true);
		int totalTime = (WORDSTIME + intervalValue) * counts * frequencyValue;
		int hours = totalTime / 3600;
		int minutes = totalTime % 3600 / 60;
		int seconds = totalTime % 3600 % 60;
		String contents = "本次听写\n总共" + counts + "个生词\n生词朗读间隔" + intervalValue + "秒\n每个生词朗读" + frequencyValue + "遍\n大约用时" + totalTime + "秒\n即" + hours + "时" + minutes + "分" + seconds + "秒";

		textView_information.setText(contents);

		textView_start.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				intent = new Intent();
				intent.putExtra("selected" ,selected);
				intent.putExtra("phase" ,phase);
				intent.putExtra("units" ,unit);
				intent.setClass(getApplicationContext() ,ListenWriteMain.class);
				if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
				{
					Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
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
				intent.putExtra("selected" ,selected);
				intent.putExtra("units" ,unit);
				intent.setClass(getApplicationContext() ,ListenWriteTipsPlaySetting.class);
				if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
				{
					Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
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

}
