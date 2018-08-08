package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.BoolStatus;

public abstract class ExtendedBasePO extends DomainBasePO{
	private String deleted = BoolStatus.N.toString();

	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
}
