package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.WXPropertyPO;
import com.hotelpal.service.common.so.WXPropertySO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class WXPropertyDao extends MysqlBaseDao<WXPropertySO, WXPropertyPO>{
	private static final String TABLE_NAME = TableNames.TABLE_WX_PROPERTY;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList("id,createTime,updateTime,type,value,expireIn".split(",")));//Total 6.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<WXPropertyPO> getPOClass() {
		return WXPropertyPO.class;
	}
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
	@Override
	protected List<String> getTableColumnList() {
		return TABLE_COLUMNS_LIST;
	}
	@Override
	protected Map<String, Integer> getTableColumnMap() {
		return TABLE_COLUMN_MAP;
	}
	@Override
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, WXPropertySO so, String alias) {
	
	}

	public void insertUpdate(String type, String value, Integer expireIn) {
		WXPropertySO so = new WXPropertySO();
		so.setType(type);
		WXPropertyPO po = this.getOne(so);
		if (po == null) {
			po = new WXPropertyPO();
			po.setType(type);
			po.setValue(value);
			po.setExpireIn(expireIn);
			this.create(po);
		} else {
			po.setValue(value);
			po.setExpireIn(expireIn);
			this.update(po);
		}
	}
}
