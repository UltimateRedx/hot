package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.MysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.enums.LessonType;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.StatisticsPO;
import com.hotelpal.service.common.so.StatisticsSO;
import com.hotelpal.service.common.vo.StatisticsVO;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class StatisticsDao extends MysqlBaseDao<StatisticsSO, StatisticsPO> {
	@Resource
	private LessonDao lessonDao;
	
	private static final String TABLE_NAME = TableNames.TABLE_STATISTICS;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"type,statisticsDate,value,statisticsId").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<StatisticsPO> getPOClass() {
		return StatisticsPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, StatisticsSO so, String alias) {
	
	}
	
	
	public void getSiteStatisticsData(StatisticsVO res, String from ,String to) {
		String sql = "select " +
				" sum(if(type='SITE_PV', value, 0)) pv," +
				" sum(if(type='SITE_UV', value, 0)) uv " +
				" from " + TABLE_NAME +
				" where statisticsDate>=? and statisticsDate<? ";
		dao.queryForObject(sql, new Object[]{from, to}, (rs, i) -> {
			int index = 1;
			res.setPv(rs.getLong(index++));
			res.setUv(rs.getLong(index));
			return null;
		});
	}
	
	public void getCourseStatisticsData(StatisticsVO res, String from ,String to) {
		String sql = "select " +
				" sum(if(s.type='COURSE_PV', s.value, 0)), " +
				" sum(if(s.type='COURSE_UV', s.value, 0)), " +
				" sum(if(s.type='LESSON_PV' and l.id is not null, s.value, 0)), " +
				" sum(if(s.type='LESSON_UV' and l.id is not null, s.value, 0)) " +
				" from " + TABLE_NAME + " s " +
				" left join " + lessonDao.getTableName() + " l on s.statisticsId = l.id and l.type=? " +
				" where statisticsDate >=? and statisticsDate<? ";
		dao.queryForObject(sql, new Object[]{LessonType.SELF.toString(), from, to}, (rs, i) -> {
			int index = 1;
			res.setNormalCoursePv(rs.getLong(index++));
			res.setNormalCourseUv(rs.getLong(index++));
			res.setSelfCoursePv(rs.getLong(index++));
			res.setSelfCourseUv(rs.getLong(index));
			return null;
		});
	}
	
	public Map<Integer, ValuePair<Long, Long>> getStatisticsByCourseList(List<Integer> courseIdList, String from, String to) {
		String pre = "select type, statisticsId, sum(value) from " + TABLE_NAME +
				" where type in(?,?) and statisticsDate>=? and statisticsDate<? and statisticsId in (";
		String[] arr = new String[courseIdList.size()];
		Arrays.fill(arr, "?");
		String suf = ") group by type, statisticsId";
		Map<Integer, ValuePair<Long, Long>> res = new HashMap<>();
		for (Integer id : courseIdList) {
			res.put(id, new ValuePair<>(0L, 0L));
		}
		dao.query(pre + String.join(",", arr) + suf,
				ArrayUtils.addAll(new Object[]{StatisticsType.COURSE_PV.toString(), StatisticsType.COURSE_UV.toString(), from, to}, courseIdList.toArray()),
				rch -> {
					String type = rch.getString("type");
					if (type.equalsIgnoreCase(StatisticsType.COURSE_PV.toString())) {
						res.get(rch.getInt(2)).setName(rch.getLong(3));
					} else {
						res.get(rch.getInt(2)).setValue(rch.getLong(3));
					}
		});
		return res;
	}
	
	public Map<Integer, ValuePair<Long, Long>> getStatisticsByLessonList(List<Integer> lessonIdList, String from, String to) {
		String pre = "select type, statisticsId, sum(value) from " + TABLE_NAME +
				" where type in(?,?) and createTime>=? and createTime<? and statisticsId in (";
		String[] arr = new String[lessonIdList.size()];
		Arrays.fill(arr, "?");
		String suf = ") group by type, statisticsId";
		Map<Integer, ValuePair<Long, Long>> res = new HashMap<>();
		for (Integer id : lessonIdList) {
			res.put(id, new ValuePair<>(0L, 0L));
		}
		dao.query(pre + String.join(",", arr) + suf,
				ArrayUtils.addAll(new Object[]{StatisticsType.LESSON_PV.toString(), StatisticsType.LESSON_UV.toString(), from, to}, lessonIdList.toArray()),
				rch -> {
					String type = rch.getString("type");
					if (type.equalsIgnoreCase(StatisticsType.LESSON_PV.toString())) {
						res.get(rch.getInt(2)).setName(rch.getLong(3));
					} else {
						res.get(rch.getInt(2)).setValue(rch.getLong(3));
					}
				});
		return res;
	}
}
