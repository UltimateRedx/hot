package com.hotelpal.service.basic.mysql.dao;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.CommentPO;
import com.hotelpal.service.common.so.CommentSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.CommentVO;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class CommentDao extends ExtendedMysqlBaseDao<CommentSO, CommentPO> {
	private static final String TABLE_NAME = TableNames.TABLE_COMMENT;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,domainId,lessonId,replyToId,zanCount,content,elite,speaker").split(",")));//Total 11.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<CommentPO> getPOClass() {
		return CommentPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, CommentSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getZanCountGreaterThan() != null) {
			buff.append(" AND ").append(alias).append("`zanCount` > ?");
			params.add(so.getZanCountGreaterThan());
		}
	}
	
	public List<CommentVO> getByIdListVO(Integer lessonId, List<Integer> idList) {
		CommentSO so = new CommentSO();
		so.setIdList(idList);
		so.setLessonId(lessonId);
		so.setIgnoreDomainId(true);
		return getCommentList(so);
	}
	public List<CommentVO> getCommentList(CommentSO so) {
		String userTable = TableNames.TABLE_USER;
		String userRelaTable = TableNames.TABLE_USER_RELA;
		String zanLogTable = TableNames.TABLE_ZAN_LOG;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		params.add(SecurityContextHolder.getUserDomainId());
		this.searchSO(buff, params, so, "com");
		StringBuilder suffix = new StringBuilder();
		List<Object> suffixParams = new ArrayList<>();
		this.searchSuffix(suffix, suffixParams, so, "com");
		String sql = "select" +
				" com.id," +
				" com.createTime," +
				" com.updateTime," +
				" com.deleted," +
				" com.lessonId," +
				" com.replyToId," +
				" com.zanCount," +
				" com.content," +
				" com.elite," +
				" if(com.speaker='N', u.nick, sp.nick) nick, " +
				" if(com.speaker='N', u.title, sp.title) title, " +
				" if(com.speaker='N', u.headImg, sp.headImg) headImg," +
				" if(com.speaker='N', u.company, sp.company)company, " +
				" count(zl.id)>0 liked," +
				" com.speaker " +
				" from `" + TABLE_NAME + "` com" +
				" left join `" + userRelaTable + "` rela on com.domainId = rela.domainId" +
				" left join `" + userTable + "` u on rela.userId=u.id " +
				" left join `cc_speaker` sp on com.domainId=sp.id " +
				" left join `" + zanLogTable + "` zl on zl.domainId = ? and zl.commentId=com.id " +
				" where 1=1 " + buff.toString() +
				" group by com.id " + suffix.toString();
		return dao.query(sql, ArrayUtils.addAll(params.toArray(), suffixParams.toArray()), (rs, i) -> {
			CommentVO vo = new CommentVO();
			int index = 1;
			vo.setId(rs.getInt(index++));
			vo.setCreateTime(DateUtils.toCalendar(rs.getString(index++), true).getTime());
			vo.setUpdateTime(DateUtils.toCalendar(rs.getString(index++), true).getTime());
			vo.setDeleted(rs.getString(index++));
			vo.setLessonId(rs.getInt(index++));
			vo.setReplyToId(rs.getInt(index++));
			vo.setZanCount(rs.getInt(index++));
			vo.setContent(rs.getString(index++));
			vo.setElite(rs.getString(index++));
			vo.setUserNick(rs.getString(index++));
			vo.setUserTitle(rs.getString(index++));
			vo.setUserHeadImg(rs.getString(index++));
			vo.setUserCompany(rs.getString(index++));
			vo.setLiked(rs.getBoolean(index++) ? BoolStatus.Y.toString() : BoolStatus.N.toString());
			vo.setSpeaker(rs.getString(index++));
			return vo;
		});
	}
	
	@Override
	public List<CommentPO> getList(CommentSO so) {
		String TABLE_RELA = TableNames.TABLE_USER_RELA;
		String TABLE_USER = TableNames.TABLE_USER;
		String TABLE_LESSON = TableNames.TABLE_LESSON;
		String TABLE_COURSE = TableNames.TABLE_COURSE;
		String TABLE_SPEAKER = TableNames.TABLE_SPEAKER;
		String pre = " select "  +
					" 	c.createTime,"  +
					" 	if(c.speaker='Y', sp2.nick, u.nick), "  +
					" 	c.content,"  +
					" 	c.speaker," +
					"   c.elite, " +
					"   c.deleted, " +
					"   c.id "  +
					" from " + TABLE_NAME + " c"  +
					" left join " + TABLE_RELA + " rela on c.domainId = rela.domainId"  +
					" left join " + TABLE_USER + " u  on rela.userId  = u.id"  +
					" inner join " + TABLE_LESSON + " l on c.lessonId = l.id"  +
					" left join " + TABLE_COURSE + " course on l.courseId = course.id"  +
					" left join " + TABLE_SPEAKER + " sp on rela.phone = sp.phone and course.speakerId = sp.id " +
					" left join " + TABLE_SPEAKER + " sp2 on c.domainId=sp2.id " +
					" where 1=1 " ;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		this.searchSO(buff, params, so, "c");
		if (StringUtils.isNullEmpty(so.getOrderBy())) {
			so.setOrderBy("id");
			so.setOrder("desc");
		}
		this.searchSuffix(buff, params, so, "c");
		String sql = pre + buff.toString();
		List<CommentPO> poList = dao.query(sql, params.toArray(), (rs, i) -> {
			CommentPO po = new CommentPO();
			int index = 1;
			po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
			po.setNick(rs.getString(index++));
			po.setContent(rs.getString(index++));
			po.setSpeaker(rs.getString(index++));
			po.setElite(rs.getString(index++));
			po.setDeleted(rs.getString(index++));
			po.setId(rs.getInt(index++));
			return po;
		});
		return poList;
	}
	
	private List<CommentVO> getEliteCommentList(Integer lessonId) {
		String sql = "select " + getTableColumnString() + " from " + TABLE_NAME + " where elite = ? and lessonId=? " +
				" union all " +
				" select " + getTableColumnString() + " from " + TABLE_NAME + " where elite=? and lessonId=? and zanCount>? order by zanCount desc limit ?";
		return dao.query(sql, new Object[]{BoolStatus.Y.toString(), lessonId, BoolStatus.N.toString(), lessonId, 5, 3}, (rs, index) -> {
			CommentVO vo = new CommentVO();
			
			return vo;
		});
	}
}
