package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.UserCoursePO;
import com.hotelpal.service.common.so.UserCourseSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class UserCourseDao extends DomainMysqlBaseDao<UserCourseSO, UserCoursePO>{
	private static final String TABLE_NAME = TableNames.TABLE_USER_COURSE;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList("id,createTime,updateTime,domainId,level,freeCourseNum,expiryIn,nonce".split(",")));//Total 8.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<UserCoursePO> getPOClass() {
		return UserCoursePO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, UserCourseSO so, String alias) {
	
	}
	
	public UserCoursePO getByNonce(String nonce) {
		String sql = "SELECT " + this.getTableColumnString() + " FROM `" + TABLE_NAME + "` WHERE NONCE=?";
		List<UserCoursePO> poList = dao.query(sql, new Object[]{nonce}, new RowMapperImpl<>(UserCoursePO.class));
		if (!poList.isEmpty()) {
			return poList.get(0);
		}
		return null;
	}
}
