package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.enums.BoolStatus;

public abstract class ExtendedBaseVO extends DomainBaseVO{
	private String deleted = BoolStatus.N.toString();
	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
}
