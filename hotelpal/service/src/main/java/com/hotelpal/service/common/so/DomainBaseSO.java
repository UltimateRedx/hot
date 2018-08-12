package com.hotelpal.service.common.so;

public abstract class DomainBaseSO extends BaseSO{
	private Boolean ignoreDomainId = false;
	public DomainBaseSO(){}
	public DomainBaseSO(boolean infinity) {
		super(infinity);
	}
	private Integer domainId;
	
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public Boolean getIgnoreDomainId() {
		return ignoreDomainId;
	}
	public void setIgnoreDomainId(Boolean ignoreDomainId) {
		this.ignoreDomainId = ignoreDomainId;
	}
}
