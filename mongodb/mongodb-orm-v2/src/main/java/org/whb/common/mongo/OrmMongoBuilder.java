package org.whb.common.mongo;

import static org.whb.common.mongo.orm.MongoConstants.ID;
import static org.whb.common.mongo.orm.MongoConstants.NEG_ONE;
import static org.whb.common.mongo.orm.MongoConstants.POS_ONE;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoBeanUtil;
import org.whb.common.mongo.orm.MongoCollection;
import org.whb.common.mongo.orm.MongoElement;
import org.whb.common.mongo.orm.MongoElement.IndexType;
import org.whb.common.util.ClassUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
/**
 * mongodb 与 java bean关系构造器
 * @author whb
 *
 */
public class OrmMongoBuilder {
    
    private boolean dbNeedShard = false;
    
    private String scanPackage;
    
    private DB db;
    
    /**
     * ORM构造器
     * @param dbNeedShard 数据库是否要分片
     * @param scanPackage 扫描的class目录
     * @param db          Mongodb连接
     */
    public OrmMongoBuilder(boolean dbNeedShard, String scanPackage, DB db) {
        super();
        this.dbNeedShard = dbNeedShard;
        this.scanPackage = scanPackage;
        this.db = db;
    }

    public OrmMongoBuilder(String scanPackage, DB db) {
        this(false, scanPackage, db);
    }

    public void build(){
        //数据库分片
        if(isDbNeedShard()){
            wrapPrint(shardDB());
        }
        
        //扫描加载IMongoBean
        scanMongoBeanClasses(getScanPackage());
    }
    
    protected void wrapPrint(String result){
        System.out.println(result);
    }
    
    protected void scanMongoBeanClasses(String basePackage){
        if(basePackage == null){
            return;
        }
        Set<Class<?>> set = ClassUtil.getClasses(basePackage);
        for(Class<?> c: set){
            if (IMongoBean.class.isAssignableFrom(c)) {
                if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
                } else {
                    //注册
                    boolean success = MongoBeanUtil.addMappingClass(c);
                    if(success){
                        //执行其他业务
                        doBusiness(c);
                    }
                }
            }
        }
    }
    
    protected void doBusiness(Class<?> c){
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(c);
        if(amc != null){
            //集合分片
            if(isDbNeedShard() && amc.needShard()){
                wrapPrint(shardCollection(c));
            }
            
            //创建单列简单索引
            Map<String, MongoElement> map = MongoBeanUtil.getMongoElementAnnotationMap(c);
            if(map != null){
                for(MongoElement mongoElement: map.values()){
                    if(mongoElement.indexType() == IndexType.Negative){
                        createIndex(amc.name(), mongoElement.name(), false);
                    }else if(mongoElement.indexType() == IndexType.Positive){
                        createIndex(amc.name(), mongoElement.name(), true);
                    }
                }
            }
        }
    }
    
    public DBCollection getCollection(String name) {
        return getDb().getCollection(name);
    }
    
    /**
     * 数据库分片
     * { enablesharding :"jelly_360"}
     * @return
     */
    public String shardDB(){
        DBObject cmd = new BasicDBObject("enablesharding", getDb().getName());
        
        CommandResult commandResult = getDb().getMongo().getDB("admin").command(cmd);
        
        return commandResult.toString();
    }
    
    /**
     * 集合分片
     * @param clazz 
     * @return
     */
    public String shardCollection(Class<?> clazz){
        MongoCollection amc = MongoBeanUtil.getMongoCollectionAnnotation(clazz);
        if (amc != null) {
            return this.shardCollection(amc.name());
        }
        return "Annotation MongoCollection is null: " + clazz.getName();
    }
    
    /**
     * 集合分片
     * 默认用hashed方式，key是_id
     * { shardcollection : "jelly_ios.user_data", key : {_id: "hashed"} }
     * @param name
     */
    public String shardCollection(String name){
        if(getDb().collectionExists(name) == false){
            DBObject cmd = new BasicDBObject();
            cmd.put("shardcollection", getDb().getName() + "." + name);
            cmd.put("key", new BasicDBObject("_id", "hashed"));
            
            CommandResult commandResult = getDb().getMongo().getDB("admin").command(cmd);
            
            return commandResult.toString();
        }
        return "collection exists: " + getDb().getName() + "." + name;
    }
    
    /**
     * 创建索引
     * @param collectionName
     * @param key
     * @param asc
     */
    public void createIndex(String collectionName, String key, boolean asc){
        if(asc){
            getCollection(collectionName).createIndex(new BasicDBObject(key, POS_ONE));
        }else{
            getCollection(collectionName).createIndex(new BasicDBObject(key, NEG_ONE));
        }
    }
    
    /**
     * 创建函数
     * @param funcName
     * @param function
     */
    public void createFunction(String funcName, String function){
        getCollection("system.js").save(new BasicDBObject(ID, funcName).append("value", function));
    }

    public boolean isDbNeedShard() {
        return dbNeedShard;
    }

    public void setDbNeedShard(boolean dbNeedShard) {
        this.dbNeedShard = dbNeedShard;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
