package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CouponType;
import com.hotelpal.service.common.po.SysCouponPO;
import com.hotelpal.service.common.po.UserCouponPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.extra.UserCouponUserPO;
import com.hotelpal.service.common.so.UserCouponSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class UserCouponDao extends DomainMysqlBaseDao<UserCouponSO, UserCouponPO>{
	@Resource
	private SysCouponDao sysCouponDao;
	@Resource
	private UserRelaDao userRelaDao;
	
	private static final String TABLE_NAME = TableNames.TABLE_USER_COUPON;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,type,sysCouponId,used,value,validity").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<UserCouponPO> getPOClass() {
		return UserCouponPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, UserCouponSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getValidityFrom() != null) {
			buff.append(" AND ").append(alias).append("`validity` >= ?");
			params.add(so.getValidityFrom());
		}
		if (so.getValidityTo() != null) {
			buff.append(" AND ").append(alias).append("`validity` < ?");
			params.add(so.getValidityTo());
		}
		if (ArrayUtils.isNotNullEmpty(so.getIncludeType())) {
			buff.append(" AND ").append(alias).append("`type` IN(");
			StringBuilder sb = new StringBuilder();
			for (String type : so.getIncludeType()) {
				sb.append(",?");
				params.add(type);
			}
			buff.append(sb.toString().replaceFirst(",", "")).append(") ");
		}
		if (so.getValid() != null && so.getValid()) {
			buff.append(" AND ").append(alias).append("`validity` > sysdate()");
		}
	}
	
	public Integer getUserTotalCoupon() {
		String sql = "SELECT SUM(value) FROM " + TABLE_NAME + " WHERE type=? and domainId=? ";
		return dao.queryForObject(sql, new Object[]{CouponType.COURSE_REG_INVITE.toString(), SecurityContextHolder.getUserDomainId()}, Integer.class);
	}
	public Integer countBySysCouponId(Integer sysCouponId) {
		String sql = "SELECT COUNT(id) FROM " + TABLE_NAME +
				" WHERE sysCouponId=? ";
		return dao.queryForObject(sql, new Object[]{sysCouponId}, Integer.class);
	}
	
	/**
	 * 包含系统coupon的detail
	 */
	public List<UserCouponPO> getAllUserCoupon() {
		String sql = "SELECT " + this.getColumnAlias("uc") + "," +
				sysCouponDao.getColumnAlias("sc") +
				" FROM " + TABLE_NAME + " uc " +
				" LEFT JOIN " + sysCouponDao.getTableName() + " sc on uc.sysCouponId=sc.id " +
				" WHERE uc.type in (?,?,?)  AND uc.domainId=?" +
				" ORDER BY uc.validity,uc.value desc" ;
		return dao.query(sql, new Object[]{CouponType.COURSE.toString(), CouponType.COURSE_REG.toString(), CouponType.COURSE_REG_INVITE.toString(),
						SecurityContextHolder.getUserDomainId()}, (rs, idx) -> {
			UserCouponPO po = new UserCouponPO();
			int index = 1;
			po.setId(rs.getInt(index++));
			po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setDomainId(rs.getInt(index++));
			po.setType(rs.getString(index++));
			po.setSysCouponId(rs.getInt(index++));
			po.setUsed(rs.getString(index++));
			po.setValue(rs.getInt(index++));
			po.setValidity(DateUtils.toDate(rs.getString(index++), true));
			
			SysCouponPO detail = new SysCouponPO();
			detail.setId(rs.getInt(index++));
			detail.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			detail.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
			detail.setDeleted(rs.getString(index++));
			detail.setType(rs.getString(index++));
			detail.setName(rs.getString(index++));
			detail.setValue(rs.getInt(index++));
			detail.setTotal(rs.getInt(index++));
			detail.setValidityType(rs.getString(index++));
			detail.setValidity(DateUtils.toDate(rs.getString(index++), true));
			detail.setValidityDays(rs.getInt(index++));
			detail.setApply(rs.getString(index++));
			detail.setApplyToPrice(rs.getInt(index++));
			detail.setApplyToCourse(rs.getString(index++));
			po.setDetail(detail);
			return po;
		});
	}
	
	public List<UserCouponUserPO> getUserCouponAndUser(UserCouponSO so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		this.searchSO(buff, params, so, "uc");
		String sql = "SELECT " + this.getColumnAlias("uc") + "," + sysCouponDao.getColumnAlias("sc") + "," +
				userRelaDao.getColumnAlias("rela") +
				" FROM " + TABLE_NAME + " uc " +
				" LEFT JOIN " + sysCouponDao.getTableName() + " sc on uc.sysCouponId=sc.id " +
				" LEFT JOIN " + userRelaDao.getTableName() + " rela on uc.domainId=rela.domainId " +
				" WHERE 1=1 " + buff.toString();
		return dao.query(sql, params.toArray(), (rs, idx) -> {
			UserCouponUserPO po = new UserCouponUserPO();
			int index = 1;
			po.setId(rs.getInt(index++));
			po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setDomainId(rs.getInt(index++));
			po.setType(rs.getString(index++));
			po.setSysCouponId(rs.getInt(index++));
			po.setUsed(rs.getString(index++));
			po.setValue(rs.getInt(index++));
			po.setValidity(DateUtils.toDate(rs.getString(index++), true));

			SysCouponPO sysCoupon = new SysCouponPO();
			sysCoupon.setName(rs.getString("sc.name"));
			po.setDetail(sysCoupon);

			UserPO user = new UserPO();
			user.setOpenId(rs.getString("rela.openId"));
			po.setUser(user);
			return po;
		});
	}
	
	public Integer getFreeCourseLeft() {
		String sql = "SELECT COUNT(id) from " + TABLE_NAME +
			" where domainId=? and type=? and used=? and validity>sysdate()";
		return dao.queryForObject(sql, new Object[]{SecurityContextHolder.getUserDomainId(), CouponType.CARD.toString(),
			BoolStatus.N.toString()}, Integer.class);
	}

	public Map<Integer, Integer> getSysCouponSpent(List<Integer> sysCouponIdList) {
		if (ArrayUtils.isNullEmpty(sysCouponIdList)) {
			return Collections.emptyMap();
		}
		String[] arr = new String[sysCouponIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "SELECT sysCouponId, count(*) from " + TABLE_NAME + " where sysCouponId in(" +
				String.join(",", arr) + ") group by sysCouponId";
		Map<Integer, Integer> resMap = new HashMap<>();
		for (Integer id : sysCouponIdList) {
			resMap.put(id, 0);
		}
		dao.query(sql, sysCouponIdList.toArray(), rch -> {
			resMap.put(rch.getInt(1), rch.getInt(2));
		});
		return resMap;
	}
}
