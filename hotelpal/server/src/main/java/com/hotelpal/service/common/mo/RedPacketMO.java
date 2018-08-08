package com.hotelpal.service.common.mo;

public class RedPacketMO {
	private String userHeadImg;
	private String userName;

	private String speakerHeadImg;
	private String LessonTitle;
	private String speakerTitle;
	private String speakerCompany;
	private String speakerName;

	private Integer redPacketRemained;

	private Boolean alreadyOpened;
	private String content;
	
	public String getUserHeadImg() {
		return userHeadImg;
	}
	public void setUserHeadImg(String userHeadImg) {
		this.userHeadImg = userHeadImg;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSpeakerHeadImg() {
		return speakerHeadImg;
	}
	public void setSpeakerHeadImg(String speakerHeadImg) {
		this.speakerHeadImg = speakerHeadImg;
	}
	public String getLessonTitle() {
		return LessonTitle;
	}
	public void setLessonTitle(String lessonTitle) {
		LessonTitle = lessonTitle;
	}
	public String getSpeakerTitle() {
		return speakerTitle;
	}
	public void setSpeakerTitle(String speakerTitle) {
		this.speakerTitle = speakerTitle;
	}
	public String getSpeakerCompany() {
		return speakerCompany;
	}
	public void setSpeakerCompany(String speakerCompany) {
		this.speakerCompany = speakerCompany;
	}
	public String getSpeakerName() {
		return speakerName;
	}
	public void setSpeakerName(String speakerName) {
		this.speakerName = speakerName;
	}
	public Integer getRedPacketRemained() {
		return redPacketRemained;
	}
	public void setRedPacketRemained(Integer redPacketRemained) {
		this.redPacketRemained = redPacketRemained;
	}
	public Boolean getAlreadyOpened() {
		return alreadyOpened;
	}
	public void setAlreadyOpened(Boolean alreadyOpened) {
		this.alreadyOpened = alreadyOpened;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
