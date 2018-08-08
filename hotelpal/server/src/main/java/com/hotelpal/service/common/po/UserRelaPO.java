package com.hotelpal.service.common.po;

import java.util.Date;

public class UserRelaPO extends BasePO {
	
	private String openId;
	//每次新创建的用户userId不变
	private Integer userId;
	//由于更改手机号等引起的其他表中的用户关联id，可能会变
	//相当于第一次注册手机号码的userId
	private Integer domainId;
	private String phone;
	private Date liveVipStartTime;
	private Integer validity;
	private Date phoneRegTime;
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public Date getLiveVipStartTime() {
		return liveVipStartTime;
	}
	public void setLiveVipStartTime(Date liveVipStartTime) {
		this.liveVipStartTime = liveVipStartTime;
	}
	public Integer getValidity() {
		return validity;
	}
	public void setValidity(Integer validity) {
		this.validity = validity;
	}
	public Date getPhoneRegTime() {
		return phoneRegTime;
	}
	public void setPhoneRegTime(Date phoneRegTime) {
		this.phoneRegTime = phoneRegTime;
	}
}
