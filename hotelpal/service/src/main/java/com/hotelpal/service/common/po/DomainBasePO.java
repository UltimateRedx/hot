package com.hotelpal.service.common.po;

public abstract class DomainBasePO extends BasePO {
	private Integer domainId;
	private Boolean useSpecifiedDomain = false;
	
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public Boolean getUseSpecifiedDomain() {
		return useSpecifiedDomain;
	}
	public void setUseSpecifiedDomain(Boolean useSpecifiedDomain) {
		this.useSpecifiedDomain = useSpecifiedDomain;
	}
}
