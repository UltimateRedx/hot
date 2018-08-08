package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.StatisticsDao;
import com.hotelpal.service.basic.mysql.dao.UserDao;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.common.context.SecurityContext;
import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.StatisticsType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.StatisticsPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.StatisticsSO;
import com.hotelpal.service.common.utils.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Component
@Transactional
public class CommonService {
	@Resource
	private UserDao userDao;
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private StatisticsDao statisticsDao;
	
	public void putAuth(String openId) {
		UserPO userPO = userRelaDao.getByOpenId(openId);
		if (userPO == null) {
			throw new ServiceException(ServiceException.DAO_OPENID_NOT_FOUND);
		}
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			context = new SecurityContext();
		}
		context.setDomainId(userPO.getDomainId());
		context.setOpenId(openId);
		context.setPhone(userPO.getPhone());
		context.setUserId(userPO.getId());
		context.setLiveVip(BoolStatus.N.toString());
		if (BoolStatus.Y.toString().equalsIgnoreCase(userPO.getLiveVip()) && userPO.getValidity() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(userPO.getLiveVipStartTime());
			cal.add(Calendar.DATE, userPO.getValidity() - 1);
			context.setLiveVipValidity(DateUtils.setMaxTime(cal).getTime());
			context.setLiveVip(BoolStatus.Y.toString());
		}
		SecurityContextHolder.setContext(context);
		UserPO user = userDao.getById(userPO.getId());
		user.setLastLoginTime(new Date());
		userDao.update(user);
	}
	
	public void logSitePV(int pv) {
		if (pv == 0) return;
		StatisticsSO so = new StatisticsSO();
		so.setOrder("desc");
		so.setOrderBy("statisticsDate");
		so.setType(StatisticsType.SITE_PV.toString());
		StatisticsPO po = statisticsDao.getOne(so);
		if (po == null) {
			po = new StatisticsPO();
			po.setValue(0);
			po.setType(StatisticsType.SITE_PV.toString());
			po.setStatisticsDate(new Date());
			po.setValue(po.getValue() + pv);
			statisticsDao.create(po);
			return;
		}
		po.setValue(po.getValue() + pv);
		statisticsDao.update(po);
		Calendar now = Calendar.getInstance();
		Calendar statisticsDate = Calendar.getInstance();
		statisticsDate.setTime(po.getStatisticsDate());
		if (now.get(Calendar.DATE) != statisticsDate.get(Calendar.DATE)) {
			StatisticsPO newDay = new StatisticsPO();
			newDay.setValue(0);
			newDay.setStatisticsDate(new Date());
			newDay.setType(StatisticsType.SITE_PV.toString());
			statisticsDao.create(newDay);
		}
	}
	
	public void logSiteUV(int uv) {
		if (uv == 0) return;
		StatisticsSO so = new StatisticsSO();
		so.setOrder("desc");
		so.setOrderBy("statisticsDate");
		so.setType(StatisticsType.SITE_UV.toString());
		StatisticsPO po = statisticsDao.getOne(so);
		if (po == null) {
			po = new StatisticsPO();
			po.setType(StatisticsType.SITE_UV.toString());
			po.setStatisticsDate(new Date());
			po.setValue(uv);
			statisticsDao.create(  po);
			return;
		}
		po.setValue(uv);
		statisticsDao.update(po);
		Calendar now = Calendar.getInstance();
		Calendar statisticsDate = Calendar.getInstance();
		statisticsDate.setTime(po.getStatisticsDate());
		if (now.get(Calendar.DATE) != statisticsDate.get(Calendar.DATE)) {
			StatisticsPO newDay = new StatisticsPO();
			newDay.setValue(0);
			newDay.setStatisticsDate(new Date());
			newDay.setType(StatisticsType.SITE_UV.toString());
			statisticsDao.create(newDay);
		}
	}
	
	public void logCourseLessonPVUV (Integer courseId, StatisticsType type, int value) {
		if (value == 0) return;
		StatisticsSO so = new StatisticsSO();
		so.setOrder("desc");
		so.setOrderBy("statisticsDate");
		so.setType(type.toString());
		so.setStatisticsId(courseId);
		StatisticsPO po = statisticsDao.getOne(so);
		if (po == null) {
			po = new StatisticsPO();
			po.setValue(value);
			po.setType(type.toString());
			po.setStatisticsDate(new Date());
			po.setStatisticsId(courseId);
			statisticsDao.create(po);
			return;
		}
		po.setValue(StatisticsType.COURSE_PV.equals(type) || StatisticsType.LESSON_PV.equals(type) ? (po.getValue() + value) : value);
		statisticsDao.update(po);
		Calendar now = Calendar.getInstance();
		Calendar statisticsDate = Calendar.getInstance();
		statisticsDate.setTime(po.getStatisticsDate());
		if (now.get(Calendar.DATE) != statisticsDate.get(Calendar.DATE)) {
			StatisticsPO newDay = new StatisticsPO();
			newDay.setValue(0);
			newDay.setStatisticsDate(new Date());
			newDay.setType(type.toString());
			newDay.setStatisticsId(courseId);
			statisticsDao.create(newDay);
		}
	}
	
}
