package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.AdminSessionMO;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.AdminService;
import com.hotelpal.service.service.ContextService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

@Controller
public class AdminLoginController extends BaseController{

	@Resource
	private ContextService contextService;
	@Resource
	private AdminService adminService;

	@RequestMapping(value = "/admin/login")
	@ResponseBody
	public PackVO login(HttpServletRequest request, String user, String auth) {
		if (StringUtils.isNullEmpty(user) || StringUtils.isNullEmpty(auth)) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		try {
			PackVO<Set<String>> res = new PackVO<>();
			res.setVo(contextService.adminLogin(request.getSession(), user, auth));
			return res;
		} catch (Exception e) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			throw e;
		}
	}
	
	@RequestMapping(value = "/admin/logout")
	@ResponseBody
	public PackVO<Void> logout(HttpServletRequest request) {
		contextService.adminLogout(request.getSession());
		return new PackVO<>();
	}

	@RequestMapping(value = "/admin/heartBeat")
	@ResponseBody
	public PackVO<Void> heartBeat() {
		if (logger.isDebugEnabled()) {
			logger.debug("heart beating...");
		}
		return new PackVO<>();
	}

	@RequestMapping(value = "/admin/resetPW")
	@ResponseBody
	public PackVO<Void> resetPW(HttpServletRequest request, @RequestParam String old, @RequestParam String nova) {
		AdminSessionMO mo = (AdminSessionMO) request.getSession().getAttribute("adminLoginInfo");
		contextService.resetPW(mo.getUser(), old, nova);
		return new PackVO<>();
	}

	@RequestMapping(value = "/admin/getAdminAuth")
	@ResponseBody
	public PackVO getAdminAuth() {
		return new PackVO<>(adminService.getAllAdminAuth());
	}

	@RequestMapping(value = "/admin/authorizeMenu")
	@ResponseBody
	public PackVO authorizeMenu(Integer uid, String menu) {
		adminService.authorizeMenu(uid, menu);
		return new PackVO();
	}
}
