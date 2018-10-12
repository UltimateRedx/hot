package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.AdminUserDao;
import com.hotelpal.service.basic.mysql.dao.security.ResourceGroupDao;
import com.hotelpal.service.common.po.AdminUserPO;
import com.hotelpal.service.common.po.security.ResourceGroupPO;
import com.hotelpal.service.common.po.security.ResourcePO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminService {
	@Resource
	private ResourceGroupDao resourceGroupDao;
	@Resource
	private AdminUserDao adminUserDao;


	public List<AdminUserPO> getAllAdminAuth() {
		List<ResourcePO> resources = resourceGroupDao.getAllResources();
		// list -> map
		Map<Integer, ResourcePO> resourceMap = new HashMap<>();
		for (ResourcePO po : resources) {
			resourceMap.put(po.getId(), po);
		}

		List<AdminUserPO> allAdmin = adminUserDao.getAllAdminAuth();
		for (AdminUserPO po : allAdmin) {
			Set<Integer> grantedResourceIds = po.getGrantedResourceIds();
			po.setGrantedResources(grantedResourceIds.stream().map(resourceMap::get).collect(Collectors.toSet()));
		}
		return allAdmin;
	}

	public void authorizeMenu(Integer adminUserId, String menu) {
		ResourceGroupPO resourceGroup = resourceGroupDao.getByGroupName(menu);
		AdminUserPO user = adminUserDao.getById(adminUserId);
		Set<Integer> grantedResource = user.getResourceGroupsSet();
		if (!grantedResource.contains(resourceGroup.getId())) {
			grantedResource.add(resourceGroup.getId());
			user.setResourceGroups(grantedResource);
			adminUserDao.update(user);
		}
	}

}
