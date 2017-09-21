package com.runcom.jiazhangbang.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class ServerIsOpenUtil extends AsyncTask < String , String , String >
{

	/**
	 * 
	 * 判断服务器是否开启
	 * 
	 * @param path
	 *            网络服务器地址
	 * @return 服务器开启 服务器未开启
	 */

	@Override
	public String doInBackground(String...params )
	{
		try
		{
			HttpGet get = new HttpGet(params[0]);
			Log.d("LOG" ,params[0]);
			HttpResponse response = new DefaultHttpClient().execute(get);

			Log.d("LOG" ,params[0]);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				return "success";
			}
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
			return "ClientError";
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return "ServerError";
		}
		return "success";
	}
	
	public boolean judge(String...params )
	{
		try
		{
			HttpGet get = new HttpGet(params[0]);
			Log.d("LOG" ,params[0]);
			HttpResponse response = new DefaultHttpClient().execute(get);
			
			Log.d("LOG" ,params[0]);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				return true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
}
