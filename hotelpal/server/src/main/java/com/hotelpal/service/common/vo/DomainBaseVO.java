package com.hotelpal.service.common.vo;

public abstract class DomainBaseVO extends BaseVO{
	private Integer domainId;
	
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
}
