package com.hotelpal.service.web.controller.liveCourseController.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.so.live.LiveCourseSO;
import com.hotelpal.service.common.vo.AdminLiveCourseVO;
import com.hotelpal.service.common.vo.LiveCourseCurveVO;
import com.hotelpal.service.common.vo.LiveCourseStatisticsVO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.live.LiveContentService;
import com.hotelpal.service.service.live.LiveCourseService;
import com.hotelpal.service.web.controller.BaseController;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/liveCourse")
public class AdminLiveCourseController extends BaseController {
	private static final String DOMAIN = PropertyHolder.getProperty("DOMAIN_NAME_HTTP");
	@Resource
	private LiveCourseService liveCourseService;
	@Resource
	private LiveContentService liveContentService;
	
	@RequestMapping(value = "/updateLiveCourse")
	@ResponseBody
	public PackVO<Void> updateLiveCourse(LiveCourseSO so) {
		liveCourseService.updateLiveCourse(so);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/removeLiveCourse")
	@ResponseBody
	public PackVO<Void> removeLiveCourse(Integer id) {
		liveCourseService.removeLiveCourse(id);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/getLiveCoursePageList")
	@ResponseBody
	public PackVO getPageList(@RequestBody LiveCourseSO so) {
		List<AdminLiveCourseVO> voList = liveCourseService.getPageList(so);
		PackVO<AdminLiveCourseVO> res = new PackVO<>();
		res.setVoList(voList);
		res.setPageInfo(so);
		return res;
	}
	
	@RequestMapping(value = "/startLive")
	@ResponseBody
	public PackVO<Void> startLive(Integer courseId) {
		liveCourseService.startLive(courseId);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/terminateLive")
	@ResponseBody
	public PackVO<Void> terminateLive(Integer courseId) {
		liveCourseService.terminateLive(courseId);
		return new PackVO<>();
	}
	
	
	@RequestMapping(value = "/getCourseStatistics")
	@ResponseBody
	public PackVO<LiveCourseStatisticsVO> LiveCourseStatisticsVO(Integer courseId) {
		PackVO<LiveCourseStatisticsVO> pack = new PackVO<>();
		pack.setVo(liveContentService.getStatisticsData(courseId));
		return pack;
	}
	
	@RequestMapping(value = "/getCourseStatisticsCurve")
	@ResponseBody
	public PackVO<LiveCourseCurveVO> getCourseStatisticsCurve(Integer courseId) {
		PackVO<LiveCourseCurveVO> pack = new PackVO<>();
		pack.setVo(liveContentService.getStatisticsCurve(courseId));
		return pack;
	}
	
	@RequestMapping(value = "/updateCourseImage")
	@ResponseBody
	public PackVO<String> updateCourseImage(Integer courseId, @RequestParam List<String> imgList) {
		if (CollectionUtils.isEmpty(imgList)) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		String liveImg = liveCourseService.updateCourseImage(courseId, imgList);
		PackVO<String> pack = new PackVO<>();
		pack.setVo(liveImg);
		return pack;
	}
	
	@RequestMapping(value = "/getLiveImgList")
	@ResponseBody
	public PackVO<String> getLiveImgList(Integer courseId) {
		PackVO<String> pack = new PackVO<>();
		pack.setVoList(liveCourseService.getLiveImg(courseId));
		pack.setVo(DOMAIN + "/admin/#/" + liveCourseService.getCourse(courseId).getLiveImg());
		return pack;
	}

	/**
	 * 配置显示的报名人数、观看人数、总观看人数等的基数
	 */
	@RequestMapping(value = "/configureBaseLine")
	@ResponseBody
	public PackVO configureBaseLine(@RequestParam Integer courseId,
									@RequestParam String type,
									@RequestParam(required = false, defaultValue = "0") Integer baseLine) {
		liveCourseService.configureDisplayBaseLine(courseId, type, baseLine);
		return new PackVO();
	}
}
