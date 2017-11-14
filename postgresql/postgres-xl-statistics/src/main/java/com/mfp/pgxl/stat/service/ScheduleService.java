package com.mfp.pgxl.stat.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mfp.pgxl.stat.db.JdbcTemplateMapping;
import com.mfp.pgxl.stat.schedule.ScheduledTaskExecutor;
import com.mfp.pgxl.stat.service.task.FormatCurrentTime;
import com.mfp.pgxl.stat.service.task.MaintainSqlite;
import com.mfp.pgxl.stat.service.task.ResetPgStat;
import com.mfp.pgxl.stat.service.task.ResetPgStatStatements;
import com.mfp.pgxl.stat.service.task.SnapPgStatActivity;
import com.mfp.pgxl.stat.service.task.SnapPgStatStatements;
import com.mfp.pgxl.stat.service.task.StatIndexes;
import com.mfp.pgxl.stat.service.task.StatTables;
import com.mfp.pgxl.stat.service.task.StatTransactionsInUnitTime;
import com.mfp.pgxl.stat.service.task.WriteStatToSqlite;
import com.mfp.pgxl.stat.utils.AllInOne;

/**
 * 
 * @author MFP
 *
 */
@Service
public class ScheduleService {

	private final static Logger logger = LoggerFactory.getLogger(ScheduleService.class);

	@Autowired
	private JdbcTemplateMapping jdbcTemplateMapping;

	private ScheduledTaskExecutor executor;

	private LinkedBlockingQueue<Object> updateSqlQueue;

	@Autowired
	private Environment env;

	/**
	 * 初始化任务和状态
	 */
	@PostConstruct
	public void postConstruct() {
		logger.info("init schedule tasks");
		updateSqlQueue = new LinkedBlockingQueue<>();
		// 启动StatWriter作为update sql的消费者
		// 为避免并发连接产生 db file lock的错误，采用阻塞队列来做。或者修改连接池连接数为1
		Thread thread = new Thread(new WriteStatToSqlite(jdbcTemplateMapping.getSqliteJdbcTemplate(), updateSqlQueue));
		thread.setDaemon(true);
		thread.start();

		executor = new ScheduledTaskExecutor(64);

		// 注册任务
		startAllTasks();
	}

	/**
	 * 销毁前结束任务
	 */
	@PreDestroy
	public void preDestroy() {
		executor.preDestroy();
	}

	protected void startAllTasks() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date beginTime = null;
		try {
			beginTime = formatter.parse("2017-01-01 00:00:00");
		} catch (Exception e) {
		}
		
		Map<String, JdbcTemplate> coordTemplates = jdbcTemplateMapping.getCoordStatJdbcTemplates();
		Map<String, JdbcTemplate> dnTemplates = jdbcTemplateMapping.getDatanodeStatJdbcTemplates();
		Map<String, JdbcTemplate> allTemplates = new HashMap<>();
		allTemplates.putAll(coordTemplates);
		allTemplates.putAll(dnTemplates);
		Map<String, JdbcTemplate> dnBzTemplates = jdbcTemplateMapping.getDatanodeBusinessJdbcTemplates();

		// 格式化当前时间
		executor.registerTask(new FormatCurrentTime(
				AllInOne.getNextTaskId(), beginTime, 
				AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.FormatCurrentTime.period"), 1)));

		// 基于pg_stat_statments计算TPS、TPM、TPD
		for (Entry<String, JdbcTemplate> entry : allTemplates.entrySet()) {
			executor.registerTask(new StatTransactionsInUnitTime(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.StatTransactionsInUnitTime.period"), 2),
					entry.getKey(), entry.getValue(), updateSqlQueue));
		}

		// Sqlite维护
		executor.registerTask(new MaintainSqlite(
				AllInOne.getNextTaskId(), beginTime, 
				AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.MaintainSqlite.period"), 86400),
				updateSqlQueue));
		
		// 重置pg_stat_statments
		for (Entry<String, JdbcTemplate> entry : allTemplates.entrySet()) {
			executor.registerTask(new ResetPgStatStatements(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.ResetPgStatStatements.period"), 86400),
					entry.getKey(), entry.getValue()));
		}
		
		// 重置pg_stat_*
		for (Entry<String, JdbcTemplate> entry : dnBzTemplates.entrySet()) {
			executor.registerTask(new ResetPgStat(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.ResetPgStat.period"), 86400),
					entry.getKey(), entry.getValue()));
		}
		
		// 基于pg_stat_statments的TOP-SQL快照
		for (Entry<String, JdbcTemplate> entry : allTemplates.entrySet()) {
			executor.registerTask(new SnapPgStatStatements(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.SnapPgStatStatements.period"), 60),
					entry.getKey(), entry.getValue(), updateSqlQueue));
		}
		
		// 基于pg_stat_all_tables的表状态
		for (Entry<String, JdbcTemplate> entry : dnBzTemplates.entrySet()) {
			executor.registerTask(new StatTables(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.StatTables.period"), 60),
					entry.getKey(), entry.getValue(), updateSqlQueue));
		}
		
		// 基于pg_stat_all_indexes的索引状态
		for (Entry<String, JdbcTemplate> entry : dnBzTemplates.entrySet()) {
			executor.registerTask(new StatIndexes(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.StatIndexes.period"), 60),
					entry.getKey(), entry.getValue(), updateSqlQueue));
		}
		
		// 基于pg_stat_activity和pg_locks的运行时间较长的连接
		for (Entry<String, JdbcTemplate> entry : allTemplates.entrySet()) {
			executor.registerTask(new SnapPgStatActivity(
					AllInOne.getNextTaskId(), beginTime,
					AllInOne.parseNumberIfErrorThenDefault(Integer.class, env.getProperty("task.SnapPgStatActivity.period"), 10),
					entry.getKey(), entry.getValue(), updateSqlQueue));
		}
		
	}

}
