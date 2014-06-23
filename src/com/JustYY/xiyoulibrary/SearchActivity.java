package com.JustYY.xiyoulibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.JustYY.xiyoulibrary.functions.HttpRequestHelper;
import com.JustYY.xiyoulibrary.functions.NetworkState;
import com.JustYY.xiyoulibrary.model.SearchResultAdapter;
import com.zxing.activity.CaptureActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;

public class SearchActivity extends Activity implements OnQueryTextListener,
		OnScrollListener {

	ListView searchResultView;
	SearchView searchKeyword;

	List<Map<String, String>> resultItems = new ArrayList<Map<String, String>>();

	private int visibleLastIndex = 0; // 最后的可视项索引
	@SuppressWarnings("unused")
	private int visibleItemCount; // 当前窗口可见项总数

	private View loadMoreView;
	// private TextView loadMoreButton;

	private String lastSearched = "";

	private int lastPage = 1;

	SearchResultAdapter ad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);
		searchKeyword = (SearchView) findViewById(R.id.searchKeyword);
		searchKeyword.setSubmitButtonEnabled(true);
		searchKeyword.setOnQueryTextListener(this);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.search, menu);
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "扫描条形码")
				.setIcon(R.drawable.barcode)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent openCameraIntent = new Intent(
								SearchActivity.this, CaptureActivity.class);
						startActivityForResult(openCameraIntent, 0);
						return false;
					}
				});
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public boolean onQueryTextSubmit(String query) {
		if (!NetworkState.isNetworkAvailable(this)) {
			Toast.makeText(this, "你没有联网哦", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (query.isEmpty()) {
			Toast.makeText(this, "关键词不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (query.length() < 2) {
			Toast.makeText(this, "关键词过短", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (lastSearched.equals(query)) {
			searchKeyword.clearFocus();
			return false;
		}
		lastSearched = query;
		lastPage = 1;
		searchKeyword.clearFocus();
		final HttpRequestHelper searchrequest = new HttpRequestHelper(
				"http://222.24.63.109/lib/default.aspx", HttpRequestHelper.POST);
		searchrequest.addParameter(new BasicNameValuePair("word", query));
		searchrequest.addParameter("type", getSearchType());
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					setListView((JSONObject) msg.obj);
					try {
						Toast.makeText(
								SearchActivity.this,
								"共 "
										+ (((JSONObject) msg.obj)
												.getInt("Amount")) + " 条记录",
								Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (msg.what == 0) {
					Toast.makeText(getApplicationContext(), "没有检索到相关图书",
							Toast.LENGTH_SHORT).show();
					if (resultItems.size() != 0) {
						resultItems.clear();
						ad.notifyDataSetChanged();
					}
					return;
				}
			}
		};

		final ProgressDialog tips = new ProgressDialog(this);
		tips.setCancelable(false);
		tips.setIndeterminate(true);
		tips.setMessage("正在请求数据，请稍后...");
		// tips.setTitle("数据请求中");
		tips.setCanceledOnTouchOutside(false);
		tips.show();
		// ProgressDialog.show(this, "数据请求中", "正在请求数据，请稍后...", true, false);
		new Thread() {
			public void run() {
				String result = searchrequest.doRequest();

				try {
					if (result.equals("null")) {
						Message msg = new Message();
						msg.what = 0;
						msg.obj = null;
						handler.sendMessage(msg);
						tips.dismiss();
						return;
					}
					JSONObject jsonObject = new JSONObject(result);
					Message msg = new Message();
					msg.what = 1;
					msg.obj = jsonObject;
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					tips.dismiss();
				}
			}
		}.start();

		return false;
	}

	protected void setListView(JSONObject result) {
		searchResultView = (ListView) findViewById(R.id.searchResultView);
		resultItems = new ArrayList<Map<String, String>>();
		JSONArray bookData;
		try {
			bookData = result.getJSONArray("BookData");

			for (int i = 0; i < bookData.length(); i++) {
				JSONObject jsonItem = bookData.getJSONObject(i);
				Map<String, String> temp = new HashMap<String, String>();
				temp.put("ID", jsonItem.getString("ID"));
				temp.put("Name", jsonItem.getString("Name"));
				resultItems.add(temp);
			}

			ad = new SearchResultAdapter(resultItems, SearchActivity.this);

			if (loadMoreView == null)
				loadMoreView = getLayoutInflater().inflate(R.layout.loadmore,
						null);

			try {
				if (resultItems.size() == result.getInt("Amount")) {
					if (searchResultView.getFooterViewsCount() == 1)
						searchResultView.removeFooterView(loadMoreView);
				} else {
					if (searchResultView.getFooterViewsCount() == 0)
						searchResultView.addFooterView(loadMoreView);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			/*
			 * if (searchResultView.getCount() > resultItems.size())
			 * searchResultView.removeFooterView(loadMoreView); if
			 * (searchResultView.getCount() < result.getInt("Amount")) {
			 * loadMoreView = getLayoutInflater().inflate(R.layout.loadmore,
			 * null); loadMoreButton = (TextView)
			 * findViewById(R.id.loadmoreButton);
			 * searchResultView.addFooterView(loadMoreView); }
			 */
			searchResultView.setAdapter(ad);

			searchResultView.setOnScrollListener(this);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		searchResultView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				AlertDialog.Builder s = new AlertDialog.Builder(
						SearchActivity.this).setTitle("提示").setMessage(
						resultItems.get(arg2).get("ID"));
				s.create().show();
			}
		});
	}

	protected String getSearchType() {
		Spinner searchType = (Spinner) findViewById(R.id.options);
		int index = searchType.getSelectedItemPosition();
		switch (index) {
		case 0:
			return "title";
		case 1:
			return "author";
		case 2:
			return "number";
		case 3:
			return "subject_term";
		case 4:
			return "call_no";
		default:
			return "title";
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (!NetworkState.isNetworkAvailable(this)) {
			Toast.makeText(this, "你没有联网哦", Toast.LENGTH_SHORT).show();
			return;
		}
		int itemsLastIndex = ad.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1; // 加上底部的loadMoreView项
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == lastIndex) {

			final HttpRequestHelper searchrequest = new HttpRequestHelper(
					"http://222.24.63.109/lib/default.aspx",
					HttpRequestHelper.POST);
			searchrequest.addParameter(new BasicNameValuePair("word",
					lastSearched));
			searchrequest.addParameter("type", getSearchType());
			lastPage++;
			searchrequest.addParameter("page", String.valueOf(lastPage));

			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						doAdd((JSONObject) msg.obj);
					}
				}
			};

			// loadMoreButton.setText("加载中...");
			final ProgressDialog tips = new ProgressDialog(this);
			tips.setCancelable(false);
			tips.setIndeterminate(true);
			tips.setMessage("正在请求数据，请稍后...");
			// tips.setTitle("数据请求中");
			tips.setCanceledOnTouchOutside(false);
			tips.show();
			// ProgressDialog.show(this, "数据请求中", "正在请求数据，请稍后...", true, false);
			new Thread() {
				public void run() {

					String result = searchrequest.doRequest();
					try {
						JSONObject jsonObject = new JSONObject(result);
						Message msg = new Message();
						msg.what = 1;
						msg.obj = jsonObject;
						handler.sendMessage(msg);
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						tips.dismiss();
					}
				}
			}.start();

			// Toast.makeText(this, "SP", Toast.LENGTH_LONG).show();
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			// Log.i("LOADMORE", "loading...");
		}
	}

	protected void doAdd(JSONObject obj) {
		JSONArray data = null;
		try {
			data = obj.getJSONArray("BookData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (data == null)
			return;
		for (int i = 0; i < data.length(); i++) {
			Map<String, String> map = new HashMap<String, String>();

			try {
				map.put("ID", data.getJSONObject(i).getString("ID"));
				map.put("Name", data.getJSONObject(i).getString("Name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			resultItems.add(map);
		}

		/*
		 * ad = new SearchResultAdapter(resultItems, SearchActivity.this);
		 * searchResultView.setAdapter(ad);
		 */
		ad.notifyDataSetChanged();

		if (loadMoreView == null)
			loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);

		try {
			if (resultItems.size() == obj.getInt("Amount")) {
				if (searchResultView.getFooterViewsCount() == 1)
					searchResultView.removeFooterView(loadMoreView);
			} else {
				if (searchResultView.getFooterViewsCount() == 0)
					searchResultView.addFooterView(loadMoreView);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		/*
		 * try { if (searchResultView.getCount() < obj.getInt("Amount")) {
		 * loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
		 * loadMoreButton = (TextView) findViewById(R.id.loadmoreButton);
		 * 
		 * } } catch (JSONException e) { // TODO 自动生成的 catch 块
		 * e.printStackTrace(); }
		 */
		// loadMoreButton.setText("加载更多...");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 处理扫描结果（在界面上显示）
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			SearchView searchKeyword = (SearchView) findViewById(R.id.searchKeyword);
			Spinner searchType = (Spinner) findViewById(R.id.options);
			searchType.setSelection(2);
			searchKeyword.setQuery(scanResult, true);
		}
	}
}
