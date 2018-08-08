package com.hotelpal.service.service.live;

import com.hotelpal.service.basic.mysql.dao.UserDao;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.po.UserRelaPO;
import com.hotelpal.service.common.so.UserRelaSO;
import com.hotelpal.service.common.utils.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Transactional
public class MemberService {
	@Resource
	private UserRelaDao userRelaDao;
	@Resource
	private UserDao userDao;
	
	public List<UserPO> getVipMemberList(UserRelaSO so) {
		so.setLiveVip(BoolStatus.Y.toString());
		so.setTotalCount(userRelaDao.getCount(so));
		so.setOrder("desc");
		so.setOrderBy("liveVipStartTime");
		List<UserPO> poList =  userRelaDao.getPageList(so);
		Calendar cal = Calendar.getInstance();
		for (UserPO po : poList) {
			cal.setTime(po.getLiveVipStartTime());
			cal.add(Calendar.DATE, po.getValidity() - 1);
			DateUtils.setMaxTime(cal);
			po.setValidityTo(cal.getTime());
		}
		return poList;
	}
	
	public void addLiveVip(String phone, Integer validity) {
		UserRelaSO so = new UserRelaSO();
		so.setPhone(phone);
		UserRelaPO po = userRelaDao.getOne(so);
		if (po == null) {
			throw new ServiceException(ServiceException.USER_PHONE_NOT_EXISTS);
		}
		UserPO user = userDao.getById(po.getUserId());
		if (user == null) {
			throw new ServiceException("数据库用户关联错误");
		}
		if (BoolStatus.Y.toString().equalsIgnoreCase(user.getLiveVip())) {
//			Date vipStartDate = po.getLiveVipStartTime();
//			Calendar thisEndTime = Calendar.getInstance();
//			thisEndTime.setTime(vipStartDate);
//			thisEndTime.add(Calendar.DATE, validity - 1);
//			DateUtils.setMaxTime(thisEndTime);
//			Calendar oriEndTime = Calendar.getInstance();
//			oriEndTime.setTime(vipStartDate);
//			oriEndTime.add(Calendar.DATE, po.getValidity() - 1);
//			DateUtils.setMaxTime(oriEndTime);
//			//过期
//			if (oriEndTime.before(new Date())) {
				po.setValidity(validity);
				po.setLiveVipStartTime(new Date());
				userRelaDao.update(po);
//			} else if (thisEndTime.after(oriEndTime)) {
//				po.setValidity(po.getValidity() + DateUtils.daysBetween(thisEndTime, oriEndTime) - 1);
//				userRelaDao.update(po);
//			}
		} else {
			user.setLiveVip(BoolStatus.Y.toString());
			userDao.update(user);
			po.setLiveVipStartTime(new Date());
			po.setValidity(validity);
			userRelaDao.update(po);
		}
	}
	
	public void removeLiveVip(String phone) {
		UserRelaSO so = new UserRelaSO();
		so.setPhone(phone);
		UserRelaPO rela = userRelaDao.getOne(so);
		if (rela == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		rela.setValidity(0);
		rela.setLiveVipStartTime(null);
		userRelaDao.update(rela);
		UserPO user = userDao.getById(rela.getUserId());
		if (user == null) {
			throw new ServiceException(ServiceException.DAO_USER_RELA_FAILED);
		}
		user.setLiveVip(BoolStatus.N.toString());
		userDao.update(user);
	}
}
