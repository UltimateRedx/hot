package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.DomainBasePO;

public class LiveCourseInviteLogPO extends DomainBasePO {
	private Integer liveCourseId;
	private Integer invitedDomainId;
	
	public Integer getInvitedDomainId() {
		return invitedDomainId;
	}
	public void setInvitedDomainId(Integer invitedDomainId) {
		this.invitedDomainId = invitedDomainId;
	}
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
}
