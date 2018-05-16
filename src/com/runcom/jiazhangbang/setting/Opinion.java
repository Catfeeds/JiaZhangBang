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
		actionbar.setTitle("意见反馈");

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
					textView_tips.setText("反馈内容不能为空哦，请再多描述一下吧");
					// Toast.makeText(getApplicationContext()
					// ,"反馈内容不能为空哦，请再多描述一下吧~" ,Toast.LENGTH_SHORT).show();
				}
				else
					if(opinionContent.length() < 5)
					{
						textView_tips.setText("字数不能低于5个字哦，请再多描述一下吧");
						// Toast.makeText(getApplicationContext()
						// ,"字数不能低于5个字哦，请再多描述一下吧~" ,Toast.LENGTH_SHORT).show();
					}
					else
						if(phoneNumberContent.isEmpty())
						{
							textView_tips.setText("手机号码不能为空哦");
							// Toast.makeText(getApplicationContext()
							// ,"手机号码不能为空哦~" ,Toast.LENGTH_SHORT).show();
						}
						else
							if( !Judge.isMobilePhoneNumber(phoneNumberContent))
							{
								textView_tips.setText("请输入真实的手机号码，以便客服及时回复您");
								// Toast.makeText(getApplicationContext()
								// ,"请输入真实的手机号码，以便客服及时回复您的反馈"
								// ,Toast.LENGTH_SHORT).show();
							}
							else
							{
								button_submit.setEnabled(false);// TODO
								textView_tips.setText("感谢您的意见和建议，我们会做的更好的");
								Toast.makeText(getApplicationContext() ,"感谢您的意见和建议，我们会做的更好的~" ,Toast.LENGTH_SHORT).show();
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
