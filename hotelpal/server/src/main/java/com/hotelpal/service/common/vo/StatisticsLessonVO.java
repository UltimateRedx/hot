package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.LessonPO;

public class StatisticsLessonVO extends LessonPO {
	private Long pv;
	private Long uv;
	
	public Long getPv() {
		return pv;
	}
	public void setPv(Long pv) {
		this.pv = pv;
	}
	public Long getUv() {
		return uv;
	}
	public void setUv(Long uv) {
		this.uv = uv;
	}
}
