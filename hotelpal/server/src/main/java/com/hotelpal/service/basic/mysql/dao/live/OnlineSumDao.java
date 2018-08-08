package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.live.OnlineSumPO;
import com.hotelpal.service.common.so.live.OnlineSumSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class OnlineSumDao extends MysqlBaseDao<OnlineSumSO, OnlineSumPO> {
	private static final String TABLE_NAME = TableNames.TABLE_ONLINE_SUM;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"liveCourseId,onlineSum").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<OnlineSumPO> getPOClass() {
		return OnlineSumPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, OnlineSumSO so, String alias) {
	
	}
}
