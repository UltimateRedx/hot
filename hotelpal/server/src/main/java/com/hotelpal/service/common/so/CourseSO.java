package com.hotelpal.service.common.so;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CourseSO extends ExtendedBaseSO{
	private String status;
	private Integer speakerId;
	private Integer lessonNum;
	private String title;
	
	private Date openTime;
	private String publish;
	
	private BigDecimal price;
	private Integer courseOrder;
	
	//course_content moved here.
	private String subTitle;
	private String bannerImg; //delimit by `,`
	private String tag;        //delimit by `,`
	
	
	//Extra
	private List<String> tagList;
	private String introduce;
	private String crowd;
	private String gain;
	private String subscribe;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getSpeakerId() {
		return speakerId;
	}
	public void setSpeakerId(Integer speakerId) {
		this.speakerId = speakerId;
	}
	public Integer getLessonNum() {
		return lessonNum;
	}
	public void setLessonNum(Integer lessonNum) {
		this.lessonNum = lessonNum;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	public String getPublish() {
		return publish;
	}
	public void setPublish(String publish) {
		this.publish = publish;
	}
	public Integer getCourseOrder() {
		return courseOrder;
	}
	public void setCourseOrder(Integer courseOrder) {
		this.courseOrder = courseOrder;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getBannerImg() {
		return bannerImg;
	}
	public void setBannerImg(String bannerImg) {
		this.bannerImg = bannerImg;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public List<String> getTagList() {
		return tagList;
	}
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getCrowd() {
		return crowd;
	}
	public void setCrowd(String crowd) {
		this.crowd = crowd;
	}
	public String getGain() {
		return gain;
	}
	public void setGain(String gain) {
		this.gain = gain;
	}
	public String getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(String subscribe) {
		this.subscribe = subscribe;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
