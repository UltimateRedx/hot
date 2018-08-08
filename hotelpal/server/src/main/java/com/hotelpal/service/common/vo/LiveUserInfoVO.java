package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.UserPO;

import java.util.List;

public class LiveUserInfoVO {
	private String liveVip;
	private String purchased;
	private String status;
	private List<UserPO> invitedUserList;
	private String enrolled;
	private String invitePoster;
	private String enrolledFor;
	private String relateCoursePurchased;
	public String getPurchased() {
		return purchased;
	}
	public void setPurchased(String purchased) {
		this.purchased = purchased;
	}
	public List<UserPO> getInvitedUserList() {
		return invitedUserList;
	}
	public void setInvitedUserList(List<UserPO> invitedUserList) {
		this.invitedUserList = invitedUserList;
	}
	public String getEnrolled() {
		return enrolled;
	}
	public void setEnrolled(String enrolled) {
		this.enrolled = enrolled;
	}
	public String getLiveVip() {
		return liveVip;
	}
	public void setLiveVip(String liveVip) {
		this.liveVip = liveVip;
	}
	public String getInvitePoster() {
		return invitePoster;
	}
	public void setInvitePoster(String invitePoster) {
		this.invitePoster = invitePoster;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEnrolledFor() {
		return enrolledFor;
	}
	public void setEnrolledFor(String enrolledFor) {
		this.enrolledFor = enrolledFor;
	}
	public String getRelateCoursePurchased() {
		return relateCoursePurchased;
	}
	public void setRelateCoursePurchased(String relateCoursePurchased) {
		this.relateCoursePurchased = relateCoursePurchased;
	}
}
