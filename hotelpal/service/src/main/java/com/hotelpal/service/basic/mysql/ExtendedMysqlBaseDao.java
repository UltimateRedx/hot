package com.hotelpal.service.basic.mysql;

import com.hotelpal.service.common.enums.BoolStatus;
import com.hotelpal.service.common.po.ExtendedBasePO;
import com.hotelpal.service.common.so.ExtendedBaseSO;


public abstract class ExtendedMysqlBaseDao<S extends ExtendedBaseSO, P extends ExtendedBasePO> extends DomainMysqlBaseDao<S, P>{
	
	@Override
	protected void fillCreateInfo(P po) {
		super.fillCreateInfo(po);
		if (getTableColumnMap().containsKey("deleted")) {
			po.setDeleted(BoolStatus.N.toString());
		}
	}
	
	@Override
	public void delete(Integer id) {
		if (id == null) return;
		P po = getById(id);
		if (po != null) {
			po.setDeleted(BoolStatus.Y.toString());
		update(po);
		}
	}
}
