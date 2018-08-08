package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.live.LiveCourseImagePO;
import com.hotelpal.service.common.so.live.LiveCourseImageSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LiveCourseImageDao extends MysqlBaseDao<LiveCourseImageSO, LiveCourseImagePO> {
	
	private static final String TABLE_NAME = TableNames.TABLE_LIVE_COURSE_IMAGE;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"liveCourseId,imgOrder,img").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<LiveCourseImagePO> getPOClass() {
		return LiveCourseImagePO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, LiveCourseImageSO so, String alias) {
	
	}
	
	public void deleteByCourseId(Integer courseId) {
		if (courseId == null || courseId <= 0) return;
		String sql = "DELETE FROM " + TABLE_NAME +
				" WHERE liveCourseId=? ";
		dao.update(sql, courseId);
	}
}
