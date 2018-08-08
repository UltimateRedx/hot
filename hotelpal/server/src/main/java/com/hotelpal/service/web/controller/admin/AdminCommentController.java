package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.po.CommentPO;
import com.hotelpal.service.common.so.CommentSO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/comment")
public class AdminCommentController {
	@Resource
	private CommentService commentService;
	
	@RequestMapping(value = "/getCommentPageList")
	@ResponseBody
	public PackVO<CommentPO> getPageList(@RequestBody CommentSO so) {
		List<CommentPO> poList = commentService.adminGetList(so);
		PackVO<CommentPO> res = new PackVO<>();
		res.setVoList(poList);
		res.setPageInfo(so);
		return res;
	}

	@RequestMapping(value = "/updateElite")
	@ResponseBody
	public PackVO<CommentPO> updateElite(@RequestBody CommentSO so) {
		commentService.updateElite(so);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/deleteComment")
	@ResponseBody
	public PackVO<CommentPO> deleteComment(Integer id) {
		commentService.deleteComment(id);
		return new PackVO<>();
	}
	
	@RequestMapping(value = "/replyComment")
	@ResponseBody
	public PackVO<CommentPO> replyComment(Integer replyToId, String content, Integer speakerId) {
		commentService.replyComment(replyToId, content, speakerId);
		return new PackVO<>();
	}
	
}
