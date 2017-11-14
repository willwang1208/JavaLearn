package com.mfp.pgxl.stat.service.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;
import com.mfp.pgxl.stat.utils.AllInOne;

public class StatTransactionsInUnitTime extends AbstractScheduledTask {
	
	private final static Logger logger = LoggerFactory.getLogger(StatTransactionsInUnitTime.class);
    
	private static final String SQL_QUERY = "select sum(calls) sum_calls, extract(epoch from now()) * 1000 tms from pg_stat_statements";
			
	private String node;
	
	private JdbcTemplate pgdb;
	
	private LinkedBlockingQueue<Object> queue;
	
	private Snap last;
	
	public StatTransactionsInUnitTime(int id, Date beginTime, int period, 
			String node, JdbcTemplate pgdb, LinkedBlockingQueue<Object> queue) {
		super(id, true, false, beginTime, period);
		this.node = node;
		this.pgdb = pgdb;
		this.queue = queue;
	}

	@Override
	public void service() {
		try {
			Map<String, Object> rs = pgdb.queryForMap(SQL_QUERY);
			if(rs != null){
				Snap current = new Snap();
				current.sum_calls = ((BigDecimal)rs.get("sum_calls")).longValue();
				current.tms = (Double)rs.get("tms");
				
				if(last != null){
					int tps = (int)((current.sum_calls - last.sum_calls) * 1.0 / (current.tms - last.tms) * 1000);
					if(tps >= 0){
						String save = "insert into stat_statements_tps values('" + node + "', '" + AllInOne.getCurrentDatetime() + "', " + tps + ")";
				    	queue.add(save);
					}
				}
				
				last = current;
			}
		} catch (Exception e) {
			logger.error(node + " " + e.getMessage());
		}
	}
	
	class Snap{
		double tms;
		long sum_calls;
		
	}
}
