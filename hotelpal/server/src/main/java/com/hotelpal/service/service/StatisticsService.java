package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.StatisticsMoreDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * pv是在数据库直接加上去的，UV需要在类加载之后重新读取一次
 */
@Component
public class StatisticsService {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
	public static final String TYPE_LESSON = "TYPE_LESSON";
	public static final String TYPE_COURSE = "TYPE_COURSE";
	public static final String TYPE_SITE = "TYPE_SITE";
	Timer timer;


	@Resource
	private LessonStatisticsChannel lessonStatisticsChannel;
	@Resource
	private CourseStatisticsChannel courseStatisticsChannel;
	@Resource
	private SiteStatisticsChannel siteStatisticsChannel;
	private boolean statisticsOn = BoolStatus.Y.toString().equalsIgnoreCase(PropertyHolder.getProperty("context.hotelpal.statisticsService"));

	@PostConstruct
	public void runFlushJob() {
		if (!statisticsOn) return;
		restoreAll();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					flushAll(false);
				}
			}, 0, 5 * 60 * 1000L);
	}

	@PreDestroy
	public void destroyFlushJob() {
		if (!statisticsOn) return;
		flushAll(true);
		timer.cancel();
		timer = null;

		//uv保存到临时表
		dumpAll();

	}

	/**
	 * @param force 表示忽略时间间隔，不等待UPDATE_INTERVAL
	 */
	private void flushAll(boolean force) {
		try {
			flush(TYPE_LESSON, force);
		} catch (Exception e) {
			logger.warn("flush statistics data(lesson) failed...", e);
		}
		try {
			flush(TYPE_COURSE, force);
		} catch (Exception e) {
			logger.warn("flush statistics data(course) failed...", e);
		}
		try {
			flush(TYPE_SITE, force);
		} catch (Exception e) {
			logger.warn("flush statistics data(site) failed...", e);
		}
	}

	private void dumpAll() {
		try {
			siteStatisticsChannel.dump();
		} catch (Exception e) {
			logger.error("dump statistics data(site) failed...", e);
		}
		try {
			courseStatisticsChannel.dump();
		} catch (Exception e) {
			logger.error("dump statistics data(course) failed...", e);
		}
		try {
			lessonStatisticsChannel.dump();
		} catch (Exception e) {
			logger.error("dump statistics data(lesson) failed...", e);
		}
	}

	private void restoreAll() {
		try {
			siteStatisticsChannel.restore();
		} catch (Exception e) {
			logger.error("restore statistics data(site) failed...", e);
		}
		try {
			courseStatisticsChannel.restore();
		} catch (Exception e) {
			logger.error("restore statistics data(course) failed...", e);
		}
		try {
			lessonStatisticsChannel.restore();
		} catch (Exception e) {
			logger.error("restore statistics data(lesson) failed...", e);
		}
	}


	public void increase(String type, Integer id, Integer domainId) {
		if (TYPE_LESSON.equalsIgnoreCase(type)) {
			lessonStatisticsChannel.increase(id, domainId);
		} else if (TYPE_COURSE.equalsIgnoreCase(type)) {
			courseStatisticsChannel.increase(id, domainId);
		} else if (TYPE_SITE.equalsIgnoreCase(type)) {
			siteStatisticsChannel.increase(id, domainId);
		}
	}

	private void flush(String type, boolean force) {
		if (TYPE_LESSON.equalsIgnoreCase(type)) {
			lessonStatisticsChannel.flushData(force);
		} else if (TYPE_COURSE.equalsIgnoreCase(type)) {
			courseStatisticsChannel.flushData(force);
		} else if (TYPE_SITE.equalsIgnoreCase(type)) {
			siteStatisticsChannel.flushData(force);
		}
	}



}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Component
abstract class StatisticsChannel {
	@Resource
	protected CommonService commonService;
	@Resource
	protected StatisticsMoreDao statisticsMoreDao;
	protected static final long UPDATE_INTERVAL = 10 * 60 *1000L;

	protected abstract void increase(Integer id, Integer domainId);

	/**
	 * 固定频率写到数据库，单线程
	 */
	protected abstract void flushData(boolean force);

	protected abstract void dump();
	protected abstract void restore();
}

/**
 * 使用ConcurrentHashMap 缓存数据，表示不能有null key。 所有lesson的pv、uv的id记录成负值
 */
@Component
class LessonStatisticsChannel extends StatisticsChannel{
	private static final Date UPDATE_TIME = new Date();
	//courseId, PV
	private static final Map<Integer, AtomicInteger> STATISTICS_MAP_PV = new ConcurrentHashMap<>();
	private static final Map<Integer, Set<Integer>> STATISTICS_MAP_UV = new ConcurrentHashMap<>();

	@Override
	protected void increase(Integer id, Integer domainId) {
		STATISTICS_MAP_PV.putIfAbsent(id, new AtomicInteger());
		STATISTICS_MAP_PV.get(id).incrementAndGet();
		STATISTICS_MAP_UV.putIfAbsent(id, ConcurrentHashMap.newKeySet());
		STATISTICS_MAP_UV.get(id).add(domainId);
	}

