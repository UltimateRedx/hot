package com.hotelpal.service.common.so;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SysCouponSO extends ExtendedBaseSO{
	private String name;
	private BigDecimal value;
	private Integer total;
	private String validityType;
	private Date validity;
	private Integer validityDays;
	//ALL,PARTICULAR
	private String apply;
	//逗号分隔的
	private BigDecimal applyToPrice;
	private List<Integer> applyToCourse;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public BigDecimal getApplyToPrice() {
		return applyToPrice;
	}
	public void setApplyToPrice(BigDecimal applyToPrice) {
		this.applyToPrice = applyToPrice;
	}
}
