package com.JustYY.xiyoulibrary;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.app.TabActivity;
import android.content.Intent;

@SuppressWarnings("deprecation")
public class UserInfoActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost tabHost;
	private Intent borrowInfoIntent;
	private Intent favoriteIntent;

	static String borrowInfoString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		borrowInfoString = (String) intent
				.getSerializableExtra("BorrowInfoString");
		//savedInstanceState.getString("BorrowInfoString");
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_with_back);
		TextView titleView = (TextView) findViewById(R.id.titlebarText);
		titleView.setText("欢迎，" + LoginActivity.userInfo.getName() + "同学！");

		this.borrowInfoIntent = new Intent(this, BorrowInfoActivity.class);
		this.favoriteIntent = new Intent(this, FavActivity.class);

		Button back = (Button) findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
			}
		});

		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);

		setupIntent();

		this.tabHost.setCurrentTabByTag("BORROW_INFO");

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.tabHost.setCurrentTabByTag("BORROW_INFO");
				// textView.setText("图书速查");
				break;
			case R.id.radio_button1:
				this.tabHost.setCurrentTabByTag("FAVORITE");
				// textView.setText("新书推荐");
				break;
			}
		}
	}

	private void setupIntent() {
		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;

		localTabHost.addTab(buildTabSpec("BORROW_INFO",
				R.string.title_activity_borrow_info, R.drawable.user_borrow,
				this.borrowInfoIntent));

		localTabHost.addTab(buildTabSpec("FAVORITE",
				R.string.title_activity_fav, R.drawable.user_fav,
				this.favoriteIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.tabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}
}
