package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.ListenLogPO;
import com.hotelpal.service.common.so.ListenLogSO;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.utils.ValidationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ListenLogDao extends DomainMysqlBaseDao<ListenLogSO, ListenLogPO> {
	private static final String TABLE_NAME = TableNames.TABLE_LISTEN_LOG;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList("id,createTime,updateTime,domainId,lessonId,recordLen,recordPos,maxPos".split(",")));//Total 8.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<ListenLogPO> getPOClass() {
		return ListenLogPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, ListenLogSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (ValidationUtils.isNotNullEmpty(so.getLessonIdList())) {
			buff.append(" AND ").append(alias).append("`lessonId` IN (");
			StringBuilder sub = new StringBuilder();
			for (Integer id : so.getLessonIdList()) {
				sub.append(",?");
				params.add(id);
			}
			buff.append(sub.deleteCharAt(0)).append(") ");
		}
	}
}
