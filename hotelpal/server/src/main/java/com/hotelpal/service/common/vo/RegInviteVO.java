package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.UserPO;

import java.util.List;

public class RegInviteVO {
	private Integer totalCoupon;
	private List<InviteList> inviteList;
	
	public Integer getTotalCoupon() {
		return totalCoupon;
	}
	public void setTotalCoupon(Integer totalCoupon) {
		this.totalCoupon = totalCoupon;
	}
	public List<InviteList> getInviteList() {
		return inviteList;
	}
	public void setInviteList(List<InviteList> inviteList) {
		this.inviteList = inviteList;
	}
	
	
	public static class InviteList {
		private String batch;
		private String CouponCollected;
		private List<UserPO> userList;
		
		public String getBatch() {
			return batch;
		}
		public void setBatch(String batch) {
			this.batch = batch;
		}
		public List<UserPO> getUserList() {
			return userList;
		}
		public void setUserList(List<UserPO> userList) {
		this.userList = userList;
	}
		public String getCouponCollected() {
			return CouponCollected;
		}
		public void setCouponCollected(String couponCollected) {
			CouponCollected = couponCollected;
		}
	}
}
