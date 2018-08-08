package com.hotelpal.service.common.po;

public class ListenLogPO extends DomainBasePO{
	private Integer lessonId;
	private Integer recordLen;
	private Integer recordPos;
	private Integer maxPos;
	
	public Integer getLessonId() {
		return lessonId;
	}
	public void setLessonId(Integer lessonId) {
		this.lessonId = lessonId;
	}
	public Integer getRecordLen() {
		return recordLen;
	}
	public void setRecordLen(Integer recordLen) {
		this.recordLen = recordLen;
	}
	public Integer getRecordPos() {
		return recordPos;
	}
	public void setRecordPos(Integer recordPos) {
		this.recordPos = recordPos;
	}
	public Integer getMaxPos() {
		return maxPos;
	}
	public void setMaxPos(Integer maxPos) {
		this.maxPos = maxPos;
	}
}
