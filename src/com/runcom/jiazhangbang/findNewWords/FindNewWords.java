/**
 * 
 */
package com.runcom.jiazhangbang.findnewwords;

import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.iflytek.voice.Text2Speech;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-10
 * @time 下午4:24:15
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.findNewWords
 * @file_name FindNewWords.java
 * @type_name FindNewWords
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class FindNewWords extends Activity
{

	private Intent intent = new Intent();
	private int selected;
	private String contents;

	private AutoCompleteTextView autoCompleteTextView;
	private TextView contentsShowTextView;
	private ImageView deleteImageView , searchImageView;

	private Map < String , String > newWordsMap = null;
	private String [] autoCompleteTextViewArrayString = null;
	private String [] autoCompleteTextViewArrayString1 = null;
	private String [] autoCompleteTextViewArrayString2 = null;
	private int note = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_new_words_main);

		selected = getIntent().getIntExtra("selected" ,1);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "查生词  " + selected + "年级";
		new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		initView();
		initDataBefore();
	}

	private void initDataBefore()
	{
		TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,Util.ChineseCourse);
		map.put("grade" ,selected + "");
		map.put("phase" ,"1");
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
				// initData();
				initDataAfter();
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				System.out.println(jsonObject.getString("result"));
				if( !"0".equals(jsonObject.getString("result").toString()))
				{
					Toast.makeText(getApplicationContext() ,jsonObject.getString("mesg") ,Toast.LENGTH_SHORT).show();
					return null;
				}
				JSONArray jsonArray = jsonObject.getJSONArray("phlist");
				newWordsMap = new TreeMap < String , String >();
				note = 0;
				String phrase = null;
				String pinyin = null;
				String desc = null;
				String type = null;
				JSONObject phlistJsonObject = null;
				int leng = jsonArray.length();
				autoCompleteTextViewArrayString1 = new String [leng];
				for(int i = 0 ; i < leng ; i ++ )
				{
					phrase = jsonArray.getString(i);
					phlistJsonObject = new JSONObject(phrase);
					phrase = phlistJsonObject.get("phrase").toString();
					autoCompleteTextViewArrayString1[note ++ ] = phrase;
					pinyin = "/*" + phlistJsonObject.get("pinyin").toString() + "*/\n";
					desc = "/*" + phlistJsonObject.get("desc").toString() + "*/\n";
					type = "/*" + phlistJsonObject.get("type").toString() + "*/\n";
					newWordsMap.put(phrase ,"pinyin:\n\t" + pinyin + "\ndesc:\n\t" + desc + "\ntype:\n\t" + type);
					System.out.println(i + "phrase:" + phrase + "pinyin:" + pinyin + "desc:" + desc + "type:" + type);
				}
				return null;
			}
		});
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void initData()
	{
		// TODO
		TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,Util.ChineseCourse);
		map.put("grade" ,selected + "");
		map.put("phase" ,"2");
		System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{

			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				initDataAfter();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				initDataAfter();
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				System.out.println(response);
				JSONObject jsonObject = new JSONObject(response);
				System.out.println(jsonObject.getString("result"));
				if( !"0".equals(jsonObject.getString("result").toString()))
				{
					Toast.makeText(getApplicationContext() ,jsonObject.getString("mesg") ,Toast.LENGTH_SHORT).show();
					return null;
				}
				JSONArray jsonArray = jsonObject.getJSONArray("phlist");
				note = 0;
				String phrase = null;
				String pinyin = null;
				String desc = null;
				String type = null;
				JSONObject phlistJsonObject = null;
				int leng = jsonArray.length();
				autoCompleteTextViewArrayString2 = new String [leng];
				for(int i = 0 ; i < leng ; i ++ )
				{
					phrase = jsonArray.getString(i);
					phlistJsonObject = new JSONObject(phrase);
					phrase = phlistJsonObject.get("phrase").toString();
					autoCompleteTextViewArrayString2[note ++ ] = phrase;
					pinyin = "/*" + phlistJsonObject.get("pinyin").toString() + "*/\n";
					desc = "/*" + phlistJsonObject.get("desc").toString() + "*/\n";
					type = "/*" + phlistJsonObject.get("type").toString() + "*/\n";
					newWordsMap.put(phrase ,"pinyin:\n\t" + pinyin + "\ndesc:\n\t" + desc + "\ntype:\n\t" + type);
					System.out.println(i + "phrase:" + phrase + "pinyin:" + pinyin + "desc:" + desc + "type:" + type);

				}
				return null;
			}

		});

	}

	private void initDataAfter()
	{
		System.out.println("gegezhixingle ");
		for(int i = 0 ; i < autoCompleteTextViewArrayString1.length ; i ++ )
		{
			System.out.println("autoCompleteTextViewArrayString1:" + i + autoCompleteTextViewArrayString1[i]);
		}
		// for(int i = 0 ; i < autoCompleteTextViewArrayString2.length ; i ++ )
		// {
		// System.out.println(autoCompleteTextViewArrayString2[i]);
		// }

		// autoCompleteTextViewArrayString = new String
		// [autoCompleteTextViewArrayString1.length +
		// autoCompleteTextViewArrayString2.length];
		autoCompleteTextViewArrayString = new String [autoCompleteTextViewArrayString1.length];
		System.arraycopy(autoCompleteTextViewArrayString1 ,0 ,autoCompleteTextViewArrayString ,0 ,autoCompleteTextViewArrayString1.length);
		// System.arraycopy(autoCompleteTextViewArrayString2
		// ,autoCompleteTextViewArrayString1.length
		// ,autoCompleteTextViewArrayString ,0
		// ,autoCompleteTextViewArrayString2.length);
		for(int i = 0 ; i < autoCompleteTextViewArrayString.length ; i ++ )
		{
			System.out.println("autoCompleteTextViewArrayString:" + i + autoCompleteTextViewArrayString[i]);
		}
		// autoCompleteTextViewArrayString =
		// { "abc", "wgc", "wgcwgc", "bbc", "java", "android", "And", "bbb",
		// "autoComplete", "asdfasdfasdfasdfasdf" };
		ArrayAdapter < String > autoCompleteTextViewArrayAdapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.simple_dropdown_item_1line , autoCompleteTextViewArrayString);

		autoCompleteTextView.setAdapter(autoCompleteTextViewArrayAdapter);
		autoCompleteTextView.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s , int start , int before , int count )
			{
				deleteImageView.setVisibility(ImageView.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s , int start , int count , int after )
			{
			}

			@Override
			public void afterTextChanged(Editable s )
			{
				// contents = s.toString();
				// loadingData(s.toString());
				// 隐藏输入法
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				// 显示或者隐藏输入法
				imm.toggleSoftInput(0 ,InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				// contents = parent.getItemAtPosition(position).toString();
				// loadingData(contents);
				// TODO
				// 隐藏输入法
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				// 显示或者隐藏输入法
				imm.toggleSoftInput(0 ,InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		deleteImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				autoCompleteTextView.setText("");
				deleteImageView.setVisibility(ImageView.INVISIBLE);
			}
		});

		searchImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				contents = autoCompleteTextView.getText().toString();
				loadingData(contents);
			}
		});

	}

	@SuppressWarnings("unused")
	private void HideOrShowInputMethod()
	{
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		// imm.toggleSoftInput(0 ,InputMethodManager.HIDE_NOT_ALWAYS);
		imm.toggleSoftInput(0 ,InputMethodManager.RESULT_HIDDEN);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void translate(View v )
	{
		contents = autoCompleteTextView.getText().toString();
		// intent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse("http://fanyi.baidu.com/#zh/en/" + contents));
		intent = new Intent(this , FindNewWordsWeb.class);
		intent.putExtra("contents" ,contents);
		if(contents.trim().isEmpty())
		{
			Toast.makeText(getApplicationContext() ,"内容为空" ,Toast.LENGTH_SHORT).show();
		}
		else
		{
			startActivity(intent);
		}
	}

	public void text2Speech(View v )
	{
		contents = autoCompleteTextView.getText().toString();
		if(contents.trim().isEmpty())
		{
			Toast.makeText(this ,"内容为空" ,Toast.LENGTH_SHORT).show();
		}
		else
		{
			new Text2Speech(getApplicationContext() , contents).play();
		}
	}

	private void loadingData(String contents )
	{
		String content = autoCompleteTextView.getText().toString();
		content = newWordsMap.get(content);
		String realContents = "\n\t" + autoCompleteTextView.getText().toString();
		if(contents.trim().isEmpty())
		{
			Toast.makeText(this ,"内容为空" ,Toast.LENGTH_SHORT).show();
		}
		else
		{
			contentsShowTextView.setText(realContents + "\n\n" + content);
		}
	}

	/**
	 * 
	 */
	private void initView()
	{
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.find_new_words_search_auto_complete_text_view);
		contentsShowTextView = (TextView) findViewById(R.id.find_new_words_contents_show);
		deleteImageView = (ImageView) findViewById(R.id.find_new_words_delete_image_view);
		searchImageView = (ImageView) findViewById(R.id.find_new_words_search_image_view);
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
			default:
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