	@Override
	protected void flushData(boolean force) {
		Calendar cal = Calendar.getInstance();
		if (force || cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
			Calendar updateTime = Calendar.getInstance();
			updateTime.setTime(UPDATE_TIME);
			boolean anotherDay = cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE);
			for (Map.Entry<Integer, AtomicInteger> en : STATISTICS_MAP_PV.entrySet()) {
				commonService.logCourseLessonPVUV(en.getKey(), StatisticsType.LESSON_PV, en.getValue().get());
				STATISTICS_MAP_PV.get(en.getKey()).set(0);
				commonService.logCourseLessonPVUV(en.getKey(), StatisticsType.LESSON_UV, STATISTICS_MAP_UV.get(en.getKey()).size());
				if (anotherDay) {
					STATISTICS_MAP_UV.get(en.getKey()).clear();
				}
			}
			UPDATE_TIME.setTime(new Date().getTime());
		}
	}

	@Override
	protected void dump() {
		statisticsMoreDao.setDateSet(StatisticsType.LESSON_UV, new Date(), STATISTICS_MAP_UV);
	}

	@Override
	protected void restore() {
		Map<Integer, Set<Integer>> map = statisticsMoreDao.getDateSet(StatisticsType.LESSON_UV, new Date());
		for (Map.Entry<Integer, Set<Integer>> en : map.entrySet()) {
			STATISTICS_MAP_UV.putIfAbsent(en.getKey(), ConcurrentHashMap.newKeySet());
			STATISTICS_MAP_UV.get(en.getKey()).addAll(en.getValue());
		}
	}
}


@Component
class CourseStatisticsChannel extends StatisticsChannel{
	private static final Date UPDATE_TIME = new Date();
	//courseId, PV
	private static final Map<Integer, AtomicInteger> STATISTICS_MAP_PV = new ConcurrentHashMap<>();
	private static final Map<Integer, Set<Integer>> STATISTICS_MAP_UV = new ConcurrentHashMap<>();

	@Override
	protected void increase(Integer id, Integer domainId) {
		STATISTICS_MAP_PV.putIfAbsent(id, new AtomicInteger());
		STATISTICS_MAP_PV.get(id).incrementAndGet();
		STATISTICS_MAP_UV.putIfAbsent(id, ConcurrentHashMap.newKeySet());
		STATISTICS_MAP_UV.get(id).add(domainId);
	}

	@Override
	protected void flushData(boolean force) {
		Calendar cal = Calendar.getInstance();
		if (force || cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
			Calendar updateTime = Calendar.getInstance();
			updateTime.setTime(UPDATE_TIME);
			boolean anotherDay = cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE);
			for (Map.Entry<Integer, AtomicInteger> en : STATISTICS_MAP_PV.entrySet()) {
				commonService.logCourseLessonPVUV(en.getKey(), StatisticsType.COURSE_PV, en.getValue().get());
				STATISTICS_MAP_PV.get(en.getKey()).set(0);
				commonService.logCourseLessonPVUV(en.getKey(), StatisticsType.COURSE_UV, STATISTICS_MAP_UV.get(en.getKey()).size());
				if (anotherDay) {
					STATISTICS_MAP_UV.get(en.getKey()).clear();
				}
			}
			UPDATE_TIME.setTime(new Date().getTime());
		}
	}
	@Override
	protected void dump() {
		statisticsMoreDao.setDateSet(StatisticsType.COURSE_UV, new Date(), STATISTICS_MAP_UV);
	}

	@Override
	protected void restore() {
		Map<Integer, Set<Integer>> map = statisticsMoreDao.getDateSet(StatisticsType.COURSE_UV, new Date());
		for (Map.Entry<Integer, Set<Integer>> en : map.entrySet()) {
			STATISTICS_MAP_UV.putIfAbsent(en.getKey(), ConcurrentHashMap.newKeySet());
			STATISTICS_MAP_UV.get(en.getKey()).addAll(en.getValue());
		}
	}
}


@Component
class SiteStatisticsChannel extends StatisticsChannel{
	private static final Date UPDATE_TIME = new Date();
	private static final AtomicInteger PV = new AtomicInteger(0);
	private static final Set<Integer> USER_TOKEN_SET = ConcurrentHashMap.newKeySet();

	@Override
	protected void increase(Integer id, Integer domainId) {
		PV.incrementAndGet();
		USER_TOKEN_SET.add(domainId);
	}

	@Override
	protected void flushData(boolean force) {
		Calendar cal = Calendar.getInstance();
		if (force || cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
			commonService.logSitePV(PV.get());
			PV.set(0);
			commonService.logSiteUV(USER_TOKEN_SET.size());
			Calendar updateTime = Calendar.getInstance();
			updateTime.setTime(UPDATE_TIME);
			if (cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE)) {
				USER_TOKEN_SET.clear();
			}
			UPDATE_TIME.setTime(new Date().getTime());
		}
	}

	@Override
	protected void dump() {
		Map<Integer, Set<Integer>> map = new HashMap<>();
		map.put(-1, USER_TOKEN_SET);
		statisticsMoreDao.setDateSet(StatisticsType.SITE_UV, new Date(), map);
	}

	@Override
	protected void restore() {
		Map<Integer, Set<Integer>> map = statisticsMoreDao.getDateSet(StatisticsType.SITE_UV, new Date());
		for (Set<Integer> s : map.values())
			USER_TOKEN_SET.addAll(s);
	}
}