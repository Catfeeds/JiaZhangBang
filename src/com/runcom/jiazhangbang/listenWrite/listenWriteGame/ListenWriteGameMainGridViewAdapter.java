package com.runcom.jiazhangbang.listenWrite.listenWriteGame;

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

public class ListenWriteGameMainGridViewAdapter extends BaseAdapter
{

	private Context context;
	private ArrayList < ListenWriteGameItemBean > listenWriteGameItemBean;
	private HashMap < Integer , Boolean > isSelectedMap , isDeletedMap;

	public ListenWriteGameMainGridViewAdapter()
	{
	}

	@SuppressLint("UseSparseArrays")
	public ListenWriteGameMainGridViewAdapter(Context context , ArrayList < ListenWriteGameItemBean > lsitenWriteGameItemBean)
	{
		isSelectedMap = new HashMap < Integer , Boolean >();
		isDeletedMap = new HashMap < Integer , Boolean >();
		this.context = context;
		this.listenWriteGameItemBean = lsitenWriteGameItemBean;
	}

	@Override
	public int getCount()
	{
		return listenWriteGameItemBean.size();
	}

	@Override
	public Object getItem(int position )
	{
		return listenWriteGameItemBean.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listen_write_game_main_gridview_item ,null);
			holder = new Holder();
			holder.phrase = (TextView) convertView.findViewById(R.id.listen_write_game_main_gridview_item_textview_name);
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		if(listenWriteGameItemBean.size() == 0 || listenWriteGameItemBean.size() <= position)
		{
		}
		else
		{
			// if(0 == position % 2)
			if(position < listenWriteGameItemBean.size() / 2)
				holder.phrase.setText(listenWriteGameItemBean.get(position).getPhrase());
			else
				holder.phrase.setText(listenWriteGameItemBean.get(position).getPinyin());
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

	public boolean getIsDeleted(int position )
	{
		// 如果当前位置的key值为空，则表示该item未被删除，返回false，否则返回true
		if(isDeletedMap.get(position) != null)
		{
			return isDeletedMap.get(position);
		}
		return false;
	}

	public void setItemIsDeletedMap(int position , boolean isDeletedMap )
	{
		this.isDeletedMap.put(position ,isDeletedMap);
		listenWriteGameItemBean.remove(position);
		notifyDataSetChanged();
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
		TextView phrase;

	}
}
