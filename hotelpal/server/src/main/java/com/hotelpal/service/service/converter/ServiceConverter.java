package com.hotelpal.service.service.converter;

import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.dto.response.*;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CourseStatus;
import com.hotelpal.service.common.enums.LessonType;
import com.hotelpal.service.common.enums.RedPacketType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.*;
import com.hotelpal.service.common.po.extra.PurchasedCoursePO;
import com.hotelpal.service.common.so.*;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.NumberUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.CommentVO;
import com.hotelpal.service.common.vo.UserVO;
import com.hotelpal.service.service.CourseService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.utils.DozerUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;


@Component
@Transactional
public class ServiceConverter {
	private static final String NEW = CourseStatus.NEW.toString();
	private static final String PREDICTION = CourseStatus.PREDICTION.toString();
	private static final String NORMAL = CourseStatus.NORMAL.toString();
	private static final int DEFAULT_RED_PACKET_NUM = 5;
	private static final String Y = BoolStatus.Y.toString();
	private static final String N = BoolStatus.N.toString();
	
	@Resource
	private UserService userService;
	@Resource
	private CommentDao commentDao;
	@Resource
	private CourseDao courseDao;
	@Resource
	private CourseService courseService;
	@Resource
	private LessonDao lessonDao;
	@Resource
	private LessonContentDao lessonContentDao;
	@Resource
	private ListenLogDao listenLogDao;
	@Resource
	private DozerUtils dozerUtils;
	@Resource
	private DozerBeanMapper dozerMapper;
	@Resource
	private RedPacketDao redPacketDao;
	
	
	
	public CommentListResponse getCommentList(String elite, Integer lessonId, Integer start, Integer limit) {
		CommentSO so = new CommentSO();
		if(Y.equalsIgnoreCase(elite)) {
			so.setElite(elite);
		}
		so.setLessonId(lessonId);
		if (start >= 0) {
			so.setFrom(start);
		}
		so.setLimit(limit);
		so.setIgnoreDomainId(true);
		Integer count = commentDao.count(so);
		so.setOrder("desc");
		so.setOrderBy("createTime");
		List<CommentVO> voList = userService.getComment(so);
		List<Integer> replyToIdList = new ArrayList<>();
		for (CommentVO vo : voList) {
			if (vo.getReplyToId() != null) {
				replyToIdList.add(vo.getReplyToId());
			}
		}
		List<CommentVO> replyToList = null;
		if (replyToIdList.size() > 0) {
			replyToList = commentDao.getByIdListVO(lessonId, replyToIdList);
		}
		CommentListResponse res = new CommentListResponse();
		res.setCount(count);
		res.setHasMore(count > start + limit);
		res.setCommentList(commentVOToResponse(voList));
		res.setReplyToCommentList(commentVOToResponse(replyToList));
		return res;
	}
	
	private List<CommentResponse> commentVOToResponse(List<CommentVO> voList) {
		if (ArrayUtils.isNullEmpty(voList)) return null;
		List<CommentResponse> resList = new ArrayList<>(voList.size());
		for (CommentVO vo : voList) {
			CommentResponse cr = new CommentResponse();
			cr.setId(vo.getId());
			cr.setLessonId(vo.getLessonId());
			cr.setCreationTime(DateUtils.getDateTimeString(vo.getCreateTime()));
			cr.setReplytoId(vo.getReplyToId());
			cr.setZanCount(vo.getZanCount());
			cr.setContent(vo.getContent());
			if (Y.toString().equalsIgnoreCase(vo.getElite())) {
				cr.setElite((byte) 1);
			} else {
				cr.setElite((byte) 0);
			}
			if (Y.toString().equalsIgnoreCase(vo.getSpeaker())) {
				cr.setIsTheSpeaker((byte) 1);
			} else {
				cr.setIsTheSpeaker((byte) 0);
			}
			cr.setUserName(vo.getUserNick());
			cr.setUserTitle(vo.getUserTitle());
			cr.setUserHeadImg(vo.getUserHeadImg());
			cr.setUserCompany(vo.getUserCompany());
			cr.setLiked(Y.toString().equalsIgnoreCase(vo.getLiked()));
			if (Y.toString().equalsIgnoreCase(vo.getDeleted())) {
				cr.setDeleted((byte) 1);
			} else {
				cr.setDeleted((byte) 0);
			}
			resList.add(cr);
		}
		return resList;
	}
	
