package com.hotelpal.service.common.mo;

import java.util.List;

public class QNPersistMO {
	private String id;
	private Integer code;
	private String desc;
	private String inputKey;
	private String inputBucket;
	private String pipeline;
	private String reqid;
	private List<QNPersistItem> items;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getInputKey() {
		return inputKey;
	}
	public void setInputKey(String inputKey) {
		this.inputKey = inputKey;
	}
	public String getInputBucket() {
		return inputBucket;
	}
	public void setInputBucket(String inputBucket) {
		this.inputBucket = inputBucket;
	}
	public String getPipeline() {
		return pipeline;
	}
	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}
	public String getReqid() {
		return reqid;
	}
	public void setReqid(String reqid) {
		this.reqid = reqid;
	}
	public List<QNPersistItem> getItems() {
		return items;
	}
	public void setItems(List<QNPersistItem> items) {
		this.items = items;
	}
	
	public static class QNPersistItem {
		private String cmd;
		private Integer code;
		private String desc;
		private String error;
		private String hash;
		private String key;
		private Integer returnOld;
		
		public String getCmd() {
			return cmd;
		}
		public void setCmd(String cmd) {
			this.cmd = cmd;
		}
		public Integer getCode() {
			return code;
		}
		public void setCode(Integer code) {
			this.code = code;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		public String getHash() {
			return hash;
		}
		public void setHash(String hash) {
			this.hash = hash;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Integer getReturnOld() {
			return returnOld;
		}
		public void setReturnOld(Integer returnOld) {
			this.returnOld = returnOld;
		}
	}
}
