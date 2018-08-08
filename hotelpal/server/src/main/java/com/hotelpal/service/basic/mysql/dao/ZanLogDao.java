package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.ZanLogPO;
import com.hotelpal.service.common.so.ZanLogSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ZanLogDao extends DomainMysqlBaseDao<ZanLogSO, ZanLogPO> {
	public static final String TABLE_NAME = TableNames.TABLE_ZAN_LOG;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList("id,createTime,updateTime,domainId,lessonId,commentId".split(",")));//Total 6.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<ZanLogPO> getPOClass() {
		return ZanLogPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, ZanLogSO so, String alias) {
	
	}
}
