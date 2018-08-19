package com.hotelpal.service.service.live;

import com.hotelpal.service.basic.mysql.dao.PurchaseLogDao;
import com.hotelpal.service.basic.mysql.dao.live.*;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.PurchaseLogPO;
import com.hotelpal.service.common.po.live.AssistantMessagePO;
import com.hotelpal.service.common.po.live.ChatLogPO;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.po.live.OnlineSumPO;
import com.hotelpal.service.common.so.PurchaseLogSO;
import com.hotelpal.service.common.so.live.AssistantMessageSO;
import com.hotelpal.service.common.so.live.LiveCourseSO;
import com.hotelpal.service.common.so.live.OnlineSumSO;
import com.hotelpal.service.common.vo.LiveCourseCurveVO;
import com.hotelpal.service.common.vo.LiveCourseStatisticsVO;
import com.hotelpal.service.service.live.netty.ServerHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Component
@Transactional
public class LiveContentService {

	@Resource
	private AssistantMessageDao assistantMessageDao;
	@Resource
	private LiveCourseDao liveCourseDao;
	@Resource
	private ChatLogDao chatLogDao;
//	@Resource
//	private LiveChatService liveChatService;
	@Resource
	private LiveEnrollDao liveEnrollDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private OnlineLogDao onlineLogDao;
	@Resource
	private OnlineSumDao onlineSumDao;
	@Resource
	private ServerHelper serverHelper;

	public Integer sendMsg(Integer courseId, String msg) {
		LiveCourseSO so = new LiveCourseSO();
		so.setId(courseId);
		if (liveCourseDao.count(so) <= 0) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		AssistantMessagePO po = new AssistantMessagePO();
		po.setLiveCourseId(courseId);
		po.setMsg(msg);
		assistantMessageDao.create(po);
		return po.getId();
	}
	
	public List<AssistantMessagePO> getMsgList(Integer courseId) {
		AssistantMessageSO so = new AssistantMessageSO();
		so.setPageSize(Integer.MAX_VALUE);
		so.setLiveCourseId(courseId);
		return assistantMessageDao.getList(so);
	}
	
	public Integer removeMsg(Integer msgId) {
		AssistantMessagePO msg = assistantMessageDao.getById(msgId);
		if (msg == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		assistantMessageDao.delete(msgId);
		serverHelper.assistantMessageRemove(msg.getLiveCourseId(), msgId);
		return msg.getLiveCourseId();
	}
	
	public void blockUser(Integer msgId) {
		ChatLogPO chat = chatLogDao.getById(msgId);
		if (chat == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		chat.setBlocked(BoolStatus.Y.toString());
		chatLogDao.update(chat);
		serverHelper.blockUser(chat.getLiveCourseId(), chat.getDomainId());
	}
	
	public LiveCourseStatisticsVO getStatisticsData(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		
		Integer tryInviteCount = liveEnrollDao.getTryInviteCount(courseId);
		PurchaseLogSO pso = new PurchaseLogSO();
		pso.setClassify(CourseType.LIVE.toString());
		pso.setCourseId(courseId);
		List<PurchaseLogPO> pList = purchaseLogDao.getList(pso);
		Integer totalFee = 0;
		for (PurchaseLogPO p : pList) {
			totalFee += p.getPayment();
		}
		//获取报名的人里面购买的人数
		Integer relaCoursePurchaseTimes = purchaseLogDao.getRelaCoursePurchaseTimes(courseId);
		LiveCourseStatisticsVO vo = new LiveCourseStatisticsVO();
		vo.setFreeEnrollTimes(course.getFreeEnrolledTimes());
		vo.setPurchaseEnrollTimes(course.getPurchasedTimes());
		vo.setTotalPeople(course.getTotalPeople());
		
		vo.setTryFreeEnrollCount(tryInviteCount);
		vo.setPurchasedFee(totalFee);
		vo.setRelaCoursePurchaseTimes(relaCoursePurchaseTimes);
		Integer onlineCount = onlineLogDao.getDistinctDomainIdCount(courseId);
		vo.setTotalOnline(onlineCount);
		vo.setVipEnrolledTimes(course.getVipEnrolledTimes());
		vo.setEnrolledOnlineCount(liveEnrollDao.getEnrolledOnlineCount(courseId));
		return vo;
	}
	
	public LiveCourseCurveVO getStatisticsCurve(Integer courseId) {
		OnlineSumSO sso = new OnlineSumSO();
		sso.setLiveCourseId(courseId);
		List<OnlineSumPO> sumList = onlineSumDao.getList(sso);
		LiveCourseCurveVO res = new LiveCourseCurveVO();
		res.setOnlineSumList(sumList);
		return res;
	}
	
}
