package com.hotelpal.service.service.parterner;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.utils.HttpPostUtils;
import com.hotelpal.service.common.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SubMailService {
	private SubMailService(){}

	private static final String APP_ID = "14794";
	private static final String APP_KEY = "7570d191a8d8a25cb7b196924419c689";
	
	private static final String CAPTCHA_PROJECT = "5aU0d1";
	private static final String PURCHASE_NOTIFY_PROJECT = "U0kZu2";
	private static final Integer TIMEOUT = 10;
	
	private static final String API_URL = "https://api.mysubmail.com/message/xsend.json";
	
	
	public static void sendCaptcha(String phone, String code) {
		HttpParams params = new HttpParams();
		params.setUrl(API_URL);
		params.setExtraHeaders(Collections.singletonList(new ValuePair<>("Content-Type", "application/x-www-form-urlencoded")));
		//The param for this sub-mail project.
		Map<String, String> map = new HashMap<>();
		map.put("code", code);
		map.put("time", String.valueOf(TIMEOUT) + "分钟");
		
		//API request map.
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appid", APP_ID);
		paramMap.put("to", phone);
		paramMap.put("project", CAPTCHA_PROJECT);
		paramMap.put("vars", JSON.toJSONString(map));
		paramMap.put("signature", APP_KEY);
		params.setRequestEntity(StringUtils.formUrl(paramMap));
		String res = HttpPostUtils.postMap(params);
		Map<String, Object> resMap = JSON.parseObject(res, HashMap.class);
		if (String.valueOf(resMap.get("status")).equalsIgnoreCase("error")) {
			throw new ServiceException(ServiceException.SUB_MAIL_SEND_FAILED);
		}
		//return void if succeed.
	}

	public static void notifyPurchase(String phone, String title) {
		HttpParams params = new HttpParams();
		params.setUrl(API_URL);
		params.setExtraHeaders(Collections.singletonList(new ValuePair<>("Content-Type", "application/x-www-form-urlencoded")));
		//The param for this sub-mail project.
		Map<String, String> map = new HashMap<>();
		map.put("title", title);

		//API request map.
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appid", APP_ID);
		paramMap.put("to", phone);
		paramMap.put("project", PURCHASE_NOTIFY_PROJECT);
		paramMap.put("vars", JSON.toJSONString(map));
		paramMap.put("signature", APP_KEY);
		params.setRequestEntity(StringUtils.formUrl(paramMap));
		String res = HttpPostUtils.postMap(params);
		Map<String, Object> resMap = JSON.parseObject(res, HashMap.class);
		if (String.valueOf(resMap.get("status")).equalsIgnoreCase("error")) {
			throw new ServiceException(ServiceException.SUB_MAIL_SEND_FAILED);
		}
	}
}
