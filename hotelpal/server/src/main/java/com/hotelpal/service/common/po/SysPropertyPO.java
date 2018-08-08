package com.hotelpal.service.common.po;

public class SysPropertyPO extends BasePO{
	public static final String NAME_STATIC_IMG1 = "NAME_STATIC_IMG1";
	public static final String NAME_STATIC_IMG2 = "NAME_STATIC_IMG2";


	private String name;
	private String value;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
