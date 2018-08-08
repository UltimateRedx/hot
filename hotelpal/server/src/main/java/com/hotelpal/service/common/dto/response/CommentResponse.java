package com.hotelpal.service.common.dto.response;

public class CommentResponse {
    private Integer id;
    private Integer lessonId;
//    private Integer userId;
    private String userName;//
    private String creationTime;//
    private Integer replytoId;
    private Integer zanCount;
    private String content;
    private Byte elite;//TODO
    private Byte isTheSpeaker;//
    private String userTitle;//
    private String userHeadImg;//
    private String userCompany;//
    private Boolean liked; //

    private Byte deleted;

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getReplytoId() {
        return replytoId;
    }

    public void setReplytoId(Integer replytoId) {
        this.replytoId = replytoId;
    }

    public Integer getZanCount() {
        return zanCount;
    }

    public void setZanCount(Integer zanCount) {
        this.zanCount = zanCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getElite() {
        return elite;
    }

    public void setElite(Byte elite) {
        this.elite = elite;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Byte getIsTheSpeaker() {
        return isTheSpeaker;
    }

    public void setIsTheSpeaker(Byte isTheSpeaker) {
        this.isTheSpeaker = isTheSpeaker;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }
}
