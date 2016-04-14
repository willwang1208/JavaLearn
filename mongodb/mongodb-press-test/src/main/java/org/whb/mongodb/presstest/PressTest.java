package org.whb.mongodb.presstest;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * java -jar MongodbPressTest.jar "find_first" 10 100000 >> press_test.out 2>&1
 * java -jar MongodbPressTest.jar "find_first" 10 100000 >> /dev/null 2>&1
 * 
 * @author whb
 *
 */
public class PressTest {
    
    public static String uri = "mongodb://127.0.0.1:20000/?maxPoolSize=";
    
    public static String db = "jelly_ios";

    public static void main(String[] args) {
        String method = args[0];
        Integer thread = Integer.parseInt(args[1]);
        Integer loop = Integer.parseInt(args[2]);
        
        final MongoClient client = new MongoClient(new MongoClientURI(uri + thread));
        
        MongoDatabase database = client.getDatabase(db);
        
        exec(database, method, thread, loop);
        
//        exec(database, "find_first", 10, 10000);
//        exec(database, "find", 10, 100);
//        exec(database, "find_insert_one", 10, 100000);
//        exec(database, "find_insert_many", 10, 10000);
//        exec(database, "find_update_one", 10, 100000);
//        exec(database, "find_delete_one", 10, 1000);
        
        Runtime.getRuntime().addShutdownHook(
            new Thread(){
                public void run(){
                    client.close();
                }
            }
        );
    }

    public static void exec(final MongoDatabase database, final String methodName, int threadCount, final int loopCountPerThread) {
        final MongodbOperator operator = new MongodbOperator();
        for(int i = 0; i < threadCount; i ++){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    for(int j = 0; j < loopCountPerThread; j ++){
                        try {
                            if("find".equals(methodName)){
                                operator.find(database);
                            }else if("find_first".equals(methodName)){
                                operator.find_first(database);
                            }else if("find_insert_one".equals(methodName)){
                                operator.find_insert_one(database);
                            }else if("find_insert_many".equals(methodName)){
                                operator.find_insert_many(database);
                            }else if("find_update_one".equals(methodName)){
                                operator.find_update_one(database);
                            }else if("find_delete_one".equals(methodName)){
                                operator.find_delete_one(database);
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
