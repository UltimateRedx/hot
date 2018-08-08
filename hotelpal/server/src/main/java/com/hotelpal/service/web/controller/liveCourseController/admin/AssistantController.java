package com.hotelpal.service.web.controller.liveCourseController.admin;

import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.live.AssistantMessagePO;
import com.hotelpal.service.common.po.live.ChatLogPO;
import com.hotelpal.service.common.so.live.ChatLogSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.live.LiveChatService;
import com.hotelpal.service.service.live.LiveContentService;
import com.hotelpal.service.service.live.LiveCourseService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/admin/live/assistant")
public class AssistantController extends BaseController {

	@Resource
	private LiveContentService liveContentService;
	@Resource
	private LiveCourseService liveCourseService;
	@Resource
	private LiveChatService liveChatService;
	
	@Deprecated
	@RequestMapping(value = "/sendMsg")
	@ResponseBody
	public PackVO<AssistantMessagePO> sendMsg(Integer courseId, String msg) {
		liveContentService.sendMsg(courseId, msg);
		PackVO<AssistantMessagePO> pack = new PackVO<>();
		pack.setVoList(liveContentService.getMsgList(courseId));
		return pack;
	}
	
	@RequestMapping(value = "/removeMsg")
	@ResponseBody
	public PackVO<AssistantMessagePO> removeMsg(Integer msgId) {
		Integer courseId = liveContentService.removeMsg(msgId);
		PackVO<AssistantMessagePO> pack = new PackVO<>();
		pack.setVoList(liveContentService.getMsgList(courseId));
		return pack;
	}
	
	@RequestMapping(value = "/blockUser")
	@ResponseBody
	public PackVO<Void> blockUser(Integer msgId) {
		liveContentService.blockUser(msgId);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/changeCouponShowStatus")
	@ResponseBody
	public PackVO<Void> changeCouponShowStatus(Integer courseId, String show) {
		liveCourseService.changeCouponShowStatus(courseId, BoolStatus.Y.toString().equalsIgnoreCase(show));
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/mockUserMsg")
	@ResponseBody
	public PackVO<Void> mockUserMsg(Integer courseId, String msg) {
		liveChatService.mockUserMsg(courseId, msg);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/userChatList")
	@ResponseBody
	public PackVO<ChatLogPO> getChatList(ChatLogSO so) {
		so.setPageSize(Integer.MAX_VALUE);
		PackVO<ChatLogPO> res = new PackVO<>();
		res.setVoList(liveCourseService.getChatList(so));
		return res;
	}
}
