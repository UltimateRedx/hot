package com.hotelpal.service.service.live;

import com.hotelpal.service.basic.mysql.dao.live.ChatLogDao;
import com.hotelpal.service.basic.mysql.dao.live.OnlineLogDao;
import com.hotelpal.service.basic.mysql.dao.live.OnlineSumDao;
import com.hotelpal.service.common.po.live.ChatLogPO;
import com.hotelpal.service.common.po.live.OnlineLogPO;
import com.hotelpal.service.common.po.live.OnlineSumPO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Transactional
public class LiveStatisticsService {
	@Resource
	private OnlineLogDao onlineLogDao;
	@Resource
	private ChatLogDao chatLogDao;
	@Resource
	private OnlineSumDao onlineSumDao;
	
	public void createOnlineLog(OnlineLogPO po) {
		onlineLogDao.create(po);
	}
	public void updateOnlineLog(OnlineLogPO po) {
		onlineLogDao.update(po);
	}
	public void createChatLog(ChatLogPO po) {
		chatLogDao.create(po);
	}
	public void createOnlineSum(OnlineSumPO po) {
		onlineSumDao.create(po);
	}
}
