package com.kangjiu.xu.kjoa;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.kangjiu.xu.server.APIServer;
import com.kangjiu.xu.server.BaseLogic;
import com.kangjiu.xu.set.ImmersedStatusbarUtils;
import com.kangjiu.xu.set.Pubset;
import com.kangjiu.xu.tool.Anim;
import com.kangjiu.xu.tool.MD5Tool;
import com.kangjiu.xu.tool.SPTool;
import com.xu.kangjiu.kjoa.R;
import com.xu.net.XuHttp;
import com.xu.net.XuStringCB;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import okhttp3.Call;

public class Login extends Activity {
	EditText login_txt_com;
	EditText login_txt_name;
	EditText login_txt_pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove title bar 即隐藏标题栏
		setContentView(R.layout.login);
		View topView = findViewById(R.id.lin);
		ImmersedStatusbarUtils.initAfterSetContentView(this, topView);
		init_ui();
		get_parmeter();

		check_login();
	}

	private void check_login() {
		long user_time = Long.parseLong(Pubset.user_time) + 172800000;
		long now_time = System.currentTimeMillis();
		if (user_time > now_time) {
			get_server();
		} else {
			Toast.makeText(Login.this, "登录超时", 1000).show();
		}
	}

	private void init_ui() {
		login_txt_com = (EditText) findViewById(R.id.login_txt_com);
		login_txt_name = (EditText) findViewById(R.id.login_txt_name);
		login_txt_pwd = (EditText) findViewById(R.id.login_txt_pwd);
		login_txt_name.setRawInputType(Configuration.KEYBOARD_QWERTY);
	}

	// 获取本地参数
	private void get_parmeter() {
		Pubset.server_name = (String) SPTool.get(Login.this, "server_name", "kjoa");
		Pubset.user_name = (String) SPTool.get(Login.this, "user_name", "");
		Pubset.user_pwd = (String) SPTool.get(Login.this, "user_pwd", "");
		Pubset.user_time = (String) SPTool.get(Login.this, "user_time", "0");
		login_txt_com.setText(Pubset.server_name);
		login_txt_name.setText(Pubset.user_name);

	}

	private void get_server() {

		String url = APIServer.BASE_SERVER;
		Log.d("login", url);
		HashMap<String, String> par = new HashMap<>();
		par.put("server_name", Pubset.server_name);
		XuHttp.httpStr("POST", url, par, new XuStringCB() {

			@Override
			public void onFaileure(int arg0, Exception arg1) {
				// TODO Auto-generated method stub
				Log.d("server no", arg1.toString());
			}

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				Log.d("server", arg0);
				try {
					JSONObject data = new JSONObject(arg0);

					if (data.getInt("code") == 0) {
						Pubset.SERVER_HOME = data.getString("server_home");
						user_login();
					} else {
						Toast.makeText(Login.this, "获取服务器失败，失败原因：" + data.getString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(Login.this, "网络连接失败", Toast.LENGTH_SHORT).show();

				}
			}
		});

	}

	private void user_login() {

		String url = BaseLogic.getUrl(APIServer.LOGIN);
		HashMap<String, String> par = new HashMap<>();
		par.put("login_name", Pubset.user_name);
		par.put("login_pwd", Pubset.user_pwd);

		XuHttp.httpStr("POST", url, par, new XuStringCB() {

			@Override
			public void onFaileure(int arg0, Exception arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String arg0) {
				try {
					JSONObject data = new JSONObject(arg0);

					if (data.getInt("code") == 0) {
						long time = System.currentTimeMillis();
						String time_stamp = String.valueOf(time);
						SPTool.put(Login.this, "user_time", time_stamp);

						Pubset.user_token = data.getString("token");
						Pubset.user_real_name = data.getJSONObject("staff").getString("real_name");
						Pubset.user_last_time = data.getJSONObject("staff").getString("last_time");
						Pubset.user_icon = data.getJSONObject("staff").getString("icon");
						Pubset.user_room_name = data.getJSONObject("room").getString("name");
						Pubset.user_department = data.getJSONObject("department").getString("name");
						Pubset.user_company_name = data.getJSONObject("company").getString("name");
						Pubset.user_company_address = data.getJSONObject("company").getString("address");
						Pubset.user_company_icon = data.getJSONObject("company").getString("icon");

						Intent intent = new Intent(Login.this, Main.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();

					} else {
						Toast.makeText(Login.this, "登录失败，失败原因：" + data.getString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(Login.this, "网络连接失败", Toast.LENGTH_SHORT).show();

				}

			}
		});

	}

	public void login(View v) {
		String myname = login_txt_name.getText().toString();
		String mypwd = login_txt_pwd.getText().toString();
		if (myname.length() != 11) {
			Toast.makeText(Login.this, "请输入手机号码！", Toast.LENGTH_SHORT).show();
		} else if (mypwd.length() < 6) {
			Toast.makeText(Login.this, "请输入不少于六位数的密码！", Toast.LENGTH_SHORT).show();
		} else {
			// 准备登录
			Pubset.user_name = myname;
			Pubset.user_pwd = MD5Tool.getMD5(mypwd);
			SPTool.put(Login.this, "user_name", Pubset.user_name);
			SPTool.put(Login.this, "user_pwd", Pubset.user_pwd);
			get_server();
		}

	}

	public void open_set(View v) {
		final RelativeLayout lv_check = (RelativeLayout) findViewById(R.id.login_ly_check);
		final RelativeLayout lv_set = (RelativeLayout) findViewById(R.id.login_ly_set);
		Anim anim = new Anim(getApplication());
		anim.toggle(lv_check, lv_set);

	}

	// 保持服务器设置
	public void close_set(View v) {
		String myserver = login_txt_com.getText().toString();
		if (myserver.length() > 0) {
			Pubset.server_name = myserver;
			SPTool.put(Login.this, "server_name", myserver);
		}

		final RelativeLayout lv_check = (RelativeLayout) findViewById(R.id.login_ly_check);
		final RelativeLayout lv_set = (RelativeLayout) findViewById(R.id.login_ly_set);
		Anim anim = new Anim(getApplication());
		anim.toggle(lv_set, lv_check);

	}
}
