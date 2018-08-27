package com.hotelpal.service.common.so;

import java.util.Date;
import java.util.List;

public class UserCouponSO extends DomainBaseSO{
	private String type;
	private String used;
	private Integer sysCouponId;
	private Date validityFrom;
	private Date validityTo;
	private List<String> includeType;
	//没有过期的
	private Boolean valid;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
	}
	public Date getValidityFrom() {
		return validityFrom;
	}
	public void setValidityFrom(Date validityFrom) {
		this.validityFrom = validityFrom;
	}
	public List<String> getIncludeType() {
		return includeType;
	}
	public void setIncludeType(List<String> includeType) {
		this.includeType = includeType;
	}
	public Integer getSysCouponId() {
		return sysCouponId;
	}
	public void setSysCouponId(Integer sysCouponId) {
		this.sysCouponId = sysCouponId;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public Date getValidityTo() {
		return validityTo;
	}
	public void setValidityTo(Date validityTo) {
		this.validityTo = validityTo;
	}
}
