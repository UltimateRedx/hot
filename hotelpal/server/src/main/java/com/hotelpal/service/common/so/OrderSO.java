package com.hotelpal.service.common.so;

import java.util.Date;

public class OrderSO extends DomainBaseSO{
	private String courseType;
	private String orderTradeNo;
	private Date purchaseDateFrom;
	private Date purchaseDateTo;
	private String searchValue;
	
	public String getOrderTradeNo() {
		return orderTradeNo;
	}
	public void setOrderTradeNo(String orderTradeNo) {
		this.orderTradeNo = orderTradeNo;
	}
	public Date getPurchaseDateFrom() {
		return purchaseDateFrom;
	}
	public void setPurchaseDateFrom(Date purchaseDateFrom) {
		this.purchaseDateFrom = purchaseDateFrom;
	}
	public Date getPurchaseDateTo() {
		return purchaseDateTo;
	}
	public void setPurchaseDateTo(Date purchaseDateTo) {
		this.purchaseDateTo = purchaseDateTo;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
}
