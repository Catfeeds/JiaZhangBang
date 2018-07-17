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
	private final ArrayList < MyText > textList = new ArrayList < MyText >();
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
			// OkHttpUtils.get().url("http://test.nutnet.cn:8800/interface/getslice.php?dev=1&os=2&term=3&textid=4&sign=1e905f32a4ac451d8388c64496e38fe7").build().execute(new
			// Callback < String >()
			OkHttpUtils.get().url("http://test.nutnet.cn:8800/interface/getslice.php?dev=1&os=2&term=3&textid=6&sign=8f47241932dd6127cd094b0f78377f60").build().execute(new Callback < String >()
			{

				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
					// Toast.makeText(getApplicationContext()
					// ,Util.okHttpUtilsConnectServerExceptionString
					// ,Toast.LENGTH_LONG).show();
					textList.clear();
					// TODO

					for(int i = 0 ; i < 1 ; i ++ )
					{
						myText = new MyText();
						myText.setLyric("独坐敬亭山001-1");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/001_1.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("唐 李白001-2");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/001_2.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("众鸟高飞尽001-3");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/001_3.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("孤云独去闲002");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/002.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("相看两不厌003");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/003.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("只有敬亭山004");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/004.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("第二课 桂林山水005");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/005.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("人们都说：“桂林山水甲天下。”006");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/006.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("我们乘着木船，荡漾在漓江上，007");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/007.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("来观赏桂林的山水。008");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/008.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("我看见过波澜壮阔的大海，009");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/009.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("欣赏过水平如镜的西湖，010");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/010.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("却从没看见过漓江这样的水。011");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/011.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("漓江的水真静啊，012");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/012.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("静得让你感觉不到它在流动；013");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/013.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("漓江的水真清啊014");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/014.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("清得可以看见江底的沙石；015");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/015.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("漓江的水真绿啊016");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/016.mp3");
						textList.add(myText);

						myText = new MyText();
						myText.setLyric("桂林的山真险啊，危峰兀立，怪石嶙峋，017");
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + "cn/4-2/mp3/017.mp3");
						textList.add(myText);

						System.out.println(textList);
					}
					// for(int i = 0 ; i < 17 ; i ++ )
					// {
					// myText = new MyText();
					// myText.setLyric(i + textname + textname);
					// myText.setSource(MySharedPreferences.getValue(getApplicationContext()
					// ,Util.resourceUrlHeadSharedPreferencesKey
					// ,Util.resourceUrlHeadSharedPreferencesKeyString
					// ,Util.RESOURCESERVER) + "cn/4-2/mp3/001_1.mp3");
					// textList.add(myText);
					// }
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
						myText.setSource(MySharedPreferences.getValue(getApplicationContext() ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,Util.RESOURCESERVER) + textListJsonObject.getString("voice"));
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
		record_text_main_listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
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
