package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.BoolStatus;

public class CommentPO extends ExtendedBasePO {
	private Integer lessonId;
	private Integer replyToId;
	private Integer zanCount;
	private String content;
	private String elite = BoolStatus.N.toString();
	private String speaker = BoolStatus.N.toString();
	//Extra
	private String nick;
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
	public Integer getZanCount() {
		return zanCount;
	}
	public void setZanCount(Integer zanCount) {
		this.zanCount = zanCount;
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
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getSpeaker() {
		return speaker;
	}
	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
}
