package com.hotelpal.service.basic.mysql;

public class TableNames {
	private TableNames(){}
	private static final String TABLE_PREFIX = "cc_";
	
	public static final String TABLE_ADMIN_USER = TABLE_PREFIX + "admin_user";
	public static final String TABLE_RESOURCE_GROUP = TABLE_PREFIX + "resource_group";
	public static final String TABLE_RESOURCE = TABLE_PREFIX + "resource";
	
	public static final String TABLE_WX_SNS_USER_INFO = TABLE_PREFIX + "wx_sns_user_info";
	public static final String TABLE_WX_USER_INFO = TABLE_PREFIX + "wx_user_info";
	public static final String TABLE_WX_PAY_RESULT = TABLE_PREFIX + "wx_pay_result";

	public static final String TABLE_SYS_PROPERTY = TABLE_PREFIX + "sys_property";
	public static final String TABLE_BANNER = TABLE_PREFIX + "banner";
	public static final String TABLE_USER = TABLE_PREFIX + "user";
	public static final String TABLE_COMMENT = TABLE_PREFIX + "comment";
	public static final String TABLE_LESSON = TABLE_PREFIX + "lesson";
	public static final String TABLE_LESSON_CONTENT = TABLE_PREFIX + "lesson_content";
	public static final String TABLE_LESSON_SELF = TABLE_PREFIX + "lesson_self";
	public static final String TABLE_PHONE_CODE = TABLE_PREFIX + "phone_code";
	public static final String TABLE_SPEAKER = TABLE_PREFIX + "speaker";
	public static final String TABLE_USER_RELA = TABLE_PREFIX + "user_rela";
	public static final String TABLE_ZAN_LOG = TABLE_PREFIX + "zan_log";
	public static final String TABLE_LISTEN_LOG = TABLE_PREFIX + "listen_log";
	public static final String TABLE_PURCHASE_LOG = TABLE_PREFIX + "purchase_log";
	public static final String TABLE_ORDER = TABLE_PREFIX + "order";
	public static final String TABLE_USER_COURSE = TABLE_PREFIX + "user_course";
	public static final String TABLE_COURSE = TABLE_PREFIX + "course";
	public static final String TABLE_RED_PACKET = TABLE_PREFIX + "red_packet";
	public static final String TABLE_WX_PROPERTY = TABLE_PREFIX + "wx_property";
	public static final String TABLE_COURSE_CONTENT = TABLE_PREFIX + "course_content";
	public static final String TABLE_LIVE_COURSE = TABLE_PREFIX + "live_course";
	public static final String TABLE_LIVE_COURSE_CONTENT = TABLE_PREFIX + "live_course_content";
	public static final String TABLE_LIVE_ENROLL = TABLE_PREFIX + "live_enroll";
	public static final String TABLE_LIVE_COURSE_INVITED_LOG = TABLE_PREFIX + "live_course_invited_log";
	public static final String TABLE_LIVE_COUPON = TABLE_PREFIX + "live_coupon";
	public static final String TABLE_LIVE_CHAT_LOG = TABLE_PREFIX + "live_chat_log";
	public static final String TABLE_ASSISTANT_MESSAGE = TABLE_PREFIX + "assistant_message";
	public static final String TABLE_STATISTICS = TABLE_PREFIX + "statistics";
	public static final String TABLE_ONLINE_LOG = TABLE_PREFIX + "online_log";
	public static final String TABLE_ONLINE_SUM = TABLE_PREFIX + "online_sum";
	public static final String TABLE_LIVE_COURSE_IMAGE = TABLE_PREFIX + "live_course_image";
	public static final String TABLE_LIVE_MOCK_USER = TABLE_PREFIX + "live_mock_user";

	public static final String TABLE_REG_INVITE = TABLE_PREFIX + "reg_invite";
	public static final String TABLE_USER_COUPON = TABLE_PREFIX + "user_coupon";
	public static final String TABLE_SYS_COUPON = TABLE_PREFIX + "sys_coupon";
	
}
