package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.live.LiveCourseContentPO;
import com.hotelpal.service.common.so.live.LiveCourseContentSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LiveCourseContentDao extends ExtendedMysqlBaseDao<LiveCourseContentSO, LiveCourseContentPO>{
	private static final String TABLE_NAME = TableNames.TABLE_LIVE_COURSE_CONTENT;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,introduce,instruction").split(",")));//Total 6.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<LiveCourseContentPO> getPOClass() {
		return LiveCourseContentPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, LiveCourseContentSO so, String alias) {
	
	}
}
