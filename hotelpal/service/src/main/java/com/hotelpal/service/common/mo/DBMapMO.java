package com.hotelpal.service.common.mo;

import java.lang.reflect.Method;

public class DBMapMO {
	private String fieldName;
	//Long/Integer...
	private Class type;
	private Method writeMethod;
	public DBMapMO() {}
	public DBMapMO(Class type, Method writeMethod) {
		this.type = type;
		this.writeMethod = writeMethod;
	}
	public DBMapMO(String fieldName, Class type, Method writeMethod) {
		this.fieldName = fieldName;
		this.type = type;
		this.writeMethod = writeMethod;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Method getWriteMethod() {
		return writeMethod;
	}
	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}
}
