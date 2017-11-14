package com.mfp.pgxl.stat.service.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;

public class ResetPgStatStatements extends AbstractScheduledTask {
	
	private final static Logger logger = LoggerFactory.getLogger(ResetPgStatStatements.class);
	
	private static final String SQL_RESET_STATEMENTS = "select pg_stat_statements_reset()";
	
	private String node;
	
	private JdbcTemplate pgdb;
	
	public ResetPgStatStatements(int id, Date beginTime, int period, 
			String node, JdbcTemplate pgdb) {
		super(id, true, false, beginTime, period);
		this.node = node;
		this.pgdb = pgdb;
	}

	@Override
	public void service() {
		try {
			pgdb.execute(SQL_RESET_STATEMENTS);
			logger.info("execute pg_stat_statements_reset() on " + node);
		} catch (Exception e) {
			logger.error(node + " " + e.getMessage());
		}
		
	}
}
