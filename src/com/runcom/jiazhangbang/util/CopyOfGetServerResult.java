package com.runcom.jiazhangbang.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.content.Context;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;

public class CopyOfGetServerResult
{
	public static String reponseString = null;
	final static Thread thread = Thread.currentThread();

	public static synchronized String getResponseString(Context context , String method , Map < String , String > mapTemp )
	{
//		Mthread mthread = new Mthread(context ,method ,mapTemp);
		new Mthread(context ,method ,mapTemp).run();
		synchronized(thread)
		{
			try
			{
				thread.wait();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return reponseString;
	}
	
	static class Mthread implements Runnable
	{
		Context context;
		String method;
		Map < String , String > mapTemp;
		public Mthread(Context context , String method , Map < String , String > mapTemp )
		{
			this.method = method;
			this.context = context;
			this.mapTemp = mapTemp;
		}
		
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
					thread.notifyAll();
					if(result.equals("0"))
						return jsonObject.toString();
					return null;
				}
			});
        }
		
	}
}
