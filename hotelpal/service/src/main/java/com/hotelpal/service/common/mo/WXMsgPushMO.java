package com.hotelpal.service.common.mo;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

public class WXMsgPushMO {
	
	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private WXMsgItem data;
	
	public static WXMsgPushMO New() {
		return new WXMsgPushMO();
	}
	private WXMsgPushMO() {
		this.data = new WXMsgItem();
	}
	public String getTouser() {
		return touser;
	}
	public WXMsgPushMO setTouser(String touser) {
		this.touser = touser;
		return this;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public WXMsgPushMO setTemplate_id(String template_id) {
		this.template_id = template_id;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public WXMsgPushMO setUrl(String url) {
		this.url = url;
		return this;
	}
	public String getTopcolor() {
		return topcolor;
	}
	public WXMsgPushMO setTopcolor(String topcolor) {
		this.topcolor = topcolor;
		return this;
	}
	public WXMsgItem getData() {
		return data;
	}
	public WXMsgPushMO add(String key, String value, String color){
		data.put(key, new Item(value, color));
		return this;
	}
	public WXMsgPushMO add(String key, String value){
		data.put(key, new Item(value));
		return this;
	}
	
	/**
	 * 直接转化成jsonString
	 * @return {String}
	 */
	public String build() {
		return JSON.toJSONString(this);
	}
	
	public class WXMsgItem extends HashMap<String, Item> {
		
		private static final long serialVersionUID = -3728490424738325020L;
		
		public WXMsgItem() {}
		
		public WXMsgItem(String key, Item item) {
			this.put(key, item);
		}
	}
	
	public class Item {
		private Object value;
		private String color;
		
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		
		public Item(Object value) {
			this.value = value;
		}
		
		public Item(Object value, String color) {
			this.value = value;
			this.color = color;
		}
	}
}
