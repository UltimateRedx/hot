package com.hotelpal.service.common.po;

import com.hotelpal.service.common.enums.BoolStatus;

import java.util.Date;

public class LessonPO extends ExtendedBasePO {
	private String type;
	private Integer courseId;
	private Date publishDate;
	private String free = BoolStatus.N.toString();
	private String onSale = BoolStatus.N.toString();
	private Integer lessonOrder;
	private Integer no;
	private String title;
	private String audioUrl;
	private Integer audioLen;
	private Integer audioSize;
	private Integer commentCount;
	private Integer contentId;
	private String coverImg;
	
	//Extra
	private String content;
	//Readable audio size
	private String resourceSize;
	
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getFree() {
		return free;
	}
	public void setFree(String free) {
		this.free = free;
	}
	public String getOnSale() {
		return onSale;
	}
	public void setOnSale(String onSale) {
		this.onSale = onSale;
	}
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	public Integer getAudioLen() {
		return audioLen;
	}
	public void setAudioLen(Integer audioLen) {
		this.audioLen = audioLen;
	}
	public Integer getAudioSize() {
		return audioSize;
	}
	public void setAudioSize(Integer audioSize) {
		this.audioSize = audioSize;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public Integer getContentId() {
		return contentId;
	}
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getResourceSize() {
		return resourceSize;
	}
	public void setResourceSize(String resourceSize) {
		this.resourceSize = resourceSize;
	}
	public Integer getLessonOrder() {
		return lessonOrder;
	}
	public void setLessonOrder(Integer lessonOrder) {
		this.lessonOrder = lessonOrder;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCoverImg() {
		return coverImg;
	}
	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}
}
