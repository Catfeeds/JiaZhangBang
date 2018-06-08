package com.runcom.jiazhangbang.recordText;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.voice.JsonParser;

public class RecordTextSpeech
{
	private Context context;
	private String fileName;
	private RecognizerDialog mIatDialog;
	private SpeechRecognizer mIat;

	private HashMap < String , String > mIatResults = new LinkedHashMap < String , String >();

	public RecordTextSpeech()
	{
	}

	public RecordTextSpeech(Context context)
	{
		this.context = context;
	}

	public RecordTextSpeech(Context context , String fileName)
	{
		this.context = context;
		this.fileName = fileName;
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
	private InitListener mInitListener = new InitListener()
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
				Toast.makeText(context ,"录制完成" ,Toast.LENGTH_SHORT).show();
				System.out.println("录制完成");
			}
			else
			{
				// Toast.makeText(context ,"else" ,Toast.LENGTH_SHORT).show();
			}
		}

		/**
		 * 识别回调错误.
		 */
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
}
