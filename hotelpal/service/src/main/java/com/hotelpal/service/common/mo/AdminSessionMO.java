package com.hotelpal.service.common.mo;

import java.util.Date;

public class AdminSessionMO {

	private String user;
	private Date loginTime;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
}
