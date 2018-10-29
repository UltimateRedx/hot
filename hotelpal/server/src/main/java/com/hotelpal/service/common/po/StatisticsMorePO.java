package com.hotelpal.service.common.po;

import java.util.Date;

public class StatisticsMorePO extends BasePO{
	private String type;
	private Date statisticsDate;
	private Integer statisticsId,
		domainId;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getStatisticsDate() {
		return statisticsDate;
	}
	public void setStatisticsDate(Date statisticsDate) {
		this.statisticsDate = statisticsDate;
	}
	public Integer getStatisticsId() {
		return statisticsId;
	}
	public void setStatisticsId(Integer statisticsId) {
		this.statisticsId = statisticsId;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
}
