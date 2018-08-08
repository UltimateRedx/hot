package com.hotelpal.service.web.controller.liveCourseController;

import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.vo.LiveUserInfoVO;
import com.hotelpal.service.service.live.LiveUserService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Controller
public class LiveUserController extends BaseController {
	@Resource
	private LiveUserService liveUserService;
	
	
	/**
	 *  付费后自动报名，邀请完成后自动报名
	 *  此接口只有直播课程会员才用
	 */
	@RequestMapping(value = "/live/enroll")
	@ResponseBody
	public BaseDTO LiveCourseEnroll(Integer courseId) {
		liveUserService.liveCourseEnroll(courseId);
		return new BaseDTO();
	}
	
	/**
	 * 抢课程关联的优惠券
	 */
	private static final Lock COUPON_LOCK = new ReentrantLock();
	@RequestMapping(value = "/live/getCoupon")
	@ResponseBody
	public BaseDTO getCoupon(Integer courseId) {
		try {
			COUPON_LOCK.lock();
			liveUserService.obtainCoupon(courseId);
		} finally {
			COUPON_LOCK.unlock();
		}
		return new BaseDTO();
	}
	
	/**
	 * 创建微信的预支付订单
	 */
	@RequestMapping(value = "/live/createPayOrder")
	@ResponseBody
	public BaseDTO<Map> createPayOrder(@RequestParam Integer courseId, Integer couponId) {
		return new BaseDTO<>(liveUserService.createPayOrder(courseId, couponId));
	}
	
	@RequestMapping(value = "/live/enrollFor")
	@ResponseBody
	public BaseDTO<LiveUserInfoVO> enrollFor(Integer courseId, String inviter) {
		boolean enrolledFor = liveUserService.enrollFor(courseId, inviter);
		return new BaseDTO<>();
	}
	
	@RequestMapping(value = "/live/inviting")
	@ResponseBody
	public BaseDTO<LiveUserInfoVO> inviting(Integer courseId) {
		liveUserService.inviting(courseId);
		return new BaseDTO<>();
	}

	@RequestMapping(value = "/live/getLiveCourseInviteImg")
	@ResponseBody
	public void getLiveCourseInviteImg(HttpServletResponse response, Integer courseId) {
		response.setHeader("Content-Type", "image/png;charset=ISO-8859-1");
		String baseStr = liveUserService.getInviteImg(courseId);
		try {
			response.getWriter().write(baseStr);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("getLiveCourseInviteImg response write error...", e);
		}
	}
}
