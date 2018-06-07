package com.runcom.jiazhangbang.recordText;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.reciteText.MyText;

public class RecordTextMainListViewAdapter extends BaseAdapter
{
	private static ArrayList < MyText > textList = new ArrayList < MyText >();
	private Context context;
	private LayoutInflater layoutInflater;

	public RecordTextMainListViewAdapter(Context context , ArrayList < MyText > textList)
	{
		this.context = context;
		RecordTextMainListViewAdapter.textList = textList;
		layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount()
	{
		return textList.size();
	}

	@Override
	public Object getItem(int position )
	{
		return textList.get(position);
	}

	@Override
	public long getItemId(int position )
	{
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position , View convertView , ViewGroup parent )
	{
		Holder holder;
		if(convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.record_text_main_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.record_text_main_listview_item_marqueetext_name);
			holder.play_button = (Button) convertView.findViewById(R.id.record_text_main_listview_item_play);
			holder.record_button = (Button) convertView.findViewById(R.id.record_text_main_listview_item_record);
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		if(textList.size() == 0 || textList.size() <= position)
		{

		}
		else
		{
			holder.name.setText(textList.get(position).getLyric());
		}

		holder.play_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				Toast.makeText(context ,"²¥·Å£º" + textList.get(position).getLyric() ,Toast.LENGTH_SHORT).show();
			}
		});

		holder.record_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				Toast.makeText(context ,"Â¼ÖÆ£º" + textList.get(position).getSource() ,Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}

	class Holder
	{
		TextView name;
		Button text_button , play_button , record_button;
	}

}
