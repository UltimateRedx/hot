package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.SysPropertyPO;
import com.hotelpal.service.common.so.UserCouponSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class SysPropertyDao extends MysqlBaseDao<UserCouponSO, SysPropertyPO> {
	private static final String TABLE_NAME = TableNames.TABLE_SYS_PROPERTY;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"name,value").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<SysPropertyPO> getPOClass() {
		return SysPropertyPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, UserCouponSO so, String baseAlias) {
	}

	public SysPropertyPO getByName(String name) {
		String sql = "select " + getTableColumnString() + " from " + TABLE_NAME + " where name=?";
		List<SysPropertyPO> poList = dao.query(sql, new Object[]{name}, new RowMapperImpl(SysPropertyPO.class));
		return poList.isEmpty() ? null : poList.get(0);
	}
}
