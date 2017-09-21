package com.runcom.jiazhangbang.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.content.Context;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;

public class GetServerResult
{
	public static String reponseString = null;

	public static String getResponseString(final Context context , final String method , final Map < String , String > mapTemp )
	{
		Vector < Thread > threads = new Vector < Thread >();
		for(int i = 0 ; i < 1 ; i ++ )
		{
			Thread thread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final TreeMap < String , String > map = Util.getMap(context);
					Set < String > keySet = mapTemp.keySet();
					Iterator < String > iter = keySet.iterator();
					while(iter.hasNext())
					{
						String key = iter.next();
						String value = mapTemp.get(key);
						map.put(key ,value);
					}

					OkHttpUtils.get().url(Util.REALSERVER + method + ".php?" + URL.getParameter(map)).build().execute(new Callback < String >()
					{

						@Override
						public void onError(Call arg0 , Exception arg1 , int arg2 )
						{
						}

						@Override
						public void onResponse(String arg0 , int arg1 )
						{
							reponseString = arg0;
						}

						@Override
						public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
						{
							String response = arg0.body().string().trim();
							JSONObject jsonObject = new JSONObject(response);
							String result = jsonObject.getString("result");
							if(result.equals("0"))
								return jsonObject.toString();
							return "";
						}
					});

				}
			});

			threads.add(thread);
			thread.start();
		}
		for(Thread thread : threads)
		{
			try
			{
				thread.join();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		// System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOok" +
		// reponseString);
		return reponseString;
	}
}
