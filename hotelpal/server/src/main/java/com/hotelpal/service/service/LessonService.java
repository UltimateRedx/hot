package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.LessonContentDao;
import com.hotelpal.service.basic.mysql.dao.LessonDao;
import com.hotelpal.service.basic.mysql.dao.StatisticsDao;
import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.enums.LessonType;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.QNMetaDataMO;
import com.hotelpal.service.common.mo.QNPersistMO;
import com.hotelpal.service.common.mo.ValuePair;
import com.hotelpal.service.common.po.BasePO;
import com.hotelpal.service.common.po.LessonContentPO;
import com.hotelpal.service.common.po.LessonPO;
import com.hotelpal.service.common.so.LessonSO;
import com.hotelpal.service.common.utils.DateUtils;
import com.hotelpal.service.common.utils.NumberUtils;
import com.hotelpal.service.common.vo.StatisticsLessonVO;
import com.hotelpal.service.service.parterner.QNService;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class LessonService {
	@Resource
	private LessonDao lessonDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	@Resource
	private LessonContentDao lessonContentDao;
	@Resource
	private StatisticsDao statisticsDao;
	
	public List<StatisticsLessonVO> getLessonList(LessonSO so, boolean containsContent) {
		so.setTotalCount(lessonDao.count(so));
		List<LessonPO> poList = lessonDao.getList(so, containsContent);
		for (LessonPO po : poList) {
			po.setResourceSize(NumberUtils.getReadableSize(po.getAudioSize()));
		}
		List<StatisticsLessonVO> voList = new ArrayList<>(poList.size());
		List<Integer> lessonIdList = poList.stream().map(BasePO::getId).collect(Collectors.toList());
		String from = DateUtils.getDateString(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		String to = DateUtils.getDateString(cal);
		Map<Integer, ValuePair<Long, Long>> resMap = statisticsDao.getStatisticsByLessonList(lessonIdList, from, to);
		for (LessonPO po : poList) {
			StatisticsLessonVO vo = dozerBeanMapper.map(po, StatisticsLessonVO.class);
			vo.setPv(resMap.get(vo.getId()).getName());
			vo.setUv(resMap.get(vo.getId()).getValue());
			voList.add(vo);
		}
		return voList;
	}
	
	public void deleteLesson(Integer id) {
		LessonPO po = lessonDao.getById(id);
		if (po == null) {
			throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
		}
		po.setDeleted(BoolStatus.Y.toString());
		lessonDao.update(po);
	}
	
	public void updateLesson(LessonSO so) {
		boolean update = so.getId() != null;
		//创建
		if (!update) {
			LessonContentPO content = new LessonContentPO();
			content.setContent(so.getContent());
			lessonContentDao.create(content);
			
			LessonPO po = dozerBeanMapper.map(so, LessonPO.class);
			po.setContentId(content.getId());
			po.setType(LessonType.SELF.toString().equalsIgnoreCase(so.getType()) ?
					LessonType.SELF.toString() : LessonType.NORMAL.toString());
			
			//order/no
			if (LessonType.SELF.toString().equalsIgnoreCase(po.getType())) {
				LessonSO lso = new LessonSO();
				lso.setDeleted(BoolStatus.N.toString());
				lso.setType(LessonType.SELF.toString());
				Integer count = lessonDao.count(lso);
				po.setLessonOrder(count + 1);
				po.setNo(count + 1);
			}
			
			lessonDao.create(po);
		} else {
			LessonPO ori = lessonDao.getById(so.getId(), true);
			if (ori == null) {
				throw new ServiceException(ServiceException.DAO_DATA_NOT_FOUND);
			}
			dozerBeanMapper.map(so, ori);
			LessonContentPO updateContent = lessonContentDao.getById(ori.getContentId());
			if (updateContent != null) {
				updateContent.setContent(so.getContent());
				lessonContentDao.update(updateContent);
			}
			lessonDao.update(ori);
		}
	}
	
	public void receivePersistNotification(QNPersistMO request) {
		if (request == null) {
			return;
		}
		if (CollectionUtils.isNotEmpty(request.getItems())) {
			for (final QNPersistMO.QNPersistItem item : request.getItems()) {
				if (item.getCode() == 0) {
					String key = item.getKey();
					QNMetaDataMO res = QNService.getMetaData(QNService.BUCKET_AUDIO, key);
					if (res != null) {
						Integer size = res.getFsize();
						if (size != null) {
							LessonSO lso = new LessonSO();
							lso.setAudioUrl(QNService.BUCKET_AUDIO + key);
							List<LessonPO> lessonList = lessonDao.getList(lso, false);
							for (LessonPO lesson : lessonList) {
								lesson.setAudioSize(size);
								lessonDao.update(lesson);
							}
						}
					}
				}
			}
		}
	}
	
	
//	private int getLessonOrder(Integer courseId) {
//		LessonSO so =  new LessonSO();
//		so.setCourseId(courseId);
//		so.setDeleted(BoolStatus.N.toString());
//	}
}
