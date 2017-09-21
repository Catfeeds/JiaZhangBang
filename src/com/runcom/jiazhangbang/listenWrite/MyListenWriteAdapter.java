package com.runcom.jiazhangbang.listenWrite;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.listenText.MyAudio;

public class MyListenWriteAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList < MyAudio > myListenWriteContentArrayList;

	public MyListenWriteAdapter(Context context , ArrayList < MyAudio > myListenWriteContentArrayList)
	{
		this.context = context;
		this.myListenWriteContentArrayList = myListenWriteContentArrayList;
	}

	@Override
	public int getCount()
	{
		return myListenWriteContentArrayList.size();
	}

	@Override
	public Object getItem(int position )
	{
		return myListenWriteContentArrayList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listen_write_backups_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.listen_write_backups_listview_item_name);

			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		if(myListenWriteContentArrayList.size() == 0 || myListenWriteContentArrayList.size() <= position)
		{
		}
		else
		{
			holder.name.setText(myListenWriteContentArrayList.get(position).getName());
		}

		return convertView;
	}

	class Holder
	{
		TextView name;
	}

}
