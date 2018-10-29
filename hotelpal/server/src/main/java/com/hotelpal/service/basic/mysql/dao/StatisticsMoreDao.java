package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.common.po.StatisticsMorePO;
import com.hotelpal.service.common.so.StatisticsMoreSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
public class StatisticsMoreDao extends MysqlBaseDao<StatisticsMoreSO, StatisticsMorePO> {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsMoreDao.class);
	private static final String TABLE_NAME = TableNames.TABLE_STATISTICS_MORE;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"type,statisticsDate,statisticsId,domainId").split(",")));
	@Override
	protected Class<StatisticsMorePO> getPOClass() {
		return StatisticsMorePO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, StatisticsMoreSO so, String alias) {
		//
	}

	/**
	 * 重新设置指定数据
	 * @param type 目前存放uv
	 * @param date 当天日期
	 * @param data {statisticsId: [domainId]}
	 */
	public void setDateSet(StatisticsType type, Date date, Map<Integer, Set<Integer>> data) {
		if (data.isEmpty()) {
			logger.warn("Statistics data: No data to store.");
		}
		Objects.requireNonNull(date);
		String clearSql = StringUtils.format(
				"delete from {} where statisticsDate=? and type=?", TABLE_NAME);
		dao.update(clearSql, DateUtils.getDateString(date), type);
		String sql = StringUtils.format(
				"insert into {} (createTime, updateTime, type, statisticsDate, statisticsId, domainId) "
						+ " values (sysdate(), sysdate(), ?, ?, ?, ?)", TABLE_NAME);
		int size = data.values().size();
		List<List<Integer>> params = new ArrayList<>(size);
		for (Map.Entry<Integer, Set<Integer>> en : data.entrySet()) {
			for (Integer domainId : en.getValue()) {
				List<Integer> param = new ArrayList<>(2);
				param.add(en.getKey(), domainId);
				params.add(param);
			}
		}

		dao.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, type.toString());
				ps.setString(2, DateUtils.getDateString(date));
				ps.setInt(3, params.get(i).get(0));
				ps.setInt(4, params.get(i).get(1));
			}

			@Override
			public int getBatchSize() {
				return size;
			}
		});
	}

	/**
	 * @return map 不保证并发性，需要再转换
	 */
	public Map<Integer, Set<Integer>> getDateSet(StatisticsType type, Date date) {
		Objects.requireNonNull(date);
		String sql = StringUtils.format("select * from {} where `type`=? and statisticsDate=? ", TABLE_NAME);
		List<StatisticsMorePO> list = dao.query(sql, new Object[]{type.toString(), DateUtils.getDateString(date)}, new RowMapperImpl<>(StatisticsMorePO.class));
		Map<Integer, Set<Integer>> map = new HashMap<>();
		for (StatisticsMorePO po : list) {
			map.putIfAbsent(po.getStatisticsId(), new HashSet<>());
			map.get(po.getStatisticsId()).add(po.getDomainId());
		}
		return map;
	}
}
