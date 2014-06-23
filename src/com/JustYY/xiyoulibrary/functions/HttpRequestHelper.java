package com.JustYY.xiyoulibrary.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpRequestHelper {
	private String URL;
	private int method;
	public static final int GET = 0, POST = 1;
	private List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

	public HttpRequestHelper(String URL, int method) {
		this.URL = URL;
		this.method = method;
	}

	public String doRequest() {
		String result = null;
		switch (method) {
		case 0:
			result = doGet();
			break;
		case 1:
			result = doPost();
			break;
		}
		return result;
	}

	private String doGet() {
		String result = null;
		BufferedReader reader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(URL));
			request.setHeader("User-Agent", "XiyouLibrary_Android_Client");
			HttpResponse response = client.execute(request);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			result = buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return result;
	}

	private String doPost() {
		String result = null;
		BufferedReader reader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			request.setURI(new URI(URL));
			request.setHeader("User-Agent", "XiyouLibrary_Android_Client");
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters, "UTF-8");
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			result = buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return result;
	}

	public boolean addParameter(BasicNameValuePair param) {
		try {
			postParameters.add(param);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean addParameter(String key, String value) {
		return this.addParameter(new BasicNameValuePair(key, value));
	}

}
