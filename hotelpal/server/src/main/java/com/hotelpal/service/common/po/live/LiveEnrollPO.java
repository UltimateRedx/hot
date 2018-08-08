package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.DomainBasePO;

public class LiveEnrollPO extends DomainBasePO {
	private String status;
	private String enrollType;
	private Integer liveCourseId;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEnrollType() {
		return enrollType;
	}
	public void setEnrollType(String enrollType) {
		this.enrollType = enrollType;
	}
}
