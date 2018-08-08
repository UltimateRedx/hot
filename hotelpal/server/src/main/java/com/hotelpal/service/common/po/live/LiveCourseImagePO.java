package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.BasePO;

public class LiveCourseImagePO extends BasePO {
	private Integer liveCourseId;
	private Integer imgOrder;
	private String img;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public Integer getImgOrder() {
		return imgOrder;
	}
	public void setImgOrder(Integer imgOrder) {
		this.imgOrder = imgOrder;
	}
}
