package com.runcom.jiazhangbang.recordText;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
import com.runcom.jiazhangbang.util.Util;

public class RecordTextMainListViewAdapter extends BaseAdapter
{
	private static ArrayList < MyText > textList = new ArrayList < MyText >();
	private Context context;
	private LayoutInflater layoutInflater;
	private MediaPlayer mediaPlayer_record , mediaPlayer_resource;

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
		mediaPlayer_record = new MediaPlayer();
		mediaPlayer_resource = new MediaPlayer();
		if(convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.record_text_main_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.record_text_main_listview_item_marqueetext_name);
			holder.playResource_button = (Button) convertView.findViewById(R.id.record_text_main_listview_item_play);
			holder.playRecord_button = (Button) convertView.findViewById(R.id.record_text_main_listview_item_record);
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

		final String filePath = Util.S2TPATH + textList.get(position).getSource().substring(textList.get(position).getSource().indexOf("8800/") + 5 ,textList.get(position).getSource().lastIndexOf(".")) + ".wav";

		holder.playResource_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				if(mediaPlayer_record.isPlaying())
				{
					Toast.makeText(context ,"录制播放：" + position + "未结束" ,Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							mediaPlayer_resource.reset();
							mediaPlayer_resource.setDataSource(textList.get(position).getSource());
							mediaPlayer_resource.prepare();
							mediaPlayer_resource.start();
							mediaPlayer_resource.setOnCompletionListener(new OnCompletionListener()
							{

								@Override
								public void onCompletion(MediaPlayer mp )
								{
									Toast.makeText(context ,"资源播放：" + textList.get(position).getLyric() + "完成" ,Toast.LENGTH_SHORT).show();
									new RecordTextSpeech(context , filePath).play();
								}
							});
						}
						catch(Exception e)
						{
							System.out.println("com.runcom.jiazhangbang.recordText.RecordTextMainListViewAdapter.getView():" + e);
						}
					}
				}).start();
			}
		});

		holder.playRecord_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v )
			{
				if(mediaPlayer_resource.isPlaying())
				{
					Toast.makeText(context ,"资源播放：" + position + "未结束" ,Toast.LENGTH_SHORT).show();
					return;
				}
				new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{
							mediaPlayer_record.reset();
							mediaPlayer_record.setDataSource(filePath);
							mediaPlayer_record.prepare();
							mediaPlayer_record.start();
							mediaPlayer_record.setOnCompletionListener(new OnCompletionListener()
							{

								@Override
								public void onCompletion(MediaPlayer mp )
								{
									Toast.makeText(context ,"录制播放：" + textList.get(position).getSource() + "完成" ,Toast.LENGTH_SHORT).show();
								}
							});
						}
						catch(Exception e)
						{
							System.out.println("com.runcom.jiazhangbang.recordText.RecordTextMainListViewAdapter.getView():" + e);
						}
					}
				}).start();
			}
		});

		return convertView;
	}

	public void setMediaPlayerCancle()
	{

		if(mediaPlayer_resource != null)
		{
			mediaPlayer_resource.release();
			mediaPlayer_resource = null;
			Toast.makeText(context ,"mediaPlayer_resource结束了" ,Toast.LENGTH_SHORT).show();
		}

		if(mediaPlayer_record != null)
		{
			mediaPlayer_record.release();
			mediaPlayer_record = null;
			Toast.makeText(context ,"mediaPlayer_record结束了" ,Toast.LENGTH_SHORT).show();
		}
	}

	class Holder
	{
		private TextView name;
		private Button playResource_button , playRecord_button;
	}

}
