package com.JustYY.xiyoulibrary.model;

import java.util.ArrayList;

import com.JustYY.xiyoulibrary.R;
import com.JustYY.xiyoulibrary.functions.HttpRequestHelper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BorrowResultAdapter extends BaseAdapter {

	ProgressDialog tips;

	class ViewHolder {
		TextView titleView;
		TextView dateView;
		TextView stateView;
		Button renewButton;
	}

	Context context;

	ArrayList<BorrowInfo> data;

	public BorrowResultAdapter(Context context, ArrayList<BorrowInfo> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return data.size();
	}

	@Override
	public BorrowInfo getItem(int position) {
		// TODO 自动生成的方法存根
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.borrowresult_item, null);
			holder = new ViewHolder();
			holder.titleView = (TextView) convertView
					.findViewById(R.id.borrow_book_title);
			holder.dateView = (TextView) convertView
					.findViewById(R.id.borrow_date);
			holder.stateView = (TextView) convertView
					.findViewById(R.id.borrow_state);
			holder.renewButton = (Button) convertView
					.findViewById(R.id.borrow_renew);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final BorrowInfo info = getItem(position);
		holder.titleView.setText(info.getName());
		holder.dateView.setText(info.getDate());
		holder.stateView.setText(info.getUserState());
		holder.stateView.setTextColor(info.getStateColor());
		if (!info.getIsRenew()) {
			holder.renewButton.setVisibility(View.INVISIBLE);
		} else {
			holder.renewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doRenew(info);
				}
			});
		}
		return convertView;
	}

	@SuppressLint("HandlerLeak")
	protected Boolean doRenew(BorrowInfo info) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Looper.prepare();
				if (msg.what == 1) {
					Toast.makeText(context, "续借成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "续借失败，请重试", Toast.LENGTH_SHORT)
							.show();
				}
				Looper.loop();
			}
		};

		
		
		tips = new ProgressDialog(context);
		tips.setCancelable(false);
		tips.setIndeterminate(true);
		tips.setMessage("正在请求数据，请稍后...");
		tips.setCanceledOnTouchOutside(false);
		tips.show();

		final HttpRequestHelper helper = new HttpRequestHelper(
				"http://222.24.63.109/xiyoulib/renew", HttpRequestHelper.POST);
		helper.addParameter("bacode", info.getBarcode());
		helper.addParameter("department_id", info.getDepartment_id());
		helper.addParameter("library_id", info.getLibrary_id());

		new Thread() {
			public void run() {
				String resString = null;
				try {
					resString = helper.doRequest();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					tips.dismiss();
				}
				Message msg = new Message();
				if (!resString.isEmpty()) {
					if (resString.equals("ok")) {
						msg.what = 1;
					} else {
						msg.what = 0;
					}
				} else {
					msg.what = 0;
				}
				handler.handleMessage(msg);
			}
		}.start();
		return false;
	}
}
