package com.hotelpal.service.common.mo;

public class ValuePair<N, V> {
	private N name;
	private V value;
	private String value0;
	private String value1;
	private String value2;
	
	public ValuePair() {}
	public ValuePair(N n, V v) {this.name = n; this.value = v;}
	
	
	public N getName() {
		return name;
	}
	public void setName(N name) {
		this.name = name;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	public String getValue0() {
		return value0;
	}
	public void setValue0(String value0) {
		this.value0 = value0;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
}
