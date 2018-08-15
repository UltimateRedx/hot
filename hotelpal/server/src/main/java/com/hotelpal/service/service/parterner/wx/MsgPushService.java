package com.hotelpal.service.service.parterner.wx;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.basic.mysql.dao.live.LiveCourseDao;
import com.hotelpal.service.basic.mysql.dao.live.LiveEnrollDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CouponValidityType;
import com.hotelpal.service.common.enums.LessonType;
import com.hotelpal.service.common.enums.LiveCourseStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.mo.WXMsgPushMO;
import com.hotelpal.service.common.po.*;
import com.hotelpal.service.common.po.extra.UserCouponUserPO;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.so.CourseSO;
import com.hotelpal.service.common.so.LessonSO;
import com.hotelpal.service.common.so.UserCouponSO;
import com.hotelpal.service.common.so.UserRelaSO;
import com.hotelpal.service.common.so.live.LiveCourseSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.HttpPostUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.service.spring.SpringTaskScheduler;
import com.hotelpal.service.web.handler.PropertyHolder;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class MsgPushService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MsgPushService.class);
	private static final String DOMAIN_LINK = PropertyHolder.getProperty("DOMAIN_NAME_HTTP");
	private static final String COURSE_LINK = PropertyHolder.getProperty("content.course.course.link");
	private static final String LESSON_LINK = PropertyHolder.getProperty("content.course.course.lesson.link");
	private static final String SELF_LESSON_LINK = PropertyHolder.getProperty("content.course.course.selfLesson.link");
	private static final String LIVE_COURSE_LINK = PropertyHolder.getProperty("content.course.live.link");
	private static final String CLASS_OPEN_TEMPLATE_ID = "KSPsBFw7R6EBE1zcyHTsXwU34lYXVCF6Ou_bmpue2-A";
	private static final String COMMENT_REPLIED_TEMPLATE_ID = "VreH3M7DQJlbkwDUZNg_bwmRHYxyt5oFBClXh-GkSSE";
	private static final String TEMPLATE_COURSE_OPEN_ID = "wQpXw2Jo9S4wZurpCGdvsJd47rshZF2Bp4VqVLxiCOc";
	private static final String TEMPLATE_STARTING_ID = "2RcZojmR6PP23YyME8kYtDZoFc_hPMFvN3tgcKd8WU4";
	private static final String TEMPLATE_TASK_COMPLETE_ID = "wYZ0KRLij9NsWiZAX693QtBRMyO1vuu11OlAdMntxNM";
	private static final String TEMPLATE_COUPON_EXPIRE_ID = "kDNApnUEqxj6gltkNTybYOsJmevBTNwyF6Q5ez_dXcE";
	private static final String NOTIFICATION_PUSH_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
	
	
	@Resource
	private CourseDao courseDao;
	@Resource
	private CourseContentDao courseContentDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private WXService wxService;
	@Resource
	private LessonDao lessonDao;
	@Resource
	private SpeakerDao speakerDao;
	@Resource
	private CommentDao commentDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private LiveCourseDao liveCourseDao;
	@Resource
	private SpringTaskScheduler springTaskScheduler;
	@Resource
	private LiveEnrollDao liveEnrollDao;
	@Resource
	private SysCouponDao sysCouponDao;
	@Resource
	private UserCouponDao userCouponDao;
	
	
	public void pushCourseOpenMsg() {
		try {
			SecurityContextHolder.loginSuperDomain();
			ExecutorService pool = Executors.newFixedThreadPool(5);
			List<CoursePO> courseList = courseDao.getIntradayOpenCourse();
			for (final CoursePO course : courseList) {
				String courseContent = course.getCourseContent().getIntroduce();
				if (!StringUtils.isNullEmpty(courseContent)) {
					courseContent = courseContent.replaceAll("(?s)<.*?>", "").replaceAll("&.*;", "").replaceAll("\\s", "");
					courseContent = courseContent.length() > 20 ? courseContent.substring(0, 20) + "..." : courseContent;
				}
				final String content = courseContent;
				List<String> openIdList = purchaseLogDao.getSubscriberOpenId(course.getId());
				for (String openId : openIdList) {
					pool.submit(() -> {
						sendClassOpenNotification(openId,
								COURSE_LINK.replaceFirst("@courseId", String.valueOf(course.getId())),
								"您好，您订阅的《" + course.getTitle() + "》今天开讲了。",
								course.getTitle(),
								content,
								course.getSpeaker() == null ? "" : course.getSpeaker().getNick(),
								DateUtils.getDateString(new Date()),
								"点击查看"
						);
					});
				}
			}
			pool.shutdown();
		} catch (Exception e) {
			LOGGER.error("pushCourseOpenMsg exception...", e);
		}
	}
	
	public void pushLessonPublishMsg() {
		try {
			SecurityContextHolder.loginSuperDomain();
			ExecutorService pool = Executors.newFixedThreadPool(5);
			List<LessonPO> lessonList = lessonDao.getIntradayPublishList();
			Set<Integer> courseIdSet = new HashSet<>();
			for (LessonPO lesson : lessonList) {
				courseIdSet.add(lesson.getCourseId());
			}
			Map<Integer, CoursePO> map = new HashMap<>();
			for (Integer courseId : courseIdSet) {
				CoursePO course = courseDao.getById(courseId);
				if (course == null) throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
				SpeakerPO speaker = speakerDao.getById(course.getSpeakerId());
				course.setSpeaker(speaker);
				map.put(courseId, course);
			}
			for (final LessonPO lesson : lessonList) {
				CoursePO course = map.get(lesson.getCourseId());
				String lessonContent = lesson.getContent();
				if (!StringUtils.isNullEmpty(lessonContent)) {
					lessonContent = lessonContent.replaceAll("(?s)<.*?>", "").replaceAll("&.*;", "").replaceAll("\\s", "");
					lessonContent = lessonContent.length() > 20 ? lessonContent.substring(0, 20) + "..." : lessonContent;
				}
				final String content = lessonContent;
				List<String> openIdList = purchaseLogDao.getSubscriberOpenId(lesson.getCourseId());
				for (String openId : openIdList) {
					pool.submit(() -> {
						sendClassOpenNotification(openId,
								LESSON_LINK.replaceFirst("@lessonId", String.valueOf(lesson.getId())).replaceFirst("@courseId", String.valueOf(lesson.getCourseId())),
						"你购买的《" + course.getTitle() + "》更新啦! 《" + lesson.getTitle() + "》今天开讲。",
								lesson.getTitle(),
								content,
								course.getSpeaker() == null ? "" : course.getSpeaker().getNick(),
								DateUtils.getDateString(new Date()),
								"点击查看"
						);
					});
				}
			}
			pool.shutdown();
		}catch (Exception e) {
			LOGGER.error("pushLessonPublishMsg exception...", e);
		}
	}
	
	public void pushCommentRepliedMsg(Integer replyToCommentId, String content) {
		try {
//			SecurityContextHolder.loginSuperDomain();
			CommentPO commentPO = commentDao.getById(replyToCommentId);
			LessonPO lesson = lessonDao.getById(commentPO.getLessonId());
			UserPO replyToUser = userRelaDao.getUserByDomainId(commentPO.getDomainId());
			UserPO user = userRelaDao.getByOpenId(SecurityContextHolder.getUserOpenId());
			sendCommentRepliedNotification(replyToUser.getOpenId(),
					LessonType.NORMAL.toString().equalsIgnoreCase(lesson.getType()) ?
							LESSON_LINK.replaceFirst("@lessonId", String.valueOf(lesson.getId())).replaceFirst("@courseId", String.valueOf(lesson.getCourseId())) :
							SELF_LESSON_LINK.replaceFirst("@lessonId", String.valueOf(lesson.getId())),
					"您好，您的留言被回复了。",
					replyToUser.getNick(),
					commentPO.getContent(),
					user.getNick(),
					content,
					"点击查看课时:" + lesson.getTitle()
			);
		}catch (Exception e) {
			LOGGER.error("pushCommentRepliedMsg exception...", e);
		}
	}

	//已报名的用户，开课前1小时推送通知
	public void pushLiveCourseOpeningMsg(Integer liveCourseId) {
		LiveCoursePO course = liveCourseDao.getById(liveCourseId);
		List<Integer> domainIdList = liveEnrollDao.getEnrolledDomainIdList(liveCourseId);
		UserRelaSO so = new UserRelaSO();
		so.setPageSize(Integer.MAX_VALUE);
		so.setDomainIdList(domainIdList);
		List<UserPO> enrolledUserList = userRelaDao.getPageList(so);
		String courseTitle = course.getTitle();
		for (UserPO user : enrolledUserList) {
			pushLiveCourseOpenNotification(LIVE_COURSE_LINK.replaceFirst("@liveCourseId", String.valueOf(liveCourseId)),
					user.getOpenId(), user.getNick(), courseTitle, DateUtils.getHHMMString(course.getOpenTime()));
		}
	}
	public void pushInviteRemainedMsg(Integer liveCourseId) {
		LiveCoursePO course = liveCourseDao.getById(liveCourseId);
		List<Map<String, Object>> list = liveEnrollDao.getInvitingList(liveCourseId);
		String courseTitle = course.getTitle();
		Calendar cal = Calendar.getInstance();
		cal.setTime(course.getOpenTime());
		String date = cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DATE) + "日 " +
				new DecimalFormat("00").format(cal.get(Calendar.HOUR_OF_DAY)) + DateUtils.TIME_DELIMITER +
				new DecimalFormat("00").format(cal.get(Calendar.MINUTE));
		for (Map<String, Object> map : list) {
			try {
				int require = course.getInviteRequire() - Integer.parseInt(String.valueOf(map.get("count")));
				pushOpeningNotification(String.valueOf(map.get("openId")), LIVE_COURSE_LINK.replaceFirst("@liveCourseId", String.valueOf(liveCourseId)),
						courseTitle, date,
						"目前您已获得" + map.get("count") + "位好友的支持，还需获得" + require + "位好友助力支持，请准时参加哦，不见不散！");
			} catch (Exception e) {
				LOGGER.error("pushLiveCourseOpeningMsg error...", e);
			}
		}
	}
	
	public void pushCouponExpireMsg() {
		SecurityContextHolder.loginSuperDomain();
		LiveCourseSO so = new LiveCourseSO();
		so.setPublish(BoolStatus.Y.toString());
		so.setPageSize(null);
		Calendar cal = Calendar.getInstance();
		DateUtils.clearTime(cal);
		so.setOpenTimeFrom(cal.getTime());
		cal.add(Calendar.DATE, 1);
		so.setOpenTimeTo(cal.getTime());
		List<LiveCoursePO> courseList = liveCourseDao.getList(so);
		for (LiveCoursePO course : courseList) {
			if (course.getSysCouponId() == null) continue;
			SysCouponPO sysCoupon  = sysCouponDao.getById(course.getSysCouponId());
			if (sysCoupon == null || !CouponValidityType.FIXED_DAY.toString().equalsIgnoreCase(sysCoupon.getValidityType())) continue;
			
			UserCouponSO cso = new UserCouponSO();
			cso.setUsed(BoolStatus.N.toString());
			cso.setSysCouponId(sysCoupon.getId());
			cso.setValidityFrom(new Date());
			List<UserCouponUserPO> userCouponList = userCouponDao.getUserCouponAndUser(cso);
			for (UserCouponUserPO userCoupon : userCouponList) {
				try {
					Calendar ex = Calendar.getInstance();
					ex.setTime(userCoupon.getValidity());
					String date = ex.get(Calendar.YEAR) + "年" + (ex.get(Calendar.MONTH) + 1) + "月" + ex.get(Calendar.DATE) + "日 ";
					pushCouponExpireNotification(userCoupon.getUser().getOpenId(), userCoupon.getName(), date);
				}catch (Exception e) {
					LOGGER.error("pushCouponExpireNotification error... ", e);
				}
			}
		}
	}

	/**
	 * 完成邀请之后的推送
	 */
	public void pushInviteCompleteMsg(Integer courseId, String openId, Date openTime) {
		Calendar now = Calendar.getInstance();
		String date = now.get(Calendar.YEAR) + "年" + (now.get(Calendar.MONTH) + 1) + "月" + now.get(Calendar.DATE) + "日";
		Calendar openCal = Calendar.getInstance();
		openCal.setTime(openTime);
		String remarkTime = (openCal.get(Calendar.MONTH) + 1) + "月" + openCal.get(Calendar.DATE) + "日" +
				new DecimalFormat("00").format(openCal.get(Calendar.HOUR_OF_DAY)) + DateUtils.TIME_DELIMITER +
				new DecimalFormat("00").format(openCal.get(Calendar.MINUTE));
		pushTaskCompleteNotification(LIVE_COURSE_LINK.replaceFirst("@liveCourseId", String.valueOf(courseId)), openId, date, remarkTime);
	}

	public void pushRemindLearnMsg() {
		SecurityContextHolder.loginSuperDomain();
		CourseSO so = new CourseSO();
		so.setPublish(BoolStatus.Y.toString());
		List<CoursePO> publishedCourseList = courseDao.getList(so);
		//更新完的课程
		List<CoursePO> courseList = new ArrayList<>();
		for (CoursePO course : publishedCourseList) {
			LessonSO lso = new LessonSO();
			lso.setCourseId(course.getId());
			lso.setOnSale(BoolStatus.Y.toString());
			lso.setPageSize(null);
			Integer count = lessonDao.count(lso);
			if (course.getLessonNum() - count <= 0) {
				courseList.add(course);
			}
		}
		for (CoursePO course : courseList) {
			CourseContentPO content = courseContentDao.getById(course.getContentId());
			String introduce = StringUtils.subString(StringUtils.removeHtml(content.getIntroduce()), 30);

			SpeakerPO speaker = speakerDao.getById(course.getSpeakerId());
			Calendar cal = Calendar.getInstance();
			cal.setTime(course.getOpenTime());
			String openTime =  cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DATE) + "日";
			List<String> openIdList = purchaseLogDao.getPurchasedNormalCourseUserOpenId(course.getId());
			for (String openId : openIdList) {
				try {
					sendClassOpenNotification(openId, COURSE_LINK.replaceFirst("@courseId", String.valueOf(course.getId())),
							"您好，您订阅课程已全部更新完毕", course.getTitle(), introduce,
							speaker.getNick() + speaker.getTitle(), openTime, "记得来听课，不要错过哦！");
				}catch (Exception e) {
					LOGGER.error("pushRemindLearnMsg error...", e);
				}
			}
		}
	}
	
	
	/**<b> 推送课程开课 </b><br/>
	 * {{first.DATA}}
	 * 课程标题：{{keyword1.DATA}}
	 * 课程内容：{{keyword2.DATA}}
	 * 主讲老师：{{keyword3.DATA}}
	 * 时间：{{keyword4.DATA}}
	 * {{remark.DATA}}
	 */
	private void sendClassOpenNotification(String openId, String url, String pre, String courseTitle, String content,
											String speakerNick, String openTime, String remark){
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setUrl(url).setTemplate_id(CLASS_OPEN_TEMPLATE_ID)
				.add("first", pre).add("keyword1", courseTitle).add("keyword2", content).add("keyword3", speakerNick)
				.add("keyword4", openTime).add("remark", remark).build();
		pushNotification(jsonStr);
	}
	
	/**
	 * {{first.DATA}}
	 * 学生姓名：{{keyword1.DATA}}
	 * 留言内容：{{keyword2.DATA}}
	 * 回复内容：{{keyword3.DATA}}
	 * 回复教师：{{keyword4.DATA}}
	 * {{remark.DATA}}
	 */
	private void sendCommentRepliedNotification(String openId, String url, String pre,
										String userNick, String content, String replyContent, String replyUserNick,
										String remark){
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setTemplate_id(COMMENT_REPLIED_TEMPLATE_ID).setUrl(url)
				.add("first", pre)
				.add("keyword1", userNick)
				.add("keyword2", content)
				.add("keyword3", replyContent)
				.add("keyword4", replyUserNick)
				.add("remark", remark).build();
		pushNotification(jsonStr);
	}
	
	/**
	 您好，{{userName.DATA}}。
	 您报名参加的{{courseName.DATA}}将于{{date.DATA}}开课，特此通知。
	 {{remark.DATA}}
	 */
	private void pushLiveCourseOpenNotification(String url, String openId, String userName,
												String courseName, String date){
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setTemplate_id(TEMPLATE_COURSE_OPEN_ID).setUrl(url)
				.add("userName", userName)
				.add("courseName", "本周直播课《" + courseName + "》，")
				.add("date", "今天" + date + "准时").add("remark", "点击详情进入直播间，立即收听").build();
		pushNotification(jsonStr);
	}

	/**
	 * {{first.DATA}}
	 * 主题：{{keyword1.DATA}}
	 * 时间：{{keyword2.DATA}}
	 * {{remark.DATA}}
	 */
	@Deprecated
	private void pushLiveCourseOpen2Notification(String openId, String keyword1, String keyword2, String remark){
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setTemplate_id(TEMPLATE_COURSE_OPEN_ID)
				.add("first", "您好，您报名参加的本周公开课即将开始，特此通知")
				.add("keyword1", "《" + keyword1 + "》")
				.add("keyword2", keyword2).add("remark", remark).build();
		pushNotification(jsonStr);
	}

	/**
	 * {{first.DATA}}
	 * 任务名称：{{keyword1.DATA}}
	 * 任务类型：{{keyword2.DATA}}
	 * 完成时间：{{keyword3.DATA}}
	 * {{remark.DATA}}
	 */
	private void pushTaskCompleteNotification(String url, String openId, String keyword3, String remarkTime){
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setTemplate_id(TEMPLATE_TASK_COMPLETE_ID).setUrl(url)
				.add("first", "恭喜您的任务已完成～")
				.add("keyword1", "公开课报名")
				.add("keyword2", "直播课")
				.add("keyword3", keyword3).add("remark", "本次公开课时间" + remarkTime + "开课，请准时参加，不见不散").build();
		pushNotification(jsonStr);
	}

	/**
	 * {{first.DATA}}
	 * 主题：{{keyword1.DATA}}
	 * 时间：{{keyword2.DATA}}
	 * {{remark.DATA}}
	 */
	private void pushOpeningNotification(String openId, String url, String title, String time, String remark) {
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setTemplate_id(TEMPLATE_STARTING_ID).setUrl(url)
				.add("first", "您好，您报名参加的本周直播课即将开始，特此通知")
				.add("keyword1", "《" + title + "》")
				.add("keyword2", time)
				.add("remark", remark).build();
		pushNotification(jsonStr);
	}

	/**
	 * {{first.DATA}}
	 * 品牌名称：{{keyword1.DATA}}
	 * 服务类型：{{keyword2.DATA}}
	 * 到期时间：{{keyword3.DATA}}
	 * {{remark.DATA}}
	 */
	private void pushCouponExpireNotification(String openId, String name, String time) {
		WXMsgPushMO request = WXMsgPushMO.New();
		String jsonStr = request.setTouser(openId).setTemplate_id(TEMPLATE_COUPON_EXPIRE_ID).setUrl(DOMAIN_LINK)
				.add("first", "您好，您的课程优惠券即将到期，特此通知")
				.add("keyword1", name)
				.add("keyword2", "订阅课程")
				.add("keyword3", time).add("remark", "请不要错过哦，点击详情立即使用哦~").build();
		pushNotification(jsonStr);
	}

	
	private void pushNotification(String body) {
		Integer maxRetryTimes = 3;
		for (int i = 0; i < maxRetryTimes; i++) {
			HttpParams params = new HttpParams();
			params.setUrl(NOTIFICATION_PUSH_URL + wxService.getAccessToken());
			params.setRequestEntity(body);
			String res = HttpPostUtils.postMap(params);
			HashedMap map = JSON.parseObject(res, HashedMap.class);
			if (Integer.parseInt(String.valueOf(map.get("errcode"))) != 0) {
				if (Integer.parseInt(String.valueOf(map.get("errcode"))) == 40001) {
					wxService.renewAccessToken();
					continue;
				}
				LOGGER.error("连接微信失败(推送消息)：" + res);
				throw new ServiceException(ServiceException.WX_COMMUNICATION_FAILED);
			} else {
				return;
			}
		}
		throw new ServiceException("发送消息一直失败。。。。");
	}



	/**
	 * 当web容器启动/课程被更新的时候加载/刷新定时任务
	 */
	public void loadOrUpdateLiveCourseOpeningTrigger(Integer courseId) {
		LiveCoursePO course = liveCourseDao.getById(courseId);
		if (BoolStatus.N.toString().equalsIgnoreCase(course.getDeleted()) && BoolStatus.Y.toString().equalsIgnoreCase(course.getPublish())
				&& LiveCourseStatus.ENROLLING.toString().equalsIgnoreCase(course.getStatus())
				&& course.getOpenTime() != null && course.getOpenTime().after(new Date())) {
			try {
				//已报名的添加开课通知
				String uid = SpringTaskScheduler.SCHEDULER_TYPE_LIVE_COURSE_OPENING + courseId;
				Calendar noticeTime = Calendar.getInstance();
				noticeTime.setTime(course.getOpenTime());
				noticeTime.add(Calendar.HOUR, -1);
				springTaskScheduler.add(uid, new CourseOpeningTrigger(this, courseId), noticeTime.getTime());
			} catch(Exception e){
				LOGGER.error("已报名的添加开课通知 Exception", e);
			}
			
			try {
				//未完成邀请的
				Calendar finalNoticeTime = Calendar.getInstance();
				//当天
				finalNoticeTime.setTime(course.getOpenTime());
				finalNoticeTime.set(Calendar.HOUR_OF_DAY, 12);
				finalNoticeTime.set(Calendar.MINUTE, 0);
				finalNoticeTime.set(Calendar.SECOND, 0);
				if (!new Date().after(finalNoticeTime.getTime())) {
					String uid = SpringTaskScheduler.SCHEDULER_TYPE_LIVE_COURSE_INVITE_INCOMPLETE_PRE + courseId;
					springTaskScheduler.add(uid, new InviteRemainderTrigger(this, courseId), finalNoticeTime.getTime());
				}
				//前一天
				finalNoticeTime.add(Calendar.DATE, -1);
				if (!new Date().after(finalNoticeTime.getTime())){
					String uid = SpringTaskScheduler.SCHEDULER_TYPE_LIVE_COURSE_INVITE_INCOMPLETE + courseId;
					springTaskScheduler.add(uid, new InviteRemainderTrigger(this, courseId), finalNoticeTime.getTime());
				}
			} catch(Exception e){
				LOGGER.error("未完成邀请的 Exception", e);
			}
		}
	}
	
	private static class CourseOpeningTrigger extends Thread{
		private MsgPushService service;
		private Integer courseId;
		CourseOpeningTrigger(MsgPushService service, Integer courseId) {
			this.service = service;
			this.courseId = courseId;
		}
		
		@Override
		public void run() {
			service.pushLiveCourseOpeningMsg(this.courseId);
		}
	}
	
	private static class InviteRemainderTrigger extends Thread{
		private MsgPushService service;
		private Integer courseId;
		InviteRemainderTrigger(MsgPushService service, Integer courseId) {
			this.service = service;
			this.courseId = courseId;
		}
		
		@Override
		public void run() {
			LOGGER.info("pushInviteRemainedMsg task running...");
			service.pushInviteRemainedMsg(this.courseId);
		}
	}
}
