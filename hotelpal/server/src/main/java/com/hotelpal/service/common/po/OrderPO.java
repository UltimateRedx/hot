package com.hotelpal.service.common.po;

public class OrderPO extends DomainBasePO {
	private String orderTradeNo;
	private Integer courseId;
	private Integer orderPrice;
	private String terminalIP;
	private String courseType;
	private String courseTitle;
	private Integer couponId;
	private Integer fee;
	
	private UserPO user;
	private PurchaseLogPO purchaseLog;
	
	public String getOrderTradeNo() {
		return orderTradeNo;
	}
	public void setOrderTradeNo(String orderTradeNo) {
		this.orderTradeNo = orderTradeNo;
	}
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public Integer getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}
	public String getTerminalIP() {
		return terminalIP;
	}
	public void setTerminalIP(String terminalIP) {
		this.terminalIP = terminalIP;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	public UserPO getUser() {
		return user;
	}
	public void setUser(UserPO user) {
		this.user = user;
	}
	public PurchaseLogPO getPurchaseLog() {
		return purchaseLog;
	}
	public void setPurchaseLog(PurchaseLogPO purchaseLog) {
		this.purchaseLog = purchaseLog;
	}
	public String getCourseTitle() {
		return courseTitle;
	}
	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}
	public Integer getCouponId() {
		return couponId;
	}
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}
	public Integer getFee() {
		return fee;
	}
	public void setFee(Integer fee) {
		this.fee = fee;
	}
}
