package com.JustYY.xiyoulibrary;

import com.JustYY.xiyoulibrary.functions.Exit;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost tabHost;
	private Intent searchIntent;
	private Intent newBookIntent;
	private Intent rankIntent;
	private Intent loginIntent;
	private Intent moreIntent;

	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		textView = (TextView) findViewById(R.id.titlebarText);

		this.searchIntent = new Intent(this, SearchActivity.class);
		this.newBookIntent = new Intent(this, NewBookActivity.class);
		this.rankIntent = new Intent(this, RankActivity.class);
		this.loginIntent = new Intent(this, LoginActivity.class);

		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button2))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button3))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button4))
				.setOnCheckedChangeListener(this);

		setupIntent();

		this.tabHost.setCurrentTabByTag("A_TAB");
		textView.setText("图书速查");
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.tabHost.setCurrentTabByTag("A_TAB");
				textView.setText("图书速查");
				break;
			case R.id.radio_button1:
				this.tabHost.setCurrentTabByTag("B_TAB");
				textView.setText("新书推荐");
				break;
			case R.id.radio_button2:
				this.tabHost.setCurrentTabByTag("C_TAB");
				textView.setText("排行榜");
				break;
			case R.id.radio_button3:
				this.tabHost.setCurrentTabByTag("D_TAB");
				textView.setText("我的借阅");
				break;
			case R.id.radio_button4:
				this.tabHost.setCurrentTabByTag("MORE_TAB");
				textView.setText("更多");
				break;
			}
		}

	}

	private void setupIntent() {
		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_search,
				R.drawable.icon_1_n, this.searchIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_new,
				R.drawable.icon_2_n, this.newBookIntent));

		localTabHost.addTab(buildTabSpec("C_TAB", R.string.main_rank,
				R.drawable.icon_3_n, this.rankIntent));

		localTabHost.addTab(buildTabSpec("D_TAB", R.string.main_login,
				R.drawable.icon_4_n, this.loginIntent));

		localTabHost.addTab(buildTabSpec("MORE_TAB", R.string.main_more,
				R.drawable.icon_5_n, this.moreIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.tabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	Exit exit = new Exit();

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == 1
				&& event.getRepeatCount() == 0) {
			pressAgainExit();
			return false;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@SuppressLint("ShowToast")
	private void pressAgainExit() {
		if (exit.isExit()) {
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "再按一次退出程序", 1000).show();
			exit.doExitInOneSecond();
		}
	}
}
