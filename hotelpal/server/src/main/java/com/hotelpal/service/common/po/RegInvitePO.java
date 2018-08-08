package com.hotelpal.service.common.po;

public class RegInvitePO extends DomainBasePO{
	private Integer invitedDomainId;
	private String batch;
	private String couponCollected;
	
	private UserPO invitedUser;
	
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public UserPO getInvitedUser() {
		return invitedUser;
	}
	public void setInvitedUser(UserPO invitedUser) {
		this.invitedUser = invitedUser;
	}
	public String getCouponCollected() {
		return couponCollected;
	}
	public void setCouponCollected(String couponCollected) {
		this.couponCollected = couponCollected;
	}
	public Integer getInvitedDomainId() {
		return invitedDomainId;
	}
	public void setInvitedDomainId(Integer invitedDomainId) {
		this.invitedDomainId = invitedDomainId;
	}
}
