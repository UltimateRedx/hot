package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.ContextService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class AdminLoginController extends BaseController{
	
	@Resource
	private ContextService contextService;

	@RequestMapping(value = "/admin/login")
	@ResponseBody
	public PackVO<Void> login(HttpServletRequest request, String user, String auth) {
		if (StringUtils.isNullEmpty(user) || StringUtils.isNullEmpty(auth)) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		try {
			contextService.adminLogin(request.getSession(), user, auth);
		} catch (Exception e) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			throw e;
		}
		return new PackVO<>();
	}

	@RequestMapping(value = "/admin/heartBeat")
	@ResponseBody
	public PackVO<Void> heartBeat() {
		logger.info("heart beating...");
		return new PackVO<>();
	}

	@RequestMapping(value = "/admin/heartBeat")
	@ResponseBody
	public PackVO<Void> resetPW(HttpServletRequest request, String old, String nova) {

		return new PackVO<>();
	}
	
}
