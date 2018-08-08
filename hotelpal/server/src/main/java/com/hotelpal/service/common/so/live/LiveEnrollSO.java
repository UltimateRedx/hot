package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.DomainBaseSO;

import java.util.List;

public class LiveEnrollSO extends DomainBaseSO {
	private String status;
	private Integer liveCourseId;
	private String enrollType;
	private List<String> statusList;
	
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
	public List<String> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}
}
