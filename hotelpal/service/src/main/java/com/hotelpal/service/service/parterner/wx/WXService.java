package com.hotelpal.service.service.parterner.wx;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.WXPropertyDao;
import com.hotelpal.service.common.enums.WXProperty;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.*;
import com.hotelpal.service.common.po.WXPropertyPO;
import com.hotelpal.service.common.so.WXPropertySO;
import com.hotelpal.service.common.utils.HttpGetUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class WXService {
	@Resource
	private WXService wxService;
	@Resource
	private WXPropertyDao dao;

	private static final Logger logger = LoggerFactory.getLogger(WXService.class);
	private static final String APPID = "wxfe666ebbf0e42897";//;"wxf766d8ef4d4fdaa6"
	private static final String SECRET = "7750a680bea69c23e0dbd987dc3ef07f";//;"e4f8eadae9e5cdfe5565e34c547a72c3"

	private static final String GET_ACCESS_TOKEN_PREFIX = "https://api.weixin.qq.com/cgi-bin/token?";
	private static final String GET_JS_API_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?";

	private static final Long EXPIRE_ADVANCE_TIME_MILLISECOND = 2 * 60 * 1000L;//2分钟

	private static Lock lock = new ReentrantLock();

	public synchronized String getAccessToken() {
		WXPropertySO so = new WXPropertySO();
		so.setType(WXProperty.ACCESS_TOKEN.toString());
		WXPropertyPO po = dao.getOne(so);
		//Will only run once.
		if (po == null) {
			//getNewOne
			AccessToken at = this.getNewAccessToken();
			WXPropertyPO property = new WXPropertyPO();
			property.setType(WXProperty.ACCESS_TOKEN.toString());
			property.setValue(at.getAccessToken());
			property.setExpireIn(at.getExpiresIn());
			wxService.create(property);
			return at.getAccessToken();
		} else {
			long trueExpireTime = po.getUpdateTime().getTime() + po.getExpireIn() * 1000;
			if (new Date().after(new Date(trueExpireTime - EXPIRE_ADVANCE_TIME_MILLISECOND)) && new Date().before(new Date(trueExpireTime))) {
				//return oldOne and getNewOne
				logger.info("Asynchronously renew access_token...");
				CompletableFuture.runAsync(() -> {
					if (lock.tryLock()) {
						try {
							renewAccessToken();
						} catch (Exception e) {
							logger.error("GetUpdate new access_token error...", e);
						} finally {
							lock.unlock();
						}
					}
				});
			} else if (new Date().after(new Date(trueExpireTime))) {
				//getNewOne.
				logger.info("Renew access_token...");
				AccessToken at = getNewAccessToken();
				po.setValue(at.getAccessToken());
				po.setExpireIn(at.getExpiresIn());
				wxService.update(po);
				return at.getAccessToken();
			}
			return po.getValue();
		}
	}

	public synchronized String getJsApiTicket() {
		WXPropertySO so = new WXPropertySO();
		so.setType(WXProperty.JS_API_TICKET.toString());
		WXPropertyPO po = dao.getOne(so);
		if (po == null) {
			//getNewOne and set to db.
			JsApiTicket ticket = getNewJsApiTicket(getAccessToken());
			WXPropertyPO property = new WXPropertyPO();
			property.setType(WXProperty.JS_API_TICKET.toString());
			property.setValue(ticket.getTicket());
			property.setExpireIn(ticket.getExpiresIn());
			wxService.create(property);
			return ticket.getTicket();
		} else {
			long trueExpireTime = po.getUpdateTime().getTime() + po.getExpireIn() * 1000;
			if (new Date().after(new Date(trueExpireTime - EXPIRE_ADVANCE_TIME_MILLISECOND)) && new Date().before(new Date(trueExpireTime))) {
				//return oldOne and getNewOne
				CompletableFuture.runAsync(() -> {
					if (lock.tryLock()) {
						try {
							JsApiTicket ticket = getNewJsApiTicket(getAccessToken());
							//Parameters like po from parent Thread/Process might be lost.
							WXPropertySO s = new WXPropertySO();
							s.setType(WXProperty.JS_API_TICKET.toString());
							WXPropertyPO p = dao.getOne(s);
							p.setExpireIn(ticket.getExpiresIn());
							p.setValue(ticket.getTicket());
							dao.update(p);
						} catch (Exception e) {
							logger.error("GetUpdate new js_api_ticket error...", e);
						} finally {
							lock.unlock();
						}
					}
				});
			} else if (new Date().after(new Date(trueExpireTime))) {
				//getNewOne.
				JsApiTicket ticket = getNewJsApiTicket(getAccessToken());
				po.setValue(ticket.getTicket());
				po.setExpireIn(ticket.getExpiresIn());
				wxService.update(po);
				return ticket.getTicket();
			}
			return po.getValue();
		}
	}

	private AccessToken getNewAccessToken() {
		String url = GET_ACCESS_TOKEN_PREFIX + "grant_type=client_credential&appid=" + APPID + "&secret=" + SECRET;
		HttpParams params = new HttpParams();
		params.setUrl(url);
		String res = HttpGetUtils.executeGet(params);
		Map<String, Object> resMap = JSON.parseObject(res, HashMap.class);
		if (StringUtils.isNullEmpty(String.valueOf(resMap.get("access_token"))))
			throw new ServiceException(ServiceException.WX_COMMUNICATION_FAILED + resMap.get("errmsg"));
		AccessToken at = new AccessToken();
		at.setAccessToken(String.valueOf(resMap.get("access_token")));
		at.setExpiresIn(Integer.parseInt(String.valueOf(resMap.get("expires_in"))));
		return at;
	}

	public synchronized void renewAccessToken() {
		AccessToken at = getNewAccessToken();
		//Parameters like po from parent Thread/Process might be lost.
		WXPropertySO s = new WXPropertySO();
		s.setType(WXProperty.ACCESS_TOKEN.toString());
		WXPropertyPO p = dao.getOne(s);
		p.setExpireIn(at.getExpiresIn());
		p.setValue(at.getAccessToken());
		wxService.update(p);
	}

	private JsApiTicket getNewJsApiTicket(String accessToken) {
		if (StringUtils.isNullEmpty(accessToken)) throw new NullPointerException();
		HttpParams params = new HttpParams();
		params.setUrl(GET_JS_API_TICKET_URL + "access_token=" + accessToken + "&type=jsapi");
		String res = HttpGetUtils.executeGet(params);
		Map<String, Object> resMap = JSON.parseObject(res, HashMap.class);
		if (StringUtils.isNullEmpty(String.valueOf(resMap.get("ticket"))))
			throw new ServiceException(ServiceException.WX_COMMUNICATION_FAILED + resMap.get("errmsg"));
		JsApiTicket ticket = new JsApiTicket();
		ticket.setExpiresIn(Integer.parseInt(String.valueOf(resMap.get("expires_in"))));
		ticket.setTicket(String.valueOf(resMap.get("ticket")));
		return ticket;
	}


	private class AccessToken {
		private Integer expiresIn;
		private String accessToken;
		Integer getExpiresIn() {
			return expiresIn;
		}
		void setExpiresIn(Integer expiresIn) {
			this.expiresIn = expiresIn;
		}
		String getAccessToken() {
			return accessToken;
		}
		void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
	}
	private class JsApiTicket {
		private String ticket;
		private Integer expiresIn;
		String getTicket() {
			return ticket;
		}
		void setTicket(String ticket) {
			this.ticket = ticket;
		}
		private Integer getExpiresIn() {
			return expiresIn;
		}
		void setExpiresIn(Integer expiresIn) {
			this.expiresIn = expiresIn;
		}
	}

	@Transactional
	public void create(WXPropertyPO po) {
		if (po != null) {
			dao.create(po);
		}
	}
	@Transactional
	public void update(WXPropertyPO po) {
		if (po != null) {
			dao.update(po);
		}
	}
}