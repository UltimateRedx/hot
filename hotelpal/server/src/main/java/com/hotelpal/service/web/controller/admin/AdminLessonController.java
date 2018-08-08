package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.so.LessonSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.common.vo.StatisticsLessonVO;
import com.hotelpal.service.service.CourseService;
import com.hotelpal.service.service.LessonService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/lesson")
public class AdminLessonController extends BaseController{
	
	@Resource
	private LessonService lessonService;
	@Resource
	private CourseService courseService;
	
	@RequestMapping(value = "/getPageList")
	@ResponseBody
	public PackVO<StatisticsLessonVO> getPageList(LessonSO so) {
		List<StatisticsLessonVO> poList = lessonService.getLessonList(so, true);
		PackVO<StatisticsLessonVO> res = new PackVO<>();
		res.setVoList(poList);
		res.setPageInfo(so);
		return res;
	}
	
	@RequestMapping(value = "/updateLesson", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<Void> updateCourse(@RequestBody LessonSO so) {
		lessonService.updateLesson(so);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/deleteLesson", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<Void> deleteLesson(Integer id) {
		lessonService.deleteLesson(id);
		return new PackVO<>();
	}
}
