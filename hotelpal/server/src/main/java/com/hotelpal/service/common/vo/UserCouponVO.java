package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.UserCouponPO;

import java.util.Date;
import java.util.List;

public class UserCouponVO {
	private Card card;
	private LiveVip liveVip;
	private List<UserCouponPO> coupon;
	
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public LiveVip getLiveVip() {
		return liveVip;
	}
	public void setLiveVip(LiveVip liveVip) {
		this.liveVip = liveVip;
	}
	public List<UserCouponPO> getCoupon() {
		return coupon;
	}
	public void setCoupon(List<UserCouponPO> coupon) {
		this.coupon = coupon;
	}
	
	
	public static class Card {
		private String exists = BoolStatus.N.toString();
		private Integer leftTimes;
		private Date validity;
		
		public String getExists() {
			return exists;
		}
		public void setExists(String exists) {
			this.exists = exists;
		}
		public Integer getLeftTimes() {
			return leftTimes;
		}
		public void setLeftTimes(Integer leftTimes) {
			this.leftTimes = leftTimes;
		}
		public Date getValidity() {
			return validity;
		}
		public void setValidity(Date validity) {
			this.validity = validity;
		}
	}
	
	public static class LiveVip {
		private String exists = BoolStatus.N.toString();
		private Date validity;
		
		public String getExists() {
			return exists;
		}
		public void setExists(String exists) {
			this.exists = exists;
		}
		public Date getValidity() {
			return validity;
		}
		public void setValidity(Date validity) {
			this.validity = validity;
		}
	}
}
