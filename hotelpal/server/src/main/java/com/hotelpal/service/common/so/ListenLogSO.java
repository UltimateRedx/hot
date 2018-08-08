package com.hotelpal.service.common.so;

import java.util.List;

public class ListenLogSO extends DomainBaseSO {
	public ListenLogSO(){}
	public ListenLogSO(boolean infinity){
		super(infinity);
	}
	private Integer lessonId;
	private List<Integer> lessonIdList;
	
	public Integer getLessonId() {
		return lessonId;
	}
	public void setLessonId(Integer lessonId) {
		this.lessonId = lessonId;
	}
	public List<Integer> getLessonIdList() {
		return lessonIdList;
	}
	public void setLessonIdList(List<Integer> lessonIdList) {
		this.lessonIdList = lessonIdList;
	}
}
