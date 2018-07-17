package com.hxl.pauserecord.record;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by HXL on 16/8/11. ��pcm�ļ�ת��Ϊwav�ļ�
 */
@SuppressLint("SimpleDateFormat")
public class PcmToWav
{
	/**
	 * �ϲ����pcm�ļ�Ϊһ��wav�ļ�
	 * 
	 * @param filePathList
	 *            pcm�ļ�·������
	 * @param destinationPath
	 *            Ŀ��wav�ļ�·��
	 * @return true|false
	 */
	public static boolean mergePCMFilesToWAVFile(List < String > filePathList , String destinationPath )
	{
		File [] file = new File [filePathList.size()];
		byte buffer[] = null;

		int TOTAL_SIZE = 0;
		int fileNum = filePathList.size();

		for(int i = 0 ; i < fileNum ; i ++ )
		{
			file[i] = new File(filePathList.get(i));
			TOTAL_SIZE += file[i].length();
		}

		// ��������������ʵȵȡ������õ���16λ������ 8000 hz
		WaveHeader header = new WaveHeader();
		// �����ֶ� = ���ݵĴ�С��TOTAL_SIZE) +
		// ͷ���ֶεĴ�С(������ǰ��4�ֽڵı�ʶ��RIFF�Լ�fileLength�����4�ֽ�)
		header.fileLength = TOTAL_SIZE + (44 - 8);
		header.FmtHdrLeth = 16;
		header.BitsPerSample = 16;
		header.Channels = 2;
		header.FormatTag = 0x0001;
		header.SamplesPerSec = 8000;
		header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		header.DataHdrLeth = TOTAL_SIZE;

		byte [] h = null;
		try
		{
			h = header.getHeader();
		}
		catch(IOException e1)
		{
			Log.e("PcmToWav" ,e1.getMessage());
			return false;
		}

		if(h.length != 44) // WAV��׼��ͷ��Ӧ����44�ֽ�,�������44���ֽ��򲻽���ת���ļ�
			return false;

		// ��ɾ��Ŀ���ļ�
		File destfile = new File(destinationPath);
		if(destfile.exists())
			destfile.delete();

		// �ϳ����е�pcm�ļ������ݣ�д��Ŀ���ļ�
		try
		{
			buffer = new byte [1024 * 4]; // Length of All Files, Total Size
			InputStream inStream = null;
			OutputStream ouStream = null;

			ouStream = new BufferedOutputStream(new FileOutputStream(destinationPath));
			ouStream.write(h ,0 ,h.length);
			for(int j = 0 ; j < fileNum ; j ++ )
			{
				inStream = new BufferedInputStream(new FileInputStream(file[j]));
				int size = inStream.read(buffer);
				while(size != -1)
				{
					ouStream.write(buffer);
					size = inStream.read(buffer);
				}
				inStream.close();
			}
			ouStream.close();
		}
		catch(FileNotFoundException e)
		{
			Log.e("PcmToWav" ,e.getMessage());
			return false;
		}
		catch(IOException ioe)
		{
			Log.e("PcmToWav" ,ioe.getMessage());
			return false;
		}
		clearFiles(filePathList);
		Log.i("PcmToWav" ,"mergePCMFilesToWAVFile  success!" + new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
		return true;

	}

	/**
	 * ��һ��pcm�ļ�ת��Ϊwav�ļ�
	 * 
	 * @param pcmPath
	 *            pcm�ļ�·��
	 * @param destinationPath
	 *            Ŀ���ļ�·��(wav)
	 * @param deletePcmFile
	 *            �Ƿ�ɾ��Դ�ļ�
	 * @return
	 */
	public static boolean makePCMFileToWAVFile(String pcmPath , String destinationPath , boolean deletePcmFile )
	{
		byte buffer[] = null;
		int TOTAL_SIZE = 0;
		File file = new File(pcmPath);
		if( !file.exists())
		{
			return false;
		}
		TOTAL_SIZE = (int) file.length();
		// ��������������ʵȵȡ������õ���16λ������ 8000 hz
		WaveHeader header = new WaveHeader();
		// �����ֶ� = ���ݵĴ�С��TOTAL_SIZE) +
		// ͷ���ֶεĴ�С(������ǰ��4�ֽڵı�ʶ��RIFF�Լ�fileLength�����4�ֽ�)
		header.fileLength = TOTAL_SIZE + (44 - 8);
		header.FmtHdrLeth = 16;
		header.BitsPerSample = 16;
		header.Channels = 2;
		header.FormatTag = 0x0001;
		header.SamplesPerSec = 8000;
		header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		header.DataHdrLeth = TOTAL_SIZE;

		byte [] h = null;
		try
		{
			h = header.getHeader();
		}
		catch(IOException e1)
		{
			Log.e("PcmToWav" ,e1.getMessage());
			return false;
		}

		if(h.length != 44) // WAV��׼��ͷ��Ӧ����44�ֽ�,�������44���ֽ��򲻽���ת���ļ�
			return false;

		// ��ɾ��Ŀ���ļ�
		File destfile = new File(destinationPath);
		if(destfile.exists())
			destfile.delete();

		// �ϳ����е�pcm�ļ������ݣ�д��Ŀ���ļ�
		try
		{
			buffer = new byte [1024 * 4]; // Length of All Files, Total Size
			InputStream inStream = null;
			OutputStream ouStream = null;

			ouStream = new BufferedOutputStream(new FileOutputStream(destinationPath));
			ouStream.write(h ,0 ,h.length);
			inStream = new BufferedInputStream(new FileInputStream(file));
			int size = inStream.read(buffer);
			while(size != -1)
			{
				ouStream.write(buffer);
				size = inStream.read(buffer);
			}
			inStream.close();
			ouStream.close();
		}
		catch(FileNotFoundException e)
		{
			Log.e("PcmToWav" ,e.getMessage());
			return false;
		}
		catch(IOException ioe)
		{
			Log.e("PcmToWav" ,ioe.getMessage());
			return false;
		}
		if(deletePcmFile)
		{
			file.delete();
		}
		Log.i("PcmToWav" ,"makePCMFileToWAVFile  success!" + new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
		return true;

	}

	/**
	 * ����ļ�
	 * 
	 * @param filePathList
	 */
	private static void clearFiles(List < String > filePathList )
	{
		for(int i = 0 ; i < filePathList.size() ; i ++ )
		{
			File file = new File(filePathList.get(i));
			if(file.exists())
			{
				file.delete();
			}
		}
	}

}
