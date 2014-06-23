package com.JustYY.xiyoulibrary;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.JustYY.xiyoulibrary.model.BorrowInfo;
import com.JustYY.xiyoulibrary.model.BorrowResultAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class BorrowInfoActivity extends Activity {

	ArrayList<BorrowInfo> borrowInfos = new ArrayList<BorrowInfo>();

	ListView resultView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_info);
		procBorrowInfoString();
		resultView = (ListView) findViewById(R.id.borrowBooks);
		doBinding();
		resultView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(BorrowInfoActivity.this,
						BookDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.borrow_info, menu);
		return true;
	}

	protected void procBorrowInfoString() {
		if (UserInfoActivity.borrowInfoString.equals("[]")) {
			Toast.makeText(this, "暂时没有借阅信息", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			JSONArray borrowInfo = new JSONArray(
					UserInfoActivity.borrowInfoString);
			for (int i = 0; i < borrowInfo.length()-1; i++) {
				JSONObject item = borrowInfo.getJSONObject(i);
				String id = item.getString("id");
				String name = item.getString("name");
				String barcode = item.getString("barcode");
				String state = item.getString("state");
				String date = item.getString("date");
				String department_id=null;
				String library_id=null;
				if (item.has("department_id") && item.has("library_id")) {
					department_id = item.getString("department_id");
					library_id = item.getString("library_id");
				}
				Boolean isRenew = item.getBoolean("isRenew");
				BorrowInfo info = new BorrowInfo(id, name, barcode, state,
						date, department_id, library_id, isRenew);
				borrowInfos.add(info);
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	protected void doBinding() {
		BorrowResultAdapter adapter = new BorrowResultAdapter(this, borrowInfos);
		resultView.setAdapter(adapter);
	}
}
