package com.hotelpal.service.common.so;

import java.util.Date;

public class StatisticsSO extends BaseSO{
	private String type;
	private Date statisticsDate;
	private Integer statisticsId;
	
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
}
