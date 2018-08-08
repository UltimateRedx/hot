package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.CommentDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.EnumHelper;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.CommentPO;
import com.hotelpal.service.common.so.CommentSO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Component
@Transactional
public class CommentService {
	@Resource
	private CommentDao commentDao;
	
	public List<CommentPO> adminGetList(CommentSO so) {
		so.setDeleted(null);
		so.setTotalCount(commentDao.count(so));
		return commentDao.getList(so);
	}
	
	public void updateElite(CommentSO so) {
		CommentPO po = commentDao.getById(so.getId());
		if (po == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if (!EnumHelper.isIn(so.getElite(), new BoolStatus[]{BoolStatus.N, BoolStatus.Y})) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		po.setElite(so.getElite());
		commentDao.update(po);
	}
	
	public void deleteComment(Integer id) {
		CommentPO po = commentDao.getById(id);
		if (po == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		commentDao.delete(id);
	}
	
	public void replyComment(Integer replyToId, String content, Integer speakerId) {
		CommentPO ori = commentDao.getById(replyToId);
		if (ori == null) throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		CommentPO po = new CommentPO();
		po.setContent(content);
		po.setReplyToId(replyToId);
		po.setLessonId(ori.getLessonId());
		po.setSpeaker(BoolStatus.Y.toString());
		po.setZanCount(0);
		SecurityContextHolder.setTargetDomain(speakerId);
		commentDao.create(po);
	}
}
