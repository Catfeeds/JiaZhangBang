package com.runcom.jiazhangbang.mainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.runcom.jiazhangbang.play.Play;

public class Search
{
	public static void searchNameFromFile(Context context , String resultBuffer , String rootPath )
	{
		File dir = new File(rootPath);
		File [] files = dir.listFiles();
		for(File file : files)
		{
			// System.out.println(file.toString());
			// if(file.isDirectory())
			// {
			// searchNameFromFile(context ,resultBuffer ,file.toString());
			// }
			// else
			if(file.toString().endsWith(".lrc"))
			{
				// System.out.println("Search:" + file.toString());
				if(searchStringFromFile(context ,resultBuffer ,file.toString()))
				{
					// Toast.makeText(context ,resultBuffer.toString() + "\n" +
					// file.toString() ,Toast.LENGTH_SHORT).show();
					// System.out.println(resultBuffer.toString() + ":" +
					// file.toString());
					jumpActivity(context ,file.toString() ,resultBuffer);
					return;
				}
			}
		}

		// System.out.println("未查到与 “" + resultBuffer.toString() + "” 相关课文");
		Toast.makeText(context ,"未查到与 “" + resultBuffer.toString() + "” 相关课文" ,Toast.LENGTH_SHORT).show();
		// return false;
	}

	static boolean searchStringFromFile(Context context , String resultBuffer , String filePath )
	{
		String Lrc_data = "";
		File mFile = new File(filePath);
		FileInputStream mFileInputStream = null;
		InputStreamReader mInputStreamReader = null;
		BufferedReader mBufferedReader = null;
		try
		{
			mFileInputStream = new FileInputStream(mFile);
			mInputStreamReader = new InputStreamReader(mFileInputStream , "ucs-2");
			mBufferedReader = new BufferedReader(mInputStreamReader);
			while((Lrc_data = mBufferedReader.readLine()) != null)
			{
				if(Lrc_data.contains(resultBuffer.toString()) || resultBuffer.contains(Lrc_data.toString()))
				{
					// System.out.println(resultBuffer.toString() + ":00:" +
					// filePath);
					return true;
				}
			}
		}
		catch(Exception e)
		{
			try
			{
				mFileInputStream = new FileInputStream(mFile);
				mInputStreamReader = new InputStreamReader(mFileInputStream , "GB2312");
				mBufferedReader = new BufferedReader(mInputStreamReader);
				while((Lrc_data = mBufferedReader.readLine()) != null)
				{
					if(Lrc_data.contains(resultBuffer.toString()) || resultBuffer.contains(Lrc_data.toString()))
					{
						// System.out.println(resultBuffer.toString() + ":01:" +
						// filePath);
						return true;
					}
				}
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			finally
			{
				try
				{
					mBufferedReader.close();
					mInputStreamReader.close();
					mFileInputStream.close();
				}
				catch(IOException e2)
				{
					e2.printStackTrace();
				}
			}
			// e.printStackTrace();
		}
		finally
		{
			try
			{
				mBufferedReader.close();
				mInputStreamReader.close();
				mFileInputStream.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	static void jumpActivity(Context context , String filePath , String resultBuffer )
	{
		// System.out.println("jumpActivity 执行了");
		Intent intent = new Intent(context , Play.class);
		intent.putExtra("filePath" ,filePath);
		intent.putExtra("resultBuffer" ,resultBuffer);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
