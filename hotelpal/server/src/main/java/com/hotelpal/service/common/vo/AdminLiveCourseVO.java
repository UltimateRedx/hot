package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.live.LiveCoursePO;

public class AdminLiveCourseVO extends LiveCoursePO {
	private Integer enrollBaseLine;
	private Integer ongoingBaseLine;
	private Integer totalBaseLine;


	public Integer getEnrollBaseLine() {
		return enrollBaseLine;
	}
	public void setEnrollBaseLine(Integer enrollBaseLine) {
		this.enrollBaseLine = enrollBaseLine;
	}
	public Integer getOngoingBaseLine() {
		return ongoingBaseLine;
	}
	public void setOngoingBaseLine(Integer ongoingBaseLine) {
		this.ongoingBaseLine = ongoingBaseLine;
	}
	public Integer getTotalBaseLine() {
		return totalBaseLine;
	}
	public void setTotalBaseLine(Integer totalBaseLine) {
		this.totalBaseLine = totalBaseLine;
	}
}
