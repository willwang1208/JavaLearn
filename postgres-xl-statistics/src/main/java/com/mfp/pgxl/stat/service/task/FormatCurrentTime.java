package com.mfp.pgxl.stat.service.task;

import java.util.Date;

import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;
import com.mfp.pgxl.stat.utils.AllInOne;

public class FormatCurrentTime extends AbstractScheduledTask {

	public FormatCurrentTime(int id, Date beginTime, int period) {
		super(id, true, false, beginTime, period);
	}

	@Override
	public void service() {
		AllInOne.computeCurrentDatetime();
	}

}
