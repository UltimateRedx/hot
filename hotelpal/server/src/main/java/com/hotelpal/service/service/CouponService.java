package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.common.context.CommonParams;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.*;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.*;
import com.hotelpal.service.common.so.SysCouponSO;
import com.hotelpal.service.common.so.UserCouponSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.RegInviteVO;
import com.hotelpal.service.common.vo.UserCouponVO;
import com.hotelpal.service.service.parterner.wx.MsgPushService;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.apache.commons.codec.digest.DigestUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class CouponService {
	private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
	private static final BigDecimal ONE_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);
	@Resource
	private SysCouponDao sysCouponDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	@Resource
	private UserCouponDao userCouponDao;
	@Resource
	private RegInviteDao regInviteDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private CourseDao courseDao;
	@Resource
	private MsgPushService msgPushService;
	@Resource
	private UserDao userDao;
	@Resource
	private SpeakerDao speakerDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	private static final String SYS_COUPON_LINK = PropertyHolder.getProperty("content.course.coupon.link");
	private static final String COUPON_SERVER_SUFFIX = "s78trAZK2fNGJic6";


	public List<SysCouponPO> getSysCoupon(SysCouponSO so) {
		List<SysCouponPO> resList = sysCouponDao.getList(so);
		List<Integer> sysCouponIdList = resList.stream().map(BasePO::getId).collect(Collectors.toList());
		Map<Integer, ValuePair<Integer, Integer>> spentMap = userCouponDao.getSysCouponSpent(sysCouponIdList);
		for (SysCouponPO coupon : resList) {
			String link = SYS_COUPON_LINK.replaceFirst("@nonce", DigestUtils.sha1Hex(coupon.getId() + COUPON_SERVER_SUFFIX))
					.replaceFirst("@sysCouponId", String.valueOf(coupon.getId()));
			coupon.setLink(link);
			coupon.setSpent(spentMap.get(coupon.getId()).getName());
			coupon.setUsed(spentMap.get(coupon.getId()).getValue());
		}
		so.setTotalCount(sysCouponDao.count(so));
		return resList;
	}
	
	public void updateSysCoupon(SysCouponSO so) {
		if (so.getId() == null) {
			SysCouponPO po = dozerBeanMapper.map(so, SysCouponPO.class);
			po.setValue(Optional.ofNullable(so.getValue()).orElse(BigDecimal.ZERO).multiply(ONE_HUNDRED).intValue());
			po.setType(CouponType.COURSE.toString());
			if (ArrayUtils.isNotNullEmpty(so.getApplyToCourse())) {
				po.setApplyToCourse(String.join(",", so.getApplyToCourse().stream().map(String::valueOf).collect(Collectors.toList())));
			} else {
				po.setApplyToCourse(null);
			}
			po.setApplyToPrice(Optional.ofNullable(so.getApplyToPrice()).orElse(BigDecimal.ZERO).multiply(ONE_HUNDRED).intValue());
			setCouponTimeToMax(po, so);
			sysCouponDao.create(po);
		} else {
			SysCouponPO po = sysCouponDao.getById(so.getId());
			po.setValue(Optional.ofNullable(so.getValue()).orElse(BigDecimal.ZERO).multiply(ONE_HUNDRED).intValue());
			if (ArrayUtils.isNotNullEmpty(so.getApplyToCourse())) {
				po.setApplyToCourse(String.join(",", so.getApplyToCourse().stream().map(String::valueOf).collect(Collectors.toList())));
			} else {
				po.setApplyToCourse(null);
			}
			po.setApplyToPrice(Optional.ofNullable(so.getApplyToPrice()).orElse(BigDecimal.ZERO).multiply(ONE_HUNDRED).intValue());
			setCouponTimeToMax(po, so);
			sysCouponDao.update(po);
		}
	}
	private void setCouponTimeToMax(SysCouponPO po, SysCouponSO so) {
		String validityType = so.getValidityType();
		if (CouponValidityType.FIXED.toString().equalsIgnoreCase(validityType)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(so.getValidity());
			DateUtils.setMaxTime(cal);
			po.setValidity(cal.getTime());
		} else if (CouponValidityType.FIXED_DAY.toString().equalsIgnoreCase(validityType)) {
			Calendar cal = Calendar.getInstance();
			DateUtils.setMaxTime(cal);
			po.setValidity(cal.getTime());
		}
	}
	
	public void deleteSysCoupon(Integer id) {
		sysCouponDao.delete(id);
	}
	public void validateCoupon(UserCouponPO coupon , Integer courseId) {
		if (coupon == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if (!SecurityContextHolder.getUserDomainId().equals(coupon.getDomainId())) {
			throw new ServiceException(ServiceException.COMMON_ILLEGAL_ACCESS);
		}
		if (BoolStatus.Y.toString().equalsIgnoreCase(coupon.getUsed())) {
			throw new ServiceException(ServiceException.COUPON_USED);
		}
		if (new Date().after(coupon.getValidity())) {
			throw new ServiceException(ServiceException.COUPON_EXPIRED);
		}
		if (EnumHelper.isIn(coupon.getType(), new CouponType[]{CouponType.COURSE})) {
			SysCouponPO sysCoupon = sysCouponDao.getById(coupon.getSysCouponId());
			if (sysCoupon == null) {
				throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
			}
			if (CouponApply.PARTICULAR.toString().equalsIgnoreCase(sysCoupon.getApply())
					&& Arrays.stream(sysCoupon.getApplyToCourse().split(",")).mapToInt(Integer::parseInt).noneMatch(i -> i == courseId)) {
				throw new ServiceException(ServiceException.COUPON_NOT_APPLICABLE);
			}
		} else if (EnumHelper.isIn(coupon.getType(), new CouponType[]{CouponType.COURSE_REG_INVITE, CouponType.COURSE_REG})) {
			CoursePO course = courseDao.getById(courseId);
			if (course.getPrice() < UserCouponPO.COUPON_REG_PRICE_REQUIRE) {
				throw new ServiceException(ServiceException.COUPON_NOT_APPLICABLE);
			}
		}
	}
	
	public UserCouponVO getUserCoupon() {
		//学习卡
		UserCouponVO.Card card = new UserCouponVO.Card();
		UserCouponSO cardSo = new UserCouponSO();
		cardSo.setType(CouponType.CARD.toString());
		cardSo.setUsed(BoolStatus.N.toString());
		cardSo.setValidityFrom(new Date());
		List<UserCouponPO> cardCouponList = userCouponDao.getList(cardSo);
		Date latestValidity;
		if (!cardCouponList.isEmpty()) {
			card.setExists(BoolStatus.Y.toString());
			card.setLeftTimes(cardCouponList.size());
			latestValidity = cardCouponList.get(0).getValidity();
			for (UserCouponPO po : cardCouponList) {
				if (po.getValidity().before(latestValidity)) {
					latestValidity.setTime(po.getValidity().getTime());
				}
			}
			card.setValidity(latestValidity);
		}
		
		//公开课vip
		UserCouponVO.LiveVip vip = new UserCouponVO.LiveVip();
		boolean isVip = BoolStatus.Y.toString().equalsIgnoreCase(SecurityContextHolder.getLiveVip());
		vip.setExists(isVip ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		if (isVip) {
			vip.setValidity(SecurityContextHolder.getLiveVipValidity());
		}
		List<UserCouponPO> couponList = userCouponDao.getAllUserCoupon();
		
		//将sysCoupon 关联的课程title查出来
		Map<Integer, List<String>> map = new HashMap<>();
		for (UserCouponPO coupon : couponList) {
			if (coupon.getDetail() != null && CouponApply.PARTICULAR.toString().equalsIgnoreCase(coupon.getDetail().getApply())) {
				Integer sysCouponId = coupon.getDetail().getId();
				if (!map.containsKey(sysCouponId)) {
					List<Integer> courseIdList = Arrays.stream(coupon.getDetail().getApplyToCourse().split(",")).map(Integer::parseInt).collect(Collectors.toList());
					map.put(sysCouponId, courseDao.getTitleListByIdList(courseIdList));
				}
			}
		}
		for (UserCouponPO coupon : couponList) {
			if (coupon.getDetail() != null && CouponApply.PARTICULAR.toString().equalsIgnoreCase(coupon.getDetail().getApply())) {
				coupon.getDetail().setApplyToCourseTitle(map.get(coupon.getDetail().getId()));
			}
		}
		UserCouponVO res = new UserCouponVO();
		res.setCard(card);
		res.setLiveVip(vip);
		res.setCoupon(couponList);
		return res;
	}
	
	public RegInviteVO getInviteRegList() {
		RegInviteVO vo = new RegInviteVO();
		vo.setTotalCoupon(userCouponDao.getUserTotalCoupon());
		List<RegInvitePO> invitedList = regInviteDao.getRegInvitedUser();
		List<RegInviteVO.InviteList> resList = new ArrayList<>();
		String batch = "";
		for (RegInvitePO po : invitedList) {
			if (!batch.equals(po.getBatch())) {
				resList.add(new RegInviteVO.InviteList());
				batch = po.getBatch();
				resList.get(resList.size() - 1).setBatch(po.getBatch());
				resList.get(resList.size() - 1).setCouponCollected(po.getCouponCollected());
				resList.get(resList.size() - 1).setUserList(new ArrayList<>(CommonParams.INVITE_REG_REQUIRE));
			}
			resList.get(resList.size() - 1).getUserList().add(po.getInvitedUser());
		}
		vo.setInviteList(resList);
		return vo;
	}
	
	
	public void collectUserCoupon(String batch) {
		List<RegInvitePO> poList = regInviteDao.getListByBatch(batch);
		if (ArrayUtils.isNullEmpty(poList)) {
			throw new ServiceException(ServiceException.COUPON_BATCH_NOT_EXISTS);
		}
		RegInvitePO po = poList.get(0);
		if (BoolStatus.Y.toString().equalsIgnoreCase(po.getCouponCollected())) {
			throw new ServiceException(ServiceException.COUPON_OBTAINED);
		}
		if (poList.size() < CommonParams.INVITE_REG_REQUIRE) {
			throw new ServiceException(ServiceException.COUPON_REG_INVITE_INCOMPLETE);
		}
		Date validity = DateUtils.setMaxTime(DateUtils.addAndGet(new Date(), CommonParams.INVITE_REG_COUPON_EXPIRE_DAYS - 1)).getTime();
		for (int i = 0; i < CommonParams.INVITE_REG_COUPON_NUM; i++) {
			UserCouponPO coupon = new UserCouponPO();
			coupon.setValue(CommonParams.INVITE_REG_VALUE);
			coupon.setType(CouponType.COURSE_REG_INVITE.toString());
			coupon.setValidity(validity);
			coupon.setUsed(BoolStatus.N.toString());
			userCouponDao.create(coupon);
		}
		for (RegInvitePO p : poList) {
			p.setCouponCollected(BoolStatus.Y.toString());
			regInviteDao.update(p);
		}
	}
	
	/**
	 * 用户从链接等获取优惠券
	 */
	public void userGetCoupon(Integer couponId, String code) {
		if (couponId == null) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		String rightCode = DigestUtils.sha1Hex(couponId + COUPON_SERVER_SUFFIX);
		if (!rightCode.equalsIgnoreCase(code)) {
			throw new ServiceException(ServiceException.COMMON_ILLEGAL_ACCESS);
		}
		SysCouponPO sysCoupon = sysCouponDao.getById(couponId);
		if (sysCoupon == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		UserCouponSO so = new UserCouponSO();
		so.setSysCouponId(couponId);
		boolean collected = userCouponDao.count(so) > 0;
		if (collected) {
			throw new ServiceException(ServiceException.COUPON_OBTAINED);
		}
		//优惠券过期校验
		if ((CouponValidityType.FIXED.toString().equals(sysCoupon.getType())
				|| CouponValidityType.FIXED_DAY.toString().equals(sysCoupon.getType()))
				&& new Date().after(sysCoupon.getValidity())) {
			throw new ServiceException(ServiceException.COUPON_EXPIRED);
		}

		Integer totalUsed = userCouponDao.countBySysCouponId(couponId);
		if (sysCoupon.getTotal() != null && sysCoupon.getTotal() > 0 && sysCoupon.getTotal() <= totalUsed) {
			throw new ServiceException(ServiceException.COUPON_DEPLETION);
		}
		
		UserCouponPO coupon = new UserCouponPO();
		coupon.setUsed(BoolStatus.N.toString());
		coupon.setType(sysCoupon.getType());
		coupon.setValue(sysCoupon.getValue());
		coupon.setSysCouponId(sysCoupon.getId());
		String sysCouponValidityType = sysCoupon.getValidityType();
		if (CouponValidityType.FIXED_DAYS.toString().equalsIgnoreCase(sysCouponValidityType)) {
			coupon.setValidity(DateUtils.setMaxTime(DateUtils.addAndGet(new Date(), sysCoupon.getValidityDays() - 1)).getTime());
		} else {
			coupon.setValidity(sysCoupon.getValidity());
		}
		userCouponDao.create(coupon);
	}

	
	public SysCouponPO getSysCouponInfo (Integer sysCouponId) {
		SysCouponPO coupon = sysCouponDao.getById(sysCouponId);
		if (coupon == null || BoolStatus.Y.toString().equalsIgnoreCase(coupon.getDeleted())) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if (CouponApply.PARTICULAR.toString().equalsIgnoreCase(coupon.getApply())) {
			List<Integer> courseIdList = Arrays.stream(coupon.getApplyToCourse().split(",")).map(Integer::parseInt).collect(Collectors.toList());
			List<String> titleList = courseDao.getTitleListByIdList(courseIdList);
			coupon.setApplyToCourseTitle(titleList);
			List<CoursePO> courseList = courseDao.getByIdList(courseIdList);
			List<Integer> speakerIdList = courseList.stream().mapToInt(CoursePO::getSpeakerId).boxed().collect(Collectors.toList());
			List<SpeakerPO> speakerList = speakerDao.getByIdList(speakerIdList);
			Map<Integer, SpeakerPO> speakerMap = new HashMap<>();
			for (SpeakerPO sp : speakerList) {
				speakerMap.put(sp.getId(), sp);
			}
			Set<Integer> purchasedCourseId = purchaseLogDao.getAllPurchasedCourseId();

			for (CoursePO course : courseList) {
				course.setSpeaker(speakerMap.get(course.getSpeakerId()));
				course.setPurchased(purchasedCourseId.contains(course.getId()));
			}
			coupon.setApplyToCoursePO(courseList);
		}
		UserCouponSO so = new UserCouponSO();
		so.setSysCouponId(sysCouponId);
		Integer count = userCouponDao.count(so);
		coupon.setAcquired(count > 0 ? BoolStatus.Y.toString() : BoolStatus.N.toString());
		return coupon;
	}

	/**
	 * 临时工作，对于没有购买葛健课程的注册用户(填写了手机号的)，分发优惠券并发送提醒
	 */
	public void gejianTask(String key) {
		SecurityContextHolder.loginSuperDomain();
		List<UserPO> userList = Collections.emptyList();
		 if ("ALL".equalsIgnoreCase(key)){
			userList = userRelaDao.getUserByNonPurchase(27);
		} else if (!StringUtils.isNullEmpty(key)) {
			userList = userDao.getByDomainIdList(Arrays.stream(key.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList()));
		}

		String title = "葛健老师想和你做好朋友\n" +
				"\n" +
				"送你一份知识红包，已发送到你的账户";
		SysCouponPO sysCoupon = sysCouponDao.getById(82);
		String dateString = DateUtils.getDateString(sysCoupon.getValidity());
		for (UserPO user : userList) {
			try {
				SecurityContextHolder.setTargetDomain(user.getDomainId());
				UserCouponPO coupon = new UserCouponPO();
				coupon.setUsed(BoolStatus.N.toString());
				coupon.setType(sysCoupon.getType());
				coupon.setValue(sysCoupon.getValue());
				coupon.setSysCouponId(sysCoupon.getId());
				coupon.setValidity(sysCoupon.getValidity());
				userCouponDao.create(coupon);
				msgPushService.pushOverdueTaskNotification(user.getOpenId(), title, "用100元知识券学酒万公式", dateString,
						"时间不等人，点击详情快来使用吧！",
						"https://hotelpal.cn/coupon");
			}catch (Exception e) {
				logger.error("create or pushNotification failed. domainId=" + user.getDomainId(), e);
			}
		}
	}


}
