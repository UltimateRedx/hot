package com.hotelpal.service.basic.mysql.dao.live;

import com.hotelpal.service.basic.mysql.DomainMysqlBaseDao;
import com.hotelpal.service.basic.mysql.TableNames;
import com.hotelpal.service.basic.mysql.dao.UserDao;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.live.ChatLogPO;
import com.hotelpal.service.common.so.live.ChatLogSO;
import com.hotelpal.service.common.utils.DateUtils;
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
public class ChatLogDao extends DomainMysqlBaseDao<ChatLogSO, ChatLogPO>{
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private UserDao userDao;
	private static final String TABLE_NAME = TableNames.TABLE_LIVE_CHAT_LOG;
	private static final List<String> TABLE_COLUMNS_LIST = new ArrayList<>(Arrays.asList(("id,createTime,updateTime," +
			"domainId,liveCourseId,msg,blocked").split(",")));//Total 7.
	private static final Map<String, Integer> TABLE_COLUMN_MAP = tableListToMap(TABLE_COLUMNS_LIST);
	@Override
	protected Class<ChatLogPO> getPOClass() {
		return ChatLogPO.class;
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
	protected void searchNonColumnField(StringBuilder buff, List<Object> params, ChatLogSO so, String alias) {
	}
	
	public List<ChatLogPO> getChatList(ChatLogSO so) {
		String rela = TableNames.TABLE_USER_RELA;
		String user  = TableNames.TABLE_USER;
		List<Object> params = new ArrayList<>();
		StringBuilder buff = new StringBuilder();
		this.searchSO(buff, params, so, "chat");
		this.searchSuffix(buff, params, so, "chat");
		Integer domainId = SecurityContextHolder.getUserDomainId();
		String sql = " SELECT " + this.getColumnAlias("chat") +
				"," + userRelaDao.getColumnAlias("rela") +
				"," + userDao.getColumnAlias("u") +
				" FROM " + TABLE_NAME + " chat " +
				" inner join " + rela + " rela on chat.domainId = rela.domainId " +
				" inner join " + user + " u on rela.userId = u.id " +
				" where 1=1 " + buff.toString();
		return dao.query(sql, params.toArray(), new RowMapper<ChatLogPO>() {
			@Override
			public ChatLogPO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ChatLogPO po = new ChatLogPO();
				int index = 1;
				po.setId(rs.getInt(index++));
				po.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
				po.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
				po.setDomainId(rs.getInt(index++));
				po.setLiveCourseId(rs.getInt(index++));
				po.setMsg(rs.getString(index++));
				po.setBlocked(rs.getString(index++));
				index++;
				UserPO user = new UserPO();
				user.setCreateTime(DateUtils.toDate(rs.getString(index++), true));
				index++;
				index++;
				index++;
				user.setPhone(rs.getString(index++));
				user.setDomainId(rs.getInt(index++));
				user.setLiveVipStartTime(DateUtils.toDate(rs.getString(index++), true));
				user.setValidity(rs.getInt(index++));
				user.setPhoneRegTime(DateUtils.toDate(rs.getString(index++), true));
				
				user.setId(rs.getInt(index++));
				index++;
				user.setUpdateTime(DateUtils.toDate(rs.getString(index++), true));
				user.setOpenId(rs.getString(index++));
				user.setHeadImg(rs.getString(index++));
				user.setNick(rs.getString(index++));
				user.setCompany(rs.getString(index++));
				user.setTitle(rs.getString(index++));
				user.setRegChannel(rs.getString(index++));
				user.setLiveVip(rs.getString(index++));
				user.setLastLoginTime(DateUtils.toDate(rs.getString(index++), true));
				po.setSelf(domainId.equals(po.getDomainId()) ? BoolStatus.Y.toString() : BoolStatus.N.toString());
				po.setUser(user);
				return po;
			}
		});
	}
}
