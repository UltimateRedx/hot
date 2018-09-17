package com.hotelpal.service.common.po.security;

import com.hotelpal.service.common.po.BasePO;

import java.util.List;

public class ResourceGroupPO extends BasePO {
	private static final long serialVersionUID = -44626427823990447L;
	private String groupName;
	private String resourceType;
	//resource 的id逗号分隔连接的字符串
	private String groupResources;
	private List<String> resources;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getGroupResources() {
		return groupResources;
	}
	public void setGroupResources(String groupResources) {
		this.groupResources = groupResources;
	}
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
}
