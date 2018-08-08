package com.hotelpal.service.common.vo;

import java.util.List;

public class DailySalesVO {
	
	private List<DailyItem> days;
	
	public List<DailyItem> getDays() {
		return days;
	}
	public void setDays(List<DailyItem> days) {
		this.days = days;
	}
	
	public static class DailyItem{
		private String date;
		private Long sales;
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public Long getSales() {
			return sales;
		}
		public void setSales(Long sales) {
			this.sales = sales;
		}
	}
}