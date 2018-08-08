package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.UserSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.ValidationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class UserDao extends MysqlBaseDao<UserSO, UserPO> {
	
	private static final String TABLE_NAME = TableNames.TABLE_USER;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList("id,createTime,updateTime,openId,headImg,nick,company,title,regChannel,liveVip,lastLoginTime".split(",")));//Total 9.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<UserPO> getPOClass() {
		return UserPO.class;
	}
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
	@Override
	protected Map<String, Integer> getTableColumnMap() {
		return TABLE_COLUMN_MAP;
	}
	@Override
	public List<String> getTableColumnList() {
		return TABLE_COLUMNS_LIST;
	}
	@Override
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, UserSO so, String alias) {
		//For custom SO properties
	}
	
	public List<UserPO> getByDomainIdList(List<Integer> domainIdList) {
	if (ValidationUtils.isNullEmpty(domainIdList)) {return null;}
		String rela = TableNames.TABLE_USER_RELA;
		String[] arr = new String[domainIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "select " + getColumnAlias("u") +
				" ,rela.domainId,rela.phone " +
				" from " + rela + " rela " +
				" inner join " + TABLE_NAME + " u on rela.userId=u.id " +
				" where rela.domainId in (" + String.join(",", arr) + ") order by field(domainId, " + String.join(",", arr) + ")";
		return dao.query(sql, ArrayUtils.addAll(domainIdList.toArray(), domainIdList.toArray()), (rs, i) -> {
			UserPO po = new UserPO();
			int index = 1;
			po.setId(rs.getInt(index++));
			po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setOpenId(rs.getString(index++));
			po.setHeadImg(rs.getString(index++));
			po.setNick(rs.getString(index++));
			po.setCompany(rs.getString(index++));
			po.setTitle(rs.getString(index++));
			po.setRegChannel(rs.getString(index++));
			po.setLiveVip(rs.getString(index++));
			po.setLastLoginTime(DateUtils.toDate(rs.getString(index++), true));
			po.setDomainId(rs.getInt(index++));
			po.setPhone(rs.getString(index++));
			return po;
		});
	}
}
