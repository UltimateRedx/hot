package com.hotelpal.service.basic.mysql;

import com.hotelpal.service.common.context.SecurityContextHolder;
import com.hotelpal.service.common.po.DomainBasePO;
import com.hotelpal.service.common.so.DomainBaseSO;
import com.hotelpal.service.common.utils.ValidationUtils;

import java.util.Date;
import java.util.List;

public abstract class DomainMysqlBaseDao<S extends DomainBaseSO, P extends DomainBasePO> extends MysqlBaseDao<S, P> {
	@Override
	protected void fillCreateInfo(P po) {
		Date date = new Date();
		po.setCreateTime(date);
		po.setUpdateTime(date);
		if (getTableColumnMap().containsKey("domainId")) {
			if (SecurityContextHolder.isSuperDomain()) {
				if (SecurityContextHolder.getTargetDomain() != null) {
					po.setDomainId(SecurityContextHolder.getTargetDomain());
				} else if (po.getUseSpecifiedDomain()) {
					;
				} else {
					po.setDomainId(SecurityContextHolder.getUserDomainId());
				}
			}else {
				po.setDomainId(SecurityContextHolder.getUserDomainId());
			}
		}
	}
	
	@Override
	protected void fillUpdateInfo(P po) {
		po.setUpdateTime(new Date());
		//DomainId does not need to update.
	}
	
	@Override
	protected void searchBaseSO(StringBuilder buff, List<Object> params, S so, String baseAlias) {
		String alias = ValidationUtils.isNotNullEmpty(baseAlias) ? " `" + baseAlias + "`." : "";
		this.searchCommonSO(buff, params, so, baseAlias);
		if (getTableColumnMap().containsKey("domainId") && so.getIgnoreDomainId() != null && !so.getIgnoreDomainId()) {
			Integer domainId = SecurityContextHolder.getUserDomainId();
			if (SecurityContextHolder.isSuperDomain() ) {
				if (SecurityContextHolder.getTargetDomain() != null) {
					domainId = SecurityContextHolder.getTargetDomain();
				} else {
					domainId = null;
				}
			}
			if (domainId != null) {
				buff.append(" AND ").append(alias).append("`domainId` = ? ");
				params.add(domainId);
			}
		}
	}
}
