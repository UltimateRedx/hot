package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.po.OrderPO;
import com.hotelpal.service.common.po.PurchaseLogPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.OrderSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class OrderDao extends DomainMysqlBaseDao<OrderSO, OrderPO>{
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private UserDao userDao;
	@Resource
	private CourseDao courseDao;
	@Resource
	private LiveCourseDao liveCourseDao;
	
	private static final String TABLE_NAME = TableNames.TABLE_ORDER;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,orderTradeNo,courseId,orderPrice,terminalIP,courseType,couponId,fee").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<OrderPO> getPOClass() {
		return OrderPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, OrderSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getCreateTimeFrom() != null) {
			buff.append(" AND ").append(alias).append(".createTime>= ? ");
			params.add(DateUtils.getDateString(so.getCreateTimeFrom()));
		}
		if (so.getCreateTimeTo() != null) {
			buff.append(" AND ").append(alias).append(".createTime < ? ");
			Date date = new Date(so.getCreateTimeTo().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
	}
	public Integer getCount(OrderSO so) {
		String purchaseLog = TableNames.TABLE_PURCHASE_LOG;
		String userRela = TableNames.TABLE_USER_RELA;
		String user = TableNames.TABLE_USER;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		searchSO(buff, params, so, "o");
		if (so.getPurchaseDateFrom() != null) {
			buff.append(" AND pl.createTime>=? ");
			params.add(DateUtils.getDateString(so.getPurchaseDateFrom()));
		}
		if (so.getPurchaseDateTo() != null) {
			buff.append(" AND pl.createTime<? ");
			Date date = new Date(so.getPurchaseDateTo().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
		if (!StringUtils.isNullEmpty(so.getSearchValue())) {
			buff.append(" AND (pl.orderTradeNo like concat('%', ?, '%') or u.nick like concat('%', ?, '%') or rela.phone like concat('%', ?, '%'))");
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
		}
		String sql = "SELECT COUNT(DISTINCT o.id) FROM " + TABLE_NAME + " o " +
				" LEFT JOIN " + purchaseLog + " pl on o.orderTradeNo=pl.orderTradeNo " +
				" LEFT JOIN " + userRela + " rela on o.domainId=rela.domainId " +
				" LEFT JOIN " + user + " u on rela.userId = u.id " +
				" WHERE 1=1 " + buff.toString();
		return dao.queryForObject(sql, params.toArray(), Integer.class);
	}
	public List<OrderPO> getOrderList(OrderSO so) {
		String liveCourse = TableNames.TABLE_LIVE_COURSE;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>(Arrays.asList(CourseType.NORMAL.toString(), CourseType.LIVE.toString()));
		searchSO(buff, params, so, "o");
		if (so.getPurchaseDateFrom() != null) {
			buff.append(" AND pl.createTime>=? ");
			params.add(DateUtils.getDateString(so.getPurchaseDateFrom()));
		}
		if (so.getPurchaseDateTo() != null) {
			buff.append(" AND pl.createTime<? ");
			Date date = new Date(so.getPurchaseDateTo().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
		if (!StringUtils.isNullEmpty(so.getSearchValue())) {
			buff.append(" AND (pl.orderTradeNo like concat('%', ?, '%') or u.nick like concat('%', ?, '%') or rela.phone like concat('%', ?, '%'))");
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
			params.add(so.getSearchValue());
		}
		searchSuffix(buff, params, so, "o");
		String sql = "SELECT " + getColumnAlias("o") + "," +
				purchaseLogDao.getColumnAlias("pl") + "," +
				userRelaDao.getColumnAlias("rela") + "," +
				userDao.getColumnAlias("u") + "," +
				courseDao.getColumnAlias("c") + "," +
				liveCourseDao.getColumnAlias("lc") +
				" FROM " + TABLE_NAME + " o " +
				" LEFT JOIN " + courseDao.getTableName() + " c on o.courseId = c.id AND o.courseType=? " +
				" LEFT JOIN " + liveCourse + " lc on o.courseId = lc.id AND o.courseType=? " +
				" LEFT JOIN " + purchaseLogDao.getTableName() + " pl on o.orderTradeNo=pl.orderTradeNo " +
				" LEFT JOIN " + userRelaDao.getTableName() + " rela on o.domainId=rela.domainId " +
				" LEFT JOIN " + userDao.getTableName() + " u on rela.userId = u.id " +
				" WHERE 1=1 " + buff.toString();
		return dao.query(sql, params.toArray(), (rs, rowNum) -> {
			OrderPO po = new OrderPO();
			po.setId(rs.getInt("o.id"));
			po.setCreateTime(DateUtils.toDate(rs.getString("o.createTime"), true));
			po.setOrderTradeNo(rs.getString("o.orderTradeNo"));
			String courseType = rs.getString("o.courseType");
			po.setCourseType(courseType);
			if (CourseType.NORMAL.toString().equalsIgnoreCase(courseType)) {
				po.setCourseTitle(rs.getString("c.title"));
			} else if(CourseType.LIVE.toString().equalsIgnoreCase(courseType)) {
				po.setCourseTitle(rs.getString("lc.title"));
			}
			po.setCouponId(rs.getInt("couponId"));
			po.setFee(rs.getInt("fee"));
			UserPO user = new UserPO();
			user.setNick(rs.getString("u.nick"));
			user.setPhone(rs.getString("rela.phone"));
			PurchaseLogPO purchaseLog = new PurchaseLogPO();
			purchaseLog.setPayMethod(null);
			if (!StringUtils.isNullEmpty(rs.getString("pl.id"))) {
				purchaseLog.setPayment(rs.getInt("pl.payment"));
				purchaseLog.setPayMethod(rs.getString("pl.payMethod"));
				purchaseLog.setCreateTime(DateUtils.toDate(rs.getString("pl.createTime"), true));
			}
			po.setUser(user);
			po.setPurchaseLog(purchaseLog);
			return po;
		});
	}

	public OrderPO getByOrderNo(String orderNo) {
		String sql = "SELECT " + getTableColumnString() + " FROM " + TABLE_NAME + " WHERE orderTradeNo=?";
		List<OrderPO> resList = dao.query(sql, new Object[]{orderNo}, new RowMapperImpl<>(OrderPO.class));
		if (!resList.isEmpty()) {
			return resList.get(0);
		}
		return null;
	}
}
