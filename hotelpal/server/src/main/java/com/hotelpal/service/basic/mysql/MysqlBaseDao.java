package com.hotelpal.service.basic.mysql;

import com.hotelpal.service.basic.BaseDao;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.DBMapMO;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.BasePO;
import com.hotelpal.service.common.so.BaseSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.utils.ValidationUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class MysqlBaseDao<S extends BaseSO, P extends BasePO> extends BaseDao<S, P> {

	/********************************SQL syntax********************************************************************************/
	protected static final String B_AND_B = " AND ";
	protected static final String B_OR_B = " OR ";
	private static final String CLASS = "class";
	protected static final String B_SELECT_B = " SELECT ";
	protected static final String B_FROM_B = " FROM ";
	protected static final String B_WHERE_B = " WHERE 1=1 ";
	protected static final String DOMAIN_ID = "domainId";




	@Resource
	protected JdbcTemplate dao;
	@Resource
	protected DozerBeanMapper dozerBeanMapper;
	
	/*********************************************PUBLIC METHODS****************************************************************************************/
	public List<P> getAll() {
		String sql = B_SELECT_B + getTableColumnString() + B_FROM_B + getTableName();
		return dao.query(sql, new Object[]{}, new RowMapperImpl<>(getPOClass()));
	}
	public void create(P po) {
		this.fillCreateInfo(po);
		String sql = this.getCreateSQL();
		KeyHolder kh = new GeneratedKeyHolder();
		dao.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				setCreateParams(ps, po);
				return ps;
			}
		}, kh);
		po.setId(kh.getKey().intValue());
	}
	public void createList(List<P> poList) {
		for (P po : poList) {
			this.fillCreateInfo(po);
		}
		List<String> columnList = getTableColumnList();
		List<String> subList = columnList.subList(1, columnList.size());
		String pre = " INSERT INTO " + getTableName() + " (" + String.join(",", subList) + ") VALUES (";
		StringBuilder buff = new StringBuilder();
		int size = subList.size();
		int i = 0;
		while(i++ < size) {
			buff.append(",?");
		}
		String sql = pre + buff.toString().replaceFirst(",", "") + ");";
		Set<String> excludeSet = new HashSet<>();
		excludeSet.add("id");
		dao.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				setPreparedStatementParams(preparedStatement, poList.get(i), excludeSet);
			}
			
			@Override
			public int getBatchSize() {
				return poList.size();
			}
		});
	}
	public void update(P po) {
		this.fillUpdateInfo(po);
		this.dao.update(this.getUpdateSQL(), preparedStatement -> {
			int index = setPreparedStatementParams(preparedStatement, po, new HashSet<>(Arrays.asList("id", "createTime",DOMAIN_ID)));
			preparedStatement.setInt(index++, po.getId());
		});
	}
	public P getById(Integer id) {
		if (id == null) return null;
		String sql = getGetByIdSQL();
		List<P> list =  dao.query(sql, new Object[]{id}, new RowMapperImpl<>(getPOClass()));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}
	public P getOne(S so) {
		List<P> list = this.getList(so);
		if (list.isEmpty()) return null;
		return list.get(0);
	}
	public void delete(Integer id) {
		if (id == null) return;
		String sql = getDeleteSQL();
		dao.update(sql, id);
	}
	public List<P> getByIdList(List<Integer> idList) {
		if (idList == null || idList.isEmpty()) {
			return Collections.emptyList();
		}
		String str = idList.stream().map(i -> "?").collect(Collectors.joining(","));
		String sql = B_SELECT_B + getTableColumnString() + B_FROM_B + "`" + getTableName() +
				"` WHERE `ID` IN (" + str + ")";
		return dao.query(sql, new RowMapperImpl<>(getPOClass()), idList.toArray());
	}
	public List<P> getList(S so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		searchSO(buff, params, so, "");
		String pre = B_SELECT_B + getTableColumnString() + B_FROM_B + "`" + getTableName() + "`" +B_WHERE_B;
		StringBuilder sql = new StringBuilder();
		searchSuffix(buff, params, so, null);
		sql.append(pre).append(buff);
		return dao.query(sql.toString(), new RowMapperImpl<>(getPOClass()), params.toArray());
	}

	public List<P> getNonPageList(S so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		searchSO(buff, params, so, "");
		String pre = "SELECT " + getTableColumnString() + " FROM `" + getTableName() + "` WHERE 1=1 ";
		return dao.query(pre + buff, new RowMapperImpl<>(getPOClass()), params.toArray());
	}

	public Integer count(S so) {
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		searchSO(buff, params, so, "");
		String pre = "SELECT COUNT(*) FROM `" + getTableName() + "` WHERE 1=1 ";
		String sql = pre + buff.toString();
		return dao.queryForObject(sql, params.toArray(), Integer.class);
	}
	public String getColumnAlias(String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? "`" + baseAlias + "`.": "";
		StringBuilder buff = new StringBuilder();
		List<String> columns = getTableColumnList();
		for (String col : columns) {
			buff.append(",").append(alias).append("`").append(col).append("`");
		}
		return buff.toString().replaceFirst(",", "");
	}
	public void searchSO(StringBuilder buff, List<Object> params, S so, String alias){
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(so.getClass());
		try {
			Map<String, Integer> columnMap = getTableColumnMap();
			for (PropertyDescriptor pd : descriptors) {
				String fieldName = pd.getName();
				if (fieldName.equalsIgnoreCase(CLASS) || fieldName.equalsIgnoreCase(DOMAIN_ID) || !columnMap.containsKey(fieldName)) continue;
				Object value = pd.getReadMethod().invoke(so);
				StringUtils.addSQLCondition(buff, params, fieldName, value, alias);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		searchBaseSO(buff, params, so, alias);
		searchNonColumnField(buff, params, so, alias);
	}
	/*********************************************ABSTRACT METHODS****************************************************************************************/
	protected abstract Class<P> getPOClass();
	protected abstract String getTableName();
	//字段列
	protected abstract List<String> getTableColumnList();
	//也存储字段列，供校验使用，查找是否存在相应列
	protected abstract void searchNonColumnField(StringBuilder buff, List<Object> params, S so, String alias);
	
	
	/*********************************************PROTECTED METHODS****************************************************************************************/
	protected static Map<String, Integer> tableListToMap(List<String> list) {
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0, j = list.size(); i < j; i++) {
			map.put(list.get(i), i);
		}
		return map;
	}
	protected String getTableColumnString() {
		return "`" + String.join("`,`", getTableColumnList()) + "`";
	}
	protected String getGetByIdSQL() {
		return "SELECT " + getTableColumnString() + " FROM `" + getTableName() + "` WHERE ID=?";
	}
	private String getDeleteSQL() {
		return "DELETE FROM `" + getTableName() + "` WHERE ID=?";
	}
	//此方法是供子类调用的
	protected void searchCommonSO(StringBuilder buff, List<Object> params, S so, String baseAlias) {
		String alias = ValidationUtils.isNotNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getCreateTimeFrom() != null) {
			buff.append(B_AND_B).append(alias).append("`createTime` >= ? ");
			params.add(so.getCreateTimeFrom());
		}
		if (so.getCreateTimeTo() != null) {
			buff.append(B_AND_B).append(alias).append("`createTime` < ? ");
			params.add(so.getCreateTimeTo());
		}
		if (so.getUpdateTimeFrom() != null) {
			buff.append(B_AND_B).append(alias).append("`updateTime` >= ? ");
			params.add(so.getUpdateTimeFrom());
		}
		if (so.getUpdateTimeTo() != null) {
			buff.append(B_AND_B).append(alias).append("`updateTime` < ? ");
			params.add(so.getUpdateTimeTo());
		}
		if (ArrayUtils.isNotNullEmpty(so.getIdList())) {
			buff.append(B_AND_B).append(alias).append("`id` IN (");
			StringBuilder sub = new StringBuilder();
			for (Integer id : so.getIdList()) {
				sub.append(",?");
				params.add(id);
			}
			buff.append(sub.deleteCharAt(0)).append(") ");
		}
	}
	protected void searchBaseSO(StringBuilder buff, List<Object> params, S so, String baseAlias) {
		this.searchCommonSO(buff, params, so, baseAlias);
	}
	
	protected void searchSuffix(StringBuilder buff, List<Object> params, S so, String baseAlias) {
		String alias = ValidationUtils.isNotNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (!StringUtils.isNullEmpty(so.getOrderBy())) {
			String[] orderBy = so.getOrderBy().split(" ");
			String order  = !StringUtils.isNullEmpty(so.getOrder()) && "desc".equalsIgnoreCase(so.getOrder()) ? "desc" : "asc";
			buff.append(" ORDER BY ").append(alias).append(orderBy[0]).append(" ").append(order);
		} else if (ArrayUtils.isNotNullEmpty(so.getOrderByList())) {
			buff.append(" ORDER BY ");
			StringBuilder sb = new StringBuilder();
			for (ValuePair<String, String> kv : so.getOrderByList()) {
				//防止sql注入，只用字符串的第一个字符
				if (!StringUtils.isNullEmpty(kv.getName())) {
					String[] orderBy = kv.getName().split(" ");
					String order  = !StringUtils.isNullEmpty(kv.getValue()) && "desc".equalsIgnoreCase(kv.getValue()) ? "desc" : "asc";
					sb.append(",").append(alias).append(orderBy[0]).append(" ").append(order);
				}
			}
			buff.append(sb.toString().replaceFirst(",", ""));
		}
		if (so.getFrom() != null && so.getLimit() != null) {
			buff.append(" LIMIT ?,? ");
			params.add(so.getFrom());
			params.add(so.getLimit());
		} else if (so.getCurrentPage() != null && so.getPageSize() != null) {
			buff.append(" LIMIT ?,? ");
			params.add((so.getCurrentPage() - 1) * so.getPageSize());
			params.add(so.getPageSize());
		}
		
	}
	protected void fillCreateInfo(P po) {
		Date date = new Date();
		po.setCreateTime(date);
		po.setUpdateTime(date);
	}
	protected void fillUpdateInfo(P po) {
		po.setUpdateTime(new Date());
	}
	/**
	 * 仅供DAO的实现类使用
	 */
	protected class RowMapperImpl<Q> implements RowMapper<Q>{
		Class<Q> clazz;
		List<String> columnList;
		Map<String, DBMapMO> map = new HashMap<>();
		public RowMapperImpl(Class<Q> clazz) {
			this.clazz = clazz;
			columnList = getTableColumnList();
			this.dismantleClass();
		}
		public RowMapperImpl(Class<Q> clazz, List<String> columnList) {
			this.clazz = clazz;
			this.columnList = columnList;
			this.dismantleClass();
		}
		private void dismantleClass() {
			PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
			for (PropertyDescriptor pd : descriptors) {
				String fieldName = pd.getName();
				if (fieldName.equalsIgnoreCase(CLASS)) continue;
				map.put(fieldName.toUpperCase(), new DBMapMO(pd.getPropertyType(), pd.getWriteMethod()));
			}
		}

		@Override
		public Q mapRow(ResultSet rs, int rowNum) throws SQLException {
			Q po;
			try {
				po = this.clazz.newInstance();
			} catch(InstantiationException | IllegalAccessException e) {
				throw new ServiceException(e);
			}
			int index = 1;
			for (String column : columnList) {
				String up = column.toUpperCase();
				if (map.containsKey(up)) {
					DBMapMO mo = map.get(up);
					try {
						mo.getWriteMethod().invoke(po, rs.getObject(index++));
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new ServiceException(e);
					}
				}
			}
			return po;
		}
	}


	/***
	 * 调用这个方法的sql中要使用getColumnAlias(baseAlias)方法或getTableColumnList()方法
	 */
	protected P mapPO (ResultSet rs, String baseAlias) {
		P po;
		try {
			po = getPOClass().newInstance();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		Map<String, DBMapMO> map = new HashMap<>();
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(getPOClass());
		for (PropertyDescriptor pd : descriptors) {
			String fieldName = pd.getName();
			if (fieldName.equalsIgnoreCase(CLASS)) continue;
			map.put(fieldName, new DBMapMO(pd.getPropertyType(), pd.getWriteMethod()));
		}
		String alias = StringUtils.isNullEmpty(baseAlias) ? "" : baseAlias + ".";
		for (String column : getTableColumnList()) {
			if (map.containsKey(column)) {
				DBMapMO mo = map.get(column);
				try {
					mo.getWriteMethod().invoke(po, rs.getObject(alias + column));
				} catch (IllegalAccessException | InvocationTargetException | SQLException e) {
					throw new ServiceException(e);
				}
			}
		}
		return po;
	}

	protected Map<String, Integer> getTableColumnMap() {
		return tableListToMap(getTableColumnList());
	};

	/*********************************************PRIVATE METHODS****************************************************************************************/
	private void setCreateParams(PreparedStatement preparedStatement, P po) throws SQLException {
		setPreparedStatementParams(preparedStatement, po, new HashSet<>());
		preparedStatement.setNull(1, Types.INTEGER);
	}
	private String getCreateSQL() {
		String pre = " INSERT INTO `" + getTableName() + "` (" + getTableColumnString() + ") VALUES(";
		StringBuilder buff = new StringBuilder();
		for (int i = 0, j = getTableColumnList().size(); i < j; i++) {
			buff.append(",?");
		}
		return pre + buff.toString().replaceFirst(",", "") + ")";
	}
	private String getUpdateSQL() {
		String pre = " UPDATE `" + getTableName() + "` SET ";
		List<String> columns = getTableColumnList();
		StringBuilder buff = new StringBuilder();
		for (String column : columns) {
			if (column.equalsIgnoreCase("createTime") || column.equalsIgnoreCase("id") || column.equalsIgnoreCase(DOMAIN_ID)) continue;
			buff.append(",`").append(column).append("`=?");
		}
		return pre + buff.toString().replaceFirst(",", "") + " WHERE ID=?";
	}
	/**
	 * If any exception throw here, check the po/dao column field.
	 */
	protected int setPreparedStatementParams(PreparedStatement preparedStatement, P po, Set<String> excludeColumns)
			throws SQLException{
		// put together field and value of po.
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(po.getClass());
		Map<String, Object> valueMap = new HashMap<>();
		try {
			for (PropertyDescriptor pd : descriptors) {
				String fieldName = pd.getName();
				if (fieldName.equalsIgnoreCase(CLASS)) continue;
				Object value = pd.getReadMethod().invoke(po);
				valueMap.put(fieldName, value);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		List<String> columnList = this.getTableColumnList();
		int index = 1;
		for (String column : columnList) {
			if (excludeColumns.contains(column)) continue;
			Object value = valueMap.get(column);
			if (value == null) {
				preparedStatement.setNull(index++, Types.VARCHAR);
			} else {
				if (value instanceof Integer)
					preparedStatement.setInt(index++, (int) value);
				else if (value instanceof Long)
					preparedStatement.setLong(index++, (long) value);
				else if (value instanceof String)
					preparedStatement.setString(index++, (String) value);
				else if (value instanceof Date)
					preparedStatement.setString(index++, DateUtils.getDateTimeString((Date) value));
				else if (value instanceof BigDecimal)
					preparedStatement.setBigDecimal(index++, (BigDecimal) value);
			}
		}
		return index;
	}
	
}
