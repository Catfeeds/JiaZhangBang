package com.runcom.jiazhangbang.login;

import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONObject;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gr.okhttp.OkHttpUtils;
import com.gr.okhttp.callback.Callback;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.runcom.jiazhangbang.R;
import com.runcom.jiazhangbang.chinese.Chinese;
import com.runcom.jiazhangbang.storage.MySharedPreferences;
import com.runcom.jiazhangbang.util.PermissionUtil;
import com.runcom.jiazhangbang.util.URL;
import com.runcom.jiazhangbang.util.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class Login extends Activity implements View.OnClickListener
{
	private EditText login_account_edittext;
	private EditText login_password_edittext;
	private Button login_login_button;
	private ImageView login_password_see_or_not;
	private ImageButton login_qq_imageButton , login_wechat_imageButton;
	private TextView activity_login_register;

	private LoadingDialog myLoadingDialog;

	private String loginSharedPrefrencesKey = Util.loginSharedPrefrencesKey;

	private Boolean first = true;
	private int FLAG_REQUEST = 3;
	private int EXPIRES = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		UMShareAPI.get(this);
		SpeechUtility.createUtility(this ,SpeechConstant.APPID + "=590aeb53");
		UMConfigure.init(this ,"58a3f9d6b27b0a332e001956" ,"wgcwgc75" ,UMConfigure.DEVICE_TYPE_PHONE ,"cd79ec4eb5e09d69b30139b4f03a7cb0");
		MobclickAgent.onProfileSignIn("123456890");
		PlatformConfig.setQQZone("1106913170" ,"nRgIkbfGTP3fSeZt");
		PlatformConfig.setWeixin("wx695b1c0dbf915db0" ,"d73cd63aa99b4d80912ff3af83033acf");
		UMConfigure.setLogEnabled(true);
		MobclickAgent.setScenarioType(this ,EScenarioType.E_DUM_NORMAL);
		UMConfigure.setEncryptEnabled(true);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		String content = "登录";
		actionbar.setTitle(content);

		initViews();
		setupEvents();
		initData();
		new PermissionUtil(this , Manifest.permission.READ_PHONE_STATE);
	}

	private void initData()
	{
		first = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"first" ,true);
		login_account_edittext.setText(MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"account" ,""));
		if( !first)
		{
			long time = Long.valueOf(MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"expires" ,String.valueOf(System.currentTimeMillis())));

			if(System.currentTimeMillis() - time >= EXPIRES * 1 * 60 * 60 * 1000)
			{

				String loginMode = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"loginMode" ,"");

				if(loginMode.equalsIgnoreCase("accountPassword"))
				{
					String account = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"account" ,"");
					String password = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"password" ,"");
					login(account ,password);
				}
				else
				{
					String openid = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"openid" ,"");
					String access_token = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"access_token" ,"");
					if(loginMode.equalsIgnoreCase("qq"))
					{
						qqLogin(openid ,access_token);
					}
					else
					{
						String refresh_token = MySharedPreferences.getValue(getApplicationContext() ,loginSharedPrefrencesKey ,"refresh_token" ,"");
						wechatLogin(openid ,access_token ,refresh_token);
					}
				}

			}
			else
			{
				startActivity(new Intent().setClass(getApplicationContext() ,Chinese.class));
				finish();
			}
		}

	}

	private void initViews()
	{
		login_login_button = (Button) findViewById(R.id.btn_login);
		login_account_edittext = (EditText) findViewById(R.id.et_account);
		login_password_edittext = (EditText) findViewById(R.id.et_password);
		login_password_see_or_not = (ImageView) findViewById(R.id.iv_see_password);
		login_qq_imageButton = (ImageButton) findViewById(R.id.imageButton_qq);
		login_wechat_imageButton = (ImageButton) findViewById(R.id.imageButton_wechat);
		activity_login_register = (TextView) findViewById(R.id.activity_login_register);
	}

	private void setupEvents()
	{
		login_login_button.setOnClickListener(this);
		login_password_see_or_not.setOnClickListener(this);
		login_qq_imageButton.setOnClickListener(this);
		login_wechat_imageButton.setOnClickListener(this);
		activity_login_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v )
	{
		switch(v.getId())
		{
			case R.id.btn_login:
				login();
				break;
			case R.id.iv_see_password:
				setPasswordVisibility();
				break;
			case R.id.imageButton_qq:
				qqAuthorize();
				break;
			case R.id.imageButton_wechat:
				wechatAuthorize();
				break;
			case R.id.activity_login_register:
				startActivity(new Intent(getApplicationContext() , Register.class));
				break;
			default:
				break;
		}
	}

	/**
	 * TODO 微信授权
	 */
	private void wechatAuthorize()
	{
		showLoading();
		// 取消授权
		UMShareAPI.get(this).deleteOauth(Login.this ,SHARE_MEDIA.WEIXIN ,new UMAuthListener()
		{

			@Override
			public void onStart(SHARE_MEDIA arg0 )
			{

			}

			@Override
			public void onError(SHARE_MEDIA arg0 , int arg1 , Throwable arg2 )
			{

			}

			@Override
			public void onComplete(SHARE_MEDIA arg0 , int arg1 , Map < String , String > arg2 )
			{

			}

			@Override
			public void onCancel(SHARE_MEDIA arg0 , int arg1 )
			{

			}
		});
		// 重新授权
		UMShareAPI.get(this).doOauthVerify(Login.this ,SHARE_MEDIA.WEIXIN ,new UMAuthListener()
		{

			@Override
			public void onStart(SHARE_MEDIA arg0 )
			{
				hideLoading();
			}

			@Override
			public void onError(SHARE_MEDIA arg0 , int arg1 , Throwable arg2 )
			{
				showToast("微信授权失败，请稍候重试！");
				System.out.println("微信授权失败，请稍候重试！");
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0 , int arg1 , Map < String , String > arg2 )
			{
				String uid = arg2.get("uid");
				String openid = arg2.get("openid");
				String unionid = arg2.get("unionid");
				String access_token = arg2.get("access_token");
				String refresh_token = arg2.get("refresh_token");
				String expires_in = arg2.get("expires_in");
				String name = arg2.get("name");
				String gender = arg2.get("gender");
				String iconurl = arg2.get("iconurl");

				String str = "uid:" + uid + "\topenid:" + openid + "\tunionid:" + unionid + "\taccess_token:" + access_token + "\trefresh_token:" + refresh_token + "\texpires_in:" + expires_in + "\tname:" + name + "\tgender:" + gender + "\ticonurl:" + iconurl;
				System.out.println(str);
				System.out.println(arg2.toString());
				wechatLogin(openid ,access_token ,refresh_token);
				showLoading();
			}

			@Override
			public void onCancel(SHARE_MEDIA arg0 , int arg1 )
			{
				showToast("您取消了微信授权");
				System.out.println("您取消了微信授权");
			}
		});
	}

	/**
	 * TODO qq授权
	 */
	private void qqAuthorize()
	{
		showLoading();
		// 取消授权
		UMShareAPI.get(this).deleteOauth(Login.this ,SHARE_MEDIA.QQ ,new UMAuthListener()
		{

			@Override
			public void onStart(SHARE_MEDIA arg0 )
			{

			}

			@Override
			public void onError(SHARE_MEDIA arg0 , int arg1 , Throwable arg2 )
			{

			}

			@Override
			public void onComplete(SHARE_MEDIA arg0 , int arg1 , Map < String , String > arg2 )
			{

			}

			@Override
			public void onCancel(SHARE_MEDIA arg0 , int arg1 )
			{

			}
		});
		// 重新授权
		UMShareAPI.get(this).doOauthVerify(Login.this ,SHARE_MEDIA.QQ ,new UMAuthListener()
		{
			@Override
			public void onStart(SHARE_MEDIA arg0 )
			{
				hideLoading();
			}

			@Override
			public void onError(SHARE_MEDIA arg0 , int arg1 , Throwable arg2 )
			{
				showToast("QQ授权失败，请稍候重试！");
				System.out.println("QQ授权失败，请稍候重试！");
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0 , int arg1 , Map < String , String > arg2 )
			{
				String uid = arg2.get("uid");
				String openid = arg2.get("openid");
				String unionid = arg2.get("unionid");
				String access_token = arg2.get("access_token");
				String refresh_token = arg2.get("refresh_token");
				String expires_in = arg2.get("expires_in");
				String name = arg2.get("name");
				String gender = arg2.get("gender");
				String iconurl = arg2.get("iconurl");
				String str = "uid:" + uid + "\topenid:" + openid + "\tunionid:" + unionid + "\taccess_token:" + access_token + "\trefresh_token:" + refresh_token + "\texpires_in:" + expires_in + "\tname:" + name + "\tgender:" + gender + "\ticonurl:" + iconurl;
				System.out.println(str);
				System.out.println(arg2.toString());

				showLoading();
				qqLogin(openid ,access_token);

			}

			@Override
			public void onCancel(SHARE_MEDIA arg0 , int arg1 )
			{
				showToast("您取消了QQ授权");
				System.out.println("您取消了QQ授权");
			}
		});
	}

	/**
	 * 
	 * @param openid
	 * @param access_token
	 * @param refresh_token
	 *            TODO 微信验证
	 */

	protected void wechatLogin(final String openid , final String access_token , final String refresh_token )
	{
		final TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("openid" ,openid);
		map.put("access_token" ,access_token);
		map.put("refresh_token" ,refresh_token);
		System.out.println(Util.REALSERVER + "wxAppLogin.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "wxAppLogin.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{

			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				hideLoading();
				Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
				{
					hideLoading();
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"first" ,false);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"loginMode" ,"wechat");
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"openid" ,openid);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"access_token" ,access_token);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"refresh_token" ,refresh_token);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"expires" ,String.valueOf(System.currentTimeMillis()));
					startActivity(new Intent(getApplicationContext() , Chinese.class));
					finish();
				}
				else
					if(arg0.equals("6") || arg0.equals("7"))
					{
						if(FLAG_REQUEST <= 0)
						{
							Toast.makeText(getApplicationContext() ,"服务器异常，请联系管理员！" ,Toast.LENGTH_SHORT).show();
							FLAG_REQUEST = 3;
						}
						else
						{
							FLAG_REQUEST -- ;
							hideLoading();
							wechatAuthorize();
						}
					}
					else
					{
						hideLoading();
						Toast.makeText(getApplicationContext() ,arg0 ,Toast.LENGTH_SHORT).show();
					}
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{

				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				System.out.println("微信验证：" + jsonObject.toString());
				String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
				if(result.equals("6") || result.equals("7"))
				{
					return result;
				}
				if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
				{
					return jsonObject.getString("mesg");
				}

				/**
				 * "uid": "12345", "session":"88761234",
				 * "expire":"2017-10-20 12:40:59", "type": "1"， "score": "1600",
				 * "coupon": "S88481234", "email":"qwe@123.com",
				 * "nickname":"啦啦", "access_token":
				 * " 10_QesGsu-0RJCShAl7v-luSEyayeKA35g9TWy32CeL5XA6ph2m1LD1TFDC4aIePjZJvrrgO4rH1I-_DYASxwVNT1Ayq_SQTf8QRRv5LV2cqs0 "
				 */
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"uid" ,jsonObject.getString("uid"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"session" ,jsonObject.getString("session"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"expire" ,jsonObject.getString("expire"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"type" ,jsonObject.getString("type"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"score" ,jsonObject.getString("score"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"coupon" ,jsonObject.getString("coupon"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"email" ,jsonObject.getString("email"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"nickname" ,jsonObject.getString("nickname"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"access_token" ,jsonObject.getString("access_token"));

				return Util.okHttpUtilsResultOkStringValue;
			}
		});

	}

	/**
	 * 
	 * @param openid
	 * @param access_token
	 *            TODO qq验证
	 */
	private void qqLogin(final String openid , final String access_token )
	{
		final TreeMap < String , String > map = Util.getMap(getApplicationContext());
		map.put("openid" ,openid);
		map.put("access_token" ,access_token);
		System.out.println(Util.REALSERVER + "qqlogin.php?" + URL.getParameter(map));
		OkHttpUtils.get().url(Util.REALSERVER + "qqlogin.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
		{

			@Override
			public void onError(Call arg0 , Exception arg1 , int arg2 )
			{
				hideLoading();
				Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(String arg0 , int arg1 )
			{
				if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
				{
					hideLoading();
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"first" ,false);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"loginMode" ,"qq");
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"openid" ,openid);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"access_token" ,access_token);
					MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"expires" ,String.valueOf(System.currentTimeMillis()));
					startActivity(new Intent(getApplicationContext() , Chinese.class));
					finish();
				}
				else
					if(arg0.equals("6") || arg0.equals("7"))
					{
						if(FLAG_REQUEST <= 0)
						{
							Toast.makeText(getApplicationContext() ,"服务器异常，请联系管理员！" ,Toast.LENGTH_SHORT).show();
							FLAG_REQUEST = 3;
						}
						else
						{
							hideLoading();
							FLAG_REQUEST -- ;
							qqAuthorize();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext() ,arg0 ,Toast.LENGTH_SHORT).show();
						hideLoading();
					}
			}

			@Override
			public String parseNetworkResponse(Response arg0 , int arg1 ) throws Exception
			{

				String response = arg0.body().string().trim();
				JSONObject jsonObject = new JSONObject(response);
				System.out.println("QQ验证：" + jsonObject.toString());
				String result = jsonObject.getString(Util.okHttpUtilsResultStringKey);
				if(result.equals("6") || result.equals("7"))
				{
					return result;
				}
				if( !Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(result))
				{
					return jsonObject.getString("mesg");
				}

				/**
				 * "uid": "12345", "session":"88761234",
				 * "expire":"2017-10-20 12:40:59", "type": "1"， "score": "1600",
				 * "coupon": "S88481234", "email":"qwe@123.com", "nickname":"啦啦"
				 */
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"uid" ,jsonObject.getString("uid"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"session" ,jsonObject.getString("session"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"expire" ,jsonObject.getString("expire"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"type" ,jsonObject.getString("type"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"score" ,jsonObject.getString("score"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"coupon" ,jsonObject.getString("coupon"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"email" ,jsonObject.getString("email"));
				MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"nickname" ,jsonObject.getString("nickname"));
				return Util.okHttpUtilsResultOkStringValue;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult(requestCode ,resultCode ,data);
		UMShareAPI.get(this).onActivityResult(requestCode ,resultCode ,data);
	}

	/**
	 * TODO 账号密码登录
	 */
	private void login(String...args )
	{
		final String login;
		final String pass;
		if(2 == args.length)
		{
			login = args[0];
			pass = args[1];
		}
		else
		{
			login = getAccount();
			pass = getPassword();
		}
		if(login.isEmpty())
		{
			showToast("您输入的账号为空！");
			return;
		}

		if(pass.isEmpty())
		{
			showToast("您输入的密码为空！");
			return;
		}
		showLoading();
		Thread loginRunnable = new Thread()
		{

			@Override
			public void run()
			{
				super.run();
				final TreeMap < String , String > map = Util.getMap(getApplicationContext());
				map.put("login" ,login);
				map.put("pass" ,pass);
				System.out.println(Util.REALSERVER + "dologin.php?" + URL.getParameter(map));
				OkHttpUtils.get().url(Util.REALSERVER + "dologin.php?" + URL.getParameter(map)).build().execute(new Callback < String >()
				{

					@Override
					public void onError(Call arg0 , Exception arg1 , int arg2 )
					{
						hideLoading();
						Toast.makeText(getApplicationContext() ,Util.okHttpUtilsConnectServerExceptionString ,Toast.LENGTH_LONG).show();
					}

					@Override
					public void onResponse(String arg0 , int arg1 )
					{
						if(Util.okHttpUtilsResultOkStringValue.equalsIgnoreCase(arg0))
						{
							showToast("登录成功");
							MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"first" ,false);
							MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"loginMode" ,"accountPassword");
							MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"account" ,login);
							MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"password" ,pass);
							MySharedPreferences.putValue(getApplicationContext() ,loginSharedPrefrencesKey ,"expires" ,String.valueOf(System.currentTimeMillis()));
							startActivity(new Intent(getApplicationContext() , Chinese.class));
							hideLoading();
							finish();
						}
						else
						{
							Toast.makeText(getApplicationContext() ,"您输入的账号或密码不匹配" ,Toast.LENGTH_LONG).show();
							hideLoading();
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
						/**
						 * "uid": "12345", "score": "1600",
						 * "coupon":"S88481234", "email": "123@abc.com",
						 * "session":"88761234", "type": "1"
						 */
						MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"uid" ,jsonObject.getString("uid"));
						MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"score" ,jsonObject.getString("score"));
						MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"coupon" ,jsonObject.getString("coupon"));
						MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"email" ,jsonObject.getString("email"));
						MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"session" ,jsonObject.getString("session"));
						MySharedPreferences.putValue(getApplicationContext() ,Util.loginSharedPrefrencesKey ,"type" ,jsonObject.getString("type"));
						return Util.okHttpUtilsResultOkStringValue;
					}
				});

			}
		};
		loginRunnable.start();

	}

	/**
	 * 设置密码可见和不可见的相互转换
	 */
	private void setPasswordVisibility()
	{
		if(login_password_see_or_not.isSelected())
		{
			login_password_see_or_not.setSelected(false);
			// 密码不可见
			login_password_edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

		}
		else
		{
			login_password_see_or_not.setSelected(true);
			// 密码可见
			login_password_edittext.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}

	}

	/**
	 * 获取账号
	 */
	private String getAccount()
	{
		return login_account_edittext.getText().toString().trim();
	}

	/**
	 * 获取密码
	 */
	private String getPassword()
	{
		return login_password_edittext.getText().toString().trim();
	}

	/**
	 * 显示加载的进度款
	 */
	private void showLoading()
	{
		if(myLoadingDialog == null)
		{
			myLoadingDialog = new LoadingDialog(this , getString(R.string.loading) , false);
		}
		myLoadingDialog.show();
	}

	/**
	 * 隐藏加载的进度框
	 */
	private void hideLoading()
	{
		if(myLoadingDialog != null)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					myLoadingDialog.cancel();
					myLoadingDialog = null;
				}
			});

		}
	}

	/**
	 * 监听回退键
	 */
	@Override
	public void onBackPressed()
	{
		if(myLoadingDialog != null)
		{
			myLoadingDialog.cancel();
			myLoadingDialog = null;
		}
		finish();

	}

	private void showToast(final String msg )
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(getApplicationContext() ,msg ,Toast.LENGTH_SHORT).show();
			}
		});

	}

	// 两秒内按返回键两次退出程序
	private long exitTime = 0;

	// 重写按返回键
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event )
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(myLoadingDialog != null)
			{
				myLoadingDialog.cancel();
				myLoadingDialog = null;
			}
			if((System.currentTimeMillis() - exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext() ,"再按一次退出程序" ,Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				MobclickAgent.onKillProcess(this);
				finish();
				System.exit(0);
			}
			return true;
		}

		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			finish();
			return true;
		}
		return super.onKeyDown(keyCode ,event);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		hideLoading();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		hideLoading();
		MobclickAgent.onPause(this);
	}

	/**
	 * 页面销毁前回调的方法
	 */
	protected void onDestroy()
	{
		if(myLoadingDialog != null)
		{
			myLoadingDialog.cancel();
			myLoadingDialog = null;
		}
		super.onDestroy();
		UMShareAPI.get(this).release();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		hideLoading();
		MobclickAgent.onPause(this);
	}

}
