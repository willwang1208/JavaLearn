package com.mfp.pgxl.stat.db;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mfp.pgxl.stat.utils.AllInOne;

@Repository
public class JdbcTemplateMapping {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private DataSourceManager dataSourceManager;
	
	private Map<String, DataSource> sources = new HashMap<>();
    	
	private Map<String, JdbcTemplate> templates = new HashMap<String, JdbcTemplate>();
    
    @PostConstruct
    public void postConstruct() {
    	System.out.println(">> Init JdbcTemplate.");
    	
    	String[] coords = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.coord.names"), "").split(",");
    	String[] datanodes = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.dn.names"), "").split(",");
    	String[] bzdbs = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.business.dbnames"), "").split(",");
    	String[] dbs = new String[bzdbs.length + 1];
    	System.arraycopy(bzdbs, 0, dbs, 0, bzdbs.length);
    	System.arraycopy(new String[]{env.getProperty("pgdb.stat.dbname")}, 0, dbs, bzdbs.length, 1);
    	
    	for(String db: dbs){
        	for(String coord: coords){
        		initPgdb(coord, db);
        	}
        	for(String datanode: datanodes){
        		initPgdb(datanode, db);
        	}
    	}
    	
    	if(env.getProperty("sqlite.name") != null){
    		initSqlite(env.getProperty("sqlite.name"));
    	}
    }
    
    @PreDestroy
    public void preDestroy() {
        for(DataSource source: sources.values()){
            dataSourceManager.closeDataSource(source);
        }
        sources.clear();
    }
    
    private void initPgdb(String node, String db){
    	String driver = env.getProperty("pgdb.driver");
    	String user = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb." + node + ".user"), env.getProperty("pgdb.user"));
    	String password = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb." + node + ".password"), env.getProperty("pgdb.password"));
    	int pool_size = AllInOne.parseNumberIfErrorThenDefault(
    			Integer.class, 
    			AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb." + node + ".pool_size"), env.getProperty("pgdb.pool_size")), 
    			8);
		String url = env.getProperty("pgdb." + node + ".url") + db;
    	
		DataSource source = dataSourceManager.createDataSource(driver, url, user, password, pool_size);
		if(source != null){
			sources.put(getTemplateKey(node, db), source);
			
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(source);
            templates.put(getTemplateKey(node, db), template);
		}
    }
    
    private void initSqlite(String name){
    	String driver = env.getProperty("sqlite.driver");
    	int pool_size = Integer.parseInt(env.getProperty("sqlite.pool_size"));
		String url = env.getProperty("sqlite.url");
    	
		DataSource source = dataSourceManager.createDataSource(driver, url, null, null, pool_size);
		if(source != null){
			sources.put(name, source);
			
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(source);
            templates.put(name, template);
		}
    }
    
    public JdbcTemplate getJdbcTemplate(String key){
        return templates.get(key);
    }
    
    public Map<String, JdbcTemplate> getCoordStatJdbcTemplates(){
    	String db = env.getProperty("pgdb.stat.dbname");
    	Map<String, JdbcTemplate> rs = new HashMap<>();
    	if(env.getProperty("pgdb.coord.names") != null){
    		String[] coords = env.getProperty("pgdb.coord.names").split(",");
        	for(String coord: coords){
        		rs.put(getTemplateKey(coord, db), getJdbcTemplate(getTemplateKey(coord, db)));
        	}
    	}
    	return rs;
    }
    
    public Map<String, JdbcTemplate> getDatanodeStatJdbcTemplates(){
    	String db = env.getProperty("pgdb.stat.dbname");
    	Map<String, JdbcTemplate> rs = new HashMap<>();
    	if(env.getProperty("pgdb.dn.names") != null){
    		String[] datanodes = env.getProperty("pgdb.dn.names").split(",");
        	for(String datanode: datanodes){
        		rs.put(getTemplateKey(datanode, db), getJdbcTemplate(getTemplateKey(datanode, db)));
        	}
    	}
        return rs;
    }
    
    public Map<String, JdbcTemplate> getDatanodeBusinessJdbcTemplates(){
    	Map<String, JdbcTemplate> rs = new HashMap<>();
    	if(env.getProperty("pgdb.dn.names") != null){
    		String[] datanodes = env.getProperty("pgdb.dn.names").split(",");
        	String[] bzdbs = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.business.dbnames"), "").split(",");
        	for(String db: bzdbs){
            	for(String datanode: datanodes){
            		rs.put(getTemplateKey(datanode, db), getJdbcTemplate(getTemplateKey(datanode, db)));
            	}
        	}
    	}
        return rs;
    }
    
    public JdbcTemplate getSqliteJdbcTemplate(){
    	return templates.get(env.getProperty("sqlite.name"));
    }

    public String getTemplateKey(String node, String db){
    	return node + "/" + db;
    }
    
    public String[] splitTemplateKey(String key){
    	return key.split("/");
    }
    
}
