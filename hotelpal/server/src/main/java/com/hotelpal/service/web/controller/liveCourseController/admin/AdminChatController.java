package com.hotelpal.service.web.controller.liveCourseController.admin;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.UserPO;
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

@ServerEndpoint(value = "/admin/live/chat/{courseId}/{token}", configurator = SpringConfigurator.class)
public class AdminChatController {
	private static final Logger logger = LoggerFactory.getLogger(AdminChatController.class);
	@Resource
	private LiveChatService liveChatService;
	
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "token") String token, @PathParam(value = "courseId") Integer courseId) {
		try {
			if (!LiveChatService.ADMIN_TOKEN.equals(token)) {
				throw new ServiceException(ServiceException.COMMON_ILLEGAL_ACCESS);
			}
			UserPO adminUser = new UserPO();
			adminUser.setDomainId(-1);
			liveChatService.join(courseId, session, adminUser);
		} catch (Exception e) {
			logger.error("ADMIN WS onOpen IOException...", e);
			try {
				session.close();
			} catch (IOException ioe) {
				logger.error("ADMIN WS onOpen&close IOException...", ioe);
			}
		} finally {
			SecurityContextHolder.getContextHolder().remove();
		}
	}
	@OnMessage
	public void onMessage(Session session, String msg, @PathParam(value = "courseId") Integer courseId) {
		liveChatService.assistantMessage(courseId, session, msg);
		SecurityContextHolder.getContextHolder().remove();
	}
	@OnClose
	public void onClose(@PathParam(value = "courseId") Integer courseId, Session session) {
		try {
			liveChatService.closeSession(courseId, session);
		} catch (IOException ioe) {
			logger.error("ADMIN WS onClose IOException...", ioe);
		} finally {
			SecurityContextHolder.getContextHolder().remove();
		}
	}

	@OnError
	public void onError(Throwable t, @PathParam(value = "courseId") Integer courseId, Session session) {
		if (t instanceof EOFException) {
			try {
				liveChatService.closeSession(courseId, session);
			} catch(IOException ioe){
				logger.error("WS onClose IOException...", ioe);
			}
		} else {
			logger.error("ADMIN WS onError...", t);
		}
		SecurityContextHolder.getContextHolder().remove();
	}
}


