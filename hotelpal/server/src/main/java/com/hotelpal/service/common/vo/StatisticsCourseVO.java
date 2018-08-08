package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.CoursePO;

public class StatisticsCourseVO extends CoursePO {
	//销量
	private Integer sold;
	//销售额
	private Long sales;
	private Long pv;
	private Long uv;
	
	public Integer getSold() {
		return sold;
	}
	public void setSold(Integer sold) {
		this.sold = sold;
	}
	public Long getSales() {
		return sales;
	}
	public void setSales(Long sales) {
		this.sales = sales;
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
}
