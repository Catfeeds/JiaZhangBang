package com.runcom.jiazhangbang.listenWrite;

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
import android.widget.Button;
import android.widget.Spinner;

import com.runcom.jiazhangbang.R;
import com.umeng.analytics.MobclickAgent;

public class ListenWriteGameTest extends Activity
{
	private int degreeSpinnerValue , gradeSpinnerValue , phaseSpinnerValue ,
	        unitSpinnerValue;
	private Spinner degreeSpinner , gradeSpinner , phaseSpinner , unitSpinner;
	private Button startButton;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_write_game_test);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "听写游戏";
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

		degreeDataList.add("简单");
		degreeDataList.add("中等");
		degreeDataList.add("困难");
		ListenWriteGameSpinnerAdapter adapter_degree = new ListenWriteGameSpinnerAdapter(getApplicationContext());
		degreeSpinner.setAdapter(adapter_degree);
		adapter_degree.setDatas(degreeDataList);

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

		gradeDataList.add("一年级");
		gradeDataList.add("二年级");
		gradeDataList.add("三年级");
		gradeDataList.add("四年级");
		gradeDataList.add("五年级");
		gradeDataList.add("六年级");

		ListenWriteGameSpinnerAdapter adapter_grade = new ListenWriteGameSpinnerAdapter(getApplicationContext());
		gradeSpinner.setAdapter(adapter_grade);
		adapter_grade.setDatas(gradeDataList);

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

		phaseDataList.add("上册");
		phaseDataList.add("下册");

		ListenWriteGameSpinnerAdapter adapter_phase = new ListenWriteGameSpinnerAdapter(getApplicationContext());
		phaseSpinner.setAdapter(adapter_phase);
		adapter_phase.setDatas(phaseDataList);
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

		unitDataList.add("第一单元");
		unitDataList.add("第二单元");
		unitDataList.add("第三单元");
		unitDataList.add("第四单元");
		unitDataList.add("第五单元");
		unitDataList.add("第六单元");
		unitDataList.add("第七单元");
		unitDataList.add("第八单元");

		ListenWriteGameSpinnerAdapter adapter_unit = new ListenWriteGameSpinnerAdapter(getApplicationContext());
		unitSpinner.setAdapter(adapter_unit);
		adapter_unit.setDatas(unitDataList);

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
		degreeSpinner = (Spinner) findViewById(R.id.listen_write_game_degree_spinner);

		gradeSpinner = (Spinner) findViewById(R.id.listen_write_game_grade_spinner);
		phaseSpinner = (Spinner) findViewById(R.id.listen_write_game_phase_spinner);
		unitSpinner = (Spinner) findViewById(R.id.listen_write_game_unit_spinner);

		startButton = (Button) findViewById(R.id.listen_write_game_start_button);
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
