package org.whb.app.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.whb.common.util.ValidateUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
/**
 * 多源mongo数据访问类
 * @author
 *
 */
@Repository
public class MongoDAO{
    
    @Autowired
    MongoDBManager mongoDBManager;
    
    @PostConstruct
    public void postConstruct() {

    }
    
    public DB getDB(String dbk){
        return mongoDBManager.getDb(dbk);
    }
    
    public DBCollection getCollection(String dbk, String name) {
        return getDB(dbk).getCollection(name);
    }
    
    public String loginMongo(String key, String dbname){
        String dbk = key + "." + dbname;
        if(mongoDBManager.getDb(dbk) != null){
            return dbk;
        }
        return null;
    }
    
    public List<String> showCollections(String dbk){
        DB db = getDB(dbk);
        
        List<String> names = new ArrayList<String>();
        
        Set<String> set = db.getCollectionNames();
        for(String name: set){
            names.add(name);
        }
        
        Collections.sort(names);
        
        return names;
    }
    
    public Map<String, Object> selectPagination(String dbk, Map<String, Object> params){
        //表名
        String table = String.valueOf(params.get("tableName"));
        
        //查询条件
        String qjson = "{}";
        if(ValidateUtil.isNotNull(params.get("where"))){
            qjson = String.valueOf(params.get("where"));
        }
        
        int numToSkip = 0;
        if(ValidateUtil.isNotNull(params.get("pageSize")) && ValidateUtil.isNotNull(params.get("pageCurrent"))){
            numToSkip = Integer.parseInt(String.valueOf(params.get("pageSize"))) * (Integer.parseInt(String.valueOf(params.get("pageCurrent"))) - 1);
        }
        
        int batchSize = 200;
        if(ValidateUtil.isNotNull(params.get("pageSize"))){
            batchSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
        }
        
        List<Map<String, Object>> list = new ArrayList<>();
        
        DBCursor cursor = find(dbk, table, qjson, numToSkip, batchSize);
        while(cursor.hasNext()){
            list.add((BasicDBObject)cursor.next());
        }
        cursor.close();
        
        long count = count(dbk, table, qjson);
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("list", list);
        result.put("count", count);
        
        return result;
    }
    
    public Map<String, Object> selectOne(String dbk, Map<String, Object> params){
        //表名
        String table = String.valueOf(params.get("tableName"));
        //主键值
        String pkValue = String.valueOf(params.get("pkValue"));
        //主键类型
        String pkType = String.valueOf(params.get("pkType"));
        
        if(ValidateUtil.isOneNullAtLeast(table, pkValue, pkType)){
            return null;
        }
        
        String qjson = null;
        if("java.lang.Integer".equals(pkType)
                || "java.lang.Long".equals(pkType)
                || "java.lang.Double".equals(pkType)){
            qjson = "{_id:" + pkValue + "}";
        }else if("org.bson.types.ObjectId".equals(pkType)){
            qjson = "{_id: { \"$oid\" : \"" + pkValue + "\"}}";
        }else{
            qjson = "{_id:\"" + pkValue + "\"}";
        }
        
        return (BasicDBObject)findOne(dbk, table, qjson);
    }
    
    public DBObject findOne(String dbk, String name, String qjson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.findOne((BasicDBObject)JSON.parse(qjson));
    }
    
    public DBObject findOne(String dbk, String name, Map<String, Object> qmap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.findOne(new BasicDBObject(qmap));
    }
    
    public DBObject tryFindOne(String dbk, String name, String qjson){
        try {
            return findOne(dbk, name, qjson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public DBObject tryFindOne(String dbk, String name, Map<String, Object> qmap){
        try {
            return findOne(dbk, name, qmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public DBCursor find(String dbk, String name, String qjson, int numToSkip, int batchSize){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.find((BasicDBObject)JSON.parse(qjson)).skip(numToSkip).limit(batchSize);
    }
    
    public DBCursor find(String dbk, String name, Map<String, Object> qmap, int numToSkip, int batchSize){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.find(new BasicDBObject(qmap)).skip(numToSkip).limit(batchSize);
    }
    
    public DBCursor find(String dbk, String name, String qjson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.find((BasicDBObject)JSON.parse(qjson));
    }
    
    public DBCursor find(String dbk, String name, Map<String, Object> qmap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.find(new BasicDBObject(qmap));
    }
    
    public long count(String dbk, String name, String qjson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.count((BasicDBObject)JSON.parse(qjson));
    }
    
    public long count(String dbk, String name, Map<String, Object> qmap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.count(new BasicDBObject(qmap));
    }
    
    public WriteResult update(String dbk, String name, String qjson, String ujson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.update((BasicDBObject)JSON.parse(qjson), (BasicDBObject)JSON.parse(ujson));
    }
    
    public WriteResult update(String dbk, String name, Map<String, Object> qmap, Map<String, Object> umap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.update(new BasicDBObject(qmap), new BasicDBObject(umap));
    }
    
    public WriteResult save(String dbk, String name, String qjson, String ujson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.update((BasicDBObject)JSON.parse(qjson), (BasicDBObject)JSON.parse(ujson), true, true);
    }
    
    public WriteResult save(String dbk, String name, Map<String, Object> qmap, Map<String, Object> umap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.update(new BasicDBObject(qmap), new BasicDBObject(umap), true, true);
    }
    
    public WriteResult insert(String dbk, String name, String ijson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.insert((BasicDBObject)JSON.parse(ijson));
    }
    
    public WriteResult insert(String dbk, String name, Map<String, Object> imap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.insert(new BasicDBObject(imap));
    }
    
    public WriteResult remove(String dbk, String name, String qjson){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.remove((BasicDBObject)JSON.parse(qjson));
    }
    
    public WriteResult remove(String dbk, String name, Map<String, Object> qmap){
        DBCollection dbc = getCollection(dbk, name);
        return dbc.remove(new BasicDBObject(qmap));
    }
}
