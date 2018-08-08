package com.hotelpal.service.common.dto.response;

import java.util.List;

public class CourseResponse {

	private Integer id;
	private String courseSn;
	private Integer userId;
	private Integer lessonCount;
	private String title;
	private String creationTime;//
	private String openTime;//
	private Byte isPublish;
	private Byte status;
	private String publishTime;//
	private Integer charge;
	private Integer commentCount;
	private String subtitle;
	private List<Tag> tag;//
	private String introduce;
	private String gain;
	private String crowd;//
	private List<String> bannerImg;

	private Byte isGift;
	private Integer payment;
	private Boolean purchased;

	private String headImg;
	private String userName;//
	private String company;
	private String userTitle;
	private String sign;
	private String speakerDescribe;

	//lesson info
	private List<LessonResponse> lessonList;

	private String orderTradeNo;
	private String purchaseDate;
	private Integer originalCharge;
	private Integer sold;

	private String subscribe;

	private Integer courseOrder;

	private Long pv;
	private Long uv;

	private Integer redPacketRemained;
	private String redPacketNonce;
	private Long revenue;


	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Long getRevenue() {
		return revenue;
	}
	public void setRevenue(Long revenue) {
		this.revenue = revenue;
	}
	public String getRedPacketNonce() {
		return redPacketNonce;
	}
	public void setRedPacketNonce(String redPacketNonce) {
		this.redPacketNonce = redPacketNonce;
	}
	public Integer getRedPacketRemained() {
		return redPacketRemained;
	}
	public void setRedPacketRemained(Integer redPacketRemained) {
		this.redPacketRemained = redPacketRemained;
	}
	public Integer getCourseOrder() {
		return courseOrder;
	}
	public void setCourseOrder(Integer courseOrder) {
		this.courseOrder = courseOrder;
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
	public String getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(String subscribe) {
		this.subscribe = subscribe;
	}
	public String getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public Integer getOriginalCharge() {
		return originalCharge;
	}
	public void setOriginalCharge(Integer originalCharge) {
		this.originalCharge = originalCharge;
	}
	public String getOrderTradeNo() {
		return orderTradeNo;
	}
	public void setOrderTradeNo(String orderTradeNo) {
		this.orderTradeNo = orderTradeNo;
	}
	public Integer getSold() {
		return sold;
	}
	public void setSold(Integer sold) {
		this.sold = sold;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCourseSn() {
		return courseSn;
	}
	public void setCourseSn(String courseSn) {
		this.courseSn = courseSn;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getLessonCount() {
		return lessonCount;
	}
	public void setLessonCount(Integer lessonCount) {
		this.lessonCount = lessonCount;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	public Byte getIsPublish() {
		return isPublish;
	}
	public void setIsPublish(Byte isPublish) {
		this.isPublish = isPublish;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public Integer getCharge() {
		return charge;
	}
	public void setCharge(Integer charge) {
		this.charge = charge;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getGain() {
		return gain;
	}
	public void setGain(String gain) {
		this.gain = gain;
	}
	public List<String> getBannerImg() {
		return bannerImg;
	}
	public void setBannerImg(List<String> bannerImg) {
		this.bannerImg = bannerImg;
	}
	public List<LessonResponse> getLessonList() {
		return lessonList;
	}
	public void setLessonList(List<LessonResponse> lessonList) {
		this.lessonList = lessonList;
	}
	public Byte getIsGift() {
		return isGift;
	}
	public void setIsGift(Byte isGift) {
		this.isGift = isGift;
	}
	public Integer getPayment() {
		return payment;
	}
	public void setPayment(Integer payment) {
		this.payment = payment;
	}
	public Boolean getPurchased() {
		return purchased;
	}
	public void setPurchased(Boolean purchased) {
		this.purchased = purchased;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getUserTitle() {
		return userTitle;
	}
	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSpeakerDescribe() {
		return speakerDescribe;
	}
	public void setSpeakerDescribe(String speakerDescribe) {
		this.speakerDescribe = speakerDescribe;
	}
	public List<Tag> getTag() {
		return tag;
	}
	public void setTag(List<Tag> tag) {
		this.tag = tag;
	}
	public String getCrowd() {
		return crowd;
	}
	public void setCrowd(String crowd) {
		this.crowd = crowd;
	}
	
	public static class Tag {
		public Tag(String t) {name = t;}
		private String name;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
