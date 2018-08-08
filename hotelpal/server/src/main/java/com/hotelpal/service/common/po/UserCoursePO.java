package com.hotelpal.service.common.po;

public class UserCoursePO extends DomainBasePO{
	//A 表示所有用户都可以使用，其他表示只能单个用户使用等(暂未涉及)
	private String level;
	private Integer freeCourseNum;
	private Integer expiryIn;
	private String nonce;
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public Integer getFreeCourseNum() {
		return freeCourseNum;
	}
	public void setFreeCourseNum(Integer freeCourseNum) {
		this.freeCourseNum = freeCourseNum;
	}
	public Integer getExpiryIn() {
		return expiryIn;
	}
	public void setExpiryIn(Integer expiryIn) {
		this.expiryIn = expiryIn;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
}
