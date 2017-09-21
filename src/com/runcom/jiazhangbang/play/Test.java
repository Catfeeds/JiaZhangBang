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
		// ������д����
		dialog.setParameter(SpeechConstant.ENGINE_TYPE ,SpeechConstant.TYPE_CLOUD);
		// ���÷��ؽ����ʽ
		dialog.setParameter(SpeechConstant.RESULT_TYPE ,"json");
		// ��������ǰ�˵�:������ʱʱ�䣬���û��೤ʱ�䲻˵��������ʱ����
		dialog.setParameter(SpeechConstant.VAD_BOS ,"4000");
		// ����������˵�:��˵㾲�����ʱ�䣬���û�ֹͣ˵���೤ʱ���ڼ���Ϊ�������룬 �Զ�ֹͣ¼��
		dialog.setParameter(SpeechConstant.VAD_EOS ,"1000");
		// ���ñ�����,����Ϊ"0"���ؽ���ޱ��,����Ϊ"1"���ؽ���б��
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
		// Toast.makeText(context , "�뿪ʼ˵��", Toast.LENGTH_SHORT).show();
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
