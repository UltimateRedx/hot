package com.hotelpal.service.common.exception;

import java.util.HashMap;
import java.util.Map;

public final class ServiceException extends RuntimeException {
	private String msgType;
	public static final String PROGRAM_LOGICAL_ERROR = "PROGRAM_LOGICAL_ERROR";
	public static final String DAO_REDIS_POOL = "DAO_REDIS_POOL";
	public static final String DAO_REDIS_CANNOT_INCR = "DAO_REDIS_CANNOT_INCR";
	public static final String DAO_COMMON_DATA_NOT_INTEGRITY = "DAO_COMMON_DATA_NOT_INTEGRITY";
	public static final String DAO_COMMON_KEY_NOT_EXISTS = "DAO_COMMON_KEY_NOT_EXISTS";
	public static final String DAO_DATA_NOT_FOUND = "DAO_DATA_NOT_FOUND";
	public static final String DAO_OPENID_NOT_FOUND = "DAO_OPENID_NOT_FOUND";
	public static final String DAO_USER_RELA_FAILED = "DAO_USER_RELA_FAILED";
	
	public static final String COMMON_DATA_PARSE_ERROR = "COMMON_DATA_PARSE_ERROR";
	public static final String COMMON_REQUEST_DATA_INVALID = "COMMON_REQUEST_DATA_INVALID";
	public static final String COMMON_TOKEN_INVALID = "COMMON_TOKEN_INVALID";
	public static final String COMMON_ILLEGAL_ACCESS  = "COMMON_ILLEGAL_ACCESS ";
	public static final String COMMON_DATA_NOT_PUBLISHED = "COMMON_DATA_NOT_PUBLISHED";
	public static final String COMMON_EMPTY_INPUT_PARAMETER = "COMMON_EMPTY_INPUT_PARAMETER";
	public static final String COMMON_USER_NOT_SUBSCRIBE = "COMMON_USER_NOT_SUBSCRIBE";

	public static final String ADMIN_USER_AUTH_FAILED = "ADMIN_USER_AUTH_FAILED";
	public static final String ADMIN_USER_AUTH_FAILED2 = "ADMIN_USER_AUTH_FAILED2";
	public static final String ADMIN_NO_SESSION_EXISTS = "ADMIN_NO_SESSION_EXISTS";
	public static final String ADMIN_USER_NO_INFO = "ADMIN_USER_NO_INFO";

	public static final String USER_DUPLICATE_PHONE_LOGIN = "USER_DUPLICATE_PHONE_LOGIN";
	public static final String USER_PHONE_INVALID = "USER_PHONE_INVALID";
	public static final String USER_PHONE_NOT_EXISTS = "USER_PHONE_NOT_EXISTS";
	public static final String USER_LOGIN_CODE_REQUIRED = "USER_LOGIN_CODE_REQUIRED";
	public static final String USER_LOGIN_CODE_NOT_EXIST = "USER_LOGIN_CODE_NOT_EXIST";
	public static final String USER_LOGIN_CODE_INVALID = "USER_LOGIN_CODE_INVALID";

	public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
	public static final String ORDER_COURSE_ALREADY_GOT = "ORDER_COURSE_ALREADY_GOT";
	public static final String ORDER_FREE_COURSE_NONE = "ORDER_FREE_COURSE_NONE";

	public static final String HTTP_BLANK_RESPONSE = "HTTP_BLANK_RESPONSE";

	public static final String DATA_NO_QUALIFICATION = "DATA_NO_QUALIFICATION";
	public static final String DATA_QUALIFICATION_TAKEN = "DATA_QUALIFICATION_TAKEN";
	public static final String DATA_LINK_INVALID = "DATA_LINK_INVALID";

	public static final String RED_PACKET_OPENED = "RED_PACKET_OPENED";
	public static final String RED_PACKET_NONE = "RED_PACKET_NONE";
	
	public static final String CODE_REQUIRED_TOO_FREQUENCY = "CODE_REQUIRED_TOO_FREQUENCY";
	
	public static final String SUB_MAIL_SEND_FAILED = "SUB_MAIL_SEND_FAILED";
	
	public static final String WX_COMMUNICATION_FAILED = "WX_COMMUNICATION_FAILED";
	
	public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
	public static final String FILE_SERVER_FAILED = "FILE_SERVER_FAILED";
	
