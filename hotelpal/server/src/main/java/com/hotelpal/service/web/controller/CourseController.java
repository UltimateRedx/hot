package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.dto.response.CourseListResponse;
import com.hotelpal.service.common.dto.response.CourseResponse;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.common.po.BannerPO;
import com.hotelpal.service.service.CommonService;
import com.hotelpal.service.service.ContentService;
import com.hotelpal.service.service.converter.ServiceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@RequestMapping(value = "/course")
public class CourseController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

	private static final Date UPDATE_TIME = new Date();
	private static final long UPDATE_INTERVAL = 10 * 60 *1000;
	//courseId, PV
	private static final Map<Integer, Integer> STATISTICS_MAP_PV = new HashMap<>();
	private static final Map<Integer, Set<Integer>> STATISTICS_MAP_UV = new HashMap<>();
	private static final Lock LOCK = new ReentrantLock();
	
	
	@Resource
	private ServiceConverter serviceConverter;
	@Resource
	private ContentService contentService;
	@Resource
	private CommonService commonService;
	/**
	 * 获取课程列表
	 */
	@RequestMapping(value = "/getCourseList")
	@ResponseBody
	public BaseDTO<CourseListResponse> getCourseList(@RequestParam(required = false, defaultValue = "0") Integer start,
	                                                 @RequestParam(required = false, defaultValue = "30") Integer limit,
	                                                 @RequestParam(required = false, defaultValue = "courseOrder") String orderBy,
	                                                 @RequestParam(required = false, defaultValue = "asc") String order) {
		CourseListResponse res = serviceConverter.getCourseList(start, limit, orderBy, order);
		return new BaseDTO<>(res);
	}

	@RequestMapping(value = "/getCourse")
	@ResponseBody
	public BaseDTO<CourseResponse> getCourse(Integer courseId) {
		CompletableFuture.runAsync(() -> {
			try {
				Integer domainId = SecurityContextHolder.getUserDomainId();
				LOCK.lock();
				STATISTICS_MAP_PV.putIfAbsent(courseId, 0);
				STATISTICS_MAP_PV.put(courseId, STATISTICS_MAP_PV.get(courseId) + 1);
				STATISTICS_MAP_UV.putIfAbsent(courseId, new HashSet<>());
				STATISTICS_MAP_UV.get(courseId).add(domainId);
				
				Calendar cal = Calendar.getInstance();
				if (cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
					Calendar updateTime = Calendar.getInstance();
					updateTime.setTime(UPDATE_TIME);
					boolean anotherDay = cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE);
					for (Integer cId : STATISTICS_MAP_PV.keySet()) {
						Integer pv = STATISTICS_MAP_PV.get(courseId);
						Integer uv = STATISTICS_MAP_UV.get(courseId).size();
						commonService.logCourseLessonPVUV(courseId, StatisticsType.COURSE_PV, pv);
						STATISTICS_MAP_PV.put(courseId, 0);
						commonService.logCourseLessonPVUV(courseId, StatisticsType.COURSE_UV, uv);
						if (anotherDay) {
							STATISTICS_MAP_UV.get(courseId).clear();
						}
					}
					UPDATE_TIME.setTime(new Date().getTime());
				}
				LOCK.unlock();
			}catch (Exception e) {
				logger.error("Statistics error: ", e);
			}
			
		});
		return new BaseDTO<>(serviceConverter.getCourse(courseId));
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/getMainBanner")
	public BaseDTO getMainBanner() {
		Map<String, List<BannerPO>> map = new HashMap<>();
		map.put("list", contentService.getMainBanner());
		return new BaseDTO<>(map);
	}


}
