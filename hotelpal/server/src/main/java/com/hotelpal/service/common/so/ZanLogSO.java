package com.hotelpal.service.common.so;

public class ZanLogSO extends DomainBaseSO {
	private Integer lessonId;
	private Integer commentId;
	
	public Integer getLessonId() {
		return lessonId;
	}
	public void setLessonId(Integer lessonId) {
		this.lessonId = lessonId;
	}
	public Integer getCommentId() {
		return commentId;
	}
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
}
