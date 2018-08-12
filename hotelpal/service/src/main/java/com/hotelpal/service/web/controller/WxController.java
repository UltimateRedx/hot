package com.hotelpal.service.web.controller;

import com.hotelpal.service.service.parterner.wx.WXService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class WxController extends BaseController{
	@Resource
	private WXService wxService;

	@RequestMapping(value = "/wx/accessToken")
	@ResponseBody
	public String getAccessToken() {
		return wxService.getAccessToken();
	}

	@RequestMapping(value = "/wx/jsApiTicket")
	@ResponseBody
	public String getJsApiTicket() {
		return wxService.getJsApiTicket();
	}

	@RequestMapping(value = "/wx/renewAccessToken")
	@ResponseBody
	public String renewAccessToken() {
		wxService.renewAccessToken();
		return "";
	}
}
