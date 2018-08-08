package com.hotelpal.service.common.dto.response;

import java.util.List;

public class CourseListResponse {

	private Boolean hasMore;
	private Integer total;
	private List<CourseResponse> courseList;

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
	public List<CourseResponse> getCourseList() {
		return courseList;
	}
	public void setCourseList(List<CourseResponse> courseList) {
		this.courseList = courseList;
	}
}
