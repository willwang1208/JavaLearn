package org.whb.postgres.tools.presstest;

import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * c3p0连接池
 * @author whb
 *
 */
public class PostgresqlClient {
    
    private String driverClass;
    
    private String url;
    
    private String user;
    
    private String password;
    
    private int poolSize;
    
    private ComboPooledDataSource source;
    
    public PostgresqlClient(String driverClass, String url, String user, String password, int poolSize) {
        super();
        this.driverClass = driverClass;
        this.url = url;
        this.user = user;
        this.password = password;
        this.poolSize = poolSize;
    }

    public void initialize(){
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
            close();
            source = null;
            e.printStackTrace();
        }
        this.source = source;
    }
    
    public ComboPooledDataSource getDataSource(){
        return source;
    }
    
    public void close(){
        if(source != null){
            try {
                source.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
