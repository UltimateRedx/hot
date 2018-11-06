package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.UserRelaPO;
import com.hotelpal.service.common.so.UserRelaSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.StatisticsVO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class UserRelaDao extends MysqlBaseDao<UserRelaSO, UserRelaPO> {
	
	@Resource
	private UserDao userDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	
	private static final String TABLE_NAME = TableNames.TABLE_USER_RELA;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
	 "openId,userId,phone,domainId,liveVipStartTime,validity,phoneRegTime").split(",")));//Total 9.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<UserRelaPO> getPOClass() {
		return UserRelaPO.class;
	}
	@Override
	public String getTableName() {
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, UserRelaSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getPhoneRegTimeFrom() != null) {
			buff.append(B_AND_B).append(alias).append("`phoneRegTime` >= ?");
			params.add(DateUtils.getDateString(so.getPhoneRegTimeFrom()));
		}
		if (so.getPhoneRegTimeTo() != null) {
			buff.append(B_AND_B).append(alias).append("`phoneRegTime` < ?");
			Date date = new Date(so.getPhoneRegTimeTo().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
		if (ArrayUtils.isNotNullEmpty(so.getPhoneList())) {
			String[] arr = new String[so.getPhoneList().size()];
			Arrays.fill(arr, "?");
			buff.append(B_AND_B).append(alias).append("`phone` in (").append(String.join(",", arr)).append(")");
			params.addAll(so.getPhoneList());
		}
		if (!StringUtils.isNullEmpty(so.getSearchValue())) {
			buff.append(" AND (rela.phone like concat('%', ?, '%') or u.nick like concat('%', ?, '%') or u.company like concat('%', ?, '%') )" );
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
		}
	}
	public UserPO getUserByDomainId(Integer domainId) {
		String sql1 = "SELECT " + this.getTableColumnString() + " FROM `" + TABLE_NAME + "` WHERE `domainId`=? AND openId IS NOT NULL";
		List<UserRelaPO> poList = dao.query(sql1, new Object[]{domainId}, new RowMapperImpl<>(UserRelaPO.class));
		if (poList.size() == 0) return null;
		UserRelaPO po = poList.get(0);
		return userDao.getById(po.getUserId());
	}
	
	public UserPO getByOpenId(String openId) {
		String table_user = TableNames.TABLE_USER;
		String sql = "select " + this.getColumnAlias("rela") +
				"," + userDao.getColumnAlias("u") +
				" from " + TABLE_NAME + " rela " +
				" inner join " + table_user + " u on rela.userId=u.id " +
				" where rela.openId=?";
		List<UserPO> list = dao.query(sql, new Object[]{openId}, new RowMapper<UserPO>(){
			@Override
			public UserPO mapRow(ResultSet rs, int i) throws SQLException {
				UserPO po = new UserPO();
				int index = 1;
				index++;
				index++;
				index++;
				index++;
				index++;
				po.setPhone(rs.getString(index++));
				po.setDomainId(rs.getInt(index++));
				po.setLiveVipStartTime(DateUtils.toDate(rs.getString(index++), true));
				po.setValidity(rs.getInt(index++));
				po.setPhoneRegTime(DateUtils.toDate(rs.getString(index++), true));
				
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
				return po;
			}
		});
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	public UserRelaPO getByDomainId(Integer domainId) {
		String sql = "select " + getTableColumnString() + " from " + TABLE_NAME + " where domainId=?";
		List<UserRelaPO> poList = dao.query(sql, new Object[]{domainId}, new RowMapperImpl<>(UserRelaPO.class));
		return poList.isEmpty() ? null : poList.get(0);
	}

	public UserRelaPO getRelaByOpenId(String openId) {
		String sql = "select "+ getTableColumnString() +
				" from " + TABLE_NAME +
				" where openId=?";
		List<UserRelaPO> list = dao.query(sql, new Object[]{openId}, new RowMapperImpl<>(UserRelaPO.class));
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	
	public Integer getCount(UserRelaSO so) {
		String user = TableNames.TABLE_USER;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		if (!StringUtils.isNullEmpty(so.getLiveVip())) {
			buff.append(" AND u.liveVip=? ");
			params.add(so.getLiveVip());
		}
		if (!StringUtils.isNullEmpty(so.getPhone())) {
			buff.append(" AND rela.phone like concat('%', ?, '%')" );
			params.add(so.getPhone());
			so.setPhone(null);
		}
		this.searchSO(buff, params, so, "rela");
		String sql = " SELECT count(rela.id) " +
		 			" FROM " + TABLE_NAME + " rela " +
					" INNER JOIN " + user + " u on rela.userId = u.id " +
					" WHERE 1=1 " + buff.toString();
		return dao.queryForObject(sql, params.toArray(), Integer.class);
	}
	
	public List<UserPO> getPageList(UserRelaSO so) {
		String user = TableNames.TABLE_USER;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		if (!StringUtils.isNullEmpty(so.getLiveVip())) {
			buff.append(" AND u.liveVip=? ");
			params.add(so.getLiveVip());
		}
		if (!StringUtils.isNullEmpty(so.getPhone())) {
			buff.append(" AND rela.phone like concat('%', ?, '%')" );
			params.add(so.getPhone());
			so.setPhone(null);
		}
		if (ArrayUtils.isNotNullEmpty(so.getDomainIdList())) {
			String[] arr = new String[so.getDomainIdList().size()];
			Arrays.fill(arr, "?");
			buff.append(" AND rela.domainId in (").append(String.join(",", arr)).append(")");
			params.addAll(so.getDomainIdList());
		}
		this.searchSO(buff, params, so, "rela");
		this.searchSuffix(buff, params, so,"rela");
		String sql = " SELECT " + this.getColumnAlias("rela") +
		 			", " + userDao.getColumnAlias("u") +
		 			" FROM " + TABLE_NAME + " rela " +
					" INNER JOIN " + user + " u on rela.userId = u.id " +
					 " WHERE 1=1 " + buff.toString();
		return dao.query(sql, params.toArray(), (rs, i) -> {
			UserPO po = new UserPO();
			int index = 1;
			index++;
			po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
			index++;
			index++;
			po.setPhone(rs.getString(index++));
			po.setDomainId(rs.getInt(index++));
			po.setLiveVipStartTime(DateUtils.toDate(rs.getString(index++), true));
			po.setValidity(rs.getInt(index++));
			po.setPhoneRegTime(DateUtils.toDate(rs.getString(index++), true));

			po.setId(rs.getInt(index++));
			index++;
			index++;
			po.setOpenId(rs.getString(index++));
			po.setHeadImg(rs.getString(index++));
			po.setNick(rs.getString(index++));
			po.setCompany(rs.getString(index++));
			po.setTitle(rs.getString(index++));
			po.setRegChannel(rs.getString(index++));
			po.setLiveVip(rs.getString(index++));
			po.setLastLoginTime(DateUtils.toDate(rs.getString(index++), true));
			return po;
		});
	}

	
	
	public List<String> getOpenIdByDomainIdList(List<Integer> domainIdList) {
		if (ArrayUtils.isNullEmpty(domainIdList)) return Collections.emptyList();
		String[] arr = new String[domainIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "SELECT openId FROM " + TABLE_NAME +
				" WHERE domainId in (" + String.join(",", arr) + ")";
		return dao.queryForList(sql, domainIdList.toArray(), String.class);
	}
	
	public void updateAll(UserRelaPO po) {
		String pre = " UPDATE `" + TABLE_NAME + "` SET ";
		StringBuilder buff = new StringBuilder();
		for (String column : TABLE_COLUMNS_LIST) {
			if (column.equalsIgnoreCase("createTime") || column.equalsIgnoreCase("id")) continue;
			buff.append(",`").append(column).append("`=?");
		}
		String sql = pre + buff.toString().replaceFirst(",", "") + " WHERE ID=?";
		dao.update(sql, preparedStatement -> {
			int index = setPreparedStatementParams(preparedStatement, po, new HashSet<>(Arrays.asList("id", "createTime")));
			preparedStatement.setInt(index, po.getId());
		});
	}
	
	public StatisticsVO getMainTotalStatisticsData() {
		String sql = "select " +
				" count(distinct rela.openId), " +
				" count(distinct rela.phone), " +
				" count( DISTINCT if(pl.payment > 0, pl.domainId, null)), " +
				" sum(pl.payment) " +
				" from " + TABLE_NAME + " rela " +
				" left join " + purchaseLogDao.getTableName() + " pl on pl.domainId = rela.domainId";
		return dao.queryForObject(sql, (rs, i) -> {
			StatisticsVO vo = new StatisticsVO();
			int index = 1;
			vo.setTotalUserCount(rs.getInt(index++));
			vo.setTotalRegUserCount(rs.getInt(index++));
			vo.setTotalFeeUserCount(rs.getInt(index++));
			vo.setTotalFee(rs.getLong(index));
			return vo;
		});
	}
	
	public void getMainStatisticsUserData(StatisticsVO res, String from ,String to) {
		String sql = "select count(id) count from cc_user_rela where createTime >= ? and createTime<? " +
				" union all " +
				" select count(id) from cc_user_rela where phoneRegTime>= ? and phoneRegTime<? " +
				" union all " +
				" select count(distinct domainId) from cc_purchase_log where createTime>=? and createTime<? and payment>0 " +
					" and domainId not in(select distinct domainId from cc_purchase_log where createTime<? and payment>0) " +
				" union all " +
				" select sum(payment) from cc_purchase_log where createTime >=? and createTime<? ";
		List<Long> resList = dao.queryForList(sql, new Object[]{from, to, from, to, from, to, from, from, to}, Long.class);
		res.setUserCount(resList.get(0));
		res.setRegUserCount(resList.get(1));
		res.setFeeUserCount(resList.get(2));
		res.setFee(resList.get(3));
	}

	public List<String> getAllOpenId() {
		String sql = "select openId from " + TABLE_NAME ;
		return dao.queryForList(sql, String.class);
	}

	/**
	 * 获取所有没有购买某个课程的注册用户
	 */
	public List<UserPO> getUserByNonPurchase(Integer courseId) {
		String sql = StringUtils.format("select u.openId,u.nick"
				+ " from {} rela "
				+ " inner join {} u on rela.userId=u.id "
				+ " where rela.phone is not null "
				+ "  and rela.openId is not null "
				+ "  and rela.domainId not in (select distinct domainId from {} where classify=? and courseId=?) ",
				TABLE_NAME, TableNames.TABLE_USER, TableNames.TABLE_PURCHASE_LOG);
		return dao.query(sql, new Object[]{ CourseType.NORMAL.toString(), courseId}, (rs, index) -> {
			UserPO user = new UserPO();
			user.setOpenId(rs.getString(1));
			user.setNick(rs.getString(2));
			return user;
		});
	}
}
