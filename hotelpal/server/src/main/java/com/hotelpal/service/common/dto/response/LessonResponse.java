package com.hotelpal.service.common.dto.response;

public class LessonResponse {
    private Integer id;
    private String lessonSn;
    private Integer courseId;
    private Integer userId;
    private String userName;//
    private String creationTime;//
    private String expectPublishDate;//
    private Byte isPublish;
    private String publishTime;//
    private Integer lessonOrder;
    private Integer lessonNo;
    private String title;
    private Integer audioLen;
    private String audio;//
    private String resourceSize;
    private Integer commentCount;
    private String content;
    private Byte freeListen;

    private Integer listenLen;
    private String listenUpdateTime;

    private String userTitle;//
    private String userCompany;//

    private CommentListResponse commentList;
    private CommentListResponse eliteCommentList;

    private Byte onSale;

    private String subtitle;
    private String coverImg;

    private Long pv;
    private Long uv;

    private Integer redPacketRemained;
    private String redPacketNonce;

    private Byte isGift;

    private Integer previousLessonId;
    private Integer nextLessonId;


    public Integer getPreviousLessonId() {
        return previousLessonId;
    }

    public void setPreviousLessonId(Integer previousLessonId) {
        this.previousLessonId = previousLessonId;
    }

    public Integer getNextLessonId() {
        return nextLessonId;
    }

    public void setNextLessonId(Integer nextLessonId) {
        this.nextLessonId = nextLessonId;
    }

    public Byte getIsGift() {
        return isGift;
    }

    public void setIsGift(Byte isGift) {
        this.isGift = isGift;
    }

    public Integer getRedPacketRemained() {
        return redPacketRemained;
    }

    public void setRedPacketRemained(Integer redPacketRemained) {
        this.redPacketRemained = redPacketRemained;
    }

    public String getRedPacketNonce() {
        return redPacketNonce;
    }

    public void setRedPacketNonce(String redPacketNonce) {
        this.redPacketNonce = redPacketNonce;
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

    public String getListenUpdateTime() {
        return listenUpdateTime;
    }

    public void setListenUpdateTime(String listenUpdateTime) {
        this.listenUpdateTime = listenUpdateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public Byte getOnSale() {
        return onSale;
    }

    public void setOnSale(Byte onSale) {
        this.onSale = onSale;
    }

    public Byte getFreeListen() {
        return freeListen;
    }

    public void setFreeListen(Byte freeListen) {
        this.freeListen = freeListen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLessonSn() {
        return lessonSn;
    }

    public void setLessonSn(String lessonSn) {
        this.lessonSn = lessonSn;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getExpectPublishDate() {
        return expectPublishDate;
    }

    public void setExpectPublishDate(String expectPublishDate) {
        this.expectPublishDate = expectPublishDate;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getLessonOrder() {
        return lessonOrder;
    }

    public void setLessonOrder(Integer lessonOrder) {
        this.lessonOrder = lessonOrder;
    }

    public Integer getLessonNo() {
        return lessonNo;
    }

    public void setLessonNo(Integer lessonNo) {
        this.lessonNo = lessonNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAudioLen() {
        return audioLen;
    }

    public void setAudioLen(Integer audioLen) {
        this.audioLen = audioLen;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getResourceSize() {
        return resourceSize;
    }

    public void setResourceSize(String resourceSize) {
        this.resourceSize = resourceSize;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Byte isPublish) {
        this.isPublish = isPublish;
    }

    public Integer getListenLen() {
        return listenLen;
    }

    public void setListenLen(Integer listenLen) {
        this.listenLen = listenLen;
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

    public CommentListResponse getCommentList() {
        return commentList;
    }

    public void setCommentList(CommentListResponse commentList) {
        this.commentList = commentList;
    }

    public CommentListResponse getEliteCommentList() {
        return eliteCommentList;
    }

    public void setEliteCommentList(CommentListResponse eliteCommentList) {
        this.eliteCommentList = eliteCommentList;
    }
}
