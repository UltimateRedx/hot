package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.SysCouponPO;
import com.hotelpal.service.common.so.SysCouponSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class SysCouponDao extends ExtendedMysqlBaseDao<SysCouponSO, SysCouponPO> {
	private static final String TABLE_NAME = TableNames.TABLE_SYS_COUPON;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,type,name,value,total,validityType,validity,validityDays,apply,applyToPrice,applyToCourse").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<SysCouponPO> getPOClass() {
		return SysCouponPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, SysCouponSO so, String alias) {
	
	}
}
