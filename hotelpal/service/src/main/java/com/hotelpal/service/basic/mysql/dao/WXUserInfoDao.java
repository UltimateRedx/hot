package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.WXUserInfoPO;
import com.hotelpal.service.common.so.WXUserInfoSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WXUserInfoDao extends MysqlBaseDao<WXUserInfoSO, WXUserInfoPO> {
	private static final String TABLE_NAME = TableNames.TABLE_WX_USER_INFO;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"subscribe,openId,nickname,sex,city,country,province,language,headImgUrl,subscribeTime,unionId,remark," +
			 "groupId,tagIdList,subscribeScene").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<WXUserInfoPO> getPOClass() {
		return WXUserInfoPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, WXUserInfoSO so, String alias) {
	}

	public WXUserInfoPO getByOpenId(String openId) {
		String sql = "SELECT " + this.getTableColumnString() + " FROM " + TABLE_NAME +
				" WHERE openId=? ";
		List<WXUserInfoPO> list = dao.query(sql, new Object[]{openId}, new RowMapperImpl(WXUserInfoPO.class));
		if (list.size() > 0) return list.get(0);
		return null;
	}
	public Map<String, ValuePair<String, String>> getByOpenIdList(List<String> openIdList) {
		if(ArrayUtils.isNullEmpty(openIdList)) return null;
		String[] arr = new String[openIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "select openId,nickname,headImgUrl,subscribe from " + TABLE_NAME + " where openId in (" + String.join(",", arr) + ")";
		Map<String, ValuePair<String, String>> res = new HashMap<>();
		for (String openId : openIdList) {
			res.put(openId, new ValuePair<>("", ""));
		}
		dao.query(sql, openIdList.toArray(), rch -> {
			res.get(rch.getString("openId")).setName(rch.getString("nickname"));
			res.get(rch.getString("openId")).setValue(rch.getString("headImgUrl"));
			res.get(rch.getString("openId")).setValue0(rch.getString("subscribe"));
		});
		return res;
	}
	public void insertUpdate(WXUserInfoPO po) {
		String exists = "select count(openId) from " + TABLE_NAME + " where openId=?";
		if (dao.queryForObject(exists, new Object[]{po.getOpenId()}, Integer.class) <= 0) {
			this.create(po);
		} else {
			updateByOpenid(po);
		}
	}

	public void updateByOpenid(WXUserInfoPO po) {
		String sql = "update " + TABLE_NAME + " set updateTime=sysdate(), subscribe=?,nickname=?,headImgUrl=?," +
				"subscribeTime=? where openId=?";
		String subscribeTime = null;
		if (po.getSubscribeTime() != null) {
			subscribeTime = DateUtils.getDateTimeString(po.getSubscribeTime());
		}
		dao.update(sql, po.getSubscribe(), po.getNickname(), po.getHeadImgUrl(), subscribeTime, po.getOpenId());
	}
}
