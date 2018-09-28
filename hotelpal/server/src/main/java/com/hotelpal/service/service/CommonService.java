package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.StatisticsDao;
import com.hotelpal.service.basic.mysql.dao.UserDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.common.po.StatisticsPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.StatisticsSO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Component
@Transactional
public class CommonService {
	private static final String DEFAULT_STATISTICS_SEARCH_ORDER_BY = "statisticsDate";
	@Resource
	private UserDao userDao;
	@Resource
	private StatisticsDao statisticsDao;
	@Resource
	private ContextService contextService;
	
	public void putAuth(String openId) {
		contextService.initContext(openId);
		UserPO user = userDao.getById(SecurityContextHolder.getUserId());
		user.setLastLoginTime(new Date());
		userDao.update(user);
	}
	
	public void logSitePV(int pv) {
		if (pv == 0) return;
		StatisticsSO so = new StatisticsSO();
		so.setOrder("desc");
		so.setOrderBy(DEFAULT_STATISTICS_SEARCH_ORDER_BY);
		so.setType(StatisticsType.SITE_PV.toString());
		StatisticsPO po = statisticsDao.getOne(so);
		if (po == null) {
			po = new StatisticsPO();
			po.setValue(0);
			po.setType(StatisticsType.SITE_PV.toString());
			po.setStatisticsDate(new Date());
			po.setValue(po.getValue() + pv);
			statisticsDao.create(po);
			return;
		}
		po.setValue(po.getValue() + pv);
		statisticsDao.update(po);
		Calendar now = Calendar.getInstance();
		Calendar statisticsDate = Calendar.getInstance();
		statisticsDate.setTime(po.getStatisticsDate());
		if (now.get(Calendar.DATE) != statisticsDate.get(Calendar.DATE)) {
			StatisticsPO newDay = new StatisticsPO();
			newDay.setValue(0);
			newDay.setStatisticsDate(new Date());
			newDay.setType(StatisticsType.SITE_PV.toString());
			statisticsDao.create(newDay);
		}
	}
	
	public void logSiteUV(int uv) {
		if (uv == 0) return;
		StatisticsSO so = new StatisticsSO();
		so.setOrder("desc");
		so.setOrderBy(DEFAULT_STATISTICS_SEARCH_ORDER_BY);
		so.setType(StatisticsType.SITE_UV.toString());
		StatisticsPO po = statisticsDao.getOne(so);
		if (po == null) {
			po = new StatisticsPO();
			po.setType(StatisticsType.SITE_UV.toString());
			po.setStatisticsDate(new Date());
			po.setValue(uv);
			statisticsDao.create(  po);
			return;
		}
		po.setValue(uv);
		statisticsDao.update(po);
		Calendar now = Calendar.getInstance();
		Calendar statisticsDate = Calendar.getInstance();
		statisticsDate.setTime(po.getStatisticsDate());
		if (now.get(Calendar.DATE) != statisticsDate.get(Calendar.DATE)) {
			StatisticsPO newDay = new StatisticsPO();
			newDay.setValue(0);
			newDay.setStatisticsDate(new Date());
			newDay.setType(StatisticsType.SITE_UV.toString());
			statisticsDao.create(newDay);
		}
	}
	
	public void logCourseLessonPVUV (Integer courseId, StatisticsType type, int value) {
		if (value == 0) return;
		StatisticsSO so = new StatisticsSO();
		so.setOrder("desc");
		so.setOrderBy(DEFAULT_STATISTICS_SEARCH_ORDER_BY);
		so.setType(type.toString());
		so.setStatisticsId(courseId);
		StatisticsPO po = statisticsDao.getOne(so);
		if (po == null) {
			po = new StatisticsPO();
			po.setValue(value);
			po.setType(type.toString());
			po.setStatisticsDate(new Date());
			po.setStatisticsId(courseId);
			statisticsDao.create(po);
			return;
		}
		po.setValue(StatisticsType.COURSE_PV.equals(type) || StatisticsType.LESSON_PV.equals(type) ? (po.getValue() + value) : value);
		statisticsDao.update(po);
		Calendar now = Calendar.getInstance();
		Calendar statisticsDate = Calendar.getInstance();
		statisticsDate.setTime(po.getStatisticsDate());
		if (now.get(Calendar.DATE) != statisticsDate.get(Calendar.DATE)) {
			StatisticsPO newDay = new StatisticsPO();
			newDay.setValue(0);
			newDay.setStatisticsDate(new Date());
			newDay.setType(type.toString());
			newDay.setStatisticsId(courseId);
			statisticsDao.create(newDay);
		}
	}
	
}
