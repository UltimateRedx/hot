package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.UserPO;

public class WxUserInfo extends UserPO {
	private String wxHeadImg;
	private String wxNickname;
	private String subscribed;
	private Integer purchasedNormalCourseCount;
	private Integer totalFee;

	public String getWxHeadImg() {
		return wxHeadImg;
	}
	public void setWxHeadImg(String wxHeadImg) {
		this.wxHeadImg = wxHeadImg;
	}
	public String getWxNickname() {
		return wxNickname;
	}
	public void setWxNickname(String wxNickname) {
		this.wxNickname = wxNickname;
	}
	public Integer getPurchasedNormalCourseCount() {
		return purchasedNormalCourseCount;
	}
	public void setPurchasedNormalCourseCount(Integer purchasedNormalCourseCount) {
		this.purchasedNormalCourseCount = purchasedNormalCourseCount;
	}
	public Integer getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}
	public String getSubscribed() {
		return subscribed;
	}
	public void setSubscribed(String subscribed) {
		this.subscribed = subscribed;
	}
}
