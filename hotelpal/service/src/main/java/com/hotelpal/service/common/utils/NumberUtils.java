package com.hotelpal.service.common.utils;

public class NumberUtils {
	
	public static String getReadableSize(Integer size) {
		if (size == null) {
			return null;
		}
		int unit = 1024;
		if (size < unit) {
			return size + " B";
		}
		int exp = (int) (Math.log(size) / Math.log(unit));
		String pre = ("KMGTPE").charAt(exp - 1) + "";
		return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
	}
}
