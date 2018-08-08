package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.live.OnlineLogPO;
import com.hotelpal.service.common.so.live.OnlineLogSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class OnlineLogDao extends DomainMysqlBaseDao<OnlineLogSO, OnlineLogPO> {
	private static final String TABLE_NAME = TableNames.TABLE_ONLINE_LOG;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,liveCourseId,offlineTime").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<OnlineLogPO> getPOClass() {
		return OnlineLogPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, OnlineLogSO so, String alias) {
	
	}
	
	public Integer getDistinctDomainIdCount(Integer courseId) {
		String sql = "SELECT COUNT(DISTINCT domainId) FROM " + TABLE_NAME +
				" WHERE liveCourseId=? ";
		return dao.queryForObject(sql, new Object[]{courseId}, Integer.class);
	}
}
