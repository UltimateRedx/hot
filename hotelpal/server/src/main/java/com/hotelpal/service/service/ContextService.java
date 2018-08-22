package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.AdminUserDao;
import com.hotelpal.service.common.context.SecurityContext;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.AdminSessionMO;
import com.hotelpal.service.common.po.AdminUserPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.utils.DateUtils;
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
	@Resource
	private UserService userService;
	@Resource
	private AdminUserDao  adminUserDao;
	@Resource
	protected JdbcTemplate dao;
	
	/**
	 * 抛出的异常需要处理掉
	 * 此方法不经过controller
	 */
	public UserPO initContext(String openId) {
		UserPO user = userService.getUserByOpenId(openId);
		SecurityContext context = new SecurityContext();
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
			if (cal.getTime().after(new Date())) {
				context.setLiveVipValidity(DateUtils.setMaxTime(cal).getTime());
				context.setLiveVip(BoolStatus.Y.toString());
			}
		}
		return user;
	}

	@Deprecated
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
		session.setAttribute("adminLoginInfo", mo);
	}

	public void adminLogin(HttpSession session, String auth) {
		List<String> authList = adminUserDao.getAllAuth();
		String hex = DigestUtils.sha256Hex(auth);
		boolean authenticated = false;
		for (String a : authList) {
			if (a.equalsIgnoreCase(hex)){
				authenticated = true;
				break;
			}
		}
		if (!authenticated) {
			throw new ServiceException(ServiceException.ADMIN_USER_AUTH_FAILED);
		}
		AdminSessionMO mo = new AdminSessionMO();
		mo.setLoginTime(new Date());
		session.setAttribute("adminLoginInfo", mo);
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
