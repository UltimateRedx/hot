package com.hotelpal.service.common.so;


import com.hotelpal.service.common.enums.BoolStatus;

public abstract class ExtendedBaseSO extends DomainBaseSO {
	private String deleted = BoolStatus.N.toString();

	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
}
