package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.so.CourseSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.common.vo.StatisticsCourseVO;
import com.hotelpal.service.service.CourseService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/course")
public class AdminCourseController extends BaseController{
	
	@Resource
	private CourseService courseService;
	
	@RequestMapping(value = "/getPageList")
	@ResponseBody
	public PackVO<StatisticsCourseVO> getPageList(CourseSO so, @RequestParam(required = false, defaultValue = "true") Boolean containsContent,
												  Date dateFrom, Date dateTo) {
		List<StatisticsCourseVO> poList = courseService.getCourseList(so, containsContent, dateFrom, dateTo);
		PackVO<StatisticsCourseVO> res = new PackVO<>();
		res.setVoList(poList);
		res.setPageInfo(so);
		return res;
	}
	
	@RequestMapping(value = "/updateCourse", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<Void> updateCourse(@RequestBody CourseSO so) {
		courseService.updateCourse(so);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/getCourse", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<CoursePO> getCourse(Integer id) {
		CoursePO po = courseService.getCourse(id, false);
		PackVO<CoursePO> pack = new PackVO<>();
		pack.setVo(po);
		return pack;
	}
	
	@RequestMapping(value = "/deleteCourse", method = RequestMethod.POST)
	@ResponseBody
	public PackVO deleteCourse(Integer id) {
		courseService.delete(id);
		return new PackVO();
	}
	
	@RequestMapping(value = "/newCourse")
	@ResponseBody
	public PackVO newCourse() {
		//TODO
		return null;
	}
}
