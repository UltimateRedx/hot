package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.ExtendedBasePO;

public class AssistantMessagePO extends ExtendedBasePO {
	private Integer liveCourseId;
	private String msg;
	
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
}
