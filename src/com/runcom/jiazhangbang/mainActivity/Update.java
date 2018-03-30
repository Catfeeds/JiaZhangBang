package com.runcom.jiazhangbang.mainActivity;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.util.Util;

public class Update
{
	public static void update(final Context context )
	{
		final String appName = "JiaZhangBang.apk";
		final String file = Util.UPDATEPath + appName;
		OkHttpUtils.get().url(Util.SERVERADDRESS_update_version_name).build().execute(new Callback < String >()
		{
			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				Toast.makeText(context ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if( !Util.okHttpUtilsResultOkStringValue.equals(arg0))
				{
					if( !new File(file).exists())
					{
						new MyTask(context , Util.UPDATEPath , appName , arg0 , "更新下载").execute(Util.SERVERADDRESS_update);
					}
					else
					{
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.parse("file://" + file) ,"application/vnd.android.package-archive");
						context.startActivity(intent);
					}
					new File(file).deleteOnExit();
				}
				else
					Toast.makeText(context ,"当前已是最新版本" ,Toast.LENGTH_SHORT).show();
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{
				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				String serverVersion = jsonObject.getString("version");
				String updateContent = jsonObject.getString("updateContent");
				String localVersion = Util.getVersionName(context);

				String [] serverDigits = serverVersion.split("\\.");
				String [] localDigits = localVersion.split("\\.");

				int server0 = Integer.parseInt(serverDigits[0]);
				int server1 = Integer.parseInt(serverDigits[1]);
				int local0 = Integer.parseInt(localDigits[0]);
				int local1 = Integer.parseInt(localDigits[1]);

				if(server0 > local0 || (server0 == local0 && server1 > local1))
				{
					return updateContent;
				}

				return "0";
			}
		});
	}

}
