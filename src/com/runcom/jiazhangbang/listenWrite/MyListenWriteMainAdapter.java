package com.runcom.jiazhangbang.listenWrite;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runcom.jiazhangbang.R;

public class MyListenWriteMainAdapter extends BaseAdapter
{

	private Context context;
	private ArrayList < NewWords > newWordslList;
	private HashMap < Integer , Boolean > isSelectedMap;

	public MyListenWriteMainAdapter()
	{
	}

	@SuppressLint("UseSparseArrays")
	public MyListenWriteMainAdapter(Context context , ArrayList < NewWords > newWordsList)
	{
		isSelectedMap = new HashMap < Integer , Boolean >();
		this.context = context;
		this.newWordslList = newWordsList;
	}

	@Override
	public int getCount()
	{
		return newWordslList.size();
	}

	@Override
	public Object getItem(int position )
	{
		return newWordslList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listen_write_main_item_text_view ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.listen_write_main_item_textview_name);
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		if(newWordslList.size() == 0 || newWordslList.size() <= position)
		{
		}
		else
		{
			holder.name.setText(newWordslList.get(position).getName());
			if(getisSelectedAt(position))
			{
				convertView.setBackgroundColor(context.getResources().getColor(R.color.yes));
			}
			else
			{
				convertView.setBackgroundColor(context.getResources().getColor(R.color.no));
			}
		}

		return convertView;
	}

	public boolean getisSelectedAt(int position )
	{
		// 如果当前位置的key值为空，则表示该item未被选择过，返回false，否则返回true
		if(isSelectedMap.get(position) != null)
		{
			return isSelectedMap.get(position);
		}
		return false;
	}

	public void setItemisSelectedMap(int position , boolean isSelectedMap )
	{
		this.isSelectedMap.put(position ,isSelectedMap);
		notifyDataSetChanged();
	}

	class Holder
	{
		TextView name;
	}
}
