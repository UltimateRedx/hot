package com.hotelpal.service.common.po;

import java.util.Date;

public class StatisticsPO extends BasePO{
	private String type;
	private Integer statisticsId;
	private Date statisticsDate;
	private Integer value;
	
	
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
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getStatisticsId() {
		return statisticsId;
	}
	public void setStatisticsId(Integer statisticsId) {
		this.statisticsId = statisticsId;
	}
}
