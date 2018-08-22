package com.hotelpal.service.service.live.netty;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.po.live.OnlineLogPO;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.service.ContextService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.live.LiveChatService;
import com.hotelpal.service.service.live.LiveCourseService;
import com.hotelpal.service.service.live.LiveStatisticsService;
import com.hotelpal.service.service.spring.SpringApplicationContext;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hotelpal.service.service.live.netty.ServerHelper.Message;
import static com.hotelpal.service.service.live.netty.ServerHelper.SingleCourseEnv;


public class CourseServiceHandler extends ChannelDuplexHandler {
	private static final Logger logger = LoggerFactory.getLogger(CourseServiceHandler.class);
	private LiveChatService liveChatService = SpringApplicationContext.applicationContext.getBean(LiveChatService.class);
	private UserService userService = SpringApplicationContext.applicationContext.getBean(UserService.class);
	private ContextService contextService = SpringApplicationContext.applicationContext.getBean(ContextService.class);
	private LiveCourseDao liveCourseDao = SpringApplicationContext.applicationContext.getBean(LiveCourseDao.class);
	private LiveCourseService liveCourseService = SpringApplicationContext.applicationContext.getBean(LiveCourseService.class);
	private LiveStatisticsService liveStatisticsService = SpringApplicationContext.applicationContext.getBean(LiveStatisticsService.class);
	private ServerHelper serverHelper = SpringApplicationContext.applicationContext.getBean(ServerHelper.class);

	private Map<Integer, ServerHelper.SingleCourseEnv> courseEnvMap = ServerHelper.courseEnvMap;

	//session-CourseId.用于记录改session在听哪个课程
	private Map<ChannelId, Integer> sessionCourseMap = ServerHelper.sessionCourseMap;

