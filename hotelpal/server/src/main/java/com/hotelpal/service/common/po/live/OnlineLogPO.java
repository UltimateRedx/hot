package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.DomainBasePO;

import java.util.Date;

public class OnlineLogPO extends DomainBasePO {
	private Integer liveCourseId;
	private Date offlineTime;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public Date getOfflineTime() {
		return offlineTime;
	}
	public void setOfflineTime(Date offlineTime) {
		this.offlineTime = offlineTime;
	}
}
