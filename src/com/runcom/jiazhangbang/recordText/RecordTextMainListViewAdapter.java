package com.runcom.jiazhangbang.recordText;

import java.io.File;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.reciteText.MyText;
import com.runcom.jiazhangbang.util.Util;

public class RecordTextMainListViewAdapter extends BaseAdapter
{
	private static ArrayList < MyText > textList = new ArrayList < MyText >();
	private final Context context;
	private MediaPlayer mediaPlayer_record , mediaPlayer_resource;

	public RecordTextMainListViewAdapter(Context context , ArrayList < MyText > textList)
	{
		this.context = context;
		RecordTextMainListViewAdapter.textList = textList;
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
		final Holder holder;
		if(mediaPlayer_resource == null)
		{
			mediaPlayer_resource = new MediaPlayer();
		}
		if(mediaPlayer_record == null)
		{
			mediaPlayer_record = new MediaPlayer();
		}
		if(convertView == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.record_text_main_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.record_text_main_listview_item_name);
			holder.playRecord_button = (ImageView) convertView.findViewById(R.id.record_text_main_listview_item_record);
			holder.voice = (TextView) convertView.findViewById(R.id.record_text_main_listview_item_marqueetext_name);
			holder.name.setBackgroundResource(R.drawable.activity_chinese_background_shape);
			holder.name.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v )
				{
					if(mediaPlayer_record.isPlaying())
					{
						Toast.makeText(context ,"录制播放未结束" ,Toast.LENGTH_SHORT).show();
						return;
					}

					if(mediaPlayer_resource.isPlaying())
					{
						Toast.makeText(context ,"资源播放未结束" ,Toast.LENGTH_SHORT).show();
						return;
					}

					mediaPlayer_resource.setOnCompletionListener(new OnCompletionListener()
					{

						@Override
						public void onCompletion(MediaPlayer mp )
						{
							holder.name.setBackgroundResource(R.drawable.activity_chinese_background_shape);
							String webVoice = holder.voice.getText().toString();
							System.out.println("资源播放：" + webVoice + "完成");
							webVoice = Util.S2TPATH + webVoice.substring(webVoice.indexOf("8800/") + 5 ,webVoice.lastIndexOf(".")) + ".wav";
							new RecordTextSpeech(context , webVoice , holder.name.getText().toString()).play();
							holder.playRecord_button.setVisibility(View.VISIBLE);

						}
					});

					holder.name.setBackgroundResource(R.color.white_pressed);

					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								mediaPlayer_resource.reset();
								mediaPlayer_resource.setDataSource(holder.voice.getText().toString());
								mediaPlayer_resource.prepare();
								mediaPlayer_resource.start();
								System.out.println("资源播放" + holder.voice.getText());
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
				public void onClick(View view )
				{
					if(mediaPlayer_resource.isPlaying())
					{
						Toast.makeText(context ,"资源播放未结束" ,Toast.LENGTH_SHORT).show();
						return;
					}

					if(mediaPlayer_record.isPlaying())
					{
						Toast.makeText(context ,"录制播放未结束" ,Toast.LENGTH_SHORT).show();
						return;
					}

					String voice = holder.voice.getText().toString();
					final String localVoicePath = Util.S2TPATH + voice.substring(voice.indexOf("8800/") + 5 ,voice.lastIndexOf(".")) + ".wav";
					if( !new File(localVoicePath).exists())
					{
						Toast.makeText(context ,"录音文件失效，请重新录制！" ,Toast.LENGTH_SHORT).show();
						return;
					}

					// holder.playRecord_button.setImageResource(R.drawable.button_play_animation);
					// final AnimationDrawable animationDrawable =
					// (AnimationDrawable)
					// holder.playRecord_button.getDrawable();
					new Thread(new Runnable()
					{

						@Override
						public void run()
						{
							try
							{
								System.out.println("播放" + holder.voice.getText() + "的录音");
								// animationDrawable.start();
								mediaPlayer_record.reset();
								mediaPlayer_record.setDataSource(localVoicePath);
								mediaPlayer_record.prepare();
								mediaPlayer_record.start();
								mediaPlayer_record.setOnCompletionListener(new OnCompletionListener()
								{

									@Override
									public void onCompletion(MediaPlayer mp )
									{
										// animationDrawable.stop();
										holder.playRecord_button.setImageResource(R.drawable.find_new_words_text2speech);
										System.out.println("播放：" + localVoicePath + "完成");
									}
								});
							}
							catch(Exception e)
							{
								// animationDrawable.stop();
								// holder.playRecord_button.setImageResource(R.drawable.find_new_words_text2speech);
								System.out.println("com.runcom.jiazhangbang.recordText.RecordTextMainListViewAdapter.getView():" + e);
							}
						}
					}).start();
				}
			});

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
			holder.voice.setText(textList.get(position).getSource());
			String localVoicePath = holder.voice.getText().toString();
			localVoicePath = Util.S2TPATH + localVoicePath.substring(localVoicePath.indexOf("8800/") + 5 ,localVoicePath.lastIndexOf(".")) + ".wav";
			if( !new File(localVoicePath).exists())
			{
				holder.playRecord_button.setVisibility(View.GONE);
			}
			else
			{
				holder.playRecord_button.setVisibility(View.VISIBLE);
			}
		}

		return convertView;
	}

	public void setMediaPlayerCancle()
	{

		if(mediaPlayer_resource.isPlaying())
		{
			mediaPlayer_resource.release();
			mediaPlayer_resource = null;
			// Toast.makeText(context ,"mediaPlayer_resource结束了"
			// ,Toast.LENGTH_SHORT).show();
		}

		if(mediaPlayer_record.isPlaying())
		{
			mediaPlayer_record.release();
			mediaPlayer_record = null;
			// Toast.makeText(context ,"mediaPlayer_record结束了"
			// ,Toast.LENGTH_SHORT).show();
		}
	}

	class Holder
	{
		private TextView name , voice;
		private ImageView playRecord_button;
	}

}
