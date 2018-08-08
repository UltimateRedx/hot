package com.hotelpal.service.common.dto.response;

public class RedPacketInfoResponse {

    private String userHeadImg;
    private String userName;

    private String courseBannerImg;
    private String speakerHeadImg;
    private String LessonTitle;
    private String LessonSubTitle;
    private String speakerTitle;
    private String speakerCompany;
    private String speakerName;

    private Integer redPacketRemained;

    private Boolean alreadyOpened;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getAlreadyOpened() {
        return alreadyOpened;
    }

    public void setAlreadyOpened(Boolean alreadyOpened) {
        this.alreadyOpened = alreadyOpened;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseBannerImg() {
        return courseBannerImg;
    }

    public void setCourseBannerImg(String courseBannerImg) {
        this.courseBannerImg = courseBannerImg;
    }

    public String getSpeakerHeadImg() {
        return speakerHeadImg;
    }

    public void setSpeakerHeadImg(String speakerHeadImg) {
        this.speakerHeadImg = speakerHeadImg;
    }

    public String getLessonTitle() {
        return LessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        LessonTitle = lessonTitle;
    }

    public String getLessonSubTitle() {
        return LessonSubTitle;
    }

    public void setLessonSubTitle(String lessonSubTitle) {
        LessonSubTitle = lessonSubTitle;
    }

    public String getSpeakerTitle() {
        return speakerTitle;
    }

    public void setSpeakerTitle(String speakerTitle) {
        this.speakerTitle = speakerTitle;
    }

    public String getSpeakerCompany() {
        return speakerCompany;
    }

    public void setSpeakerCompany(String speakerCompany) {
        this.speakerCompany = speakerCompany;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public Integer getRedPacketRemained() {
        return redPacketRemained;
    }

    public void setRedPacketRemained(Integer redPacketRemained) {
        this.redPacketRemained = redPacketRemained;
    }
}
