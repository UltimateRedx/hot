package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.AdminUserPO;
import com.hotelpal.service.common.so.AdminUserSO;
import com.hotelpal.service.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminUserDao extends ExtendedMysqlBaseDao<AdminUserSO, AdminUserPO> {
	
	private static final String TABLE_NAME = TableNames.TABLE_ADMIN_USER;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,user,name,auth,resourceGroups").split(",")));
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
		List<AdminUserPO> list = dao.query(sql, new Object[]{user}, new RowMapperImpl<>(AdminUserPO.class));
		return !list.isEmpty()? list.get(0) : null;
	}

	public List<AdminUserPO> getAllAdminAuth() {
		String sql = StringUtils.format("select u.*, group_concat(distinct r.id) resourceId"
				+ " from {} u"
				+ " left join {} g on find_in_set(g.id, u.resourceGroups)"
				+ " left join {} r on FIND_IN_SET(r.id, g.groupResources)"
				+ " where u.deleted<>? "
				+ " group by u.id", TABLE_NAME, TableNames.TABLE_RESOURCE_GROUP, TableNames.TABLE_RESOURCE);
		return dao.query(sql, new Object[]{BoolStatus.Y.toString()}, (rs, rowNum) -> {
			AdminUserPO user = this.mapPO(rs, "u");
			user.setAuth(null);
			String grantedResourceIdStr = rs.getString("resourceId");
			if (!StringUtils.isNullEmpty(grantedResourceIdStr)) {
				user.setGrantedResourceIds(Arrays.stream(grantedResourceIdStr.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toSet()));
			} else {
				user.setGrantedResourceIds(Collections.emptySet());
			}
			return user;
		});
	}
}
