package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.SpeakerPO;
import com.hotelpal.service.common.so.SpeakerSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.SpeakerService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/speaker")
public class SpeakerController extends BaseController{
	
	@Resource
	private SpeakerService speakerService;
	
	@RequestMapping(value = "/getList", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<SpeakerPO> getPageList(SpeakerSO so) {
		List<SpeakerPO> poList = speakerService.getSpeakerList(so);
		PackVO<SpeakerPO> res = new PackVO<>();
		res.setVoList(poList);
		res.setPageInfo(so);
		return res;
	}
	
	@RequestMapping(value = "/updateSpeaker", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<SpeakerPO> updateSpeaker(SpeakerSO so) {
		SpeakerPO po= speakerService.updateSpeaker(so);
		PackVO<SpeakerPO> res = new PackVO<>();
		res.setVo(po);
		return res;
	}
	
	@RequestMapping(value = "/deleteSpeaker", method = RequestMethod.POST)
	@ResponseBody
	public PackVO deleteSpeaker(Integer id) {
		if (id == null)
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		speakerService.deleteSpeaker(id);
		return new PackVO();
	}
	
	@RequestMapping(value = "/getAll", method = RequestMethod.POST)
	@ResponseBody
	public PackVO<SpeakerPO> getAll() {
		SpeakerSO so = new SpeakerSO();
		so.setLimit(Integer.MAX_VALUE);
		List<SpeakerPO> poList = speakerService.getSpeakerList(so);
		PackVO<SpeakerPO> res = new PackVO<>();
		res.setVoList(poList);
		return res;
	}
	
}
