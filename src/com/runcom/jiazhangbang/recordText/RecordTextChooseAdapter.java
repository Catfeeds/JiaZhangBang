package com.runcom.jiazhangbang.recordText;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.reciteText.MyText;

public class RecordTextChooseAdapter extends BaseAdapter
{
	private Context context;
	private LayoutInflater inflater;
	private static ArrayList < MyText > textList;

	public RecordTextChooseAdapter(Context context , ArrayList < MyText > textList)
	{
		this.context = context;
		RecordTextChooseAdapter.textList = textList;
		inflater = LayoutInflater.from(this.context);
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
	public View getView(int position , View convertView , ViewGroup parent )
	{
		Holder holder;
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.record_text_choose_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.record_text_choose_listview_item_name);

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
			holder.name.setText(textList.get(position).getName());
		}
		return convertView;
	}

	class Holder
	{
		TextView id , name , mode , data , source , link , other;
	}
}
