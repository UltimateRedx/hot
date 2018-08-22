package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.common.enums.LiveEnrollStatus;
import com.hotelpal.service.common.enums.LiveEnrollType;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.live.LiveEnrollPO;
import com.hotelpal.service.common.so.live.LiveEnrollSO;
import com.hotelpal.service.common.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LiveEnrollDao extends DomainMysqlBaseDao<LiveEnrollSO, LiveEnrollPO>{
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private LiveCourseInviteLogDao liveCourseInviteLogDao;
	private static final String TABLE_NAME = TableNames.TABLE_LIVE_ENROLL;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,liveCourseId,status,enrollType").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<LiveEnrollPO> getPOClass() {
		return LiveEnrollPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, LiveEnrollSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (CollectionUtils.isNotEmpty(so.getStatusList())) {
			buff.append(" AND ").append(alias).append("`status` IN (");
			StringBuilder sub = new StringBuilder();
			for (Integer id : so.getIdList()) {
				sub.append(",?");
				params.add(id);
			}
			buff.append(sub.deleteCharAt(0)).append(") ");
		}
	}
	
	public List<Integer> getEnrolledDomainIdList(Integer courseId) {
		String sql = "SELECT DISTINCT domainId FROM " + TABLE_NAME +
		 " WHERE liveCourseId=? AND `status`=? ";
		 return dao.queryForList(sql, new Object[]{courseId, LiveEnrollStatus.ENROLLED.toString()}, Integer.class);
	}
	
//	public List<Integer> getInvitingDomainIdList(Integer courseId) {
//		String sql = "SELECT domainId FROM " + TABLE_NAME +
//		 " WHERE liveCourseId=? AND `status`=? ";
//		 return dao.queryForList(sql, new Object[]{courseId, LiveEnrollStatus.INVITING.toString()}, Integer.class);
//	}

	public List<Map<String, Object>> getInvitingList(Integer courseId) {
		String sql = "select " +
				" rela.openId, " +
				" count(distinct log.invitedDomainId) count " +
				" from " + TABLE_NAME + " le " +
				" left join " + userRelaDao.getTableName() + " rela on le.domainId = rela.domainId " +
				" left join " + liveCourseInviteLogDao.getTableName() + " log on log.liveCourseId = le.liveCourseId AND le.domainId=log.domainId " +
				" where le.liveCourseId=? and le.status=? " +
				" AND rela.openId IS NOT NULL " +
				" AND NOT EXISTS (select * from "+ TABLE_NAME + " where liveCourseId=? and status=? and domainId=le.domainid) " +
				"  group by le.domainId ";
		return dao.queryForList(sql, courseId, LiveEnrollStatus.INVITING.toString(), courseId, LiveEnrollStatus.ENROLLED.toString());
	}
	
	public Integer getEnrolledOnlineCount(Integer liveCourseId) {
		String sql = "select count(distinct domainId) from " + TableNames.TABLE_ONLINE_LOG + " where domainId  in " +
				" (select domainId from " + TABLE_NAME + " where liveCourseId=? and status=?)";
		return dao.queryForObject(sql, new Object[]{liveCourseId, LiveEnrollStatus.ENROLLED.toString()}, Integer.class);
	}
	
	public Integer getTryInviteCount(Integer liveCourseId) {
		String sql = "SELECT SUM(count) from " +
				"(select IFNULL(count(*),0) count from " + TABLE_NAME + " m where enrollType=? and liveCourseId=?   " +
				" AND not exists ( select * from cc_live_enroll where liveCourseId=? and domainId=m.domainId and enrollType not in(?) ) " +
				" group by domainId)t ";
		return dao.queryForObject(sql, new Object[]{LiveEnrollType.INVITE.toString(), liveCourseId, liveCourseId, LiveEnrollType.INVITE.toString()}, Integer.class);
	}

	/**
	 * 返回正在邀请中(排除已经付费报名)的domainId
	 */
	public List<ValuePair<Integer, Integer>> getInvitingDomainIdList(Integer liveCourseId) {
		String sql = StringUtils.format("select le.domainId, count(ll.invitedDomainId)" +
				" from {} le " +
				" left join {} ll on le.domainId= ll.domainId and ll.liveCourseId=le.liveCourseId" +
				" where le.liveCourseId=? and le.`status`=? " +
				" and le.domainId not in(select domainId from {} where `status`=? and liveCourseId=?)" +
				" group by le.domainId",
				TABLE_NAME, TableNames.TABLE_LIVE_COURSE_INVITED_LOG, TABLE_NAME);
		return dao.query(sql, new Object[]{liveCourseId, LiveEnrollStatus.INVITING.toString(), LiveEnrollStatus.ENROLLED.toString(), liveCourseId}, (rs, index) -> {
			ValuePair<Integer, Integer> res = new ValuePair<>();
			res.setName(rs.getInt(1));
			res.setValue(rs.getInt(2));
			return res;
		});
	}

	public void updateToEnrolled(Integer liveCourseId, Integer domainId) {
		String sql = "update " + TABLE_NAME + " set `status`=?,updateTime=sysdate() where liveCourseId=? and domainId=? and enrollType=?";
		dao.update(sql, new Object[]{LiveEnrollStatus.ENROLLED.toString(), liveCourseId, domainId, LiveEnrollType.INVITE.toString()});
	}
}