	public UserInfoResponse getUserInfo() {
		UserVO vo = userService.getUserInfo();
		UserInfoResponse res = new UserInfoResponse();
		res.setId(vo.getId());
		res.setWechatOpenId(vo.getOpenId());
		res.setHeadImg(vo.getHeadImg());
		res.setPhone(vo.getPhone());
		res.setNickname(vo.getNick());
		res.setCompany(vo.getCompany());
		res.setTitle(vo.getTitle());
		res.setFreeCourseRemained(vo.getFreeCourseRemained());
		return res;
	}
	
	public UserStatisticsResponse getUserStatistics() {
		UserVO vo = userService.getUserStatistics();
		UserStatisticsResponse res = new UserStatisticsResponse();
		res.setUserId(vo.getId());
		res.setListenedLessonCount(vo.getListenedLessonCount());
		res.setListenedTimeInSecond(vo.getListenedTimeInSecond());
		res.setPurchasedCourseCount(vo.getPurchasedCourseCount());
		res.setSignedDays(vo.getSignedDays());
		return res;
	}
	
	public List<PurchasedCourseResponse> getPaidCourseList() {
		List<PurchasedCoursePO> poList = userService.getPurchasedCourse();
		List<PurchasedCourseResponse> resList = new ArrayList<>(poList.size());
		for (PurchasedCoursePO po : poList) {
			PurchasedCourseResponse res = new PurchasedCourseResponse();
			res.setId(po.getId());
			res.setHeadImg(po.getSpeakerHeadImg());
			res.setUserName(po.getSpeakerNick());
			res.setUserTitle(po.getSpeakerTitle());
			res.setUserCompany(po.getSpeakerCompany());
			res.setTitle(po.getTitle());
			res.setUpdateDate(DateUtils.getDateString(po.getUpdateDate()));
			res.setNextUpdateDate(DateUtils.getDateString(po.getNextUpdateDate()));
			res.setPublishedLessonCount(po.getPublishedLessonCount());
			res.setLessonCount(po.getLessonNum());
			res.setTradeNo(po.getTradeNo());
			res.setPurchaseDate(DateUtils.getDateString(po.getPurchaseDate()));
			res.setPayment(po.getPayment());
			res.setOriginalCharge(po.getOriginalPrice());
			String status = po.getStatus();
			if (CourseStatus.NEW.toString().equalsIgnoreCase(status))
				res.setStatus((byte) 2);
			else if(CourseStatus.PREDICTION.toString().equalsIgnoreCase(status))
				res.setStatus((byte) 0);
			else res.setStatus((byte) -1);
			res.setBannerImg(Arrays.asList(po.getBannerImg().split(",")));
			if (po.getPublishedLessonCount() == 0) {
				res.setMsg("暂无课时");
			} else {
				LessonSO lso = new LessonSO();
				lso.setCourseId(po.getId());
				lso.setPageSize(null);
				List<LessonPO> lessonList = lessonDao.getList(lso);
				
				List<Integer> lessonIdList = new ArrayList<>(lessonList.size());
				for (LessonPO l : lessonList) {
					lessonIdList.add(l.getId());
				}
				ListenLogSO so = new ListenLogSO();
				so.setLessonIdList(lessonIdList);
				so.setPageSize(null);
				List<ListenLogPO> llList = listenLogDao.getList(so);
				
				ListenLogPO llllog = null;
				Date lastListenTime = new Date(0L);
				
				for (ListenLogPO p : llList) {
					if (p.getUpdateTime().after(lastListenTime)) {
						lastListenTime.setTime(p.getUpdateTime().getTime());
						llllog = p;
					}
				}
				LessonPO lll = null;
				if (llllog != null) {
					for (LessonPO l : lessonList) {
						if (l.getId().equals(llllog.getLessonId())) {
							lll = l;
						}
					}
				}
				Integer notListenedCount = po.getPublishedLessonCount() - llList.size();
				if (notListenedCount > 0) {
					res.setMsg("您有" + notListenedCount + "节未听课时");
				} else {
					if (po.getNextUpdateDate() != null) {
						Integer days = DateUtils.daysBetween(new Date(), po.getNextUpdateDate());
						res.setMsg("将在" + days + "天后发布");
					} else {
						res.setMsg("已听完");
					}
				}
				if (llllog != null && lll != null) {
					if (llllog.getMaxPos() == 0 || llllog.getMaxPos() / lll.getAudioLen() > 0.98) {
					
					} else {
						res.setMsg("已播" + new DecimalFormat("0").format((double) llllog.getMaxPos()/ (double) lll.getAudioLen() * 100) + "% | " + lll.getTitle());
					}
				}
			}
			resList.add(res);
		}
		resList.sort((res1, res2) -> {
			Byte res1Status = res1.getStatus(), res2Status = res2.getStatus();
			Integer publishedCount = res2.getLessonCount() - res2.getPublishedLessonCount() - res1.getLessonCount() + res1.getPublishedLessonCount();
			if (publishedCount == 0) {
				if (res1Status == 0 && res2Status != 0) {
					return -1;
				} else if (res2Status == 0 && res1Status != 0) {
					return 1;
				} else {
					Calendar expectPublishedDate1 = DateUtils.toCalendar(res1.getUpdateDate(), false);
					Calendar expectPublishedDate2 = DateUtils.toCalendar(res2.getUpdateDate(), false);
					return DateUtils.daysDiffer(expectPublishedDate1, expectPublishedDate2);
				}
			}
			return publishedCount;
		});
		return resList;
	}
	
