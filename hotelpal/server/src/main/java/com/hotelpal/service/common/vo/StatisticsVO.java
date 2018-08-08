package com.hotelpal.service.common.vo;

public class StatisticsVO {
	private Integer totalUserCount;
	private Integer totalRegUserCount;
	private Integer totalFeeUserCount;
	private Long totalFee;
	
	
	//浏览次数
	private Long pv;
	private Long uv;
	private Long userCount;
	private Long regUserCount;
	private Long feeUserCount;
	private Long fee;
	
	private Long normalCoursePv;
	private Long normalCourseUv;
	private Long selfCoursePv;
	private Long selfCourseUv;
	
	
	public Integer getTotalUserCount() {
		return totalUserCount;
	}
	public void setTotalUserCount(Integer totalUserCount) {
		this.totalUserCount = totalUserCount;
	}
	public Integer getTotalRegUserCount() {
		return totalRegUserCount;
	}
	public void setTotalRegUserCount(Integer totalRegUserCount) {
		this.totalRegUserCount = totalRegUserCount;
	}
	public Integer getTotalFeeUserCount() {
		return totalFeeUserCount;
	}
	public void setTotalFeeUserCount(Integer totalFeeUserCount) {
		this.totalFeeUserCount = totalFeeUserCount;
	}
	public Long getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(Long totalFee) {
		this.totalFee = totalFee;
	}
	public Long getPv() {
		return pv;
	}
	public void setPv(Long pv) {
		this.pv = pv;
	}
	public Long getUv() {
		return uv;
	}
	public void setUv(Long uv) {
		this.uv = uv;
	}
	public Long getUserCount() {
		return userCount;
	}
	public void setUserCount(Long userCount) {
		this.userCount = userCount;
	}
	public Long getRegUserCount() {
		return regUserCount;
	}
	public void setRegUserCount(Long regUserCount) {
		this.regUserCount = regUserCount;
	}
	public Long getFeeUserCount() {
		return feeUserCount;
	}
	public void setFeeUserCount(Long feeUserCount) {
		this.feeUserCount = feeUserCount;
	}
	public Long getFee() {
		return fee;
	}
	public void setFee(Long fee) {
		this.fee = fee;
	}
	public Long getNormalCoursePv() {
		return normalCoursePv;
	}
	public void setNormalCoursePv(Long normalCoursePv) {
		this.normalCoursePv = normalCoursePv;
	}
	public Long getNormalCourseUv() {
		return normalCourseUv;
	}
	public void setNormalCourseUv(Long normalCourseUv) {
		this.normalCourseUv = normalCourseUv;
	}
	public Long getSelfCoursePv() {
		return selfCoursePv;
	}
	public void setSelfCoursePv(Long selfCoursePv) {
		this.selfCoursePv = selfCoursePv;
	}
	public Long getSelfCourseUv() {
		return selfCourseUv;
	}
	public void setSelfCourseUv(Long selfCourseUv) {
		this.selfCourseUv = selfCourseUv;
	}
}
