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

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.inputmethod.EditorInfo;
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
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.NetUtil;
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
	private int course , grade , phase , unit;
	private String contents;

	private AutoCompleteTextView autoCompleteTextView;
	private TextView contentsShowTextView;
	private ImageView deleteImageView , searchImageView;

	private Map < String , String > newWordsMap = null;
	private String [] autoCompleteTextViewArrayString = null;
	private String [] autoCompleteTextViewArrayString1 = null;
	private String [] autoCompleteTextViewArrayString2 = null;
	private int note = 0;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_new_words_main);

		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		course = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.courseSharedPreferencesKeyString[Util.FindNewWords] ,course) + 1;
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		grade = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[Util.FindNewWords] ,grade) + 1;
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		phase = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[Util.FindNewWords] ,phase) + 1;
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);
		unit = MySharedPreferences.getValue(getApplicationContext() ,Util.settingChooseSharedPreferencesKey ,Util.unitSharedPreferencesKeyString[Util.FindNewWords] ,unit);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "查生词" + Util.grade[grade] + "上学期" + Util.unit[unit];
		if(2 == phase)
			content = "查生词" + Util.grade[grade] + "下学期" + Util.unit[unit];
		actionbar.setTitle(content);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取数据......");
		progressDialog.show();

		initView();
		if(NetUtil.getNetworkState(getApplicationContext()) == NetUtil.NETWORK_NONE)
		{
			Toast.makeText(getApplicationContext() ,Util.okHttpUtilsInternetConnectExceptionString ,Toast.LENGTH_SHORT).show();
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		}
		else
		{
			initData1();
		}
	}

	private void initData1()
	{
		TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,course + "");
		map.put("grade" ,grade + "");
		map.put("phase" ,phase + "");
		map.put("unit" ,0 == unit ? -- unit + "" : unit + "");
		System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{

			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				initData2();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				initData2();
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
				JSONArray jsonArray = jsonObject.getJSONArray("phlist");
				newWordsMap = new TreeMap < String , String >();
				note = 0;
				String phrase = null;
				String pinyin = null;
				String desc = null;
				String type = null;
				JSONObject phlistJsonObject = null;
				int leng = jsonArray.length();
				if(leng <= 0)
				{
					return Util.okHttpUtilsResultExceptionStringValue;
				}
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
					// System.out.println(i + "phrase:" + phrase + "pinyin:" +
					// pinyin + "desc:" + desc + "type:" + type);
				}
				return result;
			}
		});
	}

	/**
	 * 
	 */
	private void initData2()
	{
		TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("course" ,course + "");
		map.put("grade" ,grade + "");
		map.put("phase" ,phase + "");
		map.put("unit" ,0 == unit ? -- unit + "" : unit + "");
		System.out.println(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "getphrase.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
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
				if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
				{
					Toast.makeText(getApplicationContext() ,Util.okHttpUtilsMissingResourceString ,Toast.LENGTH_LONG).show();
					finish();
				}
				else
					if(autoCompleteTextViewArrayString1.length > 0 || autoCompleteTextViewArrayString2.length > 0)
					{
						initData();
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
				String response = arg0.body().string().trim();
				// System.out.println(response);
				JSONObject jsonObject = new JSONObject(response);
				String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
				// System.out.println(result);
				autoCompleteTextViewArrayString2 = new String [0];
				if( !Util.okHttpUtilsResultOkStringValue.equals(result))
				{
					return result;
				}
				JSONArray jsonArray = jsonObject.getJSONArray("phlist");
				note = 0;
				String phrase = null;
				String pinyin = null;
				String desc = null;
				String type = null;
				JSONObject phlistJsonObject = null;
				int leng = jsonArray.length();
				if(leng <= 0)
				{
					return Util.okHttpUtilsResultExceptionStringValue;
				}
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
					// System.out.println("-" + i + "phrase:" + phrase +
					// "pinyin:" + pinyin + "desc:" + desc + "type:" + type);
				}
				return result;
			}

		});

	}

	private void initData()
	{
		// System.out.println(newWordsMap);

		int leng1 = autoCompleteTextViewArrayString1.length;
		int leng2 = autoCompleteTextViewArrayString2.length;

		// for(int i = 0 ; i < leng1 ; i ++ )
		// {
		// System.out.println("autoCompleteTextViewArrayString1:" + i +
		// autoCompleteTextViewArrayString1[i]);
		// }
		// for(int i = 0 ; i < leng2 ; i ++ )
		// {
		// System.out.println("autoCompleteTextViewArrayString2:" + i +
		// autoCompleteTextViewArrayString2[i]);
		// }

		autoCompleteTextViewArrayString = new String [leng1 + leng2];
		// autoCompleteTextViewArrayString = new String
		// [leng1];
		System.arraycopy(autoCompleteTextViewArrayString1 ,0 ,autoCompleteTextViewArrayString ,0 ,leng1);
		System.arraycopy(autoCompleteTextViewArrayString2 ,0 ,autoCompleteTextViewArrayString ,leng1 ,leng2);
		// for(int i = 0 , leng = autoCompleteTextViewArrayString.length ; i <
		// leng ; i ++ )
		// {
		// System.out.println(i + "\tautoCompleteTextViewArrayString\t" +
		// autoCompleteTextViewArrayString[i]);
		// }

		ArrayAdapter < String > autoCompleteTextViewArrayAdapter = new ArrayAdapter < String >(getApplicationContext() , R.layout.simple_dropdown_item_1line , autoCompleteTextViewArrayString);
		autoCompleteTextView.setAdapter(autoCompleteTextViewArrayAdapter);
		autoCompleteTextViewArrayAdapter.notifyDataSetChanged();
		progressDialog.dismiss();
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
			}
		});
		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView < ? > parent , View view , int position , long id )
			{
				contents = parent.getItemAtPosition(position).toString();
				loadingData(contents);
				hideOrShowInputMethod();
			}
		});

		autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v , int actionId , KeyEvent event )
			{
				// if(actionId == EditorInfo.IME_ACTION_SEND || (event != null
				// && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId
				// == EditorInfo.IME_ACTION_DONE)
				if(actionId == EditorInfo.IME_ACTION_DONE)
				{
					hideOrShowInputMethod();
					contents = autoCompleteTextView.getText().toString();
					String content = v.getText().toString();
					loadingData(contents);
					System.out.println(content + "\n" + contents);
					return true;
				}

				return false;
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
				hideOrShowInputMethod();
				contents = autoCompleteTextView.getText().toString();
				loadingData(contents);
			}
		});

	}

	private void hideOrShowInputMethod()
	{
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0 ,InputMethodManager.HIDE_NOT_ALWAYS);
	}

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
		if(contents.trim().isEmpty() || contents.trim().equals("") || contents.trim() == null)
		{
			Toast.makeText(this ,"内容为空" ,Toast.LENGTH_SHORT).show();
			contentsShowTextView.setText("");
		}
		else
		{
			String content = newWordsMap.get(contents);
			// System.out.println(content + "--" + contents);
			if(content == null)
			{
				Toast.makeText(this ,grade + "年级课文中不存在该生词，请重新输入" ,Toast.LENGTH_SHORT).show();
				autoCompleteTextView.setText("");
				deleteImageView.setVisibility(ImageView.INVISIBLE);
				contentsShowTextView.setText("");
			}
			else
			{
				contentsShowTextView.setText("\n\t" + contents + "\n\n" + content);
			}
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
		System.out.println("keyCode:" + keyCode + "\tevent:" + event);
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
