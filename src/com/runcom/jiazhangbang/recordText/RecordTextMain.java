package com.runcom.jiazhangbang.recordText;

import java.util.ArrayList;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.reciteText.MyText;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.PermissionUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class RecordTextMain extends Activity
{
	private int course , grade , phase , unit;
	private ProgressDialog progressDialog;
	private String textid , textname;
	private MyText myText = new MyText();
	private ArrayList < MyText > textList = new ArrayList < MyText >();
	private ListView record_text_main_listView;
	private RecordTextMainListViewAdapter recordTextMainListViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_text_main);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[Util.RecordText] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[Util.RecordText] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[Util.RecordText] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[Util.RecordText] ,unit);

		textid = getIntent().getStringExtra("textid");

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		textname = getIntent().getStringExtra("textname");
		actionbar.setTitle(textname);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		new PermissionUtil(this , Manifest.permission.RECORD_AUDIO);

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
			map.put("uid" ,MySharedPreferences.getValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"uid" ,null));
			map.put("textid" ,textid);
			System.out.println(Util.REALSERVER + "getslice.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getslice.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{

				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
					// Toast.makeText(getApplicationContext()
					// ,Util.okHttpUtilsConnectServerExceptionString
					// ,Toast.LENGTH_LONG).show();
					textList.clear();
					// TODO
					for(int i = 0 ; i < 17 ; i ++ )
					{
						myText = new MyText();
						myText.setLyric(i + textname);
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/001_1.mp3");
						textList.add(myText);
					}
					initView();
					// finish();
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
					{
						initView();
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
					JSONObject jsonObject = new JSONObject(arg0.body().string().trim());
					String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
					if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
					{
						return result;
					}
					JSONArray jsonArray = jsonObject.getJSONArray("slice");
					JSONObject textListJsonObject = null;
					int leng = jsonArray.length();
					if(leng <= 0)
					{
						return Util.okHttpUtilsResultExceptionStringValue;
					}
					textList.clear();
					for(int i = 0 ; i < leng ; i ++ )
					{
						textListJsonObject = new JSONObject(jsonArray.getString(i));
						myText = new MyText();
						myText.setLyric(textListJsonObject.getString("text"));
						myText.setSource(textListJsonObject.getString("voice"));
						textList.add(myText);
					}
					return Util.okHttpUtilsResultOkStringValue;
				}
			});
		}
	}

	private void initView()
	{
		// TODO textList
		record_text_main_listView = (ListView) findViewById(R.id.record_text_main_listView);
		recordTextMainListViewAdapter = new RecordTextMainListViewAdapter(RecordTextMain.this , textList);
		record_text_main_listView.setAdapter(recordTextMainListViewAdapter);
		recordTextMainListViewAdapter.notifyDataSetChanged();

		progressDialog.dismiss();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				if(progressDialog != null)
				{
					progressDialog.dismiss();
				}
				recordTextMainListViewAdapter.setMediaPlayerCancle();
				recordTextMainListViewAdapter.notifyDataSetChanged();

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

			recordTextMainListViewAdapter.setMediaPlayerCancle();
			recordTextMainListViewAdapter.notifyDataSetChanged();

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

		recordTextMainListViewAdapter.setMediaPlayerCancle();
		recordTextMainListViewAdapter.notifyDataSetChanged();

		super.onDestroy();
	}
}
