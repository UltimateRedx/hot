package com.hotelpal.service.service;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class StatisticsService {
	public static final String TYPE_LESSON = "TYPE_LESSON";
	private static final long UPDATE_INTERVAL = 10 * 60 *1000L;

	@Resource
	private LessonStatisticsChannel lessonStatisticsChannel;

	public void increase(String type, Integer id, Integer domainId) {
		switch (type) {
			case TYPE_LESSON: lessonStatisticsChannel.increase(id, domainId);break;

			default: break;
		}
	}


	private abstract static class StatisticsChannel {
		protected abstract void increase(Integer id, Integer domainId);
		protected abstract void flushData();
	}

	@Component
	static class LessonStatisticsChannel extends StatisticsChannel{
		private static final Date UPDATE_TIME = new Date();
		//courseId, PV
		private static final Map<Integer, Integer> STATISTICS_MAP_PV = new HashMap<>();
		private static final Map<Integer, Set<Integer>> STATISTICS_MAP_UV = new HashMap<>();
		private static final Lock LOCK = new ReentrantLock();

//		Integer domainId  = SecurityContextHolder.getUserDomainId();
//			LOCK.lock();
//			STATISTICS_MAP_PV.putIfAbsent(lessonId, 0);
//			STATISTICS_MAP_PV.put(lessonId, STATISTICS_MAP_PV.get(lessonId) + 1);
//			STATISTICS_MAP_UV.putIfAbsent(lessonId, new HashSet<>());
//			STATISTICS_MAP_UV.get(lessonId).add(domainId);
//
//		Calendar cal = Calendar.getInstance();
//			if (cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
//			Calendar updateTime = Calendar.getInstance();
//			updateTime.setTime(UPDATE_TIME);
//			boolean anotherDay = cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE);
//			for (Integer lId : STATISTICS_MAP_PV.keySet()) {
//				Integer pv = STATISTICS_MAP_PV.get(lId);
//				Integer uv = STATISTICS_MAP_UV.get(lId).size();
//				commonService.logCourseLessonPVUV(lId, StatisticsType.LESSON_PV, pv);
//				STATISTICS_MAP_PV.put(lId, 0);
//				commonService.logCourseLessonPVUV(lId, StatisticsType.LESSON_UV, uv);
//				if (anotherDay) {
//					STATISTICS_MAP_UV.get(lId).clear();
//				}
//			}
//			UPDATE_TIME.setTime(new Date().getTime());
//		}
//			LOCK.unlock();


		@Override
		protected void increase(Integer id, Integer domainId) {

		}

		@Override
		protected void flushData() {

		}
	}
}
