package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.po.ExtendedBasePO;
import com.hotelpal.service.common.vo.LiveUserInfoVO;

import java.util.Date;

public class LiveCoursePO extends ExtendedBasePO {
	private String title;
	private String subTitle;
	private Date openTime;
	private Integer price;
	private Integer inviteRequire;
	private String bannerImg;
	private Integer contentId;
	//报名中/直播中/结束等
	private String status = LiveCourseStatus.ENROLLING.toString();
	//上传的公共邀请图片，每个人的需将二维码置入
	private String inviteImg;
	private String speakerNick, speakerTitle;
	//上架
	private String publish = BoolStatus.N.toString();
	private Integer relaCourseId;
	private Integer sysCouponId;
	private String couponShow;
	//正在收看的人数
	private Integer totalPeople;
	private Integer purchasedTimes;
	private Integer freeEnrolledTimes;
	private Integer vipEnrolledTimes;
	
	//音频流地址
	private String liveAudio;
	//操作图片的地址
	private String liveImg;
	//当前直播正在播放的地址
	private String currentImg;
	
	//content
	private String introduce;
	private String instruction;
	private String relaCourseCouponImg;
	
	
	//extra
	private CoursePO relaCourse;
	private LiveUserInfoVO userInfo;
	//正在收看的人数
	private Integer present;
	
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
	public Integer getContentId() {
		return contentId;
	}
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public CoursePO getRelaCourse() {
		return relaCourse;
	}
	public void setRelaCourse(CoursePO relaCourse) {
		this.relaCourse = relaCourse;
	}
	public LiveUserInfoVO getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(LiveUserInfoVO userInfo) {
		this.userInfo = userInfo;
	}
	public Integer getTotalPeople() {
		return totalPeople;
	}
	public void setTotalPeople(Integer totalPeople) {
		this.totalPeople = totalPeople;
	}
	public String getCouponShow() {
		return couponShow;
	}
	public void setCouponShow(String couponShow) {
		this.couponShow = couponShow;
	}
	public Integer getPurchasedTimes() {
		return purchasedTimes;
	}
	public void setPurchasedTimes(Integer purchasedTimes) {
		this.purchasedTimes = purchasedTimes;
	}
	public Integer getFreeEnrolledTimes() {
		return freeEnrolledTimes;
	}
	public void setFreeEnrolledTimes(Integer freeEnrolledTimes) {
		this.freeEnrolledTimes = freeEnrolledTimes;
	}
	public String getLiveAudio() {
		return liveAudio;
	}
	public void setLiveAudio(String liveAudio) {
		this.liveAudio = liveAudio;
	}
	public String getLiveImg() {
		return liveImg;
	}
	public void setLiveImg(String liveImg) {
		this.liveImg = liveImg;
	}
	public Integer getSysCouponId() {
		return sysCouponId;
	}
	public void setSysCouponId(Integer sysCouponId) {
		this.sysCouponId = sysCouponId;
	}
	public Integer getVipEnrolledTimes() {
		return vipEnrolledTimes;
	}
	public void setVipEnrolledTimes(Integer vipEnrolledTimes) {
		this.vipEnrolledTimes = vipEnrolledTimes;
	}
	public String getCurrentImg() {
		return currentImg;
	}
	public void setCurrentImg(String currentImg) {
		this.currentImg = currentImg;
	}
	public Integer getPresent() {
		return present;
	}
	public void setPresent(Integer present) {
		this.present = present;
	}
	public String getRelaCourseCouponImg() {
		return relaCourseCouponImg;
	}
	public void setRelaCourseCouponImg(String relaCourseCouponImg) {
		this.relaCourseCouponImg = relaCourseCouponImg;
	}
}
