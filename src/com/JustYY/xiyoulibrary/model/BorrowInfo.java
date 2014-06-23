package com.JustYY.xiyoulibrary.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.graphics.Color;

@SuppressLint("SimpleDateFormat")
public class BorrowInfo {
	private String id, name, barcode, state, date, department_id, library_id;
	private Boolean isRenew;

	public BorrowInfo() {
		// TODO 自动生成的构造函数存根
	}

	public BorrowInfo(String id, String name, String barcode, String state,
			String date, String department_id, String library_id,
			Boolean isRenew) {
		setId(id);
		setName(name);
		setBarcode(barcode);
		setState(state);
		setDate(date);
		setDepartment_id(department_id);
		setLibrary_id(library_id);
		setIsRenew(isRenew);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(String department_id) {
		this.department_id = department_id;
	}

	public String getLibrary_id() {
		return library_id;
	}

	public void setLibrary_id(String library_id) {
		this.library_id = library_id;
	}

	public Boolean getIsRenew() {
		return isRenew;
	}

	public void setIsRenew(Boolean isRenew) {
		this.isRenew = isRenew;
	}

	public String getUserState() {
		Calendar today = Calendar.getInstance();
		Calendar backDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date returnDate = null;
		try {
			returnDate = sdf.parse(date);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		backDate.setTime(returnDate);
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		long span = backDate.getTimeInMillis() - today.getTimeInMillis();
		long day = span / (1000 * 60 * 60 * 24);
		if (day > 0)
		{
			if (day > 3) {
				return "未超期";
			} else {
				return "即将到期";
			}
		}
		else if (today.getTimeInMillis() == backDate.getTimeInMillis()) {
			return "今天到期";
		} else {
			return "已超期";
		}
	}

	public int getStateColor() {
		String state = getUserState();
		if (state.equals("未超期"))
			return Color.rgb(0, 0, 0);
		else if (state.equals("即将到期")) {
			return Color.rgb(255, 162, 0);
		} else {
			return Color.RED;
		}
	}
}
