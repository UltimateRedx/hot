package com.hotelpal.service.common.utils;


public class ArrayUtils {
	
	public static boolean isNotNullEmpty(Iterable arr) {
		return !isNullEmpty(arr);
	}
	
	public static boolean isNullEmpty(Iterable arr) {
		if (arr == null) return true;
		return !arr.iterator().hasNext();
	}
}
