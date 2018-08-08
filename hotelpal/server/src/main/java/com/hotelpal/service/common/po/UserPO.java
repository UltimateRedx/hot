package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.RegChannel;

import java.util.Date;

public class UserPO extends BasePO {
	private String openId;
	private String headImg;
	private String nick;
	private String company;
	private String title;
	private String regChannel = RegChannel.NORMAL.toString();
	
	//second stage.
	private String liveVip = BoolStatus.N.toString();
	private Date liveVipStartTime;
	private Integer validity;
	private Date validityTo;
	
	
	//extra
	private Integer domainId;
	private String phone;
	private Date phoneRegTime;
	private Date lastLoginTime;

	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRegChannel() {
		return regChannel;
	}
	public void setRegChannel(String regChannel) {
		this.regChannel = regChannel;
	}
	public String getLiveVip() {
		return liveVip;
	}
	public void setLiveVip(String liveVip) {
		this.liveVip = liveVip;
	}
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
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Date getValidityTo() {
		return validityTo;
	}
	public void setValidityTo(Date validityTo) {
		this.validityTo = validityTo;
	}
}
