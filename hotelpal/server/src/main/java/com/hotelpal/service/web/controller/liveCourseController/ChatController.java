package com.hotelpal.service.web.controller.liveCourseController;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.live.LiveChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;

//@ServerEndpoint(value = "/live/chat/{courseId}/{token}", configurator = SpringConfigurator.class)
public class ChatController {
//	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
//	@Resource
//	private LiveChatService liveChatService;
//	@Resource
//	private UserService userService;
//
//	@OnOpen
//	public void onOpen(Session session, @PathParam(value = "token") String token, @PathParam(value = "courseId") Integer courseId) {
//		try {
//			UserPO user = userService.getUserByOpenId(token);
//			liveChatService.join(courseId, session, user);
//		} catch (Exception e) {
//			logger.error("WS onOpen IOException...", e);
//			try {
//				session.close();
//			} catch (IOException ioe) {
//				logger.error("WS onOpen&close IOException...", ioe);
//			}
//		} finally {
//			SecurityContextHolder.getContextHolder().remove();
//		}
//	}
//
//	@OnClose
//	public void onClose(@PathParam(value = "courseId") Integer courseId, Session session) {
//		try {
//			liveChatService.closeSession(courseId, session);
//		} catch (IOException ioe) {
//			logger.error("WS onClose IOException...", ioe);
//		} finally {
//			SecurityContextHolder.getContextHolder().remove();
//		}
//	}
//
//	@OnMessage
//	public void onMessage(@PathParam(value = "courseId") Integer courseId, Session session, String msg) {
//		if (StringUtils.isNullEmpty(msg)) return;
//		liveChatService.submitMsg(courseId, session, msg);
//		SecurityContextHolder.getContextHolder().remove();
//	}
//
//	/**
//	 * This method is used o debug.
//	 */
//	@OnError
//	public void onError(Throwable t, @PathParam(value = "courseId") Integer courseId, Session session) {
//		if (t instanceof EOFException) {
//			try {
//				liveChatService.closeSession(courseId, session);
//			} catch(IOException ioe){
//				logger.error("WS onClose IOException...", ioe);
//			}
//		} else {
//			SecurityContextHolder.getContextHolder().remove();
//			logger.error("WS service error...", t);
//		}
//	}
}
