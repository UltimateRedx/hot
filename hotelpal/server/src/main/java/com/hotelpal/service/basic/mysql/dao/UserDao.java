package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.UserSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.utils.ValidationUtils;
import com.hotelpal.service.common.vo.WxUserInfo;
import org.springframework.stereotype.Component;

import java.util.*;

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
		if (!StringUtils.isNullEmpty(so.getSearchValue())) {
			buff.append(" AND (rela.phone like concat('%', ?, '%') or u.nick like concat('%', ?, '%') or u.company like concat('%', ?, '%') )" );
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
		}
	}
	
	public List<UserPO> getByDomainIdList(List<Integer> domainIdList) {
	if (ValidationUtils.isNullEmpty(domainIdList)) {return Collections.emptyList();}
		String rela = TableNames.TABLE_USER_RELA;
		String[] arr = new String[domainIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "select " + getColumnAlias("u") +
				" ,rela.domainId,rela.phone " +
				" from " + rela + " rela " +
				" inner join " + TABLE_NAME + " u on rela.userId=u.id " +
				" where rela.domainId in (" + String.join(",", arr) + ") order by field(domainId, " + String.join(",", arr) + ")";
		return dao.query(sql, ArrayUtils.addAll(domainIdList.toArray(), domainIdList.toArray()).toArray(), (rs, i) -> {
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

	/**
	 * 后台页面的用户信息
	 */
	public Integer getUserInfoCount(UserSO so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		this.searchNonColumnField(buff, params, so, "rela");
		String sql = StringUtils.format("select " +
				" count(rela.domainId)" +
				" from {} rela " +
				" inner join {} u on rela.userId = u.id " +
				" where 1=1 " +
				buff.toString(), TableNames.TABLE_USER_RELA, TABLE_NAME);
		return dao.queryForObject(sql, params.toArray(),Integer.class);
	}
	public List<WxUserInfo> getUserInfoPageList(UserSO so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		this.searchNonColumnField(buff, params, so, "rela");
		StringBuilder where = new StringBuilder();
		List<Object> whereParams = new ArrayList<>();
		searchSuffix(where, whereParams, so, "");
		String sql = StringUtils.format("select " +
				" rela.phone, rela.phoneRegTime, rela.domainId, u.*, count(distinct if(pl.classify=?, pl.courseId, null)) purchaseCount, sum(pl.payment) totalFee " +
				" from {} rela " +
				" inner join {} u on rela.userId = u.id " +
				" left join {} pl on pl.domainId=rela.domainId " +
				" where 1=1 " +
				buff.toString() +
				" group by rela.domainId", TableNames.TABLE_USER_RELA, TABLE_NAME, TableNames.TABLE_PURCHASE_LOG) +
				where.toString();
		return dao.query(sql, ArrayUtils.addAll(new Object[]{CourseType.NORMAL.toString()}, params.toArray(), whereParams.toArray()).toArray(),
				(rs, index) -> {
					UserPO user = this.mapPO(rs, "u");
					WxUserInfo res = dozerBeanMapper.map(user, WxUserInfo.class);
					res.setPhone(rs.getString("rela.phone"));
					res.setPhoneRegTime(DateUtils.toDate(rs.getString("rela.phoneRegTime"), true));
					res.setDomainId(rs.getInt("rela.domainId"));
					res.setPurchasedNormalCourseCount(rs.getInt("purchaseCount"));
					res.setTotalFee(rs.getInt("totalFee"));
					return res;
				});
	}
}
