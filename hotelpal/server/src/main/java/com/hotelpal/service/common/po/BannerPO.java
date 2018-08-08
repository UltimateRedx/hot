package com.hotelpal.service.common.po;

public class BannerPO extends ExtendedBasePO{
	private String name;
	private String link;
	private Integer bannerOrder;
	private String bannerImg;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Integer getBannerOrder() {
		return bannerOrder;
	}
	public void setBannerOrder(Integer bannerOrder) {
		this.bannerOrder = bannerOrder;
	}
	public String getBannerImg() {
		return bannerImg;
	}
	public void setBannerImg(String bannerImg) {
		this.bannerImg = bannerImg;
	}
}
