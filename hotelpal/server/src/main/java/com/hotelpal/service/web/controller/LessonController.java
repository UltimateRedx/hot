package com.hotelpal.service.web.controller;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.dto.response.LessonListResponse;
import com.hotelpal.service.common.dto.response.LessonResponse;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.QNPersistMO;
import com.hotelpal.service.service.CommonService;
import com.hotelpal.service.service.LessonService;
import com.hotelpal.service.service.converter.ServiceConverter;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@RequestMapping(value = "/lesson")
public class LessonController extends BaseController{
	
	private static final Date UPDATE_TIME = new Date();
	private static final long UPDATE_INTERVAL = 10 * 60 *1000;
	//courseId, PV
	private static final Map<Integer, Integer> STATISTICS_MAP_PV = new HashMap<>();
	private static final Map<Integer, Set<Integer>> STATISTICS_MAP_UV = new HashMap<>();
	private static final Lock LOCK = new ReentrantLock();
	
	@Resource
	private ServiceConverter serviceConverter;
	@Resource
	private LessonService lessonService;
	@Resource
	private CommonService commonService;
	
	@RequestMapping(value = "/getLesson")
	@ResponseBody
	public BaseDTO<LessonResponse> getLesson(Integer lessonId) {
		CompletableFuture.runAsync(() -> {
			Integer domainId  = SecurityContextHolder.getUserDomainId();
			LOCK.lock();
			STATISTICS_MAP_PV.putIfAbsent(lessonId, 0);
			STATISTICS_MAP_PV.put(lessonId, STATISTICS_MAP_PV.get(lessonId) + 1);
			STATISTICS_MAP_UV.putIfAbsent(lessonId, new HashSet<>());
			STATISTICS_MAP_UV.get(lessonId).add(domainId);
			
			Calendar cal = Calendar.getInstance();
			if (cal.getTime().getTime() >= UPDATE_TIME.getTime() + UPDATE_INTERVAL) {
				Calendar updateTime = Calendar.getInstance();
				updateTime.setTime(UPDATE_TIME);
				boolean anotherDay = cal.get(Calendar.DATE) != updateTime.get(Calendar.DATE);
				for (Integer lId : STATISTICS_MAP_PV.keySet()) {
					Integer pv = STATISTICS_MAP_PV.get(lId);
					Integer uv = STATISTICS_MAP_UV.get(lId).size();
					commonService.logCourseLessonPVUV(lId, StatisticsType.LESSON_PV, pv);
					STATISTICS_MAP_PV.put(lId, 0);
					commonService.logCourseLessonPVUV(lId, StatisticsType.LESSON_UV, uv);
					if (anotherDay) {
						STATISTICS_MAP_UV.get(lId).clear();
					}
				}
				UPDATE_TIME.setTime(new Date().getTime());
			}
			LOCK.unlock();
			
		});
		return new BaseDTO<>(serviceConverter.getLesson(lessonId));
	}
	
	@RequestMapping(value = "/getInternalLessonList")
	@ResponseBody
	public BaseDTO<LessonListResponse> getInternalLessonList(@RequestParam(required = false, defaultValue = "0") Integer start,
	                                                         @RequestParam(required = false, defaultValue = "4") Integer n,
	                                                         @RequestParam(required = false, defaultValue = "desc") String order
	                                                         ) {
		return new BaseDTO<>(serviceConverter.getSelfLessonList(start, n, order));
	}
	
	@RequestMapping(value = "/rqnpr")
	@ResponseBody
	public void receiveQiNiuPersistentResult(HttpServletRequest request) {
		try  {
			InputStream is = request.getInputStream();
			String param = IOUtils.toString(is, "UTF-8");
			QNPersistMO result = JSON.parseObject(param, QNPersistMO.class);
			lessonService.receivePersistNotification(result);
		}catch (IOException e) {
			throw new ServiceException(e);
		}
	}
}
