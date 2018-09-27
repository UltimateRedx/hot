package com.hotelpal.service.web.controller;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.dto.response.LessonListResponse;
import com.hotelpal.service.common.dto.response.LessonResponse;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.QNPersistMO;
import com.hotelpal.service.service.LessonService;
import com.hotelpal.service.service.StatisticsService;
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
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping(value = "/lesson")
public class LessonController extends BaseController{
	

	@Resource
	private ServiceConverter serviceConverter;
	@Resource
	private LessonService lessonService;
	@Resource
	private StatisticsService statisticsService;
	
	@RequestMapping(value = "/getLesson")
	@ResponseBody
	public BaseDTO<LessonResponse> getLesson(Integer lessonId) {
		LessonResponse lr = serviceConverter.getLesson(lessonId);
		boolean self = lr.getCourseId() == null || lr.getCourseId() <= 0;
		//统计单个课时 和 所有课时的
		CompletableFuture.runAsync(() -> statisticsService.increase(StatisticsService.TYPE_LESSON, lessonId, SecurityContextHolder.getUserDomainId()));
		CompletableFuture.runAsync(() -> statisticsService.increase(StatisticsService.TYPE_LESSON, self ? -2 : -1, SecurityContextHolder.getUserDomainId()));
		return new BaseDTO<>(lr);
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
