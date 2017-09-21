package com.runcom.jiazhangbang.listenWrite;

import java.util.ArrayList;

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

import com.iflytek.voice.Text2Speech;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.MyAudio;
import com.runcom.jiazhangbang.util.NetUtil;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteBackups extends Activity
{
	private MyListenWriteAdapter myListenWriteMainAdapter;
	private MyAudio myAudio;
	private ArrayList < MyAudio > myListenWriteContentArrayList = new ArrayList < MyAudio >();
	private ListView listView;
	private int selected;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_backups);

		intent = getIntent();

		selected = intent.getIntExtra("selected" ,1);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = " 听写 " + selected + "年级";
		new Text2Speech(getApplicationContext() , content).play();
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
			// TreeMap < String , String > map =
			// Util.getMap(getApplicationContext());
			// map.put("course" ,"1");
			// map.put("grade" ,"4");
			// map.put("phase" ,"2");
			// map.put("unit" ,"1");

			String [] contents =
			{ "上册          第一单元", "第二单元", "第三单元", "第四单元", "第五单元", "第六单元", "第七单元", "第八单元", "下册          第一单元", "第二单元", "第三单元", "第四单元", "第五单元", "第六单元", "第七单元", "第八单元" };
			myListenWriteContentArrayList.clear();
			for(int i = 0 ; i < contents.length ; i ++ )
			{
				myAudio = new MyAudio();
				myAudio.setName(contents[i]);
				myListenWriteContentArrayList.add(myAudio);
			}

			initOnClick();
			// OkHttpUtils.get().url(Util.SERVERADDRESS_listenWriteBackups).build().execute(new
			// Callback < String >()
			// {
			// @Override
			// public void onError(Call arg0 , Exception arg1 , int arg2 )
			// {
			// }
			//
			// @Override
			// public void onResponse(String arg0 , int arg1 )
			// {
			// initOnClick();
			// }
			//
			// @Override
			// public String parseNetworkResponse(Response arg0 , int arg1 )
			// throws Exception
			// {
			// // String response = arg0.body().string().trim();
			// // JSONObject jsonObject = new JSONObject(response);
			//
			// // String source = jsonObject.getString("source");
			// // contents = source.split(",|，");
			// String [] contents =
			// { "第一单元", "第二单元", "第三单元", "第四单元", "第五单元", "第六单元", "第七单元", "第八单元"
			// };
			// myListenWriteContentArrayList.clear();
			// for(int i = 0 ; i < contents.length ; i ++ )
			// {
			// myAudio = new MyAudio();
			// myAudio.setName(contents[i]);
			// myListenWriteContentArrayList.add(myAudio);
			// }
			//
			// return null;
			// }
			//
			// });
		}
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
				intent.putExtra("selected" ,selected);
				intent.putExtra("id" ,id);
				intent.setClass(getApplicationContext() ,ListenWriteTips.class);
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
