package com.mfp.pgxl.stat.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.alibaba.fastjson.JSON;
import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;
import com.mfp.pgxl.stat.utils.AllInOne;

public class SnapPgStatActivity extends AbstractScheduledTask {
	
	private static final Logger logger = LoggerFactory.getLogger(SnapPgStatActivity.class);
	
	private static final String SQL_JOIN = "select extract(epoch from (now() - xact_start)) xact_time, "
			+ "extract(epoch from (now() - query_start)) query_time, "
			+ "query, state, waiting, client_addr, s1.pid, datname, relname, locktype, mode, granted "
			+ "from pg_stat_activity s1 "
			+ "left join pg_locks s2 on s1.pid = s2.pid "
			+ "left join pg_class s3 on s2.relation = s3.oid "
			+ "where state != 'idle' and ((now() - xact_start) > '00:00:10' or (now() - query_start) > '00:00:10')";

	private static final String PS_SQL_INSERT = "insert into snap_pg_stat_activity values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private String node;
	
	private JdbcTemplate pgdb;
	
	private LinkedBlockingQueue<Object> queue;
	
	public SnapPgStatActivity(int id, Date beginTime, int period, 
			String node, JdbcTemplate pgdb, LinkedBlockingQueue<Object> queue) {
		super(id, true, false, beginTime, period);
		this.node = node;
		this.pgdb = pgdb;
		this.queue = queue;
	}

	@Override
	public void service() {
		queue.add("delete from snap_pg_stat_activity where node = '" + node + "'");
		
		List<Map<String, Object>> list = null;
		try {
			list = pgdb.queryForList(SQL_JOIN);
		} catch (Exception e) {
			logger.error(node + " " + e.getMessage());
		}
		
		if(list != null){
			for(Map<String, Object> map: list){
	    		
	    		queue.add(new PreparedStatementCreator(){

					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						
						PreparedStatement ps = con.prepareStatement(PS_SQL_INSERT);
						ps.setString(1, node);
						ps.setString(2, AllInOne.getCurrentDatetime());
						ps.setDouble(3, (Double)map.get("xact_time"));
						ps.setDouble(4, (Double)map.get("query_time"));
						ps.setString(5, ((String)map.get("query")).replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", " "));
						ps.setString(6, (String)map.get("state"));
						ps.setString(7, ((Boolean)map.get("waiting")).toString());
						ps.setString(8, ((Integer)map.get("pid")).toString());
						ps.setString(9, (String)map.get("datname"));
						ps.setString(10, map.get("client_addr") == null ? null : ((PGobject)map.get("client_addr")).getValue());
						ps.setString(11, (String)map.get("relname"));
						ps.setString(12, (String)map.get("locktype"));
						ps.setString(13, (String)map.get("mode"));
						ps.setString(14, map.get("granted") == null ? null : ((Boolean)map.get("granted")).toString());
						
						logger.debug(JSON.toJSONString(map));
						
						return ps;
					}
	    			
	    		});
	    	}
		}
	}
}
