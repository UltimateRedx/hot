package com.hotelpal.service.common.mo;

import com.hotelpal.service.common.po.security.ResourceGroupPO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class AdminSessionMO implements Serializable {
	
	private static final long serialVersionUID = 7450619379285783534L;
	private String user;
	private Date loginTime;
	private transient List<ResourceGroupPO> resourceGroupList;
	private Set<String> grantedResources;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Set<String> getGrantedResources() {
		return grantedResources;
	}
	public void setGrantedResources(Set<String> grantedResources) {
		this.grantedResources = grantedResources;
	}
	public List<ResourceGroupPO> getResourceGroupList() {
		return resourceGroupList;
	}
	public void setResourceGroupList(List<ResourceGroupPO> resourceGroupList) {
		this.resourceGroupList = resourceGroupList;
	}
}
