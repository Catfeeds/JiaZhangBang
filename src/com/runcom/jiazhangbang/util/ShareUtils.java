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
	 * 单独平台分享链接
	 * 
	 * @param WebUrl
	 *            链接
	 * @param title
	 *            标题
	 * @param description
	 *            描述
	 * @param imageUrl
	 *            网络图片链接
	 * @param imageID
	 *            资源文件id
	 * @param platform
	 *            分享平台
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
	 * 多平台分享文字-支持QQ空间、微信、朋友圈、收藏。
	 * 
	 * @param text
	 *            分享的文字
	 */
	public void shareMultipleText(String text )
	{
		new ShareAction(activity).withText(text).setDisplayList(SHARE_MEDIA.QZONE ,SHARE_MEDIA.WEIXIN ,SHARE_MEDIA.WEIXIN_CIRCLE ,SHARE_MEDIA.WEIXIN_FAVORITE).setCallback(umShareListener).open();
	}

	/**
	 * 多平台分享音频-支持QQ、QQ空间、微信、朋友圈、收藏。
	 * 
	 * @param title
	 *            标题
	 * @param description
	 *            描述
	 * @param musicurl
	 *            网络音频链接
	 * @param imageID
	 *            图片资源文件id
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
	 * 多平台分享链接-支持QQ、QQ空间、微信、朋友圈、收藏。
	 * 
	 * @param WebUrl
	 *            链接
	 * @param title
	 *            标题
	 * @param description
	 *            描述
	 * @param imageUrl
	 *            网络图片链接
	 * @param imageID
	 *            资源文件id
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
		// 用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
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
						Toast.makeText(activity ,share_media + "收藏成功" ,Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(activity ,share_media + "分享成功" ,Toast.LENGTH_SHORT).show();
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
					Toast.makeText(activity ,share_media + "分享失败" ,Toast.LENGTH_SHORT).show();
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
					Toast.makeText(activity ,share_media + "分享取消" ,Toast.LENGTH_SHORT).show();
				}
			});
		}

	};
}
