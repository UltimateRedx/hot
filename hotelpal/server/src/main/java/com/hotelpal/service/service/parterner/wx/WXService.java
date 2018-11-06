package com.hotelpal.service.service.parterner.wx;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.basic.mysql.dao.WXPropertyDao;
import com.hotelpal.service.basic.mysql.dao.WXUserInfoDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.WXProperty;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.*;
import com.hotelpal.service.common.po.UserRelaPO;
import com.hotelpal.service.common.po.WXPropertyPO;
import com.hotelpal.service.common.po.WXUserInfoPO;
import com.hotelpal.service.common.so.WXPropertySO;
import com.hotelpal.service.common.utils.HttpGetUtils;
import com.hotelpal.service.common.utils.HttpPostUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.utils.XMLUtils;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class WXService {
	@Resource
	private WXService wxService;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	@Resource
	private WXUserInfoDao wxUserInfoDao;

	private static final Logger logger = LoggerFactory.getLogger(WXService.class);
	public static final String APPID = "wxfe666ebbf0e42897";//;"wxf766d8ef4d4fdaa6"
	private static final String SECRET = "7750a680bea69c23e0dbd987dc3ef07f";//;"e4f8eadae9e5cdfe5565e34c547a72c3"
	private static final String WEB_GET_ACCESS_TOKEN_PREFIX = "https://api.weixin.qq.com/sns/oauth2/access_token?";
	private static final String WEB_GET_SNSUSERINFO_PREFIX = "https://api.weixin.qq.com/sns/userinfo?";
	private static final String WEB_GET_USERINFO_PREFIX = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=@accessToken&openid=@openId&lang=zh_CN";

	private static final String GET_QR_CODE_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=@accessToken";
	private static final String GET_QR_CODE_IMG_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=@ticket";
	private static final String CUSTOM_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=@accessToken";
	
	
	private static final String MCHID = "1481600082";

	private static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static final String PAY_KEY = "BFTDRRWGXLBF08KQGVSYOAKMQCXK1WJ3";//BFTDRRWGXLBF08KQGVSYOAKMQCXK1WJ3
	private static final String PAY_NOTIFY_URL = PropertyHolder.getProperty("wx.msg.pay.notify.domain");

	private static final String DOMAIN_NAME = PropertyHolder.getProperty("DOMAIN_NAME_HTTP");
	//直播邀请二维码的有效期，最长30天。一般从报名到开讲3天。
	private static final Integer QR_CODE_EXPIRES_IN = 5 * 24 * 60 * 60;

	
	
	@Resource
	private WXPropertyDao dao;

	private static Map<String, Object> getPayRequestParams() {
		String nonce = RandomStringUtils.random(32, true, true);
		Map<String, Object> requestMap = new TreeMap<>();
		requestMap.put("appid", APPID);
		requestMap.put("mch_id", MCHID);
		requestMap.put("nonce_str", nonce);
		requestMap.put("notify_url", PAY_NOTIFY_URL);
		requestMap.put("trade_type", "JSAPI");
		requestMap.put("sign_type", "MD5");
		requestMap.put("scene_info", "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"" + DOMAIN_NAME + "\",\"wap_name\": \"课程购买\"}}");
		return requestMap;
	}
	
	public static Map<String, Object> createPrePay(WXPreOrderMO mo) {
		Map<String, Object> map = getPayRequestParams();
		map.put("body", mo.getBody());
		map.put("out_trade_no", mo.getTradeNo());
		map.put("openid", mo.getOpenId());
		map.put("spbill_create_ip", mo.getIp());
		map.put("total_fee", mo.getFee());
		String stringA = StringUtils.formUrl(map);
		String tmp = stringA + "&key=" + PAY_KEY;
		String sign = DigestUtils.md5Hex(tmp).toUpperCase();
		map.put("sign", sign);
		
		Document doc = XMLUtils.mapToXML(map);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLWriter xmlWriter = new XMLWriter();
		try {
			xmlWriter.setOutputStream(baos);
			xmlWriter.write(doc);
			String xml = new String(baos.toByteArray(), "UTF-8");
			HttpParams httpParams = new HttpParams();
			httpParams.setRequestEntity(xml);
			httpParams.setUrl(PAY_URL);
			String resp = HttpPostUtils.postMap(httpParams);
			if (StringUtils.isNullEmpty(resp)) {
				throw new ServiceException(ServiceException.HTTP_BLANK_RESPONSE);
			}
			Map<String, Object> retMap = parseXML(XMLUtils.parseText(resp));
			String result_code = (String) retMap.get("result_code");
			if (!"SUCCESS".equals(result_code)) {
				throw new ServiceException((String) retMap.get("err_code_des"));
			}
			String prepayId = (String) retMap.get("prepay_id");
			String nonceStr = String.valueOf(retMap.get("nonce_str"));
			return WXSecondarySign(nonceStr, prepayId);
		} catch (Exception e) {
			logger.error("parse wx createPayOrder failed...", e);
			throw new ServiceException(e);
		}
	}
	
	private static Map<String, Object> parseXML(Document doc) {
		Element root = doc.getRootElement();
		Element return_code = root.element("return_code");
		if (!"SUCCESS".equals(return_code.getTextTrim())) {
			String return_msg = root.element("return_msg").getTextTrim();
			throw new ServiceException(return_msg);
		}
		Map<String, Object> param = new HashMap<>();
		for (Object o : root.elements()) {
			Element e = (Element) o;
			String name = e.getName();
			String value = e.getTextTrim();
			param.put(name, value);
		}
		return param;
	}
	
	private static Map<String, Object> WXSecondarySign(String nonce, String prePayId) {
		if (StringUtils.isNullEmpty(nonce)) {
			nonce = RandomStringUtils.random(32, true, true);
		}
		Map<String, Object> map = new TreeMap<>();
		long timeStamp = new Date().getTime() / 1000;
		map.put("appId", APPID);
		map.put("nonceStr", nonce);
		map.put("package", "prepay_id=" + prePayId);
		map.put("signType", "MD5");
		map.put("timeStamp", String.valueOf(timeStamp));
		String toSignStr = StringUtils.formUrl(map) + "&key=" + PAY_KEY;
		String secondarySign = DigestUtils.md5Hex(toSignStr).toUpperCase();
		map.put("paySign", secondarySign);
		return map;
	}
	
	public Map<String, Object> getThirdSign(String url) {
		if (StringUtils.isNullEmpty(url)) {
			return null;
		}
		int index = url.indexOf("#");
		Map<String, Object> map = new TreeMap<>();
		map.put("timestamp", new Date().getTime() / 1000);
		map.put("noncestr", RandomStringUtils.random(32, true, true));
		map.put("url", index > 0 ? url.substring(0, index) : url);
		map.put("jsapi_ticket", getJsApiTicket());
		map.put("sign", DigestUtils.sha1Hex(StringUtils.formUrl(map)));
		map.put("appid", APPID);
		map.remove("jsapi_ticket");
		return map;
	}
	
	public static WXWebAccessTokenMO getWebAccessToken(String code) {
		String fullURL = WEB_GET_ACCESS_TOKEN_PREFIX + "appid=" + APPID +
				"&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
		HttpParams params = new HttpParams();
		params.setUrl(fullURL);
		
		String resStr = HttpGetUtils.executeGet(params);
		WXWebAccessTokenMO res = JSON.parseObject(resStr, WXWebAccessTokenMO.class);
		if (res == null || !StringUtils.isNullEmpty(res.getErrcode())) {
			throw new ServiceException("Get web access token exception... Response: " + resStr);
		}
		return res;
	}
	public static WXSNSUserInfoMO getSNSUserInfo(String accessToken, String openId) {
		String url = WEB_GET_SNSUSERINFO_PREFIX + "access_token=" + accessToken +
			"&openid=" + openId + "&lang=zh_CN";
		HttpParams params = new HttpParams();
		params.setUrl(url);
		String resStr =  HttpGetUtils.executeGet(params);
		WXSNSUserInfoMO res = JSON.parseObject(resStr, WXSNSUserInfoMO.class);
		if (res == null || !StringUtils.isNullEmpty(res.getErrcode())) {
			throw new ServiceException("Get web snsUserInfo exception... Response: " + resStr);
		}
		return res;
    }
	public static WXUserInfoMO getUserInfo(String accessToken, String openId) {
		String url = WEB_GET_USERINFO_PREFIX.replaceFirst("@accessToken", accessToken).replaceFirst("@openId",openId);
		HttpParams params = new HttpParams();
		params.setUrl(url);
		String resStr =  HttpGetUtils.executeGet(params);
		WXUserInfoMO res = JSON.parseObject(resStr, WXUserInfoMO.class);
		if (res == null || !StringUtils.isNullEmpty(res.getErrcode())) {
			throw new ServiceException("Get web userInfo exception... Response: " + resStr);
		}
		if (res.getSubscribe_time() != null) {
			res.setSubscribe_time(new Date(res.getSubscribe_time().getTime() * 1000));
		}
		return res;
	}

	private static final String WX_MIDDLE_SERVER = "http://hotelpal.cn";
	public synchronized String getAccessToken() {
		HttpParams params = new HttpParams();
		params.setUrl(WX_MIDDLE_SERVER + "/service/wx/accessToken");
		return HttpGetUtils.executeGet(params);
	}
	public synchronized String getJsApiTicket() {
		HttpParams params = new HttpParams();
		params.setUrl(WX_MIDDLE_SERVER + "/service/wx/jsApiTicket");
		return HttpGetUtils.executeGet(params);
	}
	public synchronized void renewAccessToken() {
		HttpParams params = new HttpParams();
		params.setUrl(WX_MIDDLE_SERVER + "/service/wx/renewAccessToken");
		HttpGetUtils.executeGet(params);
	}

	
	/**
	 * 创建临时二维码
	 * @return 请求二维码图片的ticket
	 */
	public String createQrCode(Integer liveCourseId, String fromOpenId) {
		HttpParams params = new HttpParams();
		params.setUrl(GET_QR_CODE_CREATE_URL.replaceFirst("@accessToken", getAccessToken()));
		params.setRequestEntity("{\"expire_seconds\": @time, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"@str\"}}}"
				.replaceFirst("@time", String.valueOf(QR_CODE_EXPIRES_IN)).replaceFirst("@str", String.join("#", String.valueOf(liveCourseId), fromOpenId)));
		logger.info("create qrCode post data: " + params.getRequestEntity());
		String res = HttpPostUtils.postMap(params);
		Map<String, String> resMap = JSON.parseObject(res, HashMap.class);
		if (StringUtils.isNullEmpty(resMap.get("ticket"))) {
			throw new ServiceException("创建二维码失败：" + res);
		}
		return resMap.get("ticket");
	}
	
	public static byte[] getQrCodeImg(String ticket) {
		HttpParams params = new HttpParams();
		try {
			params.setUrl(GET_QR_CODE_IMG_URL.replaceFirst("@ticket", URLEncoder.encode(ticket, "UTF-8")));
		}catch (UnsupportedEncodingException e) {
			throw new ServiceException(e);
		}
		return HttpGetUtils.executeGetBytes(params);
	}
	
	public String getQrCodeTicket(Integer liveCourseId) {
		Integer domainId = SecurityContextHolder.getUserDomainId();
		String openId = SecurityContextHolder.getUserOpenId();
		WXPropertySO so = new WXPropertySO();
		so.setType(WXProperty.QR_CODE_TICKET.toString() + liveCourseId + "#" + domainId);
		WXPropertyPO po = dao.getOne(so);
		if (po == null) {
			//getNewOne
			String ticket = createQrCode(liveCourseId, openId);
			WXPropertyPO property = new WXPropertyPO();
			property.setType(WXProperty.QR_CODE_TICKET.toString() + liveCourseId + "#" + domainId);
			property.setValue(ticket);
			property.setExpireIn(QR_CODE_EXPIRES_IN);
			wxService.create(property);
			return ticket;
		} else {
			long trueExpireTime = po.getUpdateTime().getTime() + po.getExpireIn() * 1000;
			if (new Date().after(new Date(trueExpireTime))) {
				//getNewOne.
				String ticket = createQrCode(liveCourseId, openId);
				po.setValue(ticket);
				po.setExpireIn(QR_CODE_EXPIRES_IN);
				wxService.update(po);
				return ticket;
			}
			return po.getValue();
		}
	}
	
	public void customMessage(String text) {
		HttpParams params = new HttpParams();
		params.setUrl(CUSTOM_MESSAGE_URL.replaceFirst("@accessToken", getAccessToken()));
		params.setRequestEntity(text);
		HttpPostUtils.postMap(params);
	}
	
	public void updateUserInfo(Integer domainId) {
		List<String> openIdList;
		if (domainId == null) {
			openIdList = userRelaDao.getAllOpenId();
		} else {
			UserRelaPO po = userRelaDao.getByDomainId(domainId);
			if (po == null) {
				throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
			}
			openIdList = Collections.singletonList(po.getOpenId());
		}
		for (String openId : openIdList) {
			WXUserInfoMO mo = getUserInfo(getAccessToken(), openId);
			CompletableFuture.runAsync(() -> {
				wxService.doUpdateUserInfo(mo);
			});
		}
	}

	@Transactional
	public void doUpdateUserInfo(WXUserInfoMO mo) {
		WXUserInfoPO po = dozerBeanMapper.map(mo, WXUserInfoPO.class);
		po.setSubscribe(mo.getSubscribe() == 1 ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		wxUserInfoDao.insertUpdate(po);
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
	
	public void testCreateQrCode() {
		HttpParams params = new HttpParams();
		params.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + getAccessToken());
		params.setRequestEntity("{\"expire_seconds\": 600, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": \"1\"}}}");
		String res = HttpPostUtils.postMap(params);
		System.out.println(res);
		
	}}
