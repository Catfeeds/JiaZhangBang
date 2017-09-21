/**
 * 
 */
package com.runcom.jiazhangbang.findNewWords;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.voice.Text2Speech;
import com.runcom.jiazhangbang.R;
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

	Intent intent = new Intent();
	int selected;
	String contents;

	AutoCompleteTextView autoCompleteTextView;
	TextView contentsShowTextView;
	ImageView delete_imageView , search_imageView;
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
		initData();
	}

	/**
	 * 
	 */
	private void initData()
	{

		String [] autoCompleteTextViewArrayString =
		{ "abc", "wgc", "wgcwgc", "bbc", "java", "android", "And", "bbb", "autoComplete", "asdfasdfasdfasdfasdf" };
		ArrayAdapter < String > autoCompleteTextViewArrayAdapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.simple_dropdown_item_1line , autoCompleteTextViewArrayString);

		autoCompleteTextView.setAdapter(autoCompleteTextViewArrayAdapter);
		autoCompleteTextView.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s , int start , int before , int count )
			{
				delete_imageView.setVisibility(ImageView.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s , int start , int count , int after )
			{
			}

			@Override
			public void afterTextChanged(Editable s )
			{
//				contents = s.toString();
//				loadingData(s.toString());
			}
		});
		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
//				contents = parent.getItemAtPosition(position).toString();
//				loadingData(contents);
			}
		});

		delete_imageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				autoCompleteTextView.setText("");
				delete_imageView.setVisibility(ImageView.INVISIBLE);
			}
		});

		search_imageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				contents = autoCompleteTextView.getText().toString();
				loadingData(contents);
			}
		});
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void translate(View v)
	{
		contents = autoCompleteTextView.getText().toString();
//		intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fanyi.baidu.com/#zh/en/" + contents));
		intent = new Intent(this , FindNewWordsWeb.class);
		intent.putExtra("contents" ,contents);
	    if(contents.trim().isEmpty())
	    	Toast.makeText(getApplicationContext() , "内容为空" , Toast.LENGTH_SHORT).show();
	    else
	    	startActivity(intent);
	}
	
	public void text2Speech(View v)
	{
		contents = autoCompleteTextView.getText().toString();
		if(contents.trim().isEmpty())
			Toast.makeText(this , "内容为空" , Toast.LENGTH_SHORT).show();
		else
			new Text2Speech(getApplicationContext() ,contents).play();
	}

	private void loadingData(String contents )
	{
		String realContents = "\n\n 解释:\n\n\t" + "asdfasdfasadasdfaf\n详见翻译" + "\n";
		if(contents.trim().isEmpty())
			Toast.makeText(this , "内容为空" , Toast.LENGTH_SHORT).show();
		else
			contentsShowTextView.setText("\n " + autoCompleteTextView.getText().toString() + realContents);
	}

	/**
	 * 
	 */
	private void initView()
	{
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.find_new_words_search_auto_complete_text_view);
		contentsShowTextView = (TextView) findViewById(R.id.find_new_words_contents_show);
		delete_imageView = (ImageView) findViewById(R.id.find_new_words_delete_image_view);
		search_imageView = (ImageView) findViewById(R.id.find_new_words_search_image_view);
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
