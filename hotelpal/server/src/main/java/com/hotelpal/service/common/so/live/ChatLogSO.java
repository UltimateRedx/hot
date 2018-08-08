package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.DomainBaseSO;

public class ChatLogSO extends DomainBaseSO{
	private Integer courseId;
	private String blocked;
	
	public Integer getLiveCourseId() {
		return courseId;
	}
	public String getBlocked() {
		return blocked;
	}
	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
}
