package com.hotelpal.service.common.so;


public class UserSO extends BaseSO {
	private String openId;
	private String headImg;
	private String nick;
	private String company;
	private String title;
	private String regChannel;
	
	//娇艳的是新的手机号的话新建UserPO的记录
	private Boolean newPhone;
	//Old properties
    private String nickname;
	
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRegChannel() {
		return regChannel;
	}
	public void setRegChannel(String regChannel) {
		this.regChannel = regChannel;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Boolean getNewPhone() {
		return newPhone;
	}
	public void setNewPhone(Boolean newPhone) {
		this.newPhone = newPhone;
	}
}
