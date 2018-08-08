package com.hotelpal.service.common.dto.response;


public class StatisticsUserResponse {

    private Integer newUser;
    private Integer newPayedUser;
    private Integer activeUser;
    private Integer activePayedUser;
    private Long sitePV;
    private Long siteUV;
    private Long internalLessonPV;
    private Long internalLessonUV;
    private Long expertLessonPV;
    private Long expertLessonUV;

    private Long totalUser;
    private Long totalRegistedUser;
    private Long totalPurchasedUser;
    private Long totalPayment;
    private Long periodPayment;

    public Long getTotalRegistedUser() {
        return totalRegistedUser;
    }

    public void setTotalRegistedUser(Long totalRegistedUser) {
        this.totalRegistedUser = totalRegistedUser;
    }

    public Long getTotalPurchasedUser() {
        return totalPurchasedUser;
    }

    public void setTotalPurchasedUser(Long totalPurchasedUser) {
        this.totalPurchasedUser = totalPurchasedUser;
    }

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Long getPeriodPayment() {
        return periodPayment;
    }

    public void setPeriodPayment(Long periodPayment) {
        this.periodPayment = periodPayment;
    }

    public Long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Long totalUser) {
        this.totalUser = totalUser;
    }

    public Long getSitePV() {
        return sitePV;
    }

    public void setSitePV(Long sitePV) {
        this.sitePV = sitePV;
    }

    public Long getSiteUV() {
        return siteUV;
    }

    public void setSiteUV(Long siteUV) {
        this.siteUV = siteUV;
    }

    public Long getInternalLessonPV() {
        return internalLessonPV;
    }

    public void setInternalLessonPV(Long internalLessonPV) {
        this.internalLessonPV = internalLessonPV;
    }

    public Long getInternalLessonUV() {
        return internalLessonUV;
    }

    public void setInternalLessonUV(Long internalLessonUV) {
        this.internalLessonUV = internalLessonUV;
    }

    public Long getExpertLessonPV() {
        return expertLessonPV;
    }

    public void setExpertLessonPV(Long expertLessonPV) {
        this.expertLessonPV = expertLessonPV;
    }

    public Long getExpertLessonUV() {
        return expertLessonUV;
    }

    public void setExpertLessonUV(Long expertLessonUV) {
        this.expertLessonUV = expertLessonUV;
    }

    public Integer getNewUser() {
        return newUser;
    }

    public void setNewUser(Integer newUser) {
        this.newUser = newUser;
    }

    public Integer getNewPayedUser() {
        return newPayedUser;
    }

    public void setNewPayedUser(Integer newPayedUser) {
        this.newPayedUser = newPayedUser;
    }

    public Integer getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(Integer activeUser) {
        this.activeUser = activeUser;
    }

    public Integer getActivePayedUser() {
        return activePayedUser;
    }

    public void setActivePayedUser(Integer activePayedUser) {
        this.activePayedUser = activePayedUser;
    }
}
