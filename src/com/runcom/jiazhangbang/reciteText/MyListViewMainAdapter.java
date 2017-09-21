package com.runcom.jiazhangbang.reciteText;

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

@SuppressLint("InflateParams")
public class MyListViewMainAdapter extends BaseAdapter
{
	private static ArrayList < MyTextContent > myTextContent;
	private Context context;
	
	private HashMap < Integer , Boolean > isSelectedMap;

	@SuppressLint("UseSparseArrays")
    public MyListViewMainAdapter(Context context , ArrayList < MyTextContent > myTextContent)
	{
		isSelectedMap = new HashMap < Integer , Boolean >();
		this.context = context;
		MyListViewMainAdapter.myTextContent = myTextContent;
	}

	@Override
	public int getCount()
	{
		return myTextContent.size();
	}

	@Override
	public Object getItem(int position )
	{
		return myTextContent.get(position);
	}

	@Override
	public long getItemId(int position )
	{
		return position;
	}

	@Override
	public View getView(int position , View convertView , ViewGroup parent )
	{
		Holder holder;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.recite_text_main_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.recite_text_main_listview_item_recite_contents);

			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		if(myTextContent.size() == 0 || myTextContent.size() <= position)
		{

		}
		else
		{
			holder.name.setText(myTextContent.get(position).getName());
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
