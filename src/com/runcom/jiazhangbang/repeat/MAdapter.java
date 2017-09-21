package com.runcom.jiazhangbang.repeat;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runcom.jiazhangbang.R;

public class MAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	Context mContext;
	private ArrayList < String > list;
	private TextView tv;

	public MAdapter(Context c , ArrayList < String > i)
	{
		this.mContext = c;
		this.list = i;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position )
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position )
	{
		return position;
	}

	@SuppressLint(
	{ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position , View convertView , ViewGroup parent )
	{
		convertView = inflater.inflate(R.layout.repeat_record_listview_item ,null);
		tv = (TextView) convertView.findViewById(R.id.tv);
		tv.setText(list.get(position));
		return convertView;
	}
}
