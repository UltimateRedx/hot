package com.hotelpal.service.common.po.extra;

import com.hotelpal.service.common.po.UserCouponPO;
import com.hotelpal.service.common.po.UserPO;

public class UserCouponUserPO extends UserCouponPO {
	private UserPO user;
	
	public UserPO getUser() {
		return user;
	}
	public void setUser(UserPO user) {
		this.user = user;
	}
}
