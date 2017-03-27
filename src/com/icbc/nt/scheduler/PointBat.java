
/**2015-10-29
 * 积分批量计算定时任务扫描
 * @author Administrator
 */
package com.icbc.nt.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.icbc.nt.bus.BusParent;

public class PointBat implements Job{
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println("************PointBat积分批量计算定时任务扫描***********");
		
	}
}
