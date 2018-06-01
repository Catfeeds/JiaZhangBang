/**
 * 
 */
package com.runcom.jiazhangbang.reciteText;

import java.util.ArrayList;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.ShareUtils;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

/**
 * @author Administrator
 * 
 */
public class ReciteTextTextChoose extends Activity
{
	// private Intent intent = new Intent();
	private int course , grade , phase , unit;
	private SwipeMenuListView listView;
	private MyText myText = new MyText();
	private ArrayList < MyText > textID = new ArrayList < MyText >();
	private ArrayList < MyText > textList = new ArrayList < MyText >();
	private MyListViewAdapter adapter;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recite_text_listview);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[Util.ReciteTextTextChoose] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[Util.ReciteTextTextChoose] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[Util.ReciteTextTextChoose] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[Util.ReciteTextTextChoose] ,unit);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "背诵检查" + Util.grade[grade] + "上学期" + Util.unit[unit];
		if(2 == phase)
			content = "背诵检查" + Util.grade[grade] + "下学期" + Util.unit[unit];
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
			map.put("unit" ,0 == unit ? -- unit + "" : unit + "");
			System.out.println(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "gettextlist.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
			{
				@Override
				public void onError(Call arg0 , Exception arg1 , int arg2 )
				{
					Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
					System.out.println(arg1);
					finish();
				}

				@Override
				public void onResponse(String arg0 , int arg1 )
				{
					if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
					{
						System.out.println("执行");
						initTextLrc();
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
					textID.clear();
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
							myText = new MyText();
							myText.setId(textListJsonObject.getString("id"));
							myText.setName(textListJsonObject.getString("title"));
							myText.setMode(textListJsonObject.getString("desc"));
							textID.add(myText);
						}
						else
							if(1 < part)
							{
								JSONArray subjsonArray = new JSONArray(textListJsonObject.getString("partlist"));
								for(int k = 0 , length = subjsonArray.length() ; k < length ; k ++ )
								{
									myText = new MyText();
									JSONObject subjsonObject = new JSONObject(subjsonArray.getString(k));
									myText.setId(subjsonObject.getString("id"));
									myText.setName(subjsonObject.getString("title"));
									myText.setMode(subjsonObject.getString("desc"));
									textID.add(myText);
								}
							}
					}
					return result;
				}
			});
		}
	}

	private void initTextLrc()
	{
		TreeMap < String , String > map = null;
		final int leng = textID.size();
		textList.clear();
		for(int i = 0 ; i < leng ; i ++ )
		{
			final int ii = i;
			map = Util.getMap(getApplicationContext());
			map.put("textid" ,textID.get(i).getId());
			System.out.println(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map));
			OkHttpUtils.get().url(Util.REALSERVER + "getfulltext.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
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
					if(ii == leng - 1)
					{
						initListView();
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
					JSONObject jsonObject = new JSONObject(arg0.body().string().trim());
					String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
					if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
					{
						return result;
					}
					JSONObject jsonObject_attr = new JSONObject(jsonObject.getString("attr"));
					JSONObject jsonObject_partlist = new JSONObject(jsonObject_attr.getString("partlist"));
					String lyric_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("subtitle");
					String title = jsonObject_partlist.getString("title");
					String voice = jsonObject_partlist.getString("voice");
					myText = new MyText();
					myText.setName(title);
					myText.setLyric(lyric_copy);
					myText.setLink(voice);
					textList.add(myText);
					// if( !new File(Util.LYRICSPATH + title + ".lrc").exists())
					// new LrcFileDownloader(lyric_copy , title +
					// ".lrc").start();
					return result;
				}
			});
		}
	}

	private void initListView()
	{
		listView = (SwipeMenuListView) findViewById(R.id.recitText_swipeMenu_listView);
		adapter = new MyListViewAdapter(getApplicationContext() , textList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		progressDialog.dismiss();

		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				// Toast.makeText(getApplicationContext() ,"您点击了" +
				// textList.get(arg2).getName().toString()
				// ,Toast.LENGTH_SHORT).show();
				Intent open_intent = new Intent(getApplicationContext() , ReciteTextMain.class);
				open_intent.putExtra("name" ,textList.get(arg2).getName());
				open_intent.putExtra("lrc" ,textList.get(arg2).getLyric());
				startActivity(open_intent);
			}

		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				Toast.makeText(getApplicationContext() ,"您长按了" + textList.get(arg2).getName().toString() ,Toast.LENGTH_SHORT).show();
				return false;
			}

		});

		listView.setOnSwipeListener(new OnSwipeListener()
		{
			@Override
			public void onSwipeStart(int arg0 )
			{
			}

			@Override
			public void onSwipeEnd(int arg0 )
			{
			}
		});

		SwipeMenuCreator creator = new SwipeMenuCreator()
		{
			@Override
			public void create(SwipeMenu menu )
			{
				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9 ,0xC9 ,0xCE)));
				openItem.setWidth(Util.dp2px(getApplicationContext() ,90));
				openItem.setTitle("Open");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.BLACK);
				menu.addMenuItem(openItem);

				SwipeMenuItem shareItem = new SwipeMenuItem(getApplicationContext());
				shareItem.setBackground(new ColorDrawable(Color.rgb(0xF9 ,0x3F ,0x25)));
				shareItem.setWidth(Util.dp2px(getApplicationContext() ,90));
				shareItem.setTitle("Share");
				shareItem.setTitleSize(18);
				shareItem.setTitleColor(Color.BLACK);

				menu.addMenuItem(shareItem);
			}
		};
		listView.setMenuCreator(creator);

		listView.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(int position , SwipeMenu menu , int index )
			{
				switch(index)
				{
					case 0:
						// Toast.makeText(getApplicationContext() ,"您点击了" +
						// textList.get(position).getName().toString()
						// ,Toast.LENGTH_SHORT).show();
						Intent open_intent = new Intent(getApplicationContext() , ReciteTextMain.class);
						open_intent.putExtra("name" ,textList.get(position).getName());
						open_intent.putExtra("lrc" ,textList.get(position).getLyric());
						startActivity(open_intent);
						break;
					case 1:
						//TODO 音频分享微信有问题 https://developer.umeng.com/docs/66632/detail/66799
						// Toast.makeText(getApplicationContext() ,"正在分享" +
						// textList.get(position).getName().toString() + "..."
						// ,Toast.LENGTH_SHORT).show();
						new ShareUtils(ReciteTextTextChoose.this).shareMultipleMusic(textList.get(position).getName() ," " ,Util.RESOURCESERVER + textList.get(position).getLink() ,R.drawable.ic_launcher);
						// Intent share_intent = new Intent(Intent.ACTION_SEND);
						// share_intent.setType("text/*");
						// share_intent.putExtra(Intent.EXTRA_SUBJECT ,"Share");
						// String url = textList.get(position).getLyric();
						// share_intent.putExtra(Intent.EXTRA_TEXT ,url);
						// share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// startActivity(Intent.createChooser(share_intent
						// ,"分享"));
						break;
				}
				return false;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult(requestCode ,resultCode ,data);
		UMShareAPI.get(this).onActivityResult(requestCode ,resultCode ,data);
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
