package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.PurchaseLogPO;
import com.hotelpal.service.common.po.UserPO;

public class PurchaseVO extends PurchaseLogPO {
	private UserPO user;
	private String courseTitle;
	private Integer couponValue;

	public UserPO getUser() {
		return user;
	}
	public void setUser(UserPO user) {
		this.user = user;
	}
	public String getCourseTitle() {
		return courseTitle;
	}
	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}
	public Integer getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(Integer couponValue) {
		this.couponValue = couponValue;
	}
}
