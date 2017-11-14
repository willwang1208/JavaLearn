package com.mfp.pgxl.stat.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Service;

import com.mfp.pgxl.stat.db.JdbcTemplateMapping;
import com.mfp.pgxl.stat.utils.AllInOne;

@Service
public class StatisticsService {
	
    @Autowired
    private JdbcTemplateMapping jdbcTemplateMapping;
    
    @PostConstruct
    public void postConstruct(){
        
    }
    
    public Object[] tpu(String node, String beginTime, String endTime, String unit){
    	if(AllInOne.isAllNull(node, beginTime, endTime, unit)){
    		return null;
    	}
    	
    	String sql;
    	if("second".equals(unit)){
    		sql = "select dt sdt, sum(tps) tpu from stat_statements_tps as v1 where ";
    	}else if("minute".equals(unit)){
    		sql = "select sdt, sum(tpu) tpu from (select node, strftime('%Y-%m-%d %H:%M', dt) sdt, sum(tps)/count(dt)*60 tpu from stat_statements_tps group by node, sdt) as v1 where ";
    	}else if("hour".equals(unit)){
    		sql = "select sdt, sum(tpu) tpu from (select node, strftime('%Y-%m-%d %H', dt) sdt, sum(tps)/count(dt)*3600 tpu from stat_statements_tps group by node, sdt) as v1 where ";
    	}else{
    		sql = "select sdt, sum(tpu) tpu from (select node, strftime('%Y-%m-%d', dt) sdt, sum(tps)/count(dt)*3600*24 tpu from stat_statements_tps group by node, sdt) as v1 where ";
    	}
    	
    	sql += "node like '" + node + "%' and sdt >= '" + beginTime + "' and sdt <= '" + endTime + "' group by sdt order by sdt asc";
    	
    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	
    	List<Map<String, Object>> list = sqlite.queryForList(sql);
    	
    	Object[] rs = new Object[2];
    	List<String> xlist = new ArrayList<>();
    	List<Number> data = new ArrayList<>();
    	rs[0] = xlist;
    	rs[1] = data;
    	
    	for(Map<String, Object> map: list){
    		String dt = String.valueOf(map.get("sdt"));
    		int tps = (int)map.get("tpu");
    		xlist.add(dt);
    		data.add(tps);
    	}
    	return rs;
    }
    