	/**
	 * 前端主页面显示用
	 */
	public CourseListResponse getCourseList(Integer start, Integer limit, String orderBy, String order) {
		CourseSO so = new CourseSO();
		so.setFrom(start);
		so.setLimit(limit);
		so.setOrderBy(orderBy);
		so.setOrder(order);
		so.setPublish(Y.toString());
		List<CoursePO> courseList = courseService.getCourseList(so, false);
		List<CourseResponse> resCourseList = new ArrayList<>(courseList.size());
		for (CoursePO c : courseList) {
			CourseResponse r = new CourseResponse();
			r.setId(c.getId());
			r.setLessonCount(c.getLessonNum());
			r.setTitle(c.getTitle());
			r.setOpenTime(DateUtils.getDateTimeString(c.getOpenTime()));
			r.setIsPublish(Y.toString().equalsIgnoreCase(c.getPublish()) ? (byte) 1 : 0);
			r.setStatus(NEW.equalsIgnoreCase(c.getStatus()) ? (byte) 2 : PREDICTION.equalsIgnoreCase(c.getStatus()) ? (byte) 0 : -1);
			r.setCharge(c.getPrice());
			r.setSubtitle(c.getSubTitle());
			if (c.getTag() != null && c.getTag().trim().length() > 0) {
				String[] tags = c.getTag().split(",");
				List<CourseResponse.Tag> tagList = new ArrayList<>(tags.length);
				for (String t : tags) {
					tagList.add(new CourseResponse.Tag(t));
				}
				r.setTag(tagList);
			}
			if (!StringUtils.isNullEmpty(c.getBannerImg())) {
				r.setBannerImg(Arrays.asList(c.getBannerImg().split(",")));
			}
			SpeakerPO speaker = c.getSpeaker();
			if (speaker != null) {
				r.setUserName(speaker.getNick());
				r.setCompany(speaker.getCompany());
				r.setUserTitle(speaker.getTitle());
				r.setHeadImg(speaker.getHeadImg());
			}
			resCourseList.add(r);
		}
		
		CourseListResponse res = new CourseListResponse();
		res.setHasMore(so.getTotalCount() > start + limit);
		res.setTotal(so.getTotalCount());
		res.setCourseList(resCourseList);
		return res;
	}
	
