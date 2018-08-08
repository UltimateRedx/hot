package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.mo.ValuePair;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.UnsupportedEncodingException;

public class HttpPostUtils {
	
	public static String postMap(HttpParams params) {
		PostMethod post = new PostMethod(params.getUrl());
		if (ValidationUtils.isNotNullEmpty(params.getParams())) {
			post.setRequestBody(params.getParams().toArray(new NameValuePair[]{}));
		}
		if (!StringUtils.isNullEmpty(params.getRequestEntity())) {
			try {
				String charSet = StringUtils.isNullEmpty(params.getCharSet()) ? "UTF-8" : params.getCharSet();
				post.setRequestEntity(new StringRequestEntity(params.getRequestEntity(), "text/plan", charSet));
			} catch (UnsupportedEncodingException e) {
				throw new ServiceException(e);
			}
		}
		if (ValidationUtils.isNotNullEmpty(params.getExtraHeaders())) {
			for (ValuePair<String, String> p : params.getExtraHeaders()) {
				post.setRequestHeader(p.getName(), p.getValue());
			}
		}
		HttpClient client = new HttpClient();
		try {
			client.executeMethod(post);
			String resCharSet = post.getResponseCharSet() == null ? "utf-8" : post.getResponseCharSet();
			return new String(post.getResponseBodyAsString().getBytes(resCharSet), "UTF-8");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
