package com.hotelpal.service.common.context;

import java.util.Date;

public class SecurityContext {
	private Integer userId;
	private Integer domainId;
	private String phone;
	private String openId;
//	private String regChannel;
	private String liveVip;
	private Date liveVipValidity;
	
	private Integer targetDomain;
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getTargetDomain() {
		return targetDomain;
	}
	public void setTargetDomain(Integer targetDomain) {
		this.targetDomain = targetDomain;
	}
	public String getLiveVip() {
		return liveVip;
	}
	public void setLiveVip(String liveVip) {
		this.liveVip = liveVip;
	}
	public Date getLiveVipValidity() {
		return liveVipValidity;
	}
	public void setLiveVipValidity(Date liveVipValidity) {
		this.liveVipValidity = liveVipValidity;
	}
}
