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
	 * 初始化监听器。
	 */
	private final InitListener mInitListener = new InitListener()
	{
		@Override
		public void onInit(int code )
		{
			Log.d("LOG" ,"SpeechRecognizer init() code = " + code);
			if(code != ErrorCode.SUCCESS)
			{
				Toast.makeText(context ,"初始化失败，错误码：" + code ,Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 听写UI监听器
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
				 * 查找本地文件中是否包含识别到的词语
				 */
				// TODO
				// Toast.makeText(context ,resultBuffer + "录制完成"
				// ,Toast.LENGTH_SHORT).show();
				System.out.println(resultBuffer + "录制完成");

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
							Log.d(TAG ,"分数:" + grad);

							Toast.makeText(context ,"分数：" + String.valueOf(grad) ,Toast.LENGTH_SHORT).show();
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
		 * 识别回调错误.
		 */
		@Override
		public void onError(SpeechError error )
		{
			// Toast.makeText(context , "阿斯顿发生大事的发生的发生的发生发送到发送到发送到发送到发送到分" +
			// error.getPlainDescription(true) ,Toast.LENGTH_SHORT).show();
		}

	};

	public void setParam()
	{
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS ,null);
		mIat.setParameter(SpeechConstant.DOMAIN ,"iat");
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE ,SpeechConstant.TYPE_CLOUD);
		mIat.setParameter(SpeechConstant.ASR_PTT ,"0");
		mIat.setParameter(SpeechConstant.ACCENT ,"mandarin");
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE ,"json");
		mIat.setParameter(SpeechConstant.LANGUAGE ,"zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT ,"zh_cn");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS ,"4000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS ,"1000");
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT ,"0");
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
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
