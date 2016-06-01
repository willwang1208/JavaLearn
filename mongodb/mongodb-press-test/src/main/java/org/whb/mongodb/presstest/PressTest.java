package org.whb.mongodb.presstest;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * 集合的_id必须是连续的正整数
 * java -jar MongodbPressTest.jar 127.0.0.1:20000 jelly_ios user_data find_first 10 100000 >> press_test.out 2>&1
 * java -jar MongodbPressTest.jar 127.0.0.1:20000 jelly_ios user_data find_first 10 100000 >> /dev/null 2>&1
 * 
 * @author whb
 *
 */
public class PressTest {
    
    public static void main(String[] args) {
        String host = args[0];
        String db = args[1];
        String collection = args[2];
        String method = args[3];
        int thread = Integer.parseInt(args[4]);
        int loop = Integer.parseInt(args[5]);
        
        String uri = "mongodb://" + host + "/?maxPoolSize=" + thread;
        
        final MongoClient client = new MongoClient(new MongoClientURI(uri));
        
        final MongodbOperator operator = new MongodbOperator(client, db, collection);
        
        exec(operator, method, thread, loop);
        
//        exec(operator, "find_first", 10, 10000);
//        exec(operator, "find", 10, 100);
//        exec(operator, "find_insert_one", 10, 100000);
//        exec(operator, "find_insert_many", 10, 10000);
//        exec(operator, "find_update_one", 10, 100000);
//        exec(operator, "find_delete_one", 10, 1000);
        
        Runtime.getRuntime().addShutdownHook(
            new Thread(){
                public void run(){
                    client.close();
                }
            }
        );
    }

    public static void exec(final MongodbOperator operator, final String methodName, int threadCount, final int loopCountPerThread) {
        for(int i = 0; i < threadCount; i ++){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    for(int j = 0; j < loopCountPerThread; j ++){
                        try {
                            if("find".equals(methodName)){
                                operator.find();
                            }else if("find_first".equals(methodName)){
                                operator.find_first();
                            }else if("find_insert_one".equals(methodName)){
                                operator.find_insert_one();
                            }else if("find_insert_many".equals(methodName)){
                                operator.find_insert_many();
                            }else if("find_update_one".equals(methodName)){
                                operator.find_update_one();
                            }else if("find_delete_one".equals(methodName)){
                                operator.find_delete_one();
                            }else if("find_in".equals(methodName)){
                                operator.find_in();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
    }
}
