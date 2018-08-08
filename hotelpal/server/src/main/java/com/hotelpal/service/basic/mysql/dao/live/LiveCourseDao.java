package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.ExtendedMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.basic.mysql.dao.CourseContentDao;
import com.hotelpal.service.basic.mysql.dao.CourseDao;
import com.hotelpal.service.basic.mysql.dao.SpeakerDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.CourseContentPO;
import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.po.SpeakerPO;
import com.hotelpal.service.common.po.live.LiveCourseContentPO;
import com.hotelpal.service.common.po.live.LiveCoursePO;
import com.hotelpal.service.common.so.live.LiveCourseSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LiveCourseDao extends ExtendedMysqlBaseDao<LiveCourseSO, LiveCoursePO>{
	@Resource
	private LiveCourseContentDao liveCourseContentDao;
	@Resource
	private CourseDao courseDao;
	@Resource
	private CourseContentDao courseContentDao;
	@Resource
	private SpeakerDao speakerDao;
	
	
	private static final String TABLE_NAME = TableNames.TABLE_LIVE_COURSE;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"deleted,title,subTitle,openTime,price,inviteRequire,bannerImg,contentId,status,inviteImg," +
			"speakerNick,speakerTitle,publish,relaCourseId,sysCouponId,totalPeople,couponShow,purchasedTimes,freeEnrolledTimes," +
			"liveAudio,liveImg,vipEnrolledTimes,currentImg,relaCourseCouponImg").split(",")));
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<LiveCoursePO> getPOClass() {
		return LiveCoursePO.class;
	}
	@Override
	public String getTableName() {
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, LiveCourseSO so, String baseAlias) {
		String alias = !StringUtils.isNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		if (so.getOpenTimeFrom() != null) {
			buff.append(" AND ").append(alias).append("`openTime` >= ?");
			params.add(so.getOpenTimeFrom());
		}
		if (so.getOpenTimeTo() != null) {
			buff.append(" AND ").append(alias).append("`openTime` < ?");
			params.add(so.getOpenTimeTo());
		}
	}
	@Override
	public List<LiveCoursePO> getList(LiveCourseSO so) {
		String COURSE = TableNames.TABLE_LIVE_COURSE;
		String CONTENT = TableNames.TABLE_LIVE_COURSE_CONTENT;
		StringBuilder buff = new StringBuilder();
		List<Object> params = new ArrayList<>();
		this.searchSO(buff, params, so, "course");
		this.searchSuffix(buff, params, so, "course");
		String sql = "select " + getColumnAlias("course") +
				" ,content.introduce, content.instruction " +
				" from " + COURSE + " course " +
				" inner join " + CONTENT + " content on course.contentId=content.id " +
				" where 1=1 " + buff.toString();
		return dao.query(sql, params.toArray(), new RowMapper<LiveCoursePO>() {
			@Override
			public LiveCoursePO mapRow(ResultSet rs, int i) throws SQLException {
				LiveCoursePO po = new LiveCoursePO();
				int index = 1;
				po.setId(rs.getInt(index++));
				po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
				po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
				po.setDeleted(rs.getString(index++));
				po.setTitle(rs.getString(index++));
				po.setSubTitle(rs.getString(index++));
				po.setOpenTime(DateUtils.toDate(rs.getString(index++), true));
				po.setPrice(rs.getInt(index++));
				po.setInviteRequire(rs.getInt(index++));
				po.setBannerImg(rs.getString(index++));
				po.setContentId(rs.getInt(index++));
				po.setStatus(rs.getString(index++));
				po.setInviteImg(rs.getString(index++));
				po.setSpeakerNick(rs.getString(index++));
				po.setSpeakerTitle(rs.getString(index++));
				po.setPublish(rs.getString(index++));
				po.setRelaCourseId(rs.getInt(index++));
				po.setSysCouponId(rs.getInt(index++));
				po.setTotalPeople(rs.getInt(index++));
				po.setCouponShow(rs.getString(index++));
				po.setPurchasedTimes(rs.getInt(index++));
				po.setFreeEnrolledTimes(rs.getInt(index++));
				po.setLiveAudio(rs.getString(index++));
				po.setLiveImg(rs.getString(index++));
				po.setVipEnrolledTimes(rs.getInt(index++));
				po.setCurrentImg(rs.getString(index++));
				po.setRelaCourseCouponImg(rs.getString(index++));
				
				po.setIntroduce(rs.getString(index++));
				po.setInstruction(rs.getString(index++));
				return po;
			}
		});
	}
	
	
	public LiveCoursePO getById(Integer id, boolean containsRelaCourse) {
		if (id == null) return null;
		String sql = getGetByIdSQL();
		LiveCoursePO po;
		List<LiveCoursePO> list =  dao.query(sql, new Object[]{id}, new RowMapperImpl(LiveCoursePO.class));
		if (list != null && list.size() > 0) {
			po = list.get(0);
		} else {
			return null;
		}
		LiveCourseContentPO content = liveCourseContentDao.getById(po.getContentId());
		if (content != null) {
			po.setIntroduce(content.getIntroduce());
		}
		if (containsRelaCourse) {
			Integer relaCourseId = po.getRelaCourseId();
			if (relaCourseId != null) {
				CoursePO relaCourse = courseDao.getById(relaCourseId, false);
				po.setRelaCourse(relaCourse);
				if (relaCourse != null) {
					SpeakerPO speaker = speakerDao.getById(relaCourse.getSpeakerId());
					relaCourse.setSpeaker(speaker);
					CourseContentPO courseContent = courseContentDao.getById(relaCourse.getContentId());
					relaCourse.setCourseContent(courseContent);
				}
			}
		}
		return po;
	}
	
	public List<LiveCoursePO> getOpeningCourse() {
		String sql = "SELECT " + this.getTableColumnString() + " FROM " + TABLE_NAME +
				" WHERE deleted=? AND openTime<=DATE_ADD(SYSDATE(),INTERVAL 1 HOUR) ";
		return dao.query(sql, new Object[]{BoolStatus.N.toString()}, new RowMapperImpl(LiveCoursePO.class));
	}

	public List<Integer> getTableIds() {
		String sql = "SELECT id FROM " + TABLE_NAME;
		return dao.queryForList(sql, Integer.class);
	}
}
