package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.WXSNSUserInfoPO;
import com.hotelpal.service.common.so.WXSNSUserInfoSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class WXSNSUserInfoDao extends MysqlBaseDao<WXSNSUserInfoSO, WXSNSUserInfoPO> {
	private static final String TABLE_NAME = TableNames.TABLE_WX_SNS_USER_INFO;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
	 "openId,nickName,sex,province,city,country,headImgUrl,privilege,unionId").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<WXSNSUserInfoPO> getPOClass() {
		return WXSNSUserInfoPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, WXSNSUserInfoSO so, String alias) {
	
	}
	
	public WXSNSUserInfoPO getByOpenId(String openId) {
		String sql = "SELECT " + getTableColumnString() + " FROM " + TABLE_NAME + " WHERE openId = ?";
		List<WXSNSUserInfoPO> list = dao.query(sql, new Object[]{openId}, new RowMapperImpl(WXSNSUserInfoPO.class));
		return list.size() > 0 ? list.get(0) : null;
	}
}
