package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.ExtendedBaseSO;

public class AssistantMessageSO extends ExtendedBaseSO {
	private Integer liveCourseId;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
}
