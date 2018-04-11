package com.runcom.jiazhangbang.mainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.chinese.Chinese;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity
{

	private Spinner spinner;
	private ArrayAdapter < CharSequence > arrayAdapter;

	private TextView textView_one , textView_two , textView_three ,
	        textView_four , textView_five , textView_six;
	private int selected = 1;

	private ImageView Chinese_imageView , math_imageView , English_imageView;

	private TextView Chinese_textView , math_textView , English_textView;

	private TextView course_textView , animation_textView , story_textView;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SpeechUtility.createUtility(this ,SpeechConstant.APPID + "=590aeb53");
		arrayAdapter = ArrayAdapter.createFromResource(this ,R.array.classes ,R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		// SlidingMenu menu = new SlidingMenu(this);
		// menu.setMode(SlidingMenu.LEFT);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// menu.setShadowWidthRes(R.dimen.normal_view_margin18);
		// menu.setShadowDrawable(R.drawable.app_ic);
		// menu.setBehindOffsetRes(R.dimen.normal_view_margin18);
		// menu.setFadeDegree(0.35f);
		// menu.attachToActivity(this ,SlidingMenu.SLIDING_CONTENT);
		// menu.setMenu(R.layout.asdf);

		spinner = (Spinner) findViewById(R.id.main_spinner);
		spinner.setAdapter(arrayAdapter);
		selected = getIntent().getIntExtra("selected" ,0);
		spinner.setSelection(selected);
		++ selected;

		textView_one = (TextView) findViewById(R.id.main_top_one_textview);
		textView_two = (TextView) findViewById(R.id.main_top_two_textview);
		textView_three = (TextView) findViewById(R.id.main_top_three_textview);
		textView_four = (TextView) findViewById(R.id.main_top_four_textview);
		textView_five = (TextView) findViewById(R.id.main_top_five_textview);
		textView_six = (TextView) findViewById(R.id.main_top_six_textview);

		textView_one.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				selected = 1;
				Chinese_textView.setText(Util.grade[selected] + "语文");
				math_textView.setText(Util.grade[selected] + "数学");
				English_textView.setText(Util.grade[selected] + "英语");
				initImageView(selected);
			}
		});

		textView_two.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				selected = 2;
				Chinese_textView.setText(Util.grade[selected] + "语文");
				math_textView.setText(Util.grade[selected] + "数学");
				English_textView.setText(Util.grade[selected] + "英语");
				initImageView(selected);
			}
		});

		textView_three.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				selected = 3;
				Chinese_textView.setText(Util.grade[selected] + "语文");
				math_textView.setText(Util.grade[selected] + "数学");
				English_textView.setText(Util.grade[selected] + "英语");
				initImageView(selected);
			}
		});

		textView_four.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				selected = 4;
				Chinese_textView.setText(Util.grade[selected] + "语文");
				math_textView.setText(Util.grade[selected] + "数学");
				English_textView.setText(Util.grade[selected] + "英语");
				initImageView(selected);
			}
		});

		textView_five.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				selected = 5;
				Chinese_textView.setText(Util.grade[selected] + "语文");
				math_textView.setText(Util.grade[selected] + "数学");
				English_textView.setText(Util.grade[selected] + "英语");
				initImageView(selected);
			}
		});

		textView_six.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				selected = 6;
				Chinese_textView.setText(Util.grade[selected] + "语文");
				math_textView.setText(Util.grade[selected] + "数学");
				English_textView.setText(Util.grade[selected] + "英语");
				initImageView(selected);
			}
		});

		Chinese_textView = (TextView) findViewById(R.id.Chinese_textView);
		math_textView = (TextView) findViewById(R.id.math_textView);
		English_textView = (TextView) findViewById(R.id.English_textView);
		Chinese_textView.setText(Util.grade[selected] + "语文");
		math_textView.setText(Util.grade[selected] + "数学");
		English_textView.setText(Util.grade[selected] + "英语");
		spinner.setDropDownHorizontalOffset(2);
		spinner.setDropDownVerticalOffset(2);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView < ? > arg0 , View arg1 , int arg2 , long arg3 )
			{
				selected = arg2 + 1;
				Chinese_textView.setText(Util.grade[selected] + "语文下册");
				math_textView.setText(Util.grade[selected] + "数学下册");
				English_textView.setText(Util.grade[selected] + "英语下册");
				initImageView(selected);
			}

			@Override
			public void onNothingSelected(AdapterView < ? > arg0 )
			{
			}
		});

		Chinese_imageView = (ImageView) findViewById(R.id.chinese_imageView);
		math_imageView = (ImageView) findViewById(R.id.math_imageView);
		English_imageView = (ImageView) findViewById(R.id.English_imageView);

		initImageView(selected);

		Chinese_imageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				// new Text2Speech(getApplicationContext() , selected +
				// "语文").play();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext() ,Chinese.class);
				intent.putExtra("selected" ,selected);
				startActivity(intent);
			}
		});

		math_imageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				Toast.makeText(getApplicationContext() ,Util.grade[selected] + "数学" ,Toast.LENGTH_SHORT).show();
				// new Text2Speech(getApplicationContext() , selected +
				// "数学").play();
			}
		});

		English_imageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				Toast.makeText(getApplicationContext() ,Util.grade[selected] + "英语" ,Toast.LENGTH_SHORT).show();
				// new Text2Speech(getApplicationContext() , selected +
				// "英语").play();
			}
		});

		course_textView = (TextView) findViewById(R.id.course_textView);
		animation_textView = (TextView) findViewById(R.id.animation_textView);
		story_textView = (TextView) findViewById(R.id.story_textView);

		initEndTextView();

		course_textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				Toast.makeText(getApplicationContext() ,"培训课程..." ,Toast.LENGTH_SHORT).show();
			}
		});

		animation_textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				Toast.makeText(getApplicationContext() ,"动画配音..." ,Toast.LENGTH_SHORT).show();
			}
		});

		story_textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				Toast.makeText(getApplicationContext() ,"听故事..." ,Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 
	 */
	private void initEndTextView()
	{
		course_textView.setBackground(getResources().getDrawable(R.drawable.main_course_textview));
		animation_textView.setBackground(getResources().getDrawable(R.drawable.main_animation_textview));
		story_textView.setBackground(getResources().getDrawable(R.drawable.main_story_textview));
	}

	/**
	 * @param selected
	 */
	private void initImageView(int selected )
	{
		switch(selected)
		{
			case 1:
				textView_one.setBackgroundColor(getResources().getColor(R.color.yes));
				textView_two.setBackgroundColor(getResources().getColor(R.color.no));
				textView_three.setBackgroundColor(getResources().getColor(R.color.no));
				textView_four.setBackgroundColor(getResources().getColor(R.color.no));
				textView_five.setBackgroundColor(getResources().getColor(R.color.no));
				textView_six.setBackgroundColor(getResources().getColor(R.color.no));

				Chinese_imageView.setImageResource(R.drawable.main_first_up);
				math_imageView.setImageResource(R.drawable.main_first_up);
				English_imageView.setImageResource(R.drawable.main_first_up);
				break;
			case 2:
				textView_one.setBackgroundColor(getResources().getColor(R.color.no));
				textView_two.setBackgroundColor(getResources().getColor(R.color.yes));
				textView_three.setBackgroundColor(getResources().getColor(R.color.no));
				textView_four.setBackgroundColor(getResources().getColor(R.color.no));
				textView_five.setBackgroundColor(getResources().getColor(R.color.no));
				textView_six.setBackgroundColor(getResources().getColor(R.color.no));

				Chinese_imageView.setImageResource(R.drawable.main_second_up);
				math_imageView.setImageResource(R.drawable.main_second_up);
				English_imageView.setImageResource(R.drawable.main_second_up);
				break;
			case 3:
				textView_one.setBackgroundColor(getResources().getColor(R.color.no));
				textView_two.setBackgroundColor(getResources().getColor(R.color.no));
				textView_three.setBackgroundColor(getResources().getColor(R.color.yes));
				textView_four.setBackgroundColor(getResources().getColor(R.color.no));
				textView_five.setBackgroundColor(getResources().getColor(R.color.no));
				textView_six.setBackgroundColor(getResources().getColor(R.color.no));

				Chinese_imageView.setImageResource(R.drawable.main_third_up);
				math_imageView.setImageResource(R.drawable.main_third_up);
				English_imageView.setImageResource(R.drawable.main_third_up);
				break;
			case 4:

				textView_one.setBackgroundColor(getResources().getColor(R.color.no));
				textView_two.setBackgroundColor(getResources().getColor(R.color.no));
				textView_three.setBackgroundColor(getResources().getColor(R.color.no));
				textView_four.setBackgroundColor(getResources().getColor(R.color.yes));
				textView_five.setBackgroundColor(getResources().getColor(R.color.no));
				textView_six.setBackgroundColor(getResources().getColor(R.color.no));

				Chinese_imageView.setImageResource(R.drawable.main_fourth_up);
				math_imageView.setImageResource(R.drawable.main_fourth_up);
				English_imageView.setImageResource(R.drawable.main_fourth_up);
				break;
			case 5:
				textView_one.setBackgroundColor(getResources().getColor(R.color.no));
				textView_two.setBackgroundColor(getResources().getColor(R.color.no));
				textView_three.setBackgroundColor(getResources().getColor(R.color.no));
				textView_four.setBackgroundColor(getResources().getColor(R.color.no));
				textView_five.setBackgroundColor(getResources().getColor(R.color.yes));
				textView_six.setBackgroundColor(getResources().getColor(R.color.no));

				Chinese_imageView.setImageResource(R.drawable.main_fifth_up);
				math_imageView.setImageResource(R.drawable.main_fifth_up);
				English_imageView.setImageResource(R.drawable.main_fifth_up);
				break;
			case 6:

				textView_one.setBackgroundColor(getResources().getColor(R.color.no));
				textView_two.setBackgroundColor(getResources().getColor(R.color.no));
				textView_three.setBackgroundColor(getResources().getColor(R.color.no));
				textView_four.setBackgroundColor(getResources().getColor(R.color.no));
				textView_five.setBackgroundColor(getResources().getColor(R.color.no));
				textView_six.setBackgroundColor(getResources().getColor(R.color.yes));

				Chinese_imageView.setImageResource(R.drawable.main_sixth_up);
				math_imageView.setImageResource(R.drawable.main_sixth_up);
				English_imageView.setImageResource(R.drawable.main_sixth_up);
				break;
			default:
				Toast.makeText(getApplicationContext() ,"selected error or phase error!" ,Toast.LENGTH_SHORT).show();
				break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		// getMenuInflater().inflate(R.menu.setting_menu ,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		int id = item.getItemId();
		switch(id)
		{
		// case R.id.main_menu_setting_menu:
		// Intent intent = new Intent();
		// intent.setClass(getApplicationContext() ,Setting.class);
		// startActivity(intent);
		// break;
		// case R.id.main_menu_update:
		// update();
		// break;
		// case R.id.main_menu_others:
		// intent = new Intent();
		// intent.setClass(getApplicationContext() ,PlaySetting.class);
		// startActivity(intent);
		// break;
		// case R.id.main_menu_speech_recognition:
		// new Speech2Text(MainActivity.this).play();
		// break;
		// case R.id.main_menu_newConversation:
		// Toast.makeText(getApplicationContext() ,"新会话"
		// ,Toast.LENGTH_SHORT).show();
		// break;
			default:
				break;
		}

		// if(id == R.id.main_menu_update)
		// {
		// update();
		// }
		// else
		// if(id == R.id.main_menu_speech_recognition)
		// {
		// new Speech2Text(MainActivity.this).play();
		// // new Test(MainActivity.this).btnVoice();
		// }
		// else
		// if(id == R.id.main_menu_others)
		// {
		// // Toast.makeText(getApplicationContext() ,"设置"
		// // ,Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent();
		// intent.setClass(getApplicationContext() ,PlaySetting.class);
		// startActivity(intent);
		// }
		return super.onOptionsItemSelected(item);
	}

	// 两秒内按返回键两次退出程序
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis() - exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext() ,"再按一次退出程序" ,Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				MobclickAgent.onKillProcess(this);
				finish();
				System.exit(0);
			}
			return true;
		}
		else
			if(KeyEvent.KEYCODE_MENU == keyCode)
			{
				return true;
			}
		return super.onKeyDown(keyCode ,event);
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
