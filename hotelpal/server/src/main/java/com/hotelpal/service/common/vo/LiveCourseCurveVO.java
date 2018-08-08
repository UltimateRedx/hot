package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.live.OnlineLogPO;
import com.hotelpal.service.common.po.live.OnlineSumPO;

import java.util.List;

public class LiveCourseCurveVO {
	private List<OnlineSumPO> onlineSumList;
	private List<OnlineLogPO> onlineLogList;
	
	
	public List<OnlineSumPO> getOnlineSumList() {
		return onlineSumList;
	}
	public void setOnlineSumList(List<OnlineSumPO> onlineSumList) {
		this.onlineSumList = onlineSumList;
	}
	public List<OnlineLogPO> getOnlineLogList() {
		return onlineLogList;
	}
	public void setOnlineLogList(List<OnlineLogPO> onlineLogList) {
		this.onlineLogList = onlineLogList;
	}
}
