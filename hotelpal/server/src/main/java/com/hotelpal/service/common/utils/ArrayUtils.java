package com.hotelpal.service.common.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {
	private ArrayUtils(){}

	public static boolean isNotNullEmpty(Iterable arr) {
		return !isNullEmpty(arr);
	}
	
	public static boolean isNullEmpty(Iterable arr) {
		if (arr == null) return true;
		return !arr.iterator().hasNext();
	}

	public static List<Object> addAll(Object... objs) {
		if (objs.length == 0) {
			return new ArrayList<>(0);
		}
		List<Object> res = new ArrayList<>();
		for (Object obj : objs) {
			if (obj instanceof Object[] && ((Object[]) obj).length > 0) {
				res.addAll(Arrays.asList((Object[]) obj));
			} else if (!(obj instanceof Object[] && ((Object[]) obj).length == 0)) {
				res.add(obj);
			}
		}
		return res;
	}
}
