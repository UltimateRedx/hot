package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.AdminUserPO;
import com.hotelpal.service.common.so.AdminUserSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AdminUserDao extends ExtendedMysqlBaseDao<AdminUserSO, AdminUserPO> {
	
	private static final String TABLE_NAME = TableNames.TABLE_ADMIN_USER;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,user,auth,resourceGroups").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	
	@Override
	protected Class<AdminUserPO> getPOClass() {
		return AdminUserPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, AdminUserSO so, String alias) {
		// add when need
	}
	
	public AdminUserPO getByName(String user) {
		String sql = "SELECT " + this.getColumnAlias("") +
				" FROM " + TABLE_NAME + " WHERE deleted='N' AND `user`=?";
		List<AdminUserPO> list = dao.query(sql, new Object[]{user}, new RowMapperImpl(AdminUserPO.class));
		return !list.isEmpty()? list.get(0) : null;
	}

	public List<String> getAllAuth() {
		String sql = "select auth from " + TABLE_NAME + " WHERE deleted='N'";
		return dao.queryForList(sql, String.class);
	}
}
