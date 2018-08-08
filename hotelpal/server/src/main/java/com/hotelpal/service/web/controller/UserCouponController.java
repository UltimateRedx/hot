package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.po.SysCouponPO;
import com.hotelpal.service.common.vo.RegInviteVO;
import com.hotelpal.service.common.vo.UserCouponVO;
import com.hotelpal.service.service.CouponService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@RequestMapping(value = "/user/coupon")
public class UserCouponController extends BaseController{
	@Resource
	private CouponService couponService;
	
	
	@RequestMapping(value = "/getUserCoupon")
	@ResponseBody
	public BaseDTO<UserCouponVO> getUserCoupon() {
		return new BaseDTO<>(couponService.getUserCoupon());
	}
	
	@RequestMapping(value = "/getInviteRegList")
	@ResponseBody
	public BaseDTO<RegInviteVO> getInviteRegList() {
		return new BaseDTO<>(couponService.getInviteRegList());
	}
	
	@RequestMapping(value = "/collectCoupon")
	@ResponseBody
	public BaseDTO<Void> collectCoupon(String batch) {
		couponService.collectUserCoupon(batch);
		return new BaseDTO<>();
	}
	
	private static final Lock COUPON_LOCK = new ReentrantLock();
	@RequestMapping(value = "/getSysCoupon")
	@ResponseBody
	public BaseDTO<Void> getSysCoupon(String nonce, Integer sysCouponId) {
		try {
			COUPON_LOCK.lock();
			couponService.userGetCoupon(sysCouponId, nonce);
		} finally {
			COUPON_LOCK.unlock();
		}
		return new BaseDTO<>();
	}

	@RequestMapping(value = "/getSysCouponInfo")
	@ResponseBody
	public BaseDTO<SysCouponPO> getSysCouponInfo(Integer sysCouponId) {
		return new BaseDTO<>(couponService.getSysCouponInfo(sysCouponId));
	}

}
