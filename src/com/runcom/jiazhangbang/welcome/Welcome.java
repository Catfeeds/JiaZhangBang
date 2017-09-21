package com.runcom.jiazhangbang.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.judge.Judge;
import com.runcom.jiazhangbang.mainActivity.MainActivity;
import com.umeng.analytics.MobclickAgent;

public class Welcome extends Activity
{
	private EditText name_editText , phoneNumber_editText;
	String name , mobilePhoneNumber;

	private Spinner spinner;
	private ArrayAdapter < CharSequence > arrayAdapter;

	private Button login_button;

	int selected = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		arrayAdapter = ArrayAdapter.createFromResource(this ,R.array.classes ,android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner = (Spinner) findViewById(R.id.welcome_classes_spinner);
		spinner.setAdapter(arrayAdapter);
		// spinner.setGravity(Gravity.CENTER_HORIZONTAL);
		// s.setGravity(Gravity.END|Gravity.CENTER_HORIZONTAL);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				// selected = arg0.getItemAtPosition(arg2).toString();
				selected = arg2;
				// Toast.makeText(Welcome.this ,"what you selected is :" + arg2
				// + "*" + selected ,Toast.LENGTH_LONG).show();
				// Log.d("LOG" ,"what you selected is :" + selected);

			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}

		});

		name_editText = (EditText) findViewById(R.id.welcome_name_editText);
		phoneNumber_editText = (EditText) findViewById(R.id.welcome_mobilePhoneNumber_editText);
		login_button = (Button) findViewById(R.id.welcome_login_button);

		login_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				name = name_editText.getText().toString();
				mobilePhoneNumber = phoneNumber_editText.getText().toString();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext() ,MainActivity.class);
				intent.putExtra("selected" ,selected);
				Log.d("LOG" ,"name: " + name + " class: " + selected + " phoneNumber: " + mobilePhoneNumber);

				if(judge(name ,mobilePhoneNumber))
				{
					// startActivity(intent);
					finish();
				}
				else
					Toast.makeText(getApplicationContext() ,"姓名或者手机号不合法!!!" ,Toast.LENGTH_SHORT).show();
			}

		});

	}

	private boolean judge(String name , String mobilePhoneNumber )
	{
		// TODO Auto-generated method stub
		// Log.d("LOG" ,name + " " + name.length());
		if(name.length() >= 2 && Judge.isMobilePhoneNumber(mobilePhoneNumber))
			return true;
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.welcome ,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if(id == R.id.action_settings)
		// {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
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
