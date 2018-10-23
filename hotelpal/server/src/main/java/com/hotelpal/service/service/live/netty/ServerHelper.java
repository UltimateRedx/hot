package com.hotelpal.service.service.live.netty;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.SysPropertyDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveMockUserDao;
import com.hotelpal.service.basic.mysql.dao.live.OnlineLogDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.SysPropertyPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.live.*;
import com.hotelpal.service.common.so.live.OnlineLogSO;
import com.hotelpal.service.common.vo.LiveChatMessageVO;
import com.hotelpal.service.service.ContextService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.live.LiveChatService;
import com.hotelpal.service.service.live.LiveContentService;
import com.hotelpal.service.service.live.LiveCourseService;
import com.hotelpal.service.service.live.LiveStatisticsService;
import com.hotelpal.service.service.spring.SpringApplicationContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.hotelpal.service.service.live.netty.ServerHelper.SingleCourseEnv.POOLSIZE;

public class ServerHelper {
	@Resource
	private ServerHelper serverHelper;

	private static final Logger logger = LoggerFactory.getLogger(ServerHelper.class);

	@Resource
	private DozerBeanMapper dozerBeanMapper;
	private UserService userService;
	private SysPropertyDao sysPropertyDao;
	private LiveCourseDao liveCourseDao;
	private ContextService contextService;
	private LiveStatisticsService liveStatisticsService;
	private OnlineLogDao onlineLogDao;
	private LiveCourseService liveCourseService;
	private LiveMockUserDao liveMockUserDao;
	private LiveContentService liveContentService;

	public static final Map<Integer, SingleCourseEnv> courseEnvMap = new ConcurrentHashMap<>();
	public static final Map<ChannelId, Integer> sessionCourseMap = new ConcurrentHashMap<>();

	/////////////////////
	private static final String Y = BoolStatus.Y.toString();
	private static final String N = BoolStatus.N.toString();
	private static final Long STATISTICS_TIME_INTERVAL = 5 * 60 * 1000L;



	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("ServerHelper init method... Self injection: {}", serverHelper.toString());
		}
		userService = SpringApplicationContext.applicationContext.getBean(UserService.class);
		sysPropertyDao = SpringApplicationContext.applicationContext.getBean(SysPropertyDao.class);
		liveCourseDao = SpringApplicationContext.applicationContext.getBean(LiveCourseDao.class);
		contextService = SpringApplicationContext.applicationContext.getBean(ContextService.class);
		liveStatisticsService = SpringApplicationContext.applicationContext.getBean(LiveStatisticsService.class);
		onlineLogDao = SpringApplicationContext.applicationContext.getBean(OnlineLogDao.class);
		liveCourseService = SpringApplicationContext.applicationContext.getBean(LiveCourseService.class);
		liveMockUserDao = SpringApplicationContext.applicationContext.getBean(LiveMockUserDao.class);
		liveContentService = SpringApplicationContext.applicationContext.getBean(LiveContentService.class);
	}

	public void reInitCourseEnv(Integer courseId) {
		if (courseEnvMap.containsKey(courseId)) {
			shutdownOldCourseEnv(courseId);
		}
		newSingleCourseEnv(courseId);
	}

	private void newSingleCourseEnv(Integer courseId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Try to newSingleCourseEnv, courseId: {}", courseId);
		}
		SingleCourseEnv env = new SingleCourseEnv();
		Map<Integer, Integer> ongoingBaseLine = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_ONGOING, Collections.singletonList(courseId));
		env.ongoingBaseLine = ongoingBaseLine.get(courseId);
		env.published.set(true);
		courseEnvMap.put(courseId, env);

		if (logger.isDebugEnabled()) {
			Runtime rt = Runtime.getRuntime();
			logger.debug("Mem before alloc pool: total:{}, max:{}, free:{}", rt.totalMemory(), rt.maxMemory(), rt.freeMemory());
		}
		long time = System.currentTimeMillis();
		env.pool = Executors.newFixedThreadPool(POOLSIZE);
		for (int i = 0; i < POOLSIZE; i++) {
			env.pool.submit(new MessageWorker(courseId));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("pool 创建和提交耗时 {} ms", System.currentTimeMillis() - time);
			Runtime rt = Runtime.getRuntime();
			logger.debug("Mem after alloc pool: total:{}, max:{}, free:{}", rt.totalMemory(), rt.maxMemory(), rt.freeMemory());
		}

		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (LiveCourseStatus.ONGOING.toString().equalsIgnoreCase(course.getStatus())) {
			startLive(course.getId());
		}
	}
	/***
	 * 关闭此课时服务，应该在下架时才会使用
	 */
	public void shutdownOldCourseEnv(Integer courseId) {
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
			for (ChannelHandlerContext s: env.sessions.values()) {
				try {
					closeSession(courseId, s);
				} catch (Exception ignored){}
			}
			env.msgQueue.clear();
			env.subscriber.clear();
			env.sessions.clear();
//			env.allDomainIdSet.clear();
			env.blockedUserSet.clear();
			courseEnvMap.remove(courseId);
		}
	}

	public void closeSession(Integer courseId, ChannelHandlerContext context) {
		context.close();
		if (courseId == null) {
			logger.warn("courseSessionMap does not has key {}, closeSession will cause Exception. Check init method.", context.channel().id());
		}
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null) return;
		ChannelId sessionId = context.channel().id();
		UserPO user = env.subscriber.get(sessionId);

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
		env.subscriber.remove(sessionId);
		env.sessions.remove(sessionId);

		if (!env.ongoing.get()) return;
		env.msgQueue.offer(new Message(TYPE_PRESENT_UPDATE, null, sessionId, String.valueOf(env.subscriber.size())));
		signalAll(env);
		sessionCourseMap.remove(sessionId);
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
//		for (UserPO subscriber : env.subscriber.values()) {
//			env.allDomainIdSet.add(subscriber.getDomainId());
//		}
		course.setTotalPeople(env.subscriber.size());
		course.setStatus(LiveCourseStatus.ONGOING.toString());
		liveCourseService.updateLiveCourse(course);
		if (courseEnvMap.containsKey(courseId) && courseEnvMap.get(courseId).ongoing.get()) {
			return;
		}
