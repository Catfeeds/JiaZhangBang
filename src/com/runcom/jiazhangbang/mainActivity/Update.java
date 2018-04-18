package com.runcom.jiazhangbang.mainActivity;

import java.io.File;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;

public class Update
{
	private static String content , install;

	public static void update(final Context context , final Boolean first )
	{
		TreeMap < String , String > map = Util.getMap(context);
		System.out.println(Util.REALSERVER + "getver.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "getver.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{
			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				if( !first)
					Toast.makeText(context ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
				// System.out.println(arg1);
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if(Util.okHttpUtilsResultOkStringValue.equals(arg0) && !first)
				{
					Toast.makeText(context ,"当前已是最新版本" ,Toast.LENGTH_SHORT).show();
				}
				else
					if(Util.okHttpUtilsResultExceptionStringValue.equalsIgnoreCase(arg0))
					{
						File updateFile = new File(Util.UPDATEPath + Util.appName);
						if(updateFile.exists())
						{
							updateFile.delete();
						}
						new MyTask(context , Util.UPDATEPath , Util.appName , content , "更新下载").execute(install);
						updateFile.deleteOnExit();
					}
					else
						if(Util.okHttpUtilsResultOkStringValue.equals(arg0) && first)
						{

						}
						else
						{
							Toast.makeText(context ,Util.okHttpUtilsServerExceptionString ,Toast.LENGTH_LONG).show();
						}
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
				if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
				{
					return result;
				}
				String localVersion = Util.getVersionName(context);
				String minVersion = jsonObject.getString("min");
				String latestVersion = jsonObject.getString("latest");
				install = jsonObject.getString("install");
				content = jsonObject.getString("content");
				float local = Float.valueOf(localVersion);
				float min = Float.valueOf(minVersion);
				float latest = Float.valueOf(latestVersion);
				if(local < latest && local > min)
				{
					return Util.okHttpUtilsResultExceptionStringValue;
				}
				else
				{
					return Util.okHttpUtilsResultOkStringValue;
				}
			}
		});
	}

}
