package com.hotelpal.service.common.po.extra;

import java.util.Date;

public class PurchasedCoursePO {
	private Integer id;
	private String speakerHeadImg;
	private String speakerNick;
	private String speakerTitle;
	private String speakerCompany;
	private String title;
	private Date updateDate;
	private Date nextUpdateDate;
	private Integer publishedLessonCount;
	private Integer LessonNum;
	private String tradeNo;
	private Date purchaseDate;
	private Integer payment;
	private Integer originalPrice;
	private String status;
	private String bannerImg;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSpeakerHeadImg() {
		return speakerHeadImg;
	}
	public void setSpeakerHeadImg(String speakerHeadImg) {
		this.speakerHeadImg = speakerHeadImg;
	}
	public String getSpeakerNick() {
		return speakerNick;
	}
	public void setSpeakerNick(String speakerNick) {
		this.speakerNick = speakerNick;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getPublishedLessonCount() {
		return publishedLessonCount;
	}
	public void setPublishedLessonCount(Integer publishedLessonCount) {
		this.publishedLessonCount = publishedLessonCount;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public Integer getPayment() {
		return payment;
	}
	public void setPayment(Integer payment) {
		this.payment = payment;
	}
	public Integer getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(Integer originalPrice) {
		this.originalPrice = originalPrice;
	}
	public String getBannerImg() {
		return bannerImg;
	}
	public void setBannerImg(String bannerImg) {
		this.bannerImg = bannerImg;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getNextUpdateDate() {
		return nextUpdateDate;
	}
	public void setNextUpdateDate(Date nextUpdateDate) {
		this.nextUpdateDate = nextUpdateDate;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public Integer getLessonNum() {
		return LessonNum;
	}
	public void setLessonNum(Integer lessonNum) {
		LessonNum = lessonNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
