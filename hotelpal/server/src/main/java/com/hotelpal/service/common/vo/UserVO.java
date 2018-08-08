package com.hotelpal.service.common.vo;

public class UserVO extends DomainBaseVO{
	private String openId;
	private String headImg;
	private String nick;
	private String company;
	private String title;
	private String regChannel;
	
	
	//////////////getUserInfo
	private String phone;
	private Integer freeCourseRemained;
	
	
	////getUserStatistics
	private Integer signedDays;
    private Integer purchasedCourseCount;
    private Integer listenedLessonCount;
    private Integer listenedTimeInSecond;
	
	
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRegChannel() {
		return regChannel;
	}
	public void setRegChannel(String regChannel) {
		this.regChannel = regChannel;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getFreeCourseRemained() {
		return freeCourseRemained;
	}
	public void setFreeCourseRemained(Integer freeCourseRemained) {
		this.freeCourseRemained = freeCourseRemained;
	}
	public Integer getSignedDays() {
		return signedDays;
	}
	public void setSignedDays(Integer signedDays) {
		this.signedDays = signedDays;
	}
	public Integer getPurchasedCourseCount() {
		return purchasedCourseCount;
	}
	public void setPurchasedCourseCount(Integer purchasedCourseCount) {
		this.purchasedCourseCount = purchasedCourseCount;
	}
	public Integer getListenedLessonCount() {
		return listenedLessonCount;
	}
	public void setListenedLessonCount(Integer listenedLessonCount) {
		this.listenedLessonCount = listenedLessonCount;
	}
	public Integer getListenedTimeInSecond() {
		return listenedTimeInSecond;
	}
	public void setListenedTimeInSecond(Integer listenedTimeInSecond) {
		this.listenedTimeInSecond = listenedTimeInSecond;
	}
}
