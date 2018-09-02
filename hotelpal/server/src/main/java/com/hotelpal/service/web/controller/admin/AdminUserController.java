package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.so.UserSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.common.vo.WxUserInfo;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.parterner.wx.WXService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/admin/user")
public class AdminUserController {
	@Resource
	private UserService userService;
	@Resource
	private WXService wxService;
	
	@RequestMapping(value = "/getUserList")
	@ResponseBody
	public PackVO getUserList(@RequestBody UserSO so) {
		PackVO<WxUserInfo> res = new PackVO<>();
		res.setVoList(userService.getUserList(so));
		res.setPageInfo(so);
		return res;
	}

	@RequestMapping(value = "/refreshWxUserInfo")
	@ResponseBody
	public PackVO refreshWxUserInfo(Integer domainId) {
		wxService.updateUserInfo(domainId);
		return new PackVO();
	}
}
