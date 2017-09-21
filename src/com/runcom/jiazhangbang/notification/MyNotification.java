/**
 * 
 */
package com.runcom.jiazhangbang.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.welcome.Welcome;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-1
 * @time 上午11:16:27
 * @project_name JiaZhangBang
 * @package_name com.baoyz.swipemenulistview
 * @file_name Notification.java
 * @type_name Notification
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class MyNotification
{
	private static String ticker = "福利";
	private static String contentTitle = "愚人节特辑";
	private static String contentText = "男人看了流泪 , 女人看了动容.";
	private static int Drawable_id = R.drawable.ic_launcher;

	public MyNotification()
	{

	}

	public MyNotification(Context context , String ticker , String contentTitle , String contentText , int Drawable_id)
	{
		MyNotification.ticker = ticker;
		MyNotification.contentTitle = contentTitle;
		MyNotification.contentText = contentText;
		MyNotification.Drawable_id = Drawable_id;
	}

	public static void myNotification(Context context )
	{
		// Bitmap icon = BitmapFactory.decodeResource(context.getResources()
		// ,R.drawable.ic_interface);
		Notification notification = new NotificationCompat.Builder(context).setTicker(ticker) //
		.setSmallIcon(Drawable_id)//
		.setContentTitle(contentTitle)//
		.setContentText(contentText)//
		// .setLargeIcon(icon)
		.setOngoing(false)
		// .setContentInfo("contentInfo")
		.setDefaults(Notification.DEFAULT_ALL)//
		.setContentIntent(PendingIntent.getActivity(context ,100 ,new Intent(context//
		, Welcome.class) ,PendingIntent.FLAG_ONE_SHOT))//
		.setAutoCancel(true).build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1 ,notification);
	}

}
