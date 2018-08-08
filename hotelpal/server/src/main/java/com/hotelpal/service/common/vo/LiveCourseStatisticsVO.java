package com.hotelpal.service.common.vo;

public class LiveCourseStatisticsVO {

	//免费报名成功数
	private Integer freeEnrollTimes;
	//付费报名数
	private Integer purchaseEnrollTimes;
	private Integer vipEnrolledTimes;
	//总的免费报名人数(包括未完成)
	private Integer tryFreeEnrollCount;
	
	//付费报名金额
	private Integer purchasedFee;
	
	//总观看人数，取live_course表中字段
	private Integer totalPeople;
	//总听课人数
	private Integer totalOnline;
	private Integer relaCoursePurchaseTimes;
	//已报名的人中听过课的人数
	private Integer enrolledOnlineCount;
	
	
	public Double getFreeCompleteRate() {
		if (tryFreeEnrollCount != null && freeEnrollTimes != null && freeEnrollTimes > 0) {
			return  freeEnrollTimes / (double) tryFreeEnrollCount;
		}
		return 0.0;
	}
	public Integer getTotalEnrollCount() {
		return (freeEnrollTimes != null ? freeEnrollTimes : 0) + (purchaseEnrollTimes != null ? purchaseEnrollTimes : 0) +
				(vipEnrolledTimes != null ? vipEnrolledTimes : 0);
	}
	
	/**购买了关联课程*/
	public Double getPurchaseEnrollRate() {
		Integer count = getTotalEnrollCount();
		if (relaCoursePurchaseTimes != null && count != null && count != 0) {
			return  relaCoursePurchaseTimes / (double) count * 100;
		}
		return 0.0;
	}
	public Double getEnrollOnlineRate() {
		Integer count = getTotalEnrollCount();
		if (totalOnline != null && count != null && count != 0) {
			return  totalOnline / (double) count * 100;
		}
		return 0.0;
	}
	//购买/听课
	public Double getOnlinePurchaseRate() {
		if (relaCoursePurchaseTimes != null && totalOnline != null && totalOnline > 0) {
			return  relaCoursePurchaseTimes / (double) totalOnline * 100;
		}
		return 0.0;
	}
	public Double getOnlineEnrollRate() {
		if (enrolledOnlineCount != null) {
			return enrolledOnlineCount / (double) getTotalEnrollCount() * 100;
		}
		return 0.0;
	}
	
	
	public Integer getFreeEnrollTimes() {
		return freeEnrollTimes;
	}
	public void setFreeEnrollTimes(Integer freeEnrollTimes) {
		this.freeEnrollTimes = freeEnrollTimes;
	}
	public Integer getPurchaseEnrollTimes() {
		return purchaseEnrollTimes;
	}
	public void setPurchaseEnrollTimes(Integer purchaseEnrollTimes) {
		this.purchaseEnrollTimes = purchaseEnrollTimes;
	}
	public Integer getTryFreeEnrollCount() {
		return tryFreeEnrollCount;
	}
	public void setTryFreeEnrollCount(Integer tryFreeEnrollCount) {
		this.tryFreeEnrollCount = tryFreeEnrollCount;
	}
	public Integer getPurchasedFee() {
		return purchasedFee;
	}
	public void setPurchasedFee(Integer purchasedFee) {
		this.purchasedFee = purchasedFee;
	}
	public Integer getTotalPeople() {
		return totalPeople;
	}
	public void setTotalPeople(Integer totalPeople) {
		this.totalPeople = totalPeople;
	}
	public Integer getTotalOnline() {
		return totalOnline;
	}
	public void setTotalOnline(Integer totalOnline) {
		this.totalOnline = totalOnline;
	}
	public Integer getRelaCoursePurchaseTimes() {
		return relaCoursePurchaseTimes;
	}
	public void setRelaCoursePurchaseTimes(Integer relaCoursePurchaseTimes) {
		this.relaCoursePurchaseTimes = relaCoursePurchaseTimes;
	}
	public Integer getVipEnrolledTimes() {
		return vipEnrolledTimes;
	}
	public void setVipEnrolledTimes(Integer vipEnrolledTimes) {
		this.vipEnrolledTimes = vipEnrolledTimes;
	}
	public Integer getEnrolledOnlineCount() {
		return enrolledOnlineCount;
	}
	public void setEnrolledOnlineCount(Integer enrolledOnlineCount) {
		this.enrolledOnlineCount = enrolledOnlineCount;
	}
}
