package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LessonType;
import com.hotelpal.service.common.po.LessonContentPO;
import com.hotelpal.service.common.po.LessonPO;
import com.hotelpal.service.common.so.LessonSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class LessonDao extends ExtendedMysqlBaseDao<LessonSO, LessonPO> {
	@Resource
	private CourseDao courseDao;
	@Resource
	private LessonContentDao lessonContentDao;
	private static final String TABLE_NAME = TableNames.TABLE_LESSON;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(
			Arrays.asList(("id,createTime,updateTime,deleted,courseId,publishDate,free,onSale,lessonOrder,no,title," +
					"audioUrl,audioLen,audioSize,commentCount,contentId,type,coverImg").split(",")));//Total 18.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<LessonPO> getPOClass() {
		return LessonPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, LessonSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getPublishDateFrom() != null) {
			buff.append(" AND ").append(alias).append("`publishDate` >= ?");
			Date date = new Date(so.getPublishDateFrom().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
		if (so.getPublishDateTo() != null) {
			buff.append(" AND ").append(alias).append("`publishDate` < ?");
			Date date = new Date(so.getPublishDateTo().getTime());
			params.add(DateUtils.getDateString(DateUtils.increaseAndGet(date)));
		}
	}
	
	public LessonPO getById(Integer id, boolean containsContent) {
		LessonPO po = this.getById(id);
		if(containsContent) {
			LessonContentPO p = lessonContentDao.getById(po.getContentId());
			if (p != null) {
				po.setContent(p.getContent());
			}
		}
		return po;
	}
	
	public List<LessonPO> getList(LessonSO so, boolean containsContent) {
		List<LessonPO> poList = this.getList(so);
		if(containsContent) {
			for (LessonPO p : poList) {
				LessonContentPO c = lessonContentDao.getById(p.getContentId());
				if (c != null) {
					p.setContent(c.getContent());
				}
			}
		}
		return poList;
	}
	
	public List<Integer> getSelfLessonIdList() {
		String sql = "SELECT id FROM " + TABLE_NAME +
				" where type=? and publishDate<=sysdate() and deleted=?";
		return dao.queryForList(sql, new Object[]{LessonType.SELF.toString(), BoolStatus.N.toString()}, Integer.class);
	}
	
	public List<LessonPO> getIntradayPublishList() {
		String sql = "SELECT " + this.getColumnAlias("lesson") +
				" FROM " + TABLE_NAME + " lesson " +
				" INNER JOIN " + courseDao.getTableName() + " c on lesson.courseId=c.id " +
				" WHERE c.deleted='N' AND lesson.deleted='N' AND lesson.onSale='Y' AND type=? " +
				" AND DATE(lesson.publishDate)=DATE(SYSDATE()) ";
		List<LessonPO> res = dao.query(sql, new Object[]{LessonType.NORMAL.toString()}, new RowMapperImpl<>(LessonPO.class));
		for (LessonPO lesson : res) {
			LessonContentPO content = lessonContentDao.getById(lesson.getContentId());
			if (content != null) {
				lesson.setContent(content.getContent());
			}
		}
		return res;
	}
	public List<Integer> getNormalPublishedLessonIdByCourseId(Integer courseId) {
		String sql = "select id from " + TABLE_NAME + " where courseId=? and onSale=? and publishDate<=sysDate() " +
				" and deleted=? and type=? " +
				" order by lessonOrder ";
		return dao.queryForList(sql, new Object[]{courseId, BoolStatus.Y.toString(), BoolStatus.N.toString(), LessonType.NORMAL.toString()}, Integer.class);
	}
	public List<Integer> getSelfPublishedLessonIdByCourseId() {
		String sql = "select id from " + TABLE_NAME + " where publishDate<=sysDate() " +
				" and deleted=? and type=? " +
				" order by `no` ";
		return dao.queryForList(sql, new Object[]{BoolStatus.N.toString(), LessonType.SELF.toString()}, Integer.class);
	}
}
