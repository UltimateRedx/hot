package com.hotelpal.service.common.so.live;

import com.hotelpal.service.common.so.ExtendedBaseSO;

import java.util.Date;
import java.util.List;

public class LiveCourseSO extends ExtendedBaseSO {
	private String title;
	private String subTitle;
	private Date openTime;
	private Integer price;
	private Integer inviteRequire;
	private String bannerImg;
	private String inviteImg;
	private String speakerNick, speakerTitle;
	private String introduce;
	private String instruction;
	private String publish;
	private Integer relaCourseId;
	private Integer sysCouponId;
	private String relaCourseCouponImg;
	
	private String status;
	private List<String> statusList;
	private Date openTimeFrom;
	private Date openTimeTo;
	public List<String> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}
	public Date getOpenTimeFrom() {
		return openTimeFrom;
	}
	public void setOpenTimeFrom(Date openTimeFrom) {
		this.openTimeFrom = openTimeFrom;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getInviteRequire() {
		return inviteRequire;
	}
	public void setInviteRequire(Integer inviteRequire) {
		this.inviteRequire = inviteRequire;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public String getBannerImg() {
		return bannerImg;
	}
	public void setBannerImg(String bannerImg) {
		this.bannerImg = bannerImg;
	}
	public String getInviteImg() {
		return inviteImg;
	}
	public void setInviteImg(String inviteImg) {
		this.inviteImg = inviteImg;
	}
	public String getSpeakerNick() {
		return speakerNick;
	}
	public void setSpeakerNick(String speakerNick) {
		this.speakerNick = speakerNick;
	}
	public String getSpeakerTitle() {
		return speakerTitle;
	}
	public void setSpeakerTitle(String speakerTitle) {
		this.speakerTitle = speakerTitle;
	}
	public String getPublish() {
		return publish;
	}
	public void setPublish(String publish) {
		this.publish = publish;
	}
	public Integer getRelaCourseId() {
		return relaCourseId;
	}
	public void setRelaCourseId(Integer relaCourseId) {
		this.relaCourseId = relaCourseId;
	}
	public Date getOpenTimeTo() {
		return openTimeTo;
	}
	public void setOpenTimeTo(Date openTimeTo) {
		this.openTimeTo = openTimeTo;
	}
	public Integer getSysCouponId() {
		return sysCouponId;
	}
	public void setSysCouponId(Integer sysCouponId) {
		this.sysCouponId = sysCouponId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRelaCourseCouponImg() {
		return relaCourseCouponImg;
	}
	public void setRelaCourseCouponImg(String relaCourseCouponImg) {
		this.relaCourseCouponImg = relaCourseCouponImg;
	}
}
