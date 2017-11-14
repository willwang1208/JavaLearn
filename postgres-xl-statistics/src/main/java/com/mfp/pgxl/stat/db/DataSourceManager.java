package com.mfp.pgxl.stat.db;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mfp.pgxl.stat.utils.AllInOne;

@Repository
public class DataSourceManager {
	
	@Autowired
	private Environment env;
	
	private DataSourceType dataSourceType;
	
    @PostConstruct
    public void postConstruct() {
    	String type = AllInOne.getValueIfNullThenDefault(env.getProperty("sql.datasource"), "druid");
    	
    	System.out.println(">> Init DataSourceManager. " + type);
    	
    	if("druid".equals(type)){
    		dataSourceType = DataSourceType.DRUID;
    	}else if("c3p0".equals(type)){
    		dataSourceType = DataSourceType.C3P0;
    	}
    }
    
    @PreDestroy
    public void preDestroy() {
    	
    }
    
    public DataSource createDataSource(String driverClass, String url, String user, String password, int poolSize){
    	switch(dataSourceType){
	    	case DRUID:
	    		return createDruidDataSource(driverClass, url, user, password, poolSize);
	    	case C3P0:
	    		return createComboPooledDataSource(driverClass, url, user, password, poolSize);
    	}
    	return null;
    }
    
    public void closeDataSource(DataSource source){
    	if(source instanceof DruidDataSource){
    		((DruidDataSource) source).close();
    	}else if(source instanceof ComboPooledDataSource){
    		((ComboPooledDataSource) source).close();
    	}
    }
    
    protected DataSource createDruidDataSource(String driverClass, String url, String user, String password, int poolSize){
    	DruidDataSource source = null;
        try {
            source = new DruidDataSource();
            
            source.setMaxActive(poolSize);
            source.setBreakAfterAcquireFailure(false);
            source.setInitialSize(poolSize);
            source.setMaxOpenPreparedStatements(poolSize * 5);
            source.setMinIdle(poolSize);
            source.setPoolPreparedStatements(false);
            
            source.setUsername(user);
            source.setPassword(password);
            source.setDriverClassName(driverClass);
            source.setUrl(url);
            
        } catch (Exception e) {
        	closeDataSource(source);
            source = null;
            e.printStackTrace();
        }
        return source;
    }
    
    protected DataSource createComboPooledDataSource(String driverClass, String url, String user, String password, int poolSize){
    	ComboPooledDataSource source = null;
        try {
            source = new ComboPooledDataSource();
            
            source.setInitialPoolSize(poolSize);
            source.setMaxPoolSize(poolSize);
            source.setMinPoolSize(poolSize);
            source.setMaxIdleTime(60);
            source.setAcquireIncrement(2);
            source.setMaxStatements(poolSize * 5);    //缓存的Statement数量
            source.setIdleConnectionTestPeriod(1800);
            source.setAcquireRetryAttempts(2);
            source.setBreakAfterAcquireFailure(false);
            source.setTestConnectionOnCheckout(false);
            
            source.setUser(user);
            source.setPassword(password);
            source.setDriverClass(driverClass);
            source.setJdbcUrl(url);
            
        } catch (Exception e) {
        	closeDataSource(source);
            source = null;
            e.printStackTrace();
        }
        return source;
    }

    enum DataSourceType {
    	C3P0, DRUID
    }
}
