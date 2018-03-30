package com.runcom.jiazhangbang.listenText;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenWrite.MyListenWriteAdapter;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenTextBackups extends Activity
{
	private MyListenWriteAdapter myListenWriteMainAdapter;
	private MyAudio myAudio;
	private ArrayList < MyAudio > myListenWriteContentArrayList = new ArrayList < MyAudio >();
	private ListView listView;
	private int selected , phase;
	private Intent intent;
	private String [] contents = null;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_backups);

		intent = getIntent();

		selected = intent.getIntExtra("selected" ,1);
		phase = intent.getIntExtra("phase" ,1);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听课文 " + selected + "年级上册";
		if(2 == phase)
			content = "听课文 " + selected + "年级下册";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

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
			TreeMap < String , String > map = Util.getMap(getApplicationContext());
			map.put("course" ,Util.ChineseCourse);
			map.put("grade" ,selected + "");
			map.put("phase" ,phase + "");
			System.out.println(Util.REALSERVER + "getunitlist.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getunitlist.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
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
						initArray();
					}
					else
						if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
						{
							Toast.makeText(getApplicationContext() ,Util.okHttpUtilsMissingResourceString ,Toast.LENGTH_LONG).show();
							finish();
						}
						else
						{
							Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_SHORT).show();
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
					JSONArray jsonArray = jsonObject.getJSONArray("unitlist");
					// System.out.println("jsonArray: " + jsonArray.toString());
					int leng = jsonArray.length();
					if(leng <= 0)
					{
						return Util.okHttpUtilsResultExceptionStringValue;
					}
					String unitlist;
					contents = new String [leng];
					for(int i = 0 ; i < leng ; i ++ )
					{
						unitlist = jsonArray.getString(i);
						JSONObject unitListJsonObject = new JSONObject(unitlist);
						String unit = unitListJsonObject.getString("name");
						contents[i] = unit;
						// System.out.println(contents[i] + "unit: " + unit);
					}
					return result;
				}
			});
		}
	}

	private void initArray()
	{

		myListenWriteContentArrayList.clear();
		int leng = contents.length;
		for(int i = 0 ; i < leng ; i ++ )
		{
			myAudio = new MyAudio();
			myAudio.setName(contents[i]);
			myListenWriteContentArrayList.add(myAudio);
		}

		initOnClick();

	}

	private void initOnClick()
	{
		listView = (ListView) findViewById(R.id.listenWrite_listView);
		myListenWriteMainAdapter = new MyListenWriteAdapter(getApplicationContext() , myListenWriteContentArrayList);
		listView.setAdapter(myListenWriteMainAdapter);
		myListenWriteMainAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				intent = new Intent();
				intent.putExtra("selected" ,selected);// 年级
				intent.putExtra("phase" ,phase);// 上下册
				intent.putExtra("id" ,(long) ++ id);// 单元
				intent.setClass(getApplicationContext() ,CopyOfListenText.class);
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
		// getMenuInflater().inflate(R.menu.time_setting ,menu);
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
