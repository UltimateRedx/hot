package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.DomainBasePO;

public class LiveMockUserPO extends DomainBasePO {
	private Integer domainId;
	private String headImg;
	private String nick;
	private String company;
	private String title;
	
	@Override
	public Integer getDomainId() {
		return domainId;
	}
	@Override
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
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
}
