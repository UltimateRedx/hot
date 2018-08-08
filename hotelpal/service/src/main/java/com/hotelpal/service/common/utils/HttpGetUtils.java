package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.mo.ValuePair;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class HttpGetUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpGetUtils.class);
	public static String executeGet(HttpParams params) {
		GetMethod get = execute(params);
		try {
			String resCharSet = get.getResponseCharSet() == null ? "utf-8" : get.getResponseCharSet();
			return new String(get.getResponseBodyAsString().getBytes(resCharSet), "UTF-8");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public static byte[] executeGetBytes(HttpParams params) {
		GetMethod get = execute(params);
		try {
			byte[] res = get.getResponseBody();
			if (logger.isDebugEnabled()) {
				logger.debug("executeGetBytes: " + Hex.encodeHexString(res));
			}
			return res;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public static InputStream executeGetStream(HttpParams params) {
		GetMethod get = execute(params);
		try {
			return get.getResponseBodyAsStream();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	private static GetMethod execute(HttpParams params) {
		URI uri;
		try {
			uri = new URI(params.getUrl(), false, "UTF-8");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		GetMethod get = new GetMethod(uri.toString());
		if (ValidationUtils.isNotNullEmpty(params.getParams())) {
			get.setQueryString(params.getParams().toArray(new NameValuePair[]{}));
		}
		if (ValidationUtils.isNotNullEmpty(params.getExtraHeaders())) {
			for (ValuePair<String, String> p : params.getExtraHeaders()) {
				get.setRequestHeader(p.getName(), p.getValue());
			}
		}
		
		HttpClient client = new HttpClient();
		try {
			client.executeMethod(get);
			return get;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
