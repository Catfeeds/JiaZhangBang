/**
 * 
 */
package com.runcom.jiazhangbang.reciteText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.LrcRead;
import com.runcom.jiazhangbang.listenText.LyricContent;
import com.runcom.jiazhangbang.util.NetUtil;
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

public class ReciteTextMain extends Activity implements Checkable
{
	private TextView autoJudge_textView , submitScore_textView ,
	        historyScore_textView;
	private Intent intent;
	private ListView listView;
	private MyListViewMainAdapter myListViewMainAdapter;
	private MyTextContent myTextContent = new MyTextContent();
	private ArrayList < MyTextContent > myTextContentArraylist = new ArrayList < MyTextContent >();
	private int dataMax;
	private int [] counts = null;
	private String [] scores = new String [11];
	private int note = 0 , temp = 0;
	private float ans = 100.0f;

	// private int selected , phase , unit;
	private String name , lrc;

	private List < LyricContent > LyricList = new ArrayList < LyricContent >();

	// String [] scores = { "第00次成绩:         50" ,
	// "第01次成绩:          57", "第02次成绩:          77", "第03次成绩:          87",
	// "第04次成绩:          97",
	// "第05次成绩:         100", "第06次成绩:          57", "第07次成绩:          77",
	// "第08次成绩:          87",
	// "第09次成绩:          97", "第10次成绩:         100", "第11次成绩:          57",
	// "第12次成绩:          77",
	// "第13次成绩:          87", "第14次成绩:          97", "第15次成绩:         100",
	// "第16次成绩:          57",
	// "第17次成绩:          77", "第18次成绩:          87", "第19次成绩:          97",
	// "第20次成绩:         100" };
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recite_text_main);

		intent = getIntent();
		// selected = intent.getIntExtra("selected" ,1);
		// phase = intent.getIntExtra("phase" ,1);
		// unit = intent.getIntExtra("unit" ,1);
		// id = intent.getStringExtra("id");
		name = intent.getStringExtra("name");
		lrc = intent.getStringExtra("lrc");

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		// new Text2Speech(getApplicationContext() , name).play();
		actionbar.setTitle(name);

		Arrays.fill(scores ,"       暂无");
		initView();
	}

	/**
	 * 
	 */
	private void initView()
	{
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,"请检查网络连接" ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			try
			{
				LrcRead lrcRead = new LrcRead();
				lrcRead.Read(Util.LYRICSPATH + lrc ,Util.lyricChinese);
				LyricList = lrcRead.GetLyricContent();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			dataMax = LyricList.size();
			counts = new int [dataMax];
			Arrays.fill(counts ,0);
			for(int i = 0 ; i < dataMax ; i ++ )
			{
				myTextContent = new MyTextContent();
				myTextContent.setName((i + 1) + "\u3000\u3000" + LyricList.get(i).getLyric());
				myTextContentArraylist.add(myTextContent);
			}
			initListview();

		}

		autoJudge_textView = (TextView) findViewById(R.id.recite_text_main_textview_autojudge);
		autoJudge_textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				int right = 0;
				String paragraph = "";
				for(int i = 0 ; i < dataMax ; i ++ )
					if(0 == counts[i])
						right ++ ;
					else
						paragraph += (i + 1) + "、";
				ans = (float) (right * 1.0 / dataMax) * 100;
				if(paragraph.isEmpty())
					Toast.makeText(getApplicationContext() ,dataMax + "系统自动判分  " + ans + " 分" + "\n恭喜你 继续保持哟！" ,Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext() ,dataMax + "系统自动判分  " + ans + " 分" + "\n错误段落为：\n" + paragraph.substring(0 ,paragraph.length() - 1) + "." ,Toast.LENGTH_SHORT).show();
			}
		});

		submitScore_textView = (TextView) findViewById(R.id.recite_text_main_textview_submitscore);
		submitScore_textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				int right = 0;
				for(int i = 0 ; i < dataMax ; i ++ )
					if(0 == counts[i])
						right ++ ;
				ans = (float) (right * 1.0 / dataMax) * 100;
				Toast.makeText(getApplicationContext() ,"您提交了 " + ans + " 分" ,Toast.LENGTH_SHORT).show();
				if(temp > 10)
					temp = 0;
				scores[temp] = "第" + (note + 1) + "次成绩：          " + ans;
				++ temp;
				++ note;
			}
		});

		historyScore_textView = (TextView) findViewById(R.id.recite_text_main_textview_history_score);
		historyScore_textView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ReciteTextMain.this , R.style.NoBackGroundDialog);
				builder.setIcon(R.drawable.ic_launcher);
				getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				// for(int i = 0 , leng = scores.length ; i < leng ; i ++ )
				// {
				// scores[i] = "第" + i + "次成绩：          " + ans;
				// }
				builder.setTitle("您的历史成绩");
				builder.setNegativeButton("确定" ,new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog , int which )
					{
						dialog.dismiss();
					}
				});
				// builder.setPositiveButton("关闭", new
				// DialogInterface.OnClickListener()
				// {
				// @Override
				// public void onClick(DialogInterface dialog, int which)
				// {
				// dialog.dismiss();
				// }
				//
				// });

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

	}

	private void initListview()
	{
		listView = (ListView) findViewById(R.id.recite_text_main_listview);
		myListViewMainAdapter = new MyListViewMainAdapter(getApplicationContext() , myTextContentArraylist);
		listView.setAdapter(myListViewMainAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// myListViewMainAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				// 列表文件的选中效果
				// if(whichSelecte != null)
				// {
				// whichSelecte.setBackgroundColor(getResources().getColor(R.color.no));
				// }
				// view.setBackgroundColor(getResources().getColor(R.color.yes));
				// whichSelecte = view;

				if(0 == counts[position])
				{
					counts[position] = 1;
					// myListViewMainAdapter.
					// view.setBackgroundColor(getResources().getColor(R.color.yes));
				}
				else
				{
					counts[position] = 0;
					// view.setBackgroundColor(getResources().getColor(R.color.no));
				}
				boolean isSelect = myListViewMainAdapter.getisSelectedAt(position);
				myListViewMainAdapter.setItemisSelectedMap(position , !isSelect);
				myListViewMainAdapter.notifyDataSetChanged();
				Toast.makeText(getApplication() ,"position : " + position + "\nstate : " + counts[position] ,Toast.LENGTH_SHORT).show();
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
	public void onCreateContextMenu(ContextMenu menu , View view , ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu(menu ,view ,menuInfo);
		// menu.removeItem(android.R.id.selectAll);
		// menu.removeItem(android.R.id.paste);
		// menu.removeItem(android.R.id.cut);
		menu.removeItem(android.R.id.copy);
		MenuItem item = menu.findItem(android.R.id.copy);

		try
		{
			String ChkMenu = item.getTitle().toString();
			Log.d("LOG" ,item.toString() + "\nchkmenu: " + ChkMenu);
			menu.add(0 ,1 ,0 ,"加入笔记");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

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
