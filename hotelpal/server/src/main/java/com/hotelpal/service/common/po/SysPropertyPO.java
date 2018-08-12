package com.hotelpal.service.common.po;

public class SysPropertyPO extends BasePO{
	public static final String NAME_STATIC_IMG1 = "NAME_STATIC_IMG1";
	public static final String NAME_STATIC_IMG2 = "NAME_STATIC_IMG2";
	public static final String LIVE_BASE_LINE_ENROLL = "LIVE_BASE_LINE_ENROLL";
	public static final String LIVE_BASE_LINE_ONGOING = "LIVE_BASE_LINE_ONGOING";
	public static final String LIVE_BASE_LINE_TOTAL = "LIVE_BASE_LINE_TOTAL";



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
