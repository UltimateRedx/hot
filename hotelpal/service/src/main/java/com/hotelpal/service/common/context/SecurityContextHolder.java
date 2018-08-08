package com.hotelpal.service.common.context;

import java.util.Date;

public class SecurityContextHolder {
	 private static InheritableThreadLocal<SecurityContext> contextHolder = new InheritableThreadLocal<>();
	
	public static ThreadLocal<SecurityContext> getContextHolder() {
		return contextHolder;
	}
	public static Integer getUserId() {
		SecurityContext securityContext = contextHolder.get();
		if (securityContext != null) {
			return securityContext.getUserId();
		}
		return null;
	}
	public static String getUserPhone() {
		SecurityContext securityContext = contextHolder.get();
		if (securityContext != null) {
			return securityContext.getPhone();
		}
		return null;
	}
	public static Integer getUserDomainId() {
		SecurityContext securityContext = contextHolder.get();
			return securityContext.getDomainId();
	}
//	public static String getUserRegChannel() {
//		SecurityContext securityContext = contextHolder.get();
//		if (securityContext != null) {
//			return securityContext.getRegChannel();
//		}
//		return RegChannel.NORMAL.toString();
//	}
	public static String getUserOpenId() {
		SecurityContext securityContext = contextHolder.get();
			return securityContext.getOpenId();
	}
	public static String getLiveVip() {
		SecurityContext securityContext = contextHolder.get();
		if (securityContext != null) {
			return securityContext.getLiveVip();
		}
		return null;
	}
	public static Date getLiveVipValidity() {
		SecurityContext securityContext = contextHolder.get();
		if (securityContext != null) {
			return securityContext.getLiveVipValidity();
		}
		return null;
	}
	public static boolean isSuperDomain() {
		SecurityContext securityContext = contextHolder.get();
		return securityContext != null && securityContext.getDomainId() <= 0;
	}
	public static void loginSuperDomain() {
		SecurityContext context = new SecurityContext();
		context.setDomainId(-1);
		setContext(context);
	}
	public static SecurityContext getContext() {
		return contextHolder.get();
	}
	public static void setContext(SecurityContext context) {
		contextHolder.set(context);
	}
	
	public static void setTargetDomain(Integer target) {
	SecurityContext securityContext = contextHolder.get();
		if (securityContext != null) {
			securityContext.setTargetDomain(target);
		}
	}
	public static Integer getTargetDomain() {
	SecurityContext securityContext = contextHolder.get();
		if (securityContext != null) {
			return securityContext.getTargetDomain();
		}
		return null;
	}
}
