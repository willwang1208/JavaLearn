package com.mfp.pgxl.stat.service.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;

public class MaintainSqlite extends AbstractScheduledTask {
	
	private final static Logger logger = LoggerFactory.getLogger(MaintainSqlite.class);
    
	private LinkedBlockingQueue<Object> queue;
	
	public MaintainSqlite(int id, Date beginTime, int period, 
			LinkedBlockingQueue<Object> queue) {
		super(id, true, false, beginTime, period);
		this.queue = queue;
	}

	@Override
	public void service() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		String barrier = formatter.format(calendar.getTime());
		
		queue.add("delete from stat_statements_tps where dt < '" + barrier + "'");
		queue.add("vacuum");
		
		logger.info("maintain sqlite");
	}
	
}
