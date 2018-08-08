package com.hotelpal.service.service.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class SpringTaskScheduler {
	private static final Logger logger = LoggerFactory.getLogger(SpringTaskScheduler.class);
	public static final String SCHEDULER_TYPE_LIVE_COURSE_OPENING = "SCHEDULER_TYPE_LIVE_COURSE_OPENING";
	public static final String SCHEDULER_TYPE_LIVE_COURSE_INVITE_INCOMPLETE_PRE = "SCHEDULER_TYPE_LIVE_COURSE_INVITE_INCOMPLETE_PRE";
	public static final String SCHEDULER_TYPE_LIVE_COURSE_INVITE_INCOMPLETE = "SCHEDULER_TYPE_LIVE_COURSE_INVITE_INCOMPLETE";


	private static final Map<String, ScheduledFuture> SCHEDULER = new ConcurrentHashMap<>();
	
	
	@Resource
	private ThreadPoolTaskScheduler taskScheduler;
	
	
	/**
	 * 按照uid强制刷新任务
	 */
	public void add(String uid, Runnable runnable, Date date) {
		taskScheduler.setThreadNamePrefix("springTaskScheduler");
		if (SCHEDULER.containsKey(uid)) {
			ScheduledFuture future = SCHEDULER.get(uid);
			future.cancel(true);
			future = taskScheduler.schedule(runnable, date);
			SCHEDULER.put(uid, future);
			logger.info("Canceled and renewed task: " + uid);
		} else {
			ScheduledFuture future = taskScheduler.schedule(runnable, date);
			SCHEDULER.put(uid, future);
			logger.info("Put task:" + uid);
		}
	}

	public void destroyAllTasks() {
		for ( ScheduledFuture future : SCHEDULER.values()) {
			future.cancel(true);
		}
		taskScheduler.shutdown();
	}
}
