package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.CourseContentPO;
import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.po.SpeakerPO;
import com.hotelpal.service.common.po.extra.PurchasedCoursePO;
import com.hotelpal.service.common.so.CourseSO;
import com.hotelpal.service.common.utils.ArrayUtils;
import com.hotelpal.service.common.utils.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class CourseDao extends ExtendedMysqlBaseDao<CourseSO, CoursePO>{
	@Resource
	private CourseContentDao courseContentDao;
	@Resource
	private SpeakerDao speakerDao;
	private static final String TABLE_NAME = TableNames.TABLE_COURSE;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,status,speakerId,lessonNum,title,price,contentId,courseOrder,subTitle,bannerImg,tag," +
			"openTime,publish").split(",")));//Total 16.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<CoursePO> getPOClass() {
		return CoursePO.class;
	}
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
	@Override
	protected List<String> getTableColumnList() {
		return TABLE_COLUMNS_LIST;
	}
	@Override
	protected Map<String, Integer> getTableColumnMap() {
		return TABLE_COLUMN_MAP;
	}
	@Override
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, CourseSO so, String alias) {
	
	}
	
	public List<PurchasedCoursePO> getPaidCourseList() {
		String table_pl = TableNames.TABLE_PURCHASE_LOG;
		String table_course = TableNames.TABLE_COURSE;
		String table_sp = TableNames.TABLE_SPEAKER;
		String table_lesson = TableNames.TABLE_LESSON;
		Integer domainId = SecurityContextHolder.getUserDomainId();
		String sql = " SELECT " +
				" 	course.id, " +
				" 	sp.headImg, " +
				" 	sp.nick, " +
				" 	sp.title speakerTitle, " +
				" 	sp.company speakerCompany, " +
				" 	course.title, " +
				" 	max(if(lesson.publishDate<=DATE(SYSDATE()), lesson.publishDate, null)) updateDate, " +
				" 	min(if(lesson.publishDate>date(sysdate()), lesson.publishDate, null)) nextUpdateDate, " +
				" 	count(distinct IF ( lesson.publishDate <= date( SYSDATE( ) ), lesson.id, NULL ) ) publishedLessonCount, " +
				" 	course.lessonNum, " +
				" 	pl.orderTradeNo, " +
				" 	pl.createTime purchaseDate, " +
				" 	pl.payment, " +
				" 	pl.originalPrice, " +
				" 	course.`status`, " +
				" 	course.bannerImg " +
				" from " + table_pl + " pl " +
				" inner join " + table_course + " course on pl.courseId=course.id " +
				" inner join " + table_sp + " sp on course.speakerId=sp.id " +
				" left join " + table_lesson + " lesson on lesson.courseId=course.id and lesson.deleted=? " +
				"  where pl.domainId=? and course.deleted=? " +
				" group by pl.courseId ";
		return dao.query(sql, new Object[]{BoolStatus.N.toString(), domainId, BoolStatus.N.toString()}, (rs, rowNum) -> {
			PurchasedCoursePO po = new PurchasedCoursePO();
			po.setId(rs.getInt("id"));
			po.setSpeakerHeadImg(rs.getString("headImg"));
			po.setSpeakerNick(rs.getString("nick"));
			po.setSpeakerTitle(rs.getString("speakerTitle"));
			po.setSpeakerCompany(rs.getString("speakerCompany"));
			po.setTitle(rs.getString("title"));
			po.setUpdateDate(DateUtils.toDate(rs.getString("updateDate"), false));
			po.setNextUpdateDate(DateUtils.toDate(rs.getString("nextUpdateDate"), false));
			po.setPublishedLessonCount(rs.getInt("publishedLessonCount"));
			po.setLessonNum(rs.getInt("lessonNum"));
			po.setTradeNo(rs.getString("orderTradeNo"));
			po.setPurchaseDate(DateUtils.toDate(rs.getString("purchaseDate"), false));
			po.setPayment(rs.getInt("payment"));
			po.setOriginalPrice(rs.getInt("originalPrice"));
			po.setStatus(rs.getString("status"));
			po.setBannerImg(rs.getString("bannerImg"));
			return po;
		});
	}
	
	public List<CoursePO> getCourseAndContent(CourseSO so) {
		List<CoursePO> poList = this.getList(so);
		for (CoursePO po : poList) {
			CourseContentPO content = courseContentDao.getById(po.getContentId());
			if (content != null) {
				po.setCourseContent(content);
			}
		}
		return poList;
	}
	
	public CoursePO getById(Integer id, boolean containsContent) {
		CoursePO po = this.getById(id);
		if(containsContent) {
			CourseContentPO p = courseContentDao.getById(po.getContentId());
			if (p != null) {
				po.setCourseContent(p);
			}
		}
		return po;
	}
	
	public List<CoursePO> getIntradayOpenCourse() {
		String sql = "SELECT " + getTableColumnString() + " FROM " + TABLE_NAME + " WHERE deleted='N' AND publish='Y' AND DATE(openTime) = DATE(SYSDATE())";
		List<CoursePO> res = dao.query(sql, new RowMapperImpl(CoursePO.class));
		for (CoursePO course : res) {
			CourseContentPO content = courseContentDao.getById(course.getContentId());
			course.setCourseContent(content);
			SpeakerPO speaker = speakerDao.getById(course.getSpeakerId());
			course.setSpeaker(speaker);
		}
		return res;
	}
	
	public List<String> getTitleListByIdList(List<Integer> idList) {
		if (ArrayUtils.isNullEmpty(idList)) return null;
		StringBuilder sb = new StringBuilder();
		for (Integer id : idList) {
			sb.append(",?");
		}
		String sql = "SELECT title FROM " + TABLE_NAME + " WHERE id IN (" + sb.toString().replaceFirst(",", "") + ")";
		return dao.queryForList(sql,  idList.toArray(), String.class);
	}
}
