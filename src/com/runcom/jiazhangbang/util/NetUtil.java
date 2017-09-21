package com.runcom.jiazhangbang.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil
{
	public static final int NETWORK_NONE = 0;
	public static final int NETWORK_WIFI = 1;
	public static final int NETWORK_MOBILE = 2;

	public static int getNetworkState(Context context )
	{
		// 获取网络链接的服务
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi的状态
		// CONNECTING 链接, CONNECTED 连接, SUSPENDED暂停, DISCONNECTING 断开,
		// DISCONNECTED 断开, UNKNOWN 未知
		NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING)
		{
			return NETWORK_WIFI;
		}

		// 2G/3G/4G的状态
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if(state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING)
		{
			return NETWORK_MOBILE;
		}
		return NETWORK_NONE;
	}
}
