package com.iflytek.ise;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechEvaluator;

/**
 * @ClassName: SpeechEvaluatorUtil
 * @Desciption: //语音评测工具类
 * @author: zhangshihao
 * @date: 2018-06-29
 */
public class SpeechEvaluatorUtil
{

	private static final String TAG = SpeechEvaluatorUtil.class.getSimpleName();

	private static SpeechEvaluator mIse;

	public static void init(Context context )
	{
		if(mIse == null)
		{
			mIse = SpeechEvaluator.createEvaluator(context ,null);
		}
	}

	/**
	 * @param evaText
	 *            评测用句
	 * @param mEvaluatorListener
	 *            语音评测回调接口
	 * @return 评测录音存储路径
	 */
	public static void startSpeechEva(String path , String evaText , EvaluatorListener mEvaluatorListener )
	{
		setParams(path);
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIse.startEvaluating(evaText ,null ,mEvaluatorListener);
	}

	// 通过写入音频文件进行评测
	@SuppressWarnings("static-access")
	public static void startEva(String path , byte [] audioData , String evaText , EvaluatorListener mEvaluatorListener )
	{
		setParams(path);
		// 通过writeaudio方式直接写入音频时才需要此设置
		mIse.setParameter(SpeechConstant.AUDIO_SOURCE ,"-1");

		int ret = mIse.startEvaluating(evaText ,null ,mEvaluatorListener);
		// 在startEvaluating接口调用之后，加入以下方法，即可通过直接
		// 写入音频的方式进行评测业务
		if(ret != ErrorCode.SUCCESS)
		{
			Log.i(TAG ,"识别失败,错误码：" + ret);
		}
		else
		{
			if(audioData != null)
			{
				// 防止写入音频过早导致失败
				try
				{
					new Thread().sleep(100);
				}
				catch(InterruptedException e)
				{
					Log.d(TAG ,"InterruptedException :" + e);
				}
				mIse.writeAudio(audioData ,0 ,audioData.length);
				mIse.stopEvaluating();
			}
			else
			{
				Log.i(TAG ,"audioData == null");
			}
		}
	}

	private static void setParams(String path )
	{
		Log.i(TAG ,"setParams()");
		// 设置评测语种:英语:en_us 中文：zh_cn
		mIse.setParameter(SpeechConstant.LANGUAGE ,"zh_cn");
		// 设置评测题型:句子
		mIse.setParameter(SpeechConstant.ISE_CATEGORY ,"read_sentence");
		mIse.setParameter(SpeechConstant.RESULT_LEVEL ,"plain");
		mIse.setParameter(SpeechConstant.ISE_AUDIO_PATH ,path);
		mIse.setParameter(SpeechConstant.TEXT_ENCODING ,"utf-8");
		mIse.setParameter(SpeechConstant.AUDIO_FORMAT ,"wav");
	}

	// 停止评测
	public static void stopSpeechEva()
	{
		if(mIse.isEvaluating())
		{
			mIse.stopEvaluating();
		}
	}

	// 取消评测
	public static void cancelSpeechEva()
	{
		mIse.cancel();
	}

}
