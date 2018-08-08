package com.hotelpal.service.common.dto.response;

public class UserStatisticsResponse {

    private Integer userId;
    private Integer signedDays;
    private Integer purchasedCourseCount;
    private Integer listenedLessonCount;
    private Integer listenedTimeInSecond;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSignedDays() {
        return signedDays;
    }

    public void setSignedDays(Integer signedDays) {
        this.signedDays = signedDays;
    }

    public Integer getPurchasedCourseCount() {
        return purchasedCourseCount;
    }

    public void setPurchasedCourseCount(Integer purchasedCourseCount) {
        this.purchasedCourseCount = purchasedCourseCount;
    }

    public Integer getListenedLessonCount() {
        return listenedLessonCount;
    }

    public void setListenedLessonCount(Integer listenedLessonCount) {
        this.listenedLessonCount = listenedLessonCount;
    }

    public Integer getListenedTimeInSecond() {
        return listenedTimeInSecond;
    }

    public void setListenedTimeInSecond(Integer listenedTimeInSecond) {
        this.listenedTimeInSecond = listenedTimeInSecond;
    }
}
