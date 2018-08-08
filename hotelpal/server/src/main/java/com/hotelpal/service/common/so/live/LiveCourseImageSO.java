package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.BaseSO;

public class LiveCourseImageSO extends BaseSO {
	private Integer liveCourseId;
	private Integer imgOrder;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public Integer getImgOrder() {
		return imgOrder;
	}
	public void setImgOrder(Integer imgOrder) {
		this.imgOrder = imgOrder;
	}
}
