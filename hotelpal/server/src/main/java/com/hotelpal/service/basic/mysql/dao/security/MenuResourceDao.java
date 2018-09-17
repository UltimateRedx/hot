package com.hotelpal.service.basic.mysql.dao.security;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.security.MenuResourcePO;
import com.hotelpal.service.common.so.security.MenuResourceSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MenuResourceDao extends MysqlBaseDao<MenuResourceSO, MenuResourcePO> {
	private static final String TABLE_NAME = TableNames.TABLE_RESOURCE_GROUP;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"menu,resources").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<MenuResourcePO> getPOClass() {
		return MenuResourcePO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, MenuResourceSO so, String alias) {
		//Add when need
	}
}
