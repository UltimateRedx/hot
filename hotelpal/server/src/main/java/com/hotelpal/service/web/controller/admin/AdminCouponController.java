package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.po.SysCouponPO;
import com.hotelpal.service.common.so.SysCouponSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.CouponService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/admin/coupon")
public class AdminCouponController extends BaseController {
	@Resource
	private CouponService couponService;
	
	@RequestMapping(value = "/getSysCoupon")
	@ResponseBody
	public PackVO<SysCouponPO> getSysCoupon(@RequestBody SysCouponSO so) {
		PackVO<SysCouponPO> res = new PackVO<>();
		res.setVoList(couponService.getSysCoupon(so));
		res.setPageInfo(so);
		return res;
	}
	
	@RequestMapping(value = "/updateSysCoupon")
	@ResponseBody
	public PackVO<SysCouponPO> updateSysCoupon(@RequestBody SysCouponSO so) {
		couponService.updateSysCoupon(so);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/deleteSysCoupon")
	@ResponseBody
	public PackVO<SysCouponPO> deleteSysCoupon(Integer id) {
		couponService.deleteSysCoupon(id);
		return new PackVO<>();
	}
}
