package com.mfp.pgxl.stat.service.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.alibaba.fastjson.JSON;
import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;
import com.mfp.pgxl.stat.utils.AllInOne;

public class StatTables extends AbstractScheduledTask {
	
	private static final Logger logger = LoggerFactory.getLogger(StatTables.class);
	
	private static String SQL_QUERY;
	
	private static final String PS_SQL_INSERT = "insert into stat_table values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private String node;
	
	private JdbcTemplate pgdb;
	
	private LinkedBlockingQueue<Object> queue;
	
	static {
		Resource resource = new ClassPathResource("sql/table_stat.sql");
		
		List<String> sqls = null;
		try {
			sqls = AllInOne.getSqlFromInputStream(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		if(sqls != null && sqls.size() > 0){
			SQL_QUERY = sqls.get(0);
		}
	}
	
	public StatTables(int id, Date beginTime, int period, 
			String node, JdbcTemplate pgdb, LinkedBlockingQueue<Object> queue) {
		super(id, true, false, beginTime, period);
		this.node = node;
		this.pgdb = pgdb;
		this.queue = queue;
	}

	@Override
	public void service() {
		List<Map<String, Object>> list = null;
		try {
			list = pgdb.queryForList(SQL_QUERY);
		} catch (Exception e) {
			logger.error(node + " " + e.getMessage());
		}
		
		if(list != null){
			queue.add("delete from stat_table where node = '" + node + "'");
			
			for(Map<String, Object> map: list){
	    		
	    		queue.add(new PreparedStatementCreator(){

					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						
						PreparedStatement ps = con.prepareStatement(PS_SQL_INSERT);
						ps.setString(1, node);
						ps.setString(2, AllInOne.getCurrentDatetime());
						ps.setString(3, (String)map.get("db"));
						ps.setString(4, (String)map.get("schemaname"));
						ps.setString(5, (String)map.get("relname"));
						ps.setLong(6, (Long)map.get("table_size"));
						ps.setLong(7, (Long)map.get("indexes_size"));
						ps.setLong(8, (Long)map.get("total_size"));
						ps.setLong(9, (Long)map.get("seq_scan"));
						ps.setLong(10, (Long)map.get("seq_tup_read"));
						ps.setLong(11, (Long)map.get("idx_scan"));
						ps.setLong(12, (Long)map.get("idx_tup_fetch"));
						ps.setLong(13, (Long)map.get("n_tup_ins"));
						ps.setLong(14, (Long)map.get("n_tup_upd"));
						ps.setLong(15, (Long)map.get("n_tup_del"));
						ps.setLong(16, (Long)map.get("n_tup_hot_upd"));
						ps.setLong(17, (Long)map.get("tups"));
						ps.setLong(18, (Long)map.get("n_live_tup"));
						ps.setLong(19, (Long)map.get("n_dead_tup"));
						ps.setLong(20, (Long)map.get("pages"));
						ps.setLong(21, (Long)map.get("otta"));
						ps.setLong(22, (Long)map.get("wastedbytes"));
						ps.setLong(23, (Long)map.get("autovacuum_count"));
						ps.setLong(24, (Long)map.get("autoanalyze_count"));
						
						logger.debug(JSON.toJSONString(map));
						
						return ps;
					}
	    			
	    		});
	    	}
		}
	}
	
}
