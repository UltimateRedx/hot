package com.hotelpal.service.common.so;

public class CommentSO extends ExtendedBaseSO {
	
	public CommentSO(){}
	public CommentSO(boolean infinity) {
		if (infinity) {
			this.setPageSize(null);
			this.setLimit(null);
		}
	}
	private Integer lessonId;
	private Integer replyToId;
	private String content;
	private String elite;
	private Integer zanCountGreaterThan;
	public Integer getLessonId() {
		return lessonId;
	}
	public void setLessonId(Integer lessonId) {
		this.lessonId = lessonId;
	}
	public Integer getReplyToId() {
		return replyToId;
	}
	public void setReplyToId(Integer replyToId) {
		this.replyToId = replyToId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getElite() {
		return elite;
	}
	public void setElite(String elite) {
		this.elite = elite;
	}
	public Integer getZanCountGreaterThan() {
		return zanCountGreaterThan;
	}
	public void setZanCountGreaterThan(Integer zanCountGreaterThan) {
		this.zanCountGreaterThan = zanCountGreaterThan;
	}
}
