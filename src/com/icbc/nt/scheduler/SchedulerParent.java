package com.icbc.nt.scheduler;

import com.icbc.nt.bus.BusParent;
import com.icbc.nt.dom.DaoParent;

public class SchedulerParent extends BusParent{
	protected DaoParent daoObj;
	public void wglBat(){}
	public void pointBat(){}
	public DaoParent getDaoObj() {
		return daoObj;
	}
	public void setDaoObj(DaoParent daoObj) {
		this.daoObj = daoObj;
	}
}
