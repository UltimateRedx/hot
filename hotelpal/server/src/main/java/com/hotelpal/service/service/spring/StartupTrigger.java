package com.hotelpal.service.service.spring;

import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.so.live.LiveCourseSO;
import com.hotelpal.service.service.live.LiveChatService;
import com.hotelpal.service.service.parterner.wx.MsgPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

public class StartupTrigger {
	private static final Logger logger = LoggerFactory.getLogger(StartupTrigger.class);
	
	@Resource
	private MsgPushService msgPushService;
	@Resource
	private LiveCourseDao liveCourseDao;
	@Resource
	private LiveChatService liveChatService;
	
	
	/**
	 * content初始化之后将需要执行的方法放入这个里面
	 */
	public void contentInitializer() {
		startLiveService();
		loadLiveCourseScheduler();
	}
	
	/**
	 * 重置直播课程通知的定时器
	 */
	private void loadLiveCourseScheduler() {
		LiveCourseSO so = new LiveCourseSO();
		so.setDeleted(BoolStatus.N.toString());
		so.setPublish(BoolStatus.Y.toString());
		so.setStatus(LiveCourseStatus.ENROLLING.toString());
		List<LiveCoursePO> courseList = liveCourseDao.getList(so);
		for (LiveCoursePO course : courseList) {
			msgPushService.loadOrUpdateLiveCourseOpeningTrigger(course.getId());
		}
	}
	
	/**
	 * 上架的直播课程重新开启websocket
	 */
	public void startLiveService() {
		LiveCourseSO so = new LiveCourseSO();
		so.setPageSize(Integer.MAX_VALUE);
		so.setPublish(BoolStatus.Y.toString());
		List<LiveCoursePO> courseList = liveCourseDao.getList(so);
		for (LiveCoursePO po : courseList) {
			try {
				liveChatService.startService(po.getId());
			}catch (Exception e) {
				logger.error("start live course service failed...", e);
			}
		}
	}
}
