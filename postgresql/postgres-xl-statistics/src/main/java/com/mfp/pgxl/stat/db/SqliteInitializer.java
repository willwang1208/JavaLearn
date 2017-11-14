package com.mfp.pgxl.stat.db;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mfp.pgxl.stat.utils.AllInOne;

@Repository
public class SqliteInitializer {
	
	@Autowired
	private Environment env;
	
	@Autowired
	JdbcTemplateMapping jdbcTemplateMapping;
	
	@PostConstruct
    public void postConstruct() {
		init();
	}
	
    public void init() {
		String url = env.getProperty("sqlite.url");
		String fileName = url.substring(url.lastIndexOf(':') + 1, url.length());
		System.out.println("Sqlite DB: " + fileName);
		File file = new File(fileName);
		if(file.exists() == false){
			Resource resource = new ClassPathResource("sql/sqlite_init.sql");
			
			List<String> sqls = null;
			try {
				sqls = AllInOne.getSqlFromInputStream(resource.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			if(sqls != null){
				JdbcTemplate template = jdbcTemplateMapping.getSqliteJdbcTemplate();
				for (String sql: sqls) {
					System.out.println("Sqlite SQL: " + sql);
			        template.update(sql);
				}
			}
		}
	}
}
