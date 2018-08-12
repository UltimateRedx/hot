package com.hotelpal.service.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
	
	protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@ExceptionHandler
	@ResponseBody
	public String handleException(HttpServletRequest request, Exception ex) {
		logger.error("exception..", ex);
		return "";
	}
}
