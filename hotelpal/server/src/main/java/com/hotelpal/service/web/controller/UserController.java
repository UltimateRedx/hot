package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.dto.response.CommentListResponse;
import com.hotelpal.service.common.dto.response.UserInfoResponse;
import com.hotelpal.service.common.dto.response.UserStatisticsResponse;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.RedPacketMO;
import com.hotelpal.service.common.so.CommentSO;
import com.hotelpal.service.common.so.UserSO;
import com.hotelpal.service.common.so.ZanLogSO;
import com.hotelpal.service.common.utils.ValidationUtils;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.converter.ServiceConverter;
import com.hotelpal.service.service.parterner.wx.MsgPushService;
import com.hotelpal.service.service.parterner.wx.WXService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

	@Resource
	private UserService userService;
	@Resource
	private ServiceConverter serviceConverter;
	@Resource
	private WXService wxService;
	@Resource
	private MsgPushService msgPushService;
	
	@RequestMapping(value = "/sendCaptcha")
	@ResponseBody
	public BaseDTO<Void> sendCaptcha(@RequestParam String phone) {
		ValidationUtils.checkPhoneFormat(phone);
		userService.sendCaptcha(phone);
		return new BaseDTO<>();
	}
	@RequestMapping(value = "/verifyPhone")
	@ResponseBody
	public BaseDTO<Map<String, Object>> verifyPhone(@RequestParam String phone, @RequestParam String code, String inviterToken) {
		if (phone.equalsIgnoreCase(SecurityContextHolder.getUserPhone())) {
			throw new ServiceException(ServiceException.USER_DUPLICATE_PHONE_LOGIN);
		}
		ValidationUtils.checkPhoneFormat(phone);
		if (ValidationUtils.isNullEmpty(code)) {
			throw new ServiceException(ServiceException.USER_LOGIN_CODE_REQUIRED);
		}
		
		Map<String, Object> map = userService.verifyPhoneCode(phone, code, inviterToken);
		BaseDTO<Map<String, Object>> res = new BaseDTO<>();
		res.setData(map);
		return res;
	}
	
	@RequestMapping(value = "/saveUserProp")
	@ResponseBody
	public BaseDTO<Void> saveUserProp(UserSO so) {
		userService.saveUserProp(so);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/newComment")
	@ResponseBody
	public BaseDTO<Void> newComment(@RequestParam Integer lessonId, Integer replyToCommentId, @RequestParam String comment) {
		CommentSO so = new CommentSO();
		so.setContent(comment);
		so.setLessonId(lessonId);
		so.setReplyToId(replyToCommentId);
		userService.createComment(so);
		if (replyToCommentId != null) {
			CompletableFuture.runAsync(() -> msgPushService.pushCommentRepliedMsg(replyToCommentId, comment));
		}
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/addZan")
	@ResponseBody
	public BaseDTO<Void> addZan(HttpServletRequest request, ZanLogSO so) {
		userService.createZan(so);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/getEliteCommentList")
	@ResponseBody
	public BaseDTO<CommentListResponse> getEliteCommentList(@RequestParam Integer lessonId,
											 @RequestParam(required = false, defaultValue = "3") Integer count) {
		CommentListResponse res = serviceConverter.getCommentList(BoolStatus.Y.toString(), lessonId, 0, count);
		BaseDTO<CommentListResponse> dto = new BaseDTO<>();
		dto.setData(res);
		return dto;
	}
	
	@RequestMapping(value = "/getCommentList")
	@ResponseBody
	public BaseDTO<CommentListResponse> getCommentList(@RequestParam Integer lessonId, @RequestParam(required = false, defaultValue = "0") Integer start,
													   @RequestParam(required = false, defaultValue = "30") Integer limit) {
		CommentListResponse res = serviceConverter.getCommentList(BoolStatus.N.toString(), lessonId, start, limit);
		BaseDTO<CommentListResponse> dto = new BaseDTO<>();
		dto.setData(res);
		return dto;
	}
	
	/**
	 * 记录已听时长(听的次数)
	 */
	@Deprecated
	@RequestMapping(value = "/recordListenTime")
	@ResponseBody
	public BaseDTO<Void> recordListenTime(@RequestParam Integer lessonId) {
		userService.increaseListenTimes(lessonId);
		return new BaseDTO<>();
	}
	
	/**
	 * 记录听的位置
	 */
	@RequestMapping(value = "/recordListenPos")
	@ResponseBody
	public BaseDTO<Void> recordListenPos(@RequestParam Integer lessonId, @RequestParam Integer recordPos) {
		userService.recordListenPos(lessonId, recordPos);
		return new BaseDTO<>();
	}

	@RequestMapping(value = "/createPayOrder")
	@ResponseBody
	public BaseDTO<Map> createPayOrder(@RequestParam Integer courseId, Integer couponId) {
		return new BaseDTO<>(userService.createPayOrder(courseId, couponId));
	}

	@RequestMapping(value = "/pay")
	@ResponseBody
	public BaseDTO<Void> pay(@RequestParam String tradeNo) {
		userService.payCourse(tradeNo);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/openRedPacket")
	@ResponseBody
	public BaseDTO<Void> openRedPacket(@RequestParam String nonce) {
		userService.openRedPacket(nonce);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/getFreeCourse")
	@ResponseBody
	public BaseDTO<Void> getFreeCourse(@RequestParam Integer courseId) {
		userService.getFreeCourse(courseId);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/getPaidCourseList")
	@ResponseBody
	public BaseDTO<Map> getPaidCourseList() {
		Map<String, Object> map = new HashMap<>();
		map.put("courseList", serviceConverter.getPaidCourseList());
		return new BaseDTO<>(map);
	}

	@RequestMapping(value = "/getUserInfo")
	@ResponseBody
	public BaseDTO<UserInfoResponse> getUserInfo() {
		UserInfoResponse userRes = serviceConverter.getUserInfo();
		BaseDTO<UserInfoResponse> res = new BaseDTO<>();
		res.setData(userRes);
		return res;
	}

	@RequestMapping(value = "/getUserStatistics")
	@ResponseBody
	public BaseDTO<UserStatisticsResponse> getUserStatistics() {
		UserStatisticsResponse res = serviceConverter.getUserStatistics();
		return new BaseDTO<>(res);
	}


	@RequestMapping(value = "/getSign")
	@ResponseBody
	public BaseDTO<Map<String, Object>> thirdSign(@RequestParam String url) {
		Map<String, Object> map = wxService.getThirdSign(url);
		return new BaseDTO<>(map);
	}

	@RequestMapping(value = "/newInvitedUser")
	@ResponseBody
	public BaseDTO newInvitedUser(String nonce) {
		userService.confirmFreeCourse(nonce);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/getRedPacketRemained")
	@ResponseBody
	public BaseDTO<RedPacketMO> getRedPacketRemained(HttpServletRequest request, @RequestParam String nonce) {
		RedPacketMO res = userService.getRedPacketInfo(nonce);
		return new BaseDTO<>(res);
	}
}
