package com.hotelpal.service.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StringUtils {
	public static boolean isNullEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
	public static void addSQLCondition(StringBuilder buff, List<Object> params, String fieldName, Object value, String alias) {
		if (ValidationUtils.isNullEmpty(value)) return;
		String qName = StringUtils.isNullEmpty(alias) ? String.join("", "`", fieldName, "`") : String.join("", "`", alias, "`.`", fieldName, "`");
		if (value instanceof String
				|| value instanceof Number) {
			buff.append(" AND ").append(qName).append(" = ? ");
			params.add(value);
		} else if (value instanceof Collection) {
			buff.append(" AND `").append(qName).append("` IN (");
			StringBuilder sb = new StringBuilder();
			Collection c = (Collection) value;
			for (Object o : c) {
				sb.append(",?");
				params.add(o);
			}
			buff.append(sb.replace(0, 1, "")).append(") ");
		}
	}
	public static String formUrl(final Map<String, Object> map) {
		if (map == null || map.size() == 0) {
			return "";
		}
		StringBuffer buff = new StringBuffer();
		map.forEach((key, value) -> buff.append("&").append(key).append("=").append(value));
		return buff.toString().replaceFirst("&", "");
	}

	public static String removeHtml(String str) {
		str = str.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?/[\\s]*?script[\\s]*?>", "")
				.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?/[\\s]*?style[\\s]*?>", "")
				.replaceAll("<[^>]+>", "");

		return str;
	}

	public static String subString(String str, Integer maxLength) {
		if (isNullEmpty(str)) return "";
		int length = str.length();
		return length > maxLength ? str.substring(0, maxLength) + "..." : str;
	}

	public static String format(String str, Object... obj) {
		StringBuilder buff = new StringBuilder(str);
		int index = 0;
		for (Object o : obj) {
			index = buff.indexOf("{}", index);
			buff.replace(index, index + 2, String.valueOf(o));
		}
		return buff.toString();
	}
}
