package com.hotelpal.service.web.interceptor;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.service.CommonService;
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
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuthInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	@Resource
	private CommonService commonService;
	
	private static final Date PV_UPDATE_TIME = new Date();
	private static final AtomicInteger PV = new AtomicInteger(0);
	private static final Lock LOCK = new ReentrantLock();
	
	private static final Set<String> USER_TOKEN_SET = new ConcurrentSkipListSet<>();
	private static final Date UV_UPDATE_TIME = new Date();
	
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
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String queryString = request.getQueryString();
//		logger.info("Request URL: " + request.getRequestURI() + "?" + request.getQueryString());
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
		final String OPEN_ID = openId;
		try {
			commonService.putAuth(openId);
		} catch (Exception e) {
			logger.error("Init context failed.", e);
			return false;
		}
		map.put(request.getSession().getId(), new Date().getTime());
		//统计站点PV
		String queryUrl = request.getRequestURI();
		CompletableFuture.runAsync(() -> {
			for (String path : INCLUDE_URL_SET) {
				if (queryUrl.contains(path)) {
					if (new Date().getTime() >= PV_UPDATE_TIME.getTime() + 10 * 60 * 1000) {
						LOCK.lock();
						int pv = 0;
						try {
							pv = PV.incrementAndGet();
							PV.set(0);
							PV_UPDATE_TIME.setTime(new Date().getTime());
						}finally {
							LOCK.unlock();
						}
						commonService.logSitePV(pv);
					} else {
						PV.incrementAndGet();
					}
					break;
				}
			}
		});
		
		//站点UV
		CompletableFuture.runAsync(() -> {
			USER_TOKEN_SET.add(OPEN_ID);
			Calendar cal = Calendar.getInstance();
			if (cal.getTime().getTime() >= UV_UPDATE_TIME.getTime() + 10 * 60 * 1000) {
				LOCK.lock();
				int size = 0;
				try {
					size = USER_TOKEN_SET.size();
					Calendar updateTime = Calendar.getInstance();
					updateTime.setTime(UV_UPDATE_TIME);
					if (cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE)) {
						USER_TOKEN_SET.clear();
					}
					UV_UPDATE_TIME.setTime(new Date().getTime());
				}finally {
					LOCK.unlock();
				}
				commonService.logSiteUV(size);
			}
		});
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		SecurityContextHolder.getContextHolder().remove();
		logger.info("Request URL: " + request.getRequestURI() + "?" + request.getQueryString() + ";Chain handle time: " + (new Date().getTime() - map.get(request.getSession().getId())));
	}
}
