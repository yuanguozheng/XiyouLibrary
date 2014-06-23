package com.JustYY.xiyoulibrary;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.JustYY.xiyoulibrary.functions.HttpRequestHelper;
import com.JustYY.xiyoulibrary.functions.NetworkState;
import com.JustYY.xiyoulibrary.model.UserInfo;
import com.zxing.activity.CaptureActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity {

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	static UserInfo userInfo = new UserInfo();
	static String USER_NAME = "";
	static String PASSWORD_STRING = "";

	String usernameString;
	String passwordString;

	ProgressDialog tips;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message e) {
			if (e.what == 1) {
				Looper.prepare();
				procResult((String[]) e.obj);
				Looper.loop();
			} else {
				Looper.prepare();
				Toast.makeText(LoginActivity.this, "请求数据错误", Toast.LENGTH_SHORT)
						.show();
				Looper.loop();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		Button loginButton = (Button) findViewById(R.id.login);
		final EditText usernameText = (EditText) findViewById(R.id.username);
		final EditText passwordText = (EditText) findViewById(R.id.password);
		// final EditText resultText = (EditText) findViewById(R.id.editText1);
		loginButton.setOnClickListener(new Button.OnClickListener() {

			@SuppressLint("DefaultLocale")
			@Override
			public void onClick(View v) {
				if (!NetworkState.isNetworkAvailable(LoginActivity.this)) {
					Toast.makeText(LoginActivity.this, "你没有联网哦",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String s = usernameText.getText().toString().toUpperCase();
				usernameText.setText(s);

				usernameString = usernameText.getText().toString();
				passwordString = passwordText.getText().toString();

				if (usernameString.isEmpty() || passwordString.isEmpty()) {
					Toast.makeText(LoginActivity.this, "用户名或密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}

				tips = new ProgressDialog(LoginActivity.this);
				tips.setCancelable(false);
				tips.setIndeterminate(true);
				tips.setMessage("正在请求数据，请稍后...");
				tips.setCanceledOnTouchOutside(false);
				tips.show();

				final HttpRequestHelper httpRequestHelper = new HttpRequestHelper(
						"http://222.24.63.109/xiyoulib/login",
						HttpRequestHelper.POST);

				httpRequestHelper.addParameter(new BasicNameValuePair(
						"userNumber", usernameString));
				httpRequestHelper.addParameter(new BasicNameValuePair(
						"password", passwordString));

				final HttpRequestHelper infoRequest = new HttpRequestHelper(
						"http://222.24.63.109/libusername/",
						HttpRequestHelper.POST);
				infoRequest.addParameter("id", usernameString);
				infoRequest.addParameter("password", passwordString);

				new Thread() {
					public void run() {
						String resString, infoString;
						Message msg = new Message();
						try {
							resString = httpRequestHelper.doRequest();
							infoString = infoRequest.doRequest();
							if (resString.isEmpty() || infoString.isEmpty()) {
								msg.what = 0;
							} else {
								msg.what = 1;
								String[] result = new String[2];
								result[0] = resString;
								result[1] = infoString;
								msg.obj = result;
							}
						} catch (Exception e) {
							msg.what = 0;
						} finally {
							tips.dismiss();
							handler.handleMessage(msg);
						}
						// resultText.setText(aString.toCharArray(), 0,
						// aString.length());
					}
				}.start();
			}
		});
		Button scanbarcode = (Button) findViewById(R.id.username_scan);
		scanbarcode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						CaptureActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		usernameText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@SuppressLint("DefaultLocale")
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (usernameText.getText().toString().isEmpty())
					return;
				if (!hasFocus) {
					String s = usernameText.getText().toString().toUpperCase();
					usernameText.setText(s);
				}
			}
		});
	}

	public void procResult(String[] resString) {
		// handler = new Handler(LoginActivity.this.getMainLooper());
		if (resString[0].equals("shibai")) {
			Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (resString[0].equals("null")) {
			Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		try {
			JSONObject infoObj = new JSONObject(resString[1]);
			userInfo.setId(infoObj.getString("ID"));
			userInfo.setCls(infoObj.getString("Cls"));
			userInfo.setName(infoObj.getString("Name"));
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("BorrowInfoString", resString[0]);
		USER_NAME = usernameString;
		PASSWORD_STRING = passwordString;
		intent.putExtras(data);
		startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 处理扫描结果（在界面上显示）
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			EditText usernameText = (EditText) findViewById(R.id.username);
			usernameText.setText(scanResult);
			EditText passwordText = (EditText) findViewById(R.id.password);
			passwordText.requestFocus();
		}
	}
}
