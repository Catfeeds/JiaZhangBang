package com.runcom.jiazhangbang.play;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.runcom.jiazhangbang.mainActivity.Search;
import com.runcom.jiazhangbang.util.Util;

public class Test
{
	static Context context;

	public Test()
	{
	}

	public Test(Context context)
	{
		Test.context = context;
	}

	public void btnVoice()
	{
		// SpeechUtility.createUtility(context ,SpeechConstant.APPID +
		// "=590aeb53");
		RecognizerDialog dialog = new RecognizerDialog(Test.context , null);
		dialog.setParameter(SpeechConstant.LANGUAGE ,"zh_cn");
		dialog.setParameter(SpeechConstant.ACCENT ,"mandarin");
		dialog.setParameter(SpeechConstant.PARAMS ,null);
		// 设置听写引擎
		dialog.setParameter(SpeechConstant.ENGINE_TYPE ,SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		dialog.setParameter(SpeechConstant.RESULT_TYPE ,"json");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		dialog.setParameter(SpeechConstant.VAD_BOS ,"4000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		dialog.setParameter(SpeechConstant.VAD_EOS ,"1000");
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		dialog.setParameter(SpeechConstant.ASR_PTT ,"0");
		dialog.setParameter(SpeechConstant.AUDIO_FORMAT ,"wav");
		dialog.setParameter(SpeechConstant.ASR_AUDIO_PATH ,Util.S2TPATH + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss" , Locale.CHINA).format(new Date()) + ".wav");

		dialog.setListener(new RecognizerDialogListener()
		{
			@Override
			public void onResult(RecognizerResult recognizerResult , boolean b )
			{
				printResult(recognizerResult);
			}

			@Override
			public void onError(SpeechError speechError )
			{
			}
		});
		dialog.show();
		// Toast.makeText(context , "请开始说话", Toast.LENGTH_SHORT).show();
	}

	private static void printResult(RecognizerResult results )
	{
		String text = parseIatResult(results.getResultString());
		Search.searchNameFromFile(context ,text ,Util.LYRICSPATH);
	}

	public static String parseIatResult(String json )
	{
		StringBuffer ret = new StringBuffer();
		try
		{
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			JSONArray words = joResult.getJSONArray("ws");
			for(int i = 0 ; i < words.length() ; i ++ )
			{
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret.toString();
	}
}
