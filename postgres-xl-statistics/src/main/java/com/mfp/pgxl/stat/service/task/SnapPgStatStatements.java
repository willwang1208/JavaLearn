package com.mfp.pgxl.stat.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.alibaba.fastjson.JSON;
import com.mfp.pgxl.stat.schedule.AbstractScheduledTask;
import com.mfp.pgxl.stat.utils.AllInOne;

public class SnapPgStatStatements extends AbstractScheduledTask {
	
	private static final Logger logger = LoggerFactory.getLogger(SnapPgStatStatements.class);
	
	private static final String SQL_UNION = "select c.rolname, b.datname, a.* from ( "
			+ "select * from (select * from pg_stat_statements order by mean_time desc limit 10) as top_mean_time "
			+ "union "
			+ "select * from (select * from pg_stat_statements order by total_time desc limit 10) as top_time "
			+ "union "
			+ "select * from (select * from pg_stat_statements order by calls desc limit 10) as top_calls "
			+ "union "
			+ "select * from (select * from pg_stat_statements order by (blk_read_time + blk_write_time) desc limit 10) as top_io_time "
			+ "union "
			+ "select * from (select * from pg_stat_statements order by (blk_read_time + blk_write_time)/calls desc limit 10) as top_io_mean_time "
			+ "union "
			+ "select * from (select * from pg_stat_statements order by stddev_time desc limit 10) as top_stddev_time "
			+ "union "
			+ "select * from (select * from pg_stat_statements order by (shared_blks_hit + shared_blks_dirtied) desc limit 10) as top_use_memery "
			+ ") a, pg_database b, pg_authid c where a.userid = c.oid and a.dbid = b.oid";
	
	private static final String PS_SQL_INSERT = "insert into snap_pg_stat_statements values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private String node;
	
	private JdbcTemplate pgdb;
	
	private LinkedBlockingQueue<Object> queue;
	
	public SnapPgStatStatements(int id, Date beginTime, int period, 
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
			list = pgdb.queryForList(SQL_UNION);
		} catch (Exception e) {
			logger.error(node + " " + e.getMessage());
		}
		
		if(list != null){
			queue.add("delete from snap_pg_stat_statements where node = '" + node + "'");
			
			for(Map<String, Object> map: list){
	    		
	    		queue.add(new PreparedStatementCreator(){

					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						
						PreparedStatement ps = con.prepareStatement(PS_SQL_INSERT);
						ps.setString(1, node);
						ps.setString(2, AllInOne.getCurrentDatetime());
						ps.setString(3, (String)map.get("rolname"));
						ps.setString(4, (String)map.get("datname"));
						ps.setString(5, ((String)map.get("query")).replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", " "));
						ps.setLong(6, (Long)map.get("calls"));
						ps.setDouble(7, (Double)map.get("total_time"));
						ps.setDouble(8, (Double)map.get("min_time"));
						ps.setDouble(9, (Double)map.get("max_time"));
						ps.setDouble(10, (Double)map.get("mean_time"));
						ps.setDouble(11, (Double)map.get("stddev_time"));
						ps.setLong(12, (Long)map.get("rows"));
						ps.setLong(13, (Long)map.get("shared_blks_hit"));
						ps.setLong(14, (Long)map.get("shared_blks_read"));
						ps.setLong(15, (Long)map.get("shared_blks_dirtied"));
						ps.setLong(16, (Long)map.get("shared_blks_written"));
						ps.setLong(17, (Long)map.get("local_blks_hit"));
						ps.setLong(18, (Long)map.get("local_blks_read"));
						ps.setLong(19, (Long)map.get("local_blks_dirtied"));
						ps.setLong(20, (Long)map.get("local_blks_written"));
						ps.setLong(21, (Long)map.get("temp_blks_read"));
						ps.setLong(22, (Long)map.get("temp_blks_written"));
						ps.setDouble(23, (Double)map.get("blk_read_time"));
						ps.setDouble(24, (Double)map.get("blk_write_time"));
						
						logger.debug(JSON.toJSONString(map));
						
						return ps;
					}
	    			
	    		});
	    	}
		}
	}
}
