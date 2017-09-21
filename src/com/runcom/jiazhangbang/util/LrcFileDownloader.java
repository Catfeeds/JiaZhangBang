package com.runcom.jiazhangbang.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class LrcFileDownloader extends Thread
{
	private String lyric;

	public LrcFileDownloader(String lyric)
	{
		this.lyric = lyric;
	}

	@Override
	public void run()
	{

		FileOutputStream fos = null;
		HttpURLConnection conn = null;
		try
		{
			// 改地址可以下载 互联网上任何资源
			URL url = new URL(lyric);
			// 此处可以设置url设置要下载的网页
			// 2 构建连接对象
			conn = (HttpURLConnection) url.openConnection();// 一定是这种类型
			conn.setReadTimeout(3000);// 设置客户端连接超时间隔，如果超过指定时间 服务端还没有响应 就不要等了
			// 判断服务端正常的反馈是否已经到达了 客户端
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == 200)
			{
				// 获得网络字节输入流对象
				InputStream is = conn.getInputStream();
				File saveFilePath = new File(Util.LYRICSPATH + lyric.substring(lyric.lastIndexOf("/") + 1));
				Log.d("LOG" ,"1:" + saveFilePath.toString());
				if(!saveFilePath.getParentFile().exists())
				{
					saveFilePath.getParentFile().mkdirs();
				}
				if( !saveFilePath.exists())
				{
					// 建立内存到硬盘的连接
					fos = new FileOutputStream(saveFilePath);
					Log.d("LOG" ,"2: " + saveFilePath.toString());
					// 老三样 写文件
					byte [] b = new byte [1024];
					int len = 0;
					while((len = is.read(b)) != -1)
					{ // 先读到内存
						fos.write(b ,0 ,len);
					}
					fos.flush();
					Log.d("LOG" ,lyric + "下载成功");
				}
			}

		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
