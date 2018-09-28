package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.mo.HttpParams;

abstract class HttpUtils {

	static void fillProtocol(HttpParams params) {
		if (params.getUrl().contains("hotelpal") && params.getUrl().startsWith("//")) {
			params.setUrl("http:" + params.getUrl());
		} else if (params.getUrl().startsWith("//")) {
			params.setUrl("http:" + params.getUrl());
		}
	}
}