	////////////////////////////////////////////////
	private static final String Y = BoolStatus.Y.toString();
	private static final String N = BoolStatus.N.toString();


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof TextWebSocketFrame) {
			handleMsg(ctx, ((TextWebSocketFrame) msg).copy());
		}
		super.channelRead(ctx, msg);
	}

	private void handleMsg(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
		String json = msg.text();
		ClientMessage clientMsg = JSON.parseObject(json, ClientMessage.class);
		msg.release();
		if (CLIENT_USER.equalsIgnoreCase(clientMsg.getClientType()) && Y.equalsIgnoreCase(clientMsg.init) && clientMsg.initValid()) {
			initUserServerContext(ctx, clientMsg);
			initUserClientContext(ctx, clientMsg);
		} else if (CLIENT_USER.equalsIgnoreCase(clientMsg.getClientType()) && N.equalsIgnoreCase(clientMsg.init) && clientMsg.msgValid()) {
			writeNormalMessage(ctx, clientMsg);
		} else if(CLIENT_ADMIN.equalsIgnoreCase(clientMsg.getClientType())
				&& ClientMessage.ADMIN_TOKEN.equalsIgnoreCase(clientMsg.getToken())
				&& Y.equalsIgnoreCase(clientMsg.getInit())) {
			initAdminServerContext(ctx, clientMsg);
		} else if (CLIENT_ADMIN.equalsIgnoreCase(clientMsg.getClientType())
				&& ClientMessage.ADMIN_TOKEN.equalsIgnoreCase(clientMsg.getToken())
				&& N.equalsIgnoreCase(clientMsg.getInit())) {
			serverHelper.assistantMessage(clientMsg.getCourseId(), clientMsg.getMsg());
		} else if(CLIENT_PPT.equalsIgnoreCase(clientMsg.getClientType())
				&& Y.equalsIgnoreCase(clientMsg.getInit())) {
			initPPTServerContext(ctx, clientMsg);
		} else if(CLIENT_PPT.equalsIgnoreCase(clientMsg.getClientType())
				&& N.equalsIgnoreCase(clientMsg.getInit())) {
			serverHelper.changeImg(clientMsg.getCourseId(), clientMsg.getMsg());
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Client msg invalid. Full message is " + json);
			}
		}
	}

	private void initUserServerContext(ChannelHandlerContext context, ClientMessage clientMsg) {
		Integer courseId = clientMsg.getCourseId();
		if (!courseEnvMap.containsKey(courseId)) {
			context.close();
			if (logger.isDebugEnabled()) {
				logger.debug("环境中没有课程{}， 已将context关闭", courseId);
			}
			return;
		}
		sessionCourseMap.put(context.channel().id(), courseId);
		SingleCourseEnv env = courseEnvMap.get(clientMsg.getCourseId());
		UserPO user = userService.getUserByOpenId(clientMsg.getToken());
		env.subscriber.put(context.channel().id(), user);
		env.sessions.put(context.channel().id(), context);
		if (user.getDomainId() > 0 && env.ongoing.get()) {
			contextService.initContext(user.getOpenId());
			LiveCoursePO course = liveCourseDao.getById(courseId);
			course.setTotalPeople(env.subscriber.size());
			liveCourseService.updateLiveCourse(course);


			//创建个人观看记录
			if (user.getDomainId() > 0) {
				OnlineLogPO olpo = new OnlineLogPO();
				olpo.setLiveCourseId(courseId);
				liveStatisticsService.createOnlineLog(olpo);
			}
		}
	}
	private void initUserClientContext(ChannelHandlerContext context, ClientMessage clientMsg) {
		//推送人数、当前图片
		ServerHelper.SingleCourseEnv env = courseEnvMap.get(clientMsg.getCourseId());
		if (!env.ongoing.get()) {
			return;
		}
		//人数更新是推给所有人，当前用户也能收到
		env.msgQueue.offer(new Message(TYPE_PRESENT_UPDATE, null, context.channel().id(), String.valueOf(env.subscriber.size())));

		//单独推送当前图片、优惠券
		Message msg = new Message(TYPE_IMAGE_CHANGE, null, context.channel().id(), env.currentImg);
		msg.setSingleSession(true);
		env.msgQueue.offer(msg);
		boolean showCoupon = env.showCoupon.get();
		Message couponMsg = new Message(showCoupon ? TYPE_SHOW_COUPON : TYPE_HIDE_COUPON, null, context.channel().id(), null);
		couponMsg.setSingleSession(true);
		env.msgQueue.offer(couponMsg);
		serverHelper.signalAll(env);
	}
	private void writeNormalMessage(ChannelHandlerContext context, ClientMessage clientMsg) {
		ChannelId id = context.channel().id();
		serverHelper.submitMsg(sessionCourseMap.get(id), id, clientMsg.msg);
	}
	private void initAdminServerContext(ChannelHandlerContext context, ClientMessage clientMsg) {
		Integer courseId = clientMsg.getCourseId();
		if (!courseEnvMap.containsKey(courseId)) {
			context.close();
			if (logger.isDebugEnabled()) {
				logger.debug("环境中没有课程{}， 已将context关闭", courseId);
			}
			return;
		}
		sessionCourseMap.put(context.channel().id(), courseId);
		SingleCourseEnv env = courseEnvMap.get(clientMsg.getCourseId());
		UserPO user = new UserPO();
		user.setDomainId(-1);
		env.subscriber.put(context.channel().id(), user);
		env.sessions.put(context.channel().id(), context);
	}
	private void initPPTServerContext(ChannelHandlerContext context, ClientMessage clientMsg) {
		Integer courseId = clientMsg.getCourseId();
		if (!courseEnvMap.containsKey(courseId)) {
			context.close();
			if (logger.isDebugEnabled()) {
				logger.debug("环境中没有课程{}， 已将context关闭", courseId);
			}
			return;
		}
		sessionCourseMap.put(context.channel().id(), courseId);
		SingleCourseEnv env = courseEnvMap.get(clientMsg.getCourseId());
		UserPO user = new UserPO();
		user.setDomainId(-5);
		env.subscriber.put(context.channel().id(), user);
		env.sessions.put(context.channel().id(), context);
	}



	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		serverHelper.closeSession(sessionCourseMap.get(ctx.channel().id()), ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		SecurityContextHolder.getContextHolder().remove();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("ws error...", cause);
	}

	public static class ClientMessage {
		public static final String ADMIN_TOKEN = "guFvC1iN6cnFVV257dwDVbEqttQ40vcJUzvAWvBdw6k0H8Tqblk1xXs2wbO95INF";
		private String init = N;
		private Integer courseId;
		private String token;
		private String msg;
		private String clientType = CLIENT_USER;


		public boolean initValid() {
			return Y.equalsIgnoreCase(init) && courseId != null && !StringUtils.isNullEmpty(token);
		}
		public boolean msgValid() {
			return !StringUtils.isNullEmpty(token) && courseId != null && !StringUtils.isNullEmpty(msg);
		}

		public Integer getCourseId() {
			return courseId;
		}
		public void setCourseId(Integer courseId) {
			this.courseId = courseId;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getInit() {
			return init;
		}
		public void setInit(String init) {
			this.init = init;
		}
		public String getClientType() {
			return clientType;
		}
		public void setClientType(String clientType) {
			this.clientType = clientType;
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

	private static final String CLIENT_USER = "CLIENT_USER";
	private static final String CLIENT_ADMIN = "CLIENT_ADMIN";
	private static final String CLIENT_PPT = "CLIENT_PPT";
}
