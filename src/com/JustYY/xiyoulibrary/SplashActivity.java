package com.JustYY.xiyoulibrary;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Toast.makeText(getApplicationContext(), "欢迎使用西邮图书馆", Toast.LENGTH_SHORT)
				.show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						MainActivity.class);

				startActivity(intent);
				SplashActivity.this.finish();
			}
		}, 2000);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
