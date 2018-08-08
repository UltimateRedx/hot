package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.so.BaseSO;

import java.util.List;

public class PackVO<T> {
private Boolean success = Boolean.TRUE;
	
	private List<String> messages;
 
	private T vo;

	private List<T> voList;

	private Integer voTotal;
	
	private int pageSize = BaseSO.DEFAULT_PAGE_SIZE;
	private int pageNumber = 1;
	
	public void setPageInfo(BaseSO so){
		if(so == null){
			this.pageNumber = 1;
			this.pageSize = BaseSO.DEFAULT_PAGE_SIZE;
		}else{
			this.pageNumber = so.getCurrentPage() <= 0 ? 1 : so.getCurrentPage();
			this.voTotal = so.getTotalCount();
			if (so.getPageSize() != null) {
				this.pageSize = so.getPageSize() <= 0 ? BaseSO.DEFAULT_PAGE_SIZE : so.getPageSize();
			}
		}
	}
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public T getVo() {
		return vo;
	}
	public void setVo(T vo) {
		this.vo = vo;
	}
	public List<T> getVoList() {
		return voList;
	}
	public void setVoList(List<T> voList) {
		this.voList = voList;
	}
	public Integer getVoTotal() {
		return voTotal;
	}
	public void setVoTotal(Integer voTotal) {
		this.voTotal = voTotal;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
}
