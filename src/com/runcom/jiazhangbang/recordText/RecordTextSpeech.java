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
	 * ��ʼ����������
	 */
	private InitListener mInitListener = new InitListener()
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
				Toast.makeText(context ,"¼�����" ,Toast.LENGTH_SHORT).show();
				System.out.println("¼�����");
			}
			else
			{
				// Toast.makeText(context ,"else" ,Toast.LENGTH_SHORT).show();
			}
		}

		/**
		 * ʶ��ص�����.
		 */
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
}
