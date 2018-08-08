package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.WXPayResultPO;
import com.hotelpal.service.common.so.WXPayResultSO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class WXPayResultDao extends MysqlBaseDao<WXPayResultSO, WXPayResultPO> {
	private static final String TABLE_NAME = TableNames.TABLE_WX_PAY_RESULT;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"returnCode,returnMsg,appId,mchId,nonceStr,sign,signType,resultCode,errCode,errCodeDes,openId," +
			"isSubscribe,tradeType,bankType,totalFee,settlementTotalFee,feeType,cashFee,cashFeeType," +
			"transactionId,outTradeNo,timeEnd").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<WXPayResultPO> getPOClass() {
		return WXPayResultPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, WXPayResultSO so, String alias) {

	}

	public boolean existsByOrderNo(String orderNo) {
		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE outTradeNo=?";
		return dao.queryForObject(sql, new Object[]{orderNo}, Integer.class) > 0;
	}
}
