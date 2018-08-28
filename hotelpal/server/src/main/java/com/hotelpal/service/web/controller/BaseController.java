package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class BaseController {
	
	protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@ExceptionHandler(value = Throwable.class)
	@ResponseBody
	public BaseDTO<Void> handleException(HttpServletRequest request, Exception ex) {
		logger.error("exception..", ex);
		BaseDTO<Void> res = new BaseDTO<>();
		res.setSuccess(false);
		res.setMessages(Arrays.asList(ex.getMessage()));
		res.setCode(ServiceException.getExceptionCode( ((ServiceException) ex).getType()));
		return res;
	}
}
