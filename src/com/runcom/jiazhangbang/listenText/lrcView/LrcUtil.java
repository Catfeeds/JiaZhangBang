package com.runcom.jiazhangbang.listenText.lrcView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 鐜嬫�? on 2016/10/21.
 */

public class LrcUtil
{
	/**
	 * 浼犲叆鐨勫弬鏁颁负鏍囧噯姝岃瘝�?�楃涓�?
	 * 
	 * @param lrcStr
	 * @return
	 */
	public static List < LrcBean > parseStr2List(String lrcStr )
	{
		List < LrcBean > list = new ArrayList < LrcBean >();
		String lrcText = lrcStr.replaceAll("&#58;" ,":").replaceAll("&#10;" ,"\n").replaceAll("&#46;" ,".").replaceAll("&#32;" ," ").replaceAll("&#45;" ,"-").replaceAll("&#13;" ,"\r").replaceAll("&#39;" ,"'");
		String [] split = lrcText.split("\n");
		for(int i = 0 ; i < split.length ; i ++ )
		{
			String lrc = split[i];
			if(lrc.contains("."))
			{
				String min = lrc.substring(lrc.indexOf("[") + 1 ,lrc.indexOf("[") + 3);
				String seconds = lrc.substring(lrc.indexOf(":") + 1 ,lrc.indexOf(":") + 3);
				String mills = lrc.substring(lrc.indexOf(".") + 1 ,lrc.indexOf(".") + 3);
				long startTime = Long.valueOf(min) * 60 * 1000 + Long.valueOf(seconds) * 1000 + Long.valueOf(mills) * 10;
				String text = lrc.substring(lrc.indexOf("]") + 1);
				if(text == null || "".equals(text))
				{
					text = " ";
				}
				LrcBean lrcBean = new LrcBean();
				lrcBean.setStart(startTime);
				lrcBean.setLrc(text);
				list.add(lrcBean);
				if(list.size() > 1)
				{
					list.get(list.size() - 2).setEnd(startTime);
				}
				if(i == split.length - 1)
				{
					list.get(list.size() - 1).setEnd(startTime + 100000);
				}
			}
		}
		return list;
	}
}
