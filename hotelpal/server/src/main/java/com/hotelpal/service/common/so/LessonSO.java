package com.hotelpal.service.common.so;

import java.util.Date;

public class LessonSO extends ExtendedBaseSO {
	private String type;
	private Integer courseId;
	private Date publishDate;
	private String free;
	private String onSale;
	private Integer lessonOrder;
	private String title;
	private String audioUrl;
	private Integer audioLen;
	private Integer audioSize;
	private String content;
	private String coverImg;

	private Date publishDateFrom;
	private Date publishDateTo;
	private Date statisticsDateFrom;
	private Date statisticsDateTo;
	
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public Date getPublishDateFrom() {
		return publishDateFrom;
	}
	public void setPublishDateFrom(Date publishDateFrom) {
		this.publishDateFrom = publishDateFrom;
	}
	public Date getPublishDateTo() {
		return publishDateTo;
	}
	public void setPublishDateTo(Date publishDateTo) {
		this.publishDateTo = publishDateTo;
	}
	public Date getStatisticsDateFrom() {
		return statisticsDateFrom;
	}
	public void setStatisticsDateFrom(Date statisticsDateFrom) {
		this.statisticsDateFrom = statisticsDateFrom;
	}
	public Date getStatisticsDateTo() {
		return statisticsDateTo;
	}
	public void setStatisticsDateTo(Date statisticsDateTo) {
		this.statisticsDateTo = statisticsDateTo;
	}
}
