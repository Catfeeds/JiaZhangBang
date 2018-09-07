package com.iflytek.ise;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechEvaluator;

/**
 * @ClassName: SpeechEvaluatorUtil
 * @Desciption: //�������⹤����
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
	 *            �����þ�
	 * @param mEvaluatorListener
	 *            ��������ص��ӿ�
	 * @return ����¼���洢·��
	 */
	public static void startSpeechEva(String path , String evaText , EvaluatorListener mEvaluatorListener )
	{
		setParams(path);
		// ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
		// ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
		mIse.startEvaluating(evaText ,null ,mEvaluatorListener);
	}

	// ͨ��д����Ƶ�ļ���������
	@SuppressWarnings("static-access")
	public static void startEva(String path , byte [] audioData , String evaText , EvaluatorListener mEvaluatorListener )
	{
		setParams(path);
		// ͨ��writeaudio��ʽֱ��д����Ƶʱ����Ҫ������
		mIse.setParameter(SpeechConstant.AUDIO_SOURCE ,"-1");

		int ret = mIse.startEvaluating(evaText ,null ,mEvaluatorListener);
		// ��startEvaluating�ӿڵ���֮�󣬼������·���������ͨ��ֱ��
		// д����Ƶ�ķ�ʽ��������ҵ��
		if(ret != ErrorCode.SUCCESS)
		{
			Log.i(TAG ,"ʶ��ʧ��,�����룺" + ret);
		}
		else
		{
			if(audioData != null)
			{
				// ��ֹд����Ƶ���絼��ʧ��
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
		// ������������:Ӣ��:en_us ���ģ�zh_cn
		mIse.setParameter(SpeechConstant.LANGUAGE ,"zh_cn");
		// ������������:����
		mIse.setParameter(SpeechConstant.ISE_CATEGORY ,"read_sentence");
		mIse.setParameter(SpeechConstant.RESULT_LEVEL ,"plain");
		mIse.setParameter(SpeechConstant.ISE_AUDIO_PATH ,path);
		mIse.setParameter(SpeechConstant.TEXT_ENCODING ,"utf-8");
		mIse.setParameter(SpeechConstant.AUDIO_FORMAT ,"wav");
	}

	// ֹͣ����
	public static void stopSpeechEva()
	{
		if(mIse.isEvaluating())
		{
			mIse.stopEvaluating();
		}
	}

	// ȡ������
	public static void cancelSpeechEva()
	{
		mIse.cancel();
	}

}
