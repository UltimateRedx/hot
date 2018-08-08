package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.live.LiveMockUserPO;
import com.hotelpal.service.common.so.live.LiveMockUserSO;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LiveMockUserDao  extends DomainMysqlBaseDao<LiveMockUserSO, LiveMockUserPO> {
	private static final String TABLE_NAME = TableNames.TABLE_LIVE_MOCK_USER;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,headImg,nick,company,title").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<LiveMockUserPO> getPOClass() {
		return LiveMockUserPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, LiveMockUserSO so, String alias) {
	
	}
	
	public LiveMockUserPO getAny() {
		String s = "SELECT `id` FROM " + TABLE_NAME;
		List<Integer> idList = dao.queryForList(s, Integer.class);
		Integer id = idList.get(RandomUtils.nextInt(0, idList.size()));
		return this.getById(id);
	}
}
