package com.hotelpal.service.service;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.enums.PayMethod;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.WXPayResultMO;
import com.hotelpal.service.common.po.*;
import com.hotelpal.service.common.so.BannerSO;
import com.hotelpal.service.common.so.OrderSO;
import com.hotelpal.service.common.so.PurchaseLogSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.RandomUtils;
import com.hotelpal.service.common.vo.DailySalesVO;
import com.hotelpal.service.common.vo.PurchaseVO;
import com.hotelpal.service.common.vo.StatisticsVO;
import com.hotelpal.service.service.parterner.QNService;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Component
@Transactional
public class ContentService {
	private static final Logger logger = LoggerFactory.getLogger(ContentService.class);
	@Resource
	private BannerDao bannerDao;
	@Resource
	private OrderDao orderDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	@Resource
	private WXPayResultDao wxPayResultDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private UserService userService;
	@Resource
	private StatisticsDao statisticsDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private SysPropertyDao sysPropertyDao;
	@Resource
	private CourseDao courseDao;

	private static final String FREE_COURSE_LINK = PropertyHolder.getProperty("content.course.free.link");

	public String uploadImg(byte[] bytes, String key) {
		//key 添加时间戳以解决CDN缓存问题
		key = key.replaceAll("\\s+", "") + "_" + Long.toHexString(new Date().getTime());
		QNService.uploadToBucket(QNService.BUCKET_IMG, bytes, key, false);
		return QNService.IMG_DOMAIN + key;
	}
	public String uploadAudio(byte[] bytes, String key) {
		key = key.replaceAll("\\s+", "") + "_" + Long.toHexString(new Date().getTime());
		QNService.uploadToBucket(QNService.BUCKET_AUDIO, bytes, key, true);
		return QNService.AUDIO_DOMAIN + key;
	}
	
	public List<BannerPO> getMainBanner() {
		BannerSO so = new BannerSO();
		so.setPageSize(Integer.MAX_VALUE);
		so.setOrderBy("bannerOrder");
		return bannerDao.getList(so);
	}
	
	public List<OrderPO> getOrderList(OrderSO so) {
		so.setTotalCount(orderDao.getCount(so));
		return orderDao.getOrderList(so);
	}

	public List<PurchaseVO> getOrderList(PurchaseLogSO so) {
		return purchaseLogDao.getPurchaseOrderList(so);
	}

	public void updateBanner(BannerSO so) {
		if (so.getId() != null) {
			BannerPO po = dozerBeanMapper.map(so, BannerPO.class);
			bannerDao.update(po);
		} else {
			BannerPO po = dozerBeanMapper.map(so, BannerPO.class);
			bannerDao.create(po);
		}
	}

	public void removeBanner(Integer id) {
		bannerDao.delete(id);
	}

	public String createFreeCourseLink(Integer courseNum, Integer validity) {
		if (courseNum == null || validity == null) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		UserCoursePO po = new UserCoursePO();
		po.setExpiryIn(validity);
		po.setFreeCourseNum(courseNum);
		po.setLevel("A");
		String nonce = RandomUtils.createUUID();
		po.setNonce(nonce);
		//userCourseDao.create(po);
		return FREE_COURSE_LINK.replaceFirst("@nonce", nonce);
	}

	public void receiveUpdateWxPayResult(InputStream is) throws Exception{
		SAXReader reader = new SAXReader();
		Document document = reader.read(is);
		Element root = document.getRootElement();
		List list = root.elements();
		WXPayResultMO mo = new WXPayResultMO();
		for (Object e : list) {
			mapWXPayData(((Element) e).getName(), ((Element) e).getText(), mo);
		}

		logger.info("==============================微信支付" +
				(mo.getResultCode().equalsIgnoreCase("SUCCESS") ? "成功" : "失败") + ": " +
				JSON.toJSONString(mo));
		boolean orderExists = wxPayResultDao.existsByOrderNo(mo.getOutTradeNo());
		if (orderExists) return;

		wxPayResultDao.create(dozerBeanMapper.map(mo, WXPayResultPO.class));

		if (mo.getResultCode().equalsIgnoreCase("SUCCESS")) {
			PurchaseLogPO po = purchaseLogDao.getByOrderNo(mo.getOutTradeNo());
			OrderPO order = orderDao.getByOrderNo(mo.getOutTradeNo());
			if (order == null) {
				throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
			}
			SecurityContextHolder.loginSuperDomain();
			SecurityContextHolder.getContext().setTargetDomain(order.getDomainId());
			if (po == null) {
				po = new PurchaseLogPO();

				po.setClassify(order.getCourseType());
				po.setCouponId(order.getCouponId());
				po.setCourseId(order.getCourseId());
				po.setOrderTradeNo(order.getOrderTradeNo());
				po.setOriginalPrice(order.getOrderPrice());
				po.setPayment(mo.getCashFee());
				po.setPayMethod(PayMethod.NORMAL.toString());
				po.setWxConfirm(BoolStatus.Y.toString());
				po.setWxPrice(mo.getCashFee());
				purchaseLogDao.create(po);
			} else {
				po.setWxConfirm(BoolStatus.Y.toString());
				po.setWxPrice(mo.getCashFee());
				purchaseLogDao.update(po);
			}
			userService.afterPay(order);
		}
	}

