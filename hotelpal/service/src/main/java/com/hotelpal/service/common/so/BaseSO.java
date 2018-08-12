package com.hotelpal.service.common.so;

import com.hotelpal.service.common.mo.ValuePair;

import java.util.Date;
import java.util.List;

public abstract class BaseSO {
	public BaseSO(){}
	public BaseSO(boolean infinity){
		if(infinity) {
			this.from = null;
			this.limit = null;
			this.currentPage = null;
			this.pageSize = null;
		}
	}
	public static final Integer DEFAULT_PAGE_SIZE = 10;
	public static final Integer DEFAULT_CURRENT_PAGE = 1;
	private Integer id;
	private Date createTimeFrom;
	private Date createTimeTo;
	private Date updateTimeFrom;
	private Date updateTimeTo;
	private Integer from = 0;
	private Integer limit;
	private List<ValuePair<String, String>> orderByList;
	private String orderBy;
	private String order;
	private List<Integer> idList;
	
	private Integer currentPage = DEFAULT_CURRENT_PAGE;
	private Integer pageSize = DEFAULT_PAGE_SIZE;
	private Integer totalCount;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getCreateTimeFrom() {
		return createTimeFrom;
	}
	public void setCreateTimeFrom(Date createTimeFrom) {
		this.createTimeFrom = createTimeFrom;
	}
	public Date getUpdateTimeTo() {
		return updateTimeTo;
	}
	public void setUpdateTimeTo(Date updateTimeTo) {
		this.updateTimeTo = updateTimeTo;
	}
	public Date getCreateTimeTo() {
		return createTimeTo;
	}
	public void setCreateTimeTo(Date createTimeTo) {
		this.createTimeTo = createTimeTo;
	}
	public Date getUpdateTimeFrom() {
		return updateTimeFrom;
	}
	public void setUpdateTimeFrom(Date updateTimeFrom) {
		this.updateTimeFrom = updateTimeFrom;
	}
	public Integer getFrom() {
		return from;
	}
	public void setFrom(Integer from) {
		this.from = from;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public List<Integer> getIdList() {
		return idList;
	}
	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public List<ValuePair<String, String>> getOrderByList() {
		return orderByList;
	}
	public void setOrderByList(List<ValuePair<String, String>> orderByList) {
		this.orderByList = orderByList;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
}
