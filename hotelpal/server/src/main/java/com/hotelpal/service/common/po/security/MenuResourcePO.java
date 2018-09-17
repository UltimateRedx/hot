package com.hotelpal.service.common.po.security;

import com.hotelpal.service.common.po.BasePO;

public class MenuResourcePO extends BasePO {
	private String menu;
	private String resources;
	
	public String getResources() {
		return resources;
	}
	public void setResources(String resources) {
		this.resources = resources;
	}
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
}