	private void mapWXPayData(String name, String value, WXPayResultMO data) {
		String uName = name.toLowerCase();
		switch(uName) {
			case "appid": data.setAppId(value);							break;
			case "bank_type": data.setBankType(value);					break;
			case "cash_fee": data.setCashFee(Integer.valueOf(value));		break;
			case "fee_type": data.setFeeType(value); 						break;
			case "is_subscribe": data.setIsSubscribe(value);				break;
			case "mch_id": data.setMchId(value);							break;
			case "nonce_str": data.setNonceStr(value);					break;
			case "openid": data.setOpenId(value);							break;
			case "out_trade_no": data.setOutTradeNo(value);				break;
			case "result_code": data.setResultCode(value);				break;
			case "return_code": data.setReturnCode(value);				break;
			case "sign": data.setSign(value);								break;
			case "trade_type": data.setTradeType(value);					break;
			case "transaction_id": data.setTransactionId(value);			break;
			case "time_end": data.setTimeEnd(value);						break;
		}
	}
	
	public StatisticsVO getMainStatisticsData(Date from, Date to) {
		StatisticsVO res = userRelaDao.getMainTotalStatisticsData();
		Calendar _to_ = Calendar.getInstance();
		_to_.setTime(to);
		_to_.add(Calendar.DATE, 1);
		String fromStr = DateUtils.getDateString(from);
		String toStr = DateUtils.getDateString(_to_);
		statisticsDao.getSiteStatisticsData(res, fromStr, toStr);
		userRelaDao.getMainStatisticsUserData(res, fromStr, toStr);
		statisticsDao.getCourseStatisticsData(res, fromStr, toStr);
		return res;
	}
	public DailySalesVO getDailySales() {
		return purchaseLogDao.getDailySales();
	}

	public void updateStaticImg(Integer index, String url) {
		String name  = index == 1 ? SysPropertyPO.NAME_STATIC_IMG1 : SysPropertyPO.NAME_STATIC_IMG2;
		SysPropertyPO property = sysPropertyDao.getByName(name);
		if (property == null) {
			property = new SysPropertyPO();
			property.setName(name);
			property.setValue(url);
			sysPropertyDao.create(property);
		} else {
			property.setValue(url);
			sysPropertyDao.update(property);
		}
	}

	public String getStaticImgUrl(Integer index) {
		String name  = index == 1 ? SysPropertyPO.NAME_STATIC_IMG1 : SysPropertyPO.NAME_STATIC_IMG2;
		SysPropertyPO property = sysPropertyDao.getByName(name);
		return property == null ? "" : property.getValue();
	}

	public void addCourseToUser(Integer courseId, List<Integer> domainIdList) {
		CoursePO course  = courseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		DecimalFormat format = new DecimalFormat("000");
		List<OrderPO> orderList = new ArrayList<>(domainIdList.size());
		List<PurchaseLogPO> plList = new ArrayList<>(domainIdList.size());
		Map<Integer, Boolean> recordExists = purchaseLogDao.recordExists(CourseType.NORMAL, courseId, domainIdList);
		for (int i = 0; i < domainIdList.size(); i++) {
			if (recordExists.get(domainIdList.get(i))) {
				continue;
			}
			String orderTraceNo = DateUtils.getDateTimeString(new Date()).replaceAll("\\D", "") + RandomUtils.getRandomDigitalString(5000, 4) + format.format(i);
			OrderPO order = new OrderPO();
			order.setDomainId(domainIdList.get(i));
			order.setOrderTradeNo(orderTraceNo);
			order.setCourseId(courseId);
			order.setOrderPrice(course.getPrice());
			order.setCourseType(CourseType.NORMAL.toString());
			order.setFee(0);
			order.setUseSpecifiedDomain(true);
			orderList.add(order);

			PurchaseLogPO pl = new PurchaseLogPO();
			pl.setDomainId(domainIdList.get(i));
			pl.setOrderTradeNo(orderTraceNo);
			pl.setCourseId(courseId);
			pl.setPayment(0);
			pl.setOriginalPrice(order.getOrderPrice());
			pl.setPayMethod(PayMethod.NORMAL.toString());
			pl.setClassify(CourseType.NORMAL.toString());
			pl.setUseSpecifiedDomain(true);
			plList.add(pl);
		}
		orderDao.createList(orderList);
		purchaseLogDao.createList(plList);
	}
}
