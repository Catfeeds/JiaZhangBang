package com.runcom.jiazhangbang.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.findnewwords.FindNewWords;
import com.runcom.jiazhangbang.listenText.ListenTextMain;
import com.runcom.jiazhangbang.listenWrite.ListenWriteTips;
import com.runcom.jiazhangbang.listenWrite.listenWriteGame.ListenWriteGameMain;
import com.runcom.jiazhangbang.reciteText.ReciteTextTextChoose;
import com.runcom.jiazhangbang.repeat.Repeat;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class SettingChoose extends Activity
{

	private int chooseId;
	private Spinner spinner_course , spinner_grade , spinner_phase ,
	        spinner_unit;
	private Button button_submit;
	private ArrayAdapter < String > adapter_course , adapter_grade ,
	        adapter_phase , adapter_unit;
	private int courseSpinnerValue , gradeSpinnerValue , phaseSpinnerValue ,
	        unitSpinnerValue;
	private final String sharedPreferencesKey = Util.settingChooseSharedPreferencesKey;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_choose);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "返回";
		actionbar.setTitle(content);
		chooseId = getIntent().getIntExtra("class" ,Util.ListenTextMain);
		initLayout();
		initData();
		initView();
	}

	private void initView()
	{
		courseSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,0);
		gradeSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,0);
		phaseSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,0);
		unitSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,0);

		courseSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.courseSharedPreferencesKeyString[chooseId] ,courseSpinnerValue);
		gradeSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[chooseId] ,gradeSpinnerValue);
		phaseSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[chooseId] ,phaseSpinnerValue);
		unitSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.unitSharedPreferencesKeyString[chooseId] ,unitSpinnerValue);
		System.out.println("1:" + unitSpinnerValue);
		if(chooseId == Util.ListenWriteTips && unitSpinnerValue > 0)
		{
			unitSpinnerValue = MySharedPreferences.getValue(getApplicationContext() ,sharedPreferencesKey ,Util.unitSharedPreferencesKeyString[chooseId] ,unitSpinnerValue - 1);
			System.out.println("2:" + unitSpinnerValue);
		}

		System.out.println("3:" + unitSpinnerValue);
		spinner_course.setSelection(courseSpinnerValue ,true);
		spinner_grade.setSelection(gradeSpinnerValue ,true);
		spinner_phase.setSelection(phaseSpinnerValue ,true);
		spinner_unit.setSelection(unitSpinnerValue ,true);
	}

	private void initData()
	{
		List < String > courseDataList = new ArrayList < String >();

		courseDataList.add("语文");
		courseDataList.add("英语");

		adapter_course = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , courseDataList);

		spinner_course.setAdapter(adapter_course);
		spinner_course.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				courseSpinnerValue = position;
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}
		});

		List < String > gradeDataList = new ArrayList < String >();
		gradeDataList.add("一年级");
		gradeDataList.add("二年级");
		gradeDataList.add("三年级");
		gradeDataList.add("四年级");
		gradeDataList.add("五年级");
		gradeDataList.add("六年级");

		adapter_grade = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , gradeDataList);
		spinner_grade.setAdapter(adapter_grade);
		spinner_grade.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				gradeSpinnerValue = position;
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}

		});

		List < String > phaseDataList = new ArrayList < String >();
		phaseDataList.add("上学期");
		phaseDataList.add("下学期");

		adapter_phase = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , phaseDataList);
		spinner_phase.setAdapter(adapter_phase);
		spinner_phase.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				phaseSpinnerValue = position;
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}

		});

		List < String > unitDataList = new ArrayList < String >();
		if(Util.ListenWriteTips != chooseId)
		{
			unitDataList.add(Util.unit[0]);
		}
		for(int i = 1 ; i <= 8 ; i ++ )
		{
			unitDataList.add(Util.unit[i]);
		}

		adapter_unit = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , unitDataList);
		spinner_unit.setAdapter(adapter_unit);
		spinner_unit.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				unitSpinnerValue = position;
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}

		});

		button_submit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				// String content = "courseSpinnerValue:" + courseSpinnerValue +
				// "\tgradeSpinnerValue:" + gradeSpinnerValue +
				// "\tphaseSpinnerValue:" + phaseSpinnerValue +
				// "\nunitSpinnerValue:" + unitSpinnerValue;
				// Toast.makeText(getApplicationContext() ,content
				// ,Toast.LENGTH_LONG).show();
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,courseSharedPreferencesKeyString
				// ,courseSpinnerValue);
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,gradeSharedPreferencesKeyString
				// ,gradeSpinnerValue);
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,phaseSharedPreferencesKeyString
				// ,phaseSpinnerValue);
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey ,unitSharedPreferencesKeyString
				// ,unitSpinnerValue);
				// Toast.makeText(getApplicationContext() ,"修改成功"
				// ,Toast.LENGTH_LONG).show();
			}
		});
	}

	private void initLayout()
	{
		spinner_course = (Spinner) findViewById(R.id.setting_choose_course_spinner);
		spinner_grade = (Spinner) findViewById(R.id.setting_choose_grade_spinner);
		spinner_phase = (Spinner) findViewById(R.id.setting_choose_phase_spinner);
		spinner_unit = (Spinner) findViewById(R.id.setting_choose_unit_spinner);

		button_submit = (Button) findViewById(R.id.setting_choose_submit_button);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu )
	{
		getMenuInflater().inflate(R.menu.setting_choose_menu ,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case R.id.setting_choose_menu_next:
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.courseSharedPreferencesKeyString[0] ,courseSpinnerValue);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[0] ,gradeSpinnerValue);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[0] ,phaseSpinnerValue);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.unitSharedPreferencesKeyString[0] ,unitSpinnerValue);

				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.courseSharedPreferencesKeyString[chooseId] ,courseSpinnerValue);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.gradeSharedPreferencesKeyString[chooseId] ,gradeSpinnerValue);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.phaseSharedPreferencesKeyString[chooseId] ,phaseSpinnerValue);
				MySharedPreferences.putValue(getApplicationContext() ,sharedPreferencesKey ,Util.unitSharedPreferencesKeyString[chooseId] ,unitSpinnerValue);
				// if(chooseId == Util.ListenWriteTips && unitSpinnerValue > 7)
				// {
				// unitSpinnerValue -- ;
				// MySharedPreferences.putValue(getApplicationContext()
				// ,sharedPreferencesKey
				// ,Util.unitSharedPreferencesKeyString[chooseId]
				// ,unitSpinnerValue);
				// }

				switch(chooseId)
				{
					case Util.ListenTextMain:
						startActivity(new Intent().setClass(getApplicationContext() ,ListenTextMain.class));
						break;
					case Util.ListenWriteTips:
						startActivity(new Intent().setClass(getApplicationContext() ,ListenWriteTips.class));
						break;
					case Util.ReciteTextTextChoose:
						startActivity(new Intent().setClass(getApplicationContext() ,ReciteTextTextChoose.class));
						break;
					case Util.Repeat:
						startActivity(new Intent().setClass(getApplicationContext() ,Repeat.class));
						break;
					case Util.FindNewWords:
						startActivity(new Intent().setClass(getApplicationContext() ,FindNewWords.class));
						break;
					case Util.PlayGame:
						startActivity(new Intent().setClass(getApplicationContext() ,ListenWriteGameMain.class));
						break;
					case Util.RecordText:
						Toast.makeText(SettingChoose.this ,"recordText敬请期待..." ,Toast.LENGTH_LONG).show();
						// startActivity(new
						// Intent().setClass(getApplicationContext()
						// ,Login.class));
						// TODO settingChoose RecordText
						break;
					default:
						break;
				}

				break;
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 重写按返回键退出播放
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
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
