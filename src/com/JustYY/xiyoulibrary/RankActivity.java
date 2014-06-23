package com.JustYY.xiyoulibrary;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RankActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.rank, menu);
		return true;
	}

}
