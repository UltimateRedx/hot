package com.hotelpal.service.common.so;

public class RegInviteSO extends DomainBaseSO{
	private String batch;
	private String couponCollected;
	
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getCouponCollected() {
		return couponCollected;
	}
	public void setCouponCollected(String couponCollected) {
		this.couponCollected = couponCollected;
	}
}
