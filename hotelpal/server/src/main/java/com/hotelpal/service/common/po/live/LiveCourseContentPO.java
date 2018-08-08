package com.hotelpal.service.common.po.live;

import com.hotelpal.service.common.po.ExtendedBasePO;

public class LiveCourseContentPO extends ExtendedBasePO {
	private String introduce;
	private String instruction;
	
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
}
