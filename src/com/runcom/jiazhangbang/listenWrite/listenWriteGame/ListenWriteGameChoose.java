package com.runcom.jiazhangbang.listenWrite.listenWriteGame;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteGameChoose extends Activity
{
	private int degreeSpinnerValue , gradeSpinnerValue , phaseSpinnerValue ,
	        unitSpinnerValue;
	private Spinner degreeSpinner , gradeSpinner , phaseSpinner , unitSpinner;
	private Button startButton;
	private Intent intent;
	private ArrayAdapter < String > adapter_grade , adapter_phase ,
	        adapter_unit , adapter_degree;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_game_choose);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "��д��Ϸ";
		actionbar.setTitle(content);

		initLayout();
		initData();
		startAction();
	}

	private void startAction()
	{
		startButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v )
			{
				intent = new Intent();
				intent.setClass(getApplicationContext() ,ListenWriteGameMain.class);
				intent.putExtra("degree" ,degreeSpinnerValue);
				intent.putExtra("grade" ,gradeSpinnerValue);
				intent.putExtra("phase" ,phaseSpinnerValue);
				intent.putExtra("unit" ,unitSpinnerValue);
				String str = "degree:" + degreeSpinnerValue + "\ngrade:" + gradeSpinnerValue + "\nphase:" + phaseSpinnerValue + "\nunit:" + unitSpinnerValue;
				System.out.println(str);
				// Toast.makeText(getApplicationContext() ,str
				// ,Toast.LENGTH_SHORT).show();
				startActivity(intent);
			}
		});
	}

	private void initData()
	{
		final List < String > degreeDataList = new ArrayList < String >();
		final List < String > gradeDataList = new ArrayList < String >();
		final List < String > phaseDataList = new ArrayList < String >();
		final List < String > unitDataList = new ArrayList < String >();

		degreeDataList.add("��");
		degreeDataList.add("�е�");
		degreeDataList.add("����");

		adapter_degree = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , degreeDataList);
		degreeSpinner.setAdapter(adapter_degree);

		// ListenWriteGameSpinnerAdapter adapter_degree = new
		// ListenWriteGameSpinnerAdapter(getApplicationContext());
		// degreeSpinner.setAdapter(adapter_degree);
		// adapter_degree.setDatas(degreeDataList);

		degreeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				degreeSpinnerValue = position + 1;
				// Toast.makeText(getApplicationContext() ,degreeSpinnerValue
				// ,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}
		});

		gradeDataList.add("һ�꼶");
		gradeDataList.add("���꼶");
		gradeDataList.add("���꼶");
		gradeDataList.add("���꼶");
		gradeDataList.add("���꼶");
		gradeDataList.add("���꼶");

		adapter_grade = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , gradeDataList);
		gradeSpinner.setAdapter(adapter_grade);

		// ListenWriteGameSpinnerAdapter adapter_grade = new
		// ListenWriteGameSpinnerAdapter(getApplicationContext());
		// gradeSpinner.setAdapter(adapter_grade);
		// adapter_grade.setDatas(gradeDataList);

		gradeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				gradeSpinnerValue = position + 1;
				// Toast.makeText(getApplicationContext() ,gradeSpinnerValue
				// ,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}
		});

		phaseDataList.add("�ϲ�");
		phaseDataList.add("�²�");

		adapter_phase = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , phaseDataList);
		phaseSpinner.setAdapter(adapter_phase);

		// ListenWriteGameSpinnerAdapter adapter_phase = new
		// ListenWriteGameSpinnerAdapter(getApplicationContext());
		// phaseSpinner.setAdapter(adapter_phase);
		// adapter_phase.setDatas(phaseDataList);
		phaseSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				phaseSpinnerValue = position + 1;
				// Toast.makeText(getApplicationContext() ,phaseSpinnerValue
				// ,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}
		});

		for(int i = 1 ; i <= 8 ; i ++ )
		{
			unitDataList.add(Util.unit[i]);
		}
		adapter_unit = new ArrayAdapter < String >(getApplicationContext() , R.layout.spinner_item , R.id.spinnerItem_textView , unitDataList);
		unitSpinner.setAdapter(adapter_unit);

		// ListenWriteGameSpinnerAdapter adapter_unit = new
		// ListenWriteGameSpinnerAdapter(getApplicationContext());
		// unitSpinner.setAdapter(adapter_unit);
		// adapter_unit.setDatas(unitDataList);

		unitSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView < ? > parent , View view , int position , long id )
			{
				unitSpinnerValue = position + 1;
				// Toast.makeText(getApplicationContext() ,unitSpinnerValue
				// ,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView < ? > parent )
			{

			}
		});

	}

	private void initLayout()
	{
		degreeSpinner = (Spinner) findViewById(R.id.setting_choose_course_spinner);

		gradeSpinner = (Spinner) findViewById(R.id.setting_choose_grade_spinner);
		phaseSpinner = (Spinner) findViewById(R.id.setting_choose_phase_spinner);
		unitSpinner = (Spinner) findViewById(R.id.setting_choose_unit_spinner);

		startButton = (Button) findViewById(R.id.setting_choose_submit_button);
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

	// ��д�����ؼ��˳�����
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
