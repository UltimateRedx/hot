package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.exception.ServiceException;

import java.util.Collection;
import java.util.Map;

public class ValidationUtils {

	public static boolean isNotNullEmpty(Object obj) {
		if (obj == null) return false;
		if (obj instanceof String) {
			String str = (String)obj;
			if (str.length() == 0 || str.trim().length() == 0) return false;
		} else if (obj instanceof Map) {
			return !((Map) obj).isEmpty();
		} else if (obj instanceof Collection){
			return ((Collection) obj).size() > 0;
		}
		return true;
	}
	public static boolean isNullEmpty(Object obj) {
		return !isNotNullEmpty(obj);
	}
	
	public static void checkPhoneFormat(String phone) {
		if (!phone.matches("^1\\d{10}$")) {
			throw new ServiceException(ServiceException.USER_PHONE_INVALID);
		}
	}
}