//		startService(courseId);
		env.ongoing.set(true);
		env.timerTask = new Timer();
		env.timerTask.scheduleAtFixedRate(new StatisticsWorker(courseId), STATISTICS_TIME_INTERVAL, STATISTICS_TIME_INTERVAL);
		String sessionId = "";

		env.msgQueue.offer(new Message(TYPE_LIVE_START, null, null, null));
		signalAll(env);
	}
	public void shutdownLive(Integer courseId) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null) {
			throw new ServiceException("empty env by liveCourseId" + courseId);
		}
		try {
			env.ongoing.set(false);
			String sessionId = "";
			env.msgQueue.offer(new Message(TYPE_LIVE_TERMINATE, null, null, null));
			signalAll(env);
		} catch (Exception ignored) {
		} finally {
			if (env.timerTask != null) {
				env.timerTask.cancel();
				env.timerTask = null;
			}
		}
	}

	public void submitMsg(Integer courseId, ChannelId sessionId, String msg) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		UserPO user = env.subscriber.get(sessionId);
		contextService.initContext(user.getOpenId());
		ChatLogPO po = new ChatLogPO();
		po.setLiveCourseId(courseId);
		po.setMsg(msg);
		po.setBlocked(env.blockedUserSet.contains(user.getDomainId()) ? Y : N);
		liveStatisticsService.createChatLog(po);
		Integer poId = po.getId();
		env.msgQueue.offer(new Message(TYPE_USER_MESSAGE, poId, sessionId, msg));
		signalAll(env);
	}
	public void assistantMessage(Integer courseId, String msg) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		Integer poId = liveContentService.sendMsg(courseId, msg);
		env.msgQueue.offer(new Message(TYPE_ASSISTANT_MESSAGE, poId, null, msg));
		signalAll(env);
	}
	public void assistantMessageRemove(Integer courseId, Integer msgId) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		env.msgQueue.offer(new Message(TYPE_ASSISTANT_MESSAGE_REMOVE, msgId, null, null));
		signalAll(env);
	}
	public void changeImg(Integer courseId, String img) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (env == null || !env.ongoing.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_ONGOING);
		}
		env.currentImg = img;
		env.msgQueue.offer(new Message(TYPE_IMAGE_CHANGE, null, null, img));
		signalAll(env);
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course != null) {
			course.setCurrentImg(img);
			liveCourseService.updateLiveCourse(course);
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
		env.msgQueue.offer(new Message(show ? TYPE_SHOW_COUPON : TYPE_HIDE_COUPON, null, null, null));
		signalAll(env);
	}

	public void mockUserMsg(Integer courseId, String msg) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		if (!courseEnvMap.containsKey(courseId) || !env.published.get()) {
			throw new ServiceException(ServiceException.LIVE_NOT_PUBLISHED);
		}
		LiveMockUserPO mockUser = liveMockUserDao.getAny();
		UserPO user = dozerBeanMapper.map(mockUser, UserPO.class);
		ChannelId sessionId = null;
		for (Map.Entry<ChannelId, UserPO> en : env.subscriber.entrySet()) {
			if(en.getValue().getDomainId() <= 0) {
				sessionId = en.getKey();
				break;
			}
		}
		env.subscriber.put(sessionId, user);
		env.msgQueue.offer(new Message(TYPE_USER_MESSAGE, null, sessionId, msg));
		signalAll(env);
	}
	public void setOngoingBaseLine(Integer courseId, Integer baseLine) {
		SingleCourseEnv env = courseEnvMap.get(courseId);
		env.ongoingBaseLine = baseLine;
		env.msgQueue.offer(new Message(TYPE_PRESENT_UPDATE, null, null, String.valueOf(env.subscriber.size())));
		signalAll(env);
	}
	public Integer getCoursePresent(Integer liveCourseId) {
		try {
			return courseEnvMap.get(liveCourseId).subscriber.size();
		} catch (Exception ignored){}
		return 0;
	}


	public void destroyTasks() {
		List<Integer> idList = liveCourseDao.getTableIds();
		for (Integer id : idList) {
			shutdownOldCourseEnv(id);
		}
	}


	public void signalAll(SingleCourseEnv env) {
		if(env.LOCK.tryLock()) {
			try {
				env.EMPTY.signalAll();
			} finally {
				env.LOCK.unlock();
			}
		}
	}

	static class SingleCourseEnv{
		/**10-20个就够了。
		 *5线程时500并发连接， 需要3s左右等待响应
		 * 由Jmeter测试，结果只做参考。
		 */
		static final int POOLSIZE = 10;


		//用户提交的消息保存到这个队列
		Queue<Message> msgQueue = new ConcurrentLinkedQueue<>();
		//ChannelId, UserPO
		Map<ChannelId, UserPO> subscriber = new ConcurrentHashMap<>();
		Map<ChannelId, ChannelHandlerContext> sessions = new ConcurrentHashMap<>();
		ExecutorService pool;
//		Set<Integer> allDomainIdSet = ConcurrentHashMap.newKeySet();
		Set<Integer> blockedUserSet = ConcurrentHashMap.newKeySet();
		Lock LOCK = new ReentrantLock();
		Condition EMPTY = LOCK.newCondition();
		AtomicBoolean ongoing = new AtomicBoolean(false);
		AtomicBoolean published = new AtomicBoolean(false);
		AtomicBoolean showCoupon = new AtomicBoolean(false);


		//直播开始才使用
		Timer timerTask;
//		Timer heartbeatTask;
		String currentImg;
		//在线人数显示的基数
		Integer ongoingBaseLine = 0;
	}

	static class Message implements Serializable {
		private static final long serialVersionUID = -275058187689050241L;
		private String msgType;
		private Integer id;
		private Date createTime;
		private String msg;
//		private String sessionId;
		private ChannelId channelId;
		private boolean singleSession;

		public Message(String msgType, Integer id, ChannelId channelId, String msg) {
			this.msgType = msgType;
			this.id = id;
			this.channelId = channelId;
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
		public Integer getId() {
			return id;
		}
		public ChannelId getChannelId() {
			return channelId;
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

	private class MessageWorker implements Runnable {
		private Integer courseId;
		MessageWorker(Integer courseId) {
			this.courseId = courseId;
		}
		@Override
		public void run() {
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Interrupted MessageWorker. Exiting...");
					}
					return;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Message worker while loop... Course published: {}", courseEnvMap.get(courseId).published);
				}
				if (!courseEnvMap.containsKey(courseId) || !courseEnvMap.get(courseId).published.get()) return;
				SingleCourseEnv env = courseEnvMap.get(courseId);
				Message msg = env.msgQueue.poll();
				//防止其他线程已经poll
				if (msg != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Try send msg: {}", JSON.toJSONString(msg));
					}
					boolean isAssistant = TYPE_ASSISTANT_MESSAGE.equalsIgnoreCase(msg.getMsgType());
					boolean coupon = TYPE_SHOW_COUPON.equalsIgnoreCase(msg.getMsgType()) || TYPE_HIDE_COUPON.equalsIgnoreCase(msg.getMsgType());
					boolean img = TYPE_IMAGE_CHANGE.equalsIgnoreCase(msg.getMsgType());
					boolean isStartTerminate = TYPE_LIVE_START.equalsIgnoreCase(msg.getMsgType()) || TYPE_LIVE_TERMINATE.equalsIgnoreCase(msg.getMsgType());
					boolean idAssistantMsgRemove = TYPE_ASSISTANT_MESSAGE_REMOVE.equalsIgnoreCase(msg.getMsgType());
					boolean presentUpdate = TYPE_PRESENT_UPDATE.equalsIgnoreCase(msg.getMsgType());
					boolean heartbeat = TYPE_HEARTBEAT.equalsIgnoreCase(msg.getMsgType());

					try {
						//定向发送
						if (msg.isSingleSession()) {
							if (img) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								this.send(env.sessions.get(msg.getChannelId()), vo);
							}
							if (coupon) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsgType(msg.getMsgType());
								this.send(env.sessions.get(msg.getChannelId()), vo);
							}
							continue;
						}

						Collection<ChannelHandlerContext> sessionList = env.sessions.values();
						if (heartbeat) {
							for (ChannelHandlerContext s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
							continue;
						}
						if (coupon || isStartTerminate) {
							for (ChannelHandlerContext s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else if (isAssistant || idAssistantMsgRemove) {
							for (ChannelHandlerContext s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setId(msg.getId());
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else if (img) {
							for (ChannelHandlerContext s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else if (presentUpdate) {
							Integer baseLine = env.ongoingBaseLine;
							for (ChannelHandlerContext s : sessionList) {
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setMsg(String.valueOf(Integer.valueOf(msg.getMsg()) + baseLine));
								vo.setMsgType(msg.getMsgType());
								this.send(s, vo);
							}
						} else {
							UserPO user = env.subscriber.get(msg.getChannelId());
							boolean blocked = env.blockedUserSet.contains(user.getDomainId());
							for (ChannelHandlerContext s : sessionList) {
								if (blocked && (!s.channel().id().equals(msg.getChannelId())) && env.subscriber.get(s.channel().id()).getDomainId() > 0) continue;
								LiveChatMessageVO vo = new LiveChatMessageVO();
								vo.setId(msg.getId());
								vo.setBlocked(blocked ? Y : N);
								vo.setCreateTime(msg.getCreateTime());
								vo.setMsg(msg.getMsg());
								vo.setMsgType(msg.getMsgType());
								vo.setHeadImg(user.getHeadImg());
								vo.setNick(user.getNick());
								vo.setSelf(msg.getChannelId().equals(s.channel().id()) ? Y : N);
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
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}finally {
						env.LOCK.unlock();
					}
				}
			}
		}

		private void send(ChannelHandlerContext context, LiveChatMessageVO vo) {
			if (context != null && context.channel().isOpen()) {
				context.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(vo)));
			}
		}
	}

	private class StatisticsWorker extends TimerTask{
		private LiveStatisticsService liveStatisticsService = SpringApplicationContext.applicationContext.getBean(LiveStatisticsService.class);
		private Integer courseId;
		public StatisticsWorker(Integer courseId){
			this.courseId = courseId;
		}
		@Override
		public void run() {
			SingleCourseEnv env = courseEnvMap.get(courseId);
			if (!env.ongoing.get()) return;
			OnlineSumPO po = new OnlineSumPO();
			po.setLiveCourseId(courseId);
			po.setOnlineSum(env.subscriber.size());
			this.liveStatisticsService.createOnlineSum(po);
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
}
