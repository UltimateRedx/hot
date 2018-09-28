package com.hotelpal.service.basic.mysql.dao.security;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.security.ResourceGroupPO;
import com.hotelpal.service.common.po.security.ResourcePO;
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
	
	/**
	 * 返回的map 结构： {groupId, ResourceGroupPO}
	 */
	public Map<Integer, ResourceGroupPO> getGrantedResourcesList(Set<Integer> groupIds) {
		if (ArrayUtils.isNullEmpty(groupIds)) {
			return Collections.emptyMap();
		}
		Map<Integer, ResourceGroupPO> res = new LinkedHashMap<>();
		for (Integer groupId : groupIds) {
			ResourceGroupPO rr = new ResourceGroupPO();
			rr.setId(groupId);
			rr.setResources(new ArrayList<>());
			res.put(groupId, rr);
		}
		String[] arr = new String[groupIds.size()];
		Arrays.fill(arr, "?");

		String sql = StringUtils.format(
				"select rg.id,rg.groupName, res.id,res.accessPoint,res.menu " +
				" from {} rg " +
				" inner join {} res on FIND_IN_SET(res.id, rg.groupResources)" +
				" where rg.id in (" + String.join("?", arr) + ") " +
				" order by rg.id, res.id", TABLE_NAME, TableNames.TABLE_RESOURCE);
		dao.query(sql, groupIds.toArray(), rch -> {
			res.get(rch.getInt(1)).setGroupName(rch.getString(2));
			ResourcePO resource = new ResourcePO();
			resource.setId(rch.getInt(3));
			resource.setAccessPoint(rch.getString(4));
			resource.setMenu(rch.getString(5));
			res.get(rch.getInt(1)).getResources().add(resource);
		});
		return res;
	}
}
