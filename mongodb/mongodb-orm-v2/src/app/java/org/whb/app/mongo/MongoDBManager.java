package org.whb.app.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.whb.common.mongo.OrmMongoBuilder;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * mongodb数据源管理
 * @author
 *
 */
@Repository
public class MongoDBManager {

    @Autowired
    @Qualifier("mongoConfig")
    private Properties properties;

    private MongoClient mongo0;

    private Map<String, MongoClient> mongocs = new HashMap<String, MongoClient>();

    private DB db0;
    
    private Map<String, DB> dbs = new HashMap<String, DB>();
    
    @PostConstruct
    public void postConstruct() {
        try {
            //只接受0到99编号
            for (int i = 0; i < 100; i++) {
                String routers = properties.getProperty("mongodb." + i + ".router.url");
                String dbnames = properties.getProperty("mongodb." + i + ".dbname");
                if (routers != null && dbnames != null) {
                    String[] routerArray = routers.split(";");
                    String[] dbnameArray = dbnames.split(";");
                    if(routerArray.length > 0 && dbnameArray.length > 0){
                        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
                        for(String router: routerArray){
                            ServerAddress address = new ServerAddress(router);
                            addresses.add(address);
                        }
                        
                        String key = properties.getProperty("mongodb." + i + ".pk", String.valueOf(i));
                        int connections = Integer.parseInt(properties.getProperty("mongodb." + i + ".connections", "100"));
                        
                        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
                        builder.connectionsPerHost(connections);
                        MongoClientOptions options = builder.build();
                        
                        //创建MongoClient
                        MongoClient mongo = new MongoClient(addresses, options);
                        mongocs.put(key, mongo);
                        if(mongo0 == null){
                            mongo0 = mongo;
                        }
                        
                        //创建DB
                        for(String dbname: dbnameArray){
                            DB db = mongo.getDB(dbname);
                            //m0.jelly_ios
                            dbs.put(key + "." + dbname, db);
                            if(db0 == null){
                                db0 = db;
                            }
                        }
                    }
                }
            }
            
            //db0作为主库，主库初始化ORM、分片、索引等
            OrmMongoBuilder omb_local = new OrmMongoBuilder(true, "com.mfp.app.vo.mongo", getDb());
            omb_local.build();
            //元数据入库
            
            //创建函数
//          omb_local.createFunction("getNextSequence", "function (name) { "
//                  + "var ret = db.counters.findAndModify({query: { _id: name }, update: { $inc: { seq: 1 } }, new: true, upsert: true}); "
//                  + "return ret.seq; "
//                  + "}");
            
            //复杂索引、子集索引
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    @PreDestroy
    public void preDestroy() {
        for(MongoClient mc: mongocs.values()){
            if(mc != null){
                try {
                    mc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MongoClient getMongo() {
        return mongo0;
    }

    public DB getDb() {
        return db0;
    }

    public MongoClient getMongo(String key) {
        return mongocs.get(key);
    }

    public DB getDb(String key) {
        return dbs.get(key);
    }
}
