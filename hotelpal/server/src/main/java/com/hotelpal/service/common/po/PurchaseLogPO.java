package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.PayMethod;

public class PurchaseLogPO extends DomainBasePO {
	private String orderTradeNo;
	private Integer courseId;
	private Integer payment;
	private Integer originalPrice;
	private String payMethod = PayMethod.NORMAL.toString();
	private String wxConfirm;
	private Integer wxPrice;
	private Integer couponId;
	
	//second stage
	private String classify;


	//========vo
	public String getPayMethodName() {
		return "FREE".equalsIgnoreCase(payMethod) ? "学习卡" : "付费";
	}
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
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getWxConfirm() {
		return wxConfirm;
	}
	public void setWxConfirm(String wxConfirm) {
		this.wxConfirm = wxConfirm;
	}
	public Integer getWxPrice() {
		return wxPrice;
	}
	public void setWxPrice(Integer wxPrice) {
		this.wxPrice = wxPrice;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public Integer getCouponId() {
		return couponId;
	}
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}
}
