package com.hotelpal.service.common.po;

public class WXPropertyPO extends BasePO{
	private String type;//Enum WXProperty
	private String value;
	private Integer expireIn;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getExpireIn() {
		return expireIn;
	}
	public void setExpireIn(Integer expireIn) {
		this.expireIn = expireIn;
	}
}
