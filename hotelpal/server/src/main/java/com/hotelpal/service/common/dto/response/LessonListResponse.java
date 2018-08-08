package com.hotelpal.service.common.dto.response;

import java.util.List;

public class LessonListResponse {
	private Boolean hasMore;
	private Integer total;
	private Integer unListenedCount;
	private List<LessonResponse> lessonResponseList;

	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Boolean getHasMore() {
		return hasMore;
	}
	public void setHasMore(Boolean hasMore) {
		this.hasMore = hasMore;
	}
	public List<LessonResponse> getLessonResponseList() {
		return lessonResponseList;
	}
	public void setLessonResponseList(List<LessonResponse> lessonResponseList) {
		this.lessonResponseList = lessonResponseList;
	}
	public Integer getUnListenedCount() {
		return unListenedCount;
	}
	public void setUnListenedCount(Integer unListenedCount) {
		this.unListenedCount = unListenedCount;
	}
}
