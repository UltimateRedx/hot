package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.DomainBaseSO;

public class OnlineLogSO extends DomainBaseSO {
	private Integer liveCourseId;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
}
