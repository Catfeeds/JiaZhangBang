package com.iflytek.ise;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

/**
 * @ClassName: AudioRecordUtil
 * @Desciption: //¼��wav��ʽ��Ƶ
 * @author: zhangshihao
 * @date: 2018-07-21 δʹ��--------------wgc -2018-9-6
 */
public class AudioRecordUtil
{

	private static AudioRecordUtil mInstance;
	private final AudioRecord recorder;
	// ¼��Դ
	private static int audioSource = MediaRecorder.AudioSource.MIC;
	// ¼���Ĳ���Ƶ��
	private static int audioRate = 16000;
	// ¼����������������
	private static int audioChannel = AudioFormat.CHANNEL_IN_MONO;
	// ���������
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	// ����Ĵ�С
	private static int bufferSize = AudioRecord.getMinBufferSize(audioRate ,audioChannel ,audioFormat);
	// ��¼����״̬
	private boolean isRecording = false;
	// �����ź�����
	private byte [] noteArray;
	// PCM�ļ�
	private File pcmFile;
	// WAV�ļ�
	private File wavFile;
	// �ļ������
	private OutputStream os;
	// �ļ���Ŀ¼
	private final String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/eva/";

	// wav�ļ�Ŀ¼
	private final String outFileName = basePath + "/eva.wav";

	// pcm�ļ�Ŀ¼
	private final String inFileName = basePath + "/eva.pcm";

	private AudioRecordUtil()
	{
		createFile();// �����ļ�
		recorder = new AudioRecord(audioSource , audioRate , audioChannel , audioFormat , bufferSize);
	}

	public synchronized static AudioRecordUtil getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new AudioRecordUtil();
		}
		return mInstance;
	}

	// ��ȡ¼�����������߳�
	class WriteThread implements Runnable
	{
		@Override
		public void run()
		{
			writeData();
		}
	}

	// ��ʼ¼��
	public void startRecord()
	{
		isRecording = true;
		recorder.startRecording();
	}

	// ֹͣ¼��
	public void stopRecord()
	{
		isRecording = false;
		recorder.stop();
	}

	// ������д���ļ���,�ļ���д��û�����Ż�
	public void writeData()
	{
		noteArray = new byte [bufferSize];
		// �����ļ������
		try
		{
			os = new BufferedOutputStream(new FileOutputStream(pcmFile));
		}
		catch(IOException e)
		{

		}
		while(isRecording == true)
		{
			int recordSize = recorder.read(noteArray ,0 ,bufferSize);
			if(recordSize > 0)
			{
				try
				{
					os.write(noteArray);
				}
				catch(IOException e)
				{

				}
			}
		}
		if(os != null)
		{
			try
			{
				os.close();
			}
			catch(IOException e)
			{

			}
		}
	}

	// ����õ��ɲ��ŵ���Ƶ�ļ�
	public void convertWaveFile()
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen;
		long longSampleRate = AudioRecordUtil.audioRate;
		int channels = 1;
		long byteRate = 16 * AudioRecordUtil.audioRate * channels / 8;
		byte [] data = new byte [bufferSize];
		try
		{
			in = new FileInputStream(inFileName);
			out = new FileOutputStream(outFileName);
			totalAudioLen = in.getChannel().size();
			// ���ڲ�����RIFF��WAV
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out ,totalAudioLen ,totalDataLen ,longSampleRate ,channels ,byteRate);
			while(in.read(data) != -1)
			{
				out.write(data);
			}
			in.close();
			out.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * �κ�һ���ļ���ͷ��������Ӧ��ͷ�ļ����ܹ�ȷ���ı�ʾ�����ļ��ĸ�ʽ��wave��RIFF�ļ��ṹ��ÿһ����Ϊһ��chunk��������RIFF WAVE
	 * chunk�� FMT Chunk��Fact chunk,Data chunk,����Fact chunk�ǿ���ѡ��ģ�
	 */
	private void WriteWaveFileHeader(FileOutputStream out , long totalAudioLen , long totalDataLen , long longSampleRate , int channels , long byteRate ) throws IOException
	{
		byte [] header = new byte [44];
		header[0] = 'R'; // RIFF
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);// ���ݴ�С
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';// WAVE
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		// FMT Chunk
		header[12] = 'f'; // 'fmt '
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';// �����ֽ�
		// ���ݴ�С
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		// ���뷽ʽ 10HΪPCM�����ʽ
		header[20] = 1; // format = 1
		header[21] = 0;
		// ͨ����
		header[22] = (byte) channels;
		header[23] = 0;
		// �����ʣ�ÿ��ͨ���Ĳ����ٶ�
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		// ��Ƶ���ݴ�������,������*ͨ����*�������/8
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		// ȷ��ϵͳһ��Ҫ�������ٸ������ֽڵ����ݣ�ȷ����������ͨ����*����λ��
		header[32] = (byte) (1 * 16 / 8);
		header[33] = 0;
		// ÿ������������λ��
		header[34] = 16;
		header[35] = 0;
		// Data chunk
		header[36] = 'd';// data
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header ,0 ,44);
	}

	// �����ļ���,���ȴ���Ŀ¼��Ȼ�󴴽���Ӧ���ļ�
	public void createFile()
	{
		File baseFile = new File(basePath);
		if( !baseFile.exists())
			baseFile.mkdirs();
		pcmFile = new File(basePath + "/eva.pcm");
		wavFile = new File(basePath + "/eva.wav");
		if(pcmFile.exists())
		{
			pcmFile.delete();
		}
		if(wavFile.exists())
		{
			wavFile.delete();
		}
		try
		{
			pcmFile.createNewFile();
			wavFile.createNewFile();
		}
		catch(IOException e)
		{

		}
	}

	// ��Ƶ�ļ�תbyte����
	public static byte [] getAudioData(String audioPath )
	{
		byte [] buffer = null;
		try
		{
			File file = new File(audioPath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte [] b = new byte [1000];
			int n;
			while((n = fis.read(b)) != -1)
			{
				bos.write(b ,0 ,n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return buffer;
	}

	// ��¼����
	public void recordData()
	{
		new Thread(new WriteThread()).start();
	}

	public String getOutFileName()
	{
		return outFileName;
	}

}