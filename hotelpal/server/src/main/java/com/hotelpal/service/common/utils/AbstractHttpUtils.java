package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.mo.HttpParams;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

abstract class AbstractHttpUtils {
	/**
	private static final PoolingHttpClientConnectionManager POOL = new PoolingHttpClientConnectionManager(2L, TimeUnit.MINUTES);
	static {
		POOL.setDefaultMaxPerRoute(100);
		POOL.setMaxTotal(400);
		POOL.setValidateAfterInactivity(60 * 1000);
	}

	protected static final CloseableHttpClient CLIENT = HttpClients.custom().setConnectionManager(POOL).build();
	 */

	static void fillProtocol(HttpParams params) {
		if ((params.getUrl().contains("hotelpal") && params.getUrl().startsWith("//")) || params.getUrl().startsWith("//")) {
			params.setUrl("http:" + params.getUrl());
		}
	}
}
