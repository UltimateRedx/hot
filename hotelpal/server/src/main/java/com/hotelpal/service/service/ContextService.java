package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.AdminUserDao;
import com.hotelpal.service.basic.mysql.dao.security.MenuResourceDao;
import com.hotelpal.service.basic.mysql.dao.security.ResourceGroupDao;
import com.hotelpal.service.common.context.SecurityContext;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.AdminSessionMO;
import com.hotelpal.service.common.po.AdminUserPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Component
@Transactional
public class ContextService {
	private static final String ADMIN_SESSION_ATTRIBUTE_NAME = "adminLoginInfo";
	@Resource
	private UserService userService;
	@Resource
	private AdminUserDao  adminUserDao;
	@Resource
	protected JdbcTemplate dao;
	@Resource
	private ResourceGroupDao resourceGroupDao;
	@Resource
	private MenuResourceDao menuResourceDao;
	
	
	public void initContext(String openId) {
		UserPO user = userService.getUserByOpenId(openId);
		if (user == null) {
			throw new ServiceException(ServiceException.DAO_OPENID_NOT_FOUND);
		}
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			context = new SecurityContext();
		}
		context.setUserId(user.getId());
		context.setPhone(user.getPhone());
		context.setOpenId(user.getOpenId());
		context.setDomainId(user.getDomainId());
		context.setLiveVip(BoolStatus.N.toString());
		SecurityContextHolder.setContext(context);
		if (BoolStatus.Y.toString().equalsIgnoreCase(user.getLiveVip()) && user.getValidity() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(user.getLiveVipStartTime());
			cal.add(Calendar.DATE, user.getValidity() - 1);
			Date val = DateUtils.setMaxTime(cal).getTime();
			if (val.after(new Date())) {
				context.setLiveVipValidity(val);
				context.setLiveVip(BoolStatus.Y.toString());
			}
		}
	}

	public void adminLogin(HttpSession session, String user, String auth) {
		AdminUserPO adminUser = adminUserDao.getByName(user);
		if (adminUser == null) {
			throw new ServiceException(ServiceException.ADMIN_USER_AUTH_FAILED2);
		}
		if (!DigestUtils.sha256Hex(auth).equalsIgnoreCase(adminUser.getAuth())) {
			throw new ServiceException(ServiceException.ADMIN_USER_AUTH_FAILED);
		}
		AdminSessionMO mo = new AdminSessionMO();
		mo.setUser(user);
		mo.setLoginTime(new Date());
		Set<Integer> resourceGroups = adminUser.getResourceGroupsSet();
		if (resourceGroups.isEmpty()) {
			throw new ServiceException(ServiceException.COMMON_ILLEGAL_ACCESS);
		}
		//添加可以访问的资源
		Set<String> accessableResources = resourceGroupDao.getGrantedResources(resourceGroups);
		
		mo.setGrantedResources(accessableResources);
		session.setAttribute(ADMIN_SESSION_ATTRIBUTE_NAME, mo);
		//查找可以使用的菜单
		
	}
	
	public void adminLogout(HttpSession session) {
		session.removeAttribute(ADMIN_SESSION_ATTRIBUTE_NAME);
		session.invalidate();
		
	}

	public void resetPW(String user, String old, String nova) {
		AdminUserPO adminUser = adminUserDao.getByName(user);
		if (StringUtils.isNullEmpty(old) || StringUtils.isNullEmpty(nova)) {
			throw new ServiceException(ServiceException.COMMON_EMPTY_INPUT_PARAMETER);
		}
		if (!DigestUtils.sha256Hex(old).equalsIgnoreCase(adminUser.getAuth())) {
			throw new ServiceException(ServiceException.ADMIN_USER_AUTH_FAILED);
		}
		String newAuth = DigestUtils.sha256Hex(nova);
		adminUser.setAuth(newAuth);
		adminUserDao.update(adminUser);
	}

	public void runTask() {
		String userCourseSql = "select rela.domainId, sum(freeCourseNum) " +
				" from hp_user_course uc" +
				" inner join cc_user_rela rela on uc.userPhone=rela.phone " +
				" group by uc.userPhone";
		Map<Integer, Integer> userCourseMap = new HashMap<>();
		dao.query(userCourseSql, new Object[]{}, rch -> {
			userCourseMap.put(Integer.valueOf(String.valueOf(rch.getInt(1))), Integer.valueOf(String.valueOf(rch.getInt(2))));
		});
		String inviteSql = "select id from cc_user where regChannel='INVITED'";
		List<Integer> invitedDomainList = dao.queryForList(inviteSql, Integer.class);
		for (Integer domainId : invitedDomainList) {
			if (userCourseMap.containsKey(domainId)) {
				userCourseMap.put(domainId, userCourseMap.get(domainId) + 1);
			} else {
				userCourseMap.put(domainId, 1);
			}
		}
		//使用过的
		String used = "select domainId,count(id) from cc_purchase_log where payMethod='FREE' group by domainId";
		Map<Integer, Integer> usedMap = new HashMap<>();
		dao.query(used, new Object[]{}, rch -> {
			usedMap.put(Integer.valueOf(String.valueOf(rch.getInt(1))), Integer.valueOf(String.valueOf(rch.getInt(2))));
		});

		String sql = "insert into cc_user_coupon (createTime,updateTime,domainId,`type`,used,validity)" +
				" values(sysdate(),sysdate(),?,?,'N','2099-12-31')";
		for (Map.Entry<Integer, Integer> en : userCourseMap.entrySet()) {
			int left = en.getValue() - (usedMap.containsKey(en.getKey()) ? userCourseMap.get(en.getKey()) : 0);
			for (int i = 0; i < left; i++)
				dao.update(sql, en.getKey(), "CARD");
		}
	}
}
