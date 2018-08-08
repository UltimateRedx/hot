package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.*;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.CourseType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.CourseContentPO;
import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.po.SpeakerPO;
import com.hotelpal.service.common.so.CourseSO;
import com.hotelpal.service.common.so.PurchaseLogSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.StringUtils;
import com.hotelpal.service.common.vo.StatisticsCourseVO;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Component
@Transactional
public class CourseService {
	@Resource
	private CourseDao courseDao;
	@Resource
	private SpeakerDao speakerDao;
	@Resource
	private CourseContentDao courseContentDao;
	@Resource
	private PurchaseLogDao purchaseLogDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	@Resource
	private StatisticsDao statisticsDao;
	
	public List<CoursePO> getCourseList(CourseSO so, boolean containsContent) {
		so.setTotalCount(courseDao.count(so));
		List<CoursePO> poList;
		so.setOrderBy("courseOrder");
		if (containsContent) {
			poList = courseDao.getCourseAndContent(so);
		} else{
			poList = courseDao.getList(so);
		}
		for (CoursePO po : poList) {
			if (!StringUtils.isNullEmpty(po.getTag())) {
				po.setTagList(Arrays.asList(po.getTag().split(",")));
			}
			SpeakerPO s = speakerDao.getById(po.getSpeakerId());
			if (s != null) {
				po.setSpeaker(s);
			}
		}
		return poList;
	}
	public List<StatisticsCourseVO> getCourseList(CourseSO so, boolean containsContent, Date from, Date to) {
		List<CoursePO> poList = getCourseList(so, containsContent);
		List<StatisticsCourseVO> resList = new ArrayList<>(poList.size());
		List<Integer> courseIdList = new ArrayList<>(poList.size());
		for (CoursePO po : poList) {
			resList.add(dozerBeanMapper.map(po, StatisticsCourseVO.class));
			courseIdList.add(po.getId());
		}
		//添加统计信息
		if (from  == null) {
			from = new Date();
		}
		Calendar cal = Calendar.getInstance();
		if (to != null) {
			cal.setTime(to);
		}
		cal.add(Calendar.DATE, 1);
		to = cal.getTime();
		String fromStr = DateUtils.getDateString(from);
		String toStr = DateUtils.getDateString(to);
		Map<Integer, ValuePair<Long, Long>> statisticsMap = statisticsDao.getStatisticsByCourseList(courseIdList, fromStr, toStr);
		Map<Integer, ValuePair<Integer, Long>> purchaseMap = purchaseLogDao.getSaleStatisticsByCourseId(CourseType.NORMAL.toString(), courseIdList, fromStr,toStr);
		for (StatisticsCourseVO vo : resList) {
			vo.setSold(purchaseMap.get(vo.getId()).getName());
			vo.setSales(purchaseMap.get(vo.getId()).getValue());
			vo.setPv(statisticsMap.get(vo.getId()).getName());
			vo.setUv(statisticsMap.get(vo.getId()).getValue());
		}
		
		return resList;
	}
	
	
	public CoursePO getCourse(Integer id, boolean containsContent) {
		CoursePO po = courseDao.getById(id, containsContent);
		SpeakerPO speaker = speakerDao.getById(po.getSpeakerId());
		if (speaker != null) {
			po.setSpeaker(speaker);
		}
		return po;
	}
	
	public void updateCourse(CourseSO so) {
		boolean update = so.getId() != null;
		if (!update) {
			CourseContentPO content = new CourseContentPO();
			content.setIntroduce(so.getIntroduce());
			content.setCrowd(so.getCrowd());
			content.setGain(so.getGain());
			content.setSubscribe(so.getSubscribe());
			courseContentDao.create(content);
			
			CoursePO po = dozerBeanMapper.map(so, CoursePO.class);
			po.setContentId(content.getId());
			courseDao.create(po);
		} else {
			CoursePO ori = courseDao.getById(so.getId(), true);
			if (ori == null) {
				throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
			}
			dozerBeanMapper.map(so, ori);
	
			CourseContentPO updateContent = ori.getCourseContent();
			if (updateContent != null) {
				updateContent.setIntroduce(so.getIntroduce());
				updateContent.setCrowd(so.getCrowd());
				updateContent.setGain(so.getGain());
				updateContent.setSubscribe(so.getSubscribe());
				courseContentDao.update(updateContent);
			}
			courseDao.update(ori);
		}
	}
	
	public void delete(Integer id) {
		CoursePO po = courseDao.getById(id);
		if (po == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		po.setDeleted(BoolStatus.Y.toString());
		courseDao.update(po);
	}
	
	public boolean coursePurchased(Integer courseId) {
		PurchaseLogSO so = new PurchaseLogSO();
		so.setCourseId(courseId);
		return purchaseLogDao.count(so) > 0;
	}
}
