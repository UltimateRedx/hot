package com.hotelpal.service.web.controller.liveCourseController.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.UserRelaSO;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.live.MemberService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/live/vip")
public class VipMemberController extends BaseController {
	@Resource
	private MemberService memberService;
	
	@RequestMapping(value = "/getVipList")
	@ResponseBody
	public PackVO<UserPO> getVipList(UserRelaSO so) {
		List<UserPO> list = memberService.getVipMemberList(so);
		PackVO<UserPO> pack = new PackVO<>();
		pack.setVoList(list);
		pack.setPageInfo(so);
		return pack;
	}
	
	@RequestMapping(value = "/addLiveVip")
	@ResponseBody
	public PackVO<Void> addLiveVip(String phone, Integer validity) {
		if (StringUtils.isNullEmpty(phone)) {
			throw new ServiceException("手机号码为空");
		}
		if (validity == null ||validity <= 0) {
			throw new ServiceException("未设置有效期");
		}
		memberService.addLiveVip(phone, validity);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/removeLiveVip")
	@ResponseBody
	public PackVO<Void> removeLiveVip(String phone) {
		if (StringUtils.isNullEmpty(phone)) {
			throw new ServiceException("手机号码为空");
		}
		memberService.removeLiveVip(phone);
		return new PackVO<>();
	}
}
