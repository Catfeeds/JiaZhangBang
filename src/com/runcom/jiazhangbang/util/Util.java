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
	public static final Boolean debug = true;
	public static final String grade[] =
	{ "���꼶", "һ�꼶", "���꼶", "���꼶", "���꼶", "���꼶", "���꼶" };
	public static final String unit[] =
	{ "���㵥Ԫ", "��һ��Ԫ", "�ڶ���Ԫ", "������Ԫ", "���ĵ�Ԫ", "���嵥Ԫ", "������Ԫ", "���ߵ�Ԫ", "�ڰ˵�Ԫ" };
	public static final String SECRETKEY = "8848@jzb";
	public static final String REALSERVER = "http://jzb.nutnet.cn:8800/interface/";
	public static final String RESOURCESERVER = "http://res.nutnet.cn:8800/";
	public static String build = "57";
	public static final String market = "2";
	public static String os = Build.VERSION.RELEASE;
	public static final String term = "0";

	public static final String ChineseCourse = "1";
	public static final String EnglishCourse = "2";

	public static final int FirstStartAndSetChose = 0;
	public static final int ListenTextMain = 1;
	public static final int ListenWriteTips = 2;
	public static final int ReciteTextTextChose = 3;
	public static final int Repeat = 4;
	public static final int FindNewWords = 5;
	public static final int RecordText = 6;

	public static final String sharedPreferencesKeyFirstStart = "FirstStartKey";
	public static final String firstStartSharedPreferencesKeyString = "FirstStart";
	public static final String sharedPreferencesKeySettingChose = "SettingChoseKey";
	public static final String courseSharedPreferencesKeyString = "SettingChoseCourse";
	public static final String gradeSharedPreferencesKeyString = "SettingChoseGrade";
	public static final String phaseSharedPreferencesKeyString = "SettingChosePhase";
	public static final String unitSharedPreferencesKeyString = "SettingChoseUnit";

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
		}
		catch(NameNotFoundException e)
		{
			Log.d("LOG" ,"exception:" + e.toString());
			e.printStackTrace();
		}

		String dev = android.provider.Settings.Secure.getString(context.getContentResolver() ,android.provider.Settings.Secure.ANDROID_ID);
		String lang = Locale.getDefault().getLanguage();
		if(lang.contains("zh"))
			lang = "zh-Hans-CN";// ������zh-Hans��zh-Hans-CN��Ӣ����en��en-US
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

	public static final String t2sPath = "audios/t2s/";
	public static final String T2SPATH = APPPATH + t2sPath;

	public static final String s2tPath = "audios/s2t/";
	public static final String S2TPATH = APPPATH + s2tPath;

	public static final String lyricsPath = "lyrics/";
	public static final String LYRICSPATH = APPPATH + lyricsPath;

	public static final String musicsPath = "musics/";
	public static final String MUSICSPATH = APPPATH + musicsPath;

	public static final String picturesPath = "pictures/";
	public static final String PICTURESPATH = APPPATH + picturesPath;

	public static final String updatePath = "update/";
	public static final String UPDATEPath = APPPATH + updatePath;
	public static final String appName = "jzb_newest.apk";

	public static final String recordPath = "record/";
	public static final String RECORDPATH = APPPATH + recordPath;

	public static final int lyricEnglishEnglish = 0;
	public static final int lyricEnglishChinese = 1;
	public static final int lyricEnglishAll = 2;
	public static final int lyricChinese = 3;
	public static final int lyricAll = 4;

	public static final String okHttpUtilsInternetConnectExceptionString = "������������";
	public static final String okHttpUtilsResultStringKey = "result";
	public static final String okHttpUtilsResultOkStringValue = "0";
	public static final String okHttpUtilsResultExceptionStringValue = "-1";
	public static final String okHttpUtilsMesgStringKey = "mesg";
	public static final String okHttpUtilsMissingResourceString = "ȱ����Դ¼��";
	public static final String okHttpUtilsServerExceptionString = "�������쳣";
	public static final String okHttpUtilsConnectServerExceptionString = "���ӷ������쳣";

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
	 * �õ��豸��Ļ�Ŀ��
	 */
	public static int getScreenWidth(Context context )
	{
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * �õ��豸��Ļ�ĸ߶�
	 */
	public static int getScreenHeight(Context context )
	{
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * �õ��豸���ܶ�
	 */
	public static float getScreenDensity(Context context )
	{
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * ���ܶ�ת��Ϊ����
	 */
	public static int dip2px(Context context , float px )
	{
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� dip �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dp2px(Context context , float dpValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context , float pxValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��pxֵת��Ϊspֵ����֤���ִ�С����
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
