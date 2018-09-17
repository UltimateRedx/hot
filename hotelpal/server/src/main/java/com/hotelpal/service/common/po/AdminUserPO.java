package com.hotelpal.service.common.po;


import com.hotelpal.service.common.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminUserPO extends ExtendedBasePO {

	private String user;
	private String auth;
	private String resourceGroups;
	
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
			return Collections.emptySet();
		}
		return Arrays.stream(resourceGroups.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
	}
}
