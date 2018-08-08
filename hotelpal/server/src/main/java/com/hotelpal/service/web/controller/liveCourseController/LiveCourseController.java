package com.hotelpal.service.web.controller.liveCourseController;

import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.po.live.AssistantMessagePO;
import com.hotelpal.service.common.po.live.ChatLogPO;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.so.live.ChatLogSO;
import com.hotelpal.service.service.live.LiveCourseService;
import com.hotelpal.service.service.live.LiveUserService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class LiveCourseController extends BaseController {
	
	@Resource
	private LiveCourseService liveCourseService;
	@Resource
	private LiveUserService liveUserService;
	
	/**
	 * 首页用
	 */
	@RequestMapping(value = "/live/courseList")
	@ResponseBody
	public BaseDTO<List<LiveCoursePO>> getLiveCourseList() {
		List<LiveCoursePO> poList = liveCourseService.getLiveCourseList();
		return new BaseDTO<>(poList);
	}
	
	/**
	 * 单个课程，包含用户信息
	 */
	@RequestMapping(value = "/live/course")
	@ResponseBody
	public BaseDTO<LiveCoursePO> getLiveCourse(Integer courseId) {
		LiveCoursePO po = liveCourseService.getLiveCourse(courseId);
		return new BaseDTO<>(po);
	}
	
	@RequestMapping(value = "/live/chatList")
	@ResponseBody
	public BaseDTO<List<ChatLogPO>> getChatList(ChatLogSO so) {
		return new BaseDTO<>(liveCourseService.getChatList(so));
	}
	
	@RequestMapping(value = "/live/assistantMsgList")
	@ResponseBody
	public BaseDTO<List<AssistantMessagePO>> assistantMsgList(Integer courseId) {
		return new BaseDTO<>(liveCourseService.assistantMsgList(courseId));
	}
}
