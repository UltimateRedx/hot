package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.AdminUserDao;
import com.hotelpal.service.basic.mysql.dao.security.ResourceGroupDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.AdminUserPO;
import com.hotelpal.service.common.po.security.ResourceGroupPO;
import com.hotelpal.service.common.po.security.ResourcePO;
import com.hotelpal.service.common.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminService {
	private static final String DEFAULT_USER_AUTH = "hotelpal";
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
			if (!grantedResourceIds.isEmpty())
				po.setGrantedResources(grantedResourceIds.stream().map(resourceMap::get).collect(Collectors.toSet()));
			else
				po.setGrantedResources(Collections.emptySet());
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

	public void withdrawMenu(Integer adminUserId, String menu) {
		ResourceGroupPO resourceGroup = resourceGroupDao.getByGroupName(menu);
		AdminUserPO user = adminUserDao.getById(adminUserId);
		Set<Integer> grantedResource = user.getResourceGroupsSet();
		if (grantedResource.contains(resourceGroup.getId())) {
			grantedResource.remove(resourceGroup.getId());
			user.setResourceGroups(grantedResource);
			adminUserDao.update(user);
		}
	}

	public void deleteAdminUser(Integer adminUserId) {
		AdminUserPO user = adminUserDao.getById(adminUserId);
		user.setDeleted(BoolStatus.Y.toString());
		adminUserDao.update(user);
	}

	public void createAdminUser(String user, String name) {
		if (StringUtils.isNullEmpty(user)) {
			throw new ServiceException("请输入有效用户名/登录id");
		}
		if (adminUserDao.getByName(user) != null) {
			throw new ServiceException("用户名" + user + "已存在");
		}
		AdminUserPO n = new AdminUserPO();
		n.setUser(user);
		n.setAuth(DigestUtils.sha256Hex(DEFAULT_USER_AUTH));
		n.setName(name);
		adminUserDao.create(n);
	}
}
