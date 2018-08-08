package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.BasePO;

public class OnlineSumPO extends BasePO {
	private Integer liveCourseId;
	private Integer onlineSum;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public Integer getOnlineSum() {
		return onlineSum;
	}
	public void setOnlineSum(Integer onlineSum) {
		this.onlineSum = onlineSum;
	}
}
