package com.hotelpal.service.service.live;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveMockUserDao;
import com.hotelpal.service.basic.mysql.dao.live.OnlineLogDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.live.*;
import com.hotelpal.service.common.so.live.OnlineLogSO;
import com.hotelpal.service.common.vo.LiveChatMessageVO;
import com.hotelpal.service.service.ContextService;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class LiveChatService {
	private static final Logger logger = LoggerFactory.getLogger(LiveChatService.class);
	public static final String ADMIN_TOKEN = "guFvC1iN6cnFVV257dwDVbEqttQ40vcJUzvAWvBdw6k0H8Tqblk1xXs2wbO95INF";
	@Resource
	private ContextService contextService;
	@Resource
	private LiveCourseDao liveCourseDao;
	@Resource
	private LiveContentService liveContentService;
	@Resource
	private OnlineLogDao onlineLogDao;
	@Resource
	private LiveCourseService liveCourseService;
	@Resource
	private LiveStatisticsService liveStatisticsService;
	@Resource
	private LiveMockUserDao liveMockUserDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;

	////////////////////////////////////////////////
	private static final String Y = BoolStatus.Y.toString();
	private static final String N = BoolStatus.N.toString();
	
	private static final Integer POOL_SIZE = 5;
	private static final Long STATISTICS_TIME_INTERVAL = 5 * 60 * 1000L;
	private static final Map<Integer, SingleCourseEnv> courseEnvMap = new ConcurrentHashMap<>();
	
	
	/**
	 * 课程上架状态一直维持线程池、websocket可用
	 */
	public void startService(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if (!Y.equalsIgnoreCase(course.getPublish())) {
			throw new ServiceException(ServiceException.COMMON_DATA_NOT_PUBLISHED);
		}
		if (courseEnvMap.containsKey(courseId) && courseEnvMap.get(courseId).published.get()) {
			return;
		}
		courseEnvMap.put(courseId, new SingleCourseEnv());
		SingleCourseEnv env = courseEnvMap.get(courseId);
		env.published.set(true);

		for (int i = 0; i < POOL_SIZE; i++) {
			env.pool.submit(new MessageWorker(courseId));
		}
		//提交一个心跳工作任务，定时向客户端发送无意义数据
		env.heartbeatTask = new Timer();
		env.heartbeatTask.scheduleAtFixedRate(new HeartBeatWorker(courseId), 40 * 1000L, 40 * 1000L);
		
		if (LiveCourseStatus.ONGOING.toString().equalsIgnoreCase(course.getStatus())) {
			startLive(course.getId());
		}
	}

	public void shutdownService(Integer courseId) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null) {
			return;
		}
		try {
			env.published.set(false);
			env.pool.shutdownNow();
			env.pool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("shutdownService failed...", e);
		} finally {
			for (Session s: env.sessions.values()) {
				try {
					this.closeSession(courseId, s);
				} catch (Exception ignored){}
			}
			env.msgQueue.clear();
			env.subscriber.clear();
			env.sessions.clear();
			env.allDomainIdSet.clear();
			env.blockedUserSet.clear();
			env.heartbeatTask.cancel();
			env.heartbeatTask = null;
			courseEnvMap.remove(courseId);
		}
	}

	public void join(Integer courseId, Session session, UserPO user) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		if (user.getDomainId() > 0) {
			contextService.initContext(user.getOpenId());
		}
		env.sessions.put(session.getId(), session);
		env.subscriber.put(session.getId(), user);
		if (env.ongoing.get()) {
			env.allDomainIdSet.add(user.getDomainId());
			LiveCoursePO course = liveCourseDao.getById(courseId);
			course.setTotalPeople(env.allDomainIdSet.size());
			liveCourseService.updateLiveCourse(course);

			//推送观看人数
			env.msgQueue.offer(new Message(TYPE_PRESENT_UPDATE, null, session.getId(), String.valueOf(env.subscriber.size())));

			//创建个人观看记录
			if (user.getDomainId() > 0) {
				OnlineLogPO olpo = new OnlineLogPO();
				olpo.setLiveCourseId(courseId);
				liveStatisticsService.createOnlineLog(olpo);
			}
			
			//进来之后推送一次：当前图片，人数
			Message msg = new Message(TYPE_IMAGE_CHANGE, null, session.getId(), env.currentImg);
			msg.setSingleSession(true);
			env.msgQueue.offer(msg);
			boolean showCoupon = env.showCoupon.get();
			Message couponMsg = new Message(showCoupon ? TYPE_SHOW_COUPON : TYPE_HIDE_COUPON, null, session.getId(), null);
			couponMsg.setSingleSession(true);
			env.msgQueue.offer(couponMsg);
			try {
				env.LOCK.tryLock();
				env.EMPTY.signalAll();
			}finally {
				env.LOCK.unlock();
			}
		}
	}

	public void submitMsg(Integer courseId, Session session, String msg) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		UserPO user = env.subscriber.get(session.getId());
		contextService.initContext(user.getOpenId());
		ChatLogPO po = new ChatLogPO();
		po.setLiveCourseId(courseId);
		po.setMsg(msg);
		po.setBlocked(env.blockedUserSet.contains(user.getDomainId()) ? Y : N);
		liveStatisticsService.createChatLog(po);
		Integer poId = po.getId();
		env.msgQueue.offer(new Message(TYPE_USER_MESSAGE, poId, session.getId(), msg));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}

	public void assistantMessage(Integer courseId, Session session, String msg) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		Integer poId = liveContentService.sendMsg(courseId, msg);
		env.msgQueue.offer(new Message(TYPE_ASSISTANT_MESSAGE, poId, session.getId(), msg));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}

	public void assistantMessageRemove(Integer courseId, Integer msgId) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		String sessionId = "";
		for (Map.Entry<String, UserPO> en : env.subscriber.entrySet()) {
			if(en.getValue().getDomainId() <= 0) {
				sessionId = en.getKey();
				break;
			}
		}
		env.msgQueue.offer(new Message(TYPE_ASSISTANT_MESSAGE_REMOVE, msgId, sessionId, null));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}

	public void closeSession(Integer courseId, Session session) throws IOException {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null) return;
		UserPO user = env.subscriber.get(session.getId());

		//下面的session.close会递归调用这个方法.....
		if (user == null) return;
		if (env.ongoing.get() && user.getDomainId() > 0) {
			contextService.initContext(user.getOpenId());
			OnlineLogSO so = new OnlineLogSO();
			so.setLiveCourseId(courseId);
			so.setOrder("desc");
			so.setOrderBy("createTime");
			OnlineLogPO po = onlineLogDao.getOne(so);
			if(po != null){
				po.setOfflineTime(new Date());
				liveStatisticsService.updateOnlineLog(po);
			}
		}
		env.subscriber.remove(session.getId());
		env.sessions.remove(session.getId());
		session.close();

		if (!env.ongoing.get()) return;
		env.msgQueue.offer(new Message(TYPE_PRESENT_UPDATE, null, session.getId(), String.valueOf(env.subscriber.size())));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}

	public void blockUser(Integer courseId, Integer domainId) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		env.blockedUserSet.add(domainId);
	}

	public void showCoupon(Integer courseId, boolean show) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		course.setCouponShow(show ? Y : N);
		liveCourseService.updateLiveCourse(course);
		env.showCoupon.set(show);
		String sessionId = "";
		for (Map.Entry<String, UserPO> en : env.subscriber.entrySet()) {
			if(en.getValue().getDomainId() <= 0) {
				sessionId = en.getKey();
				break;
			}
		}
		env.msgQueue.offer(new Message(show ? TYPE_SHOW_COUPON : TYPE_HIDE_COUPON, null, sessionId, null));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}

	public void mockUserMsg(Integer courseId, String msg) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		String sessionId = "";
		for (Map.Entry<String, UserPO> en : env.subscriber.entrySet()) {
			if(en.getValue().getDomainId() <= 0) {
				sessionId = en.getKey();
				break;
			}
		}
		LiveMockUserPO mockUser = liveMockUserDao.getAny();
		UserPO user = dozerBeanMapper.map(mockUser, UserPO.class);
		env.subscriber.put(sessionId, user);
		env.msgQueue.offer(new Message(TYPE_USER_MESSAGE, null, sessionId, msg));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}





	public void startLive(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if (!Y.equalsIgnoreCase(course.getPublish())) {
			throw new ServiceException(ServiceException.COMMON_DATA_NOT_PUBLISHED);
		}
		SingleCourseEnv env = courseEnvMap.get(courseId);
		for (UserPO subscriber : env.subscriber.values()) {
			env.allDomainIdSet.add(subscriber.getDomainId());
		}
		course.setTotalPeople(env.allDomainIdSet.size());
		course.setStatus(LiveCourseStatus.ONGOING.toString());
		liveCourseService.updateLiveCourse(course);
		if (courseEnvMap.containsKey(courseId) && courseEnvMap.get(courseId).ongoing.get()) {
			return;
		}
		startService(courseId);
		env.ongoing.set(true);
		env.timerTask = new Timer();
		env.timerTask.scheduleAtFixedRate(new StatisticsWorker(this, courseId), STATISTICS_TIME_INTERVAL, STATISTICS_TIME_INTERVAL);
		String sessionId = "";
		
		for (Map.Entry<String, UserPO> en : env.subscriber.entrySet()) {
			if(en.getValue().getDomainId() <= 0) {
				sessionId = en.getKey();
				break;
			}
		}
		env.msgQueue.offer(new Message(TYPE_LIVE_START, null, sessionId, null));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
	}

	public void shutdownLive(Integer courseId) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null) {
			throw new ServiceException("empty env by liveCourseId" + courseId);
		}
		try {
			env.ongoing.set(false);
			String sessionId = "";
			for (Map.Entry<String, UserPO> en : env.subscriber.entrySet()) {
				if(en.getValue().getDomainId() <= 0) {
					sessionId = en.getKey();
					break;
				}
			}
			env.msgQueue.offer(new Message(TYPE_LIVE_TERMINATE, null, sessionId, null));
			try {
				env.LOCK.tryLock();
				env.EMPTY.signalAll();
			}finally {
				env.LOCK.unlock();
			}
		} catch (Exception ignored) {
		} finally {
			if (env.timerTask != null) {
				env.timerTask.cancel();
				env.timerTask = null;
			}
		}
	}

	public void changeImg(Integer courseId, Session session, String img) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null || !env.ongoing.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_ONGOING);
		}
		env.currentImg = img;
		env.msgQueue.offer(new Message(TYPE_IMAGE_CHANGE, null, session.getId(), img));
		try {
			env.LOCK.tryLock();
			env.EMPTY.signalAll();
		}finally {
			env.LOCK.unlock();
		}
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course != null) {
			course.setCurrentImg(img);
			liveCourseService.updateLiveCourse(course);
		}
	}
	
	
	
	private class MessageWorker implements Runnable {
		private Integer courseId;
		MessageWorker(Integer courseId) {
			this.courseId = courseId;
		}
		@Override
		public void run() {
			while (true) {
				if (!courseEnvMap.containsKey(courseId) || !courseEnvMap.get(courseId).published.get()) return;
				SingleCourseEnv env = courseEnvMap.get(courseId);
				Message msg = env.msgQueue.poll();
				//防止其他线程已经poll
				if (msg != null) {

					boolean isAssistant = TYPE_ASSISTANT_MESSAGE.equalsIgnoreCase(msg.getMsgType());
					boolean coupon = TYPE_SHOW_COUPON.equalsIgnoreCase(msg.getMsgType()) || TYPE_HIDE_COUPON.equalsIgnoreCase(msg.getMsgType());
					boolean img = TYPE_IMAGE_CHANGE.equalsIgnoreCase(msg.getMsgType());
					boolean isStartTerminate = TYPE_LIVE_START.equalsIgnoreCase(msg.getMsgType()) || TYPE_LIVE_TERMINATE.equalsIgnoreCase(msg.getMsgType());
					boolean idAssistantMsgRemove = TYPE_ASSISTANT_MESSAGE_REMOVE.equalsIgnoreCase(msg.getMsgType());
					boolean presentUpdate = TYPE_PRESENT_UPDATE.equalsIgnoreCase(msg.getMsgType());
					boolean heartbeat = TYPE_HEARTBEAT.equalsIgnoreCase(msg.getMsgType());
					
					try {
						
						if (msg.isSingleSession()) {
							if (img) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								this.send(env.sessions.get(msg.getSessionId()), vo);
							}
							if (coupon) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsgType(msg.getMsgType());
								this.send(env.sessions.get(msg.getSessionId()), vo);
							}
							continue;
						}
						Collection<Session> sessionList = env.sessions.values();
						if (heartbeat) {
							for (Session s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
							continue;
						}
						UserPO user = env.subscriber.get(msg.getSessionId());
						if (coupon || isStartTerminate) {
							for (Session s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else if (isAssistant || idAssistantMsgRemove) {
							for (Session s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setId(msg.getId());
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else if (img || presentUpdate) {
							for (Session s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else {
							boolean blocked = env.blockedUserSet.contains(user.getDomainId());
							for (Session s : sessionList) {
								if (blocked && (!s.getId().equalsIgnoreCase(msg.getSessionId())) && env.subscriber.get(s.getId()).getDomainId() > 0) continue;
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setId(msg.getId());
								vo.setBlocked(blocked ? Y : N);
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								vo.setHeadImg(user.getHeadImg());
								vo.setNick(user.getNick());
								vo.setSelf(msg.getSessionId().equalsIgnoreCase(s.getId()) ? Y : N);
								vo.setCompany(user.getCompany());
								vo.setTitle(user.getTitle());
								
								this.send(s, vo);
							}
						}
					} catch (Exception e) {
						logger.error("send ws message failed...", e);
					}
				} else {
					try {
						if (!env.published.get()) {
							return;
						}
						env.LOCK.lock();
						env.EMPTY.await();
					}catch (Exception e) {
						return;
					}finally {
						env.LOCK.unlock();
					}
				}
			}
		}
		
		private void send(Session session, LiveChatMessageVO vo) {
			synchronized (session) {
				try {
					session.getBasicRemote().sendText(JSON.toJSONString(vo));
				} catch(Exception e) {
					logger.error("send text failed...", e);
				}
			}
		}
	}
	
	private class HeartBeatWorker extends TimerTask {
		private Integer courseId;
		HeartBeatWorker(Integer cId) {
			this.courseId = cId;
		}
		
		
		@Override
		public void run() {
			SingleCourseEnv env = courseEnvMap.get(courseId);
			env.msgQueue.offer(new Message(TYPE_HEARTBEAT, null, null, null));
			try {
				env.LOCK.tryLock();
				env.EMPTY.signalAll();
			}finally {
				env.LOCK.unlock();
			}
			
		}
	}
	private class Message implements Serializable {
		private static final long serialVersionUID = -275058187689050241L;
		private String msgType;
		private Integer id;
		private Date createTime;
		private String msg;
		private String sessionId;
		private boolean singleSession;
		
		public Message(String msgType, Integer id, String sessionId, String msg) {
			this.msgType = msgType;
			this.id = id;
			this.sessionId = sessionId;
			this.msg = msg;
			this.createTime = new Date();
		}
		
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getSessionId() {
			return sessionId;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getMsgType() {
			return msgType;
		}
		public boolean isSingleSession() {
			return singleSession;
		}
		public void setSingleSession(boolean singleSession) {
			this.singleSession = singleSession;
		}
	}
	
	private static final String TYPE_USER_MESSAGE = "TYPE_USER_MESSAGE";
	private static final String TYPE_ASSISTANT_MESSAGE = "TYPE_ASSISTANT_MESSAGE";
	private static final String TYPE_ASSISTANT_MESSAGE_REMOVE = "TYPE_ASSISTANT_MESSAGE_REMOVE";
	private static final String TYPE_SHOW_COUPON = "TYPE_SHOW_COUPON";
	private static final String TYPE_HIDE_COUPON = "TYPE_HIDE_COUPON";
	private static final String TYPE_IMAGE_CHANGE = "TYPE_IMAGE_CHANGE";
	private static final String TYPE_LIVE_START= "TYPE_LIVE_START";
	private static final String TYPE_LIVE_TERMINATE = "TYPE_LIVE_TERMINATE";
	private static final String TYPE_PRESENT_UPDATE = "TYPE_PRESENT_UPDATE";
	private static final String TYPE_EXCEPTION = "TYPE_EXCEPTION";
	private static final String TYPE_HEARTBEAT = "TYPE_HEARTBEAT";

	

	private class StatisticsWorker extends TimerTask{
		private LiveChatService service;
		private Integer courseId;
		public StatisticsWorker(LiveChatService service, Integer courseId){
			this.service = service;
			this.courseId = courseId;
		}
		@Override
		public void run() {
			SingleCourseEnv env = courseEnvMap.get(courseId);
			if (!env.ongoing.get()) return;
			OnlineSumPO po = new OnlineSumPO();
			po.setLiveCourseId(courseId);
			po.setOnlineSum(env.allDomainIdSet.size());
			service.liveStatisticsService.createOnlineSum(po);
		}
	}
	
	private class SingleCourseEnv{
		//用户提交的消息保存到这个队列
		//sessionId, msg
		Queue<Message> msgQueue = new ConcurrentLinkedQueue<>();
		//sessionId, UserPO
		Map<String, UserPO> subscriber = new ConcurrentHashMap<>();
		//sessionId, session
		Map<String, Session> sessions = new ConcurrentHashMap<>();
		ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
		Set<Integer> allDomainIdSet = ConcurrentHashMap.newKeySet();
		Set<Integer> blockedUserSet = ConcurrentHashMap.newKeySet();
		Lock LOCK = new ReentrantLock();
		Condition EMPTY = LOCK.newCondition();
		AtomicBoolean ongoing = new AtomicBoolean(false);
		Timer timerTask;
		Timer heartbeatTask;
		AtomicBoolean published = new AtomicBoolean(false);
		String currentImg;
		AtomicBoolean showCoupon = new AtomicBoolean(false);
		
	}

	public void destroyTasks() {
		List<Integer> idList = liveCourseDao.getTableIds();
		for (Integer id : idList) {
			shutdownService(id);
		}
	}
	
	public Integer getCoursePresent(Integer liveCourseId) {
		try {
			return courseEnvMap.get(liveCourseId).subscriber.size();
		} catch (Exception ignored){}
		return 0;
	}
}
