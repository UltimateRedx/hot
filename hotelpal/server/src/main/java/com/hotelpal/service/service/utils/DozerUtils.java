package com.hotelpal.service.service.utils;

import com.hotelpal.service.common.po.BasePO;
import com.hotelpal.service.common.utils.ValidationUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class DozerUtils {
	@Resource
	private DozerBeanMapper dozer;
	
	public <T>List<T> poListToVoList(final List<? extends BasePO> sourceList, Class<T> targetClass) {
		if (ValidationUtils.isNullEmpty(sourceList)) return null;
		List<T> res = new ArrayList<>();
		for (Object o : sourceList) {
			res.add(dozer.map(o, targetClass));
		}
		return res;
	}
}
