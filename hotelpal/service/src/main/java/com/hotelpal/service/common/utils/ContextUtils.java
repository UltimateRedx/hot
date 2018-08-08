package com.hotelpal.service.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContextUtils {
	
	public static HttpServletRequest getServletRequest(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public static String getRemoteIP() {
		HttpServletRequest request = getServletRequest();
		return getRemoteIpFromRequest(request);
	}
	
	public static String getRemoteIpFromRequest(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null){
			String[] forwardedIp = ip.split(",");
			List<String> matchedIPString = Arrays.stream(forwardedIp).filter(ContextUtils::isIPv4).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(matchedIPString)) {
				ip = matchedIPString.get(0);
			}
		}
		if (StringUtils.isEmpty(ip)) {
			String realIP = request.getHeader("X-Real-IP");
			ip = isIPv4(realIP) ? realIP : null;
		}
		if (StringUtils.isEmpty(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	private static boolean isIPv4(String ip) {
		return ip != null && ip.matches("(\\d{1,3}\\.){3}\\d{1,3}");
	}
	
}
