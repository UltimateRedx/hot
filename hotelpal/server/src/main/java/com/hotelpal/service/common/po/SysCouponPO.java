package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.BoolStatus;

import java.util.Date;
import java.util.List;

public class SysCouponPO extends ExtendedBasePO{
	private String type;
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
	private String applyToCourse;
	
	//Extra
	private List<String> applyToCourseTitle;
	private List<CoursePO> applyToCoursePO;
	private String link;
	private String acquired = BoolStatus.N.toString();
	//已经使用多少
	private Integer spent;
	private Integer used;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
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
	public Integer getValidityDays() {
		return validityDays;
	}
	public void setValidityDays(Integer validityDays) {
		this.validityDays = validityDays;
	}
	public String getApplyToCourse() {
		return applyToCourse;
	}
	public void setApplyToCourse(String applyToCourse) {
		this.applyToCourse = applyToCourse;
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
	public List<String> getApplyToCourseTitle() {
		return applyToCourseTitle;
	}
	public void setApplyToCourseTitle(List<String> applyToCourseTitle) {
		this.applyToCourseTitle = applyToCourseTitle;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getAcquired() {
		return acquired;
	}
	public void setAcquired(String acquired) {
		this.acquired = acquired;
	}
	public Integer getSpent() {
		return spent;
	}
	public void setSpent(Integer spent) {
		this.spent = spent;
	}
	public Integer getUsed() {
		return used;
	}
	public void setUsed(Integer used) {
		this.used = used;
	}
	public List<CoursePO> getApplyToCoursePO() {
		return applyToCoursePO;
	}
	public void setApplyToCoursePO(List<CoursePO> applyToCoursePO) {
		this.applyToCoursePO = applyToCoursePO;
	}
}
