package com.runcom.jiazhangbang.util;

import java.util.TreeMap;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.storage.MySharedPreferences;

public class GetResUrlHead implements Callable < String >
{
	private Context context;

	public GetResUrlHead(Context context)
	{
		this.context = context;
	}

	@Override
	public String call() throws Exception
	{
		final TreeMap < String , String > map = Util.getMap(context);
		System.out.println(Util.REALSERVER + "getconfig.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "getconfig.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{

			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				Toast.makeText(context ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{

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
				JSONObject configJsonObject = new JSONObject(jsonObject.getString("config"));
				String content = configJsonObject.getString("resUrlHead");
				MySharedPreferences.putValue(context ,Util.resourceUrlHeadSharedPreferencesKey ,Util.resourceUrlHeadSharedPreferencesKeyString ,content);
				System.out.println("com.runcom.jiazhangbang.util.GetResUrlHead.call():" + content);
				return null;
			}

		});
		return "0";
	}

}