	public static final String LIVE_COURSE_ENROLL_FAILED = "LIVE_COURSE_ENROLL_FAILED";
	public static final String LIVE_COURSE_ALREADY_ENROLLED = "LIVE_COURSE_ALREADY_ENROLLED";
	public static final String LIVE_COURSE_NO_COUPON = "LIVE_COURSE_NO_COUPON";
	public static final String LIVE_COUPON_ALREADY_GOT = "LIVE_COUPON_ALREADY_GOT";
	public static final String LIVE_COURSE_ENROLLED_FOR = "LIVE_COURSE_ENROLLED_FOR";
	public static final String LIVE_COURSE_SELF_INVITE = "LIVE_COURSE_SELF_INVITE";
	public static final String LIVE_COURSE_INVITING_ENROLLED = "LIVE_COURSE_INVITING_ENROLLED";
	public static final String LIVE_NOT_ONGOING = "LIVE_NOT_ONGOING";
	public static final String LIVE_NOT_PUBLISHED = "LIVE_NOT_PUBLISHED";

	public static final String COUPON_NOT_APPLICABLE = "COUPON_NOT_APPLICABLE";
	public static final String COUPON_USED = "COUPON_USED";
	public static final String COUPON_EXPIRED = "COUPON_EXPIRED";
	public static final String COUPON_OBTAINED = "COUPON_OBTAINED";
	public static final String COUPON_BATCH_NOT_EXISTS = "COUPON_BATCH_NOT_EXISTS";
	public static final String COUPON_REG_INVITE_INCOMPLETE = "COUPON_REG_INVITE_INCOMPLETE";
	public static final String COUPON_DEPLETION = "COUPON_DEPLETION";
	
	public ServiceException(){}
	public ServiceException(String type){
		super(type);
		this.msgType = type;
	}
	public ServiceException(String type, Throwable e) {
		super(type, e);
		this.msgType = type;
	}
	public ServiceException(Throwable t) {
		super(t);
	}
	
