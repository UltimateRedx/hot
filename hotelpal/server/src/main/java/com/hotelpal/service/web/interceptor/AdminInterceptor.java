package com.hotelpal.service.web.interceptor;

import com.hotelpal.service.common.context.SecurityContext;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.AdminSessionMO;
import com.hotelpal.service.service.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);
	private static final String WEBAPP = "/hotelpal";
	@Resource
	private ContextService contextService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("Request URL: {}?{}", request.getRequestURI(), request.getQueryString());
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new ServiceException(ServiceException.ADMIN_NO_SESSION_EXISTS);
		}
		AdminSessionMO mo = (AdminSessionMO) session.getAttribute("adminLoginInfo");
		if (mo == null) {
			throw new ServiceException(ServiceException.ADMIN_USER_NO_INFO);
		}
		contextService.updateSessionResource(mo);
		if (logger.isDebugEnabled()) {
			logger.debug("Admin access, user: {}, granted resources: {}", mo.getUser(), mo.getGrantedResources());
		}
		String uri = request.getRequestURI();
		String resource = uri.substring(uri.indexOf(WEBAPP) + WEBAPP.length());
		boolean accessable = AuthManager.resourceAccessable(mo, resource);
		if (!accessable) {
			throw new ServiceException(ServiceException.COMMON_ILLEGAL_ACCESS);
		}
		
		SecurityContext context = new SecurityContext();
		context.setDomainId(0);
		SecurityContextHolder.setContext(context);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		SecurityContextHolder.getContextHolder().remove();
	}
}
