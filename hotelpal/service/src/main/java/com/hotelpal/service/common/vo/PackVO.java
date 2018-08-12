package com.hotelpal.service.common.vo;

import java.util.List;

public class PackVO<T> {
private Boolean success = Boolean.TRUE;
	
	private List<String> messages;
	private T vo;

	private List<T> voList;

	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public T getVo() {
		return vo;
	}
	public void setVo(T vo) {
		this.vo = vo;
	}
	public List<T> getVoList() {
		return voList;
	}
	public void setVoList(List<T> voList) {
		this.voList = voList;
	}
}