	public CourseResponse getCourse(Integer courseId) {
		CoursePO course = courseService.getCourse(courseId, true);
		CourseResponse res = new CourseResponse();
		if (course.getSpeaker() != null) {
			res.setHeadImg(course.getSpeaker().getHeadImg());
			res.setUserName(course.getSpeaker().getNick());
			res.setCompany(course.getSpeaker().getCompany());
			res.setUserTitle(course.getSpeaker().getTitle());
			res.setSpeakerDescribe(course.getSpeaker().getDesc());
		}
		res.setBannerImg(Collections.singletonList(course.getBannerImg()));
		res.setId(course.getId());
		res.setLessonCount(course.getLessonNum());
		res.setTitle(course.getTitle());
		res.setOpenTime(DateUtils.getDateTimeString(course.getOpenTime()));
		res.setIsPublish(Y.equalsIgnoreCase(course.getPublish()) ? (byte) 1 : 0);
		res.setStatus(NEW.equalsIgnoreCase(course.getStatus()) ? (byte) 2 : PREDICTION.equalsIgnoreCase(course.getStatus()) ? (byte) 0 : -1);
		res.setCharge(course.getPrice());
		res.setSubtitle(course.getSubTitle());
		res.setPurchased(courseService.coursePurchased(courseId));
		if (course.getCourseContent() != null) {
			CourseContentPO content = course.getCourseContent();
			res.setCrowd(content.getCrowd());
			res.setGain(content.getGain());
			res.setIntroduce(content.getIntroduce());
			res.setSubscribe(content.getSubscribe());
		}
		if (course.getTag() != null && course.getTag().trim().length() > 0) {
			String[] tags = course.getTag().split(",");
			List<CourseResponse.Tag> tagList = new ArrayList<>(tags.length);
			for (String t : tags) {
				tagList.add(new CourseResponse.Tag(t));
			}
			res.setTag(tagList);
		}
		if (StringUtils.isNullEmpty(course.getBannerImg())) {
			res.setBannerImg(Arrays.asList(course.getBannerImg().split(",")));
		}
		LessonSO lso = new LessonSO();
		lso.setCourseId(courseId);
		lso.setType(LessonType.NORMAL.toString());
		lso.setOnSale(Y);
		lso.setPageSize(null);
		lso.setOrderBy("lessonOrder");
		List<LessonPO> lessonList = lessonDao.getList(lso, false);
		List<LessonResponse> lessonResList = new ArrayList<>(lessonList.size());
		res.setLessonList(lessonResList);
		List<Integer> lessonIdList = new ArrayList<>(lessonList.size());
		for (LessonPO lesson : lessonList) {
			lessonIdList.add(lesson.getId());
		}
		ListenLogSO listenSo = new ListenLogSO();
		listenSo.setLessonIdList(lessonIdList);
		listenSo.setPageSize(null);
		List<ListenLogPO> listenList = listenLogDao.getList(listenSo);
		Map<Integer, Integer> lessonLenMap = new HashMap<>();
		for (ListenLogPO l : listenList) {
			lessonLenMap.put(l.getLessonId(), l.getMaxPos());
		}
		for (LessonPO l : lessonList) {
			LessonResponse lr = new LessonResponse();
			lr.setId(l.getId());
			lr.setCourseId(l.getCourseId());
			lr.setCreationTime(DateUtils.getDateTimeString(l.getCreateTime()));
			lr.setExpectPublishDate(DateUtils.getDateString(l.getPublishDate()));
			lr.setPublishTime(DateUtils.getDateString(l.getPublishDate()));
			lr.setLessonNo(l.getNo());
			lr.setLessonOrder(l.getLessonOrder());
			lr.setTitle(l.getTitle());
			lr.setAudioLen(l.getAudioLen());
			lr.setAudio(l.getAudioUrl());
			lr.setResourceSize(NumberUtils.getReadableSize(l.getAudioSize()));
			lr.setCommentCount(l.getCommentCount());
			lr.setFreeListen(Y.equalsIgnoreCase(l.getFree()) ? (byte) 1 : 0);
			lr.setOnSale(Y.equalsIgnoreCase(l.getOnSale()) ? (byte) 1 : 0);
			lr.setIsPublish(l.getPublishDate().before(new Date()) ? (byte) 1 : 0);
			lr.setListenLen(lessonLenMap.get(lr.getId()));
			lessonResList.add(lr);
		}
		return res;
	}
	
