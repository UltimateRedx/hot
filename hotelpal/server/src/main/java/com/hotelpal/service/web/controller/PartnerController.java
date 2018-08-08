package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.dto.BaseDTO;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.WXSNSUserInfoMO;
import com.hotelpal.service.common.mo.WXWebAccessTokenMO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.ContentService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.live.LiveUserService;
import com.hotelpal.service.service.parterner.wx.WXOpenService;
import com.hotelpal.service.service.parterner.wx.WXService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class PartnerController{
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@ExceptionHandler
	public void handleException(Exception ex) {
		logger.error("PartnerController exception..", ex);
		if (ex instanceof ServiceException) {
			throw (ServiceException) ex;
		} else {
			throw new ServiceException(ex);
		}
	}
	
	@Resource
	private UserService userService;
	@Resource
	private ContentService contentService;
	@Resource
	private WXOpenService wxOpenService;
	@Resource
	private LiveUserService liveUserService;
	
	@RequestMapping(value = "/WeChat/receiveRedirect")
	@ResponseBody
	public BaseDTO<Map<String, String>> accessRedirect(String code) {
		WXWebAccessTokenMO res = WXService.getWebAccessToken(code);
		WXSNSUserInfoMO userInfo = WXService.getSNSUserInfo(res.getAccess_token(), res.getOpenid());
		userService.saveUserInfo(userInfo);
		Map<String, String> map = new HashMap<>();
		map.put("token", userInfo.getOpenid());
		return new BaseDTO<>(map);
	}
	
	@RequestMapping(value = "/WeChat/receivePayedData")
	@ResponseBody
	public void receivePayedData(HttpServletRequest request, HttpServletResponse res) {
		try (InputStream is = request.getInputStream()) {
			contentService.receiveUpdateWxPayResult(is);
		} catch (Exception e) {
			logger.error("receivePayedData Exception...", e);
			throw new ServiceException(e);
		}
		Map<String, Object> params = new HashMap<>();
		params.put("return_code", "SUCCESS");
		try {
			DocumentFactory documentFactory = DocumentFactory.getInstance();
			Element root = documentFactory.createElement("xml");
			Document document = documentFactory.createDocument(root);
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				Element element = documentFactory.createElement(entry.getKey());
				root.add(element);
				element.setText(String.valueOf(entry.getValue()));
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLWriter xmlWriter = new XMLWriter();
			xmlWriter.setOutputStream(baos);
			xmlWriter.write(document);
			String xml = new String(baos.toByteArray(), "UTF-8");
			OutputStream os = res.getOutputStream();
			os.write(xml.getBytes());
			os.flush();
			os.close();
		} catch (Exception e) {
			logger.error("wx pay feed back exception... ", e);
		}
	}

	@RequestMapping(value = "/WeChat/receivePushMsg/{appId}")
	@ResponseBody
	public void receivePushMsg(HttpServletRequest request, String signature, String timestamp, String nonce, String echostr, @PathVariable String appId) {
		if (WXService.APPID.equalsIgnoreCase(appId)) {
			try {
				liveUserService.handleWxEvent(request.getInputStream());
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
	}

	private boolean checkServer(String signature, String timestamp, String nonce, String echostr) {
		Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		map.put("token", "ttttttttttooooooooookkkkkken");
		map.put("timestamp", timestamp);
		map.put("nonce", nonce);
		String s = String.join("", map.values());
		String digest = DigestUtils.sha1Hex(s);
		return digest.equalsIgnoreCase(signature);
//		return true;
	}
	
	
	@RequestMapping(value = "/thirdParty/wxAuthChange")
	@ResponseBody
	public String wxAuthChange(HttpServletRequest request) {
		String xml;
		try {
			wxOpenService.authChange(request.getInputStream());
		} catch (Exception e) {
			logger.error("IoException", e);
			return "success";
		}

		return "success";
	}

	@RequestMapping(value = "/thirdParty/getAuthorizeParams")
	@ResponseBody
	public PackVO<Map<String, String>> getAuthorizeParams() {
		Map<String, String> res = new HashMap<>();
		res.put("componentAppId", WXOpenService.OPEN_APPID);
		res.put("preAuthCode", wxOpenService.getPreAuthCode(wxOpenService.getTicket()));
		PackVO<Map<String, String>> vo = new PackVO<>();
		vo.setVo(res);
		return vo;
	}

	@RequestMapping(value = "/thirdParty/authorizerCallback")
	@ResponseBody
	public String authorizerCallback(HttpServletRequest request) {
		try {
			String input = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
			logger.info("authorizerCallback input: " + input);
		} catch (Exception e) {
			logger.error("IoException", e);
			return "success";
		}
		return "success";
	}
}
