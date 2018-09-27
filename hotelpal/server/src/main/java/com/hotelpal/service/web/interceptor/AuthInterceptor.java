package com.hotelpal.service.web.interceptor;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.service.CommonService;
import com.hotelpal.service.service.StatisticsService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AuthInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	@Resource
	private CommonService commonService;
	@Resource
	private StatisticsService statisticsService;
	
	private static final Set<String> INCLUDE_URL_SET = new HashSet<>(Arrays.asList(
			"/course/getCourseList",
			"/course/getCourse",
			"/course/getMainBanner",
			"/lesson/getLesson",
			"/lesson/getInternalLessonList",
			"/user/getPaidCourseList"
));
	
	
	private static Map<String, Long> map = new ConcurrentHashMap<>();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String queryString = request.getQueryString();
		if (StringUtils.isNullEmpty(queryString)) {
			logger.error("Empty query string. No token found.");
			return false;
		}
		List<NameValuePair> pairList = URLEncodedUtils.parse(queryString, Charset.forName("UTF-8"));
		String openId = null;
		for (NameValuePair pair : pairList) {
			if (pair.getName().equalsIgnoreCase("token")) {
				openId = pair.getValue();
			}
		}
		if (StringUtils.isNullEmpty(openId)) {
			logger.error("Query string exists, but no token found.");
			return false;
		}
		try {
			commonService.putAuth(openId);
		} catch (Exception e) {
			logger.error("Init context failed.", e);
			return false;
		}
		map.put(request.getSession().getId(), new Date().getTime());
		//统计站点
		String queryUrl = request.getRequestURI();
		CompletableFuture.runAsync(() -> {
			for (String path : INCLUDE_URL_SET) {
				if (queryUrl.contains(path)) {
					statisticsService.increase(StatisticsService.TYPE_SITE, null, SecurityContextHolder.getUserDomainId());
					break;
				}
			}
		});
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		SecurityContextHolder.getContextHolder().remove();
		for (String path : excludeLoggingPath) {
			if (request.getRequestURI().contains(path)) return;
		}
		logger.info("Request URL: {}?{};\tChain handle time: {}", request.getRequestURI(), request.getQueryString(), (new Date().getTime() - map.get(request.getSession().getId())));
	}

	private static final Set<String> excludeLoggingPath = new HashSet<>(Collections.singletonList("/user/recordListenPos"));
}
