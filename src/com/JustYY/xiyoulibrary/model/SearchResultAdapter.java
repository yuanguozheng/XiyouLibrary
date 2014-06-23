package com.JustYY.xiyoulibrary.model;

import java.util.List;
import java.util.Map;

import com.JustYY.xiyoulibrary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter {

	private class ViewHolder {
		TextView bookTitle;
	}

	private List<Map<String, String>> resultItems;

	private Context context;

	// private LayoutInflater inflater;

	public SearchResultAdapter(List<Map<String, String>> resultItems,
			Context context) {
		this.resultItems = resultItems;
		this.context = context;
	}

	@Override
	public int getCount() {
		return resultItems.size();
	}

	@Override
	public Object getItem(int position) {
		return resultItems.get(position).get("ID");
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.searchresult_item, null);
			holder = new ViewHolder();
			holder.bookTitle = (TextView) convertView
					.findViewById(R.id.searchresult_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.bookTitle.setText(resultItems.get(position).get("Name"));

		return convertView;
	}

	public void addItem(Map<String, String> it) {
		resultItems.add(it);
	}
	
}
