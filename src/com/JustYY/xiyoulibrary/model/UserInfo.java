package com.JustYY.xiyoulibrary.model;

public class UserInfo {
	private String id;
	private String cls;
	private String name;

	public UserInfo() {
		// TODO 自动生成的构造函数存根
	}
	
	public UserInfo(String id, String cls, String name) {
		this.id = id;
		this.cls = cls;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
