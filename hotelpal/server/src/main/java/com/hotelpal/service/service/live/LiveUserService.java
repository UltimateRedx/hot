package com.hotelpal.service.service.live;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseInviteLogDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveEnrollDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.enums.LiveEnrollStatus;
import com.hotelpal.service.common.enums.LiveEnrollType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.mo.WXPreOrderMO;
import com.hotelpal.service.common.po.*;
import com.hotelpal.service.common.po.live.LiveCourseInviteLogPO;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.po.live.LiveEnrollPO;
import com.hotelpal.service.common.so.PurchaseLogSO;
import com.hotelpal.service.common.so.UserCouponSO;
import com.hotelpal.service.common.so.live.LiveCourseInviteLogSO;
import com.hotelpal.service.common.so.live.LiveEnrollSO;
import com.hotelpal.service.common.utils.*;
import com.hotelpal.service.common.utils.wx.WXEncodeUtils;
import com.hotelpal.service.common.vo.LiveUserInfoVO;
import com.hotelpal.service.service.CouponService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.parterner.wx.MsgPushService;
import com.hotelpal.service.service.parterner.wx.WXService;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Transactional
public class LiveUserService {
	private static final Logger logger = LoggerFactory.getLogger(LiveUserService.class);
	@Resource
	private LiveUserService liveUserService;
	@Resource
	private UserDao userDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private LiveEnrollDao liveEnrollDao;
	@Resource
	private LiveCourseInviteLogDao liveCourseInviteLogDao;
	@Resource
	private LiveCourseDao liveCourseDao;
	@Resource
	private UserCouponDao userCouponDao;
	@Resource
	private OrderDao orderDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private CouponService couponService;
	@Resource
	private SysCouponDao sysCouponDao;
	@Resource
	private LiveCourseService liveCourseService;
	@Resource
	private WXService wxService;
	@Resource
	private UserService userService;
	@Resource
	private MsgPushService msgPushService;

	private static final String qrCodeLink = PropertyHolder.getProperty("LIVE_COURSE_INVITE_QR_CODE");
	private static final String inviteUrl  = PropertyHolder.getProperty("LIVE_COURSE_INVITE_IMG_URL");
	private static final String LIVE_COURSE_LINK = PropertyHolder.getProperty("content.course.live.link");


	private Lock lock = new ReentrantLock();
	
