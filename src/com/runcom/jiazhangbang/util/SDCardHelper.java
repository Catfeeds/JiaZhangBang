/**
 * 
 */
package com.runcom.jiazhangbang.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-13
 * @time 上午10:38:21
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.util
 * @file_name SDCardHelper.java
 * @type_name SDCardHelper
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */
public class SDCardHelper
{

	private static String TAG = "SDCardHelper";

	/*
	 * 
	 * 判断sdcard是否挂载
	 */

	public static boolean isSDCardMounted()
	{

		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}

	/*
	 * 
	 * 获取sdcard绝对物理路径
	 */

	public static String getSDCardPath()
	{

		if(isSDCardMounted())
		{

			return Environment.getExternalStorageDirectory().getAbsolutePath();

		}
		else
		{

			return null;

		}

	}

	/*
	 * 
	 * 获取sdcard的全部的空间大小。返回MB字节
	 */

	public static long getSDCardSize()
	{

		if(isSDCardMounted())
		{

			StatFs fs = new StatFs(getSDCardPath());

			// 带有Long结尾的替代方法需要API 18以上的SDK才能支持

			long size = fs.getBlockSizeLong();

			long count = fs.getBlockCountLong();

			return size * count / 1024 / 1024;

		}

		return 0;

	}

	/*
	 * 
	 * 获取sdcard的剩余的可用空间的大小。返回MB字节
	 */

	public static long getSDCardFreeSize()
	{

		if(isSDCardMounted())
		{

			StatFs fs = new StatFs(getSDCardPath());
			long size = fs.getBlockSizeLong();

			long count = fs.getFreeBlocksLong();
			return size * count / 1024 / 1024;

		}

		return 0;

	}

	/**
	 * * 获取SD卡的可用空间大小，返回MB
	 * 
	 * 
	 * 
	 * @return
	 */

	public static long getSDCardAvailableSize()
	{
		if(isSDCardMounted())
		{

			StatFs fs = new StatFs(getSDCardPath());
			long count = fs.getAvailableBlocksLong();
			long size = fs.getBlockSizeLong();
			return count * size / 1024 / 1024;

		}

		return 0;

	}

	/*
	 * 
	 * 将文件（byte[]）保存进sdcard指定的路径下 dir:指定的路径
	 */

	public static boolean saveFileToSDCard(byte [] data , String dir , String filename )
	{

		BufferedOutputStream bos = null;

		if(isSDCardMounted())
		{
			Log.i(TAG ,"==isSDCardMounted==TRUE");
			File file = new File(dir);

			Log.i(TAG ,"==file:" + file.toString() + file.exists());
			if( !file.exists())
			{

				boolean flags = file.mkdirs();
				Log.i(TAG ,"==创建文件夹:" + flags);

			}
			try
			{

				bos = new BufferedOutputStream(new FileOutputStream(new File(file , filename)));

				bos.write(data ,0 ,data.length);
				bos.flush();
				return true;

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{

				try
				{

					bos.close();

				}
				catch(IOException e)
				{

					e.printStackTrace();

				}

			}

		}

		return false;

	}

	/*
	 * 
	 * 已知文件的绝对路径，从sdcard中获取到该文件，返回byte[]
	 */

	public static byte [] loadFileFromSDCard(String filepath )
	{
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;

		if(isSDCardMounted())
		{

			File file = new File(filepath);
			if(file.exists())
			{

				try
				{

					baos = new ByteArrayOutputStream();

					bis = new BufferedInputStream(new FileInputStream(file));

					byte [] buffer = new byte [1024 * 8];
					int c = 0;

					while((c = bis.read(buffer)) != -1)
					{
						baos.write(buffer ,0 ,c);
						baos.flush();

					}

					return baos.toByteArray();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(bis != null)
						{
							bis.close();
							baos.close();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
