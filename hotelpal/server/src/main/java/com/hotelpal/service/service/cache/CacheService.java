package com.hotelpal.service.service.cache;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放图片的base64等需占用较大空间的值
 */
public class CacheService {

	private static final Map<String, Object> CACHED_KEY_VALUE_MAP = new ConcurrentHashMap<>();

	public static void setValue(String key, Object value) {
		if (StringUtils.isNullEmpty(key) || value == null) {
			throw new ServiceException(ServiceException.COMMON_EMPTY_INPUT_PARAMETER);
		}
		CACHED_KEY_VALUE_MAP.put(key, value);
	}

	public static Object getValue(String key) {
		if (StringUtils.isNullEmpty(key)) {
			throw new ServiceException(ServiceException.COMMON_EMPTY_INPUT_PARAMETER);
		}
		return CACHED_KEY_VALUE_MAP.get(key);
	}
	public static void removeValue(String key) {
		if (key == null) return;
		CACHED_KEY_VALUE_MAP.remove(key);
	}
	public static final String KEY_LIVE_COURSE_INVITE_IMG = "KEY_LIVE_COURSE_INVITE_IMG";
	public static final String KEY_LIVE_COURSE_QR_CODE_IMG = "KEY_LIVE_COURSE_QR_CODE_IMG";
	
}
