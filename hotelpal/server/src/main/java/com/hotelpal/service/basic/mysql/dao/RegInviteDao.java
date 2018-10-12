package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.context.CommonParams;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.po.RegInvitePO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.RegInviteSO;
import com.hotelpal.service.common.utils.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class RegInviteDao extends DomainMysqlBaseDao<RegInviteSO, RegInvitePO> {
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private UserDao userDao;
	private static final String TABLE_NAME = TableNames.TABLE_REG_INVITE;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,invitedDomainId,batch,couponCollected").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<RegInvitePO> getPOClass() {
		return RegInvitePO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, RegInviteSO so, String alias) {
	
	}
	
	public List<RegInvitePO> getRegInvitedUser() {
		String sql = "SELECT " + this.getColumnAlias("ri") +
				", u.headImg " +
				" FROM " + TABLE_NAME + " ri " +
				" INNER JOIN " + userRelaDao.getTableName() + " rela on ri.invitedDomainId=rela.domainId " +
				" INNER JOIN " + userDao.getTableName() + " u ON rela.userId=u.id " +
				" WHERE ri.domainId=? " +
				" ORDER BY ri.`batch` desc ";
		return dao.query(sql, new Object[]{SecurityContextHolder.getUserDomainId()}, (rs, rowIndex) -> {
			RegInvitePO po = new RegInvitePO();
			int index = 1;
			po.setId(rs.getInt(index++));
			po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setDomainId(rs.getInt(index++));
			po.setInvitedDomainId(rs.getInt(index++));
			po.setBatch(rs.getString(index++));
			po.setCouponCollected(rs.getString(index++));
			UserPO user = new UserPO();
			user.setHeadImg(rs.getString(index++));
			po.setInvitedUser(user);
			return po;
		});
	}
	
	public List<RegInvitePO> getListByBatch(String batch) {
		String sql = "SELECT " + this.getTableColumnString() +
				" FROM " + TABLE_NAME + " WHERE domainId=? and `batch`=?  ";
		return dao.query(sql, new Object[]{SecurityContextHolder.getUserDomainId(), batch}, new RowMapperImpl<>(RegInvitePO.class));
	}
	
	public String getLatestBatch() {
		String sql = "SELECT `batch` FROM " + TABLE_NAME +
				" WHERE domainId=? AND `batch` = " +
				" (SELECT MAX(`batch`) FROM " + TABLE_NAME +
				" WHERE domainId=?) ";
		List<String> list = dao.queryForList(sql, new Object[]{SecurityContextHolder.getTargetDomain(), SecurityContextHolder.getTargetDomain()}, String.class);
		if (list.size() == 0 || list.size() == CommonParams.INVITE_REG_REQUIRE) {
			return DateUtils.getDateTimeString(new Date()).replaceAll("\\D+", "") + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 18);
		}
		return list.get(0);
	}
}
