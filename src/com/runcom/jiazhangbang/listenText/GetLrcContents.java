package com.runcom.jiazhangbang.listenText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

public class GetLrcContents implements Callable < String >
{
	private String sourcePath;

	public GetLrcContents(String sourcePath)
	{
		this.sourcePath = sourcePath;
	}

	@Override
	public String call() throws Exception
	{
		String line = "";
		String content = "";
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new URL(sourcePath).openStream()));
			while((line = reader.readLine()) != null)
			{
				content += (line + "\n");
				// System.out.println(line + "\n");
			}
		}
		catch(Exception e)
		{
			content = "ÔÝÎÞ×ÖÄ»";
			System.out.println(e);
		}
		finally
		{
			try
			{
				if(reader != null)
				{
					reader.close();
				}
			}
			catch(Exception e)
			{
				content = "ÔÝÎÞ×ÖÄ»";
				System.out.println(e);
			}
		}
		return content;
	}

}
