package com.hotelpal.service.common.mo;

import org.apache.http.NameValuePair;

import java.util.List;

public class HttpParams {
	private String url;
	private List<NameValuePair> params;
	private List<ValuePair<String, String>> extraHeaders;
	private String requestEntity;
	private String charSet;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<NameValuePair> getParams() {
		return params;
	}
	public void setParams(List<NameValuePair> params) {
		this.params = params;
	}
	public List<ValuePair<String, String>> getExtraHeaders() {
		return extraHeaders;
	}
	public void setExtraHeaders(List<ValuePair<String, String>> extraHeaders) {
		this.extraHeaders = extraHeaders;
	}
	public String getRequestEntity() {
		return requestEntity;
	}
	public void setRequestEntity(String requestEntity) {
		this.requestEntity = requestEntity;
	}
	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
}
