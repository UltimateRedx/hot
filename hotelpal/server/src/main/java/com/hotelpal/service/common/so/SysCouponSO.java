package com.hotelpal.service.common.so;

import java.util.Date;
import java.util.List;

public class SysCouponSO extends ExtendedBaseSO{
	private String name;
	private Integer value;
	private Integer total;
	private String validityType;
	private Date validity;
	private Integer validityDays;
	//ALL,PARTICULAR
	private String apply;
	//逗号分隔的
	private Integer applyToPrice;
	private List<Integer> applyToCourse;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Date getValidity() {
		return validity;
	}
	public void setValidity(Date validity) {
		this.validity = validity;
	}
	public String getApply() {
		return apply;
	}
	public void setApply(String apply) {
		this.apply = apply;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<Integer> getApplyToCourse() {
		return applyToCourse;
	}
	public void setApplyToCourse(List<Integer> applyToCourse) {
		this.applyToCourse = applyToCourse;
	}
	public Integer getValidityDays() {
		return validityDays;
	}
	public void setValidityDays(Integer validityDays) {
		this.validityDays = validityDays;
	}
	public String getValidityType() {
		return validityType;
	}
	public void setValidityType(String validityType) {
		this.validityType = validityType;
	}
	public Integer getApplyToPrice() {
		return applyToPrice;
	}
	public void setApplyToPrice(Integer applyToPrice) {
		this.applyToPrice = applyToPrice;
	}
}
