package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.dto.response.CourseListResponse;
import com.hotelpal.service.common.dto.response.CourseResponse;
import com.hotelpal.service.common.po.BannerPO;
import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.ContentService;
import com.hotelpal.service.service.CourseService;
import com.hotelpal.service.service.StatisticsService;
import com.hotelpal.service.service.converter.ServiceConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping(value = "/course")
public class CourseController extends BaseController{


	@Resource
	private ServiceConverter serviceConverter;
	@Resource
	private ContentService contentService;
	@Resource
	private StatisticsService statisticsService;
	@Resource
	private CourseService courseService;
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
		CompletableFuture.runAsync(() -> statisticsService.increase(StatisticsService.TYPE_COURSE, courseId, SecurityContextHolder.getUserDomainId()));
		CompletableFuture.runAsync(() -> statisticsService.increase(StatisticsService.TYPE_COURSE, -1, SecurityContextHolder.getUserDomainId()));
		return new BaseDTO<>(serviceConverter.getCourse(courseId));
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/getMainBanner")
	public BaseDTO getMainBanner() {
		Map<String, List<BannerPO>> map = new HashMap<>();
		map.put("list", contentService.getMainBanner());
		return new BaseDTO<>(map);
	}

	/**
	 * 没有买过的，销售额在1W以上，销售量在300以上的课程
	 */
	@ResponseBody
	@RequestMapping(value = "/getRecommendCourse")
	public PackVO getRecommendCourse() {
		return new PackVO<>(courseService.getRecommendCourse());
	}

}
