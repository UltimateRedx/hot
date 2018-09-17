package com.hotelpal.service.basic.mysql.dao.security;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.security.ResourceGroupPO;
import com.hotelpal.service.common.so.security.ResourceGroupSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ResourceGroupDao extends MysqlBaseDao<ResourceGroupSO, ResourceGroupPO> {
	private static final String TABLE_NAME = TableNames.TABLE_RESOURCE_GROUP;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"groupName,groupResources").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<ResourceGroupPO> getPOClass() {
		return ResourceGroupPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, ResourceGroupSO so, String alias) {
		//add when necessary
	}
	
	public Set<String> getGrantedResources(Set<Integer> groupIds) {
		if (ArrayUtils.isNullEmpty(groupIds)) {
			return Collections.emptySet();
		}
		String[] arr = new String[groupIds.size()];
		Arrays.fill(arr, "?");
		String sql = StringUtils.format("select accessPoint from {} where find_in_set(id, (select group_concat(groupResources) from {} where id in {}))",
				TableNames.TABLE_RESOURCE, TABLE_NAME, "(" + String.join(",", arr) + ")");
		return new HashSet<>(dao.queryForList(sql, groupIds.toArray(), String.class));
	}
	
	public List<ResourceGroupPO> getGrantedResourcesList(Set<Integer> groupIds) {
		if (ArrayUtils.isNullEmpty(groupIds)) {
			return Collections.emptyList();
		}
		String[] arr = new String[groupIds.size()];
		Arrays.fill(arr, "?");
		
		
		return null;
	}
}
