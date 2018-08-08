package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.CouponType;

import java.util.Date;

public class UserCouponPO extends DomainBasePO{
	public static final Integer COUPON_REG_PRICE_REQUIRE = 99 * 100;
	private String type;
	private Integer sysCouponId;
	private String used;
	private Integer value;
	//学习卡之类的有效期
	private Date validity;
	
	
	private SysCouponPO detail;
	
	
	public String getName() {
		if (CouponType.COURSE_REG_INVITE.toString().equalsIgnoreCase(type)) {
			return "全品类优惠券";
		} else if (CouponType.COURSE_REG.toString().equalsIgnoreCase(type)) {
			return "全品类优惠券";
		} else if(CouponType.COURSE.toString().equalsIgnoreCase(type) && detail != null) {
			return detail.getName();
		}
		return null;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
	}
	public Date getValidity() {
		return validity;
	}
	public void setValidity(Date validity) {
		this.validity = validity;
	}
	public SysCouponPO getDetail() {
		return detail;
	}
	public void setDetail(SysCouponPO detail) {
		this.detail = detail;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getSysCouponId() {
		return sysCouponId;
	}
	public void setSysCouponId(Integer sysCouponId) {
		this.sysCouponId = sysCouponId;
	}
}
