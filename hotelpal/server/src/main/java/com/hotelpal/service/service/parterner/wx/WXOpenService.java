package com.hotelpal.service.service.parterner.wx;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.WXPropertyDao;
import com.hotelpal.service.common.enums.WXProperty;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.po.WXPropertyPO;
import com.hotelpal.service.common.so.WXPropertySO;
import com.hotelpal.service.common.utils.HttpPostUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.utils.XMLUtils;
import com.hotelpal.service.common.utils.wx.WXEncodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class WXOpenService {
	private static final Logger logger = LoggerFactory.getLogger(WXOpenService.class);
	private static final String OPEN_AUTH_CHANGE_NAME_APPID = "AppId";
	private static final String OPEN_AUTH_CHANGE_NAME_ENCRYPT = "Encrypt";
	private static final String OPEN_AUTH_CHANGE_NAME_INFO_TYPE = "InfoType";
	private static final String OPEN_AUTH_CHANGE_NAME_COMPONENT_VERIFY_TICKET = "ComponentVerifyTicket";
	public static final String OPEN_APPID = "wxfef930de3f27e265";
	private static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
	private static final String GET_PRE_AUTH_CODE_URL = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=@accessToken";

	private static final String OPEN_SECRET = WXEncodeUtils.OPEN_SECRET;
	private static final Long EXPIRE_ADVANCE_TIME_MILLISECOND = 2 * 60 * 1000L;//2分钟

	@Resource
	private WXPropertyDao wxPropertyDao;
	@Resource
	private WXOpenService wxOpenService;

	@Transactional
	public void authChange(InputStream is) {
		Map<String, String> resMap = XMLUtils.listMapContent(is);
		if (logger.isDebugEnabled()) {
			logger.debug("wxAuthChange input xml: " + resMap);
		}
		if (!resMap.get(OPEN_AUTH_CHANGE_NAME_APPID).equalsIgnoreCase(OPEN_APPID)) return;
		String xml = WXEncodeUtils.decrypt(resMap.get(OPEN_AUTH_CHANGE_NAME_ENCRYPT));
		Map<String, String> res = XMLUtils.listMapContent(xml);
		String cvt = res.get(OPEN_AUTH_CHANGE_NAME_COMPONENT_VERIFY_TICKET);
		if(StringUtils.isNullEmpty(cvt)) return;
		wxPropertyDao.insertUpdate(WXProperty.COMPONENT_VERIFY_TICKET.toString(), cvt, null);
	}

	public String getTicket() {
		WXPropertySO so = new WXPropertySO();
		so.setType(WXProperty.COMPONENT_VERIFY_TICKET.toString());
		WXPropertyPO po = wxPropertyDao.getOne(so);
		return po.getValue();
	}

	private static final Lock lock = new ReentrantLock();
	public synchronized String getComponentAccessToken(String ticket) {
		WXPropertySO so = new WXPropertySO();
		so.setType(WXProperty.COMPONENT_ACCESS_TOKEN.toString());
		WXPropertyPO po = wxPropertyDao.getOne(so);
		//Will only run once.
		if (po == null) {
			//getNewOne
			ComponentAccessToken at = this.getNewAccessToken(ticket);
			WXPropertyPO property = new WXPropertyPO();
			property.setType(WXProperty.COMPONENT_ACCESS_TOKEN.toString());
			property.setValue(at.getAccessToken());
			property.setExpireIn(at.getExpiresIn());
			wxOpenService.create(property);
			return at.getAccessToken();
		} else {
			long trueExpireTime = po.getUpdateTime().getTime() + po.getExpireIn() * 1000;
			if (new Date().after(new Date(trueExpireTime - EXPIRE_ADVANCE_TIME_MILLISECOND)) && new Date().before(new Date(trueExpireTime))) {
				//return oldOne and getNewOne
				CompletableFuture.runAsync(() -> {
					if (lock.tryLock()) {
						ComponentAccessToken at = getNewAccessToken(ticket);
						//Parameters like po from parent Thread/Process might be lost.
						WXPropertySO s = new WXPropertySO();
						s.setType(WXProperty.COMPONENT_ACCESS_TOKEN.toString());
						WXPropertyPO p = wxPropertyDao.getOne(s);
						p.setExpireIn(at.getExpiresIn());
						p.setValue(at.getAccessToken());
						wxOpenService.update(p);
						lock.unlock();
					}
				});
			} else if (new Date().after(new Date(trueExpireTime))) {
				//getNewOne.
				ComponentAccessToken at = getNewAccessToken(ticket);
				po.setValue(at.getAccessToken());
				po.setExpireIn(at.getExpiresIn());
				wxOpenService.update(po);
				return at.getAccessToken();
			}
			return po.getValue();
		}
	}
	private ComponentAccessToken getNewAccessToken(String ticket) {
		HttpParams params = new HttpParams();
		params.setUrl(GET_ACCESS_TOKEN_URL);
		Map<String, String> postParam = new HashMap<>();
		postParam.put("component_appid", OPEN_APPID);
		postParam.put("component_appsecret", OPEN_SECRET);
		postParam.put("component_verify_ticket", ticket);
		params.setRequestEntity(JSON.toJSONString(postParam));
		String res = HttpPostUtils.postMap(params);
		Map<String, Object> resMap = JSON.parseObject(res, HashMap.class);
		if (StringUtils.isNullEmpty(String.valueOf(resMap.get("component_access_token"))))
			throw new ServiceException(ServiceException.WX_COMMUNICATION_FAILED + JSON.toJSONString(resMap));
		ComponentAccessToken at = new ComponentAccessToken();
		at.setAccessToken(String.valueOf(resMap.get("component_access_token")));
		at.setExpiresIn(Integer.parseInt(String.valueOf(resMap.get("expires_in"))));
		return at;
	}

	public String getPreAuthCode(String ticket) {
		String accessToken = getComponentAccessToken(ticket);
		HttpParams params = new HttpParams();
		params.setUrl(GET_PRE_AUTH_CODE_URL.replaceFirst("@accessToken", accessToken));
		params.setRequestEntity("{\"component_appid\":\"@appId\"}".replaceFirst("@appId", OPEN_APPID));
		String res = HttpPostUtils.postMap(params);
		Map<String, Object>resMap = JSON.parseObject(res, HashMap.class);
		String code = String.valueOf(resMap.get("pre_auth_code"));
		if (StringUtils.isNullEmpty(code)) {
			throw new ServiceException(ServiceException.WX_COMMUNICATION_FAILED + JSON.toJSONString(resMap));
		}
		return code;
	}
	private class ComponentAccessToken {
		private String accessToken;
		private Integer expiresIn;

		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public Integer getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(Integer expiresIn) {
			this.expiresIn = expiresIn;
		}
	}
	private class PreAuthCode {
		private String preAuthCode;
		private Integer expiresIn;

		public String getPreAuthCode() {
			return preAuthCode;
		}
		public void setPreAuthCode(String preAuthCode) {
			this.preAuthCode = preAuthCode;
		}
		public Integer getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(Integer expiresIn) {
			this.expiresIn = expiresIn;
		}
	}

	@Transactional
	public void create(WXPropertyPO po) {
		if (po != null) {
			wxPropertyDao.create(po);
		}
	}

	@Transactional
	public void update(WXPropertyPO po) {
		if (po != null) {
			wxPropertyDao.update(po);
		}
	}
}
