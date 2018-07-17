package com.runcom.jiazhangbang.read;

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

public class MAdapter extends BaseAdapter
{
	private final LayoutInflater inflater;
	private final Context mContext;
	private final ArrayList < String > list;
	private final HashMap < Integer , Boolean > isSelectedMap;

	@SuppressLint("UseSparseArrays")
	public MAdapter(Context c , ArrayList < String > i)
	{
		isSelectedMap = new HashMap < Integer , Boolean >();
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

		Holder holder;
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.repeat_record_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.repeat_record_listview_item_tv);
			// holder.progressBar = (ProgressBar)
			// convertView.findViewById(R.id.repeat_record_listview_item_progressbar);
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}
		holder.name.setText(list.get(position));

		if(getisSelectedAt(position))
		{
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.yes));
		}
		else
		{
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.no));
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

	public void setProgressBar(int position , float progress )
	{

		notifyDataSetChanged();
	}

	class Holder
	{
		private TextView name;
		// ProgressBar progressBar;
	}
}
