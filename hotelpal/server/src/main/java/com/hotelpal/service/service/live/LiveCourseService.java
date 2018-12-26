package com.hotelpal.service.service.live;

import com.hotelpal.service.basic.mysql.dao.SysPropertyDao;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.basic.mysql.dao.live.*;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.BasePO;
import com.hotelpal.service.common.po.SysPropertyPO;
import com.hotelpal.service.common.po.live.*;
import com.hotelpal.service.common.so.live.ChatLogSO;
import com.hotelpal.service.common.so.live.LiveCourseImageSO;
import com.hotelpal.service.common.so.live.LiveCourseSO;
import com.hotelpal.service.common.utils.HttpGetUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.AdminLiveCourseVO;
import com.hotelpal.service.common.vo.LiveUserInfoVO;
import com.hotelpal.service.service.cache.CacheService;
import com.hotelpal.service.service.live.netty.ServerHelper;
import com.hotelpal.service.service.parterner.wx.MsgPushService;
import com.hotelpal.service.service.parterner.wx.WXService;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Transactional
public class LiveCourseService {
	private static final Logger logger = LoggerFactory.getLogger(LiveCourseService.class);
	private static final String ADMIN_DOMAIN = PropertyHolder.getProperty("http.admin.domain");
	@Resource
	private LiveCourseService liveCourseService;
	@Resource
	private LiveCourseDao liveCourseDao;
	@Resource
	private LiveCourseContentDao liveCourseContentDao;
	@Resource
	private LiveUserService liveUserService;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	@Resource
	private ChatLogDao chatLogDao;
//	@Resource
//	private LiveChatService liveChatService;
	@Resource
	private AssistantMessageDao assistantMessageDao;
	@Resource
	private LiveCourseImageDao liveCourseImageDao;
	@Resource
	private MsgPushService msgPushService;
	@Resource
	private WXService wxService;
	@Resource
	private SysPropertyDao sysPropertyDao;
	@Resource
	private LiveEnrollDao liveEnrollDao;
	@Resource
	private ServerHelper serverHelper;
	@Resource
	private UserRelaDao userRelaDao;

	
	
	
	
	private static final String LIVE_AUDIO_LINK = PropertyHolder.getProperty("content.course.live.audio.link");
	
