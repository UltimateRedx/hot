package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveEnrollDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.enums.LiveEnrollStatus;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.PurchaseLogPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.PurchaseLogSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.DailySalesVO;
import com.hotelpal.service.common.vo.PurchaseVO;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class PurchaseLogDao extends DomainMysqlBaseDao<PurchaseLogSO, PurchaseLogPO> {
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private UserDao userDao;
	@Resource
	private LiveEnrollDao liveEnrollDao;
	@Resource
	private LiveCourseDao liveCourseDao;

	private static final String TABLE_NAME = TableNames.TABLE_PURCHASE_LOG;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,orderTradeNo,courseId,payment,originalPrice,payMethod,wxConfirm,wxPrice,classify,couponId").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<PurchaseLogPO> getPOClass() {
		return PurchaseLogPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, PurchaseLogSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getPurchaseDateFrom() != null) {
			buff.append(" AND ").append(alias).append("createDate>=? ");
			params.add(DateUtils.getDateString(so.getPurchaseDateFrom()));
		}
		if (so.getPurchaseDateTo() != null) {
			buff.append(" AND ").append(alias).append("createDate<? ");
			Date date = new Date(so.getPurchaseDateTo().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
	}
	
	public List<String> getSubscriberOpenId(Integer courseId) {
		String sql = "SELECT rela.openId FROM " +
			TABLE_NAME + " pl " +
			" INNER JOIN " + userRelaDao.getTableName() + " rela on pl.domainId=rela.domainId " +
			" WHERE pl.courseId=? AND classify=? ";
		return dao.queryForList(sql, new Object[]{courseId, CourseType.NORMAL.toString()}, String.class);
	}

	public PurchaseLogPO getByOrderNo(String orderNo) {
		String sql = "SELECT " + getTableColumnString() + " FROM " + TABLE_NAME + " WHERE orderTradeNo=?";
		List<PurchaseLogPO> resList = dao.query(sql, new Object[]{orderNo}, new RowMapperImpl<>(PurchaseLogPO.class));
		if (!resList.isEmpty()) {
			return resList.get(0);
		}
		return null;
	}

	public List<String> getPurchasedNormalCourseUserOpenId(Integer courseId) {
		String sql = "select distinct rela.openId " +
				" from " + TABLE_NAME + " pl " +
				" left join " + userRelaDao.getTableName() + " rela on pl.domainId=rela.domainId " +
				" left join " + userDao.getTableName() + " u on rela.userId = u.id " +
				" left join " + TableNames.TABLE_LESSON + "lesson on pl.courseId = lesson.courseId and lesson.deleted<>'Y' and lesson.onSale='Y' " +
				" left join " + TableNames.TABLE_LISTEN_LOG + " ll on ll.lessonId = lesson.id and pl.domainId=ll.domainId " +
				" where pl.classify=? and pl.courseId=? and date(u.lastLoginTime)=? " +
				" AND ll.recordLen<lesson.audioLen/2 ";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		return dao.queryForList(sql, new Object[]{CourseType.NORMAL.toString(), courseId, DateUtils.getDateString(cal)}, String.class);
	}

	public boolean recordExists(CourseType courseType, Integer courseId, Integer domainId) {
		String sql = "SELECT count(*) from " + TABLE_NAME + " where domainId=? and courseId=? and classify=? ";
		return dao.queryForObject(sql, new Object[]{domainId, courseId, courseType.toString()}, Integer.class) > 0;
	}
	
	public DailySalesVO getDailySales() {
		String sql = "select " +
				"  DATE_FORMAT(createDate, '%m-%d'), " +
				"  IFNULL(sum(payment),0)/100 " +
				" from " + TABLE_NAME +
				" where createTime>=date_sub(sysdate(), interval 30 day) " +
				" group by createDate ";
		Map<String, Long> rolling = new LinkedHashMap<>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		DateUtils.clearTime(cal);
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 1);
		DateUtils.clearTime(today);
		while (cal.before(today)) {
			rolling.put(DateUtils.getMMDDString(cal), 0L);
			cal.add(Calendar.DATE, 1);
		}
		dao.query(sql, rch -> {
			rolling.put(rch.getString(1), rch.getLong(2));
		});

		List<DailySalesVO.DailyItem> resList = new ArrayList<>(rolling.size());
		for (Map.Entry<String, Long> en : rolling.entrySet()) {
			DailySalesVO.DailyItem item = new DailySalesVO.DailyItem();
			item.setDate(en.getKey());
			item.setSales(en.getValue());
			resList.add(item);
		}
		DailySalesVO vo = new DailySalesVO();
		vo.setDays(resList);
		return vo;
	}
	
	public Map<Integer, ValuePair<Integer, Long>> getSaleStatisticsByCourseId(String courseType, List<Integer> courseIdList, String from, String to) {
		String sql = "select courseId, count(id), sum(payment) from " + TABLE_NAME + " where classify=? and " +
				" createTime>=? and createTime<? and courseId in (";
		String[] arr = new String[courseIdList.size()];
		Arrays.fill(arr, "?");
		String suf = ") group by classify, courseId";
		Map<Integer, ValuePair<Integer, Long>> res = new HashMap<>();
		for (Integer id : courseIdList) {
			res.put(id, new ValuePair<>(0, 0L));
		}
		dao.query(sql + String.join(",", arr) + suf,
				ArrayUtils.addAll(new Object[]{courseType, from, to}, courseIdList.toArray()), rs -> {
			res.get(rs.getInt(1)).setName(rs.getInt(2));
			res.get(rs.getInt(1)).setValue(rs.getLong(3));
		});
		return res;
	}

	public Map<Integer, ValuePair<Integer, Integer>> getCourseCountFee(List<Integer> domainIdList) {
		String[] arr = new String[domainIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "select domainId, ifnull(count(case classify when '" + CourseType.NORMAL.toString() + "' then courseId else null end), 0), " +
				" ifnull(sum(payment), 0) from " + TABLE_NAME + " where " +
				" domainId in(" + String.join(",", arr) + ") group by domainId";
		Map<Integer, ValuePair<Integer, Integer>> res = new HashMap<>();
		for (Integer domainId : domainIdList) {
			res.put(domainId, new ValuePair<>(0, 0));
		}
		dao.query(sql, domainIdList.toArray(), rch -> {
			res.get(rch.getInt("domainId")).setName(rch.getInt(2));
			res.get(rch.getInt("domainId")).setValue(rch.getInt(3));
		});
		return res;
	}
	
	/**
	 * 某个直播课程里面预约成功并购买了关联课程的人数
	 */
	public Integer getRelaCoursePurchaseTimes(Integer liveCourseId) {
		String sql = "SELECT count(distinct pl.domainId) " +
				" FROM " + liveEnrollDao.getTableName() + " le " +
				" inner join " + liveCourseDao.getTableName() + " lc on le.liveCourseId=lc.id " +
				" left join " + TABLE_NAME + " pl on le.domainid = pl.domainId and pl.classify=? and pl.courseId=lc.relaCourseId " +
				" where le.`status`=? AND le.liveCourseId=?";
		return dao.queryForObject(sql, new Object[]{CourseType.NORMAL.toString(), LiveEnrollStatus.ENROLLED.toString(), liveCourseId}, Integer.class);
	}

	public Integer getUserTotalPayment() {
		String sql = "SELECT IFNULL(SUM(payment),0) FROM " + TABLE_NAME + " where domainId=?";
		return dao.queryForObject(sql, new Object[]{SecurityContextHolder.getUserDomainId()}, Integer.class);
	}

	public List<PurchaseVO> getPurchaseOrderList(PurchaseLogSO so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>(Arrays.asList(CourseType.NORMAL.toString(), CourseType.LIVE.toString()));
		searchSO(buff, params, so, "pl");
		if (!StringUtils.isNullEmpty(so.getSearchValue())) {
			buff.append(" AND (pl.orderTradeNo like concat('%', ?, '%') or u.nick like concat('%', ?, '%') or rela.phone like concat('%', ?, '%'))");
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
		}
		String commonSql = StringUtils.format(" from {} pl" +
						" inner join {} rela on pl.domainId=rela.domainId" +
						" inner join {} u on rela.userId=u.id" +
						" left join {} c on pl.courseId=c.id and pl.classify=?" +
						" left join {} lc on pl.courseId=lc.id and pl.classify=?" +
						" left join {} uc on pl.couponId=uc.id" +
						" where 1=1 ",
				TABLE_NAME, TableNames.TABLE_USER_RELA, TableNames.TABLE_USER, TableNames.TABLE_COURSE, TableNames.TABLE_LIVE_COURSE, TableNames.TABLE_USER_COUPON);
		String count = "SELECT count(*) " + commonSql + buff.toString();
		so.setTotalCount(dao.queryForObject(count, params.toArray(), Integer.class));

		searchSuffix(buff, params, so, "pl");
		String sql = "SELECT " + getColumnAlias("pl") + ",rela.phone,u.nick,c.title,lc.title, uc.`value` " +
				commonSql + buff.toString();
		return dao.query(sql, params.toArray(), (rs, i) -> {
			PurchaseLogPO po = mapPO(rs, "pl");
			PurchaseVO vo = dozerBeanMapper.map(po, PurchaseVO.class);
			UserPO user = new UserPO();
			vo.setUser(user);
			user.setNick(rs.getString("u.nick"));
			user.setPhone(rs.getString("rela.phone"));
			vo.setCourseTitle(rs.getString(CourseType.NORMAL.toString().equalsIgnoreCase(rs.getString("pl.classify")) ? "c.title" : "lc.title"));
			vo.setCouponValue(rs.getInt("uc.value"));
			return vo;
		});
	}

	public Map<Integer, Boolean> recordExists(CourseType courseType, Integer courseId, List<Integer> domainIdList) {
		if (domainIdList == null || domainIdList.size() == 0) {
			return Collections.emptyMap();
		}
		Map<Integer, Boolean> res = new HashMap<>();
		for (Integer domainId : domainIdList) {
			res.put(domainId, false);
		}
		String[] arr = new String[domainIdList.size()];
		Arrays.fill(arr, "?");
		String sql = "SELECT domainId, count(*) from " + TABLE_NAME + " where courseId=? and classify=? AND domainId IN ("
				+ String.join(",", arr) + ") group by domainId";
		dao.query(sql, ArrayUtils.addAll(new Object[]{courseId, courseType.toString()}, domainIdList.toArray()), rch -> {
			res.put(rch.getInt(1), rch.getInt(2) > 0);
		});
		return res;
	}


	/**
	 * 销量300以上，销售额1W以上
	 */
	public List<Integer> getHotCourseList() {
		String sql = StringUtils.format(
				"select courseId from (" +
						" select courseId, sum(PAYMENT) sum, count(distinct domainId) count " +
						" from {} " +
						" where classify=? " +
						" GROUP BY courseId" +
						" ) t where sum > ? and count > ?", TABLE_NAME);
		return dao.queryForList(sql, new Object[]{CourseType.NORMAL.toString(), 10000 * 100, 300}, Integer.class);
	}

	public Set<Integer> getAllPurchasedCourseId() {
		String sql = StringUtils.format("select distinct courseId from {} where domainId=? and classify=?", TABLE_NAME);
		return new HashSet<>(dao.queryForList(sql, new Object[]{SecurityContextHolder.getUserDomainId(), CourseType.NORMAL.toString()}, Integer.class));
	}
}
