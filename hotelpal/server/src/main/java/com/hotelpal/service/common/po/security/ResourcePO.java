package com.hotelpal.service.common.po.security;

import com.hotelpal.service.common.po.BasePO;

public class ResourcePO extends BasePO {
	private String accessPoint;
	private String menu;
	
	public String getAccessPoint() {
		return accessPoint;
	}
	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
}
