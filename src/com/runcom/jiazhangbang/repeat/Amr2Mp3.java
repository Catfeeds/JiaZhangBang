package com.runcom.jiazhangbang.repeat;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;

import android.annotation.SuppressLint;

public class Amr2Mp3
{
	public static void transformation(String sourcePath , String targetPath )
	{
		System.out.println(sourcePath + "\n" + targetPath);
		changeToMp3(sourcePath ,targetPath);
	}

	@SuppressLint("UseValueOf")
	public static void changeToMp3(String sourcePath , String targetPath )
	{
		File source = new File(sourcePath);
		File target = new File(targetPath);
		AudioAttributes audio = new AudioAttributes();
		Encoder encoder = new Encoder();

		audio.setCodec("libmp3lame");

		audio.setBitRate(new Integer(128000));
		audio.setChannels(new Integer(2));
		audio.setSamplingRate(new Integer(44100));

		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);

		try
		{
			encoder.encode(source ,target ,attrs);
		}
		catch(Exception e)
		{
			System.out.println("\ne:\n" + e.toString());
		}
	}

	public static void main(String [] args )
	{
		String sourcePath = "D:\\2018.04.13.16.09.04.amr";
		String targetPath = "D:\\2018.04.13.16.09.04.mp3";
		changeToMp3(sourcePath ,targetPath);
	}

}
