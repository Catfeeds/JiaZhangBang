package com.runcom.jiazhangbang.setting;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.judge.Judge;
import com.umeng.analytics.MobclickAgent;

public class Opinion extends Activity
{
	private EditText editText_opinion , editText_phoneNumber;
	private Button button_submit;
	private TextView textView_tips;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_opinion);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("�������");

		initView();

	}

	private void initView()
	{
		editText_opinion = (EditText) findViewById(R.id.setting_opinion_opinion_editText);
		editText_phoneNumber = (EditText) findViewById(R.id.setting_opinion_phone_number_editText);
		button_submit = (Button) findViewById(R.id.setting_opinion_submit_button);
		textView_tips = (TextView) findViewById(R.id.setting_opinion_opinion_tips);

		button_submit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				String opinionContent = editText_opinion.getText().toString().trim();
				String phoneNumberContent = editText_phoneNumber.getText().toString().trim();
				if(opinionContent.isEmpty())
				{
					textView_tips.setText("�������ݲ���Ϊ��Ŷ�����ٶ�����һ�°�");
					// Toast.makeText(getApplicationContext()
					// ,"�������ݲ���Ϊ��Ŷ�����ٶ�����һ�°�~" ,Toast.LENGTH_SHORT).show();
				}
				else
					if(opinionContent.length() < 5)
					{
						textView_tips.setText("�������ܵ���5����Ŷ�����ٶ�����һ�°�");
						// Toast.makeText(getApplicationContext()
						// ,"�������ܵ���5����Ŷ�����ٶ�����һ�°�~" ,Toast.LENGTH_SHORT).show();
					}
					else
						if(phoneNumberContent.isEmpty())
						{
							textView_tips.setText("�ֻ����벻��Ϊ��Ŷ");
							// Toast.makeText(getApplicationContext()
							// ,"�ֻ����벻��Ϊ��Ŷ~" ,Toast.LENGTH_SHORT).show();
						}
						else
							if( !Judge.isMobilePhoneNumber(phoneNumberContent))
							{
								textView_tips.setText("��������ʵ���ֻ����룬�Ա�ͷ���ʱ�ظ���");
								// Toast.makeText(getApplicationContext()
								// ,"��������ʵ���ֻ����룬�Ա�ͷ���ʱ�ظ����ķ���"
								// ,Toast.LENGTH_SHORT).show();
							}
							else
							{
								button_submit.setEnabled(false);// TODO
								textView_tips.setText("��л��������ͽ��飬���ǻ����ĸ��õ�");
								Toast.makeText(getApplicationContext() ,"��л��������ͽ��飬���ǻ����ĸ��õ�~" ,Toast.LENGTH_SHORT).show();
								finish();
							}
			}
		});
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
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart("PlaySettingScreen");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd("PlaySettingScreen");
		MobclickAgent.onPause(this);
	}
}