	private static final Map<String, ExceptionCode> codeMap = new HashMap<>();
	private static class ExceptionCode {
		private Integer code;
		private String msg;
		ExceptionCode(Integer code, String msg) {
			this.code = code;
			this.msg = msg;
		}
		public Integer getCode() {
			return code;
		}
		public void setCode(Integer code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
	public static Integer getExceptionCode(String type) {
		return codeMap.containsKey(type) ? codeMap.get(type).code : -1;
	}
	public String getType() {
		return this.msgType;
	}
	@Override
	public String getMessage() {
		return codeMap.containsKey(this.msgType) ? codeMap.get(msgType).getMsg() : msgType;
	}
	
	static {
		codeMap.put(PROGRAM_LOGICAL_ERROR, new ExceptionCode(-1, "内部错误"));
		codeMap.put(DAO_COMMON_DATA_NOT_INTEGRITY, new ExceptionCode(100, "数据不完整"));
		codeMap.put(DAO_DATA_NOT_FOUND, new ExceptionCode(102, "数据不存在"));
		codeMap.put(DAO_OPENID_NOT_FOUND, new ExceptionCode(103, "TOKEN不存在"));
		codeMap.put(DAO_USER_RELA_FAILED, new ExceptionCode(104, "用户信息关联失败"));
		
		codeMap.put(COMMON_DATA_PARSE_ERROR, new ExceptionCode(1, "数据库类型转换失败"));
		codeMap.put(COMMON_REQUEST_DATA_INVALID, new ExceptionCode(2, "请求参数错误"));
		codeMap.put(COMMON_TOKEN_INVALID, new ExceptionCode(3, "token验证失败"));
		codeMap.put(COMMON_ILLEGAL_ACCESS , new ExceptionCode(4, "无权访问该资源"));
		codeMap.put(COMMON_DATA_NOT_PUBLISHED, new ExceptionCode(5, "数据暂时不可用"));
		codeMap.put(COMMON_EMPTY_INPUT_PARAMETER, new ExceptionCode(6, "参数为空"));
		codeMap.put(COMMON_USER_NOT_SUBSCRIBE, new ExceptionCode(7, "没有关注公众号"));
		
		codeMap.put(ADMIN_USER_AUTH_FAILED, new ExceptionCode(400, "用户名/密码错误"));
		codeMap.put(ADMIN_USER_AUTH_FAILED2, new ExceptionCode(400, "用户名/密码错误..."));
		codeMap.put(ADMIN_NO_SESSION_EXISTS, new ExceptionCode(401, "会话访问过期"));
		codeMap.put(ADMIN_USER_NO_INFO, new ExceptionCode(401, "没有管理用户信息"));
		
		codeMap.put(USER_DUPLICATE_PHONE_LOGIN, new ExceptionCode(200, "用户重复手机登录"));
		codeMap.put(USER_PHONE_INVALID, new ExceptionCode(201, "手机号格式错误/未能识别"));
		codeMap.put(USER_PHONE_NOT_EXISTS, new ExceptionCode(202, "手机号码不存在"));
		codeMap.put(USER_LOGIN_CODE_REQUIRED, new ExceptionCode(203, "缺少校验码"));
		codeMap.put(USER_LOGIN_CODE_NOT_EXIST, new ExceptionCode(204, "未找到该号码的验证码发送记录"));
		codeMap.put(USER_LOGIN_CODE_INVALID, new ExceptionCode(205, "验证码不正确"));
		
		codeMap.put(ORDER_NOT_FOUND, new ExceptionCode(300, "未找到订单号"));
		codeMap.put(ORDER_COURSE_ALREADY_GOT, new ExceptionCode(301, "已获取课程"));
		codeMap.put(ORDER_FREE_COURSE_NONE, new ExceptionCode(302, "没有可用的免费课程"));
		codeMap.put(HTTP_BLANK_RESPONSE, new ExceptionCode(303, "空的HTTP返回值"));
		codeMap.put(DATA_NO_QUALIFICATION, new ExceptionCode(304, "未获得邀请资格"));
		codeMap.put(DATA_QUALIFICATION_TAKEN, new ExceptionCode(305, "已获得邀请资格"));
		codeMap.put(DATA_LINK_INVALID, new ExceptionCode(306, "链接已失效"));
		
		codeMap.put(RED_PACKET_OPENED, new ExceptionCode(307, "已经获得该红包"));
		codeMap.put(RED_PACKET_NONE, new ExceptionCode(308, "已没有可用红包"));
		
		codeMap.put(CODE_REQUIRED_TOO_FREQUENCY, new ExceptionCode(309, "发送验证码过于频繁"));
		
		codeMap.put(SUB_MAIL_SEND_FAILED, new ExceptionCode(310, "短信息发送失败"));
		
		codeMap.put(WX_COMMUNICATION_FAILED, new ExceptionCode(311, "连接微信失败"));
		
		codeMap.put(FILE_TOO_LARGE, new ExceptionCode(312, "上传文件太大"));
		codeMap.put(FILE_SERVER_FAILED, new ExceptionCode(313, "文件服务器错误"));
		
		codeMap.put(LIVE_COURSE_ENROLL_FAILED, new ExceptionCode(314, "报名失败"));
		codeMap.put(LIVE_COURSE_ALREADY_ENROLLED, new ExceptionCode(315, "已经报名"));
		codeMap.put(LIVE_COURSE_NO_COUPON, new ExceptionCode(316, "课程没有优惠可用"));
		codeMap.put(LIVE_COUPON_ALREADY_GOT, new ExceptionCode(317, "已经获得该优惠券"));
		codeMap.put(LIVE_COURSE_ENROLLED_FOR, new ExceptionCode(318, "已经为他人报名"));
		codeMap.put(LIVE_COURSE_SELF_INVITE, new ExceptionCode(319, "不能邀请自己"));
		codeMap.put(LIVE_COURSE_INVITING_ENROLLED, new ExceptionCode(320, "邀请进行中或已报名"));
		codeMap.put(LIVE_NOT_ONGOING, new ExceptionCode(321, "课程尚未开始直播"));
		codeMap.put(LIVE_NOT_PUBLISHED, new ExceptionCode(322, "直播服务不可用"));
		
		codeMap.put(COUPON_NOT_APPLICABLE, new ExceptionCode(323, "优惠券不适用"));
		codeMap.put(COUPON_USED, new ExceptionCode(324, "该优惠券已被使用."));
		codeMap.put(COUPON_EXPIRED, new ExceptionCode(325, "优惠券已过期"));
		codeMap.put(COUPON_OBTAINED, new ExceptionCode(326, "已经获得该优惠券"));
		codeMap.put(COUPON_BATCH_NOT_EXISTS, new ExceptionCode(327, "优惠券不存在"));
		codeMap.put(COUPON_REG_INVITE_INCOMPLETE, new ExceptionCode(328, "邀请尚未完成"));
		codeMap.put(COUPON_DEPLETION, new ExceptionCode(329, "优惠券已被抢完"));
		
		
	}
}
