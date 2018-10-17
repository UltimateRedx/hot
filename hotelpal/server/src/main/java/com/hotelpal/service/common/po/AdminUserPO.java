package com.hotelpal.service.common.po;


import com.hotelpal.service.common.po.security.ResourcePO;
import com.hotelpal.service.common.utils.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminUserPO extends ExtendedBasePO {

	private String user;
	private String auth;
	private String name;
	private String resourceGroups;
	private Set<ResourcePO> grantedResources;
	private Set<Integer> grantedResourceIds;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getResourceGroups() {
		return resourceGroups;
	}
	public void setResourceGroups(String resourceGroups) {
		this.resourceGroups = resourceGroups;
	}
	public void setResourceGroups(Set<Integer> resourceGroupsSet) {
		if (resourceGroupsSet == null || resourceGroupsSet.isEmpty()) {
			this.resourceGroups = null;
		} else {
			this.resourceGroups = String.join(",", resourceGroupsSet.stream().map(Object::toString).collect(Collectors.toList()));
		}
	}
	public Set<Integer> getResourceGroupsSet() {
		if (StringUtils.isNullEmpty(resourceGroups)) {
			return new HashSet<>();
		}
		return Arrays.stream(resourceGroups.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
	}
	public Set<ResourcePO> getGrantedResources() {
		return grantedResources;
	}
	public void setGrantedResources(Set<ResourcePO> grantedResources) {
		this.grantedResources = grantedResources;
	}
	public Set<Integer> getGrantedResourceIds() {
		return grantedResourceIds;
	}
	public void setGrantedResourceIds(Set<Integer> grantedResourceIds) {
		this.grantedResourceIds = grantedResourceIds;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
