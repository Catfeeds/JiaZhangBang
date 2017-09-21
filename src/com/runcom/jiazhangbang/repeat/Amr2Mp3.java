package com.runcom.jiazhangbang.repeat;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;

import android.annotation.SuppressLint;

@SuppressLint("UseValueOf")
public class Amr2Mp3
{
	public static void transformation(String sourcePath , String targetPath )
	{
		// String sourcePath =
		// "E:\\Eclipse_Web\\lbtm\\webapp\\uploadFiles\\1395047224460.amr";
		// String targetPath =
		// "E:\\Eclipse_Web\\lbtm\\webapp\\uploadFiles\\1395047224460.mp3";
		changeToMp3(sourcePath ,targetPath);
	}

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
			System.out.println(e + "\n" + e.toString());
		}
	}

}