	public LiveCoursePO getCourse(Integer courseId) {
		return liveCourseDao.getById(courseId);
	}
	public void updateLiveCourse(LiveCoursePO po) {
		liveCourseDao.update(po);
	}
	public void removeLiveCourse(Integer id) {
		LiveCoursePO course = liveCourseDao.getById(id);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		liveCourseDao.delete(id);
	}
	
	
	public List<LiveCoursePO> getLiveCourseList() {
		LiveCourseSO so = new LiveCourseSO();
		so.setPublish(BoolStatus.Y.toString());
		so.setOrderBy("createTime");
		so.setOrder("desc");
		List<LiveCoursePO> courseList = liveCourseDao.getList(so);
		List<Integer> idList = courseList.stream().map(BasePO::getId).collect(Collectors.toList());
		Map<Integer, Integer> baseLineMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_ENROLL, idList);
		Map<Integer, Integer> ongoingBaseLineMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_ONGOING, idList);
		Map<Integer, Integer> totalBaseLineMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_TOTAL, idList);
		for (LiveCoursePO course : courseList) {
			course.setPresent(serverHelper.getCoursePresent(course.getId()) + ongoingBaseLineMap.get(course.getId()));
			Integer times = course.getFreeEnrolledTimes() == null ? 0 : course.getFreeEnrolledTimes();
			course.setFreeEnrolledTimes(times + baseLineMap.get(course.getId()));
			Integer totalTimes = course.getTotalPeople() == null ? 0 : course.getTotalPeople();
			course.setTotalPeople(totalTimes + totalBaseLineMap.get(course.getId()));
			course.setPurchasedTimes(course.getFreePurchasedTimes() + course.getPurchasedTimes());
		}
		return courseList;
	}
	public List<AdminLiveCourseVO> getPageList(LiveCourseSO so) {
//		so.setDeleted(null);
		so.setTotalCount(liveCourseDao.count(so));
		List<LiveCoursePO> poList = liveCourseDao.getList(so);
		List<AdminLiveCourseVO> voList = new ArrayList<>(poList.size());
		List<Integer> idList = poList.stream().map(BasePO::getId).collect(Collectors.toList());
		Map<Integer, Integer> enrollMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_ENROLL, idList);
		Map<Integer, Integer> ongoingMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_ONGOING, idList);
		Map<Integer, Integer> totalMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_TOTAL, idList);
		for (LiveCoursePO po : poList) {
			AdminLiveCourseVO vo = dozerBeanMapper.map(po, AdminLiveCourseVO.class);
			vo.setEnrollBaseLine(enrollMap.get(po.getId()));
			vo.setOngoingBaseLine(ongoingMap.get(po.getId()));
			vo.setTotalBaseLine(totalMap.get(po.getId()));
			voList.add(vo);
		}
		return voList;
	}
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateLiveCourse(LiveCourseSO so){
		LiveCoursePO course = liveCourseService.doUpdateLiveCourse(so);
		msgPushService.loadOrUpdateLiveCourseOpeningTrigger(course.getId());
		//start or terminate live
		if (!BoolStatus.Y.toString().equalsIgnoreCase(course.getDeleted())) {
			serverHelper.reInitCourseEnv(course.getId());
		}else {
			serverHelper.shutdownOldCourseEnv(course.getId());
		}
		//update invite img cache
		CacheService.removeValue(CacheService.KEY_LIVE_COURSE_INVITE_IMG + course.getId());
		getImgCache(course.getId());
	}

	private static final BigDecimal ONE_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);
	@Transactional
	public LiveCoursePO doUpdateLiveCourse(LiveCourseSO so) {
		so.setPrice(Optional.ofNullable(so.getPrice()).orElse(BigDecimal.ZERO).multiply(ONE_HUNDRED));
		boolean create = false;
		if(so.getId() == null) create = true;
		LiveCoursePO course = liveCourseDao.getById(so.getId());
		if (course == null) create = true;
		if (create) {
			LiveCourseContentPO content = new LiveCourseContentPO();
			content.setInstruction(so.getInstruction());
			content.setIntroduce(so.getIntroduce());
			liveCourseContentDao.create(content);
			course = dozerBeanMapper.map(so, LiveCoursePO.class);
			course.setContentId(content.getId());
			course.setStatus(LiveCourseStatus.ENROLLING.toString());
			course.setCouponShow(BoolStatus.N.toString());
			course.setPurchasedTimes(0);
			course.setFreeEnrolledTimes(0);
			liveCourseDao.create(course);
			course.setLiveAudio(LIVE_AUDIO_LINK.replaceFirst("@courseId", String.valueOf(course.getId())));
			liveCourseDao.update(course);
		} else {
			LiveCourseContentPO content = liveCourseContentDao.getById(course.getContentId());
			content.setIntroduce(so.getIntroduce());
			content.setInstruction(so.getInstruction());
			liveCourseContentDao.update(content);
			String status = course.getStatus();
			dozerBeanMapper.map(so, course);
			course.setStatus(status);
			liveCourseDao.update(course);
		}
		updateEnroll(course.getId(), course.getInviteRequire(), course.getOpenTime());
		return course;
	}

	private void updateEnroll(Integer liveCourseId, Integer inviteRequire, Date openTime) {
		if (inviteRequire == null) return;
		List<ValuePair<Integer, Integer>> domainIdList = liveEnrollDao.getInvitingDomainIdList(liveCourseId);
		List<Integer> toUpdateList = domainIdList.stream().filter(p -> p.getValue() >= inviteRequire).mapToInt(ValuePair::getName).boxed().collect(Collectors.toList());
		List<Integer> successDomainIdList = new ArrayList<>(toUpdateList.size());
		for (Integer domainId : toUpdateList) {
			try {
					liveEnrollDao.updateToEnrolled(liveCourseId, domainId);
					successDomainIdList.add(domainId);
			}catch (Exception e) {
				logger.error("updateEnroll Exception...", e);
			}
		}
		if (successDomainIdList.isEmpty()) return;
		List<String> openIdList = userRelaDao.getOpenIdByDomainIdList(successDomainIdList);
		for (String openId : openIdList) {
			CompletableFuture.runAsync(() -> msgPushService.pushInviteCompleteMsg(liveCourseId, openId, openTime));
		}
	}


	public LiveCoursePO getLiveCourse(Integer courseId) {
		LiveCoursePO po = liveCourseDao.getById(courseId, true);
		if (po == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		Map<Integer, Integer> baseLineMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_ENROLL, Collections.singletonList(po.getId()));
		Integer times = po.getFreeEnrolledTimes() == null ? 0 : po.getFreeEnrolledTimes();
		po.setFreeEnrolledTimes(times + baseLineMap.get(po.getId()));
		Map<Integer, Integer> totalBaseLineMap = sysPropertyDao.getBaseLine(SysPropertyPO.LIVE_BASE_LINE_TOTAL, Collections.singletonList(po.getId()));
		Integer totalTimes = po.getTotalPeople() == null ? 0 : po.getTotalPeople();
		po.setTotalPeople(totalTimes + totalBaseLineMap.get(po.getId()));
		LiveUserInfoVO userInfo = liveUserService.getUserInfo(courseId, po);
		po.setUserInfo(userInfo);
		po.setPurchasedTimes(po.getFreePurchasedTimes() + po.getPurchasedTimes());
		return po;
	}
	
	public List<ChatLogPO> getChatList(ChatLogSO so) {
		so.setOrderBy("createTime");
		if (!SecurityContextHolder.isSuperDomain()) {
			so.setBlocked(BoolStatus.N.toString());
		}
		so.setIgnoreDomainId(true);
		return chatLogDao.getChatList(so);
	}
	
	public void startLive(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		serverHelper.startLive(courseId);
		course.setStatus(LiveCourseStatus.ONGOING.toString());
		liveCourseDao.update(course);
	}
	
	public void terminateLive(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		serverHelper.shutdownLive(courseId);
		course.setStatus(LiveCourseStatus.ENDED.toString());
		liveCourseDao.update(course);
	}
	
	public List<AssistantMessagePO> assistantMsgList(Integer courseId) {
		return assistantMessageDao.getMessageList(courseId);
	}
	
	public void changeCouponShowStatus(Integer courseId, boolean show) {
		serverHelper.showCoupon(courseId, show);
	}
	
	public String updateCourseImage(Integer courseId, List<String> imgList) {
		if (CollectionUtils.isEmpty(imgList)) {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (course == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		liveCourseImageDao.deleteByCourseId(courseId);
		List<LiveCourseImagePO> poList = new ArrayList<>(imgList.size());
		for (int i = 0, j = imgList.size(); i < j; i++) {
			LiveCourseImagePO po = new LiveCourseImagePO();
			po.setImg(imgList.get(i));
			po.setImgOrder(i);
			po.setLiveCourseId(courseId);
			poList.add(po);
		}
		liveCourseImageDao.createList(poList);
		if (StringUtils.isNullEmpty(course.getLiveImg())) {
			course.setLiveImg(createLiveImgUrl(courseId));
			liveCourseDao.update(course);
		}
		return ADMIN_DOMAIN + "#/" + course.getLiveImg();
	}
	
	private String createLiveImgUrl(Integer courseId) {
		return "live/img/" + courseId + "@" + RandomStringUtils.random(64, true, true);
	}
	
	public List<String> getLiveImg(Integer courseId) {
		LiveCourseImageSO so = new LiveCourseImageSO();
		so.setLiveCourseId(courseId);
		so.setPageSize(null);
		so.setOrderBy("imgOrder");
		List<LiveCourseImagePO> imgs = liveCourseImageDao.getList(so);
		List<String> resList = new ArrayList<>(imgs.size());
		for (LiveCourseImagePO po : imgs) {
			resList.add(po.getImg());
		}
		return resList;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Object getImgCache(Integer liveCourseId) {
		Object old = CacheService.getValue(CacheService.KEY_LIVE_COURSE_INVITE_IMG + liveCourseId);
		if (old != null) {
			return old;
		}
		LiveCoursePO course = liveCourseDao.getById(liveCourseId);
		if (course == null || StringUtils.isNullEmpty(course.getInviteImg())) return null;
		HttpParams params = new HttpParams();
		params.setUrl(course.getInviteImg());
		byte[] res = HttpGetUtils.executeGetBytes(params);
		CacheService.setValue(CacheService.KEY_LIVE_COURSE_INVITE_IMG + liveCourseId, res);
		return res;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public byte[] getQrCodeCache(Integer liveCourseId) {
		Integer domainId = SecurityContextHolder.getUserDomainId();
//		Object old = CacheService.getValue(CacheService.KEY_LIVE_COURSE_QR_CODE_IMG + liveCourseId + "#" + domainId);
//		if (old != null) {
//			return (byte[]) old;
//		}
		//数据库记录的ticket有效期
		String userTicket = wxService.getQrCodeTicket(liveCourseId);
		byte[] imgByte = WXService.getQrCodeImg(userTicket);
		CacheService.setValue(CacheService.KEY_LIVE_COURSE_QR_CODE_IMG + liveCourseId + "#" + domainId, imgByte);
		return imgByte;
	}

	public void configureDisplayBaseLine(Integer liveCourseId, String type, Integer baseLine) {
		if (StringUtils.isNullEmpty(type)) {
			throw new ServiceException(ServiceException.COMMON_EMPTY_INPUT_PARAMETER);
		}
		String key;
		if ("ENROLL".equalsIgnoreCase(type)) {
			key = SysPropertyPO.LIVE_BASE_LINE_ENROLL;
		} else if ("ONGOING".equalsIgnoreCase(type)) {
			key = SysPropertyPO.LIVE_BASE_LINE_ONGOING;
			serverHelper.setOngoingBaseLine(liveCourseId, baseLine);
		} else if ("TOTAL".equalsIgnoreCase(type)) {
			key = SysPropertyPO.LIVE_BASE_LINE_TOTAL;
		} else {
			throw new ServiceException(ServiceException.COMMON_REQUEST_DATA_INVALID);
		}
		SysPropertyPO property = sysPropertyDao.getByName(key + liveCourseId);
		if(property == null) {
			property = new SysPropertyPO();
			property.setName(key + liveCourseId);
			property.setValue(String.valueOf(baseLine));
			sysPropertyDao.create(property);
		} else {
			property.setValue(String.valueOf(baseLine));
			sysPropertyDao.update(property);
		}
	}
}
