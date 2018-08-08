package com.hotelpal.service.common.so;

import java.util.Date;
import java.util.List;

public class UserRelaSO extends BaseSO {
	private String openId;
	private String phone;
	private Integer domainId;
	private String liveVip;
	private Date phoneRegTimeFrom;
	private Date phoneRegTimeTo;
	private String searchValue;
	private List<Integer> domainIdList;
	private List<String> phoneList;
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getLiveVip() {
		return liveVip;
	}
	public void setLiveVip(String liveVip) {
		this.liveVip = liveVip;
	}
	public Date getPhoneRegTimeFrom() {
		return phoneRegTimeFrom;
	}
	public void setPhoneRegTimeFrom(Date phoneRegTimeFrom) {
		this.phoneRegTimeFrom = phoneRegTimeFrom;
	}
	public Date getPhoneRegTimeTo() {
		return phoneRegTimeTo;
	}
	public void setPhoneRegTimeTo(Date phoneRegTimeTo) {
		this.phoneRegTimeTo = phoneRegTimeTo;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public List<Integer> getDomainIdList() {
		return domainIdList;
	}
	public void setDomainIdList(List<Integer> domainIdList) {
		this.domainIdList = domainIdList;
	}
	public List<String> getPhoneList() {
		return phoneList;
	}
	public void setPhoneList(List<String> phoneList) {
		this.phoneList = phoneList;
	}
}
