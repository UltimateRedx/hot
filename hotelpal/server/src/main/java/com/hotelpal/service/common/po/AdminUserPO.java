package com.hotelpal.service.common.po;


public class AdminUserPO extends ExtendedBasePO {

	private String user;
	private String auth;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
}
