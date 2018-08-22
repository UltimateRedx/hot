package com.hotelpal.service.web.controller.liveCourseController.admin;

import com.hotelpal.service.common.context.SecurityContextHolder;
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

//@ServerEndpoint(value = "/live/op/{courseId}", configurator = SpringConfigurator.class)
public class ImageControlController {
//	private static final Logger logger = LoggerFactory.getLogger(ImageControlController.class);
//
//	@Resource
//	private LiveChatService liveChatService;
//
//	@OnOpen
//	public void onOpen(@PathParam(value = "courseId") Integer courseId, Session session) {
//		try {
//			UserPO opUser = new UserPO();
//			opUser.setDomainId(-5);
//			liveChatService.join(courseId, session, opUser);
//		} catch (Exception e) {
//			logger.error("op ws Exception...", e);
//			try {
//				session.close();
//			} catch (IOException ioe) {
//				logger.error("ImageControl WS onOpen IOException...", ioe);
//			}
//		} finally {
//			SecurityContextHolder.getContextHolder().remove();
//		}
//	}
//	@OnMessage
//	public void onMessage(@PathParam(value = "courseId") Integer courseId, Session session, String msg) {
//		liveChatService.changeImg(courseId, session, msg);
//		SecurityContextHolder.getContextHolder().remove();
//	}
//	@OnClose
//	public void onClose(@PathParam(value = "courseId") Integer courseId, Session session) {
//		try {
//			liveChatService.closeSession(courseId, session);
//		} catch (IOException ioe) {
//			logger.error("ImageControl WS onClose IOException...", ioe);
//		} finally {
//			SecurityContextHolder.getContextHolder().remove();
//		}
//	}
//
//	@OnError
//	public void onError(Throwable t, @PathParam(value = "courseId") Integer courseId, Session session) {
//		if (t instanceof EOFException) {
//			try {
//				liveChatService.closeSession(courseId, session);
//			} catch(IOException ioe){
//				logger.error("WS onClose IOException...", ioe);
//			}
//		} else {
//			logger.error("ImageControl WS onError...", t);
//		}
//		SecurityContextHolder.getContextHolder().remove();
//	}
}