    public List<Map<String, Object>> top_sql(String node, String[] rolnames, String[] dbs, String order_col, String asc_or_desc){
    	String sql = "select rolname, dbname, sql, sum(call_count) call_count, round(sum(total_time), 2) total_time, "
    			+ "round(sum(blk_read_time), 2) blk_read_time, round(sum(blk_write_time), 2) blk_write_time, "
    			+ "sum(shared_blks_hit) shared_blks_hit, sum(shared_blks_dirtied) shared_blks_dirtied, "
    			+ "round(total_time/call_count, 1) avg_time, "
    			+ "round(sum(stddev_time)/count(node), 1) stddev_time, "
    			+ "round((blk_read_time + blk_write_time)/call_count, 1) avg_io_time "
    			+ "from snap_pg_stat_statements where ";
    	
    	if(node != null){
    		sql += "node like '" + node + "%' and ";
    	}
    	
    	if(rolnames != null && rolnames.length != 0){
    		sql += "rolname in (";
    		for(String rolname: rolnames){
    			sql += "'" + rolname + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	if(dbs != null && dbs.length != 0){
    		sql += "dbname in (";
    		for(String db: dbs){
    			sql += "'" + db + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	sql += " 1 = 1 group by dbname, rolname, sql ";
    	
    	if(order_col != null){
    		sql += "order by " + order_col;
    	}
    	
    	if(asc_or_desc != null){
    		sql += " " + asc_or_desc;
    	}

    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	List<Map<String, Object>> list = sqlite.queryForList(sql);
    	
    	return list;
    }
    
    public List<Map<String, Object>> activity(String node, String[] dbs, String order_col, String asc_or_desc){
    	String sql = "select node, xact_time, query_time, sql, state, waiting, client_addr, "
    			+ "pid, datname, relname, locktype, mode, granted "
    			+ "from snap_pg_stat_activity where ";
    	
    	if(node != null){
    		sql += "node like '" + node + "%' and ";
    	}
    	
    	if(dbs != null && dbs.length != 0){
    		sql += "datname in (";
    		for(String db: dbs){
    			sql += "'" + db + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	sql += " 1 = 1 ";
    	
    	if(order_col != null){
    		sql += "order by " + order_col;
    	}
    	
    	if(asc_or_desc != null){
    		sql += " " + asc_or_desc;
    	}

    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	List<Map<String, Object>> list = sqlite.queryForList(sql);
    	
    	return list;
    }
    
    public List<Map<String, Object>> table_stat(String node, String[] dbs, String[] schemas, String order_col, String asc_or_desc){
    	String sql = "select db, schemaname, tablename, sum(table_size) table_size, sum(indexes_size) indexes_size, "
    			+ "sum(total_size) total_size, sum(seq_scan) seq_scan, sum(seq_tup_read) seq_tup_read, sum(idx_scan) idx_scan, "
    			+ "sum(idx_tup_fetch) idx_tup_fetch, sum(n_tup_ins) n_tup_ins, sum(n_tup_upd) n_tup_upd, sum(n_tup_del) n_tup_del, "
    			+ "sum(n_tup_hot_upd) n_tup_hot_upd, sum(tups) tups, sum(n_live_tup) n_live_tup, sum(n_dead_tup) n_dead_tup, "
    			+ "sum(pages) pages, sum(otta) otta, sum(wastedbytes) wastedbytes, sum(autovacuum_count) autovacuum_count, "
    			+ "sum(autoanalyze_count) autoanalyze_count, "
    			+ "round(case when otta=0 then 0.0 else pages*1.0/otta end, 1) bloat, "
    			+ "(case when seq_scan=0 then 0 else seq_tup_read/seq_scan end) mean_read "
    			+ "from stat_table where ";
    	
    	if(node != null && "all".equalsIgnoreCase(node) == false){
    		sql += "node like '" + node + "%' and ";
    	}
    	
    	if(dbs != null && dbs.length != 0){
    		sql += "db in (";
    		for(String db: dbs){
    			sql += "'" + db + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	if(schemas != null && schemas.length != 0){
    		sql += "schemaname in (";
    		for(String schema: schemas){
    			sql += "'" + schema + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	sql += " 1 = 1 group by db, schemaname, tablename ";
    	
    	if(order_col != null){
    		sql += "order by " + order_col;
    	}
    	
    	if(asc_or_desc != null){
    		sql += " " + asc_or_desc;
    	}

    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	List<Map<String, Object>> list = sqlite.queryForList(sql);
    	
    	return list;
    }
    
    public List<Map<String, Object>> index_stat(String node, String[] dbs, String[] schemas, String order_col, String asc_or_desc){
    	String sql = "select db, schemaname, tablename, idxname, indexdef, sum(idx_size) idx_size, sum(idx_scan) idx_scan, "
    			+ "sum(idx_tup_read) idx_tup_read, sum(idx_tup_fetch) idx_tup_fetch, sum(itups) itups, sum(ipages) ipages, "
    			+ "sum(iotta) iotta, sum(iwastedsize) iwastedsize, "
    			+ "round(case when iotta=0 then 0.0 else ipages*1.0/iotta end, 1) ibloat, "
    			+ "(case when idx_scan=0 then 0 else idx_tup_read/idx_scan end) mean_iread "
    			+ "from stat_index where ";
    	
    	if(node != null && "all".equalsIgnoreCase(node) == false){
    		sql += "node like '" + node + "%' and ";
    	}
    	
    	if(dbs != null && dbs.length != 0){
    		sql += "db in (";
    		for(String db: dbs){
    			sql += "'" + db + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	if(schemas != null && schemas.length != 0){
    		sql += "schemaname in (";
    		for(String schema: schemas){
    			sql += "'" + schema + "',";
    		}
    		sql += "'') and ";
    	}
    	
    	sql += " 1 = 1 group by db, schemaname, tablename, idxname, indexdef ";
    	
    	if(order_col != null){
    		sql += "order by " + order_col;
    	}
    	
    	if(asc_or_desc != null){
    		sql += " " + asc_or_desc;
    	}

    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	List<Map<String, Object>> list = sqlite.queryForList(sql);
    	
    	return list;
    }
    
    public List<Map<String, Object>> distinct_name(String table, String column){
    	String sql = "select distinct " + column + " from " + table;
    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	List<Map<String, Object>> list = sqlite.queryForList(sql);
    	return list;
    }
    
    public void saveDefaultConditions(String name, String condition){
    	String insert = "insert into default_condition values (?,?)";
    	String delete = "delete from default_condition where name = '" + name + "'";
    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	try {
    		sqlite.update(delete);
    		sqlite.update(new PreparedStatementCreator(){
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(insert);
					ps.setString(1, name);
					ps.setString(2, condition);
					return ps;
				}
    		});
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public String findDefaultConditions(String name){
    	String sql = "select condition from default_condition where name = '" + name + "'";
    	JdbcTemplate sqlite = jdbcTemplateMapping.getSqliteJdbcTemplate();
    	List<Map<String, Object>> rs = sqlite.queryForList(sql);
    	if(rs == null || rs.size() == 0){
    		return null;
    	}
    	return (String)rs.get(0).get("condition");
    }
    
}
