package com.runcom.jiazhangbang.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class URL
{
	public URL()
	{
	}

	public static String getParameter(Map < String , String > parameter )
	{
		String contents = Util.SECRETKEY;
		String realParameter = "";
		Set < String > keySet = parameter.keySet();
		Iterator < String > iter = keySet.iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			String value = parameter.get(key);
			contents += value;
			realParameter += (key + "=" + value + "&");
			// System.out.println(key + ":" + value);
		}
		realParameter += ("sign=" + getSign(contents));
		return realParameter;
	}

	public static String getSign(String contents )
	{
		return MD5.md5(contents);
	}

}
