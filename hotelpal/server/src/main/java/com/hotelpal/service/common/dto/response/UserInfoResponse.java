package com.hotelpal.service.common.dto.response;

import java.util.Date;

public class UserInfoResponse {
    private Integer id;
    private String userSn;
    private String wechatOpenId;
    private String headImg;
    private String phone;
    private String nickname;
    private String company;
    private String title;
    private Byte isspeaker;
    private Byte registryChannel;
    private Date authTime;
    private String authTimeStr;
    private Date registryTime;
    private String registryTimeStr;
    private String sign;
    private String speakerDescribe;
    private Integer freeCourseRemained;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserSn() {
        return userSn;
    }
    public void setUserSn(String userSn) {
        this.userSn = userSn;
    }
    public String getWechatOpenId() {
        return wechatOpenId;
    }
    public void setWechatOpenId(String wechatOpenId) {
        this.wechatOpenId = wechatOpenId;
    }
    public String getHeadImg() {
        return headImg;
    }
    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Byte getIsspeaker() {
        return isspeaker;
    }
    public void setIsspeaker(Byte isspeaker) {
        this.isspeaker = isspeaker;
    }
    public Byte getRegistryChannel() {
        return registryChannel;
    }
    public void setRegistryChannel(Byte registryChannel) {
        this.registryChannel = registryChannel;
    }
    public Date getAuthTime() {
        return authTime;
    }
    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }
    public String getAuthTimeStr() {
        return authTimeStr;
    }
    public void setAuthTimeStr(String authTimeStr) {
        this.authTimeStr = authTimeStr;
    }
    public Date getRegistryTime() {
        return registryTime;
    }
    public void setRegistryTime(Date registryTime) {
        this.registryTime = registryTime;
    }
    public String getRegistryTimeStr() {
        return registryTimeStr;
    }
    public void setRegistryTimeStr(String registryTimeStr) {
        this.registryTimeStr = registryTimeStr;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getSpeakerDescribe() {
        return speakerDescribe;
    }
    public void setSpeakerDescribe(String speakerDescribe) {
        this.speakerDescribe = speakerDescribe;
    }
    public Integer getFreeCourseRemained() {
        return freeCourseRemained;
    }
    public void setFreeCourseRemained(Integer freeCourseRemained) {
        this.freeCourseRemained = freeCourseRemained;
    }
}
