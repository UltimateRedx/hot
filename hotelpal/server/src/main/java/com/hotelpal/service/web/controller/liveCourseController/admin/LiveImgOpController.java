package com.hotelpal.service.web.controller.liveCourseController.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.live.LiveCourseService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/live/op")
public class LiveImgOpController extends BaseController {

	@Resource
	private LiveCourseService liveCourseService;

	@RequestMapping(value = "/getImgList/{value}")
	@ResponseBody
	public PackVO<String> getImgList(@PathVariable String value) {
		if (StringUtils.isNullEmpty(value)) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		String courseIdS = value.substring(0, value.indexOf('@'));
		Integer courseId = Integer.parseInt(courseIdS);
		
		PackVO<String> res = new PackVO<>();
		res.setVoList(liveCourseService.getLiveImg(courseId));
		return res;
	}
}
