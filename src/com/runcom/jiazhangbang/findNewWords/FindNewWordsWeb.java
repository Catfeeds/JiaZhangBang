/**
 * 
 */
package com.runcom.jiazhangbang.findnewwords;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.runcom.jiazhangbang.R;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-5-12
 * @time ����9:35:14
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.findNewWords
 * @file_name FindNewWordsWeb.java
 * @type_name FindNewWordsWeb
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class FindNewWordsWeb extends Activity
{
	private WebView webView;
	private ProgressDialog progressDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_new_words_webview);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "����";
		// new Text2Speech(getApplicationContext() , content).play();
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("���ڻ�ȡ����......");
		progressDialog.show();

		webView = (WebView) findViewById(R.id.find_new_words_contnets_show_webview);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.loadUrl("http://fanyi.baidu.com/#zh/en/" + getIntent().getStringExtra("contents"));
		// ����WebViewĬ��ʹ�õ�������ϵͳĬ�����������ҳ����Ϊ��ʹ��ҳ��WebView��
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view , String url )
			{
				// ����ֵ��true��ʱ�����ȥWebView�򿪣�Ϊfalse����ϵͳ�����������������
				view.loadUrl(url);
				return true;
			}
		});

		webView.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view , int newProgress )
			{
				progressDialog.dismiss();
				if(newProgress == 100)
				{
					// ��ҳ�������
					// Toast.makeText(getApplicationContext() , "��ҳ�������" ,
					// Toast.LENGTH_SHORT).show();
				}
				else
				{
					// ������
					// Toast.makeText(getApplicationContext() , "������..." ,
					// Toast.LENGTH_SHORT).show();
				}

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

	// ��д�����ؼ�
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
