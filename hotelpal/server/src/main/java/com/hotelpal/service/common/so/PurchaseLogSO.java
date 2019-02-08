package com.hotelpal.service.common.so;


import java.util.Date;

public class PurchaseLogSO extends DomainBaseSO {
	private Integer courseId;
	private String payMethod;
	private String classify;

	private Date purchaseDateFrom;
	private Date purchaseDateTo;
	private String searchValue;
	/** 课程名称搜索*/
	private String searchValueCourse;

	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
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
	public String getSearchValueCourse() {
		return searchValueCourse;
	}
	public void setSearchValueCourse(String searchValueCourse) {
		this.searchValueCourse = searchValueCourse;
	}
}
