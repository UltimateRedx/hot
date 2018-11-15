package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CourseStatus;

import java.util.Date;
import java.util.List;

public class CoursePO extends ExtendedBasePO{
	private String status = CourseStatus.NORMAL.toString();
	private Integer speakerId;
	private Integer lessonNum;
	private String title;
	
	private Date openTime;
	private String publish = BoolStatus.N.toString();
	
	private Integer price;
	private Integer contentId;
	private Integer courseOrder;
	
	//course_content moved here.
	private String subTitle;
	private String bannerImg; //delimit by `,`
	private String tag;        //delimit by `,`
	
	
	//Extra
	private SpeakerPO speaker;
	private List<String> tagList;
	private CourseContentPO courseContent;
	private Boolean purchased;
	
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
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getContentId() {
		return contentId;
	}
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
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
	public String getPublish() {
		return publish;
	}
	public void setPublish(String publish) {
		this.publish = publish;
	}
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	public List<String> getTagList() {
		return tagList;
	}
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	public SpeakerPO getSpeaker() {
		return speaker;
	}
	public void setSpeaker(SpeakerPO speaker) {
		this.speaker = speaker;
	}
	public CourseContentPO getCourseContent() {
		return courseContent;
	}
	public void setCourseContent(CourseContentPO courseContent) {
		this.courseContent = courseContent;
	}
	public Boolean getPurchased() {
		return purchased;
	}
	public void setPurchased(Boolean purchased) {
		this.purchased = purchased;
	}
}
