package com.hotelpal.service.service;

import com.hotelpal.service.common.enums.StatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StatisticsService {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
	public static final String TYPE_LESSON = "TYPE_LESSON";
	public static final String TYPE_COURSE = "TYPE_COURSE";
	public static final String TYPE_SITE = "TYPE_SITE";
	Timer timer = new Timer();


	@Resource
	private LessonStatisticsChannel lessonStatisticsChannel;
	@Resource
	private CourseStatisticsChannel courseStatisticsChannel;
	@Resource
	private SiteStatisticsChannel siteStatisticsChannel;

	@PostConstruct
	public void runFlushJob() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				flushAll();
			}
		}, 0, 5 * 60 * 1000L);
	}

	@PreDestroy
	public void destroyFlushJob() {
		flushAll();
		timer.cancel();
		timer = null;
	}

	private void flushAll() {
		try {
			flush(TYPE_LESSON);
		} catch (Exception e) {
			logger.warn("flush statistics data(lesson) failed...", e);
		}
		try {
			flush(TYPE_COURSE);
		} catch (Exception e) {
			logger.warn("flush statistics data(course) failed...", e);
		}
		try {
			flush(TYPE_SITE);
		} catch (Exception e) {
			logger.warn("flush statistics data(site) failed...", e);
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

	public void flush(String type) {
		if (TYPE_LESSON.equalsIgnoreCase(type)) {
			lessonStatisticsChannel.flushData();
		} else if (TYPE_COURSE.equalsIgnoreCase(type)) {
			courseStatisticsChannel.flushData();
		} else if (TYPE_SITE.equalsIgnoreCase(type)) {
			siteStatisticsChannel.flushData();
		}
	}



}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Component
abstract class StatisticsChannel {
	@Resource
	protected CommonService commonService;
	protected static final long UPDATE_INTERVAL = 10 * 60 *1000L;

	protected abstract void increase(Integer id, Integer domainId);

	/**
	 * 固定频率写到数据库，单线程
	 */
	protected abstract void flushData();
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
	protected void flushData() {
		Calendar cal = Calendar.getInstance();
		if (cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
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
	protected void flushData() {
		Calendar cal = Calendar.getInstance();
		if (cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
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
	protected void flushData() {
		Calendar cal = Calendar.getInstance();
		if (cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
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
}