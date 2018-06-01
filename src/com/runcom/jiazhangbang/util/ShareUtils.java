package com.runcom.jiazhangbang.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;

/**
 * 
 * @author Administrator
 * 
 */
public class ShareUtils
{
	private Activity activity;

	public ShareUtils(Activity activity)
	{
		this.activity = activity;
	}

	/**
	 * 
	 * ����ƽ̨��������
	 * 
	 * @param WebUrl
	 *            ����
	 * @param title
	 *            ����
	 * @param description
	 *            ����
	 * @param imageUrl
	 *            ����ͼƬ����
	 * @param imageID
	 *            ��Դ�ļ�id
	 * @param platform
	 *            ����ƽ̨
	 */
	public void shareSingleLink(String WebUrl , String title , String description , String imageUrl , int imageID , SHARE_MEDIA platform )
	{
		UMWeb web = new UMWeb(WebUrl);
		web.setTitle(title);
		web.setDescription(description);
		UMImage image = null;
		if(TextUtils.isEmpty(imageUrl))
		{
			image = new UMImage(activity , imageID);
		}
		else
		{
			image = new UMImage(activity , imageUrl);
		}
		image.compressFormat = Bitmap.CompressFormat.PNG;
		web.setThumb(image);

		new ShareAction(activity).setPlatform(platform).withMedia(web).setCallback(umShareListener).share();

	}

	/**
	 * ��ƽ̨��������-֧��QQ�ռ䡢΢�š�����Ȧ���ղء�
	 * 
	 * @param text
	 *            ���������
	 */
	public void shareMultipleText(String text )
	{
		new ShareAction(activity).withText(text).setDisplayList(SHARE_MEDIA.QZONE ,SHARE_MEDIA.WEIXIN ,SHARE_MEDIA.WEIXIN_CIRCLE ,SHARE_MEDIA.WEIXIN_FAVORITE).setCallback(umShareListener).open();
	}

	/**
	 * ��ƽ̨������Ƶ-֧��QQ��QQ�ռ䡢΢�š�����Ȧ���ղء�
	 * 
	 * @param title
	 *            ����
	 * @param description
	 *            ����
	 * @param musicurl
	 *            ������Ƶ����
	 * @param imageID
	 *            ͼƬ��Դ�ļ�id
	 */
	public void shareMultipleMusic(String title , String description , String musicurl , int imageID )
	{
		UMusic music = new UMusic(musicurl);
		music.setTitle(title);
		UMImage image = new UMImage(activity , imageID);
		image.compressFormat = Bitmap.CompressFormat.PNG;
		music.setThumb(image);
		music.setDescription(description);
		music.setmTargetUrl(musicurl);
		new ShareAction(activity).withMedia(music).setDisplayList(SHARE_MEDIA.QQ ,SHARE_MEDIA.QZONE ,SHARE_MEDIA.WEIXIN ,SHARE_MEDIA.WEIXIN_CIRCLE ,SHARE_MEDIA.WEIXIN_FAVORITE).setCallback(umShareListener).open();
	}

	/**
	 * ��ƽ̨��������-֧��QQ��QQ�ռ䡢΢�š�����Ȧ���ղء�
	 * 
	 * @param WebUrl
	 *            ����
	 * @param title
	 *            ����
	 * @param description
	 *            ����
	 * @param imageUrl
	 *            ����ͼƬ����
	 * @param imageID
	 *            ��Դ�ļ�id
	 */
	public void shareMultipleLink(String WebUrl , String title , String description , String imageUrl , int imageID )
	{
		UMWeb web = new UMWeb(WebUrl);
		web.setTitle(title);
		web.setDescription(description);
		UMImage image = null;
		if(TextUtils.isEmpty(imageUrl))
		{
			image = new UMImage(activity , imageID);
		}
		else
		{
			image = new UMImage(activity , imageUrl);
		}
		// �û�����͸��������ͼƬ�����������ַ�ʽ������qq���ѣ�΢������Ȧ����֧��͸������ͼƬ�����ɺ�ɫ
		image.compressFormat = Bitmap.CompressFormat.PNG;
		web.setThumb(image);
		new ShareAction(activity).withMedia(web).setDisplayList(SHARE_MEDIA.QQ ,SHARE_MEDIA.QZONE ,SHARE_MEDIA.WEIXIN ,SHARE_MEDIA.WEIXIN_CIRCLE ,SHARE_MEDIA.WEIXIN_FAVORITE).setCallback(umShareListener).open();
	}

	UMShareListener umShareListener = new UMShareListener()
	{

		@Override
		public void onStart(SHARE_MEDIA share_media )
		{

		}

		@Override
		public void onResult(final SHARE_MEDIA share_media )
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if(share_media.name().equals("WEIXIN_FAVORITE"))
					{
						Toast.makeText(activity ,share_media + "�ղسɹ�" ,Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(activity ,share_media + "����ɹ�" ,Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

		@Override
		public void onError(final SHARE_MEDIA share_media , final Throwable throwable )
		{
			if(throwable != null)
			{
				Log.d("throw" ,"throw:" + throwable.getMessage());
			}
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity ,share_media + "����ʧ��" ,Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onCancel(final SHARE_MEDIA share_media )
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity ,share_media + "����ȡ��" ,Toast.LENGTH_SHORT).show();
				}
			});
		}

	};
}