	public LessonResponse getLesson(Integer lessonId) {
		LessonPO lesson = lessonDao.getById(lessonId, true);
		if (lesson == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		if (LessonType.NORMAL.toString().equalsIgnoreCase(lesson.getType()) && !Y.equalsIgnoreCase(lesson.getOnSale())) {
			throw new ServiceException(ServiceException.COMMON_DATA_NOT_PUBLISHED);
		}
		if (lesson.getPublishDate().after(new Date())) {
			throw new ServiceException(ServiceException.COMMON_DATA_NOT_PUBLISHED);
		}
		LessonResponse res = new LessonResponse();
		res.setId(lesson.getId());
		res.setCourseId(lesson.getCourseId());
		res.setCreationTime(DateUtils.getDateTimeString(lesson.getCreateTime()));
		res.setExpectPublishDate(DateUtils.getDateString(lesson.getPublishDate()));
		res.setPublishTime(DateUtils.getDateString(lesson.getPublishDate()));
		res.setIsPublish(lesson.getPublishDate().before(new Date()) ? (byte) 1 : 0);
		res.setLessonNo(lesson.getNo());
		res.setLessonOrder(lesson.getLessonOrder());
		res.setTitle(lesson.getTitle());
		res.setAudioLen(lesson.getAudioLen());
		res.setAudio(lesson.getAudioUrl());
		res.setResourceSize(NumberUtils.getReadableSize(lesson.getAudioSize()));
		res.setCommentCount(lesson.getCommentCount());
		res.setFreeListen(Y.equalsIgnoreCase(lesson.getFree()) ? (byte) 1 : 0);
		res.setOnSale(Y.equalsIgnoreCase(lesson.getOnSale()) ? (byte) 1 : 0);
		res.setCoverImg(lesson.getCoverImg());

		List<Integer> lessonIdList;
		if (LessonType.NORMAL.toString().equalsIgnoreCase(lesson.getType())) {
			lessonIdList = lessonDao.getNormalPublishedLessonIdByCourseId(lesson.getCourseId());
		} else {
			lessonIdList = lessonDao.getSelfPublishedLessonIdByCourseId();
		}
		List<Integer> lIdList = new ArrayList<>(lessonIdList.size() + 2);
		lIdList.add(null);
		lIdList.addAll(lessonIdList);
		lIdList.add(null);
		Integer indx = lIdList.indexOf(lessonId);
		res.setPreviousLessonId(lIdList.get(indx - 1));
		res.setNextLessonId(lIdList.get(indx + 1));

		LessonContentPO content = lessonContentDao.getById(lesson.getContentId());
		if (content != null) {
			res.setContent(content.getContent());
		}
		res.setEliteCommentList(getEliteCommentList(lessonId));
		res.setCommentList(getCommentList(lessonId));
		//红包
		RedPacketSO rso = new RedPacketSO();
		rso.setType(RedPacketType.SENDER.toString());
		rso.setLessonId(lessonId);
		RedPacketPO rp = redPacketDao.getOne(rso);
		if (rp == null) {
			rp = new RedPacketPO();
			rp.setLessonId(lessonId);
			rp.setNonce(RandomStringUtils.random(32, true, true));
			rp.setType(RedPacketType.SENDER.toString());
			redPacketDao.create(rp);
			res.setRedPacketNonce(rp.getNonce());
			res.setRedPacketRemained(DEFAULT_RED_PACKET_NUM);
		} else {
			res.setRedPacketNonce(rp.getNonce());
			RedPacketSO uso = new RedPacketSO();
			uso.setLessonId(lessonId);
			uso.setType(RedPacketType.RECEIVER.toString());
			uso.setNonce(rp.getNonce());
			uso.setIgnoreDomainId(true);
			int used = redPacketDao.count(uso);
			res.setRedPacketRemained(DEFAULT_RED_PACKET_NUM - used);
		}
		
		//已听记录
		ListenLogSO lso = new ListenLogSO();
		lso.setLessonId(lessonId);
		ListenLogPO llog = listenLogDao.getOne(lso);
		if (llog != null) {
			res.setListenLen(llog.getRecordPos());
		}
//		//抢过别人的红包，可以免费听
//		RedPacketSO selfso = new RedPacketSO();
//		selfso.setLessonId(lessonId);
		
		return res;
	}
	private CommentListResponse getEliteCommentList(Integer lessonId) {
		CommentSO eliteSO = new CommentSO(true);
		eliteSO.setElite(Y);
		eliteSO.setLessonId(lessonId);
		eliteSO.setIgnoreDomainId(true);
		List<CommentVO> eliteList = commentDao.getCommentList(eliteSO);
		CommentSO zanSO = new CommentSO();
		zanSO.setElite(N);
		zanSO.setLessonId(lessonId);
		zanSO.setIgnoreDomainId(true);
		zanSO.setLimit(3);
		zanSO.setZanCountGreaterThan(5);
		zanSO.setOrderBy("zanCount");
		zanSO.setOrder("desc");
		eliteList.addAll(commentDao.getCommentList(zanSO));
		CommentListResponse res = new CommentListResponse();
		List<Integer> replyIdList = new ArrayList<>();
		if (ArrayUtils.isNullEmpty(eliteList)) return res;
		for (CommentVO c : eliteList) {
			if (c.getReplyToId() != null) {
				replyIdList.add(c.getReplyToId());
			}
		}
		res.setCommentList(mapComment(eliteList));
		if (replyIdList.size() > 0) {
			CommentSO rso = new CommentSO();
			rso.setIdList(replyIdList);
			rso.setIgnoreDomainId(true);
			List<CommentVO> replyCommentList = commentDao.getCommentList(rso);
			res.setReplyToCommentList(mapComment(replyCommentList));
		}
		return res;
	}
	private CommentListResponse getCommentList(Integer lessonId) {
		CommentSO cso = new CommentSO();
		cso.setLessonId(lessonId);
		cso.setOrderBy("createTime");
		cso.setOrder("desc");
		cso.setIgnoreDomainId(true);
		Integer count = commentDao.count(cso);
		List<CommentVO> eliteList = commentDao.getCommentList(cso);
		List<Integer> replyIdList = new ArrayList<>();
		if (eliteList.size() > 0) {
			for (CommentVO c : eliteList) {
				if (c.getReplyToId() != null) {
					replyIdList.add(c.getReplyToId());
				}
			}
		}
		
		CommentListResponse res = new CommentListResponse();
		res.setCount(count);
		res.setCommentList(mapComment(eliteList));
		res.setHasMore(count > cso.getPageSize());
		if (replyIdList.size() > 0) {
			CommentSO rso = new CommentSO();
			rso.setIdList(replyIdList);
			rso.setIgnoreDomainId(true);
			List<CommentVO> replyCommentList = commentDao.getCommentList(rso);
			res.setReplyToCommentList(mapComment(replyCommentList));
		}
		return res;
	}
	
	private List<CommentResponse> mapComment(List<CommentVO> commentList) {
		List<CommentResponse> resList = new ArrayList<>(commentList.size());
		for (CommentVO comment : commentList) {
			CommentResponse res = new CommentResponse();
			res.setId(comment.getId());
			res.setUserName(comment.getUserNick());
			res.setCreationTime(DateUtils.getDateTimeString(comment.getCreateTime()));
			res.setReplytoId(comment.getReplyToId());
			res.setZanCount(comment.getZanCount());
			res.setContent(comment.getContent());
			res.setElite(Y.toString().equalsIgnoreCase(comment.getElite()) ? (byte) 1 : 0);
			res.setIsTheSpeaker(Y.toString().equalsIgnoreCase(comment.getSpeaker()) ? (byte) 1 : 0);
			res.setUserTitle(comment.getUserTitle());
			res.setUserHeadImg(comment.getUserHeadImg());
			res.setUserCompany(comment.getUserCompany());
			res.setLiked(Y.toString().equalsIgnoreCase(comment.getLiked()));
			resList.add(res);
		}
		return resList;
	}
	
	public LessonListResponse getSelfLessonList(Integer start, Integer limit, String order) {
		LessonSO lso = new LessonSO();
		lso.setFrom(start);
		lso.setLimit(limit);
		lso.setOrderBy("publishDate");
		lso.setOrder(order);
		lso.setType(LessonType.SELF.toString());
		lso.setPublishDateTo(new Date());
		Integer count = lessonDao.count(lso);
		List<LessonPO> lessonList = lessonDao.getList(lso);
		
		List<Integer> lessonIdList = new ArrayList<>(lessonList.size());
		for (LessonPO lesson : lessonList) {
			lessonIdList.add(lesson.getId());
		}
		ListenLogSO listenSo = new ListenLogSO();
		listenSo.setLessonIdList(lessonIdList);
		List<ListenLogPO> listenList = listenLogDao.getList(listenSo);
		Map<Integer, Integer> lessonLenMap = new HashMap<>();
		for (ListenLogPO l : listenList) {
			lessonLenMap.put(l.getLessonId(), l.getMaxPos());
		}
		List<LessonResponse> lessonResList = new ArrayList<>(lessonList.size());
		for (LessonPO l : lessonList) {
			LessonResponse lr = new LessonResponse();
			lr.setId(l.getId());
			lr.setCreationTime(DateUtils.getDateTimeString(l.getCreateTime()));
			lr.setExpectPublishDate(DateUtils.getDateString(l.getPublishDate()));
			lr.setPublishTime(DateUtils.getDateString(l.getPublishDate()));
			lr.setLessonNo(l.getNo());
			lr.setLessonOrder(l.getLessonOrder());
			lr.setTitle(l.getTitle());
			lr.setAudioLen(l.getAudioLen());
			lr.setAudio(l.getAudioUrl());
			lr.setResourceSize(NumberUtils.getReadableSize(l.getAudioSize()));
			lr.setCommentCount(l.getCommentCount());
			lr.setCoverImg(l.getCoverImg());
			lr.setListenLen(lessonLenMap.get(lr.getId()));
			lr.setIsPublish((byte) 1);
			lessonResList.add(lr);
		}
		List<Integer> selfLessonIdList = lessonDao.getSelfLessonIdList();
		ListenLogSO ll = new ListenLogSO();
		ll.setLessonIdList(selfLessonIdList);
		Integer listenedCount = listenLogDao.count(ll);
		LessonListResponse res = new LessonListResponse();
		res.setHasMore(count > start + limit);
		res.setTotal(count);
		res.setLessonResponseList(lessonResList);
		res.setUnListenedCount(count - listenedCount);
		return res;
	}
}
