package com.runcom.jiazhangbang.reciteText;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.runcom.jiazhangbang.R;

@SuppressLint("InflateParams")
public class MyListViewAdapter extends BaseAdapter
{
	public static ArrayList < MyText > textList;
	
	private LayoutInflater inflater;
	private Context context;

	public MyListViewAdapter(LayoutInflater inflater , ArrayList < MyText > audioList)
	{
		this.inflater = inflater;
		MyListViewAdapter.textList = audioList;
	}

	public MyListViewAdapter(Context context , ArrayList < MyText > textList)
	{
		this.context = context;
		MyListViewAdapter.textList = textList;
		inflater = LayoutInflater.from(this.context);
	}

	// @SuppressWarnings("unchecked")
	// public void setAudioList(ArrayList < MyAudio > audioList)
	// {
	// if(audioList != null)
	// {
	// audioList = (ArrayList < MyAudio >) audioList.clone();
	// notifyDataSetChanged();
	// }
	// }
	//
	// public void clearAudioList()
	// {
	// if(audioList != null)
	// {
	// audioList.clear();
	// }
	// notifyDataSetChanged();
	// }

	@Override
	public int getCount()
	{
		// return audioList == null ? 0 : audioList.size();
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

	@Override
	public View getView(final int position , View convertView , ViewGroup parent )
	{
		Holder holder;
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.recite_text_listview_item ,null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.recite_text_listview_item_name);
			holder.mode = (TextView) convertView.findViewById(R.id.recite_text_listview_item_recite_mode);

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
			holder.mode.setText(textList.get(position).getMode());
		}

		// convertView.setOnClickListener(new View.OnClickListener()
		// {
		// @Override
		// public void onClick(View v )
		// {
		// if(NetUtil.getNetworkState(inflater.getContext()) ==
		// NetUtil.NETWORN_NONE)
		// {
		// Toast.makeText(inflater.getContext() ,"Çë¼ì²éÍøÂçÁ¬½Ó"
		// ,Toast.LENGTH_SHORT).show();
		// }
		// else
		// {
		// Toast.makeText(inflater.getContext() ,"Äúµã»÷ÁË" +
		// audioList.get(position).getName().toString()
		// ,Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(inflater.getContext() , Play.class);
		// String source = audioList.get(position).getSource();
		// // source = "http://abv.cn/music/ºì¶¹.mp3";// Ç§Ç§ãÚ¸è ºì¶¹ ¹â»ÔËêÔÂ.mp3
		// intent.putExtra("source" ,source);
		// String lyric = audioList.get(position).getLyric();
		// Log.d("LOG" ,"audio: " + source + "\nlyric: " + lyric);
		// // lyric = "http://abv.cn/music/Íõ·Æ_ºì¶¹.lrc";
		// intent.putExtra("lyric" ,lyric);
		// inflater.getContext().startActivity(intent);
		// }
		// }
		//
		// });

		// ImageButton imageButton_share = (ImageButton)
		// convertView.findViewById(R.id.tab1_fragment_list_item_share);
		// final ImageButton imageButton_download = (ImageButton)
		// convertView.findViewById(R.id.tab1_fragement_list_item_download);
		// imageButton_share.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v )
		// {
		// if(NetUtil.getNetworkState(inflater.getContext()) ==
		// NetUtil.NETWORK_NONE)
		// {
		// Toast.makeText(inflater.getContext() ,"Çë¼ì²éÍøÂçÁ¬½Ó"
		// ,Toast.LENGTH_SHORT).show();
		// }
		// else
		// {
		// Toast.makeText(inflater.getContext() ,"ÕýÔÚ·ÖÏí" +
		// audioList.get(position).getName().toString() + "..."
		// ,Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setType("text/*");
		// intent.putExtra(Intent.EXTRA_SUBJECT ,"Share");
		// String url = ("www.baidu.com").toString();
		// intent.putExtra(Intent.EXTRA_TEXT ,url);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// inflater.getContext().startActivity(Intent.createChooser(intent
		// ,"·ÖÏí"));
		// }
		// }
		// });

		// imageButton_download.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v )
		// {
		// if(NetUtil.getNetworkState(inflater.getContext()) ==
		// NetUtil.NETWORK_NONE)
		// {
		// Toast.makeText(inflater.getContext() ,"Çë¼ì²éÍøÂçÁ¬½Ó"
		// ,Toast.LENGTH_SHORT).show();
		// }
		// else
		// {
		// Toast.makeText(inflater.getContext() ,"ÕýÔÚÏÂÔØ" +
		// audioList.get(position).getName().toString() + "..."
		// ,Toast.LENGTH_SHORT).show();
		// String urlString = audioList.get(position).getLink().toString();
		// urlString = "http://abv.cn/music/ºì¶¹.mp3";// Ç§Ç§ãÚ¸è ºì¶¹ ¹â»ÔËêÔÂ.mp3
		// String fileName = urlString.substring(urlString.lastIndexOf("/") +
		// 1);
		// try
		// {
		// fileName = URLEncoder.encode(fileName ,"UTF-8");
		// }
		// catch(UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// }
		//
		// urlString = urlString.substring(0 ,urlString.lastIndexOf("/") + 1) +
		// fileName;
		// if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		// {
		// File saveDir = new File(Util.musicsPath);
		// try
		// {
		// download(URLDecoder.decode(fileName.substring(0
		// ,fileName.lastIndexOf(".")) + " " ,"UTF-8") ,urlString ,saveDir);
		// }
		// catch(UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// }
		// imageButton_download.setEnabled(false);
		//
		// }
		// else
		// {
		// Toast.makeText(inflater.getContext() ,"Çë¼ì²éSD¿¨"
		// ,Toast.LENGTH_LONG).show();
		// }
		// }
		// }
		// });

		return convertView;
	}

	class Holder
	{
		TextView id , name , mode , data , source , link , other;
		ImageButton share , download;
	}

	public void download(String filename , String path , File savDir )
	{
		// DownloadTask task = new DownloadTask(filename , handler ,
		// inflater.getContext() , path , savDir);
		// new Thread(task).start();
	}

	public Handler handler = new UIHandler();

	static class UIHandler extends Handler
	{
		public void handleMessage(Message msg )
		{
			switch(msg.what)
			{
				case 1: // ¸üÐÂ½ø¶È
					break;
				case -1: // ÏÂÔØÊ§°Ü
					break;
			}
		}
	}

}
