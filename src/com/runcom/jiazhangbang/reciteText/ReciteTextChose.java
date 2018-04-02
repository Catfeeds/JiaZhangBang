/**
 * 
 */
package com.runcom.jiazhangbang.reciteText;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
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
import com.runcom.jiazhangbang.util.LrcFileDownloader;
import com.runcom.jiazhangbang.util.NetUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Administrator
 * 
 */
public class ReciteTextChose extends Activity
{
	private Intent intent = new Intent();
	private int selected;
	private int phase;
	private int unit;
	private SwipeMenuListView listView;
	private MyText myText = new MyText();
	private ArrayList < MyText > textList = new ArrayList < MyText >();
	private ArrayList < String > lrcList = new ArrayList < String >();
	private MyListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recite_text_listview);

		intent = getIntent();
		selected = intent.getIntExtra("selected" ,1);
		phase = intent.getIntExtra("phase" ,1);
		unit = intent.getIntExtra("unit" ,1);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "背课文  " + Util.grade[selected] + "上册第" + unit + "单元";
		if(2 == phase)
			content = "背课文  " + Util.grade[selected] + "下册第" + unit + "单元";
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
			final TreeMap < String , String > map = Util.getMap(getApplicationContext());
			map.put("course" ,Util.ChineseCourse);
			map.put("grade" ,selected + "");
			map.put("phase" ,phase + "");
			map.put("unit" ,unit + "");
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
					textList.clear();
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
						myText = new MyText();
						if(1 == part)
						{
							myText.setId(textListJsonObject.getString("id"));
							myText.setName(textListJsonObject.getString("title"));
							myText.setMode(textListJsonObject.getString("desc"));
							textList.add(myText);
						}
						else
							if(1 < part)
							{
								JSONArray subjsonArray = new JSONArray(textListJsonObject.getJSONArray("partlist"));
								for(int k = 0 , length = subjsonArray.length() ; k < length ; k ++ )
								{
									JSONObject subjsonObject = new JSONObject(subjsonArray.getString(k));
									myText.setId(subjsonObject.getString("id"));
									myText.setName(subjsonObject.getString("title"));
									myText.setMode(subjsonObject.getString("desc"));
									textList.add(myText);
								}
							}
							else
							{
								Toast.makeText(getApplicationContext() ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
								System.exit(0);
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
		final int leng = textList.size();
		for(int i = 0 ; i < leng ; i ++ )
		{
			final int ii = i;
			map = Util.getMap(getApplicationContext());
			map.put("textid" ,textList.get(i).getId());
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
						if( !Util.okHttpUtilsResultStringKey.equalsIgnoreCase(arg0))
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
					if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
					{
						return result;
					}
					JSONObject jsonObject_attr = new JSONObject(jsonObject.getString("attr"));
					JSONObject jsonObject_partlist = new JSONObject(jsonObject_attr.getString("partlist"));
					String lyric_copy = Util.RESOURCESERVER + jsonObject_partlist.getString("subtitle");
					String title = jsonObject_partlist.getString("title");
					lrcList.add(title + ".lrc");
					if( !new File(Util.LYRICSPATH + title + ".lrc").exists())
						new LrcFileDownloader(lyric_copy , title + ".lrc").start();
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
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				// Toast.makeText(getApplicationContext() ,"您点击了" +
				// textList.get(arg2).getName().toString()
				// ,Toast.LENGTH_SHORT).show();
				Intent open_intent = new Intent(getApplicationContext() , ReciteTextMain.class);
				open_intent.putExtra("selected" ,selected);
				open_intent.putExtra("phase" ,phase);
				open_intent.putExtra("unit" ,arg2 + 1);
				open_intent.putExtra("name" ,textList.get(arg2).getName());
				open_intent.putExtra("id" ,textList.get(arg2).getId());
				open_intent.putExtra("lrc" ,lrcList.get(arg2));
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
						open_intent.putExtra("selected" ,selected);
						open_intent.putExtra("phase" ,phase);
						open_intent.putExtra("unit" ,position + 1);
						open_intent.putExtra("name" ,textList.get(position).getName());
						open_intent.putExtra("id" ,textList.get(position).getId());
						open_intent.putExtra("lrc" ,lrcList.get(position));
						startActivity(open_intent);
						break;
					case 1:
						// Toast.makeText(getApplicationContext() ,"正在分享" +
						// textList.get(position).getName().toString() + "..."
						// ,Toast.LENGTH_SHORT).show();
						Intent share_intent = new Intent(Intent.ACTION_SEND);
						share_intent.setType("text/*");
						share_intent.putExtra(Intent.EXTRA_SUBJECT ,"Share");
						String url = (textList.get(position).getSource().toString()).toString();
						share_intent.putExtra(Intent.EXTRA_TEXT ,url);
						share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(Intent.createChooser(share_intent ,"分享"));
						break;
				}
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.welcome ,menu);
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
}
