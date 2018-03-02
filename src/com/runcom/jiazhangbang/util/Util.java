package com.runcom.jiazhangbang.util;

//superTextView BRVAH ARouter smartRefreshlayout takePhoto rxpermission okGo
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import com.runcom.jiazhangbang.R;

public class Util
{

	public static final String SECRETKEY = "8848@jzb";
	public static final String REALSERVER = "http://jzb.nutnet.cn:8800/interface/";
	public static final String RESOURCESERVER = "http://res.nutnet.cn:8800/";
	public static String build = "57";
	public static final String market = "2";
	public static String os = Build.VERSION.RELEASE;
	public static final String term = "0";
	public static final String ChineseCourse = "1";// 1:语文
	public static final String EnglishCourse = "2";// 2:英语

	public static TreeMap < String , String > getMap(Context context )
	{
		String app = "";
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		try
		{
			packageInfo = packageManager.getPackageInfo(context.getPackageName() ,0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			app = context.getResources().getString(labelRes);
			// try
			// {
			// app = URLEncoder.encode(app , "UTF-8");
			// }
			// catch(UnsupportedEncodingException e)
			// {
			// e.printStackTrace();
			// }
		}
		catch(NameNotFoundException e)
		{
			Log.d("LOG" ,"exception:" + e.toString());
			e.printStackTrace();
		}

		String dev = android.provider.Settings.Secure.getString(context.getContentResolver() ,android.provider.Settings.Secure.ANDROID_ID);
		String lang = Locale.getDefault().getLanguage();
		if(lang.contains("zh"))
			lang = "zh-Hans-CN";// 中文是zh-Hans或zh-Hans-CN，英文是en或en-US
		else
			lang = "en";
		String ver = packageInfo.versionName;

		Map < String , String > map = new TreeMap < String , String >();
		map.put("term" ,Util.term);
		map.put("os" ,Util.os);
		map.put("dev" ,dev);
		map.put("app" ,app);
		map.put("ver" ,ver);
		map.put("build" ,Util.build);
		map.put("lang" ,lang);

		return (TreeMap < String , String >) map;
	}

	public static final String ROOTPATH = SDCardHelper.getSDCardPath();

	public static final String appPath = "/&JiaZhangBang/";
	public static final String APPPATH = ROOTPATH + appPath;

	public static final String articlesPath = "articles/";
	public static final String ARTICLESPATH = APPPATH + articlesPath;

	public static final String t2sPath = "audios/t2s/";
	public static final String T2SPATH = APPPATH + t2sPath;

	public static final String s2tPath = "audios/s2t/";
	public static final String S2TPATH = APPPATH + s2tPath;

	public static final String lyricsPath = "lyrics/";
	public static final String LYRICSPATH = APPPATH + lyricsPath;

	public static final String musicsPath = "musics/";
	public static final String MUSICSPATH = APPPATH + musicsPath;

	public static final String cachePath = "cache/";
	public static final String CACHEPATH = APPPATH + cachePath;

	public static final String picturesPath = "pictures/";
	public static final String PICTURESPATH = APPPATH + picturesPath;

	public static final String updatePath = "update/";
	public static final String UPDATEPath = APPPATH + updatePath;

	public static final String localAudioListCache = "localAudioList.log";
	public static final String LOCALAUDIOLISTCACHE = CACHEPATH + localAudioListCache;

	public static final String recordPath = "record/";
	public static final String RECORDPATH = APPPATH + recordPath;

	// public static final String SERVERADDRESS = SERVER + "Jiazhangbang/";
	// public static final String SERVER_RESOURCE = SERVER + "wgcwgc/";
	//
	// public static final String SERVERADDRESS_findNewWords = SERVERADDRESS +
	// "findNewWords.jsp?type=0";
	// public static final String SERVERADDRESS_listenText = SERVERADDRESS +
	// "listenText.jsp?type=0";
	// public static final String SERVERADDRESS_listenWrite = SERVERADDRESS +
	// "listenWrite.jsp?type=0";
	// public static final String SERVERADDRESS_listenWriteCopy = SERVERADDRESS
	// + "listenWriteCopy.jsp?type=3";
	// public static final String SERVERADDRESS_listenWriteBackups =
	// SERVERADDRESS + "listenWriteBackups.jsp?type=4";
	// public static final String SERVERADDRESS_listenWriteTips = SERVERADDRESS
	// + "listenWriteTips.jsp?type=5";
	// public static final String SERVERADDRESS_listenWriteMain = SERVERADDRESS
	// + "listenWriteMain.jsp?type=6";
	// public static final String SERVERADDRESS_reciteText = SERVERADDRESS +
	// "reciteText.jsp?type=0";
	// public static final String SERVERADDRESS_reciteTextMain = SERVERADDRESS +
	// "reciteText.jsp?type=1";
	// public static final String SERVERADDRESS_repeat = SERVERADDRESS +
	// "repeat.jsp?type=0";
	// public static final String SERVERADDRESS_update_version_name =
	// SERVERADDRESS + "update.jsp?type=2";
	public static final String SERVER = "http://172.16.0.119:8080/";
	public static final String SERVERADDRESS = SERVER + "JiaZhangBang/";
	public static final String SERVER_RESOURCE = SERVER + "JiaZhangBang/";

	public static final String SERVERADDRESS_repeat = SERVERADDRESS + "repeat.jsp?type=0";
	public static final String SERVERADDRESS_update_version_name = SERVERADDRESS + "update.jsp";

	public static final String SERVERADDRESS_update = SERVER_RESOURCE + "JiaZhangBang.apk";

	public static final String Mp3Server = SERVER_RESOURCE + "mp3/";
	public static final String NewWordsServer = SERVER_RESOURCE + "t2s2/";

	// public static final String serverAddress00 =
	// "http://abv.cn/music/红豆.mp3";
	// public static final String serverAddress01 = "http://www.baidu.com";
	public static final int lyricEnglishEnglish = 0;
	public static final int lyricEnglishChinese = 1;
	public static final int lyricEnglishAll = 2;
	public static final int lyricChinese = 3;
	public static final int lyricAll = 4;

	public static final String okHttpUtilsResultStringKey = "result";
	public static final String okHttpUtilsResultStringValue = "0";
	public static final String okHttpUtilsMesgStringKey = "mesg";
	public static final String okHttpUtilsMesgStringValue = "";

	public static String getVersionName(Context context )
	{
		try
		{
			return context.getPackageManager().getPackageInfo(context.getPackageName() ,0).versionName;
		}
		catch(NameNotFoundException e)
		{
			return context.getString(R.string.can_not_find_version_name);
		}
	}

	/**
	 * 得到设备屏幕的宽度
	 */
	public static int getScreenWidth(Context context )
	{
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 得到设备屏幕的高度
	 */
	public static int getScreenHeight(Context context )
	{
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 得到设备的密度
	 */
	public static float getScreenDensity(Context context )
	{
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 把密度转换为像素
	 */
	public static int dip2px(Context context , float px )
	{
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

	/**
	 * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context , float dpValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context , float pxValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 */
	public static int px2sp(Context context , float pxValue )
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(Context context , float spValue )
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}
