/**
 * 
 */
package com.iflytek.voice;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.runcom.jiazhangbang.util.Util;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-5-5
 * @time 下午4:59:45
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.util
 * @file_name Text2Speech.java
 * @type_name Text2Speech
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class Text2Speech
{

	private String contents;
	private Context context;

	public Text2Speech()
	{

	}

	public Text2Speech(Context context , String contents)
	{
		this.context = context;
		this.contents = contents;
	}

	/**
	 * @param args
	 */
	public void play()
	{
		SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context ,null);
		mTts.setParameter(SpeechConstant.VOICE_NAME ,"xiaoyan"); // 设置发音人
		mTts.setParameter(SpeechConstant.SPEED ,"50");// 设置语速
		mTts.setParameter(SpeechConstant.VOLUME ,"80");// 设置音量，范围0~100
		mTts.setParameter(SpeechConstant.ENGINE_TYPE ,SpeechConstant.TYPE_CLOUD); // 设置云端
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT ,"wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH ,Util.T2SPATH + contents.trim() + ".wav");
		int code = mTts.startSpeaking(contents ,mSynListener);
		if (code != ErrorCode.SUCCESS)
		{
			Toast.makeText(context ,"语音合成失败,错误码: " + code , Toast.LENGTH_SHORT).show();
		}
	}

	private SynthesizerListener mSynListener = new SynthesizerListener()
	{
		// 会话结束回调接口，没有错误时，error为null
		public void onCompleted(SpeechError error )
		{
		}

		// 缓冲进度回调
		// percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
		public void onBufferProgress(int percent , int beginPos , int endPos , String info )
		{
		}

		// 开始播放
		public void onSpeakBegin()
		{
		}

		// 暂停播放
		public void onSpeakPaused()
		{
		}

		// 播放进度回调
		// percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
		public void onSpeakProgress(int percent , int beginPos , int endPos )
		{
		}

		// 恢复播放回调接口
		public void onSpeakResumed()
		{
		}

		// 会话事件回调接口
		public void onEvent(int arg0 , int arg1 , int arg2 , Bundle arg3 )
		{
		}
	};

}
