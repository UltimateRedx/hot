package com.hotelpal.service.common.po.security;

import com.hotelpal.service.common.po.BasePO;

public class ResourcePO extends BasePO {
	private String accessPoint;
	
	public String getAccessPoint() {
		return accessPoint;
	}
	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}
}
