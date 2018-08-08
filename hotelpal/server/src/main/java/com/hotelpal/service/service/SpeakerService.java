package com.hotelpal.service.service;

import com.hotelpal.service.basic.mysql.dao.SpeakerDao;
import com.hotelpal.service.common.po.SpeakerPO;
import com.hotelpal.service.common.so.SpeakerSO;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Component
@Transactional
public class SpeakerService {
	@Resource
	private SpeakerDao speakerDao;
	@Resource
	private DozerBeanMapper dozerBeanMapper;
	
	public List<SpeakerPO> getSpeakerList(SpeakerSO so) {
		so.setTotalCount(speakerDao.count(so));
		return speakerDao.getList(so);
	}
	
	public SpeakerPO updateSpeaker(SpeakerSO so) {
		SpeakerPO res = new SpeakerPO();
		if (so.getId() != null) {
			SpeakerPO po = speakerDao.getById(so.getId());
			if (po == null) {
				//create
				res = dozerBeanMapper.map(so, SpeakerPO.class);
				speakerDao.create(res);
			} else{
				//update
				res = dozerBeanMapper.map(so, SpeakerPO.class);
				speakerDao.update(res);
			}
		} else {
			//create
			res = dozerBeanMapper.map(so, SpeakerPO.class);
			speakerDao.create(res);
		}
		return res;
	}
	
	public void deleteSpeaker(Integer id) {
		speakerDao.delete(id);
	}
}
