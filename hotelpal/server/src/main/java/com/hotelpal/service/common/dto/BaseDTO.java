package com.hotelpal.service.common.dto;

import java.util.List;

public class BaseDTO<T> {
	private int code = 0;
	private T data;
	private Boolean success = true;
	private List<String> messages;
	
	public BaseDTO() {}
	public BaseDTO(T obj) {
		this.data = obj;
	}
	
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
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
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
