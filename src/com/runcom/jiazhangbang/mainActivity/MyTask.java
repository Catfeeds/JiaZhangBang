/**
 * 
 */
package com.runcom.jiazhangbang.mainActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.SDCardHelper;
import com.runcom.jiazhangbang.util.Util;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-13
 * @time 上午11:20:16
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.mainActivity
 * @file_name MyTask.java
 * @type_name MyTask
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class MyTask extends AsyncTask < String , Void , byte [] >
{
	private Context context;
	private ProgressDialog pDialog;
	private String path = Util.PICTURESPATH;
	private String fileName;

	public MyTask(Context context)
	{
		this.context = context;
		pDialog = new ProgressDialog(context);
		pDialog.setIcon(R.drawable.ic_launcher);
		pDialog.setMessage("加载中...");
	}

	public MyTask(Context context , String fileName)
	{
		this.context = context;
		this.fileName = fileName;
		pDialog = new ProgressDialog(context);
		pDialog.setIcon(R.drawable.ic_launcher);
		pDialog.setMessage("加载中...");
	}
	
	public MyTask(Context context , String path , String fileName)
	{
		this.context = context;
		this.path = path;
		this.fileName = fileName;
		pDialog = new ProgressDialog(context , 0);
		pDialog.setIcon(R.drawable.ic_launcher);
		pDialog.setMessage("更新内容加载中...");
		pDialog.setTitle("下载更新");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
	
	public MyTask(Context context , String path , String fileName , String contents , String title)
	{
		this.context = context;
		this.path = path;
		this.fileName = fileName;
		pDialog = new ProgressDialog(context , 0);
		pDialog.setIcon(R.drawable.ic_launcher);
		pDialog.setMessage(contents);
		pDialog.setTitle(title);
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		pDialog.show();
	}

	@Override
	protected byte [] doInBackground(String...params )
	{
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			URL url = new URL(params[0]);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			
			httpConn.setDoInput(true);
			httpConn.connect();
			
			if(httpConn.getResponseCode() == 200)
			{
				bis = new BufferedInputStream(httpConn.getInputStream());
				byte [] buffer = new byte [1024 * 8];
				long total = httpConn.getContentLength();
				int count = 0;
				int inputSize = 0;
				while((inputSize = bis.read(buffer)) != -1)
				{
					baos.write(buffer ,0 ,inputSize);
					count += inputSize;
					pDialog.setProgress((int)(( count / (float ) total) * 100));
					baos.flush();
				}
				return baos.toByteArray();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(byte [] result )
	{
		super.onPostExecute(result);
		if(result == null)
		{
			Toast.makeText(context ,"加载失败！" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			// 将字节数组转成Bitmap，然后将bitmap加载的imageview控件中
			// Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0,
			// result.length);
			// imageView_main_img.setImageBitmap(bitmap);
			if(SDCardHelper.saveFileToSDCard(result , path ,fileName))
			{// 调用自己封装的方法保存文件 到SD卡中
//				Toast.makeText(context ,"保存成功！" ,Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file://"+ path + fileName),"application/vnd.android.package-archive");
				context.startActivity(intent);
			}
			else
			{
				Toast.makeText(context ,"文件保存失败！" ,Toast.LENGTH_LONG).show();
			}
		}
		pDialog.dismiss();
	}
}
