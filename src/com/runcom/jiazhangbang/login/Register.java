package com.runcom.jiazhangbang.login;

import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.judge.Judge;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;

public class Register extends Activity implements OnClickListener
{
	private EditText editText_account , editText_nickname ,
	        editText_register_phoneORemail , editText_register_password ,
	        editText_register_password_again , editText_register_checknumber;
	private TextView textView_getchecknumber , textView_tipshow;
	private Button button_register;

	private Timer timer;
	private int second = 60;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "返回";
		actionbar.setTitle(content);

		initView();

	}

	private void initView()
	{
		editText_account = (EditText) findViewById(R.id.register_account_edittext);
		editText_nickname = (EditText) findViewById(R.id.register_nickname_edittext);
		editText_register_phoneORemail = (EditText) findViewById(R.id.register_phone_or_email_edittext);
		editText_register_password = (EditText) findViewById(R.id.register_password_edittext);
		editText_register_password_again = (EditText) findViewById(R.id.register_password_again_edittext);
		editText_register_checknumber = (EditText) findViewById(R.id.register_checknumber_edittext);
		textView_getchecknumber = (TextView) findViewById(R.id.register_getchecknumber_textview);
		textView_tipshow = (TextView) findViewById(R.id.register_tipshow_textview);
		button_register = (Button) findViewById(R.id.register_register_button);

		editText_account.setOnClickListener(this);
		editText_nickname.setOnClickListener(this);
		editText_register_phoneORemail.setOnClickListener(this);
		editText_register_password.setOnClickListener(this);
		editText_register_password_again.setOnClickListener(this);
		editText_register_checknumber.setOnClickListener(this);
		textView_getchecknumber.setOnClickListener(this);
		textView_tipshow.setOnClickListener(this);
		button_register.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v )
	{
		switch(v.getId())
		{
			case R.id.register_register_button:
				String account = editText_account.getText().toString().trim();
				String nickname = editText_nickname.getText().toString().trim();
				String phoneORemail = editText_register_phoneORemail.getText().toString().trim();
				String password = editText_register_password.getText().toString().trim();
				String password_again = editText_register_password_again.getText().toString().trim();
				String check_number = editText_register_checknumber.getText().toString().trim();

				if(judge(account ,nickname ,phoneORemail ,password ,password_again ,check_number))
				{
					final TreeMap < String , String > map = Util.getMap(getApplicationContext());
					if(Judge.isMobilePhoneNumber(phoneORemail))
					{
						map.put("phone" ,phoneORemail);
					}
					else
					{
						map.put("email" ,phoneORemail);
					}
					map.put("login" ,URLEncoder.encode(account));
					map.put("nickname" ,URLEncoder.encode(nickname));
					map.put("pass" ,password);
					map.put("chkcode" ,check_number);
					System.out.println(Util.REALSERVER + "newUser.php?" + URL.getParameter(map));
					OkHttpUtils.get().url(Util.REALSERVER + "newUser.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
					{
						// TODO
						@Override
						public void onError(Call arg0 , Exception arg1 , int arg2 )
						{
							Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
						}

						@Override
						public void onResponse(String arg0 , int arg1 )
						{
							if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
							{
								Toast.makeText(getApplicationContext() ,"注册成功" ,Toast.LENGTH_LONG).show();
								finish();
							}
							else
							{
								textView_tipshow.setText(arg0);
							}
						}

						@Override
						public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
						{
							String response = arg0.body().string().trim();
							JSONObject jsonObject = new JSONObject(response);
							String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
							if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
							{
								return jsonObject.getString("mesg");
							}

							return Util.okHttpUtilsResultOkStringValue;
						}
					});
				}
				break;

			case R.id.register_getchecknumber_textview:
				String phone_or_email_getchecknumber = editText_register_phoneORemail.getText().toString().trim();
				if(Judge.isEmail(phone_or_email_getchecknumber) || Judge.isMobilePhoneNumber(phone_or_email_getchecknumber))
				{
					second = 60;
					showTime();
					textView_getchecknumber.setText("");
					textView_getchecknumber.setEnabled(false);
					final TreeMap < String , String > map = Util.getMap(getApplicationContext());
					String url = null;
					if(Judge.isEmail(phone_or_email_getchecknumber))
					{
						map.put("email" ,phone_or_email_getchecknumber);
						url = "emailCheck.php?";
					}
					else
					{
						map.put("mobile" ,phone_or_email_getchecknumber);
						url = "smsCheck.php?";
					}

					System.out.println(Util.REALSERVER + url + URL.getParameter(map));
					OkHttpUtils.get().url(Util.REALSERVER + url + URL.getParameter(map)).build().execute(new Callback < String >()
					{
						// TODO
						@Override
						public void onError(Call arg0 , Exception arg1 , int arg2 )
						{
							Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
						}

						@Override
						public void onResponse(String arg0 , int arg1 )
						{
							if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
							{
								// showTime();
								// textView_getchecknumber.setText("");
								// textView_getchecknumber.setEnabled(false);
								Toast.makeText(getApplicationContext() ,"验证码发送成功" ,Toast.LENGTH_LONG).show();
							}
							else
							{
								textView_tipshow.setText(arg0);
							}
						}

						@Override
						public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
						{
							String response = arg0.body().string().trim();
							JSONObject jsonObject = new JSONObject(response);
							String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
							if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
							{
								return jsonObject.getString("mesg");
							}

							return Util.okHttpUtilsResultOkStringValue;
						}
					});

				}
				else
				{
					textView_tipshow.setText("手机号或者邮箱不对");
				}
				break;
			default:
				break;
		}
	}

	// 计时器异步更新界面
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg )
		{
			textView_getchecknumber.setText(msg.getData().getString("msg"));
			super.handleMessage(msg);
		}
	};

	// 录音计时
	private void showTime()
	{
		TimerTask timerTask = new TimerTask()
		{

			@Override
			public void run()
			{
				second -- ;
				Message message = new Message();
				Bundle bundle = new Bundle();
				if(second <= 0)
				{
					bundle.putString("msg" ,"获取验证码");
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							textView_getchecknumber.setEnabled(true);
							timer.cancel();
						}
					});
				}
				else
				{
					bundle.putString("msg" ,second + "s");
				}
				message.setData(bundle);
				handler.sendMessage(message);
			}

		};
		timer = new Timer();
		timer.schedule(timerTask ,1000 ,1000);
	}

	private Boolean judge(String account , String nickname , String phoneORemail , String password , String password_again , String check_number )
	{
		if(account == null || account.isEmpty())
		{
			textView_tipshow.setText("用户名不能为空");
			return false;
		}
		if(nickname == null || nickname.isEmpty())
		{
			textView_tipshow.setText("昵称不能为空");
			return false;
		}
		if(Judge.isNotNickname(nickname))
		{
			textView_tipshow.setText("昵称不能为非法字符");
			return false;
		}
		if(phoneORemail == null || phoneORemail.isEmpty())
		{
			textView_tipshow.setText("手机号或者邮箱不能为空");
			return false;
		}
		if( !(Judge.isMobilePhoneNumber(phoneORemail) || Judge.isEmail(phoneORemail)))
		{
			textView_tipshow.setText("手机号或者邮箱不对");
			return false;
		}
		if(password == null || password_again == null || password.isEmpty() || password_again.isEmpty())
		{
			textView_tipshow.setText("密码不能为空");
			return false;
		}
		if( !password.equals(password_again))
		{
			textView_tipshow.setText("密码不一致");
			return false;
		}
		if(password.length() < 4 || password.length() > 10)
		{
			textView_tipshow.setText("密码要在4-10位之间");
			return false;
		}
		if(check_number == null || check_number.isEmpty())
		{
			textView_tipshow.setText("验证码不能为空");
			return false;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 重写按返回键退出播放
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		MobclickAgent.onPause(this);
	}

}
