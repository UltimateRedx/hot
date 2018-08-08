package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.DomainBasePO;
import com.hotelpal.service.common.po.UserPO;

public class ChatLogPO extends DomainBasePO {
	private Integer liveCourseId;
	private String msg;
	private String blocked;
	private String self;
	
	private UserPO user;
	
	public Integer getLiveCourseId() {
		return liveCourseId;
	}
	public void setLiveCourseId(Integer liveCourseId) {
		this.liveCourseId = liveCourseId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public UserPO getUser() {
		return user;
	}
	public void setUser(UserPO user) {
		this.user = user;
	}
	public String getBlocked() {
		return blocked;
	}
	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}
	public String getSelf() {
		return self;
	}
	public void setSelf(String self) {
		this.self = self;
	}
}
