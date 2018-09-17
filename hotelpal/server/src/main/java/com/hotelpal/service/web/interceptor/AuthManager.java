package com.hotelpal.service.web.interceptor;

import com.hotelpal.service.common.mo.AdminSessionMO;

import java.util.Objects;

class AuthManager {
	private AuthManager(){}
	
	static boolean resourceAccessable(AdminSessionMO mo, String url) {
		Objects.requireNonNull(mo);
		boolean fastCheck =  mo.getGrantedResources().contains(url);
		if (fastCheck) {
			return true;
		}
		for (String resource : mo.getGrantedResources()) {
			if (resource.contains(url)) {
				return true;
			}
		}
		return false;
	}
	
}
