package com.runcom.jiazhangbang.recordText;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.ise.AudioRecordUtil;
import com.iflytek.ise.SpeechEvaluatorUtil;
import com.iflytek.voice.JsonParser;

public class RecordTextSpeech
{
	private Context context;
	private String fileName;
	private String content;
	private RecognizerDialog mIatDialog;
	private SpeechRecognizer mIat;
	private final String TAG = "RecordTextSpeech";

	private final HashMap < String , String > mIatResults = new LinkedHashMap < String , String >();

	public RecordTextSpeech()
	{
	}

	public RecordTextSpeech(Context context)
	{
		this.context = context;
	}

	public RecordTextSpeech(Context context , String fileName , String content)
	{
		this.context = context;
		this.fileName = fileName;
		this.content = content;
	}

	public void play()
	{
		mIat = SpeechRecognizer.createRecognizer(context ,null);
		// mIat = SpeechRecognizer.createRecognizer(context ,mInitListener);
		setParam();
		mIatResults.clear();
		mIatDialog = new RecognizerDialog(context , mInitListener);
		mIatDialog.setListener(mRecognizerDialogListener);
		mIatDialog.show();
		// System.out.println("gege zhi xing le yi ci");
	}

	/**
	 * ��ʼ����������
	 */
	private final InitListener mInitListener = new InitListener()
	{
		@Override
		public void onInit(int code )
		{
			Log.d("LOG" ,"SpeechRecognizer init() code = " + code);
			if(code != ErrorCode.SUCCESS)
			{
				Toast.makeText(context ,"��ʼ��ʧ�ܣ������룺" + code ,Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * ��дUI������
	 */
	RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener()
	{
		@Override
		public void onResult(RecognizerResult results , boolean isLast )
		{
			String text = JsonParser.parseIatResult(results.getResultString());
			String sn = null;
			if( !isLast)
			{
				try
				{
					JSONObject resultJson = new JSONObject(results.getResultString());
					sn = resultJson.optString("sn");
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}

				mIatResults.put(sn ,text);

				StringBuffer resultBuffer = new StringBuffer();
				for(String key : mIatResults.keySet())
				{
					resultBuffer.append(mIatResults.get(key));
				}
				/**
				 * ���ұ����ļ����Ƿ����ʶ�𵽵Ĵ���
				 */
				// TODO
				// Toast.makeText(context ,resultBuffer + "¼�����"
				// ,Toast.LENGTH_SHORT).show();
				System.out.println(resultBuffer + "¼�����");

				try
				{
					Thread.sleep(3 * 1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				// TODO
				Log.d(TAG ,fileName);
				SpeechEvaluatorUtil.init(context);
				SpeechEvaluatorUtil.startEva(fileName ,AudioRecordUtil.getAudioData(fileName) ,content ,new EvaluatorListener()
				{
					@Override
					public void onVolumeChanged(int i , byte [] bytes )
					{

					}

					@Override
					public void onBeginOfSpeech()
					{
						Log.d(TAG ,"onBeginOfSpeech...");
					}

					@Override
					public void onEndOfSpeech()
					{
						Log.d(TAG ,"onEndOfSpeech...");
					}

					@Override
					public void onResult(EvaluatorResult result , boolean isLast )
					{
						Log.d(TAG ,"onResult : isLast == " + isLast);
						if(isLast)
						{
							StringBuilder builder = new StringBuilder();
							builder.append(result.getResultString());
							float grad = parseXml(builder.toString());
							Log.d(TAG ,"����:" + grad);

							Toast.makeText(context ,"������" + String.valueOf(grad) ,Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onError(SpeechError speechError )
					{
						Log.d(TAG ,"onError ...");
						if(speechError != null)
						{
							Log.d(TAG ,"speechError:" + speechError.getErrorCode() + "," + speechError.getErrorDescription());
							// Toast.makeText(context
							// ,"speechError:" +
							// speechError.getErrorCode() + "," +
							// speechError.getErrorDescription()
							// ,Toast.LENGTH_SHORT).show();
						}
						else
						{
							Log.d(TAG ,"evaluator over");
						}
					}

					@Override
					public void onEvent(int i , int i1 , int i2 , Bundle bundle )
					{

					}
				});
			}
			else
			{
				// Toast.makeText(context ,"else" ,Toast.LENGTH_SHORT).show();
			}
		}

		/**
		 * ʶ��ص�����.
		 */
		@Override
		public void onError(SpeechError error )
		{
			// Toast.makeText(context , "��˹�ٷ������µķ����ķ����ķ������͵����͵����͵����͵����͵���" +
			// error.getPlainDescription(true) ,Toast.LENGTH_SHORT).show();
		}

	};

	public void setParam()
	{
		// ��ղ���
		mIat.setParameter(SpeechConstant.PARAMS ,null);
		mIat.setParameter(SpeechConstant.DOMAIN ,"iat");
		// ������д����
		mIat.setParameter(SpeechConstant.ENGINE_TYPE ,SpeechConstant.TYPE_CLOUD);
		mIat.setParameter(SpeechConstant.ASR_PTT ,"0");
		mIat.setParameter(SpeechConstant.ACCENT ,"mandarin");
		// ���÷��ؽ����ʽ
		mIat.setParameter(SpeechConstant.RESULT_TYPE ,"json");
		mIat.setParameter(SpeechConstant.LANGUAGE ,"zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT ,"zh_cn");
		// ��������ǰ�˵�:������ʱʱ�䣬���û��೤ʱ�䲻˵��������ʱ����
		mIat.setParameter(SpeechConstant.VAD_BOS ,"4000");
		// ����������˵�:��˵㾲�����ʱ�䣬���û�ֹͣ˵���೤ʱ���ڼ���Ϊ�������룬 �Զ�ֹͣ¼��
		mIat.setParameter(SpeechConstant.VAD_EOS ,"1000");
		// ���ñ�����,����Ϊ"0"���ؽ���ޱ��,����Ϊ"1"���ؽ���б��
		mIat.setParameter(SpeechConstant.ASR_PTT ,"0");
		// ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
		// ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT ,"wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH ,fileName);
	}

	@SuppressWarnings("static-access")
	private float parseXml(String xmlStr )
	{
		float totalScore = 0f;
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlStr));
			int eventType = xmlPullParser.getEventType();
			String value;
			while(eventType != xmlPullParser.END_DOCUMENT)
			{
				String nodeName = xmlPullParser.getName();
				switch(eventType)
				{
					case XmlPullParser.START_TAG:
						if("total_score".equals(nodeName))
						{
							value = xmlPullParser.getAttributeValue(0);
							totalScore = Float.parseFloat(value);
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				eventType = xmlPullParser.next();
			}
		}
		catch(XmlPullParserException xppe)
		{
			Log.i(TAG ,xppe.toString());
		}
		catch(IOException ioe)
		{
			Log.i(TAG ,ioe.toString());
		}
		return totalScore;
	}
}
