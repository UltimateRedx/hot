package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.DomainBaseSO;

public class LiveCourseInviteLogSO extends DomainBaseSO {
	private Integer liveCourseId;
	private Integer invitedDomainId;
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public Integer getInvitedDomainId() {
		return invitedDomainId;
	}
	public void setInvitedDomainId(Integer invitedDomainId) {
		this.invitedDomainId = invitedDomainId;
	}
}