	public LiveUserInfoVO getUserInfo(Integer liveCourseId, LiveCoursePO liveCourse) {
		//paid
		PurchaseLogSO pso = new PurchaseLogSO();
		pso.setClassify(CourseType.LIVE.toString());
		pso.setCourseId(liveCourseId);
		boolean purchased = purchaseLogDao.count(pso) > 0;
		
		//invite
		LiveCourseInviteLogSO iso = new LiveCourseInviteLogSO();
		iso.setLiveCourseId(liveCourseId);
		List<LiveCourseInviteLogPO> inviteLogPOList = liveCourseInviteLogDao.getList(iso);
		List<Integer> domainIdList = new ArrayList<>();
		for (LiveCourseInviteLogPO po : inviteLogPOList) {
			domainIdList.add(po.getInvitedDomainId());
		}
		List<UserPO> userList = userDao.getByDomainIdList(domainIdList);
		//enroll
		String enrollStatus = LiveEnrollStatus.NONE.toString();
		LiveEnrollSO enrollSO = new LiveEnrollSO();
		enrollSO.setLiveCourseId(liveCourseId);
		List<LiveEnrollPO> enrollInfoList = liveEnrollDao.getList(enrollSO);
		for (LiveEnrollPO p : enrollInfoList) {
			if (LiveEnrollStatus.ENROLLED.toString().equalsIgnoreCase(p.getStatus())) {
				enrollStatus = p.getStatus();
				break;
			}
			enrollStatus = p.getStatus();
		}
		//有没有为别人报名
		LiveCourseInviteLogSO lso = new LiveCourseInviteLogSO();
		lso.setLiveCourseId(liveCourseId);
		lso.setInvitedDomainId(SecurityContextHolder.getUserDomainId());
		lso.setIgnoreDomainId(true);
		List<LiveCourseInviteLogPO> inviterList = liveCourseInviteLogDao.getList(lso);
		String inviterOpenId = null;
		if (inviterList.size() > 0) {
			Integer inviterDomainId = inviterList.get(0).getDomainId();
			UserPO InviterUser = userRelaDao.getUserByDomainId(inviterDomainId);
			inviterOpenId = InviterUser.getOpenId();
		}
		//关联课程有没有购买
		boolean relaCoursePurchased = false;
		if (liveCourse.getRelaCourse() != null) {
			relaCoursePurchased = purchaseLogDao.recordExists(CourseType.NORMAL, liveCourse.getRelaCourse().getId(), SecurityContextHolder.getUserDomainId());
		}
		//return value
		LiveUserInfoVO vo = new LiveUserInfoVO();
		vo.setLiveVip(SecurityContextHolder.getLiveVip());
		vo.setPurchased(purchased ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		vo.setInvitedUserList(userList);
		vo.setStatus(enrollStatus);
		vo.setEnrolled(LiveEnrollStatus.ENROLLED.toString().equalsIgnoreCase(enrollStatus) ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		vo.setEnrolledFor(inviterOpenId);
		vo.setInvitePoster(inviteUrl.replaceFirst("@openId", SecurityContextHolder.getUserOpenId()).replaceFirst("@liveCourseId", String.valueOf(liveCourseId)));
		vo.setRelateCoursePurchased(relaCoursePurchased ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		return vo;
	}
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String getInviteImg(Integer liveCourseId) {
		try {
			byte[] qrCodeImgByte = liveCourseService.getQrCodeCache(liveCourseId);
			BufferedImage bufferedQrCode = ImageIO.read(ImageIO.createImageInputStream(new ByteArrayInputStream(qrCodeImgByte)));
			byte[] imgRes = (byte[]) liveCourseService.getImgCache(liveCourseId);
			ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imgRes));
			BufferedImage courseBufferedImg = ImageIO.read(iis);
			int height = courseBufferedImg.getHeight();
			int width = courseBufferedImg.getWidth();
			/*
			 * 将二维码画到海报上
			 * 二维码大小 160*160
			 * 二维码距离底边(202-160)，距离右边40
			 */
			courseBufferedImg.getGraphics().drawImage(bufferedQrCode, width-40-160, height-202, 160, 160, null);
			//将用户信息写到海报上
			UserPO user = userDao.getById(SecurityContextHolder.getUserId());
			HttpParams params = new HttpParams();
			params.setUrl(user.getHeadImg());
			InputStream headIS = HttpGetUtils.executeGetStream(params);
			// 可能获取不到
			double rate = width / 750D;
			Graphics baseGraph = courseBufferedImg.getGraphics();
			int blockMarginLeft = 30;
			int blockMarginTop = 30;
			if (headIS != null) {
				BufferedImage bufferedHead = ImageIO.read(ImageIO.createImageInputStream(headIS));
				/*
				 * 用户信息块 (宽度为750px时)距离上边30，左边60, 72*72, radius=72/2
				 * 需要按比例缩放
				 */
				//生成一个与bufferedHead一样大小的图片，准备剪裁
				BufferedImage copy = new BufferedImage(bufferedHead.getWidth(), bufferedHead.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D copyGraph = copy.createGraphics();
				copyGraph.setClip(new Ellipse2D.Double(0, 0, copy.getWidth(), copy.getHeight()));
				copyGraph.drawImage(bufferedHead, 0, 0, null);
				copyGraph.dispose();

				baseGraph.drawImage(copy, blockMarginLeft, blockMarginTop, 72, 72, null);
			}
			int nickMarginLeft = (int)((blockMarginLeft + 72 + 20) * rate);
			int nickMarginTop = (int)((blockMarginTop + 24) * rate);
			baseGraph.setColor(new Color(0x999999));
			baseGraph.setFont(new Font("微软雅黑", Font.PLAIN, 24));
			baseGraph.drawString(user.getNick(), nickMarginLeft, nickMarginTop);

			int constantMarginTop = (int)((blockMarginTop + 24 + 6 + 30) * rate);
			baseGraph.setColor(new Color(0x666666));
			baseGraph.setFont(new Font("微软雅黑", Font.PLAIN, 30));
			baseGraph.drawString("送你一堂免费课", nickMarginLeft, constantMarginTop);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(courseBufferedImg, "png", baos);
			byte[] imageInByte = baos.toByteArray();
			return new String(imageInByte, "ISO-8859-1");
		}catch (Exception e) {
			logger.error("create img exception...", e);
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 只有会员报名才使用这个接口
	 */
	public void liveCourseEnroll(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if(!BoolStatus.Y.toString().equalsIgnoreCase(SecurityContextHolder.getLiveVip())) {
			doCreateOrder(courseId, null);
			return;
		}
		LiveEnrollSO so = new LiveEnrollSO();
		so.setEnrollType(LiveEnrollType.VIP.toString());
		so.setLiveCourseId(courseId);
		so.setStatus(LiveEnrollStatus.ENROLLED.toString());
		boolean enrolled = liveEnrollDao.count(so) > 0;
		if (enrolled) {
			throw new ServiceException(ServiceException.LIVE_COURSE_ALREADY_ENROLLED);
		}
		LiveEnrollPO po = new LiveEnrollPO();
		po.setEnrollType(LiveEnrollType.VIP.toString());
		po.setStatus(LiveEnrollStatus.ENROLLED.toString());
		po.setLiveCourseId(courseId);
		liveEnrollDao.create(po);
		//更新课程的VIP报名数
		LiveEnrollSO countSO = new LiveEnrollSO();
		countSO.setEnrollType(LiveEnrollType.VIP.toString());
		countSO.setLiveCourseId(courseId);
		countSO.setStatus(LiveEnrollStatus.ENROLLED.toString());
		countSO.setIgnoreDomainId(true);
		Integer vipEnrolledCount = liveEnrollDao.count(countSO);
		course.setVipEnrolledTimes(vipEnrolledCount);
		liveCourseDao.update(course);
	}
	
	//课程关联的优惠券
	public void obtainCoupon(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		Integer courseCouponId = course.getSysCouponId();
		if (courseCouponId == null) {
			throw new ServiceException(ServiceException.LIVE_COURSE_NO_COUPON);
		}
		SysCouponPO sysCoupon = sysCouponDao.getById(courseCouponId);
		UserCouponSO so = new UserCouponSO();
		so.setSysCouponId(courseCouponId);
		UserCouponPO coupon = userCouponDao.getOne(so);
		if (coupon != null) {
			throw new ServiceException(ServiceException.COUPON_OBTAINED);
		}
		Integer totalUsed = userCouponDao.countBySysCouponId(courseCouponId);
		if (sysCoupon.getTotal() != null && sysCoupon.getTotal() > 0 && sysCoupon.getTotal() <= totalUsed) {
			throw new ServiceException(ServiceException.COUPON_DEPLETION);
		}
		coupon = new UserCouponPO();
		coupon.setUsed(BoolStatus.N.toString());
		coupon.setValidity(sysCoupon.getValidity());
		coupon.setType(sysCoupon.getType());
		coupon.setSysCouponId(courseCouponId);
		coupon.setValue(sysCoupon.getValue());
		userCouponDao.create(coupon);
	}
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> createPayOrder(Integer courseId, Integer couponId) {
		boolean purchased = purchaseLogDao.recordExists(CourseType.NORMAL, courseId, SecurityContextHolder.getUserDomainId());
		if (purchased) {
			throw new ServiceException(ServiceException.ORDER_COURSE_ALREADY_GOT);
		}
		WXPreOrderMO mo = liveUserService.doCreateOrder(courseId, couponId);
		Map<String, Object> res = new HashMap<>();
		if (BoolStatus.N.toString().equalsIgnoreCase(mo.getPurchased())) {
			res = WXService.createPrePay(mo);
		}
		res.put("purchased", mo.getPurchased());
		res.put("tradeNo", mo.getTradeNo());
		return res;
	}

	@Transactional
	public WXPreOrderMO doCreateOrder(Integer courseId, Integer couponId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		UserCouponPO coupon = null;
		if (couponId != null) {
			coupon = userCouponDao.getById(couponId);
			couponService.validateCoupon(coupon, courseId);
		}
		Integer fee = coupon == null ? course.getPrice() : (course.getPrice() - coupon.getValue());
		fee = fee < 0 ? 0 : fee;
		//create order in local db first.
		OrderPO po = new OrderPO();
		po.setCourseType(CourseType.LIVE.toString());
		po.setCourseId(courseId);
		po.setOrderPrice(course.getPrice());
		po.setFee(fee);
		po.setOrderTradeNo(DateUtils.getDateTimeString(new Date()).replaceAll("\\D", "") + RandomUtils.getRandomDigitalString(5000, 4));
		po.setTerminalIP(ContextUtils.getRemoteIP());
		orderDao.create(po);
		boolean purchased = false;
		if (fee <= 0) {
			userService.payCourse(po.getOrderTradeNo());
			purchased = true;
		}

		//Send request to WX server.
		String openId = SecurityContextHolder.getUserOpenId();
		WXPreOrderMO mo = new WXPreOrderMO();
		mo.setBody("报名直播-" + course.getTitle());
		mo.setTradeNo(po.getOrderTradeNo());
		mo.setOpenId(openId);
		mo.setIp(po.getTerminalIP());
		mo.setFee(po.getOrderPrice());
		mo.setPurchased(purchased ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		return mo;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean enrollFor(Integer courseId, String openId) {
		return liveUserService.doEnrolledFor(courseId, openId);
	}
	@Transactional
	public boolean doEnrolledFor(Integer courseId, String openId) {
		if (SecurityContextHolder.getUserOpenId().equalsIgnoreCase(openId)) {
			throw new ServiceException(ServiceException.LIVE_COURSE_SELF_INVITE);
		}
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		//已经帮助其他人
		LiveCourseInviteLogSO enrolledForSo = new LiveCourseInviteLogSO();
		enrolledForSo.setInvitedDomainId(SecurityContextHolder.getUserDomainId());
		enrolledForSo.setLiveCourseId(courseId);
		enrolledForSo.setIgnoreDomainId(true);
		if (liveCourseInviteLogDao.count(enrolledForSo) > 0) {
			throw new ServiceException(ServiceException.LIVE_COURSE_ENROLLED_FOR);
		}
		Integer require = course.getInviteRequire();
		int r = require == null ? 0 : require;
		UserPO inviter = userRelaDao.getByOpenId(openId);
		if (inviter == null) {
			throw new ServiceException(ServiceException.DAO_OPENID_NOT_FOUND);
		}
		Integer userDomainId = SecurityContextHolder.getUserDomainId();
		UserPO currentUser = userDao.getById(SecurityContextHolder.getUserId());
		SecurityContextHolder.loginSuperDomain();
		SecurityContextHolder.getContext().setTargetDomain(inviter.getDomainId());
		LiveCourseInviteLogSO so = new LiveCourseInviteLogSO();
		so.setLiveCourseId(courseId);
		try {
			lock.lock();
			int inviteCount = liveCourseInviteLogDao.count(so);
			boolean over = inviteCount >= r;
			if (over) {
				return false;
			}
			LiveCourseInviteLogPO po = new LiveCourseInviteLogPO();
			po.setInvitedDomainId(userDomainId);
			po.setLiveCourseId(courseId);
			liveCourseInviteLogDao.create(po);
			//自动报名
			if (inviteCount + 1 >= r) {
				LiveEnrollSO inviterSO = new LiveEnrollSO();
				inviterSO.setLiveCourseId(courseId);
				inviterSO.setEnrollType(LiveEnrollType.INVITE.toString());
				LiveEnrollPO inviterPO = liveEnrollDao.getOne(inviterSO);
				if (!LiveEnrollStatus.ENROLLED.toString().equalsIgnoreCase(inviterPO.getStatus())) {
					inviterPO.setStatus(LiveEnrollStatus.ENROLLED.toString());
					liveEnrollDao.update(inviterPO);
					updateEnrollCount(courseId);
					//异步推送任务完成的消息
					CompletableFuture.runAsync(() -> msgPushService.pushInviteCompleteMsg(courseId, inviter.getOpenId(), course.getOpenTime()));
				}
			} else {
				CompletableFuture.runAsync(() ->
					msgPushService.pushEnrollForNotification(openId, currentUser.getNick(), DateUtils.getTimeString(new Date()), r, inviteCount + 1,
							LIVE_COURSE_LINK.replaceFirst("@liveCourseId", String.valueOf(courseId)))
				);
			}
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	public void inviting(Integer courseId) {
		LiveEnrollSO so = new LiveEnrollSO();
		so.setLiveCourseId(courseId);
		so.setStatus(LiveEnrollStatus.ENROLLED.toString());
		boolean enrolled = liveEnrollDao.count(so) > 0;
		if (enrolled) {
			throw new ServiceException(ServiceException.LIVE_COURSE_ALREADY_ENROLLED);
		}
		so.setEnrollType(LiveEnrollType.INVITE.toString());
		so.setStatus(LiveEnrollStatus.INVITING.toString());
		boolean inviting = liveEnrollDao.count(so) > 0;
		if (inviting) {
			throw new ServiceException(ServiceException.LIVE_COURSE_INVITING_ENROLLED);
		}
		LiveEnrollPO po = new LiveEnrollPO();
		po.setStatus(LiveEnrollStatus.INVITING.toString());
		po.setEnrollType(LiveEnrollType.INVITE.toString());
		po.setLiveCourseId(courseId);
		liveEnrollDao.create(po);
		//生成邀请图片
		
	}

	/**
	 * 需要可以重复调用
	 */
	public void afterPaid(Integer courseId, Integer originalCoursePrice) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		LiveEnrollPO po = new LiveEnrollPO();
		po.setEnrollType(originalCoursePrice == 0 ? LiveEnrollType.PURCHASE_FREE.toString() : LiveEnrollType.PURCHASE.toString());
		po.setLiveCourseId(courseId);
		po.setStatus(LiveEnrollStatus.ENROLLED.toString());
		try {
			liveEnrollDao.create(po);
			updateEnrollCount(courseId);
		} catch (DuplicateKeyException ignored) {}
	}
	
	
	private void updateEnrollCount(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if(course == null) return;
		LiveEnrollSO so = new LiveEnrollSO();
		so.setIgnoreDomainId(true);
		so.setStatus(LiveEnrollStatus.ENROLLED.toString());
		so.setEnrollType(LiveEnrollType.INVITE.toString());
		so.setLiveCourseId(courseId);
		Integer inviteCount = liveEnrollDao.count(so);
		so.setEnrollType(LiveEnrollType.PURCHASE.toString());
		Integer purchaseCount = liveEnrollDao.count(so);
		so.setEnrollType(LiveEnrollType.PURCHASE_FREE.toString());
		Integer freePurchaseCount = liveEnrollDao.count(so);
		course.setFreeEnrolledTimes(inviteCount);
		course.setPurchasedTimes(purchaseCount);
		course.setFreePurchasedTimes(freePurchaseCount);
		liveCourseDao.update(course);
	}
	
	/**
	 * <xml>
	 *     <ToUserName><![CDATA[gh_af85985d3872]]></ToUserName>
	 *     <Encrypt>
	 *         <![CDATA[4Kg7LSyY9ds//q9KDTJfTPaiUVorfnewfP5yhfQfg44aq/bas+fvpDRrqZaaomNW/Aa6266YBInz2wMyzTvbH6eogIVDBZ3KEjS2kheaHHfRuWfdw8VKG4wpFEVeL1wsqQ+IoNluhk5wh0YkpZmEuKaB/xxIhN2kT4SwFYISfNXc289svrmt1l0xr4HvbgaJCKj0WZcKC97E0M/s4O/6qXzuP3Dzb07pC5tCpDKa2rY9PRtkcBwo477vP2wcpl7uxIjZk8jA+5hVCbx/DYmBBWd/bMo7MlMh44kEjSszZeqEQmlugVEHE0bK+AyRmp1PqUyg9oP7rmNufwfXnSPUP3eXI9A0yVYqX2e3gYPSFdA4sD3Oen4b53/37vzCgrAj39xBSR0MTFo91xZr1We0JCEBtHe6KEkbuilWnWO0ZOBxlL42MjvVuQ7UTjmPtk/nRBks7JBE9yO+rns7xrbNU/yi8wchiFEccGN7M307Df0GvfzF71sqmRvu8I2VRwuEOlROIsAZVhcWuQyRFIeZx19myABI8BauGhdVnZjWOGBoS1W58i0sIgsAwlMCJTXjjkiY4To3XHkTr2o6GBTv1Q==]]>
	 *     </Encrypt>
	 * </xml>
	 * after decrypt :
	 * <xml>
	 *     <ToUserName><![CDATA[gh_af85985d3872]]></ToUserName>
	 *     <FromUserName><![CDATA[oyH7Q0c0d92cblJsJ0n8LyBtwets]]></FromUserName> 谁扫描的
	 *     <CreateTime>1531536727</CreateTime>
	 *     <MsgType><![CDATA[event]]></MsgType>
	 *     <Event><![CDATA[SCAN]]></Event>
	 *     <EventKey><![CDATA[1]]></EventKey> liveCourseId#openId
	 *     <Ticket><![CDATA[gQGy7jwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyVmloS3R3OC1mZ2gxbWFEOWhyMWQAAgQyZUlbAwRYAgAA]]></Ticket>
	 * </xml>
	 *
	 */
	public void handleWxEvent(InputStream is) {
		String encrypt = XMLUtils.listMapContent(is).get("Encrypt");
		String xml = WXEncodeUtils.decrypt(encrypt);
		Map<String, String> res = XMLUtils.listMapContent(xml);
		if ("event".equalsIgnoreCase(res.get("MsgType")) && Arrays.asList("subscribe".toUpperCase(), "SCAN").contains(res.get("Event").toUpperCase())) {
			if (StringUtils.isNullEmpty(res.get("EventKey"))) return;
			String key = res.get("EventKey").replaceFirst("qrscene_", "");
			if (StringUtils.isNullEmpty(key) || !key.contains("#")) return;
			String liveCourseId = key.substring(0, key.indexOf('#'));
			String invitorOpenId = key.substring(key.indexOf('#') + 1);
			UserPO user = userRelaDao.getByOpenId(res.get("FromUserName"));
			Map<String, Object> msg = new HashMap<>();
			Map<String, Object> content = new HashMap<>();
			String href = qrCodeLink.replaceFirst("@liveCourseId", liveCourseId).replaceFirst("@openId", invitorOpenId);
			msg.put("touser", res.get("FromUserName"));
			msg.put("msgtype", "text");
			msg.put("text", content);
			content.put("content", (user != null ? user.getNick() : "") + " 你好，\n\n欢迎收听【酒店邦成长营】本周的直播课！！！\n" +
					"\n" +
					"点击下方“公开课学习”，进入「成长营直播间」免费学习酒店行业最地道的实战知识～\n" +
					"\n" +
					"<a href='" + href + "'>➡️公开课学习</a>");
			wxService.customMessage(JSON.toJSONString(msg));
		}
	}
}
