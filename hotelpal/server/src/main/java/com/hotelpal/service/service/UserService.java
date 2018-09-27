package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.common.context.CommonParams;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.*;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.*;
import com.hotelpal.service.common.po.*;
import com.hotelpal.service.common.po.extra.PurchasedCoursePO;
import com.hotelpal.service.common.so.*;
import com.hotelpal.service.common.utils.*;
import com.hotelpal.service.common.vo.CommentVO;
import com.hotelpal.service.common.vo.UserVO;
import com.hotelpal.service.common.vo.WxUserInfo;
import com.hotelpal.service.service.live.LiveUserService;
import com.hotelpal.service.service.parterner.SubMailService;
import com.hotelpal.service.service.parterner.wx.WXService;
import org.apache.commons.lang3.RandomStringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	@Resource
	private UserService userService;
	@Resource
	private UserDao userDao;
	@Resource
	private PhoneCodeDao phoneCodeDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private CommentDao commentDao;
	@Resource
	private ZanLogDao zanLogDao;
	@Resource
	private ListenLogDao listenLogDao;
	@Resource
	private LessonDao lessonDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private OrderDao orderDao;
	@Resource
	private UserCourseDao userCourseDao;
	@Resource
	private CourseDao courseDao;
	@Resource
	private RedPacketDao redPacketDao;
	@Resource
	private SpeakerDao speakerDao;
	@Resource
	private LessonContentDao lessonContentDao;
	@Resource
	private DozerBeanMapper dozer;
	@Resource
	private LiveUserService liveUserService;
	@Resource
	private WXSNSUserInfoDao wxsnsUserInfoDao;
	@Resource
	private UserCouponDao userCouponDao;
	@Resource
	private CouponService couponService;
	@Resource
	private RegInviteDao regInviteDao;
	@Resource
	private ContentService contentService;
	@Resource
	private WXUserInfoDao wxUserInfoDao;
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void saveUserInfo(WXSNSUserInfoMO mo) {
		userService.saveWxUserInfo(mo);
		userService.refineUserInfo(mo);
	}

	@Transactional
	public void saveWxUserInfo(WXSNSUserInfoMO mo) {
		UserPO user = userRelaDao.getByOpenId(mo.getOpenid());
		if (user == null) {
			WXSNSUserInfoPO info = wxsnsUserInfoDao.getByOpenId(mo.getOpenid());
			if (info == null) {
				info = new WXSNSUserInfoPO();
				info.setOpenId(mo.getOpenid());
				info.setNickName(mo.getNickname());
				info.setSex(mo.getSex());
				info.setProvince(mo.getProvince());
				info.setCity(mo.getCity());
				info.setCountry(mo.getCountry());
				info.setHeadImgUrl(mo.getHeadimgurl());
				info.setPrivilege(mo.getPrivilege());
				info.setUnionId(mo.getUnionid());
				wxsnsUserInfoDao.create(info);
			}
		}
	}
	@Transactional
	public void refineUserInfo(WXSNSUserInfoMO mo) {
		if (userRelaDao.getByOpenId(mo.getOpenid()) != null) return;
		UserPO user = new UserPO();
		user.setOpenId(mo.getOpenid());
		user.setHeadImg(mo.getHeadimgurl());
		if (!StringUtils.isNullEmpty(mo.getHeadimgurl())) {
			HttpParams params = new HttpParams();
			params.setUrl(mo.getHeadimgurl());
			try {
				byte[] headImgContentByte = HttpGetUtils.executeGetBytes(params);
				String url = contentService.uploadImg(headImgContentByte, "u_" + mo.getOpenid());
				user.setHeadImg(url);
			}catch(Exception e) {
				logger.error("refine head img failed...", e);
			}
		}
		user.setNick(mo.getNickname());
		user.setRegChannel(RegChannel.NORMAL.toString());
		user.setLiveVip(BoolStatus.N.toString());
		userDao.create(user);
		UserRelaPO rela = new UserRelaPO();
		rela.setOpenId(mo.getOpenid());
		rela.setUserId(user.getId());
		rela.setDomainId(user.getId());
		userRelaDao.create(rela);
	}


	/**
	 * 场景：
	 * I、用户未绑定号码，首次绑定{号码不存在，同1；号码存在，同2.2}
	 * II、用户已绑定号码{绑定自己号码，同0；绑定没有的新号码，同2.1；绑定已有的号码，同2.2}
	 * 0、校验同一个号码，直接抛错返回
	 * 1、添加新号码。直接在user_rela找到openId并更新phone
	 * 2、更换手机号码(保留原来的手机号码记录).
	 * 	2.1、新的手机号码
	 *		//2.1.1、将原有的openId置为NULL
	 *		//2.1.2、新建userRela
	 *		//2.1.3、将openId写入，新的phone写入
	 *		//2.1.4、新建user，将原有的user信息删除(?)
	 *
	 *	2.2、手机号码之前存在(等同于换微信之后再次绑定手机)
	 *		2.2.1、将旧的phone删除，将旧的domainId写入当前userRela,更新phone
	 *
	 * 最后表中可能有空的openId，空的phone，不能有空的domainId
	 */
	public Map<String, Object> verifyPhoneCode(String phone, String code, String inviterOpenId) {
		String userCurrentPhone = SecurityContextHolder.getUserPhone();
		if (phone.equalsIgnoreCase(userCurrentPhone)) {
			throw new ServiceException(ServiceException.USER_DUPLICATE_PHONE_LOGIN);
		}
		//查找校验保存的验证码
		PhoneCodeSO so = new PhoneCodeSO();
		so.setPhone(phone);
		List<PhoneCodePO> po = phoneCodeDao.getList(so);
		if (po.size() == 0) {
			throw new ServiceException(ServiceException.USER_LOGIN_CODE_NOT_EXIST);
		}
		if (!po.get(0).getCode().equals(code)) {
			throw new ServiceException(ServiceException.USER_LOGIN_CODE_INVALID);
		}
		
		UserRelaSO phoneHolderSO = new UserRelaSO();
		phoneHolderSO.setPhone(phone);
		List<UserRelaPO> phoneHolderList = userRelaDao.getList(phoneHolderSO);
		boolean isNewPhone = !(phoneHolderList.size() > 0);
		UserRelaSO currentSO = new UserRelaSO();
		currentSO.setOpenId(SecurityContextHolder.getUserOpenId());
		UserRelaPO currentUser = userRelaDao.getOne(currentSO);
		Map<String, Object> map = new HashMap<>();
		if (isNewPhone) {
			if (StringUtils.isNullEmpty(userCurrentPhone)) {
				currentUser.setPhone(phone);
				currentUser.setPhoneRegTime(new Date());
				userRelaDao.update(currentUser);
			} else {
				//绑定另外一个手机，原有的记录除了openId都保留。
				//将此openId绑定到新建的用户上
				UserPO currentUserPO = userDao.getById(currentUser.getUserId());
				currentUserPO.setOpenId(null);
				userDao.update(currentUserPO);
				String currentUserOpenId = currentUser.getOpenId();
				//当前记录openId清空，其余不变
				currentUser.setOpenId(null);
				userRelaDao.update(currentUser);

				//相当于需要新建一个用户，user也需要新建
				UserPO newCurrentUserPO = dozer.map(currentUserPO, UserPO.class);
				newCurrentUserPO.setId(null);
				newCurrentUserPO.setOpenId(currentUserOpenId);
				newCurrentUserPO.setLiveVip(null);
				userDao.create(newCurrentUserPO);

				UserRelaPO newCurrentUserRela = new UserRelaPO();
				newCurrentUserRela.setOpenId(currentUserOpenId);
				newCurrentUserRela.setUserId(newCurrentUserPO.getId());
				newCurrentUserRela.setDomainId(newCurrentUserPO.getId());
				newCurrentUserRela.setPhoneRegTime(new Date());
				newCurrentUserRela.setPhone(phone);
				userRelaDao.create(newCurrentUserRela);
			}
			
		} else {
			/*1、 old用户的手机号清掉
			 * 2、手机号填写到current user_rela的上
			 * 3、查询 old user 到内存
			 * 4、删除current user
			 * 5、old user的openId改变为current user 的openId
			 * 6.old user_rela 的domainId 查询到内存
			 * 7、old user_rela的domainId 置为-1
			 * 8、current user_rela 的domainId 置为 old user_rela 的domainId
			 * //=============以上完成当前用户的绑定
			 * 9、根据old user 创建村的 user, 得到id
			 * 10、将9中得到的id更新到old user_rela 的userId和domainId
			 *         ||
			 *        \ /
			 * 1、查询old/current user_rela、user
			 * 2、删除删除current user
			 * 3、将old user 的openId改为current user 的openId，更新数据库
			 * 4、由old 新建 user => newUser 得到Id
			 * 5、old user_rela 的userId.domainId = newUser.id, phone = null, 更新数据库
			 * 6、current user_rela 的 userId, domainId = old user_rela的userId，domainId， phone = phone
			 */
			//Step 0
			String currentOpenId = SecurityContextHolder.getUserOpenId();
			Integer currentUserId = SecurityContextHolder.getUserId();
			UserRelaPO phoneHolder = phoneHolderList.get(0);
			String oldOpenId = phoneHolder.getOpenId();
			Integer oldUserId = phoneHolder.getUserId();
			// Step 1
			UserRelaPO _currentUserRela_ = userRelaDao.getRelaByOpenId(currentOpenId);
			UserRelaPO _oldUserRela_ = userRelaDao.getRelaByOpenId(oldOpenId);
			UserPO _oldUser_ = userDao.getById(oldUserId);
			//Step 2
			userDao.delete(currentUserId);
			//Step 3
			UserPO _oldUser_copy_ = dozer.map(_oldUser_, UserPO.class);
			_oldUser_copy_.setOpenId(currentOpenId);
			_oldUser_copy_.setLiveVip(BoolStatus.N.toString());
			userDao.update(_oldUser_copy_);
			// Step 4
			UserPO _newUser_ = dozer.map(_oldUser_, UserPO.class);
			_newUser_.setId(null);
			userDao.create(_newUser_);
			Integer _newUser_id_ = _newUser_.getId();
			//Step 5
			UserRelaPO _olduserRela_copy = dozer.map(_oldUserRela_, UserRelaPO.class);
			_olduserRela_copy.setUserId(_newUser_id_);
			_olduserRela_copy.setDomainId(_newUser_id_);
			_olduserRela_copy.setPhone(null);
			_olduserRela_copy.setPhoneRegTime(null);
			userRelaDao.updateAll(_olduserRela_copy);
			//Step 6
			_currentUserRela_.setUserId(_oldUserRela_.getUserId());
			_currentUserRela_.setDomainId(_oldUserRela_.getDomainId());
			_currentUserRela_.setPhone(phone);
			_currentUserRela_.setPhoneRegTime(_oldUserRela_.getPhoneRegTime());
			userRelaDao.updateAll(_currentUserRela_);
		}
		map.put("newInvitedUser", false);
		if (isNewPhone && !StringUtils.isNullEmpty(inviterOpenId)) {
			try {
				newInvitedUser(inviterOpenId);
				map.put("newInvitedUser", true);
			} catch (Exception e) {
				map.put("newInvitedUser", false);
			}
		}
		map.put("newPhone", isNewPhone);
		return map;
	}
	
	private void newInvitedUser(String inviterOpenId) {
		obtainCoupon();
		//将邀请记录写入表
		Integer currentUserDomainId = SecurityContextHolder.getUserDomainId();
		SecurityContextHolder.loginSuperDomain();
		UserPO inviter = userRelaDao.getByOpenId(inviterOpenId);
		SecurityContextHolder.getContext().setTargetDomain(inviter.getDomainId());
		String batch = regInviteDao.getLatestBatch();
		RegInvitePO ri = new RegInvitePO();
		ri.setBatch(batch);
		ri.setInvitedDomainId(currentUserDomainId);
		ri.setCouponCollected(BoolStatus.N.toString());
		regInviteDao.create(ri);
	}
	public void saveUserProp(UserSO so) {
		UserPO po = userDao.getById(SecurityContextHolder.getUserId());
		po.setHeadImg(so.getHeadImg());
		po.setCompany(so.getCompany());
		po.setTitle(so.getTitle());
		po.setNick(so.getNickname());
		userDao.update(po);
	}
	
	public void createComment(CommentSO so) {
		CommentPO po = new CommentPO();
		dozer.map(so, po);
		po.setElite(BoolStatus.N.toString());
		po.setZanCount(0);
		commentDao.create(po);
	}
	
	public void createZan(ZanLogSO so) {
		ZanLogPO po = new ZanLogPO();
		dozer.map(so, po);
		try {
			zanLogDao.create(po);
		} catch(DuplicateKeyException e) {
			return;
		}
		
		//更新评论点赞数量
		CommentPO comment = commentDao.getById(so.getCommentId());
		if (comment == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		comment.setZanCount(comment.getZanCount() + 1);
		commentDao.update(comment);
	}
	
	public List<CommentVO> getComment(CommentSO so) {
		return commentDao.getCommentList(so);
	}
	
	public void increaseListenTimes(Integer lessonId) {
		ListenLogSO so = new ListenLogSO();
		so.setLessonId(lessonId);
		List<ListenLogPO> poList = listenLogDao.getList(so);
		LessonPO lessonPO = lessonDao.getById(lessonId);
		if (!poList.isEmpty()) {
			ListenLogPO po = poList.get(0);
			po.setRecordLen(po.getRecordLen() + lessonPO.getAudioLen());
			listenLogDao.update(po);
		} else {
			ListenLogPO po = new ListenLogPO();
			po.setRecordLen(lessonPO.getAudioLen());
			po.setMaxPos(0);
			po.setRecordPos(0);
			po.setLessonId(lessonId);
			listenLogDao.create(po);
		}
	}
	
	public void recordListenPos(Integer lessonId, Integer pos) {
		LessonPO lessonPO = lessonDao.getById(lessonId);
		Integer audioLen = lessonPO.getAudioLen();
		Integer realLen = pos > audioLen ? audioLen : pos;
		ListenLogSO so = new ListenLogSO();
		so.setLessonId(lessonId);
		List<ListenLogPO> poList = listenLogDao.getList(so);
		if (!poList.isEmpty()) {
			ListenLogPO po = poList.get(0);
			po.setRecordPos(realLen);
			if (po.getMaxPos() < realLen) {
				po.setMaxPos(realLen);
			}
			po.setRecordLen(po.getRecordLen() + 4);
			listenLogDao.update(po);
		} else {
			ListenLogPO po = new ListenLogPO();
			po.setRecordLen(0);
			po.setMaxPos(realLen);
			po.setRecordPos(realLen);
			po.setLessonId(lessonId);
			listenLogDao.create(po);
		}
	}
	
	public void payCourse(String orderTradeNo) {
		OrderPO orderPO = orderDao.getByOrderNo(orderTradeNo);
		if (orderPO == null) {
			throw new ServiceException(ServiceException.ORDER_NOT_FOUND);
		}
		PurchaseLogPO po = purchaseLogDao.getByOrderNo(orderTradeNo);
		if (po == null) {
			po = new PurchaseLogPO();
			po.setCourseId(orderPO.getCourseId());
			po.setOrderTradeNo(orderTradeNo);
			po.setOriginalPrice(orderPO.getOrderPrice());
			po.setPayment(orderPO.getFee());
			po.setPayMethod(PayMethod.NORMAL.toString());
			po.setClassify(orderPO.getCourseType());
			po.setCouponId(orderPO.getCouponId());
			purchaseLogDao.create(po);
			//短信提醒
			if (CourseType.NORMAL.toString().equalsIgnoreCase(orderPO.getCourseType())) {
				CoursePO course = courseDao.getById(orderPO.getCourseId());
				SubMailService.notifyPurchase(SecurityContextHolder.getUserPhone(), course.getTitle());
			}
		}
		afterPay(orderPO);
	}

	/**
	 * 方法内的方法需要都可以重复调用
	 */
	public void afterPay(OrderPO orderPO) {
		if (CourseType.LIVE.toString().equalsIgnoreCase(orderPO.getCourseType())) {
			liveUserService.afterPaid(orderPO.getCourseId(), orderPO.getOrderPrice());
		}
		//优惠券标记为已使用
		if (orderPO.getCouponId() != null) {
			UserCouponPO coupon = userCouponDao.getById(orderPO.getCouponId());
			if (coupon != null && !BoolStatus.Y.toString().equalsIgnoreCase(coupon.getUsed())) {
				coupon.setUsed(BoolStatus.Y.toString());
				userCouponDao.update(coupon);
			}
		}
	}
	
	public void getFreeCourse(Integer courseId) {
		PurchaseLogSO plSO = new PurchaseLogSO();
		plSO.setCourseId(courseId);
		Integer count = purchaseLogDao.count(plSO);
		if (count > 0) {
			throw new ServiceException(ServiceException.ORDER_COURSE_ALREADY_GOT);
		}
		UserCouponSO cso = new UserCouponSO();
		cso.setType(CouponType.CARD.toString());
		cso.setUsed(BoolStatus.N.toString());
		cso.setValidityFrom(new Date());
		UserCouponPO coupon = userCouponDao.getOne(cso);
		if (coupon == null) {
			throw new ServiceException(ServiceException.COUPON_DEPLETION);
		}

		CoursePO course = courseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		PurchaseLogPO po = new PurchaseLogPO();
		po.setPayMethod(PayMethod.FREE.toString());
		po.setPayment(0);
		po.setOriginalPrice(course.getPrice());
		po.setOrderTradeNo("f" + DateUtils.getDateTimeString(new Date()).replaceAll("\\D", "") + RandomUtils.getRandomDigitalString(5000, 4));
		po.setCourseId(courseId);
		po.setCouponId(coupon.getId());
		po.setClassify(CourseType.NORMAL.toString());
		purchaseLogDao.create(po);
		
		coupon.setUsed(BoolStatus.Y.toString());
		userCouponDao.update(coupon);

		//发送短信提醒，此代码块只会执行一次
		SubMailService.notifyPurchase(SecurityContextHolder.getUserPhone(), course.getTitle());
	}
	
	public UserVO getUserInfo() {
		String openId = SecurityContextHolder.getUserOpenId();
		
		//user
		UserSO userSO = new UserSO();
		userSO.setOpenId(openId);
		List<UserPO> userPOList = userDao.getList(userSO);
		UserPO uPO = userPOList.get(0);
		UserVO vo = dozer.map(uPO, UserVO.class);
		
		vo.setFreeCourseRemained(userCouponDao.getFreeCourseLeft());
		
		//rela
		UserRelaSO relaSo = new UserRelaSO();
		relaSo.setOpenId(openId);
		List<UserRelaPO> relaPoList = userRelaDao.getList(relaSo);
		UserRelaPO rela = relaPoList.get(0);
		vo.setPhone(rela.getPhone());
		return vo;
	}
	
	public UserVO getUserStatistics() {
		PurchaseLogSO pso = new PurchaseLogSO();
		pso.setClassify(CourseType.NORMAL.toString());
		Integer purchasedCourseCount = purchaseLogDao.count(pso);
		List<ListenLogPO> llPOList = listenLogDao.getList(new ListenLogSO(true));
		Integer listenedTime = 0;
		for (ListenLogPO po : llPOList) {
			listenedTime += po.getRecordLen();
		}
		UserPO upo = userRelaDao.getByOpenId(SecurityContextHolder.getUserOpenId());
		Integer days = DateUtils.daysBetween(upo.getCreateTime(), new Date());
		UserVO vo = new UserVO();
		vo.setId(SecurityContextHolder.getUserId());
		vo.setPurchasedCourseCount(purchasedCourseCount);
		vo.setListenedLessonCount(llPOList.size());
		vo.setListenedTimeInSecond(listenedTime);
		vo.setSignedDays(days);
		return vo;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> createPayOrder(Integer courseId, Integer couponId) {
		boolean purchased = purchaseLogDao.recordExists(CourseType.NORMAL, courseId, SecurityContextHolder.getUserDomainId());
		if (purchased) {
			throw new ServiceException(ServiceException.ORDER_COURSE_ALREADY_GOT);
		}
		WXPreOrderMO mo = userService.doCreateOrder(courseId, couponId);
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
		CoursePO course = courseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}

		//create order in local db first.
		UserCouponPO coupon = null;
		if (couponId != null) {
			coupon = userCouponDao.getById(couponId);
			couponService.validateCoupon(coupon, courseId);
		}
		BigDecimal fee = new BigDecimal(course.getPrice());
		if (coupon != null) {
			fee = fee.subtract(new BigDecimal(coupon.getValue()));
		}
		fee = fee.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : fee;
		OrderPO po = new OrderPO();
		po.setCourseType(CourseType.NORMAL.toString());
		po.setCourseId(courseId);
		po.setOrderPrice(course.getPrice());
		po.setOrderTradeNo(DateUtils.getDateTimeString(new Date()).replaceAll("\\D", "") + RandomUtils.getRandomDigitalString(5000, 4));
		po.setTerminalIP(ContextUtils.getRemoteIP());
		po.setCouponId(coupon == null ? null : couponId);
		po.setFee(fee.intValue());
		orderDao.create(po);
		boolean purchased = false;
		if (fee.compareTo(BigDecimal.ZERO) <= 0) {
			payCourse(po.getOrderTradeNo());
			purchased = true;
		}
		String openId = SecurityContextHolder.getUserOpenId();
		WXPreOrderMO mo = new WXPreOrderMO();
		mo.setBody("购买课程-" + course.getTitle());
		mo.setTradeNo(po.getOrderTradeNo());
		mo.setOpenId(openId);
		mo.setIp(po.getTerminalIP());
		mo.setFee(po.getFee());
		mo.setPurchased(purchased ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		return mo;
	}

	public List<PurchasedCoursePO> getPurchasedCourse() {
		return courseDao.getPaidCourseList();
	}
	
	public void confirmFreeCourse(String nonce) {
		UserCoursePO po = userCourseDao.getByNonce(nonce);
		if (po == null) {
			throw new ServiceException(ServiceException.DATA_NO_QUALIFICATION);
		}
		Integer domainId = SecurityContextHolder.getUserDomainId();
		if (domainId.equals(po.getDomainId())) {
			throw new ServiceException(ServiceException.DATA_QUALIFICATION_TAKEN);
		} else if (po.getDomainId() != null) {
			throw new ServiceException(ServiceException.DATA_LINK_INVALID);
		}
		po.setDomainId(domainId);
		userCourseDao.update(po);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, po.getExpiryIn() - 1);
		List<UserCouponPO> couponList = new ArrayList<>(po.getFreeCourseNum());
		for(int i = 0; i < po.getFreeCourseNum(); i++) {
			UserCouponPO coupon = new UserCouponPO();
			coupon.setUsed(BoolStatus.N.toString());
			coupon.setValidity(DateUtils.setMaxTime(cal).getTime());
			coupon.setType(CouponType.CARD.toString());
			couponList.add(coupon);
		}
		userCouponDao.createList(couponList);
	}
	
	public RedPacketMO getRedPacketInfo(String nonce) {
		RedPacketSO so = new RedPacketSO();
		so.setNonce(nonce);
		so.setType(RedPacketType.SENDER.toString());
		so.setIgnoreDomainId(true);
		List<RedPacketPO> poList = redPacketDao.getList(so);
		if (poList.isEmpty()) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		RedPacketPO po = poList.get(0);
		UserPO sender = userRelaDao.getUserByDomainId(po.getDomainId());
		LessonPO lesson = lessonDao.getById(po.getLessonId());
		LessonContentPO content = lessonContentDao.getById(lesson.getContentId());
		CoursePO course = courseDao.getById(lesson.getCourseId());
		SpeakerPO speaker = speakerDao.getById(course.getSpeakerId());
		RedPacketSO openedSO = new RedPacketSO();
		openedSO.setType(RedPacketType.RECEIVER.toString());
		openedSO.setNonce(nonce);
		openedSO.setIgnoreDomainId(true);
		List<RedPacketPO> openedPOList = redPacketDao.getList(openedSO);
		Integer domainId = SecurityContextHolder.getUserDomainId();
		boolean opened = false;
		for (RedPacketPO p : openedPOList) {
			if (domainId.equals(p.getDomainId())) {
				opened = true;
				break;
			}
		}
		RedPacketMO mo = new RedPacketMO();
		mo.setUserHeadImg(sender.getHeadImg());
		mo.setUserName(sender.getNick());
		mo.setSpeakerCompany(speaker.getCompany());
		mo.setSpeakerHeadImg(speaker.getHeadImg());
		mo.setSpeakerTitle(speaker.getTitle());
		mo.setSpeakerName(speaker.getNick());
		mo.setLessonTitle(lesson.getTitle());
		mo.setContent(content.getContent());
		mo.setRedPacketRemained(CommonParams.RED_PACKET_NUM - openedPOList.size());
		mo.setAlreadyOpened(opened);
		return mo;
	}
	
	public void openRedPacket(String nonce) {
		RedPacketSO so = new RedPacketSO();
		so.setNonce(nonce);
		so.setType(RedPacketType.SENDER.toString());
		so.setIgnoreDomainId(true);
		List<RedPacketPO> poList = redPacketDao.getList(so);
		if (poList.size() == 0) throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		RedPacketPO po = poList.get(0);
		RedPacketSO rso = new RedPacketSO();
		rso.setNonce(nonce);
		rso.setType(RedPacketType.RECEIVER.toString());
		rso.setIgnoreDomainId(true);
		List<RedPacketPO> receiverList = redPacketDao.getList(rso);
		Integer domainId = SecurityContextHolder.getUserDomainId();
		for (RedPacketPO r : receiverList) {
			if (domainId.equals(r.getDomainId())) {
				throw new ServiceException(ServiceException.RED_PACKET_OPENED);
			}
		}
		if (CommonParams.RED_PACKET_NUM - receiverList.size() <= 0) throw new ServiceException(ServiceException.RED_PACKET_NONE);
		RedPacketPO receiver = new RedPacketPO();
		receiver.setLessonId(po.getLessonId());
		receiver.setNonce(nonce);
		receiver.setType(RedPacketType.RECEIVER.toString());
		redPacketDao.create(receiver);
	}
	
	public void sendCaptcha(String phone) {
		PhoneCodeSO so = new PhoneCodeSO();
		so.setPhone(phone);
		List<PhoneCodePO> poList = phoneCodeDao.getList(so);
		if (poList.size() == 0) {
			String captcha = RandomStringUtils.random(4, false, true);
			SubMailService.sendCaptcha(phone, captcha);
			PhoneCodePO po = new PhoneCodePO();
			po.setCode(captcha);
			po.setPhone(phone);
			phoneCodeDao.create(po);
		} else {
			PhoneCodePO po = poList.get(0);
			Date updateTime = po.getUpdateTime();
			if (new Date().getTime() - updateTime.getTime() < CommonParams.CAPTCHA_TIME_INTERVAL) {
				throw new ServiceException(ServiceException.CODE_REQUIRED_TOO_FREQUENCY);
			} else {
				String captcha = RandomStringUtils.random(4, false, true);
				SubMailService.sendCaptcha(phone, captcha);
				po.setCode(captcha);
				phoneCodeDao.update(po);
			}
		}
	}

	public UserPO getUserByOpenId(String openId) {
		if (StringUtils.isNullEmpty(openId)) {
			throw new ServiceException(ServiceException.COMMON_TOKEN_INVALID);
		}
		UserPO user = userRelaDao.getByOpenId(openId);
		if (user == null) {
			throw new ServiceException(ServiceException.DAO_OPENID_NOT_FOUND);
		}
		return user;
	}
	
	
	public List<WxUserInfo> getUserList(UserSO so) {
		Integer count = userDao.getUserInfoCount(so);
		so.setTotalCount(count);
		if (count == 0) return Collections.emptyList();
		List<WxUserInfo> poList = userDao.getUserInfoPageList(so);
		List<String> openIdList = new ArrayList<>(poList.size());
		for (UserPO po : poList) {
			openIdList.add(po.getOpenId());
		}
		Map<String, ValuePair<String, String>> wxInfoMap = wxUserInfoDao.getByOpenIdList(openIdList);
		for (WxUserInfo info : poList) {
			info.setWxNickname(wxInfoMap.get(info.getOpenId()).getName());
			info.setWxHeadImg(wxInfoMap.get(info.getOpenId()).getValue());
			info.setSubscribed(wxInfoMap.get(info.getOpenId()).getValue0());
		}
		return poList;
	}
	
	private void obtainCoupon() {
		UserCouponSO so = new UserCouponSO();
		so.setType(CouponType.COURSE_REG.toString());
		Integer count = userCouponDao.count(so);
		if (count > 0) {
			return;
		}
		UserCouponPO coupon = new UserCouponPO();
		coupon.setType(CouponType.COURSE_REG.toString());
		coupon.setUsed(BoolStatus.N.toString());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, CommonParams.INVITE_REG_COUPON_EXPIRE_DAYS - 1);
		coupon.setValidity(DateUtils.setMaxTime(cal).getTime());
		coupon.setValue(CommonParams.INVITE_REG_VALUE);
		userCouponDao.create(coupon);
	}

	public List<UserPO> getUserByPhone(List<String> phoneList) {
		if (ArrayUtils.isNullEmpty(phoneList)) return Collections.emptyList();
		UserRelaSO so = new UserRelaSO();
		so.setPhoneList(phoneList);
		so.setOrderBy("phone");
		List<UserRelaPO> relaList = userRelaDao.getNonPageList(so);
		List<Integer> uidList = relaList.stream().map(UserRelaPO::getUserId).collect(Collectors.toList());
		List<UserPO> userList = userDao.getByDomainIdList(uidList);
		Map<Integer, UserPO> map = new HashMap<>();
		for (UserPO u : userList) {
			map.put(u.getId(), u);
		}
		List<UserPO> resList = new ArrayList<>(relaList.size());
		for (UserRelaPO rela : relaList) {
			UserPO user = map.get(rela.getUserId());
			user.setPhone(rela.getPhone());
			resList.add(user);
		}
		return resList;
	}
	
}
