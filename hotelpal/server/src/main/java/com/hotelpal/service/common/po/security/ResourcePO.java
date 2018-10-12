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

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ResourcePO && this.getId().equals(((ResourcePO) obj).getId());
	}
}
