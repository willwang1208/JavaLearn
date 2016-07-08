package org.whb.postgres.tools.presstest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 压力测试PostgreSQL，集合的_id必须是连续的正整数
 * java -jar PressTest.jar jdbc:postgresql://172.16.0.3:5432/mydb3 postgres postgres user_data find_first 10 1 >> press_test.out 2>&1
 * 
 * @author whb
 *
 */
public class PressTest {
    
    public static void main(String[] args) {
        String driverClass = "org.postgresql.Driver";
        String url = args[0];
        String user = args[1];
        String password = args[2];
        String tableName = args[3];
        String method = args[4];
        int threadCount = Integer.parseInt(args[5]);
        int poolSize = threadCount;
        int interval = Integer.parseInt(args[6]);   //采集时间间隔，单位秒，0表示不监视
        
        PostgresqlClient client = new PostgresqlClient(driverClass, url, user, password, poolSize);
        client.initialize();
        
        try {
            PostgresqlOperator operator = new PostgresqlOperator(client, tableName);
            exec(operator, method, threadCount, interval);
//          exec(operator, "find_first_not_ps", 10, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        Runtime.getRuntime().addShutdownHook(
            new Thread(){
                public void run(){
                    client.close();
                }
            }
        );
    }

    public static void exec(PostgresqlOperator operator, String methodName, int threadCount, int interval) {
        List<PressJob> jobs = new ArrayList<>();
        for(int i = 0; i < threadCount; i ++){
            PressJob pressJob = null;
            if("find".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find();
                    }
                });
            }else if("find_first".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_first();
                    }
                });
            }else if("find_insert_one".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_insert_one();
                    }
                });
            }else if("find_insert_many".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_insert_many();
                    }
                });
            }else if("find_update_one".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_update_one();
                    }
                });
            }else if("find_delete_one".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_delete_one();
                    }
                });
            }else if("find_in".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_in();
                    }
                });
            }else if("find_first_not_ps".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_first_not_ps();
                    }
                });
            }else if("find_update_one_not_ps".equals(methodName)){
                pressJob = new PressJob(new PressJob.PostgresqlOperatorAdapter() {
                    @Override
                    public void execute() throws SQLException {
                        operator.find_update_one_not_ps();
                    }
                });
            }
            
            if(pressJob != null){
                new Thread(pressJob).start();
                jobs.add(pressJob);
            }
        }
        
        if(interval > 0 && jobs.size() > 0){
            new Thread(new WatchJob(methodName, jobs, interval)).start();
        }
    }
}
