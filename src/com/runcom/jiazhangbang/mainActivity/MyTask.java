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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.util.SDCardHelper;

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
	private ProgressDialog progressDialog;
	private String path;
	private String fileName;
	private Boolean flag = true;

	// public MyTask(Context context)
	// {
	// this.context = context;
	// progressDialog = new ProgressDialog(context);
	// progressDialog.setIcon(R.drawable.ic_launcher);
	// progressDialog.setMessage("加载中...");
	// }

	// public MyTask(Context context , String fileName)
	// {
	// this.context = context;
	// this.fileName = fileName;
	// progressDialog = new ProgressDialog(context);
	// progressDialog.setIcon(R.drawable.ic_launcher);
	// progressDialog.setMessage("加载中...");
	// }

	// public MyTask(final Context context , String path , String fileName)
	// {
	// this.context = context;
	// this.path = path;
	// this.fileName = fileName;
	// progressDialog = new ProgressDialog(context , 0);
	// progressDialog.setIcon(R.drawable.ic_launcher);
	// progressDialog.setMessage("更新内容加载中...");
	// progressDialog.setTitle("下载更新");
	// progressDialog.setCancelable(false);
	// progressDialog.setMax(100);
	// progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	// progressDialog.setButton(DialogInterface.BUTTON_POSITIVE ,"后台更新" ,new
	// DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog , int which )
	// {
	// Toast.makeText(context ,"已切换至后台更新" ,Toast.LENGTH_SHORT).show();
	// flag = true;
	// }
	// });
	// progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE ,"取消" ,new
	// DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog , int which )
	// {
	// Toast.makeText(context ,"您取消了本次更新" ,Toast.LENGTH_SHORT).show();
	// flag = false;
	// }
	// });
	// }

	public MyTask(final Context context , String path , String fileName , String contents , String title)
	{
		this.context = context;
		this.path = path;
		this.fileName = fileName;
		progressDialog = new ProgressDialog(context , 0);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setMessage(contents);
		progressDialog.setTitle(title);
		progressDialog.setCancelable(false);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE ,"后台更新" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Toast.makeText(context ,"已切换至后台更新" ,Toast.LENGTH_SHORT).show();
				flag = true;
			}
		});
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE ,"取消" ,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog , int which )
			{
				Toast.makeText(context ,"您取消了本次更新" ,Toast.LENGTH_SHORT).show();
				flag = false;
			}
		});
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		progressDialog.show();
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
					progressDialog.setProgress((int) ((count / (float) total) * 100));
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
			Toast.makeText(context ,"更新失败 请检查网络后重试" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			// 将字节数组转成Bitmap，然后将bitmap加载的imageview控件中
			// Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0,
			// result.length);
			// imageView_main_img.setImageBitmap(bitmap);
			if(flag && SDCardHelper.saveFileToSDCard(result ,path ,fileName))
			{// 调用自己封装的方法保存文件 到SD卡中
				// Toast.makeText(context ,"保存成功！" ,Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file://" + path + fileName) ,"application/vnd.android.package-archive");
				context.startActivity(intent);
			}
			else
			{
				// Toast.makeText(context ,"取消更新" ,Toast.LENGTH_LONG).show();
			}
		}
		progressDialog.dismiss();
	}
}
