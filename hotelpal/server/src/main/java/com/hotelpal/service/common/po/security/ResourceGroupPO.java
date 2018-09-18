package com.hotelpal.service.common.po.security;

import com.hotelpal.service.common.po.BasePO;

import java.util.List;
import java.util.Objects;

public class ResourceGroupPO extends BasePO {
	private static final long serialVersionUID = -44626427823990447L;
	private String groupName;
	//resource 的id逗号分隔连接的字符串
	private String groupResources;
	private List<ResourcePO> resources;

	@Override
	public boolean equals(Object o) {
		return o instanceof ResourceGroupPO && ((ResourceGroupPO) o).getId().equals(getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupResources() {
		return groupResources;
	}
	public void setGroupResources(String groupResources) {
		this.groupResources = groupResources;
	}
	public List<ResourcePO> getResources() {
		return resources;
	}
	public void setResources(List<ResourcePO> resources) {
		this.resources = resources;
	}
}
