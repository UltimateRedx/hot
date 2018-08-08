package com.hotelpal.service.common.dto.response;

import java.util.List;

public class PurchasedCourseResponse {
    private Integer id;
    private String headImg;
    private String userName;
    private String userTitle;
    private String userCompany;
    private String title;
    private String openDate;
    private String updateDate;
    private String nextUpdateDate;
    private Integer publishedLessonCount;
    private Integer LessonCount;
    private Integer listenLen;//
    private String listenLessonTitle;//
    private Integer lessonAudioLen;//
    private String tradeNo;
    private String purchaseDate;
    private Integer payment;
    private Integer originalCharge;
    private Byte isGift;
    private Integer notListenedCount;//

    private String msg;//

    private Byte status;
    private List<String> bannerImg;//
    private String bannerImgStr;

    public String getNextUpdateDate() {
        return nextUpdateDate;
    }

    public void setNextUpdateDate(String nextUpdateDate) {
        this.nextUpdateDate = nextUpdateDate;
    }

    public String getBannerImgStr() {
        return bannerImgStr;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public void setBannerImgStr(String bannerImgStr) {
        this.bannerImgStr = bannerImgStr;
    }

    public List<String> getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(List<String> bannerImg) {
        this.bannerImg = bannerImg;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getNotListenedCount() {
        return notListenedCount;
    }

    public void setNotListenedCount(Integer notListenedCount) {
        this.notListenedCount = notListenedCount;
    }

    public Byte getIsGift() {
        return isGift;
    }

    public void setIsGift(Byte isGift) {
        this.isGift = isGift;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getPublishedLessonCount() {
        return publishedLessonCount;
    }

    public void setPublishedLessonCount(Integer publishedLessonCount) {
        this.publishedLessonCount = publishedLessonCount;
    }

    public Integer getLessonCount() {
        return LessonCount;
    }

    public void setLessonCount(Integer lessonCount) {
        LessonCount = lessonCount;
    }

    public Integer getListenLen() {
        return listenLen;
    }

    public void setListenLen(Integer listenLen) {
        this.listenLen = listenLen;
    }

    public String getListenLessonTitle() {
        return listenLessonTitle;
    }

    public void setListenLessonTitle(String listenLessonTitle) {
        this.listenLessonTitle = listenLessonTitle;
    }

    public Integer getLessonAudioLen() {
        return lessonAudioLen;
    }

    public void setLessonAudioLen(Integer lessonAudioLen) {
        this.lessonAudioLen = lessonAudioLen;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    public Integer getOriginalCharge() {
        return originalCharge;
    }

    public void setOriginalCharge(Integer originalCharge) {
        this.originalCharge = originalCharge;
    }
}
